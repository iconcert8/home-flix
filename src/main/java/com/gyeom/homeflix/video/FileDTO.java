package com.gyeom.homeflix.video;

import java.util.Arrays;

public class FileDTO {
    private String[] path;
    private String name;
    private FileType type;

    public String[] getPath() {
        return path;
    }

    public String getName() {
        return name;
    }

    public FileType getType() {
        return type;
    }

    public FileDTO(String[] path, String name, FileType type) {
        this.path = path;
        this.name = name;
        this.type = type;
    }

    @Override
    public String toString() {
        return "FileDTO{" +
                "path=" + Arrays.toString(path) +
                ", name='" + name + '\'' +
                ", type=" + type +
                '}';
    }
}
