package com.esferalia.aon.gwt.fiscal.client.mod303;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod303Key;
import com.esferalia.aon.watson.error.AonCoreException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("ms/Mod303")
public interface Mod303Service extends RemoteService {

	// ---------------------------------------------------------------MODELO 303
	Mod303 getMod303(Occam occam, int id) throws AonCoreException;
	LinkedList<Mod303> getMod303s(Occam occam) throws AonCoreException;
	Mod303 initialize(Occam occam, Mod303 mod303) throws AonCoreException;
	Mod303 create(Occam occam, Mod303 mod303) throws AonCoreException;
	Mod303 reset(Occam occam, Mod303 model) throws AonCoreException;
	
	void delete(Occam occam, Mod303 mod303) throws AonCoreException;
	Mod303 save(Occam occam, Mod303 mod303) throws AonCoreException;
	Mod303 saveComments(Occam occam, Mod303 mod303) throws AonCoreException;
	Mod303 initializeForFinish(Occam occam, Mod303 mod303) throws AonCoreException;
	Mod303 calculate(Occam occam, Mod303 mod303) throws AonCoreException;
	Mod303 calculateProrrate(Occam occam, Mod303 mod303) throws AonCoreException;
	String getInfo(Occam occam, Mod303 mod303, IModelScript<Mod303Key> script, FiscalModelKeyInfo infoKey) throws AonCoreException;
	Mod303 markAsFinished(Occam occam, Mod303 mod303) throws AonCoreException;
	Mod303 markAsPending(Occam occam, Mod303 mod303) throws AonCoreException;
	Mod303 markAsSent(Occam occam, Mod303 mod303) throws AonCoreException;
	Mod303 markAsCustomerCheck(Occam occam, Mod303 mod303) throws AonCoreException;
	Invoice getInvoice(Occam occam, int invoiceId) throws AonCoreException;
	
	Mod303 doRecord(Occam occam, Mod303 mod303) throws AonCoreException;
	Mod303 unrecord(Occam occam, Mod303 mod303) throws AonCoreException;
	
}
