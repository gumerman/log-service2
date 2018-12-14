package mil.osd.opa.ptd.log_service.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import mil.osd.opa.ptd.log_service.beans.ErrorLog;
import mil.osd.opa.ptd.log_service.mapper.LogMapper;
import mil.osd.opa.ptd.log_service.utils.WdlptEncode;
import mil.osd.opa.ptd.log_service.validator.BeanValidator;



/**
 * Service for all logging that is intended to be available in the application.
 * 
 * @author gumermrd
 * 
 */
@Service("logService")
public class LogService {

  private final Logger log = LoggerFactory.getLogger(this.getClass());
  @Autowired
  LogThresholdService logThreshold;
  @Autowired
  LogMapper logMapper;
  @Autowired
  LogWriterService logWriter;
  @Autowired
  LogProperties logProperties;
  @Autowired
  LookupService lookup;
  
  /**
   * Get the current bean context and return it to the calling class.
   * 
   * @return
   */
  public static LogService getLogger() {
    LogService logService;
//    if (!Constants.isUnitTesting()) {
//      WebApplicationContext ctx = ContextLoader.getCurrentWebApplicationContext();
//      logService = ctx.getBean(LogService.class);
//    } else {
    ApplicationContext ac = ApplicationContextProvider.getApplicationContext();
    logService = ac.getBean(LogService.class);
//    }
    return logService;
  }

  /**
   * Place holder for old Log4J implementation.
   * 
   */
  public boolean isDebugEnabled() {
    return true;
  }

  /**
   * Place holder for old Log4J implementation.
   * 
   */
  public boolean isInfoEnabled() {
    return true;
  }

  /**
   * Reset all class level properties when logout and start-up.
   * 
   */
  public void reset() {
 //     logParms.reset();
  }

  /**
   * @param String systemTest 
   *          tell service which log table to be inserted in to
   * @param dlptErrMsgId
   *          ID (DLPT_ERR_MSG.DLPT_ERR_MSG_ID)
   * @return single ErrorLog object if id matches
   */
  public ErrorLog getLogById(long dlptErrMsgId) {

    ErrorLog errorLog = null;
    dlptErrMsgId = BeanValidator.validateLongValue(dlptErrMsgId, "dlptErrMsgId");
    errorLog = logMapper.getLogById(dlptErrMsgId);
    return errorLog;
  }

  /**
   * Lookup all data from the log table.
   * 
   * @param String systemTest 
   *          tell service which log table to be inserted in to
   * @return all ErrorLog objects
   */
  public List<ErrorLog> getLogs(Long starting, Long pageSize) {
    List<ErrorLog> errorLogs = null;
    errorLogs = logMapper.getLogs(starting, pageSize);
    return errorLogs;
  }

  /**
   * Lookup current data from the log table.
   * 
   * @param String systemTest 
   *          tell service which log table to be inserted in to
   * @return all ErrorLog objects
   */
  public List<ErrorLog> getLastLogs() {
    List<ErrorLog> errorLogs = null;
    errorLogs = logMapper.getLastLogs(500L);
    return errorLogs;
  }

