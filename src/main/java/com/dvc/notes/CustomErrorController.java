package com.dvc.notes;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller // Catch-all solution for any missed exceptions.
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    private String handleError() {
        return "errors/404";
    }

}
