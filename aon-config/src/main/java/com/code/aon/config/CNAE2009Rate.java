package com.code.aon.config;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import com.code.aon.AonVersion;
import com.esferalia.aon.entity.master.CNAE2009RateDB;

@Entity
@Table(name="cnae2009_rate")
public class CNAE2009Rate extends CNAE2009RateDB {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
}
