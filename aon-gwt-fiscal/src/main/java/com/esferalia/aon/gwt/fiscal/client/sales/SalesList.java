package com.esferalia.aon.gwt.fiscal.client.sales;

import java.util.List;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDateBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDockLayout;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.fiscal.client.registry.RegistryModuleOptions;
import com.esferalia.aon.occam.api.model.management.Sales;
import com.esferalia.aon.occam.api.model.sales.SalesParams;
import com.esferalia.aon.watson.mutable.MutableInt;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.SimplePanel;

public class SalesList extends AonCustomDockLayout {
	
	private static CommonServiceAsync COMMON_SERVICE;
	
	// TariffList UI

	private HTMLPanel container;
	private HTMLPanel messagePanel = new HTMLPanel("");
	
	private AonCustomDateBox date = new AonCustomDateBox("Fecha");
	private AonCustomDateBox deliveryDate = new AonCustomDateBox("Fecha Entrega");
	private AonCustomListBox status = new AonCustomListBox("Estado");
	
	private AonCustomListBox sort = new AonCustomListBox("Ordenar Por");
	private AonCustomListBox asc = new AonCustomListBox("Orden");
	
	private SimplePanel tableContainer;
	private ScrollPanel tableScrollPanel;
	private AonCustomTable tab;
	
	private DateTimeFormat formatDate = DateTimeFormat.getFormat("dd/MM/yyyy");
	
	private RegistryModuleOptions options;
	
	private SalesParams params;
	
	// Table UI
	private final int limit = 100;
	private final MutableInt offset = new MutableInt(0);
	private final MutableInt moreData = new MutableInt(0);
	private final MutableInt searchEnabled = new MutableInt( 0 );
	
	private int lastScrollPos = 0;

