package com.code.aon.ui.asset.event;

import java.util.Date;

import org.apache.commons.lang.ArrayUtils;

import com.code.aon.asset.Asset;
import com.code.aon.asset.dao.IAssetAlias;
import com.code.aon.asset.enumeration.ActivityStatus;
import com.code.aon.bridge.plugin.Utils;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.asset.controller.ActivityBasicController;
import com.code.aon.ui.form.event.ControllerSearchListener;

public class ActivityBasicSearchListener extends ControllerSearchListener{

	private Asset asset;
	private ActivityStatus[] activityStatuses;
	private Date fromDate;
	private Date toDate;
	private String who;
	
	public Asset getAsset() {
		return asset;
	}

	public void setAsset(Asset asset) {
		this.asset = asset;
	}

	public ActivityStatus[] getActivityStatuses() {
		if(activityStatuses==null){
			activityStatuses = getdefaultActivityStatus();
		}
		return activityStatuses;
	}

	private ActivityStatus[] getdefaultActivityStatus() {
		ActivityStatus[] defaultActivityStatus = {ActivityStatus.PENDING};
		return defaultActivityStatus;
	}

	public void setActivityStatuses(ActivityStatus[] activityStatuses) {
		this.activityStatuses = activityStatuses;
	}	
	
	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate= fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}
	
	public String getWho() {
		return who;
	}

	public void setWho(String who) {
		this.who = who;
	}
		
	@Override
	protected void init() throws ManagerBeanException {
		Criteria criteria = getController().getCriteria();
		AuthPrincipal user = Utils.getAuthPrincipal();
		String name = user.getShortName();
		
		setAsset(((ActivityBasicController)getController()).getAsset());
		criteria.addGreaterThanOrEqualExpression(getController().getFieldName(IAssetAlias.ASSET_ACTIVITY_WHO), name);
		ActivityStatus[] defaultActivityStatus = {ActivityStatus.PENDING};
		setActivityStatuses(defaultActivityStatus);
	}
	
	@Override
	protected void completeCriteria() throws ManagerBeanException, ExpressionException {
		Criteria criteria = getController().getCriteria();
		
		if (getAsset() != null && getAsset().getId() != null) {
			criteria.addEqualExpression(getController().getFieldName(IAssetAlias.ASSET_ACTIVITY_ASSET_ID), getAsset().getId());
			setAsset(new Asset());
		}
		if (!ArrayUtils.isEmpty(getActivityStatuses())) {
			String status = getController().resolveAlias(IAssetAlias.ASSET_ACTIVITY_STATUS);
			addEnumToCriteria(criteria, status, getActivityStatuses());
			setActivityStatuses(null);
		}
		if(getFromDate() != null){
			criteria.addGreaterThanOrEqualExpression(getController().getFieldName(IAssetAlias.ASSET_ACTIVITY_DATE), getFromDate());
			setFromDate(null);
		}
		if(getToDate() != null){
			criteria.addLessThanOrEqualExpression(getController().getFieldName(IAssetAlias.ASSET_ACTIVITY_DATE), getToDate());
			setToDate(null);
		}
		
	}

}
