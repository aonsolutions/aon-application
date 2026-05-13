package com.esferalia.aon.gwt.fiscal.client.registry;

import java.util.LinkedList;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
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
import com.esferalia.aon.gwt.common.client.widget.solutions.RegistryPaymethodBanksTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.WorkplaceTable;
import com.esferalia.aon.occam.api.model.GeoZone;
import com.esferalia.aon.occam.api.model.registry.CompanyFull;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.registry.RegistrySource;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.DocumentType;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class RegistryEntryPanel extends AonCustomDockLayout {

	// ------------------------------------------------- CommonServiceAsync

	static CommonServiceAsync commonService;

	private static void initializeCommonService() {
		if (commonService == null) {
			CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
			commonService = new CommonServiceAsyncDecorator(commonServiceRaw);
		}
	}

	// ------------------------------------------------- Variables

	private RegistryModuleOptions options;
	private RegistrySource registrySource;

	private HTMLPanel container;

	private HTMLPanel messagePanel = new HTMLPanel(AonStringUtils.EMPTY);
	
	private AonCustomTextBox alias = new AonCustomTextBox(AON.MSG.alias());
	private AonCustomListBox documentNationality = new AonCustomListBox("Pais");
	private AonCustomListBox documentType = new AonCustomListBox("Tipo");
	private AonCustomTextBox document = new AonCustomTextBox("Documento");
	private AonCustomTextBox name = new AonCustomTextBox(AON.MSG.enterpriseName());
	private AonCustomTextBox firstSurname = new AonCustomTextBox("Apellido");
	private AonCustomTextBox secondSurname = new AonCustomTextBox("Apellido 2");
	
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
	
	private CompanyFull company;
	private Registry registry;
	
	private boolean editEnable = false;
	
	// ------------------------------------------------- Constructor

	public RegistryEntryPanel(RegistryModuleOptions options, RegistrySource registrySource) {
		super(getToolbarTitle(registrySource));

		this.options = options;
		this.registrySource = registrySource;

		initializeCommonService();

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
	protected void onClearFilter() {
	}

	private void createToolbar() {
		AonToolbarButton saveButton = new AonToolbarButton("Guardar", AON.CSS.aonIconSave());
		saveButton.addClickHandler(e -> {
			switch (this.registrySource) {
			case ENVIROMENT:
			case COMPANY: {
				saveCompanyFull(saved -> {
				});
				break;
			}
			default:
				throw new IllegalArgumentException("Unexpected value: " + this.registrySource);
			}
		});
		addToolbarButton(saveButton);
		
		AonToolbarButton editButton = new AonToolbarButton("Editar datos", AON.CSS.aonIconEdit());
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

	// ------------------------------------------------- DataBase

	private void getRegistryBySource() {
		switch (this.registrySource) {
		case ENVIROMENT:
		case COMPANY: {
			getCompanyFull(companyFull -> initCompanyRegistry() );
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

			document.addValueChangeHandler(e -> registry.setDocument(e.getValue()));
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
		
		if (!this.registrySource.equals(RegistrySource.ENVIROMENT)) {
			registryPaymethodBanksTable = new RegistryPaymethodBanksTable(options.getDomainName(), options.getDomain(), options.getUser(), registry.getId()) {
	
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
		
		if (!this.registrySource.equals(RegistrySource.ENVIROMENT)) {
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
		
		tablayoutPanel.add(new AonMainCertificatesPanel(options.getDomainName(), options.getDomain(), options.getUser()), "Certificados");
		
		aonVisualIdentity = new AonVisualIdentity(options.getDomainName(), options.getDomain(), options.getUser(), registry.getId(), this.registrySource);
		tablayoutPanel.add(aonVisualIdentity, this.registrySource.equals(RegistrySource.ENVIROMENT) ? "Logo" : "Logo / Firma");

		container.add(tablayoutPanel);

		Scheduler.get().scheduleDeferred(new Command() {
			public void execute() {
				name.setFocus(true);
			}
		});
	}
	
	private void createNameByDocumentType() {
		rightInfoTable.clear();

		if(registry.getDocumentType().equals(DocumentType.CIF)) {
			name.addValueChangeHandler(e -> registry.setName(e.getValue()));
			name.setValue(registry.getName());
			name.setEnable(editEnable);
			rightInfoTable.add(createRow(name));
		} else {
			name.setEnable(editEnable);
			name.setValue(registry.getPersonName());
			
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

}
