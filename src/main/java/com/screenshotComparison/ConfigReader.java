package com.screenshotComparison;

import java.util.Properties;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ConfigReader {
	
    public static String getProperty(String propertyName) {
        Properties config = new Properties();
        try {
            InputStream iStream = new FileInputStream(System.getProperty("user.dir")+"\\config.properties");
            config.load(iStream);
        } catch (IOException e) {
        }
        return (config.getProperty(propertyName));
    }
}
