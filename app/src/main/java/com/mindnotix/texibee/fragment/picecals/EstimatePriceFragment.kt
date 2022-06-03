package com.mindnotix.texibee.fragment.picecals

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mindnotix.texibee.R
import com.mindnotix.texibee.databinding.FragmentEstimatePriceBinding


class EstimatePriceFragment : BottomSheetDialogFragment() {
    lateinit var binding: FragmentEstimatePriceBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_estimate_price, container, false)
        return binding.root
    }

}