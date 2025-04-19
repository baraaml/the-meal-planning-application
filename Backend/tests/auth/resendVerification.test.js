const request = require("supertest");
const app = require("../../app.js"); // Adjust path if needed
const prisma = require("../../config/prismaClient.js");
const { BadRequestError } = require("../../errors/badRequestError.js");
const { generateOTP, hashOTP } = require("../../utils/otpUtlis.js");
const { sendVerificationEmail } = require("../../utils/emailUtlis.js");

// Mock dependencies
jest.mock("../../config/prismaClient", () => ({
  user: { findUnique: jest.fn() },
  verificationCode: { findFirst: jest.fn() },
  $transaction: jest.fn(),
}));

jest.mock("../../utils/otpUtlis.js", () => ({
  generateOTP: jest.fn(() => "123456"),
  hashOTP: jest.fn(() => "hashedOTP"),
}));

jest.mock("../../utils/emailUtlis.js", () => ({
  sendVerificationEmail: jest.fn(() => Promise.resolve()),
}));

describe("POST /auth/resend-verification", () => {
  afterEach(() => {
    jest.clearAllMocks();
  });

  it("should return 400 if user is not found", async () => {
    prisma.user.findUnique.mockResolvedValue(null);

    const res = await request(app)
      .post("/auth/resend-verification")
      .send({ email: "nonexistent@example.com" });

    expect(res.status).toBe(400);
    expect(res.body.message).toBe("User not found");
  });

  it("should return 400 if user is already verified", async () => {
    prisma.user.findUnique.mockResolvedValue({ id: "123", isVerified: true });

    const res = await request(app)
      .post("/auth/resend-verification")
      .send({ email: "verified@example.com" });

    expect(res.status).toBe(400);
    expect(res.body.message).toBe(
      "User is already verified, Please go to login page"
    );
  });

  it("should enforce rate limiting (1-minute cooldown)", async () => {
    prisma.user.findUnique.mockResolvedValue({ id: "123", isVerified: false });
    prisma.verificationCode.findFirst.mockResolvedValue({
      createdAt: new Date(Date.now() - 30 * 1000), // OTP created 30 sec ago
    });

    const res = await request(app)
      .post("/auth/resend-verification")
      .send({ email: "user@example.com" });

    expect(res.status).toBe(400);
    expect(res.body.message).toBe("Please wait before requesting another OTP.");
  });

  it("should generate and send a new OTP if conditions are met", async () => {
    prisma.user.findUnique.mockResolvedValue({ id: "123", isVerified: false });
    prisma.verificationCode.findFirst.mockResolvedValue({
      createdAt: new Date(Date.now() - 2 * 60 * 1000), // OTP created 2 mins ago
    });
    prisma.$transaction.mockResolvedValue();

    const res = await request(app)
      .post("/auth/resend-verification")
      .send({ email: "user@example.com" });

    expect(res.status).toBe(200);
    expect(res.body.success).toBe(true);
    expect(res.body.message).toBe(
      "A new verification code has been sent to your email."
    );
    expect(generateOTP).toHaveBeenCalled();
    expect(hashOTP).toHaveBeenCalledWith("123456");
    expect(sendVerificationEmail).toHaveBeenCalledWith(
      "user@example.com",
      "123456"
    );
  });
});
