package com.esferalia.aon.gwt.common.client.widget.solutions;

import java.util.logging.Logger;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.RegistryService;
import com.esferalia.aon.gwt.common.client.RegistryServiceAsync;
import com.esferalia.aon.gwt.common.client.RegistryServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.widget.CountryListBox;
import com.esferalia.aon.gwt.common.client.widget.MediaTypeListBox;
import com.esferalia.aon.gwt.common.client.widget.StreetTypeListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayTable.AonDisplayTableCell;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayTable.AonDisplayTableRow;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.GeoZone;
import com.esferalia.aon.occam.api.model.IAccount;
import com.esferalia.aon.occam.api.model.IScopable;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.registry.RegistryAddress;
import com.esferalia.aon.occam.api.model.registry.RegistryBank;
import com.esferalia.aon.occam.api.model.registry.RegistryFull;
import com.esferalia.aon.occam.api.model.registry.RegistryMedia;
import com.esferalia.aon.occam.api.model.security.Scope;
import com.esferalia.aon.occam.api.model.type.MediaType;
import com.esferalia.aon.occam.api.model.type.MediaType.IMediaTypeVisitor;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.logging.client.ConsoleLogHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

public class AonRegistryFullPanel<R extends RegistryFull<?>> extends ScrollPanel implements Focusable {
	public static final int MIN_WIDTH = 850;
	public static final int MIN_HEIGHT = 650;
	private RegistryServiceAsync service;

	private static final Logger LOGGER = Logger.getLogger(AonRegistryFullPanel.class.getName());
	static {
		LOGGER.addHandler( new ConsoleLogHandler() );
	}

	public static interface AonRegistryFullPanelCallback<R extends RegistryFull<?>> {
		void onAccept(R registryFull);
		void onCancel();
		void onError(Throwable caught);
		void onDocumenthanged(R registryFull);
		void setFocus(boolean b);
	}

	private final FlowPanel rootPanel = new FlowPanel();
	private final AonTextBox nameText = new AonTextBox();
	
	private AonAccountBox accountBox;
	
	public AonRegistryFullPanel(AonModuleOptions<?> options, R registryFull,AonRegistryFullPanelCallback<R> callback) {
		setStyleName(AON.CSS.aonScrollArea());
		addStyleName(AON.CSS.aonBoxSizingBorderBox());
		addStyleName(AON.CSS.aonWidthAll());
		addStyleName(AON.CSS.aonHeightAll());
		setWidget(rootPanel);

		addRegistry(options, registryFull,callback);
		addExtended(options, registryFull,callback);
		paintAddresses(options, registryFull,callback);
		paintMedias(options, registryFull,callback);
		paintBanks(options, registryFull, callback);
	}
	protected FlowPanel getRootPanel() {
		return rootPanel;
	}
	protected AonDisplayTable getNewTab() {
		AonDisplayTable displayTab = new AonDisplayTable();
		displayTab.addStyleName(AON.CSS.aonWidthAlmostAll());
		displayTab.addStyleName(AON.CSS.aonBlockCenter());
		return displayTab;
	}

	@Override
	public int getTabIndex() {
		return nameText.getTabIndex();
	}

	@Override
	public void setAccessKey(char key) {
		nameText.setAccessKey(key);
	}

	@Override
	public void setFocus(boolean focused) {
		nameText.setFocus(focused);
	}

	@Override
	public void setTabIndex(int index) {
		nameText.setTabIndex(index);
	}
	
