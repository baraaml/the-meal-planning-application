const express = require("express");

const {
  registerUser,
  loginUser,
  logoutUser,
  changePassword,
  verifyEmail,
  resendVerification,
  forgotPassword,
  refreshToken,
} = require("../controllers/authController");

// register
const validateRequest = require("../middlewares/validate");

// validators
const {
  registerSchema,
  otpSchema,
  loginSchema,
  resendVerificationSchema,
  forgotPasswordSchema,
} = require("../validators/authValidator");

const router = express.Router();

// Public Routes
router.post("/login", validateRequest(loginSchema), loginUser);
router.post("/register", validateRequest(registerSchema), registerUser);
router.post("/verify-email", validateRequest(otpSchema), verifyEmail);
router.post(
  "/resend-verification",
  validateRequest(resendVerificationSchema),
  resendVerification
);
router.post(
  "/forgot-password",
  validateRequest(forgotPasswordSchema),
  forgotPassword
);

// Protected Routes
router.post("/logout", logoutUser);
router.patch("/change-password", changePassword);
router.get("/refresh-token", refreshToken);

module.exports = router;
