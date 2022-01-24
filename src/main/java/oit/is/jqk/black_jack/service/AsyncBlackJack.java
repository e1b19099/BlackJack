package oit.is.jqk.black_jack.service;

import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import ch.qos.logback.core.net.SyslogOutputStream;
import oit.is.jqk.black_jack.model.Card;
import oit.is.jqk.black_jack.model.CardMapper;
import oit.is.jqk.black_jack.model.Deal;
import oit.is.jqk.black_jack.model.DealMapper;
import oit.is.jqk.black_jack.model.DeckMapper;
import oit.is.jqk.black_jack.model.Members;
import oit.is.jqk.black_jack.model.MyRoom;
import oit.is.jqk.black_jack.model.Room;
import oit.is.jqk.black_jack.model.RoomMapper;
import oit.is.jqk.black_jack.model.RoomUser;
import oit.is.jqk.black_jack.model.RoomUserMapper;
import oit.is.jqk.black_jack.model.Userinfo;
import oit.is.jqk.black_jack.model.UserinfoMapper;
import oit.is.jqk.black_jack.model.Deck;
import java.util.Collections;

@Service
public class AsyncBlackJack {
  boolean dbUpdated;
  boolean noLimit = false;

  private final Logger logger = LoggerFactory.getLogger(AsyncBlackJack.class);

  @Autowired
  RoomMapper roomMapper;

  @Autowired
  RoomUserMapper roomUserMapper;

  @Autowired
  DealMapper dealMapper;

  @Autowired
  DeckMapper deckMapper;

  @Autowired
  UserinfoMapper userinfoMapper;

  @Autowired
  CardMapper cardMapper;

  @Transactional
  public MyRoom selectMyRoom(int room_id) {
    Room room = roomMapper.selectRoomById(room_id);

    MyRoom myroom = new MyRoom(room);
    ArrayList<RoomUser> ru = roomUserMapper.selectRoomUserByRoomid(room_id);
    ArrayList<Members> members = new ArrayList<>();
    for (RoomUser roomuser : ru) {
      Members member = new Members(roomuser);
      Userinfo ui = userinfoMapper.selectUserById(member.getUser_id());
      ArrayList<Deal> deal = dealMapper.selectDealById(member.getDeal_id());
      member.setTotal(sumHand(room_id, ui.getUser_id()));
      member.setHaving_chip(ui.getChip());
      member.setDeals(deal);
      member.setName(ui.getUsername());
      members.add(member);
    }
    myroom.setMembers(members);
    return myroom;
  }

  @Transactional
  public void enterRoom(int room_id, int user_id) {
    boolean isEntering = false;
    ArrayList<RoomUser> users = roomUserMapper.selectRoomUserByRoomid(room_id);
    for (RoomUser ru : users) {
      if (ru.getUser_id() == user_id) {
        // return "error_joined.html";
        isEntering = true;
      }
    }
    if (isEntering == false) {
      roomUserMapper.insertRoomUser(room_id, user_id);
      for (int i = 0; i < 2; i++) {
        Card card = drawCard(room_id);
        dealUser(room_id, user_id, card.getId());
      }
      ArrayList<Deal> dealer_deal = dealMapper
          .selectDealById(roomUserMapper.selectRoomUserByAllId(room_id, 0).getDeal_id());
      if (dealer_deal.size() == 0) {
        for (int i = 0; i < 2; i++) {
          Card card = drawCard(room_id);
          dealUser(room_id, 0, card.getId());
        }
      }
    }
    /*
     * RoomUser ru = roomUserMapper.selectRoomUserByAllId(room_id, user_id);
     * RoomUser dealer = roomUserMapper.selectRoomUserByAllId(room_id, 0);
     * dealMapper.deleteUserDeal(ru.getDeal_id());
     * dealMapper.deleteUserDeal(dealer.getDeal_id());
     */
    dbUpdated = true;
  }

  @Transactional
  public void updateTurn(int room_id) {
    Room room = roomMapper.selectRoomById(room_id);
    int turn = room.getTurn();
    room.setTurn(turn + 1);
    roomMapper.updateTurnById(room);
    dbUpdated = true;
  }

  @Transactional
  public Card drawCard(int room_id) {
    Deck deck = deckMapper.selectDeckById(room_id);
    int card_id = deck.getId();
    deckMapper.deleteDeckById(room_id, card_id);
    Card getCard = cardMapper.selectById(card_id);
    dbUpdated = true;
    return getCard;
  }

  @Transactional
  public void dealUser(int room_id, int user_id, int card_id) {
    RoomUser ruUser = roomUserMapper.selectRoomUserByAllId(room_id, user_id);
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
    dbUpdated = true;
  }

  @Transactional
  public void deleteDeck(int room_id) {
    deckMapper.deleteDeckData(room_id);
  }

  @Transactional
  public void insertAllDeck(int room_id, ArrayList<Card> deck) {
    deckMapper.bulkinsert(room_id, deck);
  }

