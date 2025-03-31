/**
 * @fileoverview Community controller for handling HTTP requests
 * @module controllers/community
 */

const { StatusCodes } = require("http-status-codes");
const communityService = require("../services/community.service");
const CustomAPIError = require("../errors");
const { community } = require("../config/prismaClient");

/**
 * Creates a new community
 * @route POST /api/v1/community
 * @access Private
 */
const createCommunity = async (req, res) => {
  const { userId } = req.user;

  // Use the service to create the community
  const community = await communityService.createCommunity(req.body, userId);

  res.status(StatusCodes.CREATED).json({
    success: true,
    message: "Community created successfully",
    community,
  });
};

/**
 * Gets a single community by ID
 * @route GET /api/v1/community/:id
 * @access Private
 */
const getSingleCommunity = async (req, res) => {
  const { id } = req.params;

  // Use the service to get the community
  const community = await communityService.getCommunityById(id);

  res.status(StatusCodes.OK).json({
    success: true,
    community,
  });
};

/**
 * Gets all communities
 * @route GET /api/v1/community
 * @access Private
 */
const getAllCommunities = async (req, res) => {
  // Use the service to get all communities
  const communities = await communityService.getAllCommunities();

  res.status(StatusCodes.OK).json({
    success: true,
    count: communities.length,
    communities,
  });
};

/**
 * Joins a user (as a member) to a community
 * @route POST /api/v1/community/:id/members
 * @acess Private
 */
const joinCommunity = async (req, res) => {
  // Get the user id from the auth layer
  const { userId } = req.user;
  const { id } = req.params;

  console.log(`Community id: ${id}`);
  const community = await communityService.getCommunityById(id);

  console.log(community);
  const addedMember = await communityService.joinCommunity(id, userId);

  res.status(StatusCodes.CREATED).json({
    success: true,
    message: "Successfully joined the community.",
    community: addedMember,
  });
};

/**
 * Leaves a user (as a member) from a community
 * @route DELETE /api/v1/community/:id/leave
 * @acess Private
 */
const leaveCommunity = async (req, res) => {
  // Get the user id from the auth layer
  const { userId } = req.user;

  const { id } = req.params;

  console.log(`Community id: ${id}`);
  const community = await communityService.leaveCommunity(id);
  console.log(community);

  res.status(StatusCodes.CREATED).json({
    message: "Successfully left the community.",
    community: addedMember,
  });
};

/**
 * Gets all members of a community
 * @route GET /api/v1/communiy/:id/members
 * @access Private
 */
const getAllMembers = async (req, res) => {
  const { id } = req.params;

  const members = await communityService.getAllMembers(id);

  res.status(StatusCodes.OK).json({
    success: true,
    count: members.length,
    members: members,
  });
};

/**
 * Sets list of members as admins
 * @route PATCH /api/v1/community/:id/admins
 * @access Private
 */
const setAdmins = async (req, res) => {
  // Get the user id from the auth layer
  const { userId } = req.user;
  const { id } = req.params;
  const { memberIDs } = req.body;

  const newAdmins = await communityService.setAdmins(id, userId, memberIDs);

  res.status(StatusCodes.OK).json({
    success: true,
    message: "Members successfully promoted to admins.",
    admins: newAdmins,
  });
};

/**
 * Deletes a community with Id
 * @route DELETE /api/v1/community/:id/
 * @access Private
 */
const deleteCommunity = async (req, res) => {
  res.status(StatusCodes.OK).json({
    success: true,
    message: "Community is deleted successfully.",
  });
};

module.exports = {
  createCommunity,
  getSingleCommunity,
  getAllCommunities,
  joinCommunity,
  getAllMembers,
  leaveCommunity,
  setAdmins,
  deleteCommunity,
};
