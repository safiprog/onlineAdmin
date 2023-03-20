package com.example.onlineappadmin.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.onlineappadmin.MainActivity
import com.example.onlineappadmin.R
import com.example.onlineappadmin.activity.AllOdersActivity
import com.example.onlineappadmin.databinding.FragmentHomeBinding
import kotlin.math.log


class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
         binding=FragmentHomeBinding.inflate(layoutInflater)

        binding.button.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_categoryFragment)
        }
        binding.button2.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_productFragment)
        }
        binding.button3.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_sliderFragment)
        }
        binding.button4.setOnClickListener {
            startActivity(Intent(requireContext(),AllOdersActivity::class.java))
        }

        return binding.root



    }





}