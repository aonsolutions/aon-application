package com.esferalia.aon.gwt.common.client.widget.solutions;

import java.util.logging.Logger;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.InvoiceTransactionListBox;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.registry.CustomerFull;
import com.google.gwt.logging.client.ConsoleLogHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;

public class AonCustomerFullPanel extends AonRegistryFullPanel<CustomerFull> implements Focusable {
	
	private static final Logger LOGGER = Logger.getLogger(AonCustomerFullPanel.class.getName());
	static {
		LOGGER.addHandler( new ConsoleLogHandler() );
	}
	
	public AonCustomerFullPanel(AonModuleOptions<?> options, CustomerFull customerFull, AonRegistryFullPanelCallback<CustomerFull> callback) {
		super( options, customerFull, callback);
	}
	
	@Override
	protected void addExtended(AonModuleOptions<?> options, CustomerFull  customerFull, AonRegistryFullPanelCallback<CustomerFull> callback) {
		addCustomerInfo( options, customerFull);
		addFiscalInfo(options, customerFull);
		addDomainInfo(options, customerFull);
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
		
		final InvoiceTransactionListBox transactionBox = new InvoiceTransactionListBox();
		transactionBox.setValue(customer.getTransaction());
		transactionBox.addChangeHandler(event -> customer.setTransaction(transactionBox.getValue()));
		
		addBasicRow(displayTab,new InlineLabel(AON.MSG.transactionType()), transactionBox);

		final CheckBox surcharge = new CheckBox(AON.MSG.surcharge());
		final CheckBox withholding = new CheckBox(AON.MSG.withholding());
		
		FlowPanel taxPanel = new  FlowPanel();
		
		surcharge.setValue(customer.isSurcharge());
		surcharge.setStyleName(AON.CSS.aonMarginRight());
		surcharge.addStyleName(AON.CSS.aonNowrap());
		surcharge.addClickHandler(event -> customer.setSurcharge(surcharge.getValue()));
		taxPanel.add(surcharge);
		
		withholding.setValue(customer.isWithholding());
		withholding.setStyleName(AON.CSS.aonMarginRight());
		withholding.addStyleName(AON.CSS.aonNowrap());
		withholding.addClickHandler(event -> customer.setWithholding(withholding.getValue()));
		taxPanel.add(withholding);
		
		addBasicRow(displayTab,new InlineLabel(AON.MSG.fiscalInformation()), taxPanel);				
	}

	private void addDomainInfo(AonModuleOptions<?> options, CustomerFull customerFull) {
		if (customerFull != null && customerFull.getId() != null) {
			AonDisplayTable displayTab = getNewTab();
			getRootPanel().add(displayTab);
			FlowPanel domainPanel = new  FlowPanel();
			Label domainLabel = new Label();
			domainPanel.add(domainLabel);
			addBasicRow(displayTab,new InlineLabel("Dominio vinculado"), domainPanel);				

			getService().getDomainLinked(options.getDomainName(), options.getDomain(), options.getUser(), customerFull.getId(), new AsyncCallback<Domain>() {
				
				@Override
				public void onSuccess(Domain domain) {
					if (domain != null) {
						domainLabel.setText( domain.getId() + " - " + domain.getName()); 
					} else {
						domainLabel.setText( "Sin dominio vinculado");
					}
				}
				
				@Override
				public void onFailure(Throwable caught) {
					domainLabel.setText( caught.getMessage() );
				}
			});
		}
	}

	@Override
	protected void addButtons(AonModuleOptions<?> options, AonToolbar toolbar, CustomerFull customerFull, AonRegistryFullPanelCallback<CustomerFull> callback) {
    	final AonToolbarButton okButton = new AonToolbarButton(AON.MSG.accept(), AON.CSS.aonIconAccept());
    	okButton.addClickHandler(event -> {
			okButton.setEnabled(false);
			getService().save(options.getDomainName(), options.getDomain(), options.getUser(), customerFull, new AsyncCallback<CustomerFull>() {

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
		});
    	toolbar.add(okButton);
    	
    	final AonToolbarButton cancelButton = new AonToolbarButton(AON.MSG.cancelAction(), AON.CSS.aonIconCancel());
    	cancelButton.addClickHandler(event -> {
			cancelButton.setEnabled(false);
			callback.onCancel();
		});
    	toolbar.add(cancelButton);
	}

	public void setAccountEnabled(boolean enabled) {
		setAccountEnable(enabled);
	}
	
}
