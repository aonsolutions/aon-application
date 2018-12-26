package com.esferalia.aon.gwt.document.client.nuevo;

import com.esferalia.aon.occam.api.model.Domain;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface IDocumentalAsync {

	void getAttachLink(Domain domain, Integer id, AsyncCallback<String> callback);
	
}
