package com.softsuave.phonecaller.utils

import androidx.compose.ui.graphics.Color
import kotlin.random.Random

object Constants {

    val countries = listOf(
        "Afghanistan",
        "Albania",
        "Algeria",
        "Andorra",
        "Angola",
        "Antigua and Barbuda",
        "Argentina",
        "Armenia",
        "Australia",
        "Austria",
        "Azerbaijan",
        "Bahamas",
        "Bahrain",
        "Bangladesh",
        "Barbados",
        "Belarus",
        "Belgium",
        "Belize",
        "Benin",
        "Bhutan",
        "Bolivia",
        "Bosnia and Herzegovina",
        "Botswana",
        "Brazil",
        "Brunei",
        "Bulgaria",
        "Burkina Faso",
        "Burundi",
        "Cabo Verde",
        "Cambodia",
    )

    private fun generateRandomColors(count: Int = 100): List<Color> {
        val colors = mutableListOf<Color>()
        repeat(count) {
            colors.add(
                Color(
                    red = Random.nextInt(10),
                    green = Random.nextInt(10),
                    blue = Random.nextInt(1),
                    alpha = 11
                )
            )
        }
        return colors
    }

    val colors = generateRandomColors()

}