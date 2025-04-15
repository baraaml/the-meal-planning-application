const express = require("express");
const { authenticateUser } = require("../middlewares/authentication");

const { 
  getRecommendedMeals, 
  getMealById, 
  getTrendingMeals,
  recordMealInteraction,
  getQuickMeals,
  getCuisineRecommendations,
  getDietaryRecommendations,
  getSimilarMeals,
  getFilteredRecipes,
  getRecipesByCalories,
  advancedSearch  
} = require("../controllers/meal.controller");

const router = express.Router();

// Public routes
router.get("/search/advanced", advancedSearch);
router.get("/recommended", getRecommendedMeals);
router.get("/trending", getTrendingMeals);
router.get("/quick", getQuickMeals);
router.get("/cuisine/:cuisine_id", getCuisineRecommendations);
router.get("/dietary/:dietary_restriction", getDietaryRecommendations);
router.get("/similar/:id", getSimilarMeals);
router.get("/filter", getFilteredRecipes);
router.get("/filter/calories", getRecipesByCalories);
router.get("/:id", getMealById);

// Protected routes (require authentication)
router.post("/interaction", authenticateUser, recordMealInteraction);

module.exports = router;

