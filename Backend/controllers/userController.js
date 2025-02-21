const {
  registerUser,
  generateAuthToken,
  comparePassword,
} = require("../models/userModel");
const BadRequestError = require("../errors/badRequestError");
const UnauthenticatedError = require("../errors/UnauthenticatedError");
const { StatusCodes } = require("http-status-codes");
const { registerSchema } = require("../validators/authValidator");
const { generateOTP, hashOTP, verifyHashedOTP } = require("../utils/otpUtlis");
const passwordUtils = require("../utils/passwordUtils");
const { sendVerificationEmail } = require("../utils/emailUtlis");

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

// @desc login a user
// @route api/v1/users/login
// @access Public
const loginUser = async (req, res) => {
  const { email, password } = req.body;

  if (!email) throw new BadRequestError("Please provide your email");
  if (!password) throw new BadRequestError("Please provide your password");

  // Find user by email
  const user = await prisma.user.findUnique({ where: { email } });
  if (!user) throw new UnauthenticatedError("Invalid email");

  // Check password
  const isPasswordCorrect = await comparePassword(password, user.password);
  if (!isPasswordCorrect) throw new UnauthenticatedError("Invalid password");

  // Generate token
  const token = generateAuthToken(user);

  res.status(StatusCodes.OK).json({
    success: true,
    msg: "Login successful",
    user,
    token,
  });
};

// @desc get all users
// @route api/v1/users
// @access Public
const getAllUsers = async (req, res) => {
  const users = await prisma.user.findMany();
  res.status(StatusCodes.OK).json(users);
};

// @desc get a single user
// @route api/v1/users/:id
// @access Public
const getSingleUser = async (req, res) => {
  const { id } = req.params;

  const user = await prisma.user.findUnique({ where: { id: Number(id) } });

  if (!user) throw new BadRequestError("User not found");

  res.status(StatusCodes.OK).json(user);
};

const logoutUser = async (req, res) => {
  res.send("Logout user");
};

const changePassword = async (req, res) => {
  res.send("Change password");
};

const updateUser = async (req, res) => {
  res.send("Update user");
};

const deleteUser = async (req, res) => {
  res.send("Delete user");
};

const verifyEmail = async (req, res) => {
  res.send("Verify email");
};

module.exports = {
  registerUser: registerUserController, // Renamed to avoid conflict with imported function
  loginUser,
  getAllUsers,
  getSingleUser,
  logoutUser,
  changePassword,
  updateUser,
  deleteUser,
  verifyEmail,
};
