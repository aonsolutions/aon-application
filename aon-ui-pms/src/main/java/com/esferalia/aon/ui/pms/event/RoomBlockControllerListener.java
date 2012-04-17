package com.esferalia.aon.ui.pms.event;

import java.util.Date;

import com.code.aon.asset.AssetActivity;
import com.code.aon.asset.enumeration.ActivityStatus;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.pms.ProjectReservation;
import com.esferalia.aon.pms.ProjectReservationRoomDetail;
import com.esferalia.aon.pms.Room;
import com.esferalia.aon.ui.pms.controller.IPmsConstants;

public class RoomBlockControllerListener extends ControllerAdapter {
	
	@Override
	public void beforeModelInitialized(ControllerEvent event)
			throws ControllerListenerException {
		try {
			getController().getCriteria().addEqualExpression(getController().getFieldName(IEntityAlias.ASSET_ACTIVITY_STATUS), ActivityStatus.BLOCKED);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException("Error al buscar.");
		}
	}
	
	@Override
	public void beforeModelSearched(ControllerEvent event)
			throws ControllerListenerException {
		try {
			getController().getCriteria().addEqualExpression(getController().getFieldName(IEntityAlias.ASSET_ACTIVITY_STATUS), ActivityStatus.BLOCKED);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException("Error al buscar.");
		}
	}
	
	@Override
	public void beforeBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		IController roomController = FormUtil.getController(IPmsConstants.ROOM_CONTROLLER_NAME);
		AssetActivity aa = (AssetActivity) this.getController().getTo();
		if(!isFreeRoom()){
			ProjectReservation reservation = getProjectReservation((Room)roomController.getTo(), aa.getDate()); 
			String msg = "Imposible continuar. ";
			if(reservation!=null){
				msg += "La habitacion ha sido asignada en la fecha indicada a la reserva nº "+reservation.getId();
			}
			throw new ControllerListenerException(msg);
		}
		aa.setAsset(((Room)roomController.getTo()).getAsset());
		aa.setStatus(ActivityStatus.BLOCKED);
		aa.setFromTime(aa.getDate());
		aa.setToTime(aa.getDate());
	}

	@Override
	public void beforeBeanUpdated(ControllerEvent event)
			throws ControllerListenerException {
		IController roomController = FormUtil.getController(IPmsConstants.ROOM_CONTROLLER_NAME);
		AssetActivity aa = (AssetActivity) this.getController().getTo();
		if(!isFreeRoom()){
			ProjectReservation reservation = getProjectReservation((Room)roomController.getTo(), aa.getDate()); 
			String msg = "Imposible continuar. ";
			if(reservation!=null){
				msg += "La habitacion ha sido asignada en la fecha indicada a la reserva nº "+reservation.getId();
			}
			throw new ControllerListenerException(msg);
		}
	}
	
	private boolean isFreeRoom() throws ControllerListenerException {
		IController roomController = FormUtil.getController(IPmsConstants.ROOM_CONTROLLER_NAME);
		Room room = (Room)roomController.getTo(); 
		try {
			IManagerBean bean = BeanManager.getManagerBean(AssetActivity.class);
			AssetActivity aa = (AssetActivity) this.getController().getTo();
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.ASSET_ACTIVITY_ASSET_ID), room.getAsset().getId());
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.ASSET_ACTIVITY_DATE), aa.getDate());
			return bean.getCount(criteria)<=0;
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException("Error al comprobar la disponibilidad de la estancia.");
		}
	}
	
	private ProjectReservation getProjectReservation(Room room, Date date) {
		try {
			IManagerBean bean = BeanManager.getManagerBean(ProjectReservationRoomDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.PROJECT_RESERVATION_ROOM_DETAIL_ASSET_ACTIVITY_ASSET_ID), room.getAsset().getId());
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.PROJECT_RESERVATION_ROOM_DETAIL_ASSET_ACTIVITY_DATE), date);
			return ((ProjectReservationRoomDetail) bean.getList(criteria).get(0)).getProjectReservationRoom().getProjectReservation();
		} catch (Exception e) {
		}
		return null;
	}
	
	
}
