class CustomAPIError extends Error {
  constructor(message, statusCode = 500, data = null) {
    super(message);
    this.statusCode = statusCode;
    this.data = data;
  }
}

module.exports = CustomAPIError;
