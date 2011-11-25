package com.code.aon.accounting.summary;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.account.Account;
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
import com.code.aon.entity.IEntityAlias;
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
			ACCOUNT_ID_ALIAS = accountBean.getFieldName(IEntityAlias.ACCOUNT_ID);
			ACCOUNT_DESCRIPTION_ALIAS = accountBean.getFieldName(IEntityAlias.ACCOUNT_DESCRIPTION);
			ACCOUNT_ALIAS_ALIAS = accountBean.getFieldName(IEntityAlias.ACCOUNT_ALIAS);
			ACCOUNT_LEVEL_ALIAS = accountBean.getFieldName(IEntityAlias.ACCOUNT_LEVEL);
			ACCOUNT_ENTRY_ENABLED_ALIAS = accountBean.getFieldName(IEntityAlias.ACCOUNT_ENTRY_ENABLED);
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
				if (add && !params.isLowerLevelVisible() && !account.isEntryEnabled()
						&& account.getLevel() < params.getAccountLevel()) {
					add = false;
				}
				if (add && params.isLowerLevelVisible() && account.getLevel() < params.getFromAccountLevel()) {
					add = false;
				}
				if (add) {
					String likeAccount = account.getId() + PERCENT;
					sum.setString(p, likeAccount);
					sumSet = sum.executeQuery();
					Double debit = 0.0;
					Double credit = 0.0;
					Summary s = null;
					if (!params.isMonthlyGrouping()) {
						if (sumSet.next()) {
							debit = CommonUtil.round(debit + sumSet.getDouble(1));
							credit = CommonUtil.round(credit + sumSet.getDouble(2));
						}
						s = new Summary();
						s.setDebit(debit);
						s.setCredit(credit);
					} else {
						s = getSummaryMonthly(sumSet,account.getId(),debit,credit,authomaticBalance);
					}
					if (add) {
						s.setId(account.getId());
						s.setDescription(account.getDescription());
						s.setLastLevel(params.getAccountLevel() == account.getLevel() || account.isEntryEnabled());
						s.setInitialCredit(0);					
						s.setInitialDebit(0);
						
						if (s.isLastLevel()) {
							Balance openingBalance = getOpeningEntryBalance(params.getStartDate(), account.getId(),params.getSecurityLevel());
							Balance fromOpeningBalance = null;
							Date dateTo = DateUtils.addDays(params.getStartDate(), -1);
							if (openingBalance!=null) {
								s.setInitialDebit(openingBalance.getDebit());
								s.setInitialCredit(openingBalance.getCredit());

								// Si la fecha del asiento de apertura encontrado esta en el rango de fechas pedidas, se asume que el 
								// importe del asiento de apertura pertenece al ejercicio.
								long op = openingBalance.getFromDate().getTime();
								long pa = params.getStartDate().getTime();
								if (op >= pa) {
									s.setOpeningDebit(openingBalance.getDebit());
									s.setOpeningCredit(openingBalance.getCredit());
								}
								
								// Si existe un asiento de apertura que sirva como punto de partida, se calcula el acumulado 
								// desde el asiento de apertura hasta el inicio del periodo solicitado, en el caso de no ser el mismo dia. 
								if (!DateUtils.isSameDay(openingBalance.getFromDate(), params.getStartDate())) {
									fromOpeningBalance = getPeriodBalance(openingBalance.getFromDate(), dateTo,account.getId(),params.getSecurityLevel(),false,false);
								}
							} else {
								// Si no existe un asiento de apertura que sirva como punto de partida, se calcula el acumulado 
								// desde el principio de los tiempos hasta el inicio del periodo solicitado.
								fromOpeningBalance = getPeriodBalance(new Date(0), dateTo, account.getId(),params.getSecurityLevel(),false,false);
							}
							if (fromOpeningBalance!=null) {
								if (openingBalance!=null) {
									// Si exiten acumulados anteriores y asiento de apertura, éste se descuenta de los acumulados anteriores.
									fromOpeningBalance.substractBalance(openingBalance);
								}
								s.setInitialDebit(s.getInitialDebit() + fromOpeningBalance.getDebit());
								s.setInitialCredit(s.getInitialCredit() + fromOpeningBalance.getCredit());
							}
						}
					}
					if (params.isExcludeBalancedAccounts() && CommonUtil.round(
							(s.getDebit() + s.getOpeningDebit()) - 
							(s.getCredit() + s.getOpeningCredit())) == 0) {
						add = false;
					}
					if (add 
							&& !params.isNoTouchedAccountVisible() 
							&& CommonUtil.round(s.getDebit()) == 0 
							&& CommonUtil.round(s.getCredit()) == 0
							&& CommonUtil.round(s.getOpeningDebit()) == 0 
							&& CommonUtil.round(s.getOpeningCredit()) == 0									
						) {
							add = false;
						}
					if (add) {
						sc.add(s);
					}
					
					sumSet.close();
				}
			}
			HibernateUtil.commitTransaction(sessionName);
			if (params.isLowerLevelVisible()) {
				fillLowerLevelAccounts(sc);
			}
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

	private void fillLowerLevelAccounts(SummaryCollection sc) {
		for (Summary s: sc.getSummaryList()) {
			if (!s.isLastLevel()) {
				for (Summary s0: sc.getSummaryList()) {
					if (s0.isLastLevel() && StringUtils.startsWith(s0.getId(), s.getId())  ) {
						s.setInitialCredit( CommonUtil.round(s.getInitialCredit() + s0.getInitialCredit()));
						s.setInitialDebit( CommonUtil.round(s.getInitialDebit() + s0.getInitialDebit()));
					}
				}
			}
		}
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
		if (params.isPeriodNotNull()) {
			sumStmt.append(" AND s.account_period = ?");
		}
		if (params.isExcludeOpeningEntry()) {
			sumStmt.append(" AND s.entry_type != " + AccountEntryType.OPENING.ordinal());
		}
		if (params.isExcludeOperatingEntry()) {
			sumStmt.append(" AND s.entry_type != " + AccountEntryType.OPERATING.ordinal());
		}
		if (params.isExcludeClosingEntry()) {
			sumStmt.append(" AND s.entry_type != " + AccountEntryType.CLOSING.ordinal());
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

	private Summary getSummaryMonthly(ResultSet sumSet, String accountId, double debit, double credit, boolean authomaticBalance) throws SQLException {
		Double[] months = {0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0};
		double sumDebit = 0.0;
		double sumCredit = 0.0;
		while (sumSet.next()) {
			debit = CommonUtil.round(sumSet.getDouble(1));
			credit = CommonUtil.round(sumSet.getDouble(2));
			sumDebit = CommonUtil.round(sumDebit + debit);
			sumCredit = CommonUtil.round(sumCredit + credit);
			double amount = 0;
			int month = (sumSet.getInt(3) - 1);
			if (authomaticBalance && accountId.startsWith("7") ) {
				amount = CommonUtil.round(credit - debit);
			} else {
				amount = CommonUtil.round(debit - credit);
			}
			months[month] = amount;
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
				stmt.append(" FROM account_entry a,account_entry_detail d ");
				stmt.append(" WHERE a.id = d.account_entry");
				stmt.append(" AND a.entry_type = ?");
				stmt.append(" AND a.entry_date = ?");
				if (securityLevel != null ) {
					stmt.append(" AND a.security_level = ?");
				}
		 		stmt.append(" AND d.account LIKE ?");
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
			StringBuffer sumStmt = getStatement(params);
			try {
				int p = 1  
				 + (params.getFromDate() != null?1:0)
				 + (params.getToDate() != null?1:0)
				 + (params.getPeriod() != null && params.getPeriod().getId() != null?1:0)
				 + (params.getSecurityLevel() != null?1:0);
				sum = prepareStatement(sessionName,sum,sumStmt,params);
				String likeAccount = accountId + PERCENT;
				sum.setString(p, likeAccount);
				sumSet = sum.executeQuery();
				if (sumSet.next()) {
					balance.setFromDate(fromDate);
					balance.setToDate(toDate);
					balance.set(CommonUtil.round(sumSet.getDouble(1)),CommonUtil.round(sumSet.getDouble(2)));
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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
	
	
}
