# 🍽️ Meal Explorer

A small Android training app built end-to-end against
[TheMealDB](https://www.themealdb.com/api.php) — splash, home with search and
category filtering, and a meal details screen powered by real API calls.

This repository follows the **Meal Explorer Android XML Trainee Plan** and
delivers all four weeks: project setup, full static flow, API integration,
and search + details integration.

---

## 🛠️ Tech stack

* Kotlin
* XML layouts + Material 3
* Single Activity + Fragments + Navigation Component
* MVVM with `ViewModel` + `LiveData`
* Coroutines
* Retrofit + Gson
* RecyclerView (`ListAdapter` + `DiffUtil`)
* Coil for image loading
* ViewBinding

---

## 🧠 Architecture

```
ui/  ──── observes ───►  ViewModel  ──── calls ──►  Repository  ──► ApiService
                            │                            │
                            └──── LiveData<ApiResult> ◄──┘
```

* `ui/` — Fragments + Adapters. Stateless, just renders what the ViewModel says.
* `data/model/` — Lean UI models (`Meal`, `MealDetails`, `Ingredient`, `Category`).
* `data/remote/` — Retrofit interface, DTOs, base URL constants, sealed `ApiResult`.
* `data/repository/` — Maps DTOs → UI models, wraps errors as `ApiResult.Error`.
* `util/` — Small string helpers.

---

## 📁 Folder structure

```
app/src/main/java/com/example/mealexplorer
├── MainActivity.kt
├── data
│   ├── model
│   │   ├── Category.kt
│   │   ├── Meal.kt
│   │   └── MealDetails.kt
│   ├── remote
│   │   ├── ApiConstants.kt
│   │   ├── ApiResult.kt
│   │   ├── MealApiService.kt
│   │   └── dto
│   │       └── Dtos.kt
│   └── repository
│       └── MealRepository.kt
├── ui
│   ├── details
│   │   ├── MealDetailsFragment.kt
│   │   └── MealDetailsViewModel.kt
│   ├── home
│   │   ├── HomeFragment.kt
│   │   ├── HomeViewModel.kt
│   │   └── MealAdapter.kt
│   └── splash
│       └── SplashFragment.kt
└── util
    └── StringExtensions.kt
```

---

## 🌐 API endpoints used

Base URL: `https://www.themealdb.com/api/json/v1/1/`

| Endpoint                     | Where it's called             |
|------------------------------|-------------------------------|
| `categories.php`             | Home — build category chips   |
| `search.php?f=a`             | Home — default first load     |
| `search.php?s=<query>`       | Home — search bar (debounced) |
| `filter.php?c=<category>`    | Home — chip filter            |
| `lookup.php?i=<mealId>`      | Details — full meal info      |

---

## 🧩 Screens

| Screen   | What it does                                                          |
|----------|-----------------------------------------------------------------------|
| Splash   | 1.5s branded entry, then auto-navigates to Home (and is popped).      |
| Home     | Title, search bar, dynamic category chips, vertical meal cards.       |
| Details  | Hero image, name, category, area, instructions, ingredients with measures. |
| States   | Reusable loading / empty / error layouts included by both fragments.  |

---

## 🔁 Data flow on Home

1. `HomeViewModel.init` triggers `loadCategories()` and `loadMeals()` in parallel.
2. Categories fill the chip group dynamically — `"All"` is always first.
3. Default meals come from `search.php?f=a`.
4. Tapping a chip calls `filter.php?c=<name>`. Tapping `"All"` reloads default.
5. Typing in the search bar debounces 350ms then calls `search.php?s=<query>`.
   Search overrides the active chip; clearing it falls back to the chip.
6. Tapping a card navigates to Details with the meal id.

## 🔁 Data flow on Details

1. Fragment reads `arg_meal_id` from `arguments`.
2. `MealDetailsViewModel.load(id)` triggers `lookup.php?i=<id>`.
3. Repository flattens the 20 `strIngredient` / `strMeasure` field pairs into a clean list.
4. UI renders hero image, name, category/area badges, instructions, and ingredient rows.
5. Retry on error simply re-issues `lookup.php` with the saved id.

---

## ✅ Plan delivery checklist

* **Week 1** — Project structure, splash, static home, item card, mock data ✅
* **Week 2** — Static end-to-end flow, details fragment, navigation Home → Details, loading/empty/error views ✅
* **Week 3** — `ApiConstants`, `ApiResult`, `MealApiService`, `MealRepository`, real categories + default meals on Home ✅
* **Week 4** — Search via `search.php?s=`, details via `lookup.php?i=`, real states wired throughout ✅

---

## 🚀 Run it

1. Open the project in Android Studio (Hedgehog or newer).
2. Sync Gradle (Retrofit / Coroutines / Lifecycle pulls automatically).
3. Run on a device or emulator with internet access.

No API key configuration needed — the project uses TheMealDB developer test key `1` baked into the base URL.
