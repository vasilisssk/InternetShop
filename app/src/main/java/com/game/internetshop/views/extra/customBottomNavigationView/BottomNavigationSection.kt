package com.game.internetshop.views.extra.customBottomNavigationView

data class BottomNavigationSection(
    val title: String,
    val iconSource: IconSource = IconSource.NotDefined,
    val link: String
)