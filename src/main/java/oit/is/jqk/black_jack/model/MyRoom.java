package oit.is.jqk.black_jack.model;

import java.util.ArrayList;

public class MyRoom extends Room {
  ArrayList<Members> members;
  Deck deck;
  boolean isLimit;

  public boolean isLimit() {
    return isLimit;
  }

  public void setLimit(boolean isLimit) {
    this.isLimit = isLimit;
  }

  public MyRoom(Room room) {
    super(room);
    this.isLimit = false;
  }

  public ArrayList<Members> getMembers() {
    return members;
  }

  public void setMembers(ArrayList<Members> members) {
    this.members = members;
  }

  public Deck getDeck() {
    return deck;
  }

  public void setDeck(Deck deck) {
    this.deck = deck;
  }
}
