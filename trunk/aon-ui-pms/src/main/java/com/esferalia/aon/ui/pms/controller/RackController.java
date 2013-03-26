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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.hibernate.Query;
import org.hibernate.Session;

import com.code.aon.asset.enumeration.ActivityStatus;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.pms.Hotel;
import com.esferalia.aon.pms.Room;
import com.esferalia.aon.pms.enumeration.ReservationStatus;
import com.esferalia.aon.ui.pms.event.RackSearchListener;

public class RackController extends BasicController implements IPmsConstants {
	
	private FilterParams filterParams;
	private Map<Integer, List<RackTo>> rackActivityMap;

	public FilterParams getFilterParams() {
		if (filterParams == null) {
			filterParams = new FilterParams();
		}
		return filterParams;
	}

	public void setFilterParams(FilterParams filterParams) {
		this.filterParams = filterParams;
	}

	public Map<Integer, List<RackTo>> getRackActivityMap() {
		return rackActivityMap;
	}

	public void setRackActivityMap(Map<Integer, List<RackTo>> rackActivityMap) {
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

	@Override
	public List<ITransferObject> search(int start, int count) throws ManagerBeanException {
		List<ITransferObject> list = super.search(start, count);
		afterSearch(list);
		return list;
	}

	@SuppressWarnings("unchecked")
	private void afterSearch(List<ITransferObject> rackList) throws ManagerBeanException {
		if (rackList.size() > 0) {
			String roomClause = "";
			for (ITransferObject ito : rackList) {
				Room room = (Room)ito;
				if (!roomClause.equals("")) {
					roomClause += ", ";
				}
				roomClause += room.getAsset().getId();
			}
			String whereClause = "WHERE " + DomainManager.getSQLWhereClause("AssetActivity.domain");
			whereClause += " AND AssetActivity.asset IN (" + roomClause + ") AND AssetActivity.date BETWEEN :start AND :end";
			
			Session session = HibernateUtil.getSession(HibernateUtil.getSessionFactoryName());
			String sqlSelect = "SELECT AssetActivity.asset, AssetActivity.date, AssetActivity.status, AssetActivity.why, " +
								"ProjectReservation.project, ProjectReservation.code, ProjectReservation.start_date, ProjectReservation.end_date, " +
								"ProjectReservation.status, ProjectReservationGuest.name, ProjectReservationGuest.surname " +
								"FROM asset_activity as AssetActivity " +
								"LEFT JOIN project_reservation_room_detail as ProjectReservationRoomDetail " +
									"ON ProjectReservationRoomDetail.asset_activity = AssetActivity.id " +
								"LEFT JOIN project_reservation_room ProjectReservationRoom " +
									"ON ProjectReservationRoom.id = ProjectReservationRoomDetail.project_reservation_room " +
								"LEFT JOIN project_reservation as ProjectReservation " +
									"ON ProjectReservation.project = ProjectReservationRoom.project_reservation " +
								"LEFT JOIN project_reservation_guest as ProjectReservationGuest " +
									"ON ProjectReservationGuest.project_reservation = ProjectReservationRoom.project_reservation " +
									"AND ProjectReservationGuest.guest_index = 1 " +
								whereClause +
								" ORDER BY AssetActivity.asset, AssetActivity.date";
			Query sqlQuery = session.createSQLQuery(sqlSelect);
			sqlQuery.setDate("start", getFilterParams().getViewerStartDate());
			sqlQuery.setDate("end", getFilterParams().getViewerEndDate());
			buildRackActivityMap(sqlQuery.list());
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

	public void buildRackActivityMap(List<Object[]> assetReservationList) throws ManagerBeanException {
		rackActivityMap = new HashMap<Integer, List<RackTo>>();
		List<Date> rackDayList = getRackDayList();
		List<RackTo> rackActivityList = new LinkedList<RackTo>();

		Integer lastRoomId = null;
		for (Object[] assetReservationObj : assetReservationList) {
			Integer roomId = (Integer)assetReservationObj[0];
			if (lastRoomId == null || lastRoomId.intValue() != roomId.intValue()) {
				rackActivityList = initializeRackActivityList(rackDayList.size());
				lastRoomId = roomId;
			}
			
			RackTo rackTo = new RackTo();
			rackTo.setRoomId(roomId);
			rackTo.setRoomDate((Date)assetReservationObj[1]);
			rackTo.setRoomStatus(ActivityStatus.values()[(Byte)assetReservationObj[2]]);
			rackTo.setRoomComments((String)assetReservationObj[3]);
			if (assetReservationObj[4] != null) {
				rackTo.setReservationId((Integer)assetReservationObj[4]);
				rackTo.setReservationCode((String)assetReservationObj[5]);
				rackTo.setReservationStart((Date)assetReservationObj[6]);
				rackTo.setReservationEnd((Date)assetReservationObj[7]);
				rackTo.setReservationStatus(ReservationStatus.values()[(Byte)assetReservationObj[8]]);
				String guest = StringUtils.isEmpty((String)assetReservationObj[9]) ? "" : (String)assetReservationObj[9] + " ";
				guest += StringUtils.isEmpty((String)assetReservationObj[10]) ? "" : (String)assetReservationObj[10];
				rackTo.setReservationGuest(guest);
			}

			rackActivityList.set(rackDayList.indexOf(rackTo.getRoomDate()), rackTo);
			rackActivityMap.put(roomId, rackActivityList);
		}
	}

	private List<RackTo> initializeRackActivityList(int size) {
		List<RackTo> rackActivityList = new LinkedList<RackTo>();
		for (int i=0; i<size; i++) {
			rackActivityList.add(new RackTo());
		}
		return rackActivityList;
	}

	public List<RackTo> getRackActivityList() {
		if (model.isRowAvailable()) {
			Room room = (Room)model.getRowData();
			if (rackActivityMap.containsKey(room.getId())) {
				return rackActivityMap.get(room.getId());
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
			return DateUtils.addMonths(viewerStartDate, 1);
		}
		public int getViewerDays() {
			return (int)CommonUtil.getDaysBetweenDates(getFilterParams().getViewerStartDate(), getFilterParams().getViewerEndDate()) + 1;
		}

		public Integer getStartDateIncrease() {
			if(startDateIncrease == null){
				startDateIncrease = 3;
			}
			return startDateIncrease;
		}
		public void setStartDateIncrease(Integer startDateIncrease) {
			this.startDateIncrease = startDateIncrease;
		}

	}

	public class RackTo {
		private Integer roomId;
		private Date roomDate;
		private ActivityStatus roomStatus;
		private String roomComments;
		private Integer reservationId;
		private String reservationCode;
		private Date reservationStart;
		private Date reservationEnd;
		private ReservationStatus reservationStatus;
		private String reservationGuest;

		public Integer getRoomId() {
			return roomId;
		}
		public void setRoomId(Integer roomId) {
			this.roomId = roomId;
		}

		public Date getRoomDate() {
			return roomDate;
		}
		public void setRoomDate(Date roomDate) {
			this.roomDate = roomDate;
		}

		public ActivityStatus getRoomStatus() {
			return roomStatus;
		}
		public void setRoomStatus(ActivityStatus roomStatus) {
			this.roomStatus = roomStatus;
		}

		public String getRoomComments() {
			return roomComments;
		}
		public void setRoomComments(String roomComments) {
			this.roomComments = roomComments;
		}

		public Integer getReservationId() {
			return reservationId;
		}
		public void setReservationId(Integer reservationId) {
			this.reservationId = reservationId;
		}

		public String getReservationCode() {
			return reservationCode;
		}
		public void setReservationCode(String reservationCode) {
			this.reservationCode = reservationCode;
		}

		public Date getReservationStart() {
			return reservationStart;
		}
		public void setReservationStart(Date reservationStart) {
			this.reservationStart = reservationStart;
		}

		public Date getReservationEnd() {
			return reservationEnd;
		}
		public void setReservationEnd(Date reservationEnd) {
			this.reservationEnd = reservationEnd;
		}

		public ReservationStatus getReservationStatus() {
			return reservationStatus;
		}
		public void setReservationStatus(ReservationStatus reservationStatus) {
			this.reservationStatus = reservationStatus;
		}

		public String getReservationGuest() {
			return reservationGuest;
		}
		public void setReservationGuest(String reservationGuest) {
			this.reservationGuest = reservationGuest;
		}

	    public boolean isFirstNight() {
	    	if (getReservationStart() != null && getRoomDate() != null) {
	    		return getReservationStart().equals(getRoomDate());
	    	}
	    	return false;
	    }

	    public boolean isLastNight() {
	    	if (getReservationStart() != null && getRoomDate() != null) {
	    		return getReservationEnd().equals(getRoomDate());
	    	}
	    	return false;
	    }

	    public boolean isInvoiced() {
	    	if (getReservationStatus() != null) {
	    		return getReservationStatus().equals(ReservationStatus.INVOICED);
	    	}
	    	return false;
	    }

	}

}
