package com.esferalia.aon.gwt.payroll.client;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog.AonAcceptDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarSmallButton;
import com.esferalia.aon.gwt.payroll.shared.AgreementInfo;
import com.esferalia.aon.gwt.payroll.shared.AgreementInfo.Level;
import com.esferalia.aon.gwt.payroll.shared.AgreementInfo.LevelData;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DatePicker;

public abstract class AgreementSalaryTableTab extends ResizeComposite {
	
	// ------------------------------------------ UiBinder 

	private static AgreementSalaryTableTabUiBinder uiBinder = GWT.create(AgreementSalaryTableTabUiBinder.class);

	interface AgreementSalaryTableTabUiBinder extends UiBinder<Widget, AgreementSalaryTableTab> {}

	// ------------------------------------------ UiFields
	
	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {
		String cellWidth();
		String columnBorder();
		String datePickerPanel();
		String deleteFixed();
		String dialogGlass();
		String dialogZIndex();
		String gridCell();
		String gridTitle();
		String headerDeleteFixed();
		String headerFixed();
		String headerFSize();
		String headerLevelFixed();
		String levelFixed();
		String oddRow();
		String textCenter();
		String widthAll();
	}
	
	@UiField
	HTMLPanel messagePanel;
	
	@UiField(provided = true)
	AonToolbar toolbar;
	
	@UiField
	ScrollPanel salaryScrollPanel;
	
	@UiField
	Grid salaryGrid;
	
	@UiField
	HTMLPanel agreementSalaryTableMessage;
	
	// ------------------------------------------ Variables
	
	private DateTimeFormat formatDate = DateTimeFormat.getFormat("dd/MM/yyyy");
	private AgreementInfo agreement;
	
	private boolean hasChange = false;
	
	private AonToolbarSmallButton saveBtn;
	private AonToolbarSmallButton newDateBtn;
	private ListBox datesLB;
	private AonToolbarSmallButton deleteDateBtn;
	private AonToolbarSmallButton variablesVisivility;
	
	// ------------------------------------------ Constructor

	protected AgreementSalaryTableTab() {
		createToolbar();
		initWidget(uiBinder.createAndBindUi(this));
	}

	// ------------------------------------------ setAgreementPreview
	
	public void setAgreementSalaryTable(AgreementInfo agreementIn) {
		agreement = agreementIn;
		hideMessage();
		showSalaryTable();
		toolbar.setTitle(agreement.getDescription());
		fillDatesLB();
		createSalaryTable();
	}

	private void fillDatesLB() {
		datesLB.clear();
		agreement.getSortedDates().forEach(date -> datesLB.addItem(formatDate.format(date), formatDate.format(date)));
		datesLB.setSelectedIndex(0);
	}

	// ------------------------------------------ salaryTable
	
	private void createSalaryTable() {
		if(agreement.getSortedDates().isEmpty())
			showDatesPanel();
		else {
			showSalaryTable();
			salaryScrollPanel.setHeight((Window.getClientHeight() - 280) + "px");
			getSalaryTableHeader();
			fillSalaryTable();
			salaryTableWidth();
		}
	}
	
	private void getSalaryTableHeader() {
		Date selectedDate = formatDate.parse(datesLB.getSelectedValue());
		
		salaryGrid.clear();
		salaryGrid.resize(0, agreement.getVariablesByDate(selectedDate).size()+3);
		int row = salaryGrid.insertRow(salaryGrid.getRowCount());
		
		Label level = new Label("Nivel");
		level.addStyleName(style.gridTitle());
		level.addStyleName(style.textCenter());
		level.addStyleName(style.cellWidth());
		level.addStyleName(style.headerFSize());
		salaryGrid.setWidget(row, 0, level);
		salaryGrid.getCellFormatter().addStyleName(row, 0, style.headerLevelFixed());
		salaryGrid.getColumnFormatter().addStyleName(0, style.columnBorder());
		
		int col = 1;
		
		for(String variable : agreement.getVariablesByDate(selectedDate)) {
			Label label = new Label(variable);
			label.addStyleName(style.gridTitle());
			label.addStyleName(style.textCenter());
			label.addStyleName(style.cellWidth());
			label.addStyleName(style.headerFSize());
			salaryGrid.setWidget(row, col, label);
			salaryGrid.getColumnFormatter().setWidth(col, "120px");
			salaryGrid.getColumnFormatter().addStyleName(col, style.columnBorder());
			salaryGrid.getCellFormatter().addStyleName(row, col, style.headerFixed());
			
			col++;
		}
		
		Label emptyCell = new Label("");
		emptyCell.addStyleName(style.widthAll());
		salaryGrid.setWidget(row, col, emptyCell);
		salaryGrid.getCellFormatter().addStyleName(row, col, style.headerFixed());
		salaryGrid.getColumnFormatter().addStyleName(col, style.widthAll());
		
		col++;
		
		Label deleteCell = new Label("");
		salaryGrid.setWidget(row, col, deleteCell);
		salaryGrid.getCellFormatter().addStyleName(row, col, style.headerDeleteFixed());
	}

