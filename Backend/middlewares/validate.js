const { StatusCodes } = require("http-status-codes");

const validateRequest = (schema) => (req, res, next) => {
  const { error } = schema.validate(req.body, { abortEarly: false }); // abortEarly: false will return all the errors

  if (error) {
    const errorMessages = error.details.map((err) => err.message);

    return res.status(StatusCodes.BAD_REQUEST).json({
      success: false,
      message: "Validation Error",
      errors: errorMessages, // send all the errors
    });
  }

  next();
};

module.exports = validateRequest;
