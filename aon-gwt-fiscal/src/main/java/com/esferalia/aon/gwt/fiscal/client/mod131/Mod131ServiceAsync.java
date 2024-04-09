package com.esferalia.aon.gwt.fiscal.client.mod131;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod131;
import com.esferalia.aon.occam.api.model.fiscal.Mod131Activity;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod131Key;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface Mod131ServiceAsync {

	void get(Occam occam, int id,AsyncCallback<Mod131> callback);
	void getMod131s(Occam occam,AsyncCallback<LinkedList<Mod131>> callback);
	void calculate(Occam occam, Mod131 mod131,AsyncCallback<Mod131> callback);
	void calculateActivity(Occam occam, Mod131 mod131, Mod131Activity activity, AsyncCallback<Mod131Activity> callback);
	void delete(Occam occam, Mod131 mod131,AsyncCallback<Void> callback);
	void save(Occam occam, Mod131 mod131,AsyncCallback<Mod131> asyncCallback);
	void saveComments(Occam occam, Mod131 mod131,AsyncCallback<Mod131> asyncCallback);
	void initializeForFinish(Occam occam, Mod131 mod131,AsyncCallback<Mod131> asyncCallback);
	void markAsFinished(Occam occam, Mod131 mod131,AsyncCallback<Mod131> asyncCallback);
	void markAsSent(Occam occam, Mod131 mod131,AsyncCallback<Mod131> asyncCallback);
	void markAsPending(Occam occam, Mod131 mod131,AsyncCallback<Mod131> asyncCallback);
	void markAsCustomerCheck(Occam occam, Mod131 mod131,AsyncCallback<Mod131> asyncCallback);
	void initialize(Occam occam, Mod131 mod131,AsyncCallback<Mod131> asyncCallback);
	void create(Occam occam, Mod131 mod131, AsyncCallback<Mod131> callback);
	void getInfo(Occam occam, Mod131 mod131, IModelScript<Mod131Key> script, FiscalModelKeyInfo infoKey,AsyncCallback<String> callback);
	void getInvoice(Occam occam, int invoiceId, AsyncCallback<Invoice> callback);

}
