const express = require("express");

const {
  registerUser,
  loginUser,
  getSingleUser,
  getAllUsers,
  logoutUser,
  changePassword,
  updateUser,
  deleteUser,
  verifyEmail,
} = require("../controllers/userController");

const router = express.Router();

// Public Routes
router.post("/register", registerUser);
router.post("/login", loginUser);
router.post("/froget-password", loginUser);
router.get("/verify-email", verifyEmail);

// Protected Routes
router.post("/logout", logoutUser);
router.patch("/change-password", changePassword);

//  User Management Routes
router
  .route("/:id")
  .get(getSingleUser)
  .patch(updateUser)
  .delete(deleteUser);

// Admin Routes
router.get("/", getAllUsers);
router.get("/:id", getSingleUser);

module.exports = router;
