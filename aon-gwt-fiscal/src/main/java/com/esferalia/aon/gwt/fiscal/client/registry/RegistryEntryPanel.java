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
import com.esferalia.aon.gwt.common.client.widget.solutions.MediaTable;
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

	private TabLayoutPanel tablayoutPanel;

	// Other Info
	private AddressTable addressTable;
	private MediaTable mediaTable;

	private CompanyFull company;
	private Registry registry;

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

		AonCustomTextBox name = new AonCustomTextBox("Nombre");
		name.addValueChangeHandler(e -> registry.setName(e.getValue()));
		name.setValue(registry.getName());
		leftInfoTable.add(createRow(name));

		if (!this.registrySource.equals(RegistrySource.ENVIROMENT)) {
			AonCustomListBox documentNationality = new AonCustomListBox("Pais");
			documentNationality.clearItems();
			for (int i = 0; i < Country.values().length; i++)
				documentNationality.addItem(Country.values()[i].getIso2(), Country.values()[i].getIso2());
			documentNationality.addChangeHandler(
					e -> registry.setDocumentCountry(Country.safeValueOf(documentNationality.getValue())));
			documentNationality.setValue(registry.getDocumentCountry().getIso2());
			documentNationality.getElement().getStyle().setProperty("max-width", "5rem");

			AonCustomListBox documentType = new AonCustomListBox("Tipo");
			documentType.clearItems();
			for (int i = 0; i < DocumentType.values().length; i++)
				documentType.addItem(DocumentType.values()[i].getDescription(), DocumentType.values()[i].toString());
			documentType
					.addChangeHandler(e -> registry.setDocumentType(DocumentType.safeValueOf(documentType.getValue())));
			documentType.setValue(registry.getDocumentType().toString());
			documentType.getElement().getStyle().setProperty("max-width", "7rem");

			AonCustomTextBox document = new AonCustomTextBox("Documento");
			document.addValueChangeHandler(e -> registry.setDocument(e.getValue()));
			document.setValue(registry.getDocument());

			AonCustomListBox entity = new AonCustomListBox("Entidad");
			entity.clearItems();
			entity.addItem("P. F\u00edsicas", "0");
			entity.addItem("P. Jur\u00eddicas", "1");
			entity.setValue(registry.isLegalPerson() ? "1" : "0");
			entity.addChangeHandler(
					e -> registry.setLegalPerson(AonStringUtils.equalsIgnoreCase(entity.getValue(), "1")));

			leftInfoTable.add(createRow(documentType, documentNationality, document, entity));
		}

		gridPanel.add(leftInfoTable);

		HTMLPanel rightInfoTable = createTable();
		rightInfoTable.getElement().getStyle().setProperty("flex", "1");

		AonCustomTextBox alias = new AonCustomTextBox("Alias");
		alias.addValueChangeHandler(e -> registry.setAlias(e.getValue()));
		alias.setValue(registry.getAlias());
		rightInfoTable.add(createRow(alias));

		gridPanel.add(rightInfoTable);

		container.add(gridPanel);

		mediaTable = new MediaTable(options.getDomainName(), options.getDomain(), options.getUser(), registry.getId()) {

			@Override
			protected void onShowErrorMessage(String errorMessage) {
				AonMessagePanel.showError(messagePanel, errorMessage);
			}

		};

		tablayoutPanel.add(mediaTable, "Contacto");

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

		tablayoutPanel.add(new AonMainCertificatesPanel(options.getDomainName(), options.getDomain(), options.getUser()),
				"Certificados");

		container.add(tablayoutPanel);

		Scheduler.get().scheduleDeferred(new Command() {
			public void execute() {
				name.setFocus(true);
			}
		});
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
