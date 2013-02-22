package com.code.aon.product;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.code.aon.common.annotations.Heritable;
import com.esferalia.aon.entity.master.ProductCategoryGroupDB;

@Entity
@Table(name="pcategory_group")
@Heritable
public class ProductCategoryGroup extends ProductCategoryGroupDB {

	private static final long serialVersionUID = 1L;

}