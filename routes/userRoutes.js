const express = require("express");

const {
    registerUser,
    loginUser,
    getSingleUser,
    getAllUsers,
} = require("../controllers/userController");

const router = express.Router();

router.post("/register", registerUser);
router.post("/login", loginUser);
router.get("/:id", getSingleUser);
router.get("/", getAllUsers);

module.exports = router;
