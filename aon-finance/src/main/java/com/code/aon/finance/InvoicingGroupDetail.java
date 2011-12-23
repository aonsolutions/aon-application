package com.code.aon.finance;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.esferalia.aon.entity.master.InvoicingGroupDetailDB;

@Entity
@Table(name="invoicing_group_detail", uniqueConstraints = @UniqueConstraint(columnNames="child"))
public class InvoicingGroupDetail extends InvoicingGroupDetailDB {
	
	private static final long serialVersionUID = 1L;

}
