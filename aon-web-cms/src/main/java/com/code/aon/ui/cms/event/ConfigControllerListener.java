package com.code.aon.ui.cms.event;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.code.aon.cms.Config;
import com.code.aon.cms.dao.ICMSAlias;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.cms.controller.ConfigController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class ConfigControllerListener extends ControllerAdapter {

	private static final Logger LOGGER = Logger.getLogger(ConfigControllerListener.class.getName());
	
	@Override
	public void beforeModelInitialized(ControllerEvent event)
			throws ControllerListenerException {
		try{
			ConfigController controller = (ConfigController) event.getController();
			IManagerBean bean = BeanManager.getManagerBean(Config.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(ICMSAlias.CONFIG_ID), new Integer(1));
			controller.setCriteria(criteria);
		}catch (Throwable th) {
			LOGGER.log(Level.SEVERE, th.getMessage(), th);
			
		}
	}
	
	@Override
	public void afterModelInitialized(ControllerEvent event)
			throws ControllerListenerException {
		ConfigController controller = (ConfigController) event.getController();
		controller.onSelectFirst(null);
		if (controller.getTo()==null){
			controller.onReset(null);
		}else{
			controller.loadCurrentLanguage();
		}
	}
	
}
