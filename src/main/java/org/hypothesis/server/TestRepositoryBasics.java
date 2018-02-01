package org.hypothesis.server;

import org.fejoa.repository.CommitSignature;
import org.fejoa.storage.Hash;

import javax.swing.text.AbstractDocument;
import java.io.ByteArrayInputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

public class TestRepositoryBasics {
    public static final String dirName = "testRepositoryBasicsDir";
    public  static final String branchName = "basicBranch";

    public static void main(String[] args) {
        suspendFun bindings = suspendFun.Companion.create();
        RepoWrapper repoWrapper=bindings.createRepository(dirName,branchName);
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



    }
}
