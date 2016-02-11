package com.esferalia.aon.gwt.viewer.client;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.MailAccount;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("Viewer")
public interface IViewer extends RemoteService {
	public String getAsHTML(Attach attach, int zoom);
	
	public void share(String email, Attach attach);
	
	public void sendGmail(String to, String issue, String message, Attach attach);
	
	public void sendEmail(Domain domain, String from, String to, String issue, String message, Attach attach);
	
	public LinkedList<MailAccount> getMailAccountList(Domain domain);
	
	public void print(String msg);
}
