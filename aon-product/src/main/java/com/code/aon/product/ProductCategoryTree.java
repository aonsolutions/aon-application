package com.code.aon.product;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.esferalia.aon.entity.master.ProductCategoryTreeDB;

@Entity
@Table(name="pcategory_tree")
public class ProductCategoryTree extends  ProductCategoryTreeDB {

	private static final long serialVersionUID = 1L;


}