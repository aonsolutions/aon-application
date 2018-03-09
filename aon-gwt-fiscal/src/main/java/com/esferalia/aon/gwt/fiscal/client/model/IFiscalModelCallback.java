package com.esferalia.aon.gwt.fiscal.client.model;

import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;

public interface IFiscalModelCallback<T extends FiscalModel> {
	String getDomainName();
	String getUser();
	int getDomain();
	T getFiscalModel();

	boolean isFinished();
	boolean isDirty();
	void markAsDirty();
	void identificationLabelChanged();

	void onAccept();
	void onCancel();
	
	void showErrorMsg(String msg);
	void showVisorAEAT();
	void showInfoPanel(String text);
	
}
