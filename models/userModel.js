const { PrismaClient } = require("@prisma/client");
const bcrypt = require("bcrypt");
const tokenUtils = require("../utils/tokenUtils");
const passwordUtils = require("../utils/passwordUtils");

const prisma = new PrismaClient();

// Register a new user
const registerUser = async (data) => {
    const { name, email, password } = data;

    // Hash password before saving
    const hashedPassword = await passwordUtils.hash(password);

    // Create user in DB
    const user = await prisma.user.create({
        data: { name, email, password: hashedPassword },
    });

    return user;
};

// Generate JWT
const generateAuthToken = (user) => {
    return tokenUtils.sign({
        userId: user.id,
        name: user.name,
    });
};

// Compare password
const comparePassword = async (candidatePassword, storedPassword) => {
    return passwordUtils.compare(candidatePassword, storedPassword);
};

module.exports = {
    registerUser,
    generateAuthToken,
    comparePassword,
};
