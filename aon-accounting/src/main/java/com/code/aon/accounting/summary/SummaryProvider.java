package com.code.aon.accounting.summary;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.account.Account;
import com.code.aon.account.dao.IAccountAlias;
import com.code.aon.accounting.AccountEntry;
import com.code.aon.accounting.Period;
import com.code.aon.accounting.enumeration.AccountEntryType;
import com.code.aon.accounting.util.AccountingUtil;
import com.code.aon.accounting.util.Balance;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ql.util.ExpressionUtilities;

public class SummaryProvider {

	private final static Logger LOGGER = LoggerFactory.getLogger(SummaryProvider.class);

	private static final String PERCENT = "%";
	
	public static String ACCOUNT_ID_ALIAS = null;
	public static String ACCOUNT_DESCRIPTION_ALIAS = null;
	public static String ACCOUNT_ALIAS_ALIAS = null;
	public static String ACCOUNT_LEVEL_ALIAS = null;
	public static String ACCOUNT_ENTRY_ENABLED_ALIAS = null;

	static {
		try {
			IManagerBean accountBean = BeanManager.getManagerBean(Account.class);
			ACCOUNT_ID_ALIAS = accountBean.getFieldName(IAccountAlias.ACCOUNT_ID);
			ACCOUNT_DESCRIPTION_ALIAS = accountBean.getFieldName(IAccountAlias.ACCOUNT_DESCRIPTION);
			ACCOUNT_ALIAS_ALIAS = accountBean.getFieldName(IAccountAlias.ACCOUNT_ALIAS);
			ACCOUNT_LEVEL_ALIAS = accountBean.getFieldName(IAccountAlias.ACCOUNT_LEVEL);
			ACCOUNT_ENTRY_ENABLED_ALIAS = accountBean.getFieldName(IAccountAlias.ACCOUNT_ENTRY_ENABLED);
		} catch (Exception e) {
			LOGGER.error("Error obtining field alias", e);
		}
	}

	private AccountingUtil accountingUtil;
	
	public AccountingUtil getAccountingUtil() {
		if (accountingUtil == null) {
			accountingUtil = new  AccountingUtil();
		}
		return accountingUtil;
	}

	public SummaryCollection getSummaryCollection(SummaryProviderParameters params) throws ManagerBeanException {
		return getSummaryCollection(params,true);
	}

