const CustomAPIError = require("./customAPIError");
const UnauthenticatedError = require("./UnauthenticatedError");
const NotFoundError = require("./not-found");
const BadRequestError = require("./BadRequestError");
const UnauthorizedError = require("./UnautherizedError");
const TooManyRequestsError = require("./TooManyRequestsError");
const ConflictError = require("./ConflictError");
const ForbiddenError = require("./ForbiddenError");

module.exports = {
  CustomAPIError,
  UnauthenticatedError,
  NotFoundError,
  BadRequestError,
  UnauthorizedError,
  TooManyRequestsError,
  ConflictError,
  ForbiddenError,
};
