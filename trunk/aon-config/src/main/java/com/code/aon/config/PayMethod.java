package com.code.aon.config;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.code.aon.common.annotations.Heritable;
import com.esferalia.aon.entity.master.PayMethodDB;

@Entity
@Table(name="pay_method")
@Heritable
public class PayMethod extends PayMethodDB {
	
	private static final long serialVersionUID = 1L;
	
} 
