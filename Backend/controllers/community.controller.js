const { StatusCodes } = require("http-status-codes");

const createCommunity = async (req, res) => {
  const { name, description, image, recipeCreationPermission, categories = [] } = req.body;

  if (categories.length > 0) {
    // Fetch categories by name
    const existingCategories = await prisma.category.findMany({
      where: { name: { in: categories } },
      select: { id: true, name: true },
    });

    // Create a map of valid category names to their IDs
    const categoryMap = new Map(existingCategories.map(({ name, id }) => [name, id]));

    // Identify invalid category names
    const invalidCategories = categories.filter((catName) => !categoryMap.has(catName));

    if (invalidCategories.length > 0) {
      throw new BadrequestError("Some categories do not exist.", { invalidCategories });
    }

    // Convert valid category names to their IDs
    const categoryIds = existingCategories.map(({ id }) => id);

    // Atomic transaction to ensure consistency
    const newCommunity = await prisma.$transaction(async (tx) => {
      return tx.community.create({
        data: {
          name,
          description,
          image,
          recipeCreationPermission,
          categories: {
            connect: categoryIds.map((id) => ({ id })),
          },
        },
      });
    });

    return res.status(StatusCodes.CREATED).json({
      success: true,
      message: "New community has been created.",
      community: newCommunity,
    });
  }

  // If no categories are provided, just create the community
  const newCommunity = await prisma.community.create({
    data: {
      name,
      description,
      image,
      recipeCreationPermission,
    },
  });

  res.status(StatusCodes.CREATED).json({
    success: true,
    message: "New community has been created.",
    community: newCommunity,
  });
};


module.exports = {
  createCommunity,
};
