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
    private val testUserId = "test_user_id"

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
            userId = testUserId,
            titulo = "Tarea de prueba",
            descripcion = "Descripción",
            completada = false,
            proyectoId = "proyecto_1",
            fechaCreacion = System.currentTimeMillis(),
            fechaVencimiento = System.currentTimeMillis() + 86400000,
            sincronizado = false
        )

        tareaDao.insertar(tarea)
        val tareas = tareaDao.obtenerTodas(testUserId).first()

        assert(tareas.size == 1)
        assert(tareas[0].titulo == "Tarea de prueba")
    }

    @Test
    fun eliminarTarea() = runTest {
        val id = UUID.randomUUID().toString()
        val tarea = TareaEntity(
            id = id,
            userId = testUserId,
            titulo = "Tarea a eliminar",
            descripcion = "",
            completada = false,
            proyectoId = "proyecto_1",
            fechaCreacion = System.currentTimeMillis(),
            fechaVencimiento = null,
            sincronizado = false
        )

        tareaDao.insertar(tarea)
        tareaDao.eliminar(id, testUserId)
        val tareas = tareaDao.obtenerTodas(testUserId).first()

        assert(tareas.isEmpty())
    }

    @Test
    fun actualizarEstadoTarea() = runTest {
        val id = UUID.randomUUID().toString()
        val tarea = TareaEntity(
            id = id,
            userId = testUserId,
            titulo = "Tarea",
            descripcion = "",
            completada = false,
            proyectoId = "proyecto_1",
            fechaCreacion = System.currentTimeMillis(),
            fechaVencimiento = null,
            sincronizado = false
        )

        tareaDao.insertar(tarea)
        tareaDao.actualizar(tarea.copy(completada = true))
        val tareaActualizada = tareaDao.obtenerPorId(id, testUserId)

        assert(tareaActualizada?.completada == true)
    }
}
