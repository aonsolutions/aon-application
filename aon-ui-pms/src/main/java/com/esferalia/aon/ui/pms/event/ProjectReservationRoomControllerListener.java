package com.esferalia.aon.ui.pms.event;

import java.util.Arrays;

import org.apache.commons.lang.ArrayUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.pms.ProjectReservationRoom;
import com.esferalia.aon.pms.ProjectReservationService;
import com.esferalia.aon.ui.pms.controller.ProjectReservationRoomController;

public class ProjectReservationRoomControllerListener extends ControllerAdapter {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		ProjectReservationRoom to = (ProjectReservationRoom)event.getController().getTo();
		to.setRoomIndex(0);
	}

	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		ProjectReservationRoomController controller = (ProjectReservationRoomController)event.getController();
		linkServicesToRoom((ProjectReservationRoom)controller.getTo(), controller.getLinkedServices());
	}

	@Override
	public void afterBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		ProjectReservationRoomController controller = (ProjectReservationRoomController)event.getController();
		linkServicesToRoom((ProjectReservationRoom)controller.getTo(), controller.getLinkedServices());
	}

	private void linkServicesToRoom(ProjectReservationRoom reservationRoom, Integer[] services) throws ControllerListenerException {
		try {
			IManagerBean reservationServiceBean = BeanManager.getManagerBean(ProjectReservationService.class);
			Criteria criteria = new Criteria();
			String alias = reservationServiceBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_SERVICE_PROJECT_RESERVATION_ID);
			criteria.addEqualExpression(alias, reservationRoom.getProjectReservation().getId());
			if (services != null && services.length > 0) {
				alias = reservationServiceBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_SERVICE_ID);
				Expression idExpr = ExpressionUtilities.getInExpression(alias, Arrays.asList(services));
				alias = reservationServiceBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_SERVICE_PROJECT_RESERVATION_ROOM);
				Expression roomExpr = ExpressionUtilities.getEqualExpression(alias, reservationRoom.getId());
				criteria.addExpression(ExpressionUtilities.getOrExpression(idExpr, roomExpr));
			} else {
				alias = reservationServiceBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_SERVICE_PROJECT_RESERVATION_ROOM);
				criteria.addEqualExpression(alias, reservationRoom.getId());
			}
			for (ITransferObject ito : reservationServiceBean.getList(criteria)) {
				ProjectReservationService reservationService = (ProjectReservationService)ito;
				if (ArrayUtils.indexOf(services, reservationService.getId()) >= 0) {
					if (reservationService.getProjectReservationRoom() == null || reservationService.getProjectReservationRoom() != reservationRoom.getId()) {
			    		reservationService.setProjectReservationRoom(reservationRoom.getId());
			    		reservationServiceBean.update(reservationService);
			    	}
				} else {
		    		reservationService.setProjectReservationRoom(null);
		    		reservationServiceBean.update(reservationService);
				}
			}
		} catch (ManagerBeanException ex) {
			throw new ControllerListenerException(ex.getMessage());
		}
	}

}