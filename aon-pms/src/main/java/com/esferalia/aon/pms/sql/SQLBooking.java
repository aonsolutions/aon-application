package com.esferalia.aon.pms.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.time.DateUtils;

import com.code.aon.common.domain.DomainManager;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.dbutils.AonSQLException;
import com.esferalia.aon.pms.Booking;
import com.esferalia.aon.pms.ProjectReservation;
import com.esferalia.aon.pms.ProjectReservationRoom;
import com.esferalia.aon.pms.enumeration.BookingStayType;
import com.esferalia.aon.pms.enumeration.ReservationCheckStatus;
import com.esferalia.aon.pms.enumeration.ReservationStatus;

public class SQLBooking implements ISQLConstants {

	public static String INSERT_BOOKING =
			"INSERT INTO booking (" + DOMAIN + ", " + PROJECT_RESERVATION_ROOM + ", " + HOTEL + ", " + AGENCY + 
			", " + ITEM + ", " + TARIFF + ", " + STAY_DATE + ", " + STAY_TYPE + ", " + GUESTS + ") " +
			"VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

	public static String DELETE_RESERVATION_ROOM_BOOKING =
			"DELETE FROM booking WHERE " + PROJECT_RESERVATION_ROOM + " = ?";

	public static String SELECT_RESERVATION_BOOKING =
			"SELECT PR.project AS " + RESERVATION + ", PRR.id AS " + RESERVATION_ROOM + ", PRRD.id AS " + RESERVATION_ROOM_DETAIL +
			", PRR.domain AS " + DOMAIN + ", IFNULL(R.hotel, PR.hotel) AS " + HOTEL + ", PR.agency AS " + AGENCY + ", IFNULL(R.item, PRR.item) AS " + ITEM +
			", PRR.tariff AS " + TARIFF + ", IFNULL(AA.date, PR.start_date) AS " + START_DATE + ", PR.end_date AS " + END_DATE + 
			", PRR.adults + PRR.children AS " + GUESTS +
			" FROM project_reservation_room AS PRR" +
			" LEFT JOIN project_reservation AS PR ON PR.project = PRR.project_reservation" +
			" LEFT JOIN project_reservation_room_detail AS PRRD ON PRRD.project_reservation_room = PRR.id" +
			" LEFT JOIN asset_activity AS AA ON AA.id = PRRD.asset_activity" +
			" LEFT JOIN room AS R ON R.asset = AA.asset" +
			" WHERE PR.project = ?" +
			" AND (PR.early_check_out = 0 OR PR.start_date < DATE(PR.end_time))" + 
			" ORDER BY " + RESERVATION + "," + RESERVATION_ROOM + "," + START_DATE;

	public static String SELECT_RESERVATION_ROOM_BOOKING =
			"SELECT PR.project AS " + RESERVATION + ", PRR.id AS " + RESERVATION_ROOM + ", PRRD.id AS " + RESERVATION_ROOM_DETAIL +
			", PRR.domain AS " + DOMAIN + ", IFNULL(R.hotel, PR.hotel) AS " + HOTEL + ", PR.agency AS " + AGENCY + ", IFNULL(R.item, PRR.item) AS " + ITEM +
			", PRR.tariff AS " + TARIFF + ", IFNULL(AA.date, PR.start_date) AS " + START_DATE + ", PR.end_date AS " + END_DATE + 
			", PRR.adults + PRR.children AS " + GUESTS +
			" FROM project_reservation_room AS PRR" +
			" LEFT JOIN project_reservation AS PR ON PR.project = PRR.project_reservation" +
			" LEFT JOIN project_reservation_room_detail AS PRRD ON PRRD.project_reservation_room = PRR.id" +
			" LEFT JOIN asset_activity AS AA ON AA.id = PRRD.asset_activity" +
			" LEFT JOIN room AS R ON R.asset = AA.asset" +
			" WHERE PRR.id = ?" +
			" AND (PR.early_check_out = 0 OR PR.start_date < DATE(PR.end_time))" + 
			" ORDER BY " + RESERVATION + "," + RESERVATION_ROOM + "," + START_DATE;

