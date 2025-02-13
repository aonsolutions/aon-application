package com.esferalia.aon.gwt.fiscal.client.product;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog.AonAcceptDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.fiscal.client.product.ItemTariffPanel.ItemTariffCallback;
import com.esferalia.aon.gwt.fiscal.client.registry.RegistryModuleOptions;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.product.ItemTariff;
import com.esferalia.aon.occam.api.model.product.ItemTariffType;
import com.esferalia.aon.occam.api.model.product.Product;
import com.esferalia.aon.occam.api.model.product.Tariff;
import com.esferalia.aon.watson.mutable.MutableInt;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.logging.client.ConsoleLogHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;

public abstract class ItemTariffTable extends ScrollPanel{
	
	private static CommonServiceAsync COMMON_SERVICE;
	
	private static final Logger LOGGER = Logger.getLogger(CommonServiceAsync.class.getName());
	static { LOGGER.addHandler( new ConsoleLogHandler() ); }
	
	private final int limit = 100;
	private final MutableInt offset = new MutableInt(0);
	private final MutableInt moreData = new MutableInt(0);
	private final MutableInt searchEnabled = new MutableInt( 0 );
	
	private SimplePanel container;
	private AonCustomTable tab;
	private int lastScrollPos = 0;
	
	// Tariff
	private Product product;
	private Item item;
	private List<Tariff> tariffList;
	
	private RegistryModuleOptions options;
	
	private List<ItemTariff> itemTariffs;
	
	private static enum COLS {
		  TAR("Tarifa"								,"-moz-available", "white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, TYP("Tipo"								,"4rem", "")
		, APL("Aplica"								,"4rem", "")
		, PRI("Precio"								,"4rem", "")
		, PER("Porcent."							,"5rem", "")
		, NET("Neto"								,"4rem", "")
		, IVA("I.V.A. %"							,"4rem", "")
		, PVP("P.V.P."								,"4rem", "")
		, BUT(AonStringUtils.EMPTY					,"3rem", "")
		;

		String headerLabel;
		String colWidth;
		String colStyle;
		
		private COLS(String headerLabel, String colWidth, String colStyle) {
			this.headerLabel = headerLabel;
			this.colWidth = colWidth;
			this.colStyle = colStyle;
		}
		public String getColWidth() {
			return colWidth;
		}
		public String getColStyle() {
			return colStyle;
		}
		public String getHeaderLabel() {
			return headerLabel;
		}
	}
	
