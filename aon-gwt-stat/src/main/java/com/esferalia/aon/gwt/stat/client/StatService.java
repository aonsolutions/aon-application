package com.esferalia.aon.gwt.stat.client;

import com.esferalia.aon.occam.api.model.stat.StatData;
import com.esferalia.aon.occam.api.model.stat.StatParams;
import com.esferalia.aon.watson.error.AonCoreException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("Stats")
public interface StatService extends RemoteService {
	
	StatParams createStatParams(String domainName, String user, int domain) throws AonCoreException;
	StatData<String, String, Double> getStatData(String domainName, String user,Integer domainId, StatParams params);
	String getInvoicesReport(String domainName, String user, int domain, StatParams params) throws AonCoreException;
	
}
