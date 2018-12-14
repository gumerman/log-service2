package mil.osd.opa.ptd.log_service.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import mil.osd.opa.ptd.log_service.security.CsrfSecurityRequestMatcher;
import mil.osd.opa.ptd.log_service.security.CustomAccessDeniedHandler;

@SuppressWarnings("deprecation")
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
  
  @Autowired
  private CustomAccessDeniedHandler accessDeniedHandler;
  @Autowired
  private CsrfSecurityRequestMatcher csrfSecurityRequestMatcher;

	// Authentication : User --> Roles
	protected void configure(AuthenticationManagerBuilder auth)
			throws Exception {
    auth.inMemoryAuthentication()
      .passwordEncoder(customPasswordEncoder())
//      .passwordEncoder(NoOpPasswordEncoder.getInstance())
      .withUser("user").password("$2a$04$Cc3sqHBshqI2ssfwdAMp6uSs3Nwwu6FiV1WuL6LyFb2/EFzvr0IQC").roles("USER")
      .and()
      .withUser("admin").password("$2a$04$wzbJRIG/fxxpxXGP2QKQgeakt.Bp26RVryehomycaUtglVfihG4fq").roles("USER", "ADMIN");
}

  // Authorization : Role -> Access
  protected void configure(HttpSecurity http) throws Exception {
  http
    .csrf().disable()
//    .csrf().requireCsrfProtectionMatcher(csrfSecurityRequestMatcher)
//    .and()
    .authorizeRequests()
    .and()
    .exceptionHandling().accessDeniedHandler(accessDeniedHandler)
    .and()
    .authorizeRequests()
    .antMatchers("/logger/**").authenticated()
    .antMatchers("/logger/login").permitAll()
    .antMatchers("/logger/").permitAll()
    .and()
    .httpBasic()
    .and()
//    .formLogin()
//      .loginPage("/login")
//      .permitAll()
//      .and()
    .logout()
      .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
      .logoutSuccessUrl("/login?logout")
      .permitAll();
	}

//  @SuppressWarnings("deprecation")
//  @Bean
//  public static NoOpPasswordEncoder passwordEncoder() {
//    return (NoOpPasswordEncoder) NoOpPasswordEncoder.getInstance();
//  }
  // Clear text passwords
//  @Bean
//  public PasswordEncoder passwordEncoder() {
//      return new PasswordEncoder() {
//          @Override
//          public String encode(CharSequence rawPassword) {
//              return rawPassword.toString();
//          }
//
//          @Override
//          public boolean matches(CharSequence rawPassword, String encodedPassword) {
//              return rawPassword.toString().equals(encodedPassword);
//          }
//      };
//  }
  
  // Password with bcrypt
  @Bean
  public PasswordEncoder customPasswordEncoder() {
    return new PasswordEncoder() {

      @Override
      public String encode(CharSequence rawPassword) {
        return BCrypt.hashpw(rawPassword.toString(), BCrypt.gensalt(4));
      }

      @Override
      public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return BCrypt.checkpw(rawPassword.toString(), encodedPassword);
      }
    };
  }
}
