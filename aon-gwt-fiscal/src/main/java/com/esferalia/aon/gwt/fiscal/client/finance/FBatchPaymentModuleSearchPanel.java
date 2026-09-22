package com.esferalia.aon.gwt.fiscal.client.finance;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDateBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomTextBox;
import com.esferalia.aon.gwt.fiscal.client.FinanceService;
import com.esferalia.aon.gwt.fiscal.client.FinanceServiceAsync;
import com.esferalia.aon.gwt.fiscal.client.FinanceServiceAsyncDecorator;
import com.esferalia.aon.occam.api.model.FBatchParams;
import com.esferalia.aon.occam.api.model.finance.FBatch;
import com.esferalia.aon.occam.api.model.registry.RegistryBank;
import com.esferalia.aon.occam.api.model.type.FBatchStatus;
import com.esferalia.aon.occam.api.model.type.FBatchType;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;


public class FBatchPaymentModuleSearchPanel extends HTMLPanel implements HasValueChangeHandlers<FBatchParams>{
	
	// Variables
	
	private static FinanceServiceAsync FINANCE_SERVICE;
	
	private FBatchType fbatchType;

	private AonCustomTextBox description = new AonCustomTextBox("Descripci\u00f3n");
	
	private AonCustomDateBox fromIssueDate = new AonCustomDateBox("F. Desde");
	private AonCustomDateBox toIssueDate = new AonCustomDateBox("F. Hasta");
	
	private AonCustomListBox bank = new AonCustomListBox("Cuenta Bancaria");
	private AonCustomListBox type = new AonCustomListBox("Tipo Fichero");
	
	private AonCustomListBox status = new AonCustomListBox("Estado");
	private AonCustomListBox confidential = new AonCustomListBox("Confidencial");
	
	public static interface IFBatchPanelCallback {
		boolean isSelected(FBatch finance);
	}
	
	// -------------------------------------------------------------------
	// -----------------------  CONSTRUCTOR  -----------------------------
	// -------------------------------------------------------------------
	
	public FBatchPaymentModuleSearchPanel(final FinanceModuleOptions opt, FBatchType fbatchType) {
		super(AonStringUtils.EMPTY);
		addStyleName(AON.CSS.aonFlexColumn());
		getElement().getStyle().setProperty("margin", "0 1rem");
		getElement().getStyle().setProperty("padding", "1rem");
		getElement().getStyle().setProperty("background-color", "rgb(241, 241, 241)");
		getElement().getStyle().setProperty("border-radius", "10px");
		
		this.fbatchType = fbatchType;
		
		FinanceServiceAsync financeServiceRaw = GWT.create(FinanceService.class);
		FINANCE_SERVICE = new FinanceServiceAsyncDecorator(financeServiceRaw);
		
		description.addValueChangeHandler(e -> search(opt));
		fromIssueDate.addValueChangeHandler(e -> search(opt));
		toIssueDate.addValueChangeHandler(e -> search(opt));
		bank.addChangeHandler(e -> search(opt));
		type.addChangeHandler(e -> search(opt));
		status.addChangeHandler(e -> search(opt));
		confidential.addChangeHandler(e -> search(opt));
		
		bank.clearItems();
		bank.addItem("-", "");
		
		FINANCE_SERVICE.getCompanyBanks(opt.getDomainName(), opt.getDomain(), opt.getUser(), new AsyncCallback<LinkedList<RegistryBank>>() {
			
			@Override
			public void onSuccess(LinkedList<RegistryBank> companyBanks) {
				companyBanks.stream().filter(companyBank -> companyBank.isActive()).forEach(companyBank -> {
					bank.addItem("(" + companyBank.getAlias() + ") " + companyBank.getBankAccount().toString(), companyBank.getId().toString());
				});
			}
			
			@Override
			public void onFailure(Throwable arg0) {}
			
		});
		
		type.clearItems();
		
		if(FBatchType.PAYROLL_PAYMENT == fbatchType)
			type.addItem("SEPA 34-14 N\u00f3mina (XML)", "10");
		else if(FBatchType.PAYMENT == fbatchType) {
			type.addItem("Todo", "-1");
			type.addItem("VISA", "0");
			type.addItem("SEPA 34-14 (XML)", "9");
		} else if(FBatchType.CHARGE == fbatchType) {
			type.addItem("Todo", "-1");
			type.addItem("SEPA 19-14 CORE (XML)", "8");
			type.addItem("SEPA 58 ANTICIPO (XML)", "12");
			type.addItem("SEPA 58 COBRO (XML)", "13");
			type.addItem("SEPA 34-14 ABONO (XML)", "14");
		}
		
		status.clearItems();
		status.addItem("-", "");
		
		for(int i=0; i < FBatchStatus.values().length; i++) {
			FBatchStatus fBatchStatus = FBatchStatus.values()[i];
			status.addItem(fBatchStatus.getDescription(), i + "");
		}
		
		confidential.clearItems();
		confidential.addItem("Todo", "");
		confidential.addItem("No Confidencial", "0");
		confidential.addItem("Confidencial", "1");
		
		add(createRow(description, fromIssueDate, toIssueDate, status));
		add(createRow(bank, type, confidential));
		initialize(opt);
	}
	
	private HTMLPanel createRow(Widget ...w) {
		HTMLPanel row = new HTMLPanel(AonStringUtils.EMPTY);
		row.addStyleName(AON.CSS.aonItemFlex());
		
		for(int i=0; i < w.length; i++)
			row.add(w[i]);
		
		return row;
	}
	
	// -------------------------------------------------------------------
	// ---------------------  SEARCH & PARAMS  ---------------------------
	// -------------------------------------------------------------------

	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<FBatchParams> handler) {
		return super.addHandler(handler, ValueChangeEvent.getType());
	}

	private void search(final FinanceModuleOptions opt) {
		ValueChangeEvent.<FBatchParams>fire( FBatchPaymentModuleSearchPanel.this, getParams( opt ) ); 
	}
	
	public void hideDescription() {
		description.setVisible(false);
	}

	public void initialize(final FinanceModuleOptions opt) {
		description.setValue(null);
		fromIssueDate.setValue(null);
		toIssueDate.setValue(null);
		bank.setValue("");
		type.setValue("");
		status.setValue("");
		confidential.setValue("");
	}
	
	public FBatchParams getParams( final FinanceModuleOptions opt) {
		return new FBatchParams()
			.setDomain(opt.getDomain())
			.setDomainName(opt.getDomainName())
			.setDescription(description.getValue())
			.setFromIssueDate(fromIssueDate.getValue())
			.setToIssueDate(toIssueDate.getValue())
			.setRbank(AonStringUtils.isBlank(bank.getValue()) ? null : Integer.parseInt(bank.getValue()))
			.setFbatchType(fbatchType)
			.setType(AonStringUtils.isBlank(type.getValue()) || "-1".equals(type.getValue())
			         ? null
			         : (byte) Integer.parseInt(type.getValue()))
			.setStatus(AonStringUtils.isBlank(status.getValue()) ? null :  (byte) Integer.parseInt(status.getValue()))
			.setConfidential(AonStringUtils.isBlank(confidential.getValue()) ? null : AonStringUtils.equalsIgnoreCase(confidential.getValue(), "1"))
			;
	}
	
}
