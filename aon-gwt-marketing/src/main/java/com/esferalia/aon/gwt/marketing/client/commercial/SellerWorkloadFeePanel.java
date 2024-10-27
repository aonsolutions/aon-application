package com.esferalia.aon.gwt.marketing.client.commercial;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AonDateUtils;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarSmallButton;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.occam.api.model.SellerWorkloadParams;
import com.esferalia.aon.occam.api.model.fee.Fee;
import com.esferalia.aon.occam.api.model.registry.Seller;
import com.esferalia.aon.occam.api.model.registry.SellerWorkload;
import com.esferalia.aon.occam.api.model.registry.SellerWorkloadPeriod;
import com.esferalia.aon.watson.mutable.MutableInt;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.logging.client.ConsoleLogHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public abstract class SellerWorkloadFeePanel extends ScrollPanel {

	private static CommonServiceAsync COMMON_SERVICE;
	
	private static final Logger LOGGER = Logger.getLogger(SellerWorkloadFeePanel.class.getName());
	static { LOGGER.addHandler( new ConsoleLogHandler() ); }
	
	private final int limit = 100;
	private final MutableInt offset = new MutableInt(0);
	private final MutableInt moreData = new MutableInt(0);
	private final MutableInt searchEnabled = new MutableInt( 0 );
	
	private SimplePanel container;
	private ScrollPanel scrollPanel;
	private AonCustomTable tab;
	private int lastScrollPos = 0;
	
	private SellerWorkloadParams params;
	private Map<Integer, Seller> rowSellers = new HashMap<>();
	private Map<Integer, AonTableButton> selectedItems = new HashMap<>();
	
	private static enum COLS {
		  
		DES(AON.MSG.customer()						,"-moz-available"	,"min-width: 5rem; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, STA(AON.MSG.status()						,"7rem"				,"")
		, TYP(AON.MSG.scope()						,"7rem"				,"")
		, CON("Concepto"							,"-moz-available"	,"min-width: 5rem; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, PER("Perido"								,"5rem"				,"")
		, QUA("Cantidad"							,"5rem"				,"text-align: right;")
		, PRI("Precio"								,"6rem"				,"text-align: right;")
		, DIS("Descuento"							,"6rem"				,"text-align: right;")
		, STR("F. Desde"							,"6rem"				,"")
		, BIL("F. Facturaci\u00f3n"					,"6rem"				,"")
		, END("F. Hasta"							,"6rem"				,"")
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
		this.rowSellers.clear();

		container = new SimplePanel();
		container.getElement().getStyle().setProperty("padding", "0 1rem 0 1px");
		setWidget(container);
		
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
		container.clear();
		tab = new AonCustomTable();
		tab.setMaxHeight((Window.getClientHeight() - 200) + "px");
		scrollPanel = new ScrollPanel(tab);
		
		paintHeader();
		container.setWidget(scrollPanel);
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
		
		getList(sellersWorkloadFee -> {
			boolean something = false;
			
			for(Fee sellerWorkloadFee : sellersWorkloadFee ) {
				something = true;
				paintRow(sellerWorkloadFee);
			}
			
			if (sellersWorkloadFee.size() < limit) {
				disableMoreData();
			} else {
				offset.setValue(offset.intValue() + sellersWorkloadFee.size() - 1);
				enableMoreData();
			}
			
			if (!something) {
				FlowPanel line = new FlowPanel();
				InlineLabel label = new InlineLabel(AON.MSG.noData());
				line.add(label);
				container.clear();
				container.add(line);
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
		tab.addInlineStyle(customerLabel, COLS.DES.getStyles());
		tab.addRow(row, customerLabel, COLS.DES.getColWidth());
		
		Label statusLabel = new Label(fee.getCustomer().getStatus().getDescription());
		tab.addRow(row, statusLabel, COLS.STA.getColWidth());
		
		Label productLabel = new Label(fee.getDescription());
		tab.addInlineStyle(productLabel, COLS.CON.getStyles());
		tab.addRow(row, productLabel, COLS.CON.getColWidth());
		
		Label periodLabel = new Label(fee.getPeriod().getValue().toString());
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
		
		Label startLabel = new Label(AonDateUtils.formatDate(fee.getStartDate()));
		tab.addRow(row, startLabel, COLS.STR.getColWidth());
		
		Label billingLabel = new Label(AonDateUtils.formatMonthYear(fee.getBillingDate()));
		tab.addRow(row, billingLabel, COLS.BIL.getColWidth());
		
		Label endLabel = new Label(AonDateUtils.formatDate(fee.getEndDate()));
		tab.addRow(row, endLabel, COLS.END.getColWidth());
		
		AonToolbarSmallButton infoBtn = new AonToolbarSmallButton("", AON.CSS.aonIconInfo());
		infoBtn.setTitle(createFeeInfo(fee));
		buttonContainer.add(infoBtn);
		tab.addRow(row, buttonContainer, COLS.BUT.getColWidth());
	}
	
	private static String formatToEuro(double amount) {
        // Format the double value as a number with two decimal places
        NumberFormat numberFormat = NumberFormat.getFormat("#,##0.00");

        // Manually append the Euro symbol ()
        return numberFormat.format(amount) + " \u20AC";
    }
	
	private String createFeeInfo(Fee fee) {
		String tooltip = "";
		
		tooltip += "Seguridad: " + fee.getSecurityLevel().getName();
		tooltip += "\nC. Trabajo: " + fee.getWorkplace().getDescription();
		
		if(null != fee.getSeller() && AonStringUtils.isNotBlank(fee.getSeller().getName())) tooltip += "\nComercial: " + fee.getSeller().getName();
		if(null != fee.getInvoicingGroup() && AonStringUtils.isNotBlank(fee.getInvoicingGroup().getDescription())) tooltip += "\nG. Facturac\u00f3n: " + fee.getInvoicingGroup().getDescription();
		if(null != fee.getProject() && AonStringUtils.isNotBlank(fee.getProject().getName())) tooltip += "\nProyecto: " + fee.getProject().getName();
		
		return tooltip;
	}
	
	private void getList(Consumer<List<Fee>> success) {
		COMMON_SERVICE.getSellersWorkloadFees(params, new AsyncCallback<List<Fee>>() {
			
			@Override
			public void onSuccess(List<Fee> sellerswokloadFees) {
				success.accept(sellerswokloadFees);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				// Error
			}
		});
	}
	
	public void resetSearchOffset() {
		offset.setValue(0);
		rowSellers.clear();
		selectedItems.clear();
	}
	
	public AonCustomTable getTable() {
		return tab;
	}

	public Integer getSellerListPosition(Integer sellerId) {
		List<Seller> sellers = rowSellers.values().stream().collect(Collectors.toList());
		for(int i=0; i<sellers.size(); i++)
			if(sellers.get(i).getId().equals(sellerId))
				return i;
		return 0;
	}
	
	public void getSellerListCount(Consumer<Integer> finish) {
		COMMON_SERVICE.getSellersWorkloadCount(params, new AsyncCallback<Integer>() {
					
					@Override
					public void onSuccess(Integer count) {
						finish.accept(count);
					}
					
					@Override
					public void onFailure(Throwable caught) {
						finish.accept(null);
					}
				});
	}

	protected abstract void onShowSuccessMessage(String successMessage);
	protected abstract void onShowErrorMessage(String errorMessage);
	protected abstract void onShowLoadingMessage(String loadingMessage);
	
}

