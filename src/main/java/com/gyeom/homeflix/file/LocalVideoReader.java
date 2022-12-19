package com.gyeom.homeflix.file;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Service
public class LocalVideoReader implements VideoReader {
    private final Logger log = LoggerFactory.getLogger(this.getClass().getSimpleName());
    public static final String VIDEO_MIME_TYPE = "video/mp4";

    @Override
    public List<FileDTO> list(String... path) {
        StringBuilder sb = new StringBuilder(".");
        for(String p : path) sb.append("/").append(p);
        File dir = new File(sb.toString());
        if(dir.isFile()) return new ArrayList<>();

        LinkedList<FileDTO> result = new LinkedList<>();
        File[] files = dir.listFiles();
        if(files == null) files = new File[0];
        for(File file : files){
            FileDTO dto = toFileDTO(file, path);
            if(dto != null){
                if(dto.getType() == FileType.FOLDER) result.addFirst(dto);
                else result.addLast(dto);
            }
        }

        return result;
    }

    @Nullable
    private FileDTO toFileDTO(File file, String[] path){
        FileDTO dto = null;
        String[] dtoPath = new String[path.length+1];
        System.arraycopy(path, 0, dtoPath, 0, path.length);
        dtoPath[path.length] = file.getName();

        if(file.isFile()){
            try {
                String type = Files.probeContentType(file.toPath());
                if(type == null) return null;
                if(type.equals(VIDEO_MIME_TYPE)){
                    dto = new FileDTO(dtoPath, file.getName(), FileType.VIDEO);
                }
            } catch (IOException e) {
                log.error("Files.probeContentType() method is error.");
                log.error(e.getMessage());
            }
        }

        if(file.isDirectory()){
            dto = new FileDTO(dtoPath, file.getName(), FileType.FOLDER);
        }

        return dto;
    }
}
