package com.esferalia.aon.gwt.fiscal.client.mod123;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod123;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod123Key;
import com.esferalia.aon.watson.error.AonCoreException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("ms/Mod123")
public interface Mod123Service extends RemoteService {
	
	Mod123 getMod123(Occam occam, int id) throws AonCoreException;
	LinkedList<Mod123> getMod123s(Occam occam) throws AonCoreException;
	Mod123 calculate(Occam occam, Mod123 mod123) throws AonCoreException;
	Mod123 save(Occam occam, Mod123 mod123) throws AonCoreException;
	Mod123 saveComments(Occam occam, Mod123 mod123) throws AonCoreException;
	Mod123 initializeForFinish(Occam occam, Mod123 mod123) throws AonCoreException;
	void delete(Occam occam, Mod123 mod123) throws AonCoreException;
	Mod123 markAsFinished(Occam occam, Mod123 mod123) throws AonCoreException;
	Mod123 markAsPending(Occam occam, Mod123 mod123) throws AonCoreException;
	Mod123 markAsSent(Occam occam, Mod123 mod123) throws AonCoreException;
	Mod123 markAsCustomerCheck(Occam occam, Mod123 mod123) throws AonCoreException;
	Mod123 initialize(Occam occam, Mod123 mod123) throws AonCoreException;
	Mod123 create(Occam occam, Mod123 mod123) throws AonCoreException;
	String getInfo(Occam occam, Mod123 mod123, IModelScript<Mod123Key> script, FiscalModelKeyInfo infoKey) throws AonCoreException;
	Invoice getInvoice(Occam occam, int invoiceId) throws AonCoreException;
	
}
