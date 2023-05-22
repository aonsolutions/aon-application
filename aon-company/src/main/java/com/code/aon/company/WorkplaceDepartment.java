package com.code.aon.company;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import com.code.aon.AonVersion;
import com.esferalia.aon.entity.master.WorkplaceDepartmentDB;

@Entity
@Table(name="workplace_department")
public class WorkplaceDepartment extends WorkplaceDepartmentDB {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	public WorkplaceDepartment() {
		setActive(true);
	}

}
