package com.certified.autopaper.ui.profile

import android.R
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.load
import coil.transform.CircleCropTransformation
import com.certified.autopaper.data.model.Agent
import com.certified.autopaper.databinding.FragmentPersonalDetailsBinding
import com.certified.autopaper.util.Extensions.showToast
import com.certified.autopaper.util.UIState
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException

@AndroidEntryPoint
class PersonalDetailsFragment : Fragment() {

    private var _binding: FragmentPersonalDetailsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProfileViewModel by activityViewModels()
    private val args: PersonalDetailsFragmentArgs by navArgs()
    private lateinit var auth: FirebaseAuth
    private lateinit var storage: FirebaseStorage
    private lateinit var user: Agent
    private val required = "* Required"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentPersonalDetailsBinding.inflate(layoutInflater, container, false)
        auth = Firebase.auth
        storage = Firebase.storage
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
            lifecycleOwner = this@PersonalDetailsFragment
            uiState = viewModel.personalDetailsUiState

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

            ivProfileImage.setOnClickListener { launchChangeProfileImageDialog() }

            btnSave.setOnClickListener {
                val name = etName.text.toString()
                val phoneNumber = etPhone.text.toString()
                val email = etEmail.text.toString()
                val state = etState.text.toString()
                val townResidence = etTownResidence.text.toString()
                val homeAddress = etHomeAddress.text.toString()

                if (name.isBlank()) {
                    etNameLayout.error = required
                    etName.requestFocus()
                    return@setOnClickListener
                }

                if (state.isBlank()) {
                    etStateLayout.error = required
                    etState.requestFocus()
                    return@setOnClickListener
                }

                if (townResidence.isBlank()) {
                    etTownResidenceLayout.error = required
                    etTownResidence.requestFocus()
                    return@setOnClickListener
                }

                if (homeAddress.isBlank()) {
                    etHomeAddressLayout.error = required
                    etHomeAddress.requestFocus()
                    return@setOnClickListener
                }

                with(viewModel) {
                    personalDetailsUiState.set(UIState.LOADING)
                    updateProfile(
                        user!!.copy(
                            name = name,
                            phoneNumber = phoneNumber,
                            email = email,
                            state = state,
                            townResidence = townResidence,
                            homeAddress = homeAddress,
                            complete = true
                        )
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        val arrayAdapter = ArrayAdapter(
            requireContext(),
            R.layout.simple_spinner_dropdown_item,
            resources.getStringArray(com.certified.autopaper.R.array.states)
        )

        binding.etState.setAdapter(arrayAdapter)
    }

    private fun launchChangeProfileImageDialog() {
        val builder = MaterialAlertDialogBuilder(requireContext())
        val selection = arrayOf(
            "Take picture",
            "Choose from gallery",
        )
        builder.setTitle("Options")
        builder.setSingleChoiceItems(selection, -1) { dialog, which ->
            when (which) {
                0 -> launchCamera()
                1 -> chooseFromGallery()
            }
            dialog.dismiss()
        }
        builder.show()
    }

    private fun launchCamera() {
        viewModel.personalDetailsUiState.set(UIState.LOADING)
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        } catch (e: ActivityNotFoundException) {
            showToast("An error occurred: ${e.localizedMessage}")
        }
    }

    private fun chooseFromGallery() {
        viewModel.personalDetailsUiState.set(UIState.LOADING)
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/jpeg", "image/png"))
        try {
            startActivityForResult(
                Intent.createChooser(intent, "Select image"),
                PICK_IMAGE_CODE
            )
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context, "An error occurred: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            assert(data != null)
            try {
                val bitmap = data?.extras!!["data"] as Bitmap?
                requireContext().openFileOutput("profile_image", Context.MODE_PRIVATE).use {
                    bitmap?.compress(Bitmap.CompressFormat.PNG, 100, it)
                }
                val file = File(requireContext().filesDir, "profile_image")
                uploadImage(Uri.fromFile(file))
            } catch (e: IOException) {
                e.printStackTrace()
            }
        } else if (requestCode == PICK_IMAGE_CODE && resultCode == Activity.RESULT_OK) {
            assert(data != null)
            try {
                uploadImage(data?.data)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }
        }
    }

    private fun uploadImage(uri: Uri?) {
        val path = "profileImages/${auth.currentUser!!.uid}/profileImage.jpg"
        viewModel.apply {
            personalDetailsUiState.set(UIState.LOADING)
            uploadImage(uri, path)
            binding.ivProfileImage.load(uri) {
                transformations(CircleCropTransformation())
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val REQUEST_IMAGE_CAPTURE = 101
        const val PICK_IMAGE_CODE = 102
    }
}