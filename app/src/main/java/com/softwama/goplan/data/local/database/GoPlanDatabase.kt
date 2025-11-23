package com.softwama.goplan.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
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
    version = 2,  // ← Incrementa la versión
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
                )
                    .addMigrations(MIGRATION_1_2)  // ← Agregar migración
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Agregar columna userId a las tablas existentes con valor por defecto
                database.execSQL("ALTER TABLE tareas ADD COLUMN userId TEXT NOT NULL DEFAULT ''")
                database.execSQL("ALTER TABLE proyectos ADD COLUMN userId TEXT NOT NULL DEFAULT ''")
                database.execSQL("ALTER TABLE actividades ADD COLUMN userId TEXT NOT NULL DEFAULT ''")
            }
        }
    }
}