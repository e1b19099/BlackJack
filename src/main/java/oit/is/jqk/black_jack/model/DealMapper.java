package oit.is.jqk.black_jack.model;

import java.util.ArrayList;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface DealMapper {

  @Insert("INSERT INTO deal (deal_id,deal_number,id) VALUES (#{deal_id},#{deal_number},#{id});")
  void insertDeal(Deal deal);

  @Select("SELECT * FROM deal where deal_id = #{deal_id}")
  ArrayList<Deal> selectDealById(int deal_id);
}
