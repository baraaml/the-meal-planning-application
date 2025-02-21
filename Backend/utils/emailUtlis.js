const nodemailer = require("nodemailer");
require("dotenv").config();

const transporter = nodemailer.createTransport({
  host: process.env.SMTP_HOST, // smtp-relay.brevo.com
  port: Number(process.env.SMTP_PORT), // 587
  secure: false, // Must be false for port 587 (STARTTLS)
  auth: {
    user: process.env.SMTP_USER,
    pass: process.env.SMTP_PASS,
  },
  // tls: {
  //   rejectUnauthorized: false, // Bypass self-signed certificate issues
  // },
});

const sendVerificationEmail = async (email, otp) => {
  const mailOptions = {
    from: '"MealFlow Support" <bojack25333@gmail.com>',
    to: email,
    subject: "Verify Your Email - MealFlow",
    html: `
      <!DOCTYPE html>
      <html>
      <head>
        <meta charset="utf-8">
        <title>Email Verification</title>
        <style>
          body { font-family: sans-serif; line-height: 1.6; margin: 0; padding: 20px; background-color: #f4f4f4; }
          .container { max-width: 600px; margin: 0 auto; background-color: #ffffff; padding: 30px; border-radius: 8px; box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1); }
          h2 { color: #333; }
          p { color: #555; }
          .otp { background-color: #e0f7fa; padding: 10px 20px; border-radius: 5px; font-size: 1.2em; font-weight: bold; color: #1565c0; text-align: center; margin: 20px 0; }
          .expiry { font-size: 0.9em; color: #777; }
          .footer { margin-top: 30px; text-align: center; color: #999; }
        </style>
      </head>
      <body>
        <div class="container">
          <h2>Email Verification</h2>
          <p>Dear User,</p>
          <p>Thank you for registering. Please use the following One-Time Password (OTP) to verify your email address:</p>
          <div class="otp">${otp}</div>
          <p class="expiry">This OTP will expire in 15 minutes.</p>
          <p>If you did not request this verification, please ignore this email.</p>
          <div class="footer">
            <p>This is an automatically generated email. Please do not reply to this message.</p>
          </div>
        </div>
      </body>
      </html>
    `,
  };

  await transporter.sendMail(mailOptions);
};

module.exports = { sendVerificationEmail };
