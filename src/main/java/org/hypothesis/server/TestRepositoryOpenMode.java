package org.hypothesis.server;

import org.fejoa.crypto.CryptoSettings;
import org.fejoa.repository.Repository;
import org.fejoa.storage.RandomDataAccess;
import org.fejoa.storage.StorageBackend;
import org.fejoa.support.IOException;




public class TestRepositoryOpenMode {
    public static final String dirName = "RepoOpenModeTest";
    public  static final String branchName = "repoBranch";

    public static void main(String[] args) {
        suspendFun bindings = suspendFun.Companion.create();
        //bindings.storageOpen(dirName, branchName);
        RepoWrapper repoWrapper=bindings.createRepository(dirName,branchName);
        RandomDataAccessWrapper randomDataAccessWrapper;
        try {
           // randomDataAccessWrapper=repoWrapper.open("dir/test",RandomDataAccess.Mode.READ);

        }catch (Exception e){
            System.out.println(e.toString());

        }
        randomDataAccessWrapper=repoWrapper.open("test",RandomDataAccess.Mode.WRITE);
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
