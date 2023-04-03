package com.example.snapoil.model

data class Brand(
    val id: Int,
    val name: String) {

    override fun toString(): String {
        return name
    }
}