package com.simonercole.nine.model

import kotlin.properties.Delegates

class KeyboardTile {
    var value by Delegates.notNull<Char>()
    var isVisible by Delegates.notNull<Boolean>()
    var isGuessed by Delegates.notNull<Boolean>()
}