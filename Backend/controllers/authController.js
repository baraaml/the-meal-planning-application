const {
  registerUser,
  generateAuthToken,
  comparePassword,
} = require("../models/userModel");
const BadRequestError = require("../errors/BadRequestError");
const CustomAPIError = require("../errors");
const UnauthenticatedError = require("../errors/UnauthenticatedError");
const { StatusCodes } = require("http-status-codes");
const { registerSchema } = require("../validators/authValidator");
const {
  generateOTP,
  hashOTP,
  verifyHashedOTP,
  deleteOldOTPs,
} = require("../utils/otpUtlis");
const passwordUtils = require("../utils/passwordUtils");
const {
  sendVerificationEmail,
  sendPasswordResetEmail,
} = require("../utils/emailUtlis");
const tokenUtils = require("../utils/tokenUtils");
const prisma = require("../config/prismaClient");
const BadrequestError = require("../errors/BadRequestError");

// @desc (POST) register a new user
// @route api/v1/users/register
// @access Public
const registerUserController = async (req, res) => {
  const { username, email, password } = req.body;

  // // Check if email or username exist
  const existingUser = await prisma.user.findFirst({
    where: {
      OR: [
        { email: email.toLowerCase() },
        { username: username.toLowerCase() },
      ],
    },
  });
  if (existingUser) {
    if (existingUser.username === username.toLowerCase()) {
      throw new CustomAPIError.ConflictError("Username already registered");
    }
    if (existingUser.email === email.toLowerCase()) {
      throw new CustomAPIError.ConflictError("Email already registered");
    }
  }

  // hash the password
  const hashedPassword = await passwordUtils.hash(password);

  // generate and hash OTP
  const otp = generateOTP();
  const hashedOTP = await hashOTP(otp);

  // Set OTP expiration time (15 minutes)
  const expirationTime = new Date();
  expirationTime.setMinutes(expirationTime.getMinutes() + 15);

  // store the user data in the database with isVerified: false
  const user = await prisma.$transaction(async (prisma) => {
    const newUser = await prisma.user.create({
      data: {
        username: username.toLowerCase(),
        email: email.toLowerCase(),
        password: hashedPassword,
        isVerified: false,
      },
    });

    await prisma.verificationCode.create({
      data: {
        code: hashedOTP,
        expiresAt: expirationTime,
        userId: newUser.id,
      },
    });

    return newUser;
  });

  // send the verification code to the user email
  await sendVerificationEmail(email, otp);

  // send response
  res.status(StatusCodes.CREATED).json({
    success: true,
    message:
      "User account created successfully. Please check your email for the verification code.",
  });
};

// @desc (POST) verify the otp code and activate the account
// @route api/v1/users/verify-email
// @access Public
const verifyEmail = async (req, res) => {
  const { otp, email } = req.body;

  // Find the verification entry for the given email
  const otpExists = await prisma.verificationCode.findFirst({
    where: {
      isUsed: false,
      expiresAt: { gt: new Date() },
      user: { email: email.toLowerCase() },
    },
    include: { user: true },
  });

  if (!otpExists || !(await verifyHashedOTP(otp, otpExists.code))) {
    throw new UnauthenticatedError("Invalid or expired OTP");
  }

  const userId = otpExists.user.id;

  await prisma.$transaction(async (prisma) => {
    // Mark the user as verified
    await prisma.user.update({
      where: { id: userId },
      data: {
        isVerified: true,
        verifiedAt: new Date(),
      },
    });

    // Remove the used OTP entry
    await prisma.verificationCode.delete({
      where: { id: otpExists.id },
    });
  });

  // Generate short-lived access token
  const accessToken = tokenUtils.signAccessToken({ userId });

  // Generate long-lived refresh token
  const refreshToken = tokenUtils.signRefreshToken({ userId });
  const refreshTokenExpiry = new Date();
  refreshTokenExpiry.setFullYear(refreshTokenExpiry.getFullYear() + 1); // 1 year expiry

  // Store refresh token in database
  await prisma.refreshToken.create({
    data: {
      token: refreshToken,
      userId,
      expiresAt: refreshTokenExpiry,
    },
  });

  res.status(StatusCodes.ACCEPTED).json({
    success: true,
    message: "Email verified successfully",
    data: {
      accessToken,
      refreshToken,
      user: {
        id: userId,
        email: otpExists.user.email,
        isVerified: true,
      },
    },
  });
};

