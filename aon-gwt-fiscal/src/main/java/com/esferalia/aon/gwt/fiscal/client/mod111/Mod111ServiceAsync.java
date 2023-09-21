package com.esferalia.aon.gwt.fiscal.client.mod111;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod111;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod111Key;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface Mod111ServiceAsync {
	
	void getMod111(Occam occam, int id,AsyncCallback<Mod111> callback);
	void getMod111s(Occam occam,AsyncCallback<LinkedList<Mod111>> callback);
	void calculate(Occam occam, Mod111 mod111,AsyncCallback<Mod111> callback);
	void delete(Occam occam, Mod111 mod111,AsyncCallback<Void> callback);
	void save(Occam occam, Mod111 mod111,AsyncCallback<Mod111> asyncCallback);
	void saveComments(Occam occam, Mod111 mod111,AsyncCallback<Mod111> asyncCallback);
	void initializeForFinish(Occam occam, Mod111 mod111,AsyncCallback<Mod111> asyncCallback);
	void markAsFinished(Occam occam, Mod111 mod111,AsyncCallback<Mod111> asyncCallback);
	void markAsPending(Occam occam, Mod111 mod111,AsyncCallback<Mod111> asyncCallback);
	void markAsSent(Occam occam, Mod111 mod111, AsyncCallback<Mod111> asyncCallback);
	void markAsCustomerCheck(Occam occam, Mod111 currentMod, AsyncCallback<Mod111> asyncCallback);
	void initialize(Occam occam, Mod111 mod111,AsyncCallback<Mod111> asyncCallback);
	void create(Occam occam, Mod111 mod111, AsyncCallback<Mod111> callback);
	void getInfo(Occam occam, Mod111 mod111, IModelScript<Mod111Key> script, FiscalModelKeyInfo infoKey,AsyncCallback<String> callback);
	void getInvoice(Occam occam, int invoiceId, AsyncCallback<Invoice> callback);
}
