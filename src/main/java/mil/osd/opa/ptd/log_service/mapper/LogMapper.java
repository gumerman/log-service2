package mil.osd.opa.ptd.log_service.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.jdbc.SQL;

import mil.osd.opa.ptd.log_service.beans.ErrorLog;

@Mapper
public interface LogMapper {

    @SelectProvider(type = LogSqlBuilder.class, method = "buildGetLogById")
    ErrorLog getLogById(@Param("dlptErrMsgId") long dlptErrMsgId);
    
    class LogSqlBuilder {
      public String buildGetLogById(Map parameters) {
        return new SQL(){{
          SELECT("DLPT_ERR_MSG_ID as dlptErrMsgId," +
              "LOG_LVL_CD as logLvlCd," +
              "TST_OPR_SESN_ID as tstOprSesnId," +
              "RESULT_ID as resultId," +
              "SUBTST_ID as subtstId," +
              "PASSAGE_ID as passageId," +
              "DLPT_ERR_MSG_DTM as dlptErrMsgDtm," +
              "SRC_CLS_NM as srcClsNm," +
              "SRC_MTHD_NM as srcMthdNm," +
              "DLPT_ERR_MSG_CD_TX as dlptErrMsgCdTx," + 
              "DLPT_ERR_MSG_TX as dlptErrMsgTx," +
              "EXC_THRWN_TX as excThrwnTx," +
              "HOST_NM as hostNm");
          FROM("DLPT_ERR_MSG");
          WHERE("DLPT_ERR_MSG_ID = #{dlptErrMsgId,jdbcType=DECIMAL}"); 
        }}.toString();
      }
    }
    
  @SelectProvider(type = LogSqlBuilder2.class, method = "buildGetLogById")
  ErrorLog getLogByIdTest(@Param("systemTest") String systemTest, 
                          @Param("dlptErrMsgId") long dlptErrMsgId);
  
  class LogSqlBuilder2 {
    public String buildGetLogById(Map parameters) {
      final String systemTest = (String) parameters.get("systemTest");
      return new SQL(){{
        SELECT("DLPT_ERR_MSG_ID as dlptErrMsgId," +
            "LOG_LVL_CD as logLvlCd," +
            "TST_OPR_SESN_ID as tstOprSesnId," +
            "RESULT_ID as resultId," +
            "SUBTST_ID as subtstId," +
            "PASSAGE_ID as passageId," +
            "DLPT_ERR_MSG_DTM as dlptErrMsgDtm," +
            "SRC_CLS_NM as srcClsNm," +
            "SRC_MTHD_NM as srcMthdNm," +
            "DLPT_ERR_MSG_CD_TX as dlptErrMsgCdTx," + 
            "DLPT_ERR_MSG_TX as dlptErrMsgTx," +
            "EXC_THRWN_TX as excThrwnTx," +
            "HOST_NM as hostNm");
      if (systemTest != null && systemTest.equals("")) {
          FROM("DLPT_ERR_MSG");
        } else {
          FROM("DLPT_ERR_MSGTEST");
        }
        WHERE("DLPT_ERR_MSG_ID = #{dlptErrMsgId,jdbcType=DECIMAL}"); 
      }}.toString();
    }
  }
  
  @Select({"select * from (",
      "select DLPT_ERR_MSG_ID as dlptErrMsgId, ",
      "LOG_LVL_CD as logLvlCd, ", 
      "TST_OPR_SESN_ID as tstOprSesnId, ",
      "RESULT_ID as resultId, ",
      "SUBTST_ID as subtstId, ",
      "PASSAGE_ID as passageId, ",
      "DLPT_ERR_MSG_DTM as dlptErrMsgDtm, ", 
      "SRC_CLS_NM as srcClsNm, ",
      "SRC_MTHD_NM as srcMthdNm, ",
      "DLPT_ERR_MSG_CD_TX as dlptErrMsgCdTx, ",
      "DLPT_ERR_MSG_TX as dlptErrMsgTx," +
      "EXC_THRWN_TX as excThrwnTx, ",
      "HOST_NM as hostNm ",
      "from  DLPT_ERR_MSG ",
      "order by DLPT_ERR_MSG_ID desc",
      ") LIMIT #{starting,jdbcType=DECIMAL}, #{pageSize,jdbcType=DECIMAL}"})
    List<ErrorLog> getLogs(@Param("starting") Long starting, @Param("pageSize") Long pageSize);

