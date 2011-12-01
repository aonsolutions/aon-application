package com.code.aon.ui.cms.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.cms.HiruOrganizerCentre;
import com.code.aon.cms.dao.ICMSAlias;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class HiruOrganizerCentreControllerListener extends ControllerAdapter {

	private final static Logger LOGGER = LoggerFactory.getLogger(HiruOrganizerCentreControllerListener.class);
	
	@Override
	public void beforeModelInitialized(ControllerEvent event)
			throws ControllerListenerException {
		try{
			Criteria criteria = event.getController().getCriteria();
			IManagerBean bean = BeanManager.getManagerBean(HiruOrganizerCentre.class);
			criteria.addOrder(bean.getFieldName(ICMSAlias.HIRU_ORGANIZER_CENTRE_NAME));
			event.getController().setCriteria(criteria);
		}catch (Throwable th) {
			LOGGER.error(th.getMessage(), th);
		}
	}

}
