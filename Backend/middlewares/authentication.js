const { PrismaClient } = require("@prisma/client");
const prisma = new PrismaClient();
const { UnauthenticatedError, UnauthorizedError } = require("../errors");
const tokenUtils = require("../utils/tokenUtils");

const authenticateUser = async (req, res, next) => {
  let accessToken;
  let refreshToken;

  // Check for access token in headers
  const authHeader = req.headers.authorization;
  if (authHeader && authHeader.startsWith("Bearer")) {
    accessToken = authHeader.split(" ")[1];
  }

  // Check for refresh token in request body 
  if (req.body?.refreshToken) {
    refreshToken = req.body.refreshToken;
  }

  if (!accessToken && !refreshToken) {
    throw new UnauthenticatedError("Authentication invalid");
  }

  try {
    if (accessToken) {
      // Verify access token
      const payload = tokenUtils.verify(accessToken);
      req.user = {
        userId: payload.userId,
        role: payload.role,
        tokenType: "access",
      };
      return next();
    }
  } catch (accessTokenError) {
    // Access token is invalid or expired - try refresh token
    if (!refreshToken) {
      throw new UnauthenticatedError("Authentication invalid");
    }

    try {
      // Verify refresh token and check database
      const refreshPayload = tokenUtils.verify(refreshToken);

      // Check if refresh token exists in database
      const storedToken = await prisma.refreshToken.findUnique({
        where: { token: refreshToken },
        include: { user: true },
      });

      if (!storedToken || storedToken.user.id !== refreshPayload.userId) {
        throw new UnauthenticatedError("Authentication invalid");
      }

      // Generate new tokens
      const newAccessToken = tokenUtils.signAccessToken({
        userId: storedToken.user.id,
        role: storedToken.user.role,
      });

      const newRefreshToken = tokenUtils.signRefreshToken({
        userId: storedToken.user.id,
      });

      // Update database with new refresh token (token rotation)
      await prisma.$transaction([
        prisma.refreshToken.delete({ where: { id: storedToken.id } }),
        prisma.refreshToken.create({
          data: {
            token: newRefreshToken,
            userId: storedToken.user.id,
            expiresAt: new Date(Date.now() + 365 * 24 * 60 * 60 * 1000), // 1 year
          },
        }),
      ]);

      // Attach new tokens to response
      res.status(200).json({
        success: true,
        accessToken: newAccessToken,
        refreshToken: newRefreshToken, // Send refresh token in response, not cookies
      });

      // Set user in request
      req.user = {
        userId: storedToken.user.id,
        role: storedToken.user.role,
        tokenType: "refresh",
      };

      next();
    } catch (refreshError) {
      throw new UnauthenticatedError("Please log in again.");
    }
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
