package com.esferalia.aon.occam.api.model;

import java.io.Serializable;
import java.util.Date;

import com.esferalia.aon.occam.api.model.type.SecurityLevel;

public interface IAccountParams extends Serializable {
	
	int getDomain();
	Integer getPeriod();
	Date getFromDate();
	Date getToDate();
	Integer getActivity();
	SecurityLevel getSecurityLevel();
	
}
