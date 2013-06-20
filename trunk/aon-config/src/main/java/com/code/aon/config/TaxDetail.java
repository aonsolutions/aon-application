package com.code.aon.config;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.code.aon.common.annotations.Heritable;
import com.esferalia.aon.entity.master.TaxDetailDB;

@Entity
@Table(name="tax_detail")
@Heritable
public class TaxDetail extends TaxDetailDB {
	
	private static final long serialVersionUID = 1L;
	
} 
