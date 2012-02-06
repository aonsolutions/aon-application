package com.esferalia.aon.ui.pms.controller;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.hibernate.Query;
import org.hibernate.Session;

import com.code.aon.common.BeanManager;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.product.Item;
import com.code.aon.ui.form.BasicController;
import com.esferalia.aon.pms.Hotel;
import com.esferalia.aon.pms.ProjectReservation;
import com.esferalia.aon.pms.ProjectReservationRoom;
import com.esferalia.aon.pms.Room;

public class RoomAvailabilityController extends BasicController implements IPmsConstants {
	
	private FilterParams filterParams;
	private Room availableRoom;
	private List<SelectItem> availableRoomList;

	public FilterParams getFilterParams() {
		if (filterParams == null) {
			filterParams = new FilterParams();
		}
		return filterParams;
	}

	public void setFilterParams(FilterParams filterParams) {
		this.filterParams = filterParams;
	}

	public Room getAvailableRoom() {
		return availableRoom;
	}

	public void setAvailableRoom(Room availableRoom) {
		this.availableRoom = availableRoom;
	}

	public List<SelectItem> getAvailableRoomList() throws ManagerBeanException {
		if (availableRoomList == null) {
			availableRoomList = new LinkedList<SelectItem>();
			for (Object obj : obtainAvailableRoomList()) {
				Room room = (Room)BeanManager.getManagerBean(Room.class).get((Integer)obj);
				SelectItem selectItem = new SelectItem(room, room.getAsset().getName());
				availableRoomList.add(selectItem);
			}

			if (availableRoom == null && availableRoomList.size() > 0) {
				availableRoom = (Room)availableRoomList.get(0).getValue();
			}
		}
		return availableRoomList;
	}

	public void setAvailableRoomList(List<SelectItem> availableRoomList) {
		this.availableRoomList = availableRoomList;
	}

	public int getAvailableRoomCount() throws ManagerBeanException {
		return getAvailableRoomList().size();
	}
	
	public void onInitializeRoomList(ProjectReservationRoom reservationRoom) {
		setAvailableRoom(null);
		setAvailableRoomList(null);
		resetFilterParams(reservationRoom);
	}

	public void onFilter(ActionEvent event) {
		setAvailableRoom(null);
		setAvailableRoomList(null);
	}

	public void onItemFilterChanged(ValueChangeEvent event) {
		setAvailableRoom(null);
		setAvailableRoomList(null);
	}

	private void resetFilterParams(ProjectReservationRoom reservationRoom) {
		ProjectReservation reservation = reservationRoom.getProjectReservation();
		setFilterParams(null);
		getFilterParams().setHotel(reservation.getHotel());
		getFilterParams().setItem(reservationRoom.getItem());
		getFilterParams().setViewerStartDate(reservation.getStartDate());
		getFilterParams().setAssetAvailability((int)CommonUtil.getDaysBetweenDates(reservation.getStartDate(), reservation.getEndDate()));
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
		if (getFilterParams().getAssetAvailability() != null && getFilterParams().getAssetAvailability() > 0 && getFilterParams().getViewerStartDate() != null) {
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
							"LEFT JOIN asset_feature as AssetFeature on AssetFeature.asset = Room.asset " +
							whereClause +
							" GROUP BY Room.asset" +
							" ORDER BY Room.asset";
		Query sqlQuery = session.createSQLQuery(sqlSelect);
		if (!StringUtils.isEmpty(getFilterParams().getName())) {
			sqlQuery.setString("name", getFilterParams().getName() + "%");
		}
		if (getFilterParams().getAssetAvailability() != null && getFilterParams().getAssetAvailability() > 0 && getFilterParams().getViewerStartDate() != null) {
			Calendar calendar = new GregorianCalendar();
			calendar.setTime(getFilterParams().getViewerStartDate());
			sqlQuery.setDate("start", calendar.getTime());
			calendar.add(Calendar.DATE, getFilterParams().getAssetAvailability() - 1);
			sqlQuery.setDate("end", calendar.getTime());
		}
		return sqlQuery.list();
	}


	public class FilterParams {
		private Hotel hotel;
		private Item item;
		private String name;
		private Date viewerStartDate;
		private Integer assetAvailability;
		private Integer[] featureFilter;

		public FilterParams() {
			viewerStartDate = new Date();
			assetAvailability = 0;
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
			return DateUtils.addMonths(viewerStartDate, 1);
		}
		public int getViewerDays() {
			return (int)CommonUtil.getDaysBetweenDates(getFilterParams().getViewerStartDate(), getFilterParams().getViewerEndDate()) + 1;
		}

		public Integer getAssetAvailability() {
			return assetAvailability;
		}
		public void setAssetAvailability(Integer assetAvailability) {
			this.assetAvailability = assetAvailability;
		}

		public Integer[] getFeatureFilter() {
			return featureFilter;
		}
		public void setFeatureFilter(Integer[] featureFilter) {
			this.featureFilter = featureFilter;
		}

	}

}
