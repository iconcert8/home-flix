package com.gyeom.homeflix;

import com.gyeom.homeflix.video.FileDTO;
import com.gyeom.homeflix.video.screenDTO.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping(value = "screen")
public class ScreenController {

    Logger log = LoggerFactory.getLogger(this.getClass().getSimpleName());

    private final VideoController videoController;
    public ScreenController(VideoController videoController){
        this.videoController = videoController;
    }

    @GetMapping(value = "/login")
    public ModelAndView goLogin(){
        final String VIEW_LOGIN = "login.html";
        ModelAndView mv = new ModelAndView();
        mv.setViewName(VIEW_LOGIN);
        return mv;
    }

    @GetMapping(value = {"/videos", "/videos/{path}"})
    public ModelAndView goScreenVideos(@PathVariable("path") Optional<String> path){
        final String KEY_PATH = "path";
        final String KEY_LIST = "list";
        final String VIEW_VIDEO_LIST = "video_list.html";

        ModelAndView mv = new ModelAndView();
        mv.setViewName(VIEW_VIDEO_LIST);
        mv.addObject(KEY_PATH, makeVideoListPathObject(path));
        mv.addObject(KEY_LIST, makeVideoListListObject(path));
        return mv;
    }

    @GetMapping(value = {"/video/{path}"})
    public  ModelAndView goScreenStream(@PathVariable("path") String path){
        final String VIEW_VIDEO_STREAM = "video_stream.html";
        final String KEY_STREAMURL = "streamUrl";
        final String KEY_PATH = "path";
        final String KEY_VIDEO_NAME = "name";
        ModelAndView mv = new ModelAndView();
        mv.setViewName(VIEW_VIDEO_STREAM);
        mv.addObject(KEY_STREAMURL, path);
        mv.addObject(KEY_VIDEO_NAME, makeVideoStreamNameObject(path));
        mv.addObject(KEY_PATH, makeVideoStreamPathObject(path));
        return mv;
    }

    private List<Path> makeVideoListPathObject(Optional<String> path){
        List<Path> pathList = new LinkedList<>();
        String[] splitPath = path.map(s -> s.split(VideoController.PATH_DELIMITER)).orElse(new String[0]);
        StringBuilder sb = new StringBuilder("");
        for(int i = 0; i < splitPath.length; i++){
            if(i == 0) sb.append(splitPath[i]);
            else sb.append(VideoController.PATH_DELIMITER).append(splitPath[i]);
            pathList.add(new Path(sb.toString(), splitPath[i]));
        }
        return pathList;
    }

    private List<FileDTO> makeVideoListListObject(Optional<String> path){
        ResponseEntity<List<FileDTO>> resEntity = videoController.list(path);
        if(resEntity.getStatusCode() == HttpStatus.OK){
            return resEntity.getBody();
        }
        return Collections.emptyList();
    }

    private List<Path> makeVideoStreamPathObject(String path){
        List<Path> pathList = new LinkedList<>();
        String[] splitPath = path.split(VideoController.PATH_DELIMITER);
        StringBuilder sb = new StringBuilder("");
        for(int i = 0; i < splitPath.length-1; i++){
            if(i == 0) sb.append(splitPath[i]);
            else sb.append(VideoController.PATH_DELIMITER).append(splitPath[i]);
            pathList.add(new Path(sb.toString(), splitPath[i]));
        }
        return pathList;
    }

    private String makeVideoStreamNameObject(String path){
        String[] splitPath = path.split(VideoController.PATH_DELIMITER);
        return splitPath[splitPath.length-1];
    }
}
