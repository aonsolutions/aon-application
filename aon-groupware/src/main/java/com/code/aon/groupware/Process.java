package com.code.aon.groupware;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import com.code.aon.AonVersion;
import com.esferalia.aon.entity.master.ProcessDB;

@Entity
@Table(name="process")
public class Process extends ProcessDB {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
    public Process() {
		setActive(true);
	}

}