	public static String REGENERATE_CLEAN_BOOKING =
			"DELETE FROM booking" + 
			" WHERE domain = ?" + 
			" AND FIND_IN_SET(hotel, ?)" +
			" AND stay_date BETWEEN ? AND ?";

	public static String REGENERATE_BUILD_BOOKING =
			"SELECT PR.project AS " + RESERVATION + ", PRR.id AS " + RESERVATION_ROOM + ", PRRD.id AS " + RESERVATION_ROOM_DETAIL +
			", PRR.domain AS " + DOMAIN + ", IFNULL(R.hotel, PR.hotel) AS " + HOTEL + ", PR.agency AS " + AGENCY + ", IFNULL(R.item, PRR.item) AS " + ITEM +
			", PRR.tariff AS " + TARIFF + ", IFNULL(AA.date, PR.start_date) AS " + START_DATE + ", PR.end_date AS " + END_DATE +  
			", PR.hotel AS " + RESERVATION_HOTEL + ", PRR.adults + PRR.children AS " + GUESTS +
			" FROM project_reservation_room AS PRR" +
			" LEFT JOIN project_reservation AS PR ON PR.project = PRR.project_reservation" +
			" LEFT JOIN project_reservation_room_detail AS PRRD ON PRRD.project_reservation_room = PRR.id" +
			" LEFT JOIN asset_activity AS AA ON AA.id = PRRD.asset_activity" +
			" LEFT JOIN room AS R ON R.asset = AA.asset" +
			" WHERE PRR.domain = ?" +
			" AND PR.status <> " + ReservationStatus.CANCELLED.ordinal() + 
			" AND PR.cancellation_date IS NULL" + 
			" AND PR.check_status <> " + ReservationCheckStatus.NO_SHOW.ordinal() + 
			" AND PR.check_status <> " + ReservationCheckStatus.NO_SHOW_NO_INVOICEABLE.ordinal() +
			" AND PR.check_status <> " + ReservationCheckStatus.CANCEL_INVOICEABLE.ordinal() + 
			" AND PR.check_status <> " + ReservationCheckStatus.CANCEL_NO_INVOICEABLE.ordinal() +
			" AND (PR.early_check_out = 0 OR PR.start_date < DATE(PR.end_time))" + 
			" AND PR.end_date >= ? AND PR.start_date <= ?" +
			" AND FIND_IN_SET(IFNULL(R.hotel, PR.hotel), ?)" +
			" ORDER BY " + RESERVATION_HOTEL + "," + RESERVATION + "," + RESERVATION_ROOM + "," + START_DATE;


	public static Booking insert(Connection connection, Booking booking, boolean fillKey) throws AonSQLException {
		PreparedStatement insertStmt = null;
		ResultSet keysRs = null;
		try {
			insertStmt = connection.prepareStatement(INSERT_BOOKING);
			SQLUtils.setInt(insertStmt, 1, booking.getDomain());
			SQLUtils.setInt(insertStmt, 2, booking.getProjectReservationRoom());
			SQLUtils.setInt(insertStmt, 3, booking.getHotel());
			SQLUtils.setInt(insertStmt, 4, booking.getAgency());
			SQLUtils.setInt(insertStmt, 5, booking.getItem());
			SQLUtils.setInt(insertStmt, 6, booking.getTariff());
			SQLUtils.setDate(insertStmt, 7, booking.getStayDate());
			SQLUtils.setInt(insertStmt, 8, booking.getStayType().ordinal());
			SQLUtils.setInt(insertStmt, 9, booking.getGuests());
			insertStmt.execute();

			if (fillKey) {
				keysRs = insertStmt.getGeneratedKeys();
				if (!keysRs.next()) {
					throw new AonSQLException("Unable to recover last inserted id");
				}
				booking.setId(keysRs.getInt(1));
			}
			return booking;
		} catch (AonSQLException e) {
			throw e;
		} catch (Throwable e) {
			throw new AonSQLException(e.getMessage());
		} finally {
			SQLUtils.closeQuietly(insertStmt);
			SQLUtils.closeQuietly(keysRs);
		}
	}

