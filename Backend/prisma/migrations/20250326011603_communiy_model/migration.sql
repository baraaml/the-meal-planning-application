/*
  Warnings:

  - The primary key for the `CommunityMember` table will be changed. If it partially fails, the table could be left without primary key constraint.
  - You are about to drop the column `id` on the `CommunityMember` table. All the data in the column will be lost.

*/
-- DropIndex
DROP INDEX "CommunityMember_communityId_userId_key";

-- AlterTable
ALTER TABLE "CommunityMember" DROP CONSTRAINT "CommunityMember_pkey",
DROP COLUMN "id",
ADD CONSTRAINT "CommunityMember_pkey" PRIMARY KEY ("communityId", "userId");
