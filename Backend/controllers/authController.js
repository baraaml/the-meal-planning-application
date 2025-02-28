const {
  registerUser,
  generateAuthToken,
  comparePassword,
} = require("../models/userModel");
const BadRequestError = require("../errors/badRequestError");
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
const { sendVerificationEmail } = require("../utils/emailUtlis");
const tokenUtils = require("../utils/tokenUtils");
const prisma = require("../config/prismaClient");

// @desc register a new user
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
    if (existingUser.email === email.toLowerCase()) {
      throw new BadRequestError("Email already registered");
    }
    if (existingUser.username === username.toLowerCase()) {
      throw new BadRequestError("Username already registered");
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

// @desc verify the otp code and activate the account
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

// @desc login a user
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
  res.send("Resend verification otp");
};

const forgetPassword = async (req, res) => {
  res.send("Forget password");
};

const refreshToken = async (req, res) => {
  res.send("refresh-token");
};

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
  forgetPassword,
  refreshToken,
};
