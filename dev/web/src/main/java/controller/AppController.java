package app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AppController {

    @GetMapping(Config.SEARCH_MAPPING_URL)
    public String searchFrom(Model model) {
        model.addAttribute("query", new LSIRESQuery());
        return "search";
    }

    @PostMapping(Config.RESULTS_MAPPING_URL)
    public String searchSubmit(@ModelAttribute LSIRESQuery query) {
        return "results";
    }
}
