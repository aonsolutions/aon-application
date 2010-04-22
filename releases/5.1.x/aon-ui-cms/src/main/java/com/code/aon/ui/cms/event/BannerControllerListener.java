package com.code.aon.ui.cms.event;

import java.util.List;

import com.code.aon.cms.Banner;
import com.code.aon.cms.dao.ICMSAlias;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.cms.controller.BannerController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class BannerControllerListener extends ControllerAdapter {
	
	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		Banner banner = (Banner)event.getController().getTo();
		banner.setActive(true);
	}

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		BannerController lc = (BannerController)event.getController();
		Banner b = (Banner)event.getController().getTo();
		b.setBannerCategory(lc.getCurrentBannerCategory());
		b.setPosition(getLastPosition(lc));
	}
	
	private int getLastPosition(BannerController bc) {
		int position = 0;
		try{
			Criteria criteria = new Criteria();
			criteria.addExpression(bc.getManagerBean().getFieldName(ICMSAlias.BANNER_BANNER_CATEGORY_ID), "" + bc.getCurrentBannerCategory().getId());
			criteria.addOrder(bc.getManagerBean().getFieldName(ICMSAlias.BANNER_POSITION), false);
			List<ITransferObject> list = (List<ITransferObject>)bc.getManagerBean().getList(criteria);
			if (list.size() > 0) {
				Banner b = (Banner)list.get(0);
				position = b.getPosition();
				++position;
			}
		}catch (ManagerBeanException e) {
		} catch (ExpressionException e) {
		}
		return position;
	}
}