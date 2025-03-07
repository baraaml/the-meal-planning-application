const { PrismaClient } = require("@prisma/client");
const prisma = new PrismaClient();
const { UnauthenticatedError, UnauthorizedError } = require("../errors");
const tokenUtils = require("../utils/tokenUtils");

const authenticateUser = async (req, res, next) => {
  // Check for access token in headers
  const authHeader = req.headers.authorization;
  if (!authHeader || !authHeader.startsWith("Bearer ")) {
    throw new UnauthenticatedError("Authentication invalid");
  }

  const accessToken = authHeader.split(" ")[1];

  if (!accessToken) {
    throw new UnauthenticatedError("Authentication invalid");
  }

  try {
    // Verify access token
    const payload = tokenUtils.verify(accessToken);
    req.user = {
      userId: payload.userId,
      tokenType: "access",
    };
    next();
  } catch (error) {
    throw new UnauthenticatedError("Authentication invalid");
  }
};

const authorizeRoles = (...roles) => {
  return (req, res, next) => {
    if (!roles.includes(req.user.role)) {
      throw new UnauthorizedError("Unauthorized to access this route");
    }
    next();
  };
};

const validateTokenType = (type) => {
  return (req, res, next) => {
    if (req.user.tokenType !== type) {
      throw new UnauthorizedError(`Invalid token type for this operation`);
    }
    next();
  };
};

module.exports = {
  authenticateUser,
  authorizeRoles,
  validateTokenType,
};
