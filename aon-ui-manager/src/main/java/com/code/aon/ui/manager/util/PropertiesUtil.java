package com.code.aon.ui.manager.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertiesUtil {

	private final static Logger LOGGER = LoggerFactory.getLogger(PropertiesUtil.class);

	public static Properties loadProperties( String resource ) {
		Properties properties = new Properties();
		loadProperties( properties, resource );
		return properties;
	}	
	
	public static void loadProperties( Properties properties, String resource ) {
		InputStream in = null;
		try {
			in = PropertiesUtil.class.getResourceAsStream(resource);
			properties.load(in);
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e );
		} finally {
			IOUtils.closeQuietly(in);
		}
	}	

	public static Properties loadProperties( File file ) {
		Properties properties = new Properties();
		loadProperties( properties, file );
		return properties;
	}	
	
	public static void loadProperties( Properties properties, File file ) {
		InputStream in = null;
		try {
			if ( file.exists() && file.canRead() ) {
				in = new BufferedInputStream( new FileInputStream(file) );
				properties.load(in);
			}
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e );
		} finally {
			IOUtils.closeQuietly(in);
		}
	}	
	
	public static Properties getProperties( File file, String resource ) {
		Properties properties = loadProperties(resource);
		properties.putAll(loadProperties(file));
		return properties;
	}	
	
}
