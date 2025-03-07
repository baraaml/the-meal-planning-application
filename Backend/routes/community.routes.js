const express = require("express");
const { createCommunity } = require("../controllers/community.controller");
const { authenticateUser } = require("../middlewares/authentication");
const router = express.Router();

router.post("/", authenticateUser, createCommunity);

module.exports = router;
