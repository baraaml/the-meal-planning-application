const CustomAPIError = require("./customAPIError");
const { StatusCodes } = require("http-status-codes");

class BadrequestError extends CustomAPIError {
  constructor(message, data = null) {
    super(message);
    this.statusCode = StatusCodes.BAD_REQUEST;
    this.data = data; // Add custom data field
  }
}

module.exports = BadrequestError;
