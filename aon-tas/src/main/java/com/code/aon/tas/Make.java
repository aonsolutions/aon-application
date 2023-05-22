package com.code.aon.tas;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import com.code.aon.AonVersion;
import com.esferalia.aon.entity.master.MakeDB;

@Entity
@Table(name="make")
public class Make extends MakeDB {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	@Transient
	public String getFullName() {
		return (getName() == null) ? "" : getName();
	}
	
}
