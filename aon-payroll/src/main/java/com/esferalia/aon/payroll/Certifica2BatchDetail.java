package com.esferalia.aon.payroll;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.code.aon.AonVersion;
import com.esferalia.aon.entity.master.Certifica2BatchDetailDB;

@Entity
@Table(name="certifica2_batch_detail")
public class Certifica2BatchDetail extends Certifica2BatchDetailDB {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private String ereNumber = null; 
	
	@Transient
	public String getEreNumber() {
		return ereNumber;
	}
	
	@Transient
	public void setEreNumber(String ereNumber) {
		this.ereNumber = ereNumber;
	}

}


