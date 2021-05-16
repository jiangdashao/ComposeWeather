package me.rerere.composeweather.model

data class Loc(
    val latitude: Double,
    val longitude: Double
){
    override fun toString(): String {
        return "$latitude,$longitude"
    }
}