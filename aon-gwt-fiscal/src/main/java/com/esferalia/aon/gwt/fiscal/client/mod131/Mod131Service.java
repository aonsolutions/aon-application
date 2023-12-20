package com.esferalia.aon.gwt.fiscal.client.mod131;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod131;
import com.esferalia.aon.occam.api.model.fiscal.Mod131Activity;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod131Key;
import com.esferalia.aon.watson.error.AonCoreException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("ms/Mod131")
public interface Mod131Service extends RemoteService {

	Mod131 get(Occam occam, int id) throws AonCoreException;
	LinkedList<Mod131> getMod131s(Occam occam) throws AonCoreException;
	Mod131 calculate(Occam occam, Mod131 mod131) throws AonCoreException;
	Mod131Activity calculateActivity(Occam occam, Mod131 mod131, Mod131Activity activity) throws AonCoreException;
	Mod131 save(Occam occam, Mod131 mod131) throws AonCoreException;
	Mod131 saveComments(Occam occam, Mod131 mod131) throws AonCoreException;
	Mod131 initializeForFinish(Occam occam, Mod131 mod131) throws AonCoreException;
	Mod131 markAsFinished(Occam occam, Mod131 mod131) throws AonCoreException;
	Mod131 markAsSent(Occam occam, Mod131 mod131) throws AonCoreException;
	Mod131 markAsPending(Occam occam, Mod131 mod131) throws AonCoreException;
	Mod131 markAsCustomerCheck(Occam occam, Mod131 mod131) throws AonCoreException;
	void delete(Occam occam, Mod131 mod131) throws AonCoreException;
	Mod131 initialize(Occam occam, Mod131 mod131);
	Mod131 create(Occam occam, Mod131 mod131) throws AonCoreException;
	Mod131 reset(Occam occam, Mod131 model) throws AonCoreException;
	String getInfo(Occam occam, Mod131 mod131, IModelScript<Mod131Key> script, FiscalModelKeyInfo infoKey) throws AonCoreException;

}
