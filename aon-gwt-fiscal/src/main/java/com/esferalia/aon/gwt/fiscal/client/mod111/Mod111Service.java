package com.esferalia.aon.gwt.fiscal.client.mod111;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod111;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod111Key;
import com.esferalia.aon.watson.error.AonCoreException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("ms/Mod111")
public interface Mod111Service extends RemoteService {

	Mod111 getMod111(Occam occam, int id) throws AonCoreException;
	LinkedList<Mod111> getMod111s(Occam occam) throws AonCoreException;
	Mod111 calculate(Occam occam, Mod111 mod111) throws AonCoreException;
	Mod111 save(Occam occam, Mod111 mod111) throws AonCoreException;
	Mod111 saveComments(Occam occam, Mod111 mod111) throws AonCoreException;
	Mod111 initializeForFinish(Occam occam, Mod111 mod111) throws AonCoreException;
	Mod111 markAsFinished(Occam occam, Mod111 mod111) throws AonCoreException;
	Mod111 markAsPending(Occam occam, Mod111 mod111) throws AonCoreException;
	Mod111 markAsSent(Occam occam, Mod111 mod111) throws AonCoreException;
	Mod111 markAsCustomerCheck(Occam occam, Mod111 mod111) throws AonCoreException;
	void delete(Occam occam, Mod111 mod111) throws AonCoreException;
	Mod111 initialize(Occam occam, Mod111 mod111) throws AonCoreException;
	Mod111 create(Occam occam, Mod111 mod111) throws AonCoreException;
	String getInfo(Occam occam, Mod111 mod111, IModelScript<Mod111Key> script, FiscalModelKeyInfo infoKey) throws AonCoreException;
	Invoice getInvoice(Occam occam, int invoiceId) throws AonCoreException;
	
}
