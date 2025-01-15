package com.esferalia.aon.gwt.payroll.client;

import java.util.List;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.css.AonGwtTemplateResources;
import com.esferalia.aon.gwt.common.client.css.AonResources;
import com.esferalia.aon.gwt.common.client.css.GWTResources;
import com.esferalia.aon.gwt.payroll.shared.Enterprise;
import com.esferalia.aon.occam.api.model.Domain;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class MainSalaryPrint extends MainEntryPoint {
	
	private SalaryWidget enterpriseSalary;

	// --------------------------------------------------------------------------------------------
	// Variables
	// --------------------------------------------------------------------------------------------
		
	private DomainEnterprisesServiceAsync enterprisesService = DomainEnterprisesServiceAsync.newInstance();
	
	// --------------------------------------------------------------------------------------------
	// MainSalaryPrint
	// --------------------------------------------------------------------------------------------
	
	public MainSalaryPrint() {
		// Inject rich styles.
		AON.ensureInjected();
		GWT.<GWTResources>create(GWTResources.class).css().ensureInjected();
		GWT.<AonResources>create(AonResources.class).css().ensureInjected();
		GWT.<MainEntryPoint.CodeMirrorResources>create(MainEntryPoint.CodeMirrorResources.class).css().ensureInjected();
		GWT.<AonGwtTemplateResources>create(AonGwtTemplateResources.class).css().ensureInjected();

		
		enterpriseSalary = new SalaryWidget();
		RootLayoutPanel.get(getRootPanel() != null ? getRootPanel() : "rootPanel").add(enterpriseSalary);
	}

	// --------------------------------------------------------------------------------------------
	// onModuleLoad
	// --------------------------------------------------------------------------------------------
	
	@Override
	public void onModuleLoad() {
		enterprisesService.getDomainDetails(new AsyncCallback<Domain>() {
			
			@Override
			public void onSuccess(Domain domain) {
				if(domain.isParent()) {
					getEnterprises();
				} else {
					getEnterprise();
				}
			}
			
			@Override
			public void onFailure(Throwable caught) {}
			
		});
	}
	
	private void getEnterprises() {
		enterprisesService.getEnterprises(0, Integer.MAX_VALUE, new AsyncCallback<List<Enterprise>>() {
			
			@Override
			public void onSuccess(List<Enterprise> enterprises) {
				enterpriseSalary.setEnterprisesManagement();
				enterpriseSalary.loadSalaries();
			}
			
			@Override
			public void onFailure(Throwable caught) {}
		});
	}
	
	private void getEnterprise() {
		DomainEmployeesServiceAsync employeesService = DomainEmployeesServiceAsync.newInstance();
		
		employeesService.getEnterprise(new AsyncCallback<Enterprise>() {
			
			@Override
			public void onSuccess(Enterprise enterprise) {
				enterpriseSalary.setIsEnterprise();
				enterpriseSalary.loadSalaries();
			}
			
			@Override
			public void onFailure(Throwable caught) {}
		});
	}

}
