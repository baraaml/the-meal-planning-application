const Joi = require("joi");

const emailSchema = Joi.string()
  .trim()
  .lowercase()
  .pattern(/^[^\s@]+@[^\s@]+\.[^\s@]+$/)
  .required()
  .messages({
    "string.pattern.base": "Please provide a valid email address.",
    "string.empty": "Email is required.",
  });

const passwordSchema = Joi.string()
  .trim()
  .min(8)
  .max(64)
  .pattern(/(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[!@#$%^&*])/)
  .required()
  .messages({
    "string.min": "Password must be at least 8 characters long.",
    "string.max": "Password cannot be longer than 64 characters.",
    "string.pattern.base":
      "Password must contain at least one uppercase letter, one lowercase letter, one number, and one special character (!@#$%^&*).",
    "string.empty": "Password is required.",
  });

const usernameSchema = Joi.string()
  .trim()
  .lowercase()
  .pattern(/^[a-zA-Z][a-zA-Z0-9_]{2,29}$/)
  .required()
  .messages({
    "string.pattern.base":
      "Username must start with a letter and can only contain letters, numbers, and underscores.",
    "string.empty": "Username is required.",
  });

const registerSchema = Joi.object({
  username: usernameSchema,
  email: emailSchema,
  password: passwordSchema,
});

const loginSchema = Joi.object({
  email: emailSchema,
  password: Joi.string().trim().required().messages({
    "string.empty": "Password is required.",
  }),
});

const otpSchema = Joi.object({
  email: emailSchema,
  otp: Joi.string()
    .length(6)
    .pattern(/^\d{6}$/)
    .required()
    .messages({
      "string.empty": "OTP is required",
      "string.pattern.base": "Invalid OTP",
    }),
});

const resendVerificationSchema = Joi.object({
  email: emailSchema,
});

const forgotPasswordSchema = Joi.object({
  email: emailSchema,
});

const resetPasswordSchema = Joi.object({
  password: passwordSchema,
  token: Joi.string().required().messages({
    "string.empty": "Refresh token is required.",
  }),
});

const refreshTokenSchema = Joi.object({
  refreshToken: Joi.string().required().messages({
    "string.empty": "Refresh token is required.",
  }),
});

module.exports = {
  registerSchema,
  loginSchema,
  otpSchema,
  resendVerificationSchema,
  forgotPasswordSchema,
  resetPasswordSchema,
  refreshTokenSchema,
};
