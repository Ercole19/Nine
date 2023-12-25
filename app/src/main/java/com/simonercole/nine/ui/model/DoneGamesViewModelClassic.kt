package com.simonercole.nine.ui.model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.simonercole.nine.db.DBRepo
import com.simonercole.nine.db.GameDB
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class DoneGamesViewModelClassic(application: Application): AndroidViewModel(application) {
    var chosenDiff = MutableLiveData(Difficulty.All)
    var firstFilter = MutableLiveData(false)
    var gameStatus = MutableLiveData(Filters.ALL)
    private val gameDB = GameDB.getInstance(application.baseContext)
    private val repo = DBRepo(gameDB.getDAO())
    var games = MutableLiveData(mutableListOf<GameClassic>())
    private var allGames = mutableListOf<GameClassic>()
    var filteredGames = MutableLiveData(mutableListOf<GameClassic>())
    var deletedGames = MutableLiveData(mutableListOf<Int>())

    init {
        var l: List<GameClassic> = emptyList()
        val backgroundJob = CoroutineScope(Dispatchers.IO).launch {
            if (repo.getAllGamesClassic().isNotEmpty()) {
                l = repo.getAllGamesClassic()
            }
        }
        runBlocking {
            backgroundJob.join()
        }
        games.value = l.toMutableList()
        filteredGames.value = l.toMutableList()
        allGames = l.toMutableList()

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

    fun removeGame(game: GameClassic) {
        var list : MutableList<Int> = mutableListOf()
        list.apply {
            deletedGames.value!!.forEach{
                i->
                    this.add(i)
            }
        }
        val act = CoroutineScope(Dispatchers.IO).launch {
            repo.deleteGame(game)
        }
        runBlocking {
            act.join()
        }
        list.add(game.id)
        deletedGames.value = list
    }

    fun filterList() {
        val list = mutableListOf<GameClassic>()
        val iterator = games.value!!.iterator()
        while (iterator.hasNext()){ list.add(iterator.next()) }
        filteredGames.value = list
        
        if (firstFilter.value == true) {
            games.value = sortByBestTime()
            return
        }
        else {
            games.value = allGames
        }
        if (chosenDiff.value!! != Difficulty.All) {applyFilterDiff(chosenDiff.value!!, list)}
        if (gameStatus.value != Filters.ALL && firstFilter.value == false) {applyFilterStatus(gameStatus.value!!, list)}
        filteredGames.value = list
    }

    private fun sortByBestTime() : MutableList<GameClassic> {
        val list2  = mutableListOf<GameSort>()
        val list = mutableListOf<GameClassic>()
        val list3 = mutableListOf<GameClassic>()
        val iterator2 = filteredGames.value!!.iterator()
        while (iterator2.hasNext()){list3.add(iterator2.next())}
        val iterator = games.value!!.iterator()
        while (iterator.hasNext()){list.add(iterator.next())}

        applyFilterStatus(Filters.ONLY_WIN, list3)
        if (chosenDiff.value!! != Difficulty.All) {applyFilterDiff(chosenDiff.value!!, list3)}
        filteredGames.value = list3

       list.forEach { game->
           list2.add(GameSort(parseIt(game.time), game))
       }
       list2.sortBy { it.value }
       var finalList = mutableListOf<GameClassic>()
       list2.forEach {
           game->
                finalList.add(game.game)
       }
        return finalList
    }

    private fun parseIt(time:  String) : Int {
        val firstOne = time.substring(0,2)
        val secondOne = time.substring(5,7)
        val result = firstOne + secondOne
        return result.toInt()
    }

    private fun applyFilterDiff(difficulty: Difficulty, list: MutableList<GameClassic> ) {
        val iterator = list.iterator()
        while (iterator.hasNext()) {
            val game = iterator.next()
            if (game.difficulty != difficulty.toString()) {iterator.remove()}
        }
    }

    private fun applyFilterStatus(status : Filters, list: MutableList<GameClassic>) {
        val iterator = list.iterator()
        while(iterator.hasNext()){
            val game = iterator.next()
            if (status.toString() == "ONLY_WIN") {
                if (!game.win) iterator.remove()
            }
            else {if (game.win) iterator.remove()}
        }
    }

    fun checkEmpty() : Boolean {
        if (games.value!!.isEmpty()) return true
        else {
            games.value!!.forEach{
                if (deletedGames.value!!.contains(it.id)) return false
            }
            return true
        }
    }
}

data class GameSort(
    var value :Int,
    var game : GameClassic
)

@Suppress("UNCHECKED_CAST")
class DoneGamesFactoryClassic(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create (modelClass: Class<T>): T {
        return DoneGamesViewModelClassic(application) as T
    }
}