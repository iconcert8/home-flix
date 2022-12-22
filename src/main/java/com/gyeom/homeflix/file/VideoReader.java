package com.gyeom.homeflix.file;

import java.util.List;

public interface VideoReader {
    List<FileDTO> list(String... path);
    boolean exists(String[] path);
    boolean isVideo(String[] path);

    /**
     * Make to origin source path.
     */
    String toSourcePath(String[] path);
}
