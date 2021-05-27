package com.esferalia.aon.gwt.fiscal.client.mod190;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AsyncCallbackWrapper;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod190;
import com.esferalia.aon.occam.api.model.fiscal.Mod190Detail;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class Model190ServiceAsyncDecorator implements Model190ServiceAsync {

	private Model190ServiceAsync fsa;

	public Model190ServiceAsyncDecorator(Model190ServiceAsync mod190ServiceAsync) {
		this.fsa = mod190ServiceAsync;
	}

	@Override
	public void getMod190s(String domainName, String user, int domain, AsyncCallback<LinkedList<Mod190>> callback) {
		AON.start();
		fsa.getMod190s(domainName, user, domain, new AsyncCallbackWrapper<LinkedList<Mod190>>(callback));
	}

	@Override
	public void getMod190(String domainName, String user, int domain, Integer id, AsyncCallback<Mod190> callback) {
		AON.start();
		fsa.getMod190(domainName, user, domain, id, new AsyncCallbackWrapper<Mod190>(callback));
	}

	@Override
	public void delete(String domainName, String user, int domain, Mod190 mod190, AsyncCallback<Void> callback) {
		AON.start();
		fsa.delete(domainName, user, domain, mod190,new AsyncCallbackWrapper<Void>(callback));
	}

	@Override
	public void save(String domainName, String user, int domain, Mod190 mod190,AsyncCallback<Mod190> callback) {
		AON.start();
		fsa.save(domainName, user, domain, mod190,new AsyncCallbackWrapper<Mod190>(callback));
	}

	@Override
	public void initialize(String domainName, String user, Integer domain,Integer year, AsyncCallback<Mod190> callback) {
		AON.start();
		fsa.initialize(domainName, user, domain, year,new AsyncCallbackWrapper<Mod190>(callback));
	}

	@Override
	public void getDetail(String domainName, String user, int domain, Integer id, AsyncCallback<Mod190Detail> callback) {
		AON.start();
		fsa.getDetail(domainName, user, domain, id,new AsyncCallbackWrapper<Mod190Detail>(callback));
	}
	
	@Override
	public void saveComments(String domainName, String user, Mod190 mod190,AsyncCallback<Mod190> callback) {
		AON.start();
		fsa.saveComments(domainName, user, mod190, new AsyncCallbackWrapper<Mod190>(callback));
	}

	@Override
	public void changeStatus(String domainName, String user, Mod190 mod190, FiscalStatus newStatus,AsyncCallback<Mod190> callback) {
		AON.start();
		fsa.changeStatus(domainName, user, mod190, newStatus, new AsyncCallbackWrapper<Mod190>(callback));
	}

	@Override
	public void duplicateNextYear(String domainName, String user, Integer domain, Integer id, AsyncCallback<Mod190> callback) {
		AON.start();
		fsa.duplicateNextYear(domainName, user, domain, id, new AsyncCallbackWrapper<Mod190>(callback));
	}
	

}
