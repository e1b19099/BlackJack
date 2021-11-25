package oit.is.jqk.black_jack.model;

import org.apache.ibatis.annotations.Select;

public interface RoomMapper {
  @Select("SELECT id,suit,number from room where id = #{id}")
  Card selectById(int id);
}
