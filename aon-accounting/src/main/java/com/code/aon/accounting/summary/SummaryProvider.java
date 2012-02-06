package com.code.aon.accounting.summary;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.account.Account;
import com.code.aon.account.dao.IAccountAlias;
import com.code.aon.accounting.enumeration.AccountEntryType;
import com.code.aon.accounting.util.AccountingUtil;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ql.util.ExpressionUtilities;

public class SummaryProvider {

	private final static Logger LOGGER = LoggerFactory.getLogger(SummaryProvider.class);

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
	
	@SuppressWarnings("deprecation")
	public SummaryCollection getSummaryCollection(SummaryProviderParameters params, boolean withPreviousBalance) throws ManagerBeanException {
		if (params.getFromDate() == null && (params.getPeriod() == null || params.getPeriod().getId() == null)) {
			throw new ManagerBeanException("Se necesita una fecha de inicio para el cálculo de saldos.");
		}
		PreparedStatement accountStmt = null;
		ResultSet accountSet = null;
		PreparedStatement previousAcumStmt = null;
		ResultSet previousAcumSet = null;
		PreparedStatement acumStmt = null;
		ResultSet acumSet = null;
		boolean mustCloseSession = HibernateUtil.mustCloseSession();
		boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
		String sessionName = HibernateUtil.getSessionFactoryName();
		try {
			HibernateUtil.setCloseSession( false );
			HibernateUtil.setBeginTransaction( false  );
			HibernateUtil.startSession(sessionName);
			HibernateUtil.beginTransaction(sessionName);

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
					previousAcumStmt = prepareAcumStmt(sessionName,params,true);
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
			accountStmt = HibernateUtil.getSQLConnection(sessionName).prepareStatement(sentence);
			if (uniqueSearch) {
				fillHostVariables(accountStmt,params);
			} else {
				// Si es necesario ver las cuentas inferiores se prepara el cursor que buscará los acumulados.
				acumStmt = prepareAcumStmt(sessionName,params,false);
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
				fillMap( map, params );
			}
			// ------------------------------------------------------------------------
			
			accountSet = accountStmt.executeQuery();
			while (accountSet.next()) {
				Summary summary;
				summary = getSummary(params,accountSet,uniqueSearch,acumStmt,acumSet,previousAcumEnabled,previousAcumStmt,previousAcumSet,map);
				populateSummaryOnMap(map,summary,params);
			}
			
			HibernateUtil.commitTransaction(sessionName);
			return getSummaryCollection(params, map);
		} catch (Exception e) {
			closeResultSet(acumSet);
			closeStatement(acumStmt);
			closeResultSet(previousAcumSet);
			closeStatement(previousAcumStmt);
			closeResultSet(accountSet);
			closeStatement(accountStmt);
			try {
				HibernateUtil.rollbackTransaction(sessionName);
			} catch (DAOException daoe) {
				String msg = "Unable to rollback transaction!";
				LOGGER.error(msg, e);
			}
			throw new ManagerBeanException(e.getMessage(), e);
		} finally {
			HibernateUtil.closeSession(sessionName);
			HibernateUtil.setCloseSession( mustCloseSession );
			HibernateUtil.setBeginTransaction( mustBeginTransaction );
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

	private void fillMap(Map<String, Summary> map, SummaryProviderParameters params) throws ManagerBeanException, ExpressionException {
		IManagerBean bean = BeanManager.getManagerBean(Account.class);
		Criteria criteria = new Criteria();
		if (params.isLowerLevelVisible()) {
			criteria.addBetweenExpression(bean.getFieldName(IAccountAlias.ACCOUNT_LEVEL), params.getFromAccountLevel(), params.getAccountLevel());
		} else {
			Expression e = ExpressionUtilities.getLessThanOrEqualExpression(bean.getFieldName(IAccountAlias.ACCOUNT_LEVEL), params.getAccountLevel());
			criteria.addExpression(e);
		}
		criteria.addOrder(bean.getFieldName(IAccountAlias.ACCOUNT_CODE));
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
		}
		buf.append(" WHERE a.entryEnabled = 1");
		if (!StringUtils.isEmpty(params.getAccountExpression())) {
			buf.append(" AND ");
			buf.append(params.getAccountSQLExpression("a.code"));
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
	
	@SuppressWarnings("deprecation")
	private PreparedStatement prepareAcumStmt(String sessionName, SummaryProviderParameters params, boolean forPrevious) throws SQLException {
		StringBuffer buf = new StringBuffer();
		buf.append("SELECT SUM(aed.debit) debit,SUM(aed.credit) credit");
		if (params.isMonthlyGrouping()) {
			buf.append(",MONTH(ae.entry_date)");
		}
		buf.append(" FROM account_entry_detail aed, account_entry ae");
		buf.append(" WHERE aed.account = ?"); 
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
		if (params.isMonthlyGrouping()) {
			buf.append(" GROUP BY MONTH(ae.entry_date)");
		}
		return HibernateUtil.getSQLConnection(sessionName).prepareStatement(buf.toString());
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

	// ************************************************************************************
	// ************************************************************************************
	// ************************************************************************************
	// ************************************************************************************
	// ************************************************************************************
	// ************************************************************************************
/*
	private StringBuffer getSummaryStatement(SummaryProviderParameters params, boolean lowLevelAccounts) {
		StringBuffer sumStmt = new StringBuffer();
		sumStmt.append("SELECT SUM(d.debit),SUM(d.credit)");
		if (params.isMonthlyGrouping()) {
			sumStmt.append(",MONTH(s.entry_date)");
		}
		sumStmt.append(" FROM ");
		if (lowLevelAccounts) {
			sumStmt.append("account a,");
		}
		sumStmt.append(" account_entry_detail d, account_entry s");
		sumStmt.append(" WHERE ");
		if (lowLevelAccounts) {
			sumStmt.append(" a.code LIKE ? ");			
			sumStmt.append(" AND d.account = a.id ");			
		} else {
			sumStmt.append(" d.account = ? ");	
		}
		sumStmt.append(" AND s.id = d.account_entry");
		if (params.getFromDate() != null) {
			sumStmt.append(" AND s.entry_date >= ?");
		}
		if (params.getToDate() != null) {
			sumStmt.append(" AND s.entry_date <= ?");
		}
		if (params.isPeriodNotNull()) {
			sumStmt.append(" AND s.account_period = ?");
		}
		if (params.getPeriod() != null && params.getPeriod().getId() != null) {
			if (params.isExcludeOpeningEntry()) {
				sumStmt.append(" AND s.entry_type != " + AccountEntryType.OPENING.ordinal());
			}
			if (params.isExcludeOperatingEntry()) {
				sumStmt.append(" AND s.entry_type != " + AccountEntryType.OPERATING.ordinal());
			}
			if (params.isExcludeClosingEntry()) {
				sumStmt.append(" AND s.entry_type != " + AccountEntryType.CLOSING.ordinal());
			}
		}
		if (params.getSecurityLevel() != null) {
			sumStmt.append(" AND s.security_level = ?");
		}
		if (params.isMonthlyGrouping()) {
			sumStmt.append(" GROUP BY MONTH(s.entry_date)");
		}
		return sumStmt;
	}

	@SuppressWarnings("deprecation")
	private PreparedStatement prepareStatement(String sessionName, PreparedStatement sum, StringBuffer sumStmt, SummaryProviderParameters params) throws SQLException {
		sum = HibernateUtil.getSQLConnection(sessionName).prepareStatement(sumStmt.toString());
		int p = 2;
		if (params.getFromDate() != null) {
			sum.setDate(p, new java.sql.Date(params.getFromDate().getTime()));
			p++;
		}
		if (params.getToDate() != null) {
			sum.setDate(p, new java.sql.Date(params.getToDate().getTime()));
			p++;
		}
		if (params.getPeriod() != null && params.getPeriod().getId() != null) {
			sum.setInt(p, params.getPeriod().getId());
			p++;
		}
		if (params.getSecurityLevel() != null) {
			sum.setInt(p, params.getSecurityLevel().ordinal());
		}
		return sum;
	}

	public Balance getOpeningEntryBalance(Period period, String accountId, SecurityLevel securityLevel) throws ManagerBeanException {
		if (period == null) {
			throw new IllegalArgumentException("Period param must not be null.");
		}
		return getOpeningEntryBalance(period.getDeadline(), accountId,securityLevel);
	}

	@SuppressWarnings("deprecation")
	public Balance getOpeningEntryBalance(Date date, String accountId, SecurityLevel securityLevel) throws ManagerBeanException {
		if (date == null) {
			throw new IllegalArgumentException("date param must not be null.");
		}
		if (accountId == null) {
			throw new IllegalArgumentException("accountId param must not be null.");
		}
		
		PreparedStatement entryStmt = null;
		ResultSet entrySet  = null;
		try {
			String sessionName = HibernateUtil.getSessionFactoryName(AccountEntry.class.getName());
			
			// Se busca la fecha de un asiento de apertura inmediatamanete anterior a la fecha requerida.
			StringBuffer stmt = new StringBuffer();
			stmt.append("SELECT MAX(a.entry_date)");
			stmt.append(" FROM account_entry a ");
			stmt.append(" WHERE a.entry_date <= ? ");
			stmt.append(" AND a.entry_type = ? ");
			if (securityLevel != null ) {
				stmt.append(" AND a.security_level = ?");
			} 
			entryStmt = HibernateUtil.getSQLConnection(sessionName).prepareStatement(stmt.toString());
			int i = 0;
			entryStmt.setDate(++i, new java.sql.Date( date.getTime() ) );
			entryStmt.setInt(++i, AccountEntryType.OPENING.ordinal());
			if (securityLevel != null ) {
				entryStmt.setInt(++i, securityLevel.ordinal() );
			}
			entrySet = entryStmt.executeQuery();
			Balance b = null;
			if (entrySet.next()) {
				Date entryDate = entrySet.getDate(1);
				if (entryDate != null) {
					b = new Balance();
					b.setFromDate(entryDate);
				}
			}
			if (b != null && b.getFromDate() != null) {
				entrySet.close();
				entryStmt.close();
				stmt = new StringBuffer();	
				stmt.append("SELECT SUM(d.debit),SUM(d.credit)");
				stmt.append(" FROM account_entry a,account_entry_detail d, account acc ");
				stmt.append(" WHERE a.id = d.account_entry");
				stmt.append(" AND d.account = acc.id");
				stmt.append(" AND a.entry_type = ?");
				stmt.append(" AND a.entry_date = ?");
				if (securityLevel != null ) {
					stmt.append(" AND a.security_level = ?");
				}
		 		stmt.append(" AND acc.code LIKE ?");
				i = 0;
				entryStmt = HibernateUtil.getSQLConnection(sessionName).prepareStatement(stmt.toString());
				entryStmt.setInt(++i, AccountEntryType.OPENING.ordinal());
				entryStmt.setDate(++i, new java.sql.Date( b.getFromDate().getTime() ) );
				if (securityLevel != null ) {
					entryStmt.setInt(++i, securityLevel.ordinal() );
				}
				entryStmt.setString(++i, accountId + PERCENT);
				entrySet = entryStmt.executeQuery();
				if (entrySet.next()) {
					b.set(CommonUtil.round(entrySet.getDouble(1)), CommonUtil.round(entrySet.getDouble(2)));
				}

			}
			return b;
		} catch (Exception e) {
			throw new ManagerBeanException(e.getMessage(), e);
		} finally {
			try {
				if (entryStmt != null) {
					entryStmt.close();
				}
			} catch (Exception e) {
				// nada
			}
			try {
				if (entrySet != null) {
					entrySet.close();
				}
			} catch (Exception e) {
				// nada
			}
		}
	}

	public Balance getPeriodBalance(Date fromDate, Date toDate, String accountId, SecurityLevel securityLevel,
			boolean excludeOpeningEntry, boolean excludeClosingEntry) throws ManagerBeanException {
		Balance balance = new Balance();
		if (StringUtils.isNotEmpty(accountId)) {
			SummaryProviderParameters params = new SummaryProviderParameters();
			params.setFromDate(fromDate);
			params.setToDate(toDate);
			params.setAccountExpression(accountId);
			int level = (accountId.length() > 4) ? 5 : accountId.length();
			params.setAccountLevel(level);
			params.setSecurityLevel(securityLevel);
			params.setExcludeOpeningEntry(excludeOpeningEntry);
			params.setExcludeClosingEntry(excludeClosingEntry);
			
			PreparedStatement sum = null;
			ResultSet sumSet = null;
			String sessionName = HibernateUtil.getSessionFactoryName();
			StringBuffer sumStmt = getSummaryStatement(params,true);
			try {
				sum = prepareStatement(sessionName,sum,sumStmt,params);
				String likeAccount = accountId + PERCENT;
				sum.setString(1, likeAccount);
				sumSet = sum.executeQuery();
				if (sumSet.next()) {
					balance.setFromDate(fromDate);
					balance.setToDate(toDate);
					balance.set(CommonUtil.round(sumSet.getDouble(1)),CommonUtil.round(sumSet.getDouble(2)));
				}
			} catch (SQLException e) {
				throw new ManagerBeanException(e.getMessage(),e);
			} finally {
				try {
					if (sumSet != null) {
						sumSet.close();
					}
				} catch (Exception e) {
					// nada
				}
				try {
					if (sum != null) {
						sum.close();
					}
				} catch (Exception e) {
					// nada
				}
			}
		}
		return balance;
	}
	
*/	
	//****************************************************************
	//****************************************************************
	//****************************************************************
	//****************************************************************
	//****************************************************************
	//****************************************************************
	//****************************************************************
//	public static String ACCOUNT_CODE_ALIAS = null;
//	public static String ACCOUNT_DESCRIPTION_ALIAS = null;
//	public static String ACCOUNT_ALIAS_ALIAS = null;
//	public static String ACCOUNT_LEVEL_ALIAS = null;
//	public static String ACCOUNT_ENTRY_ENABLED_ALIAS = null;
//
//	static {
//		try {
//			IManagerBean accountBean = BeanManager.getManagerBean(Account.class);
//			ACCOUNT_CODE_ALIAS = accountBean.getFieldName(IAccountAlias.ACCOUNT_CODE);
//			ACCOUNT_DESCRIPTION_ALIAS = accountBean.getFieldName(IAccountAlias.ACCOUNT_DESCRIPTION);
//			ACCOUNT_ALIAS_ALIAS = accountBean.getFieldName(IAccountAlias.ACCOUNT_ALIAS);
//			ACCOUNT_LEVEL_ALIAS = accountBean.getFieldName(IAccountAlias.ACCOUNT_LEVEL);
//			ACCOUNT_ENTRY_ENABLED_ALIAS = accountBean.getFieldName(IAccountAlias.ACCOUNT_ENTRY_ENABLED);
//		} catch (Exception e) {
//			LOGGER.error("Error obtining field alias", e);
//		}
//	}
//
//	public SummaryCollection getSummaryCollectionVIEJUNO(SummaryProviderParameters params) throws ManagerBeanException {
//		return getSummaryCollectionVIEJUNO(params,true);
//	}
//	
//
//	@Deprecated
//	public SummaryCollection getSummaryCollectionVIEJUNO(SummaryProviderParameters params, boolean authomaticBalance ) throws ManagerBeanException {
//		PreparedStatement sum = null;
//		ResultSet sumSet = null;
//		boolean mustCloseSession = HibernateUtil.mustCloseSession();
//		boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
//		String sessionName = HibernateUtil.getSessionFactoryName();
//		try {
//			HibernateUtil.setCloseSession( false );
//			HibernateUtil.setBeginTransaction( false  );
//			
//			HibernateUtil.startSession(sessionName);
//			HibernateUtil.beginTransaction(sessionName);
//			StringBuffer sumStmt = getStatement(params);
//			IManagerBean accountBean = BeanManager.getManagerBean(Account.class);
//			List<ITransferObject> accountList = accountBean.getList(getCriteria(params));
//			int p = 1  
//			 + (params.getFromDate() != null?1:0)
//			 + (params.getToDate() != null?1:0)
//			 + (params.getPeriod() != null && params.getPeriod().getId() != null?1:0)
//			 + (params.getSecurityLevel() != null?1:0);
//			sum = prepareStatement(sessionName,sum,sumStmt,params);
//			boolean add;
//			SummaryCollection sc = new SummaryCollection();
//			for (ITransferObject to : accountList) {
//				Account account = (Account) to;
//				add = true;
//				if (add && !params.isLowerLevelVisible() && !account.isEntryEnabled()
//						&& account.getLevel() < params.getAccountLevel()) {
//					add = false;
//				}
//				if (add && params.isLowerLevelVisible() && account.getLevel() < params.getFromAccountLevel()) {
//					add = false;
//				}
//				if (add) {
//					String likeAccount = account.getCode() + PERCENT;
//					sum.setString(p, likeAccount);
//					sumSet = sum.executeQuery();
//					Double debit = 0.0;
//					Double credit = 0.0;
//					Summary s = null;
//					if (!params.isMonthlyGrouping()) {
//						if (sumSet.next()) {
//							debit = CommonUtil.round(debit + sumSet.getDouble(1));
//							credit = CommonUtil.round(credit + sumSet.getDouble(2));
//						}
//						s = new Summary();
//						s.setDebit(debit);
//						s.setCredit(credit);
//					} else {
//						s = getSummaryMonthly(sumSet,account.getCode(),debit,credit,authomaticBalance);
//					}
//					if (add) {
//						s.setId(account.getCode());
//						s.setDescription(account.getDescription());
//						s.setLastLevel(params.getAccountLevel() == account.getLevel() || account.isEntryEnabled());
//						s.setInitialCredit(0);					
//						s.setInitialDebit(0);
//						
//						if (s.isLastLevel()) {
//							Balance openingBalance = getOpeningEntryBalance(params.getStartDate(), account.getCode(),params.getSecurityLevel());
//							Balance fromOpeningBalance = null;
//							Date dateTo = DateUtils.addDays(params.getStartDate(), -1);
//							if (openingBalance!=null) {
//								s.setInitialDebit(openingBalance.getDebit());
//								s.setInitialCredit(openingBalance.getCredit());
//
//								// Si la fecha del asiento de apertura encontrado esta en el rango de fechas pedidas, se asume que el 
//								// importe del asiento de apertura pertenece al ejercicio.
//								long op = openingBalance.getFromDate().getTime();
//								long pa = params.getStartDate().getTime();
//								if (op >= pa) {
//									s.setOpeningDebit(openingBalance.getDebit());
//									s.setOpeningCredit(openingBalance.getCredit());
//								}
//								
//								// Si existe un asiento de apertura que sirva como punto de partida, se calcula el acumulado 
//								// desde el asiento de apertura hasta el inicio del periodo solicitado, en el caso de no ser el mismo dia. 
//								if (!DateUtils.isSameDay(openingBalance.getFromDate(), params.getStartDate())) {
//									fromOpeningBalance = getPeriodBalance(openingBalance.getFromDate(), dateTo,account.getCode(),params.getSecurityLevel(),false,false);
//								}
//							} else {
//								// Si no existe un asiento de apertura que sirva como punto de partida, se calcula el acumulado 
//								// desde el principio de los tiempos hasta el inicio del periodo solicitado.
//								fromOpeningBalance = getPeriodBalance(new Date(0), dateTo, account.getCode(),params.getSecurityLevel(),false,false);
//							}
//							if (fromOpeningBalance!=null) {
//								if (openingBalance!=null) {
//									// Si exiten acumulados anteriores y asiento de apertura, éste se descuenta de los acumulados anteriores.
//									fromOpeningBalance.substractBalance(openingBalance);
//								}
//								s.setInitialDebit(s.getInitialDebit() + fromOpeningBalance.getDebit());
//								s.setInitialCredit(s.getInitialCredit() + fromOpeningBalance.getCredit());
//							}
//						}
//					}
//					if (params.isExcludeBalancedAccounts() && CommonUtil.round(
//							(s.getDebit() + s.getOpeningDebit()) - 
//							(s.getCredit() + s.getOpeningCredit())) == 0) {
//						add = false;
//					}
//					if (add 
//							&& !params.isNoTouchedAccountVisible() 
//							&& CommonUtil.round(s.getDebit()) == 0 
//							&& CommonUtil.round(s.getCredit()) == 0
//							&& CommonUtil.round(s.getOpeningDebit()) == 0 
//							&& CommonUtil.round(s.getOpeningCredit()) == 0									
//						) {
//							add = false;
//						}
//					if (add) {
//						sc.add(s);
//					}
//					
//					sumSet.close();
//				}
//			}
//			HibernateUtil.commitTransaction(sessionName);
//			if (params.isLowerLevelVisible()) {
//				fillLowerLevelAccounts(sc);
//			}
//			return sc;
//		} catch (Exception e) {
//			try {
//				HibernateUtil.rollbackTransaction(sessionName);
//			} catch (DAOException daoe) {
//				String msg = "Unable to rollback transaction!";
//				LOGGER.error(msg, e);
//			}
//			throw new ManagerBeanException(e.getMessage(), e);
//		} finally {
//			try {
//				if (sumSet != null) {
//					sumSet.close();
//				}
//			} catch (Exception e) {
//				// nada
//			}
//			try {
//				if (sum != null) {
//					sum.close();
//				}
//			} catch (Exception e) {
//				// nada
//			}
//			HibernateUtil.closeSession(sessionName);
//			HibernateUtil.setCloseSession( mustCloseSession );
//			HibernateUtil.setBeginTransaction( mustBeginTransaction );
//		}
//	}
//	private void fillLowerLevelAccounts(SummaryCollection sc) {
//		for (Summary s: sc.getSummaryList()) {
//			if (!s.isLastLevel()) {
//				for (Summary s0: sc.getSummaryList()) {
//					if (s0.isLastLevel() && StringUtils.startsWith(s0.getId(), s.getId())  ) {
//						s.setInitialCredit( CommonUtil.round(s.getInitialCredit() + s0.getInitialCredit()));
//						s.setInitialDebit( CommonUtil.round(s.getInitialDebit() + s0.getInitialDebit()));
//					}
//				}
//			}
//		}
//	}
//	private StringBuffer getStatement(SummaryProviderParameters params) {
//		StringBuffer sumStmt = new StringBuffer();
//		sumStmt.append("SELECT SUM(d.debit),SUM(d.credit)");
//		if (params.isMonthlyGrouping()) {
//			sumStmt.append(",MONTH(s.entry_date)");
//		}
//		sumStmt.append(" FROM account_entry s, account_entry_detail d, account a");
//		sumStmt.append(" WHERE s.id = d.account_entry");
//		sumStmt.append(" AND d.account = a.id ");
//		if (params.getFromDate() != null) {
//			sumStmt.append(" AND s.entry_date >= ?");
//		}
//		if (params.getToDate() != null) {
//			sumStmt.append(" AND s.entry_date <= ?");
//		}
//		if (params.isPeriodNotNull()) {
//			sumStmt.append(" AND s.account_period = ?");
//		}
//		if (params.isExcludeOpeningEntry()) {
//			sumStmt.append(" AND s.entry_type != " + AccountEntryType.OPENING.ordinal());
//		}
//		if (params.isExcludeOperatingEntry()) {
//			sumStmt.append(" AND s.entry_type != " + AccountEntryType.OPERATING.ordinal());
//		}
//		if (params.isExcludeClosingEntry()) {
//			sumStmt.append(" AND s.entry_type != " + AccountEntryType.CLOSING.ordinal());
//		}
//		if (params.getSecurityLevel() != null) {
//			sumStmt.append(" AND s.security_level = ?");
//		}
//		sumStmt.append(" AND a.code LIKE ?");
//		if (params.isMonthlyGrouping()) {
//			sumStmt.append(" GROUP BY MONTH(s.entry_date)");
//		}
//		return sumStmt;
//	}
//
//	@SuppressWarnings("deprecation")
//	private PreparedStatement prepareStatement(String sessionName,PreparedStatement sum, StringBuffer sumStmt, SummaryProviderParameters params) throws SQLException {
//		sum = HibernateUtil.getSQLConnection(sessionName).prepareStatement(sumStmt.toString());
//		int p = 1;
//		if (params.getFromDate() != null) {
//			sum.setDate(p, new java.sql.Date(params.getFromDate().getTime()));
//			p++;
//		}
//		if (params.getToDate() != null) {
//			sum.setDate(p, new java.sql.Date(params.getToDate().getTime()));
//			p++;
//		}
//		if (params.getPeriod() != null && params.getPeriod().getId() != null) {
//			sum.setInt(p, params.getPeriod().getId());
//			p++;
//		}
//		if (params.getSecurityLevel() != null) {
//			sum.setInt(p, params.getSecurityLevel().ordinal());
//		}
//		return sum;
//	}
//	
//	private Criteria getCriteria(SummaryProviderParameters params) throws ExpressionException {
//		Criteria criteria = new Criteria();
//		if (!StringUtils.isEmpty(params.getAccountExpression())) {
//			criteria.addExpression(ExpressionUtilities
//					.getExpression(params.getAccountExpression(), ACCOUNT_CODE_ALIAS));
//		}
//		if (!StringUtils.isEmpty(params.getAccountDescription())) {
//			criteria.addExpression(ExpressionUtilities.getExpression(params.getAccountDescription(), 
//					ACCOUNT_DESCRIPTION_ALIAS));
//		}
//		if (!StringUtils.isEmpty(params.getAccountAlias())) {
//			criteria.addExpression(ExpressionUtilities
//					.getExpression(params.getAccountAlias(), ACCOUNT_ALIAS_ALIAS));
//		}
//		if (!params.isLowerLevelVisible()) {
//			Expression e1 = ExpressionUtilities.getEqualExpression(ACCOUNT_LEVEL_ALIAS, params.getAccountLevel());
//			Expression e21 = ExpressionUtilities.getLessThanExpression(ACCOUNT_LEVEL_ALIAS, params.getAccountLevel());
//			Expression e22 = ExpressionUtilities.getEqualExpression(ACCOUNT_ENTRY_ENABLED_ALIAS, true);
//			Expression e2 = ExpressionUtilities.getAndExpression(e21, e22);
//			Expression e = ExpressionUtilities.getOrExpression(e1, e2);
//			criteria.addExpression(e);
//		} else {
//			Expression e = ExpressionUtilities.getLessThanOrEqualExpression(ACCOUNT_LEVEL_ALIAS, params.getAccountLevel());
//			criteria.addExpression(e);
//		}
//		criteria.addOrder(ACCOUNT_CODE_ALIAS);
//		return criteria;
//	}
}
