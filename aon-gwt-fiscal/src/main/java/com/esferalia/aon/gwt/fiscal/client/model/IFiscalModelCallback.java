package com.esferalia.aon.gwt.fiscal.client.model;

import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;

public interface IFiscalModelCallback<T extends FiscalModel> {
	T getFiscalModel();
	void showErrorMsg(String msg);
	boolean isFinished();
	boolean isDirty();
	void markAsDirty();
	void identificationLabelChanged();
	void showInfoPanel(String text);
}
