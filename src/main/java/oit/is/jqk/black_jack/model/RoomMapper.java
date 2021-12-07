package oit.is.jqk.black_jack.model;

import java.util.ArrayList;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface RoomMapper {
  @Select("SELECT * from room;")
  ArrayList<Room> selectAllRoom();

  @Insert("INSERT INTO room (room_name,date,winner) VALUES (#{room_name},#{date},#{winner});")
  @Options(useGeneratedKeys = true, keyColumn = "room_id", keyProperty = "room_id")
  void insertRoom(Room room);
}
