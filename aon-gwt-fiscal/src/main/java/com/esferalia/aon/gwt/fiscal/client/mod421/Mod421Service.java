package com.esferalia.aon.gwt.fiscal.client.mod421;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod421;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod421Key;
import com.esferalia.aon.watson.error.AonCoreException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("ms/Mod421")
public interface Mod421Service extends RemoteService {

	// ---------------------------------------------------------------MODELO 421
	Mod421 getMod421(Occam occam, int id) throws AonCoreException;
	LinkedList<Mod421> getMod421s(Occam occam) throws AonCoreException;
	Mod421 initialize(Occam occam, Mod421 mod421) throws AonCoreException;
	Mod421 create(Occam occam, Mod421 mod421) throws AonCoreException;
	void delete(Occam occam, Mod421 mod421) throws AonCoreException;
	Mod421 save(Occam occam, Mod421 mod421) throws AonCoreException;
	Mod421 saveComments(Occam occam, Mod421 mod421) throws AonCoreException;
	Mod421 initializeForFinish(Occam occam, Mod421 mod421) throws AonCoreException;
	Mod421 calculate(Occam occam, Mod421 mod421) throws AonCoreException;
	String getInfo(Occam occam, Mod421 mod421, IModelScript<Mod421Key> script, FiscalModelKeyInfo infoKey) throws AonCoreException;
	Mod421 markAsFinished(Occam occam, Mod421 mod421) throws AonCoreException;
	Mod421 markAsPending(Occam occam, Mod421 mod421) throws AonCoreException;
	Mod421 markAsSent(Occam occam, Mod421 mod421) throws AonCoreException;
	Mod421 markAsCustomerCheck(Occam occam, Mod421 mod421) throws AonCoreException;
	Invoice getInvoice(Occam occam, int invoiceId) throws AonCoreException;
	Mod421 doRecord(Occam occam, Mod421 mod421) throws AonCoreException;
	Mod421 unrecord(Occam occam, Mod421 mod421) throws AonCoreException;
	
}
