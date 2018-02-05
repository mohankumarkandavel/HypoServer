package org.hypothesis.server;

import org.fejoa.storage.Hash;
import org.fejoa.storage.RandomDataAccess;
import org.fejoa.storage.StorageBackend;

import java.util.HashMap;

public class TestRepository {
    void testRepositoryBasics() {
       String dirName = "testRepositoryBasicsDir";
       String branchName = "basicBranch";

            suspendFun bindings = suspendFun.Companion.create();
            RepoWrapper repoWrapper=bindings.createRepository(dirName,branchName);
            StorageBackend.BranchBackend storage= repoWrapper.getRepository().getBranchBackend();
            HashMap<String,DatabaseStringEntry> content=new HashMap<String,DatabaseStringEntry>();
            bindings.add(repoWrapper,content,new DatabaseStringEntry("file1","file1"));
            bindings.add(repoWrapper, content, new DatabaseStringEntry("dir1/file2", "file2"));
            bindings.add(repoWrapper, content, new DatabaseStringEntry("dir1/file3", "file3"));
            bindings.add(repoWrapper, content, new DatabaseStringEntry("dir2/file4", "file4"));
            bindings.add(repoWrapper, content, new DatabaseStringEntry("dir1/sub1/file5", "file5"));
            bindings.add(repoWrapper, content, new DatabaseStringEntry("dir1/sub1/sub2/file6", "file6"));

            repoWrapper.commit(new byte[0],bindings.getSimpleCommitSignature());
            bindings.containsContent(repoWrapper,content);
            Hash tip =repoWrapper.getHead();
            System.out.println(tip);
            try {
                System.out.println(repoWrapper.getHeadCommit().getHash());
            }
            catch (Exception e){
                System.out.println(e.getMessage());
            }

            repoWrapper = repoWrapper.open(branchName, repoWrapper.getRepositoryRef(), storage, bindings.getSymCredentials());
            bindings.containsContent(repoWrapper, content);

            // test add to existing dir
            bindings.add(repoWrapper, content, new DatabaseStringEntry("dir1/file6", "file6"));
            repoWrapper.commit(new byte[0],bindings.getSimpleCommitSignature());
            repoWrapper = repoWrapper.open(branchName, repoWrapper.getRepositoryRef(), storage, bindings.getSymCredentials());
            bindings.containsContent(repoWrapper, content);

            // test update
            bindings.add(repoWrapper, content, new DatabaseStringEntry("dir1/file3", "file3Update"));
            bindings.add(repoWrapper, content, new DatabaseStringEntry("dir1/sub1/file5", "file5Update"));
            bindings.add(repoWrapper, content, new DatabaseStringEntry("dir1/sub1/sub2/file6", "file6Update"));
            repoWrapper.commit(new byte[0],bindings.getSimpleCommitSignature());
            repoWrapper = repoWrapper.open(branchName, repoWrapper.getRepositoryRef(), storage, bindings.getSymCredentials());
            bindings.containsContent(repoWrapper, content);

            // test remove
            bindings.remove(repoWrapper.getRepository(), content, "dir1/sub1/file5");
            repoWrapper.commit(new byte[0],bindings.getSimpleCommitSignature());
            repoWrapper = repoWrapper.open(branchName, repoWrapper.getRepositoryRef(), storage, bindings.getSymCredentials());
            bindings.containsContent(repoWrapper, content);

            System.out.println( repoWrapper.listFiles("notThere").size());
            System.out.println( repoWrapper.listDirectories("notThere").size());
            System.out.println( repoWrapper.listFiles("file1").size());
            System.out.println( repoWrapper.listDirectories("file1").size());
            System.out.println( repoWrapper.listDirectories("dir1/file2").size());



    }
    void testRepositoryOpenMode() {
        String dirName = "RepoOpenModeTest";
         String branchName = "repoBranch";


            suspendFun bindings = suspendFun.Companion.create();
            //bindings.storageOpen(dirName, branchName);
            RepoWrapper repoWrapper=bindings.createRepository(dirName,branchName);
            RandomDataAccessWrapper randomDataAccessWrapper;
            try {
                // randomDataAccessWrapper=repoWrapper.open("dir/test",RandomDataAccess.Mode.READ);

            }catch (Exception e){
                System.out.println(e.toString());

            }
            randomDataAccessWrapper=repoWrapper.open("test", RandomDataAccess.Mode.WRITE);
            randomDataAccessWrapper.write("Hello World".getBytes());
            randomDataAccessWrapper.close();
            byte[] data = repoWrapper.readBytes("test");
            System.out.println(new String(data));

            randomDataAccessWrapper=repoWrapper.open("test",RandomDataAccess.Mode.APPEND);
            randomDataAccessWrapper.write("!".getBytes());
            randomDataAccessWrapper.close();
            data = repoWrapper.readBytes("test");
            System.out.println(new String(data));

            randomDataAccessWrapper=repoWrapper.open("test",RandomDataAccess.Mode.WRITE);
            randomDataAccessWrapper.seek(6);
            randomDataAccessWrapper.write("there".getBytes());
            randomDataAccessWrapper.close();
            data = repoWrapper.readBytes("test");
            System.out.println(new String(data));

            randomDataAccessWrapper=repoWrapper.open("test",RandomDataAccess.Mode.INSERT);
            randomDataAccessWrapper.seek(5);
            randomDataAccessWrapper.write(" you".getBytes());
            randomDataAccessWrapper.close();
            data = repoWrapper.readBytes("test");
            System.out.println(new String(data));

            randomDataAccessWrapper=repoWrapper.open("test",RandomDataAccess.Mode.TRUNCATE);
            randomDataAccessWrapper.write("New string!".getBytes());
            randomDataAccessWrapper.close();
            data = repoWrapper.readBytes("test");
            System.out.println(new String(data));

            randomDataAccessWrapper=repoWrapper.open("test",RandomDataAccess.Mode.WRITE);
            randomDataAccessWrapper.delete(4,2);
            randomDataAccessWrapper.close();
            data = repoWrapper.readBytes("test");
            System.out.println(new String(data));

            //try to write in read mode
            randomDataAccessWrapper=repoWrapper.open("test",RandomDataAccess.Mode.READ);
            Boolean failed=false;
            try {
                //randomDataAccessWrapper.write("Hello World".getBytes());
            } catch (Exception e){
                failed = true;
            }

            randomDataAccessWrapper.close();
            System.out.println(failed);

            System.out.println("prepare");


    }

}
