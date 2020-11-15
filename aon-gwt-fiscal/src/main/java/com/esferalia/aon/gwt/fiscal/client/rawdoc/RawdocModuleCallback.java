package com.esferalia.aon.gwt.fiscal.client.rawdoc;

import com.esferalia.aon.gwt.fiscal.client.FinanceServiceAsync;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.google.gwt.user.client.ui.Widget;

public interface RawdocModuleCallback {
	
	RawdocModuleOptions getOptions();
	
	FinanceServiceAsync getFinanceService();
	
	void updateAndRefresh(Finance finance);
	
	void addExtraInfo(Widget widget);
}
