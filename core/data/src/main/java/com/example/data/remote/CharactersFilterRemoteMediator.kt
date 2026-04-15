package com.example.data.remote

import android.Manifest
import android.net.http.HttpException
import androidx.annotation.RequiresPermission
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.example.data.local.DBStorage
import com.example.data.models.toCharacterFilterEntity
import com.example.database.models.CharacterFilterEntity
import com.example.database.models.CharacterRemoteKeyEntity
import com.example.network.di.ExplorerService
import com.example.network.NetworkHelper
import java.io.IOException
import java.net.UnknownHostException
import javax.inject.Inject


@OptIn(ExperimentalPagingApi::class)
class CharactersFilterRemoteMediator @Inject constructor(
    private val explorerService: ExplorerService,
    private val name: String,
    private val dbStorage: DBStorage,
    private val networkHelper: NetworkHelper,
) : RemoteMediator<Int, CharacterFilterEntity>() {

    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, CharacterFilterEntity>
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

            val apiResponse = explorerService.getCharacterSearchPaging(name = name, page = page)

            if (apiResponse.code() == 404) {
                if (loadType == LoadType.REFRESH) {
                    dbStorage.insertCharacterFilterTransaction(emptyList(), emptyList(), true)
                }
                return MediatorResult.Success(endOfPaginationReached = true)
            }

            if (!apiResponse.isSuccessful) throw IOException("${apiResponse.code()}")

            val results = apiResponse.body()?.results ?: emptyList()
            val endOfPaginationReached = results.isEmpty() || apiResponse.body()?.info?.next == null

            val prevPage = if (page == 1) null else page - 1
            val nextPage = if (endOfPaginationReached) null else page + 1

            dbStorage.insertCharacterFilterTransaction(
                characters = results.map { it.toCharacterFilterEntity() },
                isClearDB = loadType == LoadType.REFRESH,
                remoteKeys = results.map {
                    CharacterRemoteKeyEntity(characterId = it.id, prevPage = prevPage, nextPage = nextPage)
                }
            )

            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)

        } catch (e: IOException) {
            MediatorResult.Error(e)
        } catch (e: HttpException) {
            MediatorResult.Error(e)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, CharacterFilterEntity>): CharacterRemoteKeyEntity? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()?.let { character ->
            dbStorage.getRemoteKeyByCharacterId(character.id)
        }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, CharacterFilterEntity>): CharacterRemoteKeyEntity? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { id ->
                dbStorage.getRemoteKeyByCharacterId(id)
            }
        }
    }
}
