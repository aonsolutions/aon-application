package com.code.aon.ui.accounting.check;

import java.io.Serializable;

import com.code.aon.accounting.Period;
import com.code.aon.AonVersion;

public class CheckParams implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private Period period;

	public Period getPeriod() {
		return period;
	}

	public void setPeriod(Period period) {
		this.period = period;
	}
	
	
	
}
