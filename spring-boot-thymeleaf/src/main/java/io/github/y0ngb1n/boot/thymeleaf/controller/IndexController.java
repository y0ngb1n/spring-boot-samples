package io.github.y0ngb1n.boot.thymeleaf.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

  @GetMapping(path = {"/", "index"})
  public String indexPage(Model model) {
    model.addAttribute("message", "Hello Thymeleaf!");
    return "index";
  }
}
