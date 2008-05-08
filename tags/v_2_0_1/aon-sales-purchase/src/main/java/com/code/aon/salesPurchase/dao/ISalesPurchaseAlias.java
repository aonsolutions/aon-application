package com.code.aon.salesPurchase.dao;

import com.code.aon.common.dao.DAOConstants;
import com.code.aon.common.dao.DAOConstantsEntry;
import com.code.aon.salesPurchase.SalesPurchase;

/**
 * This Class manages the DAOConstantsEntry group of this package,
 * witch manages information of an IDAO constants stored in a file,
 * 
 * @author Consulting & Development.
 * @version 1.0
 */
public interface ISalesPurchaseAlias {

	/**
     * DAOConstantsEntry for SalesPurchase.
     */
	DAOConstantsEntry SALES_PURCHASE_ENTRY = DAOConstants.getDAOConstant(SalesPurchase.class);

	/**
     * Alias name.
     */
	// Alias value: SalesPurchase_id
	// Hibernate value: SalesPurchase.id
	String  SALES_PURCHASE_ID = SALES_PURCHASE_ENTRY.getAliasNames()[0];

	/**
     * Alias name.
     */
	// Alias value: SalesPurchase_purchaseDetail_id
	// Hibernate value: SalesPurchase.purchaseDetail.id
	String  SALES_PURCHASE_PURCHASE_DETAIL_ID = SALES_PURCHASE_ENTRY.getAliasNames()[1];

	/**
     * Alias name.
     */
	// Alias value: SalesPurchase_salesDetail_id
	// Hibernate value: SalesPurchase.salesDetail.id
	String  SALES_PURCHASE_SALES_DETAIL_ID = SALES_PURCHASE_ENTRY.getAliasNames()[2];


}