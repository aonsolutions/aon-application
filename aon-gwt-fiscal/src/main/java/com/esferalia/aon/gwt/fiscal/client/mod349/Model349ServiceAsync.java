package com.esferalia.aon.gwt.fiscal.client.mod349;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod349;
import com.esferalia.aon.occam.api.model.fiscal.Mod349Detail;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Period;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface Model349ServiceAsync {
	
	void getMod349s(Occam occam, AsyncCallback<LinkedList<Mod349>> callback);
	void get(Occam occam, Integer id, AsyncCallback<Mod349> callback);
	void getDetail(Occam occam, Integer id, AsyncCallback<Mod349Detail> callback);
	void delete(Occam occam, Mod349 mod349,AsyncCallback<Void> callback);
	void save(Occam occam, Mod349 mod349, AsyncCallback<Mod349> callback);
	void reset(Occam occam, Mod349 mod349, AsyncCallback<Mod349> callback);
	void initialize(Occam occam, int year, Period period, AsyncCallback<Mod349> callback);
	void saveComments(Occam occam, Mod349 mod349,AsyncCallback<Mod349> asyncCallback);
	void changeStatus(Occam occam, Mod349 mod349, FiscalStatus newStatus, AsyncCallback<Mod349> callback);
	void getInfo(Occam occam, Mod349 mod349, Mod349Detail detail, FiscalModelKeyInfo infoKey, AsyncCallback<String> callback);
	void duplicate(Occam occam, Mod349 mod349,AsyncCallback<Mod349> callback);
	void getInvoice(Occam occam, int invoiceId, AsyncCallback<Invoice> asyncCallback);

}
