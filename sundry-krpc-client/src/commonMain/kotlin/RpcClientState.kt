package net.lsafer.sundry.krpc.client

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.rpc.RpcClient
import net.lsafer.sundry.krpc.client.internal.firstShareStateIn
import net.lsafer.sundry.krpc.client.internal.mapShareStateIn

interface RpcClientState<O : RpcClient> {
    /**
     * A flow containing the latest produced client.
     */
    val client: StateFlow<O>

    /**
     * Re-produce a fresh client into [client] flow.
     */
    suspend fun reConnect()

    /**
     * Return a [StateFlow] bound in [coroutineScope] that
     * is the result of mapping [client] flow using [block].
     */
    fun <R> mapStateIn(
        coroutineScope: CoroutineScope,
        block: (O) -> R,
    ): StateFlow<R> =
        client.mapShareStateIn(coroutineScope, block)
}

suspend fun <C : RpcClient> createRpcClientState(
    constructor: suspend () -> C,
    destructor: suspend (C) -> Unit,
    coroutineScope: CoroutineScope,
): RpcClientState<C> = createRpcClientState(
    argument = flowOf(Unit),
    constructor = { constructor() },
    destructor = destructor,
    coroutineScope = coroutineScope,
)

@OptIn(ExperimentalCoroutinesApi::class)
suspend fun <I, C : RpcClient> createRpcClientState(
    argument: Flow<I>,
    constructor: suspend (I) -> C,
    destructor: suspend (C) -> Unit,
    coroutineScope: CoroutineScope,
): RpcClientState<C> {
    val reConnectFlow = MutableSharedFlow<Unit>()
    val clientFlow = argument
        .distinctUntilChanged()
        .flatMapLatest { newArgument ->
            merge(
                flowOf(newArgument),
                reConnectFlow
                    .map { newArgument },
            )
        }
        .mapLatest { newArgument ->
            constructor(newArgument)
        }
        .runningReduce { oldClient, newClient ->
            destructor(oldClient)
            newClient
        }
        .firstShareStateIn(coroutineScope)

    return object : RpcClientState<C> {
        override val client = clientFlow

        override suspend fun reConnect() =
            reConnectFlow.emit(Unit)
    }
}
