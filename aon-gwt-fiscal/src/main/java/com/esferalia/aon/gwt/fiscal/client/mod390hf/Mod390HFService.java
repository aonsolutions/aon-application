package com.esferalia.aon.gwt.fiscal.client.mod390hf;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod390HF;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod390Key;
import com.esferalia.aon.watson.error.AonCoreException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("ms/Mod390HF")
public interface Mod390HFService extends RemoteService {

	Mod390HF get(Occam occam, int id) throws AonCoreException;
	LinkedList<Mod390HF> getMod390HFs(Occam occam) throws AonCoreException;
	Mod390HF initialize(Occam occam, Mod390HF mod);
	Mod390HF create(Occam occam, Mod390HF mod) throws AonCoreException;
	Mod390HF reset(Occam occam, Mod390HF model) throws AonCoreException;
	void delete(Occam occam, Mod390HF mod) throws AonCoreException;
	Mod390HF save(Occam occam, Mod390HF mod) throws AonCoreException;
	Mod390HF saveComments(Occam occam, Mod390HF mod) throws AonCoreException;
	Mod390HF initializeForFinish(Occam occam, Mod390HF mod) throws AonCoreException;
	Mod390HF calculate(Occam occam, Mod390HF mod) throws AonCoreException;
	Mod390HF calculateProrrate(Occam occam, Mod390HF model);
	String getInfo(Occam occam, Mod390HF mod, IModelScript<Mod390Key> script, FiscalModelKeyInfo infoKey) throws AonCoreException;
	
	Mod390HF markAsFinished(Occam occam, Mod390HF mod) throws AonCoreException;
	Mod390HF markAsPending(Occam occam, Mod390HF mod) throws AonCoreException;
	Mod390HF markAsSent(Occam occam, Mod390HF mod) throws AonCoreException;
	Mod390HF markAsCustomerCheck(Occam occam, Mod390HF mod390hf) throws AonCoreException;
	Mod390HF markAsCustomerAccepted(Occam occam, Mod390HF mod390hf) throws AonCoreException;
	Mod390HF markAsCustomerRejected(Occam occam, Mod390HF mod390hf, String reason) throws AonCoreException;

	Invoice getInvoice(Occam occam, int invoiceId) throws AonCoreException;

}
