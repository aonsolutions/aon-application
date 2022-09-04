package com.esferalia.aon.gwt.payroll.client;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
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
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
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
		String gridTitle();
		String gridCell();
		String textCenter();
		String headerSticky();
		String levelHeaderSticky();
		String levelSticky();
		String deleteHeaderSticky();
		String deleteSticky();
		String headerColor();
		String columnBorder();
		String cellWidth();
		String headerFSize();
		String widthAll();
		String datePickerPanel();
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
	
	@UiField
	HTMLPanel agreementLevelMessage;
	
	// ------------------------------------------ Variables
	
	private DateTimeFormat formatDate = DateTimeFormat.getFormat("dd/MM/yyyy");
	private AgreementInfo agreement;
	
	private AonToolbarSmallButton categoriesBtn;
	private AonToolbarSmallButton salaryTableBtn;
	private AonToolbarSmallButton newLevelBtn;
	private AonToolbarSmallButton newDateBtn;
	private ListBox datesLB;
	private AonToolbarSmallButton deleteDateBtn;
	private AonToolbarSmallButton variablesVisivility;
	
	private boolean isSalaryTableSelected = true;
	
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
		if (isSalaryTableSelected && !agreement.getSortedDates().isEmpty()) createSalaryTable();
		else createCategoryTable();
		
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
		salaryGrid.getCellFormatter().addStyleName(row, 0, style.levelHeaderSticky());
		
		int col = 1;
		
		for(String variable : agreement.getVariablesByDate(selectedDate)) {
			Label label = new Label(variable);
			label.addStyleName(style.gridTitle());
			label.addStyleName(style.textCenter());
			label.addStyleName(style.cellWidth());
			label.addStyleName(style.headerFSize());
			salaryGrid.setWidget(row, col, label);
			salaryGrid.getColumnFormatter().setWidth(col, "120px");
			
			col++;
		}
		
		Label emptyCell = new Label("");
		emptyCell.addStyleName(style.widthAll());
		salaryGrid.setWidget(row, col, emptyCell);
		salaryGrid.getColumnFormatter().addStyleName(col, style.widthAll());
		
		col++;
		
		Label deleteCell = new Label("");
		salaryGrid.setWidget(row, col, deleteCell);
		salaryGrid.getCellFormatter().addStyleName(row, col, style.deleteHeaderSticky());
		
		salaryGrid.getRowFormatter().addStyleName(row, style.headerSticky());
		
	}

	private void fillSalaryTable() {
		Date selectedDate = formatDate.parse(datesLB.getSelectedValue());
		
		for(Entry<Integer, Set<LevelData>> e : agreement.getLevelDatasMap().entrySet()) {
			Level level = agreement.getLevelById(e.getKey());
			if(level.isDeleted()) continue;
			
			int row = salaryGrid.insertRow(salaryGrid.getRowCount());
			
			Label levelCell = new Label(level.getDescription());
			levelCell.addStyleName(style.gridTitle());
			levelCell.addStyleName(style.gridCell());
			levelCell.addStyleName(style.cellWidth());
			
			salaryGrid.setWidget(row, 0, levelCell);
			salaryGrid.getCellFormatter().addStyleName(row, 0, style.levelSticky());
			
			int col = 1;
			
			for(String variable : agreement.getVariablesByDate(selectedDate)) {
				LevelData levelData = agreement.getLevelData(level.getId(), variable, selectedDate);
				TextBox cell = new ExpressionBox();
				cell.addStyleName(style.gridCell());
				cell.setValue(null == levelData ? null : levelData.getExpression());
				cell.addStyleName(style.cellWidth());
				cell.addValueChangeHandler(event -> {
					if(null == levelData || null == levelData.getId())
						agreement.createLevelData(level.getId(), variable, event.getValue(), selectedDate);
					else
						agreement.updateLevelData(level.getId(), levelData.getId(), event.getValue());
					
					setAgreementSalaryTable(agreement);
				});
						
				salaryGrid.setWidget(row, col, cell);
				col++;
			}
			
			Label emptyCell = new Label("");
			emptyCell.addStyleName(style.widthAll());
			salaryGrid.setWidget(row, col, emptyCell);
			
			col++;
			
			AonToolbarSmallButton deleteBtn = new AonToolbarSmallButton(AON.MSG.deleteAction(), AON.CSS.aonIconDelete());
			deleteBtn.addClickHandler(event -> {
				AonDialog deleteDialog = new AonDialog("Borrar nivel", new HTMLPanel("\u00bfDesea realmente eliminar el nivel <b>" + level.getDescription() +"</b>\u003f"));
				deleteDialog.confirm(new AonAcceptDialogCallback() {
					
					@Override
					public void onCancel() {
						// Not use here
					}
					
					@Override
					public void onAccept() {
						agreement.deleteLevel(level.getId());
						createSalaryTable();
					}
				});
			});
			
			salaryGrid.setWidget(row, col, level.getId() == 0 ? new Label("") : deleteBtn);
			salaryGrid.getCellFormatter().addStyleName(row, col, style.deleteSticky());
		}
		
	}

	private void salaryTableWidth() {
		salaryGrid.getColumnFormatter().setWidth(0, "100px");
	}
	
	// ------------------------------------------ categoryTable

	private void createCategoryTable() {
		if(agreement.getActiveLevels().size() <= 1)
			showLevelPanel();
		else {
			showCategoryTable();
			getCategoryTableHeader();
			fillCategoryTable();
			categoryTableWidth();
		}
	}

	private void getCategoryTableHeader() {
		salaryGrid.clear();
		salaryGrid.resize(0, 3);
		int row = salaryGrid.insertRow(salaryGrid.getRowCount());
		
		Label level = new Label("Nivel");
		level.addStyleName(style.gridTitle());
		level.addStyleName(style.cellWidth());
		level.addStyleName(style.textCenter());
		level.addStyleName(style.headerFSize());
		salaryGrid.setWidget(row, 0, level);
		
		Label category = new Label("Categoria");
		category.addStyleName(style.gridTitle());
		category.addStyleName(style.headerFSize());
		salaryGrid.setWidget(row, 1, category);
		
		Label delete = new Label("");
		delete.addStyleName(style.gridTitle());
		salaryGrid.setWidget(row, 2, delete);
		
		salaryGrid.getRowFormatter().addStyleName(row, style.headerSticky());
		salaryGrid.getRowFormatter().addStyleName(row, style.headerColor());
	}

	private void fillCategoryTable() {
		for(Entry<Integer, Set<String>> e : agreement.getCategoriesMap().entrySet()) {
			
			Level level = agreement.getLevelById(e.getKey());
			if(level.isDeleted() || level.getId() == 0) continue;
			
			int row = salaryGrid.insertRow(salaryGrid.getRowCount());
			
			Set<String> categories = e.getValue();
			StringBuilder categoriesBuilder = new StringBuilder();
			for(String category : categories){
				if(AonStringUtils.isBlank(categoriesBuilder.toString()))
					categoriesBuilder.append(category);
				else
					categoriesBuilder.append(", " + category);
			}
			
			Label levelCell = new Label(level.getDescription());
			levelCell.addStyleName(style.gridTitle());
			levelCell.addStyleName(style.cellWidth());
			levelCell.addStyleName(style.textCenter());
			
			TextBox categoryCell = new TextBox();
			categoryCell.setWidth("99%");
			categoryCell.setValue(categoriesBuilder.toString());
			categoryCell.setTitle("Categorias nivel " + level.getDescription());
			categoryCell.addValueChangeHandler(categoryValue -> {
				if(AonStringUtils.isNotBlank(categoryValue.getValue())) {
					agreement.getCategoriesMap().remove(level.getId());
					String[] categorySplit = AonStringUtils.split(categoryValue.getValue(), ',');
					for(int i = 0; i < categorySplit.length; i++)
						agreement.addCategory(level.getId(), categorySplit[i].trim());
				}
				
			});
			
			AonToolbarSmallButton deleteBtn = new AonToolbarSmallButton(AON.MSG.deleteAction(), AON.CSS.aonIconDelete());
			deleteBtn.addClickHandler(event -> {
				AonDialog deleteDialog = new AonDialog("Borrar nivel", new HTMLPanel("\u00bfDesea realmente eliminar el nivel <b>" + level.getDescription() +"</b>\u003f"));
				deleteDialog.confirm(new AonAcceptDialogCallback() {
					
					@Override
					public void onCancel() {
						// Not use here
					}
					
					@Override
					public void onAccept() {
						agreement.deleteLevel(level.getId());
						setAgreementSalaryTable(agreement);
					}
				});
			});
			
			salaryGrid.setWidget(row, 0, levelCell);
			salaryGrid.setWidget(row, 1, categoryCell);
			salaryGrid.setWidget(row, 2, deleteBtn);
		}
	}

	private void categoryTableWidth() {
		salaryGrid.setWidth("100%");
		salaryGrid.getColumnFormatter().setWidth(0, "20%");
		salaryGrid.getColumnFormatter().setWidth(1, "80%");
	}
	
	// ------------------------------------------ toolbar

	private void createToolbar() {
		toolbar = new AonToolbar("Tabla Salarial");
		
		AonToolbarSmallButton saveBtn = new AonToolbarSmallButton(AON.MSG.saveAction(), AON.CSS.aonIconSave());
		saveBtn.addClickHandler(e -> {
			showLoading("Guardando convenio " + toolbar.getTitle() + " ...");
			onSaved();
		});
		
		categoriesBtn = new AonToolbarSmallButton("Categorias", AON.CSS.aonIconList());
		salaryTableBtn = new AonToolbarSmallButton("Tabla salarial", AON.CSS.aonIconStatics());
		salaryTableBtn.setVisible(!isSalaryTableSelected);
		categoriesBtn.setVisible(isSalaryTableSelected);
		
		newLevelBtn = new AonToolbarSmallButton(AON.MSG.newAction() + " nivel/categoria", AON.CSS.aonIconAdd());
		newLevelBtn.addClickHandler(e -> {
			HorizontalPanel panel = new HorizontalPanel();
			Label description = new Label("Descripci\u00f3n: ");
			TextBox levelDescription = new TextBox();
			levelDescription.setWidth("100%");
			panel.setWidth("98%");
			panel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
			panel.add(description);
			panel.add(levelDescription);
			AonDialog dialog = new AonDialog("Nuevo nivel", panel);
			dialog.confirm(new AonAcceptDialogCallback() {
				
				@Override
				public void onCancel() {
					// Not use here
				}
				
				@Override
				public void onAccept() {
					agreement.createLevel(levelDescription.getValue());
					isSalaryTableSelected = false;
					setAgreementSalaryTable(agreement);
				}
			});
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
				} else {
					Date maxDate = agreement.getSortedDates().stream().findFirst().get();
					if(maxDate.after(newPeriod) || maxDate.equals(newPeriod))
						showWarning("Error fechas", "No se puede seleccionar un fecha anterior o igual al ultimo tramo existente");
					else {
						agreement.createNewPeriod(newPeriod);
						setAgreementSalaryTable(agreement);
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
			dialog.setShowVariables(agreement.getShownVariables());
		});
		
		categoriesBtn.addClickHandler(e -> {
			isSalaryTableSelected = false;
			createCategoryTable();
			categoriesBtn.setVisible(false);
			salaryTableBtn.setVisible(true);
		});
		
		salaryTableBtn.addClickHandler(e -> {
			isSalaryTableSelected = true;
			createSalaryTable();
			categoriesBtn.setVisible(true);
			salaryTableBtn.setVisible(false);
		});
		
		datesLB.addChangeHandler(e -> createSalaryTable());
		
		toolbar.add(saveBtn);
		toolbar.add(categoriesBtn);
		toolbar.add(salaryTableBtn);
		toolbar.add(newLevelBtn);
		toolbar.add(newDateBtn);
		toolbar.add(datesLB);
		toolbar.add(deleteDateBtn);
		toolbar.add(variablesVisivility);
	}
	
	// ------------------------------------------ Dates controler
	
	private void showSalaryTable() {
		salaryScrollPanel.getElement().getStyle().clearDisplay();
		agreementSalaryTableMessage.getElement().getStyle().setDisplay(Display.NONE);
		agreementLevelMessage.getElement().getStyle().setDisplay(Display.NONE);
		categoriesBtn.setVisible(true);
		salaryTableBtn.setVisible(false);
		newDateBtn.setVisible(true);
		datesLB.setVisible(true);
		deleteDateBtn.setVisible(true);
		variablesVisivility.setVisible(true);
		newLevelBtn.setVisible(false);
	}
	
	private void showCategoryTable() {
		salaryScrollPanel.getElement().getStyle().clearDisplay();
		agreementSalaryTableMessage.getElement().getStyle().setDisplay(Display.NONE);
		agreementLevelMessage.getElement().getStyle().setDisplay(Display.NONE);
		categoriesBtn.setVisible(false);
		salaryTableBtn.setVisible(true);
		newDateBtn.setVisible(false);
		datesLB.setVisible(false);
		deleteDateBtn.setVisible(false);
		variablesVisivility.setVisible(false);
		newLevelBtn.setVisible(true);
	}

	private void showDatesPanel() {
		agreementSalaryTableMessage.getElement().getStyle().clearDisplay();
		salaryScrollPanel.getElement().getStyle().setDisplay(Display.NONE);
		agreementLevelMessage.getElement().getStyle().setDisplay(Display.NONE);
		categoriesBtn.setVisible(true);
		salaryTableBtn.setVisible(false);
		newDateBtn.setVisible(true);
		datesLB.setVisible(false);
		deleteDateBtn.setVisible(false);
		variablesVisivility.setVisible(false);
		newLevelBtn.setVisible(false);
	}
	
	private void showLevelPanel() {
		agreementLevelMessage.getElement().getStyle().clearDisplay();
		agreementSalaryTableMessage.getElement().getStyle().setDisplay(Display.NONE);
		salaryScrollPanel.getElement().getStyle().setDisplay(Display.NONE);
		categoriesBtn.setVisible(false);
		salaryTableBtn.setVisible(false);
		newLevelBtn.setVisible(true);
		newDateBtn.setVisible(false);
		datesLB.setVisible(false);
		deleteDateBtn.setVisible(false);
		variablesVisivility.setVisible(false);
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
