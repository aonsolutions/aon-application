package net.aonsolutions.aon.gwt.aio.client;

import com.esferalia.aon.gwt.common.shared.AonData;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("ms/gwt_aio")
public interface IAio extends RemoteService{
	
	public AonData getAonData(String domainName, Integer domainId, String login);
	public AonData getAonData(String token, String domainId);

}
