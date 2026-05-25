package com.esferalia.aon.gwt.fiscal.client.registry;

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
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomTextBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMainCertificatesPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonVisualIdentity;
import com.esferalia.aon.gwt.common.client.widget.solutions.EnterpriseActivityTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.MediaTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.RDirStaffTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.RecordDataTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.RegistryGeneralDataPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.RegistryPaymethodBanksTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.WorkplaceTable;
import com.esferalia.aon.gwt.common.shared.Dni;
import com.esferalia.aon.occam.api.model.GeoZone;
import com.esferalia.aon.occam.api.model.registry.CompanyFull;
import com.esferalia.aon.occam.api.model.registry.CreditorFull;
import com.esferalia.aon.occam.api.model.registry.CustomerFull;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.registry.RegistryAddress;
import com.esferalia.aon.occam.api.model.registry.RegistryMedia;
import com.esferalia.aon.occam.api.model.registry.RegistrySource;
import com.esferalia.aon.occam.api.model.registry.SupplierFull;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.DocumentType;
import com.esferalia.aon.occam.api.model.type.MediaType;
import com.esferalia.aon.occam.api.model.type.RegistryStatus;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.ListBox;
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
	
	private AonCustomTextBox alias = new AonCustomTextBox(AON.MSG.alias());
	private AonCustomListBox documentNationality = new AonCustomListBox("Pa\u00eds");
	private AonCustomListBox documentType = new AonCustomListBox("Tipo");
	private AonCustomTextBox document = new AonCustomTextBox("Documento");
	private AonCustomTextBox name = new AonCustomTextBox(AON.MSG.enterpriseName());
	private AonCustomTextBox firstSurname = new AonCustomTextBox("Apellido");
	private AonCustomTextBox secondSurname = new AonCustomTextBox("Apellido 2");
	
	private AonCustomListBox statusLB = new AonCustomListBox("Estado");
	private AonCustomTextBox mainPhone = new AonCustomTextBox("Tel\u00e9fono");
	private AonCustomListBox phoneLB = new AonCustomListBox("Tel\u00e9fonos");
	private AonCustomTextBox mainEmail = new AonCustomTextBox("Email");
	private AonCustomListBox emailLB = new AonCustomListBox("Emails");
	private AonCustomTextBox mainAddress = new AonCustomTextBox("Direcci\u00f3n");
	private AonCustomListBox addressLB = new AonCustomListBox("Direcciones");
	
	private HTMLPanel rightInfoTable;

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
	private RegistryGeneralDataPanel registryGeneralDataPanel;
	//private AonRegistryNotes aonRegistryNotes;
	
	private CompanyFull company;
	private Registry registry;
	
	private Integer registryId;
	private CustomerFull customerFull;
	private CreditorFull creditorFull;
	private SupplierFull supplierFull;
	
	private boolean editEnable = false;
	
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
		
		//aonRegistryNotes = new AonRegistryNotes(options.getDomainName(), options.getDomain(), options.getUser(), registryId, NoteType.CUSTOMER_STATUS.name());
		
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
			getSupplierFull(supplierFull -> initCompanyRegistry() );
			break;
		}
		case CREDITOR: {
			getCreditorFull(creditorFull -> initCompanyRegistry() );
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

		HTMLPanel leftInfoTable = createTable();
		leftInfoTable.getElement().getStyle().setProperty("flex", "1");

		alias.addValueChangeHandler(e -> registry.setAlias(e.getValue()));
		alias.setValue(registry.getAlias());

		if (!this.registrySource.equals(RegistrySource.ENVIROMENT)) {
			documentNationality.clearItems();
			for (int i = 0; i < Country.values().length; i++)
				documentNationality.addItem(Country.values()[i].getIso2(), Country.values()[i].getIso2());
			documentNationality.addChangeHandler(
					e -> registry.setDocumentCountry(Country.safeValueOf(documentNationality.getValue())));
			documentNationality.setValue(registry.getDocumentCountry().getIso2());
			documentNationality.getElement().getStyle().setProperty("max-width", "4rem");
			documentNationality.setEnabled(editEnable);

			documentType.clearItems();
			for (int i = 0; i < DocumentType.values().length; i++)
				documentType.addItem(DocumentType.values()[i].getDescription(), DocumentType.values()[i].toString());
			documentType.setValue(registry.getDocumentType().toString());
			documentType.getElement().getStyle().setProperty("max-width", "5rem");
			documentType.setEnabled(editEnable);

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

			leftInfoTable.add(createRow(alias, documentType, documentNationality, document));
		} else {
			leftInfoTable.add(createRow(alias));
		}

		gridPanel.add(leftInfoTable);

		rightInfoTable = createTable();
		rightInfoTable.getElement().getStyle().setProperty("flex", "1");

		name.addValueChangeHandler(e -> registry.setName(e.getValue()));
		name.setValue(registry.getName());
		name.setEnable(editEnable);
		rightInfoTable.add(createRow(name));

		gridPanel.add(rightInfoTable);
		
		if (!this.registrySource.equals(RegistrySource.ENVIROMENT)) {
			createNameByDocumentType();
			
			documentType.addChangeHandler(e -> {
				registry.setDocumentType(DocumentType.safeValueOf(documentType.getValue()));
				createNameByDocumentType();
				registry.setLegalPerson(registry.getDocumentType().equals(DocumentType.CIF));
			});
		}

		container.add(gridPanel);
		
		if(!this.registrySource.equals(RegistrySource.ENVIROMENT) && !this.registrySource.equals(RegistrySource.COMPANY)) {
			HTMLPanel gridPanel2 = new HTMLPanel(AonStringUtils.EMPTY);
			gridPanel2.setStyleName(AON.CSS.aonItemFlex());
			gridPanel2.getElement().getStyle().setProperty("margin", "0 1rem .5rem");
			gridPanel2.getElement().getStyle().setProperty("align-items", "flex-start");
			gridPanel2.getElement().getStyle().setProperty("gap", "2rem");
			
			HTMLPanel leftInfoTable2 = createTable();
			leftInfoTable2.getElement().getStyle().setProperty("flex", "1");
			
			getAddresses();

			leftInfoTable2.add(createRow(mainAddress, addressLB));

			gridPanel2.add(leftInfoTable2);

			HTMLPanel rightInfoTable2 = createTable();
			rightInfoTable2.getElement().getStyle().setProperty("flex", "1");
			
			statusLB.clearItems();
			for(int i=0; i< RegistryStatus.values().length; i++) {
				statusLB.addItem(RegistryStatus.values()[i].getDescription(), RegistryStatus.values()[i].name());
			}
			statusLB.getElement().getStyle().setProperty("max-width", "7rem");
			
			getStatus();
			
			getPhones();
			
			getEmails();
			
			rightInfoTable2.add(createRow(mainPhone, phoneLB, mainEmail, emailLB, statusLB));

			gridPanel2.add(rightInfoTable2);
			
			gridPanel.getElement().getStyle().setProperty("margin", "0 1rem");
			
			container.add(gridPanel2);
		}
		
		if (!this.registrySource.equals(RegistrySource.ENVIROMENT) && !this.registrySource.equals(RegistrySource.COMPANY)) {
			createRegistryGeneralDataPanel();
			
			tablayoutPanel.add(registryGeneralDataPanel, "D. Generales");
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
			
			tablayoutPanel.add(rDirStaffTable, "Representates");
			
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

	private void createRegistryGeneralDataPanel() {
		switch (registrySource) {
			case CUSTOMER: 
				registryGeneralDataPanel = new RegistryGeneralDataPanel(options.getDomainName(), options.getDomain(), options.getUser(), options.getConfiguration().getAvailableScopes(), customerFull, this.registrySource) {
					
					@Override
					protected void onShowErrorMessage(String errorMessage) {
						AonMessagePanel.showError(messagePanel, errorMessage);
					}

					@Override
					protected void onShowSuccess(String successMessage) {
						AonMessagePanel.showSuccess(messagePanel, successMessage);
					}

				};
				break;
			case CREDITOR:  
				registryGeneralDataPanel = new RegistryGeneralDataPanel(options.getDomainName(), options.getDomain(), options.getUser(), options.getConfiguration().getAvailableScopes(), creditorFull, this.registrySource) {
					
					@Override
					protected void onShowErrorMessage(String errorMessage) {
						AonMessagePanel.showError(messagePanel, errorMessage);
					}

					@Override
					protected void onShowSuccess(String successMessage) {
						AonMessagePanel.showSuccess(messagePanel, successMessage);
					}

				};
				break;
			case SUPPLIER:
				registryGeneralDataPanel = new RegistryGeneralDataPanel(options.getDomainName(), options.getDomain(), options.getUser(), options.getConfiguration().getAvailableScopes(), supplierFull, this.registrySource) {
					
					@Override
					protected void onShowErrorMessage(String errorMessage) {
						AonMessagePanel.showError(messagePanel, errorMessage);
					}

					@Override
					protected void onShowSuccess(String successMessage) {
						AonMessagePanel.showSuccess(messagePanel, successMessage);
					}

				};
				break;
			default:
				throw new IllegalArgumentException("Unexpected value: " + registrySource);
		}
	}

	private void getStatus() {
		switch (registrySource) {
			case CUSTOMER: 
				statusLB.addChangeHandler(e -> customerFull.getRegistry().setStatus(RegistryStatus.safeValueOf(statusLB.getValue())));
				statusLB.setValue(customerFull.getRegistry().getStatus().name());
				setStatusColor(statusLB.getListBox(), customerFull.getRegistry().getStatus());
				break;
			case CREDITOR: 
				statusLB.addChangeHandler(e -> creditorFull.getRegistry().setStatus(RegistryStatus.safeValueOf(statusLB.getValue())));
				statusLB.setValue(creditorFull.getRegistry().getStatus().name());
				setStatusColor(statusLB.getListBox(), creditorFull.getRegistry().getStatus());
				break;
			case SUPPLIER: 
				statusLB.addChangeHandler(e -> supplierFull.getRegistry().setStatus(RegistryStatus.safeValueOf(statusLB.getValue())));
				statusLB.setValue(supplierFull.getRegistry().getStatus().name());
				setStatusColor(statusLB.getListBox(), supplierFull.getRegistry().getStatus());
				break;
			default:
				throw new IllegalArgumentException("Unexpected value: " + registrySource);
		}
	}

	private void getAddresses() {
		switch (registrySource) {
			case CUSTOMER: 
				if(customerFull.getAddresses().size() > 1) {
					addressLB = new AonCustomListBox("Direcciones (" + customerFull.getAddresses().size() + ")");
					customerFull.getAddresses().forEach(a -> addressLB.addItem(a.getFullAddress() + " " + a.getFullAddress2()));
					
					addressLB.getElement().getStyle().clearDisplay();
					mainAddress.getElement().getStyle().setDisplay(Display.NONE);
				} else {
					RegistryAddress mainAddresslValue = customerFull.getMainAddress();
					mainAddress.setEnable(false);
					if(null != mainAddresslValue)
						mainAddress.setValue(mainAddresslValue.getFullAddress());
					
					mainAddress.getElement().getStyle().clearDisplay();
					addressLB.getElement().getStyle().setDisplay(Display.NONE);
				}
				break;
			case CREDITOR: 
				if(creditorFull.getAddresses().size() > 1) {
					addressLB = new AonCustomListBox("Direcciones (" + creditorFull.getAddresses().size() + ")");
					creditorFull.getAddresses().forEach(a -> addressLB.addItem(a.getFullAddress() + " " + a.getFullAddress2()));
					
					addressLB.getElement().getStyle().clearDisplay();
					mainAddress.getElement().getStyle().setDisplay(Display.NONE);
				} else {
					RegistryAddress mainAddresslValue = creditorFull.getMainAddress();
					mainAddress.setEnable(false);
					if(null != mainAddresslValue)
						mainAddress.setValue(mainAddresslValue.getFullAddress());
					
					mainAddress.getElement().getStyle().clearDisplay();
					addressLB.getElement().getStyle().setDisplay(Display.NONE);
				}
				break;
			case SUPPLIER: 
				if(supplierFull.getAddresses().size() > 1) {
					addressLB = new AonCustomListBox("Direcciones (" + supplierFull.getAddresses().size() + ")");
					supplierFull.getAddresses().forEach(a -> addressLB.addItem(a.getFullAddress() + " " + a.getFullAddress2()));
					
					addressLB.getElement().getStyle().clearDisplay();
					mainAddress.getElement().getStyle().setDisplay(Display.NONE);
				} else {
					RegistryAddress mainAddresslValue = supplierFull.getMainAddress();
					mainAddress.setEnable(false);
					if(null != mainAddresslValue)
						mainAddress.setValue(mainAddresslValue.getFullAddress());
					
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
					
					phoneLB.getElement().getStyle().clearDisplay();
					mainPhone.getElement().getStyle().setDisplay(Display.NONE);
				} else {
					RegistryMedia mainPhonValue = customerFull.getMedias().stream().filter(m -> m.getMedia().equals(MediaType.FIXED_PHONE)).findFirst().orElse(null);
					mainPhone.setEnable(false);
					if(null != mainPhonValue)
						mainPhone.setValue(mainPhonValue.getValue());
					
					mainPhone.getElement().getStyle().clearDisplay();
					phoneLB.getElement().getStyle().setDisplay(Display.NONE);
				}
				break;
			case CREDITOR: 
				medias = creditorFull.getMedias().stream().filter(m -> m.getMedia().equals(MediaType.CELLULAR) || m.getMedia().equals(MediaType.FIXED_PHONE)).collect(Collectors.toList());
				if(medias.size() > 1) {
					phoneLB = new AonCustomListBox("Tel\u00e9fonos (" + medias.size() + ")");
					medias.forEach(a -> phoneLB.addItem(a.getValue() + ( AonStringUtils.isBlank( a.getComment()) ? "" : "( " + a.getComment() + " )") ));
					
					phoneLB.getElement().getStyle().clearDisplay();
					mainPhone.getElement().getStyle().setDisplay(Display.NONE);
				} else {
					RegistryMedia mainPhonValue = creditorFull.getMedias().stream().filter(m -> m.getMedia().equals(MediaType.FIXED_PHONE)).findFirst().orElse(null);
					mainPhone.setEnable(false);
					if(null != mainPhonValue)
						mainPhone.setValue(mainPhonValue.getValue());
					
					mainPhone.getElement().getStyle().clearDisplay();
					phoneLB.getElement().getStyle().setDisplay(Display.NONE);
				}
				break;
			case SUPPLIER: 
				medias = supplierFull.getMedias().stream().filter(m -> m.getMedia().equals(MediaType.CELLULAR) || m.getMedia().equals(MediaType.FIXED_PHONE)).collect(Collectors.toList());
				if(medias.size() > 1) {
					phoneLB = new AonCustomListBox("Tel\u00e9fonos (" + medias.size() + ")");
					medias.forEach(a -> phoneLB.addItem(a.getValue() + ( AonStringUtils.isBlank( a.getComment()) ? "" : "( " + a.getComment() + " )") ));
					
					phoneLB.getElement().getStyle().clearDisplay();
					mainPhone.getElement().getStyle().setDisplay(Display.NONE);
				} else {
					RegistryMedia mainPhonValue = supplierFull.getMedias().stream().filter(m -> m.getMedia().equals(MediaType.FIXED_PHONE)).findFirst().orElse(null);
					mainPhone.setEnable(false);
					if(null != mainPhonValue)
						mainPhone.setValue(mainPhonValue.getValue());
					
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
					
					emailLB.getElement().getStyle().clearDisplay();
					mainEmail.getElement().getStyle().setDisplay(Display.NONE);
				} else {
					RegistryMedia mainEmailValue = customerFull.getMedias().stream().filter(m -> m.getMedia().equals(MediaType.EMAIL)).findFirst().orElse(null);
					mainEmail.setEnable(false);
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
					
					emailLB.getElement().getStyle().clearDisplay();
					mainEmail.getElement().getStyle().setDisplay(Display.NONE);
				} else {
					RegistryMedia mainEmailValue = creditorFull.getMedias().stream().filter(m -> m.getMedia().equals(MediaType.EMAIL)).findFirst().orElse(null);
					mainEmail.setEnable(false);
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
					
					emailLB.getElement().getStyle().clearDisplay();
					mainEmail.getElement().getStyle().setDisplay(Display.NONE);
				} else {
					RegistryMedia mainEmailValue = supplierFull.getMedias().stream().filter(m -> m.getMedia().equals(MediaType.EMAIL)).findFirst().orElse(null);
					mainEmail.setEnable(false);
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
		rightInfoTable.clear();

		if(registry.getDocumentType().equals(DocumentType.CIF)) {
			name.addValueChangeHandler(e -> registry.setName(e.getValue()));
			name.setValue(registry.getName());
			name.setEnable(editEnable);
			name.setVisibleTitle(AON.MSG.enterpriseName());
			rightInfoTable.add(createRow(name));
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
			
			rightInfoTable.add(createRow(name, firstSurname, secondSurname));
		}
	}
	
	private void setStatusColor(ListBox lb, RegistryStatus status) {
		switch (status) {
			case INACTIVE: 
				lb.getElement().getStyle().setProperty("border-color", "red");
				lb.getElement().getStyle().setProperty("border-width", "2px");
				break;
			case BLOCKED: 
				lb.getElement().getStyle().setProperty("border-color", "orange");
				lb.getElement().getStyle().setProperty("border-width", "2px");
				break;
			default:
				break;
			
		}
		
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
		
		if(!this.registrySource.equals(RegistrySource.ENVIROMENT) && !this.registrySource.equals(RegistrySource.COMPANY) && null != registryGeneralDataPanel) {
			registryGeneralDataPanel.onSave(customerFull);
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
		
		if(!this.registrySource.equals(RegistrySource.ENVIROMENT) && !this.registrySource.equals(RegistrySource.COMPANY) && null != registryGeneralDataPanel) {
			registryGeneralDataPanel.onSave(creditorFull);
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
		
		if(!this.registrySource.equals(RegistrySource.ENVIROMENT) && !this.registrySource.equals(RegistrySource.COMPANY) && null != registryGeneralDataPanel) {
			registryGeneralDataPanel.onSave(supplierFull);
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

	protected abstract void onBack();

}
