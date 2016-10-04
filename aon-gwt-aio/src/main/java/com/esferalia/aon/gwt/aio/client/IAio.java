package com.esferalia.aon.gwt.aio.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("gwt_aio")
public interface IAio extends RemoteService{

	public void selectedMenu();
}
