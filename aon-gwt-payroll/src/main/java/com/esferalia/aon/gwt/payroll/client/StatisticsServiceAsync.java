package com.esferalia.aon.gwt.payroll.client;

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

	public abstract void saveUpdateITDataPerson(Map<Integer, ITDataPerson> map,
			AsyncCallback<Void> callback) throws IllegalArgumentException;
	
	public abstract void saveRemoveITDataPerson(Map<Integer, ITDataPerson> map,
			AsyncCallback<Void> callback) throws IllegalArgumentException;
	
	public abstract void saveInsertITDataPerson(Map<Integer, ITDataPerson> map,
			AsyncCallback<Void> callback) throws IllegalArgumentException;
}