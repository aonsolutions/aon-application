package com.esferalia.aon.gwt.payroll.client;

import com.esferalia.aon.gwt.payroll.client.FxDialog.IContextProvider;
import com.esferalia.aon.gwt.payroll.shared.Bonus;
import com.esferalia.aon.gwt.payroll.shared.ContextDescriptor;
import com.esferalia.aon.gwt.payroll.shared.EvalException;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;

public class BonusEditor extends ResizeComposite {
	
	interface Binder extends UiBinder<Widget, BonusEditor> {

	}
	private static final Binder binder = GWT.create(Binder.class);
	
	
	@UiField 
	com.esferalia.aon.gwt.payroll.client.Bonus bonusUI;
	
	private EnterprisesServiceAsync enterprisesService;
	
	public BonusEditor() {
		initWidget(binder.createAndBindUi(this));
		initEnterprisesService();
		initContextProvider();
	}
	
	public void setBonus(Bonus bonus) {
		dumpBonus(bonus);
	}
	
	// ------------------------------------------------------------------------
	
	// ------------------------------------------------------------------------ 
	
	private void dumpBonus(Bonus bonus) {
		bonusUI.setName(bonus.getName());
		bonusUI.setType(bonus.getType());
		bonusUI.setDescription(bonus.getDescription());
		bonusUI.setExpression(bonus.getExpression());
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
			public void eval(String expression, AsyncCallback<Double> callback) {
				callback.onFailure(new EvalException());
			}
			
			@Override
			public void getContext(AsyncCallback<ContextDescriptor> callback) {
				BonusEditor.this.enterprisesService.getContext(callback);
			}
		}
		
		bonusUI.setContextProvider(new ContextProvider());
	}
	
	
	
}
