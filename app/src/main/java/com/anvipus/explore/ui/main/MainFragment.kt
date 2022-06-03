package com.anvipus.explore.ui.main

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.anvipus.explore.R
import com.anvipus.explore.base.BaseFragment
import com.bumptech.glide.Glide
import com.codedisruptors.dabestofme.di.Injectable
import javax.inject.Inject

class MainFragment : BaseFragment(), Injectable {

    companion object {
        fun newInstance() = MainFragment()
    }

    override val layoutResource: Int
        get() = R.layout.main_fragment


    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModelMain: MainViewModel by activityViewModels { viewModelFactory }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.main_fragment, container, false)

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        ownIcon(null)
        showLogo()
        // TODO: Use the ViewModel
    }

}