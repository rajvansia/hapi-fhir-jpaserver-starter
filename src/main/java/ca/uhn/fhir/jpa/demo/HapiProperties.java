package ca.uhn.fhir.jpa.demo;

import ca.uhn.fhir.context.ConfigurationException;
import ca.uhn.fhir.context.FhirVersionEnum;
import ca.uhn.fhir.rest.api.EncodingEnum;
import ca.uhn.fhir.rest.server.ETagSupportEnum;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.util.Properties;

public class HapiProperties {
    public static final String SERVER_ADDRESS = "server_address";
    public static final String DEFAULT_PRETTY_PRINT = "default_pretty_print";
    public static final String MAX_PAGE_SIZE = "max_page_size";
    public static final String MAX_FETCH_SIZE = "max_fetch_size";
    public static final String DEFAULT_PAGE_SIZE = "default_page_size";
    public static final String LOGGER_NAME = "logger.name";
    public static final String LOGGER_FORMAT = "logger.format";
    public static final String ALLOW_EXTERNAL_REFERENCES = "allow_external_references";
    public static final String ALLOW_MULTIPLE_DELETE = "allow_multiple_delete";
    public static final String DATASOURCE_PASSWORD = "datasource.password";
    public static final String DATASOURCE_USERNAME = "datasource.username";
    public static final String DATASOURCE_URL = "datasource.url";
    public static final String DATASOURCE_DRIVER = "datasource.driver";
    public static final String DATASOURCE_MAX_POOL_SIZE = "datasource.max_pool_size";
    public static final String LOGGER_LOG_EXCEPTIONS = "logger.log_exceptions";
    public static final String LOGGER_ERROR_FORMAT = "logger.error_format";
    public static final String PERSISTENCE_UNIT_NAME = "persistence_unit_name";
    public static final String SERVER_BASE = "server.base";
    public static final String TEST_PORT = "test.port";
    public static final String SERVER_NAME = "server.name";
    public static final String SERVER_ID = "server.id";
    private static Properties properties;
    private static final String HAPI_PROPERTIES = "hapi.properties";
    private static final String FHIR_VERSION = "fhir_version";
    private static final String DEFAULT_ENCODING = "default_encoding";
    private static final String ETAG_SUPPORT = "etag_support";

    public static Properties getProperties() {
        if (properties == null) {
            // Load the configurable properties file
            InputStream in = null;

            try {
                in = HapiProperties.class.getClassLoader().getResourceAsStream(HAPI_PROPERTIES);
                HapiProperties.properties = new Properties();
                HapiProperties.properties.load(in);
                in.close();
            } catch (Exception e) {
                throw new ConfigurationException("Could not load HAPI properties", e);
            }

            Properties overrideProps = loadOverrideProperties();
            if(overrideProps != null) {
              properties.putAll(overrideProps);
            }
        }

        return properties;
    }

    /**
     * If a configuration file path is explicitly specified via -Dhapi.properties=<path>, the properties there will
     * be used to override the entries in the default hapi.properties file (currently under WEB-INF/classes)
     * @return properties loaded from the explicitly specified configuraiton file if there is one, or null otherwise.
     */
    private static Properties loadOverrideProperties() {
        String confFile = System.getProperty(HAPI_PROPERTIES);
        if(confFile != null) {
            try {
                Properties props = new Properties();
                props.load(new FileInputStream(confFile));
                return props;
            }
            catch (Exception e) {
                throw new ConfigurationException("Could not load HAPI properties file: " + confFile, e);
            }
        }

        return null;
    }

    private static String getProperty(String propertyName) {
        Properties properties = HapiProperties.getProperties();

        if (properties != null) {
            return properties.getProperty(propertyName);
        }

        return null;
    }

    private static String getProperty(String propertyName, String defaultValue) {
        Properties properties = HapiProperties.getProperties();

        if (properties != null) {
            String value = properties.getProperty(propertyName);

            if (value != null && value.length() > 0) {
                return value;
            }
        }

        return defaultValue;
    }

    private static Boolean getBooleanProperty(String propertyName, Boolean defaultValue) {
        String value = HapiProperties.getProperty(propertyName);

        if (value == null || value.length() == 0) {
            return defaultValue;
        }

        return Boolean.parseBoolean(value);
    }

    private static Integer getIntegerProperty(String propertyName, Integer defaultValue) {
        String value = HapiProperties.getProperty(propertyName);

        if (value == null || value.length() == 0) {
            return defaultValue;
        }

        return Integer.parseInt(value);
    }

