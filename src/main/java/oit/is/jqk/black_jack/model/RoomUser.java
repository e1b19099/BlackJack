package oit.is.jqk.black_jack.model;

import java.sql.Timestamp;

public class RoomUser extends Userinfo {
  int room_id;
  int user_id;
  int deal_id;
  String time;

  public String getTime() {
    return time.toString();
  }

  public void setTime(String time) {
    this.time = time;
  }

  public RoomUser() {

  }

  public RoomUser(RoomUser roomUser) {
    this.room_id = roomUser.getRoom_id();
    this.user_id = roomUser.getUser_id();
    this.deal_id = roomUser.getDeal_id();
    this.time = roomUser.getTime();
  }

  public int getRoom_id() {
    return room_id;
  }

  public void setRoom_id(int room_id) {
    this.room_id = room_id;
  }

  public int getUser_id() {
    return user_id;
  }

  public void setUser_id(int user_id) {
    this.user_id = user_id;
  }

  public int getDeal_id() {
    return deal_id;
  }

  public void setDeal_id(int deal_id) {
    this.deal_id = deal_id;
  }

  @Override
  public boolean equals(Object obj) {
    // TODO Auto-generated method stub
    if (obj == this)
      return true;
    if (obj instanceof RoomUser) {
      RoomUser r = (RoomUser) obj;
      if (this.user_id == r.user_id) {
        return true; // 実行される
      }
      return false;
    }
    return false;
  }

}
