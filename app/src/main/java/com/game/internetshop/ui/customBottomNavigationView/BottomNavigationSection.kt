package com.game.internetshop.ui.customBottomNavigationView

data class BottomNavigationSection(
    val title: String,
    val iconSource: IconSource = IconSource.NotDefined,
    val link: String
)