const { StatusCodes } = require("http-status-codes");

const errorHandlerMiddleware = (err, req, res, next) => {
  let customError = {
    statusCode: err.statusCode || StatusCodes.INTERNAL_SERVER_ERROR,
    msg: err.message || "Something went wrong, please try again later",
  };

  // Prisma Validation Error
  if (err.name === "PrismaClientValidationError") {
    customError.msg = "Invalid request data";
    customError.statusCode = StatusCodes.BAD_REQUEST;
  }

  // Prisma Known Request Error (e.g., Unique Constraint)
  if (err.name === "PrismaClientKnownRequestError") {
    if (err.code === "P2002") {
      customError.msg = `Duplicate value entered for ${Object.keys(
        err.meta.target
      )} field`;
      customError.statusCode = StatusCodes.BAD_REQUEST;
    }
  }

  // Joi Validation Errors (from request body validation)
  if (err.details) {
    customError.msg = "Validation Error";
    customError.statusCode = StatusCodes.BAD_REQUEST;
    customError.errors = err.details.map((detail) => detail.message);
  }

  // Mongoose Validation Errors
  if (err.name === "ValidationError") {
    customError.msg = Object.values(err.errors)
      .map((item) => item.message)
      .join(", ");
    customError.statusCode = StatusCodes.BAD_REQUEST;
  }

  // Duplicate Key Error (MongoDB)
  if (err.code && err.code === 11000) {
    customError.msg = `Duplicate value entered for ${Object.keys(
      err.keyValue
    )} field`;
    customError.statusCode = StatusCodes.BAD_REQUEST;
  }

  // Cast Error (Invalid ObjectId in MongoDB)
  if (err.name === "CastError") {
    customError.msg = `No item found with id: ${err.value}`;
    customError.statusCode = StatusCodes.NOT_FOUND;
  }

  // ✅ Include additional data from CustomAPIError (e.g., invalid categories)
  const response = {
    success: false,
    message: customError.msg,
    ...(err.data && { data: err.data }), // Include `err.data` only if it exists
  };

  // Include stack trace only in development
  if (process.env.NODE_ENV === "development") {
    response.stackTrace = err.stack;
  }

  return res.status(customError.statusCode).json(response);
};

module.exports = errorHandlerMiddleware;
