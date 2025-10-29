package com.softwama.goplan.features.suscribe.domain.model

data class Suscribe(
    val id: String = "",
    val nombre: String = "",
    val apellido: String = "",
    val correo: String = "",
    val fechaNac: String = "",
    val pass: String = "",
    val repitPass: String = ""
)