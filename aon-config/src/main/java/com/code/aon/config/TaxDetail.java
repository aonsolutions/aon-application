package com.code.aon.config;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import com.code.aon.AonVersion;
import com.code.aon.common.annotations.Heritable;
import com.code.aon.common.audit.IAuditable;
import com.esferalia.aon.entity.master.TaxDetailDB;

@Entity
@Table(name="tax_detail")
@Heritable
public class TaxDetail extends TaxDetailDB implements IAuditable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
} 
