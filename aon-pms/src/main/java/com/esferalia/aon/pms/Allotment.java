package com.esferalia.aon.pms;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.code.aon.common.audit.IAuditable;
import com.esferalia.aon.entity.master.AllotmentDB;

@Entity
@Table(name="allotment")
public class Allotment extends AllotmentDB implements IAuditable {

	private static final long serialVersionUID = 1L;

	public Allotment() {
		setActive(true);
	}

}
