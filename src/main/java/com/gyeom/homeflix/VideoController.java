package com.gyeom.homeflix;

import com.gyeom.homeflix.video.FileDTO;
import com.gyeom.homeflix.video.VideoReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.FileUrlResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Controller
public class VideoController {

    Logger log = LoggerFactory.getLogger(this.getClass().getSimpleName());
    public static final String PATH_DELIMITER = "&&";

    final VideoReader videoReader;

    public VideoController(@Qualifier(value = "localVideoReader") VideoReader videoReader){
        this.videoReader = videoReader;
    }

    /**
     * Video and Directory list in {path}.
     *
     * @param path ex)"P1&&P2&&P3"
     *             "&&" is subpath like "/".
     */
    @GetMapping(value = {"/videos", "/videos/{path}"})
    public ResponseEntity<List<FileDTO>> list(@PathVariable(value = "path") Optional<String> path){
        try {
            if(path.isEmpty()) return ResponseEntity.ok().body(videoReader.list());
            return ResponseEntity.ok().body(videoReader.list(path.get().split(PATH_DELIMITER)));
        }catch (RuntimeException e){
            return ResponseEntity.internalServerError().body(Collections.emptyList());
        }
    }

    /**
     * Response Video stream in {path, reqHeaders}.
     *
     * @param reqHeaders contains range of the file, etc.
     * @param path ex)"P1&&P2&&P3"
     *             "&&" is subpath character like "/".
     */
    @GetMapping(value = {"/video/{path}"}, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<ResourceRegion> stream(@RequestHeader HttpHeaders reqHeaders, @PathVariable(value = "path") String path) throws IOException {
        String[] ps = path.split(PATH_DELIMITER);
        if(!videoReader.exists(ps)) {log.error("Not exists file '{}'.", Arrays.toString(ps)); return ResponseEntity.notFound().build();}
        if(!videoReader.isVideo(ps)) {log.error("Not video '{}'.", Arrays.toString(ps)); return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).build();}

        FileUrlResource resource = new FileUrlResource(videoReader.toSourcePath(ps));
        ResourceRegion resourceRegion = resourceRegion(resource, reqHeaders);
        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                .contentType(MediaTypeFactory.getMediaType(resource).orElse(MediaType.APPLICATION_OCTET_STREAM))
                .body(resourceRegion);
    }

    /**
     * Response video subtitle file. only VTT file.
     *
     * @param path like "path1&&video.mp4".
     *             Find same name in "path/video.vtt".
     */
    @GetMapping(value = {"/video/subtitle/{path}"})
    public ResponseEntity<InputStreamResource> subtitle(@PathVariable("path") String path){
        String[] splitPath = path.split(VideoController.PATH_DELIMITER);
        String filename = splitPath[splitPath.length-1];
        if(filename.lastIndexOf('.') == -1) return ResponseEntity.notFound().build();

        splitPath[splitPath.length - 1] = filename.substring(0, filename.lastIndexOf('.')) + ".vtt";;

        File file = new File(videoReader.toSourcePath(splitPath));
        if(!file.exists()) return ResponseEntity.notFound().build();
        try {
            return ResponseEntity.ok().contentType(MediaType.TEXT_PLAIN).body(new InputStreamResource(new FileInputStream(file)));
        } catch (FileNotFoundException e) {
            return ResponseEntity.internalServerError().build();
        }
    }


    private ResourceRegion resourceRegion(Resource video, HttpHeaders httpHeaders) throws IOException {
        final long chunkSize = 1000000L;
        long contentLength = video.contentLength();

        if (httpHeaders.getRange().isEmpty()) {
            return new ResourceRegion(video, 0, Long.min(chunkSize, contentLength));
        }

        //noinspection OptionalGetWithoutIsPresent
        HttpRange httpRange = httpHeaders.getRange().stream().findFirst().get();
        long start = httpRange.getRangeStart(contentLength);
        long end = httpRange.getRangeEnd(contentLength);
        long rangeLength = Long.min(chunkSize, end - start + 1);
        return new ResourceRegion(video, start, rangeLength);
    }
}
