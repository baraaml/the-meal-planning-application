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

  _transformItems(items) {
    if (!items || !Array.isArray(items)) return [];
    return Promise.all(items.map(item => 
      this.getMealDetails(item.id)
    ));
  }
}

module.exports = new RecipeService();