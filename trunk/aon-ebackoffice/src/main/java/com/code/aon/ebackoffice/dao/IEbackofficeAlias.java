package com.code.aon.ebackoffice.dao;

import com.code.aon.common.dao.DAOConstants;
import com.code.aon.common.dao.DAOConstantsEntry;
import com.code.aon.ebackoffice.Ectarget;
import com.code.aon.ebackoffice.Ecconfig;
import com.code.aon.ebackoffice.Eccatalogue;

/** 
* Interface for holding entity properties constants.
*/ 
public interface IEbackofficeAlias {



	/** 
	* DAOConstantsEntry for Ectarget entity.
	*/ 
	DAOConstantsEntry ECTARGET_ENTRY = DAOConstants.getDAOConstant(Ectarget.class);

	/** 
	* Alias value: Ectarget_id
	* Hibernate value: Ectarget.id
	*/
	String  ECTARGET_ID = ECTARGET_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Ectarget_lastAccess
	* Hibernate value: Ectarget.lastAccess
	*/
	String  ECTARGET_LAST_ACCESS = ECTARGET_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Ectarget_login
	* Hibernate value: Ectarget.login
	*/
	String  ECTARGET_LOGIN = ECTARGET_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Ectarget_password
	* Hibernate value: Ectarget.password
	*/
	String  ECTARGET_PASSWORD = ECTARGET_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Ectarget_target_id
	* Hibernate value: Ectarget.target.id
	*/
	String  ECTARGET_TARGET_ID = ECTARGET_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Ectarget_type
	* Hibernate value: Ectarget.type
	*/
	String  ECTARGET_TYPE = ECTARGET_ENTRY.getAliasNames()[5];



	/** 
	* DAOConstantsEntry for Ecconfig entity.
	*/ 
	DAOConstantsEntry ECCONFIG_ENTRY = DAOConstants.getDAOConstant(Ecconfig.class);

	/** 
	* Alias value: Ecconfig_active
	* Hibernate value: Ecconfig.active
	*/
	String  ECCONFIG_ACTIVE = ECCONFIG_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Ecconfig_bankDraft_id
	* Hibernate value: Ecconfig.bankDraft.id
	*/
	String  ECCONFIG_BANK_DRAFT_ID = ECCONFIG_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Ecconfig_bankTransfer_id
	* Hibernate value: Ecconfig.bankTransfer.id
	*/
	String  ECCONFIG_BANK_TRANSFER_ID = ECCONFIG_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Ecconfig_cashOnDelivery_id
	* Hibernate value: Ecconfig.cashOnDelivery.id
	*/
	String  ECCONFIG_CASH_ON_DELIVERY_ID = ECCONFIG_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Ecconfig_commerce
	* Hibernate value: Ecconfig.commerce
	*/
	String  ECCONFIG_COMMERCE = ECCONFIG_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Ecconfig_discount
	* Hibernate value: Ecconfig.discount
	*/
	String  ECCONFIG_DISCOUNT = ECCONFIG_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: Ecconfig_ecommerceStatus
	* Hibernate value: Ecconfig.ecommerceStatus
	*/
	String  ECCONFIG_ECOMMERCE_STATUS = ECCONFIG_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: Ecconfig_freeShipping
	* Hibernate value: Ecconfig.freeShipping
	*/
	String  ECCONFIG_FREE_SHIPPING = ECCONFIG_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: Ecconfig_headerColor
	* Hibernate value: Ecconfig.headerColor
	*/
	String  ECCONFIG_HEADER_COLOR = ECCONFIG_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: Ecconfig_headerImg
	* Hibernate value: Ecconfig.headerImg
	*/
	String  ECCONFIG_HEADER_IMG = ECCONFIG_ENTRY.getAliasNames()[9];

	/** 
	* Alias value: Ecconfig_id
	* Hibernate value: Ecconfig.id
	*/
	String  ECCONFIG_ID = ECCONFIG_ENTRY.getAliasNames()[10];

	/** 
	* Alias value: Ecconfig_leftBanner
	* Hibernate value: Ecconfig.leftBanner
	*/
	String  ECCONFIG_LEFT_BANNER = ECCONFIG_ENTRY.getAliasNames()[11];

	/** 
	* Alias value: Ecconfig_legalNote1
	* Hibernate value: Ecconfig.legalNote1
	*/
	String  ECCONFIG_LEGAL_NOTE1 = ECCONFIG_ENTRY.getAliasNames()[12];

	/** 
	* Alias value: Ecconfig_legalNote2
	* Hibernate value: Ecconfig.legalNote2
	*/
	String  ECCONFIG_LEGAL_NOTE2 = ECCONFIG_ENTRY.getAliasNames()[13];

	/** 
	* Alias value: Ecconfig_legalNote3
	* Hibernate value: Ecconfig.legalNote3
	*/
	String  ECCONFIG_LEGAL_NOTE3 = ECCONFIG_ENTRY.getAliasNames()[14];

	/** 
	* Alias value: Ecconfig_name
	* Hibernate value: Ecconfig.name
	*/
	String  ECCONFIG_NAME = ECCONFIG_ENTRY.getAliasNames()[15];

	/** 
	* Alias value: Ecconfig_noteTitle1
	* Hibernate value: Ecconfig.noteTitle1
	*/
	String  ECCONFIG_NOTE_TITLE1 = ECCONFIG_ENTRY.getAliasNames()[16];

