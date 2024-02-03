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
import com.anvipus.explore.databinding.RegisterFragmentBinding
import com.anvipus.explore.ui.main.MainFragment
import com.anvipus.explore.ui.main.MainViewModel
import com.anvipus.library.model.User
import com.anvipus.library.util.Constants
import com.anvipus.library.util.state.AccountManager
import com.codedisruptors.dabestofme.di.Injectable
import javax.inject.Inject
import com.google.gson.Gson

class RegisterFragment : BaseFragment(), Injectable {
    companion object {
        fun newInstance() = RegisterFragment()
    }

    override val layoutResource: Int
        get() = R.layout.register_fragment

    override val statusBarColor: Int
        get() = R.color.colorAccent


    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModelMain: MainViewModel by activityViewModels { viewModelFactory }
    private lateinit var binding: RegisterFragmentBinding

    @Inject
    lateinit var am: AccountManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = RegisterFragmentBinding.bind(view)
        initView()
        initViewAction()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        ownTitle("Register")
        // TODO: Use the ViewModel
    }

    private fun initView(){

    }
    private fun initViewAction(){

        with(binding){
            btnDaftar.setOnClickListener {
                try{
                    val gson = Gson()
                    val sharedPrefOrderId = am.getString(Constants.KEY_LIST_USER)
                    var isRegistered = false
                    val data = User(username = etUsername.text.toString(), password = etPassword.text.toString(), email = etEmail.text.toString(), isAdmin = scAdmin.isChecked)
                    if(sharedPrefOrderId.isNullOrEmpty().not()){
                        val listUser = gson.fromJson(sharedPrefOrderId, Array<User>::class.java).asList()

                        for(data2 in listUser){
                            if(data2.username!!.equals(etUsername.text.toString(),ignoreCase = true) || data2.email!!.equals(etEmail.text.toString(),ignoreCase = true)){
                                isRegistered = true
                                Toast.makeText(requireContext(), "User Sudah Terdaftar", Toast.LENGTH_SHORT).show()
                                break
                            }
                        }
                        am.listUser.clear()
                        am.listUser.addAll(listUser)
                        am.listUser.add(data)
                    }else{
                        am.listUser.add(data)
                    }
                    if(!isRegistered){
                        val json = gson.toJson(am.listUser)
                        am.putString(Constants.KEY_LIST_USER,json)
                        navigate(RegisterFragmentDirections.actionToMain(data))
                    }
                }catch (e:Exception){
                    e.printStackTrace()
                    Toast.makeText(requireContext(), e.message.toString(), Toast.LENGTH_SHORT).show()
                }

            }
        }
    }

}