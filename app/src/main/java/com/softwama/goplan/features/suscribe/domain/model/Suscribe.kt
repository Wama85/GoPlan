package com.softwama.goplan.features.suscribe.domain.model

data class Suscribe(
    val nombre: String = "",
    val apellido: String = "",
    val correo: String = "",
    val fechaNac: String = "", // o usar LocalDate si prefieres
    val user: String = "",
    val pass: String = "",
    val repitPass: String = ""
)