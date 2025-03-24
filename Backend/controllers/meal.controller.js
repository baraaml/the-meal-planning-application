const { StatusCodes } = require("http-status-codes");

const getRecommendedMeals = async (req, res) => {
  const recommendedMeals = [
    {
      "meal_id": "meal_001",
      "name": "Spaghetti محشي",
      "description": "A classic Italian pasta dish with eggs, cheese, pancetta, and pepper.",
      "image_url": "https://mealflow.ddns.net/images/meal_001.jpg",
      "tags": ["pasta", "italian", "quick", "dinner"],
      "ingredients": [
        {"name": "Pasta", "quantity": 200, "unit": "g"},
        {"name": "Egg", "quantity": 2, "unit": "pcs"},
        {"name": "Parmesan", "quantity": 50, "unit": "g"},
        {"name": "Bacon", "quantity": 100, "unit": "g"},
        {"name": "Black Pepper", "quantity": 5, "unit": "g"}
      ],
      "instructions": [
        "Boil pasta until al dente.",
        "Cook bacon in a pan until crispy.",
        "Whisk eggs and parmesan together.",
        "Drain pasta and mix everything together with black pepper."
      ],
      "cookware": ["pan", "pot"],
      "preparation_time": 20,
      "cooking_time": 10,
      "servings": 2,
      "calories_per_serving": 450,
      "rating": 4.7,
      "reviews_count": 125,
      "created_by": {
        "user_id": "user_123",
        "username": "foodlover",
        "profile_picture": "https://mealflow.ddns.net/images/users/user_123.jpg"
      },
      "is_favorited": true,
      "is_saved": false,
      "notes": [
        {
          "note_id": "note_567",
          "user": {
            "user_id": "user_456",
            "username": "chefmaster"
          },
          "comment": "This was great, but I added garlic and it was even better!",
          "image_url": "https://mealflow.ddns.net/images/notes/note_567.jpg",
          "tags": ["modified", "garlic"],
          "did_cook": true,
          "likes": 10,
          "dislikes": 2
        }
      ],
      "interactions": {
        "views": 500,
        "likes": 200,
        "dislikes": 5,
        "shares": 30
      },
      "created_at": "2025-03-05T12:00:00Z",
      "updated_at": "2025-03-05T14:30:00Z"
    },
    {
      "meal_id": "meal_002",
      "name": "Chicken Curry",
      "description": "A flavorful Indian-inspired dish with tender chicken in a rich curry sauce.",
      "image_url": "https://mealflow.ddns.net/images/meal_002.jpg",
      "tags": ["curry", "indian", "chicken", "dinner"],
      "ingredients": [
        {"name": "Chicken Breast", "quantity": 500, "unit": "g"},
        {"name": "Curry Powder", "quantity": 20, "unit": "g"},
        {"name": "Coconut Milk", "quantity": 400, "unit": "ml"},
        {"name": "Onion", "quantity": 1, "unit": "pcs"},
        {"name": "Garlic", "quantity": 3, "unit": "cloves"},
        {"name": "Ginger", "quantity": 15, "unit": "g"},
        {"name": "Vegetable Oil", "quantity": 30, "unit": "ml"}
      ],
      "instructions": [
        "Dice chicken into cubes.",
        "Sauté onions, garlic, and ginger in oil until fragrant.",
        "Add chicken and brown on all sides.",
        "Add curry powder and stir for 1 minute.",
        "Pour in coconut milk and simmer for 20 minutes."
      ],
      "cookware": ["pan", "knife", "cutting board"],
      "preparation_time": 15,
      "cooking_time": 25,
      "servings": 4,
      "calories_per_serving": 380,
      "rating": 4.5,
      "reviews_count": 87,
      "created_by": {
        "user_id": "user_456",
        "username": "chefmaster",
        "profile_picture": "https://mealflow.ddns.net/images/users/user_456.jpg"
      },
      "is_favorited": false,
      "is_saved": true,
      "notes": [],
      "interactions": {
        "views": 320,
        "likes": 150,
        "dislikes": 8,
        "shares": 25
      },
      "created_at": "2025-02-20T15:45:00Z",
      "updated_at": "2025-03-01T10:15:00Z"
    },
    {
      "meal_id": "meal_003",
      "name": "Pizza",
      "description": "A flavorful Indian-inspired dish with tender chicken in a rich curry sauce.",
      "image_url": "https://mealflow.ddns.net/images/meal_002.jpg",
      "tags": ["curry", "indian", "chicken", "dinner"],
      "ingredients": [
        {"name": "Chicken Breast", "quantity": 500, "unit": "g"},
        {"name": "Curry Powder", "quantity": 20, "unit": "g"},
        {"name": "Coconut Milk", "quantity": 400, "unit": "ml"},
        {"name": "Onion", "quantity": 1, "unit": "pcs"},
        {"name": "Garlic", "quantity": 3, "unit": "cloves"},
        {"name": "Ginger", "quantity": 15, "unit": "g"},
        {"name": "Vegetable Oil", "quantity": 30, "unit": "ml"}
      ],
      "instructions": [
        "Dice chicken into cubes.",
        "Sauté onions, garlic, and ginger in oil until fragrant.",
        "Add chicken and brown on all sides.",
        "Add curry powder and stir for 1 minute.",
        "Pour in coconut milk and simmer for 20 minutes."
      ],
      "cookware": ["pan", "knife", "cutting board"],
      "preparation_time": 15,
      "cooking_time": 25,
      "servings": 4,
      "calories_per_serving": 380,
      "rating": 4.5,
      "reviews_count": 87,
      "created_by": {
        "user_id": "user_456",
        "username": "chefmaster",
        "profile_picture": "https://mealflow.ddns.net/images/users/user_456.jpg"
      },
      "is_favorited": false,
      "is_saved": true,
      "notes": [],
      "interactions": {
        "views": 320,
        "likes": 150,
        "dislikes": 8,
        "shares": 25
      },
      "created_at": "2025-02-20T15:45:00Z",
      "updated_at": "2025-03-01T10:15:00Z"
    },
    {
      "meal_id": "meal_004",
      "name": "Cake",
      "description": "A flavorful Indian-inspired dish with tender chicken in a rich curry sauce.",
      "image_url": "https://mealflow.ddns.net/images/meal_002.jpg",
      "tags": ["curry", "indian", "chicken", "dinner"],
      "ingredients": [
        {"name": "Chicken Breast", "quantity": 500, "unit": "g"},
        {"name": "Curry Powder", "quantity": 20, "unit": "g"},
        {"name": "Coconut Milk", "quantity": 400, "unit": "ml"},
        {"name": "Onion", "quantity": 1, "unit": "pcs"},
        {"name": "Garlic", "quantity": 3, "unit": "cloves"},
        {"name": "Ginger", "quantity": 15, "unit": "g"},
        {"name": "Vegetable Oil", "quantity": 30, "unit": "ml"}
      ],
      "instructions": [
        "Dice chicken into cubes.",
        "Sauté onions, garlic, and ginger in oil until fragrant.",
        "Add chicken and brown on all sides.",
        "Add curry powder and stir for 1 minute.",
        "Pour in coconut milk and simmer for 20 minutes."
      ],
      "cookware": ["pan", "knife", "cutting board"],
      "preparation_time": 15,
      "cooking_time": 25,
      "servings": 4,
      "calories_per_serving": 380,
      "rating": 4.5,
      "reviews_count": 87,
      "created_by": {
        "user_id": "user_456",
        "username": "chefmaster",
        "profile_picture": "https://mealflow.ddns.net/images/users/user_456.jpg"
      },
      "is_favorited": false,
      "is_saved": true,
      "notes": [],
      "interactions": {
        "views": 320,
        "likes": 150,
        "dislikes": 8,
        "shares": 25
      },
      "created_at": "2025-02-20T15:45:00Z",
      "updated_at": "2025-03-01T10:15:00Z"
    },
    {
      "meal_id": "meal_001",
      "name": "Spaghetti محشي",
      "description": "A classic Italian pasta dish with eggs, cheese, pancetta, and pepper.",
      "image_url": "https://mealflow.ddns.net/images/meal_001.jpg",
      "tags": ["pasta", "italian", "quick", "dinner"],
      "ingredients": [
        {"name": "Pasta", "quantity": 200, "unit": "g"},
        {"name": "Egg", "quantity": 2, "unit": "pcs"},
        {"name": "Parmesan", "quantity": 50, "unit": "g"},
        {"name": "Bacon", "quantity": 100, "unit": "g"},
        {"name": "Black Pepper", "quantity": 5, "unit": "g"}
      ],
      "instructions": [
        "Boil pasta until al dente.",
        "Cook bacon in a pan until crispy.",
        "Whisk eggs and parmesan together.",
        "Drain pasta and mix everything together with black pepper."
      ],
      "cookware": ["pan", "pot"],
      "preparation_time": 20,
      "cooking_time": 10,
      "servings": 2,
      "calories_per_serving": 450,
      "rating": 4.7,
      "reviews_count": 125,
      "created_by": {
        "user_id": "user_123",
        "username": "foodlover",
        "profile_picture": "https://mealflow.ddns.net/images/users/user_123.jpg"
      },
      "is_favorited": true,
      "is_saved": false,
      "notes": [
        {
          "note_id": "note_567",
          "user": {
            "user_id": "user_456",
            "username": "chefmaster"
          },
          "comment": "This was great, but I added garlic and it was even better!",
          "image_url": "https://mealflow.ddns.net/images/notes/note_567.jpg",
          "tags": ["modified", "garlic"],
          "did_cook": true,
          "likes": 10,
          "dislikes": 2
        }
      ],
      "interactions": {
        "views": 500,
        "likes": 200,
        "dislikes": 5,
        "shares": 30
      },
      "created_at": "2025-03-05T12:00:00Z",
      "updated_at": "2025-03-05T14:30:00Z"
    },
    {
      "meal_id": "meal_002",
      "name": "Chicken Curry",
      "description": "A flavorful Indian-inspired dish with tender chicken in a rich curry sauce.",
      "image_url": "https://mealflow.ddns.net/images/meal_002.jpg",
      "tags": ["curry", "indian", "chicken", "dinner"],
      "ingredients": [
        {"name": "Chicken Breast", "quantity": 500, "unit": "g"},
        {"name": "Curry Powder", "quantity": 20, "unit": "g"},
        {"name": "Coconut Milk", "quantity": 400, "unit": "ml"},
        {"name": "Onion", "quantity": 1, "unit": "pcs"},
        {"name": "Garlic", "quantity": 3, "unit": "cloves"},
        {"name": "Ginger", "quantity": 15, "unit": "g"},
        {"name": "Vegetable Oil", "quantity": 30, "unit": "ml"}
      ],
      "instructions": [
        "Dice chicken into cubes.",
        "Sauté onions, garlic, and ginger in oil until fragrant.",
        "Add chicken and brown on all sides.",
        "Add curry powder and stir for 1 minute.",
        "Pour in coconut milk and simmer for 20 minutes."
      ],
      "cookware": ["pan", "knife", "cutting board"],
      "preparation_time": 15,
      "cooking_time": 25,
      "servings": 4,
      "calories_per_serving": 380,
      "rating": 4.5,
      "reviews_count": 87,
      "created_by": {
        "user_id": "user_456",
        "username": "chefmaster",
        "profile_picture": "https://mealflow.ddns.net/images/users/user_456.jpg"
      },
      "is_favorited": false,
      "is_saved": true,
      "notes": [],
      "interactions": {
        "views": 320,
        "likes": 150,
        "dislikes": 8,
        "shares": 25
      },
      "created_at": "2025-02-20T15:45:00Z",
      "updated_at": "2025-03-01T10:15:00Z"
    },
    {
      "meal_id": "meal_003",
      "name": "Pizza",
      "description": "A flavorful Indian-inspired dish with tender chicken in a rich curry sauce.",
      "image_url": "https://mealflow.ddns.net/images/meal_002.jpg",
      "tags": ["curry", "indian", "chicken", "dinner"],
      "ingredients": [
        {"name": "Chicken Breast", "quantity": 500, "unit": "g"},
        {"name": "Curry Powder", "quantity": 20, "unit": "g"},
        {"name": "Coconut Milk", "quantity": 400, "unit": "ml"},
        {"name": "Onion", "quantity": 1, "unit": "pcs"},
        {"name": "Garlic", "quantity": 3, "unit": "cloves"},
        {"name": "Ginger", "quantity": 15, "unit": "g"},
        {"name": "Vegetable Oil", "quantity": 30, "unit": "ml"}
      ],
      "instructions": [
        "Dice chicken into cubes.",
        "Sauté onions, garlic, and ginger in oil until fragrant.",
        "Add chicken and brown on all sides.",
        "Add curry powder and stir for 1 minute.",
        "Pour in coconut milk and simmer for 20 minutes."
      ],
      "cookware": ["pan", "knife", "cutting board"],
      "preparation_time": 15,
      "cooking_time": 25,
      "servings": 4,
      "calories_per_serving": 380,
      "rating": 4.5,
      "reviews_count": 87,
      "created_by": {
        "user_id": "user_456",
        "username": "chefmaster",
        "profile_picture": "https://mealflow.ddns.net/images/users/user_456.jpg"
      },
      "is_favorited": false,
      "is_saved": true,
      "notes": [],
      "interactions": {
        "views": 320,
        "likes": 150,
        "dislikes": 8,
        "shares": 25
      },
      "created_at": "2025-02-20T15:45:00Z",
      "updated_at": "2025-03-01T10:15:00Z"
    },
    {
      "meal_id": "meal_004",
      "name": "Cake",
      "description": "A flavorful Indian-inspired dish with tender chicken in a rich curry sauce.",
      "image_url": "https://mealflow.ddns.net/images/meal_002.jpg",
      "tags": ["curry", "indian", "chicken", "dinner"],
      "ingredients": [
        {"name": "Chicken Breast", "quantity": 500, "unit": "g"},
        {"name": "Curry Powder", "quantity": 20, "unit": "g"},
        {"name": "Coconut Milk", "quantity": 400, "unit": "ml"},
        {"name": "Onion", "quantity": 1, "unit": "pcs"},
        {"name": "Garlic", "quantity": 3, "unit": "cloves"},
        {"name": "Ginger", "quantity": 15, "unit": "g"},
        {"name": "Vegetable Oil", "quantity": 30, "unit": "ml"}
      ],
      "instructions": [
        "Dice chicken into cubes.",
        "Sauté onions, garlic, and ginger in oil until fragrant.",
        "Add chicken and brown on all sides.",
        "Add curry powder and stir for 1 minute.",
        "Pour in coconut milk and simmer for 20 minutes."
      ],
      "cookware": ["pan", "knife", "cutting board"],
      "preparation_time": 15,
      "cooking_time": 25,
      "servings": 4,
      "calories_per_serving": 380,
      "rating": 4.5,
      "reviews_count": 87,
      "created_by": {
        "user_id": "user_456",
        "username": "chefmaster",
        "profile_picture": "https://mealflow.ddns.net/images/users/user_456.jpg"
      },
      "is_favorited": false,
      "is_saved": true,
      "notes": [],
      "interactions": {
        "views": 320,
        "likes": 150,
        "dislikes": 8,
        "shares": 25
      },
      "created_at": "2025-02-20T15:45:00Z",
      "updated_at": "2025-03-01T10:15:00Z"
    },
    {
      "meal_id": "meal_001",
      "name": "Spaghetti محشي",
      "description": "A classic Italian pasta dish with eggs, cheese, pancetta, and pepper.",
      "image_url": "https://mealflow.ddns.net/images/meal_001.jpg",
      "tags": ["pasta", "italian", "quick", "dinner"],
      "ingredients": [
        {"name": "Pasta", "quantity": 200, "unit": "g"},
        {"name": "Egg", "quantity": 2, "unit": "pcs"},
        {"name": "Parmesan", "quantity": 50, "unit": "g"},
        {"name": "Bacon", "quantity": 100, "unit": "g"},
        {"name": "Black Pepper", "quantity": 5, "unit": "g"}
      ],
      "instructions": [
        "Boil pasta until al dente.",
        "Cook bacon in a pan until crispy.",
        "Whisk eggs and parmesan together.",
        "Drain pasta and mix everything together with black pepper."
      ],
      "cookware": ["pan", "pot"],
      "preparation_time": 20,
      "cooking_time": 10,
      "servings": 2,
      "calories_per_serving": 450,
      "rating": 4.7,
      "reviews_count": 125,
      "created_by": {
        "user_id": "user_123",
        "username": "foodlover",
        "profile_picture": "https://mealflow.ddns.net/images/users/user_123.jpg"
      },
      "is_favorited": true,
      "is_saved": false,
      "notes": [
        {
          "note_id": "note_567",
          "user": {
            "user_id": "user_456",
            "username": "chefmaster"
          },
          "comment": "This was great, but I added garlic and it was even better!",
          "image_url": "https://mealflow.ddns.net/images/notes/note_567.jpg",
          "tags": ["modified", "garlic"],
          "did_cook": true,
          "likes": 10,
          "dislikes": 2
        }
      ],
      "interactions": {
        "views": 500,
        "likes": 200,
        "dislikes": 5,
        "shares": 30
      },
      "created_at": "2025-03-05T12:00:00Z",
      "updated_at": "2025-03-05T14:30:00Z"
    },
    {
      "meal_id": "meal_002",
      "name": "Chicken Curry",
      "description": "A flavorful Indian-inspired dish with tender chicken in a rich curry sauce.",
      "image_url": "https://mealflow.ddns.net/images/meal_002.jpg",
      "tags": ["curry", "indian", "chicken", "dinner"],
      "ingredients": [
        {"name": "Chicken Breast", "quantity": 500, "unit": "g"},
        {"name": "Curry Powder", "quantity": 20, "unit": "g"},
        {"name": "Coconut Milk", "quantity": 400, "unit": "ml"},
        {"name": "Onion", "quantity": 1, "unit": "pcs"},
        {"name": "Garlic", "quantity": 3, "unit": "cloves"},
        {"name": "Ginger", "quantity": 15, "unit": "g"},
        {"name": "Vegetable Oil", "quantity": 30, "unit": "ml"}
      ],
      "instructions": [
        "Dice chicken into cubes.",
        "Sauté onions, garlic, and ginger in oil until fragrant.",
        "Add chicken and brown on all sides.",
        "Add curry powder and stir for 1 minute.",
        "Pour in coconut milk and simmer for 20 minutes."
      ],
      "cookware": ["pan", "knife", "cutting board"],
      "preparation_time": 15,
      "cooking_time": 25,
      "servings": 4,
      "calories_per_serving": 380,
      "rating": 4.5,
      "reviews_count": 87,
      "created_by": {
        "user_id": "user_456",
        "username": "chefmaster",
        "profile_picture": "https://mealflow.ddns.net/images/users/user_456.jpg"
      },
      "is_favorited": false,
      "is_saved": true,
      "notes": [],
      "interactions": {
        "views": 320,
        "likes": 150,
        "dislikes": 8,
        "shares": 25
      },
      "created_at": "2025-02-20T15:45:00Z",
      "updated_at": "2025-03-01T10:15:00Z"
    },
    {
      "meal_id": "meal_003",
      "name": "Pizza",
      "description": "A flavorful Indian-inspired dish with tender chicken in a rich curry sauce.",
      "image_url": "https://mealflow.ddns.net/images/meal_002.jpg",
      "tags": ["curry", "indian", "chicken", "dinner"],
      "ingredients": [
        {"name": "Chicken Breast", "quantity": 500, "unit": "g"},
        {"name": "Curry Powder", "quantity": 20, "unit": "g"},
        {"name": "Coconut Milk", "quantity": 400, "unit": "ml"},
        {"name": "Onion", "quantity": 1, "unit": "pcs"},
        {"name": "Garlic", "quantity": 3, "unit": "cloves"},
        {"name": "Ginger", "quantity": 15, "unit": "g"},
        {"name": "Vegetable Oil", "quantity": 30, "unit": "ml"}
      ],
      "instructions": [
        "Dice chicken into cubes.",
        "Sauté onions, garlic, and ginger in oil until fragrant.",
        "Add chicken and brown on all sides.",
        "Add curry powder and stir for 1 minute.",
        "Pour in coconut milk and simmer for 20 minutes."
      ],
      "cookware": ["pan", "knife", "cutting board"],
      "preparation_time": 15,
      "cooking_time": 25,
      "servings": 4,
      "calories_per_serving": 380,
      "rating": 4.5,
      "reviews_count": 87,
      "created_by": {
        "user_id": "user_456",
        "username": "chefmaster",
        "profile_picture": "https://mealflow.ddns.net/images/users/user_456.jpg"
      },
      "is_favorited": false,
      "is_saved": true,
      "notes": [],
      "interactions": {
        "views": 320,
        "likes": 150,
        "dislikes": 8,
        "shares": 25
      },
      "created_at": "2025-02-20T15:45:00Z",
      "updated_at": "2025-03-01T10:15:00Z"
    },
    {
      "meal_id": "meal_004",
      "name": "Cake",
      "description": "A flavorful Indian-inspired dish with tender chicken in a rich curry sauce.",
      "image_url": "https://mealflow.ddns.net/images/meal_002.jpg",
      "tags": ["curry", "indian", "chicken", "dinner"],
      "ingredients": [
        {"name": "Chicken Breast", "quantity": 500, "unit": "g"},
        {"name": "Curry Powder", "quantity": 20, "unit": "g"},
        {"name": "Coconut Milk", "quantity": 400, "unit": "ml"},
        {"name": "Onion", "quantity": 1, "unit": "pcs"},
        {"name": "Garlic", "quantity": 3, "unit": "cloves"},
        {"name": "Ginger", "quantity": 15, "unit": "g"},
        {"name": "Vegetable Oil", "quantity": 30, "unit": "ml"}
      ],
      "instructions": [
        "Dice chicken into cubes.",
        "Sauté onions, garlic, and ginger in oil until fragrant.",
        "Add chicken and brown on all sides.",
        "Add curry powder and stir for 1 minute.",
        "Pour in coconut milk and simmer for 20 minutes."
      ],
      "cookware": ["pan", "knife", "cutting board"],
      "preparation_time": 15,
      "cooking_time": 25,
      "servings": 4,
      "calories_per_serving": 380,
      "rating": 4.5,
      "reviews_count": 87,
      "created_by": {
        "user_id": "user_456",
        "username": "chefmaster",
        "profile_picture": "https://mealflow.ddns.net/images/users/user_456.jpg"
      },
      "is_favorited": false,
      "is_saved": true,
      "notes": [],
      "interactions": {
        "views": 320,
        "likes": 150,
        "dislikes": 8,
        "shares": 25
      },
      "created_at": "2025-02-20T15:45:00Z",
      "updated_at": "2025-03-01T10:15:00Z"
    },
    {
      "meal_id": "meal_001",
      "name": "Spaghetti محشي",
      "description": "A classic Italian pasta dish with eggs, cheese, pancetta, and pepper.",
      "image_url": "https://mealflow.ddns.net/images/meal_001.jpg",
      "tags": ["pasta", "italian", "quick", "dinner"],
      "ingredients": [
        {"name": "Pasta", "quantity": 200, "unit": "g"},
        {"name": "Egg", "quantity": 2, "unit": "pcs"},
        {"name": "Parmesan", "quantity": 50, "unit": "g"},
        {"name": "Bacon", "quantity": 100, "unit": "g"},
        {"name": "Black Pepper", "quantity": 5, "unit": "g"}
      ],
      "instructions": [
        "Boil pasta until al dente.",
        "Cook bacon in a pan until crispy.",
        "Whisk eggs and parmesan together.",
        "Drain pasta and mix everything together with black pepper."
      ],
      "cookware": ["pan", "pot"],
      "preparation_time": 20,
      "cooking_time": 10,
      "servings": 2,
      "calories_per_serving": 450,
      "rating": 4.7,
      "reviews_count": 125,
      "created_by": {
        "user_id": "user_123",
        "username": "foodlover",
        "profile_picture": "https://mealflow.ddns.net/images/users/user_123.jpg"
      },
      "is_favorited": true,
      "is_saved": false,
      "notes": [
        {
          "note_id": "note_567",
          "user": {
            "user_id": "user_456",
            "username": "chefmaster"
          },
          "comment": "This was great, but I added garlic and it was even better!",
          "image_url": "https://mealflow.ddns.net/images/notes/note_567.jpg",
          "tags": ["modified", "garlic"],
          "did_cook": true,
          "likes": 10,
          "dislikes": 2
        }
      ],
      "interactions": {
        "views": 500,
        "likes": 200,
        "dislikes": 5,
        "shares": 30
      },
      "created_at": "2025-03-05T12:00:00Z",
      "updated_at": "2025-03-05T14:30:00Z"
    },
    {
      "meal_id": "meal_002",
      "name": "Chicken Curry",
      "description": "A flavorful Indian-inspired dish with tender chicken in a rich curry sauce.",
      "image_url": "https://mealflow.ddns.net/images/meal_002.jpg",
      "tags": ["curry", "indian", "chicken", "dinner"],
      "ingredients": [
        {"name": "Chicken Breast", "quantity": 500, "unit": "g"},
        {"name": "Curry Powder", "quantity": 20, "unit": "g"},
        {"name": "Coconut Milk", "quantity": 400, "unit": "ml"},
        {"name": "Onion", "quantity": 1, "unit": "pcs"},
        {"name": "Garlic", "quantity": 3, "unit": "cloves"},
        {"name": "Ginger", "quantity": 15, "unit": "g"},
        {"name": "Vegetable Oil", "quantity": 30, "unit": "ml"}
      ],
      "instructions": [
        "Dice chicken into cubes.",
        "Sauté onions, garlic, and ginger in oil until fragrant.",
        "Add chicken and brown on all sides.",
        "Add curry powder and stir for 1 minute.",
        "Pour in coconut milk and simmer for 20 minutes."
      ],
      "cookware": ["pan", "knife", "cutting board"],
      "preparation_time": 15,
      "cooking_time": 25,
      "servings": 4,
      "calories_per_serving": 380,
      "rating": 4.5,
      "reviews_count": 87,
      "created_by": {
        "user_id": "user_456",
        "username": "chefmaster",
        "profile_picture": "https://mealflow.ddns.net/images/users/user_456.jpg"
      },
      "is_favorited": false,
      "is_saved": true,
      "notes": [],
      "interactions": {
        "views": 320,
        "likes": 150,
        "dislikes": 8,
        "shares": 25
      },
      "created_at": "2025-02-20T15:45:00Z",
      "updated_at": "2025-03-01T10:15:00Z"
    },
    {
      "meal_id": "meal_003",
      "name": "Pizza",
      "description": "A flavorful Indian-inspired dish with tender chicken in a rich curry sauce.",
      "image_url": "https://mealflow.ddns.net/images/meal_002.jpg",
      "tags": ["curry", "indian", "chicken", "dinner"],
      "ingredients": [
        {"name": "Chicken Breast", "quantity": 500, "unit": "g"},
        {"name": "Curry Powder", "quantity": 20, "unit": "g"},
        {"name": "Coconut Milk", "quantity": 400, "unit": "ml"},
        {"name": "Onion", "quantity": 1, "unit": "pcs"},
        {"name": "Garlic", "quantity": 3, "unit": "cloves"},
        {"name": "Ginger", "quantity": 15, "unit": "g"},
        {"name": "Vegetable Oil", "quantity": 30, "unit": "ml"}
      ],
      "instructions": [
        "Dice chicken into cubes.",
        "Sauté onions, garlic, and ginger in oil until fragrant.",
        "Add chicken and brown on all sides.",
        "Add curry powder and stir for 1 minute.",
        "Pour in coconut milk and simmer for 20 minutes."
      ],
      "cookware": ["pan", "knife", "cutting board"],
      "preparation_time": 15,
      "cooking_time": 25,
      "servings": 4,
      "calories_per_serving": 380,
      "rating": 4.5,
      "reviews_count": 87,
      "created_by": {
        "user_id": "user_456",
        "username": "chefmaster",
        "profile_picture": "https://mealflow.ddns.net/images/users/user_456.jpg"
      },
      "is_favorited": false,
      "is_saved": true,
      "notes": [],
      "interactions": {
        "views": 320,
        "likes": 150,
        "dislikes": 8,
        "shares": 25
      },
      "created_at": "2025-02-20T15:45:00Z",
      "updated_at": "2025-03-01T10:15:00Z"
    },
    {
      "meal_id": "meal_004",
      "name": "Cake",
      "description": "A flavorful Indian-inspired dish with tender chicken in a rich curry sauce.",
      "image_url": "https://mealflow.ddns.net/images/meal_002.jpg",
      "tags": ["curry", "indian", "chicken", "dinner"],
      "ingredients": [
        {"name": "Chicken Breast", "quantity": 500, "unit": "g"},
        {"name": "Curry Powder", "quantity": 20, "unit": "g"},
        {"name": "Coconut Milk", "quantity": 400, "unit": "ml"},
        {"name": "Onion", "quantity": 1, "unit": "pcs"},
        {"name": "Garlic", "quantity": 3, "unit": "cloves"},
        {"name": "Ginger", "quantity": 15, "unit": "g"},
        {"name": "Vegetable Oil", "quantity": 30, "unit": "ml"}
      ],
      "instructions": [
        "Dice chicken into cubes.",
        "Sauté onions, garlic, and ginger in oil until fragrant.",
        "Add chicken and brown on all sides.",
        "Add curry powder and stir for 1 minute.",
        "Pour in coconut milk and simmer for 20 minutes."
      ],
      "cookware": ["pan", "knife", "cutting board"],
      "preparation_time": 15,
      "cooking_time": 25,
      "servings": 4,
      "calories_per_serving": 380,
      "rating": 4.5,
      "reviews_count": 87,
      "created_by": {
        "user_id": "user_456",
        "username": "chefmaster",
        "profile_picture": "https://mealflow.ddns.net/images/users/user_456.jpg"
      },
      "is_favorited": false,
      "is_saved": true,
      "notes": [],
      "interactions": {
        "views": 320,
        "likes": 150,
        "dislikes": 8,
        "shares": 25
      },
      "created_at": "2025-02-20T15:45:00Z",
      "updated_at": "2025-03-01T10:15:00Z"
    },
    {
      "meal_id": "meal_001",
      "name": "Spaghetti محشي",
      "description": "A classic Italian pasta dish with eggs, cheese, pancetta, and pepper.",
      "image_url": "https://mealflow.ddns.net/images/meal_001.jpg",
      "tags": ["pasta", "italian", "quick", "dinner"],
      "ingredients": [
        {"name": "Pasta", "quantity": 200, "unit": "g"},
        {"name": "Egg", "quantity": 2, "unit": "pcs"},
        {"name": "Parmesan", "quantity": 50, "unit": "g"},
        {"name": "Bacon", "quantity": 100, "unit": "g"},
        {"name": "Black Pepper", "quantity": 5, "unit": "g"}
      ],
      "instructions": [
        "Boil pasta until al dente.",
        "Cook bacon in a pan until crispy.",
        "Whisk eggs and parmesan together.",
        "Drain pasta and mix everything together with black pepper."
      ],
      "cookware": ["pan", "pot"],
      "preparation_time": 20,
      "cooking_time": 10,
      "servings": 2,
      "calories_per_serving": 450,
      "rating": 4.7,
      "reviews_count": 125,
      "created_by": {
        "user_id": "user_123",
        "username": "foodlover",
        "profile_picture": "https://mealflow.ddns.net/images/users/user_123.jpg"
      },
      "is_favorited": true,
      "is_saved": false,
      "notes": [
        {
          "note_id": "note_567",
          "user": {
            "user_id": "user_456",
            "username": "chefmaster"
          },
          "comment": "This was great, but I added garlic and it was even better!",
          "image_url": "https://mealflow.ddns.net/images/notes/note_567.jpg",
          "tags": ["modified", "garlic"],
          "did_cook": true,
          "likes": 10,
          "dislikes": 2
        }
      ],
      "interactions": {
        "views": 500,
        "likes": 200,
        "dislikes": 5,
        "shares": 30
      },
      "created_at": "2025-03-05T12:00:00Z",
      "updated_at": "2025-03-05T14:30:00Z"
    },
    {
      "meal_id": "meal_002",
      "name": "Chicken Curry",
      "description": "A flavorful Indian-inspired dish with tender chicken in a rich curry sauce.",
      "image_url": "https://mealflow.ddns.net/images/meal_002.jpg",
      "tags": ["curry", "indian", "chicken", "dinner"],
      "ingredients": [
        {"name": "Chicken Breast", "quantity": 500, "unit": "g"},
        {"name": "Curry Powder", "quantity": 20, "unit": "g"},
        {"name": "Coconut Milk", "quantity": 400, "unit": "ml"},
        {"name": "Onion", "quantity": 1, "unit": "pcs"},
        {"name": "Garlic", "quantity": 3, "unit": "cloves"},
        {"name": "Ginger", "quantity": 15, "unit": "g"},
        {"name": "Vegetable Oil", "quantity": 30, "unit": "ml"}
      ],
      "instructions": [
        "Dice chicken into cubes.",
        "Sauté onions, garlic, and ginger in oil until fragrant.",
        "Add chicken and brown on all sides.",
        "Add curry powder and stir for 1 minute.",
        "Pour in coconut milk and simmer for 20 minutes."
      ],
      "cookware": ["pan", "knife", "cutting board"],
      "preparation_time": 15,
      "cooking_time": 25,
      "servings": 4,
      "calories_per_serving": 380,
      "rating": 4.5,
      "reviews_count": 87,
      "created_by": {
        "user_id": "user_456",
        "username": "chefmaster",
        "profile_picture": "https://mealflow.ddns.net/images/users/user_456.jpg"
      },
      "is_favorited": false,
      "is_saved": true,
      "notes": [],
      "interactions": {
        "views": 320,
        "likes": 150,
        "dislikes": 8,
        "shares": 25
      },
      "created_at": "2025-02-20T15:45:00Z",
      "updated_at": "2025-03-01T10:15:00Z"
    },
    {
      "meal_id": "meal_003",
      "name": "Pizza",
      "description": "A flavorful Indian-inspired dish with tender chicken in a rich curry sauce.",
      "image_url": "https://mealflow.ddns.net/images/meal_002.jpg",
      "tags": ["curry", "indian", "chicken", "dinner"],
      "ingredients": [
        {"name": "Chicken Breast", "quantity": 500, "unit": "g"},
        {"name": "Curry Powder", "quantity": 20, "unit": "g"},
        {"name": "Coconut Milk", "quantity": 400, "unit": "ml"},
        {"name": "Onion", "quantity": 1, "unit": "pcs"},
        {"name": "Garlic", "quantity": 3, "unit": "cloves"},
        {"name": "Ginger", "quantity": 15, "unit": "g"},
        {"name": "Vegetable Oil", "quantity": 30, "unit": "ml"}
      ],
      "instructions": [
        "Dice chicken into cubes.",
        "Sauté onions, garlic, and ginger in oil until fragrant.",
        "Add chicken and brown on all sides.",
        "Add curry powder and stir for 1 minute.",
        "Pour in coconut milk and simmer for 20 minutes."
      ],
      "cookware": ["pan", "knife", "cutting board"],
      "preparation_time": 15,
      "cooking_time": 25,
      "servings": 4,
      "calories_per_serving": 380,
      "rating": 4.5,
      "reviews_count": 87,
      "created_by": {
        "user_id": "user_456",
        "username": "chefmaster",
        "profile_picture": "https://mealflow.ddns.net/images/users/user_456.jpg"
      },
      "is_favorited": false,
      "is_saved": true,
      "notes": [],
      "interactions": {
        "views": 320,
        "likes": 150,
        "dislikes": 8,
        "shares": 25
      },
      "created_at": "2025-02-20T15:45:00Z",
      "updated_at": "2025-03-01T10:15:00Z"
    },
    {
      "meal_id": "meal_004",
      "name": "Cake",
      "description": "A flavorful Indian-inspired dish with tender chicken in a rich curry sauce.",
      "image_url": "https://mealflow.ddns.net/images/meal_002.jpg",
      "tags": ["curry", "indian", "chicken", "dinner"],
      "ingredients": [
        {"name": "Chicken Breast", "quantity": 500, "unit": "g"},
        {"name": "Curry Powder", "quantity": 20, "unit": "g"},
        {"name": "Coconut Milk", "quantity": 400, "unit": "ml"},
        {"name": "Onion", "quantity": 1, "unit": "pcs"},
        {"name": "Garlic", "quantity": 3, "unit": "cloves"},
        {"name": "Ginger", "quantity": 15, "unit": "g"},
        {"name": "Vegetable Oil", "quantity": 30, "unit": "ml"}
      ],
      "instructions": [
        "Dice chicken into cubes.",
        "Sauté onions, garlic, and ginger in oil until fragrant.",
        "Add chicken and brown on all sides.",
        "Add curry powder and stir for 1 minute.",
        "Pour in coconut milk and simmer for 20 minutes."
      ],
      "cookware": ["pan", "knife", "cutting board"],
      "preparation_time": 15,
      "cooking_time": 25,
      "servings": 4,
      "calories_per_serving": 380,
      "rating": 4.5,
      "reviews_count": 87,
      "created_by": {
        "user_id": "user_456",
        "username": "chefmaster",
        "profile_picture": "https://mealflow.ddns.net/images/users/user_456.jpg"
      },
      "is_favorited": false,
      "is_saved": true,
      "notes": [],
      "interactions": {
        "views": 320,
        "likes": 150,
        "dislikes": 8,
        "shares": 25
      },
      "created_at": "2025-02-20T15:45:00Z",
      "updated_at": "2025-03-01T10:15:00Z"
    }
  ];

  res.status(StatusCodes.OK).json({
    success: true,
    message: "Recommended meals retrieved successfully",
    count: recommendedMeals.length,
    data: recommendedMeals
  });
};

module.exports = {
  getRecommendedMeals
};