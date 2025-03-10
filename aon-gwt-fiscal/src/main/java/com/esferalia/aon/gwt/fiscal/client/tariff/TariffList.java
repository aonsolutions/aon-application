package com.esferalia.aon.gwt.fiscal.client.tariff;

import java.util.List;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog.AonCustomDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDockLayout;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog.AonAcceptDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.fiscal.client.registry.RegistryModuleOptions;
import com.esferalia.aon.gwt.fiscal.client.tariff.TariffPanel.AonTariffPanelCallback;
import com.esferalia.aon.occam.api.model.tariff.Tariff;
import com.esferalia.aon.occam.api.model.tariff.TariffParams;
import com.esferalia.aon.watson.mutable.MutableInt;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.SimplePanel;

public abstract class TariffList extends AonCustomDockLayout {
	
	private static CommonServiceAsync COMMON_SERVICE;
	
	// TariffList UI

	private HTMLPanel container;
	private HTMLPanel messagePanel = new HTMLPanel("");
	
	private AonCustomListBox purchaseType = new AonCustomListBox("Tipo");
	private AonCustomListBox status = new AonCustomListBox("Estado");
	
	private AonCustomListBox sort = new AonCustomListBox("Ordenar Por");
	private AonCustomListBox asc = new AonCustomListBox("Orden");
	
	private SimplePanel tableContainer;
	private ScrollPanel tableScrollPanel;
	private AonCustomTable tab;
	
	private RegistryModuleOptions options;
	
	private TariffParams params;
	
	// Table UI
	private final int limit = 100;
	private final MutableInt offset = new MutableInt(0);
	private final MutableInt moreData = new MutableInt(0);
	private final MutableInt searchEnabled = new MutableInt( 0 );
	
	private int lastScrollPos = 0;

