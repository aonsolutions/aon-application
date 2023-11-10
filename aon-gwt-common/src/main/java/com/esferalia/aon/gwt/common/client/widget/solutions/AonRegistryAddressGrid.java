package com.esferalia.aon.gwt.common.client.widget.solutions;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.StreetTypeListBox;
import com.esferalia.aon.occam.api.model.GeoZone;
import com.esferalia.aon.occam.api.model.registry.RegistryAddress;
import com.esferalia.aon.occam.api.model.registry.RegistryFull;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TabLayoutPanel;

public class AonRegistryAddressGrid extends AonDisplayGrid {

	private RegistryFull<?> registryFull;
	private AonModuleOptions<?> options;
	private TabLayoutPanel panel;
	
	/**
	 * The constructor
	 * @param options module options
	 * @param registryFull the registry with the addresses
	 * @param rootPanel
	 */
	public AonRegistryAddressGrid(RegistryFull<?> registryFull, AonModuleOptions<?> options, TabLayoutPanel panel) {
		this.registryFull = registryFull;
		this.options = options;
		this.panel = panel;
	}
	
	/**
	 * Shows the address of a registry
	 */
	public void getAddressesGrid() {
		FlowPanel labelContainer = new FlowPanel();
		labelContainer.setStyleName(AON.CSS.aonFlexBlock());
		labelContainer.addStyleName(AON.CSS.aonBorderBottom());

		Label addressesLabel = new Label(AON.MSG.addresses());
		addressesLabel.addStyleName(AON.CSS.aonFlexGrow1());
		addressesLabel.addStyleName(AON.CSS.aonBold());
		labelContainer.add(addressesLabel);
		
		AonTableButton addAddress = new AonTableButton( AON.MSG.addAddress(), AON.CSS.aonIconAdd() );
		panel.add(labelContainer);
		
		this.addStyleName(AON.CSS.aonWidthAlmostAll());
		this.addStyleName(AON.CSS.aonBlockCenter());
		panel.add(this);
		
		addAddress.addClickHandler(event -> {
			if ( registryFull.getAddresses() == null || registryFull.getAddresses().isEmpty()) {
				paintAddressHeader(this);		
			}
			RegistryAddress registryAddress = new RegistryAddress();
			registryAddress.setMain( registryFull.getAddresses() == null || registryFull.getAddresses().isEmpty() )
				.setDirty(false);
			registryFull.addAddress(registryAddress);
			paintRow(this, options, registryAddress );
		});
		labelContainer.add(addAddress);

		if ( registryFull.getAddresses() != null) {
			if ( !registryFull.getAddresses().isEmpty()) {
				paintAddressHeader(this);		
			}
			for (RegistryAddress registryAddress: registryFull.getAddresses() ) {
				paintRow(this, options, registryAddress);
			}
		}
	}

	/**
	 * Adds the cells of the address table
	 * @param displayTab
	 */
	private void paintAddressHeader(AonRegistryAddressGrid displayTab) {
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
	 * @param registryAddress
	 */
	private void paintRow(AonRegistryAddressGrid displayTab, AonModuleOptions<?> options, RegistryAddress registryAddress) {
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
}
