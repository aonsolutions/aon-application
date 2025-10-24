package com.esferalia.aon.gwt.fiscal.client.customer;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomTextBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.GeoZone;
import com.esferalia.aon.occam.api.model.aonsolutions.AonLanguage;
import com.esferalia.aon.occam.api.model.finance.BankAccount;
import com.esferalia.aon.occam.api.model.finance.BankSwift;
import com.esferalia.aon.occam.api.model.finance.BicSwiftValidator;
import com.esferalia.aon.occam.api.model.registry.CustomerFull;
import com.esferalia.aon.occam.api.model.registry.RegistryAddress;
import com.esferalia.aon.occam.api.model.registry.RegistryBank;
import com.esferalia.aon.occam.api.model.registry.RegistryPayMethod;
import com.esferalia.aon.occam.api.model.registry.RegistryRelationship;
import com.esferalia.aon.occam.api.model.type.PayMethodType;
import com.esferalia.aon.occam.api.model.type.StreetType;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;

public abstract class CustomerInfoConfirm extends AonCustomDialog {

	private static CommonServiceAsync COMMON_SERVICE;

	private HTMLPanel content = new HTMLPanel(AonStringUtils.EMPTY);
	private HTMLPanel messagePanel = new HTMLPanel(AonStringUtils.EMPTY);
	
	private HTMLPanel generalInfoContent = new HTMLPanel(AonStringUtils.EMPTY);
	
	private AonCustomTextBox documentType = new AonCustomTextBox("Tipo Documento");
	private AonCustomTextBox documentCountry = new AonCustomTextBox("Pa\u00eds Emisi\u00f3n");
	private AonCustomTextBox document = new AonCustomTextBox("N\u00famero Documento");
	private AonCustomTextBox name = new AonCustomTextBox("Nombre/Raz\u00f3n Social");
	
	private HTMLPanel addressContent = new HTMLPanel(AonStringUtils.EMPTY);
	
	private AonCustomListBox streetType = new AonCustomListBox("Tipo V\u00eda");
	private AonCustomTextBox address = new AonCustomTextBox("Direcci\u00f3n");
	private AonCustomTextBox addressNum = new AonCustomTextBox("N\u00famero");
	private AonCustomTextBox addressInfo = new AonCustomTextBox("Rest. Direcci\u00f3n");
	
	private AonCustomTextBox addressZip = new AonCustomTextBox("C\u00f3digo Postal");
	private AonCustomListBox addressProvince = new AonCustomListBox("Provincia");
	private AonCustomTextBox addressMunicipality = new AonCustomTextBox("Localidad");
	
	private HTMLPanel payMethodContent = new HTMLPanel(AonStringUtils.EMPTY);
	
	private AonCustomListBox payMethod = new AonCustomListBox("Forma Pago");
	private AonCustomTextBox account = new AonCustomTextBox("IBAN");
	private AonCustomTextBox bic = new AonCustomTextBox("BIC");
	
	private HTMLPanel buttonsContent = new HTMLPanel(AonStringUtils.EMPTY);
	
	private Button acceptButton;
	
	private String domainName;
	private Integer domainId;
	private String user;
	private Integer customerRelatedRegistry;
	
	private RegistryRelationship registryRelationship;
	private List<RegistryAddress> customerRaddresses = new ArrayList<RegistryAddress>();
	private List<RegistryPayMethod> customerRPayMethods = new ArrayList<RegistryPayMethod>();
	
	private AonConfiguration aonConfiguration;
	
	private CustomerFull customer;
	
	private boolean hasInfoChange = false;
	private boolean hasAddressChange = false;
	private boolean hasAccountChange = false;

	public CustomerInfoConfirm(String domainName, Integer domainId, String user, Company customerCompany) {
		CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
		COMMON_SERVICE = new CommonServiceAsyncDecorator(commonServiceRaw);
		
		this.domainName = domainName;
		this.domainId = domainId;
		this.user = user;
		this.customerRelatedRegistry = customerCompany.getId();
		
		setCaption("Datos Facturaci\u00f3n");
		showCloseButton(true);
		
		getCustomerInfo(end -> {
			content.addStyleName(AON.CSS.aonFlexColumn());
			content.getElement().getStyle().setProperty("padding", "1rem 0");
			content.setWidth("32rem");
			
			content.add(messagePanel);
			
			initGeneralInfo();
			initAddress();
			initPayMethod();
			initButtons();
			
			setWidget(content);
			center();
			show();
			
			getCloseButton().addClickHandler(e -> onCancel());
		});
	}

