# Recommendation Service Documentation

This directory contains documentation resources for the Recommendation Service API.

## Available Documentation

### 1. API Documentation

[API_DOCUMENTATION.md](./API_DOCUMENTATION.md) provides comprehensive documentation for all API endpoints, including:
- Endpoint descriptions
- Request parameters
- Response formats
- Testing strategies

This is the best place to start if you want to understand the available features and how to use them.

### 2. Postman Collection

[recommendation_service.postman_collection.json](./recommendation_service.postman_collection.json) is a ready-to-use Postman collection that contains preconfigured requests for all API endpoints. You can:
- Import it into Postman
- Run requests against your local or deployed API
- Examine example responses
- Modify requests for your specific needs

See the API Documentation for instructions on importing and using the collection.

## Getting Started

1. Start the recommendation service using the provided script:
   ```bash
   cd ../
   chmod +x start_recommendation_service.sh
   ./start_recommendation_service.sh
   ```

2. Import the Postman collection to test the API endpoints.

3. Refer to the API Documentation for detailed usage instructions.

## Feature Summary

The Recommendation Service provides multiple recommendation strategies:

- **Hybrid Recommendations**: Combines multiple strategies with fallbacks
- **User-Based Collaborative Filtering**: Recommends based on similar users
- **Item-Based Collaborative Filtering**: Recommends similar items
- **Content-Based Recommendations**: Uses vector embeddings
- **Popularity-Based Recommendations**: Recommends trending content

You can choose which strategy to use via API parameters, allowing for flexible and customized recommendation experiences.

## Further Reading

For more detailed information about the implementation, refer to:
- The main [README.md](../README.md) in the project root
- Code documentation in each module
- The comments in the Postman collection requests