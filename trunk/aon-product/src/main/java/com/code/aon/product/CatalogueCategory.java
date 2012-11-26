package com.code.aon.product;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.esferalia.aon.entity.master.CatalogueCategoryDB;

@Entity
@Table(name="catalogue_category")
public class CatalogueCategory extends CatalogueCategoryDB {

	private static final long serialVersionUID = 1L;

}
