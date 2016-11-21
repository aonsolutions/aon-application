package com.esferalia.aon.gwt.aio.client;

import com.esferalia.aon.gwt.common.shared.AonData;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("gwt_aio")
public interface IAio extends RemoteService{

	public void selectedMenu();
	
	public AonData getAonData(String domainName, Integer domainId);

}
