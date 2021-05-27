package com.esferalia.aon.ui.pms.controller;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;

import com.code.aon.AonVersion;
import com.code.aon.asset.Asset;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import net.aonsolutions.core.dbutils.AonSQLException;
import net.aonsolutions.core.dbutils.DatabaseUtil;
import com.code.aon.product.Item;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.pms.Hotel;
import com.esferalia.aon.pms.ProjectReservation;
import com.esferalia.aon.pms.ProjectReservationRoom;
import com.esferalia.aon.pms.Room;
import com.esferalia.aon.pms.enumeration.RoomStatus;
import com.esferalia.aon.pms.sql.ISQLConstants;
import com.esferalia.aon.pms.sql.SQLUtils;

public class RoomAvailabilityController implements Serializable, ISQLConstants {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
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

	public List<Room> getAvailableRoomList() throws AonSQLException {
		if (availableRoomList == null) {
			availableRoomList = obtainAvailableRoomList();
		}
		return availableRoomList;
	}

	public void setAvailableRoomList(List<Room> availableRoomList) {
		this.availableRoomList = availableRoomList;
	}

	public int getAvailableRoomCount() throws AonSQLException {
		return getAvailableRoomList().size();
	}

	public void onInitializeRoomList(ProjectReservationRoom reservationRoom, Date startDate, Date endDate) throws ManagerBeanException {
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

	private void resetFilterParams(ProjectReservationRoom reservationRoom, Date startDate, Date endDate) throws ManagerBeanException {
		ProjectReservation reservation = reservationRoom.getProjectReservation();
		setFilterParams(null);
		getFilterParams().setHotel(reservation.getHotel());
		getFilterParams().setItem(obtainAvailableRoomItem(reservationRoom));
		getFilterParams().setViewerStartDate(startDate);
		getFilterParams().setViewerEndDate(endDate);
	}

	private Item obtainAvailableRoomItem(ProjectReservationRoom reservationRoom) throws ManagerBeanException {
		IManagerBean roomBean = BeanManager.getManagerBean(Room.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(roomBean.getFieldName(IEntityAlias.ROOM_HOTEL_ID), reservationRoom.getProjectReservation().getHotel().getId());
		criteria.addEqualExpression(roomBean.getFieldName(IEntityAlias.ROOM_ITEM_ID), reservationRoom.getItem().getId());
		criteria.addEqualExpression(roomBean.getFieldName(IEntityAlias.ROOM_ACTIVE), Boolean.TRUE);
		if (roomBean.getCount(criteria) > 0) {
			return reservationRoom.getItem();
		}
		return null;
	}

	private List<Room> obtainAvailableRoomList() throws AonSQLException {
		List<Room> roomList = new LinkedList<Room>();
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			connection = DatabaseUtil.getConnection(AonUtil.getDomainName());
			stmt = connection.prepareStatement(getAvailableRoomListSQL());
			int hostVar = 0;
			if (getFilterParams().getViewerStartDate() != null && getFilterParams().getViewerEndDate() != null) {
				SQLUtils.setDate(stmt, ++hostVar, getFilterParams().getViewerStartDate());
				SQLUtils.setDate(stmt, ++hostVar, DateUtils.addDays(getFilterParams().getViewerEndDate(), -1));
			}
			if (!StringUtils.isEmpty(getFilterParams().getName())) {
				SQLUtils.setString(stmt, ++hostVar, getFilterParams().getName().replace("*", "%"));
			}
			rs = stmt.executeQuery();
			while (rs.next()) {
				Room room = new Room();
				room.setAsset(new Asset());
				room.getAsset().setId(rs.getInt(ROOM));
				room.getAsset().setName(rs.getString(ROOM_NUMBER));
				room.setStatus(RoomStatus.values()[rs.getInt(ROOM_STATUS)]);
				room.setLastCleaningDate(rs.getDate(ROOM_LAST_CLEANING_DATE));
				roomList.add(room);
			}
			return roomList;
		} catch (Throwable e) {
			try {
				connection.rollback();
			} catch (SQLException ex) {
			}
			throw new AonSQLException(e.getMessage());
		} finally {
			SQLUtils.closeQuietly(rs);
			SQLUtils.closeQuietly(stmt);
			SQLUtils.closeQuietly(connection);
		}
	}

	private String getAvailableRoomListSQL() {
		StringBuffer stmt = new StringBuffer();
		stmt.append("SELECT R.asset AS " + ROOM + ", R.status AS " + ROOM_STATUS + ", R.last_cleaning_date AS " + ROOM_LAST_CLEANING_DATE + ", A.name AS " + ROOM_NUMBER);
		stmt.append(" FROM room AS R");
		stmt.append(" LEFT JOIN asset AS A ON A.id = R.asset");
		stmt.append(" LEFT JOIN asset_feature AS AF ON AF.asset = A.id");
		stmt.append(" WHERE" + DomainManager.getSQLWhereClause("R.domain"));
		stmt.append(" AND R.active = 1");
		if (getFilterParams().getHotel() != null && getFilterParams().getHotel().getId() != null) {
			stmt.append(" AND R.hotel = " + getFilterParams().getHotel().getId());
		} else {
			stmt.append(" AND R.hotel IS NOT NULL");
		}
		if (getFilterParams().getItem() != null && getFilterParams().getItem().getId() != null) {
			stmt.append(" AND R.item = " + getFilterParams().getItem().getId());
		}
		if (getFilterParams().getViewerStartDate() != null && getFilterParams().getViewerEndDate() != null) {
			stmt.append(" AND R.asset NOT IN (SELECT asset FROM asset_activity AS AA WHERE AA.date BETWEEN ? AND ?)");
		}
		if (!StringUtils.isEmpty(getFilterParams().getName())) {
			stmt.append(" AND A.name LIKE ?");
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
				stmt.append(" AND AF.feature IN (" + featureClause + ")");
			}
		}
		stmt.append(" GROUP BY R.asset");
		stmt.append(" ORDER BY A.name");

		return stmt.toString();
	}


	public static class FilterParams implements Serializable {
		
		private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
		
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
