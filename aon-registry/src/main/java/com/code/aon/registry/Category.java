package com.code.aon.registry;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.esferalia.aon.entity.master.CategoryDB;

@Entity
@Table(name="category")
public class Category extends CategoryDB  {

	private static final long serialVersionUID = 1L;

}