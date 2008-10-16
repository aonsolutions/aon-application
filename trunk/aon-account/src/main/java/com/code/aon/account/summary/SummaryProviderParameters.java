package com.code.aon.account.summary;

import java.util.Date;

import com.code.aon.account.Period;

public class SummaryProviderParameters {

	/**
	 * Literal AON-QL válido ej: 430*|400*
	 */
	private String accountExpression;

	/**
	 * TRUE si se desea que se devuelva los acumulados de los nivees inferiores,
	 * como elementos de la colección, estos valores no deben tenerse en cuenta
	 * para los acumulados totales.
	 */
	private boolean lowerLevelVisible = false;
	
	/**
	 * Si se desea que se devuelvan las flas con acumulados a cero.
	 */
	private boolean zeroSumVisible = false;
	
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

	public SummaryProviderParameters() {
		setAccountExpression(null);
		setLowerLevelVisible(false);
		setZeroSumVisible(false);
		setAccountLevel(4);
		setFromDate(null);
		setToDate(null);
		setDate( new Date() );
		setPeriod(null);
	}

	public String getAccountExpression() {
		return accountExpression;
	}

	public void setAccountExpression(String accountExpression) {
		this.accountExpression = accountExpression;
	}

	public boolean isLowerLevelVisible() {
		return lowerLevelVisible;
	}

	public void setLowerLevelVisible(boolean lowerLevelVisible) {
		this.lowerLevelVisible = lowerLevelVisible;
	}

	public boolean isZeroSumVisible() {
		return zeroSumVisible;
	}

	public void setZeroSumVisible(boolean zeroSumVisible) {
		this.zeroSumVisible = zeroSumVisible;
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
}
