const jwt = require("jsonwebtoken");

class TokenUtils {
  constructor(secret, lifetime) {
    if (!secret) {
      throw new Error("JWT_SECRET is required but not set");
    }
    this.secret = secret;
    this.lifetime = lifetime || "1h"; // Default to 1 hour
  }

  sign(payload) {
    return jwt.sign(payload, this.secret, {
      expiresIn: this.lifetime,
      algorithm: "HS256", // Explicitly setting algorithm for security
    });
  }

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
  process.env.JWT_LIFETIME
);

module.exports = tokenUtils;
