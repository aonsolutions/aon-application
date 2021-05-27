package com.esferalia.aon.gwt.fiscal.client.mod390HF;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AsyncCallbackWrapper;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod390HF;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod390Key;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class Mod390HFServiceAsyncDecorator implements Mod390HFServiceAsync {

	private Mod390HFServiceAsync fsa;

	public Mod390HFServiceAsyncDecorator(Mod390HFServiceAsync mod190ServiceAsync) {
		this.fsa = mod190ServiceAsync;
	}

	// ---------------------------------------------------------------MODELO 303

	@Override
	public void getMod390HF(String domainName, int domain, String user, int id, AsyncCallback<Mod390HF> callback) {
		AON.start();
		fsa.getMod390HF(domainName, domain, user, id, new AsyncCallbackWrapper<Mod390HF>(callback));
	}

	@Override
	public void getMod390HFs(String domainName, int domain, String user, AsyncCallback<LinkedList<Mod390HF>> callback) {
		AON.start();
		fsa.getMod390HFs(domainName, domain, user, new AsyncCallbackWrapper<LinkedList<Mod390HF>>(callback));
	}

	@Override
	public void calculate(String domainName, String user, Mod390HF mod303, AsyncCallback<Mod390HF> callback) {
		AON.start();
		fsa.calculate(domainName, user, mod303, new AsyncCallbackWrapper<Mod390HF>(callback));
	}

	@Override
	public void save(String domainName, String user, Mod390HF mod303, AsyncCallback<Mod390HF> callback) {
		AON.start();
		fsa.save(domainName, user, mod303, new AsyncCallbackWrapper<Mod390HF>(callback));
	}

	@Override
	public void saveComments(String domainName, String user, Mod390HF mod303, AsyncCallback<Mod390HF> callback) {
		AON.start();
		fsa.saveComments(domainName, user, mod303, new AsyncCallbackWrapper<Mod390HF>(callback));
	}

	@Override
	public void initializeForFinish(String domainName, String user, Mod390HF mod303, AsyncCallback<Mod390HF> callback) {
		AON.start();
		fsa.initializeForFinish(domainName, user, mod303, new AsyncCallbackWrapper<Mod390HF>(callback));
	}

	@Override
	public void markAsFinished(String domainName, String user, Mod390HF mod303, AsyncCallback<Mod390HF> callback) {
		AON.start();
		fsa.markAsFinished(domainName, user, mod303, new AsyncCallbackWrapper<Mod390HF>(callback));
	}

	@Override
	public void markAsPending(String domainName, String user, Mod390HF mod303, AsyncCallback<Mod390HF> callback) {
		AON.start();
		fsa.markAsPending(domainName, user, mod303, new AsyncCallbackWrapper<Mod390HF>(callback));
	}

	@Override
	public void initialize(String domainName, int domain, String user, Mod390HF mod303, AsyncCallback<Mod390HF> callback) {
		AON.start();
		fsa.initialize(domainName, domain, user, mod303, new AsyncCallbackWrapper<Mod390HF>(callback));

	}

	@Override
	public void create(String domainName, int domain, String user, Mod390HF mod303, AsyncCallback<Mod390HF> callback) {
		AON.start();
		fsa.create(domainName, domain, user, mod303, new AsyncCallbackWrapper<Mod390HF>(callback));

	}

	@Override
	public void declarationChanged(String domainName, int domain, String user, Mod390HF mod303, AsyncCallback<Mod390HF> callback) {
		AON.start();
		fsa.declarationChanged(domainName, domain, user, mod303, new AsyncCallbackWrapper<Mod390HF>(callback));
	}

	@Override
	public void delete(String domainName, String user, Mod390HF mod303, AsyncCallback<Void> callback) {
		AON.start();
		fsa.delete(domainName, user, mod303, new AsyncCallbackWrapper<Void>(callback));
	}

	@Override
	public void getInfo(String domainName, int domain, String user, Mod390HF mod303, IModelScript<Mod390Key> script, FiscalModelKeyInfo infoKey, AsyncCallback<String> callback) {
		AON.start();
		fsa.getInfo(domainName, domain, user, mod303, script, infoKey, new AsyncCallbackWrapper<String>(callback));

	}

	@Override
	public void markAsSent(String domainName, String user, Mod390HF mod303, AsyncCallback<Mod390HF> callback) {
		AON.start();
		fsa.markAsSent(domainName, user, mod303, new AsyncCallbackWrapper<Mod390HF>(callback));
	}
}
