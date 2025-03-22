/*
  Warnings:

  - You are about to drop the column `slug` on the `Community` table. All the data in the column will be lost.

*/
-- DropIndex
DROP INDEX "Community_slug_idx";

-- DropIndex
DROP INDEX "Community_slug_key";

-- AlterTable
ALTER TABLE "Community" DROP COLUMN "slug";