	@SuppressWarnings("deprecation")
	public SummaryCollection getSummaryCollection(SummaryProviderParameters params, boolean authomaticBalance ) throws ManagerBeanException {
		PreparedStatement sum = null;
		ResultSet sumSet = null;
		PreparedStatement entryStmt = null;
		boolean mustCloseSession = HibernateUtil.mustCloseSession();
		boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
		String sessionName = HibernateUtil.getSessionFactoryName();
		try {
			HibernateUtil.setCloseSession( false );
			HibernateUtil.setBeginTransaction( false  );
			
			HibernateUtil.startSession(sessionName);
			HibernateUtil.beginTransaction(sessionName);
			StringBuffer sumStmt = getStatement(params);
			IManagerBean accountBean = BeanManager.getManagerBean(Account.class);
			List<ITransferObject> accountList = accountBean.getList(getCriteria(params));
			Period period = params.getPeriod();
			boolean openingEntry = false;
			if (params.isExcludeOpeningEntry() && params.getPeriod() != null && params.getPeriod().getId() != null) {
				openingEntry = getAccountingUtil().existsOpeningEntry(period, params.getSecurityLevel());
			}
			boolean operatingEntry = false;
			if (params.isExcludeOperatingEntry() && params.getPeriod() != null && params.getPeriod().getId() != null) {
				operatingEntry = getAccountingUtil().existsOperatingEntry(period, params.getSecurityLevel());
			}
			boolean closingEntry = false;
			if (params.isExcludeClosingEntry() && params.getPeriod() != null && params.getPeriod().getId() != null) {
				closingEntry = getAccountingUtil().existsClosingEntry(period, params.getSecurityLevel());
			}
			StringBuffer entryStatement = getEntryStatement(params);
			entryStmt = HibernateUtil.getSQLConnection(sessionName).prepareStatement(entryStatement.toString());	
			int p = 1  
			 + (params.getFromDate() != null?1:0)
			 + (params.getToDate() != null?1:0)
			 + (params.getPeriod() != null && params.getPeriod().getId() != null?1:0)
			 + (params.getSecurityLevel() != null?1:0);
			sum = prepareStatement(sessionName,sum,sumStmt,params);
			boolean add;
			SummaryCollection sc = new SummaryCollection();
			for (ITransferObject to : accountList) {
				Account account = (Account) to;
				add = true;
				String likeAccount = account.getId() + PERCENT;
				sum.setString(p, likeAccount);
				sumSet = sum.executeQuery();
				Double debit = 0.0;
				Double credit = 0.0;
				Summary s = null;
				Balance openingBalance = null;
				openingBalance = excludeEntry(entryStmt,AccountEntryType.OPENING.ordinal(),account.getId(), params );
				if (openingEntry) {
					credit = (openingBalance!=null)?CommonUtil.round(credit - openingBalance.getCredit()):credit;	
					debit = (openingBalance!=null)?CommonUtil.round(debit - openingBalance.getDebit()):debit;	
				}
				Balance operatingBalance = null;
				if (operatingEntry) {
					operatingBalance = excludeEntry(entryStmt,AccountEntryType.OPERATING.ordinal(),account.getId(), params );
					credit = (operatingBalance!=null)?CommonUtil.round(credit - operatingBalance.getCredit()):credit;	
					debit = (operatingBalance!=null)?CommonUtil.round(debit - operatingBalance.getDebit()):debit;	
				}
				Balance closingBalance = null;
				if (closingEntry) {
					closingBalance = excludeEntry(entryStmt,AccountEntryType.CLOSING.ordinal(),account.getId(), params );
					credit = (closingBalance!=null)?CommonUtil.round(credit - closingBalance.getCredit()):credit;	
					debit = (closingBalance!=null)?CommonUtil.round(debit - closingBalance.getDebit()):debit;	
				}
				if (!params.isMonthlyGrouping()) {
					if (sumSet.next()) {
						debit = CommonUtil.round(debit + sumSet.getDouble(1));
						credit = CommonUtil.round(credit + sumSet.getDouble(2));
					}
					s = new Summary();
					s.setDebit(debit);
					s.setCredit(credit);
				} else {
					s = getSummaryMonthly(sumSet,account.getId(),debit,credit,authomaticBalance,operatingBalance,closingBalance);
				}
				if (!params.isNoTouchedAccountVisible() && 
						(CommonUtil.round(s.getDebit()) == 0 && CommonUtil.round(s.getCredit()) == 0)) {
						add = false;
					}
				if (add && !params.isLowerLevelVisible() && !account.isEntryEnabled()
						&& account.getLevel() < params.getAccountLevel()) {
					add = false;
				}
				if (add && params.isExcludeBalancedAccounts() && CommonUtil.round(debit - credit) == 0) {
					add = false;
				}
				if (add && params.isLowerLevelVisible() && account.getLevel() < params.getFromAccountLevel()) {
					add = false;
				}
				if (add) {
					s.setId(account.getId());
					s.setDescription(account.getDescription());
					s.setLastLevel(params.getAccountLevel() == account.getLevel() || account.isEntryEnabled());
					s.setInitialCredit(openingBalance!=null?openingBalance.getCredit():0.0);					
					s.setInitialDebit(openingBalance!=null?openingBalance.getDebit():0.0);
					sc.add(s);
				}
				sumSet.close();
			}
			HibernateUtil.commitTransaction(sessionName);
			return sc;
		} catch (Exception e) {
			try {
				HibernateUtil.rollbackTransaction(sessionName);
			} catch (DAOException daoe) {
				String msg = "Unable to rollback transaction!";
				LOGGER.error(msg, e);
			}
			throw new ManagerBeanException(e.getMessage(), e);
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
			HibernateUtil.closeSession(sessionName);
			HibernateUtil.setCloseSession( mustCloseSession );
			HibernateUtil.setBeginTransaction( mustBeginTransaction );
		}
	}

