package com.code.aon.config;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.esferalia.aon.entity.master.CNAE2009RateDB;

@Entity
@Table(name="cnae2009_rate")
public class CNAE2009Rate extends CNAE2009RateDB {
	
	private static final long serialVersionUID = 1L;
	
}
