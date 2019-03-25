package socidia.middleware_server.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController {

    @RequestMapping("/hello")
    public String hello() {
        return "Hello world!";
    }

    @RequestMapping("/")
    public String home() {
        return "index";
    }

}