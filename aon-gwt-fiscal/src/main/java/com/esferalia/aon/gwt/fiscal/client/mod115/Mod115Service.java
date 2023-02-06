package com.esferalia.aon.gwt.fiscal.client.mod115;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod115;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod115Key;
import com.esferalia.aon.watson.error.AonCoreException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("ms/Mod115")
public interface Mod115Service extends RemoteService {

	Mod115 getMod115(Occam occam, int id) throws AonCoreException;
	LinkedList<Mod115> getMod115s(Occam occam) throws AonCoreException;
	Mod115 calculate(Occam occam, Mod115 mod115) throws AonCoreException;
	Mod115 save(Occam occam, Mod115 mod115) throws AonCoreException;
	Mod115 saveComments(Occam occam, Mod115 mod115) throws AonCoreException;
	Mod115 initializeForFinish(Occam occam, Mod115 mod115) throws AonCoreException;
	Mod115 markAsFinished(Occam occam, Mod115 mod115) throws AonCoreException;
	Mod115 markAsPending(Occam occam, Mod115 mod115) throws AonCoreException;
	Mod115 markAsSent(Occam occam, Mod115 mod115) throws AonCoreException;
	Mod115 markAsCustomerCheck(Occam occam, Mod115 mod115) throws AonCoreException;
	void delete(Occam occam, Mod115 mod115) throws AonCoreException;
	Mod115 initialize(Occam occam, Mod115 mod115);
	Mod115 create(Occam occam, Mod115 mod115) throws AonCoreException;
	String getInfo(Occam occam, Mod115 mod115, IModelScript<Mod115Key> script, FiscalModelKeyInfo infoKey) throws AonCoreException;
	Invoice getInvoice(Occam occam, int invoiceId) throws AonCoreException;
	
}
