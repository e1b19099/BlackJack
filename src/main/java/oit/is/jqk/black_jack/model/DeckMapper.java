package oit.is.jqk.black_jack.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface DeckMapper {

  @Select("SELECT * from deck where deck_id = #{deck_id} LIMIT 1")
  Deck selectDeckById(int deck_id);

  @Insert({ "<script>", "insert into deck (deck_id,deal_card,id) values ",
      "<foreach collection=\"data\" item=\"a\" index=\"i\" separator=\",\"> ", "(#{deck_id},", "#{i},", "#{a.id}", ")",
      "</foreach>", "</script>" })
  int bulkinsert(int deck_id, @Param("data") List<Card> data);

  @Delete("DELETE FROM deck WHERE deck_id = #{deck_id} and id = #{id}")
  boolean deleteDeckById(int deck_id, int id);
}
