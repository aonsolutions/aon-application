package com.esferalia.aon.gwt.payroll.client;

import java.util.List;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.css.AonGwtTemplateResources;
import com.esferalia.aon.gwt.payroll.shared.Enterprise;
import com.esferalia.aon.occam.api.model.Domain;
import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;

public class MainSalaryPrint extends MainEntryPoint {

	// --------------------------------------------------------------------------------------------
	// UiBinder
	// --------------------------------------------------------------------------------------------
	
	interface Binder extends UiBinder<Widget, MainSalaryPrint> {}

	private static final Binder binder = GWT.create(Binder.class);
	
	// --------------------------------------------------------------------------------------------
	// EnterpriseSalaryImpl
	// --------------------------------------------------------------------------------------------
	
	private class EnterpriseSalaryImpl extends EnterpriseSalary {

		@Override
		protected void onBackClick() {
			// Nothing to do here
		}

	}

	// --------------------------------------------------------------------------------------------
	// UiFields
	// --------------------------------------------------------------------------------------------
	
	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {}
	
	@UiField(provided = true)
	EnterpriseSalary enterpriseSalary;

	// --------------------------------------------------------------------------------------------
	// Variables
	// --------------------------------------------------------------------------------------------
		
	private DomainEnterprisesServiceAsync enterprisesService = DomainEnterprisesServiceAsync.newInstance();
	
	// --------------------------------------------------------------------------------------------
	// MainSalaryPrint
	// --------------------------------------------------------------------------------------------
	
	public MainSalaryPrint() {
		enterpriseSalary = new EnterpriseSalaryImpl();
		enterpriseSalary.setSalaryPrintView();
		
		GWT.<AonGwtTemplateResources>create(AonGwtTemplateResources.class).css().ensureInjected();
		AON.ensureInjected();

		Widget ui = binder.createAndBindUi(this);
		RootLayoutPanel.get(getRootPanel() != null ? getRootPanel() : "rootPanel").add(ui);
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
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				
			}
		} );
		
		
	}
	
	private void getEnterprises() {
		enterprisesService.getEnterprises(0, Integer.MAX_VALUE, new AsyncCallback<List<Enterprise>>() {
			
			@Override
			public void onSuccess(List<Enterprise> enterprises) {
				EnterpriseSalaryObject enterpriseSalaryObject = new EnterpriseSalaryObject(enterprises);
				enterpriseSalary.setEnterpriseSalaryObject(enterpriseSalaryObject);
				enterpriseSalary.hideEditSalaryButton();
				enterpriseSalary.setEnterprisesView();
			}
			
			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	private void getEnterprise() {
		DomainEmployeesServiceAsync employeesService = DomainEmployeesServiceAsync.newInstance();
		
		employeesService.getEnterprise(new AsyncCallback<Enterprise>() {
			
			@Override
			public void onSuccess(Enterprise enterprise) {
				EnterpriseSalaryObject enterpriseSalaryObject = new EnterpriseSalaryObject(enterprise);
				enterpriseSalary.setEnterpriseSalaryObject(enterpriseSalaryObject);
				enterpriseSalary.hideEditSalaryButton();
				enterpriseSalary.setEnterpriseView();
				enterpriseSalary.setNewToolbarTitle();
			}
			
			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				
			}
		});
	}

}
