package Feature.Utility;

import java.util.Properties;

public class ConfigLoader {

    private static final String USERS = "users";
    private static final String RAMP_DURATION = "ramp_duration";
    private static final String ENV = "env";

    private static final String NEWFILE ="jsonNewFile";
    private static final String UPDATEFILE ="jsonUpdFile";
    private static final String TEMPFILE ="jsonTemplate";

    private static final String ADMINUSER = "adminUser";
    private static final String ADMINPASSWORD = "adminPassword";

    private static final String BASEURL = "baseUrl";
    private static final String ACCEPTHEADER = "acceptHeader";
    private static final String CONTENTTYPEHEADER = "contentTypeHeader";

    private static final String ENDPOINT_GETALL= "endpointPath_GetAll";
    private static final String ENDPOINT_GETSINGLE= "endpointPath_GetSingle";
    private static final String ENDPOINT_POST= "endpointPath_Post";
    private static final String ENDPOINT_PUT= "endpointPath_Put";
    private static final String ENDPOINT_DELETE= "endpointPath_Delete";


    /* Default config file is stg_config.properties */
    private static final String STG_CONFIG_PROPERTIES = "config_stg.properties";
    private static final String DEV_CONFIG_PROPERTIES = "config_dev.properties";
    private static final String QA_CONFIG_PROPERTIES = "config_qa.properties";
    private static final String PROD_CONFIG_PROPERTIES = "config_prod.properties";


    private static final String RESOURCES_PATH = System.getProperty("user.dir") + "/src/test/resources/";
    private Properties properties;
    // private final Properties properties;
    private static ConfigLoader configLoader;

    private ConfigLoader() {

        /* Setting EnvType.QA as default environment */
        /*
         * This will check for the env value from Jenkins/Maven first. If it does not
         * get any input from Jenkins/mvn cmd line, then, will take
         * stg_config.properties file as default
         */
        String env = System.getProperty(ENV, EnvType.QA.toString());

        switch (EnvType.valueOf(env)) {

            /* Only STAGE is working, Rest are taken for example */
            case STAGE: {
                properties = getConfigPropertyFile(STG_CONFIG_PROPERTIES);
                break;
            }
            case DEV: {
                properties = getConfigPropertyFile(DEV_CONFIG_PROPERTIES);
                break;
            }
            case QA: {
                properties = getConfigPropertyFile(QA_CONFIG_PROPERTIES);
                break;
            }
            case PRODUCTION: {
                properties = getConfigPropertyFile(PROD_CONFIG_PROPERTIES);
                break;
            }
            default: {
                throw new IllegalStateException("Invalid EnvType: " + env);
            }

        }
    }

    private Properties getConfigPropertyFile(String configFile) {
        return PropertyUtils.propertyLoader(RESOURCES_PATH + configFile);
    }

    private String getPropertyValue(String propertyKey) {
        String prop = properties.getProperty(propertyKey);
        if (prop != null) {
            return prop.trim();
        } else {
            throw new RuntimeException("Property " + propertyKey + " is not specified in the config.properties file");
        }
    }

    public static ConfigLoader getInstance() {
        if (configLoader == null) {
            configLoader = new ConfigLoader();
        }
        return configLoader;
    }

    public String getUsers() {
        return getPropertyValue(USERS);
    }

    public String getRampDuration() {
        return getPropertyValue(RAMP_DURATION);
    }

    public String getEnv() { return getPropertyValue(ENV); }

    public String getBaseurl() { return getPropertyValue(BASEURL); }

    public String getAcceptheader() {
        return getPropertyValue(ACCEPTHEADER);
    }

    public String getContenttypeheader() { return getPropertyValue(CONTENTTYPEHEADER); }


    public String getNewfile() {
        return getPropertyValue(NEWFILE);
    }

    public String getUpdatefile() {
        return getPropertyValue(UPDATEFILE);
    }

    public String getTempfile() { return getPropertyValue(TEMPFILE); }


    public String getEndpointGetall() {
        return getPropertyValue(ENDPOINT_GETALL);
    }

    public String getEndpointGetsingle() {
        return getPropertyValue(ENDPOINT_GETSINGLE);
    }

    public String getEndpointPost() {
        return getPropertyValue(ENDPOINT_POST);
    }

    public String getEndpointPut() {
        return getPropertyValue(ENDPOINT_PUT);
    }

    public String getEndpointDelete() {
        return getPropertyValue(ENDPOINT_DELETE);
    }

    public  String getAdminuser() { return getPropertyValue(ADMINUSER); }

    public  String getAdminpassword() {return getPropertyValue(ADMINPASSWORD); }
}
