package com.esferalia.aon.pms.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;

import com.code.aon.asset.enumeration.ActivityStatus;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.config.Tariff;
import com.code.aon.customer.Customer;
import com.code.aon.customer.InvoicingGroup;
import com.code.aon.dbutils.AonSQLException;
import com.code.aon.product.Item;
import com.esferalia.aon.pms.Allotment;
import com.esferalia.aon.pms.Hotel;

public class SQLAllotment implements ISQLConstants {

	public static String SELECT_BASIC_ALLOTMENT =
			"SELECT DISTINCT(A.id) AS " + ID + ", A.agency AS " + AGENCY + ", A.agency_group AS " + AGENCY_GROUP + 
			", A.start_date AS " + START_DATE + ", A.end_date AS " + END_DATE + ", A.rate_code AS " + RATE_CODE + 
			", A.quantity AS " + QUANTITY +
			" FROM allotment AS A" +
			" LEFT JOIN allotment_item AS AI ON AI.allotment = A.id" +
			" LEFT JOIN allotment_tariff AS AT ON AT.allotment = A.id" +
			" WHERE A.domain = ?" +
			" AND A.hotel = ?" + 
			" AND A.end_date >= ?" + 
			" AND A.start_date <= ?" + 
			" AND A.active = 1";

	public static String SELECT_ALLOTMENT_BOOKING =
			"SELECT B.stay_date AS " + STAY_DATE + ", COUNT(DISTINCT B.id) AS " + ROOMS +
			" FROM allotment AS A, booking AS B" +
			" WHERE A.domain = ?" +
			" AND A.hotel = ?" + 
			" AND (A.agency = ? OR A.agency_group = ?)" +
			" AND A.end_date >= ?" + 
			" AND A.start_date <= ?" + 
			" AND A.active = 1" +
			" AND B.hotel = A.hotel" +
			" AND B.stay_date BETWEEN A.start_date AND A.end_date" +
			" AND B.stay_date BETWEEN ? AND ?" +
			" AND B.stay_type IN (0,2)" +
			" AND ((A.agency IS NOT NULL AND B.agency = A.agency)" +
			"   OR (A.agency IS NULL AND B.agency IN (SELECT registry FROM customer WHERE invoicing_group = A.agency_group)))" +
			" AND (0 = (SELECT COUNT(*) FROM allotment_item WHERE allotment = A.id)" +
			"   OR B.item IN (SELECT item FROM allotment_item WHERE allotment = A.id))" +
			" AND (0 = (SELECT COUNT(*) FROM allotment_tariff WHERE allotment = A.id)" +
			"   OR B.tariff IN (SELECT tariff FROM allotment_tariff WHERE allotment = A.id))" +
			" GROUP BY B.stay_date" +
			" ORDER BY " + STAY_DATE;

	public static String SELECT_ALLOTMENT_RATE_BOOKING =
			"SELECT B.stay_date AS " + STAY_DATE + ", COUNT(DISTINCT B.id) AS " + ROOMS +
			" FROM booking AS B, project_reservation_room AS PRR" +
			" WHERE B.domain = ?" +
			" AND B.hotel = ?" + 
			" AND B.stay_date BETWEEN ? AND ?" +
			" AND B.stay_type IN (0,2)" +
			" AND B.project_reservation_room = PRR.id" +
			" AND PRR.allotment_rate_code = ?" +
			" AND PRR.item = ?" +
			" GROUP BY B.stay_date" +
			" UNION " +
			" SELECT AA.date AS " + STAY_DATE + ", COUNT(*) AS " + ROOMS +
			" FROM asset_activity AS AA, room AS R" +
			" WHERE R.domain = ?" +
			" AND R.hotel = ?" +
			" AND R.item = ?" +
			" AND R.active = 1" +
			" AND AA.asset = R.asset" +
			" AND AA.date BETWEEN ? AND ?" +
			" AND AA.status <> " + ActivityStatus.BUSY.ordinal() +
			" GROUP BY AA.date" +
			" ORDER BY " + STAY_DATE;

	public static boolean isAllotmentDefined(Connection connection, Allotment allotment, String items, String tariffs) throws AonSQLException {
		PreparedStatement allotmentStmt = null;
		ResultSet allotmentRs = null;
		try {
			String selectAllotment = SELECT_BASIC_ALLOTMENT + obtainWhereClause(allotment, items, tariffs);
			allotmentStmt = connection.prepareStatement(selectAllotment);
			SQLUtils.setInt(allotmentStmt, 1, DomainManager.getCurrentDomain());
			SQLUtils.setInt(allotmentStmt, 2, allotment.getHotel().getId());
			SQLUtils.setDate(allotmentStmt, 3, allotment.getStartDate());
			SQLUtils.setDate(allotmentStmt, 4, allotment.getEndDate());
			allotmentRs = allotmentStmt.executeQuery();
			return allotmentRs.next();
		} catch (Throwable e) {
			throw new AonSQLException(e.getMessage());
		} finally {
			SQLUtils.closeQuietly(allotmentStmt);
			SQLUtils.closeQuietly(allotmentRs);
		}
	}

