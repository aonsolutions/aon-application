package com.esferalia.aon.ui.pms.controller;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.hibernate.Query;
import org.hibernate.Session;

import com.code.aon.common.BeanManager;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.product.Item;
import com.code.aon.ui.form.BasicController;
import com.esferalia.aon.pms.Hotel;
import com.esferalia.aon.pms.ProjectReservation;
import com.esferalia.aon.pms.ProjectReservationRoom;
import com.esferalia.aon.pms.Room;

public class RoomAvailabilityController extends BasicController implements IPmsConstants {
	
	private FilterParams filterParams;
	private List<Room> availableRoomList;

	public FilterParams getFilterParams() {
		if (filterParams == null) {
			filterParams = new FilterParams();
		}
		return filterParams;
	}

	public void setFilterParams(FilterParams filterParams) {
		this.filterParams = filterParams;
	}

	public List<Room> getAvailableRoomList() throws ManagerBeanException {
		if (availableRoomList == null) {
			availableRoomList = new LinkedList<Room>();
			for (Object obj : obtainAvailableRoomList()) {
				Room room = (Room)BeanManager.getManagerBean(Room.class).get((Integer)obj);
				availableRoomList.add(room);
			}
		}
		return availableRoomList;
	}

	public void setAvailableRoomList(List<Room> availableRoomList) {
		this.availableRoomList = availableRoomList;
	}

	public int getAvailableRoomCount() throws ManagerBeanException {
		return getAvailableRoomList().size();
	}
	
	public void onInitializeRoomList(ProjectReservationRoom reservationRoom, Date startDate, Date endDate) {
		startDate = (startDate == null) ? reservationRoom.getProjectReservation().getStartDate() : startDate;
		endDate = (endDate == null) ? reservationRoom.getProjectReservation().getEndDate() : endDate;

		setAvailableRoomList(null);
		resetFilterParams(reservationRoom, startDate, endDate);
	}

	public void onFilter(ActionEvent event) {
		setAvailableRoomList(null);
	}

	public void onItemFilterChanged(ValueChangeEvent event) {
		setAvailableRoomList(null);
	}

	private void resetFilterParams(ProjectReservationRoom reservationRoom, Date startDate, Date endDate) {
		ProjectReservation reservation = reservationRoom.getProjectReservation();
		setFilterParams(null);
		getFilterParams().setHotel(reservation.getHotel());
		getFilterParams().setItem(reservationRoom.getItem());
		getFilterParams().setViewerStartDate(startDate);
		getFilterParams().setViewerEndDate(endDate);
	}
	
	private List<?> obtainAvailableRoomList() {
		String whereClause = "WHERE";
		if (getFilterParams().getHotel() != null && getFilterParams().getHotel().getId() != null) {
			whereClause += " Room.hotel = " + getFilterParams().getHotel().getId();
		} else {
			whereClause += " Room.hotel IS NOT NULL";
		}
		if (getFilterParams().getItem() != null && getFilterParams().getItem().getId() != null) {
			whereClause += " AND Room.item = " + getFilterParams().getItem().getId();
		}
		if (!StringUtils.isEmpty(getFilterParams().getName())) {
			whereClause += " AND Room.asset IN (SELECT id FROM asset WHERE name LIKE :name)";
		}
		if (getFilterParams().getViewerStartDate() != null && getFilterParams().getViewerEndDate() != null) {
			whereClause += " AND Room.asset NOT IN (SELECT asset FROM asset_activity WHERE date BETWEEN :start AND :end)";
		}
		if (getFilterParams().getFeatureFilter() != null && getFilterParams().getFeatureFilter().length > 0) {
			String featureClause = "";
			for (Integer id : getFilterParams().getFeatureFilter()) {
				if (!featureClause.equals("")) {
					featureClause += ",";
				}
				featureClause += id.toString();
			}
			if (featureClause != null) {
				whereClause += " AND AssetFeature.feature IN (" + featureClause + ")";
			}
		}

		Session session = HibernateUtil.getSession(HibernateUtil.getSessionFactoryName());
		String sqlSelect = "SELECT Room.asset " +
							"FROM room as Room " +
							"LEFT JOIN asset as Asset on Asset.id = Room.asset " +
							"LEFT JOIN asset_feature as AssetFeature on AssetFeature.asset = Room.asset " +
							whereClause +
							" GROUP BY Room.asset" +
							" ORDER BY Asset.name";
		Query sqlQuery = session.createSQLQuery(sqlSelect);
		if (!StringUtils.isEmpty(getFilterParams().getName())) {
			sqlQuery.setString("name", getFilterParams().getName() + "%");
		}
		if (getFilterParams().getViewerStartDate() != null && getFilterParams().getViewerEndDate() != null) {
			sqlQuery.setDate("start", getFilterParams().getViewerStartDate());
			sqlQuery.setDate("end", DateUtils.addDays(getFilterParams().getViewerEndDate(), -1));
		}
		return sqlQuery.list();
	}


	public class FilterParams {
		private Hotel hotel;
		private Item item;
		private String name;
		private Date viewerStartDate;
		private Date viewerEndDate;
		private Integer[] featureFilter;

		public FilterParams() {
			viewerStartDate = new Date();
			viewerEndDate = new Date();
		}
		
		public Hotel getHotel() {
			return hotel;
		}
		public void setHotel(Hotel hotel) {
			this.hotel = hotel;
		}

		public Item getItem() {
			return item;
		}
		public void setItem(Item item) {
			this.item = item;
		}

		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}

		public Date getViewerStartDate() {
			return viewerStartDate;
		}
		public void setViewerStartDate(Date viewerStartDate) {
			this.viewerStartDate = viewerStartDate;
		}

		public Date getViewerEndDate() {
			return viewerEndDate;
		}
		public void setViewerEndDate(Date viewerEndDate) {
			this.viewerEndDate = viewerEndDate;
		}

		public Integer[] getFeatureFilter() {
			return featureFilter;
		}
		public void setFeatureFilter(Integer[] featureFilter) {
			this.featureFilter = featureFilter;
		}

	}

}
