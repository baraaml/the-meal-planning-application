/**
 * @fileoverview Community controller for handling HTTP requests
 * @module controllers/community
 */

const { StatusCodes } = require("http-status-codes");
const communityService = require("../services/community.service");
const { clearCache } = require("../middlewares/cache");

/**
 * Creates a new community
 * @route POST /api/v1/community
 * @access Private
 */
const createCommunity = async (req, res) => {
  const { userId } = req.user;

  // Use the service to create the community
  const community = await communityService.createCommunity(req.body, userId);

  // Clear relevant caches when creating a new community
  clearCache(['/api/v1/community']);

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
 * Gets all communities with pagination, sorting and filtering
 * @route GET /api/v1/community
 * @access Private
 */
const getAllCommunities = async (req, res) => {
  // Extract pagination, sorting and filtering parameters
  const { page, limit, sort, order, name, privacy, categories } = req.query;
  
  // Prepare filter object
  const filter = {};
  if (name) filter.name = name;
  if (privacy) filter.privacy = privacy;
  if (categories) {
    filter.categories = Array.isArray(categories) 
      ? categories 
      : categories.split(',').map(c => c.trim());
  }

  // Use the service to get all communities
  const { communities, total } = await communityService.getAllCommunities({
    page: parseInt(page, 10) || 1,
    limit: parseInt(limit, 10) || 10,
    sort: sort || 'createdAt',
    order: order || 'desc',
    filter
  });

  res.status(StatusCodes.OK).json({
    success: true,
    count: communities.length,
    total,
    currentPage: parseInt(page, 10) || 1,
    totalPages: Math.ceil(total / (parseInt(limit, 10) || 10)),
    communities,
  });
};

/**
 * Joins a user (as a member) to a community
 * @route POST /api/v1/community/:id/join
 * @acess Private
 */
const joinCommunity = async (req, res) => {
  const { userId } = req.user;
  const { id: communityId } = req.params;

  const membership = await communityService.joinCommunity(communityId, userId);

  // Clear relevant caches
  clearCache([
    `/api/v1/community/${communityId}`,
    `/api/v1/community/${communityId}/members`,
    `/api/v1/community/me/joined`
  ]);

  res.status(StatusCodes.CREATED).json({
    success: true,
    message: "Successfully joined the community",
    membership,
  });
};

/**
 * Gets all members of a community with pagination
 * @route GET /api/v1/communiy/:id/members
 * @access Private
 */
const getAllMembers = async (req, res) => {
  const { id: communityId } = req.params;
  const { page, limit, role } = req.query;

  const { members, total } = await communityService.getAllMembers(communityId, {
    page: parseInt(page, 10) || 1,
    limit: parseInt(limit, 10) || 10,
    role
  });

  res.status(StatusCodes.OK).json({
    success: true,
    count: members.length,
    total,
    currentPage: parseInt(page, 10) || 1,
    totalPages: Math.ceil(total / (parseInt(limit, 10) || 10)),
    members,
  });
};

module.exports = {
  createCommunity,
  getSingleCommunity,
  getAllCommunities,
  joinCommunity,
  getAllMembers,
};