package com.gyeom.homeflix.file;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LocalVideoReaderTests {

    Logger log = LoggerFactory.getLogger(this.getClass());

    @Test
    public void list(){
        VideoReader l = new LocalVideoReader();
        log.info(l.list().toString());
        log.info(l.list("testResources").toString());
    }
}