	private void initGeneralInfo() {
		generalInfoContent.addStyleName(AON.CSS.aonFlexColumn());
		generalInfoContent.getElement().getStyle().setProperty("padding", "0 1rem");
		
		documentType.setEnable(false);
		documentCountry.setEnable(false);
		document.setEnable(false);
		
		HTMLPanel row = new HTMLPanel(AonStringUtils.EMPTY);
		row.addStyleName(AON.CSS.aonItemFlex());
		
		documentType.setWidth("7rem");
		documentCountry.setWidth("10rem");
		
		row.add(documentType);
		row.add(documentCountry);
		row.add(document);
		
		HTMLPanel row2 = new HTMLPanel(AonStringUtils.EMPTY);
		row2.addStyleName(AON.CSS.aonItemFlex());
		row2.add(name);
		
		generalInfoContent.add(row);
		generalInfoContent.add(row2);
		
		if(null != this.customer) {
			documentType.setValue(null == customer.getRegistry().getDocumentType() ? "" : customer.getRegistry().getDocumentType().getDescription());
			documentCountry.setValue(null == customer.getRegistry().getDocumentCountry() ? "" : customer.getRegistry().getDocumentCountry().getName());
			name.setValue(customer.getRegistry().getName());
			document.setValue(customer.getRegistry().getDocument());
		}
		
		name.addValueChangeHandler(e -> hasInfoChange = true);
		
		content.add(generalInfoContent);
		
	}

	private void initAddress() {
		RegistryAddress registryAddress = customerRaddresses.isEmpty() ? null : customerRaddresses.get(0);
	
		addressContent.addStyleName(AON.CSS.aonFlexColumn());
		addressContent.getElement().getStyle().setProperty("padding", "0 1rem");
		
		Label addressTitle = new Label("Direcci\u00f3n");
		addressTitle.getElement().getStyle().setProperty("font-weight", "bold");
		addressTitle.getElement().getStyle().setProperty("margin-top", ".7rem");
		
		HTMLPanel row = new HTMLPanel(AonStringUtils.EMPTY);
		row.addStyleName(AON.CSS.aonItemFlex());
		
		streetType.setMaxWidth("10rem");
		addressNum.setMaxWidth("5rem");
		
		streetType.addItem("-", "");
		StreetType.getSpanishTypes().forEach(st -> streetType.addItem(st.getDescription(), st.getAeatCode()));
		
		row.add(streetType);
		row.add(address);
		row.add(addressNum);
		
		HTMLPanel row2 = new HTMLPanel(AonStringUtils.EMPTY);
		row2.addStyleName(AON.CSS.aonItemFlex());
		
		row2.add(addressInfo);
		
		HTMLPanel row3 = new HTMLPanel(AonStringUtils.EMPTY);
		row3.addStyleName(AON.CSS.aonItemFlex());
		
		addressProvince.addItem("-", "");
		aonConfiguration.getGeozones().forEach(geozone -> addressProvince.addItem(geozone.getName(), geozone.getId().toString()));
		
		addressZip.setWidth("6rem");
		
		row3.add(addressZip);
		row3.add(addressProvince);
		row3.add(addressMunicipality);
		
		addressContent.add(addressTitle);
		addressContent.add(row);
		addressContent.add(row2);
		addressContent.add(row3);
		
		if(null != registryAddress) {
			streetType.setValue(null == registryAddress.getStreetType() ? null : registryAddress.getStreetType().getAeatCode());
			address.setValue(registryAddress.getAddress());
			addressNum.setValue(registryAddress.getNumber());
			
			addressZip.setValue(registryAddress.getZip());
			addressProvince.setValue(null == registryAddress.getGeozone() ? null : aonConfiguration.getGeozones().stream().filter(g -> g.getId().equals(registryAddress.getGeozone())).findFirst().get().getId().toString());
			addressMunicipality.setValue(registryAddress.getCity());
			
			if(null == registryAddress.getGeozone() && null != registryAddress.getZip()) {
				String geozoneCode = registryAddress.getZip().substring(0, 2);
				GeoZone geozone = aonConfiguration.getGeozones().stream().filter(g -> AonStringUtils.equalsIgnoreCase(g.getCode(), geozoneCode)).findFirst().orElse(null);
				if(null != geozone) addressProvince.setValue(geozone.getId().toString());
			}
		}
		
		streetType.addChangeHandler(e -> hasAddressChange = true);
		address.addValueChangeHandler(e -> hasAddressChange = true);
		addressNum.addValueChangeHandler(e -> hasAddressChange = true);
		addressInfo.addValueChangeHandler(e -> hasAddressChange = true);
		addressZip.addValueChangeHandler(e -> hasAddressChange = true);
		addressProvince.addChangeHandler(e -> hasAddressChange = true);
		addressMunicipality.addValueChangeHandler(e -> hasAddressChange = true);
		
		content.add(addressContent);
	}
		
