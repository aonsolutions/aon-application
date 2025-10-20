package com.esferalia.aon.gwt.fiscal.client.product;

import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog.AonAcceptDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.fiscal.client.registry.RegistryModuleOptions;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Workplace;
import com.esferalia.aon.occam.api.model.fee.Fee;
import com.esferalia.aon.occam.api.model.product.ItemComposition;
import com.esferalia.aon.occam.api.model.product.ItemTariff;
import com.esferalia.aon.occam.api.model.product.OldItem;
import com.esferalia.aon.occam.api.model.product.Product;
import com.esferalia.aon.occam.api.model.product.ProductParams;
import com.esferalia.aon.occam.api.model.registry.Project;
import com.esferalia.aon.occam.api.model.tariff.Tariff;
import com.esferalia.aon.occam.api.model.type.BillingPeriod;
import com.esferalia.aon.occam.api.model.type.DomainType;
import com.esferalia.aon.occam.api.model.type.ProductType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.TextDecoration;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.SimplePanel;

public class ProductCatalogueBooking extends HTMLPanel {
	
	private static CommonServiceAsync COMMON_SERVICE;
	
	// TariffList UI

	private HTMLPanel messagePanel = new HTMLPanel("");
	
	private SimplePanel tableContainer;
	private ScrollPanel tableScrollPanel;
	
	private RegistryModuleOptions currentDomainOptions;
	private RegistryModuleOptions officeDomainOptions;
	private Integer customerRelatedRegistry;
	private LinkedList<Workplace> workplaces;
	
	private DomainType domainType;
	private Tariff tariff;
	
	private List<Product> products;
	
	private LinkedList<Fee> customerFees;
	
	// Constructor
	public ProductCatalogueBooking(RegistryModuleOptions currentDomainOptions, RegistryModuleOptions officeDomainOptions, Integer customerRelatedRegistry, LinkedList<Workplace> workplaces) {
		super("");
		
		CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
		COMMON_SERVICE = new CommonServiceAsyncDecorator(commonServiceRaw);
		
		this.customerRelatedRegistry = customerRelatedRegistry;
		this.currentDomainOptions = currentDomainOptions;
		this.officeDomainOptions = officeDomainOptions;
		this.workplaces = workplaces;
		
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
		cataloguePanel.addStyleName(AON.CSS.aonFlexColumn());
		
		Label title = new Label("Mejora tu plan. Impulsa tu negocio.");
		title.getElement().getStyle().setProperty("font-size", "1.5rem");
		cataloguePanel.add(title);
		
		Label subtitle = new Label("Olv\u00eddate de invertir en recursos externos para aumentar tu productividad. Nuestro software tiene todo lo que necesitas en un \u00fanico lugar. \u00a1Desc\u00fabrelo!.");
		subtitle.getElement().getStyle().setProperty("font-size", ".8rem");
		subtitle.getElement().getStyle().setProperty("margin-bottom", "3rem");
		cataloguePanel.add(subtitle);
		
		List<Product> packsProducts = products.stream().filter(Product::isManufactured).collect(Collectors.toList());
		if(!packsProducts.isEmpty()) {
			createPacks(cataloguePanel, packsProducts);
		}
		
		if(!customerFees.isEmpty()) {
			Label aditional = new Label("Adicional");
			aditional.getElement().getStyle().setProperty("font-size", "1.2rem");
			aditional.getElement().getStyle().setProperty("margin", "1rem 0");
			cataloguePanel.add(aditional);
			
			List<Product> aonServices = products.stream().filter(product -> !product.isManufactured()).collect(Collectors.toList());
			if(!aonServices.isEmpty()) {
				createServices(cataloguePanel, aonServices);
			}
		}
		
		tableScrollPanel = new ScrollPanel(cataloguePanel);
		tableContainer.add(tableScrollPanel);
	}

