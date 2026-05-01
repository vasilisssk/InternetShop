package com.game.internetshop.views.extra.customBottomNavigationView

import androidx.core.content.ContextCompat
import com.google.android.material.bottomnavigation.BottomNavigationView

fun BottomNavigationView.setup(builder: BottomNavigationConfigBuilder.() -> Unit = {}) {
    val bottomNavigationConfig = BottomNavigationConfigBuilder().apply(builder).build()
    setBottomNavigationSections(bottomNavigationConfig)
    setBottomNavigationTint(bottomNavigationConfig)
}

private fun BottomNavigationView.setBottomNavigationSections(bottomNavigationConfig: BottomNavigationConfig) {
    menu.clear()
    bottomNavigationConfig.sectionList.forEachIndexed { index, bottomNavigationSection ->
        menu.add(0, index, index, bottomNavigationSection.title).apply {
            when (val src = bottomNavigationSection.iconSource) {
                is IconSource.ResourceId -> setIcon(src.drawableResourceId)
                is IconSource.Url -> bottomNavigationConfig.loader.loadIcon(this, src.url)
                IconSource.NotDefined -> {}
            }

            setOnMenuItemClickListener {
                bottomNavigationConfig.onItemClicked(bottomNavigationSection)
                false
            }
        }
    }
}

private fun BottomNavigationView.setBottomNavigationTint(config: BottomNavigationConfig) {
    config.tint?.let {
        itemIconTintList = ContextCompat.getColorStateList(context, it)
        itemTextColor = ContextCompat.getColorStateList(context, it)
    }
}
