package net.aonsolutions.aon.gwt.sii.client;

import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.finance.SiiConfiguration;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("ms/gwt_sii")
public interface ISii extends RemoteService{

	public SiiConfiguration getSiiConfiguration(Domain domain, String login);

}
