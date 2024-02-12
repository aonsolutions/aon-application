package com.esferalia.aon.gwt.fiscal.client.aeat;

import com.esferalia.aon.occam.api.model.ddff.AeatFiscalData;
import com.esferalia.aon.occam.api.model.fiscal.aeat.AEATParams;
import com.esferalia.aon.watson.error.AonCoreException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("ms/AeatComCenter")
public interface AeatService extends RemoteService {

	AeatFiscalData getAeatFiscalData(AEATParams params) throws AonCoreException;
	
}
