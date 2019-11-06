package urlshortener.web;


import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.stereotype.Controller;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;


@Controller
public class TemplateEngineController { 

    public TemplateEngineController() {}

    @GetMapping("/stadistics")
    public ModelAndView stadistics(HttpServletRequest request) {
        System.out.println("global stadistics");
        ModelAndView modelAndView;
        modelAndView = new ModelAndView("stadistics");
        // Add single Object example
        modelAndView.addObject("title", "ulink Global Stadistics from ftl");
        // Add list example
        List<String> myWordsList = new ArrayList<>();
        myWordsList.add("hello");
        myWordsList.add("world");
        myWordsList.add("example");
        modelAndView.addObject("words", myWordsList);
        return modelAndView;
    }

    @GetMapping("/warning")
    public ModelAndView warning(HttpServletRequest request) {
        ModelAndView modelAndView;
        modelAndView = new ModelAndView("warning");
        return modelAndView;
    }

    @GetMapping("/link-stats-access")
    public ModelAndView linkStatsAccess(HttpServletRequest request) {
        ModelAndView modelAndView;
        modelAndView = new ModelAndView("link-stats-access");
        return modelAndView;
    }

}
