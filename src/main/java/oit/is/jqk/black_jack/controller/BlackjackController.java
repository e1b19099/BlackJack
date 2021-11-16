package oit.is.jqk.black_jack.controller;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import oit.is.jqk.black_jack.model.Card;
import oit.is.jqk.black_jack.model.CardMapper;

@Controller
@RequestMapping("/")
public class BlackjackController {
  @Autowired 
  CardMapper cmapper;
  @GetMapping("/blackjack")
    public String Blackjack01() {
      return "blackjack.html";
    }
@GetMapping("/blackjack/draw")
  public String Blackjack02(ModelMap model) {
   Card card=cmapper.selectById(33);
   model.addAttribute("card",card);
    return "blackjack.html";
  }
 }


