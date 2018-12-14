package mil.osd.opa.ptd.log_service.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import mil.osd.opa.ptd.log_service.beans.AppProperties;

import java.util.Map;

@RestController
@RequestMapping("/")
public class WelcomeController {

    private static final Logger logger = LoggerFactory.getLogger(WelcomeController.class);

    private AppProperties app;

    @Autowired
    public void setApp(AppProperties app) {
        this.app = app;
    }

    @RequestMapping("/test")
    public String welcome(Map<String, Object> model) {

        String appProperties = app.toString();

        logger.debug("Welcome {}, {}", app);

//        model.put("message", appProperties);
        
        return "welcome: " + appProperties;
    }

}
