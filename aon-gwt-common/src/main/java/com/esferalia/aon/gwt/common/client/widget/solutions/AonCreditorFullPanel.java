package com.esferalia.aon.gwt.common.client.widget.solutions;

import java.util.logging.Logger;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.InvoiceTransactionListBox;
import com.esferalia.aon.occam.api.model.registry.Creditor;
import com.esferalia.aon.occam.api.model.registry.CreditorFull;
import com.esferalia.aon.occam.api.model.type.RegistryStatus;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.logging.client.ConsoleLogHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextArea;

public class AonCreditorFullPanel extends AonRegistryFullPanel<CreditorFull> implements Focusable {

	private static final Logger LOGGER = Logger.getLogger(AonCreditorFullPanel.class.getName());
	static {
		LOGGER.addHandler( new ConsoleLogHandler() );
	}

	public AonCreditorFullPanel(AonModuleOptions<?> options, CreditorFull creditorFull, AonRegistryFullPanelCallback<CreditorFull> callback) {
		super( options, creditorFull,callback);
	}

	@Override
	protected void addExtended(AonModuleOptions<?> options, CreditorFull creditorFull, AonRegistryFullPanelCallback<CreditorFull> callback) {
		addCreditorInfo( options, creditorFull);
		addFiscalInfo(options, creditorFull);
	}
	
	private void addCreditorInfo(AonModuleOptions<?> options, CreditorFull creditorFull) {
		AonDisplayTable displayTab = getNewTab();
		getRootPanel().add(displayTab);
		addScopeRow( displayTab, options, creditorFull.ensureCreditor());
		addStatusRow(displayTab, options, creditorFull.ensureCreditor());
		addObservationRow(displayTab, options, creditorFull.ensureCreditor());
		addAccountRow(displayTab, options, creditorFull);
	}
	
	private void addStatusRow(AonDisplayTable displayTab, AonModuleOptions<?> options, Creditor ensureCreditor) {
		// ***************************************************************** [REGISTRY STATUS]
		ListBox status = new ListBox();
		for(int i=0; i < RegistryStatus.values().length; i++)
			status.addItem(RegistryStatus.values()[i].getDescription());
		status.setSelectedIndex(ensureCreditor.getStatus().ordinal());
		status.addChangeHandler(e -> ensureCreditor.setStatus(RegistryStatus.safeValueOf(status.getSelectedValue())));
		addBasicRow(displayTab, new InlineLabel(AON.MSG.status()),status);	
	}
	
	private void addObservationRow(AonDisplayTable displayTab, AonModuleOptions<?> options, Creditor ensureCreditor) {
		// ***************************************************************** [REGISTRY OBSERVATION]
		TextArea observation = new TextArea();
		observation.setValue(ensureCreditor.getObservation());
		observation.addValueChangeHandler(e -> ensureCreditor.setObservation(e.getValue()));
		addBasicRow(displayTab, new InlineLabel("Observaciones"), observation);	
	}
	
	private void addFiscalInfo(AonModuleOptions<?> options, CreditorFull creditorFull) {
		AonDisplayTable displayTab = getNewTab();
		getRootPanel().add(displayTab);
		Creditor creditor = creditorFull.ensureCreditor();
		
		final InvoiceTransactionListBox transactionBox = new InvoiceTransactionListBox();
		transactionBox.setValue(creditor.getTransaction());
		transactionBox.addChangeHandler(event -> creditor.setTransaction(transactionBox.getValue()));
		addBasicRow(displayTab,new InlineLabel(AON.MSG.transactionType()), transactionBox);
		
		final CheckBox vatAccualPayment = new CheckBox(AON.MSG.vatAccrualPayment());
		final CheckBox withholding = new CheckBox(AON.MSG.withholding());
		
		FlowPanel taxPanel = new  FlowPanel();
		vatAccualPayment.setValue(creditor.isVatAccrualPayment());
		vatAccualPayment.setStyleName(AON.CSS.aonMarginRight());
		vatAccualPayment.addStyleName(AON.CSS.aonNowrap());
		vatAccualPayment.addClickHandler(event -> creditor.setVatAccrualPayment(vatAccualPayment.getValue()));
		taxPanel.add(vatAccualPayment);
		
		withholding.setValue(creditor.isWithholding());
		withholding.setStyleName(AON.CSS.aonMarginRight());
		withholding.addStyleName(AON.CSS.aonNowrap());
		withholding.addClickHandler(event -> creditor.setWithholding(withholding.getValue()));
		taxPanel.add(withholding);
		
		addBasicRow(displayTab,new InlineLabel(AON.MSG.fiscalInformation()), taxPanel);				
	}

	@Override
	protected void addButtons(AonModuleOptions<?> options, FlowPanel buttons, CreditorFull  creditorFull, AonRegistryFullPanelCallback<CreditorFull> callback) {
		final Button okButton = new Button();
    	okButton.setStyleName(AON.CSS.aonOkButton());
    	okButton.setText( AON.MSG.accept());
    	okButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				okButton.setEnabled(false);
				getService().save(options.getDomainName(), options.getDomain(), options.getUser(), creditorFull, new AsyncCallback<CreditorFull>() {

					@Override
					public void onSuccess(CreditorFull result) {
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
	}
	
	public void setAccountEnabled(boolean enabled) {
		setAccountEnable(enabled);
	}
	
}
