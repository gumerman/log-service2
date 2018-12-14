package mil.osd.opa.ptd.log_service.mapper;

import java.math.BigDecimal;
import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;
import org.apache.ibatis.annotations.Update;

import mil.osd.opa.ptd.log_service.beans.ErrorLogThreshold;

@Mapper
public interface LogThresholdMapper {
    
@Insert({"insert into LOG_THRSHLD (LOG_THRSHLD_ID, LOG_LVL_CD, THRSHLD_CLS_NM, TST_SITE_ID) ",
    "values (#{logThrshldId,jdbcType=DECIMAL}, #{logLvlCd,jdbcType=VARCHAR}, #{thrshldClsNm,jdbcType=VARCHAR}, #{tstSiteId,jdbcType=VARCHAR})"})
@SelectKey(
        statement = " select MAX(LOG_THRSHLD_ID) + 1 FROM LOG_THRSHLD",
        keyProperty = "logThrshldId",
        before = true,
        resultType = BigDecimal.class)
	int insertThreshold(ErrorLogThreshold threshold);

@Update({"update LOG_THRSHLD ",
    " set LOG_LVL_CD = #{threshold.logLvlCd,jdbcType=VARCHAR}, ",
         "THRSHLD_CLS_NM = #{threshold.thrshldClsNm,jdbcType=VARCHAR}, ",
         "TST_SITE_ID = #{threshold.tstSiteId,jdbcType=VARCHAR}",
    " where LOG_THRSHLD_ID = #{threshold.logThrshldId,jdbcType=DECIMAL}"
    })
    int updateThreshold(@Param("threshold") ErrorLogThreshold threshold);

//@Select("select 1 as logThreholdId, 'TRACE' as logLevel, 'mil.osd.dmdc.ptd.wdlpt' as className, '' as tstSiteId from DUAL")
//    List<ErrorLogThreshold> getThresholds();
@Select("select LOG_THRSHLD_ID as logThrshldId, LOG_LVL_CD as logLvlCd, THRSHLD_CLS_NM as thrshldClsNm, TST_SITE_ID as tstSiteId from LOG_THRSHLD")
List<ErrorLogThreshold> getThresholds();

}