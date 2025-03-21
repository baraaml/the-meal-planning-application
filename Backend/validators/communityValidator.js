const Joi = require("joi");

const recipeCreationPermissionEnum = ["ANY_MEMBER", "ADMINS_ONLY"];

const createCommunitySchema = Joi.object({
  name: Joi.string().min(3).max(50).required(),
  description: Joi.string().max(500).optional(),
  image: Joi.string().optional(), // No `uri()` because it's a file path
  recipeCreationPermission: Joi.string()
    .valid(...recipeCreationPermissionEnum)
    .default("ANY_MEMBER"),
  categories: Joi.array().items(Joi.string()).optional(),
});

module.exports = {
  createCommunitySchema,
};
