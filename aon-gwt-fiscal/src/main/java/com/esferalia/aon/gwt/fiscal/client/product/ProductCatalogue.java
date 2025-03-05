package com.esferalia.aon.gwt.fiscal.client.product;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.fiscal.client.registry.RegistryModuleOptions;
import com.esferalia.aon.occam.api.model.product.ItemComposition;
import com.esferalia.aon.occam.api.model.product.ItemTariff;
import com.esferalia.aon.occam.api.model.product.Product;
import com.esferalia.aon.occam.api.model.product.ProductParams;
import com.esferalia.aon.occam.api.model.tariff.Tariff;
import com.esferalia.aon.occam.api.model.type.DomainType;
import com.esferalia.aon.occam.api.model.type.ProductType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.TextDecoration;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.SimplePanel;

public class ProductCatalogue extends HTMLPanel {
	
	private static CommonServiceAsync COMMON_SERVICE;
	
	// TariffList UI

	private HTMLPanel messagePanel = new HTMLPanel("");
	
	private SimplePanel tableContainer;
	private ScrollPanel tableScrollPanel;
	
	private RegistryModuleOptions options;
	
	private DomainType domainType;
	private Tariff tariff;
	
	private List<Product> products;
	
	// Constructor
	public ProductCatalogue(RegistryModuleOptions options) {
		super("");
		
		CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
		COMMON_SERVICE = new CommonServiceAsyncDecorator(commonServiceRaw);
		
		this.options = options;
		
		addStyleName(AON.CSS.aonFlexColumn());
		
		add(messagePanel);
	
		tableContainer = new SimpleLayoutPanel();
		tableContainer.setHeight("100%");
		tableContainer.getElement().getStyle().setProperty("margin-left", "1rem");
		
		add(tableContainer);	
	}
	
	public void onSearch(DomainType domainType, Tariff tariff) {
		this.domainType = domainType;
		this.tariff = tariff;
		
		tableContainer.clear();
		searchDataList();
	}
	
	private void searchDataList() {
		getList(products -> {
			boolean something = products.size() != 0;
			
			createCatalogue();
			
			if (!something) {
				FlowPanel line = new FlowPanel();
				InlineLabel label = new InlineLabel(AON.MSG.noData());
				line.add(label);
				tableContainer.clear();
				tableContainer.add(line);
			}
			
		});
	}
	
	private void createCatalogue() {
		HTMLPanel cataloguePanel = new HTMLPanel("");
		cataloguePanel.addStyleName(AON.CSS.aonItemFlex());
		cataloguePanel.addStyleName(AON.CSS.aonFlexColumn());
		cataloguePanel.getElement().getStyle().setProperty("gap", "3rem");
		
		List<Product> packsProducts = products.stream().filter(product -> product.getComposition()).collect(Collectors.toList());
		if(!packsProducts.isEmpty()) {
			createPacks(cataloguePanel, packsProducts);
		}
		
		List<Product> aonServices = products.stream().filter(product -> !product.getComposition()).collect(Collectors.toList());
		if(!aonServices.isEmpty()) {
			createServices(cataloguePanel, aonServices);
		}
		
		tableScrollPanel = new ScrollPanel(cataloguePanel);
		tableContainer.add(tableScrollPanel);
	}

