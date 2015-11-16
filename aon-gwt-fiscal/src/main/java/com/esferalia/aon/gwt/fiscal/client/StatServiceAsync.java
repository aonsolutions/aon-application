
package com.esferalia.aon.gwt.fiscal.client;

import com.esferalia.aon.occam.api.model.stat.StatData;
import com.esferalia.aon.occam.api.model.stat.StatParams;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface StatServiceAsync {
	
	void createStatParams(String domainName, int domain,
			AsyncCallback<StatParams> callback);

	void getYearInvoiceTypeData(StatParams params,
			AsyncCallback<StatData<Integer,InvoiceType,Double>> callback);

	void getMonthInvoiceTypeData(StatParams params,
			AsyncCallback<StatData<Integer,InvoiceType,Double>> callback);


}
