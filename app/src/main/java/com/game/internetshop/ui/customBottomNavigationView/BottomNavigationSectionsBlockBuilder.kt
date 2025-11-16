package com.game.internetshop.ui.customBottomNavigationView

class BottomNavigationSectionsBlockBuilder {
    private val sections: MutableList<BottomNavigationSection> = mutableListOf()

    fun section(builder: BottomNavigationSectionBuilder.() -> Unit = {}) {
        BottomNavigationSectionBuilder().apply(builder).build()
            .apply(sections::add)
    }

    fun build(): SectionsBlock = SectionsBlock(sections)
}