package com.esferalia.aon.payroll.calculator.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.function.Supplier;

import com.code.aon.common.util.CommonUtil;
import com.code.aon.ql.OrderByList;
import com.esferalia.aon.payroll.calculator.LRUCache;
import com.esferalia.aon.payroll.calculator.LRUCacheFactory;
import com.esferalia.aon.payroll.calculator.sql.SQLContractSalaryCalculatorContext.AgreementContextKey;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.sql.SQLConstants.AgreementLevelDataColumns;
import com.esferalia.aon.salary.expression.ExpressionContext;
import com.esferalia.aon.salary.expression.ExpressionContext.DeferredExpressionVariable;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.salary.expression.ExpressionImpl;
import com.esferalia.aon.salary.expression.ExpressionScope;
import com.esferalia.aon.salary.expression.Period;

public class SQLAgreementContextFactory implements
		LRUCacheFactory<AgreementContextKey, ExpressionContext> {

	// @formatter:off
	private static final String AGREEMENT_DATA_SQL = "SELECT * "
			+ " FROM `agreement_data`" + " WHERE domain = ? "
			+ " AND agreement = ? " + " AND start_date <= ? "
			+ " AND ( end_date IS NULL " + " OR end_date >= ? )";
	// @formatter:on

	// @formatter:off
	private static final String AGREEMENT_LEVEL_DATA_SQL = "SELECT * "
			+ " FROM `agreement_level_data`" + " WHERE domain = ? "
			+ " AND agreement_level = ? " + " AND start_date <= ? "
			+ " AND ( end_date IS NULL " + " OR end_date >= ? )";
	// @formatter:on

	private Date endDate;
	private Date startDate;

	private PreparedStatement dataStmts[];
	private PreparedStatement agreementDataStmt;
	private PreparedStatement agreementLevelDataStmt;

	private LRUCache<AgreementKey, ExpressionContext> agreementDataCache;

	private Supplier<ExpressionContext> systemExpressionContextSupplier;

	public SQLAgreementContextFactory(Connection conn,
			Supplier<ExpressionContext> systemExpressionCtxtSupplier,
			Date startDate, Date endDate, OrderByList order)
			throws SQLException, ExpressionException {
		this.endDate = endDate;
		this.startDate = startDate;
		initAgreementDataContextCache();
		initAgreementStmt(conn, startDate, endDate, order);
		this.systemExpressionContextSupplier = systemExpressionCtxtSupplier;
	}

	public void close() throws SQLException {
		for (int i = 0; i < dataStmts.length; i++) {
			if (dataStmts[i] != null) {
				dataStmts[i].close();
				dataStmts[i] = null;
			}
		}
	}

	@Override
	protected void finalize() throws Throwable {
		close();
		super.finalize();
	}

	public ExpressionContext create(AgreementKey agreementKey) {
		try {
			ExpressionContext expressionCtx = new ExpressionContext(
					systemExpressionContextSupplier.get());

			agreementDataStmt.setInt(1, agreementKey.getDomain());
			agreementDataStmt.setInt(2, agreementKey.getId());
			loadData(agreementDataStmt, expressionCtx);

			return expressionCtx;
		} catch (SQLException e) {
			// TODO : ¿ Deberiamos crear una excepción espefícica como
			// CreateException ?
			throw new RuntimeException(e);
		}
	}

	public ExpressionContext create(AgreementContextKey key) {

		if (key == null || key.getAgreementId() == null) {
			return systemExpressionContextSupplier.get();
		} // LRUCache<K,V> as LinkedHasMap accepts null keys and/or values.

		try {
			ExpressionContext allContext = agreementDataCache
					.get(new AgreementKey(key.getAgreementId(), key.getDomain()));
			ExpressionContext levelCtx = new ExpressionContext(allContext);

			agreementLevelDataStmt.setInt(1, key.getDomain());
			agreementLevelDataStmt.setInt(2, key.getAgreementLevelId());
			loadData(agreementLevelDataStmt, levelCtx);

			return levelCtx;
		} catch (SQLException e) {
			// TODO : ¿ Deberiamos crear una excepción espefícica como
			// CreateException ?
			throw new RuntimeException(e);
		}
	};

	public ExpressionContext getSystemExpressionContext() {
		return systemExpressionContextSupplier.get();
	}

	public ExpressionContext getAgreementDataContext(int agreementId) {
		return agreementDataCache.get(agreementId);
	}

	// ------------------------------------------
	// La ropa interior
	// ------------------------------------------

	private void loadData(PreparedStatement stmt, ExpressionContext context)
			throws SQLException {
		ResultSet rs = null;
		try {
			rs = stmt.executeQuery();
			while (rs.next()) {
				ExpressionImpl expr = new ExpressionImpl();
				expr.setScope(ExpressionScope.AGREEMENT);
				expr.setName(rs.getString(AgreementLevelDataColumns.NAME));
				expr.setExpression(rs
						.getString(AgreementLevelDataColumns.EXPRESSION));
				Date start = Period.max(
						rs.getDate(AgreementLevelDataColumns.START_DATE),
						startDate);
				Date end = Period
						.min(rs.getDate(AgreementLevelDataColumns.END_DATE),
								endDate);
				try {
					context.addExpression(expr, start, end);
				} catch (Exception e) {
					DeferredExpressionVariable<Object> variable = new DeferredExpressionVariable<Object>(
							start, end, expr);
					context.putVariable(expr.getName(), variable);
				}
			}
		} finally {
			if (rs != null)
				rs.close();
		}
	}

	private void initAgreementStmt(Connection connection, Date startDate,
			Date endDate, OrderByList orderByList) throws SQLException {
		dataStmts = new PreparedStatement[2];

		String agreementDataSql = SQLContractSalaryCalculatorContext.orderBy(
				AGREEMENT_DATA_SQL, orderByList);

		agreementDataStmt = connection.prepareStatement(agreementDataSql);
		agreementDataStmt.setDate(3, new java.sql.Date(endDate.getTime()));
		agreementDataStmt.setDate(4, new java.sql.Date(startDate.getTime()));
		dataStmts[0] = agreementDataStmt;

		String agreementLevelDataSql = SQLContractSalaryCalculatorContext
				.orderBy(AGREEMENT_LEVEL_DATA_SQL, orderByList);
		agreementLevelDataStmt = connection
				.prepareStatement(agreementLevelDataSql);
		agreementLevelDataStmt.setDate(3, new java.sql.Date(endDate.getTime()));
		agreementLevelDataStmt.setDate(4,
				new java.sql.Date(startDate.getTime()));
		dataStmts[1] = agreementLevelDataStmt;
	}

	private Long getMonthDays(Date startDate, Date endDate) {
		Date startDay = CommonUtil.getMonthFirstDay(startDate);
		Date endDay = CommonUtil.getMonthLastDay(endDate);
		Long monthDays = CommonUtil.getDaysBetweenDates(startDay, endDay);
		monthDays += 1;
		return monthDays;
	}

	private void initAgreementDataContextCache() {
		LRUCacheFactory<AgreementKey, ExpressionContext> factory = new LRUCacheFactory<AgreementKey, ExpressionContext>() {
			@Override
			public ExpressionContext create(AgreementKey agreementKey) {
				return SQLAgreementContextFactory.this.create(agreementKey);
			}
		};
		this.agreementDataCache = new LRUCache<AgreementKey, ExpressionContext>(
				25, factory);
	}

	// ------------------------------------------------------------------------
	//

}