	private void addRegistry(AonModuleOptions<?> options, R registryFull, AonRegistryFullPanelCallback<R> callback) {
		AonDisplayTable displayTab = getNewTab();
		Registry registry = registryFull.getRegistry();
		LOGGER.info( "AonRegistryFullPanel --> addRegistry " + registry.getName() ); 
		getRootPanel().add(displayTab);
		// ***************************************************************** [FULL DOCUMENT]
		AonFullDocument fulldocument = new AonFullDocument();
		fulldocument.setValue(registry.getDocumentType(), registry.getDocumentCountry(), registry.getDocument());
		fulldocument.addTypeChangeHandler(event -> registry.setDocumentType(fulldocument.getType()));
		fulldocument.addCountryChangeHandler(event -> registry.setDocumentCountry(fulldocument.getCountry()));
		
		fulldocument.addDocumentChangeHandler(event -> {
			registry.setDocument(fulldocument.getDocument());
			callback.onDocumenthanged(registryFull);
		});
		
		// ***************************************************************** [NAME]		
		nameText.setValue(registry.getName());
		nameText.setVisibleLength(40);
		nameText.setMaxLength(64);
		nameText.addValueChangeHandler(event -> registry.setName(nameText.getValue()));

		// ***************************************************************** [ALIAS]
		AonTextBox aliasText = new AonTextBox();
		aliasText.setValue(registry.getAlias());
		aliasText.setVisibleLength(30);
		aliasText.setMaxLength(32);
		aliasText.addValueChangeHandler(event -> registry.setAlias(aliasText.getValue()));
					
		// ***************************************************************** [NATIONALITY]
		CountryListBox nationalityBox = new CountryListBox();
		nationalityBox.setValue(registry.getNationality());
		nationalityBox.addChangeHandler(event -> registry.setNationality(nationalityBox.getValue()));
		
		addBasicRow(displayTab,new InlineLabel(AON.MSG.document()),fulldocument);
		addBasicRow(displayTab,new InlineLabel(AON.MSG.name()),nameText);
		addBasicRow(displayTab,new InlineLabel(AON.MSG.alias()),aliasText);
		addBasicRow(displayTab,new InlineLabel(AON.MSG.nationality()),nationalityBox);
	}
	
	protected void addBasicRow(AonDisplayTable tab, Widget label, Widget widget) {
		AonDisplayTableRow row = new AonDisplayTableRow();
		tab.add(row);
		AonDisplayTableCell labelCell = row.addCell(AON.CSS.aonTableLabel());
		labelCell.setWidth("180px");
		labelCell.add(label);
		AonDisplayTableCell widgetCell = row.addCell(AON.CSS.aonWidthAuto());
		widgetCell.add(widget);
	}
	
	/**
	 * Shows the banks of a registry
	 * @param options the module options
	 * @param registryFull includes the addresses, contacts and banks of a registry
	 * @param callback
	 */
	private void paintBanks(AonModuleOptions<?> options, RegistryFull<?> registryFull, AonRegistryFullPanelCallback<R> callback) {
		FlowPanel labelContainer = new FlowPanel();
		labelContainer.setStyleName(AON.CSS.aonFlexBlock());
		labelContainer.addStyleName(AON.CSS.aonBorderBottom());

		Label banksLabel = new Label(AON.MSG.banks());
		banksLabel.addStyleName(AON.CSS.aonFlexGrow1());
		banksLabel.addStyleName(AON.CSS.aonBold());
		labelContainer.add(banksLabel);
		
		AonTableButton addBank = new AonTableButton( AON.MSG.addBank(), AON.CSS.aonIconAdd() );
		getRootPanel().add(labelContainer);
		
		AonDisplayGrid displayTab = new AonDisplayGrid();
		displayTab.addStyleName(AON.CSS.aonWidthAlmostAll());
		displayTab.addStyleName(AON.CSS.aonBlockCenter());
		getRootPanel().add(displayTab);
		
		addBank.addClickHandler(event -> {
			if (registryFull.getBanks() == null || registryFull.getBanks().isEmpty()) {
				paintBankHeader(displayTab);		
			}
			RegistryBank registryBank = new RegistryBank();
			registryFull.addBank(registryBank);
			paintBank(displayTab, options, registryBank);
		});
		
		labelContainer.add(addBank);

		if (registryFull.getBanks() != null) {
			if (!registryFull.getBanks().isEmpty()) {
				paintBankHeader(displayTab);		
			}
			for (RegistryBank registryBank: registryFull.getBanks()) {
				paintBank(displayTab, options, registryBank);
			}
		}
	}
	