	private Balance excludeEntry(PreparedStatement entryStmt, int ordinal, String account, SummaryProviderParameters params) throws SQLException {
		int i = 0;
		if (params.getPeriod() != null && params.getPeriod().getId() != null) {
			entryStmt.setString(++i, params.getPeriod().getId() );
		}
		entryStmt.setInt(++i, ordinal);
		if (params.getFromDate() != null ) {
			entryStmt.setDate(++i, new java.sql.Date( params.getFromDate().getTime() ) );
		}
		if (params.getToDate() != null ) {
			entryStmt.setDate(++i, new java.sql.Date( params.getToDate().getTime() ) );
		}
		if (params.getSecurityLevel() != null ) {
			entryStmt.setInt(++i, params.getSecurityLevel().ordinal() );
		}
		if ( params.getAccountLevel() < 5) {
			account = StringUtils.substring(account, 0, params.getAccountLevel() );	
		} 
		account += PERCENT;
		entryStmt.setString(++i, account);
		ResultSet entrySet = entryStmt.executeQuery();
		Balance b = null;
		if (entrySet.next()) {
			b = new Balance();
			b.setFromDate(entrySet.getDate(1));
			b.setDebit( CommonUtil.round(entrySet.getDouble(2)) );
			b.setCredit(CommonUtil.round(entrySet.getDouble(3)) );
		}
		entrySet.close();
		return b;
	}

	private StringBuffer getEntryStatement(SummaryProviderParameters params) {
		StringBuffer stmt = new StringBuffer();
		stmt.append("SELECT a.entry_date,SUM(d.debit),SUM(d.credit)");
		stmt.append(" FROM account_entry a,account_entry_detail d ");
		stmt.append(" WHERE a.id = d.account_entry");
		if (params.getPeriod() != null && params.getPeriod().getId() != null) {
			stmt.append(" AND a.account_period = ?");
		}
		stmt.append(" AND a.entry_type = ?");
		if (params.getFromDate() != null ) {
			stmt.append(" AND a.entry_date >= ?");
		}
		if (params.getToDate() != null ) {
			stmt.append(" AND a.entry_date <= ?");
		}
		if (params.getSecurityLevel() != null ) {
			stmt.append(" AND a.security_level = ?");
		}
		
		
		// CUIDADO! A tener en cuenta si se modifica. La siguiente variable 
		// host se añade dentro del bucle. El índice depende de las 
		// que se hayan añadido anteriormente en función de los parámetros.
 		stmt.append(" AND d.account LIKE ?");
 		// ----------------------------------------------------------------
 		
		stmt.append(" GROUP BY a.entry_date");
		return stmt;
	}
	
	private StringBuffer getStatement(SummaryProviderParameters params) {
		StringBuffer sumStmt = new StringBuffer();
		sumStmt.append("SELECT SUM(d.debit),SUM(d.credit)");
		if (params.isMonthlyGrouping()) {
			sumStmt.append(",MONTH(s.entry_date)");
		}
		sumStmt.append(" FROM account_entry s, account_entry_detail d");
		sumStmt.append(" WHERE s.id = d.account_entry");
		if (params.getFromDate() != null) {
			sumStmt.append(" AND s.entry_date >= ?");
		}
		if (params.getToDate() != null) {
			sumStmt.append(" AND s.entry_date <= ?");
		}
		if (params.getPeriod() != null && params.getPeriod().getId() != null) {
			sumStmt.append(" AND s.account_period = ?");
		}
		if (params.getSecurityLevel() != null) {
			sumStmt.append(" AND s.security_level = ?");
		}
		sumStmt.append(" AND d.account LIKE ?");
		if (params.isMonthlyGrouping()) {
			sumStmt.append(" GROUP BY MONTH(s.entry_date)");
		}
		return sumStmt;
	}

