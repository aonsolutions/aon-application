package com.esferalia.aon.pms.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;

import com.code.aon.common.domain.DomainManager;
import com.code.aon.config.Tariff;
import com.code.aon.customer.Customer;
import net.aonsolutions.core.dbutils.AonSQLException;
import com.code.aon.product.Item;
import com.esferalia.aon.pms.Hotel;
import com.esferalia.aon.pms.ProjectReservation;
import com.esferalia.aon.pms.ProjectReservationRoom;
import com.esferalia.aon.pms.ReservationRequest;
import com.esferalia.aon.pms.ReservationRequestRoom;
import com.esferalia.aon.pms.StopSales;
import com.esferalia.aon.pms.reservation.ReservationUtils;

public class SQLStopSales implements ISQLConstants {

	public static String SELECT_BASIC_STOP_SALES =
			"SELECT DISTINCT(SS.id) AS " + ID + ", SS.start_date AS " + START_DATE + ", SS.end_date AS " + END_DATE + 
			" FROM stop_sales AS SS" +
			" LEFT JOIN stop_sales_item AS SSI ON SSI.stop_sales = SS.id" +
			" LEFT JOIN stop_sales_tariff AS SST ON SST.stop_sales = SS.id" +
			" WHERE SS.domain = ?" +
			" AND SS.hotel = ?" + 
			" AND SS.end_date >= ?" + 
			" AND SS.start_date <= ?" + 
			" AND SS.active = 1";


	public static boolean isStopSalesDefined(Connection connection, StopSales stopSales, String items, String tariffs) throws AonSQLException {
		return isStopSalesDefined(connection, stopSales.getId(), stopSales.getHotel(), stopSales.getStartDate(), stopSales.getEndDate(), items, tariffs);
	}

	public static boolean isStopSalesDefined(Connection connection, Integer id, Hotel hotel, Date startDate, Date endDate, String items, String tariffs) 
			throws AonSQLException {
		PreparedStatement stopSalesStmt = null;
		ResultSet stopSalesRs = null;
		try {
			stopSalesStmt = connection.prepareStatement(SELECT_BASIC_STOP_SALES + obtainWhereClause(id, items, tariffs, false));
			SQLUtils.setInt(stopSalesStmt, 1, DomainManager.getCurrentDomain());
			SQLUtils.setInt(stopSalesStmt, 2, hotel.getId());
			SQLUtils.setDate(stopSalesStmt, 3, startDate);
			SQLUtils.setDate(stopSalesStmt, 4, endDate);
			stopSalesRs = stopSalesStmt.executeQuery();
			return stopSalesRs.next();
		} catch (Throwable e) {
			throw new AonSQLException(e.getMessage());
		} finally {
			SQLUtils.closeQuietly(stopSalesStmt);
			SQLUtils.closeQuietly(stopSalesRs);
		}
	}

	public static boolean mustStopSale(Connection connection, ProjectReservationRoom resRoom) throws AonSQLException {
		ProjectReservation res = resRoom.getProjectReservation();
		return mustStopSale(connection, res.getHotel(), res.getStartDate(), res.getEndDate(), res.getAgency(), resRoom.getItem(), resRoom.getTariff(), 1);
	}

	public static boolean mustStopSale(Connection connection, ReservationRequestRoom reqRoom) throws AonSQLException {
		return mustStopSale(connection, reqRoom, null, null);
	}

