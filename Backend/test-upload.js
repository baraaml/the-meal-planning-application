const express = require('express');
const multer = require('multer');
const app = express();

// Set up storage
const storage = multer.memoryStorage();
const upload = multer({ storage }).any();

app.post('/test-upload', (req, res) => {
  console.log('Received request');
  console.log('Content-Type:', req.get('Content-Type'));
  
  upload(req, res, function(err) {
    if (err) {
      console.error('Upload error:', err);
      return res.status(400).json({ error: err.message });
    }
    
    console.log('Files:', req.files);
    console.log('Body:', req.body);
    
    if (!req.files || req.files.length === 0) {
      return res.status(200).json({ 
        message: 'No files uploaded',
        body: req.body,
        contentType: req.get('Content-Type')
      });
    }
    
    const fileInfo = req.files.map(file => ({
      fieldname: file.fieldname,
      originalname: file.originalname,
      mimetype: file.mimetype,
      size: file.size
    }));
    
    res.json({ 
      message: 'Upload successful',
      files: fileInfo,
      body: req.body
    });
  });
});

const PORT = 3500;
app.listen(PORT, () => {
  console.log(`Test server running at http://localhost:${PORT}`);
  console.log('Send a POST request to http://localhost:${PORT}/test-upload to test file uploads');
}); 