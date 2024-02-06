package com.simonercole.nine.model

import kotlin.properties.Delegates

class GameTile {
    var  value by Delegates.notNull<Char>()
    var isFocused by Delegates.notNull<Boolean>()
    var isGuessed by Delegates.notNull<Boolean>()
    var distance by Delegates.notNull<Int>()
}