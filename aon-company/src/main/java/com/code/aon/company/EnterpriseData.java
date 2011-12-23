package com.code.aon.company;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.esferalia.aon.entity.master.EnterpriseDataDB;

@Entity
@Table(name="enterprise_data")
public class EnterpriseData extends EnterpriseDataDB {
	
	private static final long serialVersionUID = 1L;

}