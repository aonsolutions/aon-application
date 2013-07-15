package com.esferalia.aon.gwt.payroll.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.esferalia.aon.gwt.payroll.shared.Employee;
import com.esferalia.aon.gwt.payroll.shared.Events;
import com.esferalia.aon.gwt.payroll.shared.Events.Event;
import com.esferalia.aon.gwt.payroll.shared.Period;
import com.esferalia.aon.payroll.sql.SQLConstants;
import com.esferalia.aon.payroll.sql.SQLConstants.ContractColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.ContractDataColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.PersonColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.RegistryColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.SystemDataColumns;

public class SQLEvents {

	private static java.sql.Date MAX_DATE = new java.sql.Date(Long.MAX_VALUE);

	public static Events getEvents(Connection connection, Integer workplaceId,
			Date startDate, Date endDate, int offset, int limit)
			throws SQLException {
		ResultSet rs = null;
		PreparedStatement stmt = null;
		try {

			java.sql.Date sqlEndDate = SQLUtils.date2sql(endDate);
			java.sql.Date sqlStartDate = SQLUtils.date2sql(startDate);

			String sql = "SELECT * " + " FROM " + SQLConstants.CONTRACT + " ,"
					+ SQLConstants.PERSON + ", " + SQLConstants.REGISTRY
					+ " WHERE " + SQLConstants.CONTRACT + "."
					+ ContractColumns.PERSON + " = " + SQLConstants.PERSON
					+ "." + PersonColumns.REGISTRY + " AND "
					+ SQLConstants.PERSON + "." + PersonColumns.REGISTRY
					+ " = " + SQLConstants.REGISTRY + "." + RegistryColumns.ID
					+ " AND " + SQLConstants.CONTRACT + "."
					+ ContractColumns.WORKPLACE + " = ? " + " AND "
					+ SQLConstants.CONTRACT + "." + ContractColumns.START_DATE
					+ " <=  ? " + " AND ( " + SQLConstants.CONTRACT + "."
					+ ContractColumns.END_DATE + " IS NULL  " + " OR "
					+ SQLConstants.CONTRACT + "." + ContractColumns.END_DATE
					+ " >= ? ) " + " ORDER BY " + SQLConstants.PERSON + "."
					+ PersonColumns.FIRST_SURNAME + " ," + SQLConstants.PERSON
					+ "." + PersonColumns.SECOND_SURNAME + " ,"
					+ SQLConstants.CONTRACT + "." + ContractColumns.START_DATE
					+ " LIMIT ?, ?";

			stmt = connection.prepareStatement(sql);

			stmt.setInt(1, workplaceId);
			stmt.setDate(2, sqlEndDate);
			stmt.setDate(3, sqlStartDate);

			stmt.setInt(4, offset);
			stmt.setInt(5, limit);

			rs = stmt.executeQuery();

			Events events = new Events();
			events.setWorkplaceId(workplaceId);

			StringBuffer idsSqlBuffer = new StringBuffer();
			List<Integer> employeesIds = new ArrayList<Integer>(Math.min(limit,
					50));

			while (rs.next()) {
				Employee employee = new Employee();
				int employeeId = rs.getInt(ContractColumns.ID);
				employee.setId(employeeId);
				employee.setName(rs.getString(PersonColumns.NAME));
				employee.setFirstSurname(rs
						.getString(PersonColumns.FIRST_SURNAME));
				employee.setSecondSurName(rs
						.getString(PersonColumns.SECOND_SURNAME));
				employee.setDocument(rs.getString(SQLConstants.REGISTRY + "."
						+ RegistryColumns.DOCUMENT));

				events.addEmployee(employee);

				if (idsSqlBuffer.length() > 0)
					idsSqlBuffer.append(",");
				idsSqlBuffer.append("?");

				employeesIds.add(employeeId);
			}

			rs.close();
			stmt.close();

			if (employeesIds.size() == 0)
				return events; // Empty events.

			sql = "SELECT * FROM " + SQLConstants.CONTRACT_DATA + " WHERE "
					+ ContractDataColumns.START_DATE + " <=  ? " + " AND ( "
					+ ContractDataColumns.END_DATE + " IS NULL  " + " OR "
					+ ContractDataColumns.END_DATE + " >= ? ) " + " AND "
					+ ContractDataColumns.CONTRACT + " IN ( " + idsSqlBuffer
					+ " ) " + " ORDER BY " + ContractDataColumns.CONTRACT
					+ " ASC ";

			stmt = connection.prepareStatement(sql);

			stmt.setDate(1, sqlEndDate);
			stmt.setDate(2, sqlStartDate);
			for (int i = 0; i < employeesIds.size(); i++) {
				stmt.setInt(i + 3, employeesIds.get(i));
			}

			rs = stmt.executeQuery();

			while (rs.next()) {
				Event event = new Event();
				event.setName(rs.getString(ContractDataColumns.NAME));
				event.setValue(rs.getString(ContractDataColumns.EXPRESSION));
				event.setEndDate(rs.getDate(ContractDataColumns.END_DATE));
				event.setStartDate(rs.getDate(ContractDataColumns.START_DATE));

				int employeeId = rs.getInt(ContractDataColumns.CONTRACT);
				events.addEvent(employeeId, event);
			}

			return events;

		} finally {
			if (rs != null)
				rs.close();
			if (stmt != null)
				stmt.close();
		}

	}

