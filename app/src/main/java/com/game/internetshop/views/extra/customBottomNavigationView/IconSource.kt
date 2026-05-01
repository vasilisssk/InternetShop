package com.game.internetshop.views.customBottomNavigationView

import androidx.annotation.DrawableRes

sealed class IconSource {
    data class Url(val url: String) : IconSource()
    data class ResourceId(@DrawableRes val drawableResourceId: Int) : IconSource()
    object NotDefined : IconSource()

    companion object {
        fun url(url: String): Url = Url(url)
        fun resource(@DrawableRes resourceId: Int) = ResourceId(resourceId)
        fun notDefined() = NotDefined
    }
}