	private void fillSalaryTable() {
		Date selectedDate = formatDate.parse(datesLB.getSelectedValue());
		
		for(Level level : agreement.getLevels()) {
			if(level.isDeleted()) continue;
			
			int row = salaryGrid.insertRow(salaryGrid.getRowCount());
			
			Label levelCell = new Label(level.getDescription());
			levelCell.addStyleName(style.gridTitle());
			levelCell.addStyleName(style.gridCell());
			levelCell.addStyleName(style.cellWidth());
			
			salaryGrid.setWidget(row, 0, levelCell);
			salaryGrid.getCellFormatter().addStyleName(row, 0, style.levelFixed());
			if(row % 2 == 0 ) salaryGrid.getCellFormatter().addStyleName(row, 0, style.oddRow());
			
			int col = 1;
			
			for(String variable : agreement.getVariablesByDate(selectedDate)) {
				LevelData levelData = agreement.getLevelData(level.getId(), variable, selectedDate);
				TextBox cell = new ExpressionBox();
				cell.addStyleName(style.gridCell());
				cell.setValue(null == levelData ? null : levelData.getExpression());
				cell.addStyleName(style.cellWidth());
				if(row % 2 == 0 ) cell.addStyleName(style.oddRow());
				cell.getElement().getStyle().setBorderStyle(BorderStyle.NONE);
				cell.addValueChangeHandler(event -> {
					if(null == levelData || null == levelData.getId())
						agreement.createLevelData(level.getId(), variable, event.getValue(), selectedDate);
					else
						agreement.updateLevelData(level.getId(), levelData.getId(), event.getValue());
					
					setAgreementSalaryTable(agreement);
					setHasChange(true);
				});
						
				salaryGrid.setWidget(row, col, cell);
				if(row % 2 == 0 ) salaryGrid.getCellFormatter().addStyleName(row, col, style.oddRow());
				col++;
			}
			
			Label emptyCell = new Label("");
			salaryGrid.setWidget(row, col, emptyCell);
			if(row % 2 == 0 ) salaryGrid.getCellFormatter().addStyleName(row, col, style.oddRow());
			
			col++;
			
			AonToolbarSmallButton deleteBtn = new AonToolbarSmallButton(AON.MSG.deleteAction(), AON.CSS.aonIconDelete());
			deleteBtn.addClickHandler(event -> {
				AonDialog deleteDialog = new AonDialog("Borrar nivel", new HTMLPanel("\u00bfDesea realmente eliminar el nivel <b>" + level.getDescription() +"</b>\u003f"));
				deleteDialog.setGlassStyleName(style.dialogGlass());
				deleteDialog.addStyleName(style.dialogZIndex());
				deleteDialog.confirm(new AonAcceptDialogCallback() {
					
					@Override
					public void onCancel() {
						// Not use here
					}
					
					@Override
					public void onAccept() {
						agreement.deleteLevel(level.getId());
						createSalaryTable();
						setHasChange(true);
					}
				});
			});
			
			salaryGrid.setWidget(row, col, level.getId() == 0 ? new Label("") : deleteBtn);
			salaryGrid.getCellFormatter().addStyleName(row, col, style.deleteFixed());
			if(row % 2 == 0 ) salaryGrid.getCellFormatter().addStyleName(row, col, style.oddRow());
		}
		
	}

	private void salaryTableWidth() {
		salaryGrid.getColumnFormatter().setWidth(0, "120px");
	}
	
	// ------------------------------------------ toolbar

