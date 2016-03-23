package com.esferalia.aon.gwt.stat.client;

import com.esferalia.aon.occam.api.model.stat.StatData;
import com.esferalia.aon.occam.api.model.stat.StatParams;
import com.esferalia.aon.watson.error.AonCoreException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("Stats")
public interface StatService extends RemoteService {
	
	StatParams createStatParams(String domainName, int domain) throws AonCoreException;
	StatData<Integer, String, Double> getYearInvoiceTypeData(String domainName,Integer domainId, StatParams params);
	StatData<Integer, String, Double> getMonthInvoiceTypeData(String domainName,Integer domainId, StatParams params);
	StatData<Integer, String, Double> getDayInvoiceTypeData(String domainName,Integer domainId, StatParams params);
	
}
