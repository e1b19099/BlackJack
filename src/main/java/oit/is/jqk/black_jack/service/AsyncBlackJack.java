package oit.is.jqk.black_jack.service;

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

@Service
public class AsyncBlackJack {
  boolean dbUpdated = false;
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
      member.setDeals(deal);
      member.setName(ui.getUsername());
      members.add(member);
    }
    myroom.setMembers(members);
    return myroom;
  }

  @Transactional
  public void updateTurn(int room_id) {
    Room room = roomMapper.selectRoomById(room_id);
    int turn = room.getTurn();
    room.setTurn(turn + 1);
    roomMapper.updateTurnById(room);
  }

  public void init(int room_id) {
    updateTurn(room_id);
  }

  public void stand(int room_id) {
    updateTurn(room_id);
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
    dbUpdated = true;
    try {
      while (true) {// 無限ループ
        // DBが更新されていなければ0.5s休み
        if (false == dbUpdated) {
          TimeUnit.MILLISECONDS.sleep(500);
          continue;
        }
        MyRoom myroom = selectMyRoom(room_id);
        ArrayList<Members> members = myroom.getMembers();
        if (myroom.getLimits() > members.size() - 1 || noLimit == true) {
          myroom.setLimit(false);
        } else {
          myroom.setLimit(true);
          noLimit = false;
        }
        ArrayList<Object> sendObj = new ArrayList<>();
        sendObj.add(myroom);
        emitter.send(sendObj, MediaType.APPLICATION_JSON);
        TimeUnit.MILLISECONDS.sleep(1000);
        // dbUpdated = false;
      }
    } catch (Exception e) {
      // 例外の名前とメッセージだけ表示する
      logger.warn("Exception:" + e.getClass().getName() + ":" + e.getMessage());
    } finally {
      // emitter.complete();
    }
    System.out.println("asyncBlackJack complete");
  }

}
