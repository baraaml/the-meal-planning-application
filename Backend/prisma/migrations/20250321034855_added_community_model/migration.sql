/*
  Warnings:

  - You are about to drop the column `logo` on the `Community` table. All the data in the column will be lost.

*/
-- CreateEnum
CREATE TYPE "RecipeCreationPermission" AS ENUM ('ANY_MEMBER', 'ADMIN_ONLY');

-- AlterTable
ALTER TABLE "Community" DROP COLUMN "logo",
ADD COLUMN     "recipeCreationPermission" "RecipeCreationPermission" NOT NULL DEFAULT 'ANY_MEMBER';

-- CreateTable
CREATE TABLE "Category" (
    "id" TEXT NOT NULL,
    "name" VARCHAR(100) NOT NULL,
    "emoji" VARCHAR(10),
    "parentId" TEXT,

    CONSTRAINT "Category_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "CommunityCategory" (
    "communityId" TEXT NOT NULL,
    "categoryId" TEXT NOT NULL,

    CONSTRAINT "CommunityCategory_pkey" PRIMARY KEY ("communityId","categoryId")
);

-- AddForeignKey
ALTER TABLE "Category" ADD CONSTRAINT "Category_parentId_fkey" FOREIGN KEY ("parentId") REFERENCES "Category"("id") ON DELETE SET NULL ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "CommunityCategory" ADD CONSTRAINT "CommunityCategory_communityId_fkey" FOREIGN KEY ("communityId") REFERENCES "Community"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "CommunityCategory" ADD CONSTRAINT "CommunityCategory_categoryId_fkey" FOREIGN KEY ("categoryId") REFERENCES "Category"("id") ON DELETE RESTRICT ON UPDATE CASCADE;
