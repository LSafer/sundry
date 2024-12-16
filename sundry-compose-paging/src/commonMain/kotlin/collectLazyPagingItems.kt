package net.lsafer.sundry.compose.paging

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisallowComposableCalls
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingConfig.Companion.MAX_SIZE_UNBOUNDED
import androidx.paging.PagingSource
import androidx.paging.PagingSource.LoadResult.Page.Companion.COUNT_UNDEFINED
import androidx.paging.cachedIn

private class PagerViewModel<Key : Any, Value : Any>(pager: Pager<Key, Value>) : ViewModel() {
    val flow = pager.flow.cachedIn(viewModelScope)
}

@Composable
fun <Key : Any, Value : Any> collectLazyPagingItems(
    viewModelStoreOwner: ViewModelStoreOwner = checkNotNull(LocalViewModelStoreOwner.current) {
        "No ViewModelStoreOwner was provided via LocalViewModelStoreOwner"
    },
    key: String,
    pageSize: Int,
    prefetchDistance: Int = pageSize,
    enablePlaceholders: Boolean = true,
    initialLoadSize: Int = pageSize * 3,
    maxSize: Int = MAX_SIZE_UNBOUNDED,
    jumpThreshold: Int = COUNT_UNDEFINED,
    source: @DisallowComposableCalls () -> PagingSource<Key, Value>,
): LazyPagingItems<Value> {
    val vm = viewModel(viewModelStoreOwner, key) {
        val config = PagingConfig(
            pageSize = pageSize,
            prefetchDistance = prefetchDistance,
            enablePlaceholders = enablePlaceholders,
            initialLoadSize = initialLoadSize,
            maxSize = maxSize,
            jumpThreshold = jumpThreshold,
        )

        PagerViewModel(
            pager = Pager(
                config = config,
                pagingSourceFactory = source,
            )
        )
    }

    return vm.flow.collectAsLazyPagingItems()
}
