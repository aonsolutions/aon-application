package com.code.aon.registry;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.code.aon.common.annotations.Heritable;
import com.esferalia.aon.entity.master.CategoryDB;

@Entity
@Table(name="category")
@Heritable
public class Category extends CategoryDB  {

	private static final long serialVersionUID = 1L;

}