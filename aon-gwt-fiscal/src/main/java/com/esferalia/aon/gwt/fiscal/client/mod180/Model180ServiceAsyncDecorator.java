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
	public void deleteMod180(String domainName, int domainId, Mod180 mod180,
			AsyncCallback<Void> callback) {
		AON.start();
		fsa.deleteMod180(domainName, domainId, mod180,
				new AsyncCallbackWrapper<Void>(callback));
	}

	@Override
	public void saveMod180(String domainName, int domainId, Mod180 mod180,
			AsyncCallback<Mod180> callback) {
		AON.start();
		fsa.saveMod180(domainName, domainId, mod180,
				new AsyncCallbackWrapper<Mod180>(callback));
	}

	@Override
	public void getMod180s(String domainName, int domainId,
			AsyncCallback<LinkedList<Mod180>> callback) {
		AON.start();
		fsa.getMod180s(domainName, domainId,
				new AsyncCallbackWrapper<LinkedList<Mod180>>(callback));
	}

	@Override
	public void initializeMod180(String domainName, Integer domain,
			Integer year, AsyncCallback<Mod180> callback) {
		AON.start();
		fsa.initializeMod180(domainName, domain, year,
				new AsyncCallbackWrapper<Mod180>(callback));
	}

	@Override
	public void getMod180(String domainName, int domainId, Integer id,
			AsyncCallback<Mod180> callback) {
		AON.start();
		fsa.getMod180(domainName, domainId, id,
				new AsyncCallbackWrapper<Mod180>(callback));
	}

	@Override
	public void getMod180Detail(String domainName, int domainId, Integer id,
			AsyncCallback<Mod180Detail> callback) {
		AON.start();
		fsa.getMod180Detail(domainName, domainId, id,
				new AsyncCallbackWrapper<Mod180Detail>(callback));
	}

	@Override
	public void saveCommentsMod180(String domainName, Mod180 mod180,
			AsyncCallback<Mod180> callback) {
		AON.start();
		fsa.saveCommentsMod180(domainName, mod180, new AsyncCallbackWrapper<Mod180>(
				callback));
	}

	@Override
	public void changeStatusMod180(String domainName, Mod180 mod180, FiscalStatus newStatus,
			AsyncCallback<Mod180> callback) {
		AON.start();
		fsa.changeStatusMod180(domainName, mod180, newStatus, 
				new AsyncCallbackWrapper<Mod180>(callback));
	}

}
