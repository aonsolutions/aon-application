package com.esferalia.aon.ui.pms.controller;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.ICollectionProvider;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.customer.Customer;
import com.code.aon.dbutils.AonSQLException;
import com.code.aon.dbutils.DatabaseUtil;
import com.code.aon.product.Item;
import com.code.aon.registry.ITariffable;
import com.code.aon.ui.common.serialize.SerializableListDataModel;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.DataScrollerState;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.pms.Hotel;
import com.esferalia.aon.pms.enumeration.BookingHolder;
import com.esferalia.aon.pms.enumeration.ReservationCheckStatus;
import com.esferalia.aon.pms.enumeration.ReservationStatus;
import com.esferalia.aon.pms.enumeration.RoomStatus;
import com.esferalia.aon.pms.reservation.ReservationUtils;
import com.esferalia.aon.pms.sql.ISQLConstants;
import com.esferalia.aon.pms.sql.SQLUtils;

public class ReservationInOutController extends DataScrollerState implements ICollectionProvider, ISQLConstants, IPmsConstants {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private ReservationUtils reservationUtils;
	private Hotel hotel;
	private boolean checkin;
	private ReservationCheckStatus[] checkStatuses;
	private Date fromDate;
	private Date toDate;
	private Integer sortMode;
	private Item touristTaxItem;

	private List<ReservationIO> reservationIOList;

	public ReservationUtils getReservationUtils() {
		if (reservationUtils == null) {
			reservationUtils = new ReservationUtils(DomainManager.getCurrentDomain());
		}
		return reservationUtils;
	}

	public Hotel getHotel() {
		return hotel;
	}
	public void setHotel(Hotel hotel) {
		this.hotel = hotel;
	}

	public boolean isCheckin() {
		return checkin;
	}
	public void setCheckin(boolean checkin) {
		this.checkin = checkin;
	}

	public ReservationCheckStatus[] getCheckStatuses() {
		return checkStatuses;
	}
	public void setCheckStatuses(ReservationCheckStatus[] checkStatuses) {
		this.checkStatuses = checkStatuses;
	}