	private void initPayMethod() {
		RegistryPayMethod registryPayMethod = customerRPayMethods.isEmpty() ? null : customerRPayMethods.get(0);
		
		payMethodContent.addStyleName(AON.CSS.aonFlexColumn());
		payMethodContent.getElement().getStyle().setProperty("padding", "0 1rem");
		
		Label payMethodTitle = new Label("Datos Bancarios");
		payMethodTitle.getElement().getStyle().setProperty("font-weight", "bold");
		payMethodTitle.getElement().getStyle().setProperty("margin-top", ".7rem");
		
		HTMLPanel row = new HTMLPanel(AonStringUtils.EMPTY);
		row.addStyleName(AON.CSS.aonItemFlex());
		
		aonConfiguration.getPayMethods().stream().filter(pm -> pm.getType().equals(PayMethodType.NEGOTIABLE_DOCUMENT)).forEach(pm -> payMethod.addItem(pm.getName(), pm.getId().toString()));
		payMethod.setEnable(false);
		
		bic.setWidth("8rem");
		
		//row.add(payMethod);
		row.add(account);
		row.add(bic);
		
		payMethodContent.add(payMethodTitle);
		payMethodContent.add(row);
		
		if(null != registryPayMethod) {
			payMethod.setValue(registryPayMethod.getPayMethod().getId().toString());
			account.setValue(registryPayMethod.getRbank().getBankAccount().getIban());
			bic.setValue(registryPayMethod.getRbank().getBic());
		}
		
		account.addValueChangeHandler(e -> {
			if(account.getValue().contains(" ")) account.setValue(account.getValue().replaceAll(" ", ""));
			
			BankAccount bankAccount = new BankAccount(account.getValue());
			if(bankAccount.isValidIban()) {
				String bankSwift = getBankSwift(account.getValue());
				bic.setValue(bankSwift);
				hasAccountChange = true;
			}
			else AonMessagePanel.showError(messagePanel, "El IBAN es incorrecto");
		});
		
		bic.addValueChangeHandler(e -> {
			if(BicSwiftValidator.isValidBic(bic.getValue())) hasAccountChange = true;
			else AonMessagePanel.showError(messagePanel, "El BIC es incorrecto");
		});
		
		content.add(payMethodContent);
	}
	
	private String getBankSwift(String account) {
		if (AonStringUtils.isNotBlank(account)) {
			BankSwift bankSwiftEntry = BankSwift.safeValueOf("B" + AonStringUtils.substring(account, 4, 8));
			return null == bankSwiftEntry ? null : bankSwiftEntry.getSwift();
		}
		return null;
	}

	private String getBankAlias(String account) {
		if (AonStringUtils.isNotBlank(account)) {
			BankSwift bankSwiftEntry = BankSwift.safeValueOf("B" + AonStringUtils.substring(account, 4, 8));
			return null == bankSwiftEntry ? null : bankSwiftEntry.getBankName();
		}
		return null;
	}
	
	private void initButtons() {
		buttonsContent.addStyleName(AON.CSS.aonItemFlex());
		buttonsContent.addStyleName(AON.CSS.aonDisplayFlexCenter());
		buttonsContent.setWidth("100%");
		buttonsContent.getElement().getStyle().setProperty("margin-top", ".5rem");
		
		acceptButton = new Button();
		acceptButton.setStyleName(AON.CSS.aonOkButton());
		acceptButton.setText("Confirmar");
		acceptButton.addClickHandler(e -> {
			if(hasInfoChange || hasAddressChange || hasAccountChange) {
				
				// Avisar y actualizar datos
				String errorMessage = emptyInfo();
				if(AonStringUtils.isNotBlank(errorMessage)) AonMessagePanel.showError(messagePanel, errorMessage);
				else warnDialog();
				
			} else {
				String errorMessage = emptyInfo();
				if(AonStringUtils.isNotBlank(errorMessage)) AonMessagePanel.showError(messagePanel, errorMessage);
				else {
					hide();
					onEnd();
				}
			}
		});
		
		buttonsContent.add(acceptButton);
		
		content.add(buttonsContent);
	}
	
