package mil.osd.opa.ptd.log_service.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface LookupMapper {

  @Select({"SELECT LKUP_VAL",
  "FROM LKUP_KEY_VAL",
  "where (LKUP_RGN_PRPT IS NULL OR LKUP_RGN_PRPT = #{region, jdbcType=VARCHAR})",
       "AND LKUP_DOMN = #{domain, jdbcType=VARCHAR}",
       "AND LKUP_KEY = #{key, jdbcType=VARCHAR}",
       "LIMIT 1"})
  String selectLookupValue(
      @Param("region") String region,
      @Param("domain") String domain,
      @Param("key") String key);

}
