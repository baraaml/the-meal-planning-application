const { StatusCodes } = require("http-status-codes");
const CustomAPIError = require("./customAPIError");

class TooManyRequestsError extends CustomAPIError {
  constructor(message = "Too many requests. Please try again later.") {
    super(message);
    this.name = "TooManyRequestsError";
    this.statusCode = StatusCodes.TOO_MANY_REQUESTS;
  }
}

module.exports = TooManyRequestsError;
