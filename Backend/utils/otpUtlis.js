const crypto = require("crypto");
const bcrypt = require("bcrypt");

const generateOTP = () => {
  return crypto.randomInt(100000, 999999).toString();
};

const hashOTP = async (otp) => {
  return await bcrypt.hash(otp, 10);
};

const verifyHashedOTP = async (otp, hashedOTP) => {
  return await bcrypt.compare(otp, hashedOTP);
};

module.exports = {
  generateOTP,
  hashOTP,
  verifyHashedOTP,
};
