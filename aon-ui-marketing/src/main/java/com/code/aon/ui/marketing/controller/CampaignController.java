package com.code.aon.ui.marketing.controller;

import java.util.LinkedList;
import java.util.List;

import javax.faces.model.SelectItem;

import com.code.aon.AonVersion;
import com.code.aon.marketing.enumeration.ActionMediaType;
import com.code.aon.marketing.enumeration.NewsType;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.event.IControllerListener;
import com.code.aon.ui.util.AonUtil;


public class CampaignController extends BasicController implements IMarketingConstants {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private IControllerListener messagesFilter;

	/**
	 * Gets the action media types.
	 * 
	 * @return the action media types
	 */
	public List<SelectItem> getActionMediaTypes() {
		MarketingCollectionsController mcc = (MarketingCollectionsController) AonUtil.getRegisteredBean(MARKETING_COLLECTIONS_CONTROLLER_NAME);
		List<SelectItem> actionMediaTypes = mcc.getActionMediaTypes();
		if ( AonUtil.isBeanValue(CAMPAIGN_CONTROLLER_NAME, SHOW_PHONE_ACTION_MEDIA_TYPE) ) {
			return actionMediaTypes;
		} 
		List<SelectItem> list = new LinkedList<SelectItem>();
		for (SelectItem item : actionMediaTypes) {
			if (! ActionMediaType.PHONE.equals(item.getValue()) ) {
				list.add(item);
			}
		}
		return list;
	}

	public IControllerListener getMessagesFilter() {
		if ( this.messagesFilter == null ) {
			this.messagesFilter = new NewsFilter(NewsType.MESSAGE);
		}
		return this.messagesFilter;
	}
	
}
