package com.certified.autopaper.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.certified.autopaper.R
import com.certified.autopaper.adapter.ViewPagerAdapter
import com.certified.autopaper.data.model.SliderItem
import com.certified.autopaper.databinding.FragmentOnboardingBinding

class OnboardingFragment : Fragment() {

    private var _binding: FragmentOnboardingBinding? = null
    private val binding get() = _binding!!
    private lateinit var sliderItem: ArrayList<SliderItem>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentOnboardingBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpSliderItem()
        setUpViewPager()
    }

    private fun setUpSliderItem() {
        sliderItem = ArrayList()
        sliderItem.add(
            SliderItem(R.drawable.onboarding1, getString(R.string.vehicle_registration_and_renewal_made_easy))
        )
        sliderItem.add(
            SliderItem(R.drawable.onboarding1, getString(R.string.become_fast_and_efficient))
        )
        sliderItem.add(
            SliderItem(R.drawable.onboarding1, getString(R.string.automated_free_reminders))
        )
        binding.pageIndicator.count = sliderItem.size
    }

    private fun setUpViewPager() {
        var currentPosition = 0
        val viewPagerAdapter = ViewPagerAdapter()
        viewPagerAdapter.submitList(sliderItem)
        binding.apply {
            viewPager.apply {
                adapter = viewPagerAdapter
                registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        super.onPageSelected(position)
                        currentPosition = position
                        binding.pageIndicator.selection = position
                        if (position == sliderItem.size - 1) {
                            btnGetStarted.visibility = View.VISIBLE
                            binding.pageIndicator.selection = position
                        } else
                            btnGetStarted.visibility = View.GONE
                    }
                })
            }

            btnSignIn.setOnClickListener { findNavController().navigate(OnboardingFragmentDirections.actionOnboardingFragmentToLoginFragment()) }
            btnSignup.setOnClickListener { findNavController().navigate(OnboardingFragmentDirections.actionOnboardingFragmentToSignupFragment()) }

//            fabNext.setOnClickListener {
//                if (currentPosition != 3) currentPosition++
//                if (currentPosition == 3) {
//                    binding.groupOnboarding.visibility = View.GONE
//                    binding.groupSplash.visibility = View.VISIBLE
//                }
//                binding.viewPager.currentItem = currentPosition
//            }
//            fabPrev.setOnClickListener {
//                if (currentPosition != 0) currentPosition--
//                binding.viewPager.currentItem = currentPosition
//            }
            btnGetStarted.setOnClickListener {
                binding.apply {
                    btnGetStarted.visibility = View.GONE
                    groupOnboarding.visibility = View.GONE
                    groupGetStarted.visibility = View.VISIBLE
                }
            }
            btnSkip.setOnClickListener {
                binding.apply {
                    btnGetStarted.visibility = View.GONE
                    groupOnboarding.visibility = View.GONE
                    groupGetStarted.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}