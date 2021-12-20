package oit.is.jqk.black_jack.model;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface DeckMapper {
  @Insert({ "<script>", "insert into deck (deck_id,deal_card,id) values ",
      "<foreach collection=\"data\" item=\"a\" index=\"i\" separator=\",\"> ", "(#{deck_id},", "#{i},", "#{a.id}", ")",
      "</foreach>", "</script>" })
  int bulkinsert(int deck_id, @Param("data") List<Card> data);
}
