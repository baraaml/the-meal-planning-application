const { StatusCodes } = require("http-status-codes");
const errorHandlerMiddleware = (err, req, res, next) => {
  let customError = {
    // set default
    statusCode: err.statusCode || StatusCodes.INTERNAL_SERVER_ERROR,
    msg: err.message || "Something went wrong try again later",
  };

  if (err.name === "ValidationError") {
    customError.msg = Object.values(err.errors)
      .map((item) => item.message)
      .join(", ");
    customError.statusCode = StatusCodes.BAD_REQUEST;
  }

  if (err.errors) {
    // Handling validation errors from Joi
    customError.msg = "Validation Error";
    customError.statusCode = StatusCodes.BAD_REQUEST;
    customError.errors = err.errors; // Send array of validation errors
  }

  if (err.name === "ValidationError") {
    customError.msg = Object.values(err.errors)
      .map((item) => item.message)
      .join(",");
    customError.statusCode = 400;
  }
  if (err.code && err.code === 11000) {
    customError.msg = `Duplicate value entered for ${Object.keys(
      err.keyValue
    )} field, please choose another value`;
    customError.statusCode = 400;
  }
  if (err.name === "CastError") {
    customError.msg = `No item found with id : ${err.value}`;
    customError.statusCode = 404;
  }

  // Log the error for debugging
  // console.error(`[Error] ${err.message}`);
  // if (err.stack) console.error(err.stack);

  const response = {
    success: false,
    message: customError.msg,
  };

  // Include stack trace in development mode
  if (process.env.NODE_ENV === "development") {
    response.stackTrace = err.stack;
  }

  return res.status(customError.statusCode).json({ response });
};

module.exports = errorHandlerMiddleware;
