/**
 * @fileoverview Community repository for handling database queries
 * @module repositories/community
 */

const prisma = require("../config/prismaClient");

/**
 * Repository layer for community-related database operations
 */
class CommunityRepository {
  /**
   * Find a community by name
   * @param {string} name - Community name
   * @returns {Promise<Object|null>} Found community or null
   */
  async findByName(name) {
    return prisma.community.findFirst({
      where: { name },
    });
  }

  /**
   * Find categories by names
   * @param {string[]} categoryNames - Array of category names
   * @returns {Promise<Array>} Found categories
   */
  async findCategoriesByNames(categoryNames) {
    return prisma.category.findMany({
      where: { name: { in: categoryNames } },
      select: { id: true, name: true },
    });
  }

  /**
   * Create a new community with all relations
   * @param {Object} communityData - Community data with relations
   * @returns {Promise<Object>} Created community
   */
  async create(communityData) {
    return prisma.community.create({
      data: communityData,
      include: {
        categories: {
          include: {
            category: true,
          },
        },
        members: {
          select: {
            role: true,
            joinedAt: true,
            isPending: true,
            user: {
              select: {
                id: true,
                name: true,
                username: true,
              },
            },
          },
        },
        owner: {
          select: {
            id: true,
            name: true,
            username: true,
          },
        },
      },
    });
  }

  /**
   * Find a community by ID with all relations
   * @param {string} id - Community ID
   * @returns {Promise<Object|null>} Found community or null
   */
  async findById(id) {
    return prisma.community.findUnique({
      where: { id },
      select: {
        id: true,
        name: true,
        description: true,
        image: true,
        privacy: true,
        recipeCreationPermission: true,
        createdAt: true,
        updatedAt: true,
        categories: {
          select: {
            category: {
              select: {
                id: true,
                name: true,
              },
            },
          },
        },
        members: {
          select: {
            role: true,
            joinedAt: true,
            isPending: true,
            user: {
              select: {
                id: true,
                name: true,
                username: true,
              },
            },
          },
        },
        owner: {
          select: {
            id: true,
            name: true,
            username: true,
          },
        },
      },
    });
  }

  /**
   * Find all communities with relations
   * @returns {Promise<Array>} List of communities
   */
  async findAll() {
    return prisma.community.findMany({
      select: {
        id: true,
        name: true,
        description: true,
        image: true,
        privacy: true,
        recipeCreationPermission: true,
        createdAt: true,
        updatedAt: true,
        categories: {
          select: {
            category: {
              select: {
                id: true,
                name: true,
              },
            },
          },
        },
        members: {
          select: {
            role: true,
            joinedAt: true,
            user: {
              select: {
                id: true,
                name: true,
                username: true,
              },
            },
          },
        },
        owner: {
          select: {
            id: true,
            name: true,
            username: true,
          },
        },
      },
    });
  }

  /**
   * Check if a user is a member of a community
   * @param {string} communityId - The ID of the community
   * @param {string} userId - The ID of the user
   */
  async isMember(communityId, userId) {
    const membership = await prisma.communityMember.findFirst({
      where: {
        communityId: communityId,
        userId: userId,
      },
      include: {
        user: true,
      },
    });
    return membership;
  }

  /**
   * Gets all the members of a specific community by ID
   * @param {string} communityId - The ID of the community
   * @returns {Promise<Array>} - An array of community members
   */
  async getAllMembers(communityId) {
    const members = await prisma.communityMember.findMany({
      where: { communityId },
    });
    return members;
  }

  /**
   * Add a user to community
   * @param {string} communityId - The ID of the community
   * @param {string} userId - The ID of the user
   * @param {string} role - The role of the user(Admin, Member)
   * @returns {Promise<object>}
   */
  async addMember(communityId, userId, role) {
    return prisma.communityMember.create({
      data: {
        communityId: communityId,
        userId: userId,
        role: role,
      },
    });
  }

  /**
   * Gets all the admins for a community
   * @param {string} communityId  - The ID of the community
   * @returns {Promise<Array>} - An array of community admins
   */
  async getAdmins(communityId) {
    const admins = await prisma.communityMember.findMany({
      where: {
        communityId: communityId,
        role: "ADMIN",
      },
      include: {
        user: true,
      },
    });
    return admins;
  }

  /**
   * Removes an admin from a community
   * @param {string} communityId  - The ID of the community
   * @param {string} userId - The ID of the user
   * @returns {Promise<Array>} - An array of community admins
   */
  async removeAdmin(communityId, userId) {
    const removedAdmin = await prisma.communityMember.delete({
      where: {
        communityId: communityId,
        userId: userId,
        role: "ADMIN",
      },
      include: {
        user: true,
      },
    });
    return removedAdmin;
  }

  /**
   * Makes members admins
   * @param {string} communityId  - The ID of the community
   * @param {string[]} memberIDs - An array of user IDs to promote
   * @returns {Promise<Array>} - An array of community admins
   */
  async makeAdmins(communityId, memberIDs) {
    const newAdmins = await prisma.communityMember.updateMany({
      where: {
        communityId: communityId,
        userId: { in: memberIDs },
        role: "MEMBER",
      },
      data: {
        role: "ADMIN",
      },
    });
    return newAdmins;
  }

  async getMembersSortedByJoinDate(communityId) {
    return prisma.communityMember.findMany({
      where: { communityId },
      orderBy: { joinedAt: "asc" }, // Sort by join date (earliest first)
    });
  }
}

module.exports = new CommunityRepository();
