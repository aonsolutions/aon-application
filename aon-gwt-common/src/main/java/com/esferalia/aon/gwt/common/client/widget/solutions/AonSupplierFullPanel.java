package com.esferalia.aon.gwt.common.client.widget.solutions;

import java.util.logging.Logger;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.InvoiceTransactionListBox;
import com.esferalia.aon.occam.api.model.registry.Supplier;
import com.esferalia.aon.occam.api.model.registry.SupplierFull;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.logging.client.ConsoleLogHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.InlineLabel;

public class AonSupplierFullPanel extends AonRegistryFullPanel<SupplierFull> implements Focusable {
	
	private static final Logger LOGGER = Logger.getLogger(AonSupplierFullPanel.class.getName());
	static {
		LOGGER.addHandler( new ConsoleLogHandler() );
	}
	
	public AonSupplierFullPanel(AonModuleOptions<?> options, SupplierFull supplierFull, AonRegistryFullPanelCallback<SupplierFull> callback) {
		super(options, supplierFull,callback);
	}


	@Override
	protected void addExtended(AonModuleOptions<?> options, SupplierFull supplierFull, AonRegistryFullPanelCallback<SupplierFull> callback) {
		addSupplierInfo( options, supplierFull);
		addFiscalInfo(options, supplierFull);
	}
	
	private void addSupplierInfo(AonModuleOptions<?> options, SupplierFull supplierFull) {
		AonDisplayTable displayTab = getNewTab();
		getRootPanel().add(displayTab);
		addScopeRow( displayTab, options, supplierFull.ensureSupplier());
		addAccountRow(displayTab, options, supplierFull);
	}
	
	private void addFiscalInfo(AonModuleOptions<?> options, SupplierFull supplierFull) {
		AonDisplayTable displayTab = getNewTab();
		getRootPanel().add(displayTab);
		Supplier supplier = supplierFull.ensureSupplier();

		final InvoiceTransactionListBox transactionBox = new InvoiceTransactionListBox();
		transactionBox.setValue(supplier.getTransaction());
		transactionBox.addChangeHandler(event -> supplier.setTransaction(transactionBox.getValue()));
		addBasicRow(displayTab,new InlineLabel(AON.MSG.transactionType()), transactionBox);

		final CheckBox vatAccualPayment = new CheckBox(AON.MSG.vatAccrualPayment());
		final CheckBox withholdingFarmer = new CheckBox(AON.MSG.withholdingFarmer());
		final CheckBox withholding = new CheckBox(AON.MSG.withholding());
		
		FlowPanel taxPanel = new  FlowPanel();
		FlowPanel taxPanel0 = new  FlowPanel();
		taxPanel.add(taxPanel0);
		vatAccualPayment.setValue(supplier.isVatAccrualPayment());
		vatAccualPayment.setStyleName(AON.CSS.aonMarginRight());
		vatAccualPayment.addStyleName(AON.CSS.aonNowrap());
		vatAccualPayment.addClickHandler(event -> supplier.setVatAccrualPayment(vatAccualPayment.getValue()));
		taxPanel0.add(vatAccualPayment);
		
		withholdingFarmer.setValue(supplier.isWithholdingFarmer());
		withholdingFarmer.setStyleName(AON.CSS.aonMarginRight());
		withholdingFarmer.addStyleName(AON.CSS.aonNowrap());
		withholdingFarmer.addClickHandler(event -> supplier.setWithholdingFarmer(withholdingFarmer.getValue()));
		taxPanel0.add(withholdingFarmer);
		
		FlowPanel taxPanel1 = new  FlowPanel();
		taxPanel.add(taxPanel1);
		withholding.setValue(supplier.isWithholding());
		withholding.setStyleName(AON.CSS.aonMarginRight());
		withholding.addStyleName(AON.CSS.aonNowrap());
		withholding.addClickHandler(event -> supplier.setWithholding(withholding.getValue()));
		taxPanel1.add(withholding);
		
		addBasicRow(displayTab, new InlineLabel(AON.MSG.fiscalInformation()), taxPanel);				
	}
	
	@Override
	protected void addButtons(AonModuleOptions<?> options, FlowPanel buttons, SupplierFull supplierFull, AonRegistryFullPanelCallback<SupplierFull> callback) {
		final Button okButton = new Button();
    	okButton.setStyleName(AON.CSS.aonOkButton());
    	okButton.setText( AON.MSG.accept());
    	okButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				okButton.setEnabled(false);
				getService().save(options.getDomainName(), options.getDomain(), options.getUser(), supplierFull, new AsyncCallback<SupplierFull>() {
					@Override
					public void onSuccess(SupplierFull result) {
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
