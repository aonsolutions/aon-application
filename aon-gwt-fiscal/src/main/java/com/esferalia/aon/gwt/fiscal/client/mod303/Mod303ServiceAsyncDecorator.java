package com.esferalia.aon.gwt.fiscal.client.mod303;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AsyncCallbackWrapper;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod303Key;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class Mod303ServiceAsyncDecorator implements Mod303ServiceAsync {

	private Mod303ServiceAsync fsa;

	public Mod303ServiceAsyncDecorator(Mod303ServiceAsync mod190ServiceAsync) {
		this.fsa = mod190ServiceAsync;
	}

	// ---------------------------------------------------------------MODELO 303

	@Override
	public void getMod303(String domainName, int domain, int id,
			AsyncCallback<Mod303> callback) {
		AON.start();
		fsa.getMod303(domainName, domain, id, new AsyncCallbackWrapper<Mod303>(
				callback));
	}

	@Override
	public void getMod303s(String domainName, int domain,
			AsyncCallback<LinkedList<Mod303>> callback) {
		AON.start();
		fsa.getMod303s(domainName, domain,
				new AsyncCallbackWrapper<LinkedList<Mod303>>(callback));
	}

	@Override
	public void calculateMod303(String domainName, Mod303 mod303,
			AsyncCallback<Mod303> callback) {
		AON.start();
		fsa.calculateMod303(domainName, mod303,
				new AsyncCallbackWrapper<Mod303>(callback));
	}

	@Override
	public void saveMod303(String domainName, Mod303 mod303,
			AsyncCallback<Mod303> callback) {
		AON.start();
		fsa.saveMod303(domainName, mod303, new AsyncCallbackWrapper<Mod303>(
				callback));
	}
	
	@Override
	public void saveCommentsMod303(String domainName, Mod303 mod303,
			AsyncCallback<Mod303> callback) {
		AON.start();
		fsa.saveCommentsMod303(domainName, mod303, new AsyncCallbackWrapper<Mod303>(
				callback));
	}

	@Override
	public void initializeForFinishMod303(String domainName, Mod303 mod303,
			AsyncCallback<Mod303> callback) {
		AON.start();
		fsa.initializeForFinishMod303(domainName, mod303, new AsyncCallbackWrapper<Mod303>(
				callback));
	}
	
	@Override
	public void finishMod303(String domainName, Mod303 mod303,
			AsyncCallback<Mod303> callback) {
		AON.start();
		fsa.finishMod303(domainName, mod303, new AsyncCallbackWrapper<Mod303>(
				callback));
	}
	
	@Override
	public void reopenMod303(String domainName, Mod303 mod303,
			AsyncCallback<Mod303> callback) {
		AON.start();
		fsa.reopenMod303(domainName, mod303, new AsyncCallbackWrapper<Mod303>(
				callback));
	}

	@Override
	public void initializeMod303(String domainName, int currentDomain,
			Mod303 mod303, AsyncCallback<Mod303> callback) {
		AON.start();
		fsa.initializeMod303(domainName, currentDomain,mod303,new AsyncCallbackWrapper<Mod303>(callback));

	}

	@Override
	public void createMod303(String domainName, int currentDomain,
			Mod303 mod303, AsyncCallback<Mod303> callback) {
		AON.start();
		fsa.createMod303(domainName, currentDomain, mod303,new AsyncCallbackWrapper<Mod303>(callback));

	}

	@Override
	public void deleteMod303(String domainName, Mod303 mod303,
			AsyncCallback<Void> callback) {
		AON.start();
		fsa.deleteMod303(domainName, mod303, new AsyncCallbackWrapper<Void>(
				callback));
	}
	@Override
	public void getInfo(String domainName, int domain, Mod303 mod303, IModelScript<Mod303Key> script, FiscalModelKeyInfo infoKey,
			AsyncCallback<String> callback) {
		AON.start();
		fsa.getInfo(domainName, domain, mod303, script, infoKey,new AsyncCallbackWrapper<String>(callback));
		
	}
	
}
