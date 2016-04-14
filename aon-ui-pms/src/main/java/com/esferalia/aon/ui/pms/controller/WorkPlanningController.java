package com.esferalia.aon.ui.pms.controller;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;

import com.code.aon.AonVersion;
import com.code.aon.asset.enumeration.ActivityStatus;
import com.code.aon.common.BeanManager;
import com.code.aon.common.ICollectionProvider;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.dbutils.AonSQLException;
import com.code.aon.dbutils.DatabaseUtil;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.common.serialize.SerializableListDataModel;
import com.code.aon.ui.form.DataScrollerState;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.pms.Hotel;
import com.esferalia.aon.pms.Room;
import com.esferalia.aon.pms.enumeration.BookingStayType;
import com.esferalia.aon.pms.enumeration.RoomWorkAction;
import com.esferalia.aon.pms.sql.ISQLConstants;
import com.esferalia.aon.pms.sql.SQLUtils;

public class WorkPlanningController extends DataScrollerState implements ICollectionProvider, ISQLConstants {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private Hotel hotel;
	private Date date;
	private Room[] rooms;
	private String roomFilter;

	private List<RoomPlanning> roomPlanningList;

	public Hotel getHotel() {
		return hotel;
	}
	public void setHotel(Hotel hotel) {
		this.hotel = hotel;
	}

	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	
	public Room[] getRooms() {
		return rooms;
	}
	public void setRooms(Room[] rooms) {
		this.rooms = rooms;
	}

	public String getRoomFilter() {
		return roomFilter;
	}
	public void setRoomFilter(String roomFilter) {
		this.roomFilter = roomFilter;
	}

	public List<RoomPlanning> getRoomPlanningList() {
		return roomPlanningList;
	}
	public void setRoomPlanningList(List<RoomPlanning> roomPlanningList) {
		this.roomPlanningList = roomPlanningList;
	}

	public void onInit(ActionEvent event) throws ManagerBeanException{
		setHotel(null);
		setDate(new Date());
		setRooms(null);
		setRoomFilter("*");
	}

	public void onHotelChanged(ValueChangeEvent event) {
		if (event.getNewValue() != null && !event.getNewValue().toString().equals("")) {
			setHotel((Hotel)event.getNewValue());
		}
	}

	public void onRoomFilterChanged(ValueChangeEvent event) {
		if (event.getNewValue() != null && !event.getNewValue().toString().equals("")) {
			setRoomFilter(event.getNewValue().toString());
		}
	}

