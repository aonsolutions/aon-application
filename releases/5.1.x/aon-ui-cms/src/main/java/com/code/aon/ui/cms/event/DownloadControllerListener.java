package com.code.aon.ui.cms.event;

import java.util.List;

import com.code.aon.cms.Download;
import com.code.aon.cms.dao.ICMSAlias;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.cms.controller.DownloadController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class DownloadControllerListener extends ControllerAdapter {
	
	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		Download download = (Download)event.getController().getTo();
		download.setActive(true);
	}

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		DownloadController dc = (DownloadController)event.getController();
		Download d = (Download)event.getController().getTo();
		d.setDownloadCategory(dc.getCurrentDownloadCategory());
		d.setPosition(getLastPosition(dc));
	}
	
	private int getLastPosition(DownloadController bc) {
		int position = 0;
		try{
			Criteria criteria = new Criteria();
			criteria.addExpression(bc.getManagerBean().getFieldName(ICMSAlias.DOWNLOAD_DOWNLOAD_CATEGORY_ID), "" + bc.getCurrentDownloadCategory().getId());
			criteria.addOrder(bc.getManagerBean().getFieldName(ICMSAlias.DOWNLOAD_POSITION), false);
			List<ITransferObject> list = (List<ITransferObject>)bc.getManagerBean().getList(criteria);
			if (list.size() > 0) {
				Download d = (Download)list.get(0);
				position = d.getPosition();
				++position;
			}
		}catch (ManagerBeanException e) {
		} catch (ExpressionException e) {
		}
		return position;
	}
}