package com.code.aon.ui.cms.event;

import com.code.aon.cms.BannerCategory;
import com.code.aon.cms.dao.ICMSAlias;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;

public class BannerCategoryControllerListener extends ControllerAdapter {

	@Override
	public void beforeModelInitialized(ControllerEvent event)
			throws ControllerListenerException {
		try{
			Criteria criteria = event.getController().getCriteria();
			IManagerBean bean = BeanManager.getManagerBean(BannerCategory.class);
			criteria.addOrder(bean.getFieldName(ICMSAlias.BANNER_CATEGORY_ALIAS));
			event.getController().setCriteria(criteria);
		}catch (Exception e) {
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
		BannerCategory to = (BannerCategory)event.getController().getTo();
		if (to.getSection().getId()==-1){
			to.setSection(null);
		}
	}

	@Override
	public void afterBeanRemoved(ControllerEvent event)
			throws ControllerListenerException {
		AonUtil.getController("banner").onSearch(null);
	}
}