	public List<SelectItem> getHotelRooms() throws ManagerBeanException {
		List<SelectItem> hotelRooms = new LinkedList<SelectItem>();
		if (getHotel() != null && getHotel().getId() != null) {
			IManagerBean roomBean = BeanManager.getManagerBean(Room.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(roomBean.getFieldName(IEntityAlias.ROOM_HOTEL_ID), getHotel().getId());
			criteria.addEqualExpression(roomBean.getFieldName(IEntityAlias.ROOM_ACTIVE), Boolean.TRUE);
			if (StringUtils.isNotBlank(getRoomFilter()) && !getRoomFilter().equals("*")) {
				String filter = getRoomFilter().replace("*", "%");
				criteria.addExpression(ExpressionUtilities.getLikeExpression(roomBean.getFieldName(IEntityAlias.ROOM_ASSET_NAME), filter));
			}
			criteria.addOrder(roomBean.getFieldName(IEntityAlias.ROOM_ASSET_NAME));
			for (ITransferObject ito : roomBean.getList(criteria)) {
				Room room = (Room)ito;
				hotelRooms.add(new SelectItem(room, room.getAsset().getName()));
			}
		}
		return hotelRooms;
	}

	public void onSearch(ActionEvent event) {
		try {
			buildRoomPlanningList();
		} catch (AonSQLException e) {
			throw new AbortProcessingException(e.getMessage(), e);
		}
		setModel(new SerializableListDataModel(getRoomPlanningList()));
	}
	
	private void buildRoomPlanningList() throws AonSQLException {
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		Connection connection = null;
		PreparedStatement planningStmt = null;
		ResultSet planningRs = null;
		try {
			connection = DatabaseUtil.getConnection(AonUtil.getDomainName());
			initializeRoomPlanningList(connection);

			planningStmt = connection.prepareStatement(getRoomPlanningSQL());
			SQLUtils.setInt(planningStmt, 1, getHotel().getId());
			SQLUtils.setDate(planningStmt, 2, getDate());
			SQLUtils.setInt(planningStmt, 3, getHotel().getId());
			SQLUtils.setDate(planningStmt, 4, getDate());
			planningRs = planningStmt.executeQuery();
			while (planningRs.next()) {
				int type = planningRs.getInt(STAY_TYPE);
				String roomNumber = planningRs.getString(ROOM_NUMBER);
				Date startDate = planningRs.getDate(START_DATE);
				int guests = planningRs.getObject(GUESTS) != null ? planningRs.getInt(GUESTS) : 0;
				Date endDate = planningRs.getDate(END_DATE);

				if (date != null) {
					RoomPlanning roomPlanning = new RoomPlanning();
					roomPlanning.setRoomNumber(roomNumber);
					int index = getRoomPlanningList().indexOf(roomPlanning);
					if (index >= 0) {
						roomPlanning = getRoomPlanningList().get(index);
						if (type == BookingStayType.CHECKIN.ordinal()) {
							roomPlanning.setPax(guests);
							roomPlanning.setAction(RoomWorkAction.CHECK_IN);
						} else if (type == BookingStayType.CHECKOUT.ordinal()) {
							if (roomPlanning.getAction() == RoomWorkAction.CHECK_IN) {
								roomPlanning.setAction(RoomWorkAction.CHECK_OUT_IN);
							} else {
								roomPlanning.setPax(guests);
								roomPlanning.setAction(RoomWorkAction.CHECK_OUT);
							}
						} else if (type == BookingStayType.STAY.ordinal()) {
							roomPlanning.setPax(guests);
							long days = CommonUtil.getDaysBetweenDates(startDate, getDate());
							if (days > 0 && days % getHotel().getSheetChanging() == 0) {
								roomPlanning.setAction(RoomWorkAction.SHEET_CHANGE);
							} else {
								roomPlanning.setAction(RoomWorkAction.CLEANING);
							}
							if (CommonUtil.getDaysBetweenDates(getDate(), endDate, false)==1) {
								roomPlanning.setRemarks("Fin estancia: "+formatter.format(endDate));
							}
						} else {
							roomPlanning.setPax(guests);
							roomPlanning.setAction(RoomWorkAction.BLOCKED);
						}
					}
				}
			}
		} catch (ManagerBeanException e) {
			try {
				connection.rollback();
			} catch (SQLException ex) {
			}
			throw new AonSQLException(e.getMessage());
		} catch (Throwable e) {
			try {
				connection.rollback();
			} catch (SQLException ex) {
			}
			throw new AonSQLException(e.getMessage());
		} finally {
			SQLUtils.closeQuietly(planningRs);
			SQLUtils.closeQuietly(planningStmt);
			SQLUtils.closeQuietly(connection);
		}
	}
	
	private void initializeRoomPlanningList(Connection connection) throws ManagerBeanException {
		setRoomPlanningList(new LinkedList<RoomPlanning>());

		if (ArrayUtils.isNotEmpty(getRooms())) {
			for (Room room : getRooms()) {
				RoomPlanning roomPlanning = new RoomPlanning();
				roomPlanning.setRoomNumber(room.getAsset().getName());
				getRoomPlanningList().add(roomPlanning);
			}
		} else {
			for (SelectItem item : getHotelRooms()) {
				RoomPlanning roomPlanning = new RoomPlanning();
				roomPlanning.setRoomNumber(item.getLabel());
				getRoomPlanningList().add(roomPlanning);
			}
		}
	}

	private String getRoomPlanningSQL() throws ManagerBeanException {
		StringBuffer stmt = new StringBuffer();
		stmt.append("SELECT B.stay_type AS " + STAY_TYPE + ", A.name AS " + ROOM_NUMBER + ", PR.start_date AS " + START_DATE + ", PR.end_date AS " + END_DATE + ", B.guests AS " + GUESTS);
		stmt.append(" FROM booking AS B, project_reservation_room AS PRR, project_reservation AS PR, project_reservation_room_detail AS PRRD");
		stmt.append(", asset_activity AS AA, asset AS A");
		stmt.append(" WHERE" + DomainManager.getSQLWhereClause("B.domain"));
		stmt.append(" AND B.hotel = ?");
		stmt.append(" AND B.stay_date = ?");
		stmt.append(" AND B.hotel = PR.hotel");
		stmt.append(" AND B.project_reservation_room = PRR.id");
		stmt.append(" AND PRR.project_reservation = PR.project");
		stmt.append(" AND PRR.id = PRRD.project_reservation_room");
		stmt.append(" AND PRRD.asset_activity = AA.id");
		stmt.append(" AND AA.asset = A.id");
		stmt.append(" AND (B.stay_type != " + BookingStayType.CHECKOUT.ordinal() +" AND AA.date = B.stay_date");
		stmt.append("    OR B.stay_type = " + BookingStayType.CHECKOUT.ordinal() + " AND AA.date = DATE_SUB(B.stay_date,INTERVAL 1 DAY))");
		if (ArrayUtils.isNotEmpty(getRooms())) {
			stmt.append(" AND A.id IN (" + getRoomIds() + ")");
		} else if (StringUtils.isNotBlank(getRoomFilter()) && !getRoomFilter().equals("*")) {
			stmt.append(" AND A.name LIKE '" + getRoomFilter().replace("*", "%") + "'");
		}
		stmt.append(" UNION ");
		stmt.append("SELECT 3 AS " + STAY_TYPE + ", A.name AS " + ROOM_NUMBER + ", AA.date AS " + START_DATE + ", null AS " + END_DATE + ", 0 AS " + GUESTS);
		stmt.append(" FROM room AS R, asset AS A, asset_activity AS AA");
		stmt.append(" WHERE" + DomainManager.getSQLWhereClause("R.domain"));
		stmt.append(" AND R.hotel = ?");
		stmt.append(" AND R.asset = A.id");
		stmt.append(" AND A.id = AA.asset");
		stmt.append(" AND AA.date = ?");
		stmt.append(" AND AA.status != " + ActivityStatus.BUSY.ordinal());
		if (ArrayUtils.isNotEmpty(getRooms())) {
			stmt.append(" AND A.id IN (" + getRoomIds() + ")");
		} else if (StringUtils.isNotBlank(getRoomFilter()) && !getRoomFilter().equals("*")) {
			stmt.append(" AND A.name LIKE '" + getRoomFilter().replace("*", "%") + "'");
		}
		stmt.append(" ORDER BY " + STAY_TYPE);

		return stmt.toString();
	}

	private String getRoomIds() throws ManagerBeanException {
		String roomIds = "";
		if (ArrayUtils.isNotEmpty(getRooms())) {
			for (Room room : getRooms()) {
				roomIds += room.getId() + ",";
			}
		}
		return StringUtils.removeEnd(roomIds, ",");
	}

	@SuppressWarnings("rawtypes")
	public Collection getCollection() {
		return getRoomPlanningList();
	}
	@SuppressWarnings("rawtypes")
	public Collection getCollection(boolean forceRefresh) {
		return getCollection();
	}
	
	/***************** ROOM PLANNING *********************************/
	
	public static class RoomPlanning implements Serializable {
		
		private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
		
		private String roomNumber;
		private Integer pax;
		private RoomWorkAction action;
		private String remarks;

		public RoomPlanning() {
			pax = 0;
			action = RoomWorkAction.FREE;
		}

		public String getRoomNumber() {
			return roomNumber;
		}
		public void setRoomNumber(String roomNumber) {
			this.roomNumber = roomNumber;
		}

		public Integer getPax() {
			return pax;
		}
		public void setPax(Integer pax) {
			this.pax = pax;
		}

		public RoomWorkAction getAction() throws ManagerBeanException{
			return action;
		}
		public void setAction(RoomWorkAction action) {
			this.action = action;
		}

		public String getRemarks() {
			return remarks;
		}

		public void setRemarks(String remarks) {
			this.remarks = remarks;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null) return false;
			final RoomPlanning o = (RoomPlanning)obj;
			return o.getRoomNumber().equals(getRoomNumber());
		}

	}

}
