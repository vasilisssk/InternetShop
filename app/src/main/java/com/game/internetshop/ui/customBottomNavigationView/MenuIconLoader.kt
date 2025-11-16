package com.game.internetshop.ui.customBottomNavigationView

import android.view.MenuItem

interface MenuIconLoader {
    fun loadIcon(menuItem: MenuItem, url: String)
}