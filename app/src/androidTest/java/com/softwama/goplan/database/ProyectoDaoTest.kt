package com.softwama.goplan.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.softwama.goplan.data.local.database.GoPlanDatabase
import com.softwama.goplan.data.local.database.dao.ProyectoDao
import com.softwama.goplan.data.local.database.entity.ProyectoEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.UUID

@RunWith(AndroidJUnit4::class)
class ProyectoDaoTest {

    private lateinit var database: GoPlanDatabase
    private lateinit var proyectoDao: ProyectoDao
    private val testUserId = "test_user_id"

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, GoPlanDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        proyectoDao = database.proyectoDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun insertarYObtenerProyecto() = runTest {
        val proyecto = ProyectoEntity(
            id = UUID.randomUUID().toString(),
            userId = testUserId,
            nombre = "Proyecto de prueba",
            descripcion = "Descripción",
            colorHex = "#2196F3",
            progreso = 0f,
            fechaCreacion = System.currentTimeMillis(),
            fechaInicio = System.currentTimeMillis(),
            fechaFin = System.currentTimeMillis() + 86400000,
            sincronizado = false
        )

        proyectoDao.insertar(proyecto)
        val proyectos = proyectoDao.obtenerTodos(testUserId).first()

        assert(proyectos.size == 1)
        assert(proyectos[0].nombre == "Proyecto de prueba")
    }

    @Test
    fun eliminarProyecto() = runTest {
        val id = UUID.randomUUID().toString()
        val proyecto = ProyectoEntity(
            id = id,
            userId = testUserId,
            nombre = "Proyecto a eliminar",
            descripcion = "",
            colorHex = "#2196F3",
            progreso = 0f,
            fechaCreacion = System.currentTimeMillis(),
            fechaInicio = System.currentTimeMillis(),
            fechaFin = System.currentTimeMillis(),
            sincronizado = false
        )

        proyectoDao.insertar(proyecto)
        proyectoDao.eliminar(id, testUserId)
        val proyectos = proyectoDao.obtenerTodos(testUserId).first()

        assert(proyectos.isEmpty())
    }

    @Test
    fun actualizarProgresoProyecto() = runTest {
        val id = UUID.randomUUID().toString()
        val proyecto = ProyectoEntity(
            id = id,
            userId = testUserId,
            nombre = "Proyecto",
            descripcion = "",
            colorHex = "#2196F3",
            progreso = 0f,
            fechaCreacion = System.currentTimeMillis(),
            fechaInicio = System.currentTimeMillis(),
            fechaFin = System.currentTimeMillis(),
            sincronizado = false
        )

        proyectoDao.insertar(proyecto)
        proyectoDao.actualizar(proyecto.copy(progreso = 0.5f))
        val proyectoActualizado = proyectoDao.obtenerPorId(id, testUserId)

        assert(proyectoActualizado?.progreso == 0.5f)
    }
}
