package com.code.aon.ui.marketing.controller;

import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

import javax.faces.model.SelectItem;

import com.code.aon.marketing.enumeration.ActionMediaType;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;


public class CampaignController extends BasicController implements IMarketingConstants {
	
	private boolean showSurvey;
	
	private boolean showPhoneActionMediaType;

	private ResourceBundle bundle;
	
	public CampaignController() {
		this.showSurvey = true;
		this.showPhoneActionMediaType = true;
		setBundleName(BUNDLE_NAME);
	}

	public boolean isShowSurvey() {
		return showSurvey;
	}

	public void setShowSurvey(boolean showSurvey) {
		this.showSurvey = showSurvey;
	}		
	
	public boolean isShowPhoneActionMediaType() {
		return showPhoneActionMediaType;
	}

	public void setShowPhoneActionMediaType(boolean showPhoneActionMediaType) {
		this.showPhoneActionMediaType = showPhoneActionMediaType;
	}

	/**
	 * Gets the action media types.
	 * 
	 * @return the action media types
	 */
	public List<SelectItem> getActionMediaTypes() {
		MarketingCollectionsController mcc = (MarketingCollectionsController) AonUtil.getRegisteredBean(MARKETING_COLLECTIONS_CONTROLLER_NAME);
		List<SelectItem> actionMediaTypes = mcc.getActionMediaTypes();
		if ( showPhoneActionMediaType ) {
			return actionMediaTypes;
		} else {
			List<SelectItem> list = new LinkedList<SelectItem>();
			for (SelectItem item : actionMediaTypes) {
				if (! ActionMediaType.PHONE.equals(item.getValue()) ) {
					list.add(item);
				}
			}
			return list;
		}
	}

	public ResourceBundle getBundle() {
		return bundle;
	}

	public void setBundleName(String bundleName) {
		this.bundle = AonUtil.getResourceBundle(bundleName);
	}	
	
}
