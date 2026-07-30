package com.esferalia.aon.gwt.fiscal.client.finance;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDateBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomNumberBox;
import com.esferalia.aon.gwt.fiscal.client.finance.FBatchPaymentModule.FBATCH_TYPE;
import com.esferalia.aon.occam.api.model.FinanceParams;
import com.esferalia.aon.occam.api.model.finance.FBatch;
import com.esferalia.aon.occam.api.model.type.FinanceStatus;
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
	
	private AonCustomDateBox fromDueDate = new AonCustomDateBox("F. Desde Vto.");
	private AonCustomDateBox toDueDate = new AonCustomDateBox("F. Hasta Vto.");
	private AonCustomNumberBox fromAmount = new AonCustomNumberBox("Desde Importe");
	private AonCustomNumberBox toAmount = new AonCustomNumberBox("Hasta Importe");
	
	private AonCustomListBox paymethodType = new AonCustomListBox("Forma Pago");
	private AonCustomListBox bankSearch = new AonCustomListBox("Banco");
	private AonCustomListBox financeShow = new AonCustomListBox("Mostrar");
	private AonCustomListBox financeType = new AonCustomListBox("T. Vencimento");
	
	private AonCustomDateBox fromInvoiceDate = new AonCustomDateBox("F. Desde Fact.");
	private AonCustomDateBox toInvoiceDate = new AonCustomDateBox("F. Hasta Fact.");
	private AonCustomListBox confidentialFilter = new AonCustomListBox("Confidencial");
	
	private FinanceModuleOptions opt;
	private FBATCH_TYPE fbatchType;
	private FBatch fbatch;
	
	private Set<PayMethodType> payMethods;
	private Map<String, String> accountAliasMap;

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
		
		this.payMethods = new HashSet<PayMethodType>();
		this.accountAliasMap = new HashMap<String, String>();
		
		fromDueDate.addValueChangeHandler(e -> search());
		toDueDate.addValueChangeHandler(e -> search());
		fromAmount.addValueChangeHandler(e -> search());
		toAmount.addValueChangeHandler(e -> search());
		
		paymethodType.addChangeHandler(e -> search());
		bankSearch.addChangeHandler(e -> search());
		financeShow.addChangeHandler(e -> search());
		financeType.addChangeHandler(e -> search());
		
		fromInvoiceDate.addValueChangeHandler(e -> search());
		toInvoiceDate.addValueChangeHandler(e -> search());
		confidentialFilter.addChangeHandler(e -> search());
		
		fromAmount.hideNearBy();
		fromAmount.getNumberBox().setMaxLength(6);
		
		toAmount.hideNearBy();
		toAmount.getNumberBox().setMaxLength(6);
		
		paymethodType.clearItems();
		paymethodType.addItem("-", "");
		
		bankSearch.clearItems();
		bankSearch.addItem("-", "");
		
		financeShow.clearItems();
		financeShow.addItem("Pagos", "1");
		financeShow.addItem("Abonos", "0");
		financeShow.addItem("Pagos y Abonos", "");
		
		financeType.clearItems();
		financeType.addItem(FinanceStatus.PENDING.getDescription(), Byte.toString(FinanceStatus.PENDING.value()));
		financeType.addItem(FinanceStatus.RETURNED.getDescription(), FinanceStatus.RETURNED.value() + "");
		financeType.addItem(FinanceStatus.PENDING.getDescription() + " y " + FinanceStatus.RETURNED.getDescription(), "");
		
		confidentialFilter.clearItems();
		confidentialFilter.addItem("Todos", "");
		confidentialFilter.addItem("NO confidenciales", "0");
		confidentialFilter.addItem("Confidenciales", "1");
		
		if(FBATCH_TYPE.PAYROLL_PAYMENT != fbatchType) {
			add(createRow(fromDueDate, toDueDate, fromAmount, toAmount));
			add(createRow(paymethodType, bankSearch, financeShow, financeType));
			add(createRow(fromInvoiceDate, toInvoiceDate, confidentialFilter));
		} else {
			add(createRow(fromDueDate, toDueDate));
			add(createRow(fromAmount, toAmount));
			add(createRow(paymethodType, bankSearch));
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

	public void setPaymethods(Set<PayMethodType> payMethods) {
		this.payMethods.addAll(payMethods);
		
		paymethodType.clearItems();
		paymethodType.addItem("-", "");
		
		payMethods.forEach(pm -> paymethodType.addItem(pm.getDescription(), Byte.toString(pm.value())));
	}

	public void setBankAccounts(Map<String, String> accountAliasMap) {
		this.accountAliasMap.putAll(accountAliasMap);
		
		bankSearch.clearItems();
		bankSearch.addItem("-", "");
		
		accountAliasMap.entrySet().forEach(entry -> bankSearch.addItem(entry.getValue() + " (" + entry.getKey() + ")", entry.getValue()));
	}
	

	public void initialize() {
		fromDueDate.setValue(null);
		toDueDate.setValue(null);
		fromAmount.setValue(null);
		toAmount.setValue(null);
		
		paymethodType.setValue("");
		bankSearch.setValue("");
		financeShow.setValue("1");
		financeType.setValue(FinanceStatus.PENDING.value() + "");
		
		fromInvoiceDate.setValue(null);
		toInvoiceDate.setValue(null);
		confidentialFilter.setValue("");
		
	}
	
	public FinanceParams getParams() {
		FinanceParams params = new FinanceParams()
				.setDomain(opt.getDomain())
				.setPayment(true)
				.setIsPayroll(FBATCH_TYPE.PAYROLL_PAYMENT == this.fbatchType)
				.setFromDueDate(fromDueDate.getValue())
				.setToDueDate(toDueDate.getValue())
				.setGTAmount(fromAmount.getValue())
				.setLTAmount(toAmount.getValue())
				.setFromInvoiceDate(fromInvoiceDate.getValue())
				.setToInvoiceDate(toInvoiceDate.getValue())
				.setSecurityLevel(AonStringUtils.isBlank(confidentialFilter.getValue()) ? null : SecurityLevel.safeValueOf(confidentialFilter.getValue()))
				;
		
		if(AonStringUtils.isNotBlank(bankSearch.getValue()))
			params.setBankAlias(bankSearch.getValue());
		
		if(AonStringUtils.equals(financeShow.getValue(), "1")) {
			params.setPayment(true);
			if(this.fbatch.getType() != (byte)0)
				params.setPayMethodType(PayMethodType.BANK_TRANSFER);
//			params.setPayMethodType(this.fbatch.getType() == (byte)0 ? PayMethodType.CREDIT_CARD : PayMethodType.BANK_TRANSFER);
		} else if(AonStringUtils.equals(financeShow.getValue(), "0")) {
			params.setCharge(true);
		} else
			params.setPaymentCharge(true);
		
		// Override payMethod if its one selected
		if(AonStringUtils.isNotBlank(paymethodType.getValue()))
			params.setPayMethodType(PayMethodType.safeValueOf(Byte.parseByte(paymethodType.getValue())));
		
		if(AonStringUtils.isBlank(financeType.getValue())) {
			params.setPending(true);
			params.setReturned(true);
		} else if(AonStringUtils.equalsIgnoreCase(financeType.getValue(), Byte.toString(FinanceStatus.PENDING.value()))) {
			params.setPending(true);
		}  else if(AonStringUtils.equalsIgnoreCase(financeType.getValue(), Byte.toString(FinanceStatus.RETURNED.value()))) {
			params.setReturned(true);
		} 
				
		return params;
	}
}
