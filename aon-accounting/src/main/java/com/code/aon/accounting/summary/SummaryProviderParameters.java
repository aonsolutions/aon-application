package com.code.aon.accounting.summary;

import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.code.aon.account.util.AccountUtil;
import com.code.aon.accounting.Period;
import com.code.aon.accounting.enumeration.Quarter;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.ast.sql.SqlRenderer;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ql.util.ExpressionUtilities;
import com.esferalia.aon.entity.IEntityAlias;

public class SummaryProviderParameters implements Cloneable{

	private static final String PERCENT = "%";
	private static final String ASTERISK = "*";
	private static final String SEMICOLON = "; ";
	private static final String EMPTY = "";
	private static final Date START_DATE = new Date(0);
	
	/**
	 * Literal AON-QL válido ej: 430*|400*
	 */
	private String accountExpression;

	private String accountDescription;

	private String accountAlias;

	private List<String> accountCostCenters;

	/**
	 * TRUE si se desea que se devuelva los acumulados de los nivees inferiores,
	 * como elementos de la colección, estos valores no deben tenerse en cuenta
	 * para los acumulados totales.
	 */
	private boolean lowerLevelVisible = false;
	
	/**
	 * Si se desea que se devuelvan las filas sin movimientos.
	 */
	private boolean noTouchedAccountVisible = false;
	
	/**
	 * Nivel de las cuentas. Se devolverán las filas que cumplan la siguiente condición.
	 *  
	 */
	private int accountLevel = 4;
	
	private int fromAccountLevel = 1;

	private Quarter quarter;
	/**
	 * Desde fecha.
	 */
	private Date fromDate;

	/**
	 * Hasta fecha.
	 */
	private Date toDate;

	/**
	 * Periodo. Valor Requerido. 
	 */
	private Period period;

	/**
	 * Fecha.
	 */
	private Date date;
	
	private SecurityLevel securityLevel;
	
	private int rowsPerPage;

	private boolean monthlyGrouping;
	
	private boolean excludeOpeningEntry;

	private boolean excludeOperatingEntry;
	
	private boolean excludeClosingEntry;
	
	private boolean excludeBalancedAccounts;
	
	private boolean previousPeriodVisible;

	private boolean coverVisible = false;
	private boolean counterVisible = false;
	private int pageCounter = 0;
	
	private boolean totalExpensesSummary;
	private boolean grossMarginSummary;
	
	private String domainName;

	public SummaryProviderParameters(String domain) {
		setDomainName(domain);
		setAccountExpression(null);
		setAccountDescription(null);
		setAccountAlias(null);
		setAccountCostCenters(null);
		setLowerLevelVisible(false);
		setNoTouchedAccountVisible(false);
		setAccountLevel(4);
		setFromAccountLevel(5);
		setQuarter(null);
		setFromDate(null);
		setToDate(null);
		setDate( new Date() );
		setPeriod(null);
		setSecurityLevel(null);
		setRowsPerPage(20);
		setMonthlyGrouping(false);
		setExcludeOpeningEntry(false);
		setExcludeOperatingEntry(false);
		setExcludeClosingEntry(false);
		setExcludeBalancedAccounts(false);
		setPreviousPeriodVisible(true);
		setPageCounter(0);
		setCounterVisible(false);
		setCoverVisible(false);
		setTotalExpensesSummary(false);
		setGrossMarginSummary(false);
	}
	

