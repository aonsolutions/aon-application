package com.code.aon.ui.cms.event;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.code.aon.cms.LinkCategory;
import com.code.aon.cms.dao.ICMSAlias;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.cms.controller.ICMSConstants;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class LinkCategoryControllerListener extends ControllerAdapter implements ICMSConstants {

	private static final Logger LOGGER = Logger.getLogger(LinkCategoryControllerListener.class.getName());
	
	@Override
	public void beforeModelInitialized(ControllerEvent event)
			throws ControllerListenerException {
		try{
			Criteria criteria = event.getController().getCriteria();
			IManagerBean bean = BeanManager.getManagerBean(LinkCategory.class);
			criteria.addOrder(bean.getFieldName(ICMSAlias.LINK_CATEGORY_POSITION));
			event.getController().setCriteria(criteria);
		}catch (Throwable th) {
			LOGGER.log(Level.SEVERE, th.getMessage(), th);
		}
	}

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		assignSection(event);
	}
	
	@Override
	public void beforeBeanUpdated(ControllerEvent event)
			throws ControllerListenerException {
		assignSection(event);
	}
	
	private void assignSection(ControllerEvent event){
		LinkCategory to = (LinkCategory)event.getController().getTo();
		if (to.getSection().getId()==-1){
			to.setSection(null);
		}
	}

	@Override
	public void afterBeanRemoved(ControllerEvent event)
			throws ControllerListenerException {
		FormUtil.getController(LINK).onSearch(null);
	}
}
