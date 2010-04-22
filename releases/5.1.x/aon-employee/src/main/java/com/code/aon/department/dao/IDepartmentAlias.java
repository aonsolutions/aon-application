package com.code.aon.department.dao;

import com.code.aon.common.dao.DAOConstants;
import com.code.aon.common.dao.DAOConstantsEntry;
import com.code.aon.department.Department;

public interface IDepartmentAlias {


	DAOConstantsEntry DEPARTMENT_ENTRY = DAOConstants.getDAOConstant(Department.class);

	// Alias value: Department_description
	// Hibernate value: Department.description
	String  DEPARTMENT_DESCRIPTION = DEPARTMENT_ENTRY.getAliasNames()[0];

	// Alias value: Department_id
	// Hibernate value: Department.id
	String  DEPARTMENT_ID = DEPARTMENT_ENTRY.getAliasNames()[1];

	// Alias value: Department_parent_id
	// Hibernate value: Department.parent.id
	String  DEPARTMENT_PARENT_ID = DEPARTMENT_ENTRY.getAliasNames()[2];


}