	public static void completeEvents(Connection connection, Events events,
			Date startDate, Date endDate) throws SQLException {

		if (!events.getEmployeeIds().contains(-1)) {
			return;
		}

		int offset = events.getEmployeeCount();
		int workplaceId = events.getWorkplaceId();

		Events remain = getEvents(connection, workplaceId, startDate, endDate,
				offset, Integer.MAX_VALUE);

		Map<String, List<Event>> allEvents = events.getEvents(-1);

		for (Integer employeeId : remain.getEmployeeIds()) {
			Map<String, List<Event>> employeeEvents = remain
					.getEvents(employeeId);

			for (Entry<String, List<Event>> entry : allEvents.entrySet()) {
				String name = entry.getKey();
				List<Event> allList = entry.getValue();

				List<Event> employeeList = employeeEvents.get(entry.getKey());
				if (employeeList == null) {
					employeeEvents.put(name, allList);
				} else {
					employeeList.addAll(0, allList);
				}
			}

			events.setEvents(employeeId, employeeEvents);
		}

		events.remove(-1);
	}

	public static void saveEvents(Connection connection, Events events,
			Date startDate, Date endDate, int domainId) throws SQLException {

		PreparedStatement deleteStmt = null;
		PreparedStatement insertStmt = null;

		try {

			connection.setAutoCommit(false);

			String deleteSql = "DELETE FROM " + SQLConstants.CONTRACT_DATA
					+ " WHERE " + ContractDataColumns.CONTRACT + " = ? "
					+ " AND " + ContractDataColumns.NAME + " = ? " + " AND "
					+ ContractDataColumns.START_DATE + " <=  ? " + " AND ( "
					+ ContractDataColumns.END_DATE + " IS NULL  " + " OR "
					+ ContractDataColumns.END_DATE + " >= ? )";

			deleteStmt = connection.prepareStatement(deleteSql);
			deleteStmt.setDate(3, SQLUtils.date2sql(endDate));
			deleteStmt.setDate(4, SQLUtils.date2sql(startDate));

			String insertSql = ("INSERT INTO " + SQLConstants.CONTRACT_DATA
					+ "( " + ContractDataColumns.DOMAIN + ", "
					+ ContractDataColumns.CONTRACT + ", "
					+ ContractDataColumns.NAME + ", "
					+ ContractDataColumns.EXPRESSION + ", "
					+ ContractDataColumns.START_DATE + ", "
					+ ContractDataColumns.END_DATE + ") VALUES ( ?, ?, ? , ? , ? , ? )");

			insertStmt = connection.prepareStatement(insertSql);

			for (Integer employeeId : events.getEmployeeIds()) {
				for (String eventName : events.getEventNames(employeeId)) {
					List<Event> employeeEvents = events.getFinalEvents(
							employeeId, eventName);
					if (employeeEvents.size() == 0)
						continue;

					// Delete old events...
					deleteStmt.setInt(1, employeeId);
					deleteStmt.setString(2, eventName);
					deleteStmt.execute();

					for (Event event : employeeEvents) {
						if (event.getValue() == null)
							continue;
						insertStmt.setInt(1, domainId);
						insertStmt.setInt(2, employeeId);
						insertStmt.setString(3, event.getName());
						insertStmt.setString(4, event.getValue());
						insertStmt.setDate(5,
								SQLUtils.date2sql(event.getStartDate()));
						insertStmt.setDate(6,
								SQLUtils.date2sql(event.getEndDate()));
						insertStmt.execute();
					}
				}
			}

			connection.commit();

		} catch (SQLException e) {
			connection.rollback();
			throw e;
		} catch (RuntimeException e) {
			connection.rollback();
			throw e;
		} finally {
			if (insertStmt != null)
				insertStmt.close();
			if (deleteStmt != null)
				deleteStmt.close();
			connection.setAutoCommit(true);

		}
	}

	public static Period getAvailPeriod(Connection conn, Integer workplaceId,
			String name) throws SQLException{
		ResultSet rs = null;
		PreparedStatement stmt = null;
		try {
			String sql = "SELECT " + "MIN( " + SQLConstants.CONTRACT_DATA
					+ "." + ContractDataColumns.START_DATE + ")"
					+ ", MAX( IF( ISNULL(" + SQLConstants.CONTRACT_DATA + "."
					+ ContractDataColumns.END_DATE + "), ?, "
					+ SQLConstants.CONTRACT_DATA + "."
					+ ContractDataColumns.END_DATE + "))" + " FROM "
					+ SQLConstants.CONTRACT_DATA + ", " + SQLConstants.CONTRACT
					+ " WHERE " + SQLConstants.CONTRACT_DATA + "."
					+ ContractDataColumns.CONTRACT + " = "
					+ SQLConstants.CONTRACT + "." + ContractColumns.ID
					+ " AND " + ContractColumns.WORKPLACE + " = ? " + " AND "
					+ ContractDataColumns.NAME + " = ? ";
			stmt = conn.prepareStatement(sql);
			stmt.setDate(1, MAX_DATE);
			stmt.setInt(2, workplaceId);
			stmt.setString(3, name);

			rs = stmt.executeQuery();

			if (!rs.next())
				return null;

			Date startDate = rs.getDate(1);
			Date endDate = rs.getDate(2);
			
			if ( startDate == null )
				return null;

			return new Period(startDate, endDate.equals(MAX_DATE) ? null
					: endDate);

		} finally {
			if (rs != null)
				rs.close();
			if (stmt != null)
				stmt.close();
		}

	}

	// -------------------------------------------------------------------------
	//
	// -------------------------------------------------------------------------

}
