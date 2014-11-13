package com.esferalia.aon.occam.api.model.type;

import java.io.Serializable;

import com.esferalia.aon.core.commons.AonCoreException;
import com.esferalia.aon.core.commons.AonError;

public enum AccountPeriodStatus implements Serializable {

	 ACTIVE
	,INACTIVE
	,OPENING
	,OPERATING
	,CLOSED;

	public static AccountPeriodStatus getValue(Byte value) {
		if (value == null) {
			throw new AonCoreException(
					AonError.ACCOUNT_PERIOD_TYPE_INVALID, "NULL");
		}
		try {
			return AccountPeriodStatus.values()[value];
		} catch (IndexOutOfBoundsException e) {
			throw new AonCoreException(
					AonError.ACCOUNT_PERIOD_TYPE_INVALID, value);
		}
	}

}