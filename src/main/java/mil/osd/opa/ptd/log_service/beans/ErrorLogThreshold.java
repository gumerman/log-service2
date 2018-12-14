package mil.osd.opa.ptd.log_service.beans;

import java.math.BigDecimal;

import javax.validation.constraints.Size;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class ErrorLogThreshold {
    private BigDecimal logThrshldId; //LOG_THRSHLD_ID
    @Size(min = 0, max = 100)
    private String logLvlCd; //LOG_LVL_CD
    @Size(min = 0, max = 100)
    private String thrshldClsNm; //THRSHLD_CLS_NM
    @Size(min = 0, max = 9)
    private String tstSiteId; //TST_SITE_ID

    public BigDecimal getLogThrshldId() {
        return logThrshldId;
    }

    public void setLogThrshldId(BigDecimal logThrshldId) {
        this.logThrshldId = logThrshldId;
    }

    public String getLogLvlCd() {
        return logLvlCd;
    }

    public void setLogLvlCd(String logLvlCd) {
        this.logLvlCd = logLvlCd == null ? null : logLvlCd.trim();
    }

    public String getThrshldClsNm() {
        return thrshldClsNm;
    }

    public void setThrshldClsNm(String className) {
        this.thrshldClsNm = className == null ? null : className.trim();
    }

    public String getTstSiteId() {
        return tstSiteId;
    }

    public void setTstSiteId(String tstSiteId) {
        this.tstSiteId = tstSiteId == null ? null : tstSiteId.trim();
    }
}