package com.esferalia.aon.gwt.payroll.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

import com.esferalia.aon.gwt.payroll.shared.Bonus;
import com.esferalia.aon.gwt.payroll.shared.Deduction;
import com.esferalia.aon.gwt.payroll.shared.Payment;
import com.esferalia.aon.gwt.payroll.shared.SalaryDraft;
import com.esferalia.aon.gwt.payroll.shared.Variable;
import com.esferalia.aon.payroll.sql.SQLConstants;
import com.esferalia.aon.payroll.sql.SQLConstants.AgreementDataColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.AgreementLevelCategoryColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.AgreementLevelColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.AgreementLevelDataColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.AgreementPaymentColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.BonusConceptColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.ContractBonusColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.ContractColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.ContractDataColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.ContractDeductionColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.ContractEmbargoColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.ContractPaymentColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.DeductionConceptColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.PaymentConceptColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.SalaryColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.SalaryEmbargoColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.SystemDataColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.SystemDeductionColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.SystemPaymentColumns;
import com.esferalia.aon.salary.expression.Period;

public class SQLSalaryDraft {

	public static void save(Connection conn, SalaryDraft draft, Integer domain,
			Integer parentDomain) throws SQLException {
		Integer contract = draft.getEmployee().getId();

		for (Variable variable : draft.getDraftContext()) {
			makeRoom(conn, variable, contract);
			String expression = variable.getExpression();
			if (!"REMOVE_VARIABLE()".equals(expression)
					|| inAgreement(conn, variable, contract)
					|| inSystem(conn, variable, domain, parentDomain)) {
				insertData(conn, variable, contract, domain);
			} // end-if : If it's not REMOVE() or is at agreement or system.
		}

		for (Payment payment : draft.getDraftPayments()) {
			makeRoomPayment(conn, payment, contract);
			String expression = payment.getExpression();
			
			if ( StringUtils.containsIgnoreCase(expression, "CONVENIO()" )){
				continue;
			}
			
			if (!"REMOVE()".equals(expression)
					|| inAgreement(conn, payment, contract)
					|| inSystem(conn, payment, domain, parentDomain)) {
				insertPayment(conn, payment, contract, domain);
			} // end-if : If it's not REMOVE() or is at agreement or system.
		}

		for (Deduction deduction : draft.getDraftDeductions()) {
			makeRoomDeduction(conn, deduction, contract);
			String expression = deduction.getExpression();
			if (!"REMOVE()".equals(expression)
					|| inSystem(conn, deduction, domain, parentDomain)) {
				insertDeduction(conn, deduction, contract, domain);
			}
		} 
		for (Deduction embargo : draft.getDraftEmbargos()) {
			makeRoomEmbargo(conn, embargo, contract);
			String expression = embargo.getExpression();
			if (!"REMOVE()".equals(expression)
					|| inSystem(conn, embargo, domain, parentDomain)) {
				insertEmbargo(conn, embargo, contract, domain);
			}
		}
		for (Bonus bonus : draft.getDraftBonuses()) {
			makeRoomBonus(conn, bonus, contract);
			String expression = bonus.getExpression();
			if (!"REMOVE()".equals(expression)
					/* Nooo System */) {
				insertBonus(conn, bonus, contract, domain);
			}
		}
	}



	// -------------------------------------------
	// Private
	// -------------------------------------------
	
	

	private static final String CONTRACT_DATA_INSERT = "INSERT INTO "
			+ SQLConstants.CONTRACT_DATA + " ( " + ContractDataColumns.DOMAIN
			+ ", " + ContractDataColumns.CONTRACT + ", "
			+ ContractDataColumns.NAME + ", " + ContractDataColumns.EXPRESSION
			+ ", " + ContractDataColumns.START_DATE + ", "
			+ ContractDataColumns.END_DATE + " ) VALUES (?, ?, ?, ?, ?, ?)";

	private static final String CONTRACT_DATA_DELETE_SQL = "DELETE FROM "
			+ SQLConstants.CONTRACT_DATA + " WHERE " + ContractDataColumns.ID
			+ " = ? ";

	private static void makeRoom(Connection conn, Variable variable,
			Integer contract) throws SQLException {
		ResultSet rs = null;
		PreparedStatement queryStmt = null;
		PreparedStatement insertStmt = null;
		PreparedStatement deleteStmt = null;
		try {

			java.sql.Date endDate = SQLUtils.date2sql(variable.getEndDate());
			java.sql.Date startDate = SQLUtils.date2sql(variable.getStartDate());

			String sql = "SELECT * " + " FROM " + SQLConstants.CONTRACT_DATA
					+ " WHERE " + ContractDataColumns.CONTRACT + "= ? "
					+ " AND " + ContractDataColumns.NAME + " = ? " + " AND ( "
					+ ContractDataColumns.END_DATE + " IS NULL " + " OR "
					+ ContractDataColumns.END_DATE + " >= ?  ) ";

			if (endDate != null)
				sql += " AND " + ContractDataColumns.START_DATE + " <= ? ";

			queryStmt = conn.prepareStatement(sql);

			queryStmt.setInt(1, contract);
			queryStmt.setString(2, variable.getName());
			queryStmt.setDate(3, startDate);

			if (endDate != null)
				queryStmt.setDate(4, endDate);

			deleteStmt = conn.prepareStatement(CONTRACT_DATA_DELETE_SQL);

			insertStmt = conn.prepareStatement(CONTRACT_DATA_INSERT);

			rs = queryStmt.executeQuery();
			while (rs.next()) {
				Date sqlStartDate = rs.getDate(ContractDataColumns.START_DATE);
				Date sqlEndDate = rs.getDate(ContractDataColumns.END_DATE);

				SQLUtils.setInt(insertStmt, 1, rs.getInt(ContractDataColumns.DOMAIN));
				SQLUtils.setInt(insertStmt, 2, rs.getInt(ContractDataColumns.CONTRACT));
				SQLUtils.setString(insertStmt, 3, rs.getString(ContractDataColumns.NAME));
				SQLUtils.setString(insertStmt, 4,
						rs.getString(ContractDataColumns.EXPRESSION));
				SQLUtils.setDate(insertStmt, 5,sqlStartDate);
				SQLUtils.setDate(insertStmt, 6, sqlEndDate);

				if (Period.compare(startDate, sqlStartDate) > 0) {
					SQLUtils.setDate(insertStmt, 6, SQLUtils.date2sql(addDay(startDate, -1)));
					insertStmt.execute();
					SQLUtils.setDate(insertStmt, 6, sqlEndDate);
				}
				if (Period.compare(endDate, sqlEndDate) < 0) {
					SQLUtils.setDate(insertStmt, 5, SQLUtils.date2sql(addDay(endDate, 1)));
					insertStmt.execute();
				}

				// delete old
				Integer id = rs.getInt(ContractDataColumns.ID);
				deleteStmt.setInt(1, id);
				deleteStmt.execute();
			}

		} finally {
			if (rs != null)
				rs.close();
			if (queryStmt != null)
				queryStmt.close();
			if (deleteStmt != null)
				deleteStmt.close();
			if (insertStmt != null)
				insertStmt.close();
		}
	}

