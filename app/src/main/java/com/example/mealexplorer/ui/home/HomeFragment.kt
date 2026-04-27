package com.example.mealexplorer.ui.home

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.mealexplorer.R
import com.example.mealexplorer.data.model.Category
import com.example.mealexplorer.data.model.Meal
import com.example.mealexplorer.data.remote.ApiResult
import com.example.mealexplorer.databinding.FragmentHomeBinding
import com.example.mealexplorer.ui.details.MealDetailsFragment
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()
    private lateinit var mealAdapter: MealAdapter

    private var suppressChipCallback = false

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
        setupRecycler()
        setupSearch()
        setupRetry()
        observeViewModel()
    }

    private fun setupRecycler() {
        mealAdapter = MealAdapter(onMealClick = ::openDetails)
        binding.rvMeals.adapter = mealAdapter
    }

    private fun setupSearch() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                viewModel.onSearchQueryChanged(s?.toString().orEmpty())
            }
        })
    }

    private fun setupRetry() {
        binding.errorView.btnRetry.setOnClickListener {
            // Retry both streams in case categories failed too.
            viewModel.loadCategories()
            viewModel.loadMeals()
        }
    }


    private fun observeViewModel() {
        viewModel.categoriesState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is ApiResult.Success -> renderChips(state.data)
                ApiResult.Empty -> renderChips(emptyList())
                is ApiResult.Error,
                ApiResult.Loading -> {

                    if (binding.chipGroupCategories.childCount == 0) {
                        renderChips(emptyList())
                    }
                }
            }
        }

        viewModel.mealsState.observe(viewLifecycleOwner) { state ->
            when (state) {
                ApiResult.Loading -> showLoading()
                is ApiResult.Success -> showMeals(state.data)
                ApiResult.Empty -> showEmpty()
                is ApiResult.Error -> showError(state.message)
            }
        }
    }

    private fun renderChips(categories: List<Category>) {
        val group: ChipGroup = binding.chipGroupCategories
        suppressChipCallback = true
        group.setOnCheckedStateChangeListener(null)
        group.removeAllViews()

        addChip(
            group = group,
            id = HomeViewModel.ALL_CATEGORY,
            label = getString(R.string.category_all),
            checked = viewModel.selectedCategoryId == HomeViewModel.ALL_CATEGORY
        )

        categories.forEach { category ->
            addChip(
                group = group,
                id = category.name,
                label = category.name,
                checked = viewModel.selectedCategoryId == category.name
            )
        }

        group.setOnCheckedStateChangeListener { chipGroup, checkedIds ->
            if (suppressChipCallback) return@setOnCheckedStateChangeListener
            val checkedId = checkedIds.firstOrNull() ?: return@setOnCheckedStateChangeListener
            val chip = chipGroup.findViewById<Chip>(checkedId) ?: return@setOnCheckedStateChangeListener
            val tag = chip.tag as? String ?: return@setOnCheckedStateChangeListener
            viewModel.onCategorySelected(tag)
        }
        suppressChipCallback = false
    }

    private fun addChip(group: ChipGroup, id: String, label: String, checked: Boolean) {
        val chip = layoutInflater
            .inflate(R.layout.chip_category, group, false) as Chip
        chip.text = label
        chip.isChecked = checked
        chip.tag = id
        group.addView(chip)
    }


    private fun showLoading() {
        binding.rvMeals.visibility = View.GONE
        binding.loadingView.root.visibility = View.VISIBLE
        binding.emptyView.root.visibility = View.GONE
        binding.errorView.root.visibility = View.GONE
    }

    private fun showMeals(meals: List<Meal>) {
        binding.rvMeals.visibility = View.VISIBLE
        binding.loadingView.root.visibility = View.GONE
        binding.emptyView.root.visibility = View.GONE
        binding.errorView.root.visibility = View.GONE
        mealAdapter.submitList(meals)
    }

    private fun showEmpty() {
        binding.rvMeals.visibility = View.GONE
        binding.loadingView.root.visibility = View.GONE
        binding.emptyView.root.visibility = View.VISIBLE
        binding.errorView.root.visibility = View.GONE
        mealAdapter.submitList(emptyList())
    }

    private fun showError(message: String) {
        binding.rvMeals.visibility = View.GONE
        binding.loadingView.root.visibility = View.GONE
        binding.emptyView.root.visibility = View.GONE
        binding.errorView.root.visibility = View.VISIBLE
        binding.errorView.tvErrorMessage.text = message
    }


    private fun openDetails(meal: Meal) {
        val args = Bundle().apply {
            putString(MealDetailsFragment.ARG_MEAL_ID, meal.id)
            putString(MealDetailsFragment.ARG_MEAL_NAME, meal.name)
        }
        findNavController().navigate(
            R.id.action_homeFragment_to_mealDetailsFragment,
            args
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
