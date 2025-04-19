// middlewares/parseFormData.js
const parseFormData = (req, res, next) => {
  try {
    // Convert `categories` from string to an array
    if (req.body.categories) {
      req.body.categories = JSON.parse(req.body.categories);
    }

    // Convert `recipeCreationPermission` to uppercase
    if (req.body.recipeCreationPermission) {
      req.body.recipeCreationPermission = req.body.recipeCreationPermission.toUpperCase();
    }

    next();
  } catch (error) {
    return res.status(400).json({
      success: false,
      error: "Invalid input format",
      message: "Ensure categories is a valid JSON array",
    });
  }
};

module.exports = parseFormData