package controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AppController {

    @GetMapping(RouteConfiguration.SEARCH_MAPPING_ROUTE)
    public String searchFrom(Model model) {
        model.addAttribute("query", new LSIRESQuery());
        return "search";
    }

    @PostMapping(RouteConfiguration.RESULTS_MAPPING_ROUTE)
    public String searchSubmit(@ModelAttribute LSIRESQuery query) {
        return "results";
    }
}
