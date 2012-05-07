package com.code.aon.commercial;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.esferalia.aon.entity.master.CommissionItemDB;

@Entity
@Table(name="commission_item")
public class CommissionItem extends CommissionItemDB {

	private static final long serialVersionUID = 1L;

}
