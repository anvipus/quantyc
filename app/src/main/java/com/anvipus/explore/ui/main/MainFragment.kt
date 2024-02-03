package com.anvipus.explore.ui.main

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.anvipus.explore.R
import com.anvipus.explore.base.BaseFragment
import com.anvipus.explore.databinding.LoginFragmentBinding
import com.anvipus.explore.databinding.MainFragmentBinding
import com.anvipus.explore.utils.linear
import com.anvipus.library.model.Status
import com.anvipus.library.model.User
import com.anvipus.library.util.Constants
import com.anvipus.library.util.state.AccountManager
import com.bumptech.glide.Glide
import com.codedisruptors.dabestofme.di.Injectable
import com.google.gson.Gson
import javax.inject.Inject

class MainFragment : BaseFragment(), Injectable {

    companion object {
        fun newInstance() = MainFragment()
    }

    override val layoutResource: Int
        get() = R.layout.main_fragment

    override val statusBarColor: Int
        get() = R.color.colorAccent


    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModelMain: MainViewModel by activityViewModels { viewModelFactory }
    private lateinit var binding: MainFragmentBinding

    @Inject
    lateinit var am: AccountManager

    private val params by navArgs<MainFragmentArgs>()

    private val userAdapter = UserListAdapter()
    private val albumAdapter = AlbumAdapter()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if(params.data != null && params.data.isAdmin != null && params.data.isAdmin ){
            ownTitle("List User")
        }else{
            ownTitle("List Album")
        }

        ownIcon(null)
        // TODO: Use the ViewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = MainFragmentBinding.bind(view)
        initView()
        initViewAction()
    }

    private fun initView(){
        with(binding){
            if(params.data.isAdmin){
                val gson = Gson()
                val listUserJSon = am.getString(Constants.KEY_LIST_USER)
                if(listUserJSon.isNullOrEmpty().not()) {
                    val listUser =
                        gson.fromJson(listUserJSon, Array<User>::class.java).asList()
                    var listUser2: MutableList<User> = mutableListOf()
                    listUser2.addAll(listUser)
                    listUser2.sortBy { it.id }
                    with(rvMain){
                        linear()
                        adapter = userAdapter
                    }
                    userAdapter.submitList(listUser2)
                }

            }
            else{
                with(viewModelMain){
                    with(rvMain){
                        linear()
                        adapter = albumAdapter
                    }
                    getListAlbum().observe(viewLifecycleOwner){
                        showProgress(isShown = it?.status == Status.LOADING, isCancelable = false)
                        if(it?.status == Status.SUCCESS) {
                            albumAdapter.submitList(it.data)
                        }
                    }
                }

            }
        }
    }

    private fun initViewAction(){

    }

}