const axios = require('axios');
const config = require('../config/pythonService');
const recipeTransformer = require('../transformers/recipeTransformer');
// config/pythonService.js
require('dotenv').config();



class RecipeService {
  constructor() {
    this.client = axios.create({
      baseURL: `${config.baseUrl}/api/${config.apiVersion}`,
      timeout: config.timeout
    });
  }

  async getRecommendedMeals(userId = config.defaultUserId, limit = 10) {
    try {
      const response = await this.client.get(`/recommend/user/${userId}`, {
        params: { limit, recommendation_type: 'hybrid' }
      });
      
      return this._transformItems(response.data.items);
    } catch (error) {
      console.error('Failed to fetch recommended meals:', error);
      throw error;
    }
  }

  async getTrendingMeals(limit = 10, timeWindow = 'week') {
    try {
      const response = await this.client.get('/trending', {
        params: { limit, time_window: timeWindow }
      });
      
      return this._transformItems(response.data.items);
    } catch (error) {
      console.error('Failed to fetch trending meals:', error);
      throw error;
    }
  }

  async getSimilarMeals(recipeId, limit = 5) {
    try {
      const response = await this.client.get(`/recommend/similar/${recipeId}`, {
        params: { limit }
      });
      
      return this._transformItems(response.data.items);
    } catch (error) {
      console.error(`Failed to fetch similar meals for recipe ${recipeId}:`, error);
      throw error;
    }
  }

  async getMealDetails(recipeId) {
    try {
      const response = await this.client.get(`/recipes/${recipeId}`);
      return recipeTransformer.transformToMeal(response.data);
    } catch (error) {
      console.error(`Failed to fetch meal details for recipe ${recipeId}:`, error);
      throw error;
    }
  }

  async recordInteraction(userId, mealId, interactionType, rating = null) {
    try {
      await this.client.post('/interactions', {
        user_id: userId,
        meal_id: mealId,
        interaction_type: interactionType,
        rating
      });
      return true;
    } catch (error) {
      console.error(`Failed to record interaction for user ${userId} on meal ${mealId}:`, error);
      throw error;
    }
  }

  // NEW ENDPOINTS

  async getQuickMeals(maxTime = 30, limit = 10, cuisine = null, dietaryRestriction = null) {
    try {
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
      console.error('Failed to fetch quick meals:', error);
      throw error;
    }
  }

  async getCuisineRecommendations(cuisineId, limit = 10) {
    try {
      const response = await this.client.get(`/recommend/cuisine/${cuisineId}`, {
        params: { limit }
      });
      
      return this._transformItems(response.data.items);
    } catch (error) {
      console.error(`Failed to fetch cuisine recommendations for ${cuisineId}:`, error);
      throw error;
    }
  }

  async getDietaryRecommendations(dietaryRestriction, limit = 10) {
    try {
      const response = await this.client.get(`/recommend/dietary/${dietaryRestriction}`, {
        params: { limit }
      });
      
      return this._transformItems(response.data.items);
    } catch (error) {
      console.error(`Failed to fetch dietary recommendations for ${dietaryRestriction}:`, error);
      throw error;
    }
  }

  async getFilteredRecipes(options = {}) {
    try {
      const { page = 1, limit = 20, region, subRegion, minCalories, maxCalories } = options;
      
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
      console.error('Failed to fetch filtered recipes:', error);
      throw error;
    }
  }

  async getRecipesByCalories(min = 0, max = 1000, page = 1, limit = 20) {
    try {
      const response = await this.client.get('/recipes/filter/calories', {
        params: { min, max, page, limit }
      });
      
      return response.data.map(recipe => recipeTransformer.transformToMeal(recipe));
    } catch (error) {
      console.error('Failed to fetch recipes by calories:', error);
      throw error;
    }
  }

  _transformItems(items) {
    if (!items || !Array.isArray(items)) return [];
    return Promise.all(items.map(item => 
      this.getMealDetails(item.id)
    ));
  }
}

module.exports = new RecipeService();
module.exports = {
  baseUrl: process.env.PYTHON_SERVICE_URL || 'http://127.0.0.1:9999',
  apiVersion: process.env.PYTHON_API_VERSION || 'v1',
  timeout: parseInt(process.env.PYTHON_SERVICE_TIMEOUT || '5000'),
  defaultUserId: process.env.DEFAULT_USER_ID || 'default_user'
};