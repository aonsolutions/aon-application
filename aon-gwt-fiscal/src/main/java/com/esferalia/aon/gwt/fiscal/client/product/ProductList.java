package com.esferalia.aon.gwt.fiscal.client.product;

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
import com.esferalia.aon.gwt.fiscal.client.product.ProductPanel.AonProductPanelCallback;
import com.esferalia.aon.gwt.fiscal.client.registry.RegistryModuleOptions;
import com.esferalia.aon.occam.api.model.product.Product;
import com.esferalia.aon.occam.api.model.product.ProductCategory;
import com.esferalia.aon.occam.api.model.product.ProductParams;
import com.esferalia.aon.occam.api.model.product.ProductStatus;
import com.esferalia.aon.occam.api.model.type.ProductType;
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

public abstract class ProductList extends AonCustomDockLayout {
	
	private static CommonServiceAsync COMMON_SERVICE;
	
	// ProductList UI

	private HTMLPanel container;
	private HTMLPanel messagePanel = new HTMLPanel("");
	
	private AonCustomListBox category = new AonCustomListBox("Categor\u00eda");
	private AonCustomListBox type = new AonCustomListBox("Tipo");
	private AonCustomListBox status = new AonCustomListBox("Estado");
	
	private AonCustomListBox sort = new AonCustomListBox("Ordenar Por");
	private AonCustomListBox asc = new AonCustomListBox("Orden");
	
	private SimplePanel tableContainer;
	private ScrollPanel tableScrollPanel;
	private AonCustomTable tab;
	
	private RegistryModuleOptions options;
	
	// Table UI
	private final int limit = 100;
	private final MutableInt offset = new MutableInt(0);
	private final MutableInt moreData = new MutableInt(0);
	private final MutableInt searchEnabled = new MutableInt( 0 );
	
	private int lastScrollPos = 0;
	
	private ProductParams params;

	private List<ProductCategory> productCategories;
	
	private boolean fetchingData = false;
	
