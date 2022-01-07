package oit.is.jqk.black_jack.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

@Service
public class AsyncShopService57 {
  boolean dbUpdated = false;

  private final Logger logger = LoggerFactory.getLogger(AsyncShopService57.class);

  @Autowired
  RoomMapper roomMapper;

  @Autowired
  RoomUserMapper roomUserMapper;

  @Autowired
  DealMapper dealMapper;

  @Autowired
  DeckMapper deckMapper;

  /**
   * 購入対象の果物IDの果物をDBから削除し，購入対象の果物オブジェクトを返す
   *
   * @param id 購入対象の果物のID
   * @return 購入対象の果物のオブジェクトを返す
   */
  /*
   * @Transactional public Fruit syncBuyFruits(int id) { // 削除対象のフルーツを取得 Fruit
   * fruit = fMapper.selectById(id);
   *
   * // 削除 fMapper.deleteById(id);
   *
   * // 非同期でDB更新したことを共有する際に利用する this.dbUpdated = true;
   *
   * return fruit; }
   */
  @Transactional
  public MyRoom selectMyRoom(int room_id) {
    Room fruits7 = roomMapper.selectRoomById(room_id);

    MyRoom myroom = new MyRoom(fruits7);
    ArrayList<RoomUser> ru = roomUserMapper.selectRoomUserByRoomid(room_id);
    ArrayList<Members> members = new ArrayList<>();
    for (RoomUser roomuser : ru) {
      Members member = new Members(roomuser);
      ArrayList<Deal> deal = dealMapper.selectDealById(member.getDeal_id());
      member.setDeals(deal);
      members.add(member);
    }
    myroom.setMembers(members);
    return myroom;
  }

  /**
   * dbUpdatedがtrueのときのみブラウザにDBからフルーツリストを取得して送付する
   *
   * @param emitter
   */
  @Async
  public void asyncShowFruitsList(SseEmitter emitter, int room_id, int user_id) {
    dbUpdated = true;
    try {
      while (true) {// 無限ループ
        // DBが更新されていなければ0.5s休み
        if (false == dbUpdated) {
          TimeUnit.MILLISECONDS.sleep(500);
          continue;
        }
        // DBが更新されていれば更新後のフルーツリストを取得してsendし，1s休み，dbUpdatedをfalseにする
        MyRoom myroom = selectMyRoom(room_id);
        ArrayList<Members> members = myroom.getMembers();
        if (myroom.getLimits() > members.size() - 1) {
          myroom.setLimit(false);
        } else {
          myroom.setLimit(true);
        }
        ArrayList<Object> sendObj = new ArrayList<>();
        sendObj.add(myroom);
        Map<String, String> status = new HashMap<String, String>();
        status.put("turn", "1");
        sendObj.add(status);
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
    System.out.println("asyncShowFruitsList complete");
  }

}
