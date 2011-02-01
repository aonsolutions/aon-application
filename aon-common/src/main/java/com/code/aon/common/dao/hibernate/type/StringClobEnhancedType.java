package com.code.aon.common.dao.hibernate.type;

import org.hibernate.type.StringClobType;
import org.hibernate.usertype.EnhancedUserType;

/**
 * Extends StringClobType to be an EnhancedUserType.
 *
 * @author esferalia Networks. atellitu - 16/04/2009
 */
public class StringClobEnhancedType extends StringClobType implements EnhancedUserType {

	@Override
	public Object fromXMLString(String xmlValue) {
		return xmlValue;
	}

	@Override
	public String objectToSQLString(Object value) {
		return (String) value;
	}

	@Override
	public String toXMLString(Object value) {
		return (String) value;
	}
	
}