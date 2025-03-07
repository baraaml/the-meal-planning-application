const { StatusCodes } = require("http-status-codes");

const createCommunity = async (req, res) => {
  res.status(StatusCodes.CREATED).json({
    success: true,
    message: "New community has been created.",
  });
};

module.exports = {
  createCommunity,
};
