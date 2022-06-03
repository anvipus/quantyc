package com.anvipus.explore

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.anvipus.explore.databinding.MainActivityBinding
import com.anvipus.explore.ui.main.MainFragment
import com.anvipus.explore.ui.main.MainViewModel
import com.anvipus.explore.utils.closeKeyboard
import com.anvipus.explore.utils.hide
import com.anvipus.explore.utils.show
import com.anvipus.library.util.resDrawable
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import kotlinx.android.synthetic.main.toolbar_white.*
import javax.inject.Inject

class MainActivity : AppCompatActivity(), HasAndroidInjector {

    private lateinit var mDataBinding: MainActivityBinding
    // Required for Dagger AndroidInject
    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Any>
    override fun androidInjector(): AndroidInjector<Any> = dispatchingAndroidInjector

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val mainViewModel: MainViewModel by viewModels { viewModelFactory }

    lateinit var layoutToolbar: ConstraintLayout
    lateinit var toolbar: Toolbar
    lateinit var toolbarTitle: TextView
    lateinit var toolbarLogo: ImageView
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mDataBinding = DataBindingUtil.setContentView(this, R.layout.main_activity)
        layoutToolbar = mDataBinding.layoutToolbar.toolbarParent
        toolbar = mDataBinding.layoutToolbar.toolbar
        toolbarTitle = mDataBinding.layoutToolbar.tvTitle
        toolbarLogo = mDataBinding.layoutToolbar.ivLogoSpin

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_container) as NavHostFragment
        navController = navHostFragment.navController

        appBarConfiguration = AppBarConfiguration(navController.graph)
        toolbar.setupWithNavController(navController, appBarConfiguration)
        toolbar.setNavigationOnClickListener {
            closeKeyboard()
            navController.navigateUp() || super.onSupportNavigateUp()
        }
        navController.addOnDestinationChangedListener { _, _, _ ->
            toolbar.navigationIcon = resDrawable(R.drawable.ic_back_arrow_dark)
        }
    }

    fun setTitleToolbar(title: String) {
        with(mDataBinding){
            layoutToolbar.tvTitle.show()
            layoutToolbar.ivLogoSpin.hide()
            layoutToolbar.lineToolbar.show()
            layoutToolbar.tvTitle.text = title
        }
    }

    fun hideTitleToolbar() {
        with(mDataBinding){
            layoutToolbar.tvTitle.hide()
            layoutToolbar.ivLogoSpin.show()
            layoutToolbar.lineToolbar.hide()

        }

    }


}