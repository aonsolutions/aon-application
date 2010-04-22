package com.code.aon.ui.cms.event;

import java.util.List;

import com.code.aon.cms.Album;
import com.code.aon.cms.dao.ICMSAlias;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.cms.controller.AlbumController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class AlbumControllerListener extends ControllerAdapter {
	
	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		Album album = (Album)event.getController().getTo();
		album.setActive(true);
	}

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		AlbumController ac = (AlbumController)event.getController();
		Album a = (Album)event.getController().getTo();
		a.setAlbumCategory(ac.getCurrentAlbumCategory());
		a.setPosition(getLastPosition(ac));
	}
	
	private int getLastPosition(AlbumController ac) {
		int position = 0;
		try{
			Criteria criteria = new Criteria();
			criteria.addExpression(ac.getManagerBean().getFieldName(ICMSAlias.ALBUM_ALBUM_CATEGORY_ID), "" + ac.getCurrentAlbumCategory().getId());
			criteria.addOrder(ac.getManagerBean().getFieldName(ICMSAlias.ALBUM_POSITION), false);
			List<ITransferObject> list = (List<ITransferObject>)ac.getManagerBean().getList(criteria);
			if (list.size() > 0) {
				Album a = (Album)list.get(0);
				position = a.getPosition();
				++position;
			}
		}catch (ManagerBeanException e) {
		} catch (ExpressionException e) {
		}
		return position;
	}
}