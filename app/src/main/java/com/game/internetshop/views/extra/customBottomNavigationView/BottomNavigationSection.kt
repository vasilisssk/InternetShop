package com.game.internetshop.views.customBottomNavigationView

data class BottomNavigationSection(
    val title: String,
    val iconSource: IconSource = IconSource.NotDefined,
    val link: String
)