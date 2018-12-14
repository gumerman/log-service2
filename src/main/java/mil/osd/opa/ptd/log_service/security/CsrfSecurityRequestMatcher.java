package mil.osd.opa.ptd.log_service.security;

import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.web.util.matcher.RegexRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;

@Component
public class CsrfSecurityRequestMatcher implements RequestMatcher {
  // Don't put CSRF token for these methods
  private Pattern allowedMethods = Pattern.compile("^(GET|HEAD|TRACE|OPTIONS)$");
  // Do put token for these paths only
  private RegexRequestMatcher protectedMatcher = new RegexRequestMatcher("/rest/*", null);

  @Override
  public boolean matches(HttpServletRequest request) {   
      // No CSRF token for GET, etc.
      if(allowedMethods.matcher(request.getMethod()).matches()){
          return false;
      }
      // Protect CSRF for REST calls with POST/PUT?DELETE
      if (protectedMatcher.matches(request)) {
        return true;
      }
      
      // Don't protect other URL's because they are EMMA and version.jsp, etc.
      return false;
  }
}