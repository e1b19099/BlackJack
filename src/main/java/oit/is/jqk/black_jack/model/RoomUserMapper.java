package oit.is.jqk.black_jack.model;

import java.util.ArrayList;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface RoomUserMapper {
  @Select("SELECT * from roomuser;")
  ArrayList<RoomUser> selectAllRoomUser();

  @Select("SELECT * from roomuser natural join userinfo where room_id = #{id}")
  ArrayList<RoomUser> selectRoomUserByRoomid(int id);

  @Insert("INSERT INTO roomuser (room_id,user_id) values (#{room_id}, #{user_id})")
  void insertRoomUser(int room_id, int user_id);
}
