package oit.is.jqk.black_jack.model;

import java.util.ArrayList;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.Delete;

@Mapper
public interface RoomUserMapper {

  @Select("SELECT * from roomuser;")
  ArrayList<RoomUser> selectAllRoomUser();

  @Select("SELECT * from roomuser where room_id = #{id} order by time")
  ArrayList<RoomUser> selectRoomUserByRoomid(int id);

  @Select("SELECT * from roomuser where room_id = #{room_id} and user_id = #{user_id}")
  RoomUser selectRoomUserByAllId(int room_id, int user_id);

  @Insert("INSERT INTO roomuser (room_id,user_id,time) values (#{room_id}, #{user_id}, now())")
  void insertRoomUser(int room_id, int user_id);

  @Select("SELECT count(*) from roomuser where room_id = #{id}")
  int selectRoomUserCount(int id);

  @Delete("Delete from roomuser where room_id = #{room_id} and user_id =#{user_id} limit 1")
  void deleteUserdata(int room_id, int user_id);

  @Update("UPDATE roomuser SET use_chip = use_chip + #{bet} WHERE room_id = #{room_id} and user_id = #{user_id}")
  void updateUseChipById(int room_id, int user_id, int bet);

  @Select("SELECT count(*) FROM ROOMUSER where use_chip = 0 and room_id = #{room_id}")
  int selectBettedUserCount(int room_id);
}
