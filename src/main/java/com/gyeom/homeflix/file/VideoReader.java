package com.gyeom.homeflix.file;

import java.util.List;

public interface VideoReader {
    List<FileDTO> list(String... path);
}
