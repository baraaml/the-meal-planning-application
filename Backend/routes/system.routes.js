const express = require("express");
const router = express.Router();
const config = require("../config/pythonService");
const axios = require("axios");

// System health check endpoint
router.get("/health", async (req, res) => {
  try {
    const systemInfo = {
      status: "ok",
      timestamp: new Date().toISOString(),
      environment: process.env.NODE_ENV || "development",
      nodejs: {
        version: process.version,
        uptime: process.uptime(),
        memoryUsage: process.memoryUsage(),
      },
      config: {
        pythonService: {
          baseUrl: config.baseUrl,
          apiVersion: config.apiVersion,
          timeout: config.timeout,
          defaultUserId: config.defaultUserId
        }
      }
    };

    // Check Python service connectivity
    try {
      const pythonServiceUrl = `${config.baseUrl}/api/${config.apiVersion}/health`;
      console.log(`Checking Python service at: ${pythonServiceUrl}`);
      
      const response = await axios.get(pythonServiceUrl, { timeout: 3000 });
      systemInfo.pythonService = {
        status: "connected",
        response: response.data
      };
    } catch (error) {
      systemInfo.pythonService = {
        status: "error",
        error: error.message,
        code: error.code,
        details: error.isAxiosError 
          ? {
              config: error.config ? {
                url: error.config.url,
                method: error.config.method,
                timeout: error.config.timeout
              } : 'No config available'
            }
          : 'Not an Axios error'
      };
    }

    res.status(200).json(systemInfo);
  } catch (error) {
    res.status(500).json({
      status: "error",
      message: error.message,
      stack: process.env.NODE_ENV === "development" ? error.stack : undefined
    });
  }
});

module.exports = router;