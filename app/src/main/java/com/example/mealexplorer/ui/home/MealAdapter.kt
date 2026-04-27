package com.example.mealexplorer.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.mealexplorer.R
import com.example.mealexplorer.data.model.Meal
import com.example.mealexplorer.databinding.ItemMealBinding


class MealAdapter(
    private val onMealClick: (Meal) -> Unit
) : ListAdapter<Meal, MealAdapter.MealViewHolder>(DIFF) {

    inner class MealViewHolder(
        private val binding: ItemMealBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(meal: Meal) {
            binding.tvMealName.text = meal.name

            val category = meal.category
            if (category.isNullOrBlank()) {
                binding.tvMealCategory.visibility = android.view.View.GONE
            } else {
                binding.tvMealCategory.visibility = android.view.View.VISIBLE
                binding.tvMealCategory.text = binding.root.context
                    .getString(R.string.category_label, category)
            }

            binding.ivMeal.load(meal.imageUrl) {
                crossfade(true)
                placeholder(R.drawable.bg_image_placeholder)
                error(R.drawable.bg_image_placeholder)
            }

            binding.root.setOnClickListener { onMealClick(meal) }
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
        holder.bind(getItem(position))
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<Meal>() {
            override fun areItemsTheSame(oldItem: Meal, newItem: Meal): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Meal, newItem: Meal): Boolean =
                oldItem == newItem
        }
    }
}
