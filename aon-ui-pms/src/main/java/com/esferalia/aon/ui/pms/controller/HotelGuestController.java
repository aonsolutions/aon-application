package com.esferalia.aon.ui.pms.controller;

import static com.code.aon.ui.common.ICommonMessages.PMS_ROOM_ABBRV;
import static com.code.aon.ui.common.ICommonMessages.SEE;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import com.code.aon.AonVersion;
import com.code.aon.common.ICollectionProvider;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.dbutils.AonSQLException;
import com.code.aon.dbutils.DatabaseUtil;
import com.code.aon.ui.common.serialize.SerializableListDataModel;
import com.code.aon.ui.form.DataScrollerState;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.pms.Hotel;
import com.esferalia.aon.pms.enumeration.ReservationCheckStatus;
import com.esferalia.aon.pms.enumeration.ReservationStatus;
import com.esferalia.aon.pms.sql.ISQLConstants;
import com.esferalia.aon.pms.sql.SQLUtils;
import com.esferalia.aon.ui.pms.util.HotelGuestUtils;

public class HotelGuestController extends DataScrollerState implements ICollectionProvider, ISQLConstants {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private Hotel hotel;
	private Date date;

	private List<HotelGuest> hotelGuestList;	
	private Map<Integer, String> reservationRoomsMap;	

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

	public List<HotelGuest> getHotelGuestList() {
		return hotelGuestList;
	}
	public void setHotelGuestList(List<HotelGuest> hotelGuestList) {
		this.hotelGuestList = hotelGuestList;
	}

	public Map<Integer, String> getReservationRoomsMap() {
		return reservationRoomsMap;
	}
	public void setReservationRoomsMap(Map<Integer, String> reservationRoomsMap) {
		this.reservationRoomsMap = reservationRoomsMap;
	}

	public void onInit(ActionEvent event) throws ManagerBeanException{
		setHotel(null);
		setDate(new Date());
	}

	public void onSearch(ActionEvent event) {
		try {
			buildHotelGuestList();
		} catch (AonSQLException e) {
			throw new AbortProcessingException(e.getMessage(), e);
		}
		setModel(new SerializableListDataModel(getHotelGuestList()));
	}

