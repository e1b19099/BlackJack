package oit.is.jqk.black_jack.controller;

import java.sql.Date;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import javax.swing.UIManager;

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
import oit.is.jqk.black_jack.model.DeckMapper;
import oit.is.jqk.black_jack.model.Room;
import oit.is.jqk.black_jack.model.RoomMapper;
import oit.is.jqk.black_jack.model.RoomUser;
import oit.is.jqk.black_jack.model.RoomUserMapper;
import oit.is.jqk.black_jack.model.Userinfo;
import oit.is.jqk.black_jack.model.UserinfoMapper;

@Controller
@RequestMapping("/")
public class BlackjackController {
  @Autowired
  CardMapper cmapper;

  @Autowired
  RoomMapper rMapper;

  @Autowired
  UserinfoMapper uMapper;

  @Autowired
  RoomUserMapper ruMapper;

  @Autowired
  DeckMapper dMapper;

  ArrayList<Card> cList = new ArrayList<>();
  ArrayList<Card> dList = new ArrayList<>();
  ArrayList<Card> deck = new ArrayList<>();

  int betChip = 0;

  @GetMapping("/help")
  public String help() {
    return "help.html";
  }

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
  public String sample36(@RequestParam String roomname, @RequestParam Integer count, ModelMap model) {
    Room room = new Room();
    room.setRoom_name(roomname);
    long miliseconds = System.currentTimeMillis();
    Date date = new Date(miliseconds);
    room.setDate(date);
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
  public String Blackjack01(Principal prin, @PathVariable Integer room_id, ModelMap model) {
    Room room = rMapper.selectRoomById(room_id);
    if (room == null) {
      return "/error";
    }
    ArrayList<RoomUser> users = ruMapper.selectAllRoomUser();
    Userinfo user = uMapper.selectUserByName(prin.getName());
    for (RoomUser ru : users) {
      if (ru.getUser_id() == user.getUser_id()) {
        return "error_joined.html";
      }
    }

    ruMapper.insertRoomUser(room_id, user.getUser_id());
    model.addAttribute("room_id", room_id);
    cList.clear();
    dList.clear();
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
    // ArrayList<Card> dCards = new ArrayList<>(); //playerHit
    // ArrayList<Card> AddDCards = new ArrayList<>();
    cList.clear();
    dList.clear();
    deck = cmapper.selectAll();
    // デッキシャッフル
    Collections.shuffle(deck);
    dMapper.bulkinsert(room_id, deck);
    int total = 0;
    boolean stand_flag = false;
    int dTotal = 0;// スタンド後の数字の合計
    int twodTotal = 0; // 初期手札の数字の合計
    // プレイヤーの処理
    // 初期手札の配布
    for (int i = 0; i < 2; i++) {
      // Random rand = new Random();
      // int id = rand.nextInt(52) % 52 + 1;
      // Card card = cmapper.selectById(id);
      Card card = deck.remove(0);
      int number = card.getNumber();
      if (number > 10)
        number = 10;
      total += number;
      cList.add(card);
    }
    // ディーラーの処理
    // 初期手札の配布
    for (int i = 0; i < 2; i++) {
      // Random rand = new Random();
      // int id = rand.nextInt(52) % 52 + 1;
      // Card card = cmapper.selectById(id);
      Card card = deck.remove(0);
      int number = card.getNumber();
      if (number > 10) {
        number = 10;
      }
      dTotal += number;
      dList.add(card);// dCardsをdListに変更_Z0413
      twodTotal = dTotal;
    }
    // ヒット処理を停止 playerHit
    /*
     * // ヒット処理 while (dTotal <= 16) { Random rand = new Random(); int id =
     * rand.nextInt(52) % 52 + 1; Card Addcard = cmapper.selectById(id); int number2
     * = Addcard.getNumber(); if (number2 > 10) { number2 = 10; } dTotal += number2;
     * AddDCards.add(Addcard); }
     */
    model.addAttribute("room_id", room_id);
    model.addAttribute("cards", cList);
    model.addAttribute("total", total);
    model.addAttribute("dCards", dList);// model.addAttribute("dCards", dCards); //playerHit dCardsをdListに変更
    model.addAttribute("dTotal", dTotal);
    // model.addAttribute("AddDCards", AddDCards); //playerHit
    model.addAttribute("tmpdTotal", twodTotal);
    model.addAttribute("stand_flag", stand_flag);

    model.addAttribute("bet", betChip);

    return "blackjack.html";
  }

  // Hit処理
  @GetMapping("/blackjack/{room_id}/hit")
  public String Blackjack04Hit(Principal prin, @PathVariable Integer room_id, ModelMap model) {
    // ArrayList<Card> dCards = new ArrayList<>(); //playerHit
    ArrayList<Card> AddDCards = new ArrayList<>();
    int total = 0;
    int dTotal = 0;// スタンド後の数字の合計
    int twodTotal = 0; // 初期手札の数字の合計
    int result = 0;
    boolean stand_flag = false;

    String loginUser = prin.getName();// addBet

    // プレイヤーの処理
    // Hit前の手札の合計
    for (Card card : cList) {
      int number = card.getNumber();
      if (number > 10)
        number = 10;
      total += number;
    }

    // Random rand = new Random();
    // int id = rand.nextInt(52) % 52 + 1;
    // Card card = cmapper.selectById(id);
    Card Hitcard = deck.remove(0);
    int hitNumber = Hitcard.getNumber();
    if (hitNumber > 10) {
      hitNumber = 10;
    }
    total += hitNumber;
    cList.add(Hitcard);

    // ディーラーの処理
    // 初期手札の合計
    for (Card card : dList) {
      int number = card.getNumber();
      if (number > 10) {
        number = 10;
      }
      dTotal += number;
      twodTotal = dTotal;
    }
    if (total > 21) {
      stand_flag = true;
      // ヒット処理
      while (dTotal <= 16) {
        // Random rand = new Random();
        // int id = rand.nextInt(52) % 52 + 1;
        // Card card = cmapper.selectById(id);
        Card Addcard = deck.remove(0);
        int number2 = Addcard.getNumber();
        if (number2 > 10) {
          number2 = 10;
        }
        dTotal += number2;
        AddDCards.add(Addcard);
      }
      model.addAttribute("AddDCards", AddDCards);
      model.addAttribute("dTotal", dTotal);

      // 勝敗判定
      int p = total, d = dTotal;

      if (total > 21)
        p = -1;
      if (dTotal > 21)
        d = 0;

      if (p > d) {
        result = 1;
        uMapper.updateChipById(uMapper.selectUserIdByName(loginUser), betChip * 2);// addBet
      } else if (p < d) {
        result = -1;
      } else if (p == d) {
        result = 2;
        uMapper.updateChipById(uMapper.selectUserIdByName(loginUser), betChip);// addBet
      }
      Userinfo user = uMapper.selectUserByName(loginUser);

      model.addAttribute("user", user);
    } else
      model.addAttribute("bet", betChip);

    model.addAttribute("tmpdTotal", twodTotal);
    model.addAttribute("room_id", room_id);
    model.addAttribute("cards", cList);
    model.addAttribute("total", total);
    model.addAttribute("dCards", dList);
    model.addAttribute("stand_flag", stand_flag);
    model.addAttribute("result", result);

    return "blackjack.html";
  }

  // スタンド処理
  @GetMapping("/blackjack/{room_id}/stand")
  public String Blackjack05stand(Principal prin, @PathVariable Integer room_id, ModelMap model) {
    int total = 0;
    int dTotal = 0;// スタンド後の数字の合計
    int tmpdTotal = 0; // 初期手札の数字の合計
    ArrayList<Card> AddDCards = new ArrayList<>();
    boolean stand_flag = true;
    String loginUser = prin.getName();// ユーザ名取得

    // プレイヤーの初期手札の内容取得
    for (Card card : cList) {
      int number = card.getNumber();
      if (number > 10)
        number = 10;
      total += number;
    }
    /*
     * // ディーラーの初期手札の内容取得 for (Card card : dList) { int number = card.getNumber();
     * if (number > 10) number = 10; dTotal += number; } // 追加手札の内容取得 if
     * (dList.size() - 1 > 2) { Card dAddDcards = dList.get(dList.size() - 1);
     * tmpdTotal = dTotal - dAddDcards.getNumber(); model.addAttribute("AddDCards",
     * dAddDcards); }
     */

    // ディーラーの処理
    // 初期手札の合計
    for (Card card : dList) {
      int number = card.getNumber();
      if (number > 10) {
        number = 10;
      }
      dTotal += number;
      tmpdTotal = dTotal;
    }

    // ヒット処理
    while (dTotal <= 16) {
      // Random rand = new Random();
      // int id = rand.nextInt(52) % 52 + 1;
      // Card card = cmapper.selectById(id);
      Card Addcard = deck.remove(0);
      int number2 = Addcard.getNumber();
      if (number2 > 10) {
        number2 = 10;
      }
      dTotal += number2;
      AddDCards.add(Addcard);
    }
    model.addAttribute("AddDCards", AddDCards);

    // 勝敗判定
    int p = total, d = dTotal;
    int result = 0;
    if (total > 21)
      p = -1;
    if (dTotal > 21)
      d = 0;

    if (p > d) {
      result = 1;
      uMapper.updateChipById(uMapper.selectUserIdByName(loginUser), betChip * 2);// addBet
    } else if (p < d) {
      result = -1;
    } else if (p == d) {
      result = 2;
      uMapper.updateChipById(uMapper.selectUserIdByName(loginUser), betChip);// addBet
    }
    Userinfo user = uMapper.selectUserByName(loginUser);

    model.addAttribute("room_id", room_id);
    model.addAttribute("cards", cList);
    model.addAttribute("total", total);
    model.addAttribute("dCards", dList);
    model.addAttribute("dTotal", dTotal);

    model.addAttribute("tmpdTotal", tmpdTotal);
    model.addAttribute("stand_flag", stand_flag);
    model.addAttribute("user", user);
    model.addAttribute("result", result);
    return "blackjack.html";
  }

  // Bet
  @PostMapping("/blackjack/{room_id}/bet")
  public String BlackjackBet(Principal prin, @RequestParam Integer bet, @PathVariable Integer room_id, ModelMap model) {
    if (bet <= 0) {
      model.addAttribute("room_id", room_id);
      return "blackjack.html";
    }
    betChip = bet;
    String loginUser = prin.getName();

    uMapper.updateChipById(uMapper.selectUserIdByName(loginUser), -betChip);
    Userinfo user = uMapper.selectUserByName(loginUser);

    model.addAttribute("user", user);
    model.addAttribute("room_id", room_id);
    model.addAttribute("bet", betChip);
    return "blackjack.html";

  }
}