	private Summary getSummaryMonthly(ResultSet sumSet, String accountId, double debit, double credit, boolean authomaticBalance, Balance oeb, Balance ceb) throws SQLException {
		List<Double> months = new ArrayList<Double>(12);
		for (int i = 0; i < 12; i++) {
			months.add(new Double(0));
		}
		int operatingMonth = -1;
		if (oeb != null) {
			Date operatingEntryDate = oeb.getFromDate();
			Calendar c = Calendar.getInstance();
			c.setTime(operatingEntryDate);
			operatingMonth = c.get(Calendar.MONTH);
		}
		int closingMonth = -1;
		if (ceb != null) {
			Date closingEntryDate = ceb.getFromDate();
			Calendar c = Calendar.getInstance();
			c.setTime(closingEntryDate);
			closingMonth = c.get(Calendar.MONTH);
		}
		double sumDebit = 0.0;
		double sumCredit = 0.0;
		while (sumSet.next()) {
			debit = CommonUtil.round(sumSet.getDouble(1));
			credit = CommonUtil.round(sumSet.getDouble(2));
			sumDebit = CommonUtil.round(sumDebit + debit);
			sumCredit = CommonUtil.round(sumCredit + credit);
			double amount = 0;
			int month = (sumSet.getInt(3) - 1);
			if (oeb != null && month==operatingMonth) {
				credit = CommonUtil.round(credit - oeb.getCredit());	
				debit = CommonUtil.round(debit - oeb.getDebit());	
			}
			if (ceb != null && month==closingMonth) {
				credit = CommonUtil.round(credit - ceb.getCredit());	
				debit = CommonUtil.round(debit - ceb.getDebit());	
			}
			if (authomaticBalance && accountId.startsWith("7") ) {
				amount = CommonUtil.round(credit - debit);
			} else {
				amount = CommonUtil.round(debit - credit);
			}
			
			months.set(month, amount);
		}
		SummaryMonthly sm = new SummaryMonthly();
		sm.setMonths(months);
		sm.setDebit(sumDebit);
		sm.setCredit(sumCredit);
		return sm;
	}

	private Criteria getCriteria(SummaryProviderParameters params) throws ExpressionException {
		Criteria criteria = new Criteria();
		if (!StringUtils.isEmpty(params.getAccountExpression())) {
			criteria.addExpression(ExpressionUtilities
					.getExpression(params.getAccountExpression(), ACCOUNT_ID_ALIAS));
		}
		if (!StringUtils.isEmpty(params.getAccountDescription())) {
			criteria.addExpression(ExpressionUtilities.getExpression(params.getAccountDescription(), 
					ACCOUNT_DESCRIPTION_ALIAS));
		}
		if (!StringUtils.isEmpty(params.getAccountAlias())) {
			criteria.addExpression(ExpressionUtilities
					.getExpression(params.getAccountAlias(), ACCOUNT_ALIAS_ALIAS));
		}
		if (!params.isLowerLevelVisible()) {
			Expression e1 = ExpressionUtilities.getEqualExpression(ACCOUNT_LEVEL_ALIAS, params.getAccountLevel());
			Expression e21 = ExpressionUtilities.getLessThanExpression(ACCOUNT_LEVEL_ALIAS, params.getAccountLevel());
			Expression e22 = ExpressionUtilities.getEqualExpression(ACCOUNT_ENTRY_ENABLED_ALIAS, true);
			Expression e2 = ExpressionUtilities.getAndExpression(e21, e22);
			Expression e = ExpressionUtilities.getOrExpression(e1, e2);
			criteria.addExpression(e);
		} else {
			Expression e = ExpressionUtilities.getLessThanOrEqualExpression(ACCOUNT_LEVEL_ALIAS, params.getAccountLevel());
			criteria.addExpression(e);
		}
		criteria.addOrder(ACCOUNT_ID_ALIAS);
		return criteria;
	}