	private void warnDialog() {
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
		acceptBtnDialog.setText("Continuar");
		buttonsPanel.add(acceptBtnDialog);
		
		HTMLPanel messagePanel = new HTMLPanel("");
		messagePanel.addStyleName(AON.CSS.aonItemFlex());
		
		AonTableButton info = new AonTableButton("Actualizaci\u00f3n Informaci\u00f3n", AON.CSS.aonIconInfo());
		HTMLPanel message = new HTMLPanel("Los datos modificados se van a actualizar. <br>Con estos datos ya se han emitido facturas y puede provocar que tenga que emitir rectificativas correspondientes al ejercicio actual.");
		
		messagePanel.add(info);
		messagePanel.add(message);
		dialogContent.add(messagePanel);
		
		acceptBtnDialog.addClickHandler(ev -> {
			uploadInfo(end -> {
				dialog.hide();
				hide();
				onEnd();
			});
		});
		
		dialogContent.add(buttonsPanel);
		
		dialog.setCaption("Actualizaci\u00f3n Informaci\u00f3n");
		dialog.add(dialogContent);
		dialog.center();
		dialog.show();
	}

	private void uploadInfo(Consumer<Void> end) {
		if(hasInfoChange) {
			updateComapnyIfo(finish -> end.accept(null));
		} else if(hasAddressChange) {
			updateRegistryAddress(finish -> end.accept(null));
		} else if(hasAccountChange) {
			updateRegistryPayMethod(finish -> end.accept(null));
		} else 
			end.accept(null);
	}
	
	private void updateComapnyIfo(Consumer<Void> end) {
		if(hasInfoChange) {
			
			customer.getRegistry().setName(name.getValue());
			
			COMMON_SERVICE.saveCustomer(domainName, domainId, user, customer.getRegistry(), new AsyncCallback<Customer>() {

				@Override
				public void onFailure(Throwable caught) {
					AonDialog errorDialog = new AonDialog("Error datos generales", new HTMLPanel(caught.getMessage()));
					errorDialog.info();
				}

				@Override
				public void onSuccess(Customer result) {
					if(hasAddressChange) {
						updateRegistryAddress(finish -> end.accept(null));
					} else if(hasAccountChange) {
						updateRegistryPayMethod(finish -> end.accept(null));
					} else 
						end.accept(null);
				}
				
			});
			
		} else
			end.accept(null);
	}
	
	private void updateRegistryAddress(Consumer<Void> end) {
		if(hasAddressChange) {
			RegistryAddress updateRegistryAddress = customerRaddresses.stream().findFirst().orElse(new RegistryAddress());
			
			if(null == updateRegistryAddress.getId()) {
				updateRegistryAddress.setDomain(domainId);
				updateRegistryAddress.setRegistry(registryRelationship.getRegistry());
			}
			
			updateRegistryAddress.setStreetType(StreetType.getForAeatCode(streetType.getValue(), AonLanguage.SPANISH));
			updateRegistryAddress.setAddress(address.getValue());
			updateRegistryAddress.setNumber(addressNum.getValue());
			updateRegistryAddress.setAddress2(addressInfo.getValue());
			updateRegistryAddress.setZip(addressZip.getValue());
			updateRegistryAddress.setGeozone(aonConfiguration.getGeozones().stream().filter(g -> g.getId().equals(Integer.parseInt(addressProvince.getValue()))).findFirst().get().getId());
			updateRegistryAddress.setCity(addressMunicipality.getValue());
			
			COMMON_SERVICE.saveRegistryAddress(domainName, domainId, user, updateRegistryAddress, new AsyncCallback<RegistryAddress>() {
				
				@Override
				public void onSuccess(RegistryAddress registryAddress) {
					if(hasAccountChange) {
						updateRegistryPayMethod(finish -> end.accept(null));
					} else end.accept(null);
				}
				
				@Override
				public void onFailure(Throwable caught) {
					AonDialog errorDialog = new AonDialog("Error direcci\u00f3n", new HTMLPanel(caught.getMessage()));
					errorDialog.info();
				}
				
			});
		} else 
			end.accept(null);
	}
	
