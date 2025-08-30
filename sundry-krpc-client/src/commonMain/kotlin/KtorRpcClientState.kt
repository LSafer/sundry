package net.lsafer.sundry.krpc.client

import io.ktor.websocket.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.rpc.krpc.ktor.client.KtorRpcClient

typealias KtorRpcClientState = RpcClientState<KtorRpcClient>

suspend fun createKtorRpcClientState(
    constructor: suspend () -> KtorRpcClient,
    destructor: suspend (KtorRpcClient) -> Unit = {},
    coroutineScope: CoroutineScope,
): KtorRpcClientState = createKtorRpcClientState(
    argument = flowOf(Unit),
    constructor = { constructor() },
    destructor = destructor,
    coroutineScope = coroutineScope,
)

suspend fun <I> createKtorRpcClientState(
    argument: Flow<I>,
    constructor: suspend (I) -> KtorRpcClient,
    destructor: suspend (KtorRpcClient) -> Unit = {},
    coroutineScope: CoroutineScope,
): KtorRpcClientState = createRpcClientState(
    argument = argument,
    constructor = constructor,
    destructor = {
        destructor(it)
        it.webSocketSession.await().close()
    },
    coroutineScope = coroutineScope,
)
