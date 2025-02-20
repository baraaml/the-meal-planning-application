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

const start = async () => {
  try {
    await prisma.$connect();
    app.listen(port, () => console.log(`Server is listening on ${port}...`));
  } catch (error) {
    console.error("Error starting server:", error);
    process.exit(1);
  }
};

// Graceful shutdown
process.on("SIGINT", async () => {
  await prisma.$disconnect();
  console.log("Prisma disconnected. Server shutting down...");
  process.exit(0);
});

start();