	/**
	 * Adds the cells of the bank table
	 * @param displayTab
	 */
	private void paintBankHeader(AonDisplayGrid displayTab) {
		displayTab.addHeaderRow()
			.addCell(new InlineLabel(AON.MSG.bankAccount()));
			/*.addCell(new InlineLabel("Bic"))
			.addCell(new InlineLabel("Sufijo"))
			.addCell(new InlineLabel("Alias"))
			.addCell(new InlineLabel("Requisici\u00F3n"))
			.addCell(new InlineLabel("Orden domiciliaci\u00F3n adeudo directo SEPA"));*/
	}
	
	/**
	 * Paint one bank when we add a new bank
	 * @param displayTab
	 * @param options the module options
	 * @param registryAddress
	 */
	private void paintBank(AonDisplayGrid displayTab, AonModuleOptions<?> options, RegistryBank registryBank) {
			
		// ***************************************************************** [BANK ACCOUNT]		
		final AonBankAccountBox aonBankAccountBox = new AonBankAccountBox();
		aonBankAccountBox.setWidth("250px");
		aonBankAccountBox.setValue(registryBank.getBankAccount());
		aonBankAccountBox.addValueChangeHandler(event -> registryBank.setBankAccount(aonBankAccountBox.getValue()));
		
		// ***************************************************************** [BIC]		
		/*
		 * final AonTextBox bicText = new AonTextBox(); bicText.setVisibleLength(10);
		 * bicText.setMaxLength(20); bicText.setValue(registryBank.getBic());
		 * bicText.addChangeHandler(event -> registryBank.setBic(bicText.getValue()));
		 * 
		 * // ***************************************************************** [SUFFIX]
		 * final AonTextBox suffixText = new AonTextBox();
		 * suffixText.setVisibleLength(5); suffixText.setMaxLength(10);
		 * suffixText.setValue(registryBank.getSuffix());
		 * suffixText.addChangeHandler(event ->
		 * registryBank.setSuffix(suffixText.getValue()));
		 * 
		 * // ***************************************************************** [ALIAS]
		 * final AonTextBox aliasText = new AonTextBox();
		 * aliasText.setVisibleLength(10); aliasText.setMaxLength(20);
		 * aliasText.setValue(registryBank.getAlias()); aliasText.addChangeHandler(event
		 * -> registryBank.setAlias(aliasText.getValue()));
		 * 
		 * // *****************************************************************
		 * [REQUISITION] final AonTextBox requisitionText = new AonTextBox();
		 * requisitionText.setVisibleLength(10); requisitionText.setMaxLength(20);
		 * requisitionText.setValue(registryBank.getRequisition());
		 * requisitionText.addChangeHandler(event ->
		 * registryBank.setRequisition(requisitionText.getValue()));
		 * 
		 * // ***************************************************************** [SEPA
		 * MANDATE REF] final AonTextBox sepaMandateRefText = new AonTextBox();
		 * sepaMandateRefText.setVisibleLength(10); sepaMandateRefText.setMaxLength(20);
		 * sepaMandateRefText.setValue(registryBank.getSepaMandateRef());
		 * sepaMandateRefText.addChangeHandler(event ->
		 * registryBank.setSepaMandateRef(sepaMandateRefText.getValue()));
		 * 
		 */
		
		final AonTableButton deleteBankButton = new AonTableButton( AON.MSG.deleteBank(),AON.CSS.aonIconDelete());
		final AonTableButton restoreBankButton = new AonTableButton( AON.MSG.restoreAction(),AON.CSS.aonIconRestoreDeleted());
		
		deleteBankButton.setVisible(!registryBank.isRemoved());
		restoreBankButton.setVisible(registryBank.isRemoved());
		
		FlowPanel buttons = new FlowPanel();
		buttons.add(deleteBankButton);
		buttons.add(restoreBankButton);
		
		deleteBankButton.addClickHandler(event -> {
			registryBank.setRemoved(true);
			deleteBankButton.setVisible(false);
			restoreBankButton.setVisible(true);
			aonBankAccountBox.addStyleName(AON.CSS.aonTextLineThrough());
			aonBankAccountBox.setEnabled(false);
			/*
			 * bicText.addStyleName(AON.CSS.aonTextLineThrough());
			 * bicText.setEnabled(false);
			 * suffixText.addStyleName(AON.CSS.aonTextLineThrough());
			 * suffixText.setEnabled(false);
			 * aliasText.addStyleName(AON.CSS.aonTextLineThrough());
			 * aliasText.setEnabled(false);
			 * requisitionText.addStyleName(AON.CSS.aonTextLineThrough());
			 * requisitionText.setEnabled(false);
			 * sepaMandateRefText.addStyleName(AON.CSS.aonTextLineThrough());
			 * sepaMandateRefText.setEnabled(false);
			 */
		});
		
		restoreBankButton.addClickHandler(event -> {
			registryBank.setRemoved(false);
			restoreBankButton.setVisible(false);
			deleteBankButton.setVisible(true);
			aonBankAccountBox.removeStyleName(AON.CSS.aonTextLineThrough());
			aonBankAccountBox.setEnabled(true);
			/*
			 * bicText.removeStyleName(AON.CSS.aonTextLineThrough());
			 * bicText.setEnabled(true);
			 * suffixText.removeStyleName(AON.CSS.aonTextLineThrough());
			 * suffixText.setEnabled(true);
			 * aliasText.removeStyleName(AON.CSS.aonTextLineThrough());
			 * aliasText.setEnabled(true);
			 * requisitionText.removeStyleName(AON.CSS.aonTextLineThrough());
			 * requisitionText.setEnabled(true);
			 * sepaMandateRefText.removeStyleName(AON.CSS.aonTextLineThrough());
			 * sepaMandateRefText.setEnabled(true);
			 */
		});
		
		displayTab.addHeaderRow()
			.addCell(aonBankAccountBox)
				/*
				 * .addCell(bicText) .addCell(suffixText) .addCell(aliasText)
				 * .addCell(requisitionText) .addCell(sepaMandateRefText)
				 */
			.addCell(buttons);
	}
	
