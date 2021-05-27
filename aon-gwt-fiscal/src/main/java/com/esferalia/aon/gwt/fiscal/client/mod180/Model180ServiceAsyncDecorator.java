package com.esferalia.aon.gwt.fiscal.client.mod180;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AsyncCallbackWrapper;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod180;
import com.esferalia.aon.occam.api.model.fiscal.Mod180Detail;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class Model180ServiceAsyncDecorator implements Model180ServiceAsync {

	private Model180ServiceAsync fsa;

	public Model180ServiceAsyncDecorator(Model180ServiceAsync fsa) {
		this.fsa = fsa;
	}

	@Override
	public void deleteMod180(String domainName, String user, int domainId, Mod180 mod180, AsyncCallback<Void> callback) {
		AON.start();
		fsa.deleteMod180(domainName, user, domainId, mod180, new AsyncCallbackWrapper<Void>(callback));
	}

	@Override
	public void saveMod180(String domainName, String user, int domainId, Mod180 mod180, AsyncCallback<Mod180> callback) {
		AON.start();
		fsa.saveMod180(domainName, user, domainId, mod180, new AsyncCallbackWrapper<Mod180>(callback));
	}

	@Override
	public void getMod180s(String domainName, String user, int domainId, AsyncCallback<LinkedList<Mod180>> callback) {
		AON.start();
		fsa.getMod180s(domainName, user, domainId, new AsyncCallbackWrapper<LinkedList<Mod180>>(callback));
	}

	@Override
	public void initializeMod180(String domainName, String user, Integer domain, Integer year, AsyncCallback<Mod180> callback) {
		AON.start();
		fsa.initializeMod180(domainName, user, domain, year, new AsyncCallbackWrapper<Mod180>(callback));
	}

	@Override
	public void getMod180(String domainName, String user, int domainId, Integer id, AsyncCallback<Mod180> callback) {
		AON.start();
		fsa.getMod180(domainName, user, domainId, id, new AsyncCallbackWrapper<Mod180>(callback));
	}

	@Override
	public void getMod180Detail(String domainName, String user, int domainId, Integer id, AsyncCallback<Mod180Detail> callback) {
		AON.start();
		fsa.getMod180Detail(domainName, user, domainId, id, new AsyncCallbackWrapper<Mod180Detail>(callback));
	}

	@Override
	public void saveCommentsMod180(String domainName, String user, Mod180 mod180, AsyncCallback<Mod180> callback) {
		AON.start();
		fsa.saveCommentsMod180(domainName, user, mod180, new AsyncCallbackWrapper<Mod180>(callback));
	}

	@Override
	public void changeStatusMod180(String domainName, String user, Mod180 mod180, FiscalStatus newStatus, AsyncCallback<Mod180> callback) {
		AON.start();
		fsa.changeStatusMod180(domainName, user, mod180, newStatus, new AsyncCallbackWrapper<Mod180>(callback));
	}

	@Override
	public void duplicateNextYear(String domainName, String user, Integer domain, Integer id, AsyncCallback<Mod180> callback) {
		AON.start();
		fsa.duplicateNextYear(domainName, user, domain, id, new AsyncCallbackWrapper<Mod180>(callback));
	}

}