	private static boolean inSystem(Connection conn, Variable variable,
			Integer domain, Integer parentDomain) throws SQLException {

		ResultSet rs = null;
		PreparedStatement queryStmt = null;
		try {

			String sql = "SELECT 1 " + " FROM " + SQLConstants.SYSTEM_DATA
					+ " AS DATA" + " WHERE DATA." + SystemDataColumns.DOMAIN
					+ " IN ( ?, ? ) " + " AND DATA." + SystemDataColumns.NAME
					+ " =  ? " + " AND DATA." + SystemDataColumns.START_DATE
					+ " <= ? " + " AND ( DATA." + SystemDataColumns.END_DATE
					+ " >= ? " + " OR DATA." + SystemDataColumns.END_DATE
					+ " IS NULL )";

			queryStmt = conn.prepareStatement(sql);
			queryStmt.setInt(1, domain);
			queryStmt.setInt(2, parentDomain);
			queryStmt.setString(3, variable.getName());
			queryStmt.setDate(4, SQLUtils.date2sql(variable.getEndDate()));
			queryStmt.setDate(5, SQLUtils.date2sql(variable.getStartDate()));

			rs = queryStmt.executeQuery();

			return rs.next();

		} finally {
			if (rs != null)
				rs.close();
			if (queryStmt != null)
				queryStmt.close();
		}
	}

	private static boolean inAgreement(Connection conn, Variable variable,
			Integer contract) throws SQLException {

		ResultSet rs = null;
		PreparedStatement queryStmt = null;
		try {

			String sql = "SELECT 1 " + " FROM "
					+ SQLConstants.AGREEMENT_LEVEL_DATA + " AS DATA" + ", "
					+ SQLConstants.AGREEMENT_LEVEL_CATEGORY + " AS CATEGORY"
					+ ", " + SQLConstants.CONTRACT + " AS CONTRACT "
					+ " WHERE CONTRACT." + ContractColumns.ID + " =  ? "
					+ " AND CONTRACT."
					+ ContractColumns.AGREEMENT_LEVEL
//					+ ContractColumns.AGREEMENT_LEVEL_CATEGORY + " = CATEGORY."
//					+ AgreementLevelCategoryColumns.ID + " AND CATEGORY."
//					+ AgreementLevelCategoryColumns.AGREEMENT_LEVEL
					+ " = DATA." + AgreementLevelDataColumns.AGREEMENT_LEVEL + " AND DATA."
					+ AgreementLevelDataColumns.NAME + " =  ? " + " AND DATA."
					+ AgreementLevelDataColumns.START_DATE + " <= ? "
					+ " AND ( DATA." + AgreementLevelDataColumns.END_DATE
					+ " >= ? " + " OR DATA."
					+ AgreementLevelDataColumns.END_DATE + " IS NULL )";

			queryStmt = conn.prepareStatement(sql);
			queryStmt.setInt(1, contract);
			queryStmt.setString(2, variable.getName());
			queryStmt.setDate(3, SQLUtils.date2sql(variable.getEndDate()));
			queryStmt.setDate(4, SQLUtils.date2sql(variable.getStartDate()));

			rs = queryStmt.executeQuery();

			if (rs.next())
				return true;
			rs.close();

			sql = "SELECT 1 " + " FROM " + SQLConstants.AGREEMENT_DATA
					+ " AS DATA" + ", " + SQLConstants.AGREEMENT_LEVEL
					+ " AS LEVEL" + ", "
					+ SQLConstants.AGREEMENT_LEVEL_CATEGORY + " AS CATEGORY"
					+ ", " + SQLConstants.CONTRACT + " AS CONTRACT "
					+ " WHERE CONTRACT." + ContractColumns.ID + " =  ? "
					+ " AND CONTRACT."
					+ ContractColumns.AGREEMENT_LEVEL
//					+ ContractColumns.AGREEMENT_LEVEL_CATEGORY + " = CATEGORY."
//					+ AgreementLevelCategoryColumns.ID + " AND CATEGORY."
//					+ AgreementLevelCategoryColumns.AGREEMENT_LEVEL
					+ " = LEVEL." + AgreementLevelColumns.ID + " AND LEVEL."
					+ AgreementLevelColumns.AGREEMENT + " =  DATA."
					+ AgreementDataColumns.AGREEMENT + " AND DATA."
					+ AgreementDataColumns.NAME + " =  ? " + " AND DATA."
					+ AgreementDataColumns.START_DATE + " <= ? "
					+ " AND ( DATA." + AgreementDataColumns.END_DATE + " >= ? "
					+ " OR DATA." + AgreementDataColumns.END_DATE
					+ " IS NULL )";

			queryStmt = conn.prepareStatement(sql);
			queryStmt.setInt(1, contract);
			queryStmt.setString(2, variable.getName());
			queryStmt.setDate(3, SQLUtils.date2sql(variable.getEndDate()));
			queryStmt.setDate(4, SQLUtils.date2sql(variable.getStartDate()));

			rs = queryStmt.executeQuery();
			
			return rs.next();

		} finally {
			if (rs != null)
				rs.close();
			if (queryStmt != null)
				queryStmt.close();
		}
	}

