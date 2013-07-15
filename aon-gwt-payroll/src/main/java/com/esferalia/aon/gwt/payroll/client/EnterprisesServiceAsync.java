package com.esferalia.aon.gwt.payroll.client;

import java.util.List;

import com.esferalia.aon.gwt.payroll.shared.Enterprise;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>EnterprisesService</code>.
 */
public interface EnterprisesServiceAsync {
	void getEnterprises(int offset , int limit, AsyncCallback<List<Enterprise>> callback);
}
