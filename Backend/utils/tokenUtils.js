const jwt = require("jsonwebtoken");

class TokenUtils {
  constructor(secret, accessTokenLifetime, refreshTokenLifetime, passwordResetTokenLifetime) {
    if (!secret) {
      throw new Error("JWT_SECRET is required but not set");
    }
    this.secret = secret;
    this.accessTokenLifetime = accessTokenLifetime || "15m"; // Default to 15 minutes
    this.refreshTokenLifetime = refreshTokenLifetime || "1y"; // Default to 1 year
    this.passwordResetTokenLifetime = passwordResetTokenLifetime || "1h"; // Default to 1 hour
  }

  // Generate access token
  signAccessToken(payload) {
    return jwt.sign(payload, this.secret, {
      expiresIn: this.accessTokenLifetime,
      algorithm: "HS256",
    });
  }

  // Generate refresh token
  signRefreshToken(payload) {
    return jwt.sign(payload, this.secret, {
      expiresIn: this.refreshTokenLifetime,
      algorithm: "HS256",
    });
  }

  // Generate password reset token
  signPasswordResetToken(payload) {
    return jwt.sign(payload, this.secret, {
      expiresIn: this.passwordResetTokenLifetime,
      algorithm: "HS256",
    });
  }

  // Verify token
  verify(token) {
    try {
      return jwt.verify(token, this.secret);
    } catch (error) {
      console.error("JWT verification failed:", error.message);
      return null; // Return null instead of throwing an error
    }
  }
}

// Singleton instance
const tokenUtils = new TokenUtils(
  process.env.JWT_SECRET,
  process.env.JWT_ACCESS_TOKEN_LIFETIME,
  process.env.JWT_REFRESH_TOKEN_LIFETIME,
  process.env.JWT_PASSWORD_RESET_TOKEN_LIFETIME
);

module.exports = tokenUtils;
