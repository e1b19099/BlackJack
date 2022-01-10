package oit.is.jqk.black_jack.model;

import java.util.ArrayList;

public class Members extends RoomUser {
  ArrayList<Deal> deals;
  String name;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Members(RoomUser roomUser) {
    super(roomUser);
  }

  public ArrayList<Deal> getDeals() {
    return deals;
  }

  public void setDeals(ArrayList<Deal> deals) {
    this.deals = deals;
  }
}
