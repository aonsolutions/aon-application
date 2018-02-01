package com.esferalia.aon.gwt.payroll.sql;

import static com.esferalia.aon.gwt.payroll.shared.AgreementDraft.isRemove;
import static com.esferalia.aon.gwt.payroll.sql.SQLUtils.getInteger;
import static com.esferalia.aon.gwt.payroll.sql.SQLUtils.getType;
import static com.esferalia.aon.jooq.tables.AgreementPayment.AGREEMENT_PAYMENT;
import static com.esferalia.aon.jooq.tables.PaymentConcept.PAYMENT_CONCEPT;
import static com.esferalia.aon.jooq.tables.AgreementData.AGREEMENT_DATA;
import static com.esferalia.aon.jooq.tables.AgreementLevel.AGREEMENT_LEVEL;
import static com.esferalia.aon.jooq.tables.AgreementLevelData.AGREEMENT_LEVEL_DATA;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.jooq.Cursor;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Result;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.gwt.payroll.jooq.JooqAgreement;
import com.esferalia.aon.gwt.payroll.jooq.JooqUtils;
import com.esferalia.aon.gwt.payroll.shared.AgreementDraft;
import com.esferalia.aon.gwt.payroll.shared.AgreementDraft.Level;
import com.esferalia.aon.gwt.payroll.shared.AgreementDraft.SalaryTable;
import com.esferalia.aon.gwt.payroll.shared.Extra;
import com.esferalia.aon.gwt.payroll.shared.Payment;
import com.esferalia.aon.gwt.payroll.shared.Salary;
import com.esferalia.aon.gwt.payroll.shared.SalaryDraft.Scope;
import com.esferalia.aon.gwt.payroll.shared.StringVariable;
import com.esferalia.aon.gwt.payroll.shared.Variable;
import com.esferalia.aon.jooq.tables.AgreementData;
import com.esferalia.aon.jooq.tables.PaymentConcept;
import com.esferalia.aon.payroll.sql.SQLConstants;
import com.esferalia.aon.payroll.sql.SQLConstants.AgreementColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.AgreementDataColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.AgreementExtraColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.AgreementLevelCategoryColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.AgreementLevelColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.AgreementLevelDataColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.AgreementPaymentColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.PaymentConceptColumns;
import com.esferalia.aon.salary.expression.Period;
import com.google.gwt.user.client.Window;

public class SQLAgreementDraft {

	private static Settings SETTINGS = null;

	@SuppressWarnings("serial")
	private static class DBVariable extends StringVariable {
		private Integer id;

		public Integer getId() {
			return id;
		}

		public void setId(Integer id) {
			this.id = id;
		}

	}
	
	public static ArrayList<String> getEraseAgreement(Connection connection, Integer agreementId, Date startDate,
			Date endDate) throws SQLException {
		
		ArrayList<String> eraseList = new ArrayList<>();
		DSLContext dslContext = DSL.using(connection, getDefaultSettings());
		
		Result<Record1<String>> recordData = dslContext.select(AGREEMENT_DATA.NAME)
					.from(AGREEMENT_DATA)
					.where(AGREEMENT_DATA.AGREEMENT.eq(agreementId))
					.fetch();
		
		for (Record1<String> r : recordData){
			eraseList.add(r.get(AGREEMENT_DATA.NAME));
		}
		
		//SELECT name FROM agreement_level_data WHERE agreement_level IN (SELECT id FROM agreement_level WHERE agreement=1156)
		
		Result<Record1<String>> recordLevel = dslContext.select(AGREEMENT_LEVEL_DATA.NAME)
				.from(AGREEMENT_LEVEL_DATA)
				.where(AGREEMENT_LEVEL_DATA.AGREEMENT_LEVEL
						.in(dslContext.select(AGREEMENT_LEVEL.ID)
								.from(AGREEMENT_LEVEL)
								.where(AGREEMENT_LEVEL.AGREEMENT.eq(agreementId))))
				.fetch();
	
		for (Record1<String> r : recordLevel){
			eraseList.add(r.get(AGREEMENT_LEVEL_DATA.NAME));
		}
		
		return eraseList;
	}

