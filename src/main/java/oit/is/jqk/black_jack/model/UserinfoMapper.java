package oit.is.jqk.black_jack.model;

import java.util.Optional;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;

@Mapper
public interface UserinfoMapper {
  @Select("SELECT user_id,username,password,chip from userinfo where username = #{username}")
  Optional<Userinfo> findUser(String username);

  @Select("SELECT user_id from userinfo where username = #{username}")
  int selectUserIdByName(String username);

  @Select("SELECT user_id,username,password,chip from userinfo where username = #{username}")
  Userinfo selectUserByName(String username);

  @Select("SELECT * from userinfo where user_id = #{user_id}")
  Userinfo selectUserById(int user_id);

  @Insert("INSERT INTO userinfo (username,password) values (#{username},#{password});")
  @Options(useGeneratedKeys = true, keyColumn = "user_id", keyProperty = "user_id")
  void insertUserinfo(Userinfo userinfo);

  @Update("UPDATE userinfo SET chip = chip + #{bet} WHERE user_id = #{id}")
  void updateChipById(int id, int bet);

}
