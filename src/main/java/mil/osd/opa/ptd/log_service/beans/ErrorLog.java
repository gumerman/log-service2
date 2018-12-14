package mil.osd.opa.ptd.log_service.beans;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class ErrorLog {

    private long dlptErrMsgId; //DLPT_ERR_MSG_ID
    private String logLvlCd; //LOG_LVL_CD
    private Integer tstOprSesnId; //TST_OPR_SESN_ID
    private Long resultId; //RESULT_ID
    private Long subtstId; //SUBTST_ID
    private Integer passageId; //PASSAGE_ID
    private Date dlptErrMsgDtm; //DLPT_ERR_MSG_DTM
    private String srcClsNm; //SRC_CLS_NM
    private String srcMthdNm; //SRC_MTHD_NM
    private String dlptErrMsgCdTx; //DLPT_ERR_MSG_CD_TX
    private String dlptErrMsgTx; //DLPT_ERR_MSG_TX
    private String excThrwnTx; //EXC_THRWN_TX
    private String hostNm; //HOST_NM
    
        
    public long getDlptErrMsgId() {
        return dlptErrMsgId;
    }

    public void setDlptErrMsgId(long dlptErrMsgId) {
        this.dlptErrMsgId = dlptErrMsgId;
    }
    
    // force null LOG_LEVEL to be ERROR
    public String getLogLvlCd() {
        if (null == logLvlCd) {
           logLvlCd = "ERROR" ;
        } 
        return logLvlCd;
    }

    public void setLogLvlCd(String logLvlCd) {
        this.logLvlCd = logLvlCd == null ? null : logLvlCd.trim();
    }

    public Long getResultId() {
        return resultId;
    }

    public void setResultId(Long resultId) {
        this.resultId = resultId;
    }

    public Long getSubtstId() {
		return subtstId;
	}

	public void setSubtstId(Long subtstId) {
		this.subtstId = subtstId;
	}

    public void setPassageId(Integer passageId) {
        this.passageId = passageId;
    }

    public Integer getPassageId() {
		return passageId;
	}

	public Date getDlptErrMsgDtm() {
        return dlptErrMsgDtm;
    }

    public void setDlptErrMsgDtm(Date dlptErrMsgDtm) {
        this.dlptErrMsgDtm = dlptErrMsgDtm;
    }

    public String getSrcClsNm() {
        if (this.srcClsNm != null) { 
            if (this.srcClsNm.length() > 256) {
               this.setSrcClsNm(this.srcClsNm.substring(0,255));
            }
         }
        return this.srcClsNm;
    }

    public void setSrcClsNm(String srcClsNm) {
        this.srcClsNm = srcClsNm == null ? null : srcClsNm.trim();
    }

    public String getSrcMthdNm() {
        if (this.srcMthdNm != null) { 
               if (this.srcMthdNm.length() > 256) {
                  this.setsrcMthdNm(this.srcMthdNm.substring(0,255));
               }
        }
        return this.srcMthdNm;
    }

    public void setsrcMthdNm(String srcMthdNm) {
        this.srcMthdNm = srcMthdNm == null ? null : srcMthdNm.trim();
    }
    

    public String getDlptErrMsgCdTx() {
		return dlptErrMsgCdTx;
	}

	public void setDlptErrMsgCdTx(String dlptErrMsgCdTx) {
		this.dlptErrMsgCdTx = dlptErrMsgCdTx == null ? null :dlptErrMsgCdTx.trim();
	}

	public String getDlptErrMsgTx() {
        return dlptErrMsgTx;
    }

    public void setDlptErrMsgTx(String message) {
        this.dlptErrMsgTx = message == null ? null : message.trim();
    }

    public String getExcThrwnTx() {
        return this.excThrwnTx;
    }

    public void setExcThrwnTx(String excThrwnTx) {
        this.excThrwnTx = excThrwnTx == null ? null : excThrwnTx.trim();
    }
    
    public void setExcThrwnTx(Throwable t) {
        String excThrwnTx = throwableToString(t);
        this.excThrwnTx = excThrwnTx == null ? null : excThrwnTx.trim();
    }
    
    public String toString() {
        String outStr = "Level:" + this.getLogLvlCd() +
                        " Class/Method: " + this.getSrcClsNm() + "." + this.getSrcMthdNm() +
        		        " Err #:" + this.getDlptErrMsgCdTx() +
        		        " TestOperatorSessionId: " + this.getTstOprSesnId() +
        		        " ResultId: " + this.getResultId() +
        		        " SubTestId: " + this.getSubtstId() +
        		        " PassageId: " + this.getPassageId() +
                    " Message: " + this.getDlptErrMsgTx() +
                    " Exception: " + this.getExcThrwnTx();
        return outStr;
    }

    public String toLog4J(final String systemTest) {
      String outStr = " Class/Method: " + this.getSrcClsNm() + "." + this.getSrcMthdNm() +
                  " Err #:" + this.getDlptErrMsgCdTx() +
                  " SystemTest: " + systemTest +
                  " TestOperatorSessionId: " + this.getTstOprSesnId() +
                  " ResultId: " + this.getResultId() +
                  " SubTestId: " + this.getSubtstId() +
                  " PassageId: " + this.getPassageId() +
                  " Message: " + this.getDlptErrMsgTx() +
                  " Exception: " + this.getExcThrwnTx();
      return outStr;
  }

    /*
     * 
     *  Fluent Builder methods.
     * 
     */
    
    public ErrorLog level(String logLvlCd) {
        this.logLvlCd = logLvlCd;
        return this;
    }
    public ErrorLog tstOprSesnId(Integer tstOprSesnId) {
        this.tstOprSesnId = tstOprSesnId;
        return this;
    }
    public ErrorLog resultId(long resultId) {
        this.resultId = resultId;
        return this;
    }
    public ErrorLog className(String srcClsNm) {
        this.srcClsNm = srcClsNm;
        return this;
    }
    public ErrorLog method(String srcMthdNm) {
        this.srcMthdNm = srcMthdNm;
        return this;
    }
    public ErrorLog messageCd(String messageCdText) {
        this.dlptErrMsgCdTx = messageCdText;
        return this;
    }
    public ErrorLog message(String messageText) {
        this.dlptErrMsgTx = messageText;
        return this;
    }
    public ErrorLog exception(String ex) {
        this.setExcThrwnTx(ex);
        return this;    
    }
    public ErrorLog exception(Throwable t) {
        this.setExcThrwnTx(t);
        return this;
    }
    public String getHostNm() {
        return hostNm;
    }

    public void setHostNm(String hostNm) {
        this.hostNm = hostNm;
    }

    public Integer getTstOprSesnId() {
        return tstOprSesnId;
    }

    public void setTstOprSesnId(Integer tstOprSesnId) {
        this.tstOprSesnId = tstOprSesnId;
    }

    /*
     * extra utility stuff..
     */

    // Takes a throwable and converts its stacktrace to a String
    // so we can put it in the database (CLOB).
    public String throwableToString(Throwable throwable) {
        if (throwable == null) return "";
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        throwable.printStackTrace(pw);
        String stackTrace = sw.toString();

        return stackTrace;
    }

 
}


