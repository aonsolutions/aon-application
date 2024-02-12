package com.esferalia.aon.gwt.fiscal.client.aeat;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AsyncCallbackWrapper;
import com.esferalia.aon.occam.api.model.ddff.AeatFiscalData;
import com.esferalia.aon.occam.api.model.fiscal.aeat.AEATParams;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class AeatServiceAsyncDecorator implements AeatServiceAsync {

	private AeatServiceAsync fsa;

	public AeatServiceAsyncDecorator(AeatServiceAsync rawServiceAsync) {
		this.fsa = rawServiceAsync;
	}
	
	@Override
	public void getAeatFiscalData(AEATParams params, AsyncCallback<AeatFiscalData> callback) {
		AON.start();
		fsa.getAeatFiscalData(params, new AsyncCallbackWrapper<>(callback));
	}
	
}