  @Transactional
  public void init(int room_id, ArrayList<Members> members) {
    try {
      // deleteDeck(room_id);

      // updateTurn(room_id);

      /*
       * for (int j = 1; j < members.size(); j++) { for (int i = 0; i < 2; i++) { Card
       * card = drawCard(room_id); dealUser(room_id, members.get(j).getUser_id(),
       * card.getId()); } }
       */

      // ディーラーの処理
      // 初期手札の配布
      /*
       * for (int i = 0; i < 2; i++) { Card card = drawCard(room_id);
       * dealUser(room_id, 0, card.getId()); }
       */

    } catch (Exception e) {
      logger.warn("Exception:" + e.getClass().getName() + ":" + e.getMessage());
      System.out.println("エラー発生中");
    } finally {
      dbUpdated = true;
    }

  }

  @Transactional
  public void hit(int room_id, int user_id) {
    Card getCard = drawCard(room_id);
    dealUser(room_id, user_id, getCard.getId());
    dbUpdated = true;
  }

  @Transactional
  public void stand(int room_id) {
    updateTurn(room_id);
    dbUpdated = true;
  }

  @Transactional
  public void bet(int room_id, int user_id, int bet) {
    roomUserMapper.updateUseChipById(room_id, user_id, bet);
    userinfoMapper.updateChipById(user_id, -bet);
    dbUpdated = true;
  }

  public void forcedStart() {
    noLimit = true;
  }

  /**
   * dbUpdatedがtrueのときのみブラウザにDBからフルーツリストを取得して送付する
   *
   * @param emitter
   */
  @Async
  public void asyncShowBlackJack(SseEmitter emitter, int room_id, int user_id) {
    try {
      dbUpdated = true;
      while (true) {// 無限ループ
        // DBが更新されていなければ0.5s休み
        if (false == dbUpdated) {
          TimeUnit.MILLISECONDS.sleep(500);
          continue;
        }
        MyRoom myroom = selectMyRoom(room_id);
        ArrayList<Members> members = myroom.getMembers();
        int turn = myroom.getTurn();
        switch (turn) {
          case -1:
            if (myroom.getLimits() <= members.size() - 1) {
              myroom.setLimit(true);
              updateTurn(room_id);
              myroom.setTurn(turn + 1);
              noLimit = false;
            }
            break;
          case 0:
            int betted = roomUserMapper.selectBettedUserCount(room_id);
            if (betted <= 1) {
              init(room_id, members);
              updateTurn(room_id);
              myroom.setTurn(turn + 1);
            }
            break;

        }
        turn = myroom.getTurn();
        if (turn >= 1) {
          if (turn < members.size() && members.get(turn).getTotal() > 22) {
            updateTurn(room_id);
            myroom.setTurn(turn + 1);
          }
        }
        if (turn >= members.size()) {
          members.get(0).setTotal(sumHand(room_id, 0));
          while (members.get(0).getTotal() <= 16) {
            Card Addcard = drawCard(room_id);
            dealUser(room_id, 0, Addcard.getId());
            // dList.add(Addcard);
            members.get(0).setTotal(sumHand(room_id, 0));
          }
          Members dealer = members.get(0);
          int d = dealer.getTotal();
          if (d > 21)
            d = 0;
          for (int i = 1; i < members.size(); i++) {
            Members member = members.get(i);
            int p = member.getTotal();

            if (p > 21)
              p = -1;

            if (p > d) {
              member.setResult(1);
              userinfoMapper.updateChipById(member.getUser_id(), member.getUse_chip() * 2);
            } else if (p < d) {
              member.setResult(-1);
            } else {
              member.setResult(2);
              userinfoMapper.updateChipById(member.getUser_id(), member.getUse_chip());
            }
            myroom.setMembers(members);
          }

        } else if (turn >= 1 && turn <= members.size() - 1) {
          ArrayList<Deal> dealer_deals = members.get(0).getDeals();
          dealer_deals.get(0).setId(53);
          myroom.getMembers().get(0).setDeals(dealer_deals);
        }
        ArrayList<Object> sendObj = new ArrayList<>();
        sendObj.add(myroom);
        emitter.send(sendObj, MediaType.APPLICATION_JSON);
        TimeUnit.MILLISECONDS.sleep(300);
        dbUpdated = true;
      }
    } catch (Exception e) {
      // 例外の名前とメッセージだけ表示する
      logger.warn("Exception:" + e.getClass().getName() + ":" + e.getMessage());
    } finally {
      // emitter.complete();
    }
    System.out.println("asyncBlackJack complete");
  }

  // 手札の合計
  public int sumHand(int room_id, int user_id) {
    RoomUser roomuser = roomUserMapper.selectRoomUserByAllId(room_id, user_id);
    ArrayList<Card> list = dealMapper.selectDealCardById(roomuser.getDeal_id());

    int total = 0;
    int countAce = 0;
    for (Card card : list) {
      int number = card.getNumber();
      if (number > 10)
        number = 10;
      if (number == 1) {
        number = 11;
        countAce += 1;
      }
      total += number;
    }
    while (total > 21 && countAce > 0) {
      total -= 10;
      countAce -= 1;
    }
    dbUpdated = true;

    return total;
  }

}
