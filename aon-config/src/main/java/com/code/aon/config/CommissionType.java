package com.code.aon.config;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.esferalia.aon.entity.master.CommissionTypeDB;

@Entity
@Table(name="commission_type")
public class CommissionType extends CommissionTypeDB {
	
	private static final long serialVersionUID = 1L;
	
} 
