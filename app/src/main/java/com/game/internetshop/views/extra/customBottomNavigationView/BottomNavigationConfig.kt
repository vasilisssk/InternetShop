package com.game.internetshop.views.customBottomNavigationView

import androidx.annotation.ColorRes

data class BottomNavigationConfig(
    val sectionList: List<BottomNavigationSection>,
    @ColorRes val tint: Int? = null,
    val onItemClicked: (BottomNavigationSection) -> Unit,
    val loader: MenuIconLoader
)
