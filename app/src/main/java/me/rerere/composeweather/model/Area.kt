package me.rerere.composeweather.model

data class Area(
    var name: String,
    var location: Loc? = null,
    var weather: WeatherData? = null,
    val isCurrentLocation: Boolean = false
){
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Area

        if (name != other.name) return false
        if (location != other.location) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + (location?.hashCode() ?: 0)
        return result
    }
}