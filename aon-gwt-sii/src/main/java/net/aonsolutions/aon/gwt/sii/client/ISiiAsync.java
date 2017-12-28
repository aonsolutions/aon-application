package net.aonsolutions.aon.gwt.sii.client;

import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ISiiAsync {
	
	void getAdministration(Domain domain, String login, AsyncCallback<Administration> callback);

}
