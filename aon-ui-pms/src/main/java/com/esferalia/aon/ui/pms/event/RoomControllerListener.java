package com.esferalia.aon.ui.pms.event;

import javax.faces.event.AbortProcessingException;

import com.code.aon.asset.Asset;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.ui.asset.controller.IAssetConstants;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.pms.Room;

public class RoomControllerListener extends ControllerAdapter {
	
	@Override
	public void afterBeanSelected(ControllerEvent event)
			throws ControllerListenerException {
		BasicController controller = (BasicController)FormUtil.getController(IAssetConstants.ASSET_CONTROLLER_NAME);
		Room room = (Room) event.getController().getTo();
		try {
			controller.select(null, room.getId());
		} catch (ManagerBeanException e) {
			throw new AbortProcessingException("error al seleccionar el recurso.");
		}
	}
	
	@Override
	public void afterBeanRemoved(ControllerEvent event)
			throws ControllerListenerException {
		try {
			IManagerBean bean = BeanManager.getManagerBean(Asset.class);
			bean.remove( bean.get(((Room)getController().getTo()).getId()) );
		} catch (ManagerBeanException e) {
			throw new AbortProcessingException("error al borrar el recurso.");
		}
	}
	
	@Override
	public void afterBeanCreated(ControllerEvent event)
			throws ControllerListenerException {
		try {
			IManagerBean bean = BeanManager.getManagerBean(Room.class);
			Criteria criteria = new Criteria();
			Projection projection = Projection.max(bean.getFieldName(IEntityAlias.ROOM_ID));
			Object value = bean.getUniqueResult(projection, criteria);
			value.toString();
			if( value != null ){
				Room room = (Room) bean.get((Integer)value);
				Room newRoom = (Room) getController().getTo();
				newRoom.setHotel(room.getHotel());
				newRoom.setItem(room.getItem());
			}
		} catch (ManagerBeanException e) {
			throw new AbortProcessingException("error al sugerir los nuevos datos.");
		}
	}
	
}
