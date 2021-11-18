package com.esferalia.aon.gwt.fiscal.client.model;

import java.util.LinkedList;

import com.esferalia.aon.gwt.fiscal.client.FiscalModelModuleOptions;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.Mod111;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface IFiscalModelCallback<T extends FiscalModel,O extends FiscalModelModuleOptions<T>> {
	O getOptions();
	void onAccept(T model);
	void onCancel(T model);
	void onRemove(T model);
	void onNew();
	void onReset(T oldModel);

	void showInfoPanel(String text);
	void cleanInfoPanel();
	void showError(String msg);
	
}
