const axios = require('axios');
const config = require('../config/pythonService');
const recipeTransformer = require('../transformers/recipeTransformer');

class RecipeService {
  constructor() {
    this.client = axios.create({
      baseURL: `${config.baseUrl}/api/${config.apiVersion}`,
      timeout: config.timeout
    });
  }

  async getRecommendedMeals(userId = config.defaultUserId, limit = 10, options = {}) {
    try {
      const params = { 
        limit, 
        recommendation_type: options.recommendation_type || 'hybrid'
      };

      if (options.cuisine) params.cuisine = options.cuisine;
      if (options.dietary_restriction) params.dietary_restriction = options.dietary_restriction;

      const response = await this.client.get(`/recommend/user/${userId}`, { params });
      
      return this._transformItems(response.data.items);
    } catch (error) {
      this._handleError('Failed to fetch recommended meals:', error);
      throw error;
    }
  }

  async getTrendingMeals(limit = 10, options = {}) {
    try {
      const params = { 
        limit, 
        time_window: options.time_window || 'week'
      };

      if (options.cuisine) params.cuisine = options.cuisine;
      if (options.dietary_restriction) params.dietary_restriction = options.dietary_restriction;

      const response = await this.client.get('/trending', { params });
      
      return this._transformItems(response.data.items);
    } catch (error) {
      this._handleError('Failed to fetch trending meals:', error);
      throw error;
    }
  }

  async getSimilarMeals(recipeId, limit = 5, options = {}) {
    try {
      const params = { 
        limit, 
        similarity_method: options.similarity_method || 'content'
      };

      const response = await this.client.get(`/recommend/similar/${recipeId}`, { params });
      
      return this._transformItems(response.data.items);
    } catch (error) {
      this._handleError(`Failed to fetch similar meals for recipe ${recipeId}:`, error);
      throw error;
    }
  }

  async getMealDetails(recipeId) {
    try {
      const response = await this.client.get(`/recipes/${recipeId}`);
      return recipeTransformer.transformToMeal(response.data);
    } catch (error) {
      this._handleError(`Failed to fetch meal details for recipe ${recipeId}:`, error);
      throw error;
    }
  }

  async recordInteraction(userId, mealId, interactionType, rating = null) {
    try {
      await this.client.post('/interactions', {
        user_id: userId,
        meal_id: mealId,
        content_type: 'recipe',
        interaction_type: interactionType,
        rating
      });
      return true;
    } catch (error) {
      this._handleError(`Failed to record interaction for user ${userId} on meal ${mealId}:`, error);
      throw error;
    }
  }

  // Quick meals recommendation
  async getQuickMeals(maxTime = 30, limit = 10, options = {}) {
    try {
      const params = { 
        max_time: maxTime, 
        limit,
        cuisine: options.cuisine,
        dietary_restriction: options.dietary_restriction
      };

      const response = await this.client.get('/recommend/quick', { params });
      
      return this._transformItems(response.data.items);
    } catch (error) {
      this._handleError('Failed to fetch quick meals:', error);
      throw error;
    }
  }

  // Cuisine-specific recommendations
  async getCuisineRecommendations(cuisineId, limit = 10) {
    try {
      const response = await this.client.get(`/recommend/cuisine/${cuisineId}`, {
        params: { limit }
      });
      
      return this._transformItems(response.data.items);
    } catch (error) {
      this._handleError(`Failed to fetch cuisine recommendations for ${cuisineId}:`, error);
      throw error;
    }
  }

  // Dietary recommendations
  async getDietaryRecommendations(dietaryRestriction, limit = 10) {
    try {
      const response = await this.client.get(`/recommend/dietary/${dietaryRestriction}`, {
        params: { limit }
      });
      
      return this._transformItems(response.data.items);
    } catch (error) {
      this._handleError(`Failed to fetch dietary recommendations for ${dietaryRestriction}:`, error);
      throw error;
    }
  }

  // Filtered recipes
  async getFilteredRecipes(options = {}) {
    try {
      const params = {
        page: options.page || 1,
        limit: options.limit || 20,
        region: options.region,
        sub_region: options.subRegion,
        min_calories: options.minCalories,
        max_calories: options.maxCalories
      };

      const response = await this.client.get('/recipes', { params });
      
      return response.data.map(recipe => recipeTransformer.transformToMeal(recipe));
    } catch (error) {
      this._handleError('Failed to fetch filtered recipes:', error);
      throw error;
    }
  }

  // Recipes by calorie range
  async getRecipesByCalories(min = 0, max = 1000, page = 1, limit = 20) {
    try {
      const params = { min, max, page, limit };

      const response = await this.client.get('/recipes/filter/calories', { params });
      
      return response.data.map(recipe => recipeTransformer.transformToMeal(recipe));
    } catch (error) {
      this._handleError('Failed to fetch recipes by calories:', error);
      throw error;
    }
  }

  // Advanced search
  async advancedSearch(criteria = {}) {
    try {
      const params = {
        query: criteria.query,
        cuisines: criteria.cuisines ? 
          (Array.isArray(criteria.cuisines) ? criteria.cuisines.join(',') : criteria.cuisines) : undefined,
        dietary: criteria.dietaryRestrictions ? 
          (Array.isArray(criteria.dietaryRestrictions) ? criteria.dietaryRestrictions.join(',') : criteria.dietaryRestrictions) : undefined,
        include_ingredients: criteria.includeIngredients ? 
          (Array.isArray(criteria.includeIngredients) ? criteria.includeIngredients.join(',') : criteria.includeIngredients) : undefined,
        exclude_ingredients: criteria.excludeIngredients ? 
          (Array.isArray(criteria.excludeIngredients) ? criteria.excludeIngredients.join(',') : criteria.excludeIngredients) : undefined,
        min_calories: criteria.minCalories,
        max_calories: criteria.maxCalories,
        max_prep_time: criteria.maxPrepTime,
        max_cook_time: criteria.maxCookTime,
        max_total_time: criteria.maxTotalTime,
        sort_by: criteria.sortBy || 'relevance',
        sort_order: criteria.sortOrder || 'desc',
        page: criteria.page || 1,
        limit: criteria.limit || 20
      };

      const response = await this.client.get('/recipes/search', { params });
      
      const results = response.data.results || response.data;
      return results.map(recipe => recipeTransformer.transformToMeal(recipe));
    } catch (error) {
      this._handleError('Failed to perform advanced search:', error);
      throw error;
    }
  }

  // Helper method to transform items
  async _transformItems(items) {
    try {
      if (!items || !Array.isArray(items)) return [];

      const transformedItems = [];
      for (const item of items) {
        try {
          const mealDetails = await this.getMealDetails(item.id);
          transformedItems.push(mealDetails);
        } catch (error) {
          console.error(`Error transforming item ${item.id}:`, error);
        }
      }

      return transformedItems;
    } catch (error) {
      console.error('Error in _transformItems:', error);
      return [];
    }
  }

  // Error handling method
  _handleError(message, error) {
    console.error(message, error);

    if (error.isAxiosError) {
      if (error.response) {
        console.error(`API Error ${error.response.status}: ${error.response.data?.detail || 'Unknown error'}`);
      } else if (error.request) {
        console.error('No response received from the server');
      } else {
        console.error('Error setting up request:', error.message);
      }
    }
  }
}

module.exports = new RecipeService();