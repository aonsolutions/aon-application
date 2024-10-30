package net.aonsolutions.occam.impl.handler;

import org.jooq.Field;

import com.esferalia.aon.watson.util.AonStringUtils;

class HandlerUtils {
	
	private HandlerUtils() {
	}
	
	static boolean overflows( Field<String> field, String value) {
		return (AonStringUtils.length(value) > field.getDataType().length() );
	}

}