	public Date getFromDate() {
		return fromDate;
	}
	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}
	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public Integer getSortMode() {
		return sortMode;
	}
	public void setSortMode(Integer sortMode) {
		this.sortMode = sortMode;
	}

	public Item getTouristTaxItem() {
		return touristTaxItem;
	}
	public void setTouristTaxItem(Item touristTaxItem) {
		this.touristTaxItem = touristTaxItem;
	}

	public List<ReservationIO> getReservationIOList() {
		return reservationIOList;
	}
	public void setReservationIOList(List<ReservationIO> reservationIOList) {
		this.reservationIOList = reservationIOList;
	}

	public void onInit(ActionEvent event) {
		setCheckin(true);
		setCheckStatuses(new ReservationCheckStatus[]{ReservationCheckStatus.NO_CHECK});
		setFromDate(new Date());
		setToDate(new Date());
		setSortMode(0);
	}

	public void onCheckChanged(ValueChangeEvent event) {
		if (event.getNewValue() != null && !event.getNewValue().toString().equals("")) {
			if ((Boolean)event.getNewValue()) {
				setCheckStatuses(new ReservationCheckStatus[]{ReservationCheckStatus.NO_CHECK});
			} else {
				setCheckStatuses(new ReservationCheckStatus[]{ReservationCheckStatus.NO_CHECK, ReservationCheckStatus.CHECK_IN});
			}
		}
	}

	public void onSearch(ActionEvent event) {
		try {
			setTouristTaxItem((getHotel()!= null && getHotel().isTouristTax()) ? getReservationUtils().obtainTouristTaxItem() : null);
		} catch (ManagerBeanException e) {
			setTouristTaxItem(null);
		}

		try {
			buildReservationIOList();
		} catch (AonSQLException e) {
			throw new AbortProcessingException(e.getMessage(), e);
		}
		setModel(new SerializableListDataModel(getReservationIOList()));
	}
	
	public void buildReservationIOList() throws AonSQLException {
		Connection connection = null;
		PreparedStatement reservationIOStmt = null;
		ResultSet reservationIORs = null;
		try {
			setReservationIOList(new LinkedList<ReservationIO>());

			connection = DatabaseUtil.getConnection(AonUtil.getDomainName());
			reservationIOStmt = connection.prepareStatement(getReservationIOSQL());
			SQLUtils.setDate(reservationIOStmt, 1, getFromDate());
			SQLUtils.setDate(reservationIOStmt, 2, getToDate());
			SQLUtils.setInt(reservationIOStmt, 3, isCheckin() ? 0 : 1);
			reservationIORs = reservationIOStmt.executeQuery();
			while (reservationIORs.next()) {
				ReservationIO reservationIO = new ReservationIO(this);
				reservationIO.setReservation(reservationIORs.getInt(RESERVATION));
				reservationIO.setCode(reservationIORs.getString(CODE));
				reservationIO.setCheckInDate(reservationIORs.getDate(START_DATE));
				reservationIO.setCheckOutDate(reservationIORs.getDate(END_DATE));
				reservationIO.setCheckStatus(ReservationCheckStatus.values()[reservationIORs.getInt(CHECK_STATUS)]);
				reservationIO.setStatus(ReservationStatus.values()[reservationIORs.getInt(STATUS)]);
				reservationIO.setHolder(BookingHolder.values()[reservationIORs.getInt(HOLDER)]);
				reservationIO.setGuest(reservationIORs.getString(GUEST));
				reservationIO.setEmail(reservationIORs.getString(EMAIL));
				reservationIO.setPhone(reservationIORs.getString(PHONE));
				reservationIO.setAgency(reservationIORs.getString(AGENCY));
				reservationIO.setTotal(reservationIORs.getObject(TOTAL) != null ? reservationIORs.getDouble(TOTAL) : 0);
				reservationIO.setHotelReservation(reservationIORs.getInt(RESERVATION_HOTEL));
				reservationIO.setHotelReservationName(reservationIORs.getString(RESERVATION_HOTEL_NAME));
				reservationIO.setHotel(reservationIORs.getInt(HOTEL));
				reservationIO.setHotelName(reservationIORs.getString(HOTEL_NAME));
				reservationIO.setHotelCustomer(reservationIORs.getInt(HOTEL_CUSTOMER));
				reservationIO.setHotelTouristTax(reservationIORs.getBoolean(HOTEL_TOURIST_TAX));
				reservationIO.setComments(reservationIORs.getString(COMMENTS));
				reservationIO.setTouristTaxFree(reservationIORs.getObject(TOURIST_TAX_FREE) != null ? reservationIORs.getInt(TOURIST_TAX_FREE) : null);
				reservationIO.setStayDate(reservationIORs.getDate(STAY_DATE));
				reservationIO.setAdults(reservationIORs.getObject(ADULTS) != null ? reservationIORs.getInt(ADULTS) : 0);
				reservationIO.setChildren(reservationIORs.getObject(CHILDREN) != null ? reservationIORs.getInt(CHILDREN) : 0);
				reservationIO.setRoomCode(reservationIORs.getString(ROOM_CODE));
				reservationIO.setRoomType(reservationIORs.getString(ROOM_TYPE));
				reservationIO.setRoomNumber(reservationIORs.getString(ROOM_NUMBER));
				reservationIO.setRoomStatus(reservationIORs.getObject(ROOM_STATUS) != null ? RoomStatus.values()[reservationIORs.getInt(ROOM_STATUS)] : null);
				reservationIO.setRoomCount(reservationIORs.getInt(ROOMS));
				reservationIO.setMealPlan(reservationIORs.getString(MEAL_PLAN));
				reservationIO.setTouristTaxAmount(obtainTouristTaxAmount(reservationIO));

				getReservationIOList().add(reservationIO);
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
			SQLUtils.closeQuietly(reservationIORs);
			SQLUtils.closeQuietly(reservationIOStmt);
			SQLUtils.closeQuietly(connection);
		}
	}

	private String getReservationIOSQL() throws ManagerBeanException {
		StringBuffer stmt = new StringBuffer();
		stmt.append("SELECT PR.project AS " + RESERVATION + ", PR.code AS " + CODE + ", PR.start_date AS " + START_DATE + ", PR.end_date AS " + END_DATE);
		stmt.append(", PR.check_status AS " + CHECK_STATUS + ", PR.status AS " + STATUS + ", PR.booking_holder AS " + HOLDER + ", PR.total AS " + TOTAL);
		stmt.append(", PR.hotel_reservation AS " + RESERVATION_HOTEL + ", PR.hotel AS " + HOTEL + ", PR.comments AS " + COMMENTS);
		stmt.append(", PR.tourist_tax_free AS " + TOURIST_TAX_FREE + ", B.stay_date AS " + STAY_DATE);
		stmt.append(", PRR.adults AS " + ADULTS + ", PRR.children AS " + CHILDREN);
		stmt.append(", IF(R.alias IS NOT NULL AND R.alias != '', R.alias, R.name) AS " + AGENCY + ", P.code AS " + ROOM_CODE + ", P.name AS " + ROOM_TYPE);
		stmt.append(", (SELECT W.description FROM workplace AS W, hotel AS H");
		stmt.append("     WHERE H.id = PR.hotel_reservation AND W.id = H.workplace LIMIT 1) AS " + RESERVATION_HOTEL_NAME);
		stmt.append(", (SELECT W.description FROM workplace AS W, hotel AS H");
		stmt.append("     WHERE H.id = PR.hotel AND W.id = H.workplace LIMIT 1) AS " + HOTEL_NAME);
		stmt.append(", (SELECT W.customer FROM workplace AS W, hotel AS H");
		stmt.append("     WHERE H.id = PR.hotel AND W.id = H.workplace LIMIT 1) AS " + HOTEL_CUSTOMER);
		stmt.append(", (SELECT H.tourist_tax FROM hotel AS H");
		stmt.append("     WHERE H.id = PR.hotel LIMIT 1) AS " + HOTEL_TOURIST_TAX);
		stmt.append(", (SELECT CONCAT(PRG.name, ' ', PRG.surname) FROM project_reservation_guest AS PRG");
		stmt.append("     WHERE PRG.project_reservation = PR.project AND guest_index = 1 LIMIT 1) AS " + GUEST);
		stmt.append(", (SELECT PRG.email FROM project_reservation_guest AS PRG");
		stmt.append("     WHERE PRG.project_reservation = PR.project AND guest_index = 1 LIMIT 1) AS " + EMAIL);
		stmt.append(", (SELECT PRG.phone FROM project_reservation_guest AS PRG");
		stmt.append("     WHERE PRG.project_reservation = PR.project AND guest_index = 1 LIMIT 1) AS " + PHONE);
		stmt.append(", (SELECT A.name FROM project_reservation_room_detail AS PRRD, asset_activity AS AA, asset AS A");
		stmt.append("     WHERE PRRD.project_reservation_room = B.project_reservation_room AND PRRD.asset_activity = AA.id");
		stmt.append("     AND AA.date = IF (B.stay_type = 0, B.stay_date, DATE_SUB(B.stay_date, INTERVAL 1 DAY))");
		stmt.append("     AND AA.asset = A.id LIMIT 1) AS " + ROOM_NUMBER);
		stmt.append(", (SELECT IF(B.stay_date = CURRENT_DATE AND B.stay_type = 0, IF(DATE(R.last_cleaning_date) = CURRENT_DATE, R.status, 1), NULL) ");
		stmt.append("     FROM project_reservation_room_detail AS PRRD, asset_activity AS AA, room AS R");
		stmt.append("     WHERE PRRD.project_reservation_room = B.project_reservation_room AND PRRD.asset_activity = AA.id");
		stmt.append("     AND AA.date = IF (B.stay_type = 0, B.stay_date, DATE_SUB(B.stay_date, INTERVAL 1 DAY))");
		stmt.append("     AND AA.asset = R.asset LIMIT 1) AS " + ROOM_STATUS);
		stmt.append(", (SELECT COUNT(*) FROM room AS R");
		stmt.append("     WHERE R.hotel = PR.hotel");
		stmt.append("     AND R.item = I.id");
		stmt.append("     AND R.active = 1) AS " + ROOMS);
		stmt.append(", (SELECT PRS.meal_plan FROM project_reservation_service AS PRS, item AS I2, product AS P2");
		stmt.append("     WHERE PRS.project_reservation = PR.project AND PRS.project_reservation_room = B.project_reservation_room");
		stmt.append("     AND PRS.item = I2.id AND I2.detail IS NOT NULL");
		stmt.append("     AND I2.product = P2.id ORDER BY P2.composition DESC LIMIT 1) AS " + MEAL_PLAN);
		stmt.append(" FROM booking AS B");
		stmt.append(" LEFT JOIN project_reservation_room AS PRR ON B.project_reservation_room = PRR.id");
		stmt.append(" LEFT JOIN project_reservation AS PR ON PRR.project_reservation = PR.project");
		stmt.append(" LEFT JOIN registry AS R ON PR.agency = R.id");
		stmt.append(" LEFT JOIN item AS I ON PRR.item = I.id");
		stmt.append(" LEFT JOIN product AS P ON I.product = P.id");
		stmt.append(" WHERE" + DomainManager.getSQLWhereClause("B.domain"));
		if (isCheckin()) {
			stmt.append(" AND (PR.hotel IN (" + getHotelIds() + ") OR PR.hotel_reservation IN (" + getHotelIds() + "))");
		} else {
			stmt.append(" AND B.hotel IN (" + getHotelIds() + ")");
		}
		stmt.append(" AND B.stay_date BETWEEN ? AND ?");
		stmt.append(" AND B.stay_type = ?");
		if (ArrayUtils.getLength(getCheckStatuses()) > 0) {
			stmt.append(" AND PR.check_status IN (" + getCheckStatusIds() + ")");
		}
		stmt.append(" ORDER BY " + getOrderByClause());

		return stmt.toString();
	}

	private String getHotelIds() throws ManagerBeanException {
		String hotelIds = "";
		if (getHotel() != null) {
			hotelIds = getHotel().getId().toString();
		} else {
			PmsCollectionsController collectionsController = (PmsCollectionsController)AonUtil.getRegisteredBean(IPmsConstants.COLLECTIONS_CONTROLLER_NAME);
			hotelIds = StringUtils.join(collectionsController.getCurrentUserHotelIds(), ",");
		}
		return hotelIds;
	}

	private String getCheckStatusIds() {
		Integer[] statusIds = ArrayUtils.EMPTY_INTEGER_OBJECT_ARRAY;
		for (ReservationCheckStatus status : getCheckStatuses()) {
			statusIds = (Integer[])ArrayUtils.add(statusIds, status.ordinal());
		}
		return ArrayUtils.getLength(statusIds) > 0 ? StringUtils.join(statusIds, ",") : "";
	}

	private String getOrderByClause() {
		switch (getSortMode()) {
			case 1: return GUEST;
			case 2: return AGENCY + "," + GUEST;
			case 3: return AGENCY;
			case 4: return "IFNULL(" + ROOM_NUMBER + ", 'z')";
		}
		return RESERVATION;
	}

	private double obtainTouristTaxAmount(ReservationIO reservationIO) throws ManagerBeanException {
		if (isCheckin() && getTouristTaxItem() != null && reservationIO.isHotelTouristTax() && reservationIO.getTouristTaxFree() == null) {
			ITariffable iTariffable = (ITariffable)BeanManager.getManagerBean(Customer.class).get(reservationIO.getHotelCustomer());
			Date startDate = reservationIO.getCheckInDate();
			Date endDate = reservationIO.getCheckOutDate();
			return getReservationUtils().getReservationTouristTaxAmount(getTouristTaxItem(), iTariffable, startDate, endDate, reservationIO.getAdults(), false);
		}
		return 0;
	}

	public void onSelect(ActionEvent event) {
		try {
			if (getModel().isRowAvailable()) {
				ReservationIO reservationIO = (ReservationIO)getModel().getRowData();
				BasicController reservationController = (BasicController)AonUtil.getRegisteredBean(RESERVATION_CONTROLLER_NAME);
				reservationController.onLoad(event, reservationIO.getReservation(), RESERVATION_IO_LIST_NAME, RESERVATION_IO_CONTROLLER_NAME + ".onSearch");
			}
		} catch (ManagerBeanException e) {
			String msg = "No se ha podido seleccionar la Reserva.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}


	@SuppressWarnings("rawtypes")
	public Collection getCollection() {
		return getReservationIOList();
	}
	@SuppressWarnings("rawtypes")
	public Collection getCollection(boolean forceRefresh) {
		return getCollection();
	}

	/***************** RESERVATION IO *********************************/

	public static class ReservationIO implements Serializable {
		
		private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
		
		private ReservationInOutController controller;
		
		private Integer reservation;
		private Integer hotelReservation;
		private String hotelReservationName;
		private Integer hotel;
		private String hotelName;
		private Integer hotelCustomer;
		private Boolean hotelTouristTax;
		private String code;
		private Date checkInDate;
		private Date checkOutDate;
		private ReservationCheckStatus checkStatus;
		private ReservationStatus status;
		private BookingHolder holder;
		private String guest;
		private String email;
		private String phone;
		private String agency;
		private Double total;
		private String comments;
		private Integer touristTaxFree;
		private Date stayDate;
		private Integer adults;
		private Integer children;
		private String roomCode;
		private String roomType;
		private String roomNumber;
		private RoomStatus roomStatus;
		private int roomCount;
		private String mealPlan;
		private Double touristTaxAmount;
		
		public ReservationIO(ReservationInOutController controller) {
			this.controller = controller;
		}
		
		public Integer getReservation() {
			return reservation;
		}
		public void setReservation(Integer reservation) {
			this.reservation = reservation;
		}

		public Integer getHotelReservation() {
			return hotelReservation;
		}
		public void setHotelReservation(Integer hotelReservation) {
			this.hotelReservation = hotelReservation;
		}

		public String getHotelReservationName() {
			return hotelReservationName;
		}
		public void setHotelReservationName(String hotelReservationName) {
			this.hotelReservationName = hotelReservationName;
		}

		public Integer getHotel() {
			return hotel;
		}
		public void setHotel(Integer hotel) {
			this.hotel = hotel;
		}

		public String getHotelName() {
			return hotelName;
		}
		public void setHotelName(String hotelName) {
			this.hotelName = hotelName;
		}

		public Integer getHotelCustomer() {
			return hotelCustomer;
		}
		public void setHotelCustomer(Integer hotelCustomer) {
			this.hotelCustomer = hotelCustomer;
		}

		public Boolean isHotelTouristTax() {
			return hotelTouristTax;
		}
		public void setHotelTouristTax(Boolean hotelTouristTax) {
			this.hotelTouristTax = hotelTouristTax;
		}

		public String getCode() {
			return code;
		}
		public void setCode(String code) {
			this.code = code;
		}

		public Date getCheckInDate() {
			return checkInDate;
		}
		public void setCheckInDate(Date checkInDate) {
			this.checkInDate = checkInDate;
		}

		public Date getCheckOutDate() {
			return checkOutDate;
		}
		public void setCheckOutDate(Date checkOutDate) {
			this.checkOutDate = checkOutDate;
		}

		public ReservationCheckStatus getCheckStatus() {
			return checkStatus;
		}
		public void setCheckStatus(ReservationCheckStatus checkStatus) {
			this.checkStatus = checkStatus;
		}

		public ReservationStatus getStatus() {
			return status;
		}
		public void setStatus(ReservationStatus status) {
			this.status = status;
		}

		public BookingHolder getHolder() {
			return holder;
		}
		public void setHolder(BookingHolder holder) {
			this.holder = holder;
		}

		public String getGuest() {
			return guest;
		}
		public void setGuest(String guest) {
			this.guest = guest;
		}

		public String getEmail() {
			return email;
		}
		public void setEmail(String email) {
			this.email = email;
		}

		public String getPhone() {
			return phone;
		}
		public void setPhone(String phone) {
			this.phone = phone;
		}

		public String getAgency() {
			return agency;
		}
		public void setAgency(String agency) {
			this.agency = agency;
		}

		public Double getTotal() {
			return total;
		}
		public void setTotal(Double total) {
			this.total = total;
		}

		public String getComments() {
			return comments;
		}
		public void setComments(String comments) {
			this.comments = comments;
		}

		public Integer getTouristTaxFree() {
			return touristTaxFree;
		}
		public void setTouristTaxFree(Integer touristTaxFree) {
			this.touristTaxFree = touristTaxFree;
		}

		public Date getStayDate() {
			return stayDate;
		}
		public void setStayDate(Date stayDate) {
			this.stayDate = stayDate;
		}

		public Integer getAdults() {
			return adults;
		}
		public void setAdults(Integer adults) {
			this.adults = adults;
		}

		public Integer getChildren() {
			return children;
		}
		public void setChildren(Integer children) {
			this.children = children;
		}

		public String getRoomCode() {
			return roomCode;
		}
		public void setRoomCode(String roomCode) {
			this.roomCode = roomCode;
		}

		public String getRoomType() {
			return roomType;
		}
		public void setRoomType(String roomType) {
			this.roomType = roomType;
		}

		public String getRoomNumber() {
			return roomNumber;
		}
		public void setRoomNumber(String roomNumber) {
			this.roomNumber = roomNumber;
		}

		public RoomStatus getRoomStatus() {
			return roomStatus;
		}
		public void setRoomStatus(RoomStatus roomStatus) {
			this.roomStatus = roomStatus;
		}

		public int getRoomCount() {
			return roomCount;
		}
		public void setRoomCount(int roomCount) {
			this.roomCount = roomCount;
		}

		public String getMealPlan() {
			return mealPlan;
		}
		public void setMealPlan(String mealPlan) {
			this.mealPlan = mealPlan;
		}

		public Double getTouristTaxAmount() {
			return touristTaxAmount;
		}
		public void setTouristTaxAmount(Double touristTaxAmount) {
			this.touristTaxAmount = touristTaxAmount;
		}

		public boolean isCheckIn() {
			return getCheckStatus() == ReservationCheckStatus.CHECK_IN;
		}
		public boolean isCheckOut() {
			return getCheckStatus() == ReservationCheckStatus.CHECK_OUT;
		}
		public boolean isLateCheckIn() {
			return (controller.isCheckin() && getCheckInDate().compareTo(getStayDate()) != 0);
		}
		public boolean isEarlyCheckOut() {
			return (!controller.isCheckin() && getCheckOutDate().compareTo(getStayDate()) != 0);
		}
		public boolean isWrongCheck() {
			return isLateCheckIn() || isEarlyCheckOut();
		}

		public boolean isBlocked() {
			return getStatus() == ReservationStatus.BLOCKED;
		}
		public boolean isInvoiced() {
			return getStatus() == ReservationStatus.INVOICED;
		}

		public boolean isGuestHolder() {
			return getHolder() == BookingHolder.GUEST;
		}
		public boolean isAgencyHolder() {
			return getHolder() == BookingHolder.AGENCY;
		}

		public int getPax() {
			return adults + children;
		}

		public boolean isDiverted() {
			return (controller.isCheckin() && getHotel() != getHotelReservation());
		}
		public boolean isMyDivert() {
			return (isDiverted() && getHotel().equals(controller.getHotel().getId()));
		}

		public boolean isRoomClean() {
			return getRoomStatus() == RoomStatus.CLEAN;
		}
		public boolean isRoomDirty() {
			return getRoomStatus() == RoomStatus.DIRTY;
		}
		public boolean isRoomOccupied() {
			return getRoomStatus() == RoomStatus.DO_NOT_DISTURB;
		}

		public boolean isNoRooms() {
			return getRoomCount() == 0;
		}

	}

}
