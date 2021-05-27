package com.code.aon.accounting.summary;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;

import com.code.aon.account.Account;
import com.code.aon.accounting.enumeration.AccountEntryType;
import com.code.aon.accounting.util.AccountingUtil;
import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.common.util.CommonUtil;
import net.aonsolutions.core.dbutils.DatabaseUtil;
import net.aonsolutions.core.pool.AonConnectionException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ql.util.ExpressionUtilities;
import com.esferalia.aon.entity.IEntityAlias;

public class SummaryProvider implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	public Summary getUniqueSummary(SummaryProviderParameters params) throws ManagerBeanException {
		SummaryCollection sc = getSummaryCollection(params, true);
		if (sc != null) {
			List<Summary> list = sc.getSummaryList();
			if (list != null && list.size() > 0) {
				return list.get(0);
			}
		}
		return null;
	}
	public SummaryCollection getSummaryCollection(SummaryProviderParameters params, boolean withPreviousBalance) throws ManagerBeanException {
		Connection c = null;
		try {
			c = DatabaseUtil.getConnection(params.getDomainName());
			return getSummaryCollection(c,params,withPreviousBalance);
		} catch (AonConnectionException e) {
			throw new ManagerBeanException(e.getMessage(), e);
		} finally {
			DatabaseUtil.closeQuietly(c);
		}
	}
	
	public SummaryCollection getSummaryCollection(Connection conn, SummaryProviderParameters params, boolean withPreviousBalance) throws ManagerBeanException {
		if (params.getFromDate() == null && (params.getPeriod() == null || params.getPeriod().getId() == null)) {
			throw new ManagerBeanException("Se necesita una fecha de inicio para el cálculo de saldos.");
		}
		PreparedStatement accountStmt = null;
		ResultSet accountSet = null;
		PreparedStatement previousAcumStmt = null;
		ResultSet previousAcumSet = null;
		PreparedStatement acumStmt = null;
		ResultSet acumSet = null;
		try {
			AccountingUtil accountingUtil = new AccountingUtil();
			// Primera fecha de toda la contabilidad.
			Date accountInitialDate = accountingUtil.getFirstPeriodInitialDate(); 
			
			// Se calcula la fecha inicial que se ha requerido en la petición
			Date firstDate = params.getFromDate() != null
						?params.getFromDate()
						:params.getPeriod().getInitiationDate();
			
			// Si se indicó explicitamente que no se desean acumulados anteriores.
			boolean previousAcumEnabled = withPreviousBalance;
			
			// ------------------------------------------------------------------------
			// si la fecha inicial de los parámetros es igual o inferior a la fecha de inicio 
			// de contabilización, se evita el cálculo de los acumulados anteriores. pues no tiene sentido.
			if (previousAcumEnabled) {
				previousAcumEnabled = (firstDate.after(accountInitialDate));
				if (previousAcumEnabled) {
					previousAcumStmt = prepareAcumStmt(conn,params,true);
					previousAcumStmt.setDate(2,new java.sql.Date( accountInitialDate.getTime()) );
					previousAcumStmt.setDate(3,new java.sql.Date( firstDate.getTime()) );
				}
			}
			// ------------------------------------------------------------------------
			
			
			// ------------------------------------------------------------------------
			// Si no es necesario ver las cuentas sin movimientos o de nivel inferior 
			// se busca todo de golpe.
			boolean uniqueSearch = !params.isNoTouchedAccountVisible() &&
								   !params.isLowerLevelVisible() && 
								   params.getAccountLevel() ==  5;			
			String sentence = getAccountSentence(params,uniqueSearch);
			accountStmt = conn.prepareStatement(sentence);
			if (uniqueSearch) {
				fillHostVariables(accountStmt,params);
			} else {
				// Si es necesario ver las cuentas inferiores se prepara el cursor que buscará los acumulados.
				acumStmt = prepareAcumStmt(conn,params,false);
				acumStmt.setDate(2,new java.sql.Date( firstDate.getTime()) );
				Date until = (params.getPeriod() != null && params.getPeriod().getDeadline() != null)
						?params.getPeriod().getDeadline()			// Fecha fin del ejercicio
						:accountingUtil.getLastPeriodDeadline();    // Fecha fin del último ejercicio-
				Date finalDate = params.getToDate() != null?params.getToDate():until;
				// Se suma uno a la fecha fin, para poder utilizar la misma sentencia 
				// de que en el caso de los acumulados anteriores.
				Calendar c = Calendar.getInstance();
				c.setTime(finalDate);
				c.add(Calendar.DAY_OF_MONTH, 1);
				acumStmt.setDate(3,new java.sql.Date( c.getTimeInMillis()) );
			}
			// ------------------------------------------------------------------------
			
			// ------------------------------------------------------------------------
			// Si se van a mostrar cuentas de nivel inferior a 5, se rellena el mapa con las
			// cuentas inferiores susceptibles de entrar en el listado.
			Map<String,Summary> map = new TreeMap<String, Summary>();
			if (params.isLowerLevelVisible() || params.getAccountLevel() < 5 ) {
				fillMap(conn, map, params );
			}
			// ------------------------------------------------------------------------

			accountSet = accountStmt.executeQuery();
			while (accountSet.next()) {
				Summary summary;
				summary = getSummary(params,accountSet,uniqueSearch,acumStmt,acumSet,previousAcumEnabled,previousAcumStmt,previousAcumSet,map);
				populateSummaryOnMap(map,summary,params);
			}
			
			return getSummaryCollection(params, map);
		} catch (Exception e) {
			throw new ManagerBeanException(e.getMessage(), e);
		} finally {
			closeResultSet(acumSet);
			closeStatement(acumStmt);
			closeResultSet(previousAcumSet);
			closeStatement(previousAcumStmt);
			closeResultSet(accountSet);
			closeStatement(accountStmt);
		}
		
	}
	
	private SummaryCollection getSummaryCollection(SummaryProviderParameters params, Map<String, Summary> map) {
		SummaryCollection sc = new SummaryCollection();
		for (Summary s : map.values()) {
			// Se excluyen las cuentas que no se han tocado en el listado.
			boolean add = s.isTouched();
			
			// Se excluyen las cuentas sin movimientos si asi se requiere.
			if (add && !params.isNoTouchedAccountVisible() && s.isEmpty()) {
				add = false;	
			}
			// Se excluyen las cuentas con saldo cero si asi se requiere.
			if (add && params.isExcludeBalancedAccounts() && s.isBalanced() ) {
				add = false;
			}
			if (add) {
				sc.add(s);
			}
		}
		return sc;
	}

	private void fillHostVariables(PreparedStatement accountStmt, SummaryProviderParameters params) throws SQLException {
		int p = 1;
		if (params.getFromDate() != null) {
			accountStmt.setDate(p, new java.sql.Date(params.getFromDate().getTime()));
			p++;
		}
		if (params.getToDate() != null) {
			accountStmt.setDate(p, new java.sql.Date(params.getToDate().getTime()));
			p++;
		}
		if (params.getPeriod() != null && params.getPeriod().getId() != null) {
			accountStmt.setInt(p, params.getPeriod().getId());
			p++;
		}
	}

	private Summary getSummary(SummaryProviderParameters params, ResultSet accountSet, boolean uniqueSearch, PreparedStatement acumStmt, ResultSet acumSet, boolean previousAcumEnabled, PreparedStatement previousAcumStmt, ResultSet previousAcumSet, Map<String, Summary> map) throws SQLException {
		boolean entryEnabled = (accountSet.getInt(6) == 1);
		boolean lastLevel = params.getAccountLevel() == accountSet.getInt(5) || entryEnabled;
		String code = accountSet.getString(3);
		Summary summary;
		if (!params.isMonthlyGrouping()) {
			summary = new Summary();	
		} else {
			summary = map.get(code);
			if (summary == null) {
				SummaryMonthly summaryMonthly = new SummaryMonthly();
				summary = summaryMonthly;
			}
		}
		summary.setAccountId(accountSet.getInt(1));
		summary.setCode(code);
		summary.setDescription(accountSet.getString(4));
		summary.setLastLevel(lastLevel);
		summary.setTouched(true);
		if (uniqueSearch) {
			summary.setDebit(CommonUtil.round(accountSet.getDouble(7)));
			summary.setCredit(CommonUtil.round(accountSet.getDouble(8)));
			if (params.isMonthlyGrouping()) {
				fillSummaryMonthly(accountSet.getInt(9),summary);
			}
		} else {
			acumStmt.setInt(1, summary.getAccountId());
			acumSet = acumStmt.executeQuery();
			while (acumSet.next()) {
				summary.setDebit(CommonUtil.round(acumSet.getDouble(1)));
				summary.setCredit(CommonUtil.round(acumSet.getDouble(2)));
				if (params.isMonthlyGrouping()) {
					fillSummaryMonthly(acumSet.getInt(3),summary);
				}
			}
			acumSet.close();
		}
		if (previousAcumEnabled) {
			previousAcumStmt.setInt(1, summary.getAccountId());
			previousAcumSet = previousAcumStmt.executeQuery();
			if (previousAcumSet.next()) {
				summary.setInitialDebit(CommonUtil.round(previousAcumSet.getDouble(1)));
				summary.setInitialCredit(CommonUtil.round(previousAcumSet.getDouble(2)));
			}
			previousAcumSet.close();
		}
		return summary;
	}
	
	private void fillSummaryMonthly(int month, Summary summary) {
		month = month - 1;
		double amount;
		if (summary.getCode().startsWith("7") ) {
			amount = CommonUtil.round(summary.getCredit() - summary.getDebit());
		} else {
			amount = CommonUtil.round(summary.getDebit() - summary.getCredit());
		}
		((SummaryMonthly) summary).getMonths()[month] = amount;
	}

	private void populateSummaryOnMap(Map<String, Summary> map, Summary summary, SummaryProviderParameters params) {
		String code = summary.getCode();
		if (params.getAccountLevel() == 5) {
			map.put(code, summary);	
		}
		int from = params.getAccountLevel() == 5 ? 4 : params.getAccountLevel();
		int until = params.isLowerLevelVisible() ? params.getFromAccountLevel() : params.getAccountLevel();
		for ( ; from >= until; from-- ) {
			String key = StringUtils.substring(code, 0, from);
			map.get(key).add(summary);
		}
	}

	private void fillMap(Connection conn, Map<String, Summary> map, SummaryProviderParameters params) throws ManagerBeanException, ExpressionException {
		IManagerBean bean = BeanManager.getManagerBean(Account.class);
		Criteria criteria = new Criteria();
		if (params.isLowerLevelVisible()) {
			criteria.addBetweenExpression(bean.getFieldName(IEntityAlias.ACCOUNT_LEVEL), params.getFromAccountLevel(), params.getAccountLevel());
		} else {
			Expression e = ExpressionUtilities.getLessThanOrEqualExpression(bean.getFieldName(IEntityAlias.ACCOUNT_LEVEL), params.getAccountLevel());
			criteria.addExpression(e);
		}
		criteria.addOrder(bean.getFieldName(IEntityAlias.ACCOUNT_CODE));
		List<ITransferObject> list = bean.getList(criteria);
		for (ITransferObject to : list) {
			Account account = (Account) to;
			Summary s;
			if (!params.isMonthlyGrouping()) {
				s = new Summary();
			} else {
				s = new SummaryMonthly();
			}
			s.setAccountId(account.getId());
			s.setCode(account.getCode());
			s.setDescription(account.getDescription());
			s.setLastLevel(account.getLevel() == params.getAccountLevel());
			s.setTouched(false);
			map.put(account.getCode(), s);
		}
	}

	private void closeStatement(PreparedStatement stmt) {
		try {
			if (stmt != null) {
				stmt.close();
			}
		} catch (Exception e) {
		}
	}
	private void closeResultSet(ResultSet rs) {
		try {
			if (rs!= null) {
				rs.close();
			}
		} catch (Exception e) {
		}
	}

	private String getAccountSentence(SummaryProviderParameters params, boolean uniqueSearch) throws SQLException, ExpressionException {
		StringBuffer buf = new StringBuffer();
		buf.append("SELECT a.id,a.domain,a.code,a.description,a.level,a.entryEnabled");
		if (uniqueSearch) {
			buf.append(" ,SUM(aed.debit) debit");
			buf.append(" ,SUM(aed.credit) credit");
			if (params.isMonthlyGrouping()) {
				buf.append(",MONTH(ae.entry_date)");
			}
		}
		buf.append(" FROM account a");
		if (uniqueSearch) {
			buf.append(" INNER JOIN account_entry_detail aed ON aed.account = a.id"); 
			buf.append(" INNER JOIN account_entry ae ON aed.account_entry = ae.id");
			if (params.getFromDate() != null) {
				buf.append(" AND ae.entry_date >= ?");
			}
			if (params.getToDate() != null) {
				buf.append(" AND ae.entry_date <= ?");
			}
			if (params.isPeriodNotNull()) {
				buf.append(" AND ae.account_period = ?");
			}
			if (params.getPeriod() != null && params.getPeriod().getId() != null) {
				if (params.isExcludeOperatingEntry()) {
					buf.append(" AND ae.entry_type != " + AccountEntryType.OPERATING.ordinal());
				}
				if (params.isExcludeClosingEntry()) {
					buf.append(" AND ae.entry_type != " + AccountEntryType.CLOSING.ordinal());
				}
			}
			if (params.getSecurityLevel() != null) {
				buf.append(" AND ae.security_level = " + params.getSecurityLevel().ordinal());
			}
			if (params.isNotEmptyDocumentNumber()) {
				buf.append(" AND aed.document_number = '" + params.getDocumentNumber() + "'");
			}
		}
		buf.append(" WHERE a.entryEnabled = 1");
		buf.append(" AND " + DomainManager.getSQLWhereClause("a.domain",Account.class));
		if (uniqueSearch) {
			buf.append(" AND " + DomainManager.getSQLWhereClause("ae.domain"));
		}
		
		if (!StringUtils.isEmpty(params.getAccountExpression())) {
			buf.append(" AND ");
			buf.append(params.getAccountSQLExpression("a.code"));
		}
		if (params.hasAccountCostCenters()) {
			buf.append(" AND ");
			buf.append(params.getAccountCostCenterSQLExpression("a.cost_center"));
		}		
		if (params.isTotalExpensesSummary()) {
			buf.append(" AND a.code >= '610' AND  a.code < '7'");
		}
		if (params.isGrossMarginSummary()) {
			buf.append(" AND (a.code LIKE '60%' OR  a.code LIKE '7%')");
		}
		if (!StringUtils.isEmpty(params.getAccountDescription())) {
			buf.append(" AND a.description LIKE '");
			buf.append(params.getDescriptionLikeExpression());
			buf.append("'");
		}
		if (!StringUtils.isEmpty(params.getAccountAlias())) {
			buf.append(" AND a.alias LIKE '");
			buf.append(params.getAliasLikeExpression());
			buf.append("'");
		}
		if (uniqueSearch) {		
			buf.append(" GROUP BY a.code,a.description");
			if (params.isMonthlyGrouping()) {
				buf.append(",MONTH(ae.entry_date)");
			}
		}
		buf.append(" ORDER BY domain,code");
		return buf.toString();
	}
	
	private PreparedStatement prepareAcumStmt(Connection conn, SummaryProviderParameters params, boolean forPrevious) throws SQLException {
		StringBuffer buf = new StringBuffer();
		buf.append("SELECT SUM(aed.debit) debit,SUM(aed.credit) credit");
		if (params.isMonthlyGrouping()) {
			buf.append(",MONTH(ae.entry_date)");
		}
		buf.append(" FROM account_entry_detail aed, account_entry ae");
		buf.append(" WHERE aed.account = ?");
		buf.append(" AND " + DomainManager.getSQLWhereClause("aed.domain"));
		buf.append(" AND aed.account_entry = ae.id");
		buf.append(" AND ae.entry_date >= ?");
		buf.append(" AND ae.entry_date < ?");
		if (!forPrevious) {
			if (params.getPeriod() != null && params.getPeriod().getId() != null) {
				if (params.isExcludeOperatingEntry()) {
					buf.append(" AND ae.entry_type != " + AccountEntryType.OPERATING.ordinal());
				}
				if (params.isExcludeClosingEntry()) {
					buf.append(" AND ae.entry_type != " + AccountEntryType.CLOSING.ordinal());
				}
			}
		}
		if (params.getSecurityLevel() != null) {
			buf.append(" AND ae.security_level = " + params.getSecurityLevel().ordinal());
		}
		if (params.isNotEmptyDocumentNumber()) {
			buf.append(" AND aed.document_number = '" + params.getDocumentNumber() + "'");
		}
		if (params.isMonthlyGrouping()) {
			buf.append(" GROUP BY MONTH(ae.entry_date)");
		}
		return conn.prepareStatement(buf.toString());
	}

	public SummaryCollection getTotalExpensesSummaryCollection(SummaryProviderParameters params) throws ManagerBeanException {
		params.setTotalExpensesSummary(true);
		params.setGrossMarginSummary(false);
		// Sin acumulados previos, puesto que es un listado unicamente vinculado a un ejercicio.
		SummaryCollection sc = getSummaryCollection(params,false);
		return sc;
	}

	public SummaryCollection getGrossMarginSummaryCollection(SummaryProviderParameters params) throws ManagerBeanException {
		params.setGrossMarginSummary(true);
		params.setTotalExpensesSummary(false);
		// Sin acumulados previos, puesto que es un listado unicamente vinculado a un ejercicio.
		SummaryCollection sc = getSummaryCollection(params,false);
		
		// Es habitual que las ventas aparezcan antes que las compras en la zona de "Margen Bruto" el listado de PyG.
		Comparator<Summary> comparator = new Comparator<Summary>() {
			@Override
			public int compare(Summary sum1, Summary sum2) {
				String account1 = sum1.getCode();
				String account2 = sum2.getCode();

				if (account1.substring(0, 1).equals(account2.substring(0, 1))) {
					return account1.compareTo(account2);
				}
				return (account1.substring(0, 1).equals("7")) ? -1 : 1;
			}
		};
		Collections.sort(sc.getSummaryList(), comparator);
		// -------------------------------------------------------------------------------
		
		
		return sc;
	}
	
}
