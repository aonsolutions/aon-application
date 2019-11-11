package net.aonsolutions.aon.gwt.document.client;

import com.esferalia.aon.occam.api.model.Domain;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface IDocumentalAsync {

	void getAttachLink(Domain domain, String login, Integer id, AsyncCallback<String> callback);
	
}
