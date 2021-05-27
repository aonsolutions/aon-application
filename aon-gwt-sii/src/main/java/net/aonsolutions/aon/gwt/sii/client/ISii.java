package net.aonsolutions.aon.gwt.sii.client;

import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("ms/gwt_sii")
public interface ISii extends RemoteService{

	public Administration getAdministration(Domain domain, String login);

}
