package com.code.aon.ui.marketing.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.marketing.controller.NewsController;
import com.esferalia.aon.entity.IEntityAlias;

public class NewsControllerListener extends ControllerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(NewsControllerListener.class.getName());

	@Override
	public void beforeModelSearched(ControllerEvent event)
			throws ControllerListenerException {
		NewsController controller = (NewsController) event.getController();
		try {					
			Criteria criteria = controller.getCriteria();
			criteria.addEqualExpression(controller.getFieldName(IEntityAlias.NEWS_TYPE), controller.getType());
		} catch (ManagerBeanException e) {
			LOGGER.error("Error filtering news", e);
		}
	}

}
