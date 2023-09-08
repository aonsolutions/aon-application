package com.esferalia.aon.gwt.fiscal.client.mod130;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod130;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod130Key;
import com.esferalia.aon.watson.error.AonCoreException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("ms/Mod130")
public interface Mod130Service extends RemoteService {
	
	Mod130 getMod130(Occam occam, int id) throws AonCoreException;
	LinkedList<Mod130> getMod130s(Occam occam) throws AonCoreException;
	Mod130 calculate(Occam occam, Mod130 mod130) throws AonCoreException;
	Mod130 save(Occam occam, Mod130 mod130) throws AonCoreException;
	Mod130 saveComments(Occam occam, Mod130 mod130) throws AonCoreException;
	Mod130 initializeForFinish(Occam occam, Mod130 mod130) throws AonCoreException;
	void delete(Occam occam, Mod130 mod130) throws AonCoreException;
	Mod130 initialize(Occam occam, Mod130 mod130) throws AonCoreException;
	Mod130 create(Occam occam, Mod130 mod130) throws AonCoreException;
	
	String getInfo(Occam occam, Mod130 mod130, IModelScript<Mod130Key> script, FiscalModelKeyInfo infoKey) throws AonCoreException;

	Mod130 markAsFinished(Occam occam, Mod130 mod130) throws AonCoreException;
	Mod130 markAsSent(Occam occam, Mod130 mod130) throws AonCoreException;
	Mod130 markAsPending(Occam occam, Mod130 mod130) throws AonCoreException;
	Mod130 markAsCustomerCheck(Occam occam, Mod130 mod130) throws AonCoreException;
	Mod130 markAsCustomerAccepted(Occam occam, Mod130 mod130) throws AonCoreException;
	Mod130 markAsCustomerRejected(Occam occam, Mod130 mod130, String reason) throws AonCoreException;
	
	Invoice getInvoice(Occam occam, int invoiceId) throws AonCoreException;

}
