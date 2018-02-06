package net.aonsolutions.aon.gwt.commercial.client;

import com.esferalia.aon.gwt.common.shared.AonData;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("gwt_commercial")
public interface ICommercial extends RemoteService{
	
	public AonData getAonData(String domainName, Integer domainId);

}
