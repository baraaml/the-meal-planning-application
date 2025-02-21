const express = require("express");
require("express-async-errors");
require("dotenv").config();
const { PrismaClient } = require("@prisma/client");

const prisma = new PrismaClient();
const app = express();

const userRouter = require("./routes/userRoutes");
const notFound = require("./middlewares/notFound");
const errorHandlerMiddleware = require("./middlewares/errorHandler");

app.use(express.json());
app.use("/api/v1/users", userRouter);

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
