package com.esferalia.aon.gwt.fiscal.client.customer;

import java.io.Serializable;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.logging.Logger;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.fiscal.client.MainEntryPoint;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceStatus;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.logging.client.ConsoleLogHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.SimplePanel;

import net.aonsolutions.gwt.pdfjs.client.FullViewer;

public class CustomerInvoiceModule extends MainEntryPoint {
	
	// -------- CustomerInvoiceRow
	
	private class CustomerInvoiceRow implements Serializable {

		private static final long serialVersionUID = 934794938928921248L;
		
		private HTMLPanel row;
		private AonTableButton openDocument;
		private AonTableButton closeDocument;
		
		public CustomerInvoiceRow(HTMLPanel row, AonTableButton openDocument, AonTableButton closeDocument) {
			super();
			this.row = row;
			this.openDocument = openDocument;
			this.closeDocument = closeDocument;
		}

		public HTMLPanel getRow() {
			return row;
		}

		public AonTableButton getOpenDocument() {
			return openDocument;
		}

		public AonTableButton getCloseDocument() {
			return closeDocument;
		}
		
	}

	private static final Logger LOGGER = Logger.getLogger(CustomerInvoiceModule.class.getName());
	
	static { LOGGER.addHandler(new ConsoleLogHandler()); }
	
	private DateTimeFormat formatDate = DateTimeFormat.getFormat("dd/MM/yyyy");

	private static CommonServiceAsync COMMON_SERVICE;
	
	private RootLayoutPanel root;
	private DockLayoutPanel dockPanel;
	
	private SimplePanel tableContainer;
	private ScrollPanel tableScrollPanel;
	private AonCustomTable tab;
	
	private FullViewer viewer;
	
	private Integer customerId;
	
	private Map<Integer, CustomerInvoiceRow> rows = new HashMap<Integer, CustomerInvoiceRow>();
	