	private static enum COLS {
		  COD(AON.MSG.code()						,"15rem"  			,"white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, DES(AON.MSG.description()					,"-moz-available"  	,"min-width: 5rem; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, CAT(AON.MSG.category()					,"15rem" 			,"white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, STA("Estado"								,"7rem" 			,"white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, PCK("Tipo"								,"5rem" 			,"white-space: nowrap; overflow: hidden; text-overflow: ellipsis;") // Pack o servicio
		, COM("Compuesto"							,"6rem" 			,"white-space: nowrap; overflow: hidden; text-overflow: ellipsis;") // Pack o servicio
		, BUT(AonStringUtils.EMPTY					,"3rem" 			,"")
		;
		
		// Añadir al filtro

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
	public ProductList(RegistryModuleOptions options) {
		super("SERVICIOS AON");
		
		CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
		COMMON_SERVICE = new CommonServiceAsyncDecorator(commonServiceRaw);
		
		this.options = options;
		
		addButtonsToolbar();
		
		hideToolbarFilterMessages();
		setSearchPlaceholder("Busque por c\u00f3digo / Descripci\u00f3n ...");
		addOnSearchHandler(e -> { if(!fetchingData) onSearch(); });
		
		type.clearItems();
		type.addItem("-", "");
		type.addItem("Servicion Aon", "false");
		type.addItem("Pack", "true");
		type.getListBox().addChangeHandler(event -> onSearch());
		
		addFilterWidget(type);
		
		status.clearItems();
		status.addItem("-", "");
		status.addItem(ProductStatus.ACTIVE.getDescription(), ProductStatus.ACTIVE.ordinal() + "");
		status.addItem(ProductStatus.DISCONTINUED.getDescription(), ProductStatus.DISCONTINUED.ordinal() + "");
		status.getListBox().addChangeHandler(event -> onSearch());
		
		addFilterWidget(status);
		
		category.clearItems();
		category.addItem( "Todas", "");
		getProductCategories(productCategories ->
			productCategories.forEach(productCategory -> category.addItem(productCategory.getName(), productCategory.getId().toString()))
		);
		category.getListBox().addChangeHandler(event -> onSearch());
		
		addFilterWidget(category);
		
		sort.addItem("C\u00f3digo", "code");
		sort.addItem("Nombre", "name");
		sort.addItem("Categor\u00eda", "category");
		sort.addItem("Compuesto", "composite");
		sort.addItem("Estado", "status");
		sort.addItem("Pack", "pack");
		sort.setValue("pack");
		sort.getListBox().addChangeHandler(event -> onSearch());
		
		asc.addItem("Ascendente", "true");
		asc.addItem("Descendete", "false");
		asc.setValue("false");
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
		category.getListBox().setSelectedIndex(0);
		
		resetSearchOffset();
		
		onSearch();
	}
	
	private void resetSearchOffset() {
		offset.setValue(0);
	}
	
	private void addButtonsToolbar() {
		AonToolbarButton newButton = new AonToolbarButton( "Nuevo Servicio Aon", AON.CSS.aonIconAdd());
		newButton.addClickHandler(e -> showProductDialog());
		addToolbarButton(newButton);
		
		AonToolbarButton catalogue = new AonToolbarButton( "Cat\u00e1logo", AON.CSS.aonIconCatalogue());
		catalogue.addClickHandler(e -> onCatalogueShow());
		addToolbarButton(catalogue);
	}
	
	private void showProductDialog() {
		final AonCustomDialog dialog = new AonCustomDialog();
		dialog.setCaption( "NUEVO PRODUCTO" );
		dialog.showCloseButton(true);
		ProductPanel productPanel = new ProductPanel(options, new AonProductPanelCallback(){

			@Override
			public void onAccept(Product product) {
				dialog.hide();
				onSearch();
			}
		
		});
			
		dialog.add( productPanel );
		dialog.showLoadedCB(new AonCustomDialogCallback() {
			
			@Override
			public void onEnd() {
				productPanel.focusCode();
			}
		});
	}

	public void onSearch() {
		getWidgetParams();
		resetSearchOffset();
		onSearchData();
	}

	public void getWidgetParams() {
		params = new ProductParams()
			.setDomainName(options.getDomainName())
			.setDomain(options.getDomain())
			.setUser(options.getUser())
			.setType(ProductType.AUXILIARY)
			.setDescription(getSearchTextBox().getValue())
			.setProductComposition(AonStringUtils.isBlank(type.getValue()) ? null : Boolean.parseBoolean(type.getValue()))
			.setStatus(AonStringUtils.isBlank(status.getValue()) ? null : ProductStatus.safeValueOf(Byte.parseByte(status.getValue())))
			.setCategory(AonStringUtils.isBlank(category.getValue()) ? null : Integer.parseInt(category.getValue()))
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
		fetchingData = true;
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
		
		getList(products -> {
			boolean something = false;
			
			for(Product product : products) {
				something = true;
				paintRow(product);
			}
			
			if (products.size() < limit) {
				disableMoreData();
			} else {
				offset.setValue(offset.intValue() + products.size() - 1);
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
			fetchingData = false;
		});
	}
	
	private void paintRow(Product product) {
		FlowPanel buttonContainer = new FlowPanel();
		buttonContainer.getElement().getStyle().setTextAlign(TextAlign.CENTER);
		
		AonTableButton button;
		button = new AonTableButton("Borrar Producto", AON.CSS.aonIconDelete());
		button.addStyleName(AON.CSS.aonCustomRowButtom());
		button.addClickHandler(event -> {
			event.stopPropagation();
			button.setEnabled(false);
			AonDialog dialog = new AonDialog("Eliminaci\u00f3n Producto",
					new HTML("Se va a proceder a eliminar el producto <b>" + product.getName() + "</b>.<br>\u00bfEsta seguro que desea proceder con la eliminaci\u00f3n\u003f. Este proceso ser\u00e1 irreversible"));
			
			dialog.confirm(new AonAcceptDialogCallback() {

				@Override
				public void onCancel() {
					button.setEnabled(true);
				}

				@Override
				public void onAccept() {
					delete(product);
				}
			});
		});
		buttonContainer.add(button);
		
		HTMLPanel row = tab.createRow();
		row.addDomHandler(e -> onProductSelect(product), ClickEvent.getType());
		
		tab.addRow(row, new Label(product.getCode()), COLS.COD.getColWidth());
		
		Label description = new Label(product.getName());
		description.setTitle(product.getName());
		tab.addInlineStyle(description, COLS.DES.getCellStyleClass());
		tab.addRow(row, description, COLS.DES.getColWidth());
		
		tab.addRow(row, new Label(null == product.getCategory() ? "" : product.getCategory().getName()), COLS.CAT.getColWidth());
		
		tab.addRow(row, new Label(null == product.getStatus() ? "" : product.getStatus().getDescription()), COLS.STA.getColWidth());
		
		tab.addRow(row, new Label(product.isManufactured() ? "Pack" : "Servicio"), COLS.PCK.getColWidth());
		
		tab.addRow(row, new Label(product.isComposition() ? "Si" : "No"), COLS.COM.getColWidth());
		
		tab.addRow(row, buttonContainer, COLS.BUT.getColWidth());
	}
	
	private void getList(Consumer<List<Product>> success) {
		COMMON_SERVICE.getProducts(params, new AsyncCallback<List<Product>>() {
			
			@Override
			public void onSuccess(List<Product> products) {
				success.accept(products);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				AonMessagePanel.showError(messagePanel, "Error productos: " + caught.getMessage());
			}
		});
	}
	
	private void delete(Product product) {
		COMMON_SERVICE.deleteProduct(params.getDomainName(), params.getDomain(), params.getUser(), product.getId(), new AsyncCallback<Void>() {
			
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
	
	private void getProductCategories(Consumer<List<ProductCategory>> success) {
		COMMON_SERVICE.getProductCategories(options.getDomainName(), options.getDomain(), options.getUser(), new AsyncCallback<List<ProductCategory>>() {
			
			@Override
			public void onSuccess(List<ProductCategory> productCategoriesDb) {
				productCategories = productCategoriesDb;
				success.accept(productCategories);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				AonMessagePanel.showError(messagePanel, "Error obteniendo producto: " + caught.getMessage());
			}
		});
	}
	
	protected abstract void onProductSelect(Product product);
	protected abstract void onCatalogueShow();
	
}
