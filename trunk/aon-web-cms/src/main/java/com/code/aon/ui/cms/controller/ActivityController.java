package com.code.aon.ui.cms.controller;

import java.util.List;

import javax.faces.event.ActionEvent;

import com.code.aon.cms.Activity;
import com.code.aon.cms.ActivityDetail;
import com.code.aon.cms.dao.ICMSAlias;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.cms.util.ControllerUtil;


public class ActivityController extends GridI18nController {

	@SuppressWarnings("unused")
	public void onSelect(ActionEvent event) {
		super.onSelect(event);
		loadCurrentLanguage();
	}

	public String getI18nDescription() throws ManagerBeanException {
		String description = "";
		ActivityDetail detail = getCurrentDetail();
		if (detail != null) description = detail.getDescription();
		return description;
	}

	private ActivityDetail getCurrentDetail() throws ManagerBeanException {
		ActivityDetail detail = null;
		Activity master = (Activity)this.model.getRowData();
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(getManagerBeanI18n().getFieldName(ICMSAlias.ACTIVITY_DETAIL_ACTIVITY_ID), master.getId());
		criteria.addEqualExpression(getManagerBeanI18n().getFieldName(ICMSAlias.ACTIVITY_DETAIL_LANGUAGE_ID), ControllerUtil.getCurrentLanguage().getId());
		List<ITransferObject> list = (List<ITransferObject>)getManagerBeanI18n().getList(criteria);
		if (list.size() > 0) {
			detail = (ActivityDetail)list.get(0);
		}
		return detail;
	}

}