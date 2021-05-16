package me.rerere.composeweather.util

object WindUtil {
    private val directArr = listOf(
        "北",
        "东北偏北",
        "东北",
        "东北偏东",
        "东",
        "东南偏东",
        "东南",
        "东南偏南",
        "南",
        "西南偏南",
        "西南",
        "西南偏西",
        "西",
        "西北偏西",
        "西北",
        "西北偏北"
    )

    fun windAngleToWindDirection(degrees: Int): String {
        var index = 0;
        if (348.75 <= degrees && degrees <= 360) {
            index = 0;
        } else if (0 <= degrees && degrees <= 11.25) {
            index = 0;
        } else if (11.25 < degrees && degrees <= 33.75) {
            index = 1;
        } else if (33.75 < degrees && degrees <= 56.25) {
            index = 2;
        } else if (56.25 < degrees && degrees <= 78.75) {
            index = 3;
        } else if (78.75 < degrees && degrees <= 101.25) {
            index = 4;
        } else if (101.25 < degrees && degrees <= 123.75) {
            index = 5;
        } else if (123.75 < degrees && degrees <= 146.25) {
            index = 6;
        } else if (146.25 < degrees && degrees <= 168.75) {
            index = 7;
        } else if (168.75 < degrees && degrees <= 191.25) {
            index = 8;
        } else if (191.25 < degrees && degrees <= 213.75) {
            index = 9;
        } else if (213.75 < degrees && degrees <= 236.25) {
            index = 10;
        } else if (236.25 < degrees && degrees <= 258.75) {
            index = 11;
        } else if (258.75 < degrees && degrees <= 281.25) {
            index = 12;
        } else if (281.25 < degrees && degrees <= 303.75) {
            index = 13;
        } else if (303.75 < degrees && degrees <= 326.25) {
            index = 14;
        } else if (326.25 < degrees && degrees < 348.75) {
            index = 15;
        } else {
            error("bad direction: $degrees")
        }
        return directArr[index] + "风"
    }

    fun windSpeedToWindLevel(windSpeed: Double): String {
        var index = 0;
        if (0.0 <= windSpeed && windSpeed < 0.3) {
            index = 0;
        } else if (0.3 <= windSpeed && windSpeed < 1.5) {
            index = 1;
        } else if (1.5 <= windSpeed && windSpeed < 3.3) {
            index = 2;
        } else if (3.3 <= windSpeed && windSpeed < 5.4) {
            index = 3;
        } else if (5.4 <= windSpeed && windSpeed < 7.9) {
            index = 4;
        } else if (7.9 <= windSpeed && windSpeed < 10.7) {
            index = 5;
        } else if (10.7 <= windSpeed && windSpeed < 13.8) {
            index = 6;
        } else if (13.8 <= windSpeed && windSpeed < 17.1) {
            index = 7;
        } else if (17.1 <= windSpeed && windSpeed < 20.7) {
            index = 8;
        } else if (20.7 <= windSpeed && windSpeed < 24.4) {
            index = 9;
        } else if (28.4 <= windSpeed && windSpeed < 32.6) {
            index = 10;
        } else if (32.6 <= windSpeed && windSpeed < 36.9) {
            index = 11;
        } else if (36.9 <= windSpeed && windSpeed < 41.4) {
            index = 12;
        } else if (41.4 <= windSpeed && windSpeed < 46.1) {
            index = 13;
        } else if (46.1 <= windSpeed && windSpeed < 50.9) {
            index = 14;
        } else if (50.9 <= windSpeed && windSpeed < 56.0) {
            index = 15;
        } else if (56.0 <= windSpeed && windSpeed < 61.2) {
            index = 16;
        } else {
            index = 17;
        }
        return "$index 级";
    }
}