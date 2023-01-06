package net.aonsolutions.aon.gwt.warehouse.client;

import java.util.List;

import com.esferalia.aon.occam.api.model.Elaboration;
import com.esferalia.aon.occam.api.model.Occam;
import com.google.gwt.user.client.rpc.AsyncCallback;

import net.aonsolutions.aon.gwt.warehouse.shared.ElaborationParams;

public interface ElaborationServiceAsync {

	void getElaborations(Occam occam, ElaborationParams params, AsyncCallback<List<Elaboration>> callback);
}
