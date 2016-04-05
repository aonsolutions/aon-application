package com.esferalia.aon.payroll.calculator.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import com.code.aon.common.util.CommonUtil;
import com.code.aon.ql.OrderByList;
import com.esferalia.aon.payroll.calculator.LRUCache;
import com.esferalia.aon.payroll.calculator.LRUCacheFactory;
import com.esferalia.aon.payroll.calculator.sql.SQLContractSalaryCalculatorContext.AgreementContextKey;
import com.esferalia.aon.payroll.sql.SQLConstants.AgreementLevelDataColumns;
import com.esferalia.aon.salary.expression.ExpressionContext;
import com.esferalia.aon.salary.expression.ExpressionContext.DeferredExpressionVariable;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.salary.expression.ExpressionImpl;
import com.esferalia.aon.salary.expression.ExpressionScope;
import com.esferalia.aon.salary.expression.IExpressionVariable;
import com.esferalia.aon.salary.expression.ITimedResult;
import com.esferalia.aon.salary.expression.ITimedVariable;
import com.esferalia.aon.salary.expression.Period;
import com.esferalia.aon.watson.util.Pair;

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

	// @formatter:off
	private static final String IS_AGREEMENT_DOMAIN_SQL = "SELECT * "
			+ " FROM `agreement`" 
			+ " WHERE `domain` = ? "
			+ " AND `id` = ? " ;
	// @formatter:on

	private Date endDate;
	private Date startDate;

	private PreparedStatement dataStmts[];
	private PreparedStatement agreementDataStmt;
	private PreparedStatement agreementLevelDataStmt;
	private PreparedStatement isAgreementDomainStmt;

	private LRUCache<AgreementKey, ExpressionContext> agreementDataCache;

	private Supplier<ExpressionContext> systemExpressionContextSupplier;

	private Map<AgreementKey, Map<String, Pair<ITimedVariable<?>, ITimedVariable<?>>>> agreementDataRedefined;
	private Map<AgreementContextKey, Map<String, Pair<ITimedVariable<?>, ITimedVariable<?>>>> agreementLevelRedefined;

	public SQLAgreementContextFactory(Connection conn,
			Supplier<ExpressionContext> systemExpressionCtxtSupplier,
			Date startDate, Date endDate, OrderByList order)
			throws SQLException, ExpressionException {
		this.endDate = endDate;
		this.startDate = startDate;
		initAgreementDataContextCache();
		initAgreementStmt(conn, startDate, endDate, order);
		this.systemExpressionContextSupplier = systemExpressionCtxtSupplier;

		agreementDataRedefined = new HashMap<AgreementKey, Map<String, Pair<ITimedVariable<?>, ITimedVariable<?>>>>();
		agreementLevelRedefined = new HashMap<AgreementContextKey, Map<String, Pair<ITimedVariable<?>, ITimedVariable<?>>>>();
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
			
			Map<String, Pair<ITimedVariable<?>, ITimedVariable<?>>> redefined = 
					loadData(agreementDataStmt, expressionCtx);
			agreementDataRedefined.put(agreementKey, redefined);

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
			//@formatter:off
			AgreementKey agreementKey = new AgreementKey(key.getAgreementId(), key.getDomain());
//			ExpressionContext levelCtx = 
//					isAgreementDomain(agreementKey) ? 
//					new ExpressionContext(agreementDataCache.get(agreementKey)) : 
//					new ExpressionContext(); 
			ExpressionContext levelCtx =  new ExpressionContext(agreementDataCache.get(agreementKey)) ; 
			//@formatter:on

			agreementLevelDataStmt.setInt(1, key.getDomain());
			agreementLevelDataStmt.setInt(2, key.getAgreementLevelId());
			
			Map<String, Pair<ITimedVariable<?>, ITimedVariable<?>>> redefined =
					loadData(agreementLevelDataStmt, levelCtx);
			if ( agreementDataRedefined.containsKey(agreementKey) )
				redefined.putAll(agreementDataRedefined.get(agreementKey));
			agreementLevelRedefined.put(key, redefined);

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
	
	public Map<String, Pair<ITimedVariable<?>, ITimedVariable<?>>> getImplicitRedefined(AgreementContextKey key) {
		return agreementLevelRedefined.containsKey(key) ? agreementLevelRedefined.get(key) : Collections.emptyMap();
	}

	// ------------------------------------------
	// La ropa interior
	// ------------------------------------------

	private Map<String, Pair<ITimedVariable<?>, ITimedVariable<?>>> loadData(PreparedStatement stmt, ExpressionContext context)
			throws SQLException {
		ResultSet rs = null;
		Map<String, Pair<ITimedVariable<?>, ITimedVariable<?>>> redefinedMap = 
				new HashMap<String, Pair<ITimedVariable<?>, ITimedVariable<?>>>();
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
				ITimedVariable<?> implicit = context.getVariable(
						expr.getName(), start, end);
				try {
					List<ITimedResult<Object>> results = context.addExpression(
							expr, start, end);

					Pair<ITimedVariable<?>, ITimedVariable<?>> redefined =onRedefinedImplicit(context, expr.getName(), implicit,
							results);
					if ( redefined != null )
						redefinedMap.put(expr.getName(), redefined);

				} catch (Exception e) {
					DeferredExpressionVariable<Object> variable = new DeferredExpressionVariable<Object>(
							start, end, expr);
					context.putVariable(expr.getName(), variable);
					Pair<ITimedVariable<?>, ITimedVariable<?>> redefined =onRedefinedImplicit(context, expr.getName(), implicit,
							variable);
					if ( redefined != null )
						redefinedMap.put(expr.getName(), redefined);
				}
			}
			return redefinedMap;
		} finally {
			if (rs != null)
				rs.close();
		}
	}

	private Pair<ITimedVariable<?>, ITimedVariable<?>> onRedefinedImplicit(
			ExpressionContext ctx, String name, ITimedVariable<?> implicit,
			List<ITimedResult<Object>> results) {
		if (results == null)
			return null;
		if (results.isEmpty())
			return null;
		if (implicit == null)
			return null;
		if (implicit instanceof IExpressionVariable<?>
				&& ((IExpressionVariable<?>) implicit).getExpression()
						.getScope().compareTo(ExpressionScope.AGREEMENT) >= 0)
			return null;

		return new Pair<ITimedVariable<?>, ITimedVariable<?>>(results.get(0),
				implicit);

	}

	private Pair<ITimedVariable<?>, ITimedVariable<?>> onRedefinedImplicit(
			ExpressionContext ctx, String name, ITimedVariable<?> implicit,
			DeferredExpressionVariable<?> deferred) {
		if (implicit == null)
			return null;
		if (implicit instanceof IExpressionVariable<?>
				&& ((IExpressionVariable<?>) implicit).getExpression()
						.getScope().compareTo(ExpressionScope.AGREEMENT) >= 0)
			return null;

		return new Pair<ITimedVariable<?>, ITimedVariable<?>>(deferred,
				implicit);

	}

	private boolean isAgreementDomain(AgreementKey key) throws SQLException {
		ResultSet rs = null;
		try {
			isAgreementDomainStmt.setInt(1, key.getDomain());
			isAgreementDomainStmt.setInt(2, key.getId());
			rs = isAgreementDomainStmt.executeQuery();
			return rs.next();
		} finally {
			if (rs != null)
				rs.close();
		}
	}

	private void initAgreementStmt(Connection connection, Date startDate,
			Date endDate, OrderByList orderByList) throws SQLException {
		dataStmts = new PreparedStatement[3];

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

		isAgreementDomainStmt = connection
				.prepareStatement(IS_AGREEMENT_DOMAIN_SQL);
		dataStmts[2] = isAgreementDomainStmt;
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
