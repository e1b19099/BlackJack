package oit.is.jqk.black_jack.model;

import java.util.Optional;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserinfoMapper {
  @Select("SELECT user_id,username,password,chip from userinfo where username = #{username}")
  Optional<Userinfo> findUser(String username);
}
