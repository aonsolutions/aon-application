package com.code.aon.ui.cms.event;

import java.util.List;

import com.code.aon.cms.AlbumCategory;
import com.code.aon.cms.dao.ICMSAlias;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class AlbumCategoryControllerListener extends ControllerAdapter {

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		AlbumCategory albumCategory = (AlbumCategory)event.getController().getTo();
		albumCategory.setActive(true);
		albumCategory.setPosition(getLastPosition(event));
	}
	
	private int getLastPosition(ControllerEvent event) {
		int position = 0;
		try{
			Criteria criteria = new Criteria();
			criteria.addOrder(event.getController().getManagerBean().getFieldName(ICMSAlias.ALBUM_CATEGORY_POSITION), false);
			List<ITransferObject> list = (List<ITransferObject>)event.getController().getManagerBean().getList(criteria);
			if (list.size() > 0) {
				AlbumCategory albumCategory = (AlbumCategory)list.get(0);
				position = albumCategory.getPosition();
				++position;
			}
		}catch (ManagerBeanException e) {
		}
		return position;
	}
}