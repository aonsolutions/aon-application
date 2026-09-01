package com.esferalia.aon.gwt.marketing.client.commercial;

import java.util.function.Consumer;
import java.util.logging.Logger;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AonDateUtils;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarSmallButton;
import com.esferalia.aon.occam.api.model.SellerWorkloadParams;
import com.esferalia.aon.occam.api.model.fee.Fee;
import com.esferalia.aon.occam.api.model.finance.InvoiceDetail;
import com.esferalia.aon.occam.api.model.registry.SellerWorkloadContent;
import com.esferalia.aon.occam.api.model.type.BillingPeriod;
import com.esferalia.aon.watson.mutable.MutableInt;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.logging.client.ConsoleLogHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;

public abstract class SellerWorkloadFeePanel extends ScrollPanel {

	private static CommonServiceAsync COMMON_SERVICE;
	
	private static final Logger LOGGER = Logger.getLogger(SellerWorkloadFeePanel.class.getName());
	static { LOGGER.addHandler( new ConsoleLogHandler() ); }
	
	private final int limit = 100;
	private final MutableInt offset = new MutableInt(0);
	private final MutableInt moreData = new MutableInt(0);
	private final MutableInt searchEnabled = new MutableInt( 0 );
	
	private AonCustomTable tab;
	private int lastScrollPos = 0;
	
	private SellerWorkloadParams params;
	
	private static enum COLS {
		  
