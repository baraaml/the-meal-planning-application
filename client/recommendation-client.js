/**
 * Recommendation Service Client for Node.js
 * 
 * Provides an interface for interacting with the recommendation service API.
 */
const fetch = require('node-fetch');

// Load configuration
const RECOMMENDATION_API_URL = process.env.RECOMMENDATION_API_URL || 'http://localhost:8000';

/**
 * Get personalized recommendations for a user
 * 
 * @param {string} userId - The user's ID
 * @param {string} contentType - Optional content type filter ('post' or 'community')
 * @param {number} limit - Maximum number of recommendations
 * @param {string} recommendationType - Algorithm to use ('hybrid', 'item-based', 'user-based')
 * @returns {Promise<Array>} - Recommended items
 */
async function getUserRecommendations(userId, contentType = null, limit = 10, recommendationType = 'hybrid') {
  try {
    let url = `${RECOMMENDATION_API_URL}/recommend/user/${userId}?limit=${limit}&recommendation_type=${recommendationType}`;
    if (contentType) {
      url += `&content_type=${contentType}`;
    }
    
    const response = await fetch(url);
    
    if (!response.ok) {
      const error = await response.json();
      throw new Error(error.detail || 'Error fetching user recommendations');
    }
    
    const data = await response.json();
    return data.items;
  } catch (error) {
    console.error('Error fetching user recommendations:', error);
    return [];
  }
}

/**
 * Get similar content recommendations
 * 
 * @param {string} contentType - The type of content ('post' or 'community')
 * @param {string} contentId - The ID of the content
 * @param {number} limit - Maximum number of recommendations
 * @param {string} similarityMethod - Method to determine similarity ('content' or 'interaction')
 * @returns {Promise<Array>} - Similar items
 */
async function getSimilarContent(contentType, contentId, limit = 10, similarityMethod = 'content') {
  try {
    const url = `${RECOMMENDATION_API_URL}/recommend/similar/${contentType}/${contentId}?limit=${limit}&similarity_method=${similarityMethod}`;
    const response = await fetch(url);
    
    if (!response.ok) {
      const error = await response.json();
      throw new Error(error.detail || 'Error fetching similar content');
    }
    
    const data = await response.json();
    return data.items;
  } catch (error) {
    console.error('Error fetching similar content:', error);
    return [];
  }
}

/**
 * Record a user interaction with content
 * 
 * @param {string} userId - The user's ID
 * @param {string} contentId - The ID of the content
 * @param {string} contentType - The type of content ('post', 'community', 'comment')
 * @param {string} interactionType - The type of interaction ('view', 'click', 'vote', etc.)
 * @returns {Promise<boolean>} - Success status
 */
async function recordInteraction(userId, contentId, contentType, interactionType) {
  try {
    const url = `${RECOMMENDATION_API_URL}/interactions`;
    const response = await fetch(url, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        user_id: userId,
        meal_id: contentId,
        content_type: contentType,
        interaction_type: interactionType
      })
    });
    
    if (!response.ok) {
      const error = await response.json();
      throw new Error(error.detail || 'Error recording interaction');
    }
    
    return true;
  } catch (error) {
    console.error('Error recording interaction:', error);
    return false;
  }
}

/**
 * Get trending content
 * 
 * @param {string} contentType - The type of content ('post', 'community', or 'all')
 * @param {string} timeWindow - Time window ('day', 'week', 'month')
 * @param {number} limit - Maximum number of items
 * @returns {Promise<Array>} - Trending items
 */
async function getTrendingContent(contentType = 'all', timeWindow = 'day', limit = 10) {
  try {
    const url = `${RECOMMENDATION_API_URL}/trending/${contentType}?time_window=${timeWindow}&limit=${limit}`;
    const response = await fetch(url);
    
    if (!response.ok) {
      const error = await response.json();
      throw new Error(error.detail || 'Error fetching trending content');
    }
    
    const data = await response.json();
    return data.items;
  } catch (error) {
    console.error('Error fetching trending content:', error);
    return [];
  }
}

/**
 * Get community recommendations based on category
 * 
 * @param {string} categoryId - The ID of the category
 * @param {number} limit - Maximum number of items
 * @returns {Promise<Array>} - Communities in the category
 */
async function getCategoryRecommendations(categoryId, limit = 10) {
  try {
    const url = `${RECOMMENDATION_API_URL}/recommend/category/${categoryId}?limit=${limit}`;
    const response = await fetch(url);
    
    if (!response.ok) {
      const error = await response.json();
      throw new Error(error.detail || 'Error fetching category recommendations');
    }
    
    const data = await response.json();
    return data.items;
  } catch (error) {
    console.error('Error fetching category recommendations:', error);
    return [];
  }
}

module.exports = {
  getUserRecommendations,
  getSimilarContent,
  recordInteraction,
  getTrendingContent,
  getCategoryRecommendations
};