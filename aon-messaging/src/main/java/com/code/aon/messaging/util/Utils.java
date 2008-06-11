package com.code.aon.messaging.util;

public class Utils {

	public static final String parsePhoneNumber(String phone) {
		StringBuffer _phone = new StringBuffer();
		char[] array = phone.trim().toCharArray();
		if ( array[0] == '+' )
			_phone.append( array[0] );
		for (int i = 0; i < array.length; i++) {
			if ( array[ i ] >= '0' && array[ i ] <= '9' )
				_phone.append( array[ i ] ); 
		}
		return _phone.toString();
	}

}