	public ItemTariffTable(RegistryModuleOptions options, Product product, Item item) {
		CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
		COMMON_SERVICE = new CommonServiceAsyncDecorator(commonServiceRaw);
		
		this.options = options;
		this.product = product;
		this.item = item;
		
		container = new SimplePanel();
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
	
	public void onSearch() {
		enableMoreData();
		search();
	}

	private void search() {
		container.clear();
		tab = new AonCustomTable();
		
		paintHeader();
		container.setWidget(tab);
		searchData();
	}
	
	private void paintHeader() {
		tab.createHeader();
		for ( COLS col : COLS.values()) 
			tab.addHeader(new Label(col.getHeaderLabel()), col.getColWidth());
	}
	
	private void searchData() {
		if (!isMoreData()) return;
		
		getList(itemTariffs -> {
			boolean something = false;
			
			for(ItemTariff itemTariff : itemTariffs) {
				something = true;
				paintRow(itemTariff);
			}
			
			if (itemTariffs.size() < limit) {
				disableMoreData();
			} else {
				offset.setValue(offset.intValue() + itemTariffs.size() - 1);
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
	
	private void paintRow(ItemTariff itemTariff) {
		FlowPanel buttonContainer = new FlowPanel();
		buttonContainer.getElement().getStyle().setTextAlign(TextAlign.RIGHT);
		
		if(itemTariff.getTariffType() == (byte)1) {
			AonTableButton button;
			button = new AonTableButton("Borrar documento", AON.CSS.aonIconDelete());
			button.addStyleName(AON.CSS.aonCustomRowButtom());
			button.addClickHandler( new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					event.stopPropagation();
					button.setEnabled(false);
					AonDialog dialog = new AonDialog("Eliminaci\u00f3n Documento",
							new HTML("Se va a proceder a eliminar la tarifa del producto <b>" + itemTariff.getTariff().getName() + "</b>.<br>\u00bfEsta seguro que desea proceder con la eliminaci\u00f3n\u003f. Este proceso ser\u00e1 irreversible"));
					
					dialog.confirm(new AonAcceptDialogCallback() {

						@Override
						public void onCancel() {
							button.setEnabled(true);
						}

						@Override
						public void onAccept() {
							deleteItemTariff(itemTariff.getId());
						}
					});
				}
			});
			buttonContainer.add(button);
		}
		
		HTMLPanel row = tab.createRow();
		
		row.addDomHandler(e -> onUpdateItemTariff(itemTariff), ClickEvent.getType());
		
		Label tariff = new Label(itemTariff.getTariff().getName());
		tariff.setTitle(itemTariff.getTariff().getName());
		tab.addInlineStyle(tariff, COLS.TAR.getColStyle());
		tab.addRow(row, tariff, COLS.TAR.getColWidth());
		
		tab.addRow(row, new Label(itemTariff.getTariffType() == (byte)0 ? "Tarifa" : "Manual"), COLS.TYP.getColWidth());
		
		tab.addRow(row, new Label(itemTariff.getType().getDescription()), COLS.APL.getColWidth());
		tab.addRow(row, new Label(formaDouble(itemTariff.getPrice())), COLS.PRI.getColWidth());
		tab.addRow(row, new Label(formaDouble(itemTariff.getProfitPercent())), COLS.PER.getColWidth());
		tab.addRow(row, new Label(formaDouble(getNeto(itemTariff.getProfitPercent(), itemTariff.getPrice()))), COLS.NET.getColWidth());
		tab.addRow(row, new Label(formaDouble(product.getVat().getPercentage())), COLS.IVA.getColWidth());
		tab.addRow(row, new Label(formaDouble(getPVP(product.getVat().getPercentage(), getNeto(itemTariff.getProfitPercent(), itemTariff.getPrice())))), COLS.PVP.getColWidth());
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
	
	private double getNeto(double percent, double price) {
		return price - (price * percent / 100);
	}
	
	private double getPVP(double percent, double price) {
		return price + (price * percent / 100);
	}
	
	private void onUpdateItemTariff(ItemTariff itemTariff) {
		final AonCustomDialog dialog = new AonCustomDialog();
		dialog.showCloseButton(true);
		dialog.setCaption("Tarifa Producto");
		
		ItemTariffPanel itemTariffPanel = new ItemTariffPanel(options, tariffList, product, item, itemTariff, new ItemTariffCallback() {
			
			@Override
			public void onAccept(ItemTariff itemTariff) {
				dialog.hide();
				onSearch();
			}
		});
		
		dialog.add( itemTariffPanel );
		dialog.showLoaded();
	}

	private void getTariffs(Consumer<List<Tariff>> success) {
		COMMON_SERVICE.getTariffs(options.getDomainName(), options.getDomain(), options.getUser(), new AsyncCallback<List<Tariff>>() {
			
			@Override
			public void onSuccess(List<Tariff> tariffsDb) {
				tariffList = tariffsDb;
				itemTariffs.addAll(tariffList.stream().map(tariff -> {
					return new ItemTariff()
						.setDomain(tariff.getDomain())
						.setItem(item.getId())
						.setTariff(tariff)
						.setTariffType((byte)0)
						.setType(ItemTariffType.SALE_BASE)
						.setProfitPercent(tariff.getDiscount())
						.setPrice(item.getPrice())
						;
					
				}).collect(Collectors.toList()));
				success.accept(tariffsDb);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				onShowErrorMessage("Error obteniendo tarifas: " + caught.getMessage());
			}
		});
	}
	
	private void getList(Consumer<List<ItemTariff>> success) {
		itemTariffs = new ArrayList<ItemTariff>();
		
		getTariffs(tariffs -> {
			COMMON_SERVICE.getItemTariffs(options.getDomainName(), options.getDomain(), options.getUser(), item.getId(), new AsyncCallback<List<ItemTariff>>() {
				
				@Override
				public void onSuccess(List<ItemTariff> itemTariffsDb) {
					itemTariffsDb.forEach(tariff -> tariff.setTariffType((byte)1));
					itemTariffs.addAll(itemTariffsDb);
					itemTariffs.sort((o1, o2) -> o1.getTariff().getName().compareTo(o2.getTariff().getName()));
					success.accept(itemTariffs);
				}
				
				@Override
				public void onFailure(Throwable caught) {
					onShowErrorMessage("Error obteniendo tarifas producto: " + caught.getMessage());
				}
			});
		});
	}
	
	private void deleteItemTariff(Integer id) {
		COMMON_SERVICE.deleteItemTariff(options.getDomainName(), options.getDomain(), options.getUser(), id, new AsyncCallback<Void>() {
			
			@Override
			public void onSuccess(Void deleted) {
				onSearch();
			}
			
			@Override
			public void onFailure(Throwable caught) {
				onShowErrorMessage("Error borrado tarifa producto: " + caught.getMessage());
			}
		});
	}

	protected abstract void onShowErrorMessage(String errorMessage);
}
