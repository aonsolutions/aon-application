package com.esferalia.aon.gwt.fiscal.client.mod369;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod369;
import com.esferalia.aon.occam.api.model.fiscal.Mod369Detail;
import com.esferalia.aon.occam.api.model.type.Period;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface Model369ServiceAsync {

	void getMod369s(Occam occam, AsyncCallback<LinkedList<Mod369>> callback);
	void get(Occam occam,Integer id, AsyncCallback<Mod369> callback);
	void delete(Occam occam,Mod369 mod369, AsyncCallback<Void> callback);
	void save(Occam occam,Mod369 mod369, AsyncCallback<Mod369> callback);
	void initialize(Occam occam, Integer year, Period period,AsyncCallback<Mod369> callback);
	void saveComments(Occam occam, Mod369 mod369,AsyncCallback<Mod369> asyncCallback);
	void changeStatus(Occam occam, Mod369 mod369, FiscalStatus newStatus, AsyncCallback<Mod369> callback);
	void getInfo(Occam occam, Mod369 model, Mod369Detail mod369Detail, byte detailType, AsyncCallback<String> callback);
	void getInvoice(Occam occam, int invoiceId, AsyncCallback<Invoice> asyncCallback);

}
