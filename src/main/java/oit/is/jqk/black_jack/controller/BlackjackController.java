package oit.is.jqk.black_jack.controller;

import java.sql.Date;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import oit.is.jqk.black_jack.model.Card;
import oit.is.jqk.black_jack.model.CardMapper;
import oit.is.jqk.black_jack.model.Deal;
import oit.is.jqk.black_jack.model.DealMapper;
import oit.is.jqk.black_jack.model.Deck;
import oit.is.jqk.black_jack.model.DeckMapper;
import oit.is.jqk.black_jack.model.Room;
import oit.is.jqk.black_jack.model.RoomMapper;
import oit.is.jqk.black_jack.model.RoomUser;
import oit.is.jqk.black_jack.model.RoomUserMapper;
import oit.is.jqk.black_jack.model.Userinfo;
import oit.is.jqk.black_jack.model.UserinfoMapper;
import oit.is.jqk.black_jack.service.AsyncCountFruit56;
import oit.is.jqk.black_jack.service.AsyncBlackJack;

@Controller
@RequestMapping("/")
public class BlackjackController {
  @Autowired
  CardMapper cMapper;

  @Autowired
  RoomMapper rMapper;

  @Autowired
  UserinfoMapper uMapper;

  @Autowired
  RoomUserMapper ruMapper;

  @Autowired
  DeckMapper deckMapper;

  @Autowired
  DealMapper dealMapper;

  @Autowired
  private AsyncCountFruit56 ac56;

  @Autowired
  private AsyncBlackJack bj;

  ArrayList<Card> cList = new ArrayList<>();
  ArrayList<Card> dList = new ArrayList<>();
  ArrayList<Card> deck = new ArrayList<>();

  int betChip = 0;// 賭けた金額

  @GetMapping("/help")
  public String help() {
    return "help.html";
  }

  /*
   * ルームに所属しているメンバの数を引数として受け取った Roomリストに付属させて返却する。
   *
   * 引数:ArrayList<Room> 戻り値:ArrayList<Room>
   */
  public ArrayList<Room> countRoomMember(ArrayList<Room> rooms) {
    for (Room cntRoom : rooms) {
      int memberCount = ruMapper.selectRoomUserCount(cntRoom.getRoom_id()) - 1;
      if (memberCount == -1) {
        ruMapper.insertRoomUser(cntRoom.getRoom_id(), 0);
        memberCount = ruMapper.selectRoomUserCount(cntRoom.getRoom_id()) - 1;
      }
      cntRoom.setCount(memberCount);
    }
    return rooms;
  }

