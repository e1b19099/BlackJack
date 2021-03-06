package oit.is.jqk.black_jack.model;

import java.sql.Date;

public class Room {
  int room_id;
  String room_name;
  int limits;
  int count;
  int turn;

  public Room() {

  }

  public Room(Room room) {
    this.room_id = room.getRoom_id();
    this.room_name = room.getRoom_name();
    this.limits = room.getLimits();
    this.count = room.getCount();
    this.turn = room.getTurn();
  }

  public int getTurn() {
    return turn;
  }

  public void setTurn(int turn) {
    this.turn = turn;
  }

  public int getLimits() {
    return limits;
  }

  public void setLimits(int limits) {
    this.limits = limits;
  }

  public int getCount() {
    return count;
  }

  public void setCount(int count) {
    this.count = count;
  }

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
