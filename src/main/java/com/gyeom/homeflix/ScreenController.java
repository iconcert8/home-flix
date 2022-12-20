package com.gyeom.homeflix;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.Optional;

@Controller
@RequestMapping(value = "screen")
public class ScreenController {

    Logger log = LoggerFactory.getLogger(this.getClass().getSimpleName());

    @GetMapping(value = {"/videos", "/videos/{path}"})
    @SuppressWarnings("unused")
    public ModelAndView goScreenVideos(@PathVariable("path") Optional<String> path){
        ModelAndView mv = new ModelAndView();
        mv.addObject("path", path);
        mv.setViewName("video_list.html");
        return mv;
    }
}