	private void createPacks(HTMLPanel cataloguePanel, List<Product> packsProducts) {
		HTMLPanel centerPacksCataloguePanel = new HTMLPanel("");
		centerPacksCataloguePanel.setWidth("100%");
		centerPacksCataloguePanel.getElement().getStyle().setProperty("display", "flex");
		centerPacksCataloguePanel.getElement().getStyle().setProperty("justify-content", "center");
		
		HTMLPanel packsCataloguePanel = new HTMLPanel("");
		packsCataloguePanel.addStyleName(AON.CSS.aonPacksCataloguePanel());
		centerPacksCataloguePanel.add(packsCataloguePanel);
		cataloguePanel.add(centerPacksCataloguePanel);
		
		getItemTariff(null, itemTariffAll -> {
			List<ItemTariff> itemTariffs = itemTariffAll.stream().filter(itemTariffIt -> itemTariffIt.getTariff().getId().equals(tariff.getId())).collect(Collectors.toList());
			packsProducts.sort(Comparator.comparingDouble(p -> { 
				Optional<ItemTariff> itOpt = itemTariffs.stream().filter(it -> it.getItem().equals(p.getItem().getId())).findFirst();
				return itOpt.isEmpty() ? p.getItem().getPrice() : (p.getItem().getPrice() - (p.getItem().getPrice() * itOpt.get().getProfitPercent() / 100));
			}));
			createProductCard(packsCataloguePanel, packsProducts, 0);
		});
	}
	
	private void createProductCard(HTMLPanel packsCataloguePanel, List<Product> packsProducts, int index) {
		if (index >= packsProducts.size()) {
	        // Terminado
	        return;
	    }

	    Product packProduct = packsProducts.get(index);
	    getItemCompositions(packProduct.getItem().getId(), itemCompositions -> {
	        // Crear la carta después de obtener itemCompositions
	    	
	    	getItemTariff(packProduct.getItem().getId(), itemTariff -> {
	    		CataloguePackCard card;
	    		if(itemTariff.isEmpty())
	    			card = new CataloguePackCard(packProduct, tariff, itemCompositions);
	    		else
	    			card = new CataloguePackCard(packProduct, itemTariff.get(), itemCompositions);
		        packsCataloguePanel.add(card);

		        // Procesar el siguiente pack
		        createProductCard(packsCataloguePanel, packsProducts, index + 1);
			});	
	    });
	}

	private void createServices(HTMLPanel cataloguePanel, List<Product> aonServices) {
		HTMLPanel servicesPanel = new HTMLPanel("");
		servicesPanel.addStyleName(AON.CSS.aonItemFlex());
		servicesPanel.addStyleName(AON.CSS.aonFlexColumn());
		servicesPanel.setWidth("100%");;
		
		aonServices.forEach(aonService -> {
			getItemTariff(aonService.getItem().getId(), itemTariff -> {
				HTMLPanel servicePanel = new HTMLPanel("");
				servicePanel.addStyleName(AON.CSS.aonItemFlex());
				servicePanel.setWidth("100%");
				servicePanel.getElement().getStyle().setProperty("justify-content", "space-between");
				servicePanel.getElement().getStyle().setProperty("padding", "1rem");
				servicePanel.getElement().getStyle().setProperty("border", "1px solid #ebebeb");
				servicePanel.getElement().getStyle().setProperty("border-radius", "0.5rem");
				
				HTMLPanel codeNamePanel = new HTMLPanel("");
				codeNamePanel.addStyleName(AON.CSS.aonItemFlex());
				
				Label code = new Label(aonService.getCode());
				code.setWidth("10rem");
				codeNamePanel.add(code);
				
				Label name = new Label(aonService.getName());
				name.getElement().getStyle().setFontWeight(FontWeight.BOLD);
				codeNamePanel.add(name);
				
				servicePanel.add(codeNamePanel);
				
				HTMLPanel pricePanel = new HTMLPanel("");
				pricePanel.addStyleName(AON.CSS.aonItemFlex());
				pricePanel.getElement().getStyle().setProperty("flex-direction", "row-reverse");
				
				Label price = new Label(formaDouble(aonService.getItem().getPrice()) + " \u20ac");
				
				if(itemTariff.isEmpty()) {
					if(tariff.getDiscount() != 0.00) {
						double tariffPrice = getTariffPrice(aonService.getItem().getPrice(), tariff.getDiscount());
						Label newPrice = new Label(tariffPrice == 0.00 ? "Gratis" : (formaDouble(tariffPrice) + " \u20ac"));
						newPrice.getElement().getStyle().setProperty("font-size", "1rem");
						newPrice.getElement().getStyle().setColor("#0ea90e");
						pricePanel.add(newPrice);
						
						price.getElement().getStyle().setColor("#848484");
						price.getElement().getStyle().setTextDecoration(TextDecoration.LINE_THROUGH);
					} else {
						price.getElement().getStyle().setProperty("font-size", "1rem");
					}
				} else {
					if(itemTariff.get().getProfitPercent() != 0.00) {
						double tariffPrice = getTariffPrice(aonService.getItem().getPrice(), itemTariff.get().getProfitPercent());
						Label newPrice = new Label(tariffPrice == 0.00 ? "Gratis" : (formaDouble(tariffPrice) + " \u20ac"));
						newPrice.getElement().getStyle().setProperty("font-size", "1rem");
						newPrice.getElement().getStyle().setColor("#0ea90e");
						pricePanel.add(newPrice);
						
						price.getElement().getStyle().setColor("#848484");
						price.getElement().getStyle().setTextDecoration(TextDecoration.LINE_THROUGH);
					} else {
						price.getElement().getStyle().setProperty("font-size", "1rem");
					}
				}
				
				
				
				pricePanel.add(price);
				
				servicePanel.add(pricePanel);
				
				servicesPanel.add(servicePanel);
			});
		});
		
		cataloguePanel.add(servicesPanel);
		
	}
	
