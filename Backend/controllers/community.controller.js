const { StatusCodes } = require("http-status-codes");
const CustomAPIError = require("../errors");
const prisma = require("../config/prismaClient");

/**
 * Creates a new community
 * @route POST /api/v1/community
 * @access Private
 */
const createCommunity = async (req, res) => {
  const { name, description, image, recipeCreationPermission, privacy } =
    req.body;
  const { userId } = req.user;

  // Check if community with same name already exists
  const existingCommunity = await prisma.community.findFirst({
    where: { name },
  });

  if (existingCommunity) {
    throw new CustomAPIError.ConflictError(
      "A community with this name already exists"
    );
  }

  // Parse categories from request body
  const { categories = "[]" } = req.body;
  const parsedCategories =
    typeof categories === "string" ? JSON.parse(categories) : categories;

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

  if (parsedCategories.length > 0) {
    // Fetch categories by name
    const existingCategories = await prisma.category.findMany({
      where: { name: { in: parsedCategories } },
      select: { id: true, name: true },
    });
    console.log("existingCategories");
    console.log(existingCategories);

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

    // Add categories to community data using the many-to-many relation
    communityData.categories = {
      create: existingCategories.map((category) => ({
        category: {
          connect: { id: category.id },
        },
      })),
    };
  }

  console.log("⭐ Final community data:", communityData);

  // Create the community with its categories and member
  const newCommunity = await prisma.community.create({
    data: communityData,
    include: {
      categories: {
        include: {
          category: true,
        },
      },
      members: true,
      owner: {
        select: {
          id: true,
          name: true,
          email: true,
        },
      },
    },
  });

  // Transform the response to include category names directly
  const formattedCommunity = {
    ...newCommunity,
    categories: newCommunity.categories.map((cc) => cc.category),
  };

  res.status(StatusCodes.CREATED).json({
    success: true,
    message: "Community created successfully",
    community: formattedCommunity,
  });
};

/**
 * Gets a single community by ID
 * @route GET /api/v1/community/:id
 * @access Private
 */
const getSingleCommunity = async (req, res) => {
  const { id } = req.params;

  const community = await prisma.community.findUnique({
    where: { id },
    select: {
      id: true,
      name: true,
      description: true,
      image: true,
      privacy: true,
      recipeCreationPermission: true,
      createdAt: true,
      updatedAt: true,
      categories: {
        select: {
          category: {
            select: {
              id: true,
              name: true
            }
          }
        }
      },
      members: {
        select: {
          id: true,
          role: true,
          joinedAt: true,
          user: {
            select: {
              id: true,
              name: true,
              username: true
            }
          }
        }
      },
      owner: {
        select: {
          id: true,
          name: true,
          username: true
        }
      }
    }
  });

  if (!community) {
    throw new CustomAPIError.NotFoundError(`No community found with id: ${id}`);
  }

  // Transform categories array
  const formattedCommunity = {
    ...community,
    categories: community.categories.map(({category}) => category)
  };

  res.status(StatusCodes.OK).json({
    success: true,
    community: formattedCommunity
  });
};


module.exports = {
  createCommunity,
  getSingleCommunity,
};