	public static Set<Payment> getPaymentsAux(Connection connection,
			int agreementId, Date startDate, Date endDate, Integer... domains)
					throws SQLException {

		Cursor<Record> cursor = null;
		DSLContext dslContext = DSL.using(connection, getDefaultSettings());

		try {

			Collection<Integer> domainList = new ArrayList<Integer>(
					domains.length);
			for (Integer domain : domains) {
				if (domain != null)
					domainList.add(domain);
			}

			java.sql.Date sqlEndDate = SQLUtils.date2sql(endDate);
			java.sql.Date sqlStartDate = SQLUtils.date2sql(startDate);

			cursor = dslContext.select()
					.from(AGREEMENT_PAYMENT.leftOuterJoin(PAYMENT_CONCEPT)
							.on(AGREEMENT_PAYMENT.PAYMENT_CONCEPT
									.eq(PAYMENT_CONCEPT.ID)))
					.where(AGREEMENT_PAYMENT.AGREEMENT.eq(agreementId)
							.and(AGREEMENT_PAYMENT.END_DATE.isNull()
									.or(AGREEMENT_PAYMENT.END_DATE
											.greaterOrEqual(sqlStartDate)))
							.and(AGREEMENT_PAYMENT.START_DATE
									.lessOrEqual(sqlEndDate))
							//.and(AGREEMENT_PAYMENT.DOMAIN.in(domainList))
							)
					.fetchLazy();

			Set<Payment> payments = new HashSet<Payment>();

			for (Record record : cursor) {

				Payment payment = new Payment();

				payment.setStartDate(sqlStartDate);
				payment.setEndDate(sqlEndDate);

				payment.setId(record.getValue(AGREEMENT_PAYMENT.ID));
				payment.setDomain(record.getValue(AGREEMENT_PAYMENT.DOMAIN));

				payment.setExpression(
						getAux(String.class, record.getValue(AGREEMENT_PAYMENT.EXPRESSION),
								record.getValue(PAYMENT_CONCEPT.EXPRESSION)));
				payment.setIrpfExpression(
						getAux(String.class, record.getValue(AGREEMENT_PAYMENT.IRPF_EXPRESSION),
								record.getValue(PAYMENT_CONCEPT.IRPF_EXPRESSION)));
				payment.setQuoteExpression(
						getAux(String.class, record.getValue(AGREEMENT_PAYMENT.QUOTE_EXPRESSION),
								record.getValue(PAYMENT_CONCEPT.QUOTE_EXPRESSION)));
				payment.setDescription(
						getAux(String.class, record.getValue(AGREEMENT_PAYMENT.DESCRIPTION),
								record.getValue(PAYMENT_CONCEPT.DESCRIPTION)));

				Object paymentType = getAux(Object.class,
						record.getValue(AGREEMENT_PAYMENT.TYPE), record.getValue(PAYMENT_CONCEPT.TYPE));
				payment.setType(getType(paymentType, Payment.Type.class));

				Byte month = getAux(Byte.class, record.getValue(AGREEMENT_PAYMENT.MONTH));
				payment.setMonth(month != null ? month.shortValue() : null);

				Object salaryType = record
						.getValue(AGREEMENT_PAYMENT.SALARY_TYPE);
				payment.setSalaryType(getType(salaryType, Salary.Type.class));

				payment.setName(record.getValue(PAYMENT_CONCEPT.CODE));

				payment.setConceptId(getAux(Integer.class, record.getValue(PAYMENT_CONCEPT.ID)));

				payments.add(payment);
			}

			return payments;

		}catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (cursor != null)
				cursor.close();
		}
	}

	public static Set<Payment> getPayments(Connection connection,
			int agreementId, Date startDate, Date endDate, Integer... domains)
					throws SQLException {
		ResultSet rs = null;
		PreparedStatement stmt = null;
		try {

			List<Integer> domainList = new ArrayList<Integer>(domains.length);
			for (Integer domain : domains) {
				if (domain != null) {
					domainList.add(domain);
				}
			}

			java.sql.Date sqlEndDate = SQLUtils.date2sql(endDate);
			java.sql.Date sqlStartDate = SQLUtils.date2sql(startDate);

			stmt = connection.prepareStatement("SELECT * " + " FROM "
					+ SQLConstants.AGREEMENT_PAYMENT + " LEFT JOIN "
					+ SQLConstants.PAYMENT_CONCEPT + " ON ( "
					+ SQLConstants.AGREEMENT_PAYMENT + "."
					+ AgreementPaymentColumns.PAYMENT_CONCEPT + " = "
					+ SQLConstants.PAYMENT_CONCEPT + "."
					+ PaymentConceptColumns.ID + ")" + " WHERE "
					+ SQLConstants.AGREEMENT_PAYMENT + "."
					+ AgreementPaymentColumns.AGREEMENT + " = ? " + " AND ( "
					+ SQLConstants.AGREEMENT_PAYMENT + "."
					+ AgreementPaymentColumns.END_DATE + " IS NULL " + " OR "
					+ SQLConstants.AGREEMENT_PAYMENT + "."
					+ AgreementPaymentColumns.END_DATE + " >= ?  ) " + " AND "
					+ SQLConstants.AGREEMENT_PAYMENT + "."
					+ AgreementPaymentColumns.START_DATE + " <= ? " + " AND "
					+ SQLConstants.AGREEMENT_PAYMENT + "."
					+ AgreementColumns.DOMAIN + " IN ("
					+ StringUtils.repeat("?", ",", domainList.size()) + ")"
					+ " ORDER BY "+ 
					SQLConstants.AGREEMENT_PAYMENT + "." 
					+AgreementPaymentColumns.DOMAIN
					+ " DESC ");

			int i = 1;
			stmt.setInt(i++, agreementId);
			stmt.setDate(i++, sqlStartDate);
			stmt.setDate(i++, sqlEndDate);
			for (Integer domain : domainList)
				stmt.setInt(i++, domain);

			rs = stmt.executeQuery();

			Set<Payment> payments = new TreeSet<Payment>((p1,p2)-> { 
				int compare = p2.getDomain().compareTo(p1.getDomain());
				return compare != 0 ? compare : p1.getId().compareTo(p2.getId());
				});

			while (rs.next()) {
				Payment payment = new Payment();

				payment.setStartDate(sqlStartDate);
				payment.setEndDate(sqlEndDate);

				payment.setId(rs.getInt(SQLConstants.AGREEMENT_PAYMENT + "."
						+ AgreementPaymentColumns.ID));

				payment.setDomain(rs.getInt(SQLConstants.AGREEMENT_PAYMENT + "."
						+ AgreementPaymentColumns.DOMAIN));

				// bellow payment's properties may be inherit from concept
				payment.setExpression(get(rs,
						AgreementPaymentColumns.EXPRESSION,
						PaymentConceptColumns.EXPRESSION, String.class));
				payment.setIrpfExpression(get(rs,
						AgreementPaymentColumns.IRPF_EXPRESSION,
						PaymentConceptColumns.IRPF_EXPRESSION, String.class));
				payment.setQuoteExpression(get(rs,
						AgreementPaymentColumns.QUOTE_EXPRESSION,
						PaymentConceptColumns.QUOTE_EXPRESSION, String.class));
				payment.setDescription(get(rs,
						AgreementPaymentColumns.DESCRIPTION,
						PaymentConceptColumns.DESCRIPTION, String.class));
				Object paymentType = get(rs, AgreementPaymentColumns.TYPE,
						PaymentConceptColumns.TYPE, Object.class);
				payment.setType(getType(paymentType, Payment.Type.class));

				// 'month' & 'salary' only at agreement's payment....
				Integer month = getInteger(rs, AgreementPaymentColumns.MONTH);
				payment.setMonth(month != null ? month.shortValue() : null);

				Object salaryType = rs.getObject(SQLConstants.AGREEMENT_PAYMENT
						+ "." + AgreementPaymentColumns.SALARY_TYPE);
				payment.setSalaryType(getType(salaryType, Salary.Type.class));

				// 'name' it's the code of concept.
				payment.setName(rs.getString(SQLConstants.PAYMENT_CONCEPT + "."
						+ PaymentConceptColumns.CODE));

				payment.setConceptId(getInteger(rs, SQLConstants.PAYMENT_CONCEPT
						+ "." + PaymentConceptColumns.ID));
				
				payment.setConcept(
						rs.getInt(SQLConstants.PAYMENT_CONCEPT + "." + PaymentConceptColumns.ID),
						getInteger(rs, SQLConstants.PAYMENT_CONCEPT + "." + PaymentConceptColumns.DOMAIN),
						rs.getString(SQLConstants.PAYMENT_CONCEPT + "."+ PaymentConceptColumns.CODE),
						rs.getString(SQLConstants.PAYMENT_CONCEPT + "."+ PaymentConceptColumns.DESCRIPTION),
						rs.getString(SQLConstants.PAYMENT_CONCEPT + "." + PaymentConceptColumns.TYPE),
						rs.getString(SQLConstants.PAYMENT_CONCEPT + "." + PaymentConceptColumns.DESCRIPTION_DECORABLE),
						rs.getString(SQLConstants.PAYMENT_CONCEPT + "."+ PaymentConceptColumns.EXPRESSION),
						rs.getString(SQLConstants.PAYMENT_CONCEPT + "."+ PaymentConceptColumns.IRPF_EXPRESSION),
						rs.getString(SQLConstants.PAYMENT_CONCEPT + "."+ PaymentConceptColumns.QUOTE_EXPRESSION)
				);

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

	public static SalaryTable getSalaryTable(Connection connection,
			int agreementId, Date startDate, Date endDate, Integer... domains) throws SQLException {
		ResultSet rs = null;
		PreparedStatement stmt = null;
		try {
			
			String domainsParams = Arrays.asList(domains)
					.stream()
					.filter(domain-> domain != null)
					.map(domain->"?")
					.collect(Collectors.joining(","));

			java.sql.Date sqlEndDate = SQLUtils.date2sql(endDate);
			java.sql.Date sqlStartDate = SQLUtils.date2sql(startDate);

			// First of all, agreement data. These are inherited by all levels.
			stmt = connection.prepareStatement("SELECT * " + " FROM "
					+ SQLConstants.AGREEMENT_DATA + " WHERE "
					+ AgreementDataColumns.AGREEMENT + " = ? " + " AND ( "
					+ AgreementDataColumns.END_DATE + " IS NULL " + " OR "
					+ AgreementDataColumns.END_DATE + " >= ?  ) " + " AND "
					+ AgreementDataColumns.START_DATE + " <= ? " + " AND " 
					+ AgreementDataColumns.DOMAIN + " IN (" + domainsParams + ")"
					);

			stmt.setInt(1, agreementId);
			stmt.setDate(2, sqlStartDate);
			stmt.setDate(3, sqlEndDate);
			for ( int i = 0; i < domains.length; i++)
				if ( domains[i] != null )
					stmt.setInt(4+i, domains[i]);
				

			rs = stmt.executeQuery();

			SalaryTable salaryTable = new SalaryTable();

			while (rs.next()) {
				Variable var = new StringVariable();
				var.setScope(Scope.AGREEMENT);
				var.setStartDate(sqlStartDate);
				var.setEndDate(sqlEndDate);
				var.setName(rs.getString(AgreementDataColumns.NAME));
				var.setExpression(
						rs.getString(AgreementDataColumns.EXPRESSION));
				salaryTable.put(0, var);
			}

			rs.close();
			stmt.close();

			// Second, level speficic data.
			stmt = connection.prepareStatement("SELECT * " + " FROM "
					+ SQLConstants.AGREEMENT_LEVEL + " ,"
					+ SQLConstants.AGREEMENT_LEVEL_DATA + " WHERE "
					+ SQLConstants.AGREEMENT_LEVEL + "."
					+ AgreementLevelColumns.AGREEMENT + "= ? " + " AND ("
					+ SQLConstants.AGREEMENT_LEVEL + "."
					+ AgreementLevelColumns.ID + " =  "
					+ SQLConstants.AGREEMENT_LEVEL_DATA + "."
					+ AgreementLevelDataColumns.AGREEMENT_LEVEL + ")"
					+ " AND ( " + SQLConstants.AGREEMENT_LEVEL_DATA + "."
					+ AgreementLevelDataColumns.END_DATE + " IS NULL " + " OR "
					+ SQLConstants.AGREEMENT_LEVEL_DATA + "."
					+ AgreementLevelDataColumns.END_DATE + " >= ?  ) " + " AND "
					+ SQLConstants.AGREEMENT_LEVEL_DATA + "."
					+ AgreementLevelDataColumns.START_DATE + " <= ? "  + " AND "
					+ SQLConstants.AGREEMENT_LEVEL_DATA + "."
					+ AgreementLevelDataColumns.DOMAIN + " IN (" + domainsParams + ")"
					);

			stmt.setInt(1, agreementId);
			stmt.setDate(2, sqlStartDate);
			stmt.setDate(3, sqlEndDate);
			for ( int i = 0; i < domains.length; i++)
				if ( domains[i] != null )
					stmt.setInt(4+i, domains[i]);

			rs = stmt.executeQuery();

			while (rs.next()) {
				Variable var = new StringVariable();
				var.setScope(Scope.AGREEMENT);
				var.setStartDate(sqlStartDate);
				var.setEndDate(sqlEndDate);
				var.setName(rs.getString(AgreementLevelDataColumns.NAME));
				var.setExpression(
						rs.getString(AgreementLevelDataColumns.EXPRESSION));
				salaryTable.put(
						rs.getInt(AgreementLevelDataColumns.AGREEMENT_LEVEL),
						var);
			}

			return salaryTable;

		} finally {
			if (rs != null)
				rs.close();
			if (stmt != null)
				stmt.close();
		}

	}

	public static Set<Level> getLevels(Connection connection, int agreementId, Integer ...domains)
			throws SQLException {
		ResultSet rs = null;
		PreparedStatement stmt = null;
		try {

			String domainsParams = Arrays.asList(domains)
					.stream()
					.filter(domain-> domain != null)
					.map(domain->"?")
					.collect(Collectors.joining(","));
			// @formatter:off
			stmt = connection.prepareStatement(
					"SELECT * " 
					+ " FROM " + SQLConstants.AGREEMENT_LEVEL 
					+ " WHERE " + AgreementLevelColumns.AGREEMENT + " = ?  AND "
					+ SQLConstants.AGREEMENT_LEVEL + "."
					+ AgreementLevelColumns.DOMAIN + " IN ( " + domainsParams + " )"
					);
			// @formatter:on

			stmt.setInt(1, agreementId);
			for ( int i = 0; i < domains.length; i++ )
				if ( domains[i] != null )
					stmt.setInt(2 + i, domains[i]);

			rs = stmt.executeQuery();

			Set<Level> levels = new HashSet<Level>();

			while (rs.next()) {
				Level level = new Level();
				level.setId(rs.getInt(AgreementLevelColumns.ID));
				level.setDescription(
						rs.getString(AgreementLevelColumns.DESCRIPTION));
				levels.add(level);
			}

			return levels;

		} finally {
			if (rs != null)
				rs.close();
			if (stmt != null)
				stmt.close();
		}

	}

	/*
	 * Returns level categories. The categories are returned in the order in
	 * which categories were inserted into the Database.
	 */
	public static Map<Integer, Set<String>> getCategories(Connection connection,
			int agreementId, Integer ...domains) throws SQLException {
		ResultSet rs = null;
		PreparedStatement stmt = null;
		try {
			String domainsParams = Arrays.asList(domains)
					.stream()
					.filter(domain-> domain != null)
					.map(domain->"?")
					.collect(Collectors.joining(","));

			// @formatter:off
			stmt = connection.prepareStatement("SELECT * " + " FROM "
					+ SQLConstants.AGREEMENT_LEVEL + " ,"
					+ SQLConstants.AGREEMENT_LEVEL_CATEGORY + " WHERE "
					+ SQLConstants.AGREEMENT_LEVEL + "."
					+ AgreementLevelColumns.AGREEMENT + "= ? " + " AND ("
					+ SQLConstants.AGREEMENT_LEVEL + "."
					+ AgreementLevelColumns.ID + " =  "
					+ SQLConstants.AGREEMENT_LEVEL_CATEGORY + "."
					+ AgreementLevelCategoryColumns.AGREEMENT_LEVEL + ") " + " AND "
					+ SQLConstants.AGREEMENT_LEVEL_CATEGORY + "."
					+ AgreementLevelCategoryColumns.DOMAIN + " IN ( " + domainsParams + " )"
					+ " ORDER BY " + SQLConstants.AGREEMENT_LEVEL_CATEGORY
					+ "." + AgreementLevelCategoryColumns.ID);
			// @formatter:on

			stmt.setInt(1, agreementId);
			for ( int i = 0; i < domains.length; i++ )
				if ( domains[i] != null )
					stmt.setInt(2 + i, domains[i]);

			rs = stmt.executeQuery();

			Map<Integer, Set<String>> categoriesMap = new HashMap<Integer, Set<String>>();

			while (rs.next()) {
				Integer level = rs.getInt(SQLConstants.AGREEMENT_LEVEL_CATEGORY
						+ "." + AgreementLevelCategoryColumns.AGREEMENT_LEVEL);
				Set<String> categories = categoriesMap.get(level);
				if (categories == null) {
					categoriesMap.put(level,
							categories = new LinkedHashSet<String>());
				}
				categories.add(
						rs.getString(SQLConstants.AGREEMENT_LEVEL_CATEGORY + "."
								+ AgreementLevelCategoryColumns.DESCRIPTION));
			}

			return categoriesMap;

		} finally {
			if (rs != null)
				rs.close();
			if (stmt != null)
				stmt.close();
		}

	}

	public static SortedSet<Date> getDatesWithChanges(Connection connection,
			int agreementId, Integer... domainIds) throws SQLException {
		ResultSet rs = null;
		PreparedStatement stmt = null;
		try {

			String in = StringUtils.repeat("?", ",", domainIds.length);

			SortedSet<Date> months = new TreeSet<Date>();

			// @formatter:off
			stmt = connection.prepareStatement("SELECT "
					+ AgreementDataColumns.START_DATE + " FROM "
					+ SQLConstants.AGREEMENT_DATA + " WHERE "
					+ AgreementDataColumns.AGREEMENT + " = ? " + " AND "
					+ AgreementDataColumns.DOMAIN + " IN ( " + in + " )"
					+ " GROUP BY 1");
			// @formatter:on
			stmt.setInt(1, agreementId);
			for (int i = 0; i < domainIds.length; i++)
				stmt.setInt(2 + i, domainIds[i]);
			rs = stmt.executeQuery();
			while (rs.next())
				months.add(rs.getDate(AgreementDataColumns.START_DATE));
			rs.close();
			stmt.close();

			// @formatter:off
			stmt = connection.prepareStatement("SELECT "
					+ AgreementLevelDataColumns.START_DATE + " FROM "
					+ SQLConstants.AGREEMENT_LEVEL + " LEFT JOIN "
					+ SQLConstants.AGREEMENT_LEVEL_DATA + " ON ( "
					+ SQLConstants.AGREEMENT_LEVEL_DATA + "."
					+ AgreementLevelDataColumns.AGREEMENT_LEVEL + " = "
					+ SQLConstants.AGREEMENT_LEVEL + "."
					+ AgreementLevelColumns.ID + ")" + " WHERE "
					+ AgreementLevelColumns.AGREEMENT + " = ? " + " AND "
					+ SQLConstants.AGREEMENT_LEVEL_DATA + "."
					+ AgreementLevelDataColumns.DOMAIN + " IN ( " + in + " )"
					+ " GROUP BY 1");
			// @formatter:on
			stmt.setInt(1, agreementId);
			for (int i = 0; i < domainIds.length; i++)
				stmt.setInt(2 + i, domainIds[i]);
			rs = stmt.executeQuery();
			while (rs.next())
				months.add(rs.getDate(AgreementLevelDataColumns.START_DATE));
			rs.close();
			stmt.close();

			return months;

		} finally {
			if (rs != null)
				rs.close();
			if (stmt != null)
				stmt.close();
		}
	}

	public static Set<Extra> getExtras(Connection connection, int agreementId)
			throws SQLException {
		ResultSet rs = null;
		PreparedStatement stmt = null;
		
		try {

			// SELECT * FROM agreement_extra LEFT JOIN agreement_payment ON (agreement_extra.agreement_payment = agreement_payment.id) WHERE agreement_extra.agreement=1192;
			String sql = "SELECT * " + " FROM "
					+ SQLConstants.AGREEMENT_EXTRA +" LEFT JOIN "
					+ SQLConstants.AGREEMENT_PAYMENT + " ON ("
					+ SQLConstants.AGREEMENT_EXTRA + "." + AgreementExtraColumns.AGREEMENT_PAYMENT + " = "
					+ SQLConstants.AGREEMENT_PAYMENT + "." + AgreementPaymentColumns.ID + ") LEFT JOIN "
					+ SQLConstants.PAYMENT_CONCEPT + " ON ("
					+ SQLConstants.AGREEMENT_PAYMENT + "." + AgreementPaymentColumns.PAYMENT_CONCEPT + " = "
					+ SQLConstants.PAYMENT_CONCEPT + "." + PaymentConceptColumns.ID + ")"
					+ " WHERE "
					+ SQLConstants.AGREEMENT_EXTRA + "." + AgreementExtraColumns.AGREEMENT + " = ? ";
			
//			String sql = "SELECT * " + " FROM "
//					+ SQLConstants.AGREEMENT_EXTRA + " WHERE "
//					+ AgreementExtraColumns.AGREEMENT + " = ? ";
			
			stmt = connection.prepareStatement(sql);

			stmt.setInt(1, agreementId);
			
//			System.out.println(agreementId);
//			System.out.println(sql);

			rs = stmt.executeQuery();

			Set<Extra> extras = new HashSet<Extra>();

			while (rs.next()) {
				Extra extra = new Extra();
				extra.setId(rs.getInt(SQLConstants.AGREEMENT_EXTRA + "." + AgreementExtraColumns.ID));
				extra.setDomain(rs.getInt(SQLConstants.AGREEMENT_EXTRA + "." + AgreementExtraColumns.DOMAIN));
				extra.setPaymentId(rs.getInt(SQLConstants.AGREEMENT_EXTRA + "." + AgreementExtraColumns.AGREEMENT_PAYMENT));
				extra.setStartDate(rs.getString(SQLConstants.AGREEMENT_EXTRA + "." + AgreementExtraColumns.START_DATE));
				extra.setEndDate(rs.getString(SQLConstants.AGREEMENT_EXTRA + "." + AgreementExtraColumns.END_DATE));
				extra.setIssueDate(rs.getString(SQLConstants.AGREEMENT_EXTRA + "." + AgreementExtraColumns.ISSUE_DATE));
				
//				System.out.println(
//						"Payment Extra -> id :"+rs.getInt(SQLConstants.AGREEMENT_PAYMENT + "." + AgreementPaymentColumns.ID)
//						+ ", expression :"+ rs.getString(SQLConstants.AGREEMENT_PAYMENT + "." + AgreementPaymentColumns.EXPRESSION)
//						+ ", description :"+rs.getString(SQLConstants.AGREEMENT_PAYMENT + "." + AgreementPaymentColumns.DESCRIPTION)
//						+", startDate :"+rs.getDate(SQLConstants.AGREEMENT_PAYMENT + "." + AgreementPaymentColumns.START_DATE)
//						+ ", description_decorable :"+ rs.getByte(SQLConstants.AGREEMENT_PAYMENT + "." + AgreementPaymentColumns.DESCRIPTION_DECORABLE)
//						+ ", irpf_expression :"+rs.getString(SQLConstants.AGREEMENT_PAYMENT + "." + AgreementPaymentColumns.IRPF_EXPRESSION)
//						+ ", quote_expression :"+rs.getString(SQLConstants.AGREEMENT_PAYMENT + "." + AgreementPaymentColumns.QUOTE_EXPRESSION)
//				);
				
				Payment payment = new Payment();
				payment.setId(rs.getInt(SQLConstants.AGREEMENT_PAYMENT + "." + AgreementPaymentColumns.ID));
				payment.setExpression(rs.getString(SQLConstants.AGREEMENT_PAYMENT + "." + AgreementPaymentColumns.EXPRESSION));
				payment.setDescription(rs.getString(SQLConstants.AGREEMENT_PAYMENT + "." + AgreementPaymentColumns.DESCRIPTION));
				payment.setStartDate(rs.getDate(SQLConstants.AGREEMENT_PAYMENT + "." + AgreementPaymentColumns.START_DATE));
				payment.setEndDate(rs.getDate(SQLConstants.AGREEMENT_PAYMENT + "." + AgreementPaymentColumns.END_DATE));
				payment.setDescriptionTemplate(rs.getString(SQLConstants.AGREEMENT_PAYMENT + "." + AgreementPaymentColumns.DESCRIPTION_DECORABLE));
				payment.setIrpfExpression(rs.getString(SQLConstants.AGREEMENT_PAYMENT + "." + AgreementPaymentColumns.IRPF_EXPRESSION));
				payment.setQuoteExpression(rs.getString(SQLConstants.AGREEMENT_PAYMENT + "." + AgreementPaymentColumns.QUOTE_EXPRESSION));
				payment.setConceptId(rs.getInt(SQLConstants.PAYMENT_CONCEPT + "." + PaymentConceptColumns.ID));
				Object paymentType = get(rs, AgreementPaymentColumns.TYPE,
						PaymentConceptColumns.TYPE, Object.class);
				payment.setType(getType(paymentType, Payment.Type.class));
				// 'month' & 'salary' only at agreement's payment....
				Integer month = getInteger(rs, AgreementPaymentColumns.MONTH);
				payment.setMonth(month != null ? month.shortValue() : null);

				Object salaryType = rs.getObject(SQLConstants.AGREEMENT_PAYMENT
						+ "." + AgreementPaymentColumns.SALARY_TYPE);
				payment.setSalaryType(getType(salaryType, Salary.Type.class));
				
				payment.setConcept(
						rs.getInt(SQLConstants.PAYMENT_CONCEPT + "." + PaymentConceptColumns.ID),
						rs.getInt(SQLConstants.PAYMENT_CONCEPT + "." + PaymentConceptColumns.DOMAIN), 
						rs.getString(SQLConstants.PAYMENT_CONCEPT + "." + PaymentConceptColumns.CODE), 
						rs.getString(SQLConstants.PAYMENT_CONCEPT + "." + PaymentConceptColumns.DESCRIPTION), 
						rs.getString(SQLConstants.PAYMENT_CONCEPT + "." + PaymentConceptColumns.TYPE), 
						rs.getString(SQLConstants.PAYMENT_CONCEPT + "." + PaymentConceptColumns.DESCRIPTION_DECORABLE), 
						rs.getString(SQLConstants.PAYMENT_CONCEPT + "." + PaymentConceptColumns.EXPRESSION), 
						rs.getString(SQLConstants.PAYMENT_CONCEPT + "." + PaymentConceptColumns.IRPF_EXPRESSION), 
						rs.getString(SQLConstants.PAYMENT_CONCEPT + "." + PaymentConceptColumns.QUOTE_EXPRESSION)
				);
				
				extra.setPayment(payment);
				
//				extra.setPayment(
//						rs.getInt(SQLConstants.AGREEMENT_PAYMENT + "." + AgreementPaymentColumns.ID),
//						rs.getString(SQLConstants.AGREEMENT_PAYMENT + "." + AgreementPaymentColumns.EXPRESSION), 
//						rs.getString(SQLConstants.AGREEMENT_PAYMENT + "." + AgreementPaymentColumns.DESCRIPTION), 
//						rs.getDate(SQLConstants.AGREEMENT_PAYMENT + "." + AgreementPaymentColumns.START_DATE),
//						rs.getByte(SQLConstants.AGREEMENT_PAYMENT + "." + AgreementPaymentColumns.DESCRIPTION_DECORABLE), 
//						rs.getString(SQLConstants.AGREEMENT_PAYMENT + "." + AgreementPaymentColumns.IRPF_EXPRESSION),
//						rs.getString(SQLConstants.AGREEMENT_PAYMENT + "." + AgreementPaymentColumns.QUOTE_EXPRESSION)
//				);
				
				extras.add(extra);
			}

			return extras;

		} finally {
			if (rs != null)
				rs.close();
			if (stmt != null)
				stmt.close();
		}

	}

	public static void save(Connection conn, AgreementDraft draft,
			Integer domain, Integer parentDomain) throws SQLException {

		if (draft.getId() < 0)
			insert(conn, draft, domain, parentDomain);
		else
			update(conn, draft, domain, parentDomain);
	}

	public static void insert(Connection conn, AgreementDraft draft,
			Integer domainId, Integer parentDomain) throws SQLException {

		int agreementId = insertAgreement(conn, domainId, draft);
		draft.setId(agreementId);

		SalaryTable salaryTable = draft.getDraftSalaryTable();

		for (Variable variable : salaryTable.getVariables(0)) {
			insertData(conn, domainId, draft.getId(), variable);
		}

		Map<Integer, Set<String>> draftCategoriesMap = draft
				.getDraftCategories();

		for (Level level : draft.getDraftLevels()) {
			int levelId = insertLevel(conn, domainId, draft.getId(), level);

			// Salary Table
			for (Variable variable : salaryTable.getVariables(levelId))
				insertLevelData(conn, domainId, levelId, variable);
			// Categories
			Set<String> draftCategories = draftCategoriesMap.get(level.getId());

			updateCategories(conn, domainId, levelId, Collections.emptySet(),
					draftCategories);
		}

		for (Payment payment : draft.getDraftPayments()) {
			if (!isRemove(payment)) {
				// Warning, we update payment id, it's a potential risk.
				int paymentId = JooqAgreement.insertPayment(conn, domainId,
						draft.getId(), payment);
				syncExtra(draft.getDraftExtras(), payment.getId(), paymentId);
				payment.setId(paymentId);
			}
		}

		for (Extra extra : draft.getDraftExtras()) {
			if (isRemove(extra)) {
				JooqAgreement.insertExtra(conn, domainId, draft.getId(), extra);
			}
		}
	}

	public static void update(Connection conn, AgreementDraft draft,
			Integer domainId, Integer parentDomain) throws SQLException {

		updateAgreement(conn, domainId, draft);

		Set<Level> draftLevels = draft.getDraftLevels();
		SalaryTable salaryTable = draft.getDraftSalaryTable();
		Map<Integer, Set<String>> draftCategoriesMap = draft
				.getDraftCategories();

		Map<Integer, Set<String>> dbCategoriesMap = getCategories(conn, draft.getId(), domainId, parentDomain);

		for (Level level : draftLevels) {

			if (isRemove(level)) {
				removeLevel(conn, level.getId());
				continue;
			}

			int dbId = level.getId();
			int draftId = level.getId();

			if (dbId < 0)
				dbId = insertLevel(conn, domainId, draft.getId(), level);
			else
				updateLevel(conn, level);

			for (Variable variable : salaryTable.getVariables(draftId)) {
				updateLevelData(conn, domainId, dbId, variable);
			}

			if (draftCategoriesMap.containsKey(draftId)) {
				
				
				updateCategories(conn, domainId, dbId,
						dbCategoriesMap.get(draftId),
						draftCategoriesMap.get(draftId));
			}
		}

		for (Variable variable : salaryTable.getVariables(0)) {
			updateData(conn, domainId, draft.getId(), variable);
		}

		for (Level level : draft.getLevels()) {

			int levelId = level.getId();

			if (levelId == 0)
				continue;

			if (draftLevels.contains(level))
				continue;

			for (Variable variable : salaryTable.getVariables(levelId)) {
				updateLevelData(conn, domainId, levelId, variable);
			}

			if (draftCategoriesMap.containsKey(levelId))
				updateCategories(conn, domainId, levelId,
						dbCategoriesMap.get(levelId),
						draftCategoriesMap.get(levelId));
		}

		for (Payment payment : draft.getDraftPayments()) {
			if (payment.getId() < 0) {
				if (!isRemove(payment)) {
					// Warning, we update payment id, it's a potential risk.
					int paymentId = JooqAgreement.insertPayment(conn, domainId,
							draft.getId(), payment);
					syncExtra(draft.getDraftExtras(), payment.getId(),
							paymentId);
					payment.setId(paymentId);
				}
			} else {
				if (!isRemove(payment)) {
					updatePayment(conn, domainId, draft.getId(), payment);
				} else {
					removePayment(conn, domainId, draft.getId(), payment);
				}
			}

		}

		for (Extra extra : draft.getDraftExtras()) {
			if (extra.getId() < 0) {
				if (!isRemove(extra)) {
					JooqAgreement.insertExtra(conn, domainId, draft.getId(),
							extra);
				}
			} else {
				if (!isRemove(extra)) {
					JooqAgreement.updateExtra(conn, extra);
				} else {
					JooqAgreement.removeExtra(conn, extra.getId());
				}
			}

		}
	}

	private static void syncExtra(Collection<Extra> extras, int oldPaymentId,
			int newPaymentId) {
		for (Extra extra : extras) {
			if (extra.getPaymentId() == oldPaymentId) {
				extra.setPaymentId(newPaymentId);
				return;
			}
		}
	}

	private static int insertAgreement(Connection conn, Integer domainId,
			AgreementDraft draft) throws SQLException {
		ResultSet rs = null;
		PreparedStatement stmt = null;
		try {

			// @formatter:off
			stmt = conn.prepareStatement("INSERT INTO "
					+ SQLConstants.AGREEMENT + " ( " + AgreementColumns.DOMAIN
					+ ", " + AgreementColumns.DESCRIPTION + ")"
					+ " VALUES ( ?,?)", new String[] { AgreementColumns.ID });
			// @formatter:on
			stmt.setInt(1, domainId);
			stmt.setString(2, draft.getDescription());
			stmt.executeUpdate();
			rs = stmt.getGeneratedKeys();
			rs.next();
			return rs.getInt(1);

		} finally {
			if (rs != null)
				rs.close();
			if (stmt != null)
				stmt.close();
		}
	}

	private static void updateAgreement(Connection conn, Integer domainId,
			AgreementDraft draft) throws SQLException {
		PreparedStatement stmt = null;
		try {

			// @formatter:off
			stmt = conn.prepareStatement("UPDATE " + SQLConstants.AGREEMENT
					+ " SET " + AgreementColumns.DESCRIPTION + " = ? "
					+ " WHERE " + AgreementColumns.ID + " = ? ");
			// @formatter:on
			stmt.setString(1, draft.getDescription());
			stmt.setInt(2, draft.getId());
			stmt.executeUpdate();
			stmt.close();

		} finally {
			if (stmt != null)
				stmt.close();
		}
	}

	private static void __insertExtra(Connection conn, Integer domainId,
			Integer agreementId, Extra extra) throws SQLException {
		PreparedStatement stmt = null;
		try {

			// @formatter:off
			stmt = conn.prepareStatement("INSERT INTO "
					+ SQLConstants.AGREEMENT_EXTRA + " ( "
					+ AgreementExtraColumns.DOMAIN + ", "
					+ AgreementExtraColumns.AGREEMENT + ", "
					+ AgreementExtraColumns.START_DATE + ", "
					+ AgreementExtraColumns.END_DATE + ", "
					+ AgreementExtraColumns.ISSUE_DATE + ", "
					+ AgreementExtraColumns.AGREEMENT_PAYMENT + ")"
					+ " VALUES (?,?,?,?,?,?)",
					new String[] { AgreementExtraColumns.ID });
			// @formatter:on
			stmt.setInt(1, domainId);
			stmt.setInt(2, agreementId);
			stmt.setString(3, extra.getStartDate());
			stmt.setString(4, extra.getEndDate());
			stmt.setString(5, extra.getIssueDate());
			Integer paymentId = extra.getPaymentId();
			if (paymentId != null)
				stmt.setInt(6, extra.getPaymentId());
			else
				stmt.setNull(6, Types.INTEGER);

			stmt.executeUpdate();

		} finally {
			if (stmt != null)
				stmt.close();
		}

	}

	private static void __updateExtra(Connection conn, Integer domainId,
			Integer agreementId, Extra extra) throws SQLException {
		PreparedStatement stmt = null;
		try {
			// @formatter:off
			stmt = conn.prepareStatement("UPDATE "
					+ SQLConstants.AGREEMENT_EXTRA + " SET "
					+ AgreementExtraColumns.START_DATE + " = ? ,"
					+ AgreementExtraColumns.END_DATE + " = ? ,"
					+ AgreementExtraColumns.ISSUE_DATE + " = ? ,"
					+ AgreementExtraColumns.AGREEMENT_PAYMENT + " = ?"
					+ " WHERE " + AgreementExtraColumns.ID + " = ? ");
			// @formatter:on
			stmt.setString(1, extra.getStartDate());
			stmt.setString(2, extra.getEndDate());
			stmt.setString(3, extra.getIssueDate());
			Integer paymentId = extra.getPaymentId();
			if (paymentId != null)
				stmt.setInt(4, extra.getPaymentId());
			else
				stmt.setNull(4, Types.INTEGER);
			stmt.setInt(5, extra.getId());
			stmt.executeUpdate();
			stmt.close();

		} finally {
			if (stmt != null)
				stmt.close();
		}

	}

	private static void __removeExtra(Connection conn, Integer extraId)
			throws SQLException {
		PreparedStatement stmt = null;
		try {
			// @formatter:off
			stmt = conn.prepareStatement("DELETE FROM "
					+ SQLConstants.AGREEMENT_EXTRA + " WHERE "
					+ AgreementExtraColumns.ID + " = ? ");
			// @formatter:on
			stmt.setInt(1, extraId);
			stmt.executeUpdate();

		} finally {
			if (stmt != null)
				stmt.close();
		}

	}

	private static int insertLevel(Connection conn, Integer domainId,
			Integer agreementId, Level level) throws SQLException {
		ResultSet rs = null;
		PreparedStatement stmt = null;
		try {

			// @formatter:off
			stmt = conn.prepareStatement("INSERT INTO "
					+ SQLConstants.AGREEMENT_LEVEL + " ( "
					+ AgreementLevelColumns.DOMAIN + ", "
					+ AgreementLevelColumns.AGREEMENT + ", "
					+ AgreementLevelColumns.DESCRIPTION + ")"
					+ " VALUES (?,?,?)",
					new String[] { AgreementLevelColumns.ID });
			// @formatter:on
			stmt.setInt(1, domainId);
			stmt.setInt(2, agreementId);
			stmt.setString(3, level.getDescription());
			stmt.executeUpdate();
			rs = stmt.getGeneratedKeys();
			rs.next();
			return rs.getInt(1);
		} finally {
			if (rs != null)
				rs.close();
			if (stmt != null)
				stmt.close();
		}

	}

	private static void insertData(Connection conn, Integer domainId,
			Integer agreementId, Variable variable) throws SQLException {
		PreparedStatement stmt = null;
		try {

			// @formatter:off
			stmt = conn.prepareStatement("INSERT INTO "
					+ SQLConstants.AGREEMENT_DATA + " ( "
					+ AgreementDataColumns.DOMAIN + ", "
					+ AgreementDataColumns.AGREEMENT + ", "
					+ AgreementDataColumns.NAME + ", "
					+ AgreementDataColumns.EXPRESSION + ", "
					+ AgreementDataColumns.START_DATE + ", "
					+ AgreementDataColumns.END_DATE + ")"
					+ " VALUES (?,?,?,?,?,?)",
					new String[] { AgreementLevelDataColumns.ID });
			// @formatter:on
			stmt.setInt(1, domainId);
			stmt.setInt(2, agreementId);
			stmt.setString(3, variable.getName());
			stmt.setString(4, variable.getExpression());
			stmt.setDate(5,
					new java.sql.Date(variable.getStartDate().getTime()));
			Date endDate = variable.getEndDate();
			if (endDate != null)
				stmt.setDate(6, new java.sql.Date(endDate.getTime()));
			else
				stmt.setNull(6, Types.DATE);

			stmt.executeUpdate();

		} finally {
			if (stmt != null)
				stmt.close();
		}

	}

	private static int insertLevelData(Connection conn, Integer domainId,
			Integer levelId, Variable variable) throws SQLException {
		ResultSet rs = null;
		PreparedStatement stmt = null;
		try {

			// @formatter:off
			stmt = conn.prepareStatement("INSERT INTO "
					+ SQLConstants.AGREEMENT_LEVEL_DATA + " ( "
					+ AgreementLevelDataColumns.DOMAIN + ", "
					+ AgreementLevelDataColumns.AGREEMENT_LEVEL + ", "
					+ AgreementLevelDataColumns.NAME + ", "
					+ AgreementLevelDataColumns.EXPRESSION + ", "
					+ AgreementLevelDataColumns.START_DATE + ", "
					+ AgreementLevelDataColumns.END_DATE + ")"
					+ " VALUES (?,?,?,?,?,?)",
					new String[] { AgreementLevelDataColumns.ID });
			// @formatter:on
			stmt.setInt(1, domainId);
			stmt.setInt(2, levelId);
			stmt.setString(3, variable.getName());
			stmt.setString(4, variable.getExpression());
			stmt.setDate(5,
					new java.sql.Date(variable.getStartDate().getTime()));
			Date endDate = variable.getEndDate();
			if (endDate != null)
				stmt.setDate(6, new java.sql.Date(endDate.getTime()));
			else
				stmt.setNull(6, Types.DATE);
			stmt.executeUpdate();

			rs = stmt.getGeneratedKeys();
			rs.next();
			return rs.getInt(1);

		} finally {
			if (rs != null)
				rs.close();
			if (stmt != null)
				stmt.close();
		}

	}

	private static void updateData(Connection conn, Integer domainId,
			Integer agreementId, Variable variable) throws SQLException {

		List<DBVariable> dbVariables = getDBData(conn, agreementId, variable);

		Period period = new Period(variable.getStartDate(),
				variable.getEndDate());

		for (DBVariable dbVariable : dbVariables) {
			Period dbPeriod = new Period(dbVariable.getStartDate(),
					dbVariable.getEndDate());
			List<Period> subs = dbPeriod.sub(period);

			if (subs.size() == 0) {
				// New data overrides completely previous data. .
				removeData(conn, dbVariable.getId());
				continue;
			}

			Period first = subs.get(0);
			if (first.equals(dbPeriod)) {
				// New data doesn't override previous data. }
			}

			// Update previous payment with new limits.
			updateData(conn, dbVariable.getId(), first);

			if (subs.size() > 1)
				copyData(conn, dbVariable.getId(), subs.get(1));

		}
		// Inserts if not empty (""), not null and not whitespace only
		if (StringUtils.isNotBlank(variable.getExpression())) {
			insertData(conn, domainId, agreementId, variable);
		}

	}

	private static void updateLevelData(Connection conn, Integer domainId,
			Integer levelId, Variable variable) throws SQLException {

		List<DBVariable> dbVariables = getDBLevelData(conn, levelId, variable);

		Period period = new Period(variable.getStartDate(),
				variable.getEndDate());

		for (DBVariable dbVariable : dbVariables) {
			Period dbPeriod = new Period(dbVariable.getStartDate(),
					dbVariable.getEndDate());
			List<Period> subs = dbPeriod.sub(period);

			if (subs.size() == 0) {
				// New data overrides completely previous data. .
				removeLevelData(conn, dbVariable.getId());
				continue;
			}

			Period first = subs.get(0);
			if (first.equals(dbPeriod)) {
				// New data doesn't override previous data. }
			}

			// Update previous payment with new limits.
			updateLevelData(conn, dbVariable.getId(), first);

			if (subs.size() > 1)
				copyLevelData(conn, dbVariable.getId(), subs.get(1));

		}
		// Inserts if not empty (""), not null and not whitespace only
		if (StringUtils.isNotBlank((variable.getExpression()))) {
			insertLevelData(conn, domainId, levelId, variable);
		}

	}

	private static void updateLevel(Connection conn, Level level)
			throws SQLException {
		PreparedStatement stmt = null;
		try {
			// @formatter:off
			stmt = conn.prepareStatement("UPDATE "
					+ SQLConstants.AGREEMENT_LEVEL + " SET "
					+ AgreementLevelColumns.DESCRIPTION + " = ? " + " WHERE "
					+ AgreementLevelColumns.ID + " = ? ");
			// @formatter:on
			stmt.setString(1, level.getDescription());
			stmt.setInt(2, level.getId());
			stmt.executeUpdate();
			stmt.close();

		} finally {
			if (stmt != null)
				stmt.close();
		}

	}

	private static void removeLevel(Connection conn, int levelId)
			throws SQLException {
		PreparedStatement stmt = null;
		try {
			// @formatter:off
			stmt = conn.prepareStatement("DELETE FROM "
					+ SQLConstants.AGREEMENT_LEVEL_CATEGORY + " WHERE "
					+ AgreementLevelCategoryColumns.AGREEMENT_LEVEL + " = ? ");
			// @formatter:on
			stmt.setInt(1, levelId);
			stmt.executeUpdate();
			stmt.close();

			// @formatter:off
			stmt = conn.prepareStatement("DELETE FROM "
					+ SQLConstants.AGREEMENT_LEVEL_DATA + " WHERE "
					+ AgreementLevelDataColumns.AGREEMENT_LEVEL + " = ? ");
			// @formatter:on
			stmt.setInt(1, levelId);
			stmt.executeUpdate();
			stmt.close();

			// @formatter:off
			stmt = conn.prepareStatement("DELETE FROM "
					+ SQLConstants.AGREEMENT_LEVEL + " WHERE "
					+ AgreementLevelColumns.ID + " = ? ");
			// @formatter:on
			stmt.setInt(1, levelId);
			stmt.executeUpdate();

		} finally {
			if (stmt != null)
				stmt.close();
		}

	}

	private static int __insertPayment(Connection conn, Integer domainId,
			Integer agreementId, Payment payment) throws SQLException {
		ResultSet rs = null;
		PreparedStatement stmt = null;
		try {

			// @formatter:off
			stmt = conn.prepareStatement("INSERT INTO "
					+ SQLConstants.AGREEMENT_PAYMENT + " ( "
					+ AgreementPaymentColumns.DOMAIN + ", "
					+ AgreementPaymentColumns.AGREEMENT + ", "

					+ AgreementPaymentColumns.PAYMENT_CONCEPT + ", "
					+ AgreementPaymentColumns.TYPE + ", "
					+ AgreementPaymentColumns.DESCRIPTION + ", "
					+ AgreementPaymentColumns.EXPRESSION + ", "
					+ AgreementPaymentColumns.IRPF_EXPRESSION + ", "
					+ AgreementPaymentColumns.QUOTE_EXPRESSION + ", "

					+ AgreementPaymentColumns.MONTH + ", "
					+ AgreementPaymentColumns.SALARY_TYPE + ", "
					+ AgreementPaymentColumns.START_DATE + ", "
					+ AgreementPaymentColumns.END_DATE + ")"
					+ " VALUES (?,?,?,?,?,?,?,?,?,?,?,?)",
					new String[] { AgreementPaymentColumns.ID });
			// @formatter:on
			stmt.setInt(1, domainId);
			stmt.setInt(2, agreementId);

			Integer conceptId = payment.getConceptId();
			if (conceptId != null)
				stmt.setInt(3, conceptId);
			else
				stmt.setNull(3, Types.INTEGER);

			Payment.Type type = payment.getType();
			if (type != null)
				stmt.setInt(4, type.ordinal());
			else
				stmt.setNull(4, Types.SMALLINT);

			stmt.setString(5, payment.getDescription());
			stmt.setString(6, payment.getExpression());
			stmt.setString(7, payment.getIrpfExpression());
			stmt.setString(8, payment.getQuoteExpression());

			Short month = payment.getMonth();
			if (month != null)
				stmt.setShort(9, month);
			else
				stmt.setNull(9, Types.SMALLINT);

			Salary.Type salaryType = payment.getSalaryType();
			if (salaryType != null)
				stmt.setInt(10, salaryType.ordinal());
			else
				stmt.setNull(10, Types.SMALLINT);

			stmt.setDate(11,
					new java.sql.Date(payment.getStartDate().getTime()));
			Date endDate = payment.getEndDate();
			if (endDate != null)
				stmt.setDate(12,
						new java.sql.Date(payment.getStartDate().getTime()));
			else
				stmt.setNull(12, Types.DATE);

			stmt.executeUpdate();

			rs = stmt.getGeneratedKeys();
			rs.next();
			return rs.getInt(1);

		} finally {
			if (rs != null)
				rs.close();
			if (stmt != null)
				stmt.close();
		}

	}

	private static void updatePayment(Connection conn, Integer domainId,
			Integer agreementId, Payment payment) throws SQLException {

		JooqAgreement.updatePayment(conn, payment);
	}

	private static void removePayment(Connection conn, Integer domainId,
			Integer agreementId, Payment payment) throws SQLException {

		JooqAgreement.removePayment(conn, payment);

	}


	private static List<DBVariable> getDBData(Connection conn, int agreementId,
			Variable variable) throws SQLException {
		ResultSet rs = null;
		PreparedStatement stmt = null;
		try {
			Date endDate = variable.getEndDate();
			// @formatter:off
			stmt = conn.prepareStatement("SELECT *"
					+ " FROM "
					+ SQLConstants.AGREEMENT_DATA
					+ " WHERE "
					+ AgreementDataColumns.AGREEMENT
					+ " = ? "
					+ " AND "
					+ AgreementDataColumns.NAME
					+ " = ?  "
					+ " AND ( "
					+ AgreementDataColumns.END_DATE
					+ " >= ?  "
					+ " OR  "
					+ AgreementDataColumns.END_DATE
					+ " IS NULL )"
					+ (endDate != null ? " AND "
							+ AgreementDataColumns.START_DATE + " <= ? " : ""));
			// @formatter:on

			stmt.setInt(1, agreementId);
			stmt.setString(2, variable.getName());
			stmt.setDate(3,
					new java.sql.Date(variable.getStartDate().getTime()));
			if (endDate != null)
				stmt.setDate(4, new java.sql.Date(endDate.getTime()));

			rs = stmt.executeQuery();

			List<DBVariable> variables = new ArrayList<DBVariable>();
			while (rs.next()) {
				DBVariable dbVar = new DBVariable();
				dbVar.setId(rs.getInt(AgreementLevelDataColumns.ID));
				dbVar.setName(rs.getString(AgreementLevelDataColumns.NAME));
				dbVar.setExpression(
						rs.getString(AgreementLevelDataColumns.EXPRESSION));
				dbVar.setStartDate(
						rs.getDate(AgreementLevelDataColumns.START_DATE));
				dbVar.setEndDate(
						rs.getDate(AgreementLevelDataColumns.END_DATE));
				variables.add(dbVar);
			}
			return variables;

		} finally {
			if (rs != null)
				rs.close();
			if (stmt != null)
				stmt.close();
		}

	}

	private static List<DBVariable> getDBLevelData(Connection conn, int levelId,
			Variable variable) throws SQLException {
		ResultSet rs = null;
		PreparedStatement stmt = null;
		try {
			Date endDate = variable.getEndDate();
			// @formatter:off
			stmt = conn.prepareStatement("SELECT *"
					+ " FROM "
					+ SQLConstants.AGREEMENT_LEVEL_DATA
					+ " WHERE "
					+ AgreementLevelDataColumns.AGREEMENT_LEVEL
					+ " = ? "
					+ " AND "
					+ AgreementLevelDataColumns.NAME
					+ " = ?  "
					+ " AND ( "
					+ AgreementLevelDataColumns.END_DATE
					+ " >= ?  "
					+ " OR  "
					+ AgreementLevelDataColumns.END_DATE
					+ " IS NULL )"
					+ (endDate != null ? " AND "
							+ AgreementLevelDataColumns.START_DATE + " <= ? "
							: ""));
			// @formatter:on

			stmt.setInt(1, levelId);
			stmt.setString(2, variable.getName());
			stmt.setDate(3,
					new java.sql.Date(variable.getStartDate().getTime()));
			if (endDate != null)
				stmt.setDate(4, new java.sql.Date(endDate.getTime()));

			rs = stmt.executeQuery();

			List<DBVariable> variables = new ArrayList<DBVariable>();
			while (rs.next()) {
				DBVariable dbVar = new DBVariable();
				dbVar.setId(rs.getInt(AgreementLevelDataColumns.ID));
				dbVar.setName(rs.getString(AgreementLevelDataColumns.NAME));
				dbVar.setExpression(
						rs.getString(AgreementLevelDataColumns.EXPRESSION));
				dbVar.setStartDate(
						rs.getDate(AgreementLevelDataColumns.START_DATE));
				dbVar.setEndDate(
						rs.getDate(AgreementLevelDataColumns.END_DATE));
				variables.add(dbVar);
			}
			return variables;

		} finally {
			if (rs != null)
				rs.close();
			if (stmt != null)
				stmt.close();
		}

	}

	private static void __updatePayment(Connection conn, Payment payment)
			throws SQLException {
		PreparedStatement stmt = null;
		try {
			// @formatter:off
			stmt = conn.prepareStatement("UPDATE "
					+ SQLConstants.AGREEMENT_PAYMENT + " SET "
					+ AgreementPaymentColumns.START_DATE + " = ?" + " ,"
					+ AgreementPaymentColumns.END_DATE + " = ?" + " ,"
					+ AgreementPaymentColumns.MONTH + " = ?" + " ,"
					+ AgreementPaymentColumns.TYPE + " = ?" + " ,"
					+ AgreementPaymentColumns.SALARY_TYPE + " = ?" + " ,"
					+ AgreementPaymentColumns.DESCRIPTION + " = ?" + " ,"
					+ AgreementPaymentColumns.EXPRESSION + " = ?" + " ,"
					+ AgreementPaymentColumns.IRPF_EXPRESSION + " = ?" + " ,"
					+ AgreementPaymentColumns.QUOTE_EXPRESSION + " = ?"
					+ " WHERE " + AgreementPaymentColumns.ID + "= ? ");
			// @formatter:on

			stmt.setDate(1,
					new java.sql.Date(payment.getStartDate().getTime()));
			Date endDate = payment.getEndDate();
			if (endDate != null)
				stmt.setDate(2, new java.sql.Date(endDate.getTime()));
			else
				stmt.setNull(2, Types.DATE);

			if (payment.getMonth() != null)
				stmt.setShort(3, payment.getMonth());
			else
				stmt.setNull(3, Types.SMALLINT);

			if (payment.getType() != null)
				stmt.setInt(4, payment.getType().ordinal());
			else
				stmt.setNull(4, Types.INTEGER);

			if (payment.getSalaryType() != null)
				stmt.setInt(5, payment.getSalaryType().ordinal());
			else
				stmt.setNull(5, Types.INTEGER);

			stmt.setString(6, payment.getDescription());
			stmt.setString(7, payment.getExpression());
			stmt.setString(8, payment.getIrpfExpression());
			stmt.setString(9, payment.getQuoteExpression());

			stmt.setInt(10, payment.getId());

			stmt.executeUpdate();

		} finally {
			if (stmt != null)
				stmt.close();
		}

	}

	private static void updatePayment(Connection conn, Integer paymentId,
			Period period) throws SQLException {
		PreparedStatement stmt = null;
		try {
			// @formatter:off
			stmt = conn.prepareStatement("UPDATE "
					+ SQLConstants.AGREEMENT_PAYMENT + " SET "
					+ AgreementPaymentColumns.START_DATE + " = ?" + " ,"
					+ AgreementPaymentColumns.END_DATE + " = ?" + " WHERE "
					+ AgreementPaymentColumns.ID + "= ? ");
			// @formatter:on

			stmt.setDate(1, new java.sql.Date(period.getStart().getTime()));
			Date endDate = period.getEnd();
			if (endDate != null)
				stmt.setDate(2, new java.sql.Date(endDate.getTime()));
			else
				stmt.setNull(2, Types.DATE);

			stmt.setInt(3, paymentId);

			stmt.executeUpdate();

		} finally {
			if (stmt != null)
				stmt.close();
		}

	}

	private static void __removePayment(Connection conn, Integer paymentId)
			throws SQLException {
		PreparedStatement stmt = null;
		try {
			// @formatter:off
			stmt = conn.prepareStatement("DELETE FROM "
					+ SQLConstants.AGREEMENT_PAYMENT + " WHERE "
					+ AgreementPaymentColumns.ID + "= ? ");
			// @formatter:on

			stmt.setInt(1, paymentId);

			stmt.executeUpdate();

		} finally {
			if (stmt != null)
				stmt.close();
		}

	}

	private static void copyPayment(Connection conn, Integer paymentId,
			Period period) throws SQLException {
		PreparedStatement stmt = null;
		try {
			// @formatter:off
			stmt = conn.prepareStatement("INSERT INTO "
					+ SQLConstants.AGREEMENT_PAYMENT + " ( "
					+ AgreementPaymentColumns.DOMAIN + ", "
					+ AgreementPaymentColumns.AGREEMENT + ", "

					+ AgreementPaymentColumns.PAYMENT_CONCEPT + ", "
					+ AgreementPaymentColumns.TYPE + ", "
					+ AgreementPaymentColumns.DESCRIPTION + ", "
					+ AgreementPaymentColumns.EXPRESSION + ", "
					+ AgreementPaymentColumns.IRPF_EXPRESSION + ", "
					+ AgreementPaymentColumns.QUOTE_EXPRESSION + ", "

					+ AgreementPaymentColumns.MONTH + ", "
					+ AgreementPaymentColumns.SALARY_TYPE + ", "
					+ AgreementPaymentColumns.START_DATE + ", "
					+ AgreementPaymentColumns.END_DATE + ")" + " ( SELECT "
					+ AgreementPaymentColumns.DOMAIN + ", "
					+ AgreementPaymentColumns.AGREEMENT

					+ ", " + AgreementPaymentColumns.PAYMENT_CONCEPT + ", "
					+ AgreementPaymentColumns.TYPE + ", "
					+ AgreementPaymentColumns.DESCRIPTION + ", "
					+ AgreementPaymentColumns.EXPRESSION + ", "
					+ AgreementPaymentColumns.IRPF_EXPRESSION + ", "
					+ AgreementPaymentColumns.QUOTE_EXPRESSION

					+ ", " + AgreementPaymentColumns.MONTH + ", "
					+ AgreementPaymentColumns.SALARY_TYPE + ",  ? " + ",  ? "
					+ " WHERE " + AgreementPaymentColumns.ID + " = ? " + ")",
					new String[] { AgreementPaymentColumns.ID });
			// @formatter:on
			stmt.setDate(1, new java.sql.Date(period.getStart().getTime()));

			Date endDate = period.getEnd();
			if (endDate != null)
				stmt.setDate(2, new java.sql.Date(endDate.getTime()));
			else
				stmt.setNull(2, Types.DATE);

			stmt.setInt(3, paymentId);

			stmt.executeUpdate();

		} finally {
			if (stmt != null)
				stmt.close();
		}

	}

	private static void updateCategories(Connection conn, Integer domainId,
			Integer levelId, Set<String> oldCategories,
			Set<String> newCategories) throws SQLException {
		PreparedStatement stmt = null;
		
		if ( newCategories == null  )
			newCategories = Collections.emptySet();
		if ( oldCategories == null  )
			oldCategories = Collections.emptySet();

		try {			
			
			Set<String> deleteCategories = new HashSet<String>(oldCategories);
			deleteCategories.removeAll(newCategories);
			
			if ( deleteCategories.size() > 0 ) {
				// @formatter:off
				stmt = conn.prepareStatement("DELETE FROM "
						+ SQLConstants.AGREEMENT_LEVEL_CATEGORY + " WHERE "
						+ AgreementLevelCategoryColumns.AGREEMENT_LEVEL + " = ? "
						+ " AND "+ AgreementLevelCategoryColumns.DESCRIPTION 
						+ " IN ("+ deleteCategories.stream().map(category-> "?").collect(Collectors.joining(",")) +")"
						);
				// @formatter:on
				stmt.setInt(1, levelId);
				
				int i = 2;
				for ( String category: deleteCategories)
					stmt.setString(i++, category);
					
				stmt.executeUpdate();
				stmt.close();
			}

			
			Set<String> insertCategories = new HashSet<String>(newCategories);
			insertCategories.removeAll(oldCategories);
			
			if (insertCategories.isEmpty())
				return;

			// @formatter:off
			stmt = conn.prepareStatement("INSERT INTO "
					+ SQLConstants.AGREEMENT_LEVEL_CATEGORY + "( "
					+ AgreementLevelCategoryColumns.DOMAIN + " , "
					+ AgreementLevelCategoryColumns.AGREEMENT_LEVEL + " , "
					+ AgreementLevelCategoryColumns.DESCRIPTION + ") "
					+ " VALUES (?, ?, ?) ");
			// @formatter:on

			for (String category : insertCategories) {
				stmt.setInt(1, domainId);
				stmt.setInt(2, levelId);
				stmt.setString(3, category);
				stmt.executeUpdate();
			}

		} finally {
			if (stmt != null)
				stmt.close();
		}
	}

	private static void removeData(Connection conn, Integer dataId)
			throws SQLException {
		PreparedStatement stmt = null;
		try {
			// @formatter:off
			stmt = conn.prepareStatement("DELETE FROM "
					+ SQLConstants.AGREEMENT_DATA + " WHERE "
					+ AgreementDataColumns.ID + "= ? ");
			// @formatter:on

			stmt.setInt(1, dataId);

			stmt.executeUpdate();

		} finally {
			if (stmt != null)
				stmt.close();
		}

	}

	private static void removeLevelData(Connection conn, Integer dataId)
			throws SQLException {
		PreparedStatement stmt = null;
		try {
			// @formatter:off
			stmt = conn.prepareStatement("DELETE FROM "
					+ SQLConstants.AGREEMENT_LEVEL_DATA + " WHERE "
					+ AgreementLevelDataColumns.ID + "= ? ");
			// @formatter:on

			stmt.setInt(1, dataId);

			stmt.executeUpdate();

		} finally {
			if (stmt != null)
				stmt.close();
		}

	}

	private static void copyData(Connection conn, Integer dataId, Period period)
			throws SQLException {
		PreparedStatement stmt = null;
		try {
			// @formatter:off
			stmt = conn.prepareStatement("INSERT INTO "
					+ SQLConstants.AGREEMENT_DATA + " ( "
					+ AgreementDataColumns.DOMAIN + ", "
					+ AgreementDataColumns.AGREEMENT + ", "

					+ AgreementDataColumns.NAME + ", "
					+ AgreementDataColumns.EXPRESSION + ", "

					+ AgreementDataColumns.START_DATE + ", "
					+ AgreementDataColumns.END_DATE + ")" + " ( SELECT "
					+ AgreementDataColumns.DOMAIN + ", "
					+ AgreementDataColumns.AGREEMENT

					+ ", " + AgreementDataColumns.NAME + ", "
					+ AgreementDataColumns.EXPRESSION + ",  ? " + ",  ? "
					+ " FROM " + SQLConstants.AGREEMENT_DATA + " WHERE "
					+ AgreementDataColumns.ID + " = ? " + ")",
					new String[] { AgreementDataColumns.ID });
			// @formatter:on
			stmt.setDate(1, new java.sql.Date(period.getStart().getTime()));

			Date endDate = period.getEnd();
			if (endDate != null)
				stmt.setDate(2, new java.sql.Date(endDate.getTime()));
			else
				stmt.setNull(2, Types.DATE);

			stmt.setInt(3, dataId);

			stmt.executeUpdate();

		} finally {
			if (stmt != null)
				stmt.close();
		}

	}

	private static void copyLevelData(Connection conn, Integer dataId,
			Period period) throws SQLException {
		PreparedStatement stmt = null;
		try {
			// @formatter:off
			stmt = conn.prepareStatement("INSERT INTO "
					+ SQLConstants.AGREEMENT_LEVEL_DATA + " ( "
					+ AgreementLevelDataColumns.DOMAIN + ", "
					+ AgreementLevelDataColumns.AGREEMENT_LEVEL + ", "

					+ AgreementLevelDataColumns.NAME + ", "
					+ AgreementLevelDataColumns.EXPRESSION + ", "

					+ AgreementLevelDataColumns.START_DATE + ", "
					+ AgreementLevelDataColumns.END_DATE + ")" + " ( SELECT "
					+ AgreementLevelDataColumns.DOMAIN + ", "
					+ AgreementLevelDataColumns.AGREEMENT_LEVEL

					+ ", " + AgreementLevelDataColumns.NAME + ", "
					+ AgreementLevelDataColumns.EXPRESSION + ",  ? " + ",  ? "
					+ " FROM " + SQLConstants.AGREEMENT_LEVEL_DATA + " WHERE "
					+ AgreementLevelDataColumns.ID + " = ? " + ")",
					new String[] { AgreementLevelDataColumns.ID });
			// @formatter:on
			stmt.setDate(1, new java.sql.Date(period.getStart().getTime()));

			Date endDate = period.getEnd();
			if (endDate != null)
				stmt.setDate(2, new java.sql.Date(endDate.getTime()));
			else
				stmt.setNull(2, Types.DATE);

			stmt.setInt(3, dataId);

			stmt.executeUpdate();

		} finally {
			if (stmt != null)
				stmt.close();
		}

	}

	private static void updateData(Connection conn, Integer dataId,
			Period period) throws SQLException {
		PreparedStatement stmt = null;
		try {
			// @formatter:off
			stmt = conn.prepareStatement("UPDATE "
					+ SQLConstants.AGREEMENT_DATA + " SET "
					+ AgreementDataColumns.START_DATE + " = ? " + ", "
					+ AgreementDataColumns.END_DATE + " = ? " + " WHERE "
					+ AgreementDataColumns.ID + "= ? ");
			// @formatter:on

			stmt.setDate(1, new java.sql.Date(period.getStart().getTime()));
			Date endDate = period.getEnd();
			if (endDate != null)
				stmt.setDate(2, new java.sql.Date(endDate.getTime()));
			else
				stmt.setNull(2, Types.DATE);

			stmt.setInt(3, dataId);

			stmt.executeUpdate();

		} finally {
			if (stmt != null)
				stmt.close();
		}

	}

	private static void updateLevelData(Connection conn, Integer dataId,
			Period period) throws SQLException {
		PreparedStatement stmt = null;
		try {
			// @formatter:off
			stmt = conn.prepareStatement("UPDATE "
					+ SQLConstants.AGREEMENT_LEVEL_DATA + " SET "
					+ AgreementLevelDataColumns.START_DATE + " = ? " + ", "
					+ AgreementLevelDataColumns.END_DATE + " = ? " + " WHERE "
					+ AgreementLevelDataColumns.ID + "= ? ");
			// @formatter:on

			stmt.setDate(1, new java.sql.Date(period.getStart().getTime()));
			Date endDate = period.getEnd();
			if (endDate != null)
				stmt.setDate(2, new java.sql.Date(endDate.getTime()));
			else
				stmt.setNull(2, Types.DATE);

			stmt.setInt(3, dataId);

			stmt.executeUpdate();

		} finally {
			if (stmt != null)
				stmt.close();
		}

	}

	private static <T> T get(ResultSet rs, String paymentColumn,
			String conceptColumn, Class<T> toType) throws SQLException {
		return SQLUtils.get(rs, toType,
				SQLConstants.AGREEMENT_PAYMENT + "." + paymentColumn,
				SQLConstants.PAYMENT_CONCEPT + "." + conceptColumn);
	}

	private static <T> T getAux(Class<T> toType, Object... values)
			throws SQLException {
		return JooqUtils.get(toType, values);
	}

	protected static Settings getDefaultSettings() {
		if (SETTINGS == null) {
			SETTINGS = new Settings();
			SETTINGS.setRenderSchema(false);
		}
		return SETTINGS;
	}

}
