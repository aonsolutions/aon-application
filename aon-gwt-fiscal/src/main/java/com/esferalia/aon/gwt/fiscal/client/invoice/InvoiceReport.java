package com.esferalia.aon.gwt.fiscal.client.invoice;

import java.util.Date;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.common.client.widget.DateBoxEx;
import com.esferalia.aon.gwt.fiscal.client.MainEntryPoint;
import com.esferalia.aon.gwt.fiscal.shared.IRequestParamsNames;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.CalendarUtil;

public class InvoiceReport extends MainEntryPoint {

	interface InvoiceReportBinder extends UiBinder<Widget, InvoiceReport> {
	}

	private static final InvoiceReportBinder INVOICE_REPORT_BINDER = GWT
			.create(InvoiceReportBinder.class);

	@UiField
	Panel formContainer;
	@UiField
	ListBox entity;

	@UiField
	CheckBox invoiceTypeSales;
	@UiField
	CheckBox invoiceTypePurchases;
	@UiField
	CheckBox invoiceTypeExpenses;
	@UiField
	CheckBox invoiceTypeUndeductible;
	
	@UiField
	CheckBox offerStatusPending;
	@UiField
	CheckBox offerStatusApproved;
	@UiField
	CheckBox offerStatusRefused;
	@UiField
	CheckBox offerStatusBlocked;
	@UiField
	CheckBox offerStatusInvoiced;
	
	@UiField
	CheckBox orderStatusPending;
	@UiField
	CheckBox orderStatusBlocked;
	@UiField
	CheckBox orderStatusServed;
	@UiField
	CheckBox orderStatusClosed;
	@UiField
	CheckBox orderStatusInvoiced;
	
	@UiField
	CheckBox deliveryStatusPending;
	@UiField
	CheckBox deliveryStatusInvoiced;
	
	@UiField(provided=true)
	FormPanel diskForm;
	@UiField
	DateBoxEx fromDate;
	@UiField
	DateBoxEx toDate;
	
	@UiField
	Button generateFileButton;
	
	
	@UiField
	Hidden domainId;
	@UiField
	Hidden domainName;
	@UiField
	Hidden user;

	@Override
	public void onModuleLoad() {
		AON.ensureInjected();
		
		diskForm = new FormPanel("_blank"); 
		
		Widget ui = INVOICE_REPORT_BINDER.createAndBindUi(this);
		
		entity.addItem( AON.MSG.invoices() );
		entity.addItem( AON.MSG.offers() );
		entity.addItem( AON.MSG.purchaseOrders() );
		entity.addItem( AON.MSG.saleOrders() );
		entity.addItem( AON.MSG.incomes() );
		entity.addItem( AON.MSG.deliveries() );
		
		entity.setSelectedIndex(0);
		onChangeEntity(null);
		
		fromDate.getTextBox().setName(IRequestParamsNames.FROM_DATE);
		toDate.getTextBox().setName(IRequestParamsNames.TO_DATE);
		domainId.setName(IRequestParamsNames.DOMAIN_ID);
		domainName.setName(IRequestParamsNames.DOMAIN_NAME);
		user.setName(IRequestParamsNames.USER);
		
		invoiceTypeSales.setName(IRequestParamsNames.INVOICE_TYPE_SALES);
		invoiceTypePurchases.setName(IRequestParamsNames.INVOICE_TYPE_PURCHASES);
		invoiceTypeExpenses.setName(IRequestParamsNames.INVOICE_TYPE_EXPENSES);
		invoiceTypeUndeductible.setName(IRequestParamsNames.INVOICE_TYPE_UNDEDUCTIBLE);
		
		invoiceTypeSales.setValue(true);
		invoiceTypePurchases.setValue(true);
		invoiceTypeExpenses.setValue(true);
		invoiceTypeUndeductible.setValue(true);
		
		offerStatusPending.setName(IRequestParamsNames.OFFER_STATUS_PENDING);
		offerStatusApproved.setName(IRequestParamsNames.OFFER_STATUS_APPROVED);
		offerStatusRefused.setName(IRequestParamsNames.OFFER_STATUS_REFUSED);
		offerStatusBlocked.setName(IRequestParamsNames.OFFER_STATUS_BLOCKED);
		offerStatusInvoiced.setName(IRequestParamsNames.OFFER_STATUS_INVOICED);
		
		offerStatusPending.setValue(true);
		offerStatusApproved.setValue(true);
		offerStatusRefused.setValue(true);
		offerStatusBlocked.setValue(true);
		offerStatusInvoiced.setValue(true);

		orderStatusPending.setName(IRequestParamsNames.ORDER_STATUS_PENDING);
		orderStatusBlocked.setName(IRequestParamsNames.ORDER_STATUS_BLOCKED);
		orderStatusServed.setName(IRequestParamsNames.ORDER_STATUS_SERVED);
		orderStatusClosed.setName(IRequestParamsNames.ORDER_STATUS_CLOSED);
		orderStatusInvoiced.setName(IRequestParamsNames.ORDER_STATUS_INVOICED);
		
		orderStatusPending.setValue(true);
		orderStatusBlocked.setValue(true);
		orderStatusServed.setValue(true);
		orderStatusClosed.setValue(true);
		orderStatusInvoiced.setValue(true);

		deliveryStatusPending.setName(IRequestParamsNames.DELIVERY_STATUS_PENDING);
		deliveryStatusInvoiced.setName(IRequestParamsNames.DELIVERY_STATUS_INVOICED);

		deliveryStatusPending.setValue(true);		
		deliveryStatusInvoiced.setValue(true);
		
		Date date = new Date();
		CalendarUtil.setToFirstDayOfMonth(date);
		fromDate.setValue(date);
		
		date = new Date();
		CalendarUtil.setToFirstDayOfMonth(date);
		CalendarUtil.addMonthsToDate(date, 1);
		CalendarUtil.addDaysToDate(date, -1);
		toDate.setValue( date );
		
		
		// Add the outer panel to the RootLayoutPanel, so that it will be
		// displayed.
		RootLayoutPanel root = RootLayoutPanel.get(getRootPanel() != null ? getRootPanel() : "rootPanel");
		root.add(ui);
	}

