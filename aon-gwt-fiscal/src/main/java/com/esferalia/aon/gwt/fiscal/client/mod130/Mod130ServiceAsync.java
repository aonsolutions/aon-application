package com.esferalia.aon.gwt.fiscal.client.mod130;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod130;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod130Key;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface Mod130ServiceAsync {
	
	void getMod130(Occam occam, int id,AsyncCallback<Mod130> callback);
	void getMod130s(Occam occam,AsyncCallback<LinkedList<Mod130>> callback);
	void calculate(Occam occam, Mod130 mod130,AsyncCallback<Mod130> callback);
	void save(Occam occam, Mod130 mod130,AsyncCallback<Mod130> asyncCallback);
	void saveComments(Occam occam, Mod130 mod130,AsyncCallback<Mod130> asyncCallback);
	void initializeForFinish(Occam occam, Mod130 mod130,AsyncCallback<Mod130> asyncCallback);
	void delete(Occam occam, Mod130 mod130,AsyncCallback<Void> callback);
	void initialize(Occam occam, Mod130 mod130,AsyncCallback<Mod130> asyncCallback);
	void create(Occam occam, Mod130 mod130, AsyncCallback<Mod130> callback);
	
	void getInfo(Occam occam, Mod130 mod130, IModelScript<Mod130Key> script, FiscalModelKeyInfo infoKey,AsyncCallback<String> callback);
	
	void markAsFinished(Occam occam, Mod130 mod130,AsyncCallback<Mod130> asyncCallback);
	void markAsSent(Occam occam, Mod130 mod130,AsyncCallback<Mod130> asyncCallback);
	void markAsPending(Occam occam, Mod130 mod130,AsyncCallback<Mod130> asyncCallback);
	void markAsCustomerCheck(Occam occam, Mod130 mod130,AsyncCallback<Mod130> asyncCallback);
	void markAsCustomerAccepted(Occam occam, Mod130 mod130, AsyncCallback<Mod130> callback);
	void markAsCustomerRejected(Occam occam, Mod130 mod130, String reason, AsyncCallback<Mod130> callback);
	void getInvoice(Occam occam, int invoiceId, AsyncCallback<Invoice> callback);
	
}
