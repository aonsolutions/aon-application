package net.aonsolutions.aon.gwt.sii.client;

import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.finance.SiiConfiguration;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ISiiAsync {
	
	void getSiiConfiguration(Domain domain, String login, AsyncCallback<SiiConfiguration> callback);

}
