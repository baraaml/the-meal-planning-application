const express = require("express");

const {
  registerUser,
  loginUser,
  getSingleUser,
  getAllUsers,
  logoutUser,
  changePassword,
  verifyEmail,
} = require("../controllers/authController");

// register
const validateRequest = require("../middlewares/validate");
const { registerSchema, otpSchema } = require("../validators/authValidator");

const router = express.Router();

// Public Routes
router.post("/login", loginUser);
router.post("/register", validateRequest(registerSchema), registerUser);
router.post("/verify-email", validateRequest(otpSchema), verifyEmail);
router.post("/resend-verification", loginUser);
router.post("/froget-password", loginUser);
router.post("/refresh-token", loginUser);

// Protected Routes
router.post("/logout", logoutUser);
router.patch("/change-password", changePassword);

// Admin Routes
router.get("/", getAllUsers);
router.get("/:id", getSingleUser);

module.exports = router;
