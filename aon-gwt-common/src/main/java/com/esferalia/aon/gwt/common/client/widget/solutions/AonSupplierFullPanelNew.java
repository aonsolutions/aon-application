package com.esferalia.aon.gwt.common.client.widget.solutions;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.RegistryService;
import com.esferalia.aon.gwt.common.client.RegistryServiceAsync;
import com.esferalia.aon.gwt.common.client.RegistryServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.widget.InvoiceTransactionListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonAddressPanel.AonAddressPanelCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMediaPanel.AonMediaPanelCallback;
import com.esferalia.aon.gwt.common.shared.DocumentValidator;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.GeoZone;
import com.esferalia.aon.occam.api.model.registry.RegistryAddress;
import com.esferalia.aon.occam.api.model.registry.RegistryMedia;
import com.esferalia.aon.occam.api.model.registry.Supplier;
import com.esferalia.aon.occam.api.model.registry.SupplierFull;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.DocumentType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.SimplePanel;

public abstract class AonSupplierFullPanelNew extends SimplePanel {
	
	public static interface AonSupplierFullPanelCallback {
		void onAccept(SupplierFull registryFull);
		void onCancel();
	}

	private static RegistryServiceAsync REGISTRY_SERVICE;
	
	// Seller Info
	private AonCustomTextBox name = new AonCustomTextBox("Nombre");
	
	private AonCustomTextBox alias = new AonCustomTextBox("Alias");
	private AonCustomListBox scope = new AonCustomListBox("Ambito");
	
	private AonCustomListBox type = new AonCustomListBox("Entidad");
	private AonCustomListBox nationality = new AonCustomListBox("Nacionalidad");
	
	private AonCustomListBox documentType = new AonCustomListBox("Documento");
	private AonCustomListBox documentNationality = new AonCustomListBox("Pais Emision");
	private AonCustomTextBox document = new AonCustomTextBox(null);
	
	private AddressTable addressTable;
	private MediaTable mediaTable;
	
	private AonModuleOptions<?> options;
	private SupplierFull supplierFull;
	
	public AonSupplierFullPanelNew(AonModuleOptions<?> options, SupplierFull registryFull, AonSupplierFullPanelCallback callback) {
		RegistryServiceAsync registryServiceRaw = GWT.create(RegistryService.class);
		REGISTRY_SERVICE = new RegistryServiceAsyncDecorator(registryServiceRaw);
		
		this.options = options;
		this.supplierFull = registryFull;
		
		show(callback);
	}
	
	public AonSupplierFullPanelNew(AonModuleOptions<?> options, AonSupplierFullPanelCallback callback) {
		RegistryServiceAsync registryServiceRaw = GWT.create(RegistryService.class);
		REGISTRY_SERVICE = new RegistryServiceAsyncDecorator(registryServiceRaw);
		
		this.options = options;
		
		this.supplierFull = SupplierFull.initialize(options.getDomain());
		
		show(callback);
	}
	
