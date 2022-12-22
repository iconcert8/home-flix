package com.gyeom.homeflix;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class EtcTests {
    Logger log = LoggerFactory.getLogger(this.getClass().getSimpleName());

    @Test
    public void localFileList(){
        File dir = new File("./");
        Assert.notNull(dir.list(), "directory has list");

        dir = new File("./build.gradle");
        Assert.isNull(dir.list(), "file has no list");
    }

    @Test
    public void localFilePath(){
        File file = new File("./build.gradle");
        log.info(file.getName());
        log.info(file.getPath());

        file = new File("./src");
        log.info(file.getName());
        log.info(file.getPath());
    }

    @Test
    public void localFileType() {
//        File file = new File("./christmasTree.jpg");
//        File file = new File("./build.gradle");
        File file = new File("./testResources/video.mp4");
        String type = null;
        try {
            type = Files.probeContentType(file.toPath());
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        log.info(type);
    }
}
