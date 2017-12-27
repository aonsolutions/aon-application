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
	public void getMod390HF(String domainName, int domain, int id,
			AsyncCallback<Mod390HF> callback) {
		AON.start();
		fsa.getMod390HF(domainName, domain, id, new AsyncCallbackWrapper<Mod390HF>(
				callback));
	}

	@Override
	public void getMod390HFs(String domainName, int domain,
			AsyncCallback<LinkedList<Mod390HF>> callback) {
		AON.start();
		fsa.getMod390HFs(domainName, domain,
				new AsyncCallbackWrapper<LinkedList<Mod390HF>>(callback));
	}

	@Override
	public void calculate(String domainName, Mod390HF mod303,
			AsyncCallback<Mod390HF> callback) {
		AON.start();
		fsa.calculate(domainName, mod303,
				new AsyncCallbackWrapper<Mod390HF>(callback));
	}

	@Override
	public void save(String domainName, Mod390HF mod303,
			AsyncCallback<Mod390HF> callback) {
		AON.start();
		fsa.save(domainName, mod303, new AsyncCallbackWrapper<Mod390HF>(
				callback));
	}
	
	@Override
	public void saveComments(String domainName, Mod390HF mod303,
			AsyncCallback<Mod390HF> callback) {
		AON.start();
		fsa.saveComments(domainName, mod303, new AsyncCallbackWrapper<Mod390HF>(
				callback));
	}

	@Override
	public void initializeForFinish(String domainName, Mod390HF mod303,
			AsyncCallback<Mod390HF> callback) {
		AON.start();
		fsa.initializeForFinish(domainName, mod303, new AsyncCallbackWrapper<Mod390HF>(
				callback));
	}
	
	@Override
	public void markAsFinished(String domainName, Mod390HF mod303,
			AsyncCallback<Mod390HF> callback) {
		AON.start();
		fsa.markAsFinished(domainName, mod303, new AsyncCallbackWrapper<Mod390HF>(
				callback));
	}
	
	@Override
	public void markAsPending(String domainName, Mod390HF mod303,
			AsyncCallback<Mod390HF> callback) {
		AON.start();
		fsa.markAsPending(domainName, mod303, new AsyncCallbackWrapper<Mod390HF>(
				callback));
	}

	@Override
	public void initialize(String domainName, int currentDomain,
			Mod390HF mod303, AsyncCallback<Mod390HF> callback) {
		AON.start();
		fsa.initialize(domainName, currentDomain,mod303,new AsyncCallbackWrapper<Mod390HF>(callback));

	}

	@Override
	public void create(String domainName, int currentDomain,
			Mod390HF mod303, AsyncCallback<Mod390HF> callback) {
		AON.start();
		fsa.create(domainName, currentDomain, mod303,new AsyncCallbackWrapper<Mod390HF>(callback));

	}

	@Override
	public void declarationChanged(String domainName, int domain, Mod390HF mod303, AsyncCallback<Mod390HF> callback) {
		AON.start();
		fsa.declarationChanged(domainName, domain, mod303,new AsyncCallbackWrapper<Mod390HF>(callback));
	}

	@Override
	public void delete(String domainName, Mod390HF mod303,
			AsyncCallback<Void> callback) {
		AON.start();
		fsa.delete(domainName, mod303, new AsyncCallbackWrapper<Void>(
				callback));
	}
	@Override
	public void getInfo(String domainName, int domain, Mod390HF mod303, IModelScript<Mod390Key> script, FiscalModelKeyInfo infoKey,
			AsyncCallback<String> callback) {
		AON.start();
		fsa.getInfo(domainName, domain, mod303, script, infoKey,new AsyncCallbackWrapper<String>(callback));
		
	}

	@Override
	public void markAsSent(String domainName, Mod390HF mod303, AsyncCallback<Mod390HF> callback) {
		AON.start();
		fsa.markAsSent(domainName, mod303, new AsyncCallbackWrapper<Mod390HF>(callback));
	}
}
