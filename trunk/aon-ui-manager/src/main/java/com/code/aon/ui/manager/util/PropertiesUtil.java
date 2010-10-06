package com.code.aon.ui.manager.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertiesUtil {

	private final static Logger LOGGER = LoggerFactory.getLogger(PropertiesUtil.class);

	public static Properties getProperties( File file, String resource ) {
		Properties properties = new Properties();
		try {
			if ( file.exists() && file.canRead() ) {
				properties.load( new FileInputStream(file) );
			} else {
				properties.load(PropertiesUtil.class.getResourceAsStream(resource));
			}
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e );
		}
		return properties;
	}	
	
}
