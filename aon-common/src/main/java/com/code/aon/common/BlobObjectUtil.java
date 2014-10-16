package com.code.aon.common;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BlobObjectUtil {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(BlobObjectUtil.class);

	private static final String SIZE_PROPERTY = "size";
	
	public static Integer getPropertySize( IBlobObject bo, String property ) {
		try {		
			String sizeProperty = SIZE_PROPERTY;
			if (! IBlobObject.DATA_PROPERTY.equals(property) ) {
				sizeProperty = property + StringUtils.capitalize(SIZE_PROPERTY);
			}
			return (Integer) PropertyUtils.getSimpleProperty(bo, sizeProperty);
		} catch (Throwable e) {
			LOGGER.error( "Error getting value of " + property, e);
		}
		return 0;
	}
	
	public static byte[] getProperty( IBlobObject bo, String property ) {
		try {
			return (byte[]) PropertyUtils.getSimpleProperty(bo, property);
		} catch (Throwable e) {
			LOGGER.error( "Error getting value of " + property, e);
		}
		return null;
	}
	
}