	private void createPacks(HTMLPanel cataloguePanel, List<Product> packsProducts) {
		HTMLPanel centerPacksCataloguePanel = new HTMLPanel("");
		centerPacksCataloguePanel.setWidth("100%");
		centerPacksCataloguePanel.getElement().getStyle().setProperty("display", "flex");
		
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
	    		CataloguePackBookingCard card;
	    		if(itemTariff.isEmpty())
	    			card = new CataloguePackBookingCard(packProduct, tariff, itemCompositions, customerFees) {

						@Override
						protected void onCreateCustomerFeeByItem(Product product) {			
							createFee(packProduct);	
						}};
	    		else
	    			card = new CataloguePackBookingCard(packProduct, itemTariff.get(), itemCompositions, customerFees) {

						@Override
						protected void onCreateCustomerFeeByItem(Product product) {
							createFee(packProduct);	
						}};
		       
	    		packsCataloguePanel.add(card);

		        // Procesar el siguiente pack
		        createProductCard(packsCataloguePanel, packsProducts, index + 1);
			});	
	    });
	}
	
	private void createFee(Product product) {
		if(product.isManufactured()) {
			List<Integer> packItemIds = products.stream().filter(p -> p.isManufactured()).map(p -> p.getItem().getId()).collect(Collectors.toList());
			Optional<Fee> feeItem = customerFees.stream().filter(cf -> packItemIds.contains(cf.getItem().getId()) && (cf.getEndDate() == null || cf.getEndDate().after(new Date()) || cf.getEndDate().equals(new Date()))).findFirst();
			
			if(feeItem.isPresent()) {
				COMMON_SERVICE.updateEndDatePackFee(officeDomainOptions.getDomainName(), officeDomainOptions.getDomain(), officeDomainOptions.getUser(), feeItem.get(), new AsyncCallback<Void>() {

					@Override
					public void onFailure(Throwable caught) {
						AonMessagePanel.showError(messagePanel, "Error creando cuota: " + caught.getMessage());
					}

					@Override
					public void onSuccess(Void arg0) {
						COMMON_SERVICE.updateBookingFee(currentDomainOptions.getDomainName(), currentDomainOptions.getDomain(), currentDomainOptions.getUser(), feeItem.get(), product, new AsyncCallback<Void>() {

							@Override
							public void onFailure(Throwable caught) {
								AonMessagePanel.showError(messagePanel, "Error creando contrataci\u00f3n: " + caught.getMessage());
							}

							@Override
							public void onSuccess(Void arg0) {
								onSearch(domainType, tariff);
							}
							
						});
					}
				});
			}
		}
		
		// Borrar servicio
		if(isFeeProduct(product.getItem().getId()) && !product.isManufactured()) {
			Optional<Fee> fee = customerFees.stream().filter(feeIt -> feeIt.getItem().getId().equals(product.getItem().getId())).findFirst();
			if(fee.isPresent())
				COMMON_SERVICE.updateEndDatePackFee(officeDomainOptions.getDomainName(), officeDomainOptions.getDomain(), officeDomainOptions.getUser(), fee.get(), new AsyncCallback<Void>() {

					@Override
					public void onFailure(Throwable caught) {
						AonMessagePanel.showError(messagePanel, "Error borrado cuota: " + caught.getMessage());
					}

					@Override
					public void onSuccess(Void arg0) {
						COMMON_SERVICE.updateBookingFee(currentDomainOptions.getDomainName(), currentDomainOptions.getDomain(), currentDomainOptions.getUser(), fee.get(), product, new AsyncCallback<Void>() {

							@Override
							public void onFailure(Throwable caught) {
								AonMessagePanel.showError(messagePanel, "Error creando contrataci\u00f3n: " + caught.getMessage());
							}

							@Override
							public void onSuccess(Void arg0) {
								onSearch(domainType, tariff);
							}
							
						});
					}
					
				});
		} else {
			Fee fee = new Fee()
					.setDomain(new Domain().setId(officeDomainOptions.getDomain()))
					.setProject(new Project())
					.setItem(new OldItem().setId(product.getItem().getId()).setBarcode(product.getItem().getBarcode()))
					.setDescription(product.getName())
					.setQuantity(1.00)
					.setPrice(product.getItem().getPrice())
					.setStartDate(new Date())
					.setBillingDate(DateUtils.getLastDayOfMonth())
					.setPeriod(BillingPeriod.MONTHLY)
					.setWorkplace(workplaces.get(0))
					;
						
				COMMON_SERVICE.createFeeRelatedRegistry(officeDomainOptions.getDomainName(), officeDomainOptions.getDomain(), officeDomainOptions.getUser(), customerRelatedRegistry, fee, new AsyncCallback<Void>() {

					@Override
					public void onFailure(Throwable caught) {
						AonMessagePanel.showError(messagePanel, "Error creando cuota: " + caught.getMessage());
					}

					@Override
					public void onSuccess(Void arg0) {
						COMMON_SERVICE.createBookingFee(currentDomainOptions.getDomainName(), currentDomainOptions.getDomain(), currentDomainOptions.getUser(), product, new AsyncCallback<Void>() {

							@Override
							public void onFailure(Throwable caught) {
								AonMessagePanel.showError(messagePanel, "Error creando contrataci\u00f3n: " + caught.getMessage());
							}

							@Override
							public void onSuccess(Void arg0) {
								onSearch(domainType, tariff);
							}
							
						});
					}
					
				});
		}
	}

	private void createServices(HTMLPanel cataloguePanel, List<Product> aonServices) {
		HTMLPanel servicesPanel = new HTMLPanel("");
		servicesPanel.addStyleName(AON.CSS.aonFlexColumn());
		servicesPanel.setWidth("100%");
		servicesPanel.getElement().getStyle().setProperty("max-width", "70rem");
		
		aonServices.forEach(aonService -> {
			getItemTariff(aonService.getItem().getId(), itemTariff -> {
				HTMLPanel servicePanel = new HTMLPanel("");
				servicePanel.addStyleName(AON.CSS.aonItemFlex());
				servicePanel.setWidth("100%");
				servicePanel.getElement().getStyle().setProperty("justify-content", "space-between");
				servicePanel.getElement().getStyle().setProperty("padding", "1rem");
				servicePanel.getElement().getStyle().setProperty("border", "1px solid #ebebeb");
				servicePanel.getElement().getStyle().setProperty("border-radius", "0.5rem");
				servicePanel.getElement().getStyle().setProperty("margin-bottom", "2rem");
				
				if(isFeeProduct(aonService.getItem().getId()))
					servicePanel.getElement().getStyle().setProperty("background-color", "#eee");
				
				
				HTMLPanel codeNamePanel = new HTMLPanel("");
				codeNamePanel.addStyleName(AON.CSS.aonFlexColumn());
				
				Label code = new Label(aonService.getCode());
				code.setWidth("10rem");
				code.getElement().getStyle().setFontWeight(FontWeight.BOLD);
				codeNamePanel.add(code);
				
				Label name = new Label(aonService.getItem().getDescription());
				name.getElement().getStyle().setProperty("padding", "1rem 2rem 1rem 0");
				codeNamePanel.add(name);
				
				servicePanel.add(codeNamePanel);
				
				HTMLPanel pricePanel = new HTMLPanel("");
				pricePanel.addStyleName(AON.CSS.aonFlexColumn());
				pricePanel.setHeight("100%");
				pricePanel.getElement().getStyle().setProperty("align-items", "center");
				
				String priceValue = formaDouble(aonService.getItem().getPrice());
				HTMLPanel price = new HTMLPanel("<b>" + priceValue.split("\\.")[0] + "</b>.<small>" + priceValue.split("\\.")[1] + "<small><b> \u20ac </b>" + " al mes *");
				price.getElement().getStyle().setProperty("text-align", "center");
				price.getElement().getStyle().setProperty("width", "8rem");
				price.getElement().getStyle().setProperty("color", isFeeProduct(aonService.getItem().getId()) ? "black" : "#002469");
							
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
						price.getElement().getStyle().setProperty("font-size", ".9rem");
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
						price.getElement().getStyle().setProperty("font-size", ".9rem");
					}
				}
				
				
				pricePanel.add(price);
				pricePanel.add(createServiceButton(aonService));
				
				servicePanel.add(pricePanel);
				
				servicesPanel.add(servicePanel);
			});
		});
		
		cataloguePanel.add(servicesPanel);
		
	}
	
	private Button createServiceButton(Product packProduct) {
		Button bookBtn = new Button();
		bookBtn.setText(isFeeProduct(packProduct.getItem().getId()) ? "Descontratar" : "Contratar");
		
		bookBtn.getElement().getStyle().setProperty("background", "none");
		bookBtn.getElement().getStyle().setProperty("color", isFeeProduct(packProduct.getItem().getId()) ? "#d56060" : "white");
		bookBtn.getElement().getStyle().setProperty("background-color", isFeeProduct(packProduct.getItem().getId()) ? "rgb(181, 180, 180)" : "#ff8f00");
		bookBtn.getElement().getStyle().setProperty("width", "10rem");
		bookBtn.getElement().getStyle().setProperty("height", "2.5rem");
		bookBtn.getElement().getStyle().setProperty("border-radius", "5px");
		bookBtn.getElement().getStyle().setProperty("border", "none");
		
		bookBtn.addClickHandler(e -> {
			AonDialog dialog = new AonDialog(packProduct.getName(), new HTMLPanel(
					isFeeProduct(packProduct.getItem().getId()) 
						? "Se va a proceder con la creaci\u00f3n de la cuota del producto <b>" + packProduct.getName() + "</b><br> Al proceder acepta los terminos y condiciones de la contrataci\u00f3n"
						: "Se va a proceder con el borrado de la cuota del producto <b>" + packProduct.getName() + "</b><br> Al proceder acepta los terminos y condiciones de la contrataci\u00f3n"));
			dialog.confirm(new AonAcceptDialogCallback() {
				
				@Override
				public void onCancel() {}
				
				@Override
				public void onAccept() {
					createFee(packProduct);
				}
			});
		});
		
		return bookBtn;
	}
	
	private boolean isFeeProduct(Integer itemId) {
		return customerFees.stream().filter(fee -> fee.getItem().getId().equals(itemId)).findAny().isPresent();
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
				.setDomainName(officeDomainOptions.getDomainName())
				.setDomain(officeDomainOptions.getDomain())
				.setUser(officeDomainOptions.getUser())
				.setType(ProductType.AUXILIARY)
				.setDomainType(domainType)
				.setOffset(0)
				.setLimit(Integer.MAX_VALUE)
				;
		
		COMMON_SERVICE.getProducts(params, new AsyncCallback<List<Product>>() {
			
			@Override
			public void onSuccess(List<Product> productsDB) {
				products = productsDB;
				getCustomerFees(cfs -> success.accept(products));
			}
			
			@Override
			public void onFailure(Throwable caught) {
				AonMessagePanel.showError(messagePanel, "Error productos: " + caught.getMessage());
			}
		});
	}
	
	private void getCustomerFees(Consumer<LinkedList<Fee>> success) {
		COMMON_SERVICE.getCustomerFeesRelatedRegistry(officeDomainOptions.getDomainName(), officeDomainOptions.getDomain(), officeDomainOptions.getUser(), this.customerRelatedRegistry, new AsyncCallback<LinkedList<Fee>>() {
			
			@Override
			public void onSuccess(LinkedList<Fee> customerFeeList) {
				customerFees = customerFeeList;
				success.accept(customerFees);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				AonMessagePanel.showError(messagePanel, "Error productos: " + caught.getMessage());
			}
		});
	}
	
	private void getItemCompositions(Integer itemId, Consumer<List<ItemComposition>> success) {
		COMMON_SERVICE.getItemCompositions(officeDomainOptions.getDomainName(), officeDomainOptions.getDomain(), officeDomainOptions.getUser(), itemId, new AsyncCallback<List<ItemComposition>>() {
			
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
		COMMON_SERVICE.getItemTariffs(officeDomainOptions.getDomainName(), officeDomainOptions.getDomain(), officeDomainOptions.getUser(), itemId, new AsyncCallback<List<ItemTariff>>() {
			
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
