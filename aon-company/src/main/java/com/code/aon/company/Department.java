package com.code.aon.company;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import com.code.aon.AonVersion;
import com.esferalia.aon.entity.master.DepartmentDB;

@Entity
@Table(name="department")
public class Department extends DepartmentDB {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

}
