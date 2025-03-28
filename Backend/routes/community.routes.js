const express = require("express");
const {
  createCommunity,
  getSingleCommunity,
  getAllCommunities,
  joinCommunity,
  getAllMembers,
  leaveCommunity,
  setAdmins,
} = require("../controllers/community.controller");
const { authenticateUser } = require("../middlewares/authentication");
const {
  createCommunitySchema,
  getAllMembersSchema,
  leaveCommunitySchema,
} = require("../validators/communityValidator");
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

router.post("/:id/join", authenticateUser, joinCommunity);

router.get(
  "/:id/members",
  authenticateUser,
  validateRequest(getAllMembersSchema, "params"),
  getAllMembers
);

router.delete(
  "/:id/leave",
  authenticateUser,
  validateRequest(leaveCommunitySchema, "params"),
  leaveCommunity
);

router.patch(
  "/:id/admins",
  authenticateUser,
  setAdmins
)

module.exports = router;
