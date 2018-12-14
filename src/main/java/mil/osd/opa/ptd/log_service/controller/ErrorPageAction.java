package mil.osd.opa.ptd.log_service.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import mil.osd.opa.ptd.log_service.service.LogService;

/**
 * This class logs uncaught and thrown exceptions
 */
@RestController
public class ErrorPageAction  {
  @Autowired
  LogService log;
  private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
  @RequestMapping("/errordisplay")
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public @ResponseBody void execute(HttpServletRequest request,
                               		HttpServletResponse response) {

    //log the error then exit. fixes fortify issues with printStackTrace.
    Throwable throwable = null;
    if (request.getAttribute("javax.servlet.error.exception") != null) {
    	throwable = (Throwable) request.getAttribute("javax.servlet.error.exception");
    };
    
	Integer statusCode = (Integer) request
			.getAttribute("javax.servlet.error.status_code");
	String servletName = (String) request
			.getAttribute("javax.servlet.error.servlet_name");
	if (servletName == null) {
		servletName = "Unknown";
	}
	String requestUri = (String) request
			.getAttribute("javax.servlet.error.request_uri");
	if (requestUri == null) {
		requestUri = "Unknown";
	}    

	String className = "";
	String msg = "";
	String stackTrace = "";
	if (throwable != null) {
		className = throwable.getClass().getName();
		msg = throwable.getMessage();
	    stackTrace = throwable.getStackTrace().toString();
	}
	
    String errorMessage = "Exception Details: Code:"+statusCode+ 
    		" Servlet Name:"+servletName+
    		" Exception Name:"+className+
    		" Requested URI:"+requestUri+
    		" Exception Message:"+msg+
    		" Trace: " + stackTrace;
    
    logger.error("Uncaught exception directed to errorPage action."+errorMessage);
    if (log != null) {
    	log.error("Uncaught exception directed to errorPage action."+errorMessage);
    }
    // return new ModelAndView("genericError");   
  }
  @RequestMapping("/errordisplay404")
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public @ResponseBody void execute404(HttpServletRequest request,
                               		   HttpServletResponse response) {

    //log the error then exit. fixes fortify issues with printStackTrace.
    Throwable throwable = (Throwable)request.getAttribute("javax.servlet.error.exception");
    
    String errorMessage = "";
    if (throwable!=null) {
    	errorMessage = "msg: " + throwable.getMessage() + " Trace: " + throwable.getStackTrace();
    } 
    
    // If we get the jquery.min.map URL then Developer Tools is open.  This is not really an error we care about.
    Object errPage = request.getAttribute("javax.servlet.forward.request_uri");
    if (errPage != null) {
      if (((String) errPage).equals("/dtat/js/jquery.min.map")) {
        return;
      }
    }
    Object errPage2 = request.getAttribute("javax.servlet.error.request_uri");
    if (errPage2 != null) {
      if (((String) errPage2).equals("/dtat/js/jquery.min.map")) {
        return;
      }
    }
    
    logger.error("404 Uncaught exception directed to errorPage action." + errorMessage + " URL1: " + (String) errPage + " URL2: " + (String) errPage2);
    if (log != null) {
    	log.error("404 Uncaught exception directed to errorPage action." + errorMessage + " URL1: " + (String) errPage + " URL2: " + (String) errPage2);
    }

//    return new ModelAndView("genericError");   
  }
  @RequestMapping("/errordisplay500")
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public @ResponseBody void execute500(HttpServletRequest request,
                               		   HttpServletResponse response) {

    //log the error then exit. fixes fortify issues with printStackTrace.
    Throwable throwable = (Throwable)request.getAttribute("javax.servlet.error.exception");
    
	String servletName = (String) request
			.getAttribute("javax.servlet.error.servlet_name");
	if (servletName == null) {
		servletName = "Unknown";
	}
	String requestUri = (String) request
			.getAttribute("javax.servlet.error.request_uri");
	if (requestUri == null) {
		requestUri = "Unknown";
	}    

	String className = "";
	String msg = "";
	String stackTrace = "";
	if (throwable != null) {
		className = throwable.getClass().getName();
		msg = throwable.getMessage();
	    stackTrace = throwable.getStackTrace().toString();
	}
	
    String errorMessage = "Exception Details: Servlet Name:"+servletName+
    		" Exception Name:"+className+
    		" Requested URI:"+requestUri+
    		" Exception Message:"+msg+
    		" Trace: " + stackTrace;

    logger.error("500 Uncaught exception directed to errorPage action."+errorMessage);
    if (log != null) {
    	log.error("500 Uncaught exception directed to errorPage action."+errorMessage);
    }
//    return new ModelAndView("genericError");   
  }
}