	// -------------------------------------------------------------- UiHandler
	@UiHandler("entity")
	void onChangeEntity(ChangeEvent event) {
		boolean invoice = (entity.getSelectedIndex() == 0); 
		boolean offer = (entity.getSelectedIndex() == 1);
		boolean purchase = (entity.getSelectedIndex() == 2);
		boolean sale = (entity.getSelectedIndex() == 3);
		boolean income = (entity.getSelectedIndex() == 4);
		boolean delivery = (entity.getSelectedIndex() == 5);
		
		invoiceTypeSales.setVisible(invoice);
		invoiceTypePurchases.setVisible(invoice);
		invoiceTypeExpenses.setVisible(invoice);
		invoiceTypeUndeductible.setVisible(invoice);
		
		offerStatusPending.setVisible(offer);
		offerStatusApproved.setVisible(offer);
		offerStatusRefused.setVisible(offer);
		offerStatusBlocked.setVisible(offer);
		offerStatusInvoiced.setVisible(offer);
		
		orderStatusPending.setVisible(purchase || sale);
		orderStatusBlocked.setVisible(purchase || sale);
		orderStatusServed.setVisible(purchase || sale);
		orderStatusClosed.setVisible(purchase || sale);
		orderStatusInvoiced.setVisible(purchase || sale);
		
		deliveryStatusPending.setVisible(income || delivery);
		deliveryStatusInvoiced.setVisible(income || delivery);
	}

	@UiHandler("generateFileButton")
	void onGenerateFileButtonClick(ClickEvent event) {
		if (fromDate.getValue() == null) {
			Window.alert("Debe indicar una fecha \"Desde\"");
		} else if (toDate.getValue() == null) {
			Window.alert("Debe indicar una fecha \"Hasta\"");
		} else if (fromDate.getValue().after(toDate.getValue())) {
			Window.alert("Si indica una fecha \"desde\" mayor que la fecha \"hasta\", no obtendr\u00E1 resultados.");
		} else {
			diskForm.setMethod(FormPanel.METHOD_POST);
			diskForm.setEncoding(FormPanel.ENCODING_URLENCODED);
			 

			if (entity.getSelectedIndex() == 0) {
				diskForm.setAction(GWT.getHostPageBaseURL() + "aon_gwt_fiscal/InvoiceReport");	
			} else if (entity.getSelectedIndex() == 1) {
				diskForm.setAction(GWT.getHostPageBaseURL() + "aon_gwt_fiscal/OfferReport");
			} else if (entity.getSelectedIndex() == 2) {
				diskForm.setAction(GWT.getHostPageBaseURL() + "aon_gwt_fiscal/PurchaseOrderReport");
			} else if (entity.getSelectedIndex() == 3) {
				diskForm.setAction(GWT.getHostPageBaseURL() + "aon_gwt_fiscal/SalesOrderReport");
			} else if (entity.getSelectedIndex() == 4) {
				diskForm.setAction(GWT.getHostPageBaseURL() + "aon_gwt_fiscal/IncomeReport");
			} else if (entity.getSelectedIndex() == 5) {
				diskForm.setAction(GWT.getHostPageBaseURL() + "aon_gwt_fiscal/DeliveryReport");
			}
			
			domainId.setValue(String.valueOf(getCurrentDomain()));
			domainName.setValue(getCurrentDomainName());
			user.setValue(getCurrentUser());
			diskForm.submit();
		}
	}

}
