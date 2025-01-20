package com.esferalia.aon.gwt.payroll.client;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.css.AonGwtTemplateResources;
import com.esferalia.aon.gwt.common.client.css.AonResources;
import com.esferalia.aon.gwt.common.client.css.GWTResources;
import com.esferalia.aon.gwt.payroll.shared.Enterprise;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class CostModule extends MainEntryPoint {
	
	private CostWidget costWidget;

	// --------------------------------------------------------------------------------------------
	// CostModule
	// --------------------------------------------------------------------------------------------
	
	public CostModule() {
		// Inject rich styles.
		AON.ensureInjected();
		GWT.<GWTResources>create(GWTResources.class).css().ensureInjected();
		GWT.<AonResources>create(AonResources.class).css().ensureInjected();
		GWT.<MainEntryPoint.CodeMirrorResources>create(MainEntryPoint.CodeMirrorResources.class).css().ensureInjected();
		GWT.<AonGwtTemplateResources>create(AonGwtTemplateResources.class).css().ensureInjected();

		
		costWidget = new CostWidget();
		RootLayoutPanel.get(getRootPanel() != null ? getRootPanel() : "rootPanel").add(costWidget);
	}

	// --------------------------------------------------------------------------------------------
	// onModuleLoad
	// --------------------------------------------------------------------------------------------
	
	@Override
	public void onModuleLoad() {
		DomainEmployeesServiceAsync employeesService = DomainEmployeesServiceAsync.newInstance();
		
		employeesService.getEnterprise(new AsyncCallback<Enterprise>() {
			
			@Override
			public void onSuccess(Enterprise enterprise) {
				costWidget.setEnterprise(enterprise.getId());
			}
			
			@Override
			public void onFailure(Throwable caught) {}
		});
	}
	
}
