const { StatusCodes } = require("http-status-codes");
const multer = require("multer");
const path = require("path");
const CustomAPIError = require("../errors");
const prisma = require("../config/prismaClient");

// Configure multer storage (Save locally in 'uploads' folder)
const storage = multer.diskStorage({
  destination: function (req, file, cb) {
    cb(null, "uploads/"); // Store in 'uploads' folder
  },
  filename: function (req, file, cb) {
    const uniqueSuffix = Date.now() + "-" + Math.round(Math.random() * 1e9);
    cb(null, uniqueSuffix + path.extname(file.originalname));
  },
});

const upload = multer({ storage });

const createCommunity = async (req, res) => {
  const { name, description, image, recipeCreationPermission } = req.body;

  const { categories = "[]" } = req.body; // Default to an empty array string
  const parsedCategories =
    typeof categories === "string" ? JSON.parse(categories) : categories;

  if (categories.length > 0) {
    // Fetch categories by name
    const existingCategories = await prisma.category.findMany({
      where: { name: { in: parsedCategories } },
      select: { id: true, name: true },
    });

    // Create a map of valid category names to their IDs
    const categoryMap = new Map(
      existingCategories.map(({ name, id }) => [name, id])
    );

    // Identify invalid category names
    const invalidCategories = parsedCategories.filter(
      (catName) => !categoryMap.has(catName)
    );

    if (invalidCategories.length > 0) {
      throw new CustomAPIError.BadRequestError(
        "Some categories do not exist.",
        { invalidCategories }
      );
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
  upload,
  createCommunity,
};
