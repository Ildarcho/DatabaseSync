package net.Ildar.DatabaseSync;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.RollingFileAppender;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Contains all program settings
 */
public class Settings {
    /**
     * SQL instance name
     */
    String SQLInstance;
    /**
     * SQL Database URL
     */
    String DBURL;
    /**
     * SQL Database username
     */
    String DBUSER;
    /**
     * SQL Database password
     */
    String DBPASS;
    /**
     * Log file name
     */
    String LOGFILE;

    /**
     * Load all settings from config.properties file
     *
     * @throws IOException when IO exception occurs
     */
    public void load() throws IOException {
        Properties properties = new Properties();
        try (InputStream propertiesFile =
                     new FileInputStream("config.properties");
        ) {
            properties.load(propertiesFile);
            SQLInstance = properties.getProperty("SQLInstance");
            DBURL = properties.getProperty("DBURL");
            DBUSER = properties.getProperty("DBUSER");
            DBPASS = properties.getProperty("DBPASS");
            LOGFILE = properties.getProperty("LOGFILE");
            if (SQLInstance == null || DBURL == null || DBUSER == null || DBPASS == null)
                throw new IOException();
        } catch (FileNotFoundException e) {
            System.err.println("Can't find config file!");
        }
    }

    /**
     * Validates  parameters
     *
     * @return true if all parameters are valid
     */
    public boolean isValid() {
        boolean valid = true;
        if (SQLInstance == null) valid = false;
        else if (DBURL == null) valid = false;
        else if (DBUSER == null) valid = false;
        else if (DBPASS == null) valid = false;
        else if (LOGFILE == null) valid = false;
        return valid;
    }


    /**
     * Constructs logger from log4j library
     *
     * @return org.apache.log4j.Logger
     */
    public Logger getLogger() {
        if (!isValid())
            return null;
        Logger logger = Logger.getLogger(DatabaseSync.class);
        RollingFileAppender appender = (RollingFileAppender) LogManager.getRootLogger().getAppender("file");
        if (appender == null)
            return null;
        appender.setFile(LOGFILE);
        return logger;
    }
}
