package com.code.aon.ui.project.event;

import java.util.Date;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.project.DailyTracking;
import com.code.aon.project.dao.IProjectAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class InboxDailyTrackingControllerListener extends ControllerAdapter {

	
	@Override
	public void beforeModelInitialized(ControllerEvent event) throws ControllerListenerException {
		try {
			Criteria criteria = event.getController().getCriteria();
			IManagerBean dailyTrackingBean = BeanManager.getManagerBean(DailyTracking.class);
			criteria.addEqualExpression(dailyTrackingBean.getFieldName(IProjectAlias.DAILY_TRACKING_USER_ID), UserUtils.getLoggedUser().getId());
			criteria.addEqualExpression(dailyTrackingBean.getFieldName(IProjectAlias.DAILY_TRACKING_TRACKING_DATE), new Date());
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e);
		}
	}
}
