package com.tfg.brais;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.view.RedirectView;
@Controller
public class SPAController {

    @GetMapping("/")
    public RedirectView redirectToAngularApp() {
        return new RedirectView("/index.html");
    }

    @GetMapping("/**/{path:[^\\.]*}")
    public String forward() {
        return "forward:/index.html";
    }
}
