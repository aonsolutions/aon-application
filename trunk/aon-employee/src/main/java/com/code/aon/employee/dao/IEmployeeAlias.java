package com.code.aon.employee.dao;

import com.code.aon.common.dao.DAOConstants;
import com.code.aon.common.dao.DAOConstantsEntry;
import com.code.aon.employee.Contract;
import com.code.aon.employee.ContractType;

/** 
* Interface for holding entity properties constants.
*/ 
public interface IEmployeeAlias {



	/** 
	* DAOConstantsEntry for Contract entity.
	*/ 
	DAOConstantsEntry CONTRACT_ENTRY = DAOConstants.getDAOConstant(Contract.class);

	/** 
	* Alias value: Contract_ccc_id
	* Hibernate value: Contract.ccc.id
	*/
	String  CONTRACT_CCC_ID = CONTRACT_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Contract_contractType_id
	* Hibernate value: Contract.contractType.id
	*/
	String  CONTRACT_CONTRACT_TYPE_ID = CONTRACT_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Contract_endDate
	* Hibernate value: Contract.endDate
	*/
	String  CONTRACT_END_DATE = CONTRACT_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Contract_id
	* Hibernate value: Contract.id
	*/
	String  CONTRACT_ID = CONTRACT_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Contract_person_id
	* Hibernate value: Contract.person.id
	*/
	String  CONTRACT_PERSON_ID = CONTRACT_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Contract_startDate
	* Hibernate value: Contract.startDate
	*/
	String  CONTRACT_START_DATE = CONTRACT_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: Contract_workPlace_id
	* Hibernate value: Contract.workPlace.id
	*/
	String  CONTRACT_WORK_PLACE_ID = CONTRACT_ENTRY.getAliasNames()[6];



	/** 
	* DAOConstantsEntry for ContractType entity.
	*/ 
	DAOConstantsEntry CONTRACT_TYPE_ENTRY = DAOConstants.getDAOConstant(ContractType.class);

	/** 
	* Alias value: ContractType_cccType
	* Hibernate value: ContractType.cccType
	*/
	String  CONTRACT_TYPE_CCC_TYPE = CONTRACT_TYPE_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: ContractType_description
	* Hibernate value: ContractType.description
	*/
	String  CONTRACT_TYPE_DESCRIPTION = CONTRACT_TYPE_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: ContractType_duration
	* Hibernate value: ContractType.duration
	*/
	String  CONTRACT_TYPE_DURATION = CONTRACT_TYPE_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: ContractType_id
	* Hibernate value: ContractType.id
	*/
	String  CONTRACT_TYPE_ID = CONTRACT_TYPE_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: ContractType_workingDay
	* Hibernate value: ContractType.workingDay
	*/
	String  CONTRACT_TYPE_WORKING_DAY = CONTRACT_TYPE_ENTRY.getAliasNames()[4];


}