	private void updateRegistryPayMethod(Consumer<Void> end) {
		if(hasAccountChange) {
			RegistryPayMethod registryPayMethod = customerRPayMethods.stream().findFirst().orElse(new RegistryPayMethod());
			
			if(null == registryPayMethod.getId()) {
				registryPayMethod.setDomain(domainId);
				registryPayMethod.setRegistry(registryRelationship.getRegistry());
			}
			
			String bankAlias = getBankAlias(account.getValue());
			if(bankAlias.length() > 25) bankAlias = bankAlias.substring(0, 25);
			
			RegistryBank rbank = new RegistryBank()
					.setBankAccount(new BankAccount(account.getValue()))
					.setBic(bic.getValue())
					.setAlias(bankAlias);
			
			if(null == registryPayMethod.getId()) {
				rbank.setDomain(domainId);
				rbank.setRegistry(registryRelationship.getRegistry());
			}
			
			registryPayMethod.setPayMethod(aonConfiguration.getPayMethods().stream().filter(pm -> pm.getType().equals(PayMethodType.NEGOTIABLE_DOCUMENT)).findFirst().get());
			registryPayMethod.setRbank(rbank);
			
			COMMON_SERVICE.saveRegistryPayMethod(domainName, domainId, user, registryPayMethod, new AsyncCallback<RegistryPayMethod>() {
				
				@Override
				public void onSuccess(RegistryPayMethod registryPayMethod) {
					end.accept(null);
				}
				
				@Override
				public void onFailure(Throwable caught) {
					AonDialog errorDialog = new AonDialog("Error direcci\u00f3n", new HTMLPanel(caught.getMessage()));
					errorDialog.info();
				}
				
			});
		} else 
			end.accept(null);
	}

	private String emptyInfo() {
		if(AonStringUtils.isBlank(name.getValue())) return "El campo raz\u00f3n social es obligatorio";
		
		if(AonStringUtils.isBlank(address.getValue())) return "El campo direcci\u00f3n es obligatorio";
		if(AonStringUtils.isBlank(addressNum.getValue())) return "El campo N\u00famero es obligatorio";
		if(AonStringUtils.isBlank(addressZip.getValue())) return "El campo C\u00f3digo Postal es obligatorio";
		if(AonStringUtils.isBlank(addressProvince.getValue())) return "El campo Provincia es obligatorio";
		if(AonStringUtils.isBlank(addressMunicipality.getValue())) return "El campo Localidad es obligatorio";
		
		if(AonStringUtils.isBlank(account.getValue()) || !new BankAccount(account.getValue()).isValidIban()) return "El campo IBAN es obligatorio o tiene un formato incorrecto";
		if(AonStringUtils.isBlank(bic.getValue()) || 
				( (null != getBankSwift(account.getValue()) && !AonStringUtils.equalsIgnoreCase(bic.getValue(), getBankSwift(account.getValue()))) || !BicSwiftValidator.isValidBic(bic.getValue())) 
		) return "El campo BIC es obligatorio o es incorrecto";
		
		return null;
	}

	private void getCustomerInfo(Consumer<Void> end) {
		COMMON_SERVICE.getAonConfiguration(domainName, domainId, user, new AsyncCallback<AonConfiguration>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Error configuraci\u00f3n dominio: " + caught.getMessage());
			}

			@Override
			public void onSuccess(AonConfiguration aonConfigurationDB) {
				aonConfiguration = aonConfigurationDB;
				
				COMMON_SERVICE.getRegistryRelationshipsByRelated(domainName, domainId, user, customerRelatedRegistry, new AsyncCallback<List<RegistryRelationship>>() {

					@Override
					public void onFailure(Throwable caught) {
						Window.alert("Error relaci\u00f3n cliente: " + caught.getMessage());
					}

					@Override
					public void onSuccess(List<RegistryRelationship> result) {
						if(result.isEmpty()) Window.alert("No existe relaci\u00f3n cliente/empresa (relationship)" );
						else {
							registryRelationship = result.get(0);
							
							COMMON_SERVICE.getCustomer(domainName, domainId, user, registryRelationship.getRegistry(), new AsyncCallback<CustomerFull>() {

								@Override
								public void onFailure(Throwable caught) {
									Window.alert("Error informaci\u00f3n cliente: " + caught.getMessage());
								}

								@Override
								public void onSuccess(CustomerFull customerDB) {
									customer = customerDB;
									
									COMMON_SERVICE.getRegistryAddresses(domainName, domainId, user, registryRelationship.getRegistry(), new AsyncCallback<List<RegistryAddress>>() {

										@Override
										public void onFailure(Throwable caught) {
											Window.alert("Error direcciones cliente: " + caught.getMessage());
										}

										@Override
										public void onSuccess(List<RegistryAddress> addresses) {
											customerRaddresses = addresses;
											
											COMMON_SERVICE.getRegistryPayMethods(domainName, domainId, user, registryRelationship.getRegistry(), new AsyncCallback<List<RegistryPayMethod>>() {

												@Override
												public void onFailure(Throwable caught) {
													Window.alert("Error direcciones cliente: " + caught.getMessage());
												}
									
												@Override
												public void onSuccess(List<RegistryPayMethod> registryPayMethods) {
													customerRPayMethods = registryPayMethods;
													end.accept(null);
												}
											});
										}
									});
								}
							});
						}
					}
				});
			}
		});
	}

	protected abstract void onEnd();
	protected abstract void onCancel();

}