	private static void insertData(Connection conn, Variable variable,
			Integer contract, Integer domain) throws SQLException {
		PreparedStatement insertStmt = null;
		try {

			insertStmt = conn.prepareStatement(CONTRACT_DATA_INSERT);

			SQLUtils.setInt(insertStmt, 1, domain);
			SQLUtils.setInt(insertStmt, 2, contract);
			SQLUtils.setString(insertStmt, 3, variable.getName());
			SQLUtils.setString(insertStmt, 4, variable.getExpression());
			SQLUtils.setDate(insertStmt, 5, SQLUtils.date2sql(variable.getStartDate()));
			SQLUtils.setDate(insertStmt, 6, SQLUtils.date2sql(variable.getEndDate()));

			insertStmt.execute();

		} finally {
			if (insertStmt != null)
				insertStmt.close();
		}
	}

	private static final String CONTRACT_PAYMENT_INSERT = "INSERT INTO "
			+ SQLConstants.CONTRACT_PAYMENT + "( "
			+ ContractPaymentColumns.DOMAIN + ", "
			+ ContractPaymentColumns.CONTRACT + ", "
			+ ContractPaymentColumns.TYPE + ", " + ContractPaymentColumns.MONTH
			+ ", " + ContractPaymentColumns.SALARY_TYPE + ", "
			+ ContractPaymentColumns.DESCRIPTION + ", "
			+ ContractPaymentColumns.EXPRESSION + ", "
			+ ContractPaymentColumns.IRPF_EXPRESSION + ", "
			+ ContractPaymentColumns.QUOTE_EXPRESSION + ", "
			+ ContractPaymentColumns.START_DATE + ", "
			+ ContractPaymentColumns.END_DATE + ", "
			+ ContractPaymentColumns.PAYMENT_CONCEPT
			+ " ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?)";

	private static final String CONTRACT_PAYMENT_DELETE_SQL = "DELETE FROM "
			+ SQLConstants.CONTRACT_PAYMENT + " WHERE "
			+ ContractPaymentColumns.ID + " = ? ";

	private static void makeRoomPayment(Connection conn, Payment payment,
			Integer contract) throws SQLException {

		ResultSet rs = null;
		PreparedStatement queryStmt = null;
		PreparedStatement insertStmt = null;
		PreparedStatement deleteStmt = null;
		try {

			java.sql.Date endDate = SQLUtils.date2sql(payment.getEndDate());
			java.sql.Date startDate = SQLUtils.date2sql(payment.getStartDate());

			String sql = "SELECT * " 
					+ " FROM " + SQLConstants.CONTRACT_PAYMENT
					+ " WHERE " + ContractPaymentColumns.ID + " = ? ";

			queryStmt = conn.prepareStatement(sql);

			queryStmt.setInt(1, payment.getId());

			deleteStmt = conn.prepareStatement(CONTRACT_PAYMENT_DELETE_SQL);

			insertStmt = conn.prepareStatement(CONTRACT_PAYMENT_INSERT);

			rs = queryStmt.executeQuery();
			if (rs.next()) {
				Date sqlStartDate = rs.getDate(ContractDataColumns.START_DATE);
				Date sqlEndDate = rs.getDate(ContractDataColumns.END_DATE);

				// Be care of primitive values ( int, short... ) that can be
				// null.
				// With 'getXXX' methods if the value is SQL NULL, the value
				// returned is 0.
				SQLUtils.setInt(insertStmt, 1, rs.getInt(ContractPaymentColumns.DOMAIN));
				SQLUtils.setInt(insertStmt, 2,
						rs.getInt(ContractPaymentColumns.CONTRACT));
				SQLUtils.set(insertStmt, 3, rs.getObject(ContractPaymentColumns.TYPE),
						Types.TINYINT); // Be care type can be null
				SQLUtils.set(insertStmt, 4, rs.getObject(ContractPaymentColumns.MONTH),
						Types.TINYINT); // Be care month can be null
				SQLUtils.set(insertStmt, 5,
						rs.getObject(ContractPaymentColumns.SALARY_TYPE),
						Types.TINYINT); // Be care salary type can be null
				SQLUtils.setString(insertStmt, 6,
						rs.getString(ContractPaymentColumns.DESCRIPTION));
				SQLUtils.setString(insertStmt, 7,
						rs.getString(ContractPaymentColumns.EXPRESSION));
				SQLUtils.setString(insertStmt, 8,
						rs.getString(ContractPaymentColumns.IRPF_EXPRESSION));
				SQLUtils.setString(insertStmt, 9,
						rs.getString(ContractPaymentColumns.QUOTE_EXPRESSION));
				SQLUtils.setDate(insertStmt, 10,
						rs.getDate(ContractPaymentColumns.START_DATE));
				SQLUtils.setDate(insertStmt, 11,
						rs.getDate(ContractPaymentColumns.END_DATE));
				SQLUtils.set(insertStmt, 12,
						rs.getObject(ContractPaymentColumns.PAYMENT_CONCEPT),
						Types.INTEGER); // Be payment_concept month can be null

				if (Period.compare(startDate, sqlStartDate) > 0) {
					SQLUtils.setDate(insertStmt, 11, SQLUtils.date2sql(addDay(startDate, -1)));
					insertStmt.execute();
					SQLUtils.setDate(insertStmt, 11, sqlEndDate); // restores original
															// end date
				}
				if (Period.compare(endDate, sqlEndDate) < 0) {
					SQLUtils.setDate(insertStmt, 10, SQLUtils.date2sql(addDay(endDate, 1)));
					insertStmt.execute();
				}

				// delete old
				Integer id = rs.getInt(ContractPaymentColumns.ID);
				deleteStmt.setInt(1, id);
				deleteStmt.execute();
			}

		} finally {
			if (rs != null)
				rs.close();
			if (queryStmt != null)
				queryStmt.close();
			if (deleteStmt != null)
				deleteStmt.close();
			if (insertStmt != null)
				insertStmt.close();
		}
	}

