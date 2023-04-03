package com.example.snapoil.model

data class GasStation(
    val id: Int,
    val city: String,
    val address: String,
    val brand: Brand) {

    override fun toString(): String {
        return "$city, $address, ${brand.name}"
    }
}
