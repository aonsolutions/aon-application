package com.esferalia.aon.ui.pms.controller;

import com.code.aon.product.Item;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.code.aon.ui.form.LinesController;
import com.esferalia.aon.pms.ProjectReservationRoom;

public class ProjectReservationRoomController extends LinesController {

	public void onItemChanged(LookupChangeEvent event) {
		ProjectReservationRoom reservationRoom = (ProjectReservationRoom)getTo();
		if (event.getNewValue() != null && !event.getNewValue().toString().equals("")) {
			Item item = (Item)event.getNewValue();
			reservationRoom.setItem(item);
		}
	}	

}