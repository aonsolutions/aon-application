package com.code.aon.finance.bridge.dao;

import com.code.aon.common.dao.DAOConstants;
import com.code.aon.common.dao.DAOConstantsEntry;
import com.code.aon.finance.bridge.FinanceSales;

/** 
* Interface for holding entity properties constants.
*/ 
public interface IFinanceBridgeAlias {



	/** 
	* DAOConstantsEntry for FinanceSales entity.
	*/ 
	DAOConstantsEntry FINANCE_SALES_ENTRY = DAOConstants.getDAOConstant(FinanceSales.class);

	/** 
	* Alias value: FinanceSales_finance_id
	* Hibernate value: FinanceSales.finance.id
	*/
	String  FINANCE_SALES_FINANCE_ID = FINANCE_SALES_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: FinanceSales_id
	* Hibernate value: FinanceSales.id
	*/
	String  FINANCE_SALES_ID = FINANCE_SALES_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: FinanceSales_sales_id
	* Hibernate value: FinanceSales.sales.id
	*/
	String  FINANCE_SALES_SALES_ID = FINANCE_SALES_ENTRY.getAliasNames()[2];


}