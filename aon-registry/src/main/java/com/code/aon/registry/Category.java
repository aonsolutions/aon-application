package com.code.aon.registry;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import com.code.aon.AonVersion;
import com.code.aon.common.annotations.Heritable;
import com.code.aon.registry.enumeration.CategoryType;
import com.esferalia.aon.entity.master.CategoryDB;

@Entity
@Table(name="category")
@Heritable
public class Category extends CategoryDB  {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
    public Category() {
    	setType( CategoryType.REGISTRY_ATTACHMENT );
    }	

}