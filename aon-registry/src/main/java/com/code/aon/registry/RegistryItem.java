package com.code.aon.registry;


import javax.persistence.Entity;
import javax.persistence.Table;

import com.esferalia.aon.entity.master.RegistryItemDB;

@Entity
@Table(name="ritem")
public class RegistryItem extends RegistryItemDB {
	
	private static final long serialVersionUID = 1L;

}