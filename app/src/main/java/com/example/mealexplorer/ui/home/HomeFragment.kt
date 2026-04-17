package com.example.mealexplorer.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.mealexplorer.data.model.Meal
import com.example.mealexplorer.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var mealAdapter: MealAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mockMeals = listOf(
            Meal(
                id = "1",
                name = "Grilled Chicken",
                category = "Chicken",
                imageUrl = "https://www.themealdb.com/images/media/meals/1529444830.jpg"
            ),
            Meal(
                id = "2",
                name = "Seafood Pasta",
                category = "Seafood",
                imageUrl = "https://www.themealdb.com/images/media/meals/wvpsxx1468256321.jpg"
            ),
            Meal(
                id = "3",
                name = "Beef Burger",
                category = "Beef",
                imageUrl = "https://www.themealdb.com/images/media/meals/sytuqu1511553755.jpg"
            )
        )

        mealAdapter = MealAdapter(mockMeals)
        binding.rvMeals.adapter = mealAdapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}