	/** 
	* Alias value: Ecconfig_noteTitle2
	* Hibernate value: Ecconfig.noteTitle2
	*/
	String  ECCONFIG_NOTE_TITLE2 = ECCONFIG_ENTRY.getAliasNames()[17];

	/** 
	* Alias value: Ecconfig_noteTitle3
	* Hibernate value: Ecconfig.noteTitle3
	*/
	String  ECCONFIG_NOTE_TITLE3 = ECCONFIG_ENTRY.getAliasNames()[18];

	/** 
	* Alias value: Ecconfig_paypal_id
	* Hibernate value: Ecconfig.paypal.id
	*/
	String  ECCONFIG_PAYPAL_ID = ECCONFIG_ENTRY.getAliasNames()[19];

	/** 
	* Alias value: Ecconfig_price
	* Hibernate value: Ecconfig.price
	*/
	String  ECCONFIG_PRICE = ECCONFIG_ENTRY.getAliasNames()[20];

	/** 
	* Alias value: Ecconfig_rightBanner
	* Hibernate value: Ecconfig.rightBanner
	*/
	String  ECCONFIG_RIGHT_BANNER = ECCONFIG_ENTRY.getAliasNames()[21];

	/** 
	* Alias value: Ecconfig_rowItems
	* Hibernate value: Ecconfig.rowItems
	*/
	String  ECCONFIG_ROW_ITEMS = ECCONFIG_ENTRY.getAliasNames()[22];

	/** 
	* Alias value: Ecconfig_series
	* Hibernate value: Ecconfig.series
	*/
	String  ECCONFIG_SERIES = ECCONFIG_ENTRY.getAliasNames()[23];

	/** 
	* Alias value: Ecconfig_shippingCosts
	* Hibernate value: Ecconfig.shippingCosts
	*/
	String  ECCONFIG_SHIPPING_COSTS = ECCONFIG_ENTRY.getAliasNames()[24];

	/** 
	* Alias value: Ecconfig_showLogin
	* Hibernate value: Ecconfig.showLogin
	*/
	String  ECCONFIG_SHOW_LOGIN = ECCONFIG_ENTRY.getAliasNames()[25];

	/** 
	* Alias value: Ecconfig_skin
	* Hibernate value: Ecconfig.skin
	*/
	String  ECCONFIG_SKIN = ECCONFIG_ENTRY.getAliasNames()[26];

	/** 
	* Alias value: Ecconfig_tariff_id
	* Hibernate value: Ecconfig.tariff.id
	*/
	String  ECCONFIG_TARIFF_ID = ECCONFIG_ENTRY.getAliasNames()[27];

	/** 
	* Alias value: Ecconfig_taxInPrice
	* Hibernate value: Ecconfig.taxInPrice
	*/
	String  ECCONFIG_TAX_IN_PRICE = ECCONFIG_ENTRY.getAliasNames()[28];

	/** 
	* Alias value: Ecconfig_telephone
	* Hibernate value: Ecconfig.telephone
	*/
	String  ECCONFIG_TELEPHONE = ECCONFIG_ENTRY.getAliasNames()[29];

	/** 
	* Alias value: Ecconfig_visa_id
	* Hibernate value: Ecconfig.visa.id
	*/
	String  ECCONFIG_VISA_ID = ECCONFIG_ENTRY.getAliasNames()[30];

	/** 
	* Alias value: Ecconfig_welcomeBanner
	* Hibernate value: Ecconfig.welcomeBanner
	*/
	String  ECCONFIG_WELCOME_BANNER = ECCONFIG_ENTRY.getAliasNames()[31];



	/** 
	* DAOConstantsEntry for Eccatalogue entity.
	*/ 
	DAOConstantsEntry ECCATALOGUE_ENTRY = DAOConstants.getDAOConstant(Eccatalogue.class);

	/** 
	* Alias value: Eccatalogue_id
	* Hibernate value: Eccatalogue.id
	*/
	String  ECCATALOGUE_ID = ECCATALOGUE_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Eccatalogue_catalogue
	* Hibernate value: Eccatalogue.catalogue
	*/
	String  ECCATALOGUE_CATALOGUE = ECCATALOGUE_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Eccatalogue_catalogue_id
	* Hibernate value: Eccatalogue.catalogue.id
	*/
	String  ECCATALOGUE_CATALOGUE_ID = ECCATALOGUE_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Eccatalogue_catalogue_endDate
	* Hibernate value: Eccatalogue.catalogue.endDate
	*/
	String  ECCATALOGUE_CATALOGUE_END_DATE = ECCATALOGUE_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Eccatalogue_catalogue_startDate
	* Hibernate value: Eccatalogue.catalogue.startDate
	*/
	String  ECCATALOGUE_CATALOGUE_START_DATE = ECCATALOGUE_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Eccatalogue_catalogueImg
	* Hibernate value: Eccatalogue.catalogueImg
	*/
	String  ECCATALOGUE_CATALOGUE_IMG = ECCATALOGUE_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: Eccatalogue_catalogueIcon
	* Hibernate value: Eccatalogue.catalogueIcon
	*/
	String  ECCATALOGUE_CATALOGUE_ICON = ECCATALOGUE_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: Eccatalogue_type
	* Hibernate value: Eccatalogue.type
	*/
	String  ECCATALOGUE_TYPE = ECCATALOGUE_ENTRY.getAliasNames()[7];


}