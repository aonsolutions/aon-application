package com.esferalia.aon.gwt.fiscal.client;

import com.esferalia.aon.occam.api.model.stat.StatData;
import com.esferalia.aon.occam.api.model.stat.StatParams;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.watson.error.AonCoreException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("Stats")
public interface StatService extends RemoteService {
	
	StatParams createStatParams(String domainName, int domain) throws AonCoreException;
	// ---------------------------------- COMMON
	StatData<Integer,InvoiceType,Double> getYearInvoiceTypeData(StatParams params) throws AonCoreException;
	StatData<Integer,InvoiceType,Double> getMonthInvoiceTypeData(StatParams params) throws AonCoreException;

}
