package mil.osd.opa.ptd.log_service.service;

import java.util.HashSet;

import javax.annotation.PostConstruct;

import org.apache.ibatis.exceptions.PersistenceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import mil.osd.opa.ptd.log_service.beans.AppProperties;
import mil.osd.opa.ptd.log_service.mapper.LookupMapper;

//

/**
 * Looks up keys and values from the LKUP_KEY_VAL table. The LKUP_KEY_VAL
 * table controls application behavior.
 */
@Component
@Scope("prototype")
public class LookupService {
  @Autowired
  protected  LogService logger;
  @Autowired
  AppProperties appProperties;
  @Autowired
  LookupMapper lookupMapper;

  @SuppressWarnings("unused")
  private static HashSet securityNoAuthSet = null;
  private String region = ""; //appProperties.getRegion();

  /**
   * Initialization.
   * 
   */
  @PostConstruct
  private void postInit() {
    region = appProperties.getRegion();
  }

  /**
   * @return String containing a Y or N string showing if log4j should be enabled in the app.
   */
  public String getlog4jLogging(){
      return getValue("WDLPT", "LOG4J_LOGGING");
  }
  /**
   * @return String containing a Y or N string showing if the custom database logging should be enabled in the app.
   */
  public String getDBLogging(){
      return getValue("WDLPT", "ACES_LOGGING");
     }
  
  @Transactional
  public String getValue(String domain, String key) {
      String value = null;
      try {
//          value = service.selectLookupValue(region, domain, key);
          value = lookupMapper.selectLookupValue(region, domain, key);
      } catch (PersistenceException e) {
        String errorMessage = "Error 1410, Error getting records from lookup table.";
        logger.error(errorMessage + " domain: " + domain + " key: " + key + e.getCause(), e);
      } catch (Exception e) {
          String errorMessage = "Error 1410, Error getting records from lookup table.";
          logger.error(errorMessage +" domain: "+domain+" key: "+key, e);
          throw e;
      }

      return value;
    }
}
