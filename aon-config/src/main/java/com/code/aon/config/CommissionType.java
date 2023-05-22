package com.code.aon.config;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import com.code.aon.AonVersion;
import com.esferalia.aon.entity.master.CommissionTypeDB;

@Entity
@Table(name="commission_type")
public class CommissionType extends CommissionTypeDB {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
} 
