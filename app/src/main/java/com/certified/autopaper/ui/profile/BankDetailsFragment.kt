package com.certified.autopaper.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.certified.autopaper.data.model.Agent
import com.certified.autopaper.databinding.FragmentBankDetailsBinding
import com.certified.autopaper.util.Extensions.showToast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class BankDetailsFragment : Fragment() {

    private var _binding: FragmentBankDetailsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProfileViewModel by activityViewModels()
    private lateinit var auth: FirebaseAuth
    private lateinit var user: Agent

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
        }

        binding.apply {
            lifecycleOwner = this@BankDetailsFragment
            uiState = viewModel.bankDetailsUiState
            etName.keyListener = null

            btnBack.setOnBackClickedListener {
                findNavController().navigate(BankDetailsFragmentDirections.actionBankDetailsFragmentToProfileFragment())
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}