package com.esferalia.aon.gwt.fiscal.client.mod193;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AsyncCallbackWrapper;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod193;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class Model193ServiceAsyncDecorator implements Model193ServiceAsync {

	private Model193ServiceAsync fsa;

	public Model193ServiceAsyncDecorator(Model193ServiceAsync mod193ServiceAsync) {
		this.fsa = mod193ServiceAsync;
	}

	@Override
	public void getMod193s(String domainName, int domain,
			AsyncCallback<LinkedList<Mod193>> callback) {
		AON.start();
		fsa.getMod193s(domainName, domain,
				new AsyncCallbackWrapper<LinkedList<Mod193>>(callback));
	}

	@Override
	public void getMod193(String domainName, int domain, Integer id,
			AsyncCallback<Mod193> callback) {
		AON.start();
		fsa.getMod193(domainName, domain, id, new AsyncCallbackWrapper<Mod193>(
				callback));
	}

	@Override
	public void delete(String domainName, int domain, Mod193 mod193,
			AsyncCallback<Void> callback) {
		AON.start();
		fsa.delete(domainName, domain, mod193,new AsyncCallbackWrapper<Void>(callback));
	}

	@Override
	public void save(String domainName, int domain, Mod193 mod193,
			AsyncCallback<Mod193> callback) {
		AON.start();
		fsa.save(domainName, domain, mod193,new AsyncCallbackWrapper<Mod193>(callback));
	}

	@Override
	public void initialize(String domainName, Integer domain,
			Integer year, AsyncCallback<Mod193> callback) {
		AON.start();
		fsa.initialize(domainName, domain, year,				new AsyncCallbackWrapper<Mod193>(callback));
	}

	@Override
	public void saveComments(String domainName, Mod193 mod193,AsyncCallback<Mod193> callback) {
		AON.start();
		fsa.saveComments(domainName, mod193, new AsyncCallbackWrapper<Mod193>(callback));
	}

	@Override
	public void changeStatus(String domainName, Mod193 mod193, FiscalStatus newStatus,AsyncCallback<Mod193> callback) {
		AON.start();
		fsa.changeStatus(domainName, mod193, newStatus, new AsyncCallbackWrapper<Mod193>(callback));
	}

	@Override
	public void duplicateNextYear(String domainName, Integer domain, Integer id, AsyncCallback<Mod193> callback) {
		AON.start();
		fsa.duplicateNextYear(domainName, domain, id, new AsyncCallbackWrapper<Mod193>(callback));
	}
}
