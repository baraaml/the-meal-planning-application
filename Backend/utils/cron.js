const cron = require("node-cron");
const { PrismaClient } = require("@prisma/client");

const prisma = new PrismaClient();

// Schedule a cron job to delete expired OTPs every 15 minutes
cron.schedule("*/15 * * * *", async () => {
  console.log("Cleaning up expired OTPs...");

  try {
    const { count } = await prisma.verificationCode.deleteMany({
      where: { expiresAt: { lt: new Date() } },
    });

    console.log(`Deleted ${count} expired OTP(s)`);
  } catch (error) {
    console.error("Error deleting expired OTPs:", error);
  }
});
