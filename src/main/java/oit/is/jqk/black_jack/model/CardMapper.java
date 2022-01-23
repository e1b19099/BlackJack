package oit.is.jqk.black_jack.model;

import java.util.ArrayList;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface CardMapper {
  @Select("SELECT id,suit,number from card where id = #{id}")
  Card selectById(int id);

  @Select("SELECT id,suit,number from card where id != 53")
  ArrayList<Card> selectAll();
}
