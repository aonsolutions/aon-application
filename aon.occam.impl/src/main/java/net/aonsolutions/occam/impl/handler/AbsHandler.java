package net.aonsolutions.occam.impl.handler;

import org.jooq.Field;

import com.esferalia.aon.watson.util.AonStringUtils;

abstract class AbsHandler {
	
	static Byte getByte(Boolean bool) {
		if (bool == null) return null;
		return (byte) (bool.booleanValue() ? 1 : 0); 
	}

	static boolean overflows( Field<String> field, String value) {
		return (AonStringUtils.length(value) > field.getDataType().length() );
	}

}