	public static Map<Date, int[]> getAllotmentBookingMap(Connection connection, Hotel hotel, Date fromDate, Date toDate, Customer agency, Item item, Tariff tariff)
			throws AonSQLException {
		Map<Date, int[]> allotmentBookingMap = new TreeMap<Date, int[]>();
		PreparedStatement allotmentStmt = null;
		ResultSet allotmentRs = null;
		PreparedStatement allotmentBookingStmt = null;
		ResultSet allotmentBookingRs = null;
		try {
			String selectAllotment = SELECT_BASIC_ALLOTMENT + obtainWhereClause(null, agency, null, null, item, tariff);
			allotmentStmt = connection.prepareStatement(selectAllotment);
			SQLUtils.setInt(allotmentStmt, 1, DomainManager.getCurrentDomain());
			SQLUtils.setInt(allotmentStmt, 2, hotel.getId());
			SQLUtils.setDate(allotmentStmt, 3, fromDate);
			SQLUtils.setDate(allotmentStmt, 4, toDate);
			allotmentRs = allotmentStmt.executeQuery();
			while (allotmentRs.next()) {
				Date startDate = allotmentRs.getDate(START_DATE);
				Date endDate = allotmentRs.getDate(END_DATE);
				int quantity = allotmentRs.getInt(QUANTITY);
				for (Date date = DateUtils.truncate(startDate, Calendar.DATE); !date.after(endDate); date = DateUtils.addDays(date, 1)) {
					if (!date.before(fromDate) && !date.after(toDate)) {
						allotmentBookingMap.put(date, new int[]{quantity, 0});
					}
				}
			}

			if (allotmentBookingMap.size() > 0) {
				int agencyGroup = (agency.getInvoicingGroup() != null && agency.getInvoicingGroup().getId() != null) ? agency.getInvoicingGroup().getId() : 0;
				allotmentBookingStmt = connection.prepareStatement(SELECT_ALLOTMENT_BOOKING);
				SQLUtils.setInt(allotmentBookingStmt, 1, DomainManager.getCurrentDomain());
				SQLUtils.setInt(allotmentBookingStmt, 2, hotel.getId());
				SQLUtils.setInt(allotmentBookingStmt, 3, agency.getId());
				SQLUtils.setInt(allotmentBookingStmt, 4, agencyGroup);
				SQLUtils.setDate(allotmentBookingStmt, 5, fromDate);
				SQLUtils.setDate(allotmentBookingStmt, 6, toDate);
				SQLUtils.setDate(allotmentBookingStmt, 7, fromDate);
				SQLUtils.setDate(allotmentBookingStmt, 8, toDate);
				allotmentBookingRs = allotmentBookingStmt.executeQuery();
				while (allotmentBookingRs.next()) {
					Date stayDate = allotmentBookingRs.getDate(STAY_DATE);
					int rooms = allotmentBookingRs.getInt(ROOMS);
					if (allotmentBookingMap.containsKey(stayDate)) {
						int[] allotmentBooking = allotmentBookingMap.get(stayDate);
						allotmentBooking[1] = allotmentBooking[1] + rooms;
					}
				}
			}

			return allotmentBookingMap;
		} catch (Throwable e) {
			throw new AonSQLException(e.getMessage());
		} finally {
			SQLUtils.closeQuietly(allotmentBookingStmt);
			SQLUtils.closeQuietly(allotmentBookingRs);
			SQLUtils.closeQuietly(allotmentStmt);
			SQLUtils.closeQuietly(allotmentRs);
		}
	}

