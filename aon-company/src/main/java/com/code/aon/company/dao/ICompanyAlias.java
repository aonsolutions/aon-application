package com.code.aon.company.dao;

import com.code.aon.common.dao.DAOConstants;
import com.code.aon.common.dao.DAOConstantsEntry;
import com.code.aon.company.Company;
import com.code.aon.company.WorkPlace;
import com.code.aon.company.Enterprise;
import com.code.aon.company.EnterpriseCCC;
import com.code.aon.company.EnterpriseData;
import com.code.aon.company.EnterpriseActivity;

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
	* Alias value: Company_documentCountry
	* Hibernate value: Company.documentCountry
	*/
	String  COMPANY_DOCUMENT_COUNTRY = COMPANY_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Company_documentType
	* Hibernate value: Company.documentType
	*/
	String  COMPANY_DOCUMENT_TYPE = COMPANY_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Company_id
	* Hibernate value: Company.id
	*/
	String  COMPANY_ID = COMPANY_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Company_name
	* Hibernate value: Company.name
	*/
	String  COMPANY_NAME = COMPANY_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: Company_nationality
	* Hibernate value: Company.nationality
	*/
	String  COMPANY_NATIONALITY = COMPANY_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: Company_type
	* Hibernate value: Company.type
	*/
	String  COMPANY_TYPE = COMPANY_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: Company_EInvoice
	* Hibernate value: Company.EInvoice
	*/
	String  COMPANY_EINVOICE = COMPANY_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: Company_active
	* Hibernate value: Company.active
	*/
	String  COMPANY_ACTIVE = COMPANY_ENTRY.getAliasNames()[9];

	/** 
	* Alias value: Company_surcharge
	* Hibernate value: Company.surcharge
	*/
	String  COMPANY_SURCHARGE = COMPANY_ENTRY.getAliasNames()[10];

	/** 
	* Alias value: Company_withholding
	* Hibernate value: Company.withholding
	*/
	String  COMPANY_WITHHOLDING = COMPANY_ENTRY.getAliasNames()[11];



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
	* Alias value: WorkPlace_calendar_id
	* Hibernate value: WorkPlace.calendar.id
	*/
	String  WORK_PLACE_CALENDAR_ID = WORK_PLACE_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: WorkPlace_description
	* Hibernate value: WorkPlace.description
	*/
	String  WORK_PLACE_DESCRIPTION = WORK_PLACE_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: WorkPlace_economicAgreement
	* Hibernate value: WorkPlace.economicAgreement
	*/
	String  WORK_PLACE_ECONOMIC_AGREEMENT = WORK_PLACE_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: WorkPlace_enterprise_id
	* Hibernate value: WorkPlace.enterprise.id
	*/
	String  WORK_PLACE_ENTERPRISE_ID = WORK_PLACE_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: WorkPlace_id
	* Hibernate value: WorkPlace.id
	*/
	String  WORK_PLACE_ID = WORK_PLACE_ENTRY.getAliasNames()[6];



	/** 
	* DAOConstantsEntry for Enterprise entity.
	*/ 
	DAOConstantsEntry ENTERPRISE_ENTRY = DAOConstants.getDAOConstant(Enterprise.class);

	/** 
	* Alias value: Enterprise_id
	* Hibernate value: Enterprise.id
	*/
	String  ENTERPRISE_ID = ENTERPRISE_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Enterprise_registry_id
	* Hibernate value: Enterprise.registry.id
	*/
	String  ENTERPRISE_REGISTRY_ID = ENTERPRISE_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Enterprise_scope_id
	* Hibernate value: Enterprise.scope.id
	*/
	String  ENTERPRISE_SCOPE_ID = ENTERPRISE_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Enterprise_registry_document
	* Hibernate value: Enterprise.registry.document
	*/
	String  ENTERPRISE_REGISTRY_DOCUMENT = ENTERPRISE_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Enterprise_registry_documentCountry
	* Hibernate value: Enterprise.registry.documentCountry
	*/
	String  ENTERPRISE_REGISTRY_DOCUMENT_COUNTRY = ENTERPRISE_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Enterprise_registry_documentType
	* Hibernate value: Enterprise.registry.documentType
	*/
	String  ENTERPRISE_REGISTRY_DOCUMENT_TYPE = ENTERPRISE_ENTRY.getAliasNames()[5];



	/** 
	* DAOConstantsEntry for EnterpriseCCC entity.
	*/ 
	DAOConstantsEntry ENTERPRISE_CCC_ENTRY = DAOConstants.getDAOConstant(EnterpriseCCC.class);

	/** 
	* Alias value: EnterpriseCCC_activity_id
	* Hibernate value: EnterpriseCCC.activity.id
	*/
	String  ENTERPRISE_CCC_ACTIVITY_ID = ENTERPRISE_CCC_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: EnterpriseCCC_ccc
	* Hibernate value: EnterpriseCCC.ccc
	*/
	String  ENTERPRISE_CCC_CCC = ENTERPRISE_CCC_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: EnterpriseCCC_geozone_id
	* Hibernate value: EnterpriseCCC.geozone.id
	*/
	String  ENTERPRISE_CCC_GEOZONE_ID = ENTERPRISE_CCC_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: EnterpriseCCC_id
	* Hibernate value: EnterpriseCCC.id
	*/
	String  ENTERPRISE_CCC_ID = ENTERPRISE_CCC_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: EnterpriseCCC_type
	* Hibernate value: EnterpriseCCC.type
	*/
	String  ENTERPRISE_CCC_TYPE = ENTERPRISE_CCC_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: EnterpriseCCC_activity_enterprise_id
	* Hibernate value: EnterpriseCCC.activity.enterprise.id
	*/
	String  ENTERPRISE_CCC_ACTIVITY_ENTERPRISE_ID = ENTERPRISE_CCC_ENTRY.getAliasNames()[5];



	/** 
	* DAOConstantsEntry for EnterpriseData entity.
	*/ 
	DAOConstantsEntry ENTERPRISE_DATA_ENTRY = DAOConstants.getDAOConstant(EnterpriseData.class);

	/** 
	* Alias value: EnterpriseData_endDate
	* Hibernate value: EnterpriseData.endDate
	*/
	String  ENTERPRISE_DATA_END_DATE = ENTERPRISE_DATA_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: EnterpriseData_enterprise_id
	* Hibernate value: EnterpriseData.enterprise.id
	*/
	String  ENTERPRISE_DATA_ENTERPRISE_ID = ENTERPRISE_DATA_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: EnterpriseData_expression
	* Hibernate value: EnterpriseData.expression
	*/
	String  ENTERPRISE_DATA_EXPRESSION = ENTERPRISE_DATA_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: EnterpriseData_id
	* Hibernate value: EnterpriseData.id
	*/
	String  ENTERPRISE_DATA_ID = ENTERPRISE_DATA_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: EnterpriseData_name
	* Hibernate value: EnterpriseData.name
	*/
	String  ENTERPRISE_DATA_NAME = ENTERPRISE_DATA_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: EnterpriseData_startDate
	* Hibernate value: EnterpriseData.startDate
	*/
	String  ENTERPRISE_DATA_START_DATE = ENTERPRISE_DATA_ENTRY.getAliasNames()[5];



	/** 
	* DAOConstantsEntry for EnterpriseActivity entity.
	*/ 
	DAOConstantsEntry ENTERPRISE_ACTIVITY_ENTRY = DAOConstants.getDAOConstant(EnterpriseActivity.class);

	/** 
	* Alias value: EnterpriseActivity_cnae_id
	* Hibernate value: EnterpriseActivity.cnae.id
	*/
	String  ENTERPRISE_ACTIVITY_CNAE_ID = ENTERPRISE_ACTIVITY_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: EnterpriseActivity_description
	* Hibernate value: EnterpriseActivity.description
	*/
	String  ENTERPRISE_ACTIVITY_DESCRIPTION = ENTERPRISE_ACTIVITY_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: EnterpriseActivity_enterprise_id
	* Hibernate value: EnterpriseActivity.enterprise.id
	*/
	String  ENTERPRISE_ACTIVITY_ENTERPRISE_ID = ENTERPRISE_ACTIVITY_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: EnterpriseActivity_id
	* Hibernate value: EnterpriseActivity.id
	*/
	String  ENTERPRISE_ACTIVITY_ID = ENTERPRISE_ACTIVITY_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: EnterpriseActivity_type
	* Hibernate value: EnterpriseActivity.type
	*/
	String  ENTERPRISE_ACTIVITY_TYPE = ENTERPRISE_ACTIVITY_ENTRY.getAliasNames()[4];


}