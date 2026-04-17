package com.example.mealexplorer.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.mealexplorer.data.model.Meal
import com.example.mealexplorer.databinding.ItemMealBinding

class MealAdapter(
    private val meals: List<Meal>
) : RecyclerView.Adapter<MealAdapter.MealViewHolder>() {

    inner class MealViewHolder(
        private val binding: ItemMealBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(meal: Meal) {
            binding.tvMealName.text = meal.name
            binding.tvMealCategory.text = "Category: ${meal.category}"
            binding.ivMeal.load(meal.imageUrl)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealViewHolder {
        val binding = ItemMealBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MealViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MealViewHolder, position: Int) {
        holder.bind(meals[position])
    }

    override fun getItemCount(): Int = meals.size
}