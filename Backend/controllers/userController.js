const {
  registerUser,
  generateAuthToken,
  comparePassword,
} = require("../models/userModel");
const BadRequestError = require("../errors/badRequestError");
const UnauthenticatedError = require("../errors/UnauthenticatedError");
const { StatusCodes } = require("http-status-codes");
const { registerSchema } = require("../validators/authValidator");
const prisma = require("../config/prismaClient");

// @desc register a new user
// @route api/v1/users/register
// @access Public
const registerUserController = async (req, res) => {
  const { name, email, password } = req.body;

  // // Check if email exists
  // const emailExists = await prisma.user.findUnique({ where: { email } });
  // if (emailExists) throw new BadRequestError("Email already in use");

  // // Register user
  // const user = await registerUser({ name, email, password });

  // // Generate token
  // const token = generateAuthToken(user);

  // res.status(StatusCodes.CREATED).json({
  //   success: true,
  //   msg: "New user is created",
  //   user,
  //   token,
  // });

  res.status(StatusCodes.CREATED).json("New user is created");
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
