const CustomAPIError = require("./customAPIError");
const { StatusCodes } = require("http-status-codes");

class ConflictError extends CustomAPIError {
  constructor(message) {
    super(message);
    this.statusCode = StatusCodes.CONFLICT; // 409
  }
}

module.exports = ConflictError;
