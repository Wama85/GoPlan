package com.softwama.goplan.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.softwama.goplan.data.local.database.GoPlanDatabase
import com.softwama.goplan.data.local.database.dao.TareaDao
import com.softwama.goplan.data.local.database.entity.TareaEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.UUID

@RunWith(AndroidJUnit4::class)
class TareaDaoTest {

    private lateinit var database: GoPlanDatabase
    private lateinit var tareaDao: TareaDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, GoPlanDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        tareaDao = database.tareaDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun insertarYObtenerTarea() = runTest {
        val tarea = TareaEntity(
            id = UUID.randomUUID().toString(),
            titulo = "Tarea de prueba",
            descripcion = "Descripción",
            completada = false,
            proyectoId = "",
            fechaCreacion = System.currentTimeMillis(),
            fechaVencimiento = null,
            sincronizado = false
        )

        tareaDao.insertar(tarea)
        val tareas = tareaDao.obtenerTodas().first()

        assert(tareas.size == 1)
        assert(tareas[0].titulo == "Tarea de prueba")
    }

    @Test
    fun eliminarTarea() = runTest {
        val id = UUID.randomUUID().toString()
        val tarea = TareaEntity(
            id = id,
            titulo = "Tarea a eliminar",
            descripcion = "",
            completada = false,
            proyectoId = "",
            fechaCreacion = System.currentTimeMillis(),
            fechaVencimiento = null,
            sincronizado = false
        )

        tareaDao.insertar(tarea)
        tareaDao.eliminar(id)
        val tareas = tareaDao.obtenerTodas().first()

        assert(tareas.isEmpty())
    }

    @Test
    fun actualizarTareaComoCompletada() = runTest {
        val id = UUID.randomUUID().toString()
        val tarea = TareaEntity(
            id = id,
            titulo = "Tarea original",
            descripcion = "",
            completada = false,
            proyectoId = "",
            fechaCreacion = System.currentTimeMillis(),
            fechaVencimiento = null,
            sincronizado = false
        )

        tareaDao.insertar(tarea)
        tareaDao.actualizar(tarea.copy(completada = true))
        val tareaActualizada = tareaDao.obtenerPorId(id)

        assert(tareaActualizada?.completada == true)
    }
}