package com.esferalia.aon.gwt.issues.client;

import com.esferalia.aon.gwt.issues.shared.AonData;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("gwt_issue")
public interface IIssues extends RemoteService{

	public String getLoggedUser();
	
	public AonData getAonData(String domainName, Integer domainId);

	public void OLDTONEW(String domainName, Integer domainId, String login);
}
