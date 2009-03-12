package com.code.aon.ui.asset.event;

import java.util.Calendar;
import java.util.Date;
import java.util.logging.Logger;

import com.code.aon.asset.Asset;
import com.code.aon.asset.dao.IAssetAlias;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.asset.controller.ActivityLinesController;
import com.code.aon.ui.asset.controller.IAssetConstants;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class ActivityLinesControllerListener extends ControllerAdapter {

	private static final Logger LOGGER = Logger.getLogger(ActivityLinesControllerListener.class.getName());
	
	@Override
	public void beforeModelInitialized(ControllerEvent event)
			throws ControllerListenerException {
		addActivityDateFitlerCriteria();
	}
	
	@Override
	public void beforeBeanUpdated(ControllerEvent event)
			throws ControllerListenerException {
		((ActivityLinesController)this.getController()).buildFromTime();
		((ActivityLinesController)this.getController()).buildToTime();
	}
	
	@Override
	public void afterBeanSelected(ControllerEvent event)
			throws ControllerListenerException {
		((ActivityLinesController)this.getController()).setControllerTime();
	}
	
	private void addActivityDateFitlerCriteria(){
		Date fromDate = ((ActivityLinesController)this.getController()).getFromDateFilter();
		Date toDate = ((ActivityLinesController)this.getController()).getToDateFilter();
		try {
			Criteria criteria = new Criteria();
			this.getController().clearCriteria();
			criteria = this.getController().getCriteria();
			
			criteria.addEqualExpression(this.getController().getFieldName(IAssetAlias.ASSET_ACTIVITY_ASSET_ID), ((Asset)FormUtil.getController(IAssetConstants.ASSET_CONTROLLER_NAME).getTo()).getId());
			//criteria.addOrder(this.getController().getFieldName(IAssetAlias.ASSET_ACTIVITY_DATE));
			//criteria.addOrder(this.getController().getFieldName(IAssetAlias.ASSET_ACTIVITY_FROM_TIME));
			
			if (toDate == null && fromDate != null ){
				criteria.addGreaterThanOrEqualExpression(this.getController().getFieldName(IAssetAlias.ASSET_ACTIVITY_DATE), fromDate);
			} else if (toDate != null && fromDate != null) {
				criteria.addBetweenExpression(this.getController().getFieldName(IAssetAlias.ASSET_ACTIVITY_DATE), fromDate, toDate);
			} else {
				((ActivityLinesController)this.getController()).setFromDateFilter(Calendar.getInstance().getTime());
			}
			this.getController().setCriteria(criteria);
		} catch (ManagerBeanException e) {
			LOGGER.severe(">>>>>> addActivityDateFitlerCriteria" + e.getMessage());
		}
	}

}
