package com.example.mealexplorer.ui.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import coil.load
import com.example.mealexplorer.R
import com.example.mealexplorer.data.model.Ingredient
import com.example.mealexplorer.data.model.MealDetails
import com.example.mealexplorer.data.remote.ApiResult
import com.example.mealexplorer.databinding.FragmentMealDetailsBinding

class MealDetailsFragment : Fragment() {

    private var _binding: FragmentMealDetailsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MealDetailsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMealDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }


        arguments?.getString(ARG_MEAL_NAME)?.let { name ->
            if (name.isNotBlank()) binding.toolbar.title = name
        }

        binding.errorView.btnRetry.setOnClickListener { viewModel.retry() }

        observe()

        if (savedInstanceState == null) {
            val mealId = arguments?.getString(ARG_MEAL_ID).orEmpty()
            if (mealId.isBlank()) {
                showError(getString(R.string.state_error_message))
            } else {
                viewModel.load(mealId)
            }
        }
    }

    private fun observe() {
        viewModel.state.observe(viewLifecycleOwner) { state ->
            when (state) {
                ApiResult.Loading -> showLoading()
                is ApiResult.Success -> showDetails(state.data)
                ApiResult.Empty -> showEmpty()
                is ApiResult.Error -> showError(state.message)
            }
        }
    }


    private fun showLoading() {
        binding.contentScroll.visibility = View.GONE
        binding.loadingView.root.visibility = View.VISIBLE
        binding.emptyView.root.visibility = View.GONE
        binding.errorView.root.visibility = View.GONE
    }

    private fun showDetails(details: MealDetails) {
        binding.contentScroll.visibility = View.VISIBLE
        binding.loadingView.root.visibility = View.GONE
        binding.emptyView.root.visibility = View.GONE
        binding.errorView.root.visibility = View.GONE

        binding.toolbar.title = details.name
        binding.tvMealName.text = details.name
        binding.tvCategory.text = getString(R.string.details_category_label) + ": " + details.category
        binding.tvArea.text = getString(R.string.details_area_label) + ": " + details.area
        binding.tvInstructions.text = details.instructions
        binding.ivMealHero.load(details.imageUrl) {
            crossfade(true)
            placeholder(R.drawable.bg_image_placeholder)
            error(R.drawable.bg_image_placeholder)
        }
        renderIngredients(details.ingredients)
    }

    private fun renderIngredients(ingredients: List<Ingredient>) {
        val container = binding.llIngredients
        container.removeAllViews()
        val inflater = LayoutInflater.from(requireContext())
        ingredients.forEach { ingredient ->
            val row: View = inflater.inflate(R.layout.item_ingredient, container, false)
            row.findViewById<TextView>(R.id.tvIngredientName).text = ingredient.name
            row.findViewById<TextView>(R.id.tvIngredientMeasure).text = ingredient.measure
            container.addView(row, LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ))
        }
    }

    private fun showEmpty() {
        binding.contentScroll.visibility = View.GONE
        binding.loadingView.root.visibility = View.GONE
        binding.emptyView.root.visibility = View.VISIBLE
        binding.errorView.root.visibility = View.GONE
    }

    private fun showError(message: String) {
        binding.contentScroll.visibility = View.GONE
        binding.loadingView.root.visibility = View.GONE
        binding.emptyView.root.visibility = View.GONE
        binding.errorView.root.visibility = View.VISIBLE
        binding.errorView.tvErrorMessage.text = message
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val ARG_MEAL_ID = "arg_meal_id"
        const val ARG_MEAL_NAME = "arg_meal_name"
    }
}
