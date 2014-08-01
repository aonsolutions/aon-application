package com.code.aon.ui.marketing.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.marketing.enumeration.NewsType;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.esferalia.aon.entity.IEntityAlias;

public class NewsFilter extends ControllerAdapter {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private final static Logger LOGGER = LoggerFactory.getLogger(NewsFilter.class);
	
	private NewsType type;

	public NewsFilter(NewsType type) {
		this.type = type;
	}

	@Override
	public void beforeModelInitialized(ControllerEvent event)
			throws ControllerListenerException {
		IController controller = event.getController();
		try {					
			Criteria criteria = controller.getCriteria();
			criteria.addEqualExpression(controller.getFieldName(IEntityAlias.NEWS_TYPE), this.type);
		} catch (ManagerBeanException e) {
			LOGGER.error("Error filtering news", e);
		}
	}
	

}
