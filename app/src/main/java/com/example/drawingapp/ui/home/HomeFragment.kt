package com.example.drawingapp.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.drawingapp.databinding.FragmentHomeBinding
import com.example.drawingapp.prompter.PromptActivity
import android.content.Intent

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textHome
        homeViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // BUTTON FOR PROMPT
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up the ViewModel text as before
        val textView: TextView = binding.textHome
        val homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        homeViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        // Find the button and set a click listener
        binding.showPromptButton.setOnClickListener {
            val intent = Intent(requireContext(), PromptActivity::class.java)
            startActivity(intent)
        }
    }
}