	private void buildHotelGuestList() throws AonSQLException {
		setHotelGuestList(new LinkedList<HotelGuest>());
		setReservationRoomsMap(new HashMap<Integer, String>());

		Connection connection = null;
		PreparedStatement hotelGuestStmt = null;
		ResultSet hotelGuestRs = null;
		try {
			connection = DatabaseUtil.getConnection(AonUtil.getDomainName());

			String seeRoomLabel = "**** " + AonUtil.getMessage(SEE) + " " + AonUtil.getMessage(PMS_ROOM_ABBRV) + " ";
			List<String> seeRoomList = new LinkedList<String>();

			hotelGuestStmt = connection.prepareStatement(getHotelGuestSQL());
			SQLUtils.setInt(hotelGuestStmt, 1, getHotel().getId());
			SQLUtils.setDate(hotelGuestStmt, 2, getDate());
			hotelGuestRs = hotelGuestStmt.executeQuery();
			while (hotelGuestRs.next()) {
				String roomNumber = hotelGuestRs.getString(ROOM_NUMBER);
				String guestName = hotelGuestRs.getString(GUEST_NAME);
				String guestDocument = hotelGuestRs.getString(GUEST_DOCUMENT);
				String guestPhone = hotelGuestRs.getString(GUEST_PHONE);
				String guestEmail = hotelGuestRs.getString(GUEST_EMAIL);
				Integer reservationId = hotelGuestRs.getInt(RESERVATION);
				Date startDate = hotelGuestRs.getDate(START_DATE);
				Date endDate = hotelGuestRs.getDate(END_DATE);
				int guests = hotelGuestRs.getObject(GUESTS) != null ? hotelGuestRs.getInt(GUESTS) : 0;

				if (reservationRoomsMap.containsKey(reservationId)) {
					if (!reservationRoomsMap.get(reservationId).equals(roomNumber)) {
						guestName = seeRoomLabel + reservationRoomsMap.get(reservationId);
						guestDocument = guestPhone = guestEmail = "";
					}
				} else {
					reservationRoomsMap.put(reservationId, roomNumber);
				}

				if (!seeRoomList.contains(roomNumber)) {
					HotelGuest hotelGuest = new HotelGuest();
					hotelGuest.setRoomNumber(roomNumber);
					hotelGuest.setGuestName(guestName);
					hotelGuest.setGuestDocument(guestDocument);
					hotelGuest.setGuestPhone(guestPhone); 
					hotelGuest.setGuestEmail(guestEmail);
					hotelGuest.setReservationId(reservationId); 
					hotelGuest.setStartDate(startDate);
					hotelGuest.setEndDate(endDate);
					hotelGuest.setGuestCount(guests);
					getHotelGuestList().add(hotelGuest);

					if (reservationRoomsMap.containsKey(reservationId) && !reservationRoomsMap.get(reservationId).equals(roomNumber)) {
						seeRoomList.add(roomNumber);
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
			SQLUtils.closeQuietly(hotelGuestRs);
			SQLUtils.closeQuietly(hotelGuestStmt);
			SQLUtils.closeQuietly(connection);
		}
	}

	private String getHotelGuestSQL() throws ManagerBeanException {
		StringBuffer stmt = new StringBuffer();
		stmt.append("SELECT A.name AS " + ROOM_NUMBER + ", CONCAT(PRG.name, ' ', PRG.surname) AS " + GUEST_NAME);
		stmt.append(", CASE WHEN NULLIF(PRG.document, '') IS NULL THEN '' ELSE CONCAT(PRG.document_country, '-', PRG.document) END AS " + GUEST_DOCUMENT);
		stmt.append(", TRIM(PRG.phone) AS " + GUEST_PHONE + ", TRIM(PRG.email) AS " + GUEST_EMAIL + ", PR.project AS " + RESERVATION);
		stmt.append(", PR.start_date AS " + START_DATE + ", PR.end_date AS " + END_DATE + ", PRR.adults + PRR.children AS " + GUESTS);
		stmt.append(" FROM project_reservation AS PR");
		stmt.append(" LEFT JOIN project_reservation_guest AS PRG ON PRG.project_reservation = PR.project");
		stmt.append(" LEFT JOIN project_reservation_room AS PRR ON PRR.project_reservation = PR.project");
		stmt.append(" LEFT JOIN project_reservation_room_detail AS PRRD ON PRRD.project_reservation_room = PRR.id");
		stmt.append(" LEFT JOIN asset_activity AS AA ON AA.id = PRRD.asset_activity");
		stmt.append(" LEFT JOIN asset AS A ON A.id = AA.asset");
		stmt.append(" LEFT JOIN room AS R ON R.asset = A.id");
		stmt.append(" WHERE" + DomainManager.getSQLWhereClause("PR.domain"));
		stmt.append(" AND PR.status <> " + ReservationStatus.CANCELLED.ordinal());
		stmt.append(" AND PR.check_status = " + ReservationCheckStatus.CHECK_IN.ordinal());
		stmt.append(" AND R.hotel = ?");
		stmt.append(" AND AA.date = ?");
		stmt.append(" ORDER BY " + ROOM_NUMBER + "," + GUEST_NAME);

		return stmt.toString();
	}

	@SuppressWarnings("rawtypes")
	public Collection getCollection() {
		return getHotelGuestList();
	}
	@SuppressWarnings("rawtypes")
	public Collection getCollection(boolean forceRefresh) {
		return getCollection();
	}

	/***************** HOTEL GUEST *********************************/

	public static class HotelGuest implements Serializable {

		private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

		private String roomNumber; 
		private String guestName;  
		private String guestDocument; 
		private String guestPhone; 
		private String guestEmail;
		private Integer reservationId; 
		private Date startDate; 
		private Date endDate;
		private Integer guestCount;

		public String getRoomNumber() {
			return roomNumber;
		}
		public void setRoomNumber(String roomNumber) {
			this.roomNumber = roomNumber;
		}

		public String getGuestName() {
			return guestName;
		}
		public void setGuestName(String guestName) {
			this.guestName = guestName;
		}

		public String getGuestDocument() {
			return guestDocument;
		}
		public void setGuestDocument(String guestDocument) {
			this.guestDocument = guestDocument;
		}

		public String getGuestPhone() {
			return guestPhone;
		}
		public void setGuestPhone(String guestPhone) {
			this.guestPhone = guestPhone;
		}

		public String getGuestEmail() {
			return guestEmail;
		}
		public void setGuestEmail(String guestEmail) {
			this.guestEmail = guestEmail;
		}

		public Integer getReservationId() {
			return reservationId;
		}
		public void setReservationId(Integer reservationId) {
			this.reservationId = reservationId;
		}

		public Date getStartDate() {
			return startDate;
		}
		public void setStartDate(Date startDate) {
			this.startDate = startDate;
		}

		public Date getEndDate() {
			return endDate;
		}
		public void setEndDate(Date endDate) {
			this.endDate = endDate;
		}

		public Integer getGuestCount() {
			return guestCount;
		}
		public void setGuestCount(Integer guestCount) {
			this.guestCount = guestCount;
		}

	}
	
	/****************** DOWNLOAD EXCEL BY COUNTRY 
	 * @throws IOException *********************/
	
	public void onByCountry() throws IOException {
		HotelGuestUtils.downloadHotelGuestByCountryExcel(hotel, date);
	}
	

}
