package com.esferalia.aon.ui.pms.controller;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.faces.event.ActionEvent;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.form.BasicController;
import com.esferalia.aon.pms.dao.IPmsAlias;
import com.esferalia.aon.pms.enumeration.ReservationStatus;

public class ProjectReservationController extends BasicController {

	private String selectedTab;

	public String getSelectedTab() {
		return selectedTab;
	}

	public void setSelectedTab(String selectedTab) {
		this.selectedTab = selectedTab;
	}

	public void onLoad(ActionEvent event) throws ManagerBeanException {
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(new Date());
		getCriteria().addEqualExpression(getFieldName(IPmsAlias.PROJECT_RESERVATION_START_DATE), new Date());
		getCriteria().addEqualExpression(getFieldName(IPmsAlias.PROJECT_RESERVATION_STATUS), ReservationStatus.ACTIVE);
		onSearch(event);
	}

}