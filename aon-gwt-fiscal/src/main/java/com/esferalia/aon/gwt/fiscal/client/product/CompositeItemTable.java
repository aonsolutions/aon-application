package com.esferalia.aon.gwt.fiscal.client.product;

import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog.AonCustomDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog.AonAcceptDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.fiscal.client.product.ItemCompositionPanel.ItemCompositionCallback;
import com.esferalia.aon.gwt.fiscal.client.registry.RegistryModuleOptions;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.product.ItemComposition;
import com.esferalia.aon.occam.api.model.product.Product;
import com.esferalia.aon.occam.api.model.type.ProductType;
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

public abstract class CompositeItemTable extends ScrollPanel{
	
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
	private Item item;
	private List<Item> itemList;
	
	private RegistryModuleOptions options;
	
	private List<ItemComposition> itemCompositions;
	
	private static enum COLS {
		  
		  ORD("Orden"								,"5rem", "")
		, ITE("Item"								,"-moz-available", "white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, CAN("Cantidad"							,"5rem", "")
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
	
	public CompositeItemTable(RegistryModuleOptions options, Product product, Item item) {
		CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
		COMMON_SERVICE = new CommonServiceAsyncDecorator(commonServiceRaw);
		
		this.options = options;
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
		
		getList(itemCompositions -> {
			boolean something = false;
			
			for(ItemComposition itemComposition : itemCompositions) {
				something = true;
				paintRow(itemComposition);
			}
			
			if (itemCompositions.size() < limit) {
				disableMoreData();
			} else {
				offset.setValue(offset.intValue() + itemCompositions.size() - 1);
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
	
	private void paintRow(ItemComposition itemComposition) {
		FlowPanel buttonContainer = new FlowPanel();
		buttonContainer.getElement().getStyle().setTextAlign(TextAlign.RIGHT);
		
		AonTableButton button;
		button = new AonTableButton("Borrar documento", AON.CSS.aonIconDelete());
		button.addStyleName(AON.CSS.aonCustomRowButtom());
		button.addClickHandler( new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				event.stopPropagation();
				button.setEnabled(false);
				AonDialog dialog = new AonDialog("Eliminaci\u00f3n Producto",
						new HTML("Se va a proceder a eliminar el producto compuesto <b>" + itemComposition.getDescription() + "</b>.<br>\u00bfEsta seguro que desea proceder con la eliminaci\u00f3n\u003f. Este proceso ser\u00e1 irreversible"));
				
				dialog.confirm(new AonAcceptDialogCallback() {

					@Override
					public void onCancel() {
						button.setEnabled(true);
					}

					@Override
					public void onAccept() {
						deleteItemComposition(itemComposition.getId());
					}
				});
			}
		});
		buttonContainer.add(button);
		
		
		HTMLPanel row = tab.createRow();
		
		row.addDomHandler(e -> onUpdateItemComposition(itemComposition), ClickEvent.getType());
		
		tab.addRow(row, new Label(itemComposition.getSequence().toString()), COLS.ORD.getColWidth());
		
		Label tariff = new Label(itemComposition.getDescription());
		tariff.setTitle(itemComposition.getDescription());
		tab.addInlineStyle(tariff, COLS.ITE.getColStyle());
		tab.addRow(row, tariff, COLS.ITE.getColWidth());
		
		tab.addRow(row, new Label(formaDouble(itemComposition.getQuantity())), COLS.CAN.getColWidth());
		
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
	
	private void onUpdateItemComposition(ItemComposition itemComposition) {
		AonCustomDialog dialog = new AonCustomDialog();
		dialog.showCloseButton(true);
		dialog.setCaption("Item Compuesto");
		
		ItemCompositionPanel itemTariffPanel = new ItemCompositionPanel(options, itemList, itemCompositions, item, itemComposition, new ItemCompositionCallback() {
			
			@Override
			public void onAccept(ItemComposition itemComposition) {
				dialog.hide();
				onSearch();
			}
		});
		
		dialog.add( itemTariffPanel );
		dialog.showLoadedCB(new AonCustomDialogCallback() {
			
			@Override
			public void onEnd() {
				dialog.center();
				dialog.show();
			}
		});
	}

	private void getItems(Consumer<List<Item>> success) {
		COMMON_SERVICE.getItems(options.getDomainName(), options.getDomain(), options.getUser(), ProductType.AUXILIARY, new AsyncCallback<List<Item>>() {
			
			@Override
			public void onSuccess(List<Item> itemsDb) {
				itemList = itemsDb;
				success.accept(itemList);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				onShowErrorMessage("Error obteniendo items : " + caught.getMessage());
			}
		});
	}
	
	private void getList(Consumer<List<ItemComposition>> success) {
		getItems(tariffs -> {
			COMMON_SERVICE.getItemCompositions(options.getDomainName(), options.getDomain(), options.getUser(), item.getId(), new AsyncCallback<List<ItemComposition>>() {
				
				@Override
				public void onSuccess(List<ItemComposition> itemCompositionsDb) {
					itemCompositions = itemCompositionsDb;
					success.accept(itemCompositions);
				}
				
				@Override
				public void onFailure(Throwable caught) {
					onShowErrorMessage("Error obteniendo item compuesto : " + caught.getMessage());
				}
			});
		});
	}
	
	private void deleteItemComposition(Integer id) {
		COMMON_SERVICE.deleteItemComposition(options.getDomainName(), options.getDomain(), options.getUser(), id, new AsyncCallback<Void>() {
			
			@Override
			public void onSuccess(Void deleted) {
				onSearch();
			}
			
			@Override
			public void onFailure(Throwable caught) {
				onShowErrorMessage("Error borrado item compuesto : " + caught.getMessage());
			}
		});
	}
	
	public void deleteCompositions(Consumer<Void> end) {
		COMMON_SERVICE.deleteItemCompositions(options.getDomainName(), options.getDomain(), options.getUser(), itemCompositions.stream().map(itemComposition -> itemComposition.getId()).collect(Collectors.toList()), new AsyncCallback<Void>() {
			@Override
			public void onSuccess(Void result) {
				end.accept(result);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				onShowErrorMessage("Error borrado item compuesto : " + caught.getMessage());
			}
		});
	}
	
	protected abstract void onShowErrorMessage(String errorMessage);
}
