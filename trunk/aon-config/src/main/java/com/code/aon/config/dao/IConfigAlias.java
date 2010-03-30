package com.code.aon.config.dao;

import com.code.aon.common.dao.DAOConstants;
import com.code.aon.common.dao.DAOConstantsEntry;
import com.code.aon.config.ApplicationParameter;
import com.code.aon.config.Bank;
import com.code.aon.config.CommissionType;
import com.code.aon.config.PayMethod;
import com.code.aon.config.Scope;
import com.code.aon.config.Series;
import com.code.aon.config.Tariff;
import com.code.aon.config.Tax;
import com.code.aon.config.TaxDetail;
import com.code.aon.config.User;
import com.code.aon.config.UserScope;
import com.code.aon.config.UserWorkGroup;
import com.code.aon.config.WorkGroup;

/** 
* Interface for holding entity properties constants.
*/ 
public interface IConfigAlias {



	/** 
	* DAOConstantsEntry for ApplicationParameter entity.
	*/ 
	DAOConstantsEntry APPLICATION_PARAMETER_ENTRY = DAOConstants.getDAOConstant(ApplicationParameter.class);

	/** 
	* Alias value: ApplicationParameter_name
	* Hibernate value: ApplicationParameter.name
	*/
	String  APPLICATION_PARAMETER_NAME = APPLICATION_PARAMETER_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: ApplicationParameter_value
	* Hibernate value: ApplicationParameter.value
	*/
	String  APPLICATION_PARAMETER_VALUE = APPLICATION_PARAMETER_ENTRY.getAliasNames()[1];



	/** 
	* DAOConstantsEntry for Bank entity.
	*/ 
	DAOConstantsEntry BANK_ENTRY = DAOConstants.getDAOConstant(Bank.class);

	/** 
	* Alias value: Bank_code
	* Hibernate value: Bank.code
	*/
	String  BANK_CODE = BANK_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Bank_id
	* Hibernate value: Bank.id
	*/
	String  BANK_ID = BANK_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Bank_name
	* Hibernate value: Bank.name
	*/
	String  BANK_NAME = BANK_ENTRY.getAliasNames()[2];



	/** 
	* DAOConstantsEntry for CommissionType entity.
	*/ 
	DAOConstantsEntry COMMISSION_TYPE_ENTRY = DAOConstants.getDAOConstant(CommissionType.class);

	/** 
	* Alias value: CommissionType_id
	* Hibernate value: CommissionType.id
	*/
	String  COMMISSION_TYPE_ID = COMMISSION_TYPE_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: CommissionType_name
	* Hibernate value: CommissionType.name
	*/
	String  COMMISSION_TYPE_NAME = COMMISSION_TYPE_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: CommissionType_rate
	* Hibernate value: CommissionType.rate
	*/
	String  COMMISSION_TYPE_RATE = COMMISSION_TYPE_ENTRY.getAliasNames()[2];



	/** 
	* DAOConstantsEntry for PayMethod entity.
	*/ 
	DAOConstantsEntry PAY_METHOD_ENTRY = DAOConstants.getDAOConstant(PayMethod.class);

	/** 
	* Alias value: PayMethod_id
	* Hibernate value: PayMethod.id
	*/
	String  PAY_METHOD_ID = PAY_METHOD_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: PayMethod_name
	* Hibernate value: PayMethod.name
	*/
	String  PAY_METHOD_NAME = PAY_METHOD_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: PayMethod_type
	* Hibernate value: PayMethod.type
	*/
	String  PAY_METHOD_TYPE = PAY_METHOD_ENTRY.getAliasNames()[2];



	/** 
	* DAOConstantsEntry for Scope entity.
	*/ 
	DAOConstantsEntry SCOPE_ENTRY = DAOConstants.getDAOConstant(Scope.class);

	/** 
	* Alias value: Scope_description
	* Hibernate value: Scope.description
	*/
	String  SCOPE_DESCRIPTION = SCOPE_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Scope_id
	* Hibernate value: Scope.id
	*/
	String  SCOPE_ID = SCOPE_ENTRY.getAliasNames()[1];



	/** 
	* DAOConstantsEntry for Series entity.
	*/ 
	DAOConstantsEntry SERIES_ENTRY = DAOConstants.getDAOConstant(Series.class);

	/** 
	* Alias value: Series_active
	* Hibernate value: Series.active
	*/
	String  SERIES_ACTIVE = SERIES_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Series_description
	* Hibernate value: Series.description
	*/
	String  SERIES_DESCRIPTION = SERIES_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Series_id
	* Hibernate value: Series.id
	*/
	String  SERIES_ID = SERIES_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Series_securityLevel
	* Hibernate value: Series.securityLevel
	*/
	String  SERIES_SECURITY_LEVEL = SERIES_ENTRY.getAliasNames()[3];



	/** 
	* DAOConstantsEntry for Tariff entity.
	*/ 
	DAOConstantsEntry TARIFF_ENTRY = DAOConstants.getDAOConstant(Tariff.class);

	/** 
	* Alias value: Tariff_id
	* Hibernate value: Tariff.id
	*/
	String  TARIFF_ID = TARIFF_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Tariff_name
	* Hibernate value: Tariff.name
	*/
	String  TARIFF_NAME = TARIFF_ENTRY.getAliasNames()[1];



	/** 
	* DAOConstantsEntry for Tax entity.
	*/ 
	DAOConstantsEntry TAX_ENTRY = DAOConstants.getDAOConstant(Tax.class);

