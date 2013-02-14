package com.code.aon.finance;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.esferalia.aon.entity.master.InvoicingGroupDB;

@Entity
@Table(name="invoicing_group", uniqueConstraints = @UniqueConstraint(columnNames="parent"))
public class InvoicingGroup extends InvoicingGroupDB {

	private static final long serialVersionUID = 1L;

}