	private static enum COLS {
		  COD(AON.MSG.code()						,"15rem"  			,"white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, DES(AON.MSG.description()					,"-moz-available"  	,"min-width: 5rem; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, TYP(AON.MSG.type()						,"10rem" 			,"")
		, DIS(AON.MSG.discount()					,"10rem" 			,"")
		, STA(AON.MSG.status()						,"10rem" 			,"")
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
	public TariffList(RegistryModuleOptions options) {
		super("SERVICIOS AON");
		
		CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
		COMMON_SERVICE = new CommonServiceAsyncDecorator(commonServiceRaw);
		
		this.options = options;
		
		addButtonsToolbar();
		
		hideToolbarFilterMessages();
		setSearchPlaceholder("Busque por c\u00f3digo / Descripci\u00f3n ...");
		addKeyUpHandler(e -> {
			String value = getSearchTextBox().getValue();
			if(AonStringUtils.isNotBlank(value) && value.length() > 3) {
				onSearch();
			} else if(AonStringUtils.isBlank(value)) {
				onSearch();
			}
		});
		
		purchaseType.clearItems();
		purchaseType.addItem( "Todas", "");
		purchaseType.addItem( "Compras", "1");
		purchaseType.addItem( "Ventas", "0");
		purchaseType.getListBox().addChangeHandler(event -> onSearch());
		
		addFilterWidget(purchaseType);
		
		status.clearItems();
		status.addItem( "Todas", "");
		status.addItem( "Inactivo", "0");
		status.addItem( "Activo", "1");
		status.getListBox().addChangeHandler(event -> onSearch());
		
		addFilterWidget(status);
		
		sort.addItem("C\u00f3digo", "code");
		sort.addItem("Nombre", "name");
		sort.addItem("Tipo", "type");
		sort.addItem("Estado", "status");
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
		
		purchaseType.setValue("0");
		status.setValue("1");
		
		resetSearchOffset();
		
		onSearch();
	}
	
	private void resetSearchOffset() {
		offset.setValue(0);
	}
	
	private void addButtonsToolbar() {
		AonToolbarButton newButton = new AonToolbarButton( "Nueva Tarifa", AON.CSS.aonIconAdd());
		newButton.addClickHandler(e -> showTariffDialog());
		addToolbarButton(newButton);
	}
	
	private void showTariffDialog() {
		AonCustomDialog dialog = new AonCustomDialog();
		dialog.setCaption( "NUEVA TARIFA" );
		dialog.showCloseButton(true);
		
		TariffPanel tariffPanel = new TariffPanel(options, new AonTariffPanelCallback(){

			@Override
			public void onAccept(Tariff tariff) {
				dialog.hide();
				onSearch();
			}
		
		});
			
		dialog.add( tariffPanel );
		
		dialog.showLoadedCB(new AonCustomDialogCallback() {
			@Override
			public void onEnd() { tariffPanel.focusCode(); }
		});
	}

	public void onSearch() {
		getWidgetParams();
		resetSearchOffset();
		onSearchData();
	}

	public void getWidgetParams() {
		params = new TariffParams()
				.setDomainName(options.getDomainName())
				.setDomain(options.getDomain())
				.setUser(options.getUser())
				.setDescription(getSearchTextBox().getValue())
				.setPurchase(AonStringUtils.isBlank(purchaseType.getValue()) ? null : Byte.parseByte(purchaseType.getValue()))
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
		
		getList(tariffs -> {
			boolean something = false;
			
			for(Tariff tariff : tariffs) {
				something = true;
				paintRow(tariff);
			}
			
			if (tariffs.size() < limit) {
				disableMoreData();
			} else {
				offset.setValue(offset.intValue() + tariffs.size() - 1);
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
	
	private void paintRow(Tariff tariff) {
		FlowPanel buttonContainer = new FlowPanel();
		buttonContainer.getElement().getStyle().setTextAlign(TextAlign.CENTER);
		
		
		AonTableButton deleteButton = new AonTableButton("Borrar Tarifa", AON.CSS.aonIconDelete());
		deleteButton.addStyleName(AON.CSS.aonCustomRowButtom());
		deleteButton.addClickHandler(event -> {
			event.stopPropagation();
			deleteButton.setEnabled(false);
			
			AonDialog dialog = new AonDialog("Eliminaci\u00f3n Tarifa",
					new HTML("Se va a proceder a eliminar la tarifa <b>" + tariff.getName() + "</b>.<br>\u00bfEsta seguro que desea proceder con la eliminaci\u00f3n\u003f. Este proceso ser\u00e1 irreversible"));
			
			dialog.confirm(new AonAcceptDialogCallback() {

				@Override
				public void onCancel() {
					deleteButton.setEnabled(true);
				}

				@Override
				public void onAccept() {
					delete(tariff);
				}
			});
		});
		buttonContainer.add(deleteButton);
		
		HTMLPanel row = tab.createRow();
		row.addDomHandler(e -> onTariffSelect(tariff), ClickEvent.getType());
		
		Label code = new Label(tariff.getCode());
		code.setTitle(tariff.getCode());
		tab.addInlineStyle(code, COLS.COD.getCellStyleClass());
		tab.addRow(row, code, COLS.COD.getColWidth());
		
		Label description = new Label(tariff.getName());
		description.setTitle(tariff.getName());
		tab.addInlineStyle(description, COLS.DES.getCellStyleClass());
		tab.addRow(row, description, COLS.DES.getColWidth());
		
		tab.addRow(row, new Label(tariff.isPurchase() ? "Compras" : "Ventas"), COLS.TYP.getColWidth());
		
		tab.addRow(row, new Label(formaDouble(tariff.getDiscount()) + " %"), COLS.DIS.getColWidth());
		
		tab.addRow(row, new Label(tariff.isActive() ? "Activo" : "Inactivo"), COLS.STA.getColWidth());
		
		tab.addRow(row, buttonContainer, COLS.BUT.getColWidth());
	}

	private static String formaDouble(double value) {
        // Round to two decimal places
        long scaledValue = Math.round(value * 100); // Scale to avoid floating-point precision issues
        long integerPart = scaledValue / 100;      // Extract integer part
        long decimalPart = scaledValue % 100;      // Extract decimal part

        // Format the result
        return integerPart + "." + (decimalPart < 10 ? "0" : "") + decimalPart /*+ " \u20ac"*/;
    }
	
	private void getList(Consumer<List<Tariff>> success) {
		COMMON_SERVICE.getTariffs(params, new AsyncCallback<List<Tariff>>() {
			
			@Override
			public void onSuccess(List<Tariff> tariffsDb) {
				success.accept(tariffsDb);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				AonMessagePanel.showError(messagePanel, "Error tarifas: " + caught.getMessage());
			}
		});
	}
	
	private void delete(Tariff tariff) {
		COMMON_SERVICE.deleteTariff(params.getDomainName(), params.getDomain(), params.getUser(), tariff.getId(), new AsyncCallback<Void>() {
			
			@Override
			public void onSuccess(Void result) {
				resetSearchOffset();
				onSearchData();
			}
			
			@Override
			public void onFailure(Throwable caught) {
				AonMessagePanel.showError(messagePanel, "Error borrado: " + caught.getMessage());
			}
		});
	}

	protected abstract void onTariffSelect(Tariff tariff);
	
}
