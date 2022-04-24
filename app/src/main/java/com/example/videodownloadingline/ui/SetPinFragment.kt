package com.example.videodownloadingline.ui

import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBar
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.videodownloadingline.MainActivity
import com.example.videodownloadingline.R
import com.example.videodownloadingline.databinding.SetLockSrcFragmentLayoutBinding

class SetPinFragment : Fragment(R.layout.set_lock_src_fragment_layout) {
    private var binding: SetLockSrcFragmentLayoutBinding? = null


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = SetLockSrcFragmentLayoutBinding.bind(view)
        initial()
        onBackPress()
    }

    private fun onBackPress() {
        findNavController().popBackStack()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun initial() {
        /*MainActivity.bottomNavigation?.hide()
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
            }
        }*/
    }

    override fun onPause() {
        super.onPause()
        (requireActivity() as MainActivity).hideBottomNav(false)
    }



    override fun onResume() {
        super.onResume()
        (requireActivity() as MainActivity).hideBottomNav(true)
        (requireActivity() as MainActivity).supportActionBar!!.displayOptions =
            ActionBar.DISPLAY_SHOW_TITLE
        (requireActivity() as MainActivity).supportActionBar!!.setDisplayShowCustomEnabled(false)
        (requireActivity() as MainActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (requireActivity() as MainActivity).supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        findNavController().popBackStack()
        return super.onOptionsItemSelected(item)
    }


}