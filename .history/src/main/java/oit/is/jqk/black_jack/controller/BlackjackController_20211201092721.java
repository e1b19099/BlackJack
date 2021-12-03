package oit.is.jqk.black_jack.controller;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import oit.is.jqk.black_jack.model.Card;
import oit.is.jqk.black_jack.model.CardMapper;
import oit.is.jqk.black_jack.model.Room;
import oit.is.jqk.black_jack.model.RoomMapper;

@Controller
@RequestMapping("/")
public class BlackjackController {
  @Autowired
  CardMapper cmapper;

  @Autowired
  RoomMapper rMapper;

  ArrayList<Card> cList = new ArrayList<>();

  @GetMapping("/room")
  public String Room01(ModelMap model) {
    ArrayList<Room> rooms = rMapper.selectAllRoom();
    if (rooms.isEmpty()) {
      model.addAttribute("rooms", 0);
    } else {
      model.addAttribute("rooms", rooms);
    }
    return "room.html";
  }

  @GetMapping("/room/make")
  public String Room02() {
    return "roommake.html";
  }

  @PostMapping("/room")
  public String sample36(@RequestParam String roomname, @RequestParam Integer count, Boolean isCPU, ModelMap model) {
    Room room = new Room();
    room.setRoom_name(roomname);
    long miliseconds = System.currentTimeMillis();
    Date date = new Date(miliseconds);
    room.setDate(date);
    if (isCPU == null) {
      isCPU = false;
    }
    room.setCPU(isCPU);
    rMapper.insertRoom(room);
    ArrayList<Room> rooms = rMapper.selectAllRoom();
    if (rooms.isEmpty()) {
      model.addAttribute("rooms", 0);
    } else {
      model.addAttribute("rooms", rooms);
    }
    return "room.html";
  }

  @GetMapping("/blackjack/{room_id}")
  public String Blackjack01(@PathVariable Integer room_id, ModelMap model) {
    model.addAttribute("room_id", room_id);
    return "blackjack.html";
  }

  @GetMapping("/blackjack/{room_id}/draw")
  public String Blackjack02(@PathVariable Integer room_id, ModelMap model) {
    Random rand = new Random();
    int id = rand.nextInt(52) % 52 + 1;
    Card card = cmapper.selectById(id);
    model.addAttribute("room_id", room_id);
    model.addAttribute("card", card);
    model.addAttribute("id", id);
    return "blackjack.html";
  }

  @GetMapping("/blackjack/{room_id}/start")
  public String Blackjack03(@PathVariable Integer room_id, ModelMap model) {
    ArrayList<Card> dCards = new ArrayList<>();
    int total = 0;
    int dTotal = 0;
    for (int i = 0; i < 2; i++) {
      Random rand = new Random();
      int id = rand.nextInt(52) % 52 + 1;
      Card card = cmapper.selectById(id);
      int number = card.getNumber();
      if (number > 10)
        number = 10;
      total += number;
      cList.add(card);
    }
    for (int i = 0; i < 2; i++) {
      Random rand = new Random();
      int id = rand.nextInt(52) % 52 + 1;
      Card card = cmapper.selectById(id);
      int number = card.getNumber();
      if (number > 10)
        number = 10;
      dTotal += number;
      dCards.add(card);
    }
    model.addAttribute("room_id", room_id);
    model.addAttribute("cards", cList);
    model.addAttribute("total", total);
    model.addAttribute("dCards", dCards);
    model.addAttribute("dTotal", dTotal);
    return "blackjack.html";
  }
}
