package com.code.aon.company;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.code.aon.common.AonVersion;
import com.esferalia.aon.entity.master.EnterpriseDataDB;

@Entity
@Table(name="enterprise_data")
public class EnterpriseData extends EnterpriseDataDB {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

}