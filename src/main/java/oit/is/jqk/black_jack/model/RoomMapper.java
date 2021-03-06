package oit.is.jqk.black_jack.model;

import java.util.ArrayList;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface RoomMapper {
  @Select("SELECT * from room;")
  ArrayList<Room> selectAllRoom();

  @Select("SELECT * from room where turn < 1")
  ArrayList<Room> selectAliveRoom();

  @Select("SELECT * from room where room_id = #{room_id}")
  Room selectRoomById(int room_id);

  @Insert("INSERT INTO room (room_name,limits,date,winner) VALUES (#{room_name},#{limits},#{date},#{winner});")
  @Options(useGeneratedKeys = true, keyColumn = "room_id", keyProperty = "room_id")
  void insertRoom(Room room);

  @Update("UPDATE ROOM SET TURN=#{turn} WHERE ROOM_ID = #{room_id}")
  void updateTurnById(Room room);

}
