const jwt = require("jsonwebtoken");

const tokenUtils = {
    sign: (payload) => {
        return jwt.sign(payload, process.env.JWT_SECRET, {
            expiresIn: process.env.JWT_LIFETIME,
        });
    },
    verify: (token) => {
        return jwt.verify(token, process.env.JWT_SECRET);
    },
};

module.exports = tokenUtils;
