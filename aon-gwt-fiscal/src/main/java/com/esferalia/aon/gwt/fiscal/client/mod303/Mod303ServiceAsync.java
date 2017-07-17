package com.esferalia.aon.gwt.fiscal.client.mod303;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod303Key;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface Mod303ServiceAsync {
	// ---------------------------------------------------------------MODELO 303
	void getMod303(String domainName, int domain, int id,AsyncCallback<Mod303> callback);
	void getMod303s(String domainName, int domain,AsyncCallback<LinkedList<Mod303>> callback);
	void calculateMod303(String domainName, Mod303 mod303,AsyncCallback<Mod303> callback);
	void deleteMod303(String currentDomainName, Mod303 mod303,AsyncCallback<Void> callback);
	void saveMod303(String domainName, Mod303 mod303,AsyncCallback<Mod303> asyncCallback);
	void saveCommentsMod303(String domainName, Mod303 mod303,AsyncCallback<Mod303> asyncCallback);
	void initializeForFinishMod303(String domainName, Mod303 mod303,AsyncCallback<Mod303> asyncCallback);
	void finishMod303(String domainName, Mod303 mod303,AsyncCallback<Mod303> asyncCallback);
	void reopenMod303(String domainName, Mod303 mod303,AsyncCallback<Mod303> asyncCallback);
	void initializeMod303(String domainName, int domain, Mod303 mod303,AsyncCallback<Mod303> asyncCallback);
	void createMod303(String domainName, int domain, Mod303 mod303, AsyncCallback<Mod303> callback);
	void getInfo(String domainName, int domain, Mod303 mod303, IModelScript<Mod303Key> script, FiscalModelKeyInfo infoKey,AsyncCallback<String> callback);

}
