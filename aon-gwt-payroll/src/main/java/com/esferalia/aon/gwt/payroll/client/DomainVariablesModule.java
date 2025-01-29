package com.esferalia.aon.gwt.payroll.client;

import java.util.Date;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.css.AonGwtTemplateResources;
import com.esferalia.aon.gwt.common.client.css.AonResources;
import com.esferalia.aon.gwt.common.client.css.GWTResources;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.google.gwt.core.client.GWT;

public class DomainVariablesModule extends MainEntryPoint {


	@Override
	public void onModuleLoad() {
		// Inject rich styles.
		AON.ensureInjected();
		GWT.<GWTResources>create(GWTResources.class).css().ensureInjected();
		GWT.<AonResources>create(AonResources.class).css().ensureInjected();
		GWT.<MainEntryPoint.CodeMirrorResources>create(MainEntryPoint.CodeMirrorResources.class).css().ensureInjected();
		GWT.<AonGwtTemplateResources>create(AonGwtTemplateResources.class).css().ensureInjected();

		Date startDate =  DateUtils.addYears2Date(DateUtils.getFirstDayOfYear(), -1);
		Date endDate = DateUtils.addYears2Date(DateUtils.getLastDayOfYear(new Date()), 1);
		DomainSystemVariablesObject domainSystemVariablesObject = new DomainSystemVariablesObject(
				Wnd.getCurrentDomain(), startDate, endDate);
		
		DomainSystemVariables domainSystemVariables = new DomainSystemVariables();
		domainSystemVariables.setTitle("Variables Entorno");
		domainSystemVariables.setVariablesObject(domainSystemVariablesObject);
		

		// Add the domain's variable element to the RootLayoutPanel, so that it will be
		// displayed.
		RootLayoutPanel root = RootLayoutPanel.get(getRootPanel() != null ? getRootPanel() : "rootPanel");
		root.add(domainSystemVariables);
	}

}
