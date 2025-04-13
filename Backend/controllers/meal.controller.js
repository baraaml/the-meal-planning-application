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

module.exports = {
  getRecommendedMeals,
  getMealById,
  getTrendingMeals,
  recordMealInteraction
};