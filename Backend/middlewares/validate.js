const { StatusCodes } = require("http-status-codes");

const validateRequest = (schema, property = "body") => (req, res, next) => {
  const { error } = schema.validate(req[property], { abortEarly: false });

  if (error) {
    const errorDetails = {};

    error.details.forEach((err) => {
      const field = err.context.key;
      errorDetails[field] = err.message; // Assign error message per field
    });

    return res.status(StatusCodes.BAD_REQUEST).json({
      success: false,
      error: "ValidationError",
      message: "Invalid input data",
      details: errorDetails, // Send structured errors
    });
  }

  next();
};

module.exports = validateRequest;
