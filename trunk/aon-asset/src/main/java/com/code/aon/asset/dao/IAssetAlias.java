package com.code.aon.asset.dao;

import com.code.aon.common.dao.DAOConstants;
import com.code.aon.common.dao.DAOConstantsEntry;
import com.code.aon.asset.Asset;
import com.code.aon.asset.AssetActivity;

/** 
* Interface for holding entity properties constants.
*/ 
public interface IAssetAlias {



	/** 
	* DAOConstantsEntry for Asset entity.
	*/ 
	DAOConstantsEntry ASSET_ENTRY = DAOConstants.getDAOConstant(Asset.class);

	/** 
	* Alias value: Asset_description
	* Hibernate value: Asset.description
	*/
	String  ASSET_DESCRIPTION = ASSET_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Asset_id
	* Hibernate value: Asset.id
	*/
	String  ASSET_ID = ASSET_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Asset_name
	* Hibernate value: Asset.name
	*/
	String  ASSET_NAME = ASSET_ENTRY.getAliasNames()[2];



	/** 
	* DAOConstantsEntry for AssetActivity entity.
	*/ 
	DAOConstantsEntry ASSET_ACTIVITY_ENTRY = DAOConstants.getDAOConstant(AssetActivity.class);

	/** 
	* Alias value: AssetActivity_asset_id
	* Hibernate value: AssetActivity.asset.id
	*/
	String  ASSET_ACTIVITY_ASSET_ID = ASSET_ACTIVITY_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: AssetActivity_asset_name
	* Hibernate value: AssetActivity.asset.name
	*/
	String  ASSET_ACTIVITY_ASSET_NAME = ASSET_ACTIVITY_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: AssetActivity_date
	* Hibernate value: AssetActivity.date
	*/
	String  ASSET_ACTIVITY_DATE = ASSET_ACTIVITY_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: AssetActivity_fromTime
	* Hibernate value: AssetActivity.fromTime
	*/
	String  ASSET_ACTIVITY_FROM_TIME = ASSET_ACTIVITY_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: AssetActivity_id
	* Hibernate value: AssetActivity.id
	*/
	String  ASSET_ACTIVITY_ID = ASSET_ACTIVITY_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: AssetActivity_toTime
	* Hibernate value: AssetActivity.toTime
	*/
	String  ASSET_ACTIVITY_TO_TIME = ASSET_ACTIVITY_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: AssetActivity_who
	* Hibernate value: AssetActivity.who
	*/
	String  ASSET_ACTIVITY_WHO = ASSET_ACTIVITY_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: AssetActivity_why
	* Hibernate value: AssetActivity.why
	*/
	String  ASSET_ACTIVITY_WHY = ASSET_ACTIVITY_ENTRY.getAliasNames()[7];


}