package com.gyeom.homeflix;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeFlixController {
    @GetMapping("/")
    public String root() {
        return "redirect:/screen/videos";
    }
}