    public static FhirVersionEnum getFhirVersion() {
        String fhirVersionString = HapiProperties.getProperty(FHIR_VERSION);

        if (fhirVersionString != null && fhirVersionString.length() > 0) {
            return FhirVersionEnum.valueOf(fhirVersionString);
        }

        return FhirVersionEnum.DSTU3;
    }

    public static ETagSupportEnum getEtagSupport() {
        String etagSupportString = HapiProperties.getProperty(ETAG_SUPPORT);

        if (etagSupportString != null && etagSupportString.length() > 0) {
            return ETagSupportEnum.valueOf(etagSupportString);
        }

        return ETagSupportEnum.ENABLED;
    }

    public static EncodingEnum getDefaultEncoding() {
        String defaultEncodingString = HapiProperties.getProperty(DEFAULT_ENCODING);

        if (defaultEncodingString != null && defaultEncodingString.length() > 0) {
            return EncodingEnum.valueOf(defaultEncodingString);
        }

        return EncodingEnum.JSON;
    }

    public static Boolean getDefaultPrettyPrint() {
        return HapiProperties.getBooleanProperty(DEFAULT_PRETTY_PRINT, true);
    }

    public static String getServerAddress() {
        return HapiProperties.getProperty(SERVER_ADDRESS);
    }

    public static Integer getDefaultPageSize() {
        return HapiProperties.getIntegerProperty(DEFAULT_PAGE_SIZE, 20);
    }

    public static Integer getMaximumPageSize() {
        return HapiProperties.getIntegerProperty(MAX_PAGE_SIZE, 200);
    }

    public static Integer getMaximumFetchSize() {
        return HapiProperties.getIntegerProperty(MAX_FETCH_SIZE, Integer.MAX_VALUE);
    }

    public static String getPersistenceUnitName() {
        return HapiProperties.getProperty(PERSISTENCE_UNIT_NAME, "HAPI_PU");
    }

    public static String getLoggerName() {
        return HapiProperties.getProperty(LOGGER_NAME, "fhirtest.access");
    }

    public static String getLoggerFormat() {
        return HapiProperties.getProperty(LOGGER_FORMAT, "Path[${servletPath}] Source[${requestHeader.x-forwarded-for}] Operation[${operationType} ${operationName} ${idOrResourceName}] UA[${requestHeader.user-agent}] Params[${requestParameters}] ResponseEncoding[${responseEncodingNoDefault}]");
    }

    public static String getLoggerErrorFormat() {
        return HapiProperties.getProperty(LOGGER_ERROR_FORMAT, "ERROR - ${requestVerb} ${requestUrl}");
    }

    public static Boolean getLoggerLogExceptions() {
        return HapiProperties.getBooleanProperty(LOGGER_LOG_EXCEPTIONS, true);
    }

    public static String getDataSourceDriver() {
        return HapiProperties.getProperty(DATASOURCE_DRIVER, "org.apache.derby.jdbc.EmbeddedDriver");
    }

    public static Integer getDataSourceMaxPoolSize() {
        return HapiProperties.getIntegerProperty(DATASOURCE_MAX_POOL_SIZE, 10);
    }

    public static String getDataSourceUrl() {
        return HapiProperties.getProperty(DATASOURCE_URL, "jdbc:derby:directory:target/jpaserver_derby_files;create=true");
    }

    public static String getDataSourceUsername() {
        return HapiProperties.getProperty(DATASOURCE_USERNAME);
    }

    public static String getDataSourcePassword() {
        return HapiProperties.getProperty(DATASOURCE_PASSWORD);
    }

    public static Boolean getAllowMultipleDelete() {
        return HapiProperties.getBooleanProperty(ALLOW_MULTIPLE_DELETE, true);
    }

    public static Boolean getAllowExternalReferences() {
        return HapiProperties.getBooleanProperty(ALLOW_EXTERNAL_REFERENCES, true);
    }

    public static Boolean getExpungeEnabled() {
        return HapiProperties.getBooleanProperty("expunge_enabled", true);
    }

    public static Integer getTestPort() {
        return HapiProperties.getIntegerProperty(TEST_PORT, 0);
    }

    public static String getServerBase() {
        return HapiProperties.getProperty(SERVER_BASE, "/baseDstu3");
    }

    public static String getServerName() {
        return HapiProperties.getProperty(SERVER_NAME, "Local Tester");
    }

    public static String getServerId() {
        return HapiProperties.getProperty(SERVER_ID, "home");
    }
}
