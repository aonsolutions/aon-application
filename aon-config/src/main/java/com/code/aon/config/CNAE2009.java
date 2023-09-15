package com.code.aon.config;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import com.code.aon.AonVersion;
import com.esferalia.aon.entity.master.CNAE2009DB;

@Entity
@Table(name="cnae2009")
public class CNAE2009 extends CNAE2009DB {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
}
