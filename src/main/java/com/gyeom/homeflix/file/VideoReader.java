package com.gyeom.homeflix.file;

import java.util.List;

public interface VideoReader {
    List<FileDTO> list(String... path);
    boolean exists(String[] path);
    boolean isVideo(String[] path);
    String toSourcePath(String[] path);
}
