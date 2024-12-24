package net.lsafer.sundry.krpc.client

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.rpc.RPCClient
import net.lsafer.sundry.krpc.client.internal.firstShareStateIn
import net.lsafer.sundry.krpc.client.internal.mapShareStateIn

interface RPCClientState<O : RPCClient> {
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

suspend fun <C : RPCClient> createRPCClientState(
    constructor: suspend () -> C,
    destructor: suspend (C) -> Unit,
    coroutineScope: CoroutineScope,
): RPCClientState<C> = createRPCClientState(
    argument = flowOf(Unit),
    constructor = { constructor() },
    destructor = destructor,
    coroutineScope = coroutineScope,
)

@OptIn(ExperimentalCoroutinesApi::class)
suspend fun <I, C : RPCClient> createRPCClientState(
    argument: Flow<I>,
    constructor: suspend (I) -> C,
    destructor: suspend (C) -> Unit,
    coroutineScope: CoroutineScope,
): RPCClientState<C> {
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

    return object : RPCClientState<C> {
        override val client = clientFlow

        override suspend fun reConnect() =
            reConnectFlow.emit(Unit)
    }
}
