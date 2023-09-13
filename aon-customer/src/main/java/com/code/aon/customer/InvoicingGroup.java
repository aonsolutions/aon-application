package com.code.aon.customer;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import com.code.aon.AonVersion;
import com.code.aon.common.audit.IAuditable;
import com.esferalia.aon.entity.master.InvoicingGroupDB;

@Entity
@Table(name="invoicing_group", uniqueConstraints = @UniqueConstraint(columnNames="customer"))
public class InvoicingGroup extends InvoicingGroupDB implements IAuditable {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	public InvoicingGroup() {
		setCustomerGrouped(true);
	}

}