	public static Map<Date, int[]> getAllotmentBookingMap(Connection connection, Hotel hotel, Date fromDate, Date toDate, String rateCode, Item item, Tariff tariff)
			throws AonSQLException {
		Map<Date, int[]> allotmentBookingMap = new TreeMap<Date, int[]>();
		PreparedStatement allotmentStmt = null;
		ResultSet allotmentRs = null;
		PreparedStatement allotmentBookingStmt = null;
		ResultSet allotmentBookingRs = null;
		try {
			String selectAllotment = SELECT_BASIC_ALLOTMENT + obtainWhereClause(null, null, null, rateCode, item, tariff);
			allotmentStmt = connection.prepareStatement(selectAllotment);
			SQLUtils.setInt(allotmentStmt, 1, DomainManager.getCurrentDomain());
			SQLUtils.setInt(allotmentStmt, 2, hotel.getId());
			SQLUtils.setDate(allotmentStmt, 3, fromDate);
			SQLUtils.setDate(allotmentStmt, 4, toDate);
			allotmentRs = allotmentStmt.executeQuery();
			while (allotmentRs.next()) {
				Date startDate = allotmentRs.getDate(START_DATE);
				Date endDate = allotmentRs.getDate(END_DATE);
				int quantity = allotmentRs.getInt(QUANTITY);
				for (Date date = DateUtils.truncate(startDate, Calendar.DATE); !date.after(endDate); date = DateUtils.addDays(date, 1)) {
					if (!date.before(fromDate) && !date.after(toDate)) {
						allotmentBookingMap.put(date, new int[]{quantity, 0});
					}
				}
			}

			if (allotmentBookingMap.size() > 0) {
				allotmentBookingStmt = connection.prepareStatement(SELECT_ALLOTMENT_RATE_BOOKING);
				SQLUtils.setInt(allotmentBookingStmt, 1, DomainManager.getCurrentDomain());
				SQLUtils.setInt(allotmentBookingStmt, 2, hotel.getId());
				SQLUtils.setDate(allotmentBookingStmt, 3, fromDate);
				SQLUtils.setDate(allotmentBookingStmt, 4, toDate);
				SQLUtils.setString(allotmentBookingStmt, 5, rateCode);
				SQLUtils.setInt(allotmentBookingStmt, 6, item.getId());
				SQLUtils.setInt(allotmentBookingStmt, 7, DomainManager.getCurrentDomain());
				SQLUtils.setInt(allotmentBookingStmt, 8, hotel.getId());
				SQLUtils.setInt(allotmentBookingStmt, 9, item.getId());
				SQLUtils.setDate(allotmentBookingStmt, 10, fromDate);
				SQLUtils.setDate(allotmentBookingStmt, 11, toDate);
				allotmentBookingRs = allotmentBookingStmt.executeQuery();
				while (allotmentBookingRs.next()) {
					Date stayDate = allotmentBookingRs.getDate(STAY_DATE);
					int rooms = allotmentBookingRs.getInt(ROOMS);
					if (allotmentBookingMap.containsKey(stayDate)) {
						int[] allotmentBooking = allotmentBookingMap.get(stayDate);
						allotmentBooking[1] = allotmentBooking[1] + rooms;
					}
				}
			}

			return allotmentBookingMap;
		} catch (Throwable e) {
			throw new AonSQLException(e.getMessage());
		} finally {
			SQLUtils.closeQuietly(allotmentBookingStmt);
			SQLUtils.closeQuietly(allotmentBookingRs);
			SQLUtils.closeQuietly(allotmentStmt);
			SQLUtils.closeQuietly(allotmentRs);
		}
	}

	private static String obtainWhereClause(Allotment allotment, String items, String tariffs) {
		return obtainWhereClause(allotment.getId(), allotment.getAgency(), allotment.getAgencyGroup(), allotment.getRateCode(), items, tariffs);
	}

	private static String obtainWhereClause(Integer id, Customer agency, InvoicingGroup agencyGroup, String rateCode, Item item, Tariff tariff) {
		String items = (item != null && item.getId() != null) ? item.getId().toString() : null;
		String tariffs = (tariff != null && tariff.getId() != null) ? tariff.getId().toString() : null;
		return obtainWhereClause(id, agency, agencyGroup, rateCode, items, tariffs);
	}

	private static String obtainWhereClause(Integer id, Customer agency, InvoicingGroup agencyGroup, String rateCode, String items, String tariffs) {
		StringBuffer where = new StringBuffer();
		if (id != null) {
			where.append(" AND A.id != " + id);
		}
		if (agency != null && agency.getId() != null) {
			where.append(" AND (A.agency = " + agency.getId());
			if (agency.getInvoicingGroup() != null && agency.getInvoicingGroup().getId() != null) {
				where.append(" OR A.agency_group = " + agency.getInvoicingGroup().getId());
			}
			where.append(")");
		}
		if (agencyGroup != null && agencyGroup.getId() != null) {
			where.append(" AND (A.agency_group = " + agencyGroup.getId());
			where.append(" OR A.agency IN (SELECT registry FROM customer WHERE invoicing_group = " + agencyGroup.getId() + ")");
			where.append(")");
		}
		if (StringUtils.isNotBlank(rateCode)) {
			where.append(" AND A.rate_code = '" + rateCode + "'");
		}
		if (StringUtils.isNotBlank(items)) {
			where.append(" AND (AI.item IN (" + items + ") OR AI.item IS NULL)");
		}
		if (StringUtils.isNotBlank(tariffs)) {
			where.append(" AND (AT.tariff IN (" + tariffs + ") OR AT.tariff IS NULL)");
		}
		return where.toString();
	}

}
