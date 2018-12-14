package mil.osd.opa.ptd.log_service.beans;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("app") // prefix app, find app.* values
public class AppProperties {

    private String region;
    private String name;
    private String version;
    
    public String getRegion() {
      return region;
    }
    public void setRegion(String region) {
      this.region = region;
    }
    public String getName() {
      return name;
    }
    public void setName(String name) {
      this.name = name;
    }
    public String getVersion() {
      return version;
    }
    public void setVersion(String version) {
      this.version = version;
    }

}
