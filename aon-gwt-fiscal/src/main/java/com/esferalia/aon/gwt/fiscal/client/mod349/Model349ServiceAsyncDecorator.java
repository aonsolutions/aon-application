package com.esferalia.aon.gwt.fiscal.client.mod349;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AsyncCallbackWrapper;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod349;
import com.esferalia.aon.occam.api.model.fiscal.Mod349Detail;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class Model349ServiceAsyncDecorator implements Model349ServiceAsync {
	
	private Model349ServiceAsync fsa;

	public Model349ServiceAsyncDecorator(Model349ServiceAsync mod349ServiceAsync) {
		this.fsa = mod349ServiceAsync;
	}
	
	
	// ---------------------------------------------------------------MODELO 349
	@Override
	public void deleteMod349(String domainName, String user, int domainId, Mod349 mod349,
			AsyncCallback<Void> callback) {
		AON.start();
		fsa.deleteMod349(domainName,user, domainId, mod349, new AsyncCallbackWrapper<Void>(callback));
	}

	@Override
	public void saveMod349(String domainName, String user, int domainId, Mod349 mod349,
			AsyncCallback<Mod349> callback) {
		AON.start();
		fsa.saveMod349(domainName,user, domainId, mod349,new AsyncCallbackWrapper<Mod349>(callback));
	}

	@Override
	public void getMod349s(String domainName, String user, int domainId,
			AsyncCallback<LinkedList<Mod349>> callback) {
		AON.start();
		fsa.getMod349s(domainName,user, domainId, new AsyncCallbackWrapper<LinkedList<Mod349>>(callback));
	}

	@Override
	public void initializeMod349(String domainName, String user, Integer domain,
			AsyncCallback<Mod349> callback) {
		AON.start();
		fsa.initializeMod349(domainName,user, domain, new AsyncCallbackWrapper<Mod349>(callback));
	}

	@Override
	public void getMod349(String domainName, String user, int domainId, Integer id,
			AsyncCallback<Mod349> callback) {
		AON.start();
		fsa.getMod349(domainName,user, domainId, id, new AsyncCallbackWrapper<Mod349>(callback));
	}

	@Override
	public void getMod349Detail(String domainName, String user, int domainId, Integer id,
			AsyncCallback<Mod349Detail> callback) {
		AON.start();
		fsa.getMod349Detail(domainName,user, domainId, id,new AsyncCallbackWrapper<Mod349Detail>(callback));
	}
	
	@Override
	public void saveCommentsMod349(String domainName, String user, Mod349 mod349,
			AsyncCallback<Mod349> callback) {
		AON.start();
		fsa.saveCommentsMod349(domainName,user, mod349, new AsyncCallbackWrapper<Mod349>(callback));
	}

	@Override
	public void changeStatusMod349(String domainName, String user, Mod349 mod349, FiscalStatus newStatus,
			AsyncCallback<Mod349> callback) {
		AON.start();
		fsa.changeStatusMod349(domainName,user, mod349, newStatus, new AsyncCallbackWrapper<Mod349>(callback));
	}
	
	@Override
	public void getInfo(String domainName, String user, int domain, Mod349 mod349, Mod349Detail detail, FiscalModelKeyInfo infoKey,
			AsyncCallback<String> callback) {
		AON.start();
		fsa.getInfo(domainName,user, domain, mod349, detail, infoKey, new AsyncCallbackWrapper<String>(callback));		
	}

}
