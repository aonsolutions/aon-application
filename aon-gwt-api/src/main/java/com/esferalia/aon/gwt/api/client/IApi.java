package com.esferalia.aon.gwt.api.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("gwt_api")
public interface IApi extends RemoteService{

	public String base(String str);
	

}
