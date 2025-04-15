const axios = require('axios');
const config = require('../config/pythonService');
const recipeTransformer = require('../transformers/recipeTransformer');

class RecipeService {
  constructor() {
    // Validate configuration
    if (!config.baseUrl) {
      console.error('ERROR: PYTHON_SERVICE_URL is not defined in environment variables');
      console.error('Fallback to default URL: http://127.0.0.1:9999');
    }

    if (!config.apiVersion) {
      console.error('ERROR: PYTHON_API_VERSION is not defined in environment variables');
      console.error('Fallback to default version: v1');
    }

    // Log the configuration for debugging
    console.log(`RecipeService initializing with:`, {
      baseUrl: config.baseUrl,
      apiVersion: config.apiVersion,
      timeout: config.timeout,
      defaultUserId: config.defaultUserId
    });

    // Create axios client with validated configuration
    this.client = axios.create({
      baseURL: `${config.baseUrl}/api/${config.apiVersion}`,
      timeout: config.timeout || 5000
    });

    // Add request interceptor for debugging
    this.client.interceptors.request.use(request => {
      console.log(`Making request to: ${request.baseURL}${request.url}`);
      return request;
    });
  }

  async getRecommendedMeals(userId = config.defaultUserId, limit = 10) {
    try {
      console.log(`Fetching recommended meals for user: ${userId}, limit: ${limit}`);
      const response = await this.client.get(`/recommend/user/${userId}`, {
        params: { limit, recommendation_type: 'hybrid' }
      });

      return this._transformItems(response.data.items);
    } catch (error) {
      this._handleError('Failed to fetch recommended meals:', error);
      throw error;
    }
  }

  async getTrendingMeals(limit = 10, timeWindow = 'week') {
    try {
      console.log(`Fetching trending meals, limit: ${limit}, time window: ${timeWindow}`);
      const response = await this.client.get('/trending', {
        params: { limit, time_window: timeWindow }
      });

      return this._transformItems(response.data.items);
    } catch (error) {
      this._handleError('Failed to fetch trending meals:', error);
      throw error;
    }
  }

  async getSimilarMeals(recipeId, limit = 5) {
    try {
      console.log(`Fetching similar meals for recipe: ${recipeId}, limit: ${limit}`);
      const response = await this.client.get(`/recommend/similar/${recipeId}`, {
        params: { limit }
      });

      return this._transformItems(response.data.items);
    } catch (error) {
      this._handleError(`Failed to fetch similar meals for recipe ${recipeId}:`, error);
      throw error;
    }
  }

  async getMealDetails(recipeId) {
    try {
      console.log(`Fetching meal details for recipe: ${recipeId}`);
      const response = await this.client.get(`/recipes/${recipeId}`);
      return recipeTransformer.transformToMeal(response.data);
    } catch (error) {
      this._handleError(`Failed to fetch meal details for recipe ${recipeId}:`, error);
      throw error;
    }
  }

  async recordInteraction(userId, mealId, interactionType, rating = null) {
    try {
      console.log(`Recording ${interactionType} interaction for user: ${userId}, meal: ${mealId}`);
      await this.client.post('/interactions', {
        user_id: userId,
        meal_id: mealId,
        interaction_type: interactionType,
        rating
      });
      return true;
    } catch (error) {
      this._handleError(`Failed to record interaction for user ${userId} on meal ${mealId}:`, error);
      throw error;
    }
  }

  async getQuickMeals(maxTime = 30, limit = 10, cuisine = null, dietaryRestriction = null) {
    try {
      console.log(`Fetching quick meals, max time: ${maxTime}, limit: ${limit}`);
      const response = await this.client.get('/recommend/quick', {
        params: {
          max_time: maxTime,
          limit,
          cuisine,
          dietary_restriction: dietaryRestriction
        }
      });

      return this._transformItems(response.data.items);
    } catch (error) {
      this._handleError('Failed to fetch quick meals:', error);
      throw error;
    }
  }

  async getCuisineRecommendations(cuisineId, limit = 10) {
    try {
      console.log(`Fetching cuisine recommendations for ${cuisineId}, limit: ${limit}`);
      const response = await this.client.get(`/recommend/cuisine/${cuisineId}`, {
        params: { limit }
      });

      return this._transformItems(response.data.items);
    } catch (error) {
      this._handleError(`Failed to fetch cuisine recommendations for ${cuisineId}:`, error);
      throw error;
    }
  }

  async getDietaryRecommendations(dietaryRestriction, limit = 10) {
    try {
      console.log(`Fetching dietary recommendations for ${dietaryRestriction}, limit: ${limit}`);
      const response = await this.client.get(`/recommend/dietary/${dietaryRestriction}`, {
        params: { limit }
      });

      return this._transformItems(response.data.items);
    } catch (error) {
      this._handleError(`Failed to fetch dietary recommendations for ${dietaryRestriction}:`, error);
      throw error;
    }
  }

