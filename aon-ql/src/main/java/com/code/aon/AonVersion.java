package com.code.aon;

import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.esferalia.aon.watson.util.AonArrayUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class AonVersion {

	private static final Logger LOGGER = Logger.getLogger(AonVersion.class.getName());
	
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
			version = AonStringUtils.trimToNull(properties.getProperty(VERSION_ATTRIBUTE));
		} catch ( Throwable th ) {
			LOGGER.warning("Imposible determinar la versión");
		}
		LOGGER.log(Level.INFO,"Version: {}", version);
		return version;
	}
	
	private static long getSerialVersionUID( String version ) {
		long result = 1;
		String value = AonStringUtils.substringBefore(version, "-");
		String[] numbers = AonStringUtils.split(value, '.');
		if ( AonArrayUtils.getLength(numbers) > 1 ) {
			if ( AonNumberUtils.isDigits(numbers[0]) ) {
				result = AonNumberUtils.toint(numbers[0]) * 100;
			}
			if ( AonNumberUtils.isDigits(numbers[1]) ) {
				result += AonNumberUtils.toint(numbers[1]);
			}
		}
		LOGGER.log(Level.INFO,"serialVersionUID: {}", result);
		return result;
	}	

}