	/**
	 * Shows the addresses of a registry
	 * @param options the module options
	 * @param registryFull includes the addresses, contacts and banks of a registry
	 * @param callback
	 */
	private void paintAddresses(AonModuleOptions<?> options, RegistryFull<?> registryFull, AonRegistryFullPanelCallback<R> callback) {
		FlowPanel labelContainer = new FlowPanel();
		labelContainer.setStyleName(AON.CSS.aonFlexBlock());
		labelContainer.addStyleName(AON.CSS.aonBorderBottom());

		Label addressesLabel = new Label(AON.MSG.addresses());
		addressesLabel.addStyleName(AON.CSS.aonFlexGrow1());
		addressesLabel.addStyleName(AON.CSS.aonBold());
		labelContainer.add(addressesLabel);
		
		AonTableButton addAddress = new AonTableButton( AON.MSG.addAddress(), AON.CSS.aonIconAdd() );
		getRootPanel().add(labelContainer);
		
		AonDisplayGrid displayTab = new AonDisplayGrid();
		displayTab.addStyleName(AON.CSS.aonWidthAlmostAll());
		displayTab.addStyleName(AON.CSS.aonBlockCenter());
		getRootPanel().add(displayTab);
		
		addAddress.addClickHandler(event -> {
			if ( registryFull.getAddresses() == null || registryFull.getAddresses().isEmpty()) {
				paintAddressHeader(displayTab);		
			}
			RegistryAddress registryAddress = new RegistryAddress();
			registryAddress.setMain( registryFull.getAddresses() == null || registryFull.getAddresses().isEmpty() )
				.setDirty(false);
			registryFull.addAddress(registryAddress);
			paintAddress(displayTab, options, registryAddress );
		});
		labelContainer.add(addAddress);

		if ( registryFull.getAddresses() != null) {
			if ( !registryFull.getAddresses().isEmpty()) {
				paintAddressHeader(displayTab);		
			}
			for (RegistryAddress registryAddress: registryFull.getAddresses() ) {
				paintAddress(displayTab, options, registryAddress);
			}
		}
	}

