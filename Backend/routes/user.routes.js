const express = require("express");

const {
  getSingleUser,
  getAllUsers,
  updateUser,
  deleteUser,
} = require("../controllers/user.controller");

const router = express.Router();

//  User Management Routes
router.route("/me").get(getSingleUser).patch(updateUser).delete(deleteUser);

// Admin Routes
router.get("/", getAllUsers);
router.get("/:id", getSingleUser);
router.delete("/:id", deleteUser);

module.exports = router;
