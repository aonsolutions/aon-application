package com.code.aon.company.dao;

import com.code.aon.common.dao.DAOConstants;
import com.code.aon.common.dao.DAOConstantsEntry;
import com.code.aon.company.Company;
import com.code.aon.company.resources.Employee;
import com.code.aon.company.resources.Resource;
import com.code.aon.company.WorkPlace;
import com.code.aon.company.WorkActivity;

/** 
* Interface for holding entity properties constants.
*/ 
public interface ICompanyAlias {



	/** 
	* DAOConstantsEntry for Company entity.
	*/ 
	DAOConstantsEntry COMPANY_ENTRY = DAOConstants.getDAOConstant(Company.class);

	/** 
	* Alias value: Company_alias
	* Hibernate value: Company.alias
	*/
	String  COMPANY_ALIAS = COMPANY_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Company_document
	* Hibernate value: Company.document
	*/
	String  COMPANY_DOCUMENT = COMPANY_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Company_id
	* Hibernate value: Company.id
	*/
	String  COMPANY_ID = COMPANY_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Company_name
	* Hibernate value: Company.name
	*/
	String  COMPANY_NAME = COMPANY_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Company_surname
	* Hibernate value: Company.surname
	*/
	String  COMPANY_SURNAME = COMPANY_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Company_type
	* Hibernate value: Company.type
	*/
	String  COMPANY_TYPE = COMPANY_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: Company_EInvoice
	* Hibernate value: Company.EInvoice
	*/
	String  COMPANY_EINVOICE = COMPANY_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: Company_active
	* Hibernate value: Company.active
	*/
	String  COMPANY_ACTIVE = COMPANY_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: Company_calendar
	* Hibernate value: Company.calendar
	*/
	String  COMPANY_CALENDAR = COMPANY_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: Company_surcharge
	* Hibernate value: Company.surcharge
	*/
	String  COMPANY_SURCHARGE = COMPANY_ENTRY.getAliasNames()[9];

	/** 
	* Alias value: Company_withholding
	* Hibernate value: Company.withholding
	*/
	String  COMPANY_WITHHOLDING = COMPANY_ENTRY.getAliasNames()[10];



	/** 
	* DAOConstantsEntry for Employee entity.
	*/ 
	DAOConstantsEntry EMPLOYEE_ENTRY = DAOConstants.getDAOConstant(Employee.class);

	/** 
	* Alias value: Employee_id
	* Hibernate value: Employee.id
	*/
	String  EMPLOYEE_ID = EMPLOYEE_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Employee_registry_id
	* Hibernate value: Employee.registry.id
	*/
	String  EMPLOYEE_REGISTRY_ID = EMPLOYEE_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Employee_registry_name
	* Hibernate value: Employee.registry.name
	*/
	String  EMPLOYEE_REGISTRY_NAME = EMPLOYEE_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Employee_registry_surname
	* Hibernate value: Employee.registry.surname
	*/
	String  EMPLOYEE_REGISTRY_SURNAME = EMPLOYEE_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Employee_registry_alias
	* Hibernate value: Employee.registry.alias
	*/
	String  EMPLOYEE_REGISTRY_ALIAS = EMPLOYEE_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Employee_registry_document
	* Hibernate value: Employee.registry.document
	*/
	String  EMPLOYEE_REGISTRY_DOCUMENT = EMPLOYEE_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: Employee_calendar
	* Hibernate value: Employee.calendar
	*/
	String  EMPLOYEE_CALENDAR = EMPLOYEE_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: Employee_socialSecurityNumber
	* Hibernate value: Employee.socialSecurityNumber
	*/
	String  EMPLOYEE_SOCIAL_SECURITY_NUMBER = EMPLOYEE_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: Employee_agreementTime
	* Hibernate value: Employee.agreementTime
	*/
	String  EMPLOYEE_AGREEMENT_TIME = EMPLOYEE_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: Employee_active
	* Hibernate value: Employee.active
	*/
	String  EMPLOYEE_ACTIVE = EMPLOYEE_ENTRY.getAliasNames()[9];



	/** 
	* DAOConstantsEntry for Resource entity.
	*/ 
	DAOConstantsEntry RESOURCE_ENTRY = DAOConstants.getDAOConstant(Resource.class);

	/** 
	* Alias value: Resource_employee_id
	* Hibernate value: Resource.employee.id
	*/
	String  RESOURCE_EMPLOYEE_ID = RESOURCE_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Resource_endingDate
	* Hibernate value: Resource.endingDate
	*/
	String  RESOURCE_ENDING_DATE = RESOURCE_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Resource_id
	* Hibernate value: Resource.id
	*/
	String  RESOURCE_ID = RESOURCE_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Resource_startingDate
	* Hibernate value: Resource.startingDate
	*/
	String  RESOURCE_STARTING_DATE = RESOURCE_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Resource_workActivity_id
	* Hibernate value: Resource.workActivity.id
	*/
	String  RESOURCE_WORK_ACTIVITY_ID = RESOURCE_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Resource_workPlace_id
	* Hibernate value: Resource.workPlace.id
	*/
	String  RESOURCE_WORK_PLACE_ID = RESOURCE_ENTRY.getAliasNames()[5];



	/** 
	* DAOConstantsEntry for WorkPlace entity.
	*/ 
	DAOConstantsEntry WORK_PLACE_ENTRY = DAOConstants.getDAOConstant(WorkPlace.class);

	/** 
	* Alias value: WorkPlace_active
	* Hibernate value: WorkPlace.active
	*/
	String  WORK_PLACE_ACTIVE = WORK_PLACE_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: WorkPlace_address_id
	* Hibernate value: WorkPlace.address.id
	*/
	String  WORK_PLACE_ADDRESS_ID = WORK_PLACE_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: WorkPlace_calendar
	* Hibernate value: WorkPlace.calendar
	*/
	String  WORK_PLACE_CALENDAR = WORK_PLACE_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: WorkPlace_description
	* Hibernate value: WorkPlace.description
	*/
	String  WORK_PLACE_DESCRIPTION = WORK_PLACE_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: WorkPlace_id
	* Hibernate value: WorkPlace.id
	*/
	String  WORK_PLACE_ID = WORK_PLACE_ENTRY.getAliasNames()[4];



	/** 
	* DAOConstantsEntry for WorkActivity entity.
	*/ 
	DAOConstantsEntry WORK_ACTIVITY_ENTRY = DAOConstants.getDAOConstant(WorkActivity.class);

	/** 
	* Alias value: WorkActivity_active
	* Hibernate value: WorkActivity.active
	*/
	String  WORK_ACTIVITY_ACTIVE = WORK_ACTIVITY_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: WorkActivity_calendar
	* Hibernate value: WorkActivity.calendar
	*/
	String  WORK_ACTIVITY_CALENDAR = WORK_ACTIVITY_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: WorkActivity_description
	* Hibernate value: WorkActivity.description
	*/
	String  WORK_ACTIVITY_DESCRIPTION = WORK_ACTIVITY_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: WorkActivity_id
	* Hibernate value: WorkActivity.id
	*/
	String  WORK_ACTIVITY_ID = WORK_ACTIVITY_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: WorkActivity_workPlace_id
	* Hibernate value: WorkActivity.workPlace.id
	*/
	String  WORK_ACTIVITY_WORK_PLACE_ID = WORK_ACTIVITY_ENTRY.getAliasNames()[4];


}