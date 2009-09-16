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
	* Alias value: Ecconfig_bankDraft
	* Hibernate value: Ecconfig.bankDraft
	*/
	String  ECCONFIG_BANK_DRAFT = ECCONFIG_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Ecconfig_bankTransfer
	* Hibernate value: Ecconfig.bankTransfer
	*/
	String  ECCONFIG_BANK_TRANSFER = ECCONFIG_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Ecconfig_cashOnDelivery
	* Hibernate value: Ecconfig.cashOnDelivery
	*/
	String  ECCONFIG_CASH_ON_DELIVERY = ECCONFIG_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Ecconfig_commerce
	* Hibernate value: Ecconfig.commerce
	*/
	String  ECCONFIG_COMMERCE = ECCONFIG_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Ecconfig_footerImg
	* Hibernate value: Ecconfig.footerImg
	*/
	String  ECCONFIG_FOOTER_IMG = ECCONFIG_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: Ecconfig_headerImg
	* Hibernate value: Ecconfig.headerImg
	*/
	String  ECCONFIG_HEADER_IMG = ECCONFIG_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: Ecconfig_id
	* Hibernate value: Ecconfig.id
	*/
	String  ECCONFIG_ID = ECCONFIG_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: Ecconfig_name
	* Hibernate value: Ecconfig.name
	*/
	String  ECCONFIG_NAME = ECCONFIG_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: Ecconfig_paypal
	* Hibernate value: Ecconfig.paypal
	*/
	String  ECCONFIG_PAYPAL = ECCONFIG_ENTRY.getAliasNames()[9];

	/** 
	* Alias value: Ecconfig_showItemPrice
	* Hibernate value: Ecconfig.showItemPrice
	*/
	String  ECCONFIG_SHOW_ITEM_PRICE = ECCONFIG_ENTRY.getAliasNames()[10];

	/** 
	* Alias value: Ecconfig_showLogin
	* Hibernate value: Ecconfig.showLogin
	*/
	String  ECCONFIG_SHOW_LOGIN = ECCONFIG_ENTRY.getAliasNames()[11];

	/** 
	* Alias value: Ecconfig_showPrice
	* Hibernate value: Ecconfig.showPrice
	*/
	String  ECCONFIG_SHOW_PRICE = ECCONFIG_ENTRY.getAliasNames()[12];

	/** 
	* Alias value: Ecconfig_skin
	* Hibernate value: Ecconfig.skin
	*/
	String  ECCONFIG_SKIN = ECCONFIG_ENTRY.getAliasNames()[13];

	/** 
	* Alias value: Ecconfig_taxInType
	* Hibernate value: Ecconfig.taxInType
	*/
	String  ECCONFIG_TAX_IN_TYPE = ECCONFIG_ENTRY.getAliasNames()[14];

	/** 
	* Alias value: Ecconfig_visa
	* Hibernate value: Ecconfig.visa
	*/
	String  ECCONFIG_VISA = ECCONFIG_ENTRY.getAliasNames()[15];

	/** 
	* Alias value: Ecconfig_wishList
	* Hibernate value: Ecconfig.wishList
	*/
	String  ECCONFIG_WISH_LIST = ECCONFIG_ENTRY.getAliasNames()[16];



	/** 
	* DAOConstantsEntry for Eccatalogue entity.
	*/ 
	DAOConstantsEntry ECCATALOGUE_ENTRY = DAOConstants.getDAOConstant(Eccatalogue.class);

	/** 
	* Alias value: Eccatalogue_catalogueImg
	* Hibernate value: Eccatalogue.catalogueImg
	*/
	String  ECCATALOGUE_CATALOGUE_IMG = ECCATALOGUE_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Eccatalogue_catalogue_id
	* Hibernate value: Eccatalogue.catalogue.id
	*/
	String  ECCATALOGUE_CATALOGUE_ID = ECCATALOGUE_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Eccatalogue_id
	* Hibernate value: Eccatalogue.id
	*/
	String  ECCATALOGUE_ID = ECCATALOGUE_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Eccatalogue_type
	* Hibernate value: Eccatalogue.type
	*/
	String  ECCATALOGUE_TYPE = ECCATALOGUE_ENTRY.getAliasNames()[3];


}