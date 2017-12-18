package com.esferalia.aon.gwt.payroll.sql;

import static com.esferalia.aon.gwt.payroll.sql.SQLUtils.getInteger;
import static com.esferalia.aon.gwt.payroll.sql.SQLUtils.getType;
import static com.esferalia.aon.jooq.tables.AgreementLevel.AGREEMENT_LEVEL;
import static com.esferalia.aon.jooq.tables.AgreementLevelCategory.AGREEMENT_LEVEL_CATEGORY;
import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.ContractData.CONTRACT_DATA;
import static com.esferalia.aon.jooq.tables.ContractPayment.CONTRACT_PAYMENT;
import static com.esferalia.aon.jooq.tables.PaymentConcept.PAYMENT_CONCEPT;
import static com.esferalia.aon.jooq.tables.Person.PERSON;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.SystemData.SYSTEM_DATA;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.jooq.Cursor;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Record2;
import org.jooq.SelectConditionStep;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.gwt.payroll.jooq.JooqUtils;
import com.esferalia.aon.gwt.payroll.shared.Employee;
import com.esferalia.aon.gwt.payroll.shared.Events;
import com.esferalia.aon.gwt.payroll.shared.Events.Event;
import com.esferalia.aon.gwt.payroll.shared.Payment;
import com.esferalia.aon.gwt.payroll.shared.Period;
import com.esferalia.aon.gwt.payroll.shared.Salary;
import com.esferalia.aon.jooq.tables.records.ContractDataRecord;
import com.esferalia.aon.jooq.tables.records.SystemDataRecord;
import com.esferalia.aon.payroll.sql.SQLConstants;
import com.esferalia.aon.payroll.sql.SQLConstants.ContractColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.ContractDataColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.ContractPaymentColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.PaymentConceptColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.PersonColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.RegistryColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.SystemDataColumns;
import com.esferalia.aon.salary.expression.ExpressionContext;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.salary.expression.ExpressionImpl;
import com.esferalia.aon.salary.expression.ITimedResult;

public class SQLEvents {

	private static java.sql.Date MAX_DATE = new java.sql.Date(Long.MAX_VALUE);

	private static Settings SETTINGS = null;

