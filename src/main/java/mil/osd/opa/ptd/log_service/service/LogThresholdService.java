package mil.osd.opa.ptd.log_service.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import mil.osd.opa.ptd.log_service.beans.ErrorLogThreshold;
import mil.osd.opa.ptd.log_service.mapper.LogThresholdMapper;
import mil.osd.opa.ptd.log_service.utils.WdlptEncode;
import mil.osd.opa.ptd.log_service.validator.BeanValidator;

/**
 * Service to store/retrieve logging thresholds by FQCN.
 * 
 * @author burkese
 *
 **/

@Service("logThreshold")
public class LogThresholdService {

  @Autowired
  LogThresholdMapper logThresholdMapper;

  private List<ErrorLogThreshold> thresholds;

  private Date lastUpdated;
  
  //created to appease Fortify
  private Object LogThresholdLock = new Object();//semaphore on access to singleton vars: thresholds , lastUpdated

  @Autowired
  public LogThresholdService(LogThresholdMapper ltm) {
	synchronized(LogThresholdLock) {
      this.logThresholdMapper = ltm;
      this.thresholds = logThresholdMapper.getThresholds();
      this.lastUpdated = new Date();
	}
  }

  public List<ErrorLogThreshold> getThresholds() {
    return thresholds;
  }

  /*
   * Scheduled task to ensure eventual synchronization across cluster.
   */
  @Scheduled(fixedDelay = 60000)
  public void refreshThresholds() {
		List<ErrorLogThreshold> thresholdList = null;
		thresholdList = logThresholdMapper.getThresholds();

		if (thresholdList != null) {
			Date aDate = new Date();
			synchronized (LogThresholdLock) {
				this.thresholds = thresholdList;
				this.lastUpdated = aDate;
			}
		}
	}

  public void setThreshold(String className, String logLevel, String tstSiteId) {
    ErrorLogThreshold threshold = new ErrorLogThreshold();
    threshold.setThrshldClsNm(className);
    threshold.setLogLvlCd(logLevel);
    threshold.setTstSiteId(tstSiteId);

    String oldval = getThresholdValue(className, tstSiteId);
    if (oldval == null) {
      insertThreshold(threshold);
    } else {
      updateThreshold(threshold);
    }

  }

  // used by setThreshold if threshold doesn't exist
  public void insertThreshold(ErrorLogThreshold threshold) {
    logThresholdMapper.insertThreshold(threshold);
    refreshThresholds();
  }

  // used by setThreshold if threshold exists
  public void updateThreshold(ErrorLogThreshold threshold) {
//    if (threshold.getTstSiteId() == null) {
//      threshold.setTstSiteId("x"); // adjust site id because where clause =, not is NULL
//    }
//    
    threshold.setLogThrshldId(BeanValidator.validateBigDecimalValue(threshold.getLogThrshldId(), "logThrshldId"));
    threshold.setLogLvlCd(WdlptEncode.forHtmlContent(threshold.getLogLvlCd()));
    threshold.setThrshldClsNm(WdlptEncode.forHtmlContent(threshold.getThrshldClsNm()));
    threshold.setTstSiteId(WdlptEncode.forHtmlContent(threshold.getTstSiteId()));
    
    logThresholdMapper.updateThreshold(threshold);
    refreshThresholds();
  }

  public ErrorLogThreshold getThreshold(String className, String tstSiteId) {
    ErrorLogThreshold threshold = null;
    
    synchronized(LogThresholdLock) {
    for (ErrorLogThreshold t : this.thresholds) {
      if (t.getThrshldClsNm().equalsIgnoreCase(className)) {
        if ((t.getTstSiteId() == null && tstSiteId == null) || (t.getTstSiteId() != null && t.getTstSiteId().equals(tstSiteId))) {
          return t; // early return to avoid long loop
        } 
      }
    }
    }

    return threshold;
  }

  public String getThresholdValue(String className, String tstSiteId) {
    String thresholdVal = null;
    synchronized(LogThresholdLock) {
    for (ErrorLogThreshold t : this.thresholds) {
      if (t.getThrshldClsNm().equalsIgnoreCase(className)) {
        if ((t.getTstSiteId() == null && tstSiteId == null) || (t.getTstSiteId() != null && t.getTstSiteId().equals(tstSiteId))) {
          return t.getLogLvlCd(); // early return to avoid long loop
        } 
      }
    }
    }
    return thresholdVal;
  }

  public String getBestMatch(String className) {
      return getBestMatch(className, null);
  }

  public String getBestMatch(String className, String tstSiteId) {
    String result = "INFO"; // should default to WARN to avoid too much
    // logging
    String clsName = className.toLowerCase();
    int matchLength = 0;
    int highMatchLength = 0;
    String candidate = "";
    synchronized(LogThresholdLock) {
    for (ErrorLogThreshold t : thresholds) {
      candidate = t.getThrshldClsNm().toLowerCase();
      if (clsName.equalsIgnoreCase(candidate)) {
        if (t.getTstSiteId() == null || tstSiteId == null || tstSiteId.equals("")) {
          return t.getLogLvlCd(); // early return, perfect match
        } else {
          if (t.getTstSiteId().equals(tstSiteId)) {
            return t.getLogLvlCd(); // early return, perfect match
          }
        }
      }
      if (clsName.contains(candidate)) {
        matchLength = candidate.length();
        if (matchLength > highMatchLength) {
          if (t.getTstSiteId() == null || tstSiteId == null) {
            result = t.getLogLvlCd();
            highMatchLength = matchLength;
          } else {
            if (t.getTstSiteId().equals(tstSiteId)) {
              result = t.getLogLvlCd();
              highMatchLength = matchLength;
            }
          }
        }
      }
    }
    }
    return result;
  }

  public Date getLastUpdated() {
    return lastUpdated;
  }

}
