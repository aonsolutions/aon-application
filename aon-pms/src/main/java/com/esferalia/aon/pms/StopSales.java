package com.esferalia.aon.pms;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.code.aon.common.audit.IAuditable;
import com.esferalia.aon.entity.master.StopSalesDB;

@Entity
@Table(name="stop_sales")
public class StopSales extends StopSalesDB implements IAuditable {

	private static final long serialVersionUID = 1L;

	public StopSales() {
		setActive(true);
	}

}