	private static boolean inAgreement(Connection conn, Payment payment,
			Integer contract) throws SQLException {

		if (payment.getConceptId() == null)
			return false;

		ResultSet rs = null;
		PreparedStatement queryStmt = null;
		try {

			String sql = "SELECT 1 " + " FROM "
					+ SQLConstants.AGREEMENT_PAYMENT + " AS PAYMENT" + ", "
					+ SQLConstants.AGREEMENT_LEVEL + " AS LEVEL" + ", "
					+ SQLConstants.AGREEMENT_LEVEL_CATEGORY + " AS CATEGORY"
					+ ", " + SQLConstants.CONTRACT + " AS CONTRACT "
					+ " WHERE CONTRACT." + ContractColumns.ID + " =  ? "
					+ " AND CONTRACT."
					+ ContractColumns.AGREEMENT_LEVEL
//					+ ContractColumns.AGREEMENT_LEVEL_CATEGORY + " = CATEGORY."
//					+ AgreementLevelCategoryColumns.ID + " AND CATEGORY."
//					+ AgreementLevelCategoryColumns.AGREEMENT_LEVEL
					+ " = LEVEL." + AgreementLevelColumns.ID + " AND LEVEL."
					+ AgreementLevelColumns.AGREEMENT + " =  PAYMENT."
					+ AgreementPaymentColumns.AGREEMENT + " AND PAYMENT."
					+ AgreementPaymentColumns.PAYMENT_CONCEPT + " =  ? "
					+ " AND ( PAYMENT."
					+ AgreementPaymentColumns.END_DATE + " >= ? "
					+ " OR PAYMENT." + AgreementPaymentColumns.END_DATE
					+ " IS NULL )" ;
					if ( payment.getEndDate() != null )
						sql += " AND PAYMENT." + AgreementPaymentColumns.START_DATE
						+ " <= ? " ;

			queryStmt = conn.prepareStatement(sql);
			queryStmt.setInt(1, contract);
			queryStmt.setInt(2, payment.getConceptId());
			queryStmt.setDate(3, SQLUtils.date2sql(payment.getStartDate()));
			
			if ( payment.getEndDate() != null )
				queryStmt.setDate(4, SQLUtils.date2sql(payment.getEndDate()));

			rs = queryStmt.executeQuery();

			return rs.next();

		} finally {
			if (rs != null)
				rs.close();
			if (queryStmt != null)
				queryStmt.close();
		}
	}

	private static boolean inSystem(Connection conn, Payment payment,
			Integer domain, Integer parentDomain) throws SQLException {

		if (payment.getConceptId() == null)
			return false;

		ResultSet rs = null;
		PreparedStatement queryStmt = null;
		try {

			String sql = "SELECT 1 " + " FROM " + SQLConstants.SYSTEM_PAYMENT
					+ " AS PAYMENT" + " WHERE PAYMENT."
					+ SystemPaymentColumns.DOMAIN + " IN ( ?, ? ) "
					+ " AND PAYMENT." + SystemPaymentColumns.PAYMENT_CONCEPT
					+ " =  ? " + " AND PAYMENT."
					+ SystemPaymentColumns.START_DATE + " <= ? "
					+ " AND ( PAYMENT." + SystemPaymentColumns.END_DATE
					+ " >= ? " + " OR PAYMENT." + SystemPaymentColumns.END_DATE
					+ " IS NULL )";

			queryStmt = conn.prepareStatement(sql);
			queryStmt.setInt(1, domain);
			queryStmt.setInt(2, parentDomain);
			queryStmt.setInt(3, payment.getConceptId());
			queryStmt.setDate(4, SQLUtils.date2sql(payment.getEndDate()));
			queryStmt.setDate(5, SQLUtils.date2sql(payment.getStartDate()));

			rs = queryStmt.executeQuery();

			return rs.next();

		} finally {
			if (rs != null)
				rs.close();
			if (queryStmt != null)
				queryStmt.close();
		}
	}

	private static void insertPayment(Connection conn, Payment payment,
			Integer contract, Integer domain) throws SQLException {
		PreparedStatement insertStmt = null;
		try {
			if (payment.getConceptId() != null)
				clearConceptInherit(conn, payment);

			insertStmt = conn.prepareStatement(CONTRACT_PAYMENT_INSERT);

			SQLUtils.setInt(insertStmt, 1, domain);
			SQLUtils.setInt(insertStmt, 2, contract);

			SQLUtils.setShort(insertStmt, 3, SQLUtils.enum2Short(payment.getType()));
			SQLUtils.setShort(insertStmt, 4, payment.getMonth());
			SQLUtils.setShort(insertStmt, 5, SQLUtils.enum2Short(payment.getSalaryType()));

			SQLUtils.setString(insertStmt, 6, payment.getDescriptionTemplate());
			SQLUtils.setString(insertStmt, 7, payment.getExpression());
			SQLUtils.setString(insertStmt, 8, payment.getIrpfExpression());
			SQLUtils.setString(insertStmt, 9, payment.getQuoteExpression());
			SQLUtils.setDate(insertStmt, 10, SQLUtils.date2sql(payment.getStartDate()));
			SQLUtils.setDate(insertStmt, 11, SQLUtils.date2sql(payment.getEndDate()));

			SQLUtils.setInt(insertStmt, 12, payment.getConceptId());

			insertStmt.execute();

		} finally {
			if (insertStmt != null)
				insertStmt.close();
		}
	}