	/** 
	* Alias value: Tax_id
	* Hibernate value: Tax.id
	*/
	String  TAX_ID = TAX_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Tax_name
	* Hibernate value: Tax.name
	*/
	String  TAX_NAME = TAX_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Tax_percentage
	* Hibernate value: Tax.percentage
	*/
	String  TAX_PERCENTAGE = TAX_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Tax_startDate
	* Hibernate value: Tax.startDate
	*/
	String  TAX_START_DATE = TAX_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Tax_surcharge
	* Hibernate value: Tax.surcharge
	*/
	String  TAX_SURCHARGE = TAX_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Tax_type
	* Hibernate value: Tax.type
	*/
	String  TAX_TYPE = TAX_ENTRY.getAliasNames()[5];



	/** 
	* DAOConstantsEntry for TaxDetail entity.
	*/ 
	DAOConstantsEntry TAX_DETAIL_ENTRY = DAOConstants.getDAOConstant(TaxDetail.class);

	/** 
	* Alias value: TaxDetail_endDate
	* Hibernate value: TaxDetail.endDate
	*/
	String  TAX_DETAIL_END_DATE = TAX_DETAIL_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: TaxDetail_id
	* Hibernate value: TaxDetail.id
	*/
	String  TAX_DETAIL_ID = TAX_DETAIL_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: TaxDetail_startDate
	* Hibernate value: TaxDetail.startDate
	*/
	String  TAX_DETAIL_START_DATE = TAX_DETAIL_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: TaxDetail_surcharge
	* Hibernate value: TaxDetail.surcharge
	*/
	String  TAX_DETAIL_SURCHARGE = TAX_DETAIL_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: TaxDetail_tax_id
	* Hibernate value: TaxDetail.tax.id
	*/
	String  TAX_DETAIL_TAX_ID = TAX_DETAIL_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: TaxDetail_value
	* Hibernate value: TaxDetail.value
	*/
	String  TAX_DETAIL_VALUE = TAX_DETAIL_ENTRY.getAliasNames()[5];



	/** 
	* DAOConstantsEntry for User entity.
	*/ 
	DAOConstantsEntry USER_ENTRY = DAOConstants.getDAOConstant(User.class);

	/** 
	* Alias value: User_aon_key
	* Hibernate value: User.aon_key
	*/
	String  USER_AON_KEY = USER_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: User_available
	* Hibernate value: User.available
	*/
	String  USER_AVAILABLE = USER_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: User_id
	* Hibernate value: User.id
	*/
	String  USER_ID = USER_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: User_login
	* Hibernate value: User.login
	*/
	String  USER_LOGIN = USER_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: User_name
	* Hibernate value: User.name
	*/
	String  USER_NAME = USER_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: User_status
	* Hibernate value: User.status
	*/
	String  USER_STATUS = USER_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: User_validate
	* Hibernate value: User.validate
	*/
	String  USER_VALIDATE = USER_ENTRY.getAliasNames()[6];



	/** 
	* DAOConstantsEntry for UserScope entity.
	*/ 
	DAOConstantsEntry USER_SCOPE_ENTRY = DAOConstants.getDAOConstant(UserScope.class);

	/** 
	* Alias value: UserScope_id
	* Hibernate value: UserScope.id
	*/
	String  USER_SCOPE_ID = USER_SCOPE_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: UserScope_scope_id
	* Hibernate value: UserScope.scope.id
	*/
	String  USER_SCOPE_SCOPE_ID = USER_SCOPE_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: UserScope_user_id
	* Hibernate value: UserScope.user.id
	*/
	String  USER_SCOPE_USER_ID = USER_SCOPE_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: UserScope_scope_description
	* Hibernate value: UserScope.scope.description
	*/
	String  USER_SCOPE_SCOPE_DESCRIPTION = USER_SCOPE_ENTRY.getAliasNames()[3];



	/** 
	* DAOConstantsEntry for UserWorkGroup entity.
	*/ 
	DAOConstantsEntry USER_WORK_GROUP_ENTRY = DAOConstants.getDAOConstant(UserWorkGroup.class);

	/** 
	* Alias value: UserWorkGroup_id
	* Hibernate value: UserWorkGroup.id
	*/
	String  USER_WORK_GROUP_ID = USER_WORK_GROUP_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: UserWorkGroup_user_id
	* Hibernate value: UserWorkGroup.user.id
	*/
	String  USER_WORK_GROUP_USER_ID = USER_WORK_GROUP_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: UserWorkGroup_workGroup_id
	* Hibernate value: UserWorkGroup.workGroup.id
	*/
	String  USER_WORK_GROUP_WORK_GROUP_ID = USER_WORK_GROUP_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: UserWorkGroup_user_name
	* Hibernate value: UserWorkGroup.user.name
	*/
	String  USER_WORK_GROUP_USER_NAME = USER_WORK_GROUP_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: UserWorkGroup_workGroup_description
	* Hibernate value: UserWorkGroup.workGroup.description
	*/
	String  USER_WORK_GROUP_WORK_GROUP_DESCRIPTION = USER_WORK_GROUP_ENTRY.getAliasNames()[4];



	/** 
	* DAOConstantsEntry for WorkGroup entity.
	*/ 
	DAOConstantsEntry WORK_GROUP_ENTRY = DAOConstants.getDAOConstant(WorkGroup.class);

	/** 
	* Alias value: WorkGroup_description
	* Hibernate value: WorkGroup.description
	*/
	String  WORK_GROUP_DESCRIPTION = WORK_GROUP_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: WorkGroup_id
	* Hibernate value: WorkGroup.id
	*/
	String  WORK_GROUP_ID = WORK_GROUP_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: WorkGroup_status
	* Hibernate value: WorkGroup.status
	*/
	String  WORK_GROUP_STATUS = WORK_GROUP_ENTRY.getAliasNames()[2];


}