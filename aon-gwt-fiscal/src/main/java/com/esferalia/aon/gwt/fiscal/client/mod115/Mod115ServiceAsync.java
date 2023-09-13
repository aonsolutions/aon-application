package com.esferalia.aon.gwt.fiscal.client.mod115;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod115;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod115Key;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface Mod115ServiceAsync {
	
	void getMod115(Occam occam, int id,AsyncCallback<Mod115> callback);
	void getMod115s(Occam occam,AsyncCallback<LinkedList<Mod115>> callback);
	void calculate(Occam occam, Mod115 mod115,AsyncCallback<Mod115> callback);
	void delete(Occam occam, Mod115 mod115,AsyncCallback<Void> callback);
	void save(Occam occam, Mod115 mod115,AsyncCallback<Mod115> asyncCallback);
	void saveComments(Occam occam, Mod115 mod115,AsyncCallback<Mod115> asyncCallback);
	void initializeForFinish(Occam occam, Mod115 mod115,AsyncCallback<Mod115> asyncCallback);
	void markAsFinished(Occam occam, Mod115 mod115,AsyncCallback<Mod115> asyncCallback);
	void markAsPending(Occam occam, Mod115 mod115,AsyncCallback<Mod115> asyncCallback);
	void markAsSent(Occam occam, Mod115 mod115, AsyncCallback<Mod115> callback);
	void markAsCustomerCheck(Occam occam, Mod115 mod115, AsyncCallback<Mod115> callback);
	void initialize(Occam occam, Mod115 mod115,AsyncCallback<Mod115> asyncCallback);
	void create(Occam occam, Mod115 mod115, AsyncCallback<Mod115> callback);
	void getInfo(Occam occam, Mod115 mod115, IModelScript<Mod115Key> script, FiscalModelKeyInfo infoKey,AsyncCallback<String> callback);
	void getInvoice(Occam occam, int invoiceId, AsyncCallback<Invoice> callback);
}
