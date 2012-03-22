package com.esferalia.aon.ui.pms.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.time.DateUtils;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.pms.Hotel;
import com.esferalia.aon.pms.ProjectReservationRoomDetail;
import com.esferalia.aon.pms.Room;
import com.esferalia.aon.ui.pms.event.RackSearchListener;

public class RackController extends BasicController implements IPmsConstants {
	
	private FilterParams filterParams;
	private Map<Room, List<ProjectReservationRoomDetail>> rackActivityMap;

	public FilterParams getFilterParams() {
		if (filterParams == null) {
			filterParams = new FilterParams();
		}
		return filterParams;
	}

	public void setFilterParams(FilterParams filterParams) {
		this.filterParams = filterParams;
	}

	public Map<Room, List<ProjectReservationRoomDetail>> getRackActivityMap() {
		return rackActivityMap;
	}

	public void setRackActivityMap(Map<Room, List<ProjectReservationRoomDetail>> rackActivityMap) {
		this.rackActivityMap = rackActivityMap;
	}

	public void onEditSearch(ActionEvent event) {
		super.onEditSearch(event);
		setFilterParams(null);
		try {
			RackSearchListener searcher = (RackSearchListener)AonUtil.getRegisteredBean(RACK_SEARCH_LISTENER_NAME);
			searcher.setHotel(obtainHotel());
		} catch (ManagerBeanException ex) {
			String msg = "No se pudo obtener la lista de Hoteles.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}

	private Hotel obtainHotel() throws ManagerBeanException {
		PmsCollectionsController collections = (PmsCollectionsController)AonUtil.getRegisteredBean(COLLECTIONS_CONTROLLER_NAME);
		List<SelectItem> hotelList = collections.getCurrentUserHotels();
		return (hotelList.size() > 0) ? (Hotel)hotelList.get(0).getValue() : null;
	}

	public void onHotelChanged(ValueChangeEvent event) throws ManagerBeanException {
		if (event.getNewValue() != null && !event.getNewValue().toString().equals("")) {
		}
	}

	public List<Date> getRackDayList() {
		List<Date> rackDays = new LinkedList<Date>();
		Date date = getFilterParams().getViewerStartDate();
		while(date.compareTo(getFilterParams().getViewerEndDate()) <= 0) {
			rackDays.add(date);
			date = DateUtils.addDays(date, 1);
		}
		return rackDays;
	}

	public void buildRackActivityMap(List<ITransferObject> reservationRoomDetailList) throws ManagerBeanException {
		rackActivityMap = new HashMap<Room, List<ProjectReservationRoomDetail>>();
		List<Date> rackDayList = getRackDayList();
		List<ProjectReservationRoomDetail> rackActivityList = new LinkedList<ProjectReservationRoomDetail>();

		Room lastRoom = null;
		for (ITransferObject ito : reservationRoomDetailList) {
			ProjectReservationRoomDetail reservationRoomDetail = (ProjectReservationRoomDetail)ito;
			Room room = reservationRoomDetail.getRoom();
			if (lastRoom == null || !lastRoom.equals(room)) {
				rackActivityList = initializeRackActivityList(rackDayList.size());
				lastRoom = room;
			}
			rackActivityList.set(rackDayList.indexOf(reservationRoomDetail.getAssetActivity().getDate()), reservationRoomDetail);
			rackActivityMap.put(room, rackActivityList);
		}
	}

	private List<ProjectReservationRoomDetail> initializeRackActivityList(int size) {
		List<ProjectReservationRoomDetail> rackActivityList = new LinkedList<ProjectReservationRoomDetail>();
		for (int i=0; i<size; i++) {
			rackActivityList.add(new ProjectReservationRoomDetail());
		}
		return rackActivityList;
	}

	public List<ProjectReservationRoomDetail> getRackActivityList() {
		if (model.isRowAvailable()) {
			Room room = (Room)model.getRowData();
			if (rackActivityMap.containsKey(room)) {
				return rackActivityMap.get(room);
			}
		}
		return initializeRackActivityList(getFilterParams().getViewerDays());
	}

	public void onDecreaseStartDate(ActionEvent event) throws ManagerBeanException {
		modifyViewerStartDate(-1);
		onSearch(event);
	}

	public void onIncreaseStartDate(ActionEvent event) throws ManagerBeanException {
		modifyViewerStartDate(1);
		onSearch(event);
	}

	private void modifyViewerStartDate(int units) {
		Date viewerStartDate = getFilterParams().getViewerStartDate();
		switch (getFilterParams().getStartDateIncrease()) {
			case 3: viewerStartDate = DateUtils.addMonths(viewerStartDate, units);
					break;
			case 2: viewerStartDate = DateUtils.addDays(viewerStartDate, units*15);
				break;
			case 1: viewerStartDate = DateUtils.addWeeks(viewerStartDate, units);
					break;
			default: viewerStartDate = DateUtils.addDays(viewerStartDate, units);
					break;
		}
		getFilterParams().setViewerStartDate(viewerStartDate);
	}

	public void onSearch(ActionEvent event) {
		try {
			clearCriteria();
		} catch (ManagerBeanException ex) {
		}
		super.onSearch(event);
	}

	public void onLoadReservation(ActionEvent event) throws ManagerBeanException {
		ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
		Map<String, String> params = ec.getRequestParameterMap();

		BasicController reservationController = (BasicController)AonUtil.getRegisteredBean(RESERVATION_CONTROLLER_NAME);
		reservationController.onLoad(event, new Integer(params.get(RACK_RESERVATION)), RACK_LIST_NAME, RACK_CONTROLLER_NAME + ".onSearch");
	}


	public class FilterParams {
		private Date viewerStartDate;
		private Integer startDateIncrease;

		public FilterParams() {
			viewerStartDate = new Date();
		}
		
		public Date getViewerStartDate() {
			return viewerStartDate;
		}
		public void setViewerStartDate(Date viewerStartDate) {
			this.viewerStartDate = viewerStartDate;
		}
		public Date getViewerEndDate() {
			return DateUtils.addDays(viewerStartDate, 15);
		}
		public int getViewerDays() {
			return (int)CommonUtil.getDaysBetweenDates(getFilterParams().getViewerStartDate(), getFilterParams().getViewerEndDate()) + 1;
		}

		public Integer getStartDateIncrease() {
			if(startDateIncrease == null){
				startDateIncrease = 2;
			}
			return startDateIncrease;
		}
		public void setStartDateIncrease(Integer startDateIncrease) {
			this.startDateIncrease = startDateIncrease;
		}

	}

}
