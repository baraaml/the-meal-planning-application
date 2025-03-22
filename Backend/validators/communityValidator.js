const Joi = require("joi");

const recipeCreationPermissionEnum = ["ANY_MEMBER", "ADMINS_ONLY"];
const privacyEnum = ["PUBLIC", "PRIVATE", "RESTRICTED"];

const createCommunitySchema = Joi.object({
  name: Joi.string().min(3).max(50).required(),
  description: Joi.string().max(500).optional(),
  image: Joi.string().allow(null, "").optional(), // Allow null or empty string for optional images
  recipeCreationPermission: Joi.string()
    .valid(...recipeCreationPermissionEnum)
    .default("ANY_MEMBER"),
  privacy: Joi.string()
    .valid(...privacyEnum)
    .default("PUBLIC"),
  categories: Joi.array().items(Joi.string()).optional(),
}).unknown(true); // Allow unknown fields to support file upload fields

module.exports = {
  createCommunitySchema,
};
