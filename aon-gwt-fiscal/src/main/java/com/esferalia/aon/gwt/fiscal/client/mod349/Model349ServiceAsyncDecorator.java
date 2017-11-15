package com.esferalia.aon.gwt.fiscal.client.mod349;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AsyncCallbackWrapper;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod349;
import com.esferalia.aon.occam.api.model.fiscal.Mod349Detail;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class Model349ServiceAsyncDecorator implements Model349ServiceAsync {
	
	private Model349ServiceAsync fsa;

	public Model349ServiceAsyncDecorator(Model349ServiceAsync mod349ServiceAsync) {
		this.fsa = mod349ServiceAsync;
	}
	
	
	// ---------------------------------------------------------------MODELO 349
	@Override
	public void deleteMod349(String domainName, int domainId, Mod349 mod349,
			AsyncCallback<Void> callback) {
		AON.start();
		fsa.deleteMod349(domainName, domainId, mod349,
				new AsyncCallbackWrapper<Void>(callback));
	}

	@Override
	public void saveMod349(String domainName, int domainId, Mod349 mod349,
			AsyncCallback<Mod349> callback) {
		AON.start();
		fsa.saveMod349(domainName, domainId, mod349,
				new AsyncCallbackWrapper<Mod349>(callback));
	}

	@Override
	public void getMod349s(String domainName, int domainId,
			AsyncCallback<LinkedList<Mod349>> callback) {
		AON.start();
		fsa.getMod349s(domainName, domainId,
				new AsyncCallbackWrapper<LinkedList<Mod349>>(callback));
	}

	@Override
	public void initializeMod349(String domainName, Integer domain,
			AsyncCallback<Mod349> callback) {
		AON.start();
		fsa.initializeMod349(domainName, domain, new AsyncCallbackWrapper<Mod349>(callback));
	}

	@Override
	public void getMod349(String domainName, int domainId, Integer id,
			AsyncCallback<Mod349> callback) {
		AON.start();
		fsa.getMod349(domainName, domainId, id,
				new AsyncCallbackWrapper<Mod349>(callback));
	}

	@Override
	public void getMod349Detail(String domainName, int domainId, Mod349 mod349,
			AsyncCallback<Mod349Detail> callback) {
		AON.start();
		fsa.getMod349Detail(domainName, domainId, mod349,new AsyncCallbackWrapper<Mod349Detail>(callback));
	}
	
	@Override
	public void saveCommentsMod349(String domainName, Mod349 mod349,
			AsyncCallback<Mod349> callback) {
		AON.start();
		fsa.saveCommentsMod349(domainName, mod349, new AsyncCallbackWrapper<Mod349>(
				callback));
	}

	@Override
	public void changeStatusMod349(String domainName, Mod349 mod349, FiscalStatus newStatus,
			AsyncCallback<Mod349> callback) {
		AON.start();
		fsa.changeStatusMod349(domainName, mod349, newStatus, 
				new AsyncCallbackWrapper<Mod349>(callback));
	}

}
