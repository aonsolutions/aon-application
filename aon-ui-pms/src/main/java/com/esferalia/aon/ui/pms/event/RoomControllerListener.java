package com.esferalia.aon.ui.pms.event;

import javax.faces.event.AbortProcessingException;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.asset.controller.IAssetConstants;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
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
}
