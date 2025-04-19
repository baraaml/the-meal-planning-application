/*
  Warnings:

  - The primary key for the `_PostToPostFlair` table will be changed. If it partially fails, the table could be left without primary key constraint.
  - A unique constraint covering the columns `[A,B]` on the table `_PostToPostFlair` will be added. If there are existing duplicate values, this will fail.

*/
-- AlterTable
ALTER TABLE "_PostToPostFlair" DROP CONSTRAINT "_PostToPostFlair_AB_pkey";

-- CreateIndex
CREATE UNIQUE INDEX "_PostToPostFlair_AB_unique" ON "_PostToPostFlair"("A", "B");
