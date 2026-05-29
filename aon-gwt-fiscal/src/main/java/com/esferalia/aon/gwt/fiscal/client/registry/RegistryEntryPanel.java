package com.esferalia.aon.gwt.fiscal.client.registry;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.RegistryService;
import com.esferalia.aon.gwt.common.client.RegistryServiceAsync;
import com.esferalia.aon.gwt.common.client.RegistryServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.widget.solutions.AddressTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDockLayout;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomSuggestBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomSuggestOracle;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomTextArea;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomTextBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomToogleButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMainCertificatesPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonVisualIdentity;
import com.esferalia.aon.gwt.common.client.widget.solutions.EnterpriseActivityTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.MediaTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.RDirStaffTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.RecordDataTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.RegistryPaymethodBanksTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.RegistryStatusSelect;
import com.esferalia.aon.gwt.common.client.widget.solutions.WorkplaceTable;
import com.esferalia.aon.gwt.common.shared.Dni;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.GeoZone;
import com.esferalia.aon.occam.api.model.registry.CompanyFull;
import com.esferalia.aon.occam.api.model.registry.CreditorFull;
import com.esferalia.aon.occam.api.model.registry.CustomerFull;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.registry.RegistryAddress;
import com.esferalia.aon.occam.api.model.registry.RegistryMedia;
import com.esferalia.aon.occam.api.model.registry.RegistrySource;
import com.esferalia.aon.occam.api.model.registry.SupplierFull;
import com.esferalia.aon.occam.api.model.security.Scope;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.DocumentType;
import com.esferalia.aon.occam.api.model.type.InvoiceTransactionType;
import com.esferalia.aon.occam.api.model.type.MediaType;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public abstract class RegistryEntryPanel extends AonCustomDockLayout {

	// ------------------------------------------------- CommonServiceAsync

	static CommonServiceAsync commonService;

	private static void initializeCommonService() {
		if (commonService == null) {
			CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
			commonService = new CommonServiceAsyncDecorator(commonServiceRaw);
		}
	}
	
	// ------------------------------------------------- RegistryServiceAsync
	
	static RegistryServiceAsync registryService;
	
	private static void initializeRegistryService() {
		if (registryService == null) {
			RegistryServiceAsync registryServiceRaw = GWT.create(RegistryService.class);
			registryService = new RegistryServiceAsyncDecorator(registryServiceRaw);
		}
	}

	// ------------------------------------------------- Variables

	private RegistryModuleOptions options;
	private RegistrySource registrySource;
	
	private AonToolbarButton backButton = new AonToolbarButton("Volver", AON.CSS.aonIconBack());

	private HTMLPanel container;

	private HTMLPanel messagePanel = new HTMLPanel(AonStringUtils.EMPTY);
	
	private RegistryStatusSelect registryStatus;
	private AonCustomListBox documentType = new AonCustomListBox("Tipo");
	private AonCustomListBox documentNationality = new AonCustomListBox("Pa\u00eds");
	private AonCustomTextBox document = new AonCustomTextBox("Documento");
	private AonCustomTextBox alias = new AonCustomTextBox(AON.MSG.alias());
	
	private AonCustomTextBox name = new AonCustomTextBox(AON.MSG.enterpriseName());
	private AonCustomTextBox firstSurname = new AonCustomTextBox("Apellido");
	private AonCustomTextBox secondSurname = new AonCustomTextBox("Apellido 2");
	
	private AonCustomTextBox mainAddress = new AonCustomTextBox("Direcci\u00f3n");
	private AonCustomListBox addressLB = new AonCustomListBox("Direcciones");
	
	private AonCustomTextBox mainPhone = new AonCustomTextBox("Tel\u00e9fono");
	private AonCustomListBox phoneLB = new AonCustomListBox("Tel\u00e9fonos");
	private AonCustomTextBox mainEmail = new AonCustomTextBox("Email");
	private AonCustomListBox emailLB = new AonCustomListBox("Emails");
	
	private AonCustomListBox scopeLB = new AonCustomListBox("\u00c1mbito");
	//private AonCustomListBox accountLB = new AonCustomListBox("Cuenta Contable"); 
	private AonCustomSuggestBox accountSB = new AonCustomSuggestBox("Cuenta Contable", new AonCustomSuggestOracle());
	
	private AonCustomTextArea observation = new AonCustomTextArea("Observaciones");
	
	private AonCustomListBox transactionLB = new AonCustomListBox("Tipo Transacci\u00f3n");
	private AonCustomToogleButton irpf = new AonCustomToogleButton("I.R.P.F.");
	private AonCustomToogleButton re = new AonCustomToogleButton("R.E.");
	private AonCustomToogleButton criterio = new AonCustomToogleButton("Criterio Caja");
	
	
	private HTMLPanel rightInfoTable;
	private HTMLPanel leftInfoTable;
	
	private TabLayoutPanel tablayoutPanel;

	// Other Info
	private AonVisualIdentity aonVisualIdentity;
	private AddressTable addressTable;
	private MediaTable mediaTable;
	private WorkplaceTable workplaceTable;
	private EnterpriseActivityTable enterpriseActivityTable;
	private RDirStaffTable rDirStaffTable;
	private RecordDataTable recordDataTable;
	private RegistryPaymethodBanksTable registryPaymethodBanksTable;
	
	private CompanyFull company;
	private Registry registry;
	
	private Integer registryId;
	private CustomerFull customerFull;
	private CreditorFull creditorFull;
	private SupplierFull supplierFull;
	
	private boolean editEnable = false;
	
	private HashSet<Account> accounts = new HashSet<Account>();
	private Timer accountsTimer;
	private boolean accountsLoading = false;
	
	// ------------------------------------------------- Constructor

	public RegistryEntryPanel(RegistryModuleOptions options, RegistrySource registrySource) {
		super(getToolbarTitle(registrySource));

		this.options = options;
		this.registrySource = registrySource;
		this.editEnable = this.registrySource.equals(RegistrySource.ENVIROMENT) || this.registrySource.equals(RegistrySource.COMPANY) ? false : true;

		initializeCommonService();
		initializeRegistryService();

		hideSearchWidget();

		createToolbar();

		tablayoutPanel = new TabLayoutPanel(25.00, Unit.PX);
		tablayoutPanel.setHeight("100%");

		container = new HTMLPanel(AonStringUtils.EMPTY);
		container.addStyleName(AON.CSS.aonFlexColumn2());
		container.getElement().getStyle().setProperty("padding", "1rem");
		add(container);

		getRegistryBySource();
	}
	
	public RegistryEntryPanel(RegistryModuleOptions options, RegistrySource registrySource, Integer registryId) {
		super(getToolbarTitle(registrySource));

		this.options = options;
		this.registrySource = registrySource;
		this.registryId = registryId;
		this.editEnable = this.registrySource.equals(RegistrySource.ENVIROMENT) || this.registrySource.equals(RegistrySource.COMPANY) ? false : true;

		initializeCommonService();
		initializeRegistryService();

		hideSearchWidget();

		createToolbar();

		tablayoutPanel = new TabLayoutPanel(25.00, Unit.PX);
		tablayoutPanel.setHeight("100%");

		container = new HTMLPanel(AonStringUtils.EMPTY);
		container.addStyleName(AON.CSS.aonFlexColumn2());
		container.getElement().getStyle().setProperty("padding", "1rem");
		add(container);
		
		getRegistryBySource();
	}
	
	public void loadNewRegistry(Integer registryId) {
		this.registryId = registryId;
		getRegistryBySource();
	}

	private static String getToolbarTitle(RegistrySource registrySource) {
		switch (registrySource) {
			case ENVIROMENT:
				return "Gesti\u00f3n Entorno";
			case COMPANY:
				return "Gesti\u00f3n Empresa";
			case CUSTOMER:
				return "Gesti\u00f3n Cliente";
			default:
				return "Gesti\u00f3n";
		}
	}

	@Override
	protected void onClearFilter() {}

	private void createToolbar() {
		backButton.addClickHandler(e -> onBack());
		backButton.setVisible(false);
		addToolbarButton(backButton);
		
		AonToolbarButton saveButton = new AonToolbarButton("Guardar", AON.CSS.aonIconSave());
		saveButton.addClickHandler(e -> {
			switch (this.registrySource) {
				case ENVIROMENT:
				case COMPANY: {
					saveCompanyFull(saved -> {
					});
					break;
				}
				case CUSTOMER: {
					saveCustomerFull(saved -> {
					});
					break;
				}
				case CREDITOR: {
					saveCreditorFull(saved -> {
					});
					break;
				}
				case SUPPLIER: {
					saveSupplierFull(saved -> {
					});
					break;
				}
				default:
					throw new IllegalArgumentException("Unexpected value: " + this.registrySource);
			}
		});
		addToolbarButton(saveButton);
		
		AonToolbarButton editButton = new AonToolbarButton("Editar datos", AON.CSS.aonIconEdit());
		editButton.setVisible(this.registrySource.equals(RegistrySource.ENVIROMENT) || this.registrySource.equals(RegistrySource.COMPANY));
		editButton.addClickHandler(e -> {
			editEnable = !editEnable;

			documentNationality.setEnabled(editEnable);
			documentType.setEnabled(editEnable);
			document.setEnable(editEnable);
			name.setEnable(editEnable);
			firstSurname.setEnable(editEnable);
			secondSurname.setEnable(editEnable);
		});
		addToolbarButton(editButton);
		
		if(!this.registrySource.equals(RegistrySource.ENVIROMENT) && !this.registrySource.equals(RegistrySource.COMPANY)) {
			AonToolbarButton deleteButton = new AonToolbarButton("Eliminar", AON.CSS.aonIconDelete());
			deleteButton.addClickHandler(e -> {
				switch (this.registrySource) {
					case CUSTOMER: {
						deleteCustomerFull(saved -> {
							onBack();
						});
						break;
					}
					case CREDITOR: {
						deleteCreditorFull(saved -> {
							onBack();
						});
						break;
					}
					case SUPPLIER: {
						deleteSupplierFull(saved -> {
							onBack();
						});
						break;
					}
					default:
						throw new IllegalArgumentException("Unexpected value: " + this.registrySource);
				}
			});
			addToolbarButton(deleteButton);
			
			AonToolbarButton prevButton = new AonToolbarButton("Previo", AON.CSS.aonIconPrev());
			prevButton.addClickHandler(e -> {
				onPrev(registryId);
			});
			addToolbarButton(prevButton);
			
			AonToolbarButton nextButton = new AonToolbarButton("Siguiente", AON.CSS.aonIconNext());
			nextButton.addClickHandler(e -> {
				onNext(registryId);
			});
			addToolbarButton(nextButton);
		}
	}

	public void showBackButton() {
		backButton.setVisible(true);
	}
	
	// ------------------------------------------------- DataBase

	private void getRegistryBySource() {
		switch (this.registrySource) {
			case ENVIROMENT:
			case COMPANY: {
				getCompanyFull(companyFull -> initCompanyRegistry() );
				break;
			}
			case CUSTOMER: {
				getCustomerFull(custmerFull -> initCompanyRegistry() );
				break;
			}
			case SUPPLIER: {
				getSupplierFull(custmerFull -> initCompanyRegistry() );
				break;
			}
			case CREDITOR: {
				getCreditorFull(custmerFull -> initCompanyRegistry() );
				break;
			}
			default:
				throw new IllegalArgumentException("Unexpected value: " + this.registrySource);
		}
	}

	private void initCompanyRegistry() {
		container.clear();
		tablayoutPanel.clear();

		container.add(messagePanel);

		HTMLPanel gridPanel = new HTMLPanel(AonStringUtils.EMPTY);
		gridPanel.setStyleName(AON.CSS.aonItemFlex());
		gridPanel.getElement().getStyle().setProperty("margin", "0 1rem 1rem");
		gridPanel.getElement().getStyle().setProperty("align-items", "flex-start");
		gridPanel.getElement().getStyle().setProperty("gap", "2rem");

		leftInfoTable = createTable();
		leftInfoTable.getElement().getStyle().setProperty("flex", "1");
		
		if(!this.registrySource.equals(RegistrySource.ENVIROMENT) && !this.registrySource.equals(RegistrySource.COMPANY)) {
			getStatus();
		}

		alias.addValueChangeHandler(e -> registry.setAlias(e.getValue()));
		alias.setValue(registry.getAlias());

		if (!this.registrySource.equals(RegistrySource.ENVIROMENT)) {
			documentType.clearItems();
			for (int i = 0; i < DocumentType.values().length; i++)
				documentType.addItem(DocumentType.values()[i].getDescription(), DocumentType.values()[i].toString());
			documentType.setValue(registry.getDocumentType().toString());
			documentType.getElement().getStyle().setProperty("max-width", "5rem");
			documentType.setEnabled(editEnable);

			documentNationality.clearItems();
			for (int i = 0; i < Country.values().length; i++)
				documentNationality.addItem(Country.values()[i].getIso2(), Country.values()[i].getIso2());
			documentNationality.addChangeHandler(
					e -> registry.setDocumentCountry(Country.safeValueOf(documentNationality.getValue())));
			documentNationality.setValue(registry.getDocumentCountry().getIso2());
			documentNationality.getElement().getStyle().setProperty("max-width", "4rem");
			documentNationality.setEnabled(editEnable);

			document.addValueChangeHandler(e -> {
				registry.setDocument(e.getValue());
				if (!checkDocumentValidation(e.getValue()))
					AonMessagePanel.showError(messagePanel, "El documento no tiene un formato valido");
				else
					AonMessagePanel.hideMessage(messagePanel);
			});
			document.setValue(registry.getDocument());
			document.setEnable(editEnable);
			document.getElement().getStyle().setProperty("max-width", "7rem");

			if(!this.registrySource.equals(RegistrySource.ENVIROMENT) && !this.registrySource.equals(RegistrySource.COMPANY))
				leftInfoTable.add(createRow(registryStatus, documentType, documentNationality, document, alias));
			else 
				leftInfoTable.add(createRow(documentType, documentNationality, document, alias));
		} else {
			leftInfoTable.add(createRow(alias));
		}

		

		rightInfoTable = createTable();
		rightInfoTable.getElement().getStyle().setProperty("flex", "1");
		
		name.addValueChangeHandler(e -> registry.setName(e.getValue()));
		name.setValue(registry.getName());
		name.setEnable(editEnable);
		
		if(!this.registrySource.equals(RegistrySource.ENVIROMENT) && !this.registrySource.equals(RegistrySource.COMPANY)) {
			
			getAddresses();
			getPhones();
			getEmails();
			
			leftInfoTable.add(createRow(name, firstSurname, secondSurname));
			
			rightInfoTable.add(createRow(mainAddress, addressLB));
			rightInfoTable.add(createRow(mainPhone, phoneLB, mainEmail, emailLB));
			
		} else {
			rightInfoTable.add(createRow(name, firstSurname, secondSurname));
		}
		
		if (!this.registrySource.equals(RegistrySource.ENVIROMENT)) {
			createNameByDocumentType();
			
			documentType.addChangeHandler(e -> {
				registry.setDocumentType(DocumentType.safeValueOf(documentType.getValue()));
				createNameByDocumentType();
				registry.setLegalPerson(registry.getDocumentType().equals(DocumentType.CIF));
			});
		} else createNameByDocumentType();

		gridPanel.add(leftInfoTable);
		gridPanel.add(rightInfoTable);
		
		container.add(gridPanel);
		
		if (!this.registrySource.equals(RegistrySource.ENVIROMENT) && !this.registrySource.equals(RegistrySource.COMPANY)) {
			scopeLB.clearItems();
			scopeLB.getElement().getStyle().setProperty("max-width", "10rem");
			options.getConfiguration().getAvailableScopes().forEach(s -> scopeLB.addItem(s.getDescription(), s.getId().toString()));
			
//			accountLB.clearItems();
//			accountLB.addItem("-", "");
//			accounts.forEach(a -> accountLB.addItem(a.getFullName(), a.getId().toString()));
			
			accountSB.getSuggestBox().getValueBox().addKeyUpHandler(event -> {

			    String text = accountSB.getValue();

			    if (text.length() <= 3) {
			        return;
			    }

			    // Cancelar timer previo
			    if (accountsTimer != null) {
			    	accountsTimer.cancel();
			    }

			    // Crear nuevo timer
			    accountsTimer = new Timer() {
			        @Override
			        public void run() {
			            launchAccountSearch(text);
			        }
			    };

			    // Esperar 300 ms antes de ejecutar
			    accountsTimer.schedule(300);
			});

			
			leftInfoTable.add(createRow(scopeLB, accountSB));
			
			transactionLB.clearItems();
			for(int i=0; i < InvoiceTransactionType.values().length; i++)
				transactionLB.addItem(InvoiceTransactionType.values()[i].getDescription(), InvoiceTransactionType.values()[i].name());
			
			irpf.getElement().getStyle().setProperty("max-width", "5rem");
			re.getElement().getStyle().setProperty("max-width", "5rem");
			criterio.getElement().getStyle().setProperty("max-width", "5rem");
			
			if(this.registrySource.equals(RegistrySource.CUSTOMER))
				leftInfoTable.add(createRow(transactionLB, irpf, re));
				
			else if(this.registrySource.equals(RegistrySource.SUPPLIER) || this.registrySource.equals(RegistrySource.CREDITOR))
				leftInfoTable.add(createRow(transactionLB, irpf, criterio));
			
			rightInfoTable.add(createRow(observation));
			
			if(null != customerFull && null != customerFull.getId()) {
				scopeLB.setValue(customerFull.getRegistry().getScope().getId().toString());
				accountSB.setValue(null == customerFull.getAccount() ? "" : customerFull.getAccount().getFullName());
			
				transactionLB.setValue(customerFull.getRegistry().getTransaction().name());
				irpf.setValue(customerFull.getRegistry().isWithholding());
				
				if(this.registrySource.equals(RegistrySource.CUSTOMER))
					re.setValue(customerFull.getRegistry().isSurcharge());
				
				observation.setValue(customerFull.getRegistry().getObservation());
			} else if(null != creditorFull && null != creditorFull.getId()) {
				scopeLB.setValue(creditorFull.getRegistry().getScope().getId().toString());
				accountSB.setValue(null == creditorFull.getAccount() ? "" : creditorFull.getAccount().getFullName());
			
				transactionLB.setValue(creditorFull.getRegistry().getTransaction().name());
				irpf.setValue(creditorFull.getRegistry().isWithholding());
				
				if(this.registrySource.equals(RegistrySource.CREDITOR))
					criterio.setValue(creditorFull.getRegistry().isVatAccrualPayment());
				
				observation.setValue(creditorFull.getRegistry().getObservation());
			} else if(null != supplierFull && null != supplierFull.getId()) {
				scopeLB.setValue(supplierFull.getRegistry().getScope().getId().toString());
				accountSB.setValue(null == supplierFull.getAccount() ? "" : supplierFull.getAccount().getFullName());
			
				transactionLB.setValue(supplierFull.getRegistry().getTransaction().name());
				irpf.setValue(supplierFull.getRegistry().isWithholding());
				
				if(this.registrySource.equals(RegistrySource.SUPPLIER))
					re.setValue(supplierFull.getRegistry().isVatAccrualPayment());
				
				observation.setValue(supplierFull.getRegistry().getObservation());
			}
		}
		
		if (!this.registrySource.equals(RegistrySource.ENVIROMENT)) {
			registryPaymethodBanksTable = new RegistryPaymethodBanksTable(options.getDomainName(), options.getDomain(), options.getUser(), registry.getId(), this.registrySource) {
	
				@Override
				protected void onShowErrorMessage(String errorMessage) {
					AonMessagePanel.showError(messagePanel, errorMessage);
				}

				@Override
				protected void onShowSuccess(String successMessage) {
					AonMessagePanel.showSuccess(messagePanel, successMessage);
				}
	
			};
			
			tablayoutPanel.add(registryPaymethodBanksTable, "D. Bancarios");
		}
		
		mediaTable = new MediaTable(options.getDomainName(), options.getDomain(), options.getUser(), registry.getId()) {

			@Override
			protected void onShowErrorMessage(String errorMessage) {
				AonMessagePanel.showError(messagePanel, errorMessage);
			}

		};

		tablayoutPanel.add(mediaTable, "Contactos");

		addressTable = new AddressTable(options.getDomainName(), options.getDomain(), options.getUser(),
				registry.getId()) {

			@Override
			protected void onShowErrorMessage(String errorMessage) {
				AonMessagePanel.showError(messagePanel, errorMessage);
			}

			@Override
			protected LinkedList<GeoZone> getAviableGeozones() { 
				return options.getConfiguration().getGeozones();
			}

		};

		tablayoutPanel.add(addressTable, "Direcciones");
		
		if (this.registrySource.equals(RegistrySource.COMPANY)) {
			workplaceTable = new WorkplaceTable(options.getDomainName(), options.getDomain(), options.getUser(), registry.getId()) {

				@Override
				protected void onShowErrorMessage(String errorMessage) {
					AonMessagePanel.showError(messagePanel, errorMessage);
				}

			};
			
			tablayoutPanel.add(workplaceTable, "C. Trabajo");
			
			enterpriseActivityTable = new EnterpriseActivityTable(options.getDomainName(), options.getDomain(), options.getUser(), registry.getId()) {

				@Override
				protected void onShowErrorMessage(String errorMessage) {
					AonMessagePanel.showError(messagePanel, errorMessage);
				}

			};
			
			tablayoutPanel.add(enterpriseActivityTable, "Actividades");
			
			rDirStaffTable = new RDirStaffTable(options.getDomainName(), options.getDomain(), options.getUser(), registry.getId()) {

				@Override
				protected void onShowErrorMessage(String errorMessage) {
					AonMessagePanel.showError(messagePanel, errorMessage);
				}

			};
			
			tablayoutPanel.add(rDirStaffTable, "Representantes");
			
			recordDataTable = new RecordDataTable(options.getDomainName(), options.getDomain(), options.getUser(), registry.getId()) {

				@Override
				protected void onShowErrorMessage(String errorMessage) {
					AonMessagePanel.showError(messagePanel, errorMessage);
				}

				@Override
				protected void onShowLoadingMessage(String loadingMessage) {
					AonMessagePanel.showLoading(messagePanel, loadingMessage);
				}

				@Override
				protected void onHideMessage() {
					AonMessagePanel.hideMessage(messagePanel);
				}

			};
			
			tablayoutPanel.add(recordDataTable, "D. Registrales");
		}
		
		if (this.registrySource.equals(RegistrySource.COMPANY) || this.registrySource.equals(RegistrySource.ENVIROMENT)) {
			tablayoutPanel.add(new AonMainCertificatesPanel(options.getDomainName(), options.getDomain(), options.getUser()), "Certificados");
			
			aonVisualIdentity = new AonVisualIdentity(options.getDomainName(), options.getDomain(), options.getUser(), registry.getId(), this.registrySource);
			tablayoutPanel.add(aonVisualIdentity, this.registrySource.equals(RegistrySource.ENVIROMENT) ? "Logo" : "Logo / Firma");
		}
		
		container.add(tablayoutPanel);

		Scheduler.get().scheduleDeferred(new Command() {
			public void execute() {
				name.setFocus(true);
			}
		});
	}
	
	private void launchAccountSearch(String filter) {

	    if (accountsLoading) {
	        return; // evita llamadas simultáneas
	    }

	    accountsLoading = true;

	    getAccountsForRegistry(accountsDB -> {

	    	accountsLoading = false;

	        List<String> suggestValues = new ArrayList<>();

	        for (Account acc : accountsDB) {
	            suggestValues.add(acc.getCode() + " - " + acc.getDescription());
	        }

	        accountSB.getOracle().setData(suggestValues);

	        accountSB.showSuggestionList();
	    });
	}


	private void getStatus() {
		switch (registrySource) {
			case CUSTOMER: 
				registryStatus = new RegistryStatusSelect(customerFull.getRegistry().getStatus());
				break;
			case CREDITOR: 
				registryStatus = new RegistryStatusSelect(creditorFull.getRegistry().getStatus());
				break;
			case SUPPLIER: 
				registryStatus = new RegistryStatusSelect(supplierFull.getRegistry().getStatus());
				break;
			default:
				throw new IllegalArgumentException("Unexpected value: " + registrySource);
		}

		if(null != registryStatus) {
			registryStatus.getElement().getStyle().setProperty("border-radius", ".325rem");
			registryStatus.getElement().getStyle().setProperty("border", ".0625rem solid #cbd5e1");
			registryStatus.getElement().getStyle().setProperty("margin-top", "14px");
			registryStatus.getElement().getStyle().setProperty("padding", "4px");
		}
	}

	private void getAddresses() {
		switch (registrySource) {
			case CUSTOMER: 
				if(customerFull.getAddresses().size() > 1) {
					addressLB = new AonCustomListBox("Direcciones (" + customerFull.getAddresses().size() + ")");
					customerFull.getAddresses().forEach(a -> addressLB.addItem(a.getFullAddress2()));
					disableInput(addressLB);
					
					addressLB.getElement().getStyle().clearDisplay();
					mainAddress.getElement().getStyle().setDisplay(Display.NONE);
				} else {
					RegistryAddress mainAddresslValue = customerFull.getMainAddress();
					disableInput(mainAddress);
					if(null != mainAddresslValue)
						mainAddress.setValue(mainAddresslValue.getFullAddress2());
					
					mainAddress.getElement().getStyle().clearDisplay();
					addressLB.getElement().getStyle().setDisplay(Display.NONE);
				}
				break;
			case CREDITOR: 
				if(creditorFull.getAddresses().size() > 1) {
					addressLB = new AonCustomListBox("Direcciones (" + creditorFull.getAddresses().size() + ")");
					creditorFull.getAddresses().forEach(a -> addressLB.addItem(a.getFullAddress2()));
					disableInput(addressLB);
					
					addressLB.getElement().getStyle().clearDisplay();
					mainAddress.getElement().getStyle().setDisplay(Display.NONE);
				} else {
					RegistryAddress mainAddresslValue = creditorFull.getMainAddress();
					disableInput(mainAddress);
					if(null != mainAddresslValue)
						mainAddress.setValue(mainAddresslValue.getFullAddress2());
					
					mainAddress.getElement().getStyle().clearDisplay();
					addressLB.getElement().getStyle().setDisplay(Display.NONE);
				}
				break;
			case SUPPLIER: 
				if(supplierFull.getAddresses().size() > 1) {
					addressLB = new AonCustomListBox("Direcciones (" + supplierFull.getAddresses().size() + ")");
					supplierFull.getAddresses().forEach(a -> addressLB.addItem(a.getFullAddress2()));
					disableInput(addressLB);
					
					addressLB.getElement().getStyle().clearDisplay();
					mainAddress.getElement().getStyle().setDisplay(Display.NONE);
				} else {
					RegistryAddress mainAddresslValue = supplierFull.getMainAddress();
					disableInput(mainAddress);
					if(null != mainAddresslValue)
						mainAddress.setValue(mainAddresslValue.getFullAddress2());
					
					mainAddress.getElement().getStyle().clearDisplay();
					addressLB.getElement().getStyle().setDisplay(Display.NONE);
				}
				break;
			default:
				throw new IllegalArgumentException("Unexpected value: " + registrySource);
		}
	}

	private void getPhones() {
		List<RegistryMedia> medias;
		switch (registrySource) {
			case CUSTOMER: 
				medias = customerFull.getMedias().stream().filter(m -> m.getMedia().equals(MediaType.CELLULAR) || m.getMedia().equals(MediaType.FIXED_PHONE)).collect(Collectors.toList());
				if(medias.size() > 1) {
					phoneLB = new AonCustomListBox("Tel\u00e9fonos (" + medias.size() + ")");
					medias.forEach(a -> phoneLB.addItem(a.getValue() + ( AonStringUtils.isBlank( a.getComment()) ? "" : "( " + a.getComment() + " )") ));
					disableInput(phoneLB);
					
					phoneLB.getElement().getStyle().clearDisplay();
					mainPhone.getElement().getStyle().setDisplay(Display.NONE);
				} else {
					RegistryMedia mainPhonValue = customerFull.getMedias().stream().filter(m -> m.getMedia().equals(MediaType.FIXED_PHONE) || m.getMedia().equals(MediaType.CELLULAR)).findFirst().orElse(null);
					disableInput(mainPhone);
					if(null != mainPhonValue)
						mainPhone.setValue(mainPhonValue.getValue() + ( AonStringUtils.isBlank( mainPhonValue.getComment()) ? "" : "( " + mainPhonValue.getComment() + " )") );
					
					mainPhone.getElement().getStyle().clearDisplay();
					phoneLB.getElement().getStyle().setDisplay(Display.NONE);
				}
				break;
			case CREDITOR: 
				medias = creditorFull.getMedias().stream().filter(m -> m.getMedia().equals(MediaType.CELLULAR) || m.getMedia().equals(MediaType.FIXED_PHONE)).collect(Collectors.toList());
				if(medias.size() > 1) {
					phoneLB = new AonCustomListBox("Tel\u00e9fonos (" + medias.size() + ")");
					medias.forEach(a -> phoneLB.addItem(a.getValue() + ( AonStringUtils.isBlank( a.getComment()) ? "" : "( " + a.getComment() + " )") ));
					disableInput(phoneLB);
					
					phoneLB.getElement().getStyle().clearDisplay();
					mainPhone.getElement().getStyle().setDisplay(Display.NONE);
				} else {
					RegistryMedia mainPhonValue = creditorFull.getMedias().stream().filter(m -> m.getMedia().equals(MediaType.FIXED_PHONE) || m.getMedia().equals(MediaType.CELLULAR)).findFirst().orElse(null);
					disableInput(mainPhone);
					if(null != mainPhonValue)
						mainPhone.setValue(mainPhonValue.getValue() + ( AonStringUtils.isBlank( mainPhonValue.getComment()) ? "" : "( " + mainPhonValue.getComment() + " )"));
					
					mainPhone.getElement().getStyle().clearDisplay();
					phoneLB.getElement().getStyle().setDisplay(Display.NONE);
				}
				break;
			case SUPPLIER: 
				medias = supplierFull.getMedias().stream().filter(m -> m.getMedia().equals(MediaType.CELLULAR) || m.getMedia().equals(MediaType.FIXED_PHONE)).collect(Collectors.toList());
				if(medias.size() > 1) {
					phoneLB = new AonCustomListBox("Tel\u00e9fonos (" + medias.size() + ")");
					medias.forEach(a -> phoneLB.addItem(a.getValue() + ( AonStringUtils.isBlank( a.getComment()) ? "" : "( " + a.getComment() + " )") ));
					disableInput(phoneLB);
					
					phoneLB.getElement().getStyle().clearDisplay();
					mainPhone.getElement().getStyle().setDisplay(Display.NONE);
				} else {
					RegistryMedia mainPhonValue = supplierFull.getMedias().stream().filter(m -> m.getMedia().equals(MediaType.FIXED_PHONE) || m.getMedia().equals(MediaType.CELLULAR)).findFirst().orElse(null);
					disableInput(mainPhone);
					if(null != mainPhonValue)
						mainPhone.setValue(mainPhonValue.getValue() + ( AonStringUtils.isBlank( mainPhonValue.getComment()) ? "" : "( " + mainPhonValue.getComment() + " )"));
					
					mainPhone.getElement().getStyle().clearDisplay();
					phoneLB.getElement().getStyle().setDisplay(Display.NONE);
				}
				break;
			default:
				throw new IllegalArgumentException("Unexpected value: " + registrySource);
		}
	}
	
	private void getEmails() {
		List<RegistryMedia> medias;
		switch (registrySource) {
			case CUSTOMER: 
				medias = customerFull.getMedias().stream().filter(m -> m.getMedia().equals(MediaType.EMAIL)).collect(Collectors.toList());
				if(medias.size() > 1) {
					emailLB = new AonCustomListBox("Emails (" + medias.size() + ")");
					medias.forEach(a -> emailLB.addItem(a.getValue() + ( AonStringUtils.isBlank( a.getComment()) ? "" : "( " + a.getComment() + " )") ));
					disableInput(emailLB);
					
					emailLB.getElement().getStyle().clearDisplay();
					mainEmail.getElement().getStyle().setDisplay(Display.NONE);
				} else {
					RegistryMedia mainEmailValue = customerFull.getMedias().stream().filter(m -> m.getMedia().equals(MediaType.EMAIL)).findFirst().orElse(null);
					disableInput(mainEmail);
					if(null != mainEmailValue)
						mainEmail.setValue(mainEmailValue.getValue());
					
					mainEmail.getElement().getStyle().clearDisplay();
					emailLB.getElement().getStyle().setDisplay(Display.NONE);
				}
				break;
			case CREDITOR: 
				medias = creditorFull.getMedias().stream().filter(m -> m.getMedia().equals(MediaType.EMAIL)).collect(Collectors.toList());
				if(medias.size() > 1) {
					emailLB = new AonCustomListBox("Emails (" + medias.size() + ")");
					medias.forEach(a -> emailLB.addItem(a.getValue() + ( AonStringUtils.isBlank( a.getComment()) ? "" : "( " + a.getComment() + " )") ));
					disableInput(emailLB);
					
					emailLB.getElement().getStyle().clearDisplay();
					mainEmail.getElement().getStyle().setDisplay(Display.NONE);
				} else {
					RegistryMedia mainEmailValue = creditorFull.getMedias().stream().filter(m -> m.getMedia().equals(MediaType.EMAIL)).findFirst().orElse(null);
					disableInput(mainEmail);
					if(null != mainEmailValue)
						mainEmail.setValue(mainEmailValue.getValue());
					
					mainEmail.getElement().getStyle().clearDisplay();
					emailLB.getElement().getStyle().setDisplay(Display.NONE);
				}
				break;
			case SUPPLIER: 
				medias = supplierFull.getMedias().stream().filter(m -> m.getMedia().equals(MediaType.EMAIL)).collect(Collectors.toList());
				if(medias.size() > 1) {
					emailLB = new AonCustomListBox("Emails (" + medias.size() + ")");
					medias.forEach(a -> emailLB.addItem(a.getValue() + ( AonStringUtils.isBlank( a.getComment()) ? "" : "( " + a.getComment() + " )") ));
					disableInput(emailLB);
					
					emailLB.getElement().getStyle().clearDisplay();
					mainEmail.getElement().getStyle().setDisplay(Display.NONE);
				} else {
					RegistryMedia mainEmailValue = supplierFull.getMedias().stream().filter(m -> m.getMedia().equals(MediaType.EMAIL)).findFirst().orElse(null);
					disableInput(mainEmail);
					if(null != mainEmailValue)
						mainEmail.setValue(mainEmailValue.getValue());
					
					mainEmail.getElement().getStyle().clearDisplay();
					emailLB.getElement().getStyle().setDisplay(Display.NONE);
				}
				break;
			default:
				throw new IllegalArgumentException("Unexpected value: " + registrySource);
		}
	}

	private void createNameByDocumentType() {
		if(registry.getDocumentType().equals(DocumentType.CIF)) {
			name.addValueChangeHandler(e -> registry.setName(e.getValue()));
			name.setValue(registry.getName());
			name.setEnable(editEnable);
			name.setVisibleTitle(AON.MSG.enterpriseName());
			
			firstSurname.getElement().getStyle().setDisplay(Display.NONE);
			secondSurname.getElement().getStyle().setDisplay(Display.NONE);
		} else {
			name.setEnable(editEnable);
			name.setValue(AonStringUtils.isBlank(registry.getPersonName()) ? registry.getName() : registry.getPersonName());
			name.setVisibleTitle("Nombre");
			
			firstSurname.setEnable(editEnable);
			firstSurname.setValue(registry.getPersonFirstsurname());

			secondSurname.setEnable(editEnable);
			secondSurname.setValue(registry.getPersonSecondsurname());
			
			name.addValueChangeHandler(e -> {
				registry.setPersonName(e.getValue());
				ensureRegistryNameByPerson(name.getValue(), firstSurname.getValue(), secondSurname.getValue());
			});
			firstSurname.addValueChangeHandler(e -> {
				registry.setPersonFirstsurname(e.getValue());
				ensureRegistryNameByPerson(name.getValue(), firstSurname.getValue(), secondSurname.getValue());
			});
			secondSurname.addValueChangeHandler(e -> {
				registry.setPersonSecondsurname(e.getValue());
				ensureRegistryNameByPerson(name.getValue(), firstSurname.getValue(), secondSurname.getValue());
			});
			
			firstSurname.getElement().getStyle().clearDisplay();
			secondSurname.getElement().getStyle().clearDisplay();
		}
	}
	
	private void disableInput(AonCustomTextBox textBox) {
		textBox.setEnable(false);
		textBox.getTextBox().getElement().getStyle().setProperty("background-color", "#f9f7f7");
	}
	
	private void disableInput(AonCustomListBox listBox) {
		listBox.getListBox().getElement().getStyle().setProperty("background-color", "#f9f7f7");
	}

	public boolean checkDocumentValidation(String document) {
		RegExp dniPattern = RegExp.compile("\\d{8}\\-?[A-HJ-NP-TV-Z]");
		RegExp niePattern = RegExp.compile("[A-Z]{1}\\d{7}[A-Z]{1}");
		
		if (dniPattern.test(document.toUpperCase()) && document.length() == 9) {
			return Dni.checkDNI(document);
		} else if (niePattern.test(document.toUpperCase()) && document.length() == 9) {
			return Dni.checkNIE(document);
		}	else
			return AonStringUtils.isBlank(document);
	}
	
	private void ensureRegistryNameByPerson(String name, String firstSurname, String secondSurname) {
		StringBuilder sb = new StringBuilder();

	    if (!AonStringUtils.isBlank(firstSurname)) {
	        sb.append(firstSurname.trim());
	    }

	    if (!AonStringUtils.isBlank(secondSurname)) {
	        if (sb.length() > 0) sb.append(" ");
	        sb.append(secondSurname.trim());
	    }

	    if (!AonStringUtils.isBlank(name)) {
	        if (sb.length() > 0) sb.append(", ");
	        sb.append(name.trim());
	    }

	    registry.setName(sb.toString());
	}

	private HTMLPanel createTable() {
		HTMLPanel table = new HTMLPanel(AonStringUtils.EMPTY);
		table.addStyleName(AON.CSS.aonFlexColumn2());
		return table;
	}

	private HTMLPanel createRow(Widget... ws) {
		HTMLPanel row = new HTMLPanel(AonStringUtils.EMPTY);
		row.addStyleName(AON.CSS.aonItemFlex());

		for (int i = 0; i < ws.length; i++)
			row.add(ws[i]);

		return row;
	}

	private void getCompanyFull(Consumer<CompanyFull> success) {
		AonMessagePanel.showLoading(messagePanel, "Obteniendo empresa");
		commonService.getCompanyFull(options.getDomainName(), options.getDomain(), options.getUser(),
				new AsyncCallback<CompanyFull>() {

					@Override
					public void onSuccess(CompanyFull companyFull) {
						AonMessagePanel.hideMessage(messagePanel);
						company = companyFull;
						registry = companyFull.getRegistry().get();
						success.accept(companyFull);
					}

					@Override
					public void onFailure(Throwable caught) {
						AonMessagePanel.showError(messagePanel, "Empresa error : " + caught.getMessage());
					}

				});
	}

	private void saveCompanyFull(Consumer<CompanyFull> success) {
		AonMessagePanel.showLoading(messagePanel, "Guardando empresa");

		company.getRegistry().copy(registry);

		commonService.saveCompanyFull(options.getDomainName(), options.getDomain(), options.getUser(), company,
				new AsyncCallback<CompanyFull>() {

					@Override
					public void onSuccess(CompanyFull companyFull) {
						AonMessagePanel.showSuccess(messagePanel, "Datos guardados correctamente");
						success.accept(companyFull);
					}

					@Override
					public void onFailure(Throwable caught) {
						AonMessagePanel.showError(messagePanel, "Empresa error guardando : " + caught.getMessage());
					}

				});
	}
	
	private void getCustomerFull(Consumer<CustomerFull> success) {
		AonMessagePanel.showLoading(messagePanel, "Obteniendo cliente");
		registryService.getCustomerFull(options.getDomainName(), options.getDomain(), options.getUser(), registryId,
				new AsyncCallback<CustomerFull>() {

					@Override
					public void onSuccess(CustomerFull customerFullDB) {
						AonMessagePanel.hideMessage(messagePanel);
						customerFull = customerFullDB;
						registry = customerFull.getRegistry().get();
						success.accept(customerFull);
					}

					@Override
					public void onFailure(Throwable caught) {
						AonMessagePanel.showError(messagePanel, "Empresa error : " + caught.getMessage());
					}

				});
	}

	private void saveCustomerFull(Consumer<CustomerFull> success) {
		AonMessagePanel.showLoading(messagePanel, "Guardando cliente");
		
		customerFull.getRegistry().copy(registry);
		customerFull.getRegistry().setStatus(registryStatus.getValue());
		
		if(!this.registrySource.equals(RegistrySource.ENVIROMENT) && !this.registrySource.equals(RegistrySource.COMPANY)) {
			customerFull.getRegistry().setScope(new Scope().setId(Integer.parseInt(scopeLB.getValue())));
			
			Account selectedAccount = accounts.stream().filter(a -> AonStringUtils.equalsIgnoreCase(a.getFullName(), accountSB.getValue())).findFirst().orElse(null);
			customerFull.setAccount(selectedAccount);
			
			customerFull.getRegistry().setTransaction(InvoiceTransactionType.valueOf(transactionLB.getValue()));
			customerFull.getRegistry().setWithholding(irpf.getValue());
			if(this.registrySource.equals(RegistrySource.CUSTOMER))
				customerFull.getRegistry().setSurcharge(re.getValue());
		}
		
		registryService.save(options.getDomainName(), options.getDomain(), options.getUser(), customerFull, new AsyncCallback<CustomerFull>() {
			
			@Override
			public void onSuccess(CustomerFull result) {
				AonMessagePanel.showSuccess(messagePanel, "Datos guardados correctamente");
				success.accept(result);
			}
			
			@Override
			public void onFailure(Throwable error) {
				AonMessagePanel.showError(messagePanel, "Error proveedor : " + error.getMessage());
			}
			
		});

	}
	
	private void deleteCustomerFull(Consumer<Void> success) {
		AonMessagePanel.showLoading(messagePanel, "Eliminando cliente");
		
		registryService.deleteCustomerFull(options.getDomainName(), options.getDomain(), options.getUser(), customerFull.getId(), new AsyncCallback<Void>() {
			
			@Override
			public void onSuccess(Void result) {
				AonMessagePanel.showSuccess(messagePanel, "Eliminado correctamente");
				success.accept(result);
			}
			
			@Override
			public void onFailure(Throwable error) {
				AonMessagePanel.showError(messagePanel, "Error eliminando : " + error.getMessage());
			}
			
		});

	}
	
	private void getCreditorFull(Consumer<CreditorFull> success) {
		AonMessagePanel.showLoading(messagePanel, "Obteniendo acreedor");
		registryService.getCreditorFull(options.getDomainName(), options.getDomain(), options.getUser(), registryId, new AsyncCallback<CreditorFull>() {

					@Override
					public void onSuccess(CreditorFull creditorFullDB) {
						AonMessagePanel.hideMessage(messagePanel);
						creditorFull = creditorFullDB;
						registry = creditorFull.getRegistry().get();
						success.accept(creditorFull);
					}

					@Override
					public void onFailure(Throwable caught) {
						AonMessagePanel.showError(messagePanel, "Empresa error : " + caught.getMessage());
					}

		});
	}

	private void saveCreditorFull(Consumer<CreditorFull> success) {
		AonMessagePanel.showLoading(messagePanel, "Guardando acreedor");
		
		creditorFull.getRegistry().copy(registry);
		creditorFull.getRegistry().setStatus(registryStatus.getValue());
		
		if(!this.registrySource.equals(RegistrySource.ENVIROMENT) && !this.registrySource.equals(RegistrySource.COMPANY)) {
			creditorFull.getRegistry().setScope(new Scope().setId(Integer.parseInt(scopeLB.getValue())));
			
			Account selectedAccount = accounts.stream().filter(a -> AonStringUtils.equalsIgnoreCase(a.getFullName(), accountSB.getValue())).findFirst().orElse(null);
			creditorFull.setAccount(selectedAccount);
			
			creditorFull.getRegistry().setTransaction(InvoiceTransactionType.valueOf(transactionLB.getValue()));
			creditorFull.getRegistry().setWithholding(irpf.getValue());
			if(this.registrySource.equals(RegistrySource.CREDITOR))
				creditorFull.getRegistry().setVatAccrualPayment(criterio.getValue());
		}
		
		registryService.save(options.getDomainName(), options.getDomain(), options.getUser(), creditorFull, new AsyncCallback<CreditorFull>() {
			
			@Override
			public void onSuccess(CreditorFull result) {
				AonMessagePanel.showSuccess(messagePanel, "Datos guardados correctamente");
				success.accept(result);
			}
			
			@Override
			public void onFailure(Throwable error) {
				AonMessagePanel.showError(messagePanel, "Error proveedor : " + error.getMessage());
			}
			
		});

	}
	
	private void deleteCreditorFull(Consumer<Void> success) {
		AonMessagePanel.showLoading(messagePanel, "Eliminando acreedor");
		
		registryService.deleteCreditorFull(options.getDomainName(), options.getDomain(), options.getUser(), creditorFull.getId(), new AsyncCallback<Void>() {
			
			@Override
			public void onSuccess(Void result) {
				AonMessagePanel.showSuccess(messagePanel, "Eliminado correctamente");
				success.accept(result);
			}
			
			@Override
			public void onFailure(Throwable error) {
				AonMessagePanel.showError(messagePanel, "Error eliminando : " + error.getMessage());
			}
			
		});

	}
	
	private void getSupplierFull(Consumer<SupplierFull> success) {
		AonMessagePanel.showLoading(messagePanel, "Obteniendo proveedor");
		registryService.getSupplierFull(options.getDomainName(), options.getDomain(), options.getUser(), registryId, new AsyncCallback<SupplierFull>() {

					@Override
					public void onSuccess(SupplierFull supplierFullDB) {
						AonMessagePanel.hideMessage(messagePanel);
						supplierFull = supplierFullDB;
						registry = supplierFull.getRegistry().get();
						success.accept(supplierFull);
					}

					@Override
					public void onFailure(Throwable caught) {
						AonMessagePanel.showError(messagePanel, "Empresa error : " + caught.getMessage());
					}

		});
	}

	private void saveSupplierFull(Consumer<SupplierFull> success) {
		AonMessagePanel.showLoading(messagePanel, "Guardando acreedor");
		
		supplierFull.getRegistry().copy(registry);
		supplierFull.getRegistry().setStatus(registryStatus.getValue());
		
		if(!this.registrySource.equals(RegistrySource.ENVIROMENT) && !this.registrySource.equals(RegistrySource.COMPANY)) {
			supplierFull.getRegistry().setScope(new Scope().setId(Integer.parseInt(scopeLB.getValue())));
			
			Account selectedAccount = accounts.stream().filter(a -> AonStringUtils.equalsIgnoreCase(a.getFullName(), accountSB.getValue())).findFirst().orElse(null);
			supplierFull.setAccount(selectedAccount);
			
			supplierFull.getRegistry().setTransaction(InvoiceTransactionType.valueOf(transactionLB.getValue()));
			supplierFull.getRegistry().setWithholding(irpf.getValue());
			if(this.registrySource.equals(RegistrySource.SUPPLIER))
				supplierFull.getRegistry().setVatAccrualPayment(criterio.getValue());
		}
		
		registryService.save(options.getDomainName(), options.getDomain(), options.getUser(), supplierFull, new AsyncCallback<SupplierFull>() {
			
			@Override
			public void onSuccess(SupplierFull result) {
				AonMessagePanel.showSuccess(messagePanel, "Datos guardados correctamente");
				success.accept(result);
			}
			
			@Override
			public void onFailure(Throwable error) {
				AonMessagePanel.showError(messagePanel, "Error proveedor : " + error.getMessage());
			}
			
		});

	}
	
	private void deleteSupplierFull(Consumer<Void> success) {
		AonMessagePanel.showLoading(messagePanel, "Eliminando proveedor");
		
		registryService.deleteSupplierFull(options.getDomainName(), options.getDomain(), options.getUser(), supplierFull.getId(), new AsyncCallback<Void>() {
			
			@Override
			public void onSuccess(Void result) {
				AonMessagePanel.showSuccess(messagePanel, "Eliminado correctamente");
				success.accept(result);
			}
			
			@Override
			public void onFailure(Throwable error) {
				AonMessagePanel.showError(messagePanel, "Error eliminando : " + error.getMessage());
			}
			
		});

	}
	
	private void getAccountsForRegistry(Consumer<List<Account>> end) {
		commonService.getAccountsForRegistry(options.getDomainName(), options.getDomain(), options.getUser(), registrySource, accountSB.getValue(), new AsyncCallback<List<Account>>() {

			@Override
			public void onFailure(Throwable caught) {
				accountsLoading = false;
				AonMessagePanel.showError(messagePanel, "Error cuentas contables: " + caught.getMessage());
			}

			@Override
			public void onSuccess(List<Account> accountsDB) {
				accountsLoading = false;
				accounts.addAll(accountsDB);
				end.accept(accountsDB);
			}
		});
	}
	
	protected abstract void onBack();
	protected abstract void onPrev(Integer registryId);
	protected abstract void onNext(Integer registryId);

}
