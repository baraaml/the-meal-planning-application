const { StatusCodes } = require("http-status-codes");
const recipeService = require("../services/recipeService");

// Get recommended meals for a user
const getRecommendedMeals = async (req, res) => {
  try {
    let userId = "default_user";
    if (req.user && req.user.userId) {
      userId = req.user.userId;
    }

    const limit = parseInt(req.query.limit) || 10;
    const meals = await recipeService.getRecommendedMeals(userId, limit);

    res.status(StatusCodes.OK).json({
      success: true,
      message: "Recommended meals retrieved successfully",
      count: meals.length,
      data: meals
    });
  } catch (error) {
    console.error("Error fetching recommended meals:", error);
    res.status(StatusCodes.INTERNAL_SERVER_ERROR).json({
      success: false,
      message: "Failed to retrieve recommended meals",
      error: error.message
    });
  }
};

// Get a single meal by ID
const getMealById = async (req, res) => {
  try {
    const mealId = req.params.id;
    const meal = await recipeService.getMealDetails(mealId);

    // Record view interaction
    if (req.user && req.user.userId) {
      await recipeService.recordInteraction(req.user.userId, mealId, "view");
    }

    res.status(StatusCodes.OK).json({
      success: true,
      message: "Meal retrieved successfully",
      data: meal
    });
  } catch (error) {
    console.error(`Error fetching meal ${req.params.id}:`, error);
    res.status(StatusCodes.NOT_FOUND).json({
      success: false,
      message: "Meal not found",
      error: error.message
    });
  }
};

// Get trending meals
const getTrendingMeals = async (req, res) => {
  try {
    const limit = parseInt(req.query.limit) || 10;
    const timeWindow = req.query.time_window || "week";

    const meals = await recipeService.getTrendingMeals(limit, timeWindow);

    res.status(StatusCodes.OK).json({
      success: true,
      message: "Trending meals retrieved successfully",
      count: meals.length,
      data: meals
    });
  } catch (error) {
    console.error("Error fetching trending meals:", error);
    res.status(StatusCodes.INTERNAL_SERVER_ERROR).json({
      success: false,
      message: "Failed to retrieve trending meals",
      error: error.message
    });
  }
};

// Record meal interaction (like, save, etc.)
const recordMealInteraction = async (req, res) => {
  try {
    const { meal_id, interaction_type, rating } = req.body;
    const userId = req.user.userId;

    await recipeService.recordInteraction(userId, meal_id, interaction_type, rating);

    res.status(StatusCodes.OK).json({
      success: true,
      message: `${interaction_type} recorded successfully`
    });
  } catch (error) {
    console.error("Error recording interaction:", error);
    res.status(StatusCodes.INTERNAL_SERVER_ERROR).json({
      success: false,
      message: "Failed to record interaction",
      error: error.message
    });
  }
};

// NEW CONTROLLER METHODS

// Get quick meals (recipes that can be prepared quickly)
const getQuickMeals = async (req, res) => {
  try {
    const maxTime = parseInt(req.query.max_time) || 30;
    const limit = parseInt(req.query.limit) || 10;
    const cuisine = req.query.cuisine || null;
    const dietaryRestriction = req.query.dietary_restriction || null;

    const meals = await recipeService.getQuickMeals(maxTime, limit, cuisine, dietaryRestriction);

    res.status(StatusCodes.OK).json({
      success: true,
      message: "Quick meals retrieved successfully",
      count: meals.length,
      data: meals
    });
  } catch (error) {
    console.error("Error fetching quick meals:", error);
    res.status(StatusCodes.INTERNAL_SERVER_ERROR).json({
      success: false,
      message: "Failed to retrieve quick meals",
      error: error.message
    });
  }
};

// Get cuisine recommendations
const getCuisineRecommendations = async (req, res) => {
  try {
    const cuisineId = req.params.cuisine_id;
    const limit = parseInt(req.query.limit) || 10;

    const meals = await recipeService.getCuisineRecommendations(cuisineId, limit);

    res.status(StatusCodes.OK).json({
      success: true,
      message: `${cuisineId} cuisine recommendations retrieved successfully`,
      count: meals.length,
      data: meals
    });
  } catch (error) {
    console.error(`Error fetching cuisine recommendations for ${req.params.cuisine_id}:`, error);
    res.status(StatusCodes.INTERNAL_SERVER_ERROR).json({
      success: false,
      message: "Failed to retrieve cuisine recommendations",
      error: error.message
    });
  }
};

// Get dietary recommendations
const getDietaryRecommendations = async (req, res) => {
  try {
    const dietaryRestriction = req.params.dietary_restriction;
    const limit = parseInt(req.query.limit) || 10;

    const meals = await recipeService.getDietaryRecommendations(dietaryRestriction, limit);

    res.status(StatusCodes.OK).json({
      success: true,
      message: `${dietaryRestriction} dietary recommendations retrieved successfully`,
      count: meals.length,
      data: meals
    });
  } catch (error) {
    console.error(`Error fetching dietary recommendations for ${req.params.dietary_restriction}:`, error);
    res.status(StatusCodes.INTERNAL_SERVER_ERROR).json({
      success: false,
      message: "Failed to retrieve dietary recommendations",
      error: error.message
    });
  }
};

