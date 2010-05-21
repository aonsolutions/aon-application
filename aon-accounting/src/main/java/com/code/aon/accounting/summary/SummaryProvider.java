package com.code.aon.accounting.summary;

import java.io.StringWriter;
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
			StringWriter sumStmt = getStatement(params);
			IManagerBean accountBean = BeanManager.getManagerBean(Account.class);
			List<ITransferObject> accountList = accountBean.getList(getCriteria(params));
			boolean operatingEntry = false;
			Period period = params.getPeriod();
			if (params.isExcludeOperatingEntry() && params.getPeriod() != null && params.getPeriod().getId() != null) {
				operatingEntry = getAccountingUtil().existsEntry(period, AccountEntryType.OPERATING, params.getSecurityLevel());
			}
			boolean closingEntry = false;
			if (params.isExcludeClosingEntry() && params.getPeriod() != null && params.getPeriod().getId() != null) {
				closingEntry = getAccountingUtil().existsEntry(period, AccountEntryType.CLOSING, params.getSecurityLevel());
			}
			if (operatingEntry || closingEntry) {
				StringBuilder entryStatement = getEntryStatement(params);
				entryStmt = HibernateUtil.getSQLConnection(sessionName).prepareStatement(entryStatement.toString());	
			}
			sum = prepareStatement(sessionName,sum,sumStmt,params);
			boolean add;
			SummaryCollection sc = new SummaryCollection();
			for (ITransferObject to : accountList) {
				Account account = (Account) to;
				add = true;
				String likeAccount = account.getId() + PERCENT; 
				sum.setString(1, likeAccount);
				sumSet = sum.executeQuery();
				Double debit = 0.0;
				Double credit = 0.0;
				Summary s = null;
				Balance oeb = null;
				Balance ceb = null;
				if (operatingEntry) {
					oeb = excludeEntry(entryStmt,AccountEntryType.OPERATING.ordinal(),account.getId(), params );
					credit = (oeb!=null)?CommonUtil.round(credit - oeb.getCredit()):credit;	
					debit = (oeb!=null)?CommonUtil.round(debit - oeb.getDebit()):debit;	
				}
				if (closingEntry) {
					ceb = excludeEntry(entryStmt,AccountEntryType.CLOSING.ordinal(),account.getId(), params );
					credit = (ceb!=null)?CommonUtil.round(credit - ceb.getCredit()):credit;	
					debit = (ceb!=null)?CommonUtil.round(debit - ceb.getDebit()):debit;	
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
					s = getSummaryMonthly(sumSet,account.getId(),debit,credit,authomaticBalance,oeb,ceb);
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
					s.setLastLevel(params.getAccountLevel() == account.getLevel()
							|| account.isEntryEnabled());
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
		entryStmt.setInt(1, ordinal);
		
		if ( params.getAccountLevel() < 5) {
			account = StringUtils.substring(account, 0, params.getAccountLevel() );	
		} 
		account += PERCENT;
		entryStmt.setString(2, account);
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

	private StringBuilder getEntryStatement(SummaryProviderParameters params) {
		StringBuilder stmt = new StringBuilder();
		stmt.append("SELECT a.entry_date,SUM(d.debit),SUM(d.credit)");
		stmt.append(" FROM account_entry a,account_entry_detail d ");
		stmt.append(" WHERE a.account_period = '");
		stmt.append( params.getPeriod().getId() );
		stmt.append("'");
		stmt.append(" AND a.entry_type = ?");
		if (params.getSecurityLevel() != null ) {
			stmt.append(" AND a.security_level = ");
			stmt.append( Integer.toString( params.getSecurityLevel().ordinal()));
		}
		stmt.append(" AND a.id = d.account_entry");
		stmt.append(" AND d.account LIKE ?");
		stmt.append(" GROUP BY a.entry_date");
		return stmt;
	}
	
	private StringWriter getStatement(SummaryProviderParameters params) {
		StringWriter sumStmt = new StringWriter();
		sumStmt.append("SELECT SUM(s.debit),SUM(s.credit)");
		if (params.isMonthlyGrouping()) {
			sumStmt.append(",MONTH(s.entry_date)");
		}
		if (!params.isBudgeted()) {
			sumStmt.append(" FROM account_summary s ");
		} else {
			sumStmt.append(" FROM account_budget_detail s ");
		}
		sumStmt.append(" WHERE s.account LIKE ?");
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

	private PreparedStatement prepareStatement(String sessionName,PreparedStatement sum, StringWriter sumStmt, SummaryProviderParameters params) throws SQLException {
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

}
