package com.esferalia.aon.gwt.payroll.client;

import java.util.LinkedHashMap;
import java.util.Map;

import com.esferalia.aon.gwt.payroll.shared.ITData;
import com.esferalia.aon.gwt.payroll.shared.ITDataPerson;
import com.esferalia.aon.gwt.payroll.shared.Statistics;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface StatisticsServiceAsync {

	public abstract void getWorkplaceStats(int workplaceId,
			AsyncCallback<Statistics> callback) throws IllegalArgumentException;

	public abstract void getEnterpriseStats(int enterpriseId,
			AsyncCallback<Statistics> callback) throws IllegalArgumentException;

	public abstract void getWorkplaceITData(int workplaceId,
			AsyncCallback<ITData> callback) throws IllegalArgumentException;

	public abstract void getEnterpriseITData(int enterpriseId,
			AsyncCallback<ITData> callback) throws IllegalArgumentException;
	
	public abstract void saveITDataPerson(Map<Integer, LinkedHashMap<Integer, ITDataPerson>> inserts, 
			Map<Integer, LinkedHashMap<Integer, ITDataPerson>> deletes, 
			Map<Integer, LinkedHashMap<Integer, ITDataPerson>> updates, 
			AsyncCallback<ITData> callback) throws IllegalArgumentException;	
}