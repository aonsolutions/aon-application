package com.code.aon.config;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.code.aon.common.AonVersion;
import com.code.aon.common.annotations.Heritable;
import com.esferalia.aon.entity.master.TariffDB;

@Entity
@Table(name="tariff")
@Heritable
public class Tariff extends TariffDB {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
} 
