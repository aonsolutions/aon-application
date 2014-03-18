package com.code.aon;

import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AonVersion {

	private static final Logger LOGGER = LoggerFactory.getLogger(AonVersion.class.getName());
	
	public static final String VERSION = getVersion();
	
	public static final long SERIAL_VERSION_UID = getSerialVersionUID(VERSION);
	
	private static final String POM_PROPERTIES = "/META-INF/maven/com.code.aon/aon.common/pom.properties";
	
	private static final String VERSION_ATTRIBUTE = "version";
	
	private static String getVersion() {
		String version = null;
		try {
			Properties properties = new Properties();
			InputStream in = AonVersion.class.getResourceAsStream(POM_PROPERTIES);
			properties.load(in);
			in.close();
			version = StringUtils.trimToNull(properties.getProperty(VERSION_ATTRIBUTE));
		} catch ( Throwable th ) {
			LOGGER.warn("Imposible determinar la versión");
		}
		LOGGER.info("Version: {}", version);
		return version;
	}
	
	private static long getSerialVersionUID( String version ) {
		long result = 1;
		String value = StringUtils.substringBefore(version, "-");
		String[] numbers = StringUtils.split(value, ".");
		if ( ArrayUtils.getLength(numbers) > 1 ) {
			if ( NumberUtils.isDigits(numbers[0]) ) {
				result = NumberUtils.toInt(numbers[0]) * 100;
			}
			if ( NumberUtils.isDigits(numbers[1]) ) {
				result += NumberUtils.toInt(numbers[1]);
			}
		}
		LOGGER.info("serialVersionUID: {}", result);
		return result;
	}	

}