	public static Events getEventsAux(Connection conn, Integer workplaceId,
			Date startDate, Date endDate, int offset, int limit, String names[])
			throws SQLException {

		DSLContext dslContext = DSL.using(conn, getDefaultSettings());

		Cursor<Record> cursor = null;

		try {

			java.sql.Date sqlEndDate = SQLUtils.date2sql(endDate);
			java.sql.Date sqlStartDate = SQLUtils.date2sql(startDate);

			SelectConditionStep<Record> select = dslContext
					.select()
					.from(CONTRACT.join(PERSON)
							.on(CONTRACT.PERSON.eq(PERSON.REGISTRY))
							.join(REGISTRY).on(PERSON.REGISTRY.eq(REGISTRY.ID)))
					.where(CONTRACT.WORKPLACE
							.eq(workplaceId)
							.and(CONTRACT.START_DATE.lessOrEqual(sqlEndDate))
							.and(CONTRACT.END_DATE.isNull().or(
									CONTRACT.END_DATE
											.greaterOrEqual(sqlStartDate))));

			cursor = select.limit(limit).offset(offset).fetchLazy();

			Events events = new Events();
			events.setWorkplaceId(workplaceId);
			Collection<Integer> employeesIds = new ArrayList<Integer>(Math.min(
					limit, 50));

			for (Record record : cursor) {

				Employee employee = new Employee();
				int employeeId = record.getValue(CONTRACT.ID);
				employee.setId(employeeId);
				employee.setName(record.getValue(PERSON.NAME));
				employee.setFirstSurname(record.getValue(PERSON.FIRST_SURNAME));
				employee.setSecondSurName(record
						.getValue(PERSON.SECOND_SURNAME));
				employee.setDocument(record.getValue(REGISTRY.DOCUMENT));

				employeesIds.add(employeeId);
				events.addEmployee(employee);

			}

			if (employeesIds.size() == 0)
				return events; // Empty events;

			Collection<String> namesHosts = new ArrayList<String>(
					Arrays.asList(names));

			// Try to load System events ( 'data' )
			List<SystemDataRecord> eventsData = dslContext
					.selectFrom(SYSTEM_DATA)
					.where(SYSTEM_DATA.START_DATE
							.lessOrEqual(sqlEndDate)
							.and(SYSTEM_DATA.END_DATE.isNull().or(
									SYSTEM_DATA.END_DATE
											.greaterOrEqual(sqlStartDate)))
							.and(SYSTEM_DATA.NAME.in(namesHosts)))
					.fetchInto(SYSTEM_DATA);

			if (eventsData != null) {

				ExpressionContext systemExpressionContext = new ExpressionContext();
				List<Event> systemEvents = new LinkedList<Event>();

				for (SystemDataRecord record : eventsData) {

					String name = record.getValue(SYSTEM_DATA.NAME);
					Date end = record.getValue(SYSTEM_DATA.END_DATE);
					Date start = record.getValue(SYSTEM_DATA.START_DATE);
					String script = record.getValue(SYSTEM_DATA.EXPRESSION);

					try {

						ExpressionImpl expression = new ExpressionImpl();
						expression.setName(name);
						expression.setExpression(script);

						List<ITimedResult<Object>> results = systemExpressionContext
								.addExpression(expression, start, end);

						for (ITimedResult<Object> result : results) {
							Event event = new Event();
							event.setName(name);
							event.setValue(result.getValue() != null ? result
									.getValue().toString() : null);
							event.setEndDate(result.getPeriod().getEnd());
							event.setStartDate(result.getPeriod().getStart());
							events.addEvent(event);
						}

					} catch (ExpressionException ex) {
						Event event = new Event();
						event.setName(name);
						event.setValue(script);
						event.setStartDate(start);
						event.setEndDate(end);
						systemEvents.add(event);
					}
				}
				List<ContractDataRecord> contractDatas = dslContext
						.selectFrom(CONTRACT_DATA)
						.where(CONTRACT_DATA.START_DATE
								.lessOrEqual(sqlEndDate)
								.and(CONTRACT_DATA.END_DATE.isNull().or(
										CONTRACT_DATA.END_DATE
												.greaterOrEqual(sqlStartDate)))
								.and(CONTRACT_DATA.CONTRACT.in(employeesIds))
								.and(CONTRACT_DATA.NAME.in(namesHosts)))
						.orderBy(CONTRACT_DATA.CONTRACT.asc())
						.fetchInto(CONTRACT_DATA);

				if (contractDatas != null) {

					int lastEmployeeId = Integer.MIN_VALUE;

					ExpressionContext employeeExpressionContext = null;

					for (ContractDataRecord record : contractDatas) {

						int employeeId = record
								.getValue(CONTRACT_DATA.CONTRACT);
						if (lastEmployeeId != employeeId) {
							employeeExpressionContext = new ExpressionContext(
									systemExpressionContext);
							if (lastEmployeeId != Integer.MIN_VALUE)
								loadSystemEvents(systemEvents,
										employeeExpressionContext, events,
										lastEmployeeId);
						}
						Event event = null;
						String name = record.getValue(SYSTEM_DATA.NAME);
						Date end = record.getValue(SYSTEM_DATA.END_DATE);
						Date start = record.getValue(SYSTEM_DATA.START_DATE);
						String script = record.getValue(SYSTEM_DATA.EXPRESSION);

						try {

							ExpressionImpl expression = new ExpressionImpl();
							expression.setName(name);
							expression.setExpression(script);
							List<ITimedResult<Object>> results = employeeExpressionContext
									.addExpression(expression, start, end);

							for (ITimedResult<Object> result : results) {
								event = new Event();
								event.setName(name);

								if (result.getValue() == null)
									event.setValue(null);
								else
									event.setValue(result.getValue().toString());

								event.setEndDate(result.getPeriod().getEnd());
								event.setStartDate(result.getPeriod()
										.getStart());
								events.addEvent(employeeId, event);
							}

						} catch (ExpressionException ex) {
							event = new Event();
							event.setName(name);
							event.setValue(script);
							event.setStartDate(start);
							event.setEndDate(end);
							events.addEvent(employeeId, event);
						}
						lastEmployeeId = employeeId;
					}
					if (lastEmployeeId != Integer.MIN_VALUE)
						loadSystemEvents(systemEvents,
								employeeExpressionContext, events,
								lastEmployeeId);
				}
			}

			return events;

		} finally {
			if (cursor != null)
				cursor.close();
		}

	}