	public void show(AonSupplierFullPanelCallback callback) {
		
		getElement().getStyle().setProperty("padding", "1rem 0");
		
		FlowPanel rootPanel = new FlowPanel();
		rootPanel.setStyleName(AON.CSS.aonFlexColumnBetween());
		
		final AonErrorPanel errorPanel = new AonErrorPanel();
		errorPanel.addStyleName(AON.CSS.aonMarginTop());
		rootPanel.add(errorPanel);
		
		FlowPanel tablePanel = new FlowPanel();
		tablePanel.setStyleName(AON.CSS.aonScrollArea());
		
		KeyUpHandler keyUpHandler = new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ESCAPE) {
					callback.onCancel();	
				}
			}
		};

		FlexTable table1 = new FlexTable();
		table1.setStyleName(AON.CSS.aonTable());
		table1.setWidth("100%");
		
		table1.setWidget(0,0,name);
		table1.getFlexCellFormatter().setColSpan(0, 0, 2);
		
		table1.setWidget(1,0,alias);
		
		scope.clearItems();
		this.options.getConfiguration().getAvailableScopes().forEach(as -> scope.addItem(as.getDescription(), as.getId().toString()));
		table1.setWidget(1,1,scope);
		
		nationality.clearItems();
		for(int i=0; i < Country.values().length; i++)
			nationality.addItem(Country.values()[i].getName(), Country.values()[i].getIso2());
		nationality.setValue("ES");
		table1.setWidget(2,0,nationality);
		
		type.clearItems();
		type.addItem("P. F\u00edsicas", "0");
		type.addItem("P. Jur\u00eddicas", "1");
		table1.setWidget(2,1,type);
		
		FlowPanel documentPanel = new FlowPanel();
		documentPanel.setStyleName(AON.CSS.aonItemFlex());
		documentPanel.getElement().getStyle().setProperty("align-items", "flex-end");
		
		documentNationality.clearItems();
		for(int i=0; i < Country.values().length; i++)
			documentNationality.addItem(Country.values()[i].getIso2(), Country.values()[i].getIso2());
		documentNationality.getElement().getStyle().setProperty("max-width", "5rem");
		documentNationality.setValue("ES");
		documentPanel.add(documentNationality);
		
		document.getTextBox().addValueChangeHandler(e -> {
			DocumentType documentTypeValidator = DocumentValidator.validateDocument(document.getValue());
			documentType.setValue(documentTypeValidator.toString());
		});
		documentPanel.add(document);
		
		table1.setWidget(3,0,documentPanel);
		
		documentType.clearItems();
		for(int i=0; i < DocumentType.values().length; i++)
			documentType.addItem(DocumentType.values()[i].getDescription(), DocumentType.values()[i].toString());
		table1.setWidget(3,1,documentType);
		
		Account acc = this.supplierFull.getAccount();
		AonAccountBox accountBox = new AonAccountBox(options.getDomainName(),options.getDomain(),options.getUser());
		accountBox.setAccount(acc);
		accountBox.addSelectionHandler(event -> {
				Account a = event.getSelectedItem();
				this.supplierFull.setAccount(a);
		});
		
		table1.setWidget(4, 0, accountBox);
		table1.getFlexCellFormatter().setColSpan(4, 0, 2);
		
		Supplier supplier = this.supplierFull.ensureSupplier();
		final InvoiceTransactionListBox transactionBox = new InvoiceTransactionListBox();
		transactionBox.setValue(supplier.getTransaction());
		transactionBox.addChangeHandler(event -> supplier.setTransaction(transactionBox.getValue()));
		
		table1.setWidget(5, 0, transactionBox);
		table1.getFlexCellFormatter().setColSpan(5, 0, 2);
		
		final CheckBox vatAccualPayment = new CheckBox(AON.MSG.vatAccrualPayment());
		final CheckBox withholdingFarmer = new CheckBox(AON.MSG.withholdingFarmer());
		final CheckBox withholding = new CheckBox(AON.MSG.withholding());
		
		FlowPanel taxPanel = new  FlowPanel();
		FlowPanel taxPanel0 = new  FlowPanel();
		taxPanel.add(taxPanel0);
		vatAccualPayment.setValue(supplier.isVatAccrualPayment());
		vatAccualPayment.setStyleName(AON.CSS.aonMarginRight());
		vatAccualPayment.addStyleName(AON.CSS.aonNowrap());
		vatAccualPayment.addClickHandler(event -> supplier.setVatAccrualPayment(vatAccualPayment.getValue()));
		taxPanel0.add(vatAccualPayment);
		
		withholdingFarmer.setValue(supplier.isWithholdingFarmer());
		withholdingFarmer.setStyleName(AON.CSS.aonMarginRight());
		withholdingFarmer.addStyleName(AON.CSS.aonNowrap());
		withholdingFarmer.addClickHandler(event -> supplier.setWithholdingFarmer(withholdingFarmer.getValue()));
		taxPanel0.add(withholdingFarmer);
		
		FlowPanel taxPanel1 = new  FlowPanel();
		taxPanel.add(taxPanel1);
		withholding.setValue(supplier.isWithholding());
		withholding.setStyleName(AON.CSS.aonMarginRight());
		withholding.addStyleName(AON.CSS.aonNowrap());
		withholding.addClickHandler(event -> supplier.setWithholding(withholding.getValue()));
		taxPanel1.add(withholding);
		
		table1.setWidget(6, 0, taxPanel);
		table1.getFlexCellFormatter().setColSpan(6, 0, 2);

		AonToolbarButton addAddress = new AonToolbarButton("Nueva Direcci\u00f3n", AON.CSS.aonIconAdd());
		addAddress.addClickHandler(e -> createAddres());
		AonCustomCard addressCard = new AonCustomCard("Direcci\u00f3n", addAddress);
		
		addressTable = new AddressTable(options.getDomainName(), options.getDomain(), options.getUser(), this.supplierFull.getRegistry().getId()) {

			@Override
			protected void onShowErrorMessage(String errorMessage) {
				//AonMessagePanel.showError(messagePanel, errorMessage);
			}

			@Override
			protected LinkedList<GeoZone> getAviableGeozones() {
				return options.getConfiguration().getGeozones();
			}
		
		};
		addressCard.add(addressTable);
		table1.setWidget(7, 0, addressCard);
		table1.getFlexCellFormatter().setColSpan(7, 0, 2);
		
		AonToolbarButton addMedia = new AonToolbarButton("Nuevo contacto", AON.CSS.aonIconAdd());
		addMedia.addClickHandler(e -> createMedia());
		AonCustomCard mediaCard = new AonCustomCard("Contacto", addMedia);
		
		mediaTable = new MediaTable(options.getDomainName(), options.getDomain(), options.getUser(), this.supplierFull.getRegistry().getId()) {

			@Override
			protected void onShowErrorMessage(String errorMessage) {
				//AonMessagePanel.showError(messagePanel, errorMessage);
			}
		
		};
		mediaCard.add(mediaTable);
		table1.setWidget(8, 0, mediaCard);
		table1.getFlexCellFormatter().setColSpan(8, 0, 2);
		
		table1.getColumnFormatter().setWidth(0, "5rem");
		
		tablePanel.add( table1 );
		
		rootPanel.add( tablePanel );
		
		FlowPanel buttons = new FlowPanel();
    	buttons.setStyleName(AON.CSS.aonTextCenter());
    	
    	final Button okButton = new Button();
    	okButton.setStyleName(AON.CSS.aonOkButton());
    	okButton.setText( AON.MSG.accept());
    	okButton.addKeyUpHandler( keyUpHandler);
    	
    	okButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				okButton.setEnabled(false);
				
				REGISTRY_SERVICE.save(options.getDomainName(), options.getDomain(), options.getUser(), supplierFull, new AsyncCallback<SupplierFull>() {
					@Override
					public void onSuccess(SupplierFull result) {
						callback.onAccept(result);
					}

					@Override
					public void onFailure(Throwable caught) {
						okButton.setEnabled(true);
					}
				});
			}
		});
    	
    	buttons.add(okButton);
    	
    	final Button cancelButton = new Button();
    	cancelButton.setStyleName(AON.CSS.aonCancelButton());
    	cancelButton.addStyleName(AON.CSS.aonMarginLeft());
    	cancelButton.setText( AON.MSG.cancelAction());
    	cancelButton.addKeyUpHandler( keyUpHandler);
    	cancelButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				cancelButton.setEnabled(false);
				callback.onCancel();
			}
		});
    	buttons.add(cancelButton);
    	rootPanel.add(buttons);
		setWidget(rootPanel);
		
		Scheduler.get().scheduleDeferred(new Command() {
	        public void execute() {
	        	name.setFocus(true);
	        	onResize();
	        }
	    });		
		
	}
	
	private void createAddres() {
		final AonCustomDialog dialog = new AonCustomDialog();
		dialog.setCaption( "Nueva Direcci\u00f3n" );
		
		final AonAddressPanel marketingCampaignPanel = new AonAddressPanel( options.getDomainName(), options.getDomain(), options.getUser(), options.getConfiguration().getGeozones(), this.supplierFull.getRegistry().getId(), new AonAddressPanelCallback() {
			
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
	
	private void createMedia() {
		final AonCustomDialog dialog = new AonCustomDialog();
		dialog.setCaption( "Nuevo Contacto" );
		
		final AonMediaPanel marketingCampaignPanel = new AonMediaPanel( options.getDomainName(), options.getDomain(), options.getUser(), this.supplierFull.getRegistry().getId(), new AonMediaPanelCallback() {
			
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

	protected abstract void onResize();

}
