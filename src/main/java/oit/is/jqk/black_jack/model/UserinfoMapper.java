package oit.is.jqk.black_jack.model;

import java.util.Optional;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.Delete;
@Mapper
public interface UserinfoMapper {
  @Select("SELECT user_id,username,password,chip from userinfo where username = #{username}")
  Optional<Userinfo> findUser(String username);

  @Select("SELECT user_id from userinfo where username = #{username}")
  int selectUserIdByName(String username);

  @Select("SELECT user_id,username,password,chip from userinfo where username = #{username}")
  Userinfo selectUserByName(String username);

  @Update("UPDATE userinfo SET chip = chip + #{bet} WHERE user_id = #{id}")
  void updateChipById(int id, int bet);


}