	public String getDomainName() {
		return domainName;
	}
	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}

	public String getAccountExpression() {
		return accountExpression;
	}
	public void setAccountExpression(String accountExpression) {
		this.accountExpression = accountExpression;
	}

	public String getAccountDescription() {
		return accountDescription;
	}

	public void setAccountDescription(String accountDescription) {
		this.accountDescription = accountDescription;
	}

	public String getAccountAlias() {
		return accountAlias;
	}

	public void setAccountAlias(String accountAlias) {
		this.accountAlias = accountAlias;
	}
	
	public List<String> getAccountCostCenters() {
		return accountCostCenters;
	}

	public void setAccountCostCenters(List<String> accountCostCenters) {
		this.accountCostCenters = accountCostCenters;
	}

	public boolean isLowerLevelVisible() {
		return lowerLevelVisible;
	}

	public void setLowerLevelVisible(boolean lowerLevelVisible) {
		this.lowerLevelVisible = lowerLevelVisible;
	}

	public boolean isNoTouchedAccountVisible() {
		return noTouchedAccountVisible;
	}

	public void setNoTouchedAccountVisible(boolean noTouchedAccountVisible) {
		this.noTouchedAccountVisible = noTouchedAccountVisible;
	}

	public int getAccountLevel() {
		return accountLevel;
	}

	public void setAccountLevel(int accountLevel) {
		this.accountLevel = accountLevel;
	}

	public int getFromAccountLevel() {
		return fromAccountLevel;
	}

	public void setFromAccountLevel(int fromAccountLevel) {
		this.fromAccountLevel = fromAccountLevel;
	}

	public Quarter getQuarter() {
		return quarter;
	}

	public void setQuarter(Quarter quarter) {
		this.quarter = quarter;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getStartDate() {
		if (getFromDate() != null) {
			return getFromDate();
		}
		if (getPeriod() != null && getPeriod().getInitiationDate() != null) {
			return getPeriod().getInitiationDate();
		}
		return START_DATE;
	}
	
	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public Period getPeriod() {
		return period;
	}
	public void setPeriod(Period period) {
		this.period = period;
	}
	public boolean isPeriodNull() {
		return (getPeriod() == null || getPeriod().getId() == null);
	}
	public boolean isPeriodNotNull() {
		return (getPeriod() != null && getPeriod().getId() != null); 
	}
	
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public SecurityLevel getSecurityLevel() {
		return securityLevel;
	}

	public void setSecurityLevel(SecurityLevel securityLevel) {
		this.securityLevel = securityLevel;
	}
	public int getRowsPerPage() {
		return rowsPerPage;
	}

	public void setRowsPerPage(int rowsPerPage) {
		this.rowsPerPage = rowsPerPage;
	}

	public boolean isMonthlyGrouping() {
		return monthlyGrouping;
	}

	public void setMonthlyGrouping(boolean monthlyGrouping) {
		this.monthlyGrouping = monthlyGrouping;
	}
	
	public boolean isExcludeOpeningEntry() {
		return excludeOpeningEntry;
	}

	public void setExcludeOpeningEntry(boolean excludeOpeningEntry) {
		this.excludeOpeningEntry = excludeOpeningEntry;
	}

	public boolean isExcludeOperatingEntry() {
		return excludeOperatingEntry;
	}

	public void setExcludeOperatingEntry(boolean excludeOperatingEntry) {
		this.excludeOperatingEntry = excludeOperatingEntry;
	}

	public boolean isExcludeClosingEntry() {
		return excludeClosingEntry;
	}

	public void setExcludeClosingEntry(boolean excludeClosingEntry) {
		this.excludeClosingEntry = excludeClosingEntry;
	}

	public boolean isExcludeBalancedAccounts() {
		return excludeBalancedAccounts;
	}
	public void setExcludeBalancedAccounts(boolean excludeBalancedAccounts) {
		this.excludeBalancedAccounts = excludeBalancedAccounts;
	}

	public boolean isPreviousPeriodVisible() {
		return previousPeriodVisible;
	}
	public void setPreviousPeriodVisible(boolean previousPeriodVisible) {
		this.previousPeriodVisible = previousPeriodVisible;
	}

	public boolean isCoverVisible() {
		return coverVisible;
	}
	public void setCoverVisible(boolean coverVisible) {
		this.coverVisible = coverVisible;
	}

	public boolean isCounterVisible() {
		return counterVisible;
	}
	public void setCounterVisible(boolean counterVisible) {
		this.counterVisible = counterVisible;
	}

	public int getPageCounter() {
		return pageCounter;
	}
	public void setPageCounter(int pageCounter) {
		this.pageCounter = pageCounter;
	}

	public boolean isConfidential() {
		return getSecurityLevel() == SecurityLevel.CONFIDENTIAL;
	}
	public boolean isNotEmptyAccountExpression(){
		return StringUtils.isNotEmpty(getAccountExpression());
	}
	public boolean isNotEmptyAccountDescription(){
		return StringUtils.isNotEmpty(getAccountDescription());
	}
	public boolean isNotEmptyAccountAlias(){
		return StringUtils.isNotEmpty(getAccountAlias());
	}

	public boolean isTotalExpensesSummary() {
		return totalExpensesSummary;
	}
	public void setTotalExpensesSummary(boolean totalExpensesSummary) {
		this.totalExpensesSummary = totalExpensesSummary;
	}

	public boolean isGrossMarginSummary() {
		return grossMarginSummary;
	}

	public void setGrossMarginSummary(boolean grossMarginSummary) {
		this.grossMarginSummary = grossMarginSummary;
	}

	@Override
	public SummaryProviderParameters clone() throws CloneNotSupportedException {
		SummaryProviderParameters cloned = new SummaryProviderParameters(getDomainName());
		cloned.setDomainName(getDomainName());
		cloned.setAccountAlias(getAccountAlias());
		if (getAccountCostCenters() != null) {
			List<String> list = new LinkedList<String>();
			for (String costCenter : getAccountCostCenters()) {
				list.add(costCenter);
			}
			cloned.setAccountCostCenters(list);	
		}
		cloned.setAccountDescription(getAccountDescription());
		cloned.setAccountExpression(getAccountExpression());
		cloned.setAccountLevel(getAccountLevel());
		cloned.setDate(getDate());
		cloned.setQuarter(getQuarter());
		cloned.setFromDate(getFromDate());
		cloned.setLowerLevelVisible(isLowerLevelVisible());
		cloned.setMonthlyGrouping(isMonthlyGrouping());
		cloned.setExcludeOpeningEntry(isExcludeOpeningEntry());
		cloned.setExcludeOperatingEntry(isExcludeOperatingEntry());
		cloned.setExcludeClosingEntry(isExcludeClosingEntry());
		cloned.setExcludeBalancedAccounts(isExcludeBalancedAccounts());
		cloned.setPreviousPeriodVisible(isPreviousPeriodVisible());
		cloned.setNoTouchedAccountVisible(isNoTouchedAccountVisible());
		cloned.setPeriod(getPeriod());
		cloned.setRowsPerPage(getRowsPerPage());
		cloned.setSecurityLevel(getSecurityLevel());
		cloned.setToDate(getToDate());
		cloned.setPageCounter(getPageCounter());
		cloned.setCounterVisible(isCounterVisible());
		cloned.setCoverVisible(isCoverVisible());
		cloned.setGrossMarginSummary(isGrossMarginSummary());
		cloned.setTotalExpensesSummary(isTotalExpensesSummary());
		return cloned;
	}
	
	
	@Override
	public String toString() {
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		StringBuffer buf = new StringBuffer();
		buf.append( isPeriodNotNull()?getPeriod().getId() + SEMICOLON: EMPTY);
		buf.append( getFromDate()!=null?formatter.format(getFromDate()) + SEMICOLON: EMPTY);
		buf.append( getToDate()!=null?formatter.format(getToDate()) + SEMICOLON: EMPTY);
		buf.append( StringUtils.isNotBlank(getAccountExpression())?getAccountExpression() + SEMICOLON: EMPTY);
		buf.append( StringUtils.isNotBlank(getAccountDescription())?getAccountDescription() + SEMICOLON: EMPTY);
		buf.append( buf.length()>0?".... ": EMPTY);
		return buf.toString();
	}

	public String getDescriptionLikeExpression() {
		return getLikeExpression(getAccountDescription());
	}
	public String getAliasLikeExpression() {
		return getLikeExpression(getAccountAlias());
	}
	public String getLikeExpression(String param) {
		if (StringUtils.isEmpty(param)) {
			return PERCENT;
		}
		if (StringUtils.countMatches(param, ASTERISK)>0) {
			return StringUtils.replace(param, ASTERISK, PERCENT);
		} 
		return PERCENT + param + PERCENT;
	}

	public String getAccountSQLExpression(String ident) throws ExpressionException {
		if (StringUtils.isNotBlank( getAccountExpression() ) ) {
			ExpressionUtilities.getExpression(getAccountExpression(), ident);
			Criteria c = new Criteria();
			c.addExpression(ExpressionUtilities.getExpression(getAccountExpression(), ident));
			StringWriter out = new StringWriter();
			SqlRenderer renderer = new SqlRenderer(out);
			c.accept(renderer);
			return out.toString();
		}
		return null;
	}

	public boolean hasAccountCostCenters() throws ExpressionException {
		return (getAccountCostCenters() != null && getAccountCostCenters().size() > 0);
	}

	public Object getAccountCostCenterSQLExpression(String ident) throws ExpressionException {
		if (getAccountCostCenters() != null && getAccountCostCenters().size() > 0) {
			Criteria c = new Criteria();
			Expression orExp = null;
			for (String costCenter : getAccountCostCenters() ) {
				Expression exp = null;
				if (AccountUtil.NO_COST_CENTER_ACCOUNT.equals(costCenter)) {
					exp = ExpressionUtilities.getNullExpression(ident);
				} else {
					exp = ExpressionUtilities.getEqualExpression(ident,costCenter);
				}
				if (orExp == null) {
					orExp = exp;
				} else {
					orExp = ExpressionUtilities.getOrExpression(orExp, exp);
				}
			}
			if (orExp != null) {
				c.addExpression(orExp);	
			}
			StringWriter out = new StringWriter();
			SqlRenderer renderer = new SqlRenderer(out);
			c.accept(renderer);
			return out.toString();
		}
		return null;
	}

	public SummaryProviderParameters getPreviousPeriodParameters() {
		SummaryProviderParameters  previous = null;
		try {
			previous = this.clone();
			if (previous.getFromDate() != null) {
				Calendar c = Calendar.getInstance();
				c.setTime(previous.getFromDate());
				c.add(Calendar.YEAR, -1);
				previous.setFromDate(c.getTime());
			}
			if (previous.getToDate() != null) {
				Calendar c = Calendar.getInstance();
				c.setTime(previous.getToDate());
				c.add(Calendar.YEAR, -1);
				previous.setToDate(c.getTime());
			}
			if (previous.getPeriod() != null) {
				IManagerBean periodBean = BeanManager.getManagerBean(Period.class);
				Criteria criteria = new Criteria();
				String deadlineAlias = periodBean.getFieldName(IEntityAlias.PERIOD_INITIATION_DATE);
				criteria.addLessThanExpression(deadlineAlias, previous.getPeriod().getInitiationDate());
				criteria.addOrder(deadlineAlias, false);
				List<ITransferObject> list = periodBean.getList(criteria);
				if (list.size() > 0 ) {
					previous.setPeriod( (Period) list.get(0));
				} else {
					previous = null;
				}
			}
			return previous;
		} catch (CloneNotSupportedException e) {
			previous = null;
		} catch (ManagerBeanException e) {
			e.printStackTrace();
			previous = null;
		}
		return previous;
	}

}
