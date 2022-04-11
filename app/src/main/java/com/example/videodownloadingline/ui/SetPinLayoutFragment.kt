package com.example.videodownloadingline.ui

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.example.videodownloadingline.MainActivity
import com.example.videodownloadingline.R
import com.example.videodownloadingline.databinding.SetLockSrcFragmentLayoutBinding
import com.example.videodownloadingline.utils.hide
import com.example.videodownloadingline.utils.show

class SetPinLayoutFragment : Fragment(R.layout.set_lock_src_fragment_layout) {
    private lateinit var binding: SetLockSrcFragmentLayoutBinding


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = SetLockSrcFragmentLayoutBinding.bind(view)
        initial()
        onBackPress()
        binding.toolBarId.toolbarHomeBtn.setOnClickListener {
            MainActivity.bottomNavigation?.show()
            MainActivity.viewPager2?.currentItem = 2
        }
    }

    private fun onBackPress() {
        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    MainActivity.bottomNavigation?.show()
                    MainActivity.viewPager2?.currentItem = 2
                }
            })
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun initial() {
        MainActivity.bottomNavigation?.hide()
        binding.toolBarId.apply {
            root.setBackgroundColor(requireActivity().getColor(R.color.Mine_Shaft_color))
            this.apply {
                searchBoxEd.hide()
                totalTabOp.hide()
                toolbarHomeBtn.apply {
                    setImageResource(R.drawable.ic_arrow_back)
                    setColorFilter(
                        requireActivity().getColor(R.color.white),
                        android.graphics.PorterDuff.Mode.MULTIPLY
                    )
                    show()
                }
                threeBotMnuBtn.hide()
            }
        }
    }


}