	/**
	 * Adds the cells of the address table
	 * @param displayTab
	 */
	private void paintAddressHeader(AonDisplayGrid displayTab) {
		displayTab.addHeaderRow()
			.addCell(new InlineLabel(""))
			.addCell(new InlineLabel(""))
			.addCell(new InlineLabel(AON.MSG.address()))
			.addCell(new InlineLabel(AON.MSG.number()))
			.addCell(new InlineLabel(AON.MSG.zip()))
			.addCell(new InlineLabel(AON.MSG.town()))
			.addCell(new InlineLabel(AON.MSG.province()))
			.addCell(new InlineLabel(""));
	}

	/**
	 * Paint one address
	 * @param displayTab
	 * @param options the module options
	 * @param registryAddress
	 */
	private void paintAddress(AonDisplayGrid displayTab, AonModuleOptions<?> options, RegistryAddress registryAddress) {
		final CheckBox mainBox = new CheckBox();
		mainBox.setValue(registryAddress.isMain());
		mainBox.addClickHandler(event -> registryAddress.setMain(mainBox.getValue()));
			
		// ***************************************************************** [STREET TYPE]		
		final StreetTypeListBox streetTypeBox = new StreetTypeListBox();
		streetTypeBox.setWidth("70px");
		streetTypeBox.setValue(registryAddress.getStreetType());
		streetTypeBox.addChangeHandler(event -> registryAddress.setStreetType(streetTypeBox.getValue()));
		
		// ***************************************************************** [ADDRESS]		
		final AonTextBox addressText = new AonTextBox();
		addressText.setValue(registryAddress.getAddress());
		addressText.addStyleName(AON.CSS.aonMarginLeftSep());
		addressText.setVisibleLength(30);
		addressText.setMaxLength(64);
		addressText.addValueChangeHandler(event -> registryAddress.setAddress(addressText.getValue()));
		
		// ***************************************************************** [NUMBER]
		final AonTextBox numberText = new AonTextBox();
		numberText.setValue(registryAddress.getNumber());
		numberText.addStyleName(AON.CSS.aonMarginLeftSep());
		numberText.setVisibleLength(5);
		numberText.setMaxLength(10);
		numberText.addValueChangeHandler(event -> registryAddress.setNumber(numberText.getValue()));

		// ***************************************************************** [ZIP]
		final AonTextBox zipText = new AonTextBox();
		zipText.setValue(registryAddress.getZip());
		zipText.setStyleName(AON.CSS.aonInputText());
		zipText.setVisibleLength(5);
		zipText.setMaxLength(10);
		zipText.addValueChangeHandler(event -> registryAddress.setZip(zipText.getValue()));

		// ***************************************************************** [CITY]
		final AonTextBox cityText = new AonTextBox();
		cityText.setValue(registryAddress.getCity());
		cityText.setVisibleLength(25);
		cityText.setMaxLength(64);
		cityText.addValueChangeHandler(event -> registryAddress.setCity(cityText.getValue()));
		
		// ***************************************************************** [GEOZONE]
		final ListBox geozoneBox = new ListBox();
		geozoneBox.setWidth("90px");
		if (options.getConfiguration().hasGeozones()) {
			zipText.addValueChangeHandler(event -> {
				if (!AonStringUtils.isBlank( zipText.getValue())) {
					String code = AonStringUtils.substring(AonStringUtils.trim(zipText.getValue()), 0, 2);
					int i = 1;
					for (GeoZone geozone : options.getConfiguration().getGeozones()) {
						if (AonStringUtils.equals(geozone.getCode(),code)) {
							geozoneBox.setSelectedIndex(i);
							int g = AonNumberUtils.toint(geozoneBox.getSelectedValue());
							registryAddress.setGeozone(g==Integer.MIN_VALUE?null:g);
						}
						i++;
					}
				}
			});
			
			geozoneBox.addItem("-----------",AonNumberUtils.toString(Integer.MIN_VALUE));
			int i = 1;
			for (GeoZone geozone : options.getConfiguration().getGeozones()) {
				geozoneBox.addItem(geozone.getName(),AonNumberUtils.toString(geozone.getId()));
				if (AonNumberUtils.equals(geozone.getId(), registryAddress.getGeozone())) {
					geozoneBox.setSelectedIndex(i);
				}
				i++;
			}
			geozoneBox.addChangeHandler (event -> {
				int g = AonNumberUtils.toint(geozoneBox.getSelectedValue());
				registryAddress.setGeozone(g==Integer.MIN_VALUE?null:g);
			});
		}
		
		final AonTableButton deleteAddressButton = new AonTableButton( AON.MSG.deleteAddress(),AON.CSS.aonIconDelete());
		final AonTableButton restoreAddressButton = new AonTableButton( AON.MSG.restoreAction(),AON.CSS.aonIconRestoreDeleted());
		
		deleteAddressButton.setVisible(!registryAddress.isRemoved());
		restoreAddressButton.setVisible(registryAddress.isRemoved());
		
		FlowPanel buttons = new FlowPanel();
		buttons.add(deleteAddressButton);
		buttons.add(restoreAddressButton);
		
		deleteAddressButton.addClickHandler(event -> {
			registryAddress.setRemoved(true);
			deleteAddressButton.setVisible(false);
			restoreAddressButton.setVisible(true);
			mainBox.setEnabled(false);
			streetTypeBox.addStyleName(AON.CSS.aonTextLineThrough());
			streetTypeBox.setEnabled(false);
			addressText.addStyleName(AON.CSS.aonTextLineThrough());
			addressText.setEnabled(false);
			numberText.addStyleName(AON.CSS.aonTextLineThrough());
			numberText.setEnabled(false);
			zipText.addStyleName(AON.CSS.aonTextLineThrough());
			zipText.setEnabled(false);
			cityText.addStyleName(AON.CSS.aonTextLineThrough());
			cityText.setEnabled(false);
			geozoneBox.addStyleName(AON.CSS.aonTextLineThrough());
			geozoneBox.setEnabled(false);
		});
		
		restoreAddressButton.addClickHandler(event -> {
			registryAddress.setRemoved(false);
			restoreAddressButton.setVisible(false);
			deleteAddressButton.setVisible(true);
			mainBox.setEnabled(true);
			streetTypeBox.removeStyleName(AON.CSS.aonTextLineThrough());
			streetTypeBox.setEnabled(true);
			addressText.removeStyleName(AON.CSS.aonTextLineThrough());
			addressText.setEnabled(true);
			numberText.removeStyleName(AON.CSS.aonTextLineThrough());
			numberText.setEnabled(true);
			zipText.removeStyleName(AON.CSS.aonTextLineThrough());
			zipText.setEnabled(true);
			cityText.removeStyleName(AON.CSS.aonTextLineThrough());
			cityText.setEnabled(true);
			geozoneBox.removeStyleName(AON.CSS.aonTextLineThrough());
			geozoneBox.setEnabled(true);
		});
		
		displayTab.addHeaderRow()
			.addCell(mainBox)
			.addCell(streetTypeBox)
			.addCell(addressText)
			.addCell(numberText)
			.addCell(zipText)
			.addCell(cityText)
			.addCell(geozoneBox)
			.addCell(buttons);
	}
	
