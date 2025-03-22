const { StatusCodes } = require("http-status-codes");
const communityService = require("../services/community.service");

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

module.exports = {
  createCommunity,
  getSingleCommunity,
  getAllCommunities,
};
