package com.example.sun;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class SunApplicationController {

    @GetMapping("/input")
    public String inputForm(Model model) {
        model.addAttribute("input", new SunCalculator());
        return "input";
    }

    @PostMapping("/input")
    public String inputSubmit(@ModelAttribute SunCalculator suncalc, Model model) {
        model.addAttribute("input", suncalc);
        return "result";
    }

}
