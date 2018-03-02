package com.esferalia.aon.gwt.fiscal.client.matrix;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.fiscal.FiscalMatrixParams;
import com.esferalia.aon.occam.api.model.fiscal.IFiscalModel;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface FiscalModelServiceAsync {

	void getFiscalPanel(String domainName,String user,int domain,FiscalMatrixParams params, AsyncCallback<LinkedList<IFiscalModel>> asyncCallback);

}
