const { PrismaClient } = require("@prisma/client");

const prisma = new PrismaClient();

const categoriesData = [
  {
    name: "Cuisine Types",
    children: [
      "Italian Cuisine",
      "French Cuisine",
      "Mexican Cuisine",
      "Middle Eastern Cuisine",
      "Asian Cuisine",
      "American Cuisine",
      "Mediterranean Cuisine",
      "Indian Cuisine",
      "African Cuisine"
    ]
  },
  {
    name: "Dietary Preferences",
    children: [
      "Vegan & Plant-Based",
      "Vegetarian",
      "Keto & Low-Carb",
      "Paleo",
      "Gluten-Free",
      "Dairy-Free",
      "High-Protein",
      "Halal & Kosher"
    ]
  },
  {
    name: "Meal Types",
    children: [
      "Breakfast & Brunch",
      "Lunch & Quick Meals",
      "Dinner & Main Courses",
      "Snacks & Appetizers",
      "Desserts & Sweets"
    ]
  },
  {
    name: "Cooking Styles & Techniques",
    children: [
      "Home Cooking",
      "Baking & Pastry",
      "Grilling & BBQ",
      "Slow Cooking & Crockpot",
      "Air Fryer Recipes",
      "One-Pot & Instant Pot Meals",
      "Fermentation & Pickling"
    ]
  },
  {
    name: "Special Occasions & Seasonal Meals",
    children: [
      "Holiday & Festive Meals",
      "Ramadan & Iftar",
      "Summer Refreshing Meals",
      "Winter Comfort Foods",
      "Birthday & Celebration Cakes"
    ]
  },
  {
    name: "Health & Wellness",
    children: [
      "Weight Loss Recipes",
      "Muscle Gain & Fitness Meals",
      "Diabetic-Friendly Meals",
      "Heart-Healthy Recipes"
    ]
  },
  {
    name: "Budget & Time-Friendly Meals",
    children: [
      "5-Ingredient Recipes",
      "30-Minute Meals",
      "Cheap & Budget-Friendly Meals",
      "Meal Prepping & Batch Cooking"
    ]
  },
  {
    name: "Cultural & Regional Foods",
    children: [
      "Street Food",
      "Traditional & Ancestral Recipes",
      "Fusion Recipes"
    ]
  },
  {
    name: "Drinks & Beverages",
    children: [
      "Coffee & Tea Lovers",
      "Smoothies & Juices",
      "Cocktails & Mocktails",
      "Homemade Brews & Fermented Drinks",
      "Detox & Wellness Drinks"
    ]
  },
  {
    name: "Kids & Family Meals",
    children: [
      "Kid-Friendly Recipes",
      "School Lunch Ideas",
      "Family Dinners",
      "Fun & Creative Snacks",
      "Baby Food & Toddler Meals"
    ]
  },
  {
    name: "Sustainability & Zero Waste",
    children: [
      "Zero-Waste Cooking",
      "Seasonal & Local Produce",
      "Nose-to-Tail Cooking",
      "Root-to-Stem Recipes",
      "Sustainable Seafood"
    ]
  },
  {
    name: "Global Street Food",
    children: [
      "Asian Street Food",
      "Latin American Street Food",
      "European Street Food",
      "African Street Food",
      "Middle Eastern Street Food"
    ]
  },
  {
    name: "Food Allergies & Intolerances",
    children: [
      "Nut-Free Recipes",
      "Egg-Free Recipes",
      "Soy-Free Recipes",
      "Shellfish-Free Recipes",
      "Low-FODMAP Recipes"
    ]
  },
  {
    name: "Cooking for One or Two",
    children: [
      "Single-Serving Recipes",
      "Date Night Meals",
      "Small-Batch Baking",
      "Minimalist Cooking"
    ]
  },
  {
    name: "Food Science & Experimentation",
    children: [
      "Molecular Gastronomy",
      "Sous Vide Cooking",
      "Food Pairing & Flavor Science",
      "DIY Kitchen Experiments"
    ]
  },
  {
    name: "Food Challenges & Trends",
    children: [
      "Viral Food Trends",
      "Spicy Food Challenges",
      "Retro & Nostalgic Recipes",
      "Food Art & Aesthetics",
      "Extreme Food Challenges"
    ]
  },
  {
    name: "Food History & Culture",
    children: [
      "Ancient Recipes",
      "Historical Dishes",
      "Indigenous Cuisine",
      "Culinary Traditions",
      "Food Anthropology"
    ]
  },
  {
    name: "Food Photography & Styling",
    children: [
      "Food Styling Tips",
      "Flat Lay Photography",
      "Recipe Video Creation",
      "Food Blogging & Content Creation"
    ]
  },
  {
    name: "Food Pairing & Wine",
    children: [
      "Wine & Cheese Pairings",
      "Beer & Food Pairings",
      "Non-Alcoholic Pairings",
      "Dessert & Drink Combos"
    ]
  },
  {
    name: "Food Hacks & Tips",
    children: [
      "Kitchen Hacks",
      "Ingredient Substitutions",
      "Time-Saving Tips",
      "Leftover Makeovers",
      "Meal Planning Strategies"
    ]
  },
  {
    name: "Food Travel & Exploration",
    children: [
      "Foodie Travel Guides",
      "Regional Food Adventures",
      "Food Tours & Experiences",
      "International Grocery Hauls"
    ]
  },
  {
    name: "Food for Mental Health",
    children: [
      "Mood-Boosting Recipes",
      "Stress-Relief Foods",
      "Brain-Boosting Meals",
      "Comfort Foods for Tough Days"
    ]
  },
  {
    name: "Food for Athletes",
    children: [
      "Pre-Workout Meals",
      "Post-Workout Recovery",
      "Endurance Fueling",
      "Hydration & Electrolytes"
    ]
  },
  {
    name: "Food for Pets",
    children: [
      "Homemade Dog Treats",
      "Cat-Friendly Recipes",
      "Healthy Pet Meals",
      "DIY Pet Food"
    ]
  },
  {
    name: "Food for Special Diets",
    children: [
      "Low-Sodium Recipes",
      "Low-Sugar Recipes",
      "Anti-Inflammatory Meals",
      "Autoimmune Protocol (AIP)"
    ]
  }
];

async function seedCategories() {
  try {
    console.log('Starting category seeding...');

    // Clear existing data (optional)
    await prisma.communityCategory.deleteMany();
    await prisma.category.deleteMany();

    // Create parent categories and their children
    for (const parentCategory of categoriesData) {
      // Create parent category
      const parent = await prisma.category.create({
        data: {
          name: parentCategory.name,
          parentId: null,
        },
      });

      // Prepare children data
      const childrenData = parentCategory.children.map(childName => ({
        name: childName,
        parentId: parent.id,
      }));

      // Batch create children
      await prisma.category.createMany({
        data: childrenData,
      });

      console.log(`Created ${parentCategory.name} with ${childrenData.length} subcategories`);
    }

    console.log('Successfully seeded all categories!');
  } catch (error) {
    console.error('Error seeding categories:', error);
    process.exit(1);
  } finally {
    await prisma.$disconnect();
  }
}

seedCategories();