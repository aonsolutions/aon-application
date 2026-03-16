package com.esferalia.aon.gwt.fiscal.client.mod421;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod421;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod421Key;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface Mod421ServiceAsync {
	// ---------------------------------------------------------------MODELO 421
	void getMod421(Occam occam, int id,AsyncCallback<Mod421> callback);
	void getMod421s(Occam occam,AsyncCallback<LinkedList<Mod421>> callback);
	void initialize(Occam occam, Mod421 mod421,AsyncCallback<Mod421> asyncCallback);
	void create(Occam occam, Mod421 mod421, AsyncCallback<Mod421> callback);
	void delete(Occam occam, Mod421 mod421,AsyncCallback<Void> callback);
	void save(Occam occam, Mod421 mod421,AsyncCallback<Mod421> asyncCallback);
	void saveComments(Occam occam, Mod421 mod421,AsyncCallback<Mod421> asyncCallback);
	void initializeForFinish(Occam occam, Mod421 mod421,AsyncCallback<Mod421> asyncCallback);
	void calculate(Occam occam, Mod421 mod421,AsyncCallback<Mod421> callback);
	void getInfo(Occam occam, Mod421 mod421, IModelScript<Mod421Key> script, FiscalModelKeyInfo infoKey,AsyncCallback<String> callback);
	void markAsFinished(Occam occam, Mod421 mod421,AsyncCallback<Mod421> asyncCallback);
	void markAsPending(Occam occam, Mod421 mod421,AsyncCallback<Mod421> asyncCallback);
	void markAsSent(Occam occam, Mod421 mod421, AsyncCallback<Mod421> asyncCallback);
	void markAsCustomerCheck(Occam occam, Mod421 mod421,AsyncCallback<Mod421> asyncCallback);
	void getInvoice(Occam occam, int invoiceId, AsyncCallback<Invoice> asyncCallback);
	void doRecord(Occam occam, Mod421 mod421, AsyncCallback<Mod421> callback);
	void unrecord(Occam occam, Mod421 mod421, AsyncCallback<Mod421> callback);

}
