package org.hypothesis.server;

public class RepositoryTest {
    public static void main(String[] args) {
        TestRepository testRepository=new TestRepository();
        testRepository.testRepositoryBasics();
        testRepository.testRepositoryOpenMode();
    }
}
