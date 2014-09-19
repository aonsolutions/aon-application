package com.esferalia.aon.gwt.payroll.client;

import java.util.List;

import com.esferalia.aon.gwt.common.shared.EvalException;
import com.esferalia.aon.gwt.payroll.client.FxDialog.IContextProvider;
import com.esferalia.aon.gwt.payroll.shared.ContextDescriptor;
import com.esferalia.aon.gwt.payroll.shared.Deduction;
import com.esferalia.aon.gwt.payroll.shared.Result;
import com.esferalia.aon.gwt.payroll.shared.Variable;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;

public class DeductionEditor extends ResizeComposite {
	
	interface Binder extends UiBinder<Widget, DeductionEditor> {

	}
	private static final Binder binder = GWT.create(Binder.class);
	
	
	@UiField 
	com.esferalia.aon.gwt.payroll.client.Deduction deductionUI;
	
	private EnterprisesServiceAsync enterprisesService;
	
	public DeductionEditor() {
		initWidget(binder.createAndBindUi(this));
		initEnterprisesService();
		initContextProvider();
	}
	
	public void setDeduction(Deduction deduction) {
		dumpDeduction(deduction);
		
	}
	
	// ------------------------------------------------------------- UIHandlers
	@UiHandler("acceptButton")
	public void onAcceptClick(ClickEvent event){
		
	}
	
	@UiHandler("deleteButton")
	public void onDeleteClick(ClickEvent event){
		
	}
	// ------------------------------------------------------------------------ 
	
	private void dumpDeduction(Deduction deduction) {
		deductionUI.setName(deduction.getName());
		deductionUI.setType(deduction.getType());
		deductionUI.setDescription(deduction.getDescription());
		deductionUI.setExpression(deduction.getExpression());
	}
	
	private void initEnterprisesService() {
		// Create a remote service proxy to talk to the server-side Enterprises
		// service.
		EnterprisesServiceAsync enterprisesServiceRaw = GWT
				.create(EnterprisesService.class);
		enterprisesService = new EnterprisesServiceAsyncDecorator(
				enterprisesServiceRaw);
	}
	
	private void initContextProvider() {
		
		class ContextProvider implements IContextProvider{
			@Override
			public boolean isEditable(String name) {
				// TODO Auto-generated method stub
				return true;
			}
			@Override
			public void eval(String expression, List<Variable> vars,
					AsyncCallback<List<Result>> callback) {
				callback.onFailure(new EvalException());
			}
			
			@Override
			public void getContext(AsyncCallback<ContextDescriptor> callback) {
				DeductionEditor.this.enterprisesService.getContext(callback);
			}
		}
		
		deductionUI.setContextProvider(new ContextProvider());
	}
	
	
	
}
