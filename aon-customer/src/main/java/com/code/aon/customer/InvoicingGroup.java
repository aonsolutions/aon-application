package com.code.aon.customer;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.code.aon.AonVersion;
import com.esferalia.aon.entity.master.InvoicingGroupDB;

@Entity
@Table(name="invoicing_group", uniqueConstraints = @UniqueConstraint(columnNames="customer"))
public class InvoicingGroup extends InvoicingGroupDB {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	public InvoicingGroup() {
		setCustomerGrouped(true);
	}

}