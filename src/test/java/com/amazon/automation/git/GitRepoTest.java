package com.amazon.automation.git;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.nio.file.Files;
import java.nio.file.Path;

public class GitRepoTest {

    @Test
    public void gitDirectoryShouldExist() {
        Path gitDir = Path.of(System.getProperty("user.dir")).resolve(".git");
        Assert.assertTrue(Files.exists(gitDir), ".git directory should exist in project root");
    }
}

