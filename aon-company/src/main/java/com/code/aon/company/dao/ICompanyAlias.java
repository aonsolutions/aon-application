package com.code.aon.company.dao;

import com.code.aon.common.dao.DAOConstants;
import com.code.aon.common.dao.DAOConstantsEntry;
import com.code.aon.company.Agreement;
import com.code.aon.company.AgreementLevel;
import com.code.aon.company.AgreementLevelCategory;
import com.code.aon.company.AgreementLevelPayment;
import com.code.aon.company.Company;
import com.code.aon.company.resources.Employee;
import com.code.aon.company.resources.Resource;
import com.code.aon.company.WorkPlace;
import com.code.aon.company.WorkActivity;
import com.code.aon.company.Enterprise;
import com.code.aon.company.EnterpriseCCC;
import com.code.aon.company.CNAE;
import com.code.aon.company.EnterpriseActivity;

/** 
* Interface for holding entity properties constants.
*/ 
public interface ICompanyAlias {



	/** 
	* DAOConstantsEntry for Agreement entity.
	*/ 
	DAOConstantsEntry AGREEMENT_ENTRY = DAOConstants.getDAOConstant(Agreement.class);

	/** 
	* Alias value: Agreement_calendar_id
	* Hibernate value: Agreement.calendar.id
	*/
	String  AGREEMENT_CALENDAR_ID = AGREEMENT_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Agreement_description
	* Hibernate value: Agreement.description
	*/
	String  AGREEMENT_DESCRIPTION = AGREEMENT_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Agreement_id
	* Hibernate value: Agreement.id
	*/
	String  AGREEMENT_ID = AGREEMENT_ENTRY.getAliasNames()[2];



	/** 
	* DAOConstantsEntry for AgreementLevel entity.
	*/ 
	DAOConstantsEntry AGREEMENT_LEVEL_ENTRY = DAOConstants.getDAOConstant(AgreementLevel.class);

	/** 
	* Alias value: AgreementLevel_agreement_id
	* Hibernate value: AgreementLevel.agreement.id
	*/
	String  AGREEMENT_LEVEL_AGREEMENT_ID = AGREEMENT_LEVEL_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: AgreementLevel_description
	* Hibernate value: AgreementLevel.description
	*/
	String  AGREEMENT_LEVEL_DESCRIPTION = AGREEMENT_LEVEL_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: AgreementLevel_id
	* Hibernate value: AgreementLevel.id
	*/
	String  AGREEMENT_LEVEL_ID = AGREEMENT_LEVEL_ENTRY.getAliasNames()[2];



	/** 
	* DAOConstantsEntry for AgreementLevelCategory entity.
	*/ 
	DAOConstantsEntry AGREEMENT_LEVEL_CATEGORY_ENTRY = DAOConstants.getDAOConstant(AgreementLevelCategory.class);

	/** 
	* Alias value: AgreementLevelCategory_description
	* Hibernate value: AgreementLevelCategory.description
	*/
	String  AGREEMENT_LEVEL_CATEGORY_DESCRIPTION = AGREEMENT_LEVEL_CATEGORY_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: AgreementLevelCategory_id
	* Hibernate value: AgreementLevelCategory.id
	*/
	String  AGREEMENT_LEVEL_CATEGORY_ID = AGREEMENT_LEVEL_CATEGORY_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: AgreementLevelCategory_level_id
	* Hibernate value: AgreementLevelCategory.level.id
	*/
	String  AGREEMENT_LEVEL_CATEGORY_LEVEL_ID = AGREEMENT_LEVEL_CATEGORY_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: AgreementLevelCategory_level_agreement_id
	* Hibernate value: AgreementLevelCategory.level.agreement.id
	*/
	String  AGREEMENT_LEVEL_CATEGORY_LEVEL_AGREEMENT_ID = AGREEMENT_LEVEL_CATEGORY_ENTRY.getAliasNames()[3];



	/** 
	* DAOConstantsEntry for AgreementLevelPayment entity.
	*/ 
	DAOConstantsEntry AGREEMENT_LEVEL_PAYMENT_ENTRY = DAOConstants.getDAOConstant(AgreementLevelPayment.class);

	/** 
	* Alias value: AgreementLevelPayment_description
	* Hibernate value: AgreementLevelPayment.description
	*/
	String  AGREEMENT_LEVEL_PAYMENT_DESCRIPTION = AGREEMENT_LEVEL_PAYMENT_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: AgreementLevelPayment_expression
	* Hibernate value: AgreementLevelPayment.expression
	*/
	String  AGREEMENT_LEVEL_PAYMENT_EXPRESSION = AGREEMENT_LEVEL_PAYMENT_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: AgreementLevelPayment_id
	* Hibernate value: AgreementLevelPayment.id
	*/
	String  AGREEMENT_LEVEL_PAYMENT_ID = AGREEMENT_LEVEL_PAYMENT_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: AgreementLevelPayment_level_id
	* Hibernate value: AgreementLevelPayment.level.id
	*/
	String  AGREEMENT_LEVEL_PAYMENT_LEVEL_ID = AGREEMENT_LEVEL_PAYMENT_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: AgreementLevelPayment_type
	* Hibernate value: AgreementLevelPayment.type
	*/
	String  AGREEMENT_LEVEL_PAYMENT_TYPE = AGREEMENT_LEVEL_PAYMENT_ENTRY.getAliasNames()[4];



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
	* Alias value: Employee_workActivity_id
	* Hibernate value: Employee.workActivity.id
	*/
	String  EMPLOYEE_WORK_ACTIVITY_ID = EMPLOYEE_ENTRY.getAliasNames()[6];

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
	* DAOConstantsEntry for WorkActivity entity.
	*/ 
	DAOConstantsEntry WORK_ACTIVITY_ENTRY = DAOConstants.getDAOConstant(WorkActivity.class);

	/** 
	* Alias value: WorkActivity_active
	* Hibernate value: WorkActivity.active
	*/
	String  WORK_ACTIVITY_ACTIVE = WORK_ACTIVITY_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: WorkActivity_description
	* Hibernate value: WorkActivity.description
	*/
	String  WORK_ACTIVITY_DESCRIPTION = WORK_ACTIVITY_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: WorkActivity_enterpriseCCC_id
	* Hibernate value: WorkActivity.enterpriseCCC.id
	*/
	String  WORK_ACTIVITY_ENTERPRISE_CCC_ID = WORK_ACTIVITY_ENTRY.getAliasNames()[2];

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
	* DAOConstantsEntry for CNAE entity.
	*/ 
	DAOConstantsEntry CNAE_ENTRY = DAOConstants.getDAOConstant(CNAE.class);

	/** 
	* Alias value: CNAE_code
	* Hibernate value: CNAE.code
	*/
	String  CNAE_CODE = CNAE_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: CNAE_id
	* Hibernate value: CNAE.id
	*/
	String  CNAE_ID = CNAE_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: CNAE_title
	* Hibernate value: CNAE.title
	*/
	String  CNAE_TITLE = CNAE_ENTRY.getAliasNames()[2];



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