package com.esferalia.aon.gwt.fiscal.client.mod303;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod303Key;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface Mod303ServiceAsync {
	// ---------------------------------------------------------------MODELO 303
	void getMod303(Occam occam, int id,AsyncCallback<Mod303> callback);
	void getMod303s(Occam occam,AsyncCallback<LinkedList<Mod303>> callback);
	void initialize(Occam occam, Mod303 mod303,AsyncCallback<Mod303> asyncCallback);
	void create(Occam occam, Mod303 mod303, AsyncCallback<Mod303> callback);
	void reset(Occam occam, Mod303 model, AsyncCallback<Mod303> asyncCallback);

	void delete(Occam occam, Mod303 mod303,AsyncCallback<Void> callback);
	void save(Occam occam, Mod303 mod303,AsyncCallback<Mod303> asyncCallback);
	void saveComments(Occam occam, Mod303 mod303,AsyncCallback<Mod303> asyncCallback);
	void initializeForFinish(Occam occam, Mod303 mod303,AsyncCallback<Mod303> asyncCallback);
	void calculate(Occam occam, Mod303 mod303,AsyncCallback<Mod303> callback);
	void calculateProrrate(Occam occam, Mod303 mod303, AsyncCallback<Mod303> callback);
	void getInfo(Occam occam, Mod303 mod303, IModelScript<Mod303Key> script, FiscalModelKeyInfo infoKey,AsyncCallback<String> callback);
	void markAsFinished(Occam occam, Mod303 mod303,AsyncCallback<Mod303> asyncCallback);
	void markAsPending(Occam occam, Mod303 mod303,AsyncCallback<Mod303> asyncCallback);
	void markAsSent(Occam occam, Mod303 mod303, AsyncCallback<Mod303> asyncCallback);
	void markAsCustomerCheck(Occam occam, Mod303 mod303,AsyncCallback<Mod303> asyncCallback);
	void getInvoice(Occam occam, int invoiceId, AsyncCallback<Invoice> asyncCallback);

	void doRecord(Occam occam, Mod303 mod303, AsyncCallback<Mod303> callback);
	void unrecord(Occam occam, Mod303 mod303, AsyncCallback<Mod303> callback);

}
