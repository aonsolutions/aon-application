package com.code.aon.finance;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import com.code.aon.AonVersion;
import com.esferalia.aon.entity.master.CashFlowForecastDB;

@Entity
@Table(name="cashflow_forecast")
public class CashFlowForecast extends CashFlowForecastDB {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	@Transient
	public boolean isUndated() {
		return (!isJanuary() && !isFebruary() && !isMarch() && !isApril() 
			&& !isMay() && !isJune() && !isJuly() && !isAugust() 
			&& !isSeptember() && !isOctober() && !isNovember() && !isDecember());
	}

	@Transient
	public boolean[] getMonths() {
		return new boolean[]{isJanuary(),isFebruary(),isMarch(),isApril(),isMay(),isJune(),
							 isJuly(),isAugust(),isSeptember(),isOctober(),isNovember(),isDecember()};
	}
	@Transient
	public void setMonths(boolean[] months) {
		setJanuary(months[0]);
		setFebruary(months[1]);
		setMarch(months[2]);
		setApril(months[3]);
		setMay(months[4]);
		setJune(months[5]);
		setJuly(months[6]);
		setAugust(months[7]);
		setSeptember(months[8]);
		setOctober(months[9]);
		setNovember(months[10]);
		setDecember(months[11]);
	}

	@Transient
	public void initializeMonths() {
		setJanuary(false);
		setFebruary(false);
		setMarch(false);
		setApril(false);
		setMay(false);
		setJune(false);
		setJuly(false);
		setAugust(false);
		setSeptember(false);
		setOctober(false);
		setNovember(false);
		setDecember(false);
	}
}
