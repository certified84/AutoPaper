package com.certified.autopaper.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.certified.autopaper.R
import com.certified.autopaper.data.model.Agent
import com.certified.autopaper.databinding.FragmentProfileBinding
import com.certified.autopaper.util.Extensions.showToast
import com.certified.autopaper.util.UIState
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProfileViewModel by activityViewModels()
    private lateinit var auth: FirebaseAuth
    private lateinit var user: Agent

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentProfileBinding.inflate(layoutInflater, container, false)
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
                user = it
                binding.user = it
                if (it != null && it.authType == "phone")
                    binding.cardSecurity.apply {
                        isEnabled = false
                        alpha = .5F
                    }
            }
        }

        binding.apply {
            uiState = viewModel.uiState
            lifecycleOwner = this@ProfileFragment

            btnBack.setOnBackClickedListener {
                findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToHomeFragment())
            }

            cardPersonalDetails.setOnClickListener {
                findNavController().navigate(
                    ProfileFragmentDirections.actionProfileFragmentToPersonalDetailsFragment(
                        user
                    )
                )
            }

            cardBankDetails.setOnClickListener {
                findNavController().navigate(
                    ProfileFragmentDirections.actionProfileFragmentToBankDetailsFragment(
                        user
                    )
                )
            }

            cardLogout.setOnClickListener {
                launchLogoutDialog()
            }

            cardSecurity.setOnClickListener {
                launchPasswordChangeDialog()
            }
        }
    }

    private fun launchLogoutDialog() {
        MaterialAlertDialogBuilder(requireContext()).apply {
            setTitle("Logout")
            setIcon(R.drawable.ic_logout)
            setMessage("Are you sure you want to logout?")
            setPositiveButton("Yes") { dialog, _ ->
                dialog.dismiss()
                viewModel.uiState.set(UIState.LOADING)
                auth.signOut()
                findNavController().navigate(
                    ProfileFragmentDirections.actionProfileFragmentToLoginFragment(
                        "onboarding"
                    )
                )
            }
            setNegativeButton("No") { dialog, _ -> dialog.cancel() }
            show()
        }
    }

    private fun launchPasswordChangeDialog() {
        val materialDialog = MaterialAlertDialogBuilder(requireContext()).apply {
            setTitle("Password change")
            setMessage("You are about to change your password. A password reset link will be sent to your email and you'll be signed out")
            setPositiveButton("Continue") { dialog, _ ->
                dialog.dismiss()
                viewModel.uiState.set(UIState.LOADING)
                auth.sendPasswordResetEmail(auth.currentUser!!.email!!).addOnSuccessListener {
                    viewModel.uiState.set(UIState.SUCCESS)
                    auth.signOut()
                    findNavController().navigate(
                        ProfileFragmentDirections.actionProfileFragmentToLoginFragment(
                            "onboarding"
                        )
                    )
                }.addOnFailureListener {
                    viewModel.uiState.set(UIState.FAILURE)
                    showToast("An error occurred: ${it.localizedMessage}")
                }
            }
            setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }
        }
        materialDialog.show()
    }

    override fun onResume() {
        super.onResume()
        if (auth.currentUser == null)
            findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToOnboardingFragment())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}