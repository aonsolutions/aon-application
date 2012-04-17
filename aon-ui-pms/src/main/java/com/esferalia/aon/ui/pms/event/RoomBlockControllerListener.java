package com.esferalia.aon.ui.pms.event;

import javax.faces.event.AbortProcessingException;

import com.code.aon.asset.AssetActivity;
import com.code.aon.asset.enumeration.ActivityStatus;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.pms.Room;

public class RoomBlockControllerListener extends ControllerAdapter {
	
	@Override
	public void beforeModelInitialized(ControllerEvent event)
			throws ControllerListenerException {
		try {
			getController().getCriteria().addEqualExpression(getController().getFieldName(IEntityAlias.ASSET_ACTIVITY_STATUS), ActivityStatus.BLOCKED);
		} catch (ManagerBeanException e) {
			throw new AbortProcessingException("error al buscar.");
		}
	}
	
	@Override
	public void beforeModelSearched(ControllerEvent event)
			throws ControllerListenerException {
		try {
			getController().getCriteria().addEqualExpression(getController().getFieldName(IEntityAlias.ASSET_ACTIVITY_STATUS), ActivityStatus.BLOCKED);
		} catch (ManagerBeanException e) {
			throw new AbortProcessingException("error al buscar.");
		}
	}
	
	@Override
	public void beforeBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		IController roomController = FormUtil.getController("room");
		AssetActivity aa = (AssetActivity) this.getController().getTo();
		aa.setAsset(((Room)roomController.getTo()).getAsset());
		aa.setStatus(ActivityStatus.BLOCKED);
		aa.setFromTime(aa.getDate());
		aa.setToTime(aa.getDate());
	}
}
