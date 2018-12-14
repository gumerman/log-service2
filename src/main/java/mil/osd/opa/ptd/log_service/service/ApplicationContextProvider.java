package mil.osd.opa.ptd.log_service.service;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * ApplicationContextProvider is a spring bean that is ApplicationContextAware upon the 
 * completion object creation the application context is injected to the object.  This 
 * allows non spring class to gain access to spring beans for execution.  In general, this
 * is not a good design  The whole application should be spring aware however this mechanism 
 * has allowed wDlpt years of use of Spring / myBatis use with Struts frame work. 
 *  
 * @author Dave Springer 
 *
 */
@Component
public class ApplicationContextProvider implements ApplicationContextAware {

  private static ApplicationContext ctx = null;

  public static ApplicationContext getApplicationContext() {
    return ctx;
  }

  /**
   * Find Bugs calls this an issue,  ST_WRITE_TO_STATIC_FROM_INSTANCE_METHOD !
   * The concern is the value ctx can be modified by multiple instances and 
   * cause some different values for different classes.  This however is not an 
   * issue, spring will load this class on startup.  All other classes access 
   * ctx via getApplicationContext() method.
   * 
   * 
   */
  public void setApplicationContext(ApplicationContext ctx) throws BeansException {
    ApplicationContextProvider.ctx =  ctx;
  }
 
  // Use for JUnit tests
  public static void setNewApplicationContext(ApplicationContext actx) throws BeansException {   
    ctx = actx;
  }

  /**
   * After utilizing the get Bean from application context too much, I am redoing it
   * it use this short hand here in ApplicationContextProvider.
   * 
   * 
   * @param ClassName.class
   * @return Object pulled from the appContext.
   * 
   */
  public static <T extends Object> T getBean(Class<T> type) {
	    return ctx.getBean(type);
	}
  /**
   * Similar to the above but when the class is part of an hierarchy, spring
   * find multiple beans that match the class.  Also bean name isn't enough either.
   * This method get the list of beans for the type, then finds the bean name in that list.
   *  
   * 
   * @param ClassName.class
   * @param The name of the bean to retrieve from the application context.
   * @return Object pulled from the appContext.
   * 
   */
  public static  <T extends Object> T getBeanInHierarchy(Class<T> type, String beanName) {
	    return ctx.getBean(beanName,type);
	}

  
}