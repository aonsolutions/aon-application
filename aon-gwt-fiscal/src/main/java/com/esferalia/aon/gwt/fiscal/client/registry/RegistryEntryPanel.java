package com.esferalia.aon.gwt.fiscal.client.registry;

import java.util.LinkedList;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.widget.solutions.AddressTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonAddressPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomCard;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDockLayout;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomTextBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMainCertificates;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMediaPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.MediaTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonAddressPanel.AonAddressPanelCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMediaPanel.AonMediaPanelCallback;
import com.esferalia.aon.occam.api.model.GeoZone;
import com.esferalia.aon.occam.api.model.registry.CompanyFull;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.registry.RegistryAddress;
import com.esferalia.aon.occam.api.model.registry.RegistryMedia;
import com.esferalia.aon.occam.api.model.registry.RegistrySource;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.DocumentType;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
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
	
	// ------------------------------------------------- Constructor

	public RegistryEntryPanel(RegistryModuleOptions options, RegistrySource registrySource) {
		super("Registry");
		
		this.options = options;
		this.registrySource = registrySource;
		
		initializeCommonService();
		
		hideSearchWidget();
		
		createToolbar();
		
		tablayoutPanel = new TabLayoutPanel(25.00, Unit.PX);
		tablayoutPanel.setHeight("100%");
		
		container = new HTMLPanel(AonStringUtils.EMPTY);
		container.addStyleName(AON.CSS.aonFlexColumn());
		add(container);
		
		getRegistryBySource();
	}

	@Override
	protected void onClearFilter() {}
	
	private void createToolbar() {
		AonToolbarButton saveButton = new AonToolbarButton("Guardar", AON.CSS.aonIconSave());
		saveButton.addClickHandler(e -> Window.alert("Save..."));
		addToolbarButton(saveButton);
	}
	
	// ------------------------------------------------- DataBase
	
	private void getRegistryBySource() {
		switch (this.registrySource) {
		case COMPANY: {
			getCompanyFull(companyFull -> {
				initCompanyRegistry(companyFull.getRegistry().get());
				setToolbarTitle(companyFull.getRegistry().getName());
			});
			break;
		}
		default:
			throw new IllegalArgumentException("Unexpected value: " + this.registrySource);
		}
		
	}
	
	private void initCompanyRegistry(Registry registry) {
		container.clear();
		tablayoutPanel.clear();
		
		container.add(messagePanel);
		
		HTMLPanel gridPanel = new HTMLPanel(AonStringUtils.EMPTY);
		gridPanel.setStyleName(AON.CSS.aonGridTwoCols());
		
		AonCustomCard infoCard = new AonCustomCard("Informaci\u00f3n");
		
		HTMLPanel generalInfoTable = createTable();
		
		AonCustomTextBox name = new AonCustomTextBox("Nombre");
		name.setValue(registry.getName());
		generalInfoTable.add(createRow(name));
		
		AonCustomTextBox alias = new AonCustomTextBox("Alias");
		alias.setValue(registry.getAlias());
		generalInfoTable.add(createRow(alias));
		
		AonCustomListBox documentNationality = new AonCustomListBox("Pais");
		documentNationality.clearItems();
		for(int i=0; i < Country.values().length; i++)
			documentNationality.addItem(Country.values()[i].getIso2(), Country.values()[i].getIso2());
		documentNationality.setValue(registry.getDocumentCountry().getIso2());
		documentNationality.getElement().getStyle().setProperty("max-width", "5rem");
		
		AonCustomListBox documentType = new AonCustomListBox("Tipo");
		documentType.clearItems();
		for(int i=0; i < DocumentType.values().length; i++)
			documentType.addItem(DocumentType.values()[i].getDescription(), DocumentType.values()[i].toString());
		documentType.setValue(registry.getDocumentType().toString());
		
		AonCustomTextBox document = new AonCustomTextBox("Documento");
		document.setValue(registry.getDocument());
		
		generalInfoTable.add(createRow(documentType, documentNationality, document));
		
		infoCard.add(generalInfoTable);
		gridPanel.add(infoCard);
		
		AonToolbarButton addAddress = new AonToolbarButton("Nueva Direcci\u00f3n", AON.CSS.aonIconAdd());
		addAddress.addClickHandler(e -> createAddres(registry));
		AonCustomCard addressCard = new AonCustomCard("Direcci\u00f3n", addAddress);
		
		addressTable = new AddressTable(options.getDomainName(), options.getDomain(), options.getUser(), registry.getId()) {

			@Override
			protected void onShowErrorMessage(String errorMessage) {
				AonMessagePanel.showError(messagePanel, errorMessage);
			}

			@Override
			protected LinkedList<GeoZone> getAviableGeozones() {
				return options.getConfiguration().getGeozones();
			}
		
		};
		addressCard.add(addressTable);
		
		gridPanel.add( addressCard );
		
		AonToolbarButton addMedia = new AonToolbarButton("Nuevo contacto", AON.CSS.aonIconAdd());
		addMedia.addClickHandler(e -> createMedia(registry));
		AonCustomCard mediaCard = new AonCustomCard("Contacto", addMedia);
		
		mediaTable = new MediaTable(options.getDomainName(), options.getDomain(), options.getUser(), registry.getId()) {

			@Override
			protected void onShowErrorMessage(String errorMessage) {
				AonMessagePanel.showError(messagePanel, errorMessage);
			}
		
		};
		mediaCard.add(mediaTable);
		
		gridPanel.add( mediaCard );
		
		ScrollPanel rootScroll = new ScrollPanel(gridPanel);
		rootScroll.getElement().getStyle().setProperty("margin-top", "1rem");
		tablayoutPanel.add(rootScroll, "Datos Generales");
		
		tablayoutPanel.add(new AonMainCertificates(options.getDomainName(), options.getDomain(), options.getUser()), "Certificados");
		
		container.add(tablayoutPanel);
		
		Scheduler.get().scheduleDeferred(new Command() {
	        public void execute() {
	        	name.setFocus(true);
	        }
	    });
	}

	private void createAddres(Registry registry) {
		final AonCustomDialog dialog = new AonCustomDialog();
		dialog.setCaption( "Nueva Direcci\u00f3n" );
		
		final AonAddressPanel marketingCampaignPanel = new AonAddressPanel( options.getDomainName(), options.getDomain(), options.getUser(), options.getConfiguration().getGeozones(), registry.getId(), new AonAddressPanelCallback() {
			
			@Override
			public void onCancel() {
				dialog.hide();
			}
			
			@Override
			public void onAccept(RegistryAddress address) {
				dialog.hide();
				addressTable.onSearch();
			}
		});
		
		dialog.add( marketingCampaignPanel );
		dialog.showLoaded();
	}
	
	private void createMedia(Registry registry) {
		final AonCustomDialog dialog = new AonCustomDialog();
		dialog.setCaption( "Nuevo Contacto" );
		
		final AonMediaPanel marketingCampaignPanel = new AonMediaPanel( options.getDomainName(), options.getDomain(), options.getUser(), registry.getId(), new AonMediaPanelCallback() {
			
			@Override
			public void onCancel() {
				dialog.hide();
			}
			
			@Override
			public void onAccept(RegistryMedia media) {
				dialog.hide();
				mediaTable.onSearch();
			}
		});
		
		dialog.add( marketingCampaignPanel );
		dialog.showLoaded();
	}
	
	private HTMLPanel createTable() {
		HTMLPanel table = new HTMLPanel(AonStringUtils.EMPTY);
		table.addStyleName(AON.CSS.aonFlexColumn());
		return table;
	}
	
	private HTMLPanel createRow(Widget ...ws) {
		HTMLPanel row = new HTMLPanel(AonStringUtils.EMPTY);
		row.addStyleName(AON.CSS.aonItemFlex());
		
		for(int i=0; i < ws.length; i++)
			row.add(ws[i]);
		
		return row;
	}

	private void getCompanyFull(Consumer<CompanyFull> success) {
		commonService.getCompanyFull(options.getDomainName(), options.getDomain(), options.getUser(), new AsyncCallback<CompanyFull>() {
			
			@Override
			public void onSuccess(CompanyFull companyFull) {
				success.accept(companyFull);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub	
			}
			
		});
	}

}
