package com.code.aon.ui.cms.event;

import com.code.aon.cms.Section;
import com.code.aon.cms.dao.ICMSAlias;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class SectionControllerListener extends ControllerAdapter {

	@Override
	public void beforeModelInitialized(ControllerEvent event)
			throws ControllerListenerException {
		try {
			Criteria criteria = event.getController().getCriteria();
			IManagerBean bean = BeanManager.getManagerBean(Section.class);
			criteria.addOrder(bean.getFieldName(ICMSAlias.SECTION_DEFAULT_),false);
			criteria.addOrder(bean.getFieldName(ICMSAlias.SECTION_ALIAS));
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e);
		}
	}

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		assignNullValues(event);
	}

	@Override
	public void beforeBeanUpdated(ControllerEvent event)
			throws ControllerListenerException {
		assignNullValues(event);
	}
	
	private void assignNullValues(ControllerEvent event){
		Section to = (Section)event.getController().getTo();
		if (to.getParent_().getId()==-1){
			to.setParent_(null);
		}
		if (to.getHeader().getId()==-1){
			to.setHeader(null);
		}
		if (to.getFooter().getId()==-1){
			to.setFooter(null);
		}
		if (to.getMenu().getId()==-1){
			to.setMenu(null);
		}
		if (to.getMenu_alt().getId()==-1){
			to.setMenu_alt(null);
		}
		if (to.getSidebar().getId()==-1){
			to.setSidebar(null);
		}
	}

}
