const { StatusCodes } = require("http-status-codes");
const prisma = require("../config/prismaClient");

// @desc Get the authenticated user
// @route GET /api/v1/users/me
// @access Private
const getSingleUser = async (req, res) => {
  res.status(StatusCodes.OK).json({ message: "User retrieved successfully" });
};

// @desc Get all users (Admin Only)
// @route GET /api/v1/users
// @access Admin
const getAllUsers = async (req, res) => {
  res.status(StatusCodes.OK).json({ message: "All users retrieved" });
};

// @desc Update the authenticated user
// @route PATCH /api/v1/users/me
// @access Private
const updateUser = async (req, res) => {
  res.status(StatusCodes.OK).json({ message: "User updated successfully" });
};

// @desc Delete the authenticated user
// @route DELETE /api/v1/users/me
// @access Private
const deleteUser = async (req, res) => {
  res.status(StatusCodes.OK).json({ message: "User deleted successfully" });
};

// @desc Delete a user by ID (Admin Only)
// @route DELETE /api/v1/users/:id
// @access Admin
const deleteUserById = async (req, res) => {
  res.status(StatusCodes.OK).json({ message: "User deleted successfully" });
};

module.exports = {
  getSingleUser,
  getAllUsers,
  updateUser,
  deleteUser,
  deleteUserById,
};
