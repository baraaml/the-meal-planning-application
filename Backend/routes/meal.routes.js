const express = require("express");
const { authenticateUser } = require("../middlewares/authentication");
const { getRecommendedMeals } = require("../controllers/meal.controller");
const router = express.Router();

// Route for getting recommended meals
router.get("/recommended", getRecommendedMeals);

// Additional routes can be added here
// For example:
// router.post("/", authenticateUser, createMeal);
// router.get("/:id", getMealById);
// router.patch("/:id", authenticateUser, updateMeal);
// router.delete("/:id", authenticateUser, deleteMeal);

module.exports = router;