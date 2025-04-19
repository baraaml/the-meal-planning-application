const express = require("express");
require("express-async-errors");
require("dotenv").config();
require("./jobs/cron"); // This will automatically start the cleanup job
const { PrismaClient } = require("@prisma/client");
const cors = require("cors");
const path = require("path");

const prisma = new PrismaClient();
const app = express();

const uploadRouter = require("./routes/upload.routes");
const authRouter = require("./routes/auth.routes");
const userRouter = require("./routes/user.routes");
const communityRouter = require("./routes/community.routes");
const mealRouter = require("./routes/meal.routes");
const systemRouter = require("./routes/system.routes"); // Add the system routes
const notFound = require("./middlewares/notFound");
const errorHandlerMiddleware = require("./middlewares/errorHandler");

app.use(cors());
app.use(express.json());
app.use("/api/v1/users", authRouter);
app.use("/api/v1/users", userRouter);
app.use("/api/v1/community", communityRouter);
app.use("/api/v1/upload", uploadRouter);
app.use("/api/v1/meal", mealRouter);
app.use("/api/v1/system", systemRouter); // Use the system routes

app.get("/", (req, res) => {
  res.send("Hello ma man");
});

app.use(notFound);
app.use(errorHandlerMiddleware);

const port = process.env.PORT || 3001;

const startServer = async () => {
  try {
    await prisma.$connect();
    const server = app.listen(port, () =>
      console.log(`Server is listening on ${port}...`)
    );

    // Graceful shutdown
    process.on("SIGINT", async () => {
      await prisma.$disconnect();
      console.log("Prisma disconnected. Server shutting down...");
      server.close(() => process.exit(0));
    });

    process.on("SIGTERM", async () => {
      await prisma.$disconnect();
      console.log("Prisma disconnected due to termination signal.");
      server.close(() => process.exit(0));
    });
  } catch (error) {
    console.error("Error starting server:", error);
    process.exit(1);
  }
};

// Only start the server if this file is executed directly (not when imported in tests)
if (require.main === module) {
  startServer();
}

// Export the Express app (without starting the server) for testing
module.exports = app;