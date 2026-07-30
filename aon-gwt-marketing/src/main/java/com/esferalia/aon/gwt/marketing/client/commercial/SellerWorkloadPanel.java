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
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.occam.api.model.SellerWorkloadParams;
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
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

public abstract class SellerWorkloadPanel extends ScrollPanel {

	private static CommonServiceAsync COMMON_SERVICE;
	
	private static final Logger LOGGER = Logger.getLogger(SellerWorkloadPanel.class.getName());
	static { LOGGER.addHandler( new ConsoleLogHandler() ); }
	
	private final int limit = 100;
	private final MutableInt offset = new MutableInt(0);
	private final MutableInt moreData = new MutableInt(0);
	private final MutableInt searchEnabled = new MutableInt( 0 );
	
	private AonCustomTable tab;
	private int lastScrollPos = 0;
	
	private SellerWorkloadParams params;
	
	private Map<Integer, Seller> rowSellers = new HashMap<>();
	private Map<Integer, AonTableButton> selectedItems = new HashMap<>();
	
	private static enum COLS {
		  DES(AON.MSG.name()							,"-moz-available"	,"min-width: 5rem; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, TYP(AON.MSG.scope()						,"7rem"				,"")
		, ACT("Estado"								,"4rem"				,"")
		, CUS("Client."								,"3.5rem"			,"text-align: right;")
		, FEE("Cuot."								,"3rem"				,"text-align: right;")
		, INV("Fact."								,"3rem"				,"text-align: right;")
		, TOT("Fact. Bruta"							,"6rem"				,"text-align: right;")
		, AMO("Fact. Neta"							,"6rem"				,"text-align: right;")
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

	public SellerWorkloadPanel(SellerWorkloadParams params) {
		CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
		COMMON_SERVICE = new CommonServiceAsyncDecorator(commonServiceRaw);
		
		this.params = params;
		this.rowSellers.clear();

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
		onShowLoadingMessage("Cargando ... (si tiene muchos agentes/clientes, puede tardar un poco)");
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
			if(!(col.equals(COLS.CUS) || col.equals(COLS.FEE) || col.equals(COLS.INV) || col.equals(COLS.TOT) || col.equals(COLS.AMO)))
				tab.addHeader(new Label(col.getHeaderLabel()), col.getColWidth(), col.getStyles());
		
		checkPeriodHeader();
		
		tab.addHeader(new Label(AonStringUtils.EMPTY), "2rem", AonStringUtils.EMPTY);
	}
	
	private void checkPeriodHeader() {
		Date periodStart = params.getPeriodStart();
		Date periodEnd = params.getPeriodEnd();

		Date startMonth = periodStart != null ? DateUtils.getFirstDayOfMonth(periodStart) : DateUtils.getFirstDayOfMonth(new Date());
		Date endMonth = periodEnd != null ? DateUtils.getFirstDayOfMonth(periodEnd) : startMonth;

		int monthsBetween = DateUtils.getMonths(endMonth, startMonth);
		if (monthsBetween < 0) {
			monthsBetween = 0;
		}

		for (int i = 0; i <= monthsBetween; i++) {
			Date monthDate = DateUtils.addMonths2Date(DateUtils.copyDateOnly(startMonth), i);
			String monthLabel = AonDateUtils.formatMonthYear(monthDate);
			boolean grayBackground = (i % 2 != 0);
			addPeriodColumns(monthLabel, grayBackground);
		}
	}

	private void addPeriodColumns(String monthLabel, boolean grayBackground) {
		String extraStyle = grayBackground
				? "background-color: #f4f4f4; height: 2rem; display: flex; align-items: center; justify-content: end; padding-right: 0.4rem;"
				: AonStringUtils.EMPTY;

		tab.addHeader(new Label(COLS.CUS.getHeaderLabel()), COLS.CUS.getColWidth(), COLS.CUS.getStyles() + extraStyle);
		tab.addHeader(new Label(COLS.FEE.getHeaderLabel()), COLS.FEE.getColWidth(), COLS.FEE.getStyles() + extraStyle);
		tab.addHeader(new Label(COLS.INV.getHeaderLabel()), COLS.INV.getColWidth(), COLS.INV.getStyles() + extraStyle);
		tab.addHeader(new Label("F. Bruta (" + monthLabel + ")"), "8rem", COLS.TOT.getStyles() + extraStyle);
		tab.addHeader(new Label("F. Neta (" + monthLabel + ")"), "8rem", COLS.AMO.getStyles() + extraStyle);
	}

	private void searchData() {
		if (!isMoreData()) return;
		
		params.setOffset(offset.intValue());
		params.setLimit(limit);
		
		getList(sellersWorkload -> {
			boolean something = false;
			
			for(SellerWorkload sellerWorkload : sellersWorkload ) {
				something = true;
				paintRow(sellerWorkload);
			}
			
			if (sellersWorkload.size() < limit) {
				disableMoreData();
			} else {
				offset.setValue(offset.intValue() + sellersWorkload.size() - 1);
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
			
			onHideMessage();
			
		});
	}

	private void paintRow(SellerWorkload sellerWorkload) {
		FlowPanel buttonContainer = new FlowPanel();
		buttonContainer.getElement().getStyle().setTextAlign(TextAlign.CENTER);
		
		HTMLPanel row = tab.createRow();
		row.addDomHandler(e -> {
			onSellerWorkloadOpen(sellerWorkload);
		}, ClickEvent.getType());
		row.getElement().getStyle().setProperty("padding", "0 12px");
		
		Label name = new Label(null != sellerWorkload.getProjectHolder() ? sellerWorkload.getProjectHolder().getTaskHolder().getName() : sellerWorkload.getName());
		name.setTitle(null != sellerWorkload.getProjectHolder() ? sellerWorkload.getProjectHolder().getTaskHolder().getName() : sellerWorkload.getName());
		tab.addInlineStyle(name, COLS.DES.getStyles());
		tab.addRow(row, name, COLS.DES.getColWidth());
		
		tab.addRow(row, new Label(sellerWorkload.getScope() == null ? null : sellerWorkload.getScope().getDescription()), COLS.TYP.getColWidth());
		
		String activeValue = sellerWorkload.isActive() ? "Activo" : "Inactivo";
		if(null != sellerWorkload.getProjectHolder())
			activeValue = sellerWorkload.getProjectHolder().getTaskHolder().getStatus().getDescription();
		
		tab.addRow(row, new Label(activeValue), COLS.ACT.getColWidth());
		
		List<Entry<Date, SellerWorkloadPeriod>> entries = sellerWorkload.getPeriods().entrySet().stream().collect(Collectors.toList());
		for(int i=0; i < entries.size(); i++) {
			Entry<Date, SellerWorkloadPeriod> entry = entries.get(i);
			
			Label customerL = new Label(entry.getValue().getCustomers().toString());
			customerL.getElement().getStyle().setTextAlign(TextAlign.CENTER);
			
			Label feeL = new Label(entry.getValue().getCustomerFees().toString());
			feeL.getElement().getStyle().setTextAlign(TextAlign.CENTER);
			
			Label invoiceL = new Label(entry.getValue().getCustomerInvoices().toString());
			invoiceL.getElement().getStyle().setTextAlign(TextAlign.CENTER);
			
			Label totalAmountL = new Label(formatToEuro(entry.getValue().getTotalAmount()));
			totalAmountL.getElement().getStyle().setTextAlign(TextAlign.RIGHT);
			
			Label netAmountL = new Label(formatToEuro(entry.getValue().getNetAmount()));
			netAmountL.getElement().getStyle().setTextAlign(TextAlign.RIGHT);
			
			if(i % 2 != 0) {
				addGrayBg(customerL);
				addGrayBg(feeL);
				addGrayBg(invoiceL);
				addGrayBg(netAmountL);
				addGrayBg(totalAmountL);
			}
			
			tab.addRow(row, customerL, COLS.CUS.getColWidth());
			tab.addRow(row, feeL, COLS.FEE.getColWidth());
			tab.addRow(row, invoiceL, COLS.INV.getColWidth());
			tab.addRow(row, totalAmountL, "8rem");
			tab.addRow(row, netAmountL,  "8rem");
		
//			if(i > 0) {
//				tab.addRow(row, customerL, COLS.CUS.getColWidth());
//				tab.addRow(row, feeL, COLS.FEE.getColWidth());
//				tab.addRow(row, invoiceL, COLS.INV.getColWidth());
//				tab.addRow(row, totalAmountL, "7.5rem");
//				tab.addRow(row, netAmountL,  "7.5rem");
//				
//			} else {
//				tab.addRow(row, customerL, COLS.CUS.getColWidth());
//				tab.addRow(row, feeL, COLS.FEE.getColWidth());
//				tab.addRow(row, invoiceL, COLS.INV.getColWidth());
//				tab.addRow(row, totalAmountL, COLS.TOT.getColWidth());
//				tab.addRow(row, netAmountL, COLS.AMO.getColWidth());
//				
//			}
		}
		
		tab.addRow(row, buttonContainer, "2rem");
		
		rowSellers.put(sellerWorkload.getId(), sellerWorkload);
	}
	
	private void addGrayBg(Widget widget) {
		widget.getElement().getStyle().setBackgroundColor("#f4f4f4");
		widget.getElement().getStyle().setProperty("height", "2.5rem");
		widget.getElement().getStyle().setProperty("display", "flex");
		widget.getElement().getStyle().setProperty("align-items", "center");
		widget.getElement().getStyle().setProperty("justify-content", "end");
		widget.getElement().getStyle().setProperty("padding-right", "0.4rem");
	}
	
	private static String formatToEuro(double amount) {
        // Format the double value as a number with two decimal places
        NumberFormat numberFormat = NumberFormat.getFormat("#,##0.00");

        // Manually append the Euro symbol ()
        return numberFormat.format(amount) + " \u20AC";
    }
	
	private void getList(Consumer<List<SellerWorkload>> success) {
		COMMON_SERVICE.getSellersWorkload(params, new AsyncCallback<List<SellerWorkload>>() {
			
			@Override
			public void onSuccess(List<SellerWorkload> sellerswokload) {
				success.accept(sellerswokload);
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
	protected abstract void onSellerWorkloadOpen(SellerWorkload sellerWorkload);
	protected abstract void onHideMessage();
	
}