	private static enum COLS {
		  STA(AonStringUtils.EMPTY					,"2rem" 			,"white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, DAT("Fecha"								,"5rem"  			,"white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, NUM("N. Factura"							,"-moz-available"  	,"min-width: 5rem; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, BAS("Base"								,"5.5rem" 			,"white-space: nowrap; overflow: hidden; text-overflow: ellipsis; text-align: right;")
		, IVA("IVA"									,"5.5rem" 			,"white-space: nowrap; overflow: hidden; text-overflow: ellipsis; text-align: right;")
		, IRP("IRPF"								,"5.5rem" 			,"white-space: nowrap; overflow: hidden; text-overflow: ellipsis; text-align: right;")
		, TOT("Total"								,"5.5rem" 			,"white-space: nowrap; overflow: hidden; text-overflow: ellipsis; text-align: right;")
		, PAY("F. Pago"								,"10rem" 			,"white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, VEN("Vto."								,"5rem" 			,"white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, IMP("Importe."							,"5.5rem" 			,"white-space: nowrap; overflow: hidden; text-overflow: ellipsis; text-align: right;")
		, EST("Estado"								,"5.5rem" 			,"white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, BUT(AonStringUtils.EMPTY					,"3rem" 			,"")
		;

		String headerLabel;
		String colWidth;
		String cellStyleClass;

		private COLS(String headerLabel,String colWidth,String cellStyleClass) {
			this.headerLabel = headerLabel;
			this.colWidth = colWidth;
			this.cellStyleClass = cellStyleClass;
		}
		public String getColWidth() {
			return colWidth;
		}
		public String getHeaderLabel() {
			return headerLabel;
		}
		public String getCellStyleClass() {
			return cellStyleClass;
		}
	}

	@Override
	public void onModuleLoad() {
		root = RootLayoutPanel.get(getRootPanel() != null ? getRootPanel() : "rootPanel");

		CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
		COMMON_SERVICE = new CommonServiceAsyncDecorator(commonServiceRaw);
		
		// Get customer from LS
		customerId = getCustomer() > 0 ? getCustomer() : null;
		
		// Remove customer from LS
		removeCustomer();
		
		moduleLoad();
	}

	public void moduleLoad() {
		AON.ensureInjected();

		dockPanel = new DockLayoutPanel(Unit.PCT);
		viewer = new FullViewer();
		
		tableContainer = new SimpleLayoutPanel();
		tableContainer.setHeight("100%");
		tableContainer.getElement().getStyle().setProperty("margin-left", "1rem");
		
		dockPanel.addEast(viewer, 0);
		dockPanel.add(tableContainer);
		
		createInvoiceList();
		
		root.add(dockPanel);
	}

	private void createInvoiceList() {
		getInvoices(invoices -> {
			tableContainer.clear();
			
			if(!invoices.isEmpty()) {
				tab = new AonCustomTable();
				tableScrollPanel = new ScrollPanel(tab);
				
				paintHeader();
				tableContainer.add(tableScrollPanel);
				
				invoices.sort(Comparator.comparing(Invoice::getIssueDate,
				        Comparator.nullsFirst(Comparator.reverseOrder())));
				
				invoices.forEach(invoice -> paintRow(invoice));
			} else {
				tableContainer.add(new Label("No existen facturas para este cliente"));
			}
			
		});
	}

	private void paintHeader() {
		tab.createHeader();
		for ( COLS col : COLS.values()) 
			tab.addHeader(new Label(col.getHeaderLabel()), col.getColWidth(), col.getCellStyleClass());
	}
	
	private void paintRow(Invoice invoice) {
		HTMLPanel row = tab.createRow();
		
		FlowPanel buttonContainer = new FlowPanel();
		buttonContainer.getElement().getStyle().setTextAlign(TextAlign.CENTER);
		
		AonTableButton openDocument = new AonTableButton("Ver documento", AON.CSS.aonIconVisibility());
		AonTableButton closeDocument = new AonTableButton("Cerrar documento", AON.CSS.aonIconVisibilityOff());
		
		openDocument.addStyleName(AON.CSS.aonCustomRowButtom());
		openDocument.addClickHandler(event -> {
			event.stopPropagation();
			openPDF(row, invoice.getId(), openDocument, closeDocument);
		});
		buttonContainer.add(openDocument);
		
		closeDocument.addStyleName(AON.CSS.aonCustomRowButtom());
		closeDocument.setVisible(false);
		closeDocument.addClickHandler(event -> {
			event.stopPropagation();
			closePDF(row, openDocument, closeDocument);
		});
		buttonContainer.add(closeDocument);
		
		row.addDomHandler(e -> {
			if(openDocument.isVisible()) openPDF(row, invoice.getId(), openDocument, closeDocument);
			else closePDF(row, openDocument, closeDocument);
		}, ClickEvent.getType());
		
		AonTableButton status = new AonTableButton(
				invoice.isRecorded() ? InvoiceStatus.SCORED.getName() : InvoiceStatus.PENDING.getName(), 
				invoice.isRecorded() ? AON.CSS.aonIconCheckCircleGreen() : AON.CSS.aonIconErrorExclamation());
		tab.addRow(row, status, COLS.STA.getColWidth());
		
		Label date = new Label(null == invoice.getIssueDate() ? "" : formatDate.format(invoice.getIssueDate()));
		tab.addInlineStyle(date, COLS.DAT.getCellStyleClass());
		tab.addRow(row, date, COLS.DAT.getColWidth());
		
		String referenceAux = "";
		if(!AonStringUtils.isBlank(invoice.getSeries())) referenceAux = referenceAux + invoice.getSeries() + "/";
		
		Label number = new Label(invoice.getNumber() > 0  ? invoice.getReferenceCode() : referenceAux);
		tab.addInlineStyle(number, COLS.NUM.getCellStyleClass());
		tab.addRow(row, number, COLS.NUM.getColWidth());
		
		Label base = new Label(formatToEuro(invoice.getTaxableBase()));
		tab.addInlineStyle(base, COLS.BAS.getCellStyleClass());
		tab.addRow(row, base, COLS.BAS.getColWidth());
		
		Label iva = new Label(formatToEuro(invoice.getVatQuota()));
		tab.addInlineStyle(iva, COLS.IVA.getCellStyleClass());
		tab.addRow(row, iva, COLS.IVA.getColWidth());
		
		Label irpf = new Label(formatToEuro(invoice.getRetentionQuota()));
		tab.addInlineStyle(irpf, COLS.IRP.getCellStyleClass());
		tab.addRow(row, irpf, COLS.IRP.getColWidth());
		
		Label total = new Label(formatToEuro(invoice.getTotal()));
		tab.addInlineStyle(total, COLS.TOT.getCellStyleClass());
		tab.addRow(row, total, COLS.TOT.getColWidth());
		
		// Finance
		Finance finance = null;
		if(invoice.hasFinances()) finance = invoice.financeStream().findFirst().get();
		
		Label payMethod = new Label(null == finance ? "" : finance.getPayMethodName());
		tab.addInlineStyle(payMethod, COLS.PAY.getCellStyleClass());
		tab.addRow(row, payMethod, COLS.PAY.getColWidth());
		
		Label vto = new Label(null == finance || null == finance.getDueDate() ? "" : formatDate.format(finance.getDueDate()));
		tab.addInlineStyle(vto, COLS.VEN.getCellStyleClass());
		tab.addRow(row, vto, COLS.VEN.getColWidth());
		
		Label amount = new Label(null == finance ? "" : formatToEuro(finance.getAmount() + finance.getExpenses()));
		tab.addInlineStyle(amount, COLS.IMP.getCellStyleClass());
		tab.addRow(row, amount, COLS.IMP.getColWidth());
		
		Label statusFinance = new Label(null == finance || finance.getFinanceStatus() == null ? "" : finance.getFinanceStatus().getDescription());
		tab.addInlineStyle(statusFinance, COLS.IMP.getCellStyleClass());
		tab.addRow(row, statusFinance, COLS.IMP.getColWidth());
		
		tab.addRow(row, buttonContainer, COLS.BUT.getColWidth());
		
		rows.put(invoice.getId(), new CustomerInvoiceRow(row, openDocument, closeDocument));
		
		// FinanceRows
		if(invoice.hasFinances() && null != finance) {
			Integer financeId = finance.getId();
			invoice.financeStream()
				.filter(f -> !f.getId().equals(financeId))
				.forEach(finan -> paintRow(finan));
		}
	}
	
	private void paintRow(Finance finance) {
		HTMLPanel row = tab.createRow();
		
		FlowPanel buttonContainer = new FlowPanel();

		Label sta = new Label();
		tab.addInlineStyle(sta, COLS.IMP.getCellStyleClass());
		tab.addRow(row, sta, COLS.STA.getColWidth());
		
		Label dat = new Label();
		tab.addInlineStyle(dat, COLS.DAT.getCellStyleClass());
		tab.addRow(row, dat, COLS.DAT.getColWidth());
		
		Label num = new Label();
		tab.addInlineStyle(num, COLS.NUM.getCellStyleClass());
		tab.addRow(row, num, COLS.NUM.getColWidth());
		
		Label bas = new Label();
		tab.addInlineStyle(bas, COLS.BAS.getCellStyleClass());
		tab.addRow(row, bas, COLS.BAS.getColWidth());
		
		Label iva = new Label();
		tab.addInlineStyle(iva, COLS.IVA.getCellStyleClass());
		tab.addRow(row, iva, COLS.IVA.getColWidth());
		
		Label irp = new Label();
		tab.addInlineStyle(irp, COLS.IRP.getCellStyleClass());
		tab.addRow(row, irp, COLS.IRP.getColWidth());
		
		Label tot = new Label();
		tab.addInlineStyle(tot, COLS.TOT.getCellStyleClass());
		tab.addRow(row, tot, COLS.TOT.getColWidth());
		
		// Finance
		Label payMethod = new Label(null == finance ? "" : finance.getPayMethodName());
		tab.addInlineStyle(payMethod, COLS.PAY.getCellStyleClass());
		tab.addRow(row, payMethod, COLS.PAY.getColWidth());
		
		Label vto = new Label(null == finance || null == finance.getDueDate() ? "" : formatDate.format(finance.getDueDate()));
		tab.addInlineStyle(vto, COLS.VEN.getCellStyleClass());
		tab.addRow(row, vto, COLS.VEN.getColWidth());
		
		Label amount = new Label(null == finance  ? "" : formatToEuro(finance.getAmount() + finance.getExpenses()));
		tab.addInlineStyle(amount, COLS.IMP.getCellStyleClass());
		tab.addRow(row, amount, COLS.IMP.getColWidth());
		
		Label statusFinance = new Label(null == finance || finance.getFinanceStatus() == null ? "" : finance.getFinanceStatus().getDescription());
		tab.addInlineStyle(statusFinance, COLS.IMP.getCellStyleClass());
		tab.addRow(row, statusFinance, COLS.IMP.getColWidth());
		
		tab.addRow(row, buttonContainer, COLS.BUT.getColWidth());
	}

	private void openPDF(HTMLPanel row, Integer invoiceId, AonTableButton openDocument, AonTableButton closeDocument) {
		dockPanel.setWidgetSize(viewer, 45);
		dockPanel.animate(500);
		
		resetRowStyles();
		
		openDocument.setVisible(false);
		closeDocument.setVisible(true);
		
		row.getElement().getStyle().setProperty("background-color", "#f1f1f1");
		
		getInvoicePDF(invoiceId, dataURI -> {
			viewer.open(dataURI);
		});
	}

	private void closePDF(HTMLPanel row, AonTableButton openDocument, AonTableButton closeDocument) {
		dockPanel.setWidgetSize(viewer, 0);
		dockPanel.animate(500);
		
		resetRowStyles();
		
		viewer.open("");
	}
	
	private void resetRowStyles() {
		rows.values().forEach(customerInvoiceRow -> {
			customerInvoiceRow.getRow().getElement().getStyle().clearProperty("background-color");
			customerInvoiceRow.getOpenDocument().setVisible(true);
			customerInvoiceRow.getCloseDocument().setVisible(false);
		});
	}

	private static String formatToEuro(double amount) {
        // Format the double value as a number with two decimal places
        NumberFormat numberFormat = NumberFormat.getFormat("#,##0.00");

        // Manually append the Euro symbol ()
        return numberFormat.format(amount) + " \u20AC";
    }

	private void getInvoices(Consumer<List<Invoice>> success) {
		COMMON_SERVICE.getCustomerInvoices(getCurrentDomainName(), getCurrentDomain(), getCurrentUser(), customerId, new AsyncCallback<List<Invoice>>() {

			@Override
			public void onSuccess(List<Invoice> result) {
				success.accept(result);
			}

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Error ontenci\u00f3n facturas: " + caught.getMessage());
			}
			
		});
	}
	
	private void getInvoicePDF(Integer invoiceId, Consumer<String> success) {
		COMMON_SERVICE.getInvoicePDF(getCurrentDomainName(), getCurrentDomain(), getCurrentUser(), invoiceId, new AsyncCallback<String>() {

			@Override
			public void onSuccess(String result) {
				success.accept(result);
			}

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Error ontenci\u00f3n facturas: " + caught.getMessage());
			}
			
		});
	}

}
