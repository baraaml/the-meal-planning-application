const CustomAPIError = require("./customAPIError");
const { StatusCodes } = require("http-status-codes");

class ForbiddenError extends CustomAPIError {
  constructor(message, data = null) {
    super(message);
    this.statusCode = StatusCodes.FORBIDDEN;
    this.data = data; // Add custom data field
  }
}

module.exports = ForbiddenError;
