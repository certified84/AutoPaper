package com.certified.autopaper.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.certified.autopaper.databinding.FragmentSignupBinding

class SignupFragment : Fragment() {

    private var _binding: FragmentSignupBinding? = null
    private val binding get() = _binding!!
    private val args: SignupFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentSignupBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            btnBack.setOnBackClickedListener {
                when (args.from) {
                    "onboarding" -> findNavController().navigate(SignupFragmentDirections.actionSignupFragmentToOnboardingFragment())
                    "login" -> findNavController().navigate(
                        SignupFragmentDirections.actionSignupFragmentToLoginFragment(
                            "signup"
                        )
                    )
                }
            }
            btnLogin.setOnClickListener {
                findNavController().navigate(
                    SignupFragmentDirections.actionSignupFragmentToLoginFragment(
                        "signup"
                    )
                )
            }
            btnContinue.setOnClickListener { findNavController().navigate(SignupFragmentDirections.actionSignupFragmentToOTPFragment()) }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}