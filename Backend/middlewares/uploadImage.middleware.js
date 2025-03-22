const multer = require("multer");
const path = require("path");
const fs = require("fs");

// Ensure uploads directory exists
const uploadDir = "uploads";
if (!fs.existsSync(uploadDir)) {
  fs.mkdirSync(uploadDir);
}

// Configure storage settings
const storage = multer.diskStorage({
  destination: function (req, file, cb) {
    cb(null, uploadDir);
  },
  filename: function (req, file, cb) {
    const uniqueSuffix = Date.now() + "-" + Math.round(Math.random() * 1e9);
    cb(null, "community-" + uniqueSuffix + path.extname(file.originalname));
  },
});

// File filter to allow only images
const fileFilter = (req, file, cb) => {
  const allowedFileTypes = /jpeg|jpg|png|gif/;
  const extname = allowedFileTypes.test(
    path.extname(file.originalname).toLowerCase()
  );
  const mimetype = allowedFileTypes.test(file.mimetype);

  if (extname && mimetype) {
    return cb(null, true);
  } else {
    return cb(new Error("Only images (jpeg, jpg, png, gif) are allowed!"));
  }
};

// Initialize multer with storage and file filtering
const upload = multer({
  storage: storage,
  fileFilter: fileFilter,
  limits: { fileSize: 5 * 1024 * 1024 }, // Limit file size to 5MB
});

// Middleware to handle file uploads
const uploadMiddleware = (req, res, next) => {
  console.log("⭐ Starting file upload");
  upload.single("image")(req, res, (err) => {
    if (err instanceof multer.MulterError) {
      console.error("⭐ Multer error:", err);
      if (err.code === "LIMIT_FILE_SIZE") {
        return res.status(400).json({
          success: false,
          message: "File size too large. Maximum size is 5MB",
        });
      }
      return res.status(400).json({
        success: false,
        message: `Upload error: ${err.message}`,
      });
    } else if (err) {
      console.error("⭐ Upload error:", err);
      return res.status(400).json({
        success: false,
        message: err.message,
      });
    }

    // If file was uploaded successfully
    if (req.file) {
      console.log("⭐ File uploaded:", req.file);
      // Convert Windows path to URL format and store relative path
      const relativePath = req.file.path.replace(/\\/g, "/");
      req.body.image = `/${relativePath}`;
    } else {
      console.log("⭐ No file uploaded");
      req.body.image = null;
    }

    next();
  });
};

module.exports = uploadMiddleware;
