package com.jobcoin.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.api.domain.addressinfo.AddressInfoTransactionState
import com.api.domain.addressinfo.AddressInfoTransactionState.*
import com.jobcoin.R
import com.jobcoin.viewmodel.SignInViewModel
import kotlinx.android.synthetic.main.fragment_signin.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class SignInFragment : Fragment() {
    companion object {
        const val TAG = "SignInFragment"

        fun newInstance(): SignInFragment {
            return SignInFragment()
        }
    }

    private val vm: SignInViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setObservers()
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_signin, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btn_sign_in.setOnClickListener {
            if (et_address.text.isEmpty()) {
                et_address.error = getString(R.string.empty_field)
            } else {
                val address = et_address.text.toString()
                vm.signIn(address)
            }
        }
    }

    private fun setObservers() {
        vm.signInState.observe(this, signInResponseStateListener)
    }

    private val signInResponseStateListener = Observer<AddressInfoTransactionState> { data ->
        when (data) {
            is Loading -> {
                progress.visibility = View.VISIBLE
            }
            is Success -> {
                progress.visibility = View.GONE

                GlobalScope.launch {
                    initSendFragment(data.addressInfo.balance)
                }

                Toast.makeText(context, getString(R.string.signin_success),
                        Toast.LENGTH_SHORT).show()
            }
            is Failure -> {
                progress.visibility = View.GONE

                Log.e(TAG, getString(R.string.signin_failure, data.error))
                Toast.makeText(context, getString(R.string.signin_failure, data.error),
                        Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun initSendFragment(balance: String) {
        activity?.supportFragmentManager?.let {
            val address = et_address.text.toString()
            val fragment = SendFragment.newInstance(address, balance)
            it.beginTransaction()
                    .replace(R.id.container, fragment)
                    .commit()
        }
    }

}