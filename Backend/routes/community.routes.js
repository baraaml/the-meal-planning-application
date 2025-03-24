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

// router.patch(
//   "/:id",
//   authenticateUser,
//   uploadMiddleware,
//   validateRequest(updateCommunitySchema),
//   updateCommunity
// );

// router.delete(
//   "/:id",
//   authenticateUser,
//   authorizeRoles(["ADMIN", "OWNER"]),
//   deleteCommunity
// );

// // Member Management
// router.post("/:id/members", authenticateUser, joinCommunity);

// router.delete("/:id/members", authenticateUser, leaveCommunity);

// router.patch(
//   "/:id/members/:userId/role",
//   authenticateUser,
//   authorizeRoles(["ADMIN", "OWNER"]),
//   updateMemberRole
// );

// router.delete(
//   "/:id/members/:userId",
//   authenticateUser,
//   authorizeRoles(["ADMIN", "OWNER"]),
//   removeMember
// );

// router.post(
//   "/:id/members/bulk-invite",
//   authenticateUser,
//   authorizeRoles(["ADMIN", "OWNER"]),
//   bulkInviteMembers
// );

// // Categories Management
// router.get(
//   "/:id/categories",
//   authenticateUser,
//   cacheMiddleware(300),
//   getCommunityCategories
// );

// router.patch(
//   "/:id/categories",
//   authenticateUser,
//   authorizeRoles(["ADMIN", "OWNER"]),
//   updateCommunityCategories
// );

// // Recipes in Community
// router.get(
//   "/:id/recipes",
//   authenticateUser,
//   cacheMiddleware(300),
//   getCommunityRecipes
// );

// // Community Discovery and Search
// router.get("/search", authenticateUser, searchCommunities);

// router.get(
//   "/me/joined",
//   authenticateUser,
//   cacheMiddleware(300),
//   getMyCommunities
// );

// // Analytics and Monitoring
// router.get(
//   "/:id/stats",
//   authenticateUser,
//   authorizeRoles(["ADMIN", "OWNER"]),
//   cacheMiddleware(300),
//   getCommunityStats
// );

// router.get(
//   "/:id/activities",
//   authenticateUser,
//   cacheMiddleware(60),
//   getCommunityActivities
// );

// // Community Members
// router.get(
//   "/:id/members",
//   authenticateUser,
//   cacheMiddleware(300),
//   getCommunityMembers
// );

// // Privacy and Moderation
// router.patch(
//   "/:id/visibility",
//   authenticateUser,
//   authorizeRoles(["ADMIN", "OWNER"]),
//   toggleCommunityVisibility
// );

// router.post(
//   "/:id/report",
//   authenticateUser,
//   rateLimiter({ windowMs: 3600 * 1000, max: 5 }), // 5 reports per hour
//   reportCommunity
// );

module.exports = router;
