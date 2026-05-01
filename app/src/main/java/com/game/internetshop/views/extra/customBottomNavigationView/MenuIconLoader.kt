package com.game.internetshop.views.extra.customBottomNavigationView

import android.view.MenuItem

interface MenuIconLoader {
    fun loadIcon(menuItem: MenuItem, url: String)
}