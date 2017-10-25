package com.esferalia.aon.gwt.fiscal.client.model;

import com.esferalia.aon.occam.api.model.fiscal.IFiscalModel;
import com.google.gwt.view.client.ProvidesKey;

public class FiscalModelProvidesKey<FM extends IFiscalModel> implements ProvidesKey<FM> {
	@Override
	public Object getKey(FM model) {
		return model == null ? null : model.getId();
	}
}

