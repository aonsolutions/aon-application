package com.esferalia.aon.gwt.payroll.sql;

import static com.esferalia.aon.gwt.payroll.sql.SQLUtils.getInteger;
import static com.esferalia.aon.gwt.payroll.sql.SQLUtils.getType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.esferalia.aon.gwt.payroll.shared.AgreementDraft;
import com.esferalia.aon.gwt.payroll.shared.AgreementDraft.Level;
import com.esferalia.aon.gwt.payroll.shared.AgreementDraft.SalaryTable;
import com.esferalia.aon.gwt.payroll.shared.Payment;
import com.esferalia.aon.gwt.payroll.shared.Salary;
import com.esferalia.aon.gwt.payroll.shared.SalaryDraft.Scope;
import com.esferalia.aon.gwt.payroll.shared.StringVariable;
import com.esferalia.aon.gwt.payroll.shared.Variable;
import com.esferalia.aon.payroll.sql.SQLConstants;
import com.esferalia.aon.payroll.sql.SQLConstants.AgreementDataColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.AgreementLevelCategoryColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.AgreementLevelColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.AgreementLevelDataColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.AgreementPaymentColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.PaymentConceptColumns;

public class SQLAgreementDraft {

	public static Set<Payment> getPayments(Connection connection,
			int agreementId, Date startDate, Date endDate) throws SQLException {
		ResultSet rs = null;
		PreparedStatement stmt = null;
		try {

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
					+ AgreementPaymentColumns.START_DATE + " <= ? ");

			stmt.setInt(1, agreementId);
			stmt.setDate(2, sqlStartDate);
			stmt.setDate(3, sqlEndDate);

			rs = stmt.executeQuery();

			Set<Payment> payments = new HashSet<Payment>();

			while (rs.next()) {
				Payment payment = new Payment();

				payment.setStartDate(sqlStartDate);
				payment.setEndDate(sqlEndDate);

				payment.setId(rs.getInt(SQLConstants.AGREEMENT_PAYMENT + "."
						+ AgreementPaymentColumns.ID));

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
			int agreementId, Date startDate, Date endDate) throws SQLException {
		ResultSet rs = null;
		PreparedStatement stmt = null;
		try {

			java.sql.Date sqlEndDate = SQLUtils.date2sql(endDate);
			java.sql.Date sqlStartDate = SQLUtils.date2sql(startDate);

			// First of all, agreement data. These are inherited by all levels.
			stmt = connection.prepareStatement("SELECT * " + " FROM "
					+ SQLConstants.AGREEMENT_DATA + " WHERE "
					+ AgreementDataColumns.AGREEMENT + " = ? " + " AND ( "
					+ AgreementDataColumns.END_DATE + " IS NULL " + " OR "
					+ AgreementDataColumns.END_DATE + " >= ?  ) " + " AND "
					+ AgreementDataColumns.START_DATE + " <= ? ");

			stmt.setInt(1, agreementId);
			stmt.setDate(2, sqlStartDate);
			stmt.setDate(3, sqlEndDate);

			rs = stmt.executeQuery();

			SalaryTable salaryTable = new SalaryTable();

			while (rs.next()) {
				Variable var = new StringVariable();
				var.setScope(Scope.AGREEMENT);
				var.setStartDate(sqlStartDate);
				var.setEndDate(sqlEndDate);
				var.setName(rs.getString(AgreementDataColumns.NAME));
				var.setExpression(rs.getString(AgreementDataColumns.EXPRESSION));
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
					+ AgreementLevelDataColumns.END_DATE + " >= ?  ) "
					+ " AND " + SQLConstants.AGREEMENT_LEVEL_DATA + "."
					+ AgreementLevelDataColumns.START_DATE + " <= ? ");

			stmt.setInt(1, agreementId);
			stmt.setDate(2, sqlStartDate);
			stmt.setDate(3, sqlEndDate);

			rs = stmt.executeQuery();

			while (rs.next()) {
				Variable var = new StringVariable();
				var.setScope(Scope.AGREEMENT);
				var.setStartDate(sqlStartDate);
				var.setEndDate(sqlEndDate);
				var.setName(rs.getString(AgreementLevelDataColumns.NAME));
				var.setExpression(rs
						.getString(AgreementLevelDataColumns.EXPRESSION));
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

	public static Set<Level> getLevels(Connection connection, int agreementId)
			throws SQLException {
		ResultSet rs = null;
		PreparedStatement stmt = null;
		try {

			stmt = connection.prepareStatement("SELECT * " + " FROM "
					+ SQLConstants.AGREEMENT_LEVEL + " WHERE "
					+ AgreementLevelColumns.AGREEMENT + " = ? ");

			stmt.setInt(1, agreementId);

			rs = stmt.executeQuery();

			Set<Level> levels = new HashSet<Level>();

			while (rs.next()) {
				Level level = new Level();
				level.setId(rs.getInt(AgreementLevelColumns.ID));
				level.setDescription(rs
						.getString(AgreementLevelColumns.DESCRIPTION));
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

	public static Map<Integer, Set<String>> getCategories(
			Connection connection, int agreementId) throws SQLException {
		ResultSet rs = null;
		PreparedStatement stmt = null;
		try {

			stmt = connection.prepareStatement("SELECT * " + " FROM "
					+ SQLConstants.AGREEMENT_LEVEL + " ,"
					+ SQLConstants.AGREEMENT_LEVEL_CATEGORY + " WHERE "
					+ SQLConstants.AGREEMENT_LEVEL + "."
					+ AgreementLevelColumns.AGREEMENT + "= ? " + " AND ("
					+ SQLConstants.AGREEMENT_LEVEL + "."
					+ AgreementLevelColumns.ID + " =  "
					+ SQLConstants.AGREEMENT_LEVEL_CATEGORY + "."
					+ AgreementLevelCategoryColumns.AGREEMENT_LEVEL + ")");

			stmt.setInt(1, agreementId);

			rs = stmt.executeQuery();

			Map<Integer, Set<String>> categoriesMap = new HashMap<Integer, Set<String>>();

			while (rs.next()) {
				Integer level = rs.getInt(SQLConstants.AGREEMENT_LEVEL_CATEGORY
						+ "." + AgreementLevelCategoryColumns.AGREEMENT_LEVEL);
				Set<String> categories = categoriesMap.get(level);
				if (categories == null) {
					categoriesMap
							.put(level, categories = new HashSet<String>());
				}
				categories.add(rs
						.getString(SQLConstants.AGREEMENT_LEVEL_CATEGORY + "."
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

	public static void save(Connection conn, AgreementDraft draft,
			Integer domain, Integer parentDomain) throws SQLException {

		Set<Level> levels = draft.getLevels();
		Set<String> vars = draft.getVariables();
		SalaryTable salaryTable = draft.getSalaryTable();

		for (Level level : levels) {
			for (String var : vars) {
				Variable variable = salaryTable.get(level.getId(), var);
			}
		}

	}

	private static <T> T get(ResultSet rs, String paymentColumn,
			String conceptColumn, Class<T> toType) throws SQLException {
		return SQLUtils.get(rs, toType, SQLConstants.AGREEMENT_PAYMENT + "."
				+ paymentColumn, SQLConstants.PAYMENT_CONCEPT + "."
				+ conceptColumn);
	}
}
