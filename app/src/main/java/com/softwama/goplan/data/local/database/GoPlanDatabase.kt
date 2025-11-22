package com.softwama.goplan.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.softwama.goplan.data.local.database.dao.ActividadDao
import com.softwama.goplan.data.local.database.dao.ProyectoDao
import com.softwama.goplan.data.local.database.dao.TareaDao
import com.softwama.goplan.data.local.database.entity.ActividadEntity
import com.softwama.goplan.data.local.database.entity.ProyectoEntity
import com.softwama.goplan.data.local.database.entity.TareaEntity

@Database(
    entities = [
        TareaEntity::class,
        ProyectoEntity::class,
        ActividadEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class GoPlanDatabase : RoomDatabase() {

    abstract fun tareaDao(): TareaDao
    abstract fun proyectoDao(): ProyectoDao
    abstract fun actividadDao(): ActividadDao

    companion object {
        @Volatile
        private var INSTANCE: GoPlanDatabase? = null

        fun getInstance(context: Context): GoPlanDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    GoPlanDatabase::class.java,
                    "goplan_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}