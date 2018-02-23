package org.hypothesis.server;

import org.fejoa.repository.Commit;
import org.fejoa.repository.Repository;
import org.fejoa.storage.Hash;
import org.fejoa.storage.StorageBackend;

import java.util.HashMap;

public class RepositoryHypoTest {
    private String dirName = "testRepositoryBasicsDir1";
    private String branchName = "basicBranch1";
    void newRepo(byte[] bytes) {
        suspendFun bindings = suspendFun.Companion.create();
        RepoWrapper repoWrapper = bindings.createRepository(dirName, branchName);
        StorageBackend.BranchBackend storage = repoWrapper.getRepository().getBranchBackend();
        repoWrapper.putBytes("test",bytes);
        System.out.println(new String(repoWrapper.readBytes("test")));

        /**HashMap<String, DatabaseStringEntry> content = new HashMap<String, DatabaseStringEntry>();
        bindings.add(repoWrapper, content, new DatabaseStringEntry("file1", "file1"));
        bindings.add(repoWrapper, content, new DatabaseStringEntry("dir1/file2", "file2"));
        bindings.add(repoWrapper, content, new DatabaseStringEntry("dir1/file3", "file3"));
        bindings.add(repoWrapper, content, new DatabaseStringEntry("dir2/file4", "file4"));
        bindings.add(repoWrapper, content, new DatabaseStringEntry("dir1/sub1/file5", "file5"));
        bindings.add(repoWrapper, content, new DatabaseStringEntry("dir1/sub1/sub2/file6", "file6"));**/

    }
    RepoWrapper newRepository() {
        String dirName = "testRepositoryBasicsDir2";
        String branchName = "basicBranch2";
        suspendFun bindings = suspendFun.Companion.create();
        RepoWrapper repoWrapper = bindings.createRepository(dirName, branchName);
        StorageBackend.BranchBackend storage = repoWrapper.getRepository().getBranchBackend();
        //repoWrapper.putBytes("test", bytes);
        //System.out.println(new String(repoWrapper.readBytes("test")));
        return repoWrapper;
    }
    Hash commitHash(RepoWrapper repository,byte[] message,byte[] bytes){
        suspendFun bindings = suspendFun.Companion.create();
        repository.putBytes("test", bytes);
        return repository.commit(message,bindings.getSimpleCommitSignature());
    }

}
