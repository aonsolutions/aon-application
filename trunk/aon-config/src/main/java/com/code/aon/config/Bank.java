package com.code.aon.config;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.esferalia.aon.entity.master.BankDB;

@Entity
@Table(name="bank")
public class Bank extends BankDB {
	
	private static final long serialVersionUID = 1L;
	
} 