	private static void clearConceptInherit(Connection conn, Payment payment)
			throws SQLException {

		ResultSet rs = null;
		PreparedStatement stmt = null;
		try {
			String sql = "SELECT * FROM " + SQLConstants.PAYMENT_CONCEPT
					+ " WHERE " + PaymentConceptColumns.ID + " = ? ";

			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, payment.getConceptId());

			rs = stmt.executeQuery();
			if (rs.next()) {
				Payment.Type type = SQLUtils.getPaymentType(rs
						.getObject(PaymentConceptColumns.TYPE));
				if (payment.getType() == type)
					payment.setType(null);
				String description = rs
						.getString(PaymentConceptColumns.DESCRIPTION);
				if (SQLUtils.sameString(description, payment.getDescription()))
					payment.setDescription(null);
				String expression = rs
						.getString(PaymentConceptColumns.EXPRESSION);
				if (SQLUtils.sameString(expression, payment.getExpression()))
					payment.setExpression(null);
				String irpfExpression = rs
						.getString(PaymentConceptColumns.IRPF_EXPRESSION);
				if (SQLUtils.sameString(irpfExpression, payment.getIrpfExpression()))
					payment.setIrpfExpression(null);
				String quoteExpression = rs
						.getString(PaymentConceptColumns.QUOTE_EXPRESSION);
				if (SQLUtils.sameString(quoteExpression, payment.getQuoteExpression()))
					payment.setQuoteExpression(null);

			}
		} finally {
			if (rs != null)
				rs.close();
			if (stmt != null)
				stmt.close();
		}
	}

	private static final String CONTRACT_BONUS_INSERT = "INSERT INTO "
			+ SQLConstants.CONTRACT_BONUS + "( "
			+ ContractBonusColumns.DOMAIN + ", "
			+ ContractBonusColumns.CONTRACT + ", "
			+ ContractBonusColumns.DESCRIPTION + ", "
			+ ContractBonusColumns.EXPRESSION + ", "
			+ ContractBonusColumns.START_DATE + ", "
			+ ContractBonusColumns.END_DATE + ", "
			+ ContractBonusColumns.BONUS_CONCEPT
			+ " ) VALUES (?, ?, ?, ?, ?, ?, ?)";

	private static final String CONTRACT_DEDUCTION_INSERT = "INSERT INTO "
			+ SQLConstants.CONTRACT_DEDUCTION + "( "
			+ ContractDeductionColumns.DOMAIN + ", "
			+ ContractDeductionColumns.CONTRACT + ", "
			+ ContractDeductionColumns.TYPE + ", "
			+ ContractDeductionColumns.MONTH + ", "
			+ ContractDeductionColumns.DESCRIPTION + ", "
			+ ContractDeductionColumns.EXPRESSION + ", "
			+ ContractDeductionColumns.START_DATE + ", "
			+ ContractDeductionColumns.END_DATE + ", "
			+ ContractDeductionColumns.DEDUCTION_CONCEPT
			+ " ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

	private static final String CONTRACT_EMBARGO_INSERT = "INSERT INTO "
			+ SQLConstants.CONTRACT_EMBARGO + "( "
			+ ContractEmbargoColumns.DOMAIN + ", "
			+ ContractEmbargoColumns.CONTRACT + ", "
			+ ContractEmbargoColumns.DESCRIPTION + ", "
			+ ContractEmbargoColumns.EXPRESSION + ", "
			+ ContractEmbargoColumns.START_DATE + ", "
			+ ContractEmbargoColumns.END_DATE 
			+ " ) VALUES (?, ?, ?, ?, ?, ?)";

	private static final String CONTRACT_DEDUCTION_DELETE_SQL = "DELETE FROM "
			+ SQLConstants.CONTRACT_DEDUCTION + " WHERE "
			+ ContractDeductionColumns.ID + " = ? ";

	private static final String CONTRACT_EMBARGO_DELETE_SQL = "DELETE FROM "
			+ SQLConstants.CONTRACT_EMBARGO + " WHERE "
			+ ContractEmbargoColumns.ID + " = ? ";

	private static final String CONTRACT_BONUS_DELETE_SQL = "DELETE FROM "
			+ SQLConstants.CONTRACT_BONUS + " WHERE "
			+ ContractBonusColumns.ID + " = ? ";
	
	
	//@formatter:off
	private static final String SALARY_EMBARGO_UPDATE_SQL =
			" UPDATE"
			+ " " + SQLConstants.SALARY_EMBARGO 
			+ " SET " + SalaryEmbargoColumns.CONTRACT_EMBARGO + " = ? "
			+ " WHERE " + SalaryEmbargoColumns.CONTRACT_EMBARGO + " = ? "
			+ " AND " + SalaryEmbargoColumns.SALARY + " IN (  "
			+ " SELECT " + SalaryColumns.ID  
			+ " FROM " + SQLConstants.SALARY 
			+ " WHERE " + SalaryColumns.CONTRACT + " = ? " 
			+ " AND " + SalaryColumns.START_DATE + " >= ? "
			+ " AND " + SalaryColumns.END_DATE + " <= ? )"
			;
			
	//@formatter:on

	private static void makeRoomDeduction(Connection conn, Deduction deduction,
			Integer contract) throws SQLException {

		ResultSet rs = null;
		PreparedStatement queryStmt = null;
		PreparedStatement insertStmt = null;
		PreparedStatement deleteStmt = null;
		try {

			java.sql.Date endDate = SQLUtils.date2sql(deduction.getEndDate());
			java.sql.Date startDate = SQLUtils.date2sql(deduction.getStartDate());

			String sql = "SELECT * " + " FROM "
					+ SQLConstants.CONTRACT_DEDUCTION + " WHERE "
					+ ContractDeductionColumns.ID + " = ? ";

			queryStmt = conn.prepareStatement(sql);
			queryStmt.setInt(1, deduction.getId());

			deleteStmt = conn.prepareStatement(CONTRACT_DEDUCTION_DELETE_SQL);
			insertStmt = conn.prepareStatement(CONTRACT_DEDUCTION_INSERT);

			rs = queryStmt.executeQuery();
			if (rs.next()) {
				Date sqlStartDate = rs.getDate(ContractDataColumns.START_DATE);
				Date sqlEndDate = rs.getDate(ContractDataColumns.END_DATE);

				// Be care of primitive values ( int, short... ) that can be
				// null.
				// With 'getXXX' methods if the value is SQL NULL, the value
				// returned is 0.
				SQLUtils.setInt(insertStmt, 1,
						rs.getInt(ContractDeductionColumns.DOMAIN)); // NOT NULL
				SQLUtils.setInt(insertStmt, 2,
						rs.getInt(ContractDeductionColumns.CONTRACT)); // NOT  NULL
				SQLUtils.set(insertStmt, 3, rs.getObject(ContractDeductionColumns.TYPE),
						Types.TINYINT); // DEFAULT NULL
				SQLUtils.set(insertStmt, 4,
						rs.getObject(ContractDeductionColumns.MONTH),
						Types.TINYINT); // DEFAULT NULL
				SQLUtils.setString(insertStmt, 5,
						rs.getString(ContractDeductionColumns.DESCRIPTION));
				SQLUtils.setString(insertStmt, 6,
						rs.getString(ContractDeductionColumns.EXPRESSION));
				SQLUtils.setDate(insertStmt, 7, sqlStartDate);
				SQLUtils.setDate(insertStmt, 8, sqlEndDate);

				SQLUtils.set(insertStmt, 9,
						rs.getObject(ContractDeductionColumns.DEDUCTION_CONCEPT), Types.INTEGER);

				if (Period.compare(startDate, sqlStartDate) > 0) {
					SQLUtils.setDate(insertStmt, 8, SQLUtils.date2sql(addDay(startDate, -1)));
					insertStmt.execute();
					SQLUtils.setDate(insertStmt, 8, sqlEndDate); // restores original end date for subsequent inserts
				}
				if (Period.compare(endDate, sqlEndDate) < 0) {
					SQLUtils.setDate(insertStmt, 7, SQLUtils.date2sql(addDay(endDate, 1)));
					insertStmt.execute();
				}

				// delete old
				Integer id = rs.getInt(ContractPaymentColumns.ID);
				deleteStmt.setInt(1, id);
				deleteStmt.execute();
			}

		} finally {
			if (rs != null)
				rs.close();
			if (queryStmt != null)
				queryStmt.close();
			if (deleteStmt != null)
				deleteStmt.close();
			if (insertStmt != null)
				insertStmt.close();
		}
	}

	private static void makeRoomEmbargo(Connection conn, Deduction deduction,
			Integer contract) throws SQLException {

		ResultSet rs = null;
		Statement checksStmt = null;
		PreparedStatement queryStmt = null;
		PreparedStatement insertStmt = null;
		PreparedStatement deleteStmt = null;
		PreparedStatement salaryStmt = null;
		try {

			java.sql.Date endDate = SQLUtils.date2sql(deduction.getEndDate());
			java.sql.Date startDate = SQLUtils.date2sql(deduction.getStartDate());

			String querySql = "SELECT * " + " FROM "
					+ SQLConstants.CONTRACT_EMBARGO + " WHERE "
					+ ContractEmbargoColumns.ID + " = ? ";

			queryStmt = conn.prepareStatement(querySql);
			queryStmt.setInt(1, deduction.getId());

			checksStmt = conn.createStatement();
			deleteStmt = conn.prepareStatement(CONTRACT_EMBARGO_DELETE_SQL);
			salaryStmt = conn.prepareStatement(SALARY_EMBARGO_UPDATE_SQL);
			insertStmt = conn.prepareStatement(CONTRACT_EMBARGO_INSERT, Statement.RETURN_GENERATED_KEYS);

			rs = queryStmt.executeQuery();
			if (rs.next()) {
				Date sqlStartDate = rs.getDate(ContractEmbargoColumns.START_DATE);
				Date sqlEndDate = rs.getDate(ContractEmbargoColumns.END_DATE);

				// Be care of primitive values ( int, short... ) that can be
				// null.
				// With 'getXXX' methods if the value is SQL NULL, the value
				// returned is 0.
				SQLUtils.setInt(insertStmt, 1,
						rs.getInt(ContractEmbargoColumns.DOMAIN)); // NOT NULL
				SQLUtils.setInt(insertStmt, 2,
						rs.getInt(ContractEmbargoColumns.CONTRACT)); // NOT  NULL
				SQLUtils.setString(insertStmt, 3,
						rs.getString(ContractEmbargoColumns.DESCRIPTION));
				SQLUtils.setString(insertStmt, 4,
						rs.getString(ContractEmbargoColumns.EXPRESSION));
				SQLUtils.setDate(insertStmt, 5, sqlStartDate);
				SQLUtils.setDate(insertStmt, 6, sqlEndDate);


				SQLUtils.setInt(salaryStmt, 2, rs.getInt(ContractPaymentColumns.ID));
				SQLUtils.setInt(salaryStmt, 3, rs.getInt(ContractPaymentColumns.CONTRACT));
				SQLUtils.setDate(salaryStmt, 4, sqlStartDate);
				SQLUtils.setDate(salaryStmt, 5, sqlEndDate);

				if (Period.compare(startDate, sqlStartDate) > 0) {
					SQLUtils.setDate(insertStmt, 6, SQLUtils.date2sql(addDay(startDate, -1)));
					insertStmt.execute();
					
					ResultSet generatedKeys = insertStmt.getGeneratedKeys();
					generatedKeys.next();
					int contractEmbargoId = generatedKeys.getInt(1);
					SQLUtils.setInt( salaryStmt, 1, contractEmbargoId);
					SQLUtils.setDate( salaryStmt, 5, SQLUtils.date2sql(addDay(startDate, -1)));
					salaryStmt.execute();
					
					SQLUtils.setDate(insertStmt, 6, sqlEndDate); // restores original end date for subsequent inserts
					SQLUtils.setDate(salaryStmt, 5, sqlEndDate);
				}
				if (Period.compare(endDate, sqlEndDate) < 0) {
					SQLUtils.setDate(insertStmt, 5, SQLUtils.date2sql(addDay(endDate, 1)));
					insertStmt.execute();

					ResultSet generatedKeys = insertStmt.getGeneratedKeys();
					generatedKeys.next();
					int contractEmbargoId = generatedKeys.getInt(1);
					SQLUtils.setInt( salaryStmt, 1, contractEmbargoId);
					SQLUtils.setDate( salaryStmt, 4, SQLUtils.date2sql(addDay(endDate, 1)));
					salaryStmt.execute();
				}

				
				// clean salaries
				//salaryStmt.setNull(1, Types.INTEGER);
				SQLUtils.setInt( salaryStmt, 1, -666);
				SQLUtils.setDate(salaryStmt, 4, startDate);
				salaryStmt.setDate(4, startDate);
				if ( endDate == null )
					salaryStmt.setString(5, "2666-01-01");
				else
					SQLUtils.setDate(salaryStmt, 5, endDate);

				checksStmt.execute("SET FOREIGN_KEY_CHECKS=0;");
				salaryStmt.execute();
				checksStmt.execute("SET FOREIGN_KEY_CHECKS=1;");

				// delete old
				deleteStmt.setInt(1, rs.getInt(ContractPaymentColumns.ID));
				deleteStmt.execute();
				
			}

		} finally {
			if (rs != null)
				rs.close();
			if (queryStmt != null)
				queryStmt.close();
			if (checksStmt != null)
				checksStmt.close();
			if (deleteStmt != null)
				deleteStmt.close();
			if (insertStmt != null)
				insertStmt.close();
			if (salaryStmt != null)
				salaryStmt.close();
		}
	}

	private static void makeRoomBonus(Connection conn, Bonus bonus,
			Integer contract) throws SQLException {

		ResultSet rs = null;
		PreparedStatement queryStmt = null;
		PreparedStatement insertStmt = null;
		PreparedStatement deleteStmt = null;
		try {

			java.sql.Date endDate = SQLUtils.date2sql(bonus.getEndDate());
			java.sql.Date startDate = SQLUtils.date2sql(bonus.getStartDate());

			String sql = "SELECT * " + " FROM "
					+ SQLConstants.CONTRACT_BONUS + " WHERE "
					+ ContractBonusColumns.ID + " = ? ";

			queryStmt = conn.prepareStatement(sql);
			queryStmt.setInt(1, bonus.getId());

			deleteStmt = conn.prepareStatement(CONTRACT_BONUS_DELETE_SQL);
			insertStmt = conn.prepareStatement(CONTRACT_BONUS_INSERT);

			rs = queryStmt.executeQuery();
			if (rs.next()) {
				Date sqlStartDate = rs.getDate(ContractDataColumns.START_DATE);
				Date sqlEndDate = rs.getDate(ContractDataColumns.END_DATE);

				// Be care of primitive values ( int, short... ) that can be
				// null.
				// With 'getXXX' methods if the value is SQL NULL, the value
				// returned is 0.
				SQLUtils.setInt(insertStmt, 1,
						rs.getInt(ContractBonusColumns.DOMAIN)); // NOT NULL
				SQLUtils.setInt(insertStmt, 2,
						rs.getInt(ContractBonusColumns.CONTRACT)); // NOT  NULL
				SQLUtils.setString(insertStmt, 3,
						rs.getString(ContractBonusColumns.DESCRIPTION));
				SQLUtils.setString(insertStmt, 4,
						rs.getString(ContractBonusColumns.EXPRESSION));
				SQLUtils.setDate(insertStmt, 5, sqlStartDate);
				SQLUtils.setDate(insertStmt, 6, sqlEndDate);
				SQLUtils.set(insertStmt, 7,
						rs.getObject(ContractBonusColumns.BONUS_CONCEPT), Types.INTEGER);

				if (Period.compare(startDate, sqlStartDate) > 0) {
					SQLUtils.setDate(insertStmt, 6, SQLUtils.date2sql(addDay(startDate, -1)));
					insertStmt.execute();
					SQLUtils.setDate(insertStmt, 6, sqlEndDate); // restores original end date for subsequent inserts
				}
				if (Period.compare(endDate, sqlEndDate) < 0) {
					SQLUtils.setDate(insertStmt, 5, SQLUtils.date2sql(addDay(endDate, 1)));
					insertStmt.execute();
				}

				// delete old
				Integer id = rs.getInt(ContractBonusColumns.ID);
				deleteStmt.setInt(1, id);
				deleteStmt.execute();
			}

		} finally {
			if (rs != null)
				rs.close();
			if (queryStmt != null)
				queryStmt.close();
			if (deleteStmt != null)
				deleteStmt.close();
			if (insertStmt != null)
				insertStmt.close();
		}
	}

	private static boolean inSystem(Connection conn, Deduction deduction,
			Integer domain, Integer parentDomain) throws SQLException {

		if (deduction.getConceptId() == null)
			return false;

		ResultSet rs = null;
		PreparedStatement queryStmt = null;
		try {

			String sql = "SELECT 1 " + " FROM " + SQLConstants.SYSTEM_DEDUCTION
					+ " AS DEDUCTION" + " WHERE DEDUCTION."
					+ SystemDeductionColumns.DOMAIN + " IN ( ?, ? ) "
					+ " AND DEDUCTION."
					+ SystemDeductionColumns.DEDUCTION_CONCEPT + " =  ? "
					+ " AND DEDUCTION." + SystemDeductionColumns.START_DATE
					+ " <= ? " + " AND ( DEDUCTION."
					+ SystemDeductionColumns.END_DATE + " >= ? "
					+ " OR DEDUCTION." + SystemDeductionColumns.END_DATE
					+ " IS NULL )";

			queryStmt = conn.prepareStatement(sql);
			queryStmt.setInt(1, domain);
			queryStmt.setInt(2, parentDomain);
			queryStmt.setInt(3, deduction.getConceptId());
			queryStmt.setDate(4, SQLUtils.date2sql(deduction.getEndDate()));
			queryStmt.setDate(5, SQLUtils.date2sql(deduction.getStartDate()));

			rs = queryStmt.executeQuery();

			return rs.next();

		} finally {
			if (rs != null)
				rs.close();
			if (queryStmt != null)
				queryStmt.close();
		}
	}

	private static void insertDeduction(Connection conn, Deduction deduction,
			Integer contract, Integer domain) throws SQLException {
		PreparedStatement insertStmt = null;
		try {
			if (deduction.getConceptId() != null)
				clearConceptInherit(conn, deduction);

			insertStmt = conn.prepareStatement(CONTRACT_DEDUCTION_INSERT);

			SQLUtils.setInt(insertStmt, 1, domain);
			SQLUtils.setInt(insertStmt, 2, contract);
			SQLUtils.setShort(insertStmt, 3, SQLUtils.enum2Short(deduction.getType()));
			SQLUtils.setShort(insertStmt, 4, deduction.getMonth());
			SQLUtils.setString(insertStmt, 5, deduction.getDescriptionTemplate());
			SQLUtils.setString(insertStmt, 6, deduction.getExpression());
			SQLUtils.setDate(insertStmt, 7, SQLUtils.date2sql(deduction.getStartDate()));
			SQLUtils.setDate(insertStmt, 8, SQLUtils.date2sql(deduction.getEndDate()));

			SQLUtils.setInt(insertStmt, 9, deduction.getConceptId());

			insertStmt.execute();

		} finally {
			if (insertStmt != null)
				insertStmt.close();
		}
	}

	private static void insertEmbargo(Connection conn, Deduction deduction,
			Integer contract, Integer domain) throws SQLException {
		PreparedStatement insertStmt = null;
		try {
			insertStmt = conn.prepareStatement(CONTRACT_EMBARGO_INSERT);

			SQLUtils.setInt(insertStmt, 1, domain);
			SQLUtils.setInt(insertStmt, 2, contract);
			SQLUtils.setString(insertStmt, 3, deduction.getDescriptionTemplate());
			SQLUtils.setString(insertStmt, 4, deduction.getExpression());
			SQLUtils.setDate(insertStmt, 5, SQLUtils.date2sql(deduction.getStartDate()));
			SQLUtils.setDate(insertStmt, 6, SQLUtils.date2sql(deduction.getEndDate()));

			insertStmt.execute();

		} finally {
			if (insertStmt != null)
				insertStmt.close();
		}
	}

	private static void insertBonus(Connection conn, Bonus bonus,
			Integer contract, Integer domain) throws SQLException {
		PreparedStatement insertStmt = null;
		try {
			if (bonus.getConceptId() != null)
				clearConceptInherit(conn, bonus);

			insertStmt = conn.prepareStatement(CONTRACT_BONUS_INSERT);

			SQLUtils.setInt(insertStmt, 1, domain);
			SQLUtils.setInt(insertStmt, 2, contract);
			SQLUtils.setString(insertStmt, 3, bonus.getDescriptionTemplate());
			SQLUtils.setString(insertStmt, 4, bonus.getExpression());
			SQLUtils.setDate(insertStmt, 5,SQLUtils.date2sql(bonus.getStartDate()));
			SQLUtils.setDate(insertStmt, 6, SQLUtils.date2sql(bonus.getEndDate()));
			SQLUtils.setInt(insertStmt, 7, bonus.getConceptId());

			insertStmt.execute();

		} finally {
			if (insertStmt != null)
				insertStmt.close();
		}
	}

	private static void clearConceptInherit(Connection conn, Deduction deduction)
			throws SQLException {

		ResultSet rs = null;
		PreparedStatement stmt = null;
		try {
			String sql = "SELECT * FROM " + SQLConstants.DEDUCTION_CONCEPT
					+ " WHERE " + DeductionConceptColumns.ID + " = ? ";

			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, deduction.getConceptId());

			rs = stmt.executeQuery();
			if (rs.next()) {
				Deduction.Type type = SQLUtils.getDeductionType(rs
						.getObject(DeductionConceptColumns.TYPE));
				if (deduction.getType() == type)
					deduction.setType(null);
				String description = rs
						.getString(DeductionConceptColumns.DESCRIPTION);
				if (SQLUtils.sameString(description, deduction.getDescription()))
					deduction.setDescription(null);
				String expression = rs
						.getString(DeductionConceptColumns.EXPRESSION);
				if (SQLUtils.sameString(expression, deduction.getExpression()))
					deduction.setExpression(null);

			}
		} finally {
			if (rs != null)
				rs.close();
			if (stmt != null)
				stmt.close();
		}
	}

	private static void clearConceptInherit(Connection conn, Bonus bonus)
			throws SQLException {

		ResultSet rs = null;
		PreparedStatement stmt = null;
		try {
			String sql = "SELECT * FROM " + SQLConstants.BONUS_CONCEPT
					+ " WHERE " + BonusConceptColumns.ID + " = ? ";

			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, bonus.getConceptId());

			rs = stmt.executeQuery();
			if (rs.next()) {
				String description = rs
						.getString(BonusConceptColumns.DESCRIPTION);
				if (SQLUtils.sameString(description, bonus.getDescription()))
					bonus.setDescription(null);
				String expression = rs
						.getString(BonusConceptColumns.EXPRESSION);
				if (SQLUtils.sameString(expression, bonus.getExpression()))
					bonus.setExpression(null);
			}
		} finally {
			if (rs != null)
				rs.close();
			if (stmt != null)
				stmt.close();
		}
	}

	public static Payment getPaymentConceptByName(Connection conn, String name) throws SQLException {
		ResultSet rs = null;
		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement("SELECT * FROM " + SQLConstants.PAYMENT_CONCEPT +  
					" WHERE " + PaymentConceptColumns.CODE + " = ? ");
			rs = stmt.executeQuery();
			if ( ! rs.next() ) 
				return null;
			Payment paymentConcept = new Payment();

			paymentConcept.setId(rs.getInt(PaymentConceptColumns.ID));
			paymentConcept
					.setName(rs.getString(PaymentConceptColumns.CODE));
			paymentConcept.setType(SQLUtils.getPaymentType(rs
					.getInt(PaymentConceptColumns.TYPE)));
			paymentConcept.setDescription(rs
					.getString(PaymentConceptColumns.DESCRIPTION));
			paymentConcept.setExpression(rs
					.getString(PaymentConceptColumns.EXPRESSION));
			paymentConcept.setIrpfExpression(rs
					.getString(PaymentConceptColumns.IRPF_EXPRESSION));
			paymentConcept.setQuoteExpression(rs
					.getString(PaymentConceptColumns.QUOTE_EXPRESSION));
			
			return paymentConcept;
			
		} finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}

	private static Date addDay(Date date, int days) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DAY_OF_MONTH, days);
		return calendar.getTime();
	}
}
