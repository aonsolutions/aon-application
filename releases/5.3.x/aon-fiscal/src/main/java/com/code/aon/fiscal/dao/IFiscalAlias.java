package com.code.aon.fiscal.dao;

import com.code.aon.common.dao.DAOConstants;
import com.code.aon.common.dao.DAOConstantsEntry;
import com.code.aon.fiscal.VatTax;
import com.code.aon.fiscal.VatTaxDetail;

/** 
* Interface for holding entity properties constants.
*/ 
public interface IFiscalAlias {



	/** 
	* DAOConstantsEntry for VatTax entity.
	*/ 
	DAOConstantsEntry VAT_TAX_ENTRY = DAOConstants.getDAOConstant(VatTax.class);

	/** 
	* Alias value: VatTax_comments
	* Hibernate value: VatTax.comments
	*/
	String  VAT_TAX_COMMENTS = VAT_TAX_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: VatTax_id
	* Hibernate value: VatTax.id
	*/
	String  VAT_TAX_ID = VAT_TAX_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: VatTax_period
	* Hibernate value: VatTax.period
	*/
	String  VAT_TAX_PERIOD = VAT_TAX_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: VatTax_status
	* Hibernate value: VatTax.status
	*/
	String  VAT_TAX_STATUS = VAT_TAX_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: VatTax_type
	* Hibernate value: VatTax.type
	*/
	String  VAT_TAX_TYPE = VAT_TAX_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: VatTax_year
	* Hibernate value: VatTax.year
	*/
	String  VAT_TAX_YEAR = VAT_TAX_ENTRY.getAliasNames()[5];



	/** 
	* DAOConstantsEntry for VatTaxDetail entity.
	*/ 
	DAOConstantsEntry VAT_TAX_DETAIL_ENTRY = DAOConstants.getDAOConstant(VatTaxDetail.class);

	/** 
	* Alias value: VatTaxDetail_deductibleQuota
	* Hibernate value: VatTaxDetail.deductibleQuota
	*/
	String  VAT_TAX_DETAIL_DEDUCTIBLE_QUOTA = VAT_TAX_DETAIL_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: VatTaxDetail_deductibleQuotaAdjust
	* Hibernate value: VatTaxDetail.deductibleQuotaAdjust
	*/
	String  VAT_TAX_DETAIL_DEDUCTIBLE_QUOTA_ADJUST = VAT_TAX_DETAIL_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: VatTaxDetail_id
	* Hibernate value: VatTaxDetail.id
	*/
	String  VAT_TAX_DETAIL_ID = VAT_TAX_DETAIL_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: VatTaxDetail_key
	* Hibernate value: VatTaxDetail.key
	*/
	String  VAT_TAX_DETAIL_KEY = VAT_TAX_DETAIL_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: VatTaxDetail_percent
	* Hibernate value: VatTaxDetail.percent
	*/
	String  VAT_TAX_DETAIL_PERCENT = VAT_TAX_DETAIL_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: VatTaxDetail_quota
	* Hibernate value: VatTaxDetail.quota
	*/
	String  VAT_TAX_DETAIL_QUOTA = VAT_TAX_DETAIL_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: VatTaxDetail_quotaAdjust
	* Hibernate value: VatTaxDetail.quotaAdjust
	*/
	String  VAT_TAX_DETAIL_QUOTA_ADJUST = VAT_TAX_DETAIL_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: VatTaxDetail_taxableBase
	* Hibernate value: VatTaxDetail.taxableBase
	*/
	String  VAT_TAX_DETAIL_TAXABLE_BASE = VAT_TAX_DETAIL_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: VatTaxDetail_taxableBaseAdjust
	* Hibernate value: VatTaxDetail.taxableBaseAdjust
	*/
	String  VAT_TAX_DETAIL_TAXABLE_BASE_ADJUST = VAT_TAX_DETAIL_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: VatTaxDetail_vatTax_id
	* Hibernate value: VatTaxDetail.vatTax.id
	*/
	String  VAT_TAX_DETAIL_VAT_TAX_ID = VAT_TAX_DETAIL_ENTRY.getAliasNames()[9];


}