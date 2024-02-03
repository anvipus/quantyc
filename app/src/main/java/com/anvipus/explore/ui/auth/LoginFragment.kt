package com.anvipus.explore.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.anvipus.explore.R
import com.anvipus.explore.base.BaseFragment
import com.anvipus.explore.databinding.LoginFragmentBinding
import com.anvipus.explore.databinding.RegisterFragmentBinding
import com.anvipus.explore.ui.main.MainFragment
import com.anvipus.explore.ui.main.MainViewModel
import com.anvipus.library.model.User
import com.anvipus.library.util.Constants
import com.anvipus.library.util.state.AccountManager
import com.codedisruptors.dabestofme.di.Injectable
import com.google.gson.Gson
import javax.inject.Inject

class LoginFragment : BaseFragment(), Injectable {
    companion object {
        fun newInstance() = LoginFragment()
    }

    override val layoutResource: Int
        get() = R.layout.login_fragment

    override val statusBarColor: Int
        get() = R.color.colorAccent


    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModelMain: MainViewModel by activityViewModels { viewModelFactory }
    private lateinit var binding: LoginFragmentBinding

    @Inject
    lateinit var am: AccountManager

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        ownTitle("Login")
        ownIcon(null)
        // TODO: Use the ViewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = LoginFragmentBinding.bind(view)
        initView()
        initViewAction()
    }

    private fun initView(){

    }
    private fun initViewAction(){
        with(binding){
            btnDaftar.setOnClickListener {
                navigate(LoginFragmentDirections.actionToRegister())
            }
            btnLogin.setOnClickListener {
                try{
                    val gson = Gson()
                    val listUserJSon = am.getString(Constants.KEY_LIST_USER)
                    if(listUserJSon.isNullOrEmpty().not()) {
                        val listUser =
                            gson.fromJson(listUserJSon, Array<User>::class.java).asList()

                        for (data2 in listUser) {
                            if (data2.username!!.equals(
                                    etUsername.text.toString(),
                                    ignoreCase = true
                                ) && data2.password!!.equals(
                                    etPassword.text.toString(),
                                    ignoreCase = true)
                            ) {
                                navigate(LoginFragmentDirections.actionToMain(data = data2))
                                break
                            }
                        }
                    }
                }catch (e:Exception){
                    e.printStackTrace()
                }

            }
        }
    }

}