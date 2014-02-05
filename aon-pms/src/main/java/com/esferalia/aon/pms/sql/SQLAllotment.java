package com.esferalia.aon.pms.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.domain.DomainManager;
import com.code.aon.dbutils.AonSQLException;
import com.esferalia.aon.pms.Allotment;

public class SQLAllotment implements ISQLConstants {

	public static String SELECT_BASIC_ALLOTMENT =
			"SELECT A.start_date AS " + START_DATE + ", A.end_date AS " + END_DATE + ", A.quantity AS " + QUANTITY +
			" FROM allotment AS A" +
			" LEFT JOIN allotment_item AS AI ON AI.allotment = A.id" +
			" LEFT JOIN allotment_tariff AS AT ON AT.allotment = A.id" +
			" WHERE A.domain = ?" +
			" AND A.hotel = ?" + 
			" AND A.start_date <= ?" + 
			" AND A.end_date >= ?" + 
			" AND A.active = 1";


	public static boolean isAllotmentOverlap(Connection connection, Allotment allotment, String items, String tariffs) throws AonSQLException {
		PreparedStatement allotmentStmt = null;
		ResultSet allotmentRs = null;
		try {
			allotmentStmt = connection.prepareStatement(SELECT_BASIC_ALLOTMENT + obtainWhereClause(allotment, items, tariffs));
			SQLUtils.setInt(allotmentStmt, 1, DomainManager.getCurrentDomain());
			SQLUtils.setInt(allotmentStmt, 2, allotment.getHotel().getId());
			SQLUtils.setDate(allotmentStmt, 3, allotment.getEndDate());
			SQLUtils.setDate(allotmentStmt, 4, allotment.getStartDate());
			allotmentRs = allotmentStmt.executeQuery();
			return allotmentRs.next();
		} catch (Throwable e) {
			throw new AonSQLException(e.getMessage());
		} finally {
			SQLUtils.closeQuietly(allotmentStmt);
			SQLUtils.closeQuietly(allotmentRs);
		}
	}

	/*public static Map<Date, Integer> getAllotmentsByDate(Connection connection, Allotment allotment, String items, String tariffs) throws AonSQLException {
		PreparedStatement allotmentStmt = null;
		try {
			allotmentStmt = connection.prepareStatement(SELECT_ALLOTMENT);
			SQLUtils.setInt(allotmentStmt, 1, DomainManager.getCurrentDomain());
			SQLUtils.setInt(allotmentStmt, 2, hotel);
			//SQLUtils.setInt(allotmentStmt, 3, agency); Agencia o Grupo!!
			SQLUtils.setDate(allotmentStmt, 4, fromDate);
			SQLUtils.setDate(allotmentStmt, 5, toDate);
			//Devolver el resultset o una lista de allotments, lo que se prefiera.
			//allotmentStmt.execute();
		} catch (Throwable e) {
			throw new AonSQLException(e.getMessage());
		} finally {
			SQLUtils.closeQuietly(allotmentStmt);
		}
		return null;
	}*/

	private static String obtainWhereClause(Allotment allotment, String items, String tariffs) {
		StringBuffer where = new StringBuffer();
		if (allotment.getId() != null) {
			where.append(" AND A.id != " + allotment.getId());
		}
		if (allotment.getAgency() != null && allotment.getAgency().getId() != null) {
			where.append(" AND (A.agency = " + allotment.getAgency().getId());
			if (allotment.getAgency().getInvoicingGroup() != null && allotment.getAgency().getInvoicingGroup().getId() != null) {
				where.append(" OR A.agency_group = " + allotment.getAgency().getInvoicingGroup().getId());
			}
			where.append(")");
		}
		if (allotment.getAgencyGroup() != null && allotment.getAgencyGroup().getId() != null) {
			where.append(" AND (A.agency_group = " + allotment.getAgencyGroup().getId());
			where.append(" OR A.agency IN (SELECT registry FROM customer WHERE invoicing_group = " + allotment.getAgencyGroup().getId() + ")");
			where.append(")");
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
