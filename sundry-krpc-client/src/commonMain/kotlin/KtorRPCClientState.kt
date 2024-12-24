package net.lsafer.sundry.krpc.client

import io.ktor.websocket.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.rpc.krpc.ktor.client.KtorRPCClient

typealias KtorRPCClientState = RPCClientState<KtorRPCClient>

suspend fun createKtorRPCClientState(
    constructor: suspend () -> KtorRPCClient,
    destructor: suspend (KtorRPCClient) -> Unit = {},
    coroutineScope: CoroutineScope,
): KtorRPCClientState = createKtorRPCClientState(
    argument = flowOf(Unit),
    constructor = { constructor() },
    destructor = destructor,
    coroutineScope = coroutineScope,
)

suspend fun <I> createKtorRPCClientState(
    argument: Flow<I>,
    constructor: suspend (I) -> KtorRPCClient,
    destructor: suspend (KtorRPCClient) -> Unit = {},
    coroutineScope: CoroutineScope,
): KtorRPCClientState = createRPCClientState(
    argument = argument,
    constructor = constructor,
    destructor = {
        destructor(it)
        it.webSocketSession.close()
    },
    coroutineScope = coroutineScope,
)
