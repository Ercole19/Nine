package com.simonercole.nine.ui.model

import android.app.Application
import androidx.compose.runtime.mutableStateOf
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

class DoneGamesViewModelGauntlet (application: Application) : AndroidViewModel(application) {

    private val gameDB = GameDB.getInstance(application.baseContext)
    private val repo = DBRepo(gameDB.getDAO())
    var games = MutableLiveData(mutableListOf<GameGauntlet>())
    private var allGames = mutableListOf<GameGauntlet>()
    var deletedGames = MutableLiveData(mutableListOf<Int>())
    var sortIt = MutableLiveData(mutableStateOf(false))


    init {
        var l: List<GameGauntlet> = emptyList()
        val backgroundJob = CoroutineScope(Dispatchers.IO).launch {
            if (repo.getAllGamesGauntlet().isNotEmpty()) {
                l = repo.getAllGamesGauntlet()
            }
        }
        runBlocking {
            backgroundJob.join()
        }
        games.value = l.toMutableList()
        allGames = l.toMutableList()
    }

    fun setFilter() {
        sortIt.value!!.value = sortIt.value!!.value.not()
    }

    fun applyFilter() {
        if (sortIt.value!!.value) {
            games.value!!.sortByDescending { it.score }
            return
        }
        else {games.value!!.sortByDescending { it.id }}
    }

    fun removeGame(game: GameGauntlet) {
        val list : MutableList<Int> = mutableListOf()
        list.apply {
            deletedGames.value!!.forEach{
                    i->
                this.add(i)
            }
        }
        val act = CoroutineScope(Dispatchers.IO).launch {
            repo.deleteGameGauntlet(game)
        }
        runBlocking {
            act.join()
        }
        list.add(game.id)
        deletedGames.value = list
    }
}




@Suppress("UNCHECKED_CAST")
class DoneGamesFactoryGauntlet(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create (modelClass: Class<T>): T {
        return DoneGamesViewModelGauntlet(application) as T
    }
}