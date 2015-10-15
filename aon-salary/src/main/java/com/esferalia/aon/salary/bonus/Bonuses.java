package com.esferalia.aon.salary.bonus;


import java.io.Serializable;

import com.code.aon.AonVersion;

public class Bonuses implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	double total;

	public Double getTotal() {
		return this.total;  
	}
	
	public void setTotal(double total) {
		this.total = total;
	}

}
