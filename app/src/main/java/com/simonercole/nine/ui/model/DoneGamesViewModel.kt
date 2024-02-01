package com.simonercole.nine.ui.model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.simonercole.nine.db.DBRepo
import com.simonercole.nine.db.GameDB
import com.simonercole.nine.ui.model.NineGameUtils.Companion.parseIt
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class DoneGamesViewModel(application: Application): AndroidViewModel(application) {
    var chosenDiff = MutableLiveData(Difficulty.All)
    var firstFilter = MutableLiveData(false)
    var gameStatus = MutableLiveData(Filters.ALL)
    var games = MutableLiveData(mutableListOf<Game>())
    private var allGames = mutableListOf<Game>()
    var filteredGames = MutableLiveData(mutableListOf<Game>())
    var deletedGames = MutableLiveData(mutableListOf<Int>())
    private var gameModel: GameModel


    init {
        gameModel = GameModel(application = getApplication())
        var totalGames: List<Game> = emptyList()
        viewModelScope.launch { totalGames = gameModel.getAllGames() }
        games.value = totalGames.toMutableList()
        filteredGames.value = totalGames.toMutableList()
        allGames = totalGames.toMutableList()

    }

    fun setChosenDiff(difficulty: Difficulty) {
        chosenDiff.value = difficulty
    }

    fun setGameStatus(filters: Filters) {
        gameStatus.value = filters
    }

    fun setFilter() {
        firstFilter.value = firstFilter.value!!.not()
    }

    fun removeGame(game: Game) {
        var list: MutableList<Int> = mutableListOf()
        list.apply {
            deletedGames.value!!.forEach { i ->
                this.add(i)
            }
        }
        viewModelScope.launch { gameModel.deleteGame(game) }
        list.add(game.id)
        deletedGames.value = list
    }

    fun filterList() {
        val list = mutableListOf<Game>()
        val iterator = games.value!!.iterator()
        while (iterator.hasNext()) {
            list.add(iterator.next())
        }
        filteredGames.value = list

        if (firstFilter.value == true) {
            games.value = sortByBestTime()
            return
        } else {
            games.value = allGames
        }
        if (chosenDiff.value!! != Difficulty.All) {
            applyFilterDiff(chosenDiff.value!!, list)
        }
        if (gameStatus.value != Filters.ALL && firstFilter.value == false) {
            applyFilterStatus(gameStatus.value!!, list)
        }
        filteredGames.value = list
    }

    private fun sortByBestTime(): MutableList<Game> {
        val list2 = mutableListOf<NineGameUtils.GameSort>()
        val list = mutableListOf<Game>()
        val list3 = mutableListOf<Game>()
        val iterator2 = filteredGames.value!!.iterator()
        while (iterator2.hasNext()) {
            list3.add(iterator2.next())
        }
        val iterator = games.value!!.iterator()
        while (iterator.hasNext()) {
            list.add(iterator.next())
        }

        applyFilterStatus(Filters.ONLY_WIN, list3)
        if (chosenDiff.value!! != Difficulty.All) {
            applyFilterDiff(chosenDiff.value!!, list3)
        }
        filteredGames.value = list3

        list.forEach { game ->
            list2.add(NineGameUtils.GameSort(parseIt(game.time), game))
        }
        list2.sortBy { it.value }
        var finalList = mutableListOf<Game>()
        list2.forEach { game ->
            finalList.add(game.game)
        }
        return finalList
    }

    private fun applyFilterDiff(difficulty: Difficulty, list: MutableList<Game>) {
        val iterator = list.iterator()
        while (iterator.hasNext()) {
            val game = iterator.next()
            if (game.difficulty != difficulty.toString()) {
                iterator.remove()
            }
        }
    }

    private fun applyFilterStatus(status: Filters, list: MutableList<Game>) {
        val iterator = list.iterator()
        while (iterator.hasNext()) {
            val game = iterator.next()
            if (status.toString() == "ONLY_WIN") {
                if (!game.win) iterator.remove()
            } else {
                if (game.win) iterator.remove()
            }
        }
    }
}

@Suppress("UNCHECKED_CAST")
class DoneGamesFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create (modelClass: Class<T>): T {
        return DoneGamesViewModel(application) as T
    }
}