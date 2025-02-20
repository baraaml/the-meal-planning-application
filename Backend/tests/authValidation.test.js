const request = require("supertest");
const app = require("../app"); // Import your Express app

describe("User Registration Validation", () => {
  const endpoint = "/api/v1/users/register"; // Your registration route

  test("❌ Should fail when required fields are missing", async () => {
    const response = await request(app).post(endpoint).send({});
    
    expect(response.status).toBe(400);
    expect(response.body).toHaveProperty("errors");
    expect(response.body.errors).toContain('"email" is required');
    expect(response.body.errors).toContain('"password" is required');
  });

  test("❌ Should fail if email format is invalid", async () => {
    const response = await request(app)
      .post(endpoint)
      .send({ username: "amr", email: "invalid-email", password: "123456" });

    expect(response.status).toBe(400);
    expect(response.body.errors).toContain('"email" must be a valid email');
  });

  test("❌ Should fail if password is too short", async () => {
    const response = await request(app)
      .post(endpoint)
      .send({ username: "amr", email: "test@mail.com", password: "123" });

    expect(response.status).toBe(400);
    expect(response.body.errors).toContain('"password" length must be at least 6 characters long');
  });

  test("✅ Should pass with valid input", async () => {
    const response = await request(app)
      .post(endpoint)
      .send({ username: "amr", email: "test@mail.com", password: "secure123" });

    expect(response.status).toBe(201);
    expect(response.body.success).toBe(true);
    expect(response.body).toHaveProperty("user");
  });
});
