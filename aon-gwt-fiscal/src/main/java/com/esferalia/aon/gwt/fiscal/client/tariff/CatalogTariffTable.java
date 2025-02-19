package com.esferalia.aon.gwt.fiscal.client.tariff;

import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Logger;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog.AonAcceptDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.fiscal.client.registry.RegistryModuleOptions;
import com.esferalia.aon.gwt.fiscal.client.tariff.TariffCataloguePanel.TariffCatalogueCallback;
import com.esferalia.aon.occam.api.model.catalogue.Catalogue;
import com.esferalia.aon.occam.api.model.tariff.Tariff;
import com.esferalia.aon.occam.api.model.tariff.TariffCatalogue;
import com.esferalia.aon.watson.mutable.MutableInt;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.event.dom.client.ClickEvent;
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

public abstract class CatalogTariffTable extends ScrollPanel{
	
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
	
	private RegistryModuleOptions options;
	
	private Tariff tariff;
	
	private static enum COLS {
		  CAT("Cat\u00e1logo"						,"-moz-available", "white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
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
	
	public CatalogTariffTable(RegistryModuleOptions options, Tariff tariff) {
		CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
		COMMON_SERVICE = new CommonServiceAsyncDecorator(commonServiceRaw);
		
		this.options = options;
		this.tariff = tariff;
		
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
		
		getList(tariffCatalogueList -> {
			boolean something = false;
			
			for(TariffCatalogue tariffCatalogue : tariffCatalogueList) {
				something = true;
				paintRow(tariffCatalogue);
			}
			
			if (tariffCatalogueList.size() < limit) {
				disableMoreData();
			} else {
				offset.setValue(offset.intValue() + tariffCatalogueList.size() - 1);
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

	private void paintRow(TariffCatalogue tariffCatalogue) {
		FlowPanel buttonContainer = new FlowPanel();
		buttonContainer.getElement().getStyle().setTextAlign(TextAlign.RIGHT);
		
		AonTableButton deleteBtn = new AonTableButton("Borrar Otro Dato", AON.CSS.aonIconDelete());
		deleteBtn.addStyleName(AON.CSS.aonCustomRowButtom());
		deleteBtn.addClickHandler(e -> {
			e.stopPropagation();
			deleteBtn.setEnabled(false);
			AonDialog dialog = new AonDialog("Eliminaci\u00f3n Cat\u00e1logo Tarifa",
					new HTML("Se va a proceder a eliminar el cat\u00e1logo <b>" + tariffCatalogue.getCatalogue().getName() + "</b> de la tarifa.<br>\u00bfEsta seguro que desea proceder con la eliminaci\u00f3n\u003f. Este proceso ser\u00e1 irreversible"));
			
			dialog.confirm(new AonAcceptDialogCallback() {

				@Override
				public void onCancel() {
					deleteBtn.setEnabled(true);
				}

				@Override
				public void onAccept() {
					deleteTariffCatalogue(tariffCatalogue.getId());
				}
			});
		});
		buttonContainer.add(deleteBtn);
		
		HTMLPanel row = tab.createRow();
		
		row.addDomHandler(e -> onUpdateTariffAddInfo(tariffCatalogue), ClickEvent.getType());
		
		Label catalogue = new Label(tariffCatalogue.getCatalogue().getName());
		catalogue.setTitle(tariffCatalogue.getCatalogue().getName());
		tab.addInlineStyle(catalogue, COLS.CAT.getColStyle());
		tab.addRow(row, catalogue, COLS.CAT.getColWidth());
		
		tab.addRow(row, buttonContainer, COLS.BUT.getColWidth());
	}

	private void onUpdateTariffAddInfo(TariffCatalogue tariffCatalogue) {
		getCatalogueList(catalogues -> {
			AonCustomDialog dialog = new AonCustomDialog();
			dialog.showCloseButton(true);
			dialog.setCaption("Otro Dato");
			
			TariffCataloguePanel tariffAddInfoPanel = new TariffCataloguePanel(options, catalogues, tariffCatalogue, new TariffCatalogueCallback() {
				
				@Override
				public void onAccept(TariffCatalogue TtriffCatalogue) {
					dialog.hide();
					onSearch();
				}
			});
			
			dialog.add( tariffAddInfoPanel );
			dialog.showLoaded();
		});
	}
	
	private void getList(Consumer<List<TariffCatalogue>> success) {
		COMMON_SERVICE.getTariffCatalgueList(options.getDomainName(), options.getDomain(), options.getUser(), tariff.getId(), new AsyncCallback<List<TariffCatalogue>>() {
			
			@Override
			public void onSuccess(List<TariffCatalogue> tariffCatalgueList) {
				success.accept(tariffCatalgueList);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				onShowErrorMessage("Error obteniendo tarifas producto: " + caught.getMessage());
			}
		});
	}
	
	private void deleteTariffCatalogue(Integer id) {
		COMMON_SERVICE.deleteTariffCatalogue(options.getDomainName(), options.getDomain(), options.getUser(), id, new AsyncCallback<Void>() {
			
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
	
	private void getCatalogueList(Consumer<List<Catalogue>> success) {
		COMMON_SERVICE.getCatalogueList(options.getDomainName(), options.getDomain(), options.getUser(), new AsyncCallback<List<Catalogue>>() {
			
			@Override
			public void onSuccess(List<Catalogue> catalogues) {
				success.accept(catalogues);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				onShowErrorMessage("Error obteniendo tarifas: " + caught.getMessage());
			}
		});
	}

	protected abstract void onShowErrorMessage(String errorMessage);
}
