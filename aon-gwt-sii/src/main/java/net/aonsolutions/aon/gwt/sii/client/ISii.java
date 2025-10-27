package net.aonsolutions.aon.gwt.sii.client;

import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationConfiguration;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("ms/gwt_sii")
public interface ISii extends RemoteService{

	public InvoiceCommunicationConfiguration getConfiguration(Occam occam);

}