	@SuppressWarnings("deprecation")
	private PreparedStatement prepareStatement(String sessionName,PreparedStatement sum, StringBuffer sumStmt, SummaryProviderParameters params) throws SQLException {
		sum = HibernateUtil.getSQLConnection(sessionName).prepareStatement(sumStmt.toString());
		int p = 1;
		if (params.getFromDate() != null) {
			sum.setDate(p, new java.sql.Date(params.getFromDate().getTime()));
			p++;
		}
		if (params.getToDate() != null) {
			sum.setDate(p, new java.sql.Date(params.getToDate().getTime()));
			p++;
		}
		if (params.getPeriod() != null && params.getPeriod().getId() != null) {
			sum.setString(p, params.getPeriod().getId());
			p++;
		}
		if (params.getSecurityLevel() != null) {
			sum.setInt(p, params.getSecurityLevel().ordinal());
		}
		return sum;
	}

	public SummaryCollection getTotalExpensesSummaryCollection(SummaryProviderParameters params) throws ManagerBeanException {
		String accountExpression = ">=610&<7";
		params.setAccountExpression(accountExpression);
		return getSummaryCollection(params);
	}

	public SummaryCollection getGrossMarginSummaryCollection(SummaryProviderParameters params) throws ManagerBeanException {
		String accountExpression = "60*|7*";
		params.setAccountExpression(accountExpression);
		return getSummaryCollection(params);
	}
	
	public Balance getOpeningEntryBalance(Date date, String accountId, SecurityLevel securityLevel) throws ManagerBeanException {
		return getAccountEntryBalance(date, accountId, AccountEntryType.OPENING,securityLevel);
	}
	public Balance getOpeningEntryBalance(Period period, String accountId, SecurityLevel securityLevel) throws ManagerBeanException {
		if (period == null) {
			throw new IllegalArgumentException("Period param must not be null.");
		}
		return getAccountEntryBalance(period.getDeadline(), accountId, AccountEntryType.OPENING,securityLevel);
	}

	public Balance getOperatingEntryBalance(Date date, String accountId, SecurityLevel securityLevel) throws ManagerBeanException {
		return getAccountEntryBalance(date, accountId, AccountEntryType.OPERATING,securityLevel);
	}
	public Balance getOperatingEntryBalance(Period period, String accountId, SecurityLevel securityLevel) throws ManagerBeanException {
		if (period == null) {
			throw new IllegalArgumentException("Period param must not be null.");
		}
		return getAccountEntryBalance(period.getDeadline(), accountId, AccountEntryType.OPERATING,securityLevel);
	}

	public Balance getClosingEntryBalance(Date date, String accountId,SecurityLevel securityLevel) throws ManagerBeanException {
		return getAccountEntryBalance(date, accountId, AccountEntryType.CLOSING,securityLevel);
	}
	public Balance getClosingEntryBalance(Period period, String accountId, SecurityLevel securityLevel) throws ManagerBeanException {
		if (period == null) {
			throw new IllegalArgumentException("period param must not be null.");
		}
		return getAccountEntryBalance(period.getDeadline(), accountId, AccountEntryType.CLOSING,securityLevel);
	}

