/**
 * 
 */
package com.esferalia.aon.gwt.payroll.client;

import java.util.List;

import com.esferalia.aon.gwt.payroll.shared.Enterprise;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author rtrepiana
 * 
 */
public class EnterprisesServiceAsyncDecorator implements EnterprisesServiceAsync {

	private EnterprisesServiceAsync enterprisesServiceAsync;

	public EnterprisesServiceAsyncDecorator(
			EnterprisesServiceAsync enterprisesServiceAsync) {
		this.enterprisesServiceAsync = enterprisesServiceAsync;
	}
	
	@Override
	public void getEnterprises(int offset, int limit, AsyncCallback<List<Enterprise>> callback) {
		AON.start();
		enterprisesServiceAsync.getEnterprises(offset, limit, new AsyncCallbackWrapper<List<Enterprise>>(callback));
	}

}
