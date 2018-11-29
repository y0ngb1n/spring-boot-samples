package io.github.y0ngb1n.boot.thymeleaf.controller;

import io.github.y0ngb1n.boot.thymeleaf.domain.User;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Tags for Thymeleaf.
 *
 * @author y0ngb1n
 * @version 1.0.0
 */
@Controller
@RequestMapping("/tags")
public class TagsController {

  private static final String USERNAME = "y0ngb1n";

  private User user = User.builder().name(USERNAME).age(18).role("admin").build();

  @GetMapping("/text-utext")
  public String textAndutext(Model model) {
    model.addAttribute("content", "<span style='color:red'>thymeleaf text output</span>");
    return "text-utext";
  }

  @GetMapping("/refer-url")
  public String referUrl(Model model) {
    model.addAttribute("username", USERNAME);
    return "refer-url";
  }

  @GetMapping("replace-text")
  public String replaceText(Model model) {
    model.addAttribute("user", user);
    model.addAttribute("onevar", "one");
    model.addAttribute("twovar", "two");
    model.addAttribute("threevar", "three");
    return "replace-text";
  }

  @GetMapping("/operator")
  public String operator(Model model) {
    model.addAttribute("user", user);
    model.addAttribute("env", "dev");
    return "operator";
  }

  @GetMapping("/condition")
  public String condition(Model model) {
    model.addAttribute("user", user);
    return "condition";
  }

  @GetMapping("/loop")
  public String loop(Model model) {
    List<User> users = new ArrayList<>(3);
    users.add(user);
    users.add(User.builder().name("tony").age(23).role("user").build());
    users.add(User.builder().name("tom").age(21).role("user").build());

    model.addAttribute("users", users);
    return "loop";
  }
}
