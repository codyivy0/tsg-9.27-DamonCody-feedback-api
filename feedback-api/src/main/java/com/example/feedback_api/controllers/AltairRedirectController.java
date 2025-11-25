package com.example.feedback_api.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Simple controller that provides convenience mappings for in-repo Graph UIs.
 * We previously had an Altair launcher here; that static launcher was removed.
 * Current mappings provide access to the in-repo GraphiQL launcher under /graphiql.
 */
@Controller
public class AltairRedirectController {
    @GetMapping("/graphiql")
    public String redirectGraphiql() {
        return "redirect:/graphiql/";
    }

    @GetMapping("/graphiql/")
    public String graphiqlDirectory() {
        return "forward:/graphiql/index.html";
    }
}
