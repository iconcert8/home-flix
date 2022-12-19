package com.gyeom.homeflix;

import com.gyeom.homeflix.file.FileDTO;
import com.gyeom.homeflix.file.VideoReader;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class HomeFlixController {

    public static final String PATH_DELIMITER = "&&";

    final VideoReader videoReader;

    public HomeFlixController(@Qualifier(value = "localVideoReader") VideoReader videoReader){
        this.videoReader = videoReader;
    }

    @GetMapping(value = {"/videos", "/videos/{path}"})
    public ResponseEntity<List<FileDTO>> list(@PathVariable(value = "path") Optional<String> path){
        try {
            if(path.isEmpty()) return new ResponseEntity<>(videoReader.list(), HttpStatus.OK);
            return new ResponseEntity<>(videoReader.list(path.get().split(PATH_DELIMITER)), HttpStatus.OK);
        }catch (RuntimeException e){
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = {"/video/{video}"})
    public ResponseEntity<StreamingResponseBody> stream(@PathVariable(value = "video") String video) {
        StreamingResponseBody stream = out -> {
            String msg = "/video/"+ video + " @ " + Instant.now();
            for(int i = 0; i < 5; i++){
                try {
                    Thread.sleep(1000);
                    out.write(msg.getBytes());
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        };
        return new ResponseEntity<>(stream, HttpStatus.OK);
    }
}
