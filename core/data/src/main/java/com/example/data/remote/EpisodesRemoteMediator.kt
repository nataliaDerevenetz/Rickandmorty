package com.example.data.remote

import android.Manifest
import android.net.http.HttpException
import androidx.annotation.RequiresPermission
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.example.data.local.DBStorage
import com.example.data.models.toEpisodeEntity
import com.example.database.models.EpisodeEntity
import com.example.database.models.EpisodeRemoteKeyEntity
import com.example.network.di.ExplorerService
import com.example.network.NetworkHelper
import java.io.IOException
import java.net.UnknownHostException
import javax.inject.Inject


@OptIn(ExperimentalPagingApi::class)
class EpisodesRemoteMediator @Inject constructor(
    private val explorerService: ExplorerService,
    private val dbStorage: DBStorage,
    private val networkHelper: NetworkHelper,
) : RemoteMediator<Int, EpisodeEntity>() {

    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, EpisodeEntity>
    ): MediatorResult {

        if (!networkHelper.isNetworkConnected()) {
            return MediatorResult.Error(UnknownHostException())
        }

        return try {
            val page = when (loadType) {
                LoadType.REFRESH -> {
                    val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                    remoteKeys?.nextPage?.minus(1) ?: 1
                }
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    val remoteKeys = getRemoteKeyForLastItem(state)
                    val nextPage = remoteKeys?.nextPage
                        ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                    nextPage
                }
            }

            val apiResponse = explorerService.getEpisodesPaging(page = page)

            if (apiResponse.code() == 404) {
                if (loadType == LoadType.REFRESH) {
                    dbStorage.insertEpisodesTransaction(emptyList(), emptyList(), true)
                }
                return MediatorResult.Success(endOfPaginationReached = true)
            }

            if (!apiResponse.isSuccessful) throw IOException("${apiResponse.code()}")

            val results = apiResponse.body()?.results ?: emptyList()
            val endOfPaginationReached = results.isEmpty() || apiResponse.body()?.info?.next == null

            val prevPage = if (page == 1) null else page - 1
            val nextPage = if (endOfPaginationReached) null else page + 1

            dbStorage.insertEpisodesTransaction( episodes = results.map { it.
                toEpisodeEntity() },
                isClearDB = loadType == LoadType.REFRESH,
                remoteKeys = results.map {
                    EpisodeRemoteKeyEntity(
                        episodeId =  it.id,
                        prevPage = prevPage,
                        nextPage = nextPage
                    )
                }
            )

            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)

        } catch (e: IOException) {
            MediatorResult.Error(e)
        } catch (e: HttpException) {
            MediatorResult.Error(e)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, EpisodeEntity>): EpisodeRemoteKeyEntity? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()?.let { character ->
            dbStorage.getEpisodeRemoteKeyByCharacterId(character.id)
        }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, EpisodeEntity>): EpisodeRemoteKeyEntity? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { id ->
                dbStorage.getEpisodeRemoteKeyByCharacterId(id)
            }
        }
    }
}