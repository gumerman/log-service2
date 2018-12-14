package mil.osd.opa.ptd.log_service.validator;

import java.math.BigDecimal;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.groups.Default;

import org.springframework.stereotype.Component;

import mil.osd.opa.ptd.log_service.utils.WdlptEncode;
import mil.osd.opa.ptd.log_service.service.ApplicationContextProvider;
import mil.osd.opa.ptd.log_service.service.LogService;

/**
 * Utility class for running bean validation for an object. 
 * 
 * 
 * @author gumermrd
 *
 */
@Component
public class BeanValidator {
  private static LogService logger =  LogService.getLogger(); //static Logger logger =  Logger.getLogger(BeanValidator.class);
  private static Validator validator = (Validator) ApplicationContextProvider.getApplicationContext().getBean("validator");
  
  public BeanValidator() {
    
  }

  /**
   * Shared routines for validation
   */
  /**
   * Validate all properties in this object
   * @return isValid
   */
  @SafeVarargs
  public static boolean isValid(final Object object, final Class<? extends Default>... classes) {
    boolean isValid = true;
    
    Set<ConstraintViolation<Object>> violations = validator.validate(object, classes);
    isValid = logViolations(violations);
    
    return isValid;
  }
  
  /**
   * Validate one property in this object
   * @param prop
   * @return isValid
   */
  public static boolean isValid(final Object object, String prop) {
    boolean isValid = true;
  
    Set<ConstraintViolation<Object>> violations = validator.validateProperty(object, prop);
    isValid = logViolations(violations);
    
    return isValid;
  }
  
  /**
   * Send all Constraint Violations to log.
   * @param violations
   * @return noErrorsFound
   */
  public static boolean logViolations(Set<ConstraintViolation<Object>> violations) {
     boolean noErrorsFound = true;
    
     for (final ConstraintViolation<Object> violation : violations) {
       logger.error(violation.getPropertyPath() + " " + violation.getMessage());
       noErrorsFound = false;
     }
     return noErrorsFound;
  }
  
//  public static void validateEntity(final Object object, final Class<? extends Default>... classes)
//      throws IllegalArgumentException {
//  final Set<ConstraintViolation<Object>> errors = validator.validate(object, classes);
//  if (!errors.isEmpty()) {
//      throw new IllegalArgumentException(returnErrors(errors));
//  }
//  }
//  
//  
//  public static String returnErrors(final Set<ConstraintViolation<Object>> errors) {
//    final StringBuilder builder = new StringBuilder();
//    for (final ConstraintViolation<Object> error : errors) {
//        builder.append(error.getMessage());
//    }
//    return builder.toString();
//  }
  /**
   *  Validate a string for min/max length
   * @param input
   * @return data
   */
  public static String validateStringValue(final String input, final String fieldName, final int min, final int max) {
    String data = null;
    
    if (input != null) {
      if (input.length() < min) {
        logger.error(WdlptEncode.forJava(fieldName + " length is less than " + min + ".  Input value: '" + input + "'"));
        throw new RuntimeException();
      }
      if (input.length() > max) {
        logger.error(WdlptEncode.forJava(fieldName + " length is greater than " + min + ".  Input value: '" + input + "'"));
        throw new RuntimeException();
      }
      data = input;
    } 
    
    return data; 
  }

  /**
   *  Validate string is a integer number and is not negative
   * @param input
   * @return data
   */
  public static String validateIntegerValue(final String input, final String fieldName) {
    Integer value = null;
    String data = null;
    if (input != null) {
      try {
        value = Integer.parseInt(input);
      } catch (NumberFormatException nfe) {
        logger.error(WdlptEncode.forJava(fieldName + " is not a valid number.  Input value: '" + input + "'"), nfe);
        throw nfe;
      }
      if (value < 0) {
        logger.error(WdlptEncode.forJava(fieldName + " is less than zero. PK should not be negative."));
        throw new RuntimeException();
      }
      data = input;
    } 
    
    return data; 
  }

  /**
   *  Validate Integer is a integer number and is not negative
   * @param input
   * @return data
   */
  public static Integer validateIntegerValue(final Integer input, final String fieldName) {
    Integer data = null;
    if (input != null) {
      if (input < 0) {
        logger.error(WdlptEncode.forJava(fieldName + " is less than zero. PK should not be negative."));
        throw new RuntimeException();
      }
      data = input;
    } 
    
    return data; 
  }
  

  /**
   *  Validate string is a long number and is not negative
   * @param input
   * @return data
   */
  public static String validateLongValue(final String input, final String fieldName) {
    Long value = null;
    String data = null;
    if (input != null) {
      try {
        value = Long.parseLong(input);
      } catch (NumberFormatException nfe) {
        logger.error(fieldName + " is not a valid number.  Input value: '" + input + "'", nfe);
        throw nfe;
      }
      if (value < 0) {
        logger.error(fieldName + " is less than zero. PK should not be negative.");
        throw new RuntimeException();
      }
      data = input;
    } 
    
    return data; 
  }

  /**
   *  Validate Long is a long number and is not negative
   * @param input
   * @return data
   */
  public static Long validateLongValue(final Long input, final String fieldName) {
    Long data = null;
    if (input != null) {
      if (input < 0) {
        logger.error(WdlptEncode.forJava(fieldName + " is less than zero. PK should not be negative."));
        throw new RuntimeException();
      }
      data = input;
    } 
    
    return data; 
  }
  
  /**
   *  Validate string is a double number and is not negative
   * @param input
   * @return data
   */
  public static String validateDoubleValue(final String input, final String fieldName) {
    Double value = null;
    String data = null;
    if (input != null) {
      try {
        value = Double.parseDouble(input);
      } catch (NumberFormatException nfe) {
        logger.error(WdlptEncode.forJava(fieldName + " is not a valid number.  Input value: '" + input + "'"), nfe);
        throw nfe;
      }
      if (value < 0) {
        logger.error(WdlptEncode.forJava(fieldName + " is less than zero. PK should not be negative."));
        throw new RuntimeException();
      }
//      DecimalFormat df = new DecimalFormat("0", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
//      df.setMaximumFractionDigits(340);
//      
//      return df.format(value); //input; 
      data = input;
    } 
    
    return data; 
  }
  
  /**
   *  Validate Double is a double number and is not negative
   * @param input
   * @return data
   */
  public static Double validateDoubleValue(final Double input, final String fieldName) {
    Double data = null;
    if (input != null) {
      if (input < 0) {
        logger.error(WdlptEncode.forJava(fieldName + " is less than zero. PK should not be negative."));
        throw new RuntimeException();
      }
      data = input;
    } 
    
    return data; 
  }
  
  /**
   *  Validate BigDecimal is a BigDecimal number and is not negative
   * @param input
   * @return data
   */
  public static BigDecimal validateBigDecimalValue(final BigDecimal input, final String fieldName) {
    BigDecimal data = null;
    if (input != null) {
      if (input.compareTo(BigDecimal.ZERO) == -1) {
        logger.error(WdlptEncode.forJava(fieldName + " is less than zero. PK should not be negative."));
        throw new RuntimeException();
      }
      data = input;
    } 
    
    return data; 
  }
  
}
