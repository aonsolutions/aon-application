package com.esferalia.aon.gwt.payroll.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("agrarian_afi")
public interface AgrarianAFIService extends RemoteService {

	Integer getParentDomainId();

	Integer getDomainId();

	String getDomainName();

}
