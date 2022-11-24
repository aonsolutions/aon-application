package com.esferalia.aon.gwt.fiscal.client.mod390hf;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod390HF;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod390Key;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface Mod390HFServiceAsync {

	void get(Occam occam,int id,AsyncCallback<Mod390HF> callback);
	void getMod390HFs(Occam occam,AsyncCallback<LinkedList<Mod390HF>> callback);
	void delete(Occam occam, Mod390HF mod390HF,AsyncCallback<Void> callback);
	void save(Occam occam, Mod390HF mod390HF,AsyncCallback<Mod390HF> asyncCallback);
	void saveComments(Occam occam, Mod390HF mod390HF,AsyncCallback<Mod390HF> asyncCallback);
	void initializeForFinish(Occam occam, Mod390HF mod390HF,AsyncCallback<Mod390HF> asyncCallback);
	void initialize(Occam occam, Mod390HF mod390HF,AsyncCallback<Mod390HF> asyncCallback);
	void create(Occam occam, Mod390HF mod390HF, AsyncCallback<Mod390HF> callback);
	void reset(Occam occam, Mod390HF model, AsyncCallback<Mod390HF> asyncCallback);
	void calculate(Occam occam, Mod390HF mod390HF,AsyncCallback<Mod390HF> callback);
	void calculateProrrate(Occam occam, Mod390HF model, AsyncCallback<Mod390HF> callback);
	void getInfo(Occam occam, Mod390HF mod390HF, IModelScript<Mod390Key> script, FiscalModelKeyInfo infoKey,AsyncCallback<String> callback);
	
	void markAsFinished(Occam occam, Mod390HF mod390HF,AsyncCallback<Mod390HF> asyncCallback);
	void markAsPending(Occam occam, Mod390HF mod390HF,AsyncCallback<Mod390HF> asyncCallback);
	void markAsSent(Occam occam, Mod390HF mod390HF, AsyncCallback<Mod390HF> asyncCallback);
	void markAsCustomerCheck(Occam occam, Mod390HF mod390hf, AsyncCallback<Mod390HF> asyncCallback);
	void markAsCustomerAccepted(Occam occam, Mod390HF mod390hf, AsyncCallback<Mod390HF> asyncCallback);
	void markAsCustomerRejected(Occam occam, Mod390HF mod390hf, String reason, AsyncCallback<Mod390HF> asyncCallback);
	void getInvoice(Occam occam, int invoiceId, AsyncCallback<Invoice> callback);

}
