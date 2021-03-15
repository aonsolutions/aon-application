package com.esferalia.aon.gwt.common.client.widget.solutions;

import java.util.logging.Logger;

import com.esferalia.aon.gwt.common.client.AON;
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
import com.esferalia.aon.occam.api.model.registry.RegistryFull;
import com.esferalia.aon.occam.api.model.registry.RegistryMedia;
import com.esferalia.aon.occam.api.model.security.Scope;
import com.esferalia.aon.occam.api.model.type.MediaType;
import com.esferalia.aon.occam.api.model.type.MediaType.IMediaTypeVisitor;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
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
		fulldocument.addTypeChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				registry.setDocumentType(fulldocument.getType());
			}
		});
		fulldocument.addCountryChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				registry.setDocumentCountry(fulldocument.getCountry());
			}
		});
		fulldocument.addDocumentChangeHandler(new ValueChangeHandler<String>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				registry.setDocument(fulldocument.getDocument());
				callback.onDocumenthanged(registryFull);
			}
			
		});
		
		// ***************************************************************** [NAME]		
		nameText.setValue(registry.getName());
		nameText.setVisibleLength(40);
		nameText.setMaxLength(64);
		nameText.addValueChangeHandler(new ValueChangeHandler<String>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				registry.setName(nameText.getValue());
			}
		});

		// ***************************************************************** [ALIAS]
		AonTextBox aliasText = new AonTextBox();
		aliasText.setValue(registry.getAlias());
		aliasText.setVisibleLength(30);
		aliasText.setMaxLength(32);
		aliasText.addValueChangeHandler(new ValueChangeHandler<String>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				registry.setAlias(aliasText.getValue());
			}
		});
		;
		
		// ***************************************************************** [NATIONALITY]
		CountryListBox nationalityBox = new CountryListBox();
		nationalityBox.setValue(registry.getNationality());
		nationalityBox.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				registry.setNationality(nationalityBox.getValue());
			}

		});
		
		
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
		
		addAddress.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				if ( registryFull.getAddresses() == null || registryFull.getAddresses().isEmpty()) {
					paintAddressHeader(displayTab);		
				}
				RegistryAddress registryAddress = new RegistryAddress();
				registryAddress.setMain( registryFull.getAddresses() == null || registryFull.getAddresses().isEmpty() )
					.setDirty(false);
				registryFull.addAddress(registryAddress);
				paintAddress(displayTab, options, registryAddress );
			}
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

	private void paintAddress(AonDisplayGrid displayTab, AonModuleOptions<?> options, RegistryAddress registryAddress) {
		final CheckBox mainBox = new CheckBox();
		mainBox.setValue(registryAddress.isMain());
		mainBox.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				registryAddress.setMain(mainBox.getValue());
			}
		});
		// ***************************************************************** [STREET TYPE]		
		final StreetTypeListBox streetTypeBox = new StreetTypeListBox();
		streetTypeBox.setWidth("70px");
		streetTypeBox.setValue(registryAddress.getStreetType());
		streetTypeBox.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				registryAddress.setStreetType(streetTypeBox.getValue());
			}
		});
		
		// ***************************************************************** [ADDRESS]		
		final AonTextBox addressText = new AonTextBox();
		addressText.setValue(registryAddress.getAddress());
		addressText.addStyleName(AON.CSS.aonMarginLeftSep());
		addressText.setVisibleLength(30);
		addressText.setMaxLength(64);
		addressText.addValueChangeHandler(new ValueChangeHandler<String>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				registryAddress.setAddress(addressText.getValue());
			}
		});
		
		// ***************************************************************** [NUMBER]
		final AonTextBox numberText = new AonTextBox();
		numberText.setValue(registryAddress.getNumber());
		numberText.addStyleName(AON.CSS.aonMarginLeftSep());
		numberText.setVisibleLength(5);
		numberText.setMaxLength(10);
		numberText.addValueChangeHandler(new ValueChangeHandler<String>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				registryAddress.setNumber(numberText.getValue());
			}
		});


		// ***************************************************************** [ZIP]
		final AonTextBox zipText = new AonTextBox();
		zipText.setValue(registryAddress.getZip());
		zipText.setStyleName(AON.CSS.aonInputText());
		zipText.setVisibleLength(5);
		zipText.setMaxLength(10);
		zipText.addValueChangeHandler(new ValueChangeHandler<String>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				registryAddress.setZip(zipText.getValue());
			}
		});

		// ***************************************************************** [CITY]
		final AonTextBox cityText = new AonTextBox();
		cityText.setValue(registryAddress.getCity());
		cityText.setVisibleLength(25);
		cityText.setMaxLength(64);
		cityText.addValueChangeHandler(new ValueChangeHandler<String>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				registryAddress.setCity(cityText.getValue());
			}
		});
		// ***************************************************************** [GEOZONE]
		final ListBox geozoneBox = new ListBox();
		geozoneBox.setWidth("90px");
		if (options.getConfiguration().hasGeozones()) {
			zipText.addValueChangeHandler(new ValueChangeHandler<String>() {
				@Override
				public void onValueChange(ValueChangeEvent<String> event) {
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
			geozoneBox.addChangeHandler(new ChangeHandler() {
				
				@Override
				public void onChange(ChangeEvent event) {
					int g = AonNumberUtils.toint(geozoneBox.getSelectedValue());
					registryAddress.setGeozone(g==Integer.MIN_VALUE?null:g);
				}
			});
		}
		
		final AonTableButton deleteAddressButton = new AonTableButton( AON.MSG.deleteContact(),AON.CSS.aonIconDelete());
		final AonTableButton restoreAddressButton = new AonTableButton( AON.MSG.restoreAction(),AON.CSS.aonIconRestoreDeleted());
		
		deleteAddressButton.setVisible(!registryAddress.isRemoved());
		restoreAddressButton.setVisible(registryAddress.isRemoved());
		
		FlowPanel buttons = new FlowPanel();
		buttons.add(deleteAddressButton);
		buttons.add(restoreAddressButton);
		
		deleteAddressButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
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
			}
		});
		restoreAddressButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
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
			}
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

		addMedia.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				if ( registryFull.getMedias() == null || registryFull.getMedias().isEmpty()) {
					paintMediaHeader(displayTab);		
				}
				RegistryMedia registryMedia = new RegistryMedia();
				registryMedia.setMedia(MediaType.FIXED_PHONE)
					.setDirty(false);
				registryFull.addMedia(registryMedia);
				paintMedia(displayTab, options, registryMedia );
			}
		});
		labelContainer.add(addMedia);
		if ( registryFull.getMedias() != null ) {
			if ( !registryFull.getMedias().isEmpty()) {
				paintMediaHeader(displayTab);		
			}
			for (RegistryMedia registryMedia : registryFull.getMedias() ) {
				paintMedia(displayTab, options, registryMedia );
			}
		}
	}
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

	private void paintMedia(AonDisplayGrid displayTab, AonModuleOptions<?> options, RegistryMedia registryMedia) {
		final MediaTypeListBox mediaBox = new MediaTypeListBox();
		mediaBox.setValue(registryMedia.getMedia());
		final AonTextBox valueBox = new AonTextBox();
		valueBox.setValue(registryMedia.getValue());
		valueBox.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				registryMedia.setValue(valueBox.getValue());
			}
		});
		final AonTextBox commentsBox = new AonTextBox();
		commentsBox.setValue(registryMedia.getComment());
		commentsBox.setVisibleLength(35);
		commentsBox.setMaxLength(64);
		commentsBox.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				registryMedia.setComment(commentsBox.getValue());
			}
		});
		final CheckBox admBox = new CheckBox();
		admBox.setValue(registryMedia.isAdministrative());
		admBox.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				registryMedia.setAdministrative(admBox.getValue());
			}
		});
		
		final CheckBox comBox = new CheckBox();
		comBox.setValue(registryMedia.isCommercial());
		comBox.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				registryMedia.setCommercial(comBox.getValue());
			}
		});
		final CheckBox tecBox = new CheckBox();
		tecBox.setValue(registryMedia.isTechnical());
		tecBox.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				registryMedia.setTechnical(tecBox.getValue());
			}
		});

		final AonTableButton deleteMediaButton = new AonTableButton( AON.MSG.deleteContact(),AON.CSS.aonIconDelete());
		final AonTableButton restoreMediaButton = new AonTableButton( AON.MSG.restoreAction(),AON.CSS.aonIconRestoreDeleted());
		
		deleteMediaButton.setVisible(!registryMedia.isRemoved());
		restoreMediaButton.setVisible(registryMedia.isRemoved());
		
		FlowPanel buttons = new FlowPanel();
		buttons.add(deleteMediaButton);
		buttons.add(restoreMediaButton);
		
		deleteMediaButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
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
			}
		});
		restoreMediaButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
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
			}
		});
		
		
		
		IMediaTypeVisitor mediaVisitor = new IMediaTypeVisitor() {
			@Override public void visitUnknown() { }
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
		mediaBox.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				registryMedia.setMedia(mediaBox.getValue());
				registryMedia.getMedia().visit( mediaVisitor );
			}
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
			scopable.setScope( options.getConfiguration().getAvailableScopes().get(0).getId() ); 
			final ListBox scopeBox = new ListBox();
			for (Scope scope : options.getConfiguration().getAvailableScopes()) {
				scopeBox.addItem(scope.getDescription(),AonNumberUtils.toString(scope.getId()));
			}
			scopeBox.addChangeHandler(new ChangeHandler() {
				
				@Override
				public void onChange(ChangeEvent event) {
					scopable.setScope(AonNumberUtils.toint(scopeBox.getSelectedValue()));
				}
			});
			addBasicRow(displayTab, new InlineLabel(AON.MSG.scope()),scopeBox);				
		}
	}

	protected void addAccountRow(AonDisplayTable displayTab,AonModuleOptions<?> options, IAccount<?> account) {
		Account acc = account.getAccount();
		final AonAccountBox accountBox = new AonAccountBox(options.getDomainName(),options.getDomain(),options.getUser());
		accountBox.setAccount(acc);
		accountBox.addSelectionHandler(new SelectionHandler<Account>() {
			
			@Override
			public void onSelection(SelectionEvent<Account> event) {
				Account a = event.getSelectedItem();
				account.setAccount(a);
			}
		});
		addBasicRow(displayTab, new InlineLabel(AON.MSG.account()), accountBox);				
	}

	protected void addExtended(AonModuleOptions<?> options, R registryFull, AonRegistryFullPanelCallback<R> callback) {
	}
	
}

