package com.esferalia.aon.ui.pms.controller;

import java.util.List;

import javax.faces.model.SelectItem;

import com.code.aon.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.warehouse.controller.WarehouseCollectionsController;
import com.esferalia.aon.pms.Hotel;

public class HotelController extends BasicController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	public List<SelectItem> getWarehouses() throws ManagerBeanException {
		Hotel hotel = (Hotel) getTo();
		return WarehouseCollectionsController.getWarehouses(hotel.getWorkPlace(), true);
	}
		
}