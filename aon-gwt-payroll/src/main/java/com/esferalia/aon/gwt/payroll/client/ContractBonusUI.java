package com.esferalia.aon.gwt.payroll.client;

import static com.esferalia.aon.gwt.payroll.client.SSBonusDraft.getFullDescription;

import java.util.Arrays;
import java.util.Date;
import java.util.stream.Collectors;

import com.esferalia.aon.gwt.payroll.shared.EmployeeContractInfo;
import com.esferalia.aon.gwt.payroll.shared.Messages;
import com.esferalia.aon.gwt.payroll.shared.SSBonusData;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public abstract class ContractBonusUI extends ResizeComposite {

	// -------------------------------------------------- UiBinder --------------------------------------------------

	private static ContractAttachUIBinder uiBinder = GWT.create(ContractAttachUIBinder.class);

	interface ContractAttachUIBinder extends UiBinder<Widget, ContractBonusUI> {}

	// -------------------------------------------------- UiFields --------------------------------------------------

	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {
		String headerLabelStyle();
		String maxWidthTB();
		String maxWidthLB();
		String clauseTD();
	}

	@UiField
	VerticalPanel contractBonusTable;

	@UiField
	Grid contractBonusDataTableHeader;
	
	@UiField
	ScrollPanel contractBonusScrollPanel;
	
	@UiField
	DeckPanel deckPanel;
	
	@UiField
	Grid contractBonusDataTable;

	@UiField
	Label messageLabel;
	
	// ------------------------------------------------------ Constructor ---------------------------------------------------------

	private DateTimeFormat formatFullDate = DateTimeFormat.getFormat("dd/MM/yyyy");
	private EmployeeContractInfo employeeContractInfo;
	private Messages messages;
	
	public ContractBonusUI() {
		initWidget(uiBinder.createAndBindUi(this));
		messages = new Messages();
		initializeView();
		deckPanel.showWidget(0);
	}
	
	public void setEmployeeContractInfo(EmployeeContractInfo employeeContractInfoIn) {
		this.employeeContractInfo = employeeContractInfoIn;
		resetAttachDataTableStructure();
		
		if(employeeContractInfo.getContractBonus().size() > 0)
			deckPanel.showWidget(1);
		
		for(SSBonusData contractBonus : employeeContractInfo.getContractBonus())
			paintContractBonus(contractBonus);
		
		messageLabel.setText("Bonificaciones actualizadas a " + formatFullDate.format(new Date()));
	}
	
	// --------------------------------------------------- UiHandlers (Aux Methods) -------------------------------------------------
	
	private void resetAttachDataTableStructure() {
		contractBonusDataTable.clear();
		contractBonusDataTable.resize(0, 0);
		contractBonusDataTable.resizeColumns(3);
		
		setColumnsWidth();
	}
	
	// -------------------------------------------------- Paint Table Header Methods --------------------------------------------------
	
	private void paintHeaderContractBonusTable() {
		int row = contractBonusDataTableHeader.insertRow(contractBonusDataTableHeader.getRowCount());
		
		Label startDate = new Label("FECHA INICIO");
		Label endDate = new Label("FECHA FIN");
		Label description = new Label("DESCRIPCI" + String.valueOf("\u00D3") + "N");
		
		description.addStyleName(style.headerLabelStyle());
		startDate.addStyleName(style.headerLabelStyle());
		endDate.addStyleName(style.headerLabelStyle());
		startDate.getElement().getStyle().setPaddingLeft(10, Unit.PX);
		
		contractBonusDataTableHeader.setWidget(row, 0, startDate);
		contractBonusDataTableHeader.setWidget(row, 1, endDate);
		contractBonusDataTableHeader.setWidget(row, 2, description);
	}
	
	// ------------------------------------------------------ Paint Table Methods -----------------------------------------------------

	private void paintContractBonus(SSBonusData ssBonusData) {
		// Insert new row
		int row = this.contractBonusDataTable.insertRow(contractBonusDataTable.getRowCount());
		
		// Description TextBox
		Label startDateLabel = new Label(formatFullDate.format(ssBonusData.getStartDate()));
		startDateLabel.getElement().getStyle().setPaddingLeft(10, Unit.PX);
		
		// Description TextBox
		Label endDateLabel = new Label(null == ssBonusData.getEndDate() ? "" : formatFullDate.format(ssBonusData.getEndDate()));
		
		// Description TextBox
		Label descriptionLabel = new Label(getFullDescription(ssBonusData));
		
		//Add to table
		contractBonusDataTable.setWidget(row, 0, startDateLabel);
		contractBonusDataTable.setWidget(row, 1, endDateLabel);
		contractBonusDataTable.setWidget(row, 2, descriptionLabel);
	}	
	
	// -------------------------------------------------- Paint Table Auxiliar Methods ---------------------------------------------------

	protected abstract void fireMessagesResults(Messages messages);
	
	// ------------------------------------------------------ Auxiliar Methods ----------------------------------------------------
	
	private void initializeView() {
		initAttachmentsTable();
		paintHeaderContractBonusTable();
		
		setScrollPanelsHeight();
		setColumnsWidth();
	}
	
	private void initAttachmentsTable() {
		contractBonusDataTableHeader.clear();
		contractBonusDataTableHeader.resize(0, 0);
		contractBonusDataTableHeader.resizeColumns(3);
		contractBonusDataTable.clear();
		contractBonusDataTable.resize(0, 0);
		contractBonusDataTable.resizeColumns(3);
	}
	
	private void setScrollPanelsHeight() {
		contractBonusScrollPanel.setHeight((Window.getClientHeight() - 390) + "px");
	}
	
	private void setColumnsWidth() {
		contractBonusDataTableHeader.getCellFormatter().getElement(0, 0).getStyle().setWidth(150, Unit.PX);
		contractBonusDataTableHeader.getCellFormatter().getElement(0, 1).getStyle().setWidth(150, Unit.PX);
		contractBonusDataTableHeader.getCellFormatter().getElement(0, 2).getStyle().setWidth(650, Unit.PX);
		
		contractBonusDataTable.getColumnFormatter().getElement(0).getStyle().setWidth(150, Unit.PX);
		contractBonusDataTable.getColumnFormatter().getElement(1).getStyle().setWidth(150, Unit.PX);
		contractBonusDataTable.getColumnFormatter().getElement(2).getStyle().setWidth(650, Unit.PX);
	}

}
