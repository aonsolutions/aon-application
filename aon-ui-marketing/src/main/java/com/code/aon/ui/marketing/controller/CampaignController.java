package com.code.aon.ui.marketing.controller;

import java.util.LinkedList;
import java.util.List;

import javax.faces.model.SelectItem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.marketing.enumeration.ActionMediaType;
import com.code.aon.marketing.enumeration.NewsType;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.form.event.IControllerListener;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;


public class CampaignController extends BasicController implements IMarketingConstants {

	private final static Logger LOGGER = LoggerFactory.getLogger(CampaignController.class);

	private IControllerListener newsFilter;

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

	public IControllerListener getNewsFilter() {
		if ( this.newsFilter == null ) {
			this.newsFilter = new ControllerAdapter() {
				@Override
				public void beforeModelInitialized(ControllerEvent event)
						throws ControllerListenerException {
					IController controller = event.getController();
					try {					
						Criteria criteria = controller.getCriteria();
						criteria.addEqualExpression(controller.getFieldName(IEntityAlias.NEWS_TYPE), NewsType.MESSAGE);
					} catch (ManagerBeanException e) {
						LOGGER.error("Error filtering news", e);
					}
				}
			};
		}
		return this.newsFilter;
	}
	
}
