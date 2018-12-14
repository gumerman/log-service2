package mil.osd.opa.ptd.log_service.beans;

import java.util.Properties;

/**
 * When the application starts the context listener 
 *   listener.ContextListener.java will inject key values 
 *   into the properties class.  This limits but does not exclude
 *   the use off the regionProperties jars use allowing for easy
 *   in developing unit tests.
 * 
 * 
 * @author Dave Springer
 *
 */
public class ApplicationProperties {

	private ApplicationProperties(){}
	
	private static Properties applicationProperties = null;
	// Modify to use TEST0 for unit testing
	private static String region = "TEST1";
	private static String projPropsPath = "TEST1";
	private static String appVersion = "";
	
	
	public static Properties getApplicationProperties() {
		return applicationProperties;
	}
	public static void setApplicationProperties(Properties applicationProperties) {
		ApplicationProperties.applicationProperties = applicationProperties;
	}
	public static String getRegion() {
		return region;
	}
	public static void setRegion(String region) {
		ApplicationProperties.region = region;
	}
	public static String getProjPropsPath() {
		return projPropsPath;
	}
	public static void setProjPropsPath(String projPropsPath) {
		ApplicationProperties.projPropsPath = projPropsPath;
	}
	public static String getAppVersion() {
		return appVersion;
	}
	public static void setAppVersion(String appVersion) {
		ApplicationProperties.appVersion = appVersion;
	}
	
	
	
}