  @GetMapping("/room")
  public String Room01(ModelMap model) {
    ArrayList<Room> rooms = rMapper.selectAllRoom();
    if (rooms.isEmpty()) {
      model.addAttribute("rooms", 0);
    } else {
      rooms = countRoomMember(rooms);
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
    room.setLimits(count);
    rMapper.insertRoom(room);
    ArrayList<Room> rooms = rMapper.selectAllRoom();
    int room_id = rooms.get(rooms.size() - 1).getRoom_id();
    ArrayList<Card> deck = cMapper.selectAll();
    // デッキシャッフル
    Collections.shuffle(deck);
    deckMapper.bulkinsert(room_id, deck);
    if (rooms.isEmpty()) {
      model.addAttribute("rooms", 0);
    } else {
      rooms = countRoomMember(rooms);
      model.addAttribute("rooms", rooms);
    }
    return "room.html";
  }

  @GetMapping("/blackjack/{room_id}/draw")
  public String Blackjack02(@PathVariable Integer room_id, ModelMap model) {
    Random rand = new Random();
    int id = rand.nextInt(52) % 52 + 1;
    Card card = cMapper.selectById(id);
    model.addAttribute("room_id", room_id);
    model.addAttribute("card", card);
    model.addAttribute("id", id);
    return "blackjack.html";
  }

  @Transactional
  public Card drawCard(int room_id) {
    Deck deck = deckMapper.selectDeckById(room_id);
    int card_id = deck.getId();
    deckMapper.deleteDeckById(room_id, card_id);
    Card getCard = cMapper.selectById(card_id);
    return getCard;
  }

  public void dealUser(int room_id, int user_id, int card_id) {
    RoomUser ruUser = ruMapper.selectRoomUserByAllId(room_id, user_id);
    int deal_id = ruUser.getDeal_id();
    ArrayList<Deal> deals = dealMapper.selectDealById(deal_id);
    int max = 0;
    if (deals != null) {
      for (Deal deal : deals) {
        if (max < deal.getDeal_number()) {
          max = deal.getDeal_number();
        }
      }
    }
    Deal newDeal = new Deal();
    newDeal.setDeal_id(deal_id);
    newDeal.setDeal_number(max + 1);
    newDeal.setId(card_id);
    dealMapper.insertDeal(newDeal);
  }

  // Bet
  @PostMapping("/blackjack/{room_id}/bet")
  @ResponseBody
  public String BlackjackBet(Principal prin, @RequestParam Integer bet, @PathVariable Integer room_id, ModelMap model) {
    if (bet <= 0 || bet > 100) {
      model.addAttribute("room_id", room_id);
      return "-1";
    }
    int user_id = uMapper.selectUserIdByName(prin.getName());
    bj.bet(room_id, user_id, bet);

    /*
     * model.addAttribute("user", user); model.addAttribute("room_id", room_id);
     * model.addAttribute("bet", betChip);
     */
    return "1";

  }

  @GetMapping("/exit/{room_id}")
  public String exit(@PathVariable Integer room_id, Principal prin) {
    Userinfo exituser = uMapper.selectUserByName(prin.getName());
    RoomUser roomuser = ruMapper.selectRoomUserByAllId(room_id, exituser.getUser_id());
    dealMapper.deleteUserDeal(roomuser.getDeal_id());
    ruMapper.deleteUserdata(room_id, exituser.getUser_id());
    return "exit.html";
  }

  @GetMapping("/sample56/step1")
  public SseEmitter pushCount() {
    // infoレベルでログを出力する

    // push処理の秘密兵器．これを利用してブラウザにpushする
    // finalは初期化したあとに再代入が行われない変数につける（意図しない再代入を防ぐ）
    final SseEmitter sseEmitter = new SseEmitter();
    this.ac56.count(sseEmitter);
    return sseEmitter;
  }

  @GetMapping("/blackjack/{room_id}")
  public String Blackjack01(Principal prin, @PathVariable Integer room_id, ModelMap model) {
    Room room = rMapper.selectRoomById(room_id);
    if (room == null) {
      return "/error";
    }
    int user_id = uMapper.selectUserIdByName(prin.getName());
    bj.enterRoom(room_id, user_id);

    model.addAttribute("room_id", room_id);
    model.addAttribute("user_id", user_id);
    // model.addAttribute("cards", cList);
    /*
     * model.addAttribute("total", total); model.addAttribute("dCards", dList); //
     * model.addAttribute("dTotal", dTotal); model.addAttribute("tmpdTotal",
     * twodTotal); model.addAttribute("stand_flag", stand_flag);
     *
     * model.addAttribute("bet", betChip);
     *
     * cList.clear(); dList.clear();
     */
    return "blackjack.html";
  }

  // START
  @GetMapping("/blackjack/{room_id}/start")
  public String Blackjack03(Principal prin, @PathVariable Integer room_id, ModelMap model) {
    // ArrayList<Card> dCards = new ArrayList<>(); //playerHit
    // ArrayList<Card> AddDCards = new ArrayList<>();
    cList.clear();
    dList.clear();

    Userinfo ui = uMapper.selectUserByName(prin.getName());
    RoomUser ru = ruMapper.selectRoomUserByAllId(room_id, ui.getUser_id());
    RoomUser dealer = ruMapper.selectRoomUserByAllId(room_id, 0);
    dealMapper.deleteUserDeal(ru.getDeal_id());
    dealMapper.deleteUserDeal(dealer.getDeal_id());
    if (deckMapper.selectDeckById(room_id) == null) {
      deck = cMapper.selectAll();
      // デッキシャッフル
      Collections.shuffle(deck);
      deckMapper.bulkinsert(room_id, deck);
    }
    int total = 0;
    boolean stand_flag = false;
    int dTotal = 0;// スタンド後の数字の合計
    int twodTotal = 0; // 初期手札の数字の合計
    // プレイヤーの処理
    // 初期手札の配布
    for (int i = 0; i < 2; i++) {
      Card card = drawCard(room_id);
      int number = card.getNumber();
      if (number > 10)
        number = 10;
      if (number == 1)
        number = 11; // addPlayerAce
      total += number;
      dealUser(room_id, ui.getUser_id(), card.getId());
      cList.add(card);
    }

    // ディーラーの処理
    // 初期手札の配布
    for (int i = 0; i < 2; i++) {
      Card card = drawCard(room_id);
      int number = card.getNumber();
      if (number > 10) {
        number = 10;
      }
      dTotal += number;
      dList.add(card);// dCardsをdListに変更_Z0413
      dealUser(room_id, 0, card.getId());
      twodTotal = dTotal;
    }

    model.addAttribute("room_id", room_id);
    model.addAttribute("cards", cList);
    model.addAttribute("total", total);
    model.addAttribute("dCards", dList);
    // model.addAttribute("dTotal", dTotal);
    model.addAttribute("tmpdTotal", twodTotal);
    model.addAttribute("stand_flag", stand_flag);

    model.addAttribute("bet", betChip);

    return "blackjack.html";
  }

  // Hit処理
  @GetMapping("/blackjack/{room_id}/hit")
  @ResponseBody
  public String Blackjack04Hit(Principal prin, @PathVariable Integer room_id, ModelMap model) {
    int total = 0;
    int twodTotal = 0; // 初期手札の数字の合計
    int result = 0;
    boolean stand_flag = false;

    String loginUser = prin.getName();
    Userinfo user = uMapper.selectUserByName(loginUser);
    // プレイヤーの処理

    // カード追加
    // Card Hitcard = drawCard(room_id);
    // cList.add(Hitcard);
    bj.hit(room_id, user.getUser_id());
    // Hit後の手札の合計

    total = bj.sumHand(room_id, user.getUser_id());

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
    boolean stand_flag = true;
    String loginUser = prin.getName();// ユーザ名取得
    Userinfo user = uMapper.selectUserByName(loginUser);
    // プレイヤーの手札の合計
    total = bj.sumHand(room_id, user.getUser_id());

    // ヒット処理
    dTotal = bj.sumHand(room_id, 0);
    while (dTotal <= 16) {
      Card Addcard = drawCard(room_id);
      bj.dealUser(room_id, 0, Addcard.getId());
      // dList.add(Addcard);
      dTotal = bj.sumHand(room_id, 0);
    }
    /*
     * while (dTotal <= 16) { // Card Addcard = deck.remove(0); Card Addcard =
     * drawCard(room_id); int number2 = Addcard.getNumber(); if (number2 > 10) {
     * number2 = 10; } dTotal += number2; AddDCards.add(Addcard); }
     * model.addAttribute("AddDCards", AddDCards);
     */

    // 勝敗判定
    int p = total, d = dTotal;
    int result = 0;
    if (total > 21)
      p = -1;
    if (dTotal > 21)
      d = 0;

    if (p > d) {
      result = 1;
      uMapper.updateChipById(uMapper.selectUserIdByName(loginUser), betChip * 2);
    } else if (p < d) {
      result = -1;
    } else if (p == d) {
      result = 2;
      uMapper.updateChipById(uMapper.selectUserIdByName(loginUser), betChip);
    }

    bj.stand(room_id);

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

  @GetMapping("/blackjack/status/{room_id}/{user_id}")
  public SseEmitter pushRoomList(@PathVariable Integer room_id, @PathVariable Integer user_id) {
    // infoレベルでログを出力する

    // push処理の秘密兵器．これを利用してブラウザにpushする
    // finalは初期化したあとに再代入が行われない変数につける（意図しない再代入を防ぐ）
    final SseEmitter sseEmitter = new SseEmitter();
    this.bj.asyncShowBlackJack(sseEmitter, room_id, user_id);
    return sseEmitter;
  }
}
