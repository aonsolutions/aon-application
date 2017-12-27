package com.esferalia.aon.gwt.fiscal.client.mod390HF;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod390HF;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod390Key;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface Mod390HFServiceAsync {
	// ---------------------------------------------------------------MODELO 303
	void getMod390HF(String domainName, int domain, int id,AsyncCallback<Mod390HF> callback);
	void getMod390HFs(String domainName, int domain,AsyncCallback<LinkedList<Mod390HF>> callback);
	void calculate(String domainName, Mod390HF mod303,AsyncCallback<Mod390HF> callback);
	void delete(String currentDomainName, Mod390HF mod303,AsyncCallback<Void> callback);
	void save(String domainName, Mod390HF mod303,AsyncCallback<Mod390HF> asyncCallback);
	void saveComments(String domainName, Mod390HF mod303,AsyncCallback<Mod390HF> asyncCallback);
	void initializeForFinish(String domainName, Mod390HF mod303,AsyncCallback<Mod390HF> asyncCallback);
	void markAsFinished(String domainName, Mod390HF mod303,AsyncCallback<Mod390HF> asyncCallback);
	void markAsPending(String domainName, Mod390HF mod303,AsyncCallback<Mod390HF> asyncCallback);
	void initialize(String domainName, int domain, Mod390HF mod303,AsyncCallback<Mod390HF> asyncCallback);
	void create(String domainName, int domain, Mod390HF mod303, AsyncCallback<Mod390HF> callback);
	void getInfo(String domainName, int domain, Mod390HF mod303, IModelScript<Mod390Key> script, FiscalModelKeyInfo infoKey,AsyncCallback<String> callback);
	void declarationChanged(String domainName, int domain, Mod390HF mod303, AsyncCallback<Mod390HF> callback);
	void markAsSent(String currentDomainName, Mod390HF mod303, AsyncCallback<Mod390HF> asyncCallback);

}
