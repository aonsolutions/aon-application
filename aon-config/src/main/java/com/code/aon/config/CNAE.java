package com.code.aon.config;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.esferalia.aon.entity.master.CNAEDB;

@Entity
@Table(name="cnae")
public class CNAE extends CNAEDB {

	private static final long serialVersionUID = 1L;

}
