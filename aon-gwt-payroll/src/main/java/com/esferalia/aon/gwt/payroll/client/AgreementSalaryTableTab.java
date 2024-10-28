package com.esferalia.aon.gwt.payroll.client;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
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
import com.esferalia.aon.gwt.payroll.shared.SpecialExpresion;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
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
		String categoryWidth();
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
		String levelCell();
		String levelDefaultValue();
		String levelFixed();
		String modify();
		String oddRow();
		String overflowEllipsis();
		String textBox();
		String textCenter();
		String valueCell();
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
	
	private ListBox categoryLB;
	
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
		
		Label category = new Label("Categoria");
		category.addStyleName(style.gridTitle());
		category.addStyleName(style.textCenter());
		category.addStyleName(style.categoryWidth());
		category.addStyleName(style.headerFSize());
		salaryGrid.setWidget(row, 0, category);
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
			
			salaryGrid.getColumnFormatter().removeStyleName(col, style.widthAll());
			
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
			if(level.getId() != 0 && ((level.isDeleted() || (null != agreement.getSelectedLevel() && !level.getId().equals(agreement.getSelectedLevel().getId()))))) continue;
			
			int row = salaryGrid.insertRow(salaryGrid.getRowCount());
			
			Widget categoryCell;
			
			if(level.getId() == 0) {
				categoryLB = createCategoryLB();
				categoryLB.ensureDebugId("category_filter");
				categoryLB.getElement().getStyle().setHeight(1.7, Unit.EM);
				categoryLB.getElement().getStyle().setWidth(290, Unit.PX);
				categoryLB.getElement().getStyle().setBorderStyle(BorderStyle.NONE);
				
				setSelectedValueLB(categoryLB, null == agreement.getSelectedLevel() ? "" : String.valueOf(agreement.getSelectedLevel().getId()));
				categoryLB.setVisible(!agreement.getLevels().isEmpty());
				
				categoryLB.addChangeHandler(e -> filterSelectedCategory());
				
				categoryCell = categoryLB;
			} else {
				String levelCategories = getLevelCategories(level);
				categoryCell = new Label(levelCategories);
				categoryCell.setTitle(getLevelCategoriesTitle(level));
				categoryCell.addStyleName(style.overflowEllipsis());
				categoryCell.addStyleName(style.gridTitle());
				categoryCell.addStyleName(style.gridCell());
				categoryCell.addStyleName(style.levelCell());
			}
			
			salaryGrid.setWidget(row, 0, categoryCell);
			salaryGrid.getCellFormatter().addStyleName(row, 0, style.levelFixed());
			if(row % 2 == 0 ) salaryGrid.getCellFormatter().addStyleName(row, 0, style.oddRow());
			
			int col = 1;
			
			for(String variable : agreement.getVariablesByDate(selectedDate)) {
				LevelData levelData = agreement.getLevelData(level.getId(), variable, selectedDate);
				TextBox cell = new ExpressionBox();
				
				cell.ensureDebugId("textBox_" + variable + "_" + level.getDescription() );

				cell.addStyleName(style.gridCell());
				cell.addStyleName(style.valueCell());
				cell.addStyleName(style.textBox());
				cell.setValue(null == levelData ? null : levelData.getExpression());
				cell.setTitle("Valor nivel retributivo");
				
				if(row % 2 == 0 ) cell.addStyleName(style.oddRow());
				cell.getElement().getStyle().setBorderStyle(BorderStyle.NONE);
				if(null != levelData && AonStringUtils.isNotBlank(levelData.getExpression()) && SpecialExpresion.parse(levelData.getExpression()).getInput().length() > 16)
					cell.setWidth((7.5 * levelData.getExpression().length()) + "px");
				else
					cell.setWidth("95%");
				
				if(levelData != null && levelData.isModify())
					cell.addStyleName(style.modify());
				else
					cell.removeStyleName(style.modify());
				
				cell.addValueChangeHandler(event -> {
					
					String expression = event.getValue();
					String value = event.getValue();
					
					try {
						double expressionValue = evalExpression(expression);
						value = expressionValue + "";
					} catch (Exception e) {
						// TODO: handle exception
					}
					
					if(null == levelData || null == levelData.getId())
						agreement.createLevelData(level.getId(), variable, value, selectedDate);
					else
						agreement.updateLevelData(level.getId(), levelData.getId(), value);
					
					setAgreementSalaryTable(agreement);
					setHasChange(true);
				});
				
				// check if level 0 or default value
				if(level.getId() == 0 || null == levelData || AonStringUtils.isBlank(levelData.getExpression())) {
					LevelData levelDataDefault = agreement.getDefaultLevelData(variable, selectedDate);
					cell.setText(null == levelDataDefault ? null : SpecialExpresion.parse(levelDataDefault.getExpression()).getInput());
					cell.setTitle("Valor por defecto");
					cell.addStyleName(style.levelDefaultValue());
					
					if(null != levelDataDefault && AonStringUtils.isNotBlank(levelDataDefault.getExpression()) && cell.getText().length() > 20)
						cell.setWidth((7.5 * levelDataDefault.getExpression().length()) + "px");
					else
						cell.setWidth("95%");
				}
						
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
	
	public double evalExpression(String expression) {
	    return calculate(expression);
	}

	public final native double calculate(String expression) /*-{
	    return eval(expression);
	}-*/;
	
	private ListBox createCategoryLB() {
		Map<String, Integer> allCategories = new TreeMap<>();
		
		for(Level levelIT : agreement.getLevels()) {
			if(levelIT.getId() == 0) continue;
			Set<String> levelCategories = agreement.getCategoriesMap().get(levelIT.getId());
			Set<String> levelContracts = agreement.getContractsMap().get(levelIT.getId());
			if(null !=levelContracts && !levelContracts.isEmpty()) levelContracts.forEach(levelContract -> allCategories.put(levelIT.getDescription() + " - " + levelContract, levelIT.getId()));
			else if(levelCategories.size() > 1) levelCategories.forEach(category -> allCategories.put(category, levelIT.getId()));
			else if(levelCategories.size() == 1) {
				String category = (String)levelCategories.toArray()[0];
				RegExp regExp = RegExp.compile("Categoria|Nivel|categoria|nivel|CATEGORIA|NIVEL?");
				MatchResult matcher = regExp.exec(category);
				boolean matchFound = matcher != null;
				if(matchFound) allCategories.put(levelIT.getDescription(), levelIT.getId());
			    else  allCategories.put(category, levelIT.getId());
			}
		}
		
		ListBox listBox = new ListBox();
		listBox.addItem("Todos", "");
		listBox.addItem("Por defecto", "0");
		allCategories.entrySet().forEach(e -> listBox.addItem(e.getKey(), e.getValue().toString()));
		return listBox;
	}
	
	private String getLevelCategories(Level level) {
		StringBuilder categoriesBuilder = new StringBuilder();
		Set<String> levelContracts = agreement.getContractsMap().get(level.getId());
		if(null !=levelContracts && !levelContracts.isEmpty()) {
			for(String levelContract : levelContracts){
				if(AonStringUtils.isBlank(categoriesBuilder.toString()))
					categoriesBuilder.append(levelContract);
				else
					categoriesBuilder.append(", " + levelContract);
			}
			return level.getDescription() + " - " + categoriesBuilder.toString();
		} else if(agreement.getCategoriesMap().get(level.getId()).size() > 1) {
			for(String category : agreement.getCategoriesMap().get(level.getId())){
				if(AonStringUtils.isBlank(categoriesBuilder.toString()))
					categoriesBuilder.append(category);
				else
					categoriesBuilder.append(", " + category);
			}
			return categoriesBuilder.toString();
		} else if(agreement.getCategoriesMap().get(level.getId()).size() == 1) {
			String category = (String)agreement.getCategoriesMap().get(level.getId()).toArray()[0];
			RegExp regExp = RegExp.compile("Categoria|Nivel|categoria|nivel|CATEGORIA|NIVEL?");
			MatchResult matcher = regExp.exec(category);
			boolean matchFound = matcher != null;
			if(matchFound) return level.getDescription();
		    else return category;
		} else return null;
	}
	
	private String getLevelCategoriesTitle(Level level) {
		StringBuilder categoriesBuilder = new StringBuilder();
		Set<String> levelContracts = agreement.getContractsMap().get(level.getId());
		if(null !=levelContracts && !levelContracts.isEmpty()) {
			
			StringBuilder contractsBuilder = new StringBuilder();
			for(String levelContract : levelContracts){
				if(AonStringUtils.isBlank(contractsBuilder.toString()))
					contractsBuilder.append(levelContract);
				else
					contractsBuilder.append(", " + levelContract);
			}
			
			for(String category : agreement.getCategoriesMap().get(level.getId())){
				if(AonStringUtils.isBlank(categoriesBuilder.toString()))
					categoriesBuilder.append(category);
				else
					categoriesBuilder.append(", " + category);
			}
			
			return  "Nivel : " + level.getDescription() + "\nContratos : " + contractsBuilder.toString() + "\nCategorias : " + categoriesBuilder.toString();
			
		} if(agreement.getCategoriesMap().get(level.getId()).size() > 1) {
			for(String category : agreement.getCategoriesMap().get(level.getId())){
				if(AonStringUtils.isBlank(categoriesBuilder.toString()))
					categoriesBuilder.append(category);
				else
					categoriesBuilder.append(", " + category);
			}
			return "Nivel : " + level.getDescription() + "\nCategorias : " + categoriesBuilder.toString();
		} else if(agreement.getCategoriesMap().get(level.getId()).size() == 1) {
			String category = (String)agreement.getCategoriesMap().get(level.getId()).toArray()[0];
			RegExp regExp = RegExp.compile("Categoria|Nivel|categoria|nivel|CATEGORIA|NIVEL?");
			MatchResult matcher = regExp.exec(category);
			boolean matchFound = matcher != null;
			if(matchFound) return "Nivel : " + level.getDescription();
		    else return "Nivel : " + level.getDescription() + "\nCategoria : " + category;
		} else return null;
	}
	
	private void filterSelectedCategory() {
		String levelId = categoryLB.getSelectedValue();
		agreement.setSelectedLevel(AonStringUtils.isBlank(levelId) ? null : agreement.getLevelById(Integer.parseInt(categoryLB.getSelectedValue())));
		setAgreementSalaryTable(agreement);
	}

	private void salaryTableWidth() {
		salaryGrid.getColumnFormatter().setWidth(0, "120px");
	}
	
	private void setSelectedValueLB(ListBox lBox, String str) {
	    String text = str;
	    int indexToFind = 0;
	    for (int i = 0; i < lBox.getItemCount(); i++) {
	        if (AonStringUtils.equalsIgnoreCase(lBox.getValue(i), text)) {
	            indexToFind = i;
	            break;
	        }
	    }
	    lBox.setSelectedIndex(indexToFind);
	}
	
	// ------------------------------------------ toolbar

	private void createToolbar() {
		toolbar = new AonToolbar("Tabla Salarial");
		
		saveBtn = new AonToolbarSmallButton(AON.MSG.saveAction(), AON.CSS.aonIconSave());
		saveBtn.ensureDebugId("acceptSalaryTableButton");
		saveBtn.addClickHandler(e -> {
			showLoading("Guardando convenio " + toolbar.getTitle() + " ...");
			setHasChange(false);
			onSaved();
		});
		
		datesLB = new ListBox();
		datesLB.ensureDebugId("datesLB");
		datesLB.getElement().getStyle().setHeight(1.7, Unit.EM);
		
		newDateBtn = new AonToolbarSmallButton(AON.MSG.newAction() + " tramo", AON.CSS.aonIconAdd());
		newDateBtn.ensureDebugId("newSalaryTabButton");
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
		deleteDateBtn.ensureDebugId("deleteSalaryTabButton");
		deleteDateBtn.addClickHandler(e -> {
			AonDialog deleteDialog = new AonDialog("Borrar tramo", new HTMLPanel("\u00bfDesea realmente eliminar el tramo <b>" + datesLB.getSelectedValue() +"</b> de la tabla salarial\u003f"));
			deleteDialog.ensureDebugId("deleteDateDialog");
			deleteDialog.setGlassStyleName(style.dialogGlass());
			deleteDialog.addStyleName(style.dialogZIndex());
			deleteDialog.confirm(new AonAcceptDialogCallback() {
				
				@Override
				public void onCancel() {
				}
				
				@Override
				public void onAccept() {
					Date selectedDate = formatDate.parse(datesLB.getSelectedValue());
					agreement.deletePeriod(selectedDate);
					deleteDialog.hide(true);
					setAgreementSalaryTable(agreement);
					showLoading("Guardando convenio " + toolbar.getTitle() + " ...");
					setHasChange(false);
					onSaved();
				}
			});
		});
		
		variablesVisivility = new AonToolbarSmallButton("Mostrar/Ocultar variables", AON.CSS.aonIconVisibility());
		variablesVisivility.addClickHandler(click -> {
			Date selectedDate = formatDate.parse(datesLB.getSelectedValue());
			AgreementVariablesDialog dialog = new AgreementVariablesDialog(agreement.getAllVariables(), agreement.getVariablesByDate(selectedDate), null) {
				
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

				@Override
				protected void onDelete(Set<String> deleteVariables) {
					// TODO Auto-generated method stub
					
				}

				@Override
				protected void onCreateVariabel(String variablesType, String value) {
					// TODO Auto-generated method stub
					
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
