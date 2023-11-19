package com.esferalia.aon.gwt.common.client.widget.solutions;

import java.util.logging.Logger;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.InvoiceTransactionListBox;
import com.esferalia.aon.occam.api.model.registry.Creditor;
import com.esferalia.aon.occam.api.model.registry.CreditorFull;
import com.google.gwt.logging.client.ConsoleLogHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.InlineLabel;

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
		addAccountRow(displayTab, options, creditorFull);
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
	protected void addButtons(AonModuleOptions<?> options, AonToolbar toolbar, CreditorFull  creditorFull, AonRegistryFullPanelCallback<CreditorFull> callback) {
		final AonToolbarButton okButton = new AonToolbarButton(AON.MSG.accept(), AON.CSS.aonIconAccept());
    	okButton.addClickHandler(event -> {
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
