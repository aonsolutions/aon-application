package com.code.aon.asset.dao;

import com.code.aon.common.dao.DAOConstants;
import com.code.aon.common.dao.DAOConstantsEntry;
import com.code.aon.asset.Asset;
import com.code.aon.asset.AssetActivity;
import com.code.aon.asset.AssetFeature;
import com.code.aon.asset.AssetInterval;
import com.code.aon.asset.AssetType;
import com.code.aon.asset.Feature;

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
	* Alias value: AssetActivity_comments
	* Hibernate value: AssetActivity.comments
	*/
	String  ASSET_ACTIVITY_COMMENTS = ASSET_ACTIVITY_ENTRY.getAliasNames()[1];

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
	* Alias value: AssetActivity_holder
	* Hibernate value: AssetActivity.holder
	*/
	String  ASSET_ACTIVITY_HOLDER = ASSET_ACTIVITY_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: AssetActivity_id
	* Hibernate value: AssetActivity.id
	*/
	String  ASSET_ACTIVITY_ID = ASSET_ACTIVITY_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: AssetActivity_status
	* Hibernate value: AssetActivity.status
	*/
	String  ASSET_ACTIVITY_STATUS = ASSET_ACTIVITY_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: AssetActivity_toTime
	* Hibernate value: AssetActivity.toTime
	*/
	String  ASSET_ACTIVITY_TO_TIME = ASSET_ACTIVITY_ENTRY.getAliasNames()[7];



	/** 
	* DAOConstantsEntry for AssetFeature entity.
	*/ 
	DAOConstantsEntry ASSET_FEATURE_ENTRY = DAOConstants.getDAOConstant(AssetFeature.class);

	/** 
	* Alias value: AssetFeature_asset_id
	* Hibernate value: AssetFeature.asset.id
	*/
	String  ASSET_FEATURE_ASSET_ID = ASSET_FEATURE_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: AssetFeature_feature_id
	* Hibernate value: AssetFeature.feature.id
	*/
	String  ASSET_FEATURE_FEATURE_ID = ASSET_FEATURE_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: AssetFeature_id
	* Hibernate value: AssetFeature.id
	*/
	String  ASSET_FEATURE_ID = ASSET_FEATURE_ENTRY.getAliasNames()[2];



	/** 
	* DAOConstantsEntry for AssetInterval entity.
	*/ 
	DAOConstantsEntry ASSET_INTERVAL_ENTRY = DAOConstants.getDAOConstant(AssetInterval.class);

	/** 
	* Alias value: AssetInterval_description
	* Hibernate value: AssetInterval.description
	*/
	String  ASSET_INTERVAL_DESCRIPTION = ASSET_INTERVAL_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: AssetInterval_endTime
	* Hibernate value: AssetInterval.endTime
	*/
	String  ASSET_INTERVAL_END_TIME = ASSET_INTERVAL_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: AssetInterval_id
	* Hibernate value: AssetInterval.id
	*/
	String  ASSET_INTERVAL_ID = ASSET_INTERVAL_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: AssetInterval_interval
	* Hibernate value: AssetInterval.interval
	*/
	String  ASSET_INTERVAL_INTERVAL = ASSET_INTERVAL_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: AssetInterval_startTime
	* Hibernate value: AssetInterval.startTime
	*/
	String  ASSET_INTERVAL_START_TIME = ASSET_INTERVAL_ENTRY.getAliasNames()[4];



	/** 
	* DAOConstantsEntry for AssetType entity.
	*/ 
	DAOConstantsEntry ASSET_TYPE_ENTRY = DAOConstants.getDAOConstant(AssetType.class);

	/** 
	* Alias value: AssetType_assetInterval_id
	* Hibernate value: AssetType.assetInterval.id
	*/
	String  ASSET_TYPE_ASSET_INTERVAL_ID = ASSET_TYPE_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: AssetType_id
	* Hibernate value: AssetType.id
	*/
	String  ASSET_TYPE_ID = ASSET_TYPE_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: AssetType_name
	* Hibernate value: AssetType.name
	*/
	String  ASSET_TYPE_NAME = ASSET_TYPE_ENTRY.getAliasNames()[2];



	/** 
	* DAOConstantsEntry for Feature entity.
	*/ 
	DAOConstantsEntry FEATURE_ENTRY = DAOConstants.getDAOConstant(Feature.class);

	/** 
	* Alias value: Feature_id
	* Hibernate value: Feature.id
	*/
	String  FEATURE_ID = FEATURE_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Feature_name
	* Hibernate value: Feature.name
	*/
	String  FEATURE_NAME = FEATURE_ENTRY.getAliasNames()[1];


}