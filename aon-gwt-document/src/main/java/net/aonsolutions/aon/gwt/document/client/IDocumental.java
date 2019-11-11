package net.aonsolutions.aon.gwt.document.client;

import com.esferalia.aon.occam.api.model.Domain;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("ms/gwt_documental")
public interface IDocumental extends RemoteService{
	
	public String getAttachLink(Domain domain, String login, Integer id);

}
