package com.code.aon.ui.cms.event;

import com.code.aon.cms.SportCoach;
import com.code.aon.cms.dao.ICMSAlias;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class SportCoachControllerListener extends ControllerAdapter {

	@Override
	public void beforeModelInitialized(ControllerEvent event)
			throws ControllerListenerException {
		try{
			Criteria criteria = event.getController().getCriteria();
			IManagerBean bean = BeanManager.getManagerBean(SportCoach.class);
			criteria.addOrder(bean.getFieldName(ICMSAlias.SPORT_COACH_SPORT_CLUB_ID));
			criteria.addOrder(bean.getFieldName(ICMSAlias.SPORT_COACH_JOB));
			criteria.addOrder(bean.getFieldName(ICMSAlias.SPORT_COACH_NAME));
			event.getController().setCriteria(criteria);
		}catch (Exception e) {
		}
	}

}
