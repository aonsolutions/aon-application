package com.code.aon.person.dao;

import com.code.aon.common.dao.DAOConstants;
import com.code.aon.common.dao.DAOConstantsEntry;
import com.code.aon.person.Person;

/** 
* Interface for holding entity properties constants.
*/ 
public interface IPersonAlias {



	/** 
	* DAOConstantsEntry for Person entity.
	*/ 
	DAOConstantsEntry PERSON_ENTRY = DAOConstants.getDAOConstant(Person.class);

	/** 
	* Alias value: Person_birthDate
	* Hibernate value: Person.birthDate
	*/
	String  PERSON_BIRTH_DATE = PERSON_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Person_gender
	* Hibernate value: Person.gender
	*/
	String  PERSON_GENDER = PERSON_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Person_id
	* Hibernate value: Person.id
	*/
	String  PERSON_ID = PERSON_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Person_maritalStatus
	* Hibernate value: Person.maritalStatus
	*/
	String  PERSON_MARITAL_STATUS = PERSON_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Person_registry_id
	* Hibernate value: Person.registry.id
	*/
	String  PERSON_REGISTRY_ID = PERSON_ENTRY.getAliasNames()[4];


}