	private void createToolbar() {
		toolbar = new AonToolbar("Tabla Salarial");
		
		saveBtn = new AonToolbarSmallButton(AON.MSG.saveAction(), AON.CSS.aonIconSave());
		saveBtn.addClickHandler(e -> {
			showLoading("Guardando convenio " + toolbar.getTitle() + " ...");
			setHasChange(false);
			onSaved();
		});
		
		datesLB = new ListBox();
		datesLB.getElement().getStyle().setHeight(1.7, Unit.EM);
		
		newDateBtn = new AonToolbarSmallButton(AON.MSG.newAction() + " tramo", AON.CSS.aonIconAdd());
		newDateBtn.addClickHandler(e -> {
			PopupPanel popup = new PopupPanel(true); // auto-hide
			DatePicker picker = new DatePicker();
			picker.setValue(new Date());
			picker.setYearAndMonthDropdownVisible(true);
			
			picker.addValueChangeHandler(event -> {
				popup.hide();
				Date newPeriod = event.getValue();
				if(agreement.getSortedDates().isEmpty()) {
					agreement.createNewPeriod(newPeriod);
					agreement.setFilteredAllVariables();
					setAgreementSalaryTable(agreement);
					setHasChange(true);
				} else {
					Date maxDate = agreement.getSortedDates().stream().findFirst().get();
					if(maxDate.after(newPeriod) || maxDate.equals(newPeriod))
						showWarning("Error fechas", "No se puede seleccionar un fecha anterior o igual al ultimo tramo existente");
					else {
						agreement.createNewPeriod(newPeriod);
						setAgreementSalaryTable(agreement);
						setHasChange(true);
					}
				}
			});
			
			popup.setWidget(picker);
			popup.setStyleName(style.datePickerPanel());
			popup.showRelativeTo(newDateBtn);

			popup.ensureDebugId("morePopupPanel");
			picker.ensureDebugId("moreDatePicker");
		});
		
		deleteDateBtn = new AonToolbarSmallButton(AON.MSG.deleteAction() + " tramo", AON.CSS.aonIconDelete());
		deleteDateBtn.addClickHandler(e -> {
			AonDialog deleteDialog = new AonDialog("Borrar tramo", new HTMLPanel("\u00bfDesea realmente eliminar el tramo <b>" + datesLB.getSelectedValue() +"</b> de la tabla salarial\u003f"));
			deleteDialog.setGlassStyleName(style.dialogGlass());
			deleteDialog.addStyleName(style.dialogZIndex());
			deleteDialog.confirm(new AonAcceptDialogCallback() {
				
				@Override
				public void onCancel() {
					// Not use here
				}
				
				@Override
				public void onAccept() {
					Date selectedDate = formatDate.parse(datesLB.getSelectedValue());
					agreement.deletePeriod(selectedDate);
					setAgreementSalaryTable(agreement);
					setHasChange(true);
				}
			});
		});
		
		variablesVisivility = new AonToolbarSmallButton("Mostrar/Ocultar variables", AON.CSS.aonIconVisibility());
		variablesVisivility.addClickHandler(click -> {
			Date selectedDate = formatDate.parse(datesLB.getSelectedValue());
			AgreementVariablesDialog dialog = new AgreementVariablesDialog(agreement.getAllVariables(), agreement.getVariablesByDate(selectedDate)) {
				
				@Override
				protected void onAccept(String variablesType, Set<String> variables) {
					switch (variablesType) {
						case "VALUES":
							agreement.setFilteredValuesVariables();
							break;
						case "NO_VALUES":
							agreement.setFilteredNoValuesVariables();
							break;
						case "ALL":
							agreement.setFilteredAllVariables();
							break;
						default:
							agreement.setFilteredVariables(variables);
							break;
					}
					createSalaryTable();	
				}
			};
			dialog.setGlassStyleName(style.dialogGlass());
			dialog.addStyleName(style.dialogZIndex());
			dialog.setShowVariables(agreement.getShownVariables());
		});
		
		datesLB.addChangeHandler(e -> createSalaryTable());
		
		toolbar.add(saveBtn);
		toolbar.add(newDateBtn);
		toolbar.add(datesLB);
		toolbar.add(deleteDateBtn);
		toolbar.add(variablesVisivility);
	}
	
	// ------------------------------------------ Dates controler
	
	private void showSalaryTable() {
		salaryScrollPanel.getElement().getStyle().clearDisplay();
		agreementSalaryTableMessage.getElement().getStyle().setDisplay(Display.NONE);
		newDateBtn.setVisible(true);
		datesLB.setVisible(true);
		deleteDateBtn.setVisible(true);
		variablesVisivility.setVisible(true);
	}
	
	private void showDatesPanel() {
		agreementSalaryTableMessage.getElement().getStyle().clearDisplay();
		salaryScrollPanel.getElement().getStyle().setDisplay(Display.NONE);
		newDateBtn.setVisible(true);
		datesLB.setVisible(false);
		deleteDateBtn.setVisible(false);
		variablesVisivility.setVisible(false);
	}
	
	// ------------------------------------------ HasChange
	
	public boolean hasChange() {
		return hasChange;
	}

	public void setHasChange(boolean hasChange) {
		this.hasChange = hasChange;
		saveBtn.setEnabled(hasChange());
		if(!hasChange()) {
			saveBtn.getElement().getStyle().setDisplay(Display.BLOCK);
			saveBtn.getElement().getStyle().setVisibility(Visibility.VISIBLE);
		}
	}
	
	// ------------------------------------------ Abstract methods
	
	public abstract void onSaved();
	public abstract void getVariables(AgreementInfo agreement, Consumer<AgreementInfo> success);
	
	// ------------------------------------------------- Aon Messages panel

	public void showSuccess(String title, String message) {
		Map<String, String> successMap = new HashMap<>();
		successMap.put(title, message);
		AonMessagePanel.showSuccess(messagePanel, successMap);
	}

	public void showError(String title, String message) {
		Map<String, String> errorMap = new HashMap<>();
		errorMap.put(title, message);
		AonMessagePanel.showError(messagePanel, errorMap);
	}
	
	private void showWarning(String title, String message) {
		Map<String, String> warningMap = new HashMap<>();
		warningMap.put(title, message);
		AonMessagePanel.showWarning(messagePanel, warningMap);
	}

	public void showLoading(String message) {
		AonMessagePanel.showLoading(messagePanel, message);
	}

	private void hideMessage() {
		AonMessagePanel.hideMessage(messagePanel);
	}
	
}