// @desc (POST) login a user
// @route api/v1/users/login
// @access Public
const loginUser = async (req, res) => {
  const { email, password } = req.body;

  // Find user by email
  const user = await prisma.user.findUnique({ where: { email } });
  if (!user) throw new UnauthenticatedError("Invalid email");

  // ensure the user is verified
  if (user.isVerified === false) {
    throw new UnauthenticatedError(
      "Account not verified. Please verify your email"
    );
  }

  // Check password
  const isPasswordCorrect = await comparePassword(password, user.password);
  if (!isPasswordCorrect) throw new UnauthenticatedError("Invalid password");

  // Generate short-lived access token
  const accessToken = tokenUtils.signAccessToken({ userId: user.id });

  // Generate long-lived refresh token
  const refreshToken = tokenUtils.signRefreshToken({ userId: user.id });
  const refreshTokenExpiry = new Date();
  refreshTokenExpiry.setFullYear(refreshTokenExpiry.getFullYear() + 1); // 1 year expiry

  // Remove existing refresh tokens before adding a new one
  await prisma.$transaction(async (prisma) => {
    await prisma.refreshToken.deleteMany({ where: { userId: user.id } });
    // Store refresh token in the database
    await prisma.refreshToken.create({
      data: {
        token: refreshToken,
        userId: user.id,
        expiresAt: refreshTokenExpiry,
      },
    });
  });

  res.status(StatusCodes.OK).json({
    success: true,
    message: "Login successful",
    data: {
      accessToken: accessToken,
      refreshToken: refreshToken,
      user: {
        id: user.id,
        email: user.email,
        isVerified: user.isVerified,
      },
    },
  });
};

const logoutUser = async (req, res) => {
  res.send("Logout user");
};

const resendVerification = async (req, res) => {
  // getting the inputs
  const { email } = req.body;

  const user = await prisma.user.findUnique({
    where: {
      email: email,
    },
  });

  // Ensure the user exists in the database
  if (!user) {
    throw new BadRequestError("User not found");
  }

  // Ensure the user is not already verified
  if (user.isVerified === true) {
    throw new BadRequestError(
      "User is already verified, Please go to login page"
    );
  }

  // getting the last otp if exists
  const lastOtp = await prisma.verificationCode.findFirst({
    where: {
      userId: user.id,
    },
    orderBy: {
      createdAt: "desc",
    },
  });

  console.log(lastOtp);

  // Check rate limiting (only allow resending once per minute)
  if (lastOtp) {
    const now = new Date();
    const lastRequestTime = new Date(lastOtp.createdAt);
    lastRequestTime.setMinutes(lastRequestTime.getMinutes() + 1);

    console.log(now);
    console.log(lastRequestTime);

    if (now < lastRequestTime) {
      throw new CustomAPIError.TooManyRequestsError(
        "Please wait before requesting another OTP."
      );
    }
  }

  // generate and hash new OTP
  const otp = generateOTP();
  const hashedOTP = await hashOTP(otp);
  const expirationTime = new Date();
  expirationTime.setMinutes(expirationTime.getMinutes() + 15);

  // store hashed otp and delete old ones
  await prisma.$transaction(async (prisma) => {
    await prisma.verificationCode.deleteMany({ where: { userId: user.id } });
    await prisma.verificationCode.create({
      data: {
        code: hashedOTP,
        expiresAt: expirationTime,
        userId: user.id,
      },
    });
  });

  // send the OTP to the user email
  await sendVerificationEmail(email, otp);

  res.status(StatusCodes.OK).json({
    success: true,
    message: "A new verification code has been sent to your email.",
  });
};

