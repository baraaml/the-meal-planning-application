const Joi = require("joi");

const registerSchema = Joi.object({
  username: Joi.string()
    .trim()
    .lowercase()
    .custom((value, helpers) => {
      if (value.length < 3) {
        return helpers.error("string.min");
      }
      if (!/^[a-zA-Z][a-zA-Z0-9_]{2,29}$/.test(value)) {
        return helpers.error("string.pattern.base");
      }
      return value;
    })
    .required()
    .messages({
      "string.min": "Username must be at least 3 characters long.",
      "string.pattern.base":
        "Username must start with a letter and can only contain letters, numbers, and underscores.",
      "string.empty": "Username is required.",
    }),
  email: Joi.string()
    .trim()
    .lowercase()
    .custom((value, helpers) => {
      if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value)) {
        return helpers.error("string.email");
      }
      return value;
    })
    .required()
    .messages({
      "string.email": "Please provide a valid email address.",
      "string.empty": "Email is required.",
    }),

  password: Joi.string()
    .trim()
    .custom((value, helpers) => {
      if (value.length < 8) {
        return helpers.error("string.min");
      }
      if (value.length > 64) {
        return helpers.error("string.max");
      }
      if (!/(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[!@#$%^&*])/.test(value)) {
        return helpers.error("string.pattern.base");
      }
      return value;
    })
    .required()
    .messages({
      "string.min": "Password must be at least 8 characters long.",
      "string.max": "Password cannot be longer than 64 characters.",
      "string.pattern.base":
        "Password must contain at least one uppercase letter, one lowercase letter, one number, and one special character (!@#$%^&*).",
      "string.empty": "Password is required.",
    }),
});

const otpSchema = Joi.object({
  email: Joi.string()
    .trim()
    .lowercase()
    .custom((value, helpers) => {
      if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value)) {
        return helpers.error("string.email");
      }
      return value;
    })
    .required()
    .messages({
      "string.email": "Please provide a valid email address.",
      "string.empty": "Email is required.",
    }),
  otp: Joi.string()
    .length(6)
    .pattern(/^\d{6}$/)
    .required()
    .messages({
      "string.empty": "OTP is required",
      "string.pattern.base": "Invalid OTP",
    }),
});

const loginSchema = Joi.object({
  email: Joi.string()
    .trim()
    .lowercase()
    .custom((value, helpers) => {
      if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value)) {
        return helpers.error("string.email");
      }
      return value;
    })
    .required()
    .messages({
      "string.email": "Please provide a valid email address.",
      "string.empty": "Email is required.",
    }),

  password: Joi.string().trim().required().messages({
    "string.empty": "Password is required.",
  }),
});

module.exports = { registerSchema, otpSchema, loginSchema };
