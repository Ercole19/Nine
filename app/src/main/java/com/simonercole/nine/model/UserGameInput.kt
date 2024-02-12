package com.simonercole.nine.model

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import com.simonercole.nine.utils.Difficulty

/**
 * Manages user input and game tiles.
 */
class UserGameInput {
    // The current user input.
    var currentInput: SnapshotStateList<GameTile> = mutableStateListOf(*Array(9) { GameTile() })
    // All user guesses.
    var allGuesses: SnapshotStateList<HashMap<Int, Pair<String, Char>>> = mutableStateListOf(HashMap())
    // The game keyboard.
    var gameKeyboard: SnapshotStateList<KeyboardTile> = mutableStateListOf(*Array(9) { KeyboardTile() })

    /**
     * Initializes the user input.
     */
    fun initInput() {
        for (i in 0..8) {
            currentInput[i].value = ' '
            currentInput[i].isGuessed = false
            currentInput[i].distance = -1
            currentInput[i].isFocused = i == 0
        }
    }

    /**
     * Clears the user input.
     */
    fun clearInput() {
        val newInput: SnapshotStateList<GameTile> = emptyList<GameTile>().toMutableStateList()
        currentInput.forEach { newInput.add(it) }
        for (i in 0..8) {
            if (!newInput[i].isGuessed) {
                gameKeyboard[getKeyBoardTile(newInput[i].value)].isVisible = true
                newInput[i].value = ' '
            }
        }
        currentInput = newInput
    }

    /**
     * Gets the index of the currently focused input.
     * @return The index of the focused input.
     */
    fun getCurrentFocusIndex(): Int {
        var currentFocusIndex: Int = -1
        for (i in 0..8) {
            if (currentInput[i].isFocused) currentFocusIndex = i
        }
        return currentFocusIndex
    }

    /**
     * Updates the focus based on user touch.
     * @param index The index touched by the user.
     */
    fun updateFocusByTouch(index: Int) {
        val newInput: SnapshotStateList<GameTile> = emptyList<GameTile>().toMutableStateList()
        currentInput.forEach { newInput.add(it) }
        if (getCurrentFocusIndex() != -1) newInput[getCurrentFocusIndex()].isFocused = false
        if (!newInput[index].isGuessed) {
            newInput[index].isFocused = true
            currentInput = newInput
        } else updateFocusByWrite(index)
    }

    /**
     * Updates the input at the specified index.
     * @param index The index to update.
     * @param char The character to update.
     */
    fun updateInput(index: Int, char: Char) {
        gameKeyboard[getKeyBoardTile(char)].isVisible = false
        val newInput: SnapshotStateList<GameTile> = emptyList<GameTile>().toMutableStateList()
        currentInput.forEach { newInput.add(it) }
        newInput[index].value = char
        currentInput = newInput
        for (i in getCurrentFocusIndex()..8) {
            if (!currentInput[i].isGuessed) {
                updateFocusByWrite(i)
                break
            }
        }
    }

    /**
     * Clears the game tile at the specified index.
     * @param index The index to clear.
     */
    fun clearGameTile(index: Int) {
        val newInput: SnapshotStateList<GameTile> = emptyList<GameTile>().toMutableStateList()
        currentInput.forEach { newInput.add(it) }
        gameKeyboard[getKeyBoardTile(currentInput[index].value)].isVisible = true
        newInput[index].value = ' '
        currentInput = newInput
    }

    /**
     * Checks if the input is full.
     * @return True if the input is full, false otherwise.
     */
    fun isInputFull(): Boolean {
        var inputFull = true
        for (i in 0..8) {
            if (currentInput[i].value == ' ') inputFull = false
        }
        return inputFull
    }

    /**
     * Updates the focus by writing at the specified index.
     * @param index The index to update.
     */
    fun updateFocusByWrite(index: Int) {
        val newInput: SnapshotStateList<GameTile> = emptyList<GameTile>().toMutableStateList()
        currentInput.forEach { newInput.add(it) }
        if (getCurrentFocusIndex() != -1) newInput[getCurrentFocusIndex()].isFocused = false
        var newIndex = index
        while (true) {
            if (isInputFull()) break

            if (currentInput[newIndex].value != ' ' || currentInput[newIndex].isGuessed) {
                if (newIndex + 1 > 8) newIndex = 0
                else newIndex++
            } else {
                newInput[newIndex].isFocused = true
                currentInput = newInput
                break
            }
        }
    }

    /**
     * Calculates the distance between current input and the sequence to guess.
     * @param sequenceToGuess The sequence to be guessed.
     */
    fun calculateDistance(sequenceToGuess: CharArray) {
        val newInput: SnapshotStateList<GameTile> = emptyList<GameTile>().toMutableStateList()
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
            if (finalDistance == 0) {
                newInput[index].isGuessed = true
                gameKeyboard[getKeyBoardTile(currentInput[index].value)].isGuessed = true
            }
        }
        currentInput = newInput
    }

    /**
     * Updates user guesses based on current attempts and difficulty.
     * @param currentAttempts The current attempts made.
     * @param difficulty The difficulty level of the game.
     */
    fun updateUserGuesses(currentAttempts: Int, difficulty: Difficulty) {
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

    /**
     * Checks if all input is correct.
     * @return True if all input is correct, false otherwise.
     */
    fun isInputAllCorrect(): Boolean {
        for (i in 0..8) {
            if (!currentInput[i].isGuessed) return false
        }
        return true
    }

    /**
     * Gets the keyboard tile index for the specified character.
     * @param char The character to search for.
     * @return The index of the keyboard tile.
     */
    private fun getKeyBoardTile(char: Char): Int {
        var index = -1
        gameKeyboard.forEachIndexed { i, it ->
            if (it.value == char) index = i
        }
        return index
    }

    /**
     * Sets up the keyboard with the sequence to guess.
     * @param sequenceToGuess The sequence of characters to guess.
     */
    fun setKeyboard(sequenceToGuess: CharArray) {
        val startingKeyboard = CharArray(9)
        sequenceToGuess.forEachIndexed { i,c -> startingKeyboard[i] = c }
        startingKeyboard.shuffle()

            startingKeyboard.forEachIndexed { i, it ->
            gameKeyboard[i].value = it
            gameKeyboard[i].isVisible = true
            gameKeyboard[i].isGuessed = false
        }

    }
}
