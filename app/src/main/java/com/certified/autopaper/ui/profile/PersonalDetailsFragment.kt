package com.certified.autopaper.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.certified.autopaper.databinding.FragmentPersonalDetailsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class PersonalDetailsFragment : Fragment() {

    private var _binding: FragmentPersonalDetailsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProfileViewModel by activityViewModels()
    private val args: PersonalDetailsFragmentArgs by navArgs()
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentPersonalDetailsBinding.inflate(layoutInflater, container, false)
        auth = Firebase.auth
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            user = args.user
            lifecycleOwner = this@PersonalDetailsFragment
            uiState = viewModel.uiState

            if (args.user?.authType == "phone")
                etPhone.apply {
                    keyListener = null
                    alpha = .5F
                }
            else if (args.user?.authType == "email")
                etEmail.apply {
                    keyListener = null
                    alpha = .5F
                }

            btnBack.setOnBackClickedListener {
                findNavController().navigate(PersonalDetailsFragmentDirections.actionPersonalDetailsFragmentToProfileFragment())
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}