package com.code.aon.ui.sales.controller;

import java.io.Serializable;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.enumeration.AppParam;
import com.code.aon.config.util.AppParamUtil;

public class SalesAppParamController implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	public boolean isActiveMarketPlace(){
		String[] marketplaces = getSalesMarketplaceValues();
		return ArrayUtils.isNotEmpty(marketplaces);
	}
	
	public boolean isActiveAmazonMarketPlace(){
		String[] marketplaces = getSalesMarketplaceValues();
		return ArrayUtils.contains(marketplaces,"AMAZON");
	}
	
	private String[] getSalesMarketplaceValues(){
		String value = AppParamUtil.getValue(AppParam.SALES_ACTIVE_MARKETPLACE);
		return StringUtils.split(value, ",");
	}
	
}