  /**
   * Just an initialization message.
   * 
   */
//  @PostConstruct
//  private void postInit() {
//    this.trace("0", "Hooray. Log service initialized!");
//  }
//
  /**
   * Method to test if the log statement should be ignored or not, based on the
   * value found by calling LogThreshold.getBestMatch(className)
   * 
   * @param statementLevel
   *          Level specified by log request.
   * @param thresholdLevel
   *          Current threshold from LOG_THRESHOLD table.
   * @return boolean depending on" comparison of threshold and loglevel in
   *         request
   */
  private boolean allowLogRequest(String statementLevel, String thresholdLevel) {
    boolean retVal = false;
    LOGLEVEL statement = LOGLEVEL.INFO;
    LOGLEVEL threshold = LOGLEVEL.WARN;
    try {
      statement = LOGLEVEL.valueOf(statementLevel);
    } catch (NumberFormatException e) {
      statement = LOGLEVEL.INFO;
    }
    ;
    try {
      threshold = LOGLEVEL.valueOf(thresholdLevel);
    } catch (NumberFormatException e) {
      threshold = LOGLEVEL.WARN;
    }
    ;

    int stmtVal = statement.getValue();
    int thrshldVal = threshold.getValue();

    if (thrshldVal <= stmtVal) {
      retVal = true;
    }

    return retVal;
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

  /**
   * Get current Stack Trace
   * @param throwable
   * @return
   */
  private StackTraceElement getWdlptStackTrace(Throwable throwable) {
    int maxdepth = 20;
    int depth = 0;
    StackTraceElement ret = null;
    StackTraceElement[] stes = throwable.getStackTrace();
    String curClass;
    for (StackTraceElement ste : stes) {
      depth += 1;
      if (depth > maxdepth) {
        break;
      }
      curClass = ste.getClassName();
      if (curClass.contains("mil.osd.opa.ptd")) {
        if (!curClass.contains("mil.osd.opa.ptd.dtat.service.LogService")) {
          ret = ste;
          break;
        }
      }
    }
    if (ret == null) {
      ret = throwable.getStackTrace()[3];
    }
    return ret;
  }

  /**
   * Get current printed Stack Trace
   * @param throwable
   * @return String
   */
  private String getStackTrace(Throwable throwable) {
    int maxdepth = 23;
    int depth = 0;
    boolean startExtracting = false;
    String ret = null;
    StackTraceElement[] stes = throwable.getStackTrace();
    String curClass;
    for (StackTraceElement ste : stes) {
      depth += 1;
      if (depth > maxdepth) {
        break;
      }
      curClass = ste.getClassName();
      if (curClass.contains("mil.osd.dmdc.ptd")) {
        if (!curClass.contains("mil.osd.opa.ptd.dtat.service.LogService")) {
          startExtracting = true;
        }
      }
      if (startExtracting) {
        ret = ret + ste.toString();
      }
    }
    if (ret == null) {
      ret = throwable.getStackTrace()[3].toString();
    }
    return ret;
  }

  /**
   * This method is private to ensure there are an equal number of "hops" from
   * all public calling methods, otherwise our automatic class and method name
   * retrieval magic won't work right!
   * 
   * @param String systemTest 
   *          tell service which log table to be inserted in to
   * @param errorLog log object to be inserted log object to be inserted
   */

  private void insert(ErrorLog errorLog) {
	  	
    Throwable throwable = null;
    if (errorLog.getSrcClsNm() == null) {
      throwable = new Throwable();
      String className = getWdlptStackTrace(throwable).getClassName();
      errorLog.setSrcClsNm(className);
    }
    Boolean isDBLogging = Boolean.TRUE;
    Boolean isLog4jLogging = Boolean.TRUE;
    
    if (logProperties!=null) {
	  isDBLogging = isDBLogging();
	  isLog4jLogging = isLog4jLogging();
    }
	
    // Log nothing if both flags FALSE
	if (!isDBLogging && !isLog4jLogging) {
		return;
	}
	
    // Check to see if this one should go to the log
    String threshold = logThreshold.getBestMatch(errorLog.getSrcClsNm()); // , getTstSiteId());

    if (allowLogRequest(errorLog.getLogLvlCd(), threshold)) {
      // Now fill out the rest of the errorLog object and publish the message
      if (errorLog.getSrcMthdNm() == null) {
        if (throwable == null) {
          throwable = new Throwable();
        }
        String methodName = getWdlptStackTrace(throwable).getMethodName();
        errorLog.setsrcMthdNm(methodName);
      }

      if (LOGLEVEL.valueOf(errorLog.getLogLvlCd()).getValue() >= 5) {
        if (errorLog.getExcThrwnTx() == null) {
          if (throwable == null) {
            throwable = new Throwable();
          }
          // Filter stack trace of this class and limit to 20 elements
          errorLog.setExcThrwnTx(getStackTrace(throwable));
        }
      }
  
      // if we get a message, append msg to exception throw column front
//      if (errorLog.getMessage() != null) {
//        String msgText = errorLog.getMessage();
//        String msgException = "";
//        if (errorLog.getExcThrwnTx() != null) {
//          msgException = msgText + " - " + errorLog.getExcThrwnTx();
//        } else {
//          msgException = msgText;
//        }
//        errorLog.setExcThrwnTx(msgException);
//      }
      // Use for debugging this class; comment out otherwise
  //    logger.info("systemTest:" + getSystemTest() + " " + errorLog.toString());

//      if ( Constants.DEBUGGING ) {
//        System.out.println(errorLog);
      
  	  if (isLog4jLogging) {
        logWriter.writeLog4J(errorLog);
  	  }

  	  if (isDBLogging) {
        logWriter.write(errorLog);
  	  }
    }
  }

  /**
   * all insertLog methods call private insert(), and act as an intermediary to
   * all the public logging methods.
   * 
   * @param String systemTest 
   *          tell service which log table to be inserted in to
   * @param errorLog log object to be inserted
   */
  private void insertLog(ErrorLog errorLog) {
    insert(errorLog);
  }

  /**
   * Format a row to insert into log table.
   * 
   * @param String systemTest 
   *          tell service which log table to be inserted in to
   * @param logLevel
   *          the level of this message (TRACE|DEBUG|INFO|WARN|ERROR|FATAL)
   * @param messageCd
   *          code for predefined message for this log entry
   */
  private void insertLog(String logLevel, String messageCd) {
    ErrorLog errorLog = new ErrorLog();
    // TODO: if MessageCd is a number, assume it is an ErrorCd, else it should
    // be assumed to be an
    // old log4j message. May backfire because a message may be a number, but it
    // is the best we
    // can do for now to convert from log4j more easily.
    if (messageCd != null) {
      try {
        Integer.parseInt(messageCd);

        errorLog = new ErrorLog().level(logLevel).messageCd(messageCd);
      } catch (NumberFormatException e) {
        errorLog = new ErrorLog().level(logLevel).messageCd("0").message(WdlptEncode.forXmlComment(messageCd));
      }
    }
    // ErrorLog errorLog = new ErrorLog().level(logLevel)
    // .messageCd(messageCd);
  
    insert(errorLog);
  }

  /**
   * Format a row to insert into log table.
   * 
   * @param String systemTest 
   *          tell service which log table to be inserted in to
   * @param logLevel
   *          the level of this message (TRACE|DEBUG|INFO|WARN|ERROR|FATAL)
   * @param messageCd
   *          code for predefined message for this log entry
   * @param message
   *          custom message for this log entry
   */
  private void insertLog(String logLevel, String messageCd, String message) {
    ErrorLog errorLog = new ErrorLog().level(logLevel).messageCd(messageCd).message(message);
    insert(errorLog);
  }

  /**
   * Format a row to insert into log table.
   * 
   * @param String systemTest 
   *          tell service which log table to be inserted in to
   * @param logLevel
   *          the level of this message (TRACE|DEBUG|INFO|WARN|ERROR|FATAL)
   * @param message
   *          custom message for this log entry
   * @param throwable
   *          Throwable, which will be used to getStackTrace and convert to
   *          string
   */
  private void insertLog(String logLevel, String messageCd, String message,
      Throwable throwable) {
    ErrorLog errorLog = new ErrorLog().level(logLevel).messageCd(messageCd).message(message)
        .exception(throwable);
    insert(errorLog);
  }

  /**
   * @param String systemTest 
   *          tell service which log table to be inserted in to
   * @param logLevel
   *          the level of this message (TRACE|DEBUG|INFO|WARN|ERROR|FATAL)
   * @param className
   *          the calling class name. Will be autopopulated if null.
   * @param methodName
   *          The calling method for this entry. Will be autopopulated if null.
   * @param messageCd
   *          code for predefined message for this log entry
   */
  private void insertLog(String logLevel, String className, String methodName,
      String messageCd) {

    ErrorLog errorLog = new ErrorLog().level(logLevel).className(className).method(methodName)
        .messageCd(messageCd);

    insert(errorLog);
  }

  /**
   * @param String systemTest 
   *          tell service which log table to be inserted in to
   * @param logLevel
   *          the level of this message (TRACE|DEBUG|INFO|WARN|ERROR|FATAL)
   * @param className
   *          the calling class name. Will be autopopulated if null.
   * @param methodName
   *          The calling method for this entry. Will be autopopulated if null.
   * @param messageCd
   *          code for predefined message for this log entry
   * @param message
   *          custom message for this log entry
   */
  private void insertLog(String logLevel, String className, String methodName,
      String messageCd, String message) {

    ErrorLog errorLog = new ErrorLog().level(logLevel).className(className).method(methodName)
        .messageCd(messageCd).message(message);

    insert(errorLog);
  }

  /**
   * @param String systemTest 
   *          tell service which log table to be inserted in to
   * @param logLevel
   *          the level of this message (TRACE|DEBUG|INFO|WARN|ERROR|FATAL)
   * @param className
   *          the calling class name. Will be autopopulated if null.
   * @param methodName
   *          The calling method for this entry. Will be autopopulated if null.
   * @param messageCd
   *          code for predefined message for this log entry
   * @param message
   *          custom message for this log entry
   * @param throwable
   *          Throwable, which will be used to getStackTrace and convert to
   *          string
   */
  private void insertLog(String logLevel, String className, String methodName,
      String messageCd, String message, Throwable throwable) {

    ErrorLog errorLog = new ErrorLog().level(logLevel).className(className).method(methodName)
        .messageCd(messageCd).message(message).exception(throwable);

    insert(errorLog);
  }

  /*
   * All flavors of the public log methods below will call the private insertLog
   * These are all essentially the same, just different signatures to match
   * those above.
   */

  /**
   * @param errorLog
   *          log object to be inserted
   */
  public void log(ErrorLog errorLog) {
    insertLog(errorLog);
  }

  /**
   * @param logLevel
   *          the level of this message (TRACE|DEBUG|INFO|WARN|ERROR|FATAL)
   * @param messageCd
   *          code for predefined message for this log entry
   */
  public void log(String logLevel, String messageCd) {

    insertLog(logLevel, messageCd);
  }

  /**
   * @param logLevel
   *          the level of this message (TRACE|DEBUG|INFO|WARN|ERROR|FATAL)
   * @param messageCd
   *          code for predefined message for this log entry
   * @param message
   *          custom message for this log entry
   */
  public void log(String logLevel, String messageCd, String message) {

    insertLog(logLevel, messageCd, message);
  }

  /**
   * @param logLevel
   *          the level of this message (TRACE|DEBUG|INFO|WARN|ERROR|FATAL)
   * @param messageCd
   *          code for predefined message for this log entry
   * @param message
   *          custom message for this log entry
   * @param throwable
   *          Throwable, which will be used to getStackTrace and convert to
   *          string
   */
  public void log(String logLevel, String messageCd, String message, Throwable throwable) {

    insertLog(logLevel, messageCd, message, throwable);
  }

  /**
   * @param logLevel
   *          the level of this message (TRACE|DEBUG|INFO|WARN|ERROR|FATAL)
   * @param className
   *          the calling class name. Will be autopopulated if null.
   * @param methodName
   *          The calling method for this entry. Will be autopopulated if null.
   * @param messageCd
   *          code for predefined message for this log entry
   */
  public void log(String logLevel, String className, String methodName, String messageCd) {

    insertLog(logLevel, className, methodName, messageCd);
  }

  /**
   * @param logLevel
   *          the level of this message (TRACE|DEBUG|INFO|WARN|ERROR|FATAL)
   * @param className
   *          the calling class name. Will be autopopulated if null.
   * @param methodName
   *          The calling method for this entry. Will be autopopulated if null.
   * @param messageCd
   *          code for predefined message for this log entry
   * @param message
   *          custom message for this log entry
   */
  public void log(String logLevel, String className, String methodName, String messageCd,
      String message) {

    insertLog(logLevel, className, methodName, messageCd, message);
  }

  /**
   * @param logLevel
   *          the level of this message (TRACE|DEBUG|INFO|WARN|ERROR|FATAL)
   * @param className
   *          the calling class name. Will be autopopulated if null.
   * @param methodName
   *          The calling method for this entry. Will be autopopulated if null.
   * @param messageCd
   *          code for predefined message for this log entry
   * @param message
   *          custom message for this log entry
   * @param throwable
   *          Throwable, which will be used to getStackTrace and convert to
   *          string
   */
  public void log(String logLevel, String className, String methodName, String messageCd,
      String message, Throwable throwable) {

    insertLog(logLevel, className, methodName, messageCd, message, throwable);
  }

  /**
   * @param errorLog
   *          log object to be inserted
   */
  public void trace(ErrorLog errorLog) {
    String logLevel = "TRACE";
    errorLog.setLogLvlCd(logLevel);
    insertLog(errorLog);
  }

  /**
   * @param messageCd
   *          code for predefined message for this log entry
   */
  public void trace(String messageCd) {
    String logLevel = "TRACE";
    insertLog(logLevel, messageCd);
  }

  /**
   * Entry point for OLD Log4J calls that have not been converted
   * 
   * @param message
   *          for predefined message for this log entry
   * @param throwable
   *          Throwable, which will be used to getStackTrace and convert to
   *          string
   */
  public void trace(String message, Throwable throwable) {
    String logLevel = "TRACE";
    insertLog(logLevel, "0", message, throwable);
  }

  /**
   * @param messageCd
   *          code for predefined message for this log entry
   * @param message
   *          custom message for this log entry
   */
  public void trace(String messageCd, String message) {
    String logLevel = "TRACE";
    insertLog(logLevel, messageCd, message);
  }

  /**
   * @param messageCd
   *          code for predefined message for this log entry
   * @param message
   *          custom message for this log entry
   * @param throwable
   *          Throwable, which will be used to getStackTrace and convert to
   *          string
   */
  public void trace(String messageCd, String message, Throwable throwable) {
    String logLevel = "TRACE";
    insertLog(logLevel, messageCd, message, throwable);
  }

  /**
   * @param className
   *          the calling class name. Will be autopopulated if null.
   * @param methodName
   *          The calling method for this entry. Will be autopopulated if null.
   * @param testOperatorSessionId
   *          the testOperatorSessionId associated with this entry.
   * @param resultId
   *          the WDLPT test result associated with this entry.
   * @param messageCd
   *          code for predefined message for this log entry
   */
  public void trace(String className, String methodName, String messageCd) {
    String logLevel = "TRACE";
    insertLog(logLevel, className, methodName, messageCd);
  }

  /**
   * @param className
   *          the calling class name. Will be autopopulated if null.
   * @param methodName
   *          The calling method for this entry. Will be autopopulated if null.
   * @param messageCd
   *          code for predefined message for this log entry
   * @param message
   *          custom message for this log entry
   */
  public void trace(String className, String methodName, String messageCd, String message) {
    String logLevel = "TRACE";
    insertLog(logLevel, className, methodName, messageCd, message);
  }

  /**
   * @param className
   *          the calling class name. Will be autopopulated if null.
   * @param methodName
   *          The calling method for this entry. Will be autopopulated if null.
   * @param messageCd
   *          code for predefined message for this log entry
   * @param message
   *          custom message for this log entry
   * @param throwable
   *          Throwable, which will be used to getStackTrace and convert to
   *          string
   */
  public void trace(String className, String methodName, String messageCd, String message,
      Throwable throwable) {
    String logLevel = "TRACE";
    insertLog(logLevel, className, methodName, messageCd, message, throwable);
  }

  /**
   * @param errorLog
   *          log object to be inserted
   */
  public void debug(ErrorLog errorLog) {
    String logLevel = "DEBUG";
    errorLog.setLogLvlCd(logLevel);
    insertLog(errorLog);
  }

  /**
   * @param messageCd
   *          code for predefined message for this log entry
   */
  public void debug(String messageCd) {
    String logLevel = "DEBUG";
    insertLog(logLevel, messageCd);
  }

  /**
   * Entry point for OLD Log4J calls that have not been converted
   * 
   * @param message
   *          for predefined message for this log entry
   * @param throwable
   *          Throwable, which will be used to getStackTrace and convert to
   *          string
   */
  public void debug(String message, Throwable throwable) {
    String logLevel = "DEBUG";
    insertLog(logLevel, "0", message, throwable);
  }

  /**
   * @param messageCd
   *          code for predefined message for this log entry
   * @param message
   *          custom message for this log entry
   */
  public void debug(String messageCd, String message) {
    String logLevel = "DEBUG";
    insertLog(logLevel, messageCd, message);
  }

  /**
   * @param messageCd
   *          code for predefined message for this log entry
   * @param message
   *          custom message for this log entry
   * @param throwable
   *          Throwable, which will be used to getStackTrace and convert to
   *          string
   */
  public void debug(String messageCd, String message, Throwable throwable) {
    String logLevel = "DEBUG";
    insertLog(logLevel, messageCd, message, throwable);
  }

  /**
   * @param className
   *          the calling class name. Will be autopopulated if null.
   * @param methodName
   *          The calling method for this entry. Will be autopopulated if null.
   * @param messageCd
   *          code for predefined message for this log entry
   */
  public void debug(String className, String methodName, String messageCd) {
    String logLevel = "DEBUG";
    insertLog(logLevel, className, methodName, messageCd);
  }

  /**
   * @param className
   *          the calling class name. Will be autopopulated if null.
   * @param methodName
   *          The calling method for this entry. Will be autopopulated if null.
   * @param messageCd
   *          code for predefined message for this log entry
   * @param message
   *          custom message for this log entry
   */
  public void debug(String className, String methodName, String messageCd, String message) {
    String logLevel = "DEBUG";
    insertLog(logLevel, className, methodName, messageCd, message);
  }

  /**
   * @param className
   *          the calling class name. Will be autopopulated if null.
   * @param methodName
   *          The calling method for this entry. Will be autopopulated if null.
   * @param messageCd
   *          code for predefined message for this log entry
   * @param message
   *          custom message for this log entry
   * @param throwable
   *          Throwable, which will be used to getStackTrace and convert to
   *          string
   */
  public void debug(String className, String methodName, String messageCd, String message,
      Throwable throwable) {
    String logLevel = "DEBUG";
    insertLog(logLevel, className, methodName, messageCd, message, throwable);
  }

  /**
   * @param errorLog
   *          log object to be inserted
   */
  public void info(ErrorLog errorLog) {
    String logLevel = "INFO";
    errorLog.setLogLvlCd(logLevel);
    insertLog(errorLog);
  }

  /**
   * @param messageCd
   *          code for predefined message for this log entry
   */
  public void info(String messageCd) {
    String logLevel = "INFO";
    insertLog(logLevel, messageCd);
  }

  /**
   * Entry point for OLD Log4J calls that have not been converted
   * 
   * @param message
   *          for predefined message for this log entry
   * @param throwable
   *          Throwable, which will be used to getStackTrace and convert to
   *          string
   */
  public void info(String message, Throwable throwable) {
    String logLevel = "INFO";
    insertLog(logLevel, "0", message, throwable);
  }

  /**
   * @param messageCd
   *          code for predefined message for this log entry
   * @param message
   *          custom message for this log entry
   */
  public void info(String messageCd, String message) {
    String logLevel = "INFO";
    insertLog(logLevel, messageCd, message);
  }

  /**
   * @param messageCd
   *          code for predefined message for this log entry
   * @param message
   *          custom message for this log entry
   * @param throwable
   *          Throwable, which will be used to getStackTrace and convert to
   *          string
   */
  public void info(String messageCd, String message, Throwable throwable) {
    String logLevel = "INFO";
    insertLog(logLevel, messageCd, message, throwable);
  }

  /**
   * @param className
   *          the calling class name. Will be autopopulated if null.
   * @param methodName
   *          The calling method for this entry. Will be autopopulated if null.
   * @param messageCd
   *          code for predefined message for this log entry
   */
  public void info(String className, String methodName, String messageCd) {
    String logLevel = "INFO";
    insertLog(logLevel, className, methodName, messageCd);
  }

  /**
   * @param className
   *          the calling class name. Will be autopopulated if null.
   * @param methodName
   *          The calling method for this entry. Will be autopopulated if null.
   * @param messageCd
   *          code for predefined message for this log entry
   * @param message
   *          custom message for this log entry
   */
  public void info(String className, String methodName, String messageCd, String message) {
    String logLevel = "INFO";
    insertLog(logLevel, className, methodName, messageCd, message);
  }

  /**
   * @param className
   *          the calling class name. Will be autopopulated if null.
   * @param methodName
   *          The calling method for this entry. Will be autopopulated if null.
   * @param messageCd
   *          code for predefined message for this log entry
   * @param message
   *          custom message for this log entry
   * @param throwable
   *          Throwable, which will be used to getStackTrace and convert to
   *          string
   */
  public void info(String className, String methodName, String messageCd, String message,
      Throwable throwable) {
    String logLevel = "INFO";
    insertLog(logLevel, className, methodName, messageCd, message, throwable);
  }

  /**
   * @param errorLog
   *          log object to be inserted
   */
  public void warn(ErrorLog errorLog) {
    String logLevel = "WARN";
    errorLog.setLogLvlCd(logLevel);
    insertLog(errorLog);
  }

  /**
   * @param messageCd
   *          code for predefined message for this log entry
   */
  public void warn(String messageCd) {
    String logLevel = "WARN";
    insertLog(logLevel, messageCd);
  }

  /**
   * Entry point for OLD Log4J calls that have not been converted
   * 
   * @param message
   *          for predefined message for this log entry
   * @param throwable
   *          Throwable, which will be used to getStackTrace and convert to
   *          string
   */
  public void warn(String message, Throwable throwable) {
    String logLevel = "WARN";
    insertLog(logLevel, "0", message, throwable);
  }

  /**
   * @param messageCd
   *          code for predefined message for this log entry
   * @param message
   *          custom message for this log entry
   */
  public void warn(String messageCd, String message) {
    String logLevel = "WARN";
    insertLog(logLevel, messageCd, message);
  }

  /**
   * @param messageCd
   *          code for predefined message for this log entry
   * @param message
   *          custom message for this log entry
   * @param throwable
   *          Throwable, which will be used to getStackTrace and convert to
   *          string
   */
  public void warn(String messageCd, String message, Throwable throwable) {
    String logLevel = "WARN";
    insertLog(logLevel, messageCd, message, throwable);
  }

  /**
   * @param className
   *          the calling class name. Will be autopopulated if null.
   * @param methodName
   *          The calling method for this entry. Will be autopopulated if null.
   * @param messageCd
   *          code for predefined message for this log entry
   */
  public void warn(String className, String methodName, String messageCd) {
    String logLevel = "WARN";
    insertLog(logLevel, className, methodName, messageCd);
  }

  /**
   * @param className
   *          the calling class name. Will be autopopulated if null.
   * @param methodName
   *          The calling method for this entry. Will be autopopulated if null.
   * @param messageCd
   *          code for predefined message for this log entry
   * @param message
   *          custom message for this log entry
   */
  public void warn(String className, String methodName, String messageCd, String message) {
    String logLevel = "WARN";
    insertLog(logLevel, className, methodName, messageCd, message);
  }

  /**
   * @param className
   *          the calling class name. Will be autopopulated if null.
   * @param methodName
   *          The calling method for this entry. Will be autopopulated if null.
   * @param messageCd
   *          code for predefined message for this log entry
   * @param message
   *          custom message for this log entry
   * @param throwable
   *          Throwable, which will be used to getStackTrace and convert to
   *          string
   */
  public void warn(String className, String methodName, String messageCd, String message,
      Throwable throwable) {
    String logLevel = "WARN";
    insertLog(logLevel, className, methodName, messageCd, message, throwable);
  }

  /**
   * @param errorLog
   *          log object to be inserted
   */
  public void error(ErrorLog errorLog) {
    String logLevel = "ERROR";
    errorLog.setLogLvlCd(logLevel);
    insertLog(errorLog);
  }

  /**
   * @param message
   *          custom message for this log entry
   */
  public void error(String messageCd) {
    String logLevel = "ERROR";
    insertLog(logLevel, messageCd);
  }

  /**
   * Entry point for OLD Log4J calls that have not been converted
   * 
   * @param message
   *          for predefined message for this log entry
   * @param throwable
   *          Throwable, which will be used to getStackTrace and convert to
   *          string
   */
  public void error(String message, Throwable throwable) {
    String logLevel = "ERROR";
    insertLog(logLevel, "0", message, throwable);
  }

  /**
   * Logs a message object with the {@link Level#ERROR ERROR} level.
   *
   * @param message
   *          the message object to log.
   */
  @Deprecated
  public void error(Object message) {
    String logLevel = "ERROR";
    insertLog(logLevel, "0", message.toString());
  }

  /**
   * @param messageCd
   *          code for predefined message for this log entry
   * @param message
   *          custom message for this log entry
   */
  public void error(String messageCd, String message) {
    String logLevel = "ERROR";
    insertLog(logLevel, messageCd, message);
  }

  /**
   * @param messageCd
   *          code for predefined message for this log entry
   * @param message
   *          custom message for this log entry
   * @param throwable
   *          Throwable, which will be used to getStackTrace and convert to
   *          string
   */
  public void error(String messageCd, String message, Throwable throwable) {
    String logLevel = "ERROR";
    insertLog(logLevel, messageCd, WdlptEncode.forHtmlContent(message), throwable);
  }

  /**
   * @param className
   *          the calling class name. Will be autopopulated if null.
   * @param methodName
   *          The calling method for this entry. Will be autopopulated if null.
   * @param messageCd
   *          code for predefined message for this log entry
   */
  public void error(String className, String methodName, String messageCd) {
    String logLevel = "ERROR";
    insertLog(logLevel, className, methodName, messageCd);
  }

  /**
   * @param className
   *          the calling class name. Will be autopopulated if null.
   * @param methodName
   *          The calling method for this entry. Will be autopopulated if null.
   * @param messageCd
   *          code for predefined message for this log entry
   * @param message
   *          custom message for this log entry
   */
  public void error(String className, String methodName, String messageCd, String message) {
    String logLevel = "ERROR";
    insertLog(logLevel, className, methodName, messageCd, message);
  }

  /**
   * @param className
   *          the calling class name. Will be autopopulated if null.
   * @param methodName
   *          The calling method for this entry. Will be autopopulated if null.
   * @param messageCd
   *          code for predefined message for this log entry
   * @param message
   *          custom message for this log entry
   * @param throwable
   *          Throwable, which will be used to getStackTrace and convert to
   *          string
   */
  public void error(String className, String methodName, String messageCd, String message,
      Throwable throwable) {
    String logLevel = "ERROR";
    insertLog(logLevel, className, methodName, messageCd, message, throwable);
  }

  /**
   * @param errorLog
   *          log object to be inserted
   */
  public void fatal(ErrorLog errorLog) {
    String logLevel = "FATAL";
    errorLog.setLogLvlCd(logLevel);
    insertLog(errorLog);
  }

  /**
   * @param messageCd
   *          code for predefined message for this log entry
   */
  public void fatal(String messageCd) {
    String logLevel = "FATAL";
    insertLog(logLevel, messageCd);
  }

  /**
   * Entry point for OLD Log4J calls that have not been converted
   * 
   * @param message
   *          for predefined message for this log entry
   * @param throwable
   *          Throwable, which will be used to getStackTrace and convert to
   *          string
   */
  public void fatal(String message, Throwable throwable) {
    String logLevel = "FATAL";
    insertLog(logLevel, "0", message, throwable);
  }

  /**
   * @param messageCd
   *          code for predefined message for this log entry
   * @param message
   *          custom message for this log entry
   */
  public void fatal(String messageCd, String message) {
    String logLevel = "FATAL";
    insertLog(logLevel, messageCd, message);
  }

  /**
   * @param messageCd
   *          code for predefined message for this log entry
   * @param message
   *          custom message for this log entry
   * @param throwable
   *          Throwable, which will be used to getStackTrace and convert to
   *          string
   */
  public void fatal(String messageCd, String message, Throwable throwable) {
    String logLevel = "FATAL";
    insertLog(logLevel, messageCd, message, throwable);
  }

  /**
   * @param className
   *          the calling class name. Will be autopopulated if null.
   * @param methodName
   *          The calling method for this entry. Will be autopopulated if null.
   * @param messageCd
   *          code for predefined message for this log entry
   */
  public void fatal(String className, String methodName, String messageCd) {
    String logLevel = "FATAL";
    insertLog(logLevel, className, methodName, messageCd);
  }

  /**
   * @param className
   *          the calling class name. Will be autopopulated if null.
   * @param methodName
   *          The calling method for this entry. Will be autopopulated if null.
   * @param messageCd
   *          code for predefined message for this log entry
   * @param message
   *          custom message for this log entry
   */
  public void fatal(String className, String methodName, String messageCd, String message) {
    String logLevel = "FATAL";
    insertLog(logLevel, className, methodName, messageCd, message);
  }

  /**
   * @param className
   *          the calling class name. Will be autopopulated if null.
   * @param methodName
   *          The calling method for this entry. Will be autopopulated if null.
   * @param messageCd
   *          code for predefined message for this log entry
   * @param message
   *          custom message for this log entry
   * @param throwable
   *          Throwable, which will be used to getStackTrace and convert to
   *          string
   */
  public void fatal(String className, String methodName, String messageCd, String message,
      Throwable throwable) {
    String logLevel = "FATAL";
    insertLog(logLevel, className, methodName, messageCd, message, throwable);
  }

//  /**
//   *  Session-based log variables. They will be used when not passed in.
//   * @return
//   */
//  private String getSystemTest() {
//    // trap an error just in case we do not have a valid session open
//    String systemTest = "";
//    try {
//      if (logParms != null) {
//        systemTest = logParms.getSystemTest();
//      }
//    } catch (BeanCreationException e) {
//      systemTest = "";
//    }
//    return systemTest;
//  }
//
//  private Integer getTestOperatorSessionId() {
//    // trap an error just in case we do not have a valid session open
//    Integer testOperatorSessionId = null;
//    try {
//      testOperatorSessionId = logParms.getTestOperatorSessionId();
//    } catch (BeanCreationException e) {
//      testOperatorSessionId = null;
//    }
//    return testOperatorSessionId;
//  }
//
//  private Long getResultId() {
//    // trap an error just in case we do not have a valid session open
//    Long resultId = null;
//    try {
//      resultId = logParms.getResultId();
//    } catch (BeanCreationException e) {
//      resultId = null;
//    }
//    return resultId;
//  }
//
//  private Long getSubtestId() {
//    // trap an error just in case we do not have a valid session open
//    Long subtestId = null;
//    try {
//      subtestId = logParms.getSubtestId();
//    } catch (BeanCreationException e) {
//      subtestId = null;
//    }
//    return subtestId;
//  }
//
//  private Integer getPassageId() {
//    // trap an error just in case we do not have a valid session open
//    Integer passageId = null;
//    try {
//      passageId = logParms.getPassageId();
//    } catch (BeanCreationException e) {
//      passageId = null;
//    }
//    return passageId;
//  }
//
//  private String getTstSiteId() {
//    // trap an error just in case we do not have a valid session open
//    String tstSiteId = null;
//    try {
//      tstSiteId = logParms.getTstSiteId();
//    } catch (BeanCreationException e) {
//      tstSiteId = null;
//    }
//    return tstSiteId;
//  }
//
//  public void setSystemTest(String systemTest) {
//    logParms.setSystemTest(systemTest);
//    logger.info("systemTest: " + logParms.getSystemTest());
//    if (!logParms.isValid()) {};
//  } 
//
//  public void setTestOperatorSessionId(Integer testOperatorSessionId) {
//    logParms.setTestOperatorSessionId(testOperatorSessionId);
//  }
//
//  public void setResultId(Long resultId) {
//    logParms.setResultId(resultId);
//  }
//
//  public void setSubtestId(Long subtestId) {
//    logParms.setSubtestId(subtestId);
//  }
//
//  public void setPassageId(Integer passageId) {
//    logParms.setPassageId(passageId);
//  }
//
//  public void setTstSiteId(String tstSiteId) {
//    logParms.setTstSiteId(tstSiteId);
//  }
//
  public Boolean isDBLogging() {
    try {
      if (logProperties.getDBLogging() == null) {
          logProperties.setDBLogging(lookupDBLogging());
      }
      return logProperties.getDBLogging();
    } catch  (BeanCreationException e) {
      return true;
    }
  }

  public Boolean lookupDBLogging() {
    Boolean a = Boolean.TRUE;
    String value = lookup.getDBLogging();
    if (value != null && value.equals("N")) {
      a = Boolean.FALSE;
    }
    return a;
  }

  public Boolean isLog4jLogging() {
    try {
      if (logProperties.getLog4jLogging() == null) {
          logProperties.setLog4jLogging(lookupLog4jLogging());
      }
      return logProperties.getLog4jLogging();
    } catch  (BeanCreationException e) {
      return true;
    }
  }

  public Boolean lookupLog4jLogging() {
    Boolean a = Boolean.TRUE;
    String value = lookup.getlog4jLogging();
    if (value != null && value.equals("N")) {
      a = Boolean.FALSE;
    }
    return a;
  }

}
