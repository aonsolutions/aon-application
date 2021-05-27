package com.esferalia.aon.gwt.fiscal.client.matrix;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AsyncCallbackWrapper;
import com.esferalia.aon.occam.api.model.fiscal.FiscalMatrixParams;
import com.esferalia.aon.occam.api.model.fiscal.IFiscalModel;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class FiscalModelServiceAsyncDecorator implements FiscalModelServiceAsync {

	private FiscalModelServiceAsync fsa;

	public FiscalModelServiceAsyncDecorator(FiscalModelServiceAsync mod190ServiceAsync) {
		this.fsa = mod190ServiceAsync;
	}

	@Override
	public void getFiscalPanel(String domainName,String user, int domain, FiscalMatrixParams params, AsyncCallback<LinkedList<IFiscalModel>> callback) {
		AON.start();
		fsa.getFiscalPanel(domainName,user, domain, params, new AsyncCallbackWrapper<LinkedList<IFiscalModel>>(callback));
	}


}
