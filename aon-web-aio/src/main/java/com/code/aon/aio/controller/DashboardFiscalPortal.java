package com.code.aon.aio.controller;

import static com.esferalia.aon.jooq.tables.Account.ACCOUNT;
import static com.esferalia.aon.jooq.tables.AccountEntry.ACCOUNT_ENTRY;
import static com.esferalia.aon.jooq.tables.AccountEntryDetail.ACCOUNT_ENTRY_DETAIL;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Connection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.faces.event.ActionEvent;

import org.apache.commons.lang.StringUtils;
import org.jooq.AggregateFunction;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record4;
import org.jooq.Result;
import org.jooq.impl.DSL;

import com.code.aon.AonVersion;
import com.code.aon.accounting.Period;
import com.code.aon.accounting.util.AccountingUtil;
import com.code.aon.common.enumeration.Month;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.dbutils.DatabaseUtil;
import com.code.aon.pool.AonConnectionException;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.occam.api.ACCOUNTING;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.accounting.AccMiningParameters;
import com.esferalia.aon.occam.api.model.accounting.AccountBalance;
import com.esferalia.aon.occam.api.model.accounting.IAccMiningKeyAccept;
import com.esferalia.aon.occam.server.accounting.AccMiningMVELContext;
import com.esferalia.aon.watson.error.AonCoreException;

