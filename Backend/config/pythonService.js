require('dotenv').config();

module.exports = {
  baseUrl: process.env.PYTHON_SERVICE_URL || 'http://127.0.0.1:9999',
  apiVersion: process.env.PYTHON_API_VERSION || 'v1',
  timeout: parseInt(process.env.PYTHON_SERVICE_TIMEOUT || '30000'),
  defaultUserId: process.env.DEFAULT_USER_ID || 'default_user'
};