const crypto = require("crypto");
const bcrypt = require("bcrypt");
const BadrequestError = require("../errors/badRequestError");
const prisma = require("../config/prismaClient");

const generateOTP = () => {
  return crypto.randomInt(100000, 999999).toString();
};

const hashOTP = async (otp) => {
  return await bcrypt.hash(otp, 10);
};

const verifyHashedOTP = async (otp, hashedOTP) => {
  return await bcrypt.compare(otp, hashedOTP);
};

const deleteOldOTPs = async (email) => {
  const user = await prisma.user.findUnique({
    where: { email: email.toLowerCase() },
  });
  if (!user) throw new BadrequestError("User not found");

  await prisma.verificationCode.deleteMany({ where: { userId: user.id } });

  return { success: true, message: "Old OTPs deleted successfully" };
};

module.exports = {
  generateOTP,
  hashOTP,
  verifyHashedOTP,
  deleteOldOTPs,
};