  @Select({"select * from (",
    "select DLPT_ERR_MSG_ID as dlptErrMsgId, ",
    "LOG_LVL_CD as logLvlCd, ", 
    "TST_OPR_SESN_ID as tstOprSesnId, ",
    "RESULT_ID as resultId, ",
    "SUBTST_ID as subtstId, ",
    "PASSAGE_ID as passageId, ",
    "DLPT_ERR_MSG_DTM as dlptErrMsgDtm, ", 
    "SRC_CLS_NM as srcClsNm, ",
    "SRC_MTHD_NM as srcMthdNm, ",
    "DLPT_ERR_MSG_CD_TX as dlptErrMsgCdTx, ",
    "DLPT_ERR_MSG_TX as dlptErrMsgTx," +
    "EXC_THRWN_TX as excThrwnTx, ",
    "HOST_NM as hostNm ",
    "from  DLPT_ERR_MSG ",
    "order by DLPT_ERR_MSG_ID desc",
    ") LIMIT #{rowLimit,jdbcType=DECIMAL}"})
  List<ErrorLog> getLastLogs(@Param("rowLimit") Long rowLimit);


// temp insert until CR DB 3.28 is done
//    @Insert({"insert into DLPT_ERR_MSG${systemTest} (DLPT_ERR_MSG_ID, DLPT_ERR_MSG_CD_TX,",
//      "RESULT_ID, SUBTST_ID, DLPT_ERR_MSG_DTM) ",
//    " values (#{dlptErrMsgId,jdbcType=DECIMAL}, ",
//            "#{log.messageCd,jdbcType=VARCHAR}, ",
//            "#{log.resultId,jdbcType=DECIMAL}, ",
//            "#{log.subTestId,jdbcType=DECIMAL}, ",
//            "systimestamp)"
//           })
//    @SelectKey(
//            statement = "select DLPT_ERR_MSG${systemTest}_ID_SEQ.NEXTVAL from dual",
//            keyProperty = "dlptErrMsgId",
//            before = true,
//            resultType = Long.class)
//    void insertLog(@Param("systemTest") String systemTest, @Param("log") ErrorLog log);

    @Insert({" insert into DLPT_ERR_MSG (DLPT_ERR_MSG_ID, LOG_LVL_CD, TST_OPR_SESN_ID, ",
        "RESULT_ID, SUBTST_ID, PASSAGE_ID, HOST_NM, DLPT_ERR_MSG_DTM, SRC_CLS_NM, ",
        "SRC_MTHD_NM, DLPT_ERR_MSG_CD_TX, DLPT_ERR_MSG_TX, EXC_THRWN_TX) ",
        "values (null, ",
                "#{log.logLvlCd,jdbcType=VARCHAR},  ",
                "#{log.tstOprSesnId,jdbcType=DECIMAL}, ",
                "#{log.resultId,jdbcType=DECIMAL}, ",
                "#{log.subtstId,jdbcType=DECIMAL}, ",
                "#{log.passageId,jdbcType=DECIMAL}, ",
                "#{log.hostNm,jdbcType=VARCHAR}, ",
                "current_timestamp, ", 
                "#{log.srcClsNm,jdbcType=VARCHAR}, ", 
                "#{log.srcMthdNm,jdbcType=VARCHAR}, ", 
                "#{log.dlptErrMsgCdTx,jdbcType=VARCHAR}, ", 
                "#{log.dlptErrMsgTx,jdbcType=VARCHAR}, ",
                "#{log.excThrwnTx,jdbcType=CLOB} )"
            })
      void insertLog(@Param("log") ErrorLog log);

    // Delete excess records from DLPT_ERR_MSG table
    @Delete({"delete from DLPT_ERR_MSG ",
             "where DLPT_ERR_MSG_ID in ",
             "(Select DLPT_ERR_MSG_ID from ",
             "(Select DLPT_ERR_MSG_ID FROM DLPT_ERR_MSG ",
             "Order By DLPT_ERR_MSG_ID Asc) as X",
             "LIMIT #{deleteCount})"
          })
    int deleteLog(@Param("deleteCount") Long deleteCount);

    // Delete excess records from DLPT_ERR_MSGTEST table
    @Delete({"delete from DLPT_ERR_MSGTEST ",
             "where DLPT_ERR_MSG_ID in ",
             "(Select DLPT_ERR_MSG_ID from ",
             "(Select DLPT_ERR_MSG_ID FROM DLPT_ERR_MSGTEST ",
             "Order By DLPT_ERR_MSG_ID Asc) as X",
             "LIMIT #{deleteCount})"
          })
    int deleteLogTest(@Param("deleteCount") Long deleteCount);

    @SelectProvider(type = LogCountBuilder.class, method = "getLogRecCount")
    Long getLogRecCount(@Param("systemTest") String systemTest);
    
    class LogCountBuilder {
      public String getLogRecCount(Map parameters) {
        final String systemTest = (String) parameters.get("systemTest");
        return new SQL(){{
          SELECT("count(*) as logRecCount ");
          if (systemTest != null && systemTest.equals("")) {
            FROM("DLPT_ERR_MSG");
          } else {
            FROM("DLPT_ERR_MSGTEST");
          }
        }}.toString();
      }
    }

}