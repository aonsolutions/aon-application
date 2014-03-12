package com.esferalia.aon.pms;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.code.aon.common.AonVersion;
import com.code.aon.config.IScopable;
import com.code.aon.customer.Customer;
import com.esferalia.aon.entity.master.HotelDB;

@Entity
@Table(name="hotel")
public class Hotel extends HotelDB implements IScopable {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	@Transient
	public Customer getCustomer() {
		return getWorkPlace().getCustomer();
	}

}
