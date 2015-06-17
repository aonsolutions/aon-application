package com.esferalia.aon.ui.pms.event;

import java.util.Date;

import com.code.aon.asset.Asset;
import com.code.aon.asset.AssetActivity;
import com.code.aon.asset.enumeration.ActivityStatus;
import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.pms.Room;
import com.esferalia.aon.ui.pms.controller.RoomController;

public class RoomControllerListener extends ControllerAdapter {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
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
	public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		Room to = (Room)getController().getTo();
		if (!to.isActive()) {
			try {
				IManagerBean assetActivityBean = BeanManager.getManagerBean(AssetActivity.class);
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(assetActivityBean.getFieldName(IEntityAlias.ASSET_ACTIVITY_ASSET_ID), to.getId());
				criteria.addEqualExpression(assetActivityBean.getFieldName(IEntityAlias.ASSET_ACTIVITY_STATUS), ActivityStatus.BUSY);
				criteria.addGreaterThanOrEqualExpression(assetActivityBean.getFieldName(IEntityAlias.ASSET_ACTIVITY_DATE), new Date());
				if (assetActivityBean.getCount(criteria) > 0) {
					throw new ControllerListenerException("La Habitación no se puede desactivar. Esta asignada a una Reserva.");
				}
			} catch (ManagerBeanException ex) {
				throw new ControllerListenerException(ex.getMessage(), ex);
			}
		}
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
