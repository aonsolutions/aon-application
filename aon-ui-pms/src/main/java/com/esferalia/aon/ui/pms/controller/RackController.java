package com.esferalia.aon.ui.pms.controller;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

import com.code.aon.asset.enumeration.ActivityStatus;
import com.code.aon.AonVersion;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.common.util.CommonUtil;
import net.aonsolutions.core.dbutils.DatabaseUtil;
import net.aonsolutions.core.pool.AonConnectionException;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.pms.Hotel;
import com.esferalia.aon.pms.Room;
import com.esferalia.aon.pms.enumeration.ReservationStatus;
import com.esferalia.aon.ui.pms.event.RackSearchListener;

public class RackController extends BasicController implements IPmsConstants {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private FilterParams filterParams;
	private Map<Integer, List<RackTo>> rackActivityMap;

	public FilterParams getFilterParams() {
		if (filterParams == null) {
			filterParams = new FilterParams(this);
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

	public void onSearch(ActionEvent event) {
		try {
			clearCriteria();
		} catch (ManagerBeanException ex) {
		}
		super.onSearch(event);
	}

	@Override
	public List<ITransferObject> search(int start, int count) throws ManagerBeanException {
		List<ITransferObject> list = super.search(start, count);
		afterSearch(list);
		return list;
	}

	private void afterSearch(List<ITransferObject> rackList) throws ManagerBeanException {
		if (rackList.size() > 0) {
			Connection conn = null;
			PreparedStatement stmt = null;
			ResultSet rs = null;
			try {
				conn = DatabaseUtil.getConnection(AonUtil.getDomainName());

				String roomClause = "";
				for (ITransferObject ito : rackList) {
					Room room = (Room)ito;
					if (!roomClause.equals("")) {
						roomClause += ", ";
					}
					roomClause += room.getAsset().getId();
				}
				String sqlSelect = "SELECT asset_activity.asset, asset_activity.date, asset_activity.status, asset_activity.why, " +
					"project_reservation.project, project_reservation.code, project_reservation.start_date, project_reservation.end_date, " +
					"project_reservation.status, project_reservation_guest.name, project_reservation_guest.surname " +
					"FROM asset_activity " +
					"LEFT JOIN project_reservation_room_detail ON project_reservation_room_detail.asset_activity = asset_activity.id " +
					"LEFT JOIN project_reservation_room ON project_reservation_room.id = project_reservation_room_detail.project_reservation_room " +
					"LEFT JOIN project_reservation ON project_reservation.project = project_reservation_room.project_reservation " +
					"LEFT JOIN project_reservation_guest ON project_reservation_guest.project_reservation = project_reservation.project " +
						"AND project_reservation_guest.guest_index = 1 " +
					"WHERE" + DomainManager.getSQLWhereClause("asset_activity.domain") +
					"AND asset_activity.asset IN (" + roomClause + ") " +
					"AND asset_activity.date BETWEEN ? AND ? " +
					"ORDER BY asset_activity.asset, asset_activity.date";
				stmt = conn.prepareStatement(sqlSelect, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
				stmt.setDate(1, new java.sql.Date(getFilterParams().getViewerStartDate().getTime()));
				stmt.setDate(2, new java.sql.Date(getFilterParams().getViewerEndDate().getTime()));
				rs = stmt.executeQuery();
				buildRackActivityMap(rs);
			} catch (SQLException ex) {
				throw new ManagerBeanException(ex.getMessage(), ex);
			} catch (AonConnectionException ex) {
				throw new ManagerBeanException(ex.getMessage(), ex);
			} finally {
				DatabaseUtil.closeQuietly(rs);
				DatabaseUtil.closeQuietly(stmt);
				DatabaseUtil.closeQuietly(conn);
			}
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

	public void buildRackActivityMap(ResultSet rs) throws SQLException {
		rackActivityMap = new HashMap<Integer, List<RackTo>>();
		List<Date> rackDayList = getRackDayList();
		List<RackTo> rackActivityList = new LinkedList<RackTo>();

		Integer lastRoomId = null;
		while (rs.next()) {
			Integer roomId = rs.getInt(1);
			if (lastRoomId == null || lastRoomId.intValue() != roomId.intValue()) {
				rackActivityList = initializeRackActivityList(rackDayList.size());
				lastRoomId = roomId;
			}

			RackTo rackTo = new RackTo();
			rackTo.setRoomId(roomId);
			rackTo.setRoomDate(rs.getDate(2));
			rackTo.setRoomStatus(ActivityStatus.values()[rs.getByte(3)]);
			rackTo.setRoomComments(rs.getString(4));
			if (rs.getObject(5) != null) {
				rackTo.setReservationId(rs.getInt(5));
				rackTo.setReservationCode(rs.getString(6));
				rackTo.setReservationStart(rs.getDate(7));
				rackTo.setReservationEnd(rs.getDate(8));
				rackTo.setReservationStatus(ReservationStatus.values()[rs.getByte(9)]);
				String guest = StringUtils.isEmpty(rs.getString(10)) ? "" : rs.getString(10) + " ";
				guest += StringUtils.isEmpty(rs.getString(11)) ? "" : rs.getString(11);
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

	public void onLoadReservation(ActionEvent event) throws ManagerBeanException {
		ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
		Map<String, String> params = ec.getRequestParameterMap();

		BasicController reservationController = (BasicController)AonUtil.getRegisteredBean(RESERVATION_CONTROLLER_NAME);
		reservationController.onLoad(event, new Integer(params.get(RACK_RESERVATION)), RACK_LIST_NAME, RACK_CONTROLLER_NAME + ".onSearch");
	}


	public static class FilterParams implements Serializable {
		
		private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
		
		private RackController controller;
		
		private Date viewerStartDate;
		private Integer startDateIncrease;
		
		public FilterParams(RackController controller) {
			this.controller = controller;
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
			return (int)CommonUtil.getDaysBetweenDates(controller.getFilterParams().getViewerStartDate(), controller.getFilterParams().getViewerEndDate()) + 1;
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

	public static class RackTo implements Serializable {
		
		private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
		
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
	    	if (getReservationEnd() != null && getRoomDate() != null) {
	    		return getReservationEnd().equals(DateUtils.addDays(getRoomDate(), 1));
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
