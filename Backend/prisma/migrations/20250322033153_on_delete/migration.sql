-- DropForeignKey
ALTER TABLE "CommunityCategory" DROP CONSTRAINT "CommunityCategory_categoryId_fkey";

-- DropForeignKey
ALTER TABLE "CommunityCategory" DROP CONSTRAINT "CommunityCategory_communityId_fkey";

-- AddForeignKey
ALTER TABLE "CommunityCategory" ADD CONSTRAINT "CommunityCategory_communityId_fkey" FOREIGN KEY ("communityId") REFERENCES "Community"("id") ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "CommunityCategory" ADD CONSTRAINT "CommunityCategory_categoryId_fkey" FOREIGN KEY ("categoryId") REFERENCES "Category"("id") ON DELETE CASCADE ON UPDATE CASCADE;
