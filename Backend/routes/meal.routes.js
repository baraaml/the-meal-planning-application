const express = require("express");
const { authenticateUser } = require("../middlewares/authentication");
const { 
  getRecommendedMeals, 
  getMealById, 
  getTrendingMeals,
  recordMealInteraction
} = require("../controllers/meal.controller");

const router = express.Router();

// Public routes
router.get("/recommended", getRecommendedMeals);
router.get("/trending", getTrendingMeals);
router.get("/:id", getMealById);

// Protected routes (require authentication)
router.post("/interaction", authenticateUser, recordMealInteraction);

module.exports = router;