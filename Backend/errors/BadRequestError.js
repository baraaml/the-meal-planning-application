const CustomAPIError = require("./customAPIError");
const { StatusCodes } = require("http-status-codes");

class BadrequestError extends CustomAPIError {
  constructor(message) {
    super(message);
    this.statusCode = StatusCodes.BAD_REQUEST;
  }
}

module.exports = BadrequestError;
