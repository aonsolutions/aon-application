package com.esferalia.aon.ui.pms.event;

import com.code.aon.asset.Asset;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.esferalia.aon.pms.Room;
import com.esferalia.aon.ui.pms.controller.RoomController;

public class RoomControllerListener extends ControllerAdapter {

	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		RoomController roomController = (RoomController)event.getController();
		if (roomController.getLastRoomAdded() != null) {
			((Room)roomController.getTo()).setHotel(roomController.getLastRoomAdded().getHotel());
			((Room)roomController.getTo()).setItem(roomController.getLastRoomAdded().getItem());
		}
	}

	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		RoomController roomController = (RoomController)event.getController();
		roomController.setLastRoomAdded((Room)roomController.getTo());
	}

	@Override
	public void afterBeanRemoved(ControllerEvent event)	throws ControllerListenerException {
		try {
			IManagerBean assetBean = BeanManager.getManagerBean(Asset.class);
			assetBean.remove(assetBean.get(((Room)getController().getTo()).getId()));
		} catch (ManagerBeanException ex) {
			throw new ControllerListenerException(ex.getMessage(), ex);
		}
	}

}
