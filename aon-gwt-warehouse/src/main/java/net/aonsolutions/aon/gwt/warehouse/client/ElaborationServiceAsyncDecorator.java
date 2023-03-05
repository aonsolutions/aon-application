package net.aonsolutions.aon.gwt.warehouse.client;

import java.util.List;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AsyncCallbackWrapper;
import com.esferalia.aon.occam.api.model.Elaboration;
import com.esferalia.aon.occam.api.model.Occam;
import com.google.gwt.user.client.rpc.AsyncCallback;

import net.aonsolutions.aon.gwt.warehouse.shared.ElaborationParams;

public class ElaborationServiceAsyncDecorator implements ElaborationServiceAsync {

	private ElaborationServiceAsync esa;

	public ElaborationServiceAsyncDecorator(ElaborationServiceAsync elaborationServiceAsync) {
		this.esa = elaborationServiceAsync;
	}
	
	@Override
	public void getElaborations(Occam occam, ElaborationParams params, AsyncCallback<List<Elaboration>> callback) {
		AON.start();
		esa.getElaborations(occam, params, new AsyncCallbackWrapper<>(callback));
	}
 	
	
}