// Get similar meals
const getSimilarMeals = async (req, res) => {
  try {
    const mealId = req.params.id;
    const limit = parseInt(req.query.limit) || 5;

    const meals = await recipeService.getSimilarMeals(mealId, limit);

    res.status(StatusCodes.OK).json({
      success: true,
      message: "Similar meals retrieved successfully",
      count: meals.length,
      data: meals
    });
  } catch (error) {
    console.error(`Error fetching similar meals for ${req.params.id}:`, error);
    res.status(StatusCodes.INTERNAL_SERVER_ERROR).json({
      success: false,
      message: "Failed to retrieve similar meals",
      error: error.message
    });
  }
};

// Get filtered recipes
const getFilteredRecipes = async (req, res) => {
  try {
    const options = {
      page: parseInt(req.query.page) || 1,
      limit: parseInt(req.query.limit) || 20,
      region: req.query.region,
      subRegion: req.query.sub_region,
      minCalories: req.query.min_calories ? parseInt(req.query.min_calories) : null,
      maxCalories: req.query.max_calories ? parseInt(req.query.max_calories) : null
    };

    const meals = await recipeService.getFilteredRecipes(options);

    res.status(StatusCodes.OK).json({
      success: true,
      message: "Filtered recipes retrieved successfully",
      count: meals.length,
      data: meals
    });
  } catch (error) {
    console.error("Error fetching filtered recipes:", error);
    res.status(StatusCodes.INTERNAL_SERVER_ERROR).json({
      success: false,
      message: "Failed to retrieve filtered recipes",
      error: error.message
    });
  }
};

// Get recipes by calorie range
const getRecipesByCalories = async (req, res) => {
  try {
    const min = parseFloat(req.query.min) || 0;
    const max = parseFloat(req.query.max) || 1000;
    const page = parseInt(req.query.page) || 1;
    const limit = parseInt(req.query.limit) || 20;

    const meals = await recipeService.getRecipesByCalories(min, max, page, limit);

    res.status(StatusCodes.OK).json({
      success: true,
      message: "Recipes by calorie range retrieved successfully",
      count: meals.length,
      data: meals
    });
  } catch (error) {
    console.error("Error fetching recipes by calories:", error);
    res.status(StatusCodes.INTERNAL_SERVER_ERROR).json({
      success: false,
      message: "Failed to retrieve recipes by calorie range",
      error: error.message
    });
  }
};
/**
 * Advanced search for recipes with multiple criteria
 * @route GET /api/v1/meal/search/advanced
 * @access Public
 */
const advancedSearch = async (req, res) => {
  try {
    // Extract all search parameters from query and format them properly
    const criteria = {
      // Text search
      query: req.query.query || null,

      // Array parameters (convert comma-separated strings to arrays)
      cuisines: req.query.cuisines ? req.query.cuisines.split(',').map(c => c.trim()) : [],
      dietaryRestrictions: req.query.dietary ? req.query.dietary.split(',').map(d => d.trim()) : [],
      includeIngredients: req.query.include_ingredients ? req.query.include_ingredients.split(',').map(i => i.trim()) : [],
      excludeIngredients: req.query.exclude_ingredients ? req.query.exclude_ingredients.split(',').map(i => i.trim()) : [],

      // Numerical parameters (convert to numbers when present)
      minCalories: req.query.min_calories ? parseInt(req.query.min_calories) : null,
      maxCalories: req.query.max_calories ? parseInt(req.query.max_calories) : null,
      maxPrepTime: req.query.max_prep_time ? parseInt(req.query.max_prep_time) : null,
      maxCookTime: req.query.max_cook_time ? parseInt(req.query.max_cook_time) : null,
      maxTotalTime: req.query.max_total_time ? parseInt(req.query.max_total_time) : null,

      // Sorting and pagination
      sortBy: req.query.sort_by || 'relevance',
      sortOrder: req.query.sort_order || 'desc',
      page: parseInt(req.query.page || '1'),
      limit: parseInt(req.query.limit || '20')
    };

    console.log('Advanced search criteria:', criteria);

    // Perform the search
    const meals = await recipeService.advancedSearch(criteria);

    // Return the results
    res.status(StatusCodes.OK).json({
      success: true,
      message: "Advanced search completed successfully",
      count: meals.length,
      page: criteria.page,
      limit: criteria.limit,
      data: meals
    });
  } catch (error) {
    console.error("Error performing advanced search:", error);

    // Provide more detailed error information in the response
    res.status(StatusCodes.INTERNAL_SERVER_ERROR).json({
      success: false,
      message: "Failed to perform advanced search",
      error: error.message,
      details: error.response?.data?.detail || "No additional details available"
    });
  }
};
module.exports = {
  advancedSearch,
  getRecommendedMeals,
  getMealById,
  getTrendingMeals,
  recordMealInteraction,
  getQuickMeals,
  getCuisineRecommendations,
  getDietaryRecommendations,
  getSimilarMeals,
  getFilteredRecipes,
  getRecipesByCalories
};