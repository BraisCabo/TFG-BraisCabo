package com.tfg.brais;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
@Controller
public class SPAController {

    @GetMapping("/**/{path:[^\\.]*}")
    public String redirect() {
        return "index.html";
    }
}
