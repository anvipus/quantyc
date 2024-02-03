package com.anvipus.explore.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.anvipus.explore.repo.MainRepo
import com.anvipus.library.model.Album
import com.anvipus.library.model.Resource
import com.anvipus.library.util.state.AccountManager
import javax.inject.Inject

class MainViewModel  @Inject constructor(
    private val repo: MainRepo,
    private val am: AccountManager
): ViewModel(){
    fun getListAlbum() = repo.getListUsers()
}