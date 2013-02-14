package com.code.aon.product;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.esferalia.aon.entity.master.ProductCategoryGroupDB;

@Entity
@Table(name="pcategory_group")
public class ProductCategoryGroup extends ProductCategoryGroupDB {

	private static final long serialVersionUID = 1L;

}