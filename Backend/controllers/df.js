const { StatusCodes } = require("http-status-codes");
const CustomAPIError = require("../errors");
const prisma = require("../config/prismaClient");

// Constants
const DEFAULT_CATEGORIES = [];
const SELECT_FIELDS = {
  id: true,
  name: true,
  description: true,
  privacy: true,
  recipeCreationPermission: true,
  createdAt: true,
  image: true,
  ownerId: true,
};

/**
 * Service class for handling community-related operations
 */
class CommunityService {
  /**
   * Validates if a community name is available
   * @param {string} name - The community name to check
   * @throws {ConflictError} If community name already exists
   */
  static async validateCommunityName(name) {
    const existingCommunity = await prisma.community.findUnique({
      where: { name },
      select: { id: true },
    });

    if (existingCommunity) {
      throw new CustomAPIError.ConflictError(
        "A community with this name already exists"
      );
    }
  }

  /**
   * Validates and retrieves category IDs from category names
   * @param {string[]} categoryNames - Array of category names
   * @returns {Promise<{categoryIds: string[], invalidCategories: string[]}>}
   */
  static async validateCategories(categoryNames) {
    if (!Array.isArray(categoryNames) || categoryNames.length === 0) {
      return { categoryIds: [], invalidCategories: [] };
    }

    const existingCategories = await prisma.category.findMany({
      where: { name: { in: categoryNames } },
      select: { id: true, name: true },
    });

    const categoryMap = new Map(
      existingCategories.map(({ name, id }) => [name, id])
    );

    const invalidCategories = categoryNames.filter(
      (name) => !categoryMap.has(name)
    );

    return {
      categoryIds: existingCategories.map(({ id }) => id),
      invalidCategories,
    };
  }

  /**
   * Creates a new community with optional categories
   * @param {Object} data - Community data
   * @param {string[]} categoryIds - Array of category IDs
   * @returns {Promise<Object>} Created community
   */
  static async createCommunity(data, categoryIds = []) {
    const createData = {
      ...data,
      ...(categoryIds.length > 0 && {
        categories: {
          connect: categoryIds.map((id) => ({ id })),
        },
      }),
    };

    return prisma.community.create({
      data: createData,
      select: SELECT_FIELDS,
    });
  }

  /**
   * Parses categories from request body
   * @param {string|string[]} categories - Categories from request
   * @returns {string[]} Parsed categories
   */
  static parseCategories(categories) {
    if (!categories) return DEFAULT_CATEGORIES;
    
    try {
      return typeof categories === "string"
        ? JSON.parse(categories)
        : categories;
    } catch (error) {
      throw new CustomAPIError.BadRequestError(
        "Invalid categories format. Expected JSON array of strings."
      );
    }
  }
}

/**
 * Creates a new community
 * @route POST /api/v1/community
 * @access Private
 */
const createCommunity = async (req, res) => {
  const { name, description, image, recipeCreationPermission, privacy } = req.body;
  const { userId } = req.user;

  try {
    // Validate community name
    await CommunityService.validateCommunityName(name);

    // Parse and validate categories
    const parsedCategories = CommunityService.parseCategories(req.body.categories);
    
    // Validate categories and get IDs
    const { categoryIds, invalidCategories } = 
      await CommunityService.validateCategories(parsedCategories);

    if (invalidCategories.length > 0) {
      throw new CustomAPIError.BadRequestError(
        "Some categories do not exist.",
        { invalidCategories }
      );
    }

    // Prepare community data
    const communityData = {
      name,
      description,
      image,
      privacy,
      recipeCreationPermission,
      ownerId: userId,
    };

    // Create community (with transaction if needed)
    const newCommunity = categoryIds.length > 0
      ? await prisma.$transaction(async () => {
          return CommunityService.createCommunity(communityData, categoryIds);
        })
      : await CommunityService.createCommunity(communityData);

    return res.status(StatusCodes.CREATED).json({
      success: true,
      message: "Community created successfully",
      data: newCommunity,
    });
  } catch (error) {
    // Let the global error handler handle known errors
    if (error instanceof CustomAPIError.CustomAPIError) {
      throw error;
    }

    // Log unexpected errors here (you can add proper logging)
    console.error("Unexpected error in createCommunity:", error);
    throw new CustomAPIError.InternalServerError(
      "An unexpected error occurred while creating the community"
    );
  }
};

module.exports = {
  createCommunity,
  // Export service for testing
  CommunityService,
};