const forgotPassword = async (req, res) => {
  const BASE_WEB_URL = "https://mealflow.ddns.net/passwordrecovery"; // Web fallback
  const BASE_APP_URL = "mealflow://reset-password"; // Deep Link for the app

  const { email } = req.body;

  const user = await prisma.user.findUnique({
    where: { email },
  });

  if (!user) {
    throw new BadRequestError("User not found");
  }

  // Check if a reset token already exists
  await prisma.passwordResetToken.deleteMany({
    where: { userId: user.id },
  });

  // Generate a password reset token
  const resetToken = tokenUtils.signPasswordResetToken({ userId: user.id });
  const expiresAt = new Date(Date.now() + 60 * 60 * 1000); // 1 hour expiration

  // store the reset token in db
  const newToken = await prisma.passwordResetToken.create({
    data: {
      token: resetToken,
      userId: user.id,
      expiresAt: expiresAt,
    },
  });

  // Construct deep link & web fallback
  const queryParams = new URLSearchParams({
    // $deep_link: "true",
    token: resetToken,
  }).toString();
  const resetLink = `${BASE_WEB_URL}?${queryParams}`;
  const appResetLink = `${BASE_APP_URL}?${queryParams}`;

  // Send email with both links
  await sendPasswordResetEmail(email, resetLink, appResetLink);

  res.status(StatusCodes.OK).json({
    success: true,
    message: "Password reset email sent successfully",
    data: {
      token: resetToken,
    },
  });
};

const resetPassword = async (req, res) => {
  const { token, newPassword } = req.body;

  // Verify the token
  const decoded = tokenUtils.verify(token);
  if (!decoded) {
    throw new UnauthenticatedError("Invalid or expired token");
  }

  // Check if token exists in database and is valid
  const resetTokenEntry = await prisma.passwordResetToken.findFirst({
    where: {
      token: token,
      isUsed: false,
      expiresAt: { gt: new Date() },
      userId: decoded.userId,
    },
  });

  if (!resetTokenEntry) {
    throw new UnauthenticatedError("Invalid or expired token");
  }

  // Hash new password
  const hashedPassword = await passwordUtils.hash(newPassword);

  // Update password and mark token as used in a transaction
  await prisma.$transaction(async (prisma) => {
    await prisma.user.update({
      where: { id: decoded.userId },
      data: { password: hashedPassword },
    });

    await prisma.passwordResetToken.update({
      where: { id: resetTokenEntry.id },
      data: { used: true },
    });
  });

  res.status(StatusCodes.OK).json({
    success: true,
    message: "Password reset successfully",
  });
};

const refreshAccessToken = async (req, res) => {
  const { refreshToken } = req.body;

  // Verify refresh token signature and expiration
  let decoded = tokenUtils.verify(refreshToken);

  if (!decoded) {
    throw new CustomAPIError.UnauthenticatedError(
      "Invalid or expired refresh token"
    );
  }

  // Find matching refresh token in database
  const existingToken = await prisma.refreshToken.findFirst({
    where: {
      token: refreshToken,
      userId: decoded.userId,
      expiresAt: { gt: new Date() },
    },
  });

  if (!existingToken) {
    throw new UnauthenticatedError("Invalid or expired refresh token");
  }

  // Generate a new access token
  const newAccessToken = tokenUtils.signAccessToken({ userId: decoded.userId });

  res.status(StatusCodes.OK).json({
    success: true,
    message: "New access token generated",
    data: { accessToken: newAccessToken },
  });
};

// @desc (POST) change the password of a user
// @route api/v1/users/change-password
// @access Private
const changePassword = async (req, res) => {
  res.send("Change password");
};

module.exports = {
  registerUser: registerUserController, // Renamed to avoid conflict with imported function
  loginUser,
  logoutUser,
  changePassword,
  verifyEmail,
  resendVerification,
  forgotPassword,
  refreshAccessToken,
  resetPassword,
};
