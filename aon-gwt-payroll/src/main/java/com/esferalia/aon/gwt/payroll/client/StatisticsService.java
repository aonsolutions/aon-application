package com.esferalia.aon.gwt.payroll.client;

import java.util.LinkedHashMap;
import java.util.Map;

import com.esferalia.aon.gwt.payroll.shared.ITData;
import com.esferalia.aon.gwt.payroll.shared.ITDataPerson;
import com.esferalia.aon.gwt.payroll.shared.Statistics;

public interface StatisticsService {

	public abstract Statistics getWorkplaceStats(String domain,int workplaceId)
			throws IllegalArgumentException;

	public abstract Statistics getEnterpriseStats(String domain,int enterpriseId)
			throws IllegalArgumentException;
	
	public abstract ITData getEnterpriseITData(String domain,int enterpriseId)
			throws IllegalArgumentException;
	
	public abstract ITData getWorkplaceITData(String domain,int workplaceId)
			throws IllegalArgumentException;
	
	public abstract void saveITDataPerson(String domain, Map<Integer, LinkedHashMap<Integer, ITDataPerson>> inserts, 
			Map<Integer, LinkedHashMap<Integer, ITDataPerson>> deletes,
			Map<Integer, LinkedHashMap<Integer, ITDataPerson>> updates) 
			throws IllegalArgumentException;	
}