	@SuppressWarnings("deprecation")
	private Balance getAccountEntryBalance(Date date, String accountId, AccountEntryType type,SecurityLevel securityLevel)
			throws ManagerBeanException {
		if (date == null) {
			throw new IllegalArgumentException("date param must not be null.");
		}
		if (accountId == null) {
			throw new IllegalArgumentException("accountId param must not be null.");
		}
		if (type == null) {
			throw new IllegalArgumentException("type param must not be null.");
		}
		
		PreparedStatement entryStmt = null;
		ResultSet entrySet  = null;
		try {
			StringBuffer stmt = new StringBuffer();
			stmt.append("SELECT a.id,a.entry_date,SUM(d.debit),SUM(d.credit)");
			stmt.append(" FROM account_entry a,account_entry_detail d ");
			stmt.append(" WHERE a.id = d.account_entry");
			stmt.append(" AND a.entry_type = ?");
			stmt.append(" AND a.entry_date <= ?");
			if (securityLevel != null ) {
				stmt.append(" AND a.security_level = ?");
			}
	 		stmt.append(" AND d.account = ?");
			stmt.append(" GROUP BY a.id,a.entry_date");
	 		stmt.append(" ORDER BY a.entry_date desc");
			String sessionName = HibernateUtil.getSessionFactoryName(AccountEntry.class.getName());
			entryStmt = HibernateUtil.getSQLConnection(sessionName).prepareStatement(stmt.toString());
			int i = 0;
			entryStmt.setInt(++i, type.ordinal());
			entryStmt.setDate(++i, new java.sql.Date( date.getTime() ) );
			if (securityLevel != null ) {
				entryStmt.setInt(++i, securityLevel.ordinal() );
			}
			entryStmt.setString(++i, accountId);
			entrySet = entryStmt.executeQuery();
			Balance b = null;
			if (entrySet.next()) {
				b = new Balance();
				b.setAccountEntry(entrySet.getInt(1));
				b.setFromDate(entrySet.getDate(2));
				b.setDebit( CommonUtil.round(entrySet.getDouble(3)) );
				b.setCredit(CommonUtil.round(entrySet.getDouble(4)) );
				b.setUnpaidBalance(CommonUtil.round(entrySet.getDouble(3)) );
				b.setCreditBalance(CommonUtil.round(entrySet.getDouble(4)) );
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
/*
		
		IManagerBean entryBean = BeanManager.getManagerBean(AccountEntry.class);
		Criteria c = new Criteria();
		String alias = entryBean.getFieldName(IAccountingAlias.ACCOUNT_ENTRY_ENTRY_DATE);
		c.addLessThanOrEqualExpression(alias, date);
		c.addOrder(alias, false);
		c.addEqualExpression(entryBean.getFieldName(IAccountingAlias.ACCOUNT_ENTRY_TYPE), type);
		if ( securityLevel != null) {
			c.addEqualExpression(entryBean.getFieldName(IAccountingAlias.ACCOUNT_ENTRY_SECURITY_LEVEL), securityLevel);	
		}
		List<ITransferObject> list = entryBean.getList(c);
		if (list.size() == 0) {
			return null;
		}
		IManagerBean entryDetailBean = BeanManager.getManagerBean(AccountEntryDetail.class);
		double debit = 0;
		double credit = 0;
		AccountEntry entry = (AccountEntry) list.get(0);
		Integer id = entry.getId();
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(entryDetailBean
				.getFieldName(IAccountingAlias.ACCOUNT_ENTRY_DETAIL_ACCOUNT_ENTRY_ID), id);
		criteria.addEqualExpression(entryDetailBean
				.getFieldName(IAccountingAlias.ACCOUNT_ENTRY_DETAIL_ACCOUNT_ID), accountId);
		List<ITransferObject> details = entryDetailBean.getList(criteria);
		if (details.size() > 0) {
			for (ITransferObject to : details) {
				AccountEntryDetail detail = (AccountEntryDetail) to;
				debit = CommonUtil.round(debit + detail.getDebit());
				credit = CommonUtil.round(credit + detail.getCredit());
			}
		}
		Balance balance = null;
		if (debit != 0 || credit != 0) {
			balance = new Balance();
			balance.setAccountEntry(entry.getId());
			balance.setFromDate(entry.getEntryDate());
			balance.setDebit(debit);
			balance.setCredit(credit);
			balance.setUnpaidBalance(debit);
			balance.setCreditBalance(credit);
		}
		return balance;
	}
*/		

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
			SummaryCollection sc = getSummaryCollection(params);
			if (sc.getSummaryList()!= null && !sc.getSummaryList().isEmpty() ) {
				Summary summary = sc.getSummaryList().get(0);
				balance.setFromDate(fromDate);
				balance.setToDate(toDate);
				balance.setDebit(summary.getDebit());
				balance.setCredit(summary.getCredit());
				balance.setUnpaidBalance(summary.getUnpaidBalance());
				balance.setCreditBalance(summary.getCreditBalance());
			}
		}
		return balance;
	}
	
	
}
