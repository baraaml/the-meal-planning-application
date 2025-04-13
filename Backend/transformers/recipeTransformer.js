class RecipeTransformer {
    transformToMeal(recipe) {
      // Map Python service recipe format to required meal object structure
      return {
        meal_id: recipe.recipe_id.toString(),
        name: recipe.recipe_title,
        description: recipe.description || `A delicious ${recipe.region || ''} dish.`,
        image_url: recipe.image_url || `https://mealflow.ddns.net/images/meals/${recipe.recipe_id}.jpg`,
        tags: this._generateTags(recipe),
        ingredients: this._formatIngredients(recipe.ingredients || []),
        instructions: this._formatInstructions(recipe.instructions),
        cookware: this._extractCookware(recipe.instructions),
        preparation_time: recipe.prep_time || 15,
        cooking_time: recipe.cook_time || 20,
        servings: recipe.servings || 2,
        calories_per_serving: recipe.calories || 350,
        rating: this._calculateRating(recipe),
        reviews_count: Math.floor(Math.random() * 200) + 10, // Placeholder
        created_by: this._getCreator(recipe),
        is_favorited: false,
        is_saved: false,
        notes: [],
        interactions: {
          views: Math.floor(Math.random() * 1000) + 100,
          likes: Math.floor(Math.random() * 400) + 50,
          dislikes: Math.floor(Math.random() * 20),
          shares: Math.floor(Math.random() * 50) + 5
        },
        created_at: recipe.created_at || new Date().toISOString(),
        updated_at: recipe.updated_at || new Date().toISOString()
      };
    }
  
    _generateTags(recipe) {
      const tags = [];
      if (recipe.region) tags.push(recipe.region.toLowerCase());
      if (recipe.sub_region) tags.push(recipe.sub_region.toLowerCase());
      
      // Add dietary tags if available
      if (recipe.diet_attributes) {
        if (recipe.diet_attributes.vegan) tags.push('vegan');
        if (recipe.diet_attributes.vegetarian) tags.push('vegetarian');
      }
      
      // Add time-based tags
      if (recipe.total_time && recipe.total_time <= 30) tags.push('quick');
      
      return tags;
    }
  
    _formatIngredients(ingredients) {
      if (!ingredients.length) return [];
      
      return ingredients.map(ingredient => ({
        name: ingredient.ingredient_name,
        quantity: ingredient.quantity || 1,
        unit: ingredient.unit || 'pcs'
      }));
    }
  
    _formatInstructions(instructions) {
      if (!instructions) return ["No instructions available."];
      
      if (typeof instructions === 'string') {
        return instructions.split(/\.\s+|[\r\n]+/).filter(step => step.trim().length > 0);
      }
      
      return instructions;
    }
  
    _extractCookware(instructions) {
      const cookwareKeywords = ['pan', 'pot', 'bowl', 'knife', 'oven', 'spoon', 'cutting board', 'blender'];
      const cookware = new Set();
      
      if (typeof instructions === 'string') {
        cookwareKeywords.forEach(item => {
          if (instructions.toLowerCase().includes(item)) {
            cookware.add(item);
          }
        });
      }
      
      return Array.from(cookware);
    }
  
    _calculateRating(recipe) {
      return recipe.rating || (Math.random() * 2 + 3).toFixed(1); // Random rating between 3.0-5.0
    }
  
    _getCreator(recipe) {
      return {
        user_id: recipe.owner_id || "user_system",
        username: recipe.owner_name || "MealFlow",
        profile_picture: "https://mealflow.ddns.net/images/users/default.jpg"
      };
    }
  }
  
  module.exports = new RecipeTransformer();