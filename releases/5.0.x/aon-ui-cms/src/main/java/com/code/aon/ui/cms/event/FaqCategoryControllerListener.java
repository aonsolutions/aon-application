package com.code.aon.ui.cms.event;

import java.util.List;

import com.code.aon.cms.DownloadCategory;
import com.code.aon.cms.FaqCategory;
import com.code.aon.cms.dao.ICMSAlias;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.cms.controller.FaqCategoryController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class FaqCategoryControllerListener extends ControllerAdapter {

	@Override
	public void beforeModelInitialized(ControllerEvent event)
			throws ControllerListenerException {
		try{
			Criteria criteria = event.getController().getCriteria();
			IManagerBean bean = BeanManager.getManagerBean(FaqCategory.class);
			criteria.addOrder(bean.getFieldName(ICMSAlias.FAQ_CATEGORY_POSITION));
			event.getController().setCriteria(criteria);
		}catch (Exception e) {
		}
	}

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException{
		FaqCategoryController controller = (FaqCategoryController) event.getController();
		FaqCategory faqCategory = (FaqCategory)controller.getTo();
		faqCategory.setActive(true);
		faqCategory.setPosition(getLastPosition(event));
		assignSection(event);
	}

	@Override
	public void beforeBeanUpdated(ControllerEvent event)
			throws ControllerListenerException {
		assignSection(event);
	}
	
	private int getLastPosition(ControllerEvent event) {
		int position = 0;
		try{
			Criteria criteria = new Criteria();
			criteria.addOrder(event.getController().getManagerBean().getFieldName(ICMSAlias.FAQ_CATEGORY_POSITION), false);
			List<ITransferObject> list = (List<ITransferObject>)event.getController().getManagerBean().getList(criteria);
			if (list.size() > 0) {
				FaqCategory faqCategory = (FaqCategory)list.get(0);
				position = faqCategory.getPosition();
				++position;
			}
		}catch (ManagerBeanException e) {
		}
		return position;
	}

	private void assignSection(ControllerEvent event){
		FaqCategory to = (FaqCategory)event.getController().getTo();
		if (to.getSection().getId()==-1){
			to.setSection(null);
		}
	}

}