	public static void insert(Connection connection, ProjectReservation reservation) throws AonSQLException {
		PreparedStatement buildStmt = null;
		ResultSet buildRs = null;
		Booking previousBooking = null;
		Date fromDate = reservation.getStartDate();
		Date toDate = reservation.getEndDate();
		try {
			buildStmt = connection.prepareStatement(SELECT_RESERVATION_BOOKING);
			SQLUtils.setInt(buildStmt, 1, reservation.getId());
			buildRs = buildStmt.executeQuery();
			while (buildRs.next()) {
				previousBooking = processInsert(connection, buildRs, previousBooking, fromDate, toDate);
			}

			if (previousBooking != null) {
				previousBooking.setStayDate(DateUtils.addDays(previousBooking.getStayDate(), 1));
				if (!previousBooking.getStayDate().before(fromDate) && !previousBooking.getStayDate().after(toDate)) {
					previousBooking.setStayType(BookingStayType.CHECKOUT);
					insert(connection, previousBooking, false);
				}
			}
		} catch (Throwable e) {
			throw new AonSQLException(e.getMessage());
		} finally {
			SQLUtils.closeQuietly(buildRs);
			SQLUtils.closeQuietly(buildStmt);
		}
	}

	public static void insert(Connection connection, ProjectReservationRoom reservationRoom) throws AonSQLException {
		PreparedStatement buildStmt = null;
		ResultSet buildRs = null;
		Booking previousBooking = null;
		Date fromDate = reservationRoom.getProjectReservation().getStartDate();
		Date toDate = reservationRoom.getProjectReservation().getEndDate();
		try {
			buildStmt = connection.prepareStatement(SELECT_RESERVATION_ROOM_BOOKING);
			SQLUtils.setInt(buildStmt, 1, reservationRoom.getId());
			buildRs = buildStmt.executeQuery();
			while (buildRs.next()) {
				previousBooking = processInsert(connection, buildRs, previousBooking, fromDate, toDate);
			}

			if (previousBooking != null) {
				previousBooking.setStayDate(DateUtils.addDays(previousBooking.getStayDate(), 1));
				if (!previousBooking.getStayDate().before(fromDate) && !previousBooking.getStayDate().after(toDate)) {
					previousBooking.setStayType(BookingStayType.CHECKOUT);
					insert(connection, previousBooking, false);
				}
			}
		} catch (Throwable e) {
			throw new AonSQLException(e.getMessage());
		} finally {
			SQLUtils.closeQuietly(buildRs);
			SQLUtils.closeQuietly(buildStmt);
		}
	}

	public static Booking processInsert(Connection connection, ResultSet buildRs, Booking previousBooking, Date fromDate, Date toDate) throws AonSQLException {
		try {
			Booking booking = new Booking();
			booking.setDomain(buildRs.getInt(DOMAIN));
			booking.setProjectReservationRoom(buildRs.getInt(RESERVATION_ROOM));
			booking.setHotel(buildRs.getInt(HOTEL));
			booking.setAgency(buildRs.getObject(AGENCY) != null ? buildRs.getInt(AGENCY) : null);
			booking.setItem(buildRs.getInt(ITEM));
			booking.setTariff(buildRs.getInt(TARIFF));
			booking.setStayDate(buildRs.getDate(START_DATE));
			booking.setGuests(buildRs.getObject(GUESTS) != null ? buildRs.getInt(GUESTS) : 0);

			boolean checkin = previousBooking == null || !isNextDayInReservation(previousBooking, booking);
			boolean checkout = false;
			if (previousBooking != null && checkin) {
				previousBooking.setStayDate(DateUtils.addDays(previousBooking.getStayDate(), 1));
				if (!previousBooking.getStayDate().before(fromDate) && !previousBooking.getStayDate().after(toDate)) {
					previousBooking.setStayType(BookingStayType.CHECKOUT);
					insert(connection, previousBooking, false);
				}
			}

			if (buildRs.getObject(RESERVATION_ROOM_DETAIL) == null) {
				Date startDate = buildRs.getDate(START_DATE);
				Date endDate = buildRs.getDate(END_DATE);
				for (Date stayDate=DateUtils.truncate(startDate, Calendar.DATE); !stayDate.after(endDate); stayDate=DateUtils.addDays(stayDate, 1)) {
					if (!stayDate.before(fromDate) && !stayDate.after(toDate)) {
						checkin = DateUtils.isSameDay(startDate, stayDate);
						checkout = DateUtils.isSameDay(endDate, stayDate);

						booking.setStayDate(stayDate);
						booking.setStayType(checkin ? BookingStayType.CHECKIN : checkout ? BookingStayType.CHECKOUT : BookingStayType.STAY);
						insert(connection, booking, false);
					}
				}
				return null;
			} else {
				if (!booking.getStayDate().before(fromDate) && !booking.getStayDate().after(toDate)) {
					booking.setStayType(checkin ? BookingStayType.CHECKIN : BookingStayType.STAY);
					insert(connection, booking, false);
				}
				return booking;
			}
		} catch (Throwable e) {
			throw new AonSQLException(e.getMessage());
		}
	}

