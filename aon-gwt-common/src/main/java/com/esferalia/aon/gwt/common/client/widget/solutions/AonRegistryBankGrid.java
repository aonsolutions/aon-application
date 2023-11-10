package com.esferalia.aon.gwt.common.client.widget.solutions;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.occam.api.model.registry.RegistryBank;
import com.esferalia.aon.occam.api.model.registry.RegistryFull;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;

public class AonRegistryBankGrid extends AonDisplayGrid {
	
	private RegistryFull<?> registryFull;
	private FlowPanel rootPanel;
	
	/**
	 * The constructor
	 * @param options module options
	 * @param registryFull the registry with the banks
	 * @param callback
	 * @param rootPanel
	 */
	public AonRegistryBankGrid(RegistryFull<?> registryFull, FlowPanel rootPanel) {
		this.registryFull = registryFull;
		this.rootPanel = rootPanel;
	}

	/**
	 * Shows the banks of a registry
	 */
	public void getBanksGrid() {
		FlowPanel labelContainer = new FlowPanel();
		labelContainer.setStyleName(AON.CSS.aonFlexBlock());
		labelContainer.addStyleName(AON.CSS.aonBorderBottom());

		Label banksLabel = new Label(AON.MSG.banks());
		banksLabel.addStyleName(AON.CSS.aonFlexGrow1());
		banksLabel.addStyleName(AON.CSS.aonBold());
		labelContainer.add(banksLabel);
		
		AonTableButton addBank = new AonTableButton( AON.MSG.addBank(), AON.CSS.aonIconAdd() );
		rootPanel.add(labelContainer);
		
		this.addStyleName(AON.CSS.aonWidthAlmostAll());
		this.addStyleName(AON.CSS.aonBlockCenter());
		rootPanel.add(this);
		
		addBank.addClickHandler(event -> {
			if (registryFull.getBanks() == null || registryFull.getBanks().isEmpty()) {
				paintBankHeader(this);		
			}
			RegistryBank registryBank = new RegistryBank();
			registryFull.addBank(registryBank);
			paintRow(this, registryBank);
		});
		
		labelContainer.add(addBank);

		if (registryFull.getBanks() != null) {
			if (!registryFull.getBanks().isEmpty()) {
				paintBankHeader(this);		
			}
			for (RegistryBank registryBank: registryFull.getBanks()) {
				paintRow(this, registryBank);
			}
		}
	}
	
	/**
	 * Adds the cells of the bank table
	 * @param displayTab
	 */
	private void paintBankHeader(AonRegistryBankGrid displayTab) {
		displayTab.addHeaderRow()
			.addCell(new InlineLabel(AON.MSG.bankAccount()))
			.addCell(new InlineLabel(AON.MSG.description()))
			.addCell(new InlineLabel("Bic / Swift"))
			.addCell(new InlineLabel("Activo"));
	}
	
	/**
	 * Paint one bank
	 * @param displayTab
	 * @param registryAddress
	 */
	private void paintRow(AonRegistryBankGrid displayTab, RegistryBank registryBank) {
		
		// ***************************************************************** [BANK ACCOUNT]		
		final AonBankAccountBox aonBankAccountBox = new AonBankAccountBox();
		aonBankAccountBox.setWidth("250px");
		aonBankAccountBox.setValue(registryBank.getBankAccount());
		aonBankAccountBox.addValueChangeHandler(event -> registryBank.setBankAccount(aonBankAccountBox.getValue()));
		
		// ***************************************************************** [DESCRIPTION]		
		final AonTextBox descriptionText = new AonTextBox();
		descriptionText.setValue(registryBank.getAlias());
		descriptionText.setVisibleLength(20);
		descriptionText.setMaxLength(25);
		descriptionText.addValueChangeHandler(event -> registryBank.setAlias(descriptionText.getValue()));

		// ***************************************************************** [BIC / SWIFT]		
		final AonTextBox bicText = new AonTextBox();
		bicText.setValue(registryBank.getBic());
		bicText.setVisibleLength(10);
		bicText.setMaxLength(15);
		bicText.addValueChangeHandler(event -> registryBank.setBic(bicText.getValue()));
		
		// ***************************************************************** [ACTIVE]	
		final CheckBox activeBox = new CheckBox();
		activeBox.setValue(registryBank.isActive());
		activeBox.addClickHandler(event -> registryBank.setActive(activeBox.getValue()));
		
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
		});
		
		restoreBankButton.addClickHandler(event -> {
			registryBank.setRemoved(false);
			restoreBankButton.setVisible(false);
			deleteBankButton.setVisible(true);
			aonBankAccountBox.removeStyleName(AON.CSS.aonTextLineThrough());
			aonBankAccountBox.setEnabled(true);
		});
		
		displayTab.addHeaderRow()
			.addCell(aonBankAccountBox)
			.addCell(descriptionText)
			.addCell(bicText)
			.addCell(activeBox)
			.addCell(buttons);
	}
}
