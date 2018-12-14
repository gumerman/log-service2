package mil.osd.opa.ptd.log_service.service;

import java.io.Serializable;

import org.springframework.stereotype.Component;

/**
 * LogProperties object holds all server wide variables.
 * 
 * @author gumermrd
 *
 */
@Component("logProperties")
public class LogProperties implements Serializable {

  private static final long serialVersionUID = 1L;

  private transient Boolean dbLogging = null;
  private transient Boolean log4jLogging = null;

	public Boolean getLog4jLogging() {
		return log4jLogging;
	}

	public void setLog4jLogging(Boolean log4jLogging) {
		this.log4jLogging = log4jLogging;
	}

	public Boolean getDBLogging() {
		return dbLogging;
	}

	public void setDBLogging(Boolean dbLogging) {
		this.dbLogging = dbLogging;
	}

}