	public static Events getEvents(Connection connection, Integer workplaceId,
			Date startDate, Date endDate, int offset, int limit, String names[])
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

			String namesHosts = StringUtils.repeat("?", ",", names.length);

			// Try to load System events ( 'data' )
			sql = "SELECT * FROM " + SQLConstants.SYSTEM_DATA + " WHERE "
					+ SystemDataColumns.START_DATE + " <=  ? " + " AND ( "
					+ SystemDataColumns.END_DATE + " IS NULL  " + " OR "
					+ SystemDataColumns.END_DATE + " >= ? ) " + " AND "
					+ SystemDataColumns.NAME + " IN (" + namesHosts + ")";
			stmt = connection.prepareStatement(sql);
			stmt.setDate(1, sqlEndDate);
			stmt.setDate(2, sqlStartDate);
			for (int i = 0; i < names.length; i++)
				stmt.setString(i + 3, names[i]);
			rs = stmt.executeQuery();

			ExpressionContext systemExpressionContext = new ExpressionContext();
			List<Event> systemEvents = new LinkedList<Event>();

			while (rs.next()) {
				String name = rs.getString(SystemDataColumns.NAME);
				Date end = rs.getDate(SystemDataColumns.END_DATE);
				Date start = rs.getDate(SystemDataColumns.START_DATE);
				String script = rs.getString(SystemDataColumns.EXPRESSION);

				try {

					ExpressionImpl expression = new ExpressionImpl();
					expression.setName(name);
					expression.setExpression(script);

					List<ITimedResult<Object>> results = systemExpressionContext
							.addExpression(expression, start, end);

					for (ITimedResult<Object> result : results) {
						Event event = new Event();
						event.setName(name);
						event.setValue(result.getValue() != null ? result
								.getValue().toString() : null);
						event.setEndDate(result.getPeriod().getEnd());
						event.setStartDate(result.getPeriod().getStart());
						events.addEvent(event);
					}
				} catch (ExpressionException e) {
					Event event = new Event();
					event.setName(name);
					event.setValue(script);
					event.setStartDate(start);
					event.setEndDate(end);
					systemEvents.add(event);
				}
			}

			rs.close();
			stmt.close();

			sql = "SELECT * FROM " + SQLConstants.CONTRACT_DATA + " WHERE "
					+ ContractDataColumns.START_DATE + " <=  ? " + " AND ( "
					+ ContractDataColumns.END_DATE + " IS NULL  " + " OR "
					+ ContractDataColumns.END_DATE + " >= ? ) " + " AND "
					+ ContractDataColumns.CONTRACT + " IN ( " + idsSqlBuffer
					+ " ) " + " AND " + ContractDataColumns.NAME + " IN ("
					+ namesHosts + ")" + " ORDER BY "
					+ ContractDataColumns.CONTRACT + " ASC ";

			stmt = connection.prepareStatement(sql);

			stmt.setDate(1, sqlEndDate);
			stmt.setDate(2, sqlStartDate);
			for (int i = 0; i < employeesIds.size(); i++)
				stmt.setInt(i + 3, employeesIds.get(i));

			for (int i = 0; i < names.length; i++)
				stmt.setString(i + 3 + employeesIds.size(), names[i]);

			rs = stmt.executeQuery();

			int lastEmployeeId = Integer.MIN_VALUE;

			ExpressionContext employeeExpressionContext = null;

