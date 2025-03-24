const express = require("express");
const {
  createCommunity,
  getSingleCommunity,
  getAllCommunities,
} = require("../controllers/community.controller");
const { authenticateUser } = require("../middlewares/authentication");
const { createCommunitySchema } = require("../validators/communityValidator");
const validateRequest = require("../middlewares/validate");
const parseFormData = require("../middlewares/parseFormData");
const uploadMiddleware = require("../middlewares/uploadImage.middleware");

const router = express.Router();

router.post(
  "/",
  authenticateUser,
  uploadMiddleware, // Handles file upload and validation
  parseFormData,
  validateRequest(createCommunitySchema),
  createCommunity
);

router.get("/:id", authenticateUser, getSingleCommunity);
router.get("/", authenticateUser, getAllCommunities);
module.exports = router;
