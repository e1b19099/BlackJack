package oit.is.jqk.black_jack.model;

import java.sql.Date;

public class Room {
  int room_id;
  String room_name;
  Date date;

  int winner;

  public int getRoom_id() {
    return room_id;
  }

  public void setRoom_id(int room_id) {
    this.room_id = room_id;
  }

  public String getRoom_name() {
    return room_name;
  }

  public void setRoom_name(String room_name) {
    this.room_name = room_name;
  }

  public Date getDate() {
    return date;
  }

  public void setDate(Date date) {
    this.date = date;
  }

  public int getWinner() {
    return winner;
  }

  public void setWinner(int winner) {
    this.winner = winner;
  }

}