			while (rs.next()) {
				int employeeId = rs.getInt(ContractDataColumns.CONTRACT);
				if (lastEmployeeId != employeeId) {
					employeeExpressionContext = new ExpressionContext(
							systemExpressionContext);
					if (lastEmployeeId != Integer.MIN_VALUE) {
						loadSystemEvents(systemEvents,
								employeeExpressionContext, events,
								lastEmployeeId);
					}
				}
				Event event = null;
				String name = rs.getString(SystemDataColumns.NAME);
				Date end = rs.getDate(SystemDataColumns.END_DATE);
				Date start = rs.getDate(SystemDataColumns.START_DATE);
				String script = rs.getString(SystemDataColumns.EXPRESSION);

				try {
					ExpressionImpl expression = new ExpressionImpl();
					expression.setName(name);
					expression.setExpression(script);
					List<ITimedResult<Object>> results = employeeExpressionContext
							.addExpression(expression, start, end);

					for (ITimedResult<Object> result : results) {
						event = new Event();
						event.setName(name);

						if (result.getValue() == null)
							event.setValue(null);
						/*
						 * else if (result.getValue() instanceof String)
						 * event.setValue(String.format("\"%s\"",
						 * result.getValue()));
						 */
						else
							event.setValue(result.getValue().toString());

						event.setEndDate(result.getPeriod().getEnd());
						event.setStartDate(result.getPeriod().getStart());
						events.addEvent(employeeId, event);
					}
				} catch (ExpressionException e) {
					event = new Event();
					event.setName(name);
					event.setValue(script);
					event.setEndDate(start);
					event.setStartDate(end);
					events.addEvent(employeeId, event);
				}
				lastEmployeeId = employeeId;
			}
			if (lastEmployeeId != Integer.MIN_VALUE) {
				loadSystemEvents(systemEvents, employeeExpressionContext,
						events, lastEmployeeId);
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
		Set<String> names = events.getEventNames(-1);
		if (names.size() == 0)
			return;

		int offset = events.getEmployeeCount();
		int workplaceId = events.getWorkplaceId();

		Events remain = getEvents(connection, workplaceId, startDate, endDate,
				offset, Integer.MAX_VALUE,
				names.toArray(new String[names.size()]));

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

	public static void saveEventsAux(Connection connection, Events events,
			Date startDate, Date endDate, int domainId) throws SQLException {

		DSLContext dslContext = DSL.using(connection, getDefaultSettings());

		connection.setAutoCommit(false);

		try {

			for (Integer employeeId : events.getEmployeeIds()) {
				for (String eventName : events.getEventNames(employeeId)) {

					List<Event> employeeEvents = events.getFinalEvents(
							employeeId, eventName);
					if (employeeEvents.size() == 0)
						continue;

					// Delete old events ...
					dslContext
							.delete(CONTRACT_DATA)
							.where(CONTRACT_DATA.CONTRACT
									.eq(employeeId)
									.and(CONTRACT_DATA.NAME.eq(eventName))
									.and(CONTRACT_DATA.START_DATE
											.lessOrEqual(SQLUtils
													.date2sql(endDate)))
									.and(CONTRACT_DATA.END_DATE
											.isNull()
											.or(CONTRACT_DATA.END_DATE.greaterOrEqual(SQLUtils
													.date2sql(startDate)))))
							.execute();

					for (Event event : employeeEvents) {

						if (event.getValue() == null)
							continue;

						dslContext
								.insertInto(CONTRACT_DATA)
								.set(CONTRACT_DATA.DOMAIN, domainId)
								.set(CONTRACT_DATA.CONTRACT, employeeId)
								.set(CONTRACT_DATA.NAME, event.getName())
								.set(CONTRACT_DATA.EXPRESSION, event.getValue())
								.set(CONTRACT_DATA.START_DATE,
										SQLUtils.date2sql(event.getStartDate()))
								.set(CONTRACT_DATA.END_DATE,
										SQLUtils.date2sql(event.getEndDate()))
								.execute();
					}
				}
			}

			connection.commit();

		} catch (RuntimeException ex) {
			connection.rollback();
			throw ex;

		} catch (Exception ex) {
			connection.rollback();
			throw ex;

		} finally {
			connection.setAutoCommit(true);
		}
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

	public static Period getAvailPeriodAux(Connection conn,
			Integer workplaceId, String name) throws SQLException {

		DSLContext dslContext = DSL.using(conn, getDefaultSettings());
		Record2<java.sql.Date, java.sql.Date> result = null;

		try {

			result = dslContext
					.select(CONTRACT_DATA.START_DATE.min(),
							CONTRACT_DATA.END_DATE.max())
					.from(CONTRACT_DATA.rightOuterJoin(CONTRACT).on(
							CONTRACT_DATA.CONTRACT.eq(CONTRACT.ID)))
					.where(CONTRACT.WORKPLACE.eq(workplaceId).and(
							CONTRACT_DATA.NAME.eq(name))).fetchOne();

			if (result == null)
				return null;

			if (result.size() == 0)
				return null;

			Date startDate = result.getValue(CONTRACT_DATA.START_DATE);
			Date endDate = (result.getValue(CONTRACT_DATA.END_DATE) != null) ? result
					.getValue(CONTRACT_DATA.END_DATE) : MAX_DATE;

			if (startDate == null)
				return null;

			return new Period(startDate, endDate.equals(MAX_DATE) ? null
					: endDate);

		} finally {

		}

	}

	public static Period getAvailPeriod(Connection conn, Integer workplaceId,
			String name) throws SQLException {
		ResultSet rs = null;
		PreparedStatement stmt = null;
		try {
			String sql = "SELECT " + "MIN( " + SQLConstants.CONTRACT_DATA + "."
					+ ContractDataColumns.START_DATE + ")"
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

			if (startDate == null)
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

	public static Set<Payment> getPaymentsAux(Connection connection,
			int workplacetId, Date startDate, Date endDate) throws SQLException {

		DSLContext dslContext = DSL.using(connection, getDefaultSettings());

		Cursor<Record> cursor = null;

		try {

			java.sql.Date sqlEndDate = SQLUtils.date2sql(endDate);
			java.sql.Date sqlStartDate = SQLUtils.date2sql(startDate);

			SelectConditionStep<Record> select = dslContext
					.select()
					.from(CONTRACT
							.leftOuterJoin(CONTRACT_PAYMENT)
							.on(CONTRACT.ID.eq(CONTRACT_PAYMENT.CONTRACT))
							.leftOuterJoin(PAYMENT_CONCEPT)
							.on(CONTRACT_PAYMENT.PAYMENT_CONCEPT
									.eq(PAYMENT_CONCEPT.ID)))
					.where(CONTRACT.WORKPLACE
							.eq(workplacetId)
							.and(CONTRACT.END_DATE.isNull().or(
									CONTRACT.END_DATE
											.greaterOrEqual(sqlStartDate)))
							.and(CONTRACT.START_DATE.lessOrEqual(sqlEndDate))
							.and(CONTRACT_PAYMENT.END_DATE.isNull().or(
									CONTRACT_PAYMENT.END_DATE
											.greaterOrEqual(sqlStartDate)))
							.and(CONTRACT_PAYMENT.START_DATE
									.lessOrEqual(sqlEndDate)));

			cursor = select.fetchLazy();

			Set<Payment> payments = new HashSet<Payment>();

			for (Record record : cursor) {

				Payment payment = new Payment();
				payment.setStartDate(sqlStartDate);
				payment.setEndDate(sqlEndDate);
				payment.setId(record.getValue(CONTRACT_PAYMENT.ID));

				// bellow payment's properties may be inherit from concept

				payment.setExpression(getPaymentAux(String.class,
						record.getValue(CONTRACT_PAYMENT.EXPRESSION),
						record.getValue(PAYMENT_CONCEPT.EXPRESSION)));

				payment.setIrpfExpression(getPaymentAux(String.class,
						record.getValue(CONTRACT_PAYMENT.IRPF_EXPRESSION),
						record.getValue(PAYMENT_CONCEPT.IRPF_EXPRESSION)));

				payment.setQuoteExpression(getPaymentAux(String.class,
						record.getValue(CONTRACT_PAYMENT.QUOTE_EXPRESSION),
						record.getValue(PAYMENT_CONCEPT.QUOTE_EXPRESSION)));

				payment.setDescription(getPaymentAux(String.class,
						record.getValue(CONTRACT_PAYMENT.DESCRIPTION),
						record.getValue(PAYMENT_CONCEPT.DESCRIPTION)));

				payment.setIrpfExpression(getPaymentAux(String.class,
						record.getValue(CONTRACT_PAYMENT.IRPF_EXPRESSION),
						record.getValue(PAYMENT_CONCEPT.IRPF_EXPRESSION)));

				Object typePayment = getPaymentAux(Object.class,
						record.getValue(CONTRACT_PAYMENT.TYPE),
						record.getValue(PAYMENT_CONCEPT.TYPE));
				payment.setType(getType(typePayment, Payment.Type.class));

				// 'month' & 'salary' only at contract's payment....
				Integer month = getPaymentAux(Integer.class,
						CONTRACT_PAYMENT.MONTH);
				payment.setMonth((month != null) ? month.shortValue() : null);

				Object salaryType = record
						.getValue(CONTRACT_PAYMENT.SALARY_TYPE);
				payment.setSalaryType(getType(salaryType, Salary.Type.class));

				// 'name' it's the code of concept.
				payment.setName(record.getValue(PAYMENT_CONCEPT.CODE));

				payments.add(payment);
			}

			return payments;

		} finally {
			if (cursor != null)
				cursor.close();
		}

	}

	public static Set<Payment> getPayments(Connection connection,
			int workplacetId, Date startDate, Date endDate) throws SQLException {
		ResultSet rs = null;
		PreparedStatement stmt = null;
		try {

			java.sql.Date sqlEndDate = SQLUtils.date2sql(endDate);
			java.sql.Date sqlStartDate = SQLUtils.date2sql(startDate);

			stmt = connection.prepareStatement("SELECT * " + " FROM "
					+ SQLConstants.CONTRACT + " LEFT JOIN "
					+ SQLConstants.CONTRACT_PAYMENT + " ON ( "
					+ SQLConstants.CONTRACT + "." + ContractColumns.ID + "= "
					+ SQLConstants.CONTRACT_PAYMENT + "."
					+ ContractPaymentColumns.CONTRACT + ")" + " LEFT JOIN "
					+ SQLConstants.PAYMENT_CONCEPT + " ON ( "
					+ SQLConstants.CONTRACT_PAYMENT + "."
					+ ContractPaymentColumns.PAYMENT_CONCEPT + " = "
					+ SQLConstants.PAYMENT_CONCEPT + "."
					+ PaymentConceptColumns.ID + ")" + " WHERE "
					+ SQLConstants.CONTRACT + "." + ContractColumns.WORKPLACE
					+ " = ? " + " AND ( " + SQLConstants.CONTRACT + "."
					+ ContractColumns.END_DATE + " IS NULL " + " OR "
					+ SQLConstants.CONTRACT + "." + ContractColumns.END_DATE
					+ " >= ?  ) " + " AND " + SQLConstants.CONTRACT + "."
					+ ContractColumns.START_DATE + " <= ? " + " AND ( "
					+ SQLConstants.CONTRACT_PAYMENT + "."
					+ ContractPaymentColumns.END_DATE + " IS NULL " + " OR "
					+ SQLConstants.CONTRACT_PAYMENT + "."
					+ ContractPaymentColumns.END_DATE + " >= ?  ) " + " AND "
					+ SQLConstants.CONTRACT_PAYMENT + "."
					+ ContractPaymentColumns.START_DATE + " <= ? ");

			stmt.setInt(1, workplacetId);
			stmt.setDate(2, sqlStartDate);
			stmt.setDate(3, sqlEndDate);
			stmt.setDate(4, sqlStartDate);
			stmt.setDate(5, sqlEndDate);

			rs = stmt.executeQuery();

			Set<Payment> payments = new HashSet<Payment>();

			while (rs.next()) {
				Payment payment = new Payment();

				payment.setStartDate(sqlStartDate);
				payment.setEndDate(sqlEndDate);

				payment.setId(rs.getInt(SQLConstants.CONTRACT_PAYMENT + "."
						+ ContractPaymentColumns.ID));

				// bellow payment's properties may be inherit from concept
				payment.setExpression(getPayment(rs,
						ContractPaymentColumns.EXPRESSION,
						PaymentConceptColumns.EXPRESSION, String.class));
				payment.setIrpfExpression(getPayment(rs,
						ContractPaymentColumns.IRPF_EXPRESSION,
						PaymentConceptColumns.IRPF_EXPRESSION, String.class));
				payment.setQuoteExpression(getPayment(rs,
						ContractPaymentColumns.QUOTE_EXPRESSION,
						PaymentConceptColumns.QUOTE_EXPRESSION, String.class));
				payment.setDescription(getPayment(rs,
						ContractPaymentColumns.DESCRIPTION,
						PaymentConceptColumns.DESCRIPTION, String.class));
				Object paymentType = getPayment(rs,
						ContractPaymentColumns.TYPE,
						PaymentConceptColumns.TYPE, Object.class);
				payment.setType(getType(paymentType, Payment.Type.class));

				// 'month' & 'salary' only at contract's payment....
				Integer month = getInteger(rs, ContractPaymentColumns.MONTH);
				payment.setMonth(month != null ? month.shortValue() : null);

				Object salaryType = rs.getObject(SQLConstants.CONTRACT_PAYMENT
						+ "." + ContractPaymentColumns.SALARY_TYPE);
				payment.setSalaryType(getType(salaryType, Salary.Type.class));

				// 'name' it's the code of concept.
				payment.setName(rs.getString(SQLConstants.PAYMENT_CONCEPT + "."
						+ PaymentConceptColumns.CODE));

				payments.add(payment);
			}

			return payments;

		} finally {
			if (rs != null)
				rs.close();
			if (stmt != null)
				stmt.close();
		}

	}
	
	public static Integer getAgreementId(Connection connection, Integer employeeId) throws SQLException {
		Integer agreementId = null;
		DSLContext dslContext = DSL.using(connection, getDefaultSettings());
		try {
			
			Record1<Integer> result = dslContext.select(AGREEMENT_LEVEL.AGREEMENT)
			.from(AGREEMENT_LEVEL)
			.leftJoin(AGREEMENT_LEVEL_CATEGORY)
			.on(AGREEMENT_LEVEL.ID.eq(AGREEMENT_LEVEL_CATEGORY.AGREEMENT_LEVEL))
			.leftJoin(CONTRACT)
			.on(AGREEMENT_LEVEL.ID.eq(CONTRACT.AGREEMENT_LEVEL))
			.where(CONTRACT.ID.eq(employeeId)).fetchOne();
			
			if(null != result)
				agreementId = result.value1();
			
			return agreementId;

		} finally {
		}
	}

	
	public static Set<Payment> getEmployeePayments(Connection connection,
			int employeeId, Date startDate, Date endDate) throws SQLException {
		ResultSet rs = null;
		PreparedStatement stmt = null;
		try {

			java.sql.Date sqlEndDate = SQLUtils.date2sql(endDate);
			java.sql.Date sqlStartDate = SQLUtils.date2sql(startDate);

			String sql = ("SELECT * " + " FROM "
					+ SQLConstants.CONTRACT + " LEFT JOIN "
					+ SQLConstants.CONTRACT_PAYMENT + " ON ( "
					+ SQLConstants.CONTRACT + "." + ContractColumns.ID + "= "
					+ SQLConstants.CONTRACT_PAYMENT + "."
					+ ContractPaymentColumns.CONTRACT + ")" + " LEFT JOIN "
					+ SQLConstants.PAYMENT_CONCEPT + " ON ( "
					+ SQLConstants.CONTRACT_PAYMENT + "."
					+ ContractPaymentColumns.PAYMENT_CONCEPT + " = "
					+ SQLConstants.PAYMENT_CONCEPT + "."
					+ PaymentConceptColumns.ID + ")" + " WHERE "
					+ SQLConstants.CONTRACT + "." + ContractColumns.ID
					+ " = ? " + " AND ( " + SQLConstants.CONTRACT + "."
					+ ContractColumns.END_DATE + " IS NULL " + " OR "
					+ SQLConstants.CONTRACT + "." + ContractColumns.END_DATE
					+ " >= ?  ) " + " AND " + SQLConstants.CONTRACT + "."
					+ ContractColumns.START_DATE + " <= ? " + " AND ( "
					+ SQLConstants.CONTRACT_PAYMENT + "."
					+ ContractPaymentColumns.END_DATE + " IS NULL " + " OR "
					+ SQLConstants.CONTRACT_PAYMENT + "."
					+ ContractPaymentColumns.END_DATE + " >= ?  ) " + " AND "
					+ SQLConstants.CONTRACT_PAYMENT + "."
					+ ContractPaymentColumns.START_DATE + " <= ? ");
			
			stmt = connection.prepareStatement(sql);

			stmt.setInt(1, employeeId);
			stmt.setDate(2, sqlStartDate);
			stmt.setDate(3, sqlEndDate);
			stmt.setDate(4, sqlStartDate);
			stmt.setDate(5, sqlEndDate);

			rs = stmt.executeQuery();

			Set<Payment> payments = new HashSet<Payment>();

			while (rs.next()) {
				Payment payment = new Payment();

				payment.setStartDate(sqlStartDate);
				payment.setEndDate(sqlEndDate);

				payment.setId(rs.getInt(SQLConstants.CONTRACT_PAYMENT + "."
						+ ContractPaymentColumns.ID));

				// bellow payment's properties may be inherit from concept
				payment.setExpression(getPayment(rs,
						ContractPaymentColumns.EXPRESSION,
						PaymentConceptColumns.EXPRESSION, String.class));
				payment.setIrpfExpression(getPayment(rs,
						ContractPaymentColumns.IRPF_EXPRESSION,
						PaymentConceptColumns.IRPF_EXPRESSION, String.class));
				payment.setQuoteExpression(getPayment(rs,
						ContractPaymentColumns.QUOTE_EXPRESSION,
						PaymentConceptColumns.QUOTE_EXPRESSION, String.class));
				payment.setDescription(getPayment(rs,
						ContractPaymentColumns.DESCRIPTION,
						PaymentConceptColumns.DESCRIPTION, String.class));
				Object paymentType = getPayment(rs,
						ContractPaymentColumns.TYPE,
						PaymentConceptColumns.TYPE, Object.class);
				payment.setType(getType(paymentType, Payment.Type.class));

				// 'month' & 'salary' only at contract's payment....
				Integer month = getInteger(rs, ContractPaymentColumns.MONTH);
				payment.setMonth(month != null ? month.shortValue() : null);

				Object salaryType = rs.getObject(SQLConstants.CONTRACT_PAYMENT
						+ "." + ContractPaymentColumns.SALARY_TYPE);
				payment.setSalaryType(getType(salaryType, Salary.Type.class));

				// 'name' it's the code of concept.
				payment.setName(rs.getString(SQLConstants.PAYMENT_CONCEPT + "."
						+ PaymentConceptColumns.CODE));

				payments.add(payment);
			}

			return payments;

		}catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		 finally {
			if (rs != null)
				rs.close();
			if (stmt != null)
				stmt.close();
		}

	}

	// -------------------------------------------------------------------------
	//
	// -------------------------------------------------------------------------

	private static <T> T getPaymentAux(Class<T> toType, Object... values) {
		return JooqUtils.get(toType, values);
	}

	private static <T> T getPayment(ResultSet rs, String paymentColumn,
			String conceptColumn, Class<T> toType) throws SQLException {
		return SQLUtils.get(rs, toType, SQLConstants.CONTRACT_PAYMENT + "."
				+ paymentColumn, SQLConstants.PAYMENT_CONCEPT + "."
				+ conceptColumn);
	}

	private static void loadSystemEvents(List<Event> systemEvents,
			ExpressionContext expressionContext, Events events, int employeeId) {
		for (Event systemEvent : systemEvents) {
			try {

				ExpressionImpl expression = new ExpressionImpl();
				expression.setName(systemEvent.getName());
				expression.setExpression(systemEvent.getValue());

				List<ITimedResult<Object>> results = expressionContext
						.addExpression(expression, systemEvent.getStartDate(),
								systemEvent.getEndDate());

				for (ITimedResult<Object> result : results) {
					Event event = new Event();
					event.setName(systemEvent.getName());
					event.setValue(result.getValue() != null ? result
							.getValue().toString() : null);
					event.setEndDate(result.getPeriod().getEnd());
					event.setStartDate(result.getPeriod().getStart());
					events.addEvent(employeeId, event);
				}
			} catch (ExpressionException ignoreOrLog) {
			}

		}

	}

	protected static Settings getDefaultSettings() {
		if (SETTINGS == null) {
			SETTINGS = new Settings();
			SETTINGS.setRenderSchema(false);
		}
		return SETTINGS;
	}
	

}
