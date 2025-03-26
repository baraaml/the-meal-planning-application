/**
 * @fileoverview Community controller for handling HTTP requests
 * @module controllers/community
 */

const CustomAPIError = require("../errors");
const communityRepository = require("../repositories/community.repository");

/**
 * Service layer for community-related business logic
 */
class CommunityService {
  /**
   * Create a new community
   * @param {Object} data - Community data
   * @param {string} userId - User ID of the creator
   * @returns {Promise<Object>} Created community
   */
  async createCommunity(data, userId) {
    const { name, description, image, recipeCreationPermission, privacy } =
      data;

    // Check if community with same name already exists
    const existingCommunity = await communityRepository.findByName(name);
    if (existingCommunity) {
      throw new CustomAPIError.ConflictError(
        "A community with this name already exists"
      );
    }

    // Parse categories from request body, ensuring it's an array
    let parsedCategories = [];
    if (data.categories) {
      try {
        // If it's a string (from form data), parse it
        if (typeof data.categories === "string") {
          // Check if it looks like a JSON array string
          if (data.categories.trim().startsWith("[")) {
            parsedCategories = JSON.parse(data.categories);
          } else {
            // If it's a comma-separated string or single value
            parsedCategories = data.categories.split(",").map((c) => c.trim());
          }
        } else if (Array.isArray(data.categories)) {
          // If it's already an array, use it directly
          parsedCategories = data.categories;
        }
      } catch (error) {
        console.error("Error parsing categories:", error);
        throw new CustomAPIError.BadRequestError(
          "Categories must be a valid JSON array or comma-separated values"
        );
      }
    }

    // Base community data
    const communityData = {
      name,
      description,
      image,
      privacy: privacy || "PUBLIC",
      recipeCreationPermission: recipeCreationPermission || "ANY_MEMBER",
      ownerId: userId,
      // Create owner as ADMIN member
      members: {
        create: {
          userId,
          role: "ADMIN",
        },
      },
    };

    // Process categories if any
    if (parsedCategories.length > 0) {
      // Fetch categories by name
      const existingCategories =
        await communityRepository.findCategoriesByNames(parsedCategories);

      // Check for invalid categories
      const foundCategoryNames = existingCategories.map((cat) => cat.name);
      const invalidCategories = parsedCategories.filter(
        (name) => !foundCategoryNames.includes(name)
      );

      if (invalidCategories.length > 0) {
        throw new CustomAPIError.BadRequestError(
          `Categories not found: ${invalidCategories.join(", ")}`
        );
      }

      // Add categories to community data
      communityData.categories = {
        create: existingCategories.map((category) => ({
          category: {
            connect: { id: category.id },
          },
        })),
      };
    }

    // Create the community with all related data
    const newCommunity = await communityRepository.create(communityData);

    // Format the community data for response
    return this.formatCommunityResponse(newCommunity);
  }

  /**
   * Get a community by ID
   * @param {string} id - Community ID
   * @returns {Promise<Object>} Community data
   */
  async getCommunityById(id) {
    const community = await communityRepository.findById(id);

    if (!community) {
      throw new CustomAPIError.NotFoundError(
        `No community found with id: ${id}`
      );
    }

    return this.formatCommunityResponse(community);
  }

  /**
   * Get all communities
   * @returns {Promise<Array>} List of communities
   */
  async getAllCommunities() {
    const communities = await communityRepository.findAll();
    return communities.map((community) =>
      this.formatCommunityResponse(community)
    );
  }

  /**
   * Join a community
   * @param {string} communityId - Community ID
   * @param {string} userId - User ID
   * @returns {Promise<Object>} Updated community
   */
  async joinCommunity(communityId, userId) {
    /*
    - Check if community exists
    - Check if user is already a member
    - Check if community is private and user is not a member
    - Add user to community
    - Return updated community
    */
    const community = await communityRepository.findById(communityId);
    if (!community) {
      throw new CustomAPIError.NotFoundError(
        `Community with id ${communityId} not found`
      );
    }

    const isMember = await communityRepository.isMember(communityId, userId);
    if (isMember) {
      throw new CustomAPIError.ConflictError(
        "User is already a member of this community."
      );
    }

    const addedMember = await communityRepository.addMember(
      communityId,
      userId,
      "MEMBER"
    );

    return addedMember;
  }
    /**
   * Leaves a community
   * @param {string} communityId - Community ID
   * @param {string} userId - User ID
   * @returns {Promise<Object>} Updated community
   */
  async leaveCommunity(communityId, userId) {
    /**
     * check for communiy Id and userId first
     * check if user is already a member or not
     * ckeck if the user is the only admin
     *  if there are other users make make the one who joined after him an admin
     *  If there are no other members, delete the community.
     * If they are not the only one admin, just remove him.
     */
  }
  /**
   * Gets all the members of a community
   * @param {string} communityId - Community ID
   */
  async getAllMembers(id) {
    return await communityRepository.getAllMembers(id);
  }

  /**
   * Format community data for response
   * @param {Object} community - Raw community data
   * @returns {Object} Formatted community data
   */
  formatCommunityResponse(community) {
    return {
      ...community,
      categories: community.categories.map((cc) => cc.category),
    };
  }
}

module.exports = new CommunityService();
