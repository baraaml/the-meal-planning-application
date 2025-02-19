const { StatusCodes } = require("http-status-codes");

const validate = (schema) => (req, res, next) => {
  const { error } = schema.validate(req.body, { abortEarly: false });

  if (error) {
    const errorMessages = error.details.map((err) => err.message);

    // Pass to the centralized error handler
    return next({
      statusCode: StatusCodes.BAD_REQUEST,
      message: "Validation Error",
      errors: errorMessages, // Send array of validation errors
    });
  }

  next();
};

module.exports = { validate };
