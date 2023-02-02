package com.esferalia.aon.gwt.fiscal.client.mod123;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod123;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod123Key;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface Mod123ServiceAsync {
	
	void getMod123(Occam occam,int id,AsyncCallback<Mod123> callback);
	void getMod123s(Occam occam,AsyncCallback<LinkedList<Mod123>> callback);
	void calculate(Occam occam, Mod123 mod123,AsyncCallback<Mod123> callback);
	void delete(Occam occam, Mod123 mod123,AsyncCallback<Void> callback);
	void save(Occam occam, Mod123 mod123,AsyncCallback<Mod123> asyncCallback);
	void saveComments(Occam occam, Mod123 mod123,AsyncCallback<Mod123> asyncCallback);
	void initializeForFinish(Occam occam, Mod123 mod123,AsyncCallback<Mod123> asyncCallback);
	void markAsFinished(Occam occam, Mod123 mod123,AsyncCallback<Mod123> asyncCallback);
	void markAsSent(Occam occam, Mod123 mod123,AsyncCallback<Mod123> asyncCallback);
	void markAsCustomerCheck(Occam occam, Mod123 mod123,AsyncCallback<Mod123> asyncCallback);
	void markAsPending(Occam occam, Mod123 mod123,AsyncCallback<Mod123> asyncCallback);
	void initialize(Occam occam, Mod123 mod123,AsyncCallback<Mod123> asyncCallback);
	void create(Occam occam, Mod123 mod123, AsyncCallback<Mod123> callback);
	void getInfo(Occam occam, Mod123 mod123, IModelScript<Mod123Key> script, FiscalModelKeyInfo infoKey,AsyncCallback<String> callback);
	void getInvoice(Occam occam, int invoiceId, AsyncCallback<Invoice> callback);
}
