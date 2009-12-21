package com.code.aon.ui.cms.event;

import java.util.List;

import com.code.aon.cms.Link;
import com.code.aon.cms.dao.ICMSAlias;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.cms.controller.LinkController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class LinkControllerListener extends ControllerAdapter {

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		LinkController lc = (LinkController)event.getController();
		Link l = (Link)event.getController().getTo();
		l.setLinkCategory(lc.getCurrentLinkCategory());
		l.setPosition(getLastPosition(lc));
	}
	
	private int getLastPosition(LinkController lc) {
		int position = 0;
		try{
			Criteria criteria = new Criteria();
			criteria.addExpression(lc.getManagerBean().getFieldName(ICMSAlias.LINK_LINK_CATEGORY_ID), "" + lc.getCurrentLinkCategory().getId());
			criteria.addOrder(lc.getManagerBean().getFieldName(ICMSAlias.LINK_POSITION), false);
			List<ITransferObject> list = (List<ITransferObject>)lc.getManagerBean().getList(criteria);
			if (list.size() > 0) {
				Link l = (Link)list.get(0);
				position = l.getPosition();
				++position;
			}
		}catch (ManagerBeanException e) {
		} catch (ExpressionException e) {
		}
		return position;
	}

}
