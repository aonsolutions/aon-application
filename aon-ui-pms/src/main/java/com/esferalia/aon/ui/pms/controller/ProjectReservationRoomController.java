package com.esferalia.aon.ui.pms.controller;

import javax.faces.event.ActionEvent;

import com.code.aon.product.Item;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.pms.ProjectReservationRoom;

public class ProjectReservationRoomController extends LinesController {
	
	private boolean showRoomOccupationWindow;
	
	public boolean isShowRoomOccupationWindow() {
		return showRoomOccupationWindow;
	}

	public void setShowRoomOccupationWindow(boolean showRoomOccupationWindow) {
		this.showRoomOccupationWindow = showRoomOccupationWindow;
	}

	public void onItemChanged(LookupChangeEvent event) {
		ProjectReservationRoom reservationRoom = (ProjectReservationRoom)getTo();
		if (event.getNewValue() != null && !event.getNewValue().toString().equals("")) {
			Item item = (Item)event.getNewValue();
			reservationRoom.setItem(item);
		}
	}	
	
	public void onShowRoomOccupationModal(ActionEvent event) {
		ReservationTableController controller = (ReservationTableController) AonUtil.getRegisteredBean(IPmsConstants.RESERVATION_TABLE_CONTROLLER_NAME);
		controller.loadFilterParams((ProjectReservationRoom) this.getTo());
	}
	
	private void getSelectedAvailableRoom(){
		ReservationTableController controller = (ReservationTableController) AonUtil.getRegisteredBean(IPmsConstants.RESERVATION_TABLE_CONTROLLER_NAME);
		controller.getRoom();
	}

}