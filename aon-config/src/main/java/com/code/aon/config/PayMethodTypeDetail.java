package com.code.aon.config;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.code.aon.account.IAccount;
import com.esferalia.aon.entity.master.PayMethodTypeDetailDB;

@Entity
@Table(name="pm_type_detail")
public class PayMethodTypeDetail extends PayMethodTypeDetailDB implements IAccount {
	
	private static final long serialVersionUID = 1L;
	
} 
