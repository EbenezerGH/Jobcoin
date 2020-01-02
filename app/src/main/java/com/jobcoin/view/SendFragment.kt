package com.jobcoin.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.api.domain.addressinfo.AddressInfoTransactionState
import com.api.domain.sendtransaction.SendTransactionState
import com.api.domain.sendtransaction.SendTransactionState.*
import com.api.domain.transactionhistory.TransactionHistoryState
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.jobcoin.*
import com.jobcoin.viewmodel.SendViewModel
import kotlinx.android.synthetic.main.fragment_send.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class SendFragment : Fragment() {
    companion object {
        const val TAG = "SendFragment"
        const val ADDRESS = "ADDRESS"
        const val BALANCE = "BALANCE"

        fun newInstance(address: String, addressInfo: String): SendFragment {
            val fragment = SendFragment()
            val bundle = Bundle()
            bundle.putString(ADDRESS, address)
            bundle.putString(BALANCE, addressInfo)
            fragment.arguments = bundle

            return fragment
        }
    }

    private val vm: SendViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setObservers()
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_send, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vm.address = arguments?.getString(ADDRESS)
        vm.balance = arguments?.getString(BALANCE)

        vm.fetchTransactionHistory()
        tv_balance.text = vm.balance
        et_from_address.setText(vm.address)

        btn_send.setOnClickListener {
            sendTransaction()
        }

        btn_sign_out.setOnClickListener {
            signOut()
        }
    }

    private fun setObservers() {
        vm.fetchTransactionHistoryState.observe(this, fetchTransactionHistoryStateListener)
        vm.sendTransactionState.observe(this, fetchSendTransactionStateListener)
        vm.fetchAddressInfoState.observe(this, fetchAddressInfoStateListener)
    }

    private val fetchTransactionHistoryStateListener = Observer<TransactionHistoryState> { state ->
        when (state) {
            is TransactionHistoryState.Loading -> {
                progress.visibility = View.VISIBLE
            }
            is TransactionHistoryState.Success -> {
                progress.visibility = View.GONE

                val data = state.transaction.toArray()
                val entries = ArrayList<Entry>()

                for (i in data.indices) {
                    entries.add(
                        Entry(
                            state.transaction[i].timestamp.timestampToFloat(),
                            state.transaction[i].amount.toFloat()
                        )
                    )
                }

                populateGraph(entries)
            }
            is TransactionHistoryState.Failure -> {
                progress.visibility = View.GONE
                Toast.makeText(
                        context,
                        getString(R.string.unable_fetch_transaction_history),
                        Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun populateGraph(entries: ArrayList<Entry>) {
        val dataSet = LineDataSet(entries, "Transactions")
        dataSet.color =
            ContextCompat.getColor(context?.let { it } ?: throw Exception("Bad color"),
                R.color.colorAccent)
        dataSet.valueTextColor =
            ContextCompat.getColor(context?.let { it } ?: throw Exception("Bad color"),
                R.color.colorPrimary)

        val lineData = LineData(dataSet)
        val timestampFormatter = TimestampValueFormatter()
        graph_holder.xAxis.valueFormatter = timestampFormatter
        graph_holder.data = lineData
        graph_holder.invalidate()
    }

    private val fetchSendTransactionStateListener = Observer<SendTransactionState> { state ->
        when (state) {
            is Loading -> {
                progress.visibility = View.VISIBLE
            }
            is Success -> {
                progress.visibility = View.GONE
                vm.fetchAddressInfo()
                Toast.makeText(context, state.transaction.status, Toast.LENGTH_SHORT).show()
            }
            is Failure -> {
                progress.visibility = View.GONE
                Toast.makeText(context, state.transaction.error, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private val fetchAddressInfoStateListener = Observer<AddressInfoTransactionState> { state ->
        when (state) {
            is AddressInfoTransactionState.Loading -> {
                progress.visibility = View.VISIBLE
            }

            is AddressInfoTransactionState.Success -> {
                progress.visibility = View.GONE

                tv_balance.text = state.addressInfo.balance

                val data = state.addressInfo.transactions.toArray()
                val entries = ArrayList<Entry>()

                for (i in data.indices) {
                    entries.add(
                        Entry(
                            state.addressInfo.transactions[i].timestamp.timestampToFloat(),
                            state.addressInfo.transactions[i].amount.toFloat()
                        )
                    )
                }

                populateGraph(entries)
            }

            is AddressInfoTransactionState.Failure -> {
                progress.visibility = View.GONE
                Toast.makeText(
                        context, getString(R.string.chart_update_failure, state.error),
                        Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun sendTransaction() {
        val from = et_from_address.text.toString()
        val to = et_to_address.text.toString()
        val amount = et_amount.text.toString()

        if (!amount.isNumeric()) {
            et_amount.error = "Amount must be a Number"
        }
        if (from.isEmpty()) {
            et_from_address.error = "Address must not be empty"
        }
        if (to.isEmpty()) {
            et_to_address.error = "Address must not be empty"
        }
        if (amount.isEmpty()) {
            et_amount.error = "Amount must be a Number"
        }
        if (from.isNotEmpty() && to.isNotEmpty() && amount.isNotEmpty() && amount.isNumeric()) {
            vm.sendTransaction(from, to, amount)

            et_to_address.setText("")
            et_amount.setText("")
        }
    }

    private fun signOut() {
        activity?.supportFragmentManager?.let {
            val fragment = SignInFragment.newInstance()
            fragmentManager?.let {
                it.beginTransaction()
                        .replace(R.id.container, fragment)
                        .commit()
            }
        }
    }
}