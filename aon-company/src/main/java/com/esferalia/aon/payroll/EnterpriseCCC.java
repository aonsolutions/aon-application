package com.esferalia.aon.payroll;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import com.code.aon.AonVersion;
import com.esferalia.aon.entity.master.EnterpriseCCCDB;

@Entity
@Table(name="enterprise_ccc")
public class EnterpriseCCC extends EnterpriseCCCDB {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	

}
