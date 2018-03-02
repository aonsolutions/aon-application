package com.esferalia.aon.gwt.fiscal.client.matrix;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.fiscal.FiscalMatrixParams;
import com.esferalia.aon.occam.api.model.fiscal.IFiscalModel;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("roms/FiscalModels")
public interface FiscalModelService extends RemoteService {

	LinkedList<IFiscalModel> getFiscalPanel(String domainName,String user,int domain, FiscalMatrixParams params);
	
}
