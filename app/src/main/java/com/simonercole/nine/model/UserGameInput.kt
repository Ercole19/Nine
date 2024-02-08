package com.simonercole.nine.model

import android.annotation.SuppressLint
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import com.simonercole.nine.utils.Difficulty


class UserGameInput {

    var currentInput : SnapshotStateList<GameTile> = mutableStateListOf(*Array(9) { GameTile() })
    var allGuesses : SnapshotStateList<HashMap<Int, Pair<String, Char>>> = mutableStateListOf(HashMap())
    var gameKeyboard : SnapshotStateList<KeyboardTile> = mutableStateListOf(*Array(9) { KeyboardTile() })

    fun initInput() {
        for (i in 0..8) {
            currentInput[i].value = ' '
            currentInput[i].isGuessed = false
            currentInput[i].distance = -1
            currentInput[i].isFocused = i == 0
        }
    }

    fun clearInput() {
        val newInput :  SnapshotStateList<GameTile>  = emptyList<GameTile>().toMutableStateList()
        currentInput.forEach { newInput.add(it) }
        for (i in 0..8) {
            if (!newInput[i].isGuessed) {
                gameKeyboard[getKeyBoardTile(newInput[i].value)].isVisible = true
                newInput[i].value = ' '
            }
        }
        currentInput = newInput
    }

    fun getCurrentFocusIndex(): Int {
        var currentFocusIndex : Int = -1
        for (i in 0..8) {
            if (currentInput[i].isFocused) currentFocusIndex = i
        }
        return currentFocusIndex
    }

    fun updateFocusByTouch(index: Int) {
        val newInput :  SnapshotStateList<GameTile>  = emptyList<GameTile>().toMutableStateList()
        currentInput.forEach { newInput.add(it) }
        if (getCurrentFocusIndex() != -1 ) newInput[getCurrentFocusIndex()].isFocused = false
        if (!newInput[index].isGuessed) {
            newInput[index].isFocused = true
            currentInput = newInput
        }
        else updateFocusByWrite(index)
    }

    fun updateInput(index: Int, char: Char) {
        gameKeyboard[getKeyBoardTile(char)].isVisible = false
        val newInput :  SnapshotStateList<GameTile>  = emptyList<GameTile>().toMutableStateList()
        currentInput.forEach { newInput.add(it) }
        newInput[index].value = char
        currentInput = newInput
        for (i in getCurrentFocusIndex()..8){
            if (!currentInput[i].isGuessed) {
                updateFocusByWrite(i)
                break
            }
        }
    }

    fun clearGameTile(index: Int) {
        val newInput :  SnapshotStateList<GameTile>  = emptyList<GameTile>().toMutableStateList()
        currentInput.forEach { newInput.add(it) }
        gameKeyboard[getKeyBoardTile(currentInput[index].value)].isVisible = true
        newInput[index].value = ' '
        currentInput = newInput
    }

     fun isInputFull() : Boolean {
        var inputFull = true
        for (i in 0..8) {
            if (currentInput[i].value == ' ') inputFull = false
        }
        return inputFull
    }

    fun updateFocusByWrite(index: Int) {
        val newInput :  SnapshotStateList<GameTile>  = emptyList<GameTile>().toMutableStateList()
        currentInput.forEach { newInput.add(it) }
        if (getCurrentFocusIndex() != -1 ) newInput[getCurrentFocusIndex()].isFocused = false
        var newIndex = index
        while (true) {
            if (isInputFull()) break

            if (currentInput[newIndex].value != ' ' || currentInput[newIndex].isGuessed) {
                if (newIndex + 1 > 8) newIndex = 0
                else newIndex++
            }

            else {
                newInput[newIndex].isFocused = true
                currentInput = newInput
                break
            }
        }
    }

    @SuppressLint("SuspiciousIndentation")
    fun calculateDistance(sequenceToGuess : CharArray) {
        val newInput :  SnapshotStateList<GameTile>  = emptyList<GameTile>().toMutableStateList()
        currentInput.forEach { newInput.add(it) }
        newInput.forEachIndexed { index, it ->
            val correctIndex = sequenceToGuess.indexOf(it.value)
            val finalDistance: Int
            val rawDistance = kotlin.math.abs(correctIndex - index)
            finalDistance = when (rawDistance) {
                in 0..4 -> rawDistance
                else -> 9 - rawDistance
            }
            newInput[index].distance = finalDistance
                if (finalDistance == 0)  {
                    newInput[index].isGuessed = true
                    gameKeyboard[getKeyBoardTile(currentInput[index].value)].isGuessed = true
                }
            }
            currentInput = newInput
        }

    fun updateUserGuesses(currentAttempts : Int, difficulty: Difficulty) {
        val newInput: SnapshotStateList<GameTile> = emptyList<GameTile>().toMutableStateList()
        currentInput.forEach { newInput.add(it) }
        val map = HashMap<Int, Pair<String, Char>>()
        val newGuesses = mutableStateListOf(HashMap<Int, Pair<String, Char>>())
        newGuesses.removeAt(0)

        allGuesses.forEach { hashMap ->
            newGuesses.add(hashMap)
        }
        for (i in 0..8) {
            if (currentAttempts == 1 && difficulty == Difficulty.Hard) {
                if (i % 2 != 0 || currentInput[i].isGuessed) {
                    map[i] = Pair(currentInput[i].distance.toString(), currentInput[i].value)
                } else {
                    map[i] = Pair("?", currentInput[i].value)
                }
            } else if (currentAttempts == 2 && difficulty == Difficulty.Hard) {
                if (i % 2 == 0 || currentInput[i].isGuessed) {
                    map[i] = Pair(currentInput[i].distance.toString(), currentInput[i].value)
                } else {
                    map[i] = Pair("?", currentInput[i].value)
                }
            } else {
                map[i] = Pair(currentInput[i].distance.toString(), currentInput[i].value)
            }

        }
        newGuesses.add(map)
        allGuesses = newGuesses
        }


    fun isInputAllCorrect() : Boolean {
        for (i in 0..8) {
            if (!currentInput[i].isGuessed) return false
        }
        return true
    }

    private fun getKeyBoardTile(char : Char) : Int {
        var index = -1
        gameKeyboard.forEachIndexed {i , it ->
            if (it.value == char) index = i
        }
        return  index
    }


    fun setKeyboard(sequenceToGuess : CharArray) {
        val startingKeyboard = sequenceToGuess
        startingKeyboard.shuffle()

        startingKeyboard.forEachIndexed { i, it ->
            gameKeyboard[i].value = it
            gameKeyboard[i].isVisible = true
            gameKeyboard[i].isGuessed = false
        }

    }
}