	public static boolean mustStopSale(Connection connection, ReservationRequestRoom reqRoom, String itemCode, String tariffCode) throws AonSQLException {
		ReservationRequest req = reqRoom.getReservationRequest();
		ReservationUtils reservationUtils = new ReservationUtils();
		Item item = reqRoom.getItem();
		Tariff tariff = null;
		try {
			if (StringUtils.isNotBlank(itemCode)) {
				item = reservationUtils.obtainRoomItem(itemCode);
			}

			tariffCode = StringUtils.isNotBlank(tariffCode) ? tariffCode : reqRoom.getTariffCode();
			if (StringUtils.isNotBlank(tariffCode)) {
				tariff = reservationUtils.obtainTariff(tariffCode);
				if (tariff == null) {
					tariff = reservationUtils.obtainDefaultTariff();
				}
			}
		} catch (Throwable e) {
			throw new AonSQLException(e.getMessage());
		} 
		return mustStopSale(connection, req.getHotel(), req.getStartDate(), req.getEndDate(), req.getAgency(), item, tariff, reqRoom.getUnits());
	}

	public static boolean mustStopSale(Connection connection, Hotel hotel, Date fromDate, Date toDate, Customer agency, Item item, Tariff tariff, int rooms) 
			throws AonSQLException {
		List<Date> stopSalesDateList = new LinkedList<Date>();
		PreparedStatement stopSalesStmt = null;
		ResultSet stopSalesRs = null;
		try {
			stopSalesStmt = connection.prepareStatement(SELECT_BASIC_STOP_SALES + obtainWhereClause(null, item, tariff, true));
			SQLUtils.setInt(stopSalesStmt, 1, DomainManager.getCurrentDomain());
			SQLUtils.setInt(stopSalesStmt, 2, hotel.getId());
			SQLUtils.setDate(stopSalesStmt, 3, fromDate);
			SQLUtils.setDate(stopSalesStmt, 4, toDate);
			stopSalesRs = stopSalesStmt.executeQuery();
			while (stopSalesRs.next()) {
				Date startDate = stopSalesRs.getDate(START_DATE);
				Date endDate = stopSalesRs.getDate(END_DATE);
				for (Date date = DateUtils.truncate(startDate, Calendar.DATE); !date.after(endDate); date = DateUtils.addDays(date, 1)) {
					if (!date.before(fromDate) && !date.after(toDate)) {
						stopSalesDateList.add(date);
					}
				}
			}
			SQLUtils.closeQuietly(stopSalesRs);

			if (stopSalesDateList.size() > 0) {
				if (agency == null || agency.getId() == null) {
					return true;
				}

				Map<Date, int[]> allotmentBookingMap = SQLAllotment.getAllotmentBookingMap(connection, hotel, fromDate, toDate, agency, item, tariff);
				for (Date date : stopSalesDateList) {
					int[] allotmentBooking = allotmentBookingMap.get(date);
					if (allotmentBooking == null || (allotmentBooking[0] - allotmentBooking[1]) < rooms) {
						return true;
					}
				}
			}

			return false;
		} catch (Throwable e) {
			throw new AonSQLException(e.getMessage());
		} finally {
			SQLUtils.closeQuietly(stopSalesStmt);
			SQLUtils.closeQuietly(stopSalesRs);
		}
	}

	private static String obtainWhereClause(Integer id, Item item, Tariff tariff, boolean includeAll) {
		String items = (item != null && item.getId() != null) ? item.getId().toString() : null;
		String tariffs = (tariff != null && tariff.getId() != null) ? tariff.getId().toString() : null;
		return obtainWhereClause(id, items, tariffs, includeAll);
	}

	private static String obtainWhereClause(Integer id, String items, String tariffs, boolean includeAll) {
		StringBuffer where = new StringBuffer();
		if (id != null) {
			where.append(" AND SS.id != " + id);
		}
		if (StringUtils.isNotBlank(items)) {
			where.append(" AND (SSI.item IN (" + items + ") OR SSI.item IS NULL)");
		} else if (includeAll) {
			where.append(" AND SSI.item IS NULL");
		}
		if (StringUtils.isNotBlank(tariffs)) {
			where.append(" AND (SST.tariff IN (" + tariffs + ") OR SST.tariff IS NULL)");
		} else if (includeAll) {
			where.append(" AND SST.tariff IS NULL");
		}
		return where.toString();
	}

}
