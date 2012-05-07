package com.esferalia.aon.ui.pms.event;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.pms.ProjectReservationRoomDetail;
import com.esferalia.aon.pms.Room;
import com.esferalia.aon.ui.pms.controller.RackController;

public class RackControllerListener extends ControllerAdapter {
	
	@Override
	public void afterModelSearched(ControllerEvent event) throws ControllerListenerException {
		RackController controller = (RackController)event.getController();
		try {
			List<Integer> rooms = new LinkedList<Integer>();
			for (ITransferObject ito : controller.getManagerBean().getList(controller.getCriteria())) {
				Room room = (Room)ito;
				rooms.add(room.getId());
			}

			if (rooms.size() > 0) {
				Date fromDate = controller.getFilterParams().getViewerStartDate();
				Date toDate = controller.getFilterParams().getViewerEndDate();
				IManagerBean reservationRoomDetailBean = BeanManager.getManagerBean(ProjectReservationRoomDetail.class);
				Criteria criteria = new Criteria();
				String alias = reservationRoomDetailBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_ROOM_DETAIL_ASSET_ACTIVITY_ASSET_ID);
				criteria.addExpression(ExpressionUtilities.getInExpression(alias, rooms));
				criteria.addBetweenExpression(reservationRoomDetailBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_ROOM_DETAIL_ASSET_ACTIVITY_DATE), fromDate, toDate);
				criteria.addOrder(reservationRoomDetailBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_ROOM_DETAIL_ASSET_ACTIVITY_ASSET_ID));
				criteria.addOrder(reservationRoomDetailBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_ROOM_DETAIL_ASSET_ACTIVITY_DATE));
				controller.buildRackActivityMap(reservationRoomDetailBean.getList(criteria));
			}
		} catch (ManagerBeanException ex) {
			throw new ControllerListenerException(ex.getMessage(), ex);
		}
	}

}