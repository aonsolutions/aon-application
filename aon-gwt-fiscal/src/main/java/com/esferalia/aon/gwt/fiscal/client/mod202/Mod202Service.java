package com.esferalia.aon.gwt.fiscal.client.mod202;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod202;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod202Key;
import com.esferalia.aon.watson.error.AonCoreException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("ms/Mod202")
public interface Mod202Service extends RemoteService {

	Mod202 getMod202(Occam occam, int id) throws AonCoreException;
	LinkedList<Mod202> getMod202s(Occam occam) throws AonCoreException;
	Mod202 calculate(Occam occam, Mod202 mod202) throws AonCoreException;
	Mod202 save(Occam occam, Mod202 mod202) throws AonCoreException;
	void delete(Occam occam, Mod202 mod202) throws AonCoreException;
	Mod202 initialize(Occam occam, Mod202 mod202) throws AonCoreException;
	Mod202 saveComments(Occam occam, Mod202 mod202) throws AonCoreException;
	Mod202 initializeForFinish(Occam occam, Mod202 mod202) throws AonCoreException;
	Mod202 markAsFinished(Occam occam, Mod202 mod202) throws AonCoreException;
	Mod202 markAsSent(Occam occam, Mod202 mod202) throws AonCoreException;
	Mod202 markAsCustomerCheck(Occam occam, Mod202 mod202) throws AonCoreException;
	Mod202 markAsPending(Occam occam, Mod202 mod202) throws AonCoreException;
	Mod202 create(Occam occam, Mod202 mod202) throws AonCoreException;
	Mod202 reset(Occam occam, Mod202 model) throws AonCoreException;
	String getInfo(Occam occam, Mod202 mod202, IModelScript<Mod202Key> script, FiscalModelKeyInfo infoKey) throws AonCoreException;

}
