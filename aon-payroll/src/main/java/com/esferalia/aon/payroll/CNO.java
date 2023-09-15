package com.esferalia.aon.payroll;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import com.code.aon.AonVersion;
import com.esferalia.aon.entity.master.CNODB;

@Entity
@Table(name = "cno")
public class CNO extends CNODB {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	@Transient
	public String getFullDescription() {
		return getId() != null ? ("(" + getCode() + ") " + getTitle()) : "";
	}

}
