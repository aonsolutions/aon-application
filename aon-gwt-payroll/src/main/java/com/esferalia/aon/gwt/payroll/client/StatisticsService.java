package com.esferalia.aon.gwt.payroll.client;

import com.esferalia.aon.gwt.payroll.shared.ITData;
import com.esferalia.aon.gwt.payroll.shared.Statistics;

public interface StatisticsService {

	public abstract Statistics getWorkplaceStats(int workplaceId)
			throws IllegalArgumentException;

	public abstract Statistics getEnterpriseStats(int enterpriseId)
			throws IllegalArgumentException;
	
	public abstract ITData getEnterpriseITData(int enterpriseId)
			throws IllegalArgumentException;
	
	public abstract ITData getWorkplaceITData(int workplaceId)
			throws IllegalArgumentException;
}