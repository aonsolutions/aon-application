package com.code.aon.ui.cms.event;

import java.util.List;

import com.code.aon.cms.Faq;
import com.code.aon.cms.dao.ICMSAlias;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.cms.controller.FaqController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class FaqControllerListener extends ControllerAdapter {

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		FaqController fc = (FaqController)event.getController();
		Faq f = (Faq)event.getController().getTo();
		f.setFaqCategory(fc.getCurrentFaqCategory());
		f.setPosition(getLastPosition(fc));
	}
	
	private int getLastPosition(FaqController fc) {
		int position = 0;
		try{
			Criteria criteria = new Criteria();
			criteria.addExpression(fc.getManagerBean().getFieldName(ICMSAlias.FAQ_FAQ_CATEGORY_ID), "" + fc.getCurrentFaqCategory().getId());
			criteria.addOrder(fc.getManagerBean().getFieldName(ICMSAlias.FAQ_POSITION), false);
			List<ITransferObject> list = (List<ITransferObject>)fc.getManagerBean().getList(criteria);
			if (list.size() > 0) {
				Faq f = (Faq)list.get(0);
				position = f.getPosition();
				++position;
			}
		}catch (ManagerBeanException e) {
		} catch (ExpressionException e) {
		}
		return position;
	}

}