public class DashboardFiscalPortal implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	public static final String FISCAL_PORTLET_TAB = "fiscalPortletTabF";
	
	private String selectedTab = "fiscalPortletTabY";
	
	private DashboardPygEntry[] yearPygEntries;
	private DashboardPygEntry[] pygEntries;
	private Period pygEntriesPeriod;
	private DashboardEntry[] expensesEntries;
	private Period expensesPeriod;
	
	private int domainId;
	private String domainName;
	
	public String getSelectedTab() {
		return selectedTab;
	}
	public void setSelectedTab(String selectedTab) {
		this.selectedTab = selectedTab;
	}

	public int getDomainId() {
		return domainId;
	}
	public void setDomainId(int domainId) {
		this.domainId = domainId;
	}

	public String getDomainName() {
		return domainName;
	}
	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}
	public Period getPygEntriesPeriod() {
		return pygEntriesPeriod;
	}
	public void setPygEntriesPeriod(Period pygEntriesPeriod) {
		this.pygEntriesPeriod = pygEntriesPeriod;
	}
	
	public Period getExpensesPeriod() {
		return expensesPeriod;
	}
	public void setExpensesPeriod(Period expensesPeriod) {
		this.expensesPeriod = expensesPeriod;
	}
	private AccMiningParameters getParams( Period period) {
		AccMiningParameters params = new AccMiningParameters();
		params.setDomain(getDomainId());
		params.setStartDate(period.getInitiationDate() );
		params.setEndDate(period.getDeadline());
		return params;
	}
	public void onPygGraph(ActionEvent event) {
		pygEntries = null;
	}
	public DashboardPygEntry[] getPygEntries() {
		if (pygEntries == null) {
			Connection c = null;
			pygEntries = new DashboardPygEntry[12];
			try {
				for (int i = 0 ; i < 12; i++) {
					pygEntries[i] = new DashboardPygEntry();
					pygEntries[i].setPeriod(Month.getMonthByValue(i).getName(AonUtil.getCurrentLocale()));
				}
				c = DatabaseUtil.getConnection(getDomainName());
				DSLContext dsl = DSL.using(c, AccountingUtil.getDefaultSettings());
				AggregateFunction<BigDecimal> sumDebit = DSL.sum(ACCOUNT_ENTRY_DETAIL.DEBIT);
				AggregateFunction<BigDecimal> sumCredit = DSL.sum(ACCOUNT_ENTRY_DETAIL.CREDIT);
				Field<Integer> monthField = DSL.month(ACCOUNT_ENTRY.ENTRY_DATE);
				Field<String> codeField = DSL.substring(ACCOUNT.CODE, 1, 4);
				Result<Record4<Integer,String,BigDecimal,BigDecimal>> result = 
					dsl.select(monthField,codeField,sumDebit,sumCredit)
					.from(ACCOUNT_ENTRY_DETAIL)
					.join(ACCOUNT_ENTRY).onKey()
					.join(ACCOUNT).on(ACCOUNT.ID.equal(ACCOUNT_ENTRY_DETAIL.ACCOUNT))
					.where(ACCOUNT_ENTRY_DETAIL.DOMAIN.equal(getDomainId()))
					.and(ACCOUNT_ENTRY.ENTRY_DATE.greaterOrEqual(new java.sql.Date(getPygEntriesPeriod().getInitiationDate().getTime())))
					.and(ACCOUNT_ENTRY.ENTRY_DATE.lessOrEqual(new java.sql.Date(getPygEntriesPeriod().getDeadline().getTime())))
					.and(ACCOUNT_ENTRY.ENTRY_TYPE.notEqual((byte) 2))
					.and( ACCOUNT.CODE.like("6%").or( ACCOUNT.CODE.like("7%") ))
					.groupBy(monthField,codeField)
					.fetch();
				for (Record4<Integer,String,BigDecimal,BigDecimal> record : result) {
					int month = record.getValue(monthField) - 1;
					String code = record.getValue(codeField);
					double debit = record.getValue(sumDebit).doubleValue();
					double credit = record.getValue(sumCredit).doubleValue();
					if (StringUtils.startsWith(code,"7")) {
						pygEntries[month].setIncome( CommonUtil.round( pygEntries[month].getIncome() + credit - debit) );
					} else if (StringUtils.startsWith(code,"60")) {
						pygEntries[month].setPurchase( CommonUtil.round( pygEntries[month].getPurchase() + debit - credit) );					
					} else if (StringUtils.startsWith(code,"68")) {
						pygEntries[month].setAmortization( CommonUtil.round( pygEntries[month].getAmortization() + debit - credit) );					
					} else if (StringUtils.startsWith(code,"6")) {
						pygEntries[month].setExpense( CommonUtil.round( pygEntries[month].getExpense() + debit - credit) );
					} 
				}
			} catch (AonConnectionException e) {
				e.printStackTrace();
				// Nothing. Se mostrara array vacio.
			} finally {
				DatabaseUtil.closeQuietly(c);
			}
		}
		return pygEntries;
	}

	public void onYearPygGraph(ActionEvent event) {
		yearPygEntries = null;
	}
	public DashboardPygEntry[] getYearPygEntries() {
		if (yearPygEntries == null) {
			Connection c = null;
			List<DashboardPygEntry> list = new LinkedList<DashboardPygEntry>();
			try {
				c = DatabaseUtil.getConnection(getDomainName());
				DSLContext dsl = DSL.using(c, AccountingUtil.getDefaultSettings());
				AggregateFunction<BigDecimal> sumDebit = DSL.sum(ACCOUNT_ENTRY_DETAIL.DEBIT);
				AggregateFunction<BigDecimal> sumCredit = DSL.sum(ACCOUNT_ENTRY_DETAIL.CREDIT);
				Field<Integer> yearField = DSL.year(ACCOUNT_ENTRY.ENTRY_DATE);
				Field<String> codeField = DSL.substring(ACCOUNT.CODE, 1, 4);
				Result<Record4<Integer,String,BigDecimal,BigDecimal>> result = 
					dsl.select(yearField,codeField,sumDebit,sumCredit)
					.from(ACCOUNT_ENTRY_DETAIL)
					.join(ACCOUNT_ENTRY).onKey()
					.join(ACCOUNT).on(ACCOUNT.ID.equal(ACCOUNT_ENTRY_DETAIL.ACCOUNT))
					.where(ACCOUNT_ENTRY_DETAIL.DOMAIN.equal(getDomainId()))
					.and(ACCOUNT_ENTRY.ENTRY_TYPE.notEqual((byte) 2))
					.and( ACCOUNT.CODE.like("6%").or( ACCOUNT.CODE.like("7%") ))
					.groupBy(yearField,codeField)
					.fetch();
				for (Record4<Integer,String,BigDecimal,BigDecimal> record : result) {
					int year = record.getValue(yearField);
					DashboardPygEntry entry = null;
					for (DashboardPygEntry entryList : list ) {
						if (entryList.getPeriod().equals(Integer.toString(year))) {
							entry = entryList;
						}
					}
					if (entry == null) {
						entry = new DashboardPygEntry();
						entry.setPeriod(Integer.toString(year));
						list.add(entry);
					}
					String code = record.getValue(codeField);
					double debit = record.getValue(sumDebit).doubleValue();
					double credit = record.getValue(sumCredit).doubleValue();
					if (StringUtils.startsWith(code,"7")) {
						entry.setIncome( CommonUtil.round( entry.getIncome() + credit - debit) );
					} else if (StringUtils.startsWith(code,"60")) {
						entry.setPurchase( CommonUtil.round( entry.getPurchase() + debit - credit) );					
					} else if (StringUtils.startsWith(code,"68")) {
						entry.setAmortization( CommonUtil.round( entry.getAmortization() + debit - credit) );					
					} else if (StringUtils.startsWith(code,"6")) {
						entry.setExpense( CommonUtil.round( entry.getExpense() + debit - credit) );
					} 
				}
				yearPygEntries = list.toArray( new DashboardPygEntry[list.size()]);
			} catch (AonConnectionException e) {
				e.printStackTrace();
				// Nothing. Se mostrara array vacio.
			} finally {
				DatabaseUtil.closeQuietly(c);
			}
		}
		return yearPygEntries;
	}

	public void onExpenseGraph(ActionEvent event) {
		expensesEntries = null;
	}
	public DashboardEntry[] getExpensesEntries() {
		if (expensesEntries == null) {
			List<DashboardEntry> list = new LinkedList<DashboardEntry>();
			AONContext aonctx = null;
			try {
				aonctx = AONContext.getAONContext(getDomainName(), getDomainId());  
				AccMiningMVELContext ctx = getAccMiningContext();
				Map<String,AccountBalance> map = ACCOUNTING.getAccountBalances(aonctx, getParams(expensesPeriod)); 
				ctx.setAccounts( map );
				for (String key : map.keySet()) {
					if ( StringUtils.startsWith(key, "6") && !StringUtils.startsWith(key, "60") ) {
						if (StringUtils.length(key) == 4) {
							Double value = (Double) ctx.evaluateExpression("expense","sdp({"+key+"})");
							if (value > 0.0) {
								DashboardEntry entry = new DashboardEntry();
								Account account = ACCOUNTING.getAccount(aonctx,key) ;
								entry.setName(account==null?key:account.getDescription());
								entry.setValue(value);
								list.add(entry);
							}
						}
					}
				}
				expensesEntries = list.toArray( new DashboardEntry[list.size()]);
			} catch (AonCoreException e) {
				// Nothing. Se mostrara array vacio.
				e.printStackTrace();
			} finally {
				if (aonctx != null) {
					aonctx.close();
				}
			}
		}
		return expensesEntries;
	}

	private AccMiningMVELContext getAccMiningContext() {
		AccMiningMVELContext ctx = new AccMiningMVELContext(new IAccMiningKeyAccept() {
			@Override
			public boolean acceptKey(Object key) {
				return ("income".equals(key) ||
						"purchase".equals(key) ||
						"expense".equals(key));
			}
		});
		return ctx;
	}
}


