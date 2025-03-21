const express = require("express");
const { createCommunity } = require("../controllers/community.controller");
const upload = require("../utils/uploads");
const { authenticateUser } = require("../middlewares/authentication");
const { createCommunitySchema } = require("../validators/communityValidator");
const validateRequest = require("../middlewares/validate");
const router = express.Router();

router.post(
  "/",
  authenticateUser,
  upload.single("image"),
  validateRequest(createCommunitySchema),
  createCommunity
);

module.exports = router;
