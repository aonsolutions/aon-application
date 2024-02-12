package com.esferalia.aon.gwt.fiscal.client.aeat;

import com.esferalia.aon.occam.api.model.ddff.AeatFiscalData;
import com.esferalia.aon.occam.api.model.fiscal.aeat.AEATParams;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface AeatServiceAsync {

	void getAeatFiscalData(AEATParams params, AsyncCallback<AeatFiscalData> callback);
	
}
