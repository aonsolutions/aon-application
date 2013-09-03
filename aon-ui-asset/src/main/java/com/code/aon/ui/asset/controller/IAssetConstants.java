package com.code.aon.ui.asset.controller;

/**
 * Interface for holding entity names constants.
 */
public interface IAssetConstants {

	String EMPTY_STRING = "";
	
	String ASSET_BUNDLE = "assetBundle";

	String ASSET_ERROR_TIME_RANGE = "asset_error_time_range";
	String ASSET_ERROR_DATE_RANGE = "asset_error_date_range";
	String ASSET_ACTIVITY_WHY = "asset_activity_why";
	String ASSET_ACTIVITY_WHO = "asset_activity_who";
	String ASSET_ASSET = "asset_asset";
	String ASSET_START_DATE = "asset_startDate";
	String ASSET_END_DATE = "asset_endDate";
	String ASSET_ACTIVITY_DAYS = "asset_activity_days";
	String ASSET_TO_TIME = "asset_toTime";
	String ASSET_FROM_TIME = "asset_fromTime";
	String ASSET_ERROR_HOVERLAP = "asset_error_hoverlap";
	
	// ************************************************************
	// BEAN
	// ************************************************************
	
	String ASSET_CONTROLLER_NAME = "asset";
	
	String ACTIVITY_DIALOG_CONTROLLER_NAME = "activityDialog";
	
	String ACTIVITY_BASIC_CONTROLLER_NAME = "activityBasic";
	
	String ACTIVITY_LINES_CONTROLLER_NAME = "activityLines";
	
	// ************************************************************
	// CONFIGURATION
	// ************************************************************
	
	int START_TIME = 8;
	
	int END_TIME = 23;
	
	int FRACTION_TIME = 15;
	
	int WEEK_DAYS = 7;

}