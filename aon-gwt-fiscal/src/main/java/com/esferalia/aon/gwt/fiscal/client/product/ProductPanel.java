package com.esferalia.aon.gwt.fiscal.client.product;

import java.util.ArrayList;
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
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomMultiSelectBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomNumberBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomTextBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.fiscal.client.registry.RegistryModuleOptions;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.aonsolutions.AonApp;
import com.esferalia.aon.occam.api.model.office.Tag;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.product.Product;
import com.esferalia.aon.occam.api.model.product.ProductCategory;
import com.esferalia.aon.occam.api.model.product.ProductKind;
import com.esferalia.aon.occam.api.model.product.ProductTag;
import com.esferalia.aon.occam.api.model.product.Tax;
import com.esferalia.aon.occam.api.model.type.DomainType;
import com.esferalia.aon.occam.api.model.type.ProductType;
import com.esferalia.aon.occam.api.model.type.TaxType;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class ProductPanel extends HTMLPanel {
	
	public static interface AonProductPanelCallback {
		void onAccept(Product product);
	}
	
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
	
	private RegistryModuleOptions options;
	
	private Product product;
	private Item item;
	private List<Tax> taxes;
	private List<ProductCategory> productCategories;
	
	// Tags
	private List<Tag> tagList;
	
	// Callback
	private AonProductPanelCallback callback;
	
	public ProductPanel(RegistryModuleOptions options, AonProductPanelCallback callback) {
		super("");
		
		this.options = options;
		this.callback = callback;
		
		this.product = new Product();
		this.item = new Item();
		
		this.product.setDomain(new Domain().setName(options.getDomainName()).setId(options.getDomain()));
		this.product.setType(ProductType.AUXILIARY);
		this.product.setKind(ProductKind.SALE);
		
		this.item.setDomain(new Domain().setName(options.getDomainName()).setId(options.getDomain()));
		
		initializeCommonService();
		
		addStyleName(AON.CSS.aonItemFlex());
		addStyleName(AON.CSS.aonFlexColumn());
		getElement().getStyle().setProperty("padding", "0 1rem");
		
		messagePanel.addStyleName(AON.CSS.aonWidthAll());
		
		add(messagePanel);
		
		add(createGeneralCard());
		add(createAditionalCard());
		
		add(createButtons());
	}

	private AonCustomCard createGeneralCard() {
		FlowPanel table = createFlexColumnPanel();
		
		status = new ProductStatusSelect(product.getStatus());
		
		generalCard = new AonCustomCard("Informaci\u00f3n General", status);
		generalCard.setToolbarWidgetShown();
		generalCard.addStyleName(AON.CSS.aonWidthAll());
		generalCard.add(table);
		
		code.getTextBox().getElement().getStyle().setProperty("text-transform", "uppercase");
		code.getTextBox().setMaxLength(15);
		code.addValueChangeHandler(e -> product.setCode(code.getValue().trim().toUpperCase()));
		
		barcode.setEnable(false);
		barcode.setValue(item.getBarcode());
		
		table.add(createRow(code, barcode));
		
		name.addValueChangeHandler(e -> {
			product.setName(name.getValue());
			item.setDescription(name.getValue());
		});
		
		table.add(createRow(name, null));
		
		app.clearItems();
		app.addItem("-", "--");
		AonApp.getValues().forEach(appIt -> app.addItem(appIt.getDescription(), AonStringUtils.leftPad(appIt.ordinal() + "", 2, "0") ));
		app.addChangeHandler(e -> createBarCode());
		
		table.add(createRow(app, null));
		
		price.hideNearBy();
		price.addValueChangeHandler(e -> {
			item.setPrice(price.getValue());
			pvp.setValue(createPVP());
		});
		
		iva.clearItems();
		getTaxTypes(taxTypes -> {
			taxTypes.forEach(taxType -> iva.addItem(taxType.getName(), taxType.getId().toString()));
			DomEvent.fireNativeEvent(Document.get().createChangeEvent(), iva.getListBox());
		});
		iva.addChangeHandler(e -> {
			product.setVat(new Tax().setId(Integer.parseInt(iva.getValue())));
			pvp.setValue(createPVP());
		});
		iva.setEnable(false);
		
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
		domainType.addBlurHandler(e -> createBarCode());
		
		table.add(createRow(domainType, null));
		
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
		
		category.clearItems();
		category.addItem("-", "");
		getProductCategories(productCategories -> {
			productCategories.forEach(productCategory -> category.addItem(productCategory.getName(), productCategory.getId().toString()));
		});
		category.addChangeHandler(e -> product.setCategory(AonStringUtils.isBlank(category.getValue()) ? null : productCategories.stream().filter(pc -> pc.getId().equals(Integer.parseInt(category.getValue()))).findFirst().get()));
		
		// TAGS
		getTags(aviableTags -> tags.setOptions(aviableTags.stream().map(aviableTag -> aviableTag.getName()).collect(Collectors.toSet())) );
		
		table.add(createRow(console, category, tags));
		
		return aditionalCard;
	}
	
	private Widget createButtons() {
		Button acceptDialog = new Button();
		acceptDialog.setStyleName(AON.CSS.aonOkButtonSmall());
		acceptDialog.setText("Crear Producto");
		acceptDialog.addClickHandler(e -> saveProduct());
		
		FlowPanel buttonsPanel = createRow(acceptDialog, null);
		buttonsPanel.getElement().getStyle().setProperty("margin", ".5rem 0 1rem 0");
		buttonsPanel.getElement().getStyle().setProperty("justify-content", "end");
			
		return buttonsPanel;
	}

	private Double createPVP() {
		// GENERAL
		return item.getPrice() + (item.getPrice() * getVatPercentage() / 100);
	}

	private double getVatPercentage() {
		Optional<Tax> taxOpt = taxes.stream().filter(tax -> AonStringUtils.equalsIgnoreCase(tax.getId().toString(), iva.getValue())).findFirst();
		return taxOpt.isEmpty() ? 0.00 : taxOpt.get().getPercentage();
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
		
		AonMessagePanel.showLoading(messagePanel, "Creando producto " + product.getName());
		
		commonService.createProduct(options.getDomainName(), options.getDomain(), options.getUser(), product, getProductTags(), item, new AsyncCallback<Product>() {
			
			@Override
			public void onSuccess(Product productDB) {
				product = productDB;
				
				AonMessagePanel.showSuccess(messagePanel, "Producto " + product.getName()+ " creado correctamente");
				
				Timer timer = new Timer() {
				     @Override
				     public void run() {
				    	 callback.onAccept(product);
				     }
				};
				timer.schedule(2500);
				
			}
			
			@Override
			public void onFailure(Throwable caught) {
				AonMessagePanel.showError(messagePanel, "Error guardado producto: " + caught.getMessage());
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
	
	public void focusCode() {
		code.setFocus(true);
	}

}
