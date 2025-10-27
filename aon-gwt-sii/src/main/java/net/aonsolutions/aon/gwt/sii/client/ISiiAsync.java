package net.aonsolutions.aon.gwt.sii.client;

import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationConfiguration;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ISiiAsync {
	
	void getConfiguration(Occam occam, AsyncCallback<InvoiceCommunicationConfiguration> callback);

}
