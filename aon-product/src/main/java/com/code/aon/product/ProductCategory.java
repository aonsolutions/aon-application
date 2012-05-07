package com.code.aon.product;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.esferalia.aon.entity.master.ProductCategoryDB;

@Entity
@Table(name="pcategory")
public final class ProductCategory extends ProductCategoryDB {

	private static final long serialVersionUID = 1L;

}