	private static enum COLS {
		  DAT(AON.MSG.date()						,"5rem" 			,"")
		, DEL("F. Entrega"							,"5rem" 			,"")
		, COD("N\u00ba Pedido"						,"10rem"  			,"")
		, CUS(AON.MSG.customer()					,"-moz-available"  	,"min-width: 5rem; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, COM("Comercial"							,"10rem" 			,"white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, SCO(AON.MSG.scope()						,"10rem" 			,"white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, TOT(AON.MSG.total()						,"5rem" 			,"text-align: right;")
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
	
	// Constructor
	public SalesList(RegistryModuleOptions options) {
		super("PROCESAR PEDIDOS");
		
		CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
		COMMON_SERVICE = new CommonServiceAsyncDecorator(commonServiceRaw);
		
		this.options = options;
		
		hideToolbarFilterMessages();
		setSearchPlaceholder("Busque por cliente, comercial, n\u00ba pedido ...");
		addKeyUpHandler(e -> {
			String value = getSearchTextBox().getValue();
			if(AonStringUtils.isNotBlank(value) && value.length() > 3) {
				onSearch();
			} else if(AonStringUtils.isBlank(value)) {
				onSearch();
			}
		});
		
		date.addValueChangeHandler(e -> onSearch());
		addFilterWidget(date);
		
		deliveryDate.addValueChangeHandler(e -> onSearch());
		addFilterWidget(deliveryDate);
		
		status.clearItems();
		status.addItem( "Todas", "");
		status.addItem( "Pediente", "0");
		status.addItem( "Bloqueado", "1");
		status.addItem( "Servido", "2");
		status.addItem( "Cerrado", "3");
		status.addItem( "Facturado", "4");
		status.addItem( "En Preparaci\u00f3n", "5");
		status.getListBox().addChangeHandler(event -> onSearch());
		
		// Default pending
		status.setValue("0");;
		status.setEnable(false);
		
		addFilterWidget(status);
		
		sort.addItem("Fecha", "date");
		sort.addItem("F. Entrega", "deliveryDate");
		sort.addItem("N\u00ba Pedido", "code");
		sort.addItem("Cliente", "customer");
		sort.addItem("Comercial", "comercial");
		sort.addItem("Estado", "status");
		sort.addItem("Ambito", "scope");
		sort.getListBox().addChangeHandler(event -> onSearch());
		
		asc.addItem("Ascendente", "true");
		asc.addItem("Descendete", "false");
		asc.getListBox().addChangeHandler(event -> onSearch());
		
		addSortWidget(sort);
		addSortWidget(asc);
		
		container = new HTMLPanel("");
		container.addStyleName(AON.CSS.aonFlexColumn());
		
		container.add(messagePanel);
	
		tableContainer = new SimpleLayoutPanel();
		tableContainer.setHeight("100%");
		tableContainer.getElement().getStyle().setProperty("margin-left", "1rem");
		
		container.add(tableContainer);
		
		add(container);
		onSearch();
		
		Scheduler.get().scheduleDeferred(new Command() {
	        public void execute() {
	        	getSearchTextBox().setFocus(true);
	        }
	    });		
	}

	@Override
	protected void onClearFilter() {
		getSearchTextBox().setValue(null, false);
		
		date.setValue(null);
		deliveryDate.setValue(null);
		status.setValue("0");
		
		resetSearchOffset();
		
		onSearch();
	}
	
	private void resetSearchOffset() {
		offset.setValue(0);
	}
	
	public void onSearch() {
		getWidgetParams();
		resetSearchOffset();
		onSearchData();
	}

	public void getWidgetParams() {
		params = new SalesParams()
				.setDomainName(options.getDomainName())
				.setDomain(options.getDomain())
				.setUser(options.getUser())
				.setDescription(getSearchTextBox().getValue())
				.setDate(date.getValue())
				.setDeliveryDate(deliveryDate.getValue())
				.setStatus(AonStringUtils.isBlank(status.getValue()) ? null : Byte.parseByte(status.getValue()))
				.setOrderBy(sort.getValue())
				.setAsc(Boolean.parseBoolean(asc.getValue()))
				;
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
	
	private void onSearchData() {
		enableMoreData();
		searchData();
	}
	
	private void searchData() {
		tableContainer.clear();
		tab = new AonCustomTable();
		tableScrollPanel = new ScrollPanel(tab);
		tableScrollPanel.getElement().getStyle().setProperty("margin", "0 1rem");
		
		tableScrollPanel.addScrollHandler(e -> {
			// ------------------------------------ Ignore scroll up.
			int oldScrollPos = lastScrollPos;
			lastScrollPos = tableScrollPanel.getVerticalScrollPosition();
			if (oldScrollPos >= lastScrollPos) {
				return;
			}
			// -----------------------------------------------------
			if (isSearchEnabled()) {
				int maxScrollTop = tableScrollPanel.getWidget().getOffsetHeight() - tableScrollPanel.getOffsetHeight();
				if (lastScrollPos >= maxScrollTop) {
					disableSearch();
					searchDataList();
				}
			}
		});
		
		paintHeader();
		tableContainer.add(tableScrollPanel);
		searchDataList();
	}
	
	private void paintHeader() {
		tab.createHeader();
		for ( COLS col : COLS.values()) 
			tab.addHeader(new Label(col.getHeaderLabel()), col.getColWidth(), col.getCellStyleClass());
	}
	
	private void searchDataList() {
		if (!isMoreData()) return;
		
		params.setOffset(offset.intValue());
		params.setLimit(limit);
		
		getList(sales -> {
			boolean something = false;
			
			for(Sales sale : sales) {
				something = true;
				paintRow(sale);
			}
			
			if (sales.size() < limit) {
				disableMoreData();
			} else {
				offset.setValue(offset.intValue() + sales.size() - 1);
				enableMoreData();
			}
			
			if (!something) {
				FlowPanel line = new FlowPanel();
				InlineLabel label = new InlineLabel(AON.MSG.noData());
				line.add(label);
				tableContainer.clear();
				tableContainer.add(line);
				disableMoreData();
			}
			enableSearch();
			
		});
	}
	
	private void paintRow(Sales sale) {
		FlowPanel buttonContainer = new FlowPanel();
		buttonContainer.getElement().getStyle().setTextAlign(TextAlign.CENTER);
		
		AonTableButton processButton = new AonTableButton("Procesar Pedido", AON.CSS.aonIconEmit());
		processButton.addStyleName(AON.CSS.aonCustomRowButtom());
		processButton.addClickHandler(event -> {
			event.stopPropagation();
			
			new ProcessSalesDialog(sale.getId(), params) {
				@Override
				public void onSaleProcess() { onSearch(); }
			};
			
		});
		buttonContainer.add(processButton);
		
		HTMLPanel row = tab.createRow();
		row.addDomHandler(e -> {}, ClickEvent.getType());
		
		Label date = new Label(null == sale.getDate() ? "" : formatDate.format(sale.getDate()));
		tab.addRow(row, date, COLS.DAT.getColWidth());
		
		Label deliveryDate = new Label(null == sale.getDeliveryDate() ? "" : formatDate.format(sale.getDeliveryDate()));
		tab.addRow(row, deliveryDate, COLS.DEL.getColWidth());
		
		Label deliveryNumber = new Label((AonStringUtils.isBlank(sale.getSeries()) ? "" : sale.getSeries() + "/") + sale.getNumber());
		tab.addRow(row, deliveryNumber, COLS.COD.getColWidth());
		
		Label customer = new Label(sale.getCustomer().getName());
		tab.addInlineStyle(customer, COLS.CUS.getCellStyleClass());
		tab.addRow(row, customer, COLS.CUS.getColWidth());
		
		Label commercial = new Label(sale.getSeller().getName());
		tab.addInlineStyle(commercial, COLS.COM.getCellStyleClass());
		tab.addRow(row, commercial, COLS.COM.getColWidth());
		
		Label scope = new Label(sale.getScope().getDescription());
		tab.addInlineStyle(scope, COLS.SCO.getCellStyleClass());
		tab.addRow(row, scope, COLS.SCO.getColWidth());
		
		double totalSales = sale.getDetails().stream().mapToDouble(detail -> getTotal(detail.getQuantity(), detail.getPrice(), detail.getTaxes(), detail.getDiscountExpression().getDiscountExpr())).sum();
		Label total = new Label(formaDouble(totalSales) + " \u20ac");
		tab.addInlineStyle(total, COLS.TOT.getCellStyleClass());
		tab.addRow(row, total, COLS.TOT.getColWidth());
		
		tab.addRow(row, buttonContainer, COLS.BUT.getColWidth());
	}
	
	private static double getTotal(double quantity, double price, double taxes, String discount) {
		double result = price;
		
		if (discount != null && !discount.trim().isEmpty()) {
            
			String[] parts = discount.replace(" ", "").split("\\+");

            for (String part : parts) {
                try {
                    double discountPercent = Double.parseDouble(part);
                    result -= result * (discountPercent / 100.0);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Formato de descuento inválido: " + part, e);
                }
            }
        }

		result += result * (taxes / 100.0);
        return result * quantity;
	}

	private static String formaDouble(double value) {
        // Round to two decimal places
        long scaledValue = Math.round(value * 100); // Scale to avoid floating-point precision issues
        long integerPart = scaledValue / 100;      // Extract integer part
        long decimalPart = scaledValue % 100;      // Extract decimal part

        // Format the result
        return integerPart + "." + (decimalPart < 10 ? "0" : "") + decimalPart /*+ " \u20ac"*/;
    }
	
	private void getList(Consumer<List<Sales>> success) {
		COMMON_SERVICE.getSales(params, new AsyncCallback<List<Sales>>() {
			
			@Override
			public void onSuccess(List<Sales> salesDB) {
				success.accept(salesDB);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				AonMessagePanel.showError(messagePanel, "Error tarifas: " + caught.getMessage());
			}
		});
	}
	
}
