package com.esferalia.aon.payroll;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.esferalia.aon.entity.master.CraBatchDetailDB;

@Entity
@Table(name="cra_batch_detail")
public class CraBatchDetail extends CraBatchDetailDB {
	
	private static final long serialVersionUID = 1L;

}