		DES(AON.MSG.customer()						,"-moz-available"	,"min-width: 5rem; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, STA(AON.MSG.status()						,"5rem"				,"")
		, CON("Producto"							,"-moz-available"	,"min-width: 5rem; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, PER("Periodo"								,"4rem"				,"")
		, QUA("Cantidad"							,"4rem"				,"text-align: right;")
		, PRI("Pre. Bruto"							,"5.5rem"			,"text-align: right;")
		, DIS("Descuento"							,"5rem"				,"text-align: right;")
		, NET("Pre. Neto"							,"5rem"				,"text-align: right;")
		, TOB("I. Bruto"							,"5rem"				,"text-align: right;")
		, TON("I. Neto"								,"5rem"				,"text-align: right;")
		, STR("F. Desde"							,"5rem"				,"text-align: center;")
		, BIL("F. Fact."							,"5rem"				,"text-align: center;")
		, END("F. Hasta"							,"5rem"				,"text-align: center;")
		, BUT(AonStringUtils.EMPTY					,"2rem"				,"")
		;

		String headerLabel;
		String colWidth;
		String styles;

		private COLS(String headerLabel,String colWidth,String styles) {
			this.headerLabel = headerLabel;
			this.colWidth = colWidth;
			this.styles = styles;
		}
		public String getColWidth() {
			return colWidth;
		}
		public String getHeaderLabel() {
			return headerLabel;
		}
		public String getStyles() {
			return styles;
		}
	}

	public SellerWorkloadFeePanel(SellerWorkloadParams params) {
		CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
		COMMON_SERVICE = new CommonServiceAsyncDecorator(commonServiceRaw);
		
		this.params = params;
		this.params.setOffset(0);
		this.params.setLimit(Integer.MAX_VALUE);

		this.getElement().getStyle().setProperty("padding", "0 1rem 0 1px");
		
		addScrollHandler(new ScrollHandler() {

			public void onScroll(ScrollEvent event) {
				// ------------------------------------ Ignore scroll up.
				int oldScrollPos = lastScrollPos;
				lastScrollPos = getVerticalScrollPosition();
				if (oldScrollPos >= lastScrollPos) {
					return;
				}
				
				// -----------------------------------------------------
				if (isSearchEnabled()) {
					int maxScrollTop = getWidget().getOffsetHeight() - getOffsetHeight();
					if (lastScrollPos >= maxScrollTop) {
						disableSearch();
						searchData();
					}
				}
			}
		});
		
		onSearch();
		
	}

	public boolean isSearchEnabled() {
		return (searchEnabled.getValue() == 0 );
	}
	public void disableSearch() {
		searchEnabled.setValue(-1);
	}
	public void enableSearch() {
		searchEnabled.setValue(0);
	}
	public boolean isMoreData() {
		return (moreData.getValue() == 0 );
	}
	public void disableMoreData() {
		moreData.setValue(-1);
	}
	public void enableMoreData() {
		moreData.setValue(0);
	}
	
	private void onSearch() {
		enableMoreData();
		search();
	}

	private void search() {
		tab = new AonCustomTable();
		this.setWidget(tab);
		
		paintHeader();
		searchData();
	}
	
	private void paintHeader() {
		tab.createHeader();
		
		for ( COLS col : COLS.values()) 
			tab.addHeader(new Label(col.getHeaderLabel()), col.getColWidth(), col.getStyles());
	}
	
	private void searchData() {
		if (!isMoreData()) return;
		
		params.setOffset(offset.intValue());
		params.setLimit(limit);
		
		getList(sellerWorkloadContent -> {
			boolean something = false;
			
			something = !sellerWorkloadContent.getFees().isEmpty() || !sellerWorkloadContent.getInvoiceDetails().isEmpty();
			
			sellerWorkloadContent.getInvoiceDetails().forEach(invoiceDetail -> {
				paintRow(invoiceDetail);
			});
			sellerWorkloadContent.getFees().forEach(fee -> {
				paintRow(fee);
			});
			
			if ((sellerWorkloadContent.getFees().size() + sellerWorkloadContent.getInvoiceDetails().size()) < limit) {
				disableMoreData();
			} else {
				offset.setValue(offset.intValue() + limit - 1);
				enableMoreData();
			}
			
			if (!something) {
				if(tab.getRowsCount() <= 1) {
					FlowPanel line = new FlowPanel();
					InlineLabel label = new InlineLabel(AON.MSG.noData());
					line.add(label);
					this.clear();
					this.setWidget(line);
				}
				disableMoreData();
			}
			
			enableSearch();
			
		});
	}
	
	private void paintRow(Fee fee) {
		FlowPanel buttonContainer = new FlowPanel();
		buttonContainer.getElement().getStyle().setTextAlign(TextAlign.CENTER);
		
		HTMLPanel row = tab.createRow();
		
		Label customerLabel = new Label(fee.getCustomer().getName());
		customerLabel.setTitle(fee.getCustomer().getName());
		tab.addInlineStyle(customerLabel, COLS.DES.getStyles());
		tab.addRow(row, customerLabel, COLS.DES.getColWidth());
		
		Label statusLabel = new Label(fee.getCustomer().getEffectiveStatus().getDescription());
		 
		// Un bloqueo programado se ve como Activo: se avisa en el tooltip
		if (fee.getCustomer().isBlockScheduled()) {
			statusLabel.setTitle("Bloqueo programado para el "
					+ AonDateUtils.formatDate(fee.getCustomer().getExpirationDate()));
			statusLabel.getElement().getStyle().setColor("orange");
		}
 
		tab.addRow(row, statusLabel, COLS.STA.getColWidth());
		
		Label productLabel = new Label(fee.getDescription());
		productLabel.setTitle(fee.getDescription());
		tab.addInlineStyle(productLabel, COLS.CON.getStyles());
		tab.addRow(row, productLabel, COLS.CON.getColWidth());
		
		Label periodLabel = new Label(BillingPeriod.toString(fee.getPeriod()));
		tab.addRow(row, periodLabel, COLS.PER.getColWidth());
		
		Label quantityLabel = new Label(null == fee.getQuantity() ? "" : fee.getQuantity().toString());
		quantityLabel.getElement().getStyle().setTextAlign(TextAlign.RIGHT);
		tab.addRow(row, quantityLabel, COLS.QUA.getColWidth());
		
		Label priceLabel = new Label(formatToEuro(null == fee.getPrice() ? 0.00 : fee.getPrice()));
		priceLabel.getElement().getStyle().setTextAlign(TextAlign.RIGHT);
		tab.addRow(row, priceLabel, COLS.PRI.getColWidth());
		
		Label discountLabel = new Label(null == fee.getDiscountExpr() ? "" : fee.getDiscountExpr());
		discountLabel.getElement().getStyle().setTextAlign(TextAlign.RIGHT);
		tab.addRow(row, discountLabel, COLS.DIS.getColWidth());
		
		Label netCostLabel = new Label(formatToEuro(null == fee.getNetCost() ? 0.00 : fee.getNetCost()));
		netCostLabel.getElement().getStyle().setTextAlign(TextAlign.RIGHT);
		tab.addRow(row, netCostLabel, COLS.NET.getColWidth());
		
		Label priceTotalLabel = new Label(formatToEuro(null == fee.getTotalPrice() ? 0.00 : fee.getTotalPrice()));
		priceTotalLabel.getElement().getStyle().setTextAlign(TextAlign.RIGHT);
		tab.addRow(row, priceTotalLabel, COLS.TOB.getColWidth());
		
		Label priceNetLabel = new Label(formatToEuro(null == fee.getTotalNetPrice() ? 0.00 : fee.getTotalNetPrice()));
		priceNetLabel.getElement().getStyle().setTextAlign(TextAlign.RIGHT);
		tab.addRow(row, priceNetLabel, COLS.TON.getColWidth());
		
		Label startLabel = new Label(AonDateUtils.formatDate(fee.getStartDate()));
		startLabel.getElement().getStyle().setTextAlign(TextAlign.CENTER);
		tab.addRow(row, startLabel, COLS.STR.getColWidth());
		
		Label billingLabel = new Label(AonDateUtils.formatMonthYear(fee.getBillingDate()));
		billingLabel.getElement().getStyle().setTextAlign(TextAlign.CENTER);
		tab.addRow(row, billingLabel, COLS.BIL.getColWidth());
		
		Label endLabel = new Label(AonDateUtils.formatDate(fee.getEndDate()));
		endLabel.getElement().getStyle().setTextAlign(TextAlign.CENTER);
		tab.addRow(row, endLabel, COLS.END.getColWidth());
		
		AonToolbarSmallButton infoBtn = new AonToolbarSmallButton("", AON.CSS.aonIconInfo());
		infoBtn.setTitle(createFeeInfo(fee));
		buttonContainer.add(infoBtn);
		tab.addRow(row, buttonContainer, COLS.BUT.getColWidth());
	}
	
	private void paintRow(InvoiceDetail invoiceDetail) {
		FlowPanel buttonContainer = new FlowPanel();
		buttonContainer.getElement().getStyle().setTextAlign(TextAlign.CENTER);
		
		HTMLPanel row = tab.createRow();
		
		Label customerLabel = new Label(invoiceDetail.getInvoice().getRegistryName());
		customerLabel.setTitle(invoiceDetail.getInvoice().getRegistryName());
		tab.addInlineStyle(customerLabel, COLS.DES.getStyles());
		tab.addRow(row, customerLabel, COLS.DES.getColWidth());
		
		Label statusLabel = new Label("Facturado");
		tab.addRow(row, statusLabel, COLS.STA.getColWidth());
		
		Label productLabel = new Label(invoiceDetail.getDescription());
		productLabel.setTitle(invoiceDetail.getDescription());
		tab.addInlineStyle(productLabel, COLS.CON.getStyles());
		tab.addRow(row, productLabel, COLS.CON.getColWidth());
		
		Label periodLabel = new Label("Factura");
		tab.addRow(row, periodLabel, COLS.PER.getColWidth());
		
		Label quantityLabel = new Label(String.valueOf(invoiceDetail.getQuantity()));
		quantityLabel.getElement().getStyle().setTextAlign(TextAlign.RIGHT);
		tab.addRow(row, quantityLabel, COLS.QUA.getColWidth());
		
		Label priceLabel = new Label(formatToEuro(invoiceDetail.getPrice()));
		priceLabel.getElement().getStyle().setTextAlign(TextAlign.RIGHT);
		tab.addRow(row, priceLabel, COLS.PRI.getColWidth());
		
		Label discountLabel = new Label(null == invoiceDetail.getDiscountExpression() ? "" : invoiceDetail.getDiscountExpression().getDiscountExpr());
		discountLabel.getElement().getStyle().setTextAlign(TextAlign.RIGHT);
		tab.addRow(row, discountLabel, COLS.DIS.getColWidth());
		
		Label netCostLabel = new Label(formatToEuro(getNetCost(invoiceDetail)));
		netCostLabel.getElement().getStyle().setTextAlign(TextAlign.RIGHT);
		tab.addRow(row, netCostLabel, COLS.NET.getColWidth());
		
		Label priceTotalLabel = new Label(formatToEuro(getTotalPrice(invoiceDetail)));
		priceTotalLabel.getElement().getStyle().setTextAlign(TextAlign.RIGHT);
		tab.addRow(row, priceTotalLabel, COLS.TOB.getColWidth());
		
		Label priceNetLabel = new Label(formatToEuro(getTotalNetPrice(invoiceDetail)));
		priceNetLabel.getElement().getStyle().setTextAlign(TextAlign.RIGHT);
		tab.addRow(row, priceNetLabel, COLS.TON.getColWidth());
		
		Label startLabel = new Label("");
		startLabel.getElement().getStyle().setTextAlign(TextAlign.CENTER);
		tab.addRow(row, startLabel, COLS.STR.getColWidth());
		
		Label billingLabel = new Label(AonDateUtils.formatMonthYear(invoiceDetail.getInvoice().getIssueDate()));
		billingLabel.getElement().getStyle().setTextAlign(TextAlign.CENTER);
		tab.addRow(row, billingLabel, COLS.BIL.getColWidth());
		
		Label endLabel = new Label("");
		endLabel.getElement().getStyle().setTextAlign(TextAlign.CENTER);
		tab.addRow(row, endLabel, COLS.END.getColWidth());
		
		tab.addRow(row, buttonContainer, COLS.BUT.getColWidth());
	}

	private double getNetCost(InvoiceDetail invoiceDetail) {
		return invoiceDetail.getPrice() * (1 - invoiceDetail.getDiscount()/100);
	}
	
	 private double getTotalPrice(InvoiceDetail invoiceDetail) {
		return invoiceDetail.getPrice() * invoiceDetail.getQuantity();
	}
	 
	private double getTotalNetPrice(InvoiceDetail invoiceDetail) {
		return getNetCost(invoiceDetail) * invoiceDetail.getQuantity();
	}
	
	private static String formatToEuro(double amount) {
        // Format the double value as a number with two decimal places
        NumberFormat numberFormat = NumberFormat.getFormat("#,##0.00");

        // Manually append the Euro symbol ()
        return numberFormat.format(amount) + " \u20AC";
    }
	
	private String createFeeInfo(Fee fee) {
		String tooltip = "";
		
		tooltip += "Seguridad: " + (null == fee.getSecurityLevel() ? "N/D" : fee.getSecurityLevel().getName());
		tooltip += "\nC. Trabajo: " + (null == fee.getWorkplace() ? "N/D" : fee.getWorkplace().getDescription());
		
		if(null != fee.getSeller() && AonStringUtils.isNotBlank(fee.getSeller().getName())) tooltip += "\nComercial: " + fee.getSeller().getName();
		if(null != fee.getInvoicingGroup() && AonStringUtils.isNotBlank(fee.getInvoicingGroup().getDescription())) tooltip += "\nG. Facturac\u00f3n: " + fee.getInvoicingGroup().getDescription();
		if(null != fee.getProject() && AonStringUtils.isNotBlank(fee.getProject().getName())) tooltip += "\nProyecto: " + fee.getProject().getName();
		
		return tooltip;
	}
	
	private void getList(Consumer<SellerWorkloadContent> success) {
		COMMON_SERVICE.getSellersWorkloadContent(params, new AsyncCallback<SellerWorkloadContent>() {
			
			@Override
			public void onSuccess(SellerWorkloadContent sellerswokloadContent) {
				success.accept(sellerswokloadContent);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				// Error
			}
		});
	}
	
	public void resetSearchOffset() {
		offset.setValue(0);
	}
	
	public AonCustomTable getTable() {
		return tab;
	}

	protected abstract void onShowSuccessMessage(String successMessage);
	protected abstract void onShowErrorMessage(String errorMessage);
	protected abstract void onShowLoadingMessage(String loadingMessage);
	
}

