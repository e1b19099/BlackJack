package oit.is.jqk.black_jack.model;

import java.util.ArrayList;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface DealMapper {


  @Insert("INSERT INTO deal (room_id,user_id,id) VALUES (#{room_id},#{user_id},#{id});")
  @Options(useGeneratedKeys = true, keyColumn = "", keyProperty = "room_id")
  void insertRoom(Room room);
}