	/**
	 * Shows the addresses of a registry
	 * @param options the module options
	 * @param registryFull includes the addresses, contacts and banks of a registry
	 * @param callback
	 */
	private void paintMedias(AonModuleOptions<?> options, R registryFull, AonRegistryFullPanelCallback<R> callback) {
		FlowPanel labelContainer = new FlowPanel();
		labelContainer.setStyleName(AON.CSS.aonFlexBlock());
		labelContainer.addStyleName(AON.CSS.aonBorderBottom());
		
		Label mediasLabel = new Label(AON.MSG.contacts());
		mediasLabel.addStyleName(AON.CSS.aonFlexGrow1());
		mediasLabel.addStyleName(AON.CSS.aonBold());
		labelContainer.add(mediasLabel);
		
		AonTableButton addMedia = new AonTableButton( AON.MSG.addContact(), AON.CSS.aonIconAdd() );
		getRootPanel().add(labelContainer);
		
		AonDisplayGrid displayTab = new AonDisplayGrid();
		displayTab.addStyleName(AON.CSS.aonWidthAlmostAll());
		displayTab.addStyleName(AON.CSS.aonBlockCenter());
		getRootPanel().add(displayTab);

		addMedia.addClickHandler(event -> {
			if ( registryFull.getMedias() == null || registryFull.getMedias().isEmpty()) {
				paintMediaHeader(displayTab);		
			}
			RegistryMedia registryMedia = new RegistryMedia();
			registryMedia.setMedia(MediaType.FIXED_PHONE)
				.setDirty(false);
			registryFull.addMedia(registryMedia);
			paintMedia(displayTab, options, registryMedia );
		});
		
		labelContainer.add(addMedia);
		if (registryFull.getMedias() != null ) {
			if (!registryFull.getMedias().isEmpty()) {
				paintMediaHeader(displayTab);		
			}
			for (RegistryMedia registryMedia : registryFull.getMedias() ) {
				paintMedia(displayTab, options, registryMedia );
			}
		}
	}
	
