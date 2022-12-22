package com.gyeom.homeflix.file;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

@Service
public class LocalVideoReader implements VideoReader {
    private final Logger log = LoggerFactory.getLogger(this.getClass().getSimpleName());
    public static final Set<String> VIDEO_MIME_TYPE_SET = new HashSet<>(){{
        add("video/mp4"); //.mp4
//        add("video/x-msvideo"); add("video/avi"); //.avi, Web does not support.
        add("video/x-matroska"); //.mkv
    }};

    @Override
    public List<FileDTO> list(String... path) {
        File dir = new File(toSourcePath(path));
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

    @Override
    public boolean exists(String[] path) {
        File file = new File(toSourcePath(path));
        return file.exists();
    }

    @Override
    public boolean isVideo(String[] path) {
        File file = new File(toSourcePath(path));
        return isVideo(file);
    }

    @Override
    public String toSourcePath(String[] path) {
        StringBuilder sb = new StringBuilder(".");
        for(String p : path) sb.append("/").append(p);
        return sb.toString();
    }

    @Nullable
    private FileDTO toFileDTO(File file, String[] path){
        FileDTO dto = null;
        String[] dtoPath = new String[path.length+1];
        System.arraycopy(path, 0, dtoPath, 0, path.length);
        dtoPath[path.length] = file.getName();

        if(file.isFile()){
            if(!isVideo(file)) return null;
            dto = new FileDTO(dtoPath, file.getName(), FileType.VIDEO);
        }

        if(file.isDirectory()){
            dto = new FileDTO(dtoPath, file.getName(), FileType.FOLDER);
        }

        return dto;
    }

    private boolean isVideo(File file){
        try {
            String type = Files.probeContentType(file.toPath());
            if(type == null) return false;
            if(VIDEO_MIME_TYPE_SET.contains(type)){
                return true;
            }
        } catch (IOException e) {
            log.error("Error at Files.probeContentType() method.");
            log.error(e.getMessage());
        }
        return false;
    }

}
