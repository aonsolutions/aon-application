package com.esferalia.aon.occam.api.model.type;

import java.io.Serializable;

import com.esferalia.aon.core.commons.AonCoreException;
import com.esferalia.aon.core.commons.AonError;


public enum SecurityLevel implements Serializable{

	OFFICIAL,
	CONFIDENTIAL;
	
	public static SecurityLevel getValue(Byte value) {
		if (value == null) return null;
		try {
			return SecurityLevel.values()[value];
		} catch (IndexOutOfBoundsException e) {
			throw new AonCoreException(
					AonError.SECURITY_LEVEL_INVALID, value);
		}
		
	}
	
}