const express = require("express");

const {
  registerUser,
  loginUser,
  logoutUser,
  changePassword,
  verifyEmail,
  resendVerification,
  forgetPassword,
  refreshToken,
} = require("../controllers/authController");

// register
const validateRequest = require("../middlewares/validate");
const { registerSchema, otpSchema } = require("../validators/authValidator");

const router = express.Router();

// Public Routes
router.post("/login", loginUser);
router.post("/register", validateRequest(registerSchema), registerUser);
router.post("/verify-email", validateRequest(otpSchema), verifyEmail);
router.post("/resend-verification", resendVerification);
router.post("/froget-password", forgetPassword);
router.post("/refresh-token", refreshToken);

// Protected Routes
router.post("/logout", logoutUser);
router.patch("/change-password", changePassword);

module.exports = router;