	/**
	 * Adds the cells of the media table
	 * @param displayTab
	 */
	private void paintMediaHeader(AonDisplayGrid displayTab) {
		displayTab.addHeaderRow()
			.addCell(new InlineLabel(AON.MSG.type()))
			.addCell(new InlineLabel(AON.MSG.data()))
			.addCell(new InlineLabel(AON.MSG.comments()))
			.addCell(new InlineLabel("ADM."))
			.addCell(new InlineLabel("COM."))
			.addCell(new InlineLabel("TEC."))
			.addCell(new InlineLabel(""));
	}

	/**
	 * Paint one address
	 * @param displayTab
	 * @param options the module options
	 * @param registryAddress
	 */
	private void paintMedia(AonDisplayGrid displayTab, AonModuleOptions<?> options, RegistryMedia registryMedia) {
		final MediaTypeListBox mediaBox = new MediaTypeListBox();
		mediaBox.setValue(registryMedia.getMedia());
		final AonTextBox valueBox = new AonTextBox();
		valueBox.setValue(registryMedia.getValue());
		valueBox.addValueChangeHandler(event -> registryMedia.setValue(valueBox.getValue()));
		
		final AonTextBox commentsBox = new AonTextBox();
		commentsBox.setValue(registryMedia.getComment());
		commentsBox.setVisibleLength(35);
		commentsBox.setMaxLength(64);
		commentsBox.addValueChangeHandler(event -> registryMedia.setComment(commentsBox.getValue()));
		
		final CheckBox admBox = new CheckBox();
		admBox.setValue(registryMedia.isAdministrative());
		admBox.addClickHandler(event -> registryMedia.setAdministrative(admBox.getValue()));
		
		final CheckBox comBox = new CheckBox();
		comBox.setValue(registryMedia.isCommercial());
		comBox.addClickHandler(event -> registryMedia.setCommercial(comBox.getValue()));
		
		final CheckBox tecBox = new CheckBox();
		tecBox.setValue(registryMedia.isTechnical());
		tecBox.addClickHandler(event -> registryMedia.setTechnical(tecBox.getValue()));

		final AonTableButton deleteMediaButton = new AonTableButton( AON.MSG.deleteContact(),AON.CSS.aonIconDelete());
		final AonTableButton restoreMediaButton = new AonTableButton( AON.MSG.restoreAction(),AON.CSS.aonIconRestoreDeleted());
		
		deleteMediaButton.setVisible(!registryMedia.isRemoved());
		restoreMediaButton.setVisible(registryMedia.isRemoved());
		
		FlowPanel buttons = new FlowPanel();
		buttons.add(deleteMediaButton);
		buttons.add(restoreMediaButton);
		
		deleteMediaButton.addClickHandler(event -> {
			registryMedia.setRemoved(true);
			deleteMediaButton.setVisible(false);
			restoreMediaButton.setVisible(true);
			mediaBox.addStyleName(AON.CSS.aonTextLineThrough());
			mediaBox.setEnabled(false);
			valueBox.addStyleName(AON.CSS.aonTextLineThrough());
			valueBox.setEnabled(false);
			commentsBox.addStyleName(AON.CSS.aonTextLineThrough());
			commentsBox.setEnabled(false);
			admBox.setEnabled(false); 
			comBox.setEnabled(false);
			tecBox.setEnabled(false);
		});
		
		restoreMediaButton.addClickHandler(event -> {
			registryMedia.setRemoved(false);
			restoreMediaButton.setVisible(false);
			deleteMediaButton.setVisible(true);
			mediaBox.removeStyleName(AON.CSS.aonTextLineThrough());
			mediaBox.setEnabled(true);
			valueBox.removeStyleName(AON.CSS.aonTextLineThrough());
			valueBox.setEnabled(true);
			commentsBox.removeStyleName(AON.CSS.aonTextLineThrough());
			commentsBox.setEnabled(true);
			admBox.setEnabled(true); 
			comBox.setEnabled(true);
			tecBox.setEnabled(true);
		});
		
		
		
		IMediaTypeVisitor mediaVisitor = new IMediaTypeVisitor() {
			@Override public void visitUnknown() {
				// Empty method
			}
			@Override public void visitFixedPhone() {
				valueBox.setVisibleLength(15);
				valueBox.setMaxLength(15);
			}
			@Override public void visitCellular() { 
				valueBox.setVisibleLength(15);
				valueBox.setMaxLength(15);
			}
			@Override public void visitFax() {
				valueBox.setVisibleLength(15);
				valueBox.setMaxLength(15);
			}
			@Override public void visitEmail() { 
				valueBox.setVisibleLength(35);
				valueBox.setMaxLength(64);
			}
			@Override public void visitWeb() { 
				valueBox.setVisibleLength(35);
				valueBox.setMaxLength(64);
			} 
		};
		
		mediaBox.addChangeHandler(event -> {
			registryMedia.setMedia(mediaBox.getValue());
			registryMedia.getMedia().visit( mediaVisitor );
		});
		
		registryMedia.getMedia().visit( mediaVisitor );
		displayTab.addRow()
			.addCell(mediaBox)
			.addCell(valueBox)
			.addCell(commentsBox)
			.addCell(admBox)
			.addCell(comBox)
			.addCell(tecBox)
			.addCell(buttons)
			;
		
	}
	
