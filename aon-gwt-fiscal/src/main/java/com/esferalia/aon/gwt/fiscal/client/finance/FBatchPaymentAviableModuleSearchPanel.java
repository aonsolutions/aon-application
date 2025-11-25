package com.esferalia.aon.gwt.fiscal.client.finance;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomCheckBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDateBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomNumberBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomTextBox;
import com.esferalia.aon.gwt.fiscal.client.finance.FBatchPaymentModule.FBATCH_TYPE;
import com.esferalia.aon.occam.api.model.FinanceParams;
import com.esferalia.aon.occam.api.model.finance.FBatch;
import com.esferalia.aon.occam.api.model.type.PayMethodType;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;


public class FBatchPaymentAviableModuleSearchPanel extends HTMLPanel implements HasValueChangeHandlers<FinanceParams>{
	
	// Variables
	
	private AonCustomNumberBox amount = new AonCustomNumberBox("Importe");
	private AonCustomCheckBox nearbyNumbers = new AonCustomCheckBox("Cant. Cercanas");
	private AonCustomTextBox concept = new AonCustomTextBox("Concepto");
	
	private AonCustomDateBox fromDueDate = new AonCustomDateBox("F. Desde Vto.");
	private AonCustomDateBox toDueDate = new AonCustomDateBox("F. Hasta Vto.");
	private AonCustomListBox confidentialFilter = new AonCustomListBox("Confidencial");
	
	private AonCustomDateBox fromInvoiceDate = new AonCustomDateBox("F. Desde Fact.");
	private AonCustomDateBox toInvoiceDate = new AonCustomDateBox("F. Hasta Fact.");
	private AonCustomTextBox referenceCode = new AonCustomTextBox("C\u00f3digo");
	
	private AonCustomTextBox bankSearch = new AonCustomTextBox("C\u00f3digo Banco");
	private AonCustomCheckBox charge = new AonCustomCheckBox("Incl. Cobros");
	private AonCustomTextBox registry = new AonCustomTextBox("Titular");
	
//	private AonAccountingRegistryBox registryBox;
	
	private FinanceModuleOptions opt;
	private FBATCH_TYPE fbatchType;
	private FBatch fbatch;

	// -------------------------------------------------------------------
	// -----------------------  CONSTRUCTOR  -----------------------------
	// -------------------------------------------------------------------
	
	public FBatchPaymentAviableModuleSearchPanel(FinanceModuleOptions opt, FBATCH_TYPE fbatchType, FBatch fbatch) {
		super(AonStringUtils.EMPTY);
		addStyleName(AON.CSS.aonFlexColumn());
		getElement().getStyle().setProperty("margin", "0 1rem");
		getElement().getStyle().setProperty("padding", "1rem");
		getElement().getStyle().setProperty("background-color", "rgb(241, 241, 241)");
		getElement().getStyle().setProperty("border-radius", "10px");
		
		this.opt = opt;
		this.fbatchType = fbatchType;
		this.fbatch = fbatch;
		
//		registryBox = new AonAccountingRegistryBox(opt, true);
		
		amount.addValueChangeHandler(e -> search());
		nearbyNumbers.addValueChangeHandler(e -> search());
		concept.addValueChangeHandler(e -> search());
		fromDueDate.addValueChangeHandler(e -> search());
		toDueDate.addValueChangeHandler(e -> search());
		confidentialFilter.addChangeHandler(e -> search());
		fromInvoiceDate.addValueChangeHandler(e -> search());
		toInvoiceDate.addValueChangeHandler(e -> search());
		referenceCode.addValueChangeHandler(e -> search());
		bankSearch.addValueChangeHandler(e -> search());
		charge.addValueChangeHandler(e -> search());
		registry.addValueChangeHandler(e -> search());
//		registryBox.addValueChangeHandler(e -> search());
		
		amount.hideNearBy();
		amount.getNumberBox().setMaxLength(6);
		
		confidentialFilter.clearItems();
		confidentialFilter.addItem("Todos", "");
		confidentialFilter.addItem("NO confidenciales", "0");
		confidentialFilter.addItem("Confidenciales", "1");
		
		bankSearch.getTextBox().setMaxLength(4);
		
		if(FBATCH_TYPE.PAYROLL_PAYMENT != fbatchType) {
			add(createRow(fromDueDate, toDueDate, amount, bankSearch));
			add(createRow(fromInvoiceDate, toInvoiceDate, charge));
//			add(createRow(bankSearch, charge, registry));
		} else {
			add(createRow(fromDueDate, toDueDate));
			add(createRow(amount, nearbyNumbers));
		}
		
		initialize();
		
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
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<FinanceParams> handler) {
		return super.addHandler(handler, ValueChangeEvent.getType());
	}

	private void search() {
		ValueChangeEvent.<FinanceParams>fire( FBatchPaymentAviableModuleSearchPanel.this, getParams() ); 
	}

	public void initialize() {
		amount.setValue(null);
		nearbyNumbers.setValue(false);
		concept.setValue(null);
		
		fromDueDate.setValue(null);
		toDueDate.setValue(null);
		confidentialFilter.setValue("");
		
		fromInvoiceDate.setValue(null);
		toInvoiceDate.setValue(null);
		referenceCode.setValue(null);
		
		bankSearch.setValue(null);
		charge.setValue(false);
		registry.setValue(null);
//		registryBox.setValue( (AccountingRegistry) null, false);
		
	}
	
	public FinanceParams getParams() {
		FinanceParams params = new FinanceParams()
				.setDomain(opt.getDomain())
				.setPayment(true)
				.setPending(true)
				.setIsPayroll(FBATCH_TYPE.PAYROLL_PAYMENT == this.fbatchType)
				.setPayMethodType(this.fbatch.getType() == (byte)0 ? PayMethodType.CREDIT_CARD : PayMethodType.BANK_TRANSFER)
				
				.setAmount((amount.getValue() != null && amount.getValue() != 0) ? amount.getValue() : null)
				.setNearbyNumbers(nearbyNumbers.getValue())
				.setConcept(concept.getValue())
				
				.setFromDueDate(fromDueDate.getValue())
				.setToDueDate(toDueDate.getValue())
				.setSecurityLevel(AonStringUtils.isBlank(confidentialFilter.getValue()) ? null : SecurityLevel.safeValueOf(confidentialFilter.getValue()))
				
				.setFromInvoiceDate(fromInvoiceDate.getValue())
				.setToInvoiceDate(toInvoiceDate.getValue())
				.setReferenceCode(referenceCode.getValue())
				
				.setBackRef(bankSearch.getValue())
				.setIncludeCharges(charge.getValue())
				.setDescription(registry.getValue())
//				.setRegistry(registryBox.getId())
				;
		
		if(charge.getValue()) params.setPayMethodType(PayMethodType.NEGOTIABLE_DOCUMENT);
		
		return params;
	}
	
}
