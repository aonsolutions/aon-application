package com.esferalia.aon.occam.api.model.type;

import java.io.Serializable;

import com.esferalia.aon.watson.AonCoreException;
import com.esferalia.aon.watson.AonError;

public enum AccountPeriodStatus implements Serializable {

	 ACTIVE
	,INACTIVE
	,OPENING
	,OPERATING
	,CLOSED;

}