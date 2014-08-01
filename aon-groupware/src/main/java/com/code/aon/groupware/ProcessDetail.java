package com.code.aon.groupware;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.code.aon.AonVersion;
import com.esferalia.aon.entity.master.ProcessDetailDB;

@Entity
@Table(name="process_detail")
public class ProcessDetail extends ProcessDetailDB {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	public ProcessDetail() {
		setActive(true);
	}
	
}