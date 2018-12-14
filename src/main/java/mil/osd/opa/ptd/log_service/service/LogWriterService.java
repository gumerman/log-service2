package mil.osd.opa.ptd.log_service.service;

import java.net.InetAddress;
import java.net.UnknownHostException;

import mil.osd.opa.ptd.log_service.service.LogService;
import mil.osd.opa.ptd.log_service.utils.WdlptEncode;
import mil.osd.opa.ptd.log_service.mapper.LogMapper;
import mil.osd.opa.ptd.log_service.beans.ErrorLog;

import org.apache.ibatis.exceptions.PersistenceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service to keep the actual writing of the log in a separate class, so we can
 * consistently set the info gleaned from the stacktrace in Log.java, but still
 * configure the transaction propagation so logs don't get rolled back!
 * 
 * @author gumermrd
 * 
 */
@Service("logWriter")
@Scope("prototype")
public class LogWriterService {

  private final Logger logger = LoggerFactory.getLogger(this.getClass());
  
  @Autowired
  LogMapper logMapper;

  private static String hostName = "UNKNOWN";

  static {
    try {
      hostName = InetAddress.getLocalHost().getCanonicalHostName();
    } catch (UnknownHostException e) {
      hostName = "NOT-FOUND";
    }

  }

  /**
   * enum to rank threshold values so we can choose what to ignore in
   * allowLogRequest().
   * 
   */
  private enum LOGLEVEL {
    TRACE(1), DEBUG(2), INFO(3), WARN(4), ERROR(5), FATAL(6);

    private int value;

    LOGLEVEL(int value) {
      this.value = value;
    }

    private int getValue() {
      return value;
    }

  }
  
  @Transactional(propagation = Propagation.NOT_SUPPORTED)
  public void write(final ErrorLog errorLog) {
      write("", errorLog);
  }
  
  @Transactional(propagation = Propagation.NOT_SUPPORTED)
  public void write(final String systemTest, final ErrorLog errorLog) {
    if (errorLog.getHostNm() == null && errorLog.getHostNm().equals("")) {
        errorLog.setHostNm(hostName);
    }
    if (systemTest != null) {
      if (systemTest.equals("") || systemTest.equals("TEST")) {
        try {
          logMapper.insertLog(errorLog);
        } catch (PersistenceException e) {
          errorLog.setDlptErrMsgTx("ERROR writing to DB log: " + e.getMessage() + " - " + errorLog.getDlptErrMsgTx());
          writeLog4J(systemTest, errorLog);
        } catch (Exception e) {  // just in case something unexpected happens, send it to log4J
          errorLog.setDlptErrMsgTx("ERROR writing to DB log: " + e.getMessage() + " - " + errorLog.getDlptErrMsgTx());
          writeLog4J(systemTest, errorLog);
        }
      }
    }
  }
  
  @Transactional(propagation = Propagation.NOT_SUPPORTED)
  public void writeLog4J(final ErrorLog errorLog) {
      writeLog4J("", errorLog);
  }

  @Transactional(propagation = Propagation.NOT_SUPPORTED)
  public void writeLog4J(final String systemTest, final ErrorLog errorLog) {
    try {
      String errMsg = WdlptEncode.forHtmlContent(errorLog.toLog4J(systemTest));
      switch (LOGLEVEL.valueOf(errorLog.getLogLvlCd()).getValue()) {
      case 1: // TRACE 
        logger.trace(errMsg);
        break;
      case 2: // DEBUG 
        logger.debug(errMsg);
        break;
      case 3: // INFO 
        logger.info(errMsg);
        break;
      case 4: // WARN 
        logger.warn(errMsg);
        break;
      case 5: // ERROR 
        logger.error(errMsg);
        break;
      case 6: // FATAL 
        logger.error(errMsg);
      default:
        try {
          logger.error("Not expecting level: " + LOGLEVEL.valueOf(errorLog.getLogLvlCd()).getValue());
        } catch (Exception e) {
          logger.error("Error during logging to Log4J! Values not valid causing an error",e);
        }
      }
    } catch (Exception e) {
      logger.error("Error during logging to Log4J! Values not valid causing an error",e);
    }
  }

}
