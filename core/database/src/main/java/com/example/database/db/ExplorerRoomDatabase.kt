package com.example.database.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.database.dao.CharacterDao
import com.example.database.dao.CharacterFilterDao
import com.example.database.dao.CharacterRemoteKeyDao
import com.example.database.dao.EpisodeDao
import com.example.database.dao.EpisodeInfoDao
import com.example.database.dao.EpisodeRemoteKeyDao
import com.example.database.models.CharacterFilterEntity
import com.example.database.models.CharacterRemoteKeyEntity
import com.example.database.models.CharacterEntity
import com.example.database.models.EpisodeCharacterCrossRef
import com.example.database.models.EpisodeEntity
import com.example.database.models.EpisodeInfoEntity
import com.example.database.models.EpisodeRemoteKeyEntity

@Database(entities = [CharacterFilterEntity::class, CharacterRemoteKeyEntity::class,
    EpisodeEntity::class, EpisodeRemoteKeyEntity::class, EpisodeInfoEntity::class,
    CharacterEntity::class, EpisodeCharacterCrossRef::class], version = 1,exportSchema = false)
abstract class ExplorerRoomDatabase: RoomDatabase() {
    abstract fun characterFilterDao() : CharacterFilterDao
    abstract fun characterRemoteKeyDao(): CharacterRemoteKeyDao
    abstract fun episodeDao() : EpisodeDao
    abstract fun episodeRemoteKeyDao(): EpisodeRemoteKeyDao
    abstract fun episodeInfoDao(): EpisodeInfoDao
    abstract fun characterDao(): CharacterDao
}