	public static void delete(Connection connection, ProjectReservation reservation) throws AonSQLException {
		PreparedStatement reservationRoomStmt = null;
		ResultSet reservationRoomRs = null;
		try {
			reservationRoomStmt = connection.prepareStatement("SELECT id AS " + RESERVATION_ROOM + " FROM project_reservation_room WHERE project_reservation = ?");
			SQLUtils.setInt(reservationRoomStmt, 1, reservation.getId());
			reservationRoomRs = reservationRoomStmt.executeQuery();
			while (reservationRoomRs.next()) {
				delete(connection, reservationRoomRs.getInt(RESERVATION_ROOM));
			}
		} catch (Throwable e) {
			throw new AonSQLException(e.getMessage());
		} finally {
			SQLUtils.closeQuietly(reservationRoomRs);
			SQLUtils.closeQuietly(reservationRoomStmt);
		}
	}

	public static void delete(Connection connection, ProjectReservationRoom reservationRoom) throws AonSQLException {
		PreparedStatement deleteStmt = null;
		try {
			delete(connection, reservationRoom.getId());
		} catch (Throwable e) {
			throw new AonSQLException(e.getMessage());
		} finally {
			SQLUtils.closeQuietly(deleteStmt);
		}
	}

	public static void delete(Connection connection, Integer reservationRoomId) throws AonSQLException {
		PreparedStatement deleteStmt = null;
		try {
			deleteStmt = connection.prepareStatement(DELETE_RESERVATION_ROOM_BOOKING);
			SQLUtils.setInt(deleteStmt, 1, reservationRoomId);
			deleteStmt.execute();
		} catch (Throwable e) {
			throw new AonSQLException(e.getMessage());
		} finally {
			SQLUtils.closeQuietly(deleteStmt);
		}
	}

	public static void regenerateClean(Connection connection, String hotelList, Date fromDate, Date toDate) throws AonSQLException {
		PreparedStatement cleanStmt = null;
		try {
			cleanStmt = connection.prepareStatement(REGENERATE_CLEAN_BOOKING);
			SQLUtils.setInt(cleanStmt, 1, DomainManager.getCurrentDomain());
			SQLUtils.setString(cleanStmt, 2, hotelList);
			SQLUtils.setDate(cleanStmt, 3, fromDate);
			SQLUtils.setDate(cleanStmt, 4, toDate);
			cleanStmt.execute();
		} catch (Throwable e) {
			throw new AonSQLException(e.getMessage());
		} finally {
			SQLUtils.closeQuietly(cleanStmt);
		}
	}

	private static boolean isNextDayInReservation(Booking booking1, Booking booking2) {
		return booking1.getProjectReservationRoom() == booking2.getProjectReservationRoom() && 
				booking1.getHotel() == booking2.getHotel() && 
				CommonUtil.getDaysBetweenDates(booking1.getStayDate(), booking2.getStayDate()) == 1;
	}

}
