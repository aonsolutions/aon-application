package com.esferalia.aon.gwt.viewer.client;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.MailAccount;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface IViewerAsync {
	void getAsHTML(Attach attach, int zoom, AsyncCallback<String> callback);
		
	void share(String email, Attach attach, AsyncCallback<Void> callback);

	void sendGmail(String to, String issue, String message, Attach attach, AsyncCallback<Void> callback);
	
	void sendEmail(Domain domain, String from, String to, String issue, String message, Attach attach, AsyncCallback<Void> callback);

	void getMailAccountList(Domain domain, AsyncCallback<LinkedList<MailAccount>> callback);

	void print(String head, String msg, AsyncCallback<Void> callback);
}
