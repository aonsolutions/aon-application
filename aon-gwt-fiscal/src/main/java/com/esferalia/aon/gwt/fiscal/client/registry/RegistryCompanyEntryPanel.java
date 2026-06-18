package com.esferalia.aon.gwt.fiscal.client.registry;

import java.util.Date;
import java.util.LinkedList;
import java.util.function.Consumer;

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
import com.esferalia.aon.gwt.common.client.widget.solutions.CompanyDomainStatusSelect;
import com.esferalia.aon.gwt.common.client.widget.solutions.EnterpriseActivityTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.MailAccountTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.MediaTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.RDirStaffTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.RecordDataTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.RegistryPaymethodBanksTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.SignatureTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.WorkplaceTable;
import com.esferalia.aon.gwt.common.shared.Dni;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.GeoZone;
import com.esferalia.aon.occam.api.model.registry.CompanyFull;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.registry.RegistrySource;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.DocumentType;
import com.esferalia.aon.occam.api.model.type.RegistryStatus;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class RegistryCompanyEntryPanel extends AonCustomDockLayout {
	
	// ------------------------------------------------- ScrollableTabLayoutPanel
	
	public class ScrollableTabLayoutPanel extends TabLayoutPanel {

	    public ScrollableTabLayoutPanel(double barHeight, Unit unit) {
	        super(barHeight, unit);

	        Scheduler.get().scheduleDeferred(() -> {
	            Element root = getElement(); // siempre existe

	            DOM.sinkEvents(root, Event.ONMOUSEWHEEL);

	            DOM.setEventListener(root, event -> {
	                if (event.getTypeInt() == Event.ONMOUSEWHEEL) {
	                    int delta = event.getMouseWheelVelocityY();

	                    root.setScrollLeft(root.getScrollLeft() + delta * 2);

	                    event.preventDefault();
	                }
	            });
	        });
	    }
	}

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

	private HTMLPanel container;

	private HTMLPanel messagePanel = new HTMLPanel(AonStringUtils.EMPTY);
	
	private CompanyDomainStatusSelect status;
	
	private AonCustomListBox documentType = new AonCustomListBox("Tipo");
	private AonCustomListBox documentNationality = new AonCustomListBox("Pa\u00eds");
	private AonCustomTextBox document = new AonCustomTextBox("Documento");
	private AonCustomTextBox alias = new AonCustomTextBox(AON.MSG.alias());
	
	private AonCustomTextBox name = new AonCustomTextBox(AON.MSG.enterpriseName());
	private AonCustomTextBox firstSurname = new AonCustomTextBox("Apellido");
	private AonCustomTextBox secondSurname = new AonCustomTextBox("Apellido 2");
	
	private HTMLPanel rightInfoTable;
	private HTMLPanel leftInfoTable;
	
//	private TabLayoutPanel tablayoutPanel;
	private ScrollableTabLayoutPanel tablayoutPanel;
	
	// Other Info
	private AonVisualIdentity aonVisualIdentity;
	private AddressTable addressTable;
	private MediaTable mediaTable;
	private WorkplaceTable workplaceTable;
	private EnterpriseActivityTable enterpriseActivityTable;
	private RDirStaffTable rDirStaffTable;
	private RecordDataTable recordDataTable;
	private RegistryPaymethodBanksTable registryPaymethodBanksTable;
	private SignatureTable signatureTable;
	private MailAccountTable mailAccountTable;
	
	private CompanyFull company;
	private Registry registry;
	
	// ------------------------------------------------- Constructor

	public RegistryCompanyEntryPanel(RegistryModuleOptions options, RegistrySource registrySource) {
		super(getToolbarTitle(registrySource));

		this.options = options;
		this.registrySource = registrySource;

		initializeCommonService();
		initializeRegistryService();

		hideSearchWidget();
		
		createToolbar();

		tablayoutPanel = new ScrollableTabLayoutPanel(25.00, Unit.PX);
		tablayoutPanel.setHeight("100%");

		container = new HTMLPanel(AonStringUtils.EMPTY);
		container.addStyleName(AON.CSS.aonFlexColumn2());
		container.getElement().getStyle().setProperty("padding", "1rem");
		add(container);

		getRegistryBySource();
	}

	private boolean isParentUser() {
		return null != options.getConfiguration().getDomain().getParentId() && null != options.getConfiguration().getUser().getDomain().getId() && options.getConfiguration().getDomain().getParentId().equals(options.getConfiguration().getUser().getDomain().getId());
	}
	
	private static String getToolbarTitle(RegistrySource registrySource) {
		switch (registrySource) {
			case ENVIROMENT:
				return "Gesti\u00f3n Entorno";
			case COMPANY:
				return "Gesti\u00f3n Empresa";
			default:
				return "Gesti\u00f3n";
		}
	}

	@Override
	protected void onClearFilter() {}

	private void createToolbar() {
		AonToolbarButton saveButton = new AonToolbarButton("Guardar", AON.CSS.aonIconSave());
		saveButton.addClickHandler(e -> {
			saveCompanyFull(saved -> {});
		});
		addToolbarButton(saveButton);
		
		if(this.registrySource == RegistrySource.COMPANY && isParentUser()) {
			
			RegistryStatus statusValue;
			if(null != options.getConfiguration().getDomain().getExpirationDate()) statusValue = RegistryStatus.BLOCKED;
			else 
				statusValue = options.getConfiguration().getDomain().isActive() ? RegistryStatus.ACTIVE : RegistryStatus.INACTIVE;
		
			status = new CompanyDomainStatusSelect(statusValue, options.getConfiguration().getDomain().getExpirationDate());
//			status.addChangeHandler(e -> {
//				RegistryStatus newStatus = status.getValue();
//			    Date exp = status.getExpirationDate();
//			    Window.alert("newStatus : " + newStatus + ", exp : " + exp);
//			});
			getToolbar().addTitleButton(status);
			
		}
	}
	
	// ------------------------------------------------- DataBase

	private void getRegistryBySource() {
		getCompanyFull(companyFull -> initCompanyRegistry() );
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

		alias.addValueChangeHandler(e -> registry.setAlias(e.getValue()));
		alias.setValue(registry.getAlias());

		if (!this.registrySource.equals(RegistrySource.ENVIROMENT)) {
			documentType.clearItems();
			for (int i = 0; i < DocumentType.values().length; i++)
				documentType.addItem(DocumentType.values()[i].getDescription(), DocumentType.values()[i].toString());
			documentType.setValue(registry.getDocumentType().toString());
			documentType.getElement().getStyle().setProperty("max-width", "5rem");
			documentType.setEnabled(isParentUser());

			documentNationality.clearItems();
			for (int i = 0; i < Country.values().length; i++)
				documentNationality.addItem(Country.values()[i].getIso2(), Country.values()[i].getIso2());
			documentNationality.addChangeHandler(
					e -> registry.setDocumentCountry(Country.safeValueOf(documentNationality.getValue())));
			documentNationality.setValue(registry.getDocumentCountry().getIso2());
			documentNationality.getElement().getStyle().setProperty("max-width", "4rem");
			documentNationality.setEnabled(isParentUser());

			document.addValueChangeHandler(e -> {
				registry.setDocument(e.getValue());
				if (!checkDocumentValidation(e.getValue()))
					AonMessagePanel.showError(messagePanel, "El documento no tiene un formato valido");
				else
					AonMessagePanel.hideMessage(messagePanel);
			});
			document.setValue(registry.getDocument());
			document.setEnable(isParentUser());
			document.getElement().getStyle().setProperty("max-width", "7rem");

			leftInfoTable.add(createRow(documentType, documentNationality, document, alias));
		} else {
			leftInfoTable.add(createRow(alias));
		}


		rightInfoTable = createTable();
		rightInfoTable.getElement().getStyle().setProperty("flex", "1");
		
		name.addValueChangeHandler(e -> registry.setName(e.getValue()));
		name.setValue(registry.getName());
		name.setEnable(isParentUser());
		
		rightInfoTable.add(createRow(name, firstSurname, secondSurname));
		
		if (!this.registrySource.equals(RegistrySource.ENVIROMENT)) {
			documentType.addChangeHandler(e -> {
				registry.setDocumentType(DocumentType.safeValueOf(documentType.getValue()));
				createNameByDocumentType();
				registry.setLegalPerson(registry.getDocumentType().equals(DocumentType.CIF));
			});
		}
		createNameByDocumentType();

		gridPanel.add(leftInfoTable);
		gridPanel.add(rightInfoTable);
		
		container.add(gridPanel);
		
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
		
		tablayoutPanel.add(new AonMainCertificatesPanel(options.getDomainName(), options.getDomain(), options.getUser()), "Certificados");
		
		aonVisualIdentity = new AonVisualIdentity(options.getDomainName(), options.getDomain(), options.getUser(), registry.getId(), this.registrySource);
		tablayoutPanel.add(aonVisualIdentity, this.registrySource.equals(RegistrySource.ENVIROMENT) ? "Logo" : "Logo / Firma");
		
		mailAccountTable = new MailAccountTable(options.getDomainName(), options.getDomain(), options.getUser()) {
			
			@Override
			protected void onShowErrorMessage(String errorMessage) {
				AonMessagePanel.showError(messagePanel, errorMessage);
			}
			
		};
		tablayoutPanel.add(mailAccountTable, "Email");
		
		signatureTable = new SignatureTable(options.getDomainName(), options.getDomain(), options.getUser()) {
			
			@Override
			protected void onShowErrorMessage(String errorMessage) {
				AonMessagePanel.showError(messagePanel, errorMessage);
			}
		};
		tablayoutPanel.add(signatureTable, "Firma Email");
		
		container.add(tablayoutPanel);

		Scheduler.get().scheduleDeferred(new Command() {
			public void execute() {
				name.setFocus(true);
			}
		});
	}

	private void createNameByDocumentType() {
		if(registry.getDocumentType().equals(DocumentType.CIF)) {
			name.addValueChangeHandler(e -> registry.setName(e.getValue()));
			name.setValue(registry.getName());
			name.setEnable(isParentUser());
			name.setVisibleTitle(AON.MSG.enterpriseName());
			
			firstSurname.getElement().getStyle().setDisplay(Display.NONE);
			secondSurname.getElement().getStyle().setDisplay(Display.NONE);
		} else {
			name.setEnable(isParentUser());
			name.setValue(AonStringUtils.isBlank(registry.getPersonName()) ? registry.getName() : registry.getPersonName());
			name.setVisibleTitle("Nombre");
			
			firstSurname.setEnable(isParentUser());
			firstSurname.setValue(registry.getPersonFirstsurname());

			secondSurname.setEnable(isParentUser());
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
		
		if(this.registrySource == RegistrySource.COMPANY) {
			
			RegistryStatus originalStatus;
			if(null != options.getConfiguration().getDomain().getExpirationDate()) originalStatus = RegistryStatus.BLOCKED;
			else 
				originalStatus = options.getConfiguration().getDomain().isActive() ? RegistryStatus.ACTIVE : RegistryStatus.INACTIVE;
			
			RegistryStatus newStatus = status.getValue();
		    Date newExpDate = status.getExpirationDate();
		    
		    if(originalStatus != newStatus) {
		    	AonMessagePanel.showLoading(messagePanel, "Guardando estado del dominio");
		    	
		    	commonService.saveDomainStatus(options.getDomainName(), options.getDomain(), options.getUser(), newStatus, newExpDate,
						new AsyncCallback<Domain>() {

							@Override
							public void onSuccess(Domain savedDomain) {
								options.getConfiguration().setDomain(savedDomain);
								saveCompany(success);
							}

							@Override
							public void onFailure(Throwable caught) {
								AonMessagePanel.showError(messagePanel, "Dominio error guardando estado : " + caught.getMessage());
							}

						});
		    	
		    } else 
		    	saveCompany(success);
			
		} else
			saveCompany(success);
	}
	
	private void saveCompany(Consumer<CompanyFull> success) {
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

}
