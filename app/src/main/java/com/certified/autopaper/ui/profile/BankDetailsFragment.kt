package com.certified.autopaper.ui.profile

import android.R
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.certified.autopaper.data.model.Agent
import com.certified.autopaper.data.model.Bank
import com.certified.autopaper.data.model.Banks
import com.certified.autopaper.databinding.FragmentBankDetailsBinding
import com.certified.autopaper.util.Config
import com.certified.autopaper.util.Extensions.showToast
import com.certified.autopaper.util.UIState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BankDetailsFragment : Fragment() {

    private var _binding: FragmentBankDetailsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProfileViewModel by activityViewModels()
    private lateinit var auth: FirebaseAuth
    private lateinit var user: Agent
    private val token = "Bearer ${Config.PSTK_SECRET_KEY}"
    private lateinit var banks: Banks
    private val required = "* Required"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentBankDetailsBinding.inflate(layoutInflater, container, false)
        auth = Firebase.auth
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(viewModel) {
            getBanks(token)
            message.observe(viewLifecycleOwner) {
                if (it != null) {
                    showToast(it)
                    _message.postValue(null)
                }
            }
            userDetails.observe(viewLifecycleOwner) {
                binding.user = it
                user = it
            }
            accountDetails.observe(viewLifecycleOwner) {
                binding.etName.setText(it.data.account_name)
            }
            banks.observe(viewLifecycleOwner) {
                this@BankDetailsFragment.banks = it
            }
        }

        binding.apply {
            lifecycleOwner = this@BankDetailsFragment
            uiState = viewModel.bankDetailsUiState
            etName.keyListener = null

            btnBack.setOnBackClickedListener {
                findNavController().navigate(BankDetailsFragmentDirections.actionBankDetailsFragmentToProfileFragment())
            }

            btnSave.setOnClickListener {
                val bankName = etBankName.text.toString()
                val accountNumber = etAccountNumber.text.toString()

                if (bankName.isBlank()) {
                    etBankNameLayout.error = required
                    etBankName.requestFocus()
                    return@setOnClickListener
                }

                if (accountNumber.isBlank()) {
                    etAccountNumberLayout.error = required
                    etAccountNumber.requestFocus()
                    return@setOnClickListener
                }

                if (etName.text.toString().isBlank()) {
                    showToast("Resolve account details first")
                    return@setOnClickListener
                }

                etBankNameLayout.error = null
                etAccountNumberLayout.error = null
                with(viewModel) {
                    bankDetailsUiState.set(UIState.LOADING)
                    updateProfile(
                        this@BankDetailsFragment.user.copy(
                            bankName = bankName,
                            accountNumber = accountNumber
                        )
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

//        Names of all the banks gotten from the paystack api
        val bankNames = mutableListOf<String>()
//        The current bank selected by the user
        var selected = Bank()

//        Observe the banks gotten from the api
        viewModel.banks.observe(viewLifecycleOwner) {

//            Add all the names of the banks from the endpoint to the bankNames
            for (bank in it.data)
                bankNames.add(bank.name)

//            Adapter to display the banks
            val arrayAdapter = ArrayAdapter(
                requireContext(),
                R.layout.simple_spinner_dropdown_item,
                bankNames
            )

            binding.apply {
                etBankName.apply {
//                    Set the adapter for the bank names
                    setAdapter(arrayAdapter)
                    setOnItemClickListener { _, _, i, _ ->
//                        Get the bank selected by the user
                        selected = it.data[i]
                        etBankNameLayout.error = null
                        Log.d("TAG", "onItemSelected: Bank: $selected")
//                        If the account number field is not empty and is equal to 10 characters, resolve the user account details using Paystack
                        if (etAccountNumber.text.toString()
                                .isNotBlank() && etAccountNumber.text?.length == 10
                        )
                            with(viewModel) {
                                bankDetailsUiState.set(UIState.LOADING)
                                resolveAccount(
                                    token,
                                    etAccountNumber.text.toString(),
                                    selected.code
                                )
                            }
                    }
                }

                etAccountNumber.doOnTextChanged { text, _, _, _ ->
                    if (text != null)
                        when (text.length) {
//                            When the user types 10 characters in the account number field resolve the account details
                            10 -> {
                                etAccountNumberLayout.error = null
                                if (etBankName.text.toString().isNotBlank()) {
                                    etBankNameLayout.error = null
                                    with(viewModel) {
                                        bankDetailsUiState.set(UIState.LOADING)
                                        resolveAccount(
                                            token,
                                            etAccountNumber.text.toString(),
                                            selected.code
                                        )
                                    }
                                } else {
                                    etBankNameLayout.error = required
                                    etBankName.requestFocus()
                                }
                            }
                            else -> {
                                etAccountNumberLayout.error = "10 digits required"
                            }
                        }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}