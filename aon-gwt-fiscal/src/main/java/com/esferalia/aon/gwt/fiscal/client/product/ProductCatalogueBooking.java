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
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.fiscal.client.customer.CustomerInfoConfirm;
import com.esferalia.aon.gwt.fiscal.client.registry.RegistryModuleOptions;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.fee.Fee;
import com.esferalia.aon.occam.api.model.product.ItemTariff;
import com.esferalia.aon.occam.api.model.product.OldItem;
import com.esferalia.aon.occam.api.model.product.Product;
import com.esferalia.aon.occam.api.model.product.ProductBooking;
import com.esferalia.aon.occam.api.model.product.ProductBookingType;
import com.esferalia.aon.occam.api.model.product.ProductParams;
import com.esferalia.aon.occam.api.model.registry.Project;
import com.esferalia.aon.occam.api.model.tariff.Tariff;
import com.esferalia.aon.occam.api.model.type.BillingPeriod;
import com.esferalia.aon.occam.api.model.type.DomainType;
import com.esferalia.aon.occam.api.model.type.ProductType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.TextDecoration;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
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
	private Company customerCompany;
	
	private DomainType domainType;
	private Tariff tariff;
	
	private List<ProductBooking> products;
	
	private LinkedList<Fee> customerFees;
	
	// Constructor
	public ProductCatalogueBooking(RegistryModuleOptions currentDomainOptions, RegistryModuleOptions officeDomainOptions, Company customerCompany) {
		super("");
		
		CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
		COMMON_SERVICE = new CommonServiceAsyncDecorator(commonServiceRaw);
		
		this.customerRelatedRegistry = customerCompany.getId();
		this.currentDomainOptions = currentDomainOptions;
		this.officeDomainOptions = officeDomainOptions;
		this.customerCompany = customerCompany;
		
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
			
			getCustomerFees(fees -> {
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
		subtitle.getElement().getStyle().setProperty("margin-bottom", "1.5rem");
		cataloguePanel.add(subtitle);
		
		List<ProductBooking> packsProducts = products.stream()
				.filter(p -> p.getBookingType().equals(ProductBookingType.PLAN) || p.getBookingType().equals(ProductBookingType.CONSULTANCY))
				.sorted(
			        Comparator.comparing(
			                ProductBooking::getBookingType,
			                Comparator.comparingInt(type -> {
			                    switch (type) {
			                        case PLAN:         return 0;
			                        case CONSULTANCY:  return 1;
			                        default:           return Integer.MAX_VALUE;
			                    }
			                })
			        ).thenComparing(
			                ProductBooking::getPosition,
			                Comparator.nullsLast(Comparator.naturalOrder())
			        )
			    )
				.collect(Collectors.toList());
		
		if(!packsProducts.isEmpty()) {
			createPacks(cataloguePanel, packsProducts);
		}
		
		if(!customerFees.isEmpty()) {
			
			List<ProductBooking> aonUsers = products.stream()
					.filter(p -> p.getBookingType().equals(ProductBookingType.USER))
					.sorted(Comparator.comparing(
				        ProductBooking::getPosition,
				        Comparator.nullsLast(Comparator.naturalOrder())
				    ))
					.collect(Collectors.toList());
			
			if(!aonUsers.isEmpty()) {
				Label users = new Label("Usuarios");
				users.getElement().getStyle().setProperty("font-size", "1.2rem");
				users.getElement().getStyle().setProperty("margin", "1rem 0");
				cataloguePanel.add(users);
				
				createUsers(cataloguePanel, aonUsers);
			}
			
			List<ProductBooking> aonServices = products.stream()
					.filter(p -> p.getBookingType().equals(ProductBookingType.SERVICE))
					.sorted(Comparator.comparing(
				        ProductBooking::getPosition,
				        Comparator.nullsLast(Comparator.naturalOrder())
				    ))
					.collect(Collectors.toList());
			
			if(!aonServices.isEmpty()) {
				Label aditional = new Label("Adicional");
				aditional.getElement().getStyle().setProperty("font-size", "1.2rem");
				aditional.getElement().getStyle().setProperty("margin", "1rem 0");
				cataloguePanel.add(aditional);
				
				createServices(cataloguePanel, aonServices);
			}
			
		}
		
		tableScrollPanel = new ScrollPanel(cataloguePanel);
		tableContainer.add(tableScrollPanel);
	}

	private void createPacks(HTMLPanel cataloguePanel, List<ProductBooking> packsProducts) {
		HTMLPanel centerPacksCataloguePanel = new HTMLPanel("");
		centerPacksCataloguePanel.setWidth("100%");
		centerPacksCataloguePanel.getElement().getStyle().setProperty("display", "flex");
		
		HTMLPanel packsCataloguePanel = new HTMLPanel("");
		packsCataloguePanel.addStyleName(AON.CSS.aonPacksCataloguePanel());
		centerPacksCataloguePanel.add(packsCataloguePanel);
		cataloguePanel.add(centerPacksCataloguePanel);
		
		getItemTariff(null, itemTariffAll -> {
			List<ItemTariff> itemTariffs = itemTariffAll.stream().filter(itemTariffIt -> itemTariffIt.getTariff().getId().equals(tariff.getId())).collect(Collectors.toList());
//			TODO: ordenar por precio segun tarifa
//			packsProducts.sort(Comparator.comparingDouble(p -> { 
//				Optional<ItemTariff> itOpt = itemTariffs.stream().filter(it -> it.getItem().equals(p.getItem().getId())).findFirst();
//				return itOpt.isEmpty() ? p.getItem().getPrice() : (p.getItem().getPrice() - (p.getItem().getPrice() * itOpt.get().getProfitPercent() / 100));
//			}));
			createProductCard(packsCataloguePanel, packsProducts, 0);
		});
	}
	
	private void createProductCard(HTMLPanel packsCataloguePanel, List<ProductBooking> packsProducts, int index) {
		if (index >= packsProducts.size()) {
	        // Terminado
	        return;
	    }

		ProductBooking packProduct = packsProducts.get(index);
	       
    	getItemTariff(packProduct.getItem().getId(), itemTariff -> {
    		CataloguePackBookingCard card;
    		if(itemTariff.isEmpty())
    			card = new CataloguePackBookingCard(packProduct, tariff, customerFees) {

					@Override
					protected void onCreateCustomerFeeByItem(Product product) {	
						if(customerFees.isEmpty()) {
							new CustomerInfoConfirm(officeDomainOptions.getDomainName(), officeDomainOptions.getDomain(), officeDomainOptions.getUser(), customerCompany) {
								
								@Override
								protected void onEnd() {
									createFee(packProduct);
								}
								
								@Override
								protected void onCancel() {}
								
							};
						} else
							createFee(packProduct);
							
					}

					@Override
					protected ProductBooking getConsultancyProduct() {
						return packsProducts.stream().filter(p -> p.getBookingType().equals(ProductBookingType.CONSULTANCY)).findFirst().orElse(null);
					}
					
				};
    		else
    			card = new CataloguePackBookingCard(packProduct, itemTariff.get(), customerFees) {

					@Override
					protected void onCreateCustomerFeeByItem(Product product) {
						if(customerFees.isEmpty()) {
							new CustomerInfoConfirm(officeDomainOptions.getDomainName(), officeDomainOptions.getDomain(), officeDomainOptions.getUser(), customerCompany) {
								
								@Override
								protected void onEnd() {
									createFee(packProduct);
								}
								
								@Override
								protected void onCancel() {}
								
							};
						} else
							createFee(packProduct);
					}

					@Override
					protected ProductBooking getConsultancyProduct() {
						return packsProducts.stream().filter(p -> p.getBookingType().equals(ProductBookingType.CONSULTANCY)).findFirst().orElse(null);
					}
					
				};
	       
    		packsCataloguePanel.add(card);

	        // Procesar el siguiente pack
	        createProductCard(packsCataloguePanel, packsProducts, index + 1);
		});	
	}
	
	private void createFee(ProductBooking product) {
		Fee newFee = new Fee()
				.setDomain(new Domain().setId(officeDomainOptions.getDomain()))
				.setProject(new Project())
				.setItem(new OldItem().setId(product.getItem().getId()).setBarcode(product.getItem().getBarcode()))
				.setDescription(product.getName())
				.setQuantity(1.00)
				.setPrice(product.getItem().getPrice())
				.setStartDate(new Date())
				.setBillingDate(DateUtils.getFirstDayOfMonth())
				.setPeriod(BillingPeriod.MONTHLY)
				;
		
		if(product.getBookingType().equals(ProductBookingType.PLAN) || product.getBookingType().equals(ProductBookingType.CONSULTANCY)) {
			List<Integer> packItemIds = products.stream().filter(p -> p.isManufactured()).map(p -> p.getItem().getId()).collect(Collectors.toList());
			Optional<Fee> feeItem = customerFees.stream().filter(cf -> packItemIds.contains(cf.getItem().getId()) && (cf.getEndDate() == null || cf.getEndDate().after(new Date()) || cf.getEndDate().equals(new Date()))).findFirst();
			
			if(feeItem.isPresent()) {
				COMMON_SERVICE.updateBookingProduct(currentDomainOptions.getDomainName(), currentDomainOptions.getDomain(), currentDomainOptions.getUser(), customerRelatedRegistry, feeItem.orElse(null), product, newFee, new AsyncCallback<Void>() {

					@Override
					public void onFailure(Throwable caught) {
						AonMessagePanel.showError(messagePanel, "Error contrataci\u00f3n: " + caught.getMessage());
					}

					@Override
					public void onSuccess(Void arg0) {
						onSearch(domainType, tariff);
						reloadDialog();
					}
				});
			} 
			else {
				
				COMMON_SERVICE.createBookingProduct(currentDomainOptions.getDomainName(), currentDomainOptions.getDomain(), currentDomainOptions.getUser(), customerRelatedRegistry, product, newFee, new AsyncCallback<Void>() {

					@Override
					public void onFailure(Throwable caught) {
						AonMessagePanel.showError(messagePanel, "Error contrataci\u00f3n: " + caught.getMessage());
					}

					@Override
					public void onSuccess(Void arg0) {
						onSearch(domainType, tariff);
						reloadDialog();
					}
				});
				
			}
		}
		
		// Borrar servicio
		else if(isFeeProduct(product.getItem().getId()) && !product.getBookingType().equals(ProductBookingType.PLAN)  && !product.getBookingType().equals(ProductBookingType.CONSULTANCY)) {
			Optional<Fee> fee = customerFees.stream().filter(feeIt -> feeIt.getItem().getId().equals(product.getItem().getId())).findFirst();
			if(fee.isPresent()){
				COMMON_SERVICE.removeBookingProduct(currentDomainOptions.getDomainName(), currentDomainOptions.getDomain(), currentDomainOptions.getUser(), customerRelatedRegistry, fee.orElse(null), product, new AsyncCallback<Void>() {

					@Override
					public void onFailure(Throwable caught) {
						AonMessagePanel.showError(messagePanel, "Error contrataci\u00f3n: " + caught.getMessage());
					}

					@Override
					public void onSuccess(Void arg0) {
						onSearch(domainType, tariff);
						reloadDialog();
					}
				});
				
			}
		} else {
			COMMON_SERVICE.createBookingProduct(currentDomainOptions.getDomainName(), currentDomainOptions.getDomain(), currentDomainOptions.getUser(), customerRelatedRegistry, product, newFee, new AsyncCallback<Void>() {

				@Override
				public void onFailure(Throwable caught) {
					AonMessagePanel.showError(messagePanel, "Error contrataci\u00f3n: " + caught.getMessage());
				}

				@Override
				public void onSuccess(Void arg0) {
					onSearch(domainType, tariff);
					reloadDialog();
				}
			});
			
		}
	}
	
	private void reloadDialog() {
		AonCustomDialog dialog = new AonCustomDialog();
		dialog.showCloseButton(true);
		
		HTMLPanel dialogContent = new HTMLPanel("");
		dialogContent.addStyleName(AON.CSS.aonFlexColumn());
		dialogContent.getElement().getStyle().setProperty("padding", "1rem");
		
		HTMLPanel buttonsPanel = new HTMLPanel("");
		buttonsPanel.addStyleName(AON.CSS.aonItemFlex());
		buttonsPanel.getElement().getStyle().setProperty("justify-content", "center");
		buttonsPanel.getElement().getStyle().setProperty("margin-top", "1rem");
		buttonsPanel.setWidth("100%");
		
		Button acceptBtnDialog = new Button();
		acceptBtnDialog.setStyleName(AON.CSS.aonOkButton());
		acceptBtnDialog.setText("Recargar");
		buttonsPanel.add(acceptBtnDialog);
		
		HTMLPanel messagePanel = new HTMLPanel("");
		messagePanel.addStyleName(AON.CSS.aonItemFlex());
		
		AonTableButton info = new AonTableButton("Informaci\u00f3n", AON.CSS.aonIconInfo());
		HTMLPanel message = new HTMLPanel("La contrataci\u00f3n ha sido actualizada. Se va a proceder a refrescar la sesi\u00f3n para actualizar las nuevas funcionalidades.");
		
		messagePanel.add(info);
		messagePanel.add(message);
		dialogContent.add(messagePanel);
		
		acceptBtnDialog.addClickHandler(ev -> {
			reloadParent();
		});
		
		dialogContent.add(buttonsPanel);
		
		dialog.setCaption("Contrataci\u00f3n Actualizada");
		dialog.add(dialogContent);
		dialog.center();
		dialog.show();
	}

	private void createServices(HTMLPanel cataloguePanel, List<ProductBooking> aonServices) {
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
				servicePanel.getElement().getStyle().setProperty("margin-bottom", "1rem");
				
				if(isFeeProduct(aonService.getItem().getId()))
					servicePanel.getElement().getStyle().setProperty("background-color", "#eee");
				
				
				HTMLPanel codeNamePanel = new HTMLPanel("");
				codeNamePanel.addStyleName(AON.CSS.aonFlexColumn());
				
				Label code = new Label(aonService.getCode());
				code.setWidth("10rem");
				code.getElement().getStyle().setFontWeight(FontWeight.BOLD);
				codeNamePanel.add(code);
				
				HTMLPanel name = new HTMLPanel(aonService.getDescriptionTemplate());
				name.getElement().getStyle().setProperty("padding", "1rem 2rem 1rem 0");
				codeNamePanel.add(name);
				
				servicePanel.add(codeNamePanel);
				
				HTMLPanel pricePanel = new HTMLPanel("");
				pricePanel.addStyleName(AON.CSS.aonFlexColumn());
				pricePanel.setHeight("100%");
				pricePanel.getElement().getStyle().setProperty("align-items", "center");
				
				String priceValue = formaDouble(aonService.getItem().getPrice());
				HTMLPanel price = new HTMLPanel("<b>" + priceValue.split("\\.")[0] + "</b>.<small>" + priceValue.split("\\.")[1] + "<small> \u20ac" + " al mes *");
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
	
	private void createUsers(HTMLPanel cataloguePanel, List<ProductBooking> aonUsers) {
		HTMLPanel servicesPanel = new HTMLPanel("");
		servicesPanel.addStyleName(AON.CSS.aonFlexColumn());
		servicesPanel.setWidth("100%");
		servicesPanel.getElement().getStyle().setProperty("max-width", "70rem");
		
		aonUsers.forEach(aonService -> {
			getItemTariff(aonService.getItem().getId(), itemTariff -> {
				HTMLPanel servicePanel = new HTMLPanel("");
				servicePanel.addStyleName(AON.CSS.aonItemFlex());
				servicePanel.setWidth("100%");
				servicePanel.getElement().getStyle().setProperty("justify-content", "space-between");
				servicePanel.getElement().getStyle().setProperty("padding", "1rem");
				servicePanel.getElement().getStyle().setProperty("border", "1px solid #ebebeb");
				servicePanel.getElement().getStyle().setProperty("border-radius", "0.5rem");
				servicePanel.getElement().getStyle().setProperty("margin-bottom", "1rem");
				
				HTMLPanel codeNamePanel = new HTMLPanel("");
				codeNamePanel.addStyleName(AON.CSS.aonFlexColumn());
				
				Label code = new Label(aonService.getCode());
				code.setWidth("10rem");
				code.getElement().getStyle().setFontWeight(FontWeight.BOLD);
				codeNamePanel.add(code);
				
				HTMLPanel name = new HTMLPanel(aonService.getDescriptionTemplate());
				name.getElement().getStyle().setProperty("padding", "1rem 2rem 1rem 0");
				codeNamePanel.add(name);
				
				servicePanel.add(codeNamePanel);
				
				HTMLPanel pricePanel = new HTMLPanel("");
				pricePanel.addStyleName(AON.CSS.aonFlexColumn());
				pricePanel.setHeight("100%");
				pricePanel.getElement().getStyle().setProperty("align-items", "center");
				
				String priceValue = formaDouble(aonService.getItem().getPrice());
				HTMLPanel price = new HTMLPanel("<b>" + priceValue.split("\\.")[0] + "</b>.<small>" + priceValue.split("\\.")[1] + "<small> \u20ac" + " al mes *");
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
				pricePanel.add(createUserButton(aonService));
				
				servicePanel.add(pricePanel);
				
				servicesPanel.add(servicePanel);
			});
		});
		
		cataloguePanel.add(servicesPanel);
		
	}
	
	private HTMLPanel createUserButton(ProductBooking aonService) {
		HTMLPanel buttonPanel = new HTMLPanel("");
		buttonPanel.addStyleName(AON.CSS.aonItemFlex());
		buttonPanel.getElement().getStyle().setProperty("gap", "1rem");
		
		Label activeUsers = new Label("Usr. Activos (" + this.customerCompany.getDomain().getMaxDefinedUsers() + ")");
		activeUsers.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		
		Button bookBtn = new Button();
		bookBtn.setText("+");
		
		bookBtn.getElement().getStyle().setProperty("background", "none");
		bookBtn.getElement().getStyle().setProperty("color", "white");
		bookBtn.getElement().getStyle().setProperty("background-color", "#ff8f00");
		bookBtn.getElement().getStyle().setProperty("width", "3rem");
		bookBtn.getElement().getStyle().setProperty("height", "2.5rem");
		bookBtn.getElement().getStyle().setProperty("border-radius", "5px");
		bookBtn.getElement().getStyle().setProperty("border", "none");
		
		bookBtn.addClickHandler(e -> {
			new UserCreationDialog(currentDomainOptions.getDomainName(), currentDomainOptions.getDomain(), currentDomainOptions.getUser(), officeDomainOptions.getDomain(), customerRelatedRegistry, aonService) {
				
				@Override
				protected void onEnd() {
					reloadParent();
				}
				
				@Override
				protected void onCancel() {}
				
			};
		});
		
		buttonPanel.add(activeUsers);
		buttonPanel.add(bookBtn);
		
		return buttonPanel;
	}
	
	private Button createServiceButton(ProductBooking packProduct) {
		Button bookBtn = new Button();
		bookBtn.setText(isFeeProduct(packProduct.getItem().getId()) ? (packProduct.isNoBooking() ? "Solicitar Baja" : "Descontratar") : (packProduct.isNoBooking() ? "Solicitar Alta" : "Contratar"));
		
		bookBtn.getElement().getStyle().setProperty("background", "none");
		bookBtn.getElement().getStyle().setProperty("color", isFeeProduct(packProduct.getItem().getId()) ? "#d56060" : "white");
		bookBtn.getElement().getStyle().setProperty("background-color", isFeeProduct(packProduct.getItem().getId()) ? "rgb(181, 180, 180)" : "#ff8f00");
		bookBtn.getElement().getStyle().setProperty("width", "10rem");
		bookBtn.getElement().getStyle().setProperty("height", "2.5rem");
		bookBtn.getElement().getStyle().setProperty("border-radius", "5px");
		bookBtn.getElement().getStyle().setProperty("border", "none");
		
		bookBtn.addClickHandler(e -> {
			
			AonCustomDialog dialog = new AonCustomDialog();
			
			HTMLPanel dialogContent = new HTMLPanel("");
			dialogContent.addStyleName(AON.CSS.aonFlexColumn());
			dialogContent.getElement().getStyle().setProperty("padding", "1rem");
			
			HTMLPanel messageDialogPanel = new HTMLPanel("");
			dialogContent.add(messageDialogPanel);
			
			HTMLPanel buttonsPanel = new HTMLPanel("");
			buttonsPanel.addStyleName(AON.CSS.aonItemFlex());
			buttonsPanel.getElement().getStyle().setProperty("justify-content", "center");
			buttonsPanel.getElement().getStyle().setProperty("margin-top", "1rem");
			buttonsPanel.setWidth("100%");
			
			Button closeBtnDialog = new Button();
			closeBtnDialog.setStyleName(AON.CSS.aonCancelButton());
			closeBtnDialog.setText(AON.MSG.cancelAction());
			closeBtnDialog.getElement().getStyle().setMarginRight(10, Unit.PX);
			closeBtnDialog.addClickHandler(ev -> dialog.hide());
			buttonsPanel.add(closeBtnDialog);
			
			Button acceptBtnDialog = new Button();
			acceptBtnDialog.setStyleName(AON.CSS.aonOkButton());
			acceptBtnDialog.setText(isFeeProduct(packProduct.getItem().getId()) ? "Descontratar" : "Contratar");
			acceptBtnDialog.getElement().getStyle().setProperty("background-color", "#eee");
			acceptBtnDialog.setEnabled(false);
			buttonsPanel.add(acceptBtnDialog);
			
			HTMLPanel message = new HTMLPanel(!isFeeProduct(packProduct.getItem().getId()) 
					? "Se va a proceder con la contrataci\u00f3n del producto <b>" + packProduct.getName() + "</b>" 
					: "Se va a proceder a descontratar el producto <b>" + packProduct.getName() + "</b>" 
					);
			dialogContent.add(message);
			
			HTMLPanel terms = new HTMLPanel("");
			terms.addStyleName(AON.CSS.aonItemFlex());
			
			CheckBox acceptTerms = new CheckBox();
			acceptTerms.addValueChangeHandler(ev -> {
				acceptBtnDialog.setEnabled(acceptTerms.getValue());
				acceptBtnDialog.getElement().getStyle().setProperty("background-color", acceptTerms.getValue() ? "transparent" : "#eee");
			});
			Label temrsMessage = new Label("He leido y acepto los ");
			Anchor termsAnchor = new Anchor("Terminos y Condiciones", "https://ayudatpymes.com/aviso-legal/terminos-condiciones/", "_blank");
			termsAnchor.getElement().getStyle().setProperty("color", "#002469");
			Label temrsMessage_2 = new Label(" de contrataci\u00f3n de Aon");
					
			terms.add(acceptTerms);
			terms.add(temrsMessage);
			terms.add(termsAnchor);
			terms.add(temrsMessage_2);
			dialogContent.add(terms);
			
			acceptBtnDialog.addClickHandler(ev -> {
				if(acceptTerms.getValue()) {
					if(customerFees.isEmpty()) {
						new CustomerInfoConfirm(officeDomainOptions.getDomainName(), officeDomainOptions.getDomain(), officeDomainOptions.getUser(), customerCompany) {
							
							@Override
							protected void onEnd() {
								createFee(packProduct);
								dialog.hide();
							}
							
							@Override
							protected void onCancel() {
								dialog.hide();
							}
							
						};
					} else {
						createFee(packProduct);
						dialog.hide();
					}
				} else
					AonMessagePanel.showError(messageDialogPanel, "Debe aceptar los terminos y condiciones para poder aceptar");
			});
			
			dialogContent.add(buttonsPanel);
			
			dialog.setCaption(packProduct.getName());
			dialog.add(dialogContent);
			dialog.center();
			dialog.show();
		});
		
		return bookBtn;
	}
	
	private boolean isFeeProduct(Integer itemId) {
		return customerFees.stream().filter(fee -> fee.getItem().getId().equals(itemId) && (fee.getEndDate() == null || (fee.getEndDate().equals(new Date()) || fee.getEndDate().after(new Date()) ))).findAny().isPresent();
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
	
	private void getList(Consumer<List<ProductBooking>> success) {
		ProductParams params = new ProductParams()
				.setDomainName(officeDomainOptions.getDomainName())
				.setDomain(officeDomainOptions.getDomain())
				.setUser(officeDomainOptions.getUser())
				.setType(ProductType.AUXILIARY)
				.setDomainType(domainType)
				.setOffset(0)
				.setLimit(Integer.MAX_VALUE)
				;
		
		COMMON_SERVICE.getProductsBooking(params, new AsyncCallback<List<ProductBooking>>() {
			
			@Override
			public void onSuccess(List<ProductBooking> productsDB) {
				products = productsDB.stream()
					    .sorted(
						        Comparator.comparing(
						                ProductBooking::getBookingType,
						                Comparator.comparingInt(type -> {
						                    switch (type) {
						                        case PLAN:         return 0;
						                        case SERVICE:      return 2;
						                        case USER:         return 1;
						                        case CONSULTANCY:  return 3;
						                        default:           return Integer.MAX_VALUE;
						                    }
						                })
						        ).thenComparing(
						                ProductBooking::getPosition,
						                Comparator.nullsLast(Comparator.naturalOrder())
						        )
						    )
						    .collect(Collectors.toList());
				
				success.accept(products);
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
	
	public static native void reloadParent() /*-{
	    $wnd.parent.location.reload();
	}-*/;
	
}
