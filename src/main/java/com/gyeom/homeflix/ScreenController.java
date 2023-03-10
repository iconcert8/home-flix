package com.gyeom.homeflix;

import com.gyeom.homeflix.login.JwtProperties;
import com.gyeom.homeflix.video.FileDTO;
import com.gyeom.homeflix.video.FileType;
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

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping(value = "screen")
public class ScreenController {

    private Logger log = LoggerFactory.getLogger(this.getClass().getSimpleName());

    private final VideoController videoController;
    public ScreenController(VideoController videoController){
        this.videoController = videoController;
    }

    @GetMapping(value = {"/login"})
    public ModelAndView goLogin(HttpServletRequest request, HttpServletResponse response){
        final String VIEW_LOGIN = "login.html";
        ModelAndView mv = new ModelAndView();
        mv.setViewName(VIEW_LOGIN);

        Cookie refreshCookie = new Cookie(JwtProperties.REFRESH_TOKEN_HEADER, null);
        refreshCookie.setMaxAge(0); // 쿠키의 expiration 타임을 0으로 하여 없앤다.
        refreshCookie.setPath("/"); // 모든 경로에서 삭제 됬음을 알린다.

        Cookie accessCookie = new Cookie(JwtProperties.ACCESS_TOKEN_HEADER, null);
        accessCookie.setMaxAge(0);
        accessCookie.setPath("/");

        Cookie accessExpireCookie = new Cookie(JwtProperties.ACCESS_EXPIRTE_DATE, null);
        accessExpireCookie.setMaxAge(0);
        accessExpireCookie.setPath("/");

        response.addCookie(refreshCookie);
        response.addCookie(accessCookie);
        response.addCookie(accessExpireCookie);

        return mv;
    }

    @GetMapping(value = {"/login/fail"})
    public ModelAndView goLoginOnFail(HttpServletRequest request, HttpServletResponse response){
        final String KEY_FAIL = "fail";
        ModelAndView mv = goLogin(request, response);
        mv.addObject(KEY_FAIL, true);

        return mv;
    }

    @GetMapping(value = {"/videos", "/videos/{path}"})
    public ModelAndView goScreenVideos(@PathVariable("path") Optional<String> path, HttpServletRequest request){
        final String KEY_PATH = "path";
        final String KEY_LIST = "list";
        final String VIEW_VIDEO_LIST = "video_list.html";

        ModelAndView mv = new ModelAndView();
        mv.setViewName(VIEW_VIDEO_LIST);
        mv.addObject(KEY_PATH, makeVideoListPathObject(path));
        mv.addObject(KEY_LIST, makeVideoListObject(path));
        return mv;
    }

    @GetMapping(value = {"/video/{path}"})
    public ModelAndView goScreenStream(@PathVariable("path") String path, HttpServletRequest request){
        final String VIEW_VIDEO_STREAM = "video_stream.html";
        final String KEY_STREAMURL = "streamUrl";
        final String KEY_PATH = "path";
        final String KEY_VIDEO_NAME = "name";
        final String KEY_PREV = "prev";
        final String KEY_NEXT = "next";
        final String KEY_LIST = "list";

        ModelAndView mv = new ModelAndView();
        mv.setViewName(VIEW_VIDEO_STREAM);
        mv.addObject(KEY_STREAMURL, path);
        mv.addObject(KEY_VIDEO_NAME, makeVideoStreamNameObject(path));
        mv.addObject(KEY_PATH, makeVideoStreamPathObject(path));

        List<FileDTO> list = makeVideoListObject(Optional.of(path.substring(0, path.lastIndexOf(VideoController.PATH_DELIMITER))));
        FileDTO[] videos = list.stream().filter(f -> f.getType() == FileType.VIDEO).toArray(FileDTO[]::new);
        int currentIdx = currentIdxInVideos(videos, makeVideoStreamNameObject(path));
        mv.addObject(KEY_PREV, currentIdx > 0 ? videos[currentIdx-1] : null);
        mv.addObject(KEY_NEXT, currentIdx < videos.length-1 ? videos[currentIdx+1] : null);
        mv.addObject(KEY_LIST, videos);
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

    private List<FileDTO> makeVideoListObject(Optional<String> path){
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

    private int currentIdxInVideos(FileDTO[] videos, String currentName){
        int currentIdx = 0;
        for(int i = 0; i < videos.length; i++){
            if(videos[i].getName().equals(currentName)) {
                currentIdx = i;
                break;
            }
        }
        return currentIdx;
    }
}
