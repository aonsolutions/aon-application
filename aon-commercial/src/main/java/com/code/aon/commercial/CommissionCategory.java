package com.code.aon.commercial;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.esferalia.aon.entity.master.CommissionCategoryDB;

@Entity
@Table(name="commission_category")
public class CommissionCategory extends CommissionCategoryDB {

	private static final long serialVersionUID = 1L;

}