	private double getTariffPrice(double price, double discount) {
		return price - (price * discount / 100);
	}

	private static String formaDouble(double value) {
        // Round to two decimal places
        long scaledValue = Math.round(value * 100); // Scale to avoid floating-point precision issues
        long integerPart = scaledValue / 100;      // Extract integer part
        long decimalPart = scaledValue % 100;      // Extract decimal part

        // Format the result
        return integerPart + "." + (decimalPart < 10 ? "0" : "") + decimalPart /*+ " \u20ac"*/;
    }
	
	private void getList(Consumer<List<Product>> success) {
		ProductParams params = new ProductParams()
				.setDomainName(options.getDomainName())
				.setDomain(options.getDomain())
				.setUser(options.getUser())
				.setType(ProductType.AUXILIARY)
				.setDomainType(domainType)
				.setOffset(0)
				.setLimit(Integer.MAX_VALUE)
				;
		
		COMMON_SERVICE.getProducts(params, new AsyncCallback<List<Product>>() {
			
			@Override
			public void onSuccess(List<Product> productsDB) {
				products = productsDB;
				success.accept(products);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				AonMessagePanel.showError(messagePanel, "Error productos: " + caught.getMessage());
			}
		});
	}
	
	private void getItemCompositions(Integer itemId, Consumer<List<ItemComposition>> success) {
		COMMON_SERVICE.getItemCompositions(options.getDomainName(), options.getDomain(), options.getUser(), itemId, new AsyncCallback<List<ItemComposition>>() {
			
			@Override
			public void onSuccess(List<ItemComposition> itemCompositionsDb) {
				success.accept(itemCompositionsDb);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				AonMessagePanel.showError(messagePanel, "Error obteniendo item compuesto : " + caught.getMessage());
			}
		});
	}
	
	private void getItemTariff(Integer itemId, Consumer<Optional<ItemTariff>> success) {
		COMMON_SERVICE.getItemTariffs(options.getDomainName(), options.getDomain(), options.getUser(), itemId, new AsyncCallback<List<ItemTariff>>() {
			
			@Override
			public void onSuccess(List<ItemTariff> itemTariffsDb) {
				Optional<ItemTariff> itemTariff = itemTariffsDb.stream().filter(itTariff -> itTariff.getTariff().getId().equals(tariff.getId())).findFirst();
				success.accept(itemTariff);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				AonMessagePanel.showError(messagePanel, "Error obteniendo tarifas producto: " + caught.getMessage());
			}
		});
	}
	
}