	protected void addScopeRow(AonDisplayTable displayTab, AonModuleOptions<?> options, IScopable<?> scopable) {
		if ( options.getConfiguration().hasAvailableScopes()) {
			scopable.setScope( options.getConfiguration().getAvailableScopes().get(0)); 
			final ListBox scopeBox = new ListBox();
			for (Scope scope : options.getConfiguration().getAvailableScopes()) {
				scopeBox.addItem(scope.getDescription(),AonNumberUtils.toString(scope.getId()));
			}
			scopeBox.addChangeHandler(event -> {
				Integer scopeId = AonNumberUtils.toint(scopeBox.getSelectedValue());
				Scope scope = options.getConfiguration().getAvailableScopes().stream()
					.filter(f -> f.getId().equals(scopeId))
					.findFirst().orElse(new Scope());
				scopable.setScope(scope);
			});
			addBasicRow(displayTab, new InlineLabel(AON.MSG.scope()),scopeBox);				
		}
	}

	protected void addAccountRow(AonDisplayTable displayTab,AonModuleOptions<?> options, IAccount<?> account) {
		Account acc = account.getAccount();
		accountBox = new AonAccountBox(options.getDomainName(),options.getDomain(),options.getUser());
		accountBox.setAccount(acc);
		accountBox.addSelectionHandler(event -> {
				Account a = event.getSelectedItem();
				account.setAccount(a);
		});
		addBasicRow(displayTab, new InlineLabel(AON.MSG.account()), accountBox);				
	}
	
	protected void setAccountEnable(boolean enabled) {
		accountBox.setEnabled(false);			
	}

	protected void addExtended(AonModuleOptions<?> options, R registryFull, AonRegistryFullPanelCallback<R> callback) {
		// Empty method
	}

	protected RegistryServiceAsync getService() {
		if (service == null) {
			RegistryServiceAsync serviceRaw = GWT.create(RegistryService.class);
			service = new RegistryServiceAsyncDecorator(serviceRaw);
		}
		return service;
	}
	
	
}

