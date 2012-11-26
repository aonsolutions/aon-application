package com.code.aon.commercial;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.esferalia.aon.entity.master.CommissionDB;

@Entity
@Table(name="commission")
public class Commission extends CommissionDB {

	private static final long serialVersionUID = 1L;

}
