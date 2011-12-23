package com.code.aon.config;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.esferalia.aon.entity.master.TariffDB;

@Entity
@Table(name="tariff")
public class Tariff extends TariffDB {
	
	private static final long serialVersionUID = 1L;
	
} 
