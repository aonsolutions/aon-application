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
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
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
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;

public abstract class SellerWorkloadPanel extends ScrollPanel {

	private static CommonServiceAsync COMMON_SERVICE;
	
	private static final Logger LOGGER = Logger.getLogger(SellerWorkloadPanel.class.getName());
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
		  CHK(AonStringUtils.EMPTY					,"2rem"				,"" )
		, DES(AON.MSG.name()						,"-moz-available"	,"")
		, DOC(AON.MSG.document()					,"30rem"			,"")
		, TYP(AON.MSG.scope()						,"30rem"			,"")
		, ACT("Estado"								,"5rem"				,"")
		, CUS("Clientes"							,"30rem"			,"text-align: right;")
		, FEE("Cuotas"								,"30rem"			,"text-align: right;")
		, AMO("Facturaci\u00f3n"					,"37rem"			,"text-align: right;")
//		, BUT(AonStringUtils.EMPTY					,"5rem"				,"")
//		, DES(AON.MSG.name()						,"-moz-available")
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
			if(col == COLS.CHK) {
				AonTableButton checkAllButton = new AonTableButton(AON.MSG.selectAction(), AON.CSS.aonIconCheck());
				checkAllButton.addClickHandler(e -> {
					List<AonTableButton> selectedItemList = selectedItems.values().stream().filter(check -> AonStringUtils.containsIgnoreCase(check.getStyleName(), AON.CSS.aonIconChecked())).collect(Collectors.toList());
					if (selectedItemList.size() == rowSellers.size() || AonStringUtils.containsIgnoreCase(checkAllButton.getStyleName(), AON.CSS.aonIconChecked())) {
						checkAllButton.addStyleName(AON.CSS.aonIconCheck());
						checkAllButton.removeStyleName(AON.CSS.aonIconChecked());
						selectedItems.values().forEach(check ->{
							check.addStyleName(AON.CSS.aonIconCheck());
							check.removeStyleName(AON.CSS.aonIconChecked());
						});
					} else {
						checkAllButton.addStyleName(AON.CSS.aonIconChecked());
						checkAllButton.removeStyleName(AON.CSS.aonIconCheck());
						selectedItems.values().forEach(check ->{
							check.addStyleName(AON.CSS.aonIconChecked());
							check.removeStyleName(AON.CSS.aonIconCheck());
						});
					}
				});
				
				tab.addHeader(checkAllButton, col.getColWidth());
			} else tab.addHeader(new Label(col.getHeaderLabel()), col.getColWidth(), col.getStyles());
		
		checkPeriodHeader();
		
		tab.addHeader(new Label(AonStringUtils.EMPTY), "5rem", AonStringUtils.EMPTY);
	}
	
	private void checkPeriodHeader() {
		if(params.getPeriod() == 1) {
			tab.addHeader(new Label(COLS.CUS.getHeaderLabel() + " (2M)"), COLS.CUS.getColWidth(), COLS.CUS.getStyles());
			tab.addHeader(new Label(COLS.FEE.getHeaderLabel() + " (2M)"), COLS.FEE.getColWidth(), COLS.FEE.getStyles());
			tab.addHeader(new Label(COLS.AMO.getHeaderLabel() + " (2M)"), COLS.AMO.getColWidth(), COLS.AMO.getStyles());
		} else if(params.getPeriod() == 2) {
			tab.addHeader(new Label(COLS.CUS.getHeaderLabel() + " (2M)"), COLS.CUS.getColWidth(), COLS.CUS.getStyles());
			tab.addHeader(new Label(COLS.FEE.getHeaderLabel() + " (2M)"), COLS.FEE.getColWidth(), COLS.FEE.getStyles());
			tab.addHeader(new Label(COLS.AMO.getHeaderLabel() + " (2M)"), COLS.AMO.getColWidth(), COLS.AMO.getStyles());
			
			tab.addHeader(new Label(COLS.CUS.getHeaderLabel() + " (3M)"), COLS.CUS.getColWidth(), COLS.CUS.getStyles());
			tab.addHeader(new Label(COLS.FEE.getHeaderLabel() + " (3M)"), COLS.FEE.getColWidth(), COLS.FEE.getStyles());
			tab.addHeader(new Label(COLS.AMO.getHeaderLabel() + " (3M)"), COLS.AMO.getColWidth(), COLS.AMO.getStyles());
		}
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
	
	private void paintRow(SellerWorkload sellerWorkload) {
		FlowPanel buttonContainer = new FlowPanel();
		buttonContainer.getElement().getStyle().setTextAlign(TextAlign.CENTER);
		
		HTMLPanel row = tab.createRow();
		row.addDomHandler(e -> {
			Window.alert("Listar Cuotas para este Agente Comencial, en el Periodo indicado en el filtro");
			//onSellerWorkloadOpen(sellerWorkload);
		}, ClickEvent.getType());
		
		AonTableButton checkButton = new AonTableButton(AON.MSG.selectAction(), AON.CSS.aonIconCheck());
		checkButton.addClickHandler(e -> {
			e.stopPropagation();
			if (AonStringUtils.containsIgnoreCase(checkButton.getStyleName(), AON.CSS.aonIconChecked())) {
				checkButton.addStyleName(AON.CSS.aonIconCheck());
				checkButton.removeStyleName(AON.CSS.aonIconChecked());
			} else {
				checkButton.addStyleName(AON.CSS.aonIconChecked());
				checkButton.removeStyleName(AON.CSS.aonIconCheck());
			}
			
//			List<AonTableButton> selectedItemList = selectedItems.values().stream().filter(check -> AonStringUtils.containsIgnoreCase(check.getStyleName(), AON.CSS.aonIconChecked())).collect(Collectors.toList());
		});
		tab.addRow(row, checkButton, COLS.CHK.getColWidth());
		
		tab.addRow(row, new Label(sellerWorkload.getName()), COLS.DES.getColWidth());
		tab.addRow(row, new Label(sellerWorkload.getDocument()), COLS.DOC.getColWidth());
		tab.addRow(row, new Label(sellerWorkload.getScope() == null ? null : sellerWorkload.getScope().getDescription()), COLS.TYP.getColWidth());
		tab.addRow(row, new Label(sellerWorkload.isActive() ? "Activo" : "Inactivo"), COLS.ACT.getColWidth());
		
		for(Entry<Date, SellerWorkloadPeriod> entry : sellerWorkload.getPeriods().entrySet()) {
			Label customerL = new Label(entry.getValue().getCustomers().toString());
			customerL.getElement().getStyle().setTextAlign(TextAlign.RIGHT);
			tab.addRow(row, customerL, COLS.CUS.getColWidth());
			
			Label feeL = new Label(entry.getValue().getCustomerFees().toString());
			feeL.getElement().getStyle().setTextAlign(TextAlign.RIGHT);
			tab.addRow(row, feeL, COLS.FEE.getColWidth());
			
			Label amountL = new Label(formatToEuro(entry.getValue().getAmount()));
			amountL.getElement().getStyle().setTextAlign(TextAlign.RIGHT);
			tab.addRow(row, amountL, COLS.AMO.getColWidth());
		}
		
		tab.addRow(row, buttonContainer, "5rem");
		
		rowSellers.put(sellerWorkload.getId(), sellerWorkload);
		selectedItems.put(sellerWorkload.getId(), checkButton);
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
	
}

