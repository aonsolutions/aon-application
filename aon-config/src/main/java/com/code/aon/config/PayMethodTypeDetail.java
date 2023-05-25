package com.code.aon.config;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import com.code.aon.account.IAccount;
import com.code.aon.AonVersion;
import com.esferalia.aon.entity.master.PayMethodTypeDetailDB;

@Entity
@Table(name="pm_type_detail")
public class PayMethodTypeDetail extends PayMethodTypeDetailDB implements IAccount {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
} 
