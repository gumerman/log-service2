package mil.osd.opa.ptd.log_service.utils;


import org.owasp.encoder.Encode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Extended Wrapper class for the OWASP Encoder library because if you send in a NULL string, 
 * you get back a string with "null" in it instead of null.
 * 
 * @author gumermrd
 *
 */
public class WdlptEncode {
  private final static Logger log = LoggerFactory.getLogger(WdlptEncode.class);
  private final static char[] IMMUNE_SQL = { ' ' };
  
  public WdlptEncode() {
    
  }

  private static String nullCheck(String value, String input) {
    if (value == null || (value.equals("null") && input == null)) {
      value = null;
    }
    
    return value;
  }
  
  public static String forJavaScript(String input) {
    String value = Encode.forJavaScript(input);
    
    return nullCheck(value, input);
  }
  
  public static String forJavaScriptSource(String input) {
    String value = Encode.forJavaScriptSource(input);
    
    return nullCheck(value, input);
  }
  
  public static String forJavaScriptBlock(String input) {
    String value = Encode.forJavaScriptBlock(input);
    
    return nullCheck(value, input);
  }
  
  public static String forJava(String input) {
    String value = Encode.forJava(input);
    
    return nullCheck(value, input);
  }
  
  public static String forHtml(String input) {
    String value = Encode.forHtml(input);
    
    return nullCheck(value, input);
  }
  
  public static String forXml(String input) {
    String value = Encode.forXml(input);
    
    return nullCheck(value, input);
  }
  
  public static String forXmlComment(String input) {
    String value = Encode.forXmlComment(input);
    
    return nullCheck(value, input);
  }
  
  public static String forSecHtml(String input) {
    String value = forJavaScript(Encode.forHtml(input));
      
    return nullCheck(value, input);
  }
  
  public static String forHtmlContent(String input) {
    String value = Encode.forHtmlContent(input);
    
    return nullCheck(value, input);
  }
  
  public static String forSecHtmlContent(String input) {
    String value = forJavaScript(Encode.forHtmlContent(input));
    
    return nullCheck(value, input);
  }
  
  public static String forHtmlAttribute(String input) {
    String value = Encode.forHtmlAttribute(input);
    
    return nullCheck(value, input);
  }
  
  public static String forSecHtmlAttribute(String input) {
    String value = forJavaScript(Encode.forHtmlAttribute(input));
    
    return nullCheck(value, input);
  }
  
  public static String forHtmlUnquotedAttribute(String input) {
    String value = Encode.forHtmlUnquotedAttribute(input);
    
    return nullCheck(value, input);
  }

  /**
   * Encode input for use in a Oracle SQL query.
   * 
   * This method is not recommended. The use of the PreparedStatement 
   * interface is the preferred approach. However, if for some reason 
   * this is impossible, then this method is provided as a weaker 
   * alternative. 
   * 
   * The best approach is to make sure any single-quotes are double-quoted.
   * Another possible approach is to use the {escape} syntax described in the
   * JDBC specification in section 1.5.6.
   * 
   * However, this syntax does not work with all drivers, and requires
   * modification of all queries.
   * 
   * @see <a href="http://java.sun.com/j2se/1.4.2/docs/guide/jdbc/getstart/statement.html">JDBC Specification</a>
   *  
   * @param input 
   *    the text to encode for SQL
   * 
   * @return input encoded for use in SQL
   */
  public static String forSQL(String input) {
    if (input == null) {
      return input;
    } else {
      return encode(IMMUNE_SQL, input);
    }

  }

  /**
   * Encode a String so that it can be safely used in a specific context.
   * 
   * @param immune
   * @param input
   *    the String to encode
   * @return the encoded String
   */
  private static String encode(char[] immune, String input) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < input.length(); i++) {
      char c = input.charAt(i);
      sb.append(encodeCharacter(immune, c));
    }
    return sb.toString();
  }

  /**
   * Default implementation that should be overridden in specific codecs.
   * 
   * @param immune
   * @param c
   *    the Character to encode
   * @return
   *    the encoded Character
   */
  private static String encodeCharacter( char[] immune, Character c ) {
    if ( c.charValue() == '\'' )
          return "\'\'";
        return ""+c;
  }

}

