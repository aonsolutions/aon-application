package com.esferalia.aon.payroll.dao;

import com.code.aon.common.dao.DAOConstants;
import com.code.aon.common.dao.DAOConstantsEntry;
import com.esferalia.aon.payroll.EnterpriseCertificate;
import com.esferalia.aon.payroll.EnterpriseCertificateDetail;

/** 
* Interface for holding entity properties constants.
*/ 
public interface IPayrollAlias {



	/** 
	* DAOConstantsEntry for EnterpriseCertificate entity.
	*/ 
	DAOConstantsEntry ENTERPRISE_CERTIFICATE_ENTRY = DAOConstants.getDAOConstant(EnterpriseCertificate.class);

	/** 
	* Alias value: EnterpriseCertificate_date
	* Hibernate value: EnterpriseCertificate.date
	*/
	String  ENTERPRISE_CERTIFICATE_DATE = ENTERPRISE_CERTIFICATE_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: EnterpriseCertificate_enterprise_id
	* Hibernate value: EnterpriseCertificate.enterprise.id
	*/
	String  ENTERPRISE_CERTIFICATE_ENTERPRISE_ID = ENTERPRISE_CERTIFICATE_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: EnterpriseCertificate_enterprise_registry_name
	* Hibernate value: EnterpriseCertificate.enterprise.registry.name
	*/
	String  ENTERPRISE_CERTIFICATE_ENTERPRISE_REGISTRY_NAME = ENTERPRISE_CERTIFICATE_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: EnterpriseCertificate_id
	* Hibernate value: EnterpriseCertificate.id
	*/
	String  ENTERPRISE_CERTIFICATE_ID = ENTERPRISE_CERTIFICATE_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: EnterpriseCertificate_sign
	* Hibernate value: EnterpriseCertificate.sign
	*/
	String  ENTERPRISE_CERTIFICATE_SIGN = ENTERPRISE_CERTIFICATE_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: EnterpriseCertificate_status
	* Hibernate value: EnterpriseCertificate.status
	*/
	String  ENTERPRISE_CERTIFICATE_STATUS = ENTERPRISE_CERTIFICATE_ENTRY.getAliasNames()[5];



	/** 
	* DAOConstantsEntry for EnterpriseCertificateDetail entity.
	*/ 
	DAOConstantsEntry ENTERPRISE_CERTIFICATE_DETAIL_ENTRY = DAOConstants.getDAOConstant(EnterpriseCertificateDetail.class);

	/** 
	* Alias value: EnterpriseCertificateDetail_contract_id
	* Hibernate value: EnterpriseCertificateDetail.contract.id
	*/
	String  ENTERPRISE_CERTIFICATE_DETAIL_CONTRACT_ID = ENTERPRISE_CERTIFICATE_DETAIL_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: EnterpriseCertificateDetail_enterpriseCertificate_id
	* Hibernate value: EnterpriseCertificateDetail.enterpriseCertificate.id
	*/
	String  ENTERPRISE_CERTIFICATE_DETAIL_ENTERPRISE_CERTIFICATE_ID = ENTERPRISE_CERTIFICATE_DETAIL_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: EnterpriseCertificateDetail_expireDate
	* Hibernate value: EnterpriseCertificateDetail.expireDate
	*/
	String  ENTERPRISE_CERTIFICATE_DETAIL_EXPIRE_DATE = ENTERPRISE_CERTIFICATE_DETAIL_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: EnterpriseCertificateDetail_id
	* Hibernate value: EnterpriseCertificateDetail.id
	*/
	String  ENTERPRISE_CERTIFICATE_DETAIL_ID = ENTERPRISE_CERTIFICATE_DETAIL_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: EnterpriseCertificateDetail_suspensionCause
	* Hibernate value: EnterpriseCertificateDetail.suspensionCause
	*/
	String  ENTERPRISE_CERTIFICATE_DETAIL_SUSPENSION_CAUSE = ENTERPRISE_CERTIFICATE_DETAIL_ENTRY.getAliasNames()[4];


}