package com.esferalia.aon.gwt.fiscal.client.product;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomCard;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomCheckBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDockLayout;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomMultiSelectBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomNumberBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomTextBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog.AonAcceptDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.fiscal.client.registry.RegistryModuleOptions;
import com.esferalia.aon.occam.api.model.aonsolutions.AonApp;
import com.esferalia.aon.occam.api.model.office.Tag;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.product.Product;
import com.esferalia.aon.occam.api.model.product.ProductCategory;
import com.esferalia.aon.occam.api.model.product.ProductTag;
import com.esferalia.aon.occam.api.model.product.Tax;
import com.esferalia.aon.occam.api.model.type.DomainType;
import com.esferalia.aon.occam.api.model.type.TaxType;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public abstract class ProductEntry extends AonCustomDockLayout {
	
	// ------------------------------------------------- CommonServiceAsync
	
	static CommonServiceAsync commonService;
	
	private static void initializeCommonService() {
		if (commonService == null) {
			CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
			commonService = new CommonServiceAsyncDecorator(commonServiceRaw);
		}
	}
	
	// ------------------------------------------------- Variables
	
	private final String EMPTY_STRING = "";
	
	private HTMLPanel container;
	
	private HTMLPanel messagePanel = new HTMLPanel(EMPTY_STRING);
	
	private AonCustomCard generalCard = new AonCustomCard("Informaci\u00f3n General");
	private ProductStatusSelect status;
	private AonCustomTextBox code = new AonCustomTextBox("C\u00f3digo");
	private AonCustomTextBox barcode = new AonCustomTextBox("C\u00f3digo Barras");
	private AonCustomTextBox name = new AonCustomTextBox("Descripci\u00f3n");
	private AonCustomNumberBox price = new AonCustomNumberBox("Precio");
	private AonCustomListBox iva = new AonCustomListBox("IVA");
	private AonCustomNumberBox pvp = new AonCustomNumberBox("P.V.P.");
	
	private AonCustomCard aditionalCard = new AonCustomCard("Informaci\u00f3n Adicional");
	private AonCustomListBox app = new AonCustomListBox("Servicio");
	private AonCustomMultiSelectBox domainType = new AonCustomMultiSelectBox("Tipo Dominio");
	private AonCustomCheckBox console = new AonCustomCheckBox("Console");
	private AonCustomListBox category = new AonCustomListBox("Categor\u00eda");
	private AonCustomMultiSelectBox tags = new AonCustomMultiSelectBox("Etiquetas");
	private AonCustomCheckBox composite = new AonCustomCheckBox("Pack");
	
	private RegistryModuleOptions options;
	
	private Product product;
	private Item item;
	private List<Tax> taxes;
	private List<ProductCategory> productCategories;
	
	// Tags
	private List<Tag> tagList;
	
	public ProductEntry(RegistryModuleOptions options) {
		super("SERVICIO AON");
		
		this.options = options;
		initializeCommonService();
		
		addButtonsToolbar();
		hideSearchWidget();
		hideToolbarFilterMessages();
		
		container = new HTMLPanel(EMPTY_STRING);
		container.addStyleName(AON.CSS.aonItemFlex());
		container.addStyleName(AON.CSS.aonFlexColumn());
		
		messagePanel.addStyleName(AON.CSS.aonWidthAll());
		
		container.add(messagePanel);
		
		add(container);
	}
	
	@Override
	protected void onClearFilter() {}
	
	private void addButtonsToolbar() {
		AonToolbarButton backButton = new AonToolbarButton(AON.MSG.backAction(), AON.CSS.aonIconBack());
		backButton.addClickHandler(e -> onBackClick());
		addToolbarButton(backButton);
		
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
					delete(product.getId());
				}
			});
		});
		
		AonToolbarButton deleteButton = new AonToolbarButton( "Borrar Agente Comercial", AON.CSS.aonIconDelete());
		deleteButton.addClickHandler(e -> {
			deleteButton.setEnabled(false);
			AonDialog dialog = new AonDialog("Eliminaci\u00f3n Producto",
					new HTML("Se va a proceder a eliminar el producto <b>" + product.getName() + "</b>.<br>\u00bfEsta seguro que desea proceder con la eliminaci\u00f3n\u003f. Este proceso ser\u00e1 irreversible"));
			
			dialog.confirm(new AonAcceptDialogCallback() {

				@Override
				public void onCancel() {
					deleteButton.setEnabled(true);
				}

				@Override
				public void onAccept() {
					delete(product.getId());
				}
			});
			
		});
		addToolbarButton(deleteButton);
		
		AonToolbarButton saveButton = new AonToolbarButton("Guardar Producto", AON.CSS.aonIconSave());
		saveButton.addClickHandler(e -> saveProduct());
		addToolbarButton(saveButton);
	}

	public void setProduct(Integer productId) {
		getProduct(productId, dbProduct -> {
			
			remove(container);
			
			container = new HTMLPanel(EMPTY_STRING);
			container.addStyleName(AON.CSS.aonItemFlex());
			container.addStyleName(AON.CSS.aonFlexColumn());
			
			messagePanel.addStyleName(AON.CSS.aonWidthAll());
			
			container.add(messagePanel);
			
			FlowPanel cardsRow = createRow(createGeneralCard(), createAditionalCard());
			cardsRow.getElement().getStyle().setProperty("align-items", "start");
			
			container.getElement().getStyle().setProperty("padding", "0 1rem");
			container.add(cardsRow);
			
			add(container);
			
			Scheduler.get().scheduleDeferred(new Command() {
		        public void execute() {
		        	name.setFocus(true);
		        }
		    });
			
		});
	}

	private AonCustomCard createGeneralCard() {
		FlowPanel table = createFlexColumnPanel();
		
		status = new ProductStatusSelect(product.getStatus());
		
		generalCard = new AonCustomCard("Informaci\u00f3n General", status);
		generalCard.setToolbarWidgetShown();
		generalCard.addStyleName(AON.CSS.aonWidthAll());
		generalCard.add(table);
		
		code.setValue(product.getCode());
		code.addValueChangeHandler(e -> product.setCode(code.getValue()));
		
		barcode.setEnable(false);
		barcode.setValue(item.getBarcode());
		
		table.add(createRow(code, barcode));
		
		name.setValue(product.getName());
		name.addValueChangeHandler(e -> {
			product.setName(name.getValue());
			item.setDescription(name.getValue());
		});
		
		table.add(createRow(name, null));
		
		app.clearItems();
		app.addItem("-", "00");
		AonApp.getValues().forEach(appIt -> app.addItem(appIt.getDescription(), AonStringUtils.leftPad(appIt.ordinal() + "", 2, "0") ));
		app.setValue(AonStringUtils.leftPad(AonStringUtils.substring(item.getBarcode(), 0, 2), 2, "0"));
		app.addChangeHandler(e -> createBarCode());
		
		table.add(createRow(app, null));
		
		price.hideNearBy();
		price.setValue(item.getPrice());
		price.addValueChangeHandler(e -> {
			item.setPrice(price.getValue());
			pvp.setValue(createPVP());
		});
		
		iva.clearItems();
		getTaxTypes(taxTypes -> {
			taxTypes.forEach(taxType -> iva.addItem(taxType.getName(), taxType.getId().toString()));
			iva.setValue(null == product.getVat() ? "" : product.getVat().getId().toString());
			
			pvp.setValue(createPVP());
		});
		iva.addChangeHandler(e -> {
			product.setVat(new Tax().setId(Integer.parseInt(iva.getValue())));
			pvp.setValue(createPVP());
		});
		
		pvp.hideNearBy();
		pvp.setEnable(false);
		
		table.add(createRow(price, iva, pvp));
		
		return generalCard;
	}

	private AonCustomCard createAditionalCard() {
		FlowPanel table = createFlexColumnPanel();
		
		aditionalCard = new AonCustomCard("Informaci\u00f3n Adicional");
		aditionalCard.addStyleName(AON.CSS.aonWidthAll());
		aditionalCard.add(table);
		
		domainType.setOptions(DomainType.getValues().stream().map(domainType -> domainType.getName()).collect(Collectors.toSet()));
		domainType.setSelectedOptions(getSelectedDomainTypes());
		domainType.addBlurHandler(e -> createBarCode());
		
		table.add(createRow(domainType, null));
		
		category.clearItems();
		category.addItem("-", "");
		getProductCategories(productCategories -> {
			productCategories.forEach(productCategory -> category.addItem(productCategory.getName(), productCategory.getId().toString()));
			category.setValue(null == product.getCategory() || null == product.getCategory().getId() ? "" : product.getCategory().getId().toString());
		});
		category.addChangeHandler(e -> product.setCategory(AonStringUtils.isBlank(category.getValue()) ? null : productCategories.stream().filter(pc -> pc.getId().equals(Integer.parseInt(category.getValue()))).findFirst().get()));
		
		// TAGS
		getTags(aviableTags -> {
			tags.setOptions(aviableTags.stream().map(aviableTag -> aviableTag.getName()).collect(Collectors.toSet()));
			
			getProductTags(product.getId(), productTags -> {
				Set<String> selectedProductTags = productTags.stream().map(productTag -> productTag.getTag().getName()).collect(Collectors.toSet());
				tags.setSelectedOptions(selectedProductTags);
			});
		});
		
		table.add(createRow(category, tags));
		
		console.setValue(isConsole());
		console.addValueChangeHandler(e -> {
			if(!console.getValue() && domainType.getSelectedOptions().contains("Administraci\u00F3n")) {
				Set<String> selectedOptions = domainType.getSelectedOptions();
				selectedOptions.remove("Administraci\u00F3n");
				domainType.setSelectedOptions(selectedOptions);
			} else if(console.getValue() && !domainType.getSelectedOptions().contains("Administraci\u00F3n")) {
				Set<String> selectedOptions = domainType.getSelectedOptions();
				selectedOptions.add("Administraci\u00F3n");
				domainType.setSelectedOptions(selectedOptions);
			}
			createBarCode();
		});
		
		composite.setValue(null == product.getComposition() ? false : product.getComposition());
		composite.addValueChangeHandler(e -> product.setComposition(composite.getValue()));
		
		table.add(createRow(console, composite));
		
		return aditionalCard;
	}

	private Set<String> getSelectedDomainTypes() {
		Set<String> domainTypes = new HashSet<String>();
		if(AonStringUtils.equalsIgnoreCase("1", AonStringUtils.substring(item.getBarcode(), 2, 3))) domainTypes.add("Empresa");
		if(AonStringUtils.equalsIgnoreCase("1", AonStringUtils.substring(item.getBarcode(), 3, 4))) domainTypes.add("Asesor\u00EDa");
		if(AonStringUtils.equalsIgnoreCase("1", AonStringUtils.substring(item.getBarcode(), 4, 5))) domainTypes.add("Garaje");
		if(AonStringUtils.equalsIgnoreCase("1", AonStringUtils.substring(item.getBarcode(), 5, 6))) domainTypes.add("Academ\u00EDa");
		if(AonStringUtils.equalsIgnoreCase("1", AonStringUtils.substring(item.getBarcode(), 6, 7))) domainTypes.add("Hotel");
		if(AonStringUtils.equalsIgnoreCase("1", AonStringUtils.substring(item.getBarcode(), 7, 8))) domainTypes.add("Administraci\u00F3n");
		if(AonStringUtils.equalsIgnoreCase("1", AonStringUtils.substring(item.getBarcode(), 8, 9))) domainTypes.add("Despacho");
		if(AonStringUtils.equalsIgnoreCase("1", AonStringUtils.substring(item.getBarcode(), 9, 10))) domainTypes.add("Gen\u00E9rico");
		if(AonStringUtils.equalsIgnoreCase("1", AonStringUtils.substring(item.getBarcode(), 10, 11))) domainTypes.add("Comercio");
		if(AonStringUtils.equalsIgnoreCase("1", AonStringUtils.substring(item.getBarcode(), 11, 12))) domainTypes.add("Kit Digital");
		
		return domainTypes;
	}

	private Double createPVP() {
		// GENERAL
		return item.getPrice() + (item.getPrice() * getVatPercentage() / 100);
	}

	private double getVatPercentage() {
		Optional<Tax> taxOpt = taxes.stream().filter(tax -> AonStringUtils.equalsIgnoreCase(tax.getId().toString(), iva.getValue())).findFirst();
		return taxOpt.isEmpty() ? 0.00 : taxOpt.get().getPercentage();
	}

	private boolean isConsole() {
		return AonStringUtils.isBlank(item.getBarcode()) ? false : AonStringUtils.equalsIgnoreCase("1", AonStringUtils.substring(item.getBarcode(), 7, 8));
	}

	private String createBarCode() {
		String barCode = app.getValue();
	
		barCode += domainType.getSelectedOptions().contains("Empresa") ? "1" : "0";
		barCode += domainType.getSelectedOptions().contains("Asesor\u00EDa") ? "1" : "0";
		barCode += domainType.getSelectedOptions().contains("Garaje") ? "1" : "0";
		barCode += domainType.getSelectedOptions().contains("Academ\u00EDa") ? "1" : "0";
		barCode += domainType.getSelectedOptions().contains("Hotel") ? "1" : "0";
		barCode += console.getValue() || domainType.getSelectedOptions().contains("Administraci\u00F3n") ? "1" : "0";
		barCode += domainType.getSelectedOptions().contains("Despacho") ? "1" : "0";
		barCode += domainType.getSelectedOptions().contains("Gen\u00E9rico") ? "1" : "0";
		barCode += domainType.getSelectedOptions().contains("Comercio") ? "1" : "0";
		barCode += domainType.getSelectedOptions().contains("Kit Digital") ? "1" : "0";
		
		barcode.setValue(barCode);
		item.setBarcode(barCode);
		
		return barCode;
	}

	private FlowPanel createFlexColumnPanel() {
        FlowPanel panel = new FlowPanel();
        panel.addStyleName(AON.CSS.aonItemFlex());
        panel.addStyleName(AON.CSS.aonFlexColumn());
        panel.setWidth("100%");
        return panel;
    }
	
	private FlowPanel createRow(Widget widget1, Widget widget2) {
        FlowPanel row = createFlexPanel();
        row.add(widget1);
        if (widget2 != null) {
            row.add(widget2);
        }
        return row;
    }
	
	private FlowPanel createRow(Widget widget1, Widget widget2, Widget widget3) {
        FlowPanel row = createFlexPanel();
        row.add(widget1);
        if (widget2 != null) row.add(widget2);
        if (widget3 != null) row.add(widget3);
        return row;
    }
	
	private FlowPanel createFlexPanel() {
        FlowPanel panel = new FlowPanel();
        panel.addStyleName(AON.CSS.aonItemFlex());
        panel.setWidth("100%");
        return panel;
    }
	
	private List<ProductTag> getProductTags() {
		Set<String> selectedTags = tags.getSelectedOptions();
		
		List<ProductTag> productTags = new ArrayList<ProductTag>();
		
		selectedTags.forEach(selectedTag -> {
			Optional<Tag> tagOpt = tagList.stream().filter(tag -> AonStringUtils.equalsIgnoreCase(tag.getName(), selectedTag)).findFirst();
			if(tagOpt.isPresent()) {
				productTags.add(
					new ProductTag()
						.setDomain(options.getDomain())
						.setProduct(product.getId())
						.setTag(tagOpt.get())
				);
			}
		});
		
		return productTags;
	}
	
	private void saveProduct() {
		// Update Status (should use handler for this)
		product.setStatus(status.getValue());
		item.setStatus(status.getValue());
		
		AonMessagePanel.showLoading(messagePanel, "Guardando producto " + product.getName());
		
		commonService.saveProduct(options.getDomainName(), options.getDomain(), options.getUser(), product, new AsyncCallback<Product>() {
			
			@Override
			public void onSuccess(Product productDB) {
				product = productDB;
				
				AonMessagePanel.showLoading(messagePanel, "Guardando etiquetas producto " + product.getName());
				
				commonService.saveProductTags(options.getDomainName(), options.getDomain(), options.getUser(), product.getId(), getProductTags(), new AsyncCallback<Void>() {

					@Override
					public void onFailure(Throwable caught) {
						AonMessagePanel.showError(messagePanel, "Error guardado etiquetas producto: " + caught.getMessage());
					}

					@Override
					public void onSuccess(Void arg0) {
						AonMessagePanel.showLoading(messagePanel, "Guardando item producto " + product.getName());
						
						commonService.saveItem(options.getDomainName(), options.getDomain(), options.getUser(), item, new AsyncCallback<Item>() {

							@Override
							public void onFailure(Throwable caught) {
								AonMessagePanel.showError(messagePanel, "Error guardado item: " + caught.getMessage());
							}

							@Override
							public void onSuccess(Item itemDB) {
								item = itemDB;
								AonMessagePanel.showSuccess(messagePanel, "Producto " + product.getName()+ " guardado correctamente");
							}
						});
					}
					
				});
				
			}
			
			@Override
			public void onFailure(Throwable caught) {
				AonMessagePanel.showError(messagePanel, "Error guardado producto: " + caught.getMessage());
			}
		});
	}
	
	private void getProduct(Integer productId, Consumer<Product> success) {
		commonService.getProduct(options.getDomainName(), options.getDomain(), options.getUser(), productId, new AsyncCallback<Product>() {
			
			@Override
			public void onSuccess(Product productDb) {
				product = productDb;
				
				commonService.getItem(options.getDomainName(), options.getDomain(), options.getUser(), productId, new AsyncCallback<Item>() {

					@Override
					public void onFailure(Throwable caught) {
						AonMessagePanel.showError(messagePanel, "Error obteniendo item: " + caught.getMessage());
					}

					@Override
					public void onSuccess(Item itemDb) {
						item = itemDb;
						success.accept(product);
					}}
				);		
			}
			
			@Override
			public void onFailure(Throwable caught) {
				AonMessagePanel.showError(messagePanel, "Error obteniendo producto: " + caught.getMessage());
			}
		});
	}
	
	private void delete(Integer productId) {
		commonService.deleteProduct(options.getDomainName(), options.getDomain(), options.getUser(), product.getId(), new AsyncCallback<Void>() {
			
			@Override
			public void onSuccess(Void result) {
				onBackClick();
			}
			
			@Override
			public void onFailure(Throwable caught) {
				AonMessagePanel.showError(messagePanel, "Error borrado: " + caught.getMessage());
			}
		});
	}
	
	private void getProductCategories(Consumer<List<ProductCategory>> success) {
		commonService.getProductCategories(options.getDomainName(), options.getDomain(), options.getUser(), new AsyncCallback<List<ProductCategory>>() {
			
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
	
	private void getTaxTypes(Consumer<List<Tax>> success) {
		commonService.getTaxTypes(options.getDomainName(), options.getDomain(), options.getUser(), TaxType.VAT, new AsyncCallback<List<Tax>>() {
			
			@Override
			public void onSuccess(List<Tax> taxTypes) {
				taxes = taxTypes;
				success.accept(taxTypes);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				AonMessagePanel.showError(messagePanel, "Error obteniendo producto: " + caught.getMessage());
			}
		});
	}
	
	private void getProductTags(Integer productId, Consumer<List<ProductTag>> success) {
		commonService.getProductTags(options.getDomainName(), options.getDomain(), options.getUser(), productId, new AsyncCallback<List<ProductTag>>() {
			
			@Override
			public void onSuccess(List<ProductTag> productTags) {
				success.accept(productTags);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				AonMessagePanel.showError(messagePanel, "Error obteniendo producto: " + caught.getMessage());
			}
		});
	}
	
	private void getTags(Consumer<List<Tag>> success) {
		commonService.getTags(options.getDomainName(), options.getDomain(), options.getUser(), new AsyncCallback<List<Tag>>() {
			
			@Override
			public void onSuccess(List<Tag> tags) {
				tagList = tags;
				success.accept(tags);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				AonMessagePanel.showError(messagePanel, "Error obteniendo producto: " + caught.getMessage());
			}
		});
	}
	
	protected abstract void onBackClick();

}
