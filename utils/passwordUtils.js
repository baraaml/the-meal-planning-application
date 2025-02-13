const bcrypt = require("bcrypt");

const passwordUtils = {
    hash: async (password) => {
        const SALT_ROUNDS = await bcrypt.genSalt(
            Number(process.env.COST_FACTOR)
        );
        return bcrypt.hash(password, SALT_ROUNDS);
    },
    compare: async (password, hash) => {
        return bcrypt.compare(password, hash);
    },
};

module.exports = passwordUtils;
