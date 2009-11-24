package com.code.aon.accounting.summary;

import java.util.Date;

import com.code.aon.accounting.Period;
import com.code.aon.common.enumeration.SecurityLevel;

public class SummaryProviderParameters {

	/**
	 * Literal AON-QL válido ej: 430*|400*
	 */
	private String accountExpression;

	private String accountDescription;

	private String accountAlias;

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
	 * 		1 --> LENGTH(account.id) = 1
	 *  	2 --> LENGTH(account.id) = 2
	 *  	3 --> LENGTH(account.id) = 3
	 *  	4 --> LENGTH(account.id) = 4 || LENGTH(account.id) = 5
	 *  	5 --> LENGTH(account.id) > 5
	 *  
	 */
	private int accountLevel = 4;
	
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

	private boolean budgeted;

	private boolean monthlyGrouping;
	

	public SummaryProviderParameters() {
		setAccountExpression(null);
		setAccountDescription(null);
		setAccountAlias(null);
		setLowerLevelVisible(false);
		setNoTouchedAccountVisible(false);
		setAccountLevel(4);
		setFromDate(null);
		setToDate(null);
		setDate( new Date() );
		setPeriod(null);
		setSecurityLevel(null);
		setRowsPerPage(20);
		setBudgeted(false);
		setMonthlyGrouping(false);
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

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
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

	public boolean isBudgeted() {
		return budgeted;
	}

	public void setBudgeted(boolean budgeted) {
		this.budgeted = budgeted;
	}

	public boolean isMonthlyGrouping() {
		return monthlyGrouping;
	}

	public void setMonthlyGrouping(boolean monthlyGrouping) {
		this.monthlyGrouping = monthlyGrouping;
	}
}
