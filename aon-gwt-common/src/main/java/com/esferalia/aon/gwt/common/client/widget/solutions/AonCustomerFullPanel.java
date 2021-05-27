package com.esferalia.aon.gwt.common.client.widget.solutions;

import java.util.logging.Logger;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.RegistryService;
import com.esferalia.aon.gwt.common.client.RegistryServiceAsync;
import com.esferalia.aon.gwt.common.client.RegistryServiceAsyncDecorator;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.registry.CustomerFull;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.logging.client.ConsoleLogHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.InlineLabel;

public class AonCustomerFullPanel extends AonRegistryFullPanel<CustomerFull> implements Focusable {
	
	private RegistryServiceAsync SERVICE;

	private static final Logger LOGGER = Logger.getLogger(AonCustomerFullPanel.class.getName());
	static {
		LOGGER.addHandler( new ConsoleLogHandler() );
	}

	public AonCustomerFullPanel(AonModuleOptions<?> options, CustomerFull customerFull, AonRegistryFullPanelCallback<CustomerFull> callback) {
		super( options, customerFull, callback);
		
		RegistryServiceAsync serviceRaw = GWT.create(RegistryService.class);
		SERVICE = new RegistryServiceAsyncDecorator(serviceRaw);

		getRootPanel().add(addButtons(options,customerFull,callback));
	}

	@Override
	protected void addExtended(AonModuleOptions<?> options, CustomerFull  customerFull, AonRegistryFullPanelCallback<CustomerFull> callback) {
		addCustomerInfo( options, customerFull);
		addFiscalInfo(options, customerFull);
	}
	
	private void addCustomerInfo(AonModuleOptions<?> options, CustomerFull customerFull) {
		AonDisplayTable displayTab = getNewTab();
		getRootPanel().add(displayTab);
		addScopeRow( displayTab, options, customerFull.ensureCustomer());
		addAccountRow(displayTab, options, customerFull);
	}
	
	private void addFiscalInfo(AonModuleOptions<?> options, CustomerFull customerFull) {
		AonDisplayTable displayTab = getNewTab();
		getRootPanel().add(displayTab);
		
		Customer customer = customerFull.ensureCustomer();
		final CheckBox surcharge = new CheckBox(AON.MSG.surcharge());
		final CheckBox withholding = new CheckBox(AON.MSG.withholding());
		
		FlowPanel taxPanel = new  FlowPanel();
		
		surcharge.setValue(customer.isSurcharge());
		surcharge.setStyleName(AON.CSS.aonMarginRight());
		surcharge.addStyleName(AON.CSS.aonNowrap());
		surcharge.addClickHandler( new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				customer.setSurcharge(surcharge.getValue());
			}
		});
		taxPanel.add(surcharge);
		
		withholding.setValue(customer.isWithholding());
		withholding.setStyleName(AON.CSS.aonMarginRight());
		withholding.addStyleName(AON.CSS.aonNowrap());
		withholding.addClickHandler( new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				customer.setWithholding(withholding.getValue());
			}
		});
		taxPanel.add(withholding);
		
		addBasicRow(displayTab,new InlineLabel(AON.MSG.fiscalInformation()), taxPanel);				
	}

	private FlowPanel addButtons(AonModuleOptions<?> options, CustomerFull customerFull, AonRegistryFullPanelCallback<CustomerFull> callback) {
    	FlowPanel buttons = new FlowPanel();
    	final Button okButton = new Button();
    	buttons.setStyleName(AON.CSS.aonTextCenter());
    	buttons.addStyleName(AON.CSS.aonMarginBottom());
    	buttons.addStyleName(AON.CSS.aonMarginTop());
    	buttons.addStyleName(AON.CSS.aonNowrap());
    	okButton.setStyleName(AON.CSS.aonOkButton());    	
    	okButton.setText( AON.MSG.accept());
    	okButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				okButton.setEnabled(false);
				SERVICE.save(options.getDomainName(), options.getDomain(), options.getUser(), customerFull, new AsyncCallback<CustomerFull>() {

					@Override
					public void onSuccess(CustomerFull result) {
						callback.onAccept(result);
					}

					@Override
					public void onFailure(Throwable caught) {
						okButton.setEnabled(true);
						callback.onError(caught);
					}
				});
			}
		});
    	buttons.add(okButton);
    	
    	final Button cancelButton = new Button();
    	cancelButton.setStyleName(AON.CSS.aonCancelButton());
    	cancelButton.addStyleName(AON.CSS.aonMarginLeft());
    	cancelButton.setText( AON.MSG.cancelAction());
    	cancelButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				cancelButton.setEnabled(false);
				callback.onCancel();
			}
		});
    	buttons.add(cancelButton);
    	
    	return buttons;
    	
	}
	
}

