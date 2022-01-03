package com.esferalia.aon.gwt.fiscal.client.model;

import com.esferalia.aon.gwt.fiscal.client.FiscalModelModuleOptions;
import com.esferalia.aon.occam.api.model.fiscal.IFiscalModel;

public interface IFiscalModelCallback<T extends IFiscalModel,O extends FiscalModelModuleOptions<T>> {
	O getOptions();
	void onAccept(T model);
	void onCancel(T model);
	void onRemove(T model);
	void onNew();

	void showInfoPanel(String text);
	void cleanInfoPanel();
	void showError(String msg);
	void hideError();
	
}