  async getFilteredRecipes(options = {}) {
    try {
      const { page = 1, limit = 20, region, subRegion, minCalories, maxCalories } = options;
      console.log(`Fetching filtered recipes with options:`, options);

      const response = await this.client.get('/recipes', {
        params: {
          page,
          limit,
          region,
          sub_region: subRegion,
          min_calories: minCalories,
          max_calories: maxCalories
        }
      });

      return response.data.map(recipe => recipeTransformer.transformToMeal(recipe));
    } catch (error) {
      this._handleError('Failed to fetch filtered recipes:', error);
      throw error;
    }
  }

  async getRecipesByCalories(min = 0, max = 1000, page = 1, limit = 20) {
    try {
      console.log(`Fetching recipes by calories, min: ${min}, max: ${max}, page: ${page}, limit: ${limit}`);
      const response = await this.client.get('/recipes/filter/calories', {
        params: { min, max, page, limit }
      });

      return response.data.map(recipe => recipeTransformer.transformToMeal(recipe));
    } catch (error) {
      this._handleError('Failed to fetch recipes by calories:', error);
      throw error;
    }
  }

  // Add this to your RecipeService.js

  /**
   * Perform an advanced search with multiple filtering criteria
   * @param {Object} criteria - Search criteria
   * @returns {Promise<Array>} - Array of meals matching the criteria
   */
  async advancedSearch(criteria = {}) {
    try {
      console.log(`Performing advanced search with criteria:`, criteria);

      // Extract all possible search criteria with defaults
      const {
        query = null,              // Text search query
        cuisines = [],             // Array of cuisines/regions
        dietaryRestrictions = [],  // Array of dietary restrictions
        excludeIngredients = [],   // Ingredients to exclude
        includeIngredients = [],   // Ingredients that must be included
        minCalories = null,        // Minimum calories
        maxCalories = null,        // Maximum calories
        maxPrepTime = null,        // Maximum preparation time
        maxCookTime = null,        // Maximum cooking time
        maxTotalTime = null,       // Maximum total time
        sortBy = 'relevance',      // Sorting method (relevance, rating, time, calories)
        sortOrder = 'desc',        // Sort order (asc, desc)
        page = 1,                  // Page number
        limit = 20                 // Items per page
      } = criteria;

      // Build query parameters
      const params = {
        page,
        limit,
        sort_by: sortBy,
        sort_order: sortOrder
      };

      // Add text search if provided
      if (query) {
        params.query = query;
      }

      // Add cuisine/region filters (comma-separated)
      if (cuisines && cuisines.length > 0) {
        params.cuisines = cuisines.join(',');
      }

      // Add dietary restriction filters (comma-separated)
      if (dietaryRestrictions && dietaryRestrictions.length > 0) {
        params.dietary = dietaryRestrictions.join(',');
      }

      // Add ingredient filters
      if (includeIngredients && includeIngredients.length > 0) {
        params.include_ingredients = includeIngredients.join(',');
      }

      if (excludeIngredients && excludeIngredients.length > 0) {
        params.exclude_ingredients = excludeIngredients.join(',');
      }

      // Add numerical range filters
      if (minCalories !== null) {
        params.min_calories = minCalories;
      }

      if (maxCalories !== null) {
        params.max_calories = maxCalories;
      }

      if (maxPrepTime !== null) {
        params.max_prep_time = maxPrepTime;
      }

      if (maxCookTime !== null) {
        params.max_cook_time = maxCookTime;
      }

      if (maxTotalTime !== null) {
        params.max_total_time = maxTotalTime;
      }

      // Make the API request
      const response = await this.client.get('/recipes/search', { params });

      // Transform the results
      return response.data.results.map(recipe => recipeTransformer.transformToMeal(recipe));
    } catch (error) {
      this._handleError('Failed to perform advanced search:', error);
      throw error;
    }
  }

  // Helper method to transform items with better error handling
  async _transformItems(items) {
    try {
      if (!items || !Array.isArray(items)) {
        console.warn('No items to transform or items is not an array');
        return [];
      }

      const transformedItems = [];
      for (const item of items) {
        try {
          const mealDetails = await this.getMealDetails(item.id);
          transformedItems.push(mealDetails);
        } catch (error) {
          console.error(`Error transforming item ${item.id}:`, error);
          // Continue with other items
        }
      }

      return transformedItems;
    } catch (error) {
      console.error('Error in _transformItems:', error);
      return [];
    }
  }

  // Helper method for consistent error handling
  _handleError(message, error) {
    console.error(message, error);

    // Handle specific axios errors
    if (error.isAxiosError) {
      if (error.code === 'ECONNREFUSED') {
        console.error(`Connection refused: Please ensure the Python service is running at ${config.baseUrl}`);
      } else if (error.code === 'ERR_INVALID_URL') {
        console.error(`Invalid URL: Please check your PYTHON_SERVICE_URL and PYTHON_API_VERSION in .env file`);
        console.error(`Current configuration: baseUrl=${config.baseUrl}, apiVersion=${config.apiVersion}`);
      } else if (error.response) {
        // The request was made and the server responded with a status code
        // that falls out of the range of 2xx
        console.error(`API Error ${error.response.status}: ${error.response.data?.detail || 'Unknown error'}`);
      } else if (error.request) {
        // The request was made but no response was received
        console.error('No response received from the server');
      }
    }
  }
}

module.exports = new RecipeService();