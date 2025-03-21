const Joi = require("joi");

const createCommunitySchema = Joi.object({
  name: Joi.string().min(3).max(50).required(),
  description: Joi.string().max(500).optional(),
  image: Joi.string().uri().optional(),
  recipeCreationPermission: Joi.string()
    .valid(...recipeCreationPermissionEnum)
    .default("ANY_MEMBER"),
  categories: Joi.array().items(Joi.string().uuid()).min(0).optional(),
});

module.exports = {
  createCommunitySchema,
};
