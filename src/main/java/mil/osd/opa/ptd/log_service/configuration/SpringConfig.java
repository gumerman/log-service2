package mil.osd.opa.ptd.log_service.configuration;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Primary;
import org.springframework.validation.Validator;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import com.zaxxer.hikari.HikariDataSource;

@Configuration
@MapperScan(value="mil.osd.opa.ptd.dtat.mapper", sqlSessionFactoryRef="sqlSessionFactory") // myBatis scanner DLPT
@EnableAspectJAutoProxy(proxyTargetClass=true) // enable AOP
public class SpringConfig {

    @Bean
    @Primary
    @ConfigurationProperties(prefix = "spring.dlpt.datasource")
    public DataSource dlptDataSource() {
        return DataSourceBuilder.create().build();
    }
    
    @Bean
    @Primary
    public SqlSessionFactory sqlSessionFactory() throws Exception {
      SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
      sessionFactory.setDataSource(dlptDataSource());
      return sessionFactory.getObject();
    }

    // For BeanValidator class to expose validator reference
    @Bean
    public Validator validator() {
      return new org.springframework.validation.beanvalidation.LocalValidatorFactoryBean();
    }
}
