package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarSmallButton;
import com.esferalia.aon.gwt.payroll.shared.AgreementInfo;
import com.esferalia.aon.gwt.payroll.shared.AgreementInfo.AgreementExtra;
import com.esferalia.aon.gwt.payroll.shared.AgreementInfo.AgreementOwner;
import com.esferalia.aon.gwt.payroll.shared.AgreementInfo.Level;
import com.esferalia.aon.gwt.payroll.shared.AgreementInfo.LevelData;
import com.esferalia.aon.gwt.payroll.shared.NumberVariable;
import com.esferalia.aon.gwt.payroll.shared.Payment;
import com.esferalia.aon.gwt.payroll.shared.SpecialExpresion;
import com.esferalia.aon.gwt.payroll.shared.StringVariable;
import com.esferalia.aon.gwt.payroll.shared.Variable;
import com.esferalia.aon.occam.api.model.aonsolutions.DomainUserRoles;
import com.esferalia.aon.occam.api.model.type.ContractType;
import com.esferalia.aon.occam.api.model.type.ContractType.ContractTypeRecord;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DeckLayoutPanel;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.ValueBoxBase.TextAlignment;
import com.google.gwt.user.client.ui.Widget;

import net.aonsolutions.gwt.pdfjs.client.FullViewer;

public abstract class AgreementPreview extends Composite {
	
	// ------------------------------------------ UiBinder 

	private static AgreementPreviewUiBinder uiBinder = GWT.create(AgreementPreviewUiBinder.class);

	interface AgreementPreviewUiBinder extends UiBinder<Widget, AgreementPreview> {}

	// ------------------------------------------ UiFields
	
	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {
		String categoryWidth();
		String cellWidth();
		String columnBorder();
		String dateNoSelected();
		String dateSelected();
		String datePanel();
		String dialogGlass();
		String dialogZIndex();
		String displayNone();
		String extraCellHeight();
		String flex();
		String gridCell();
		String gridTitle();
		String headerColor();
		String headerFixed();
		String headerFSize();
		String headerLevelFixed();
		String levelCell();
		String levelDefaultValue();
		String levelFixed();
		String modify();
		String oddRow();
		String overflowEllipsis();
		String textCenter();
		String valueCell();
		String widthAll();
	}
	
	@UiField
	HTMLPanel messagePanel;
	
	@UiField
	DeckLayoutPanel deckLayoutPanel;
	
	@UiField(provided = true)
	AonToolbar toolbar;
	
	@UiField
	TextBox description;
	
	@UiField
	TextBox ssNumber;
	
	@UiField
	HTMLPanel serviAgreementPanel;
	
	@UiField
	DisclosurePanel salaryDiscPanel;
	
	@UiField(provided = true)
	AonToolbarButton salaryDiscBtn;
	
	@UiField
	HTMLPanel salaryDiscPanelContent;
	
	@UiField
	HTMLPanel salaryTableButtons;
	
	@UiField
	HTMLPanel salaryTable;
	
	@UiField
	ScrollPanel salaryScrollPanel;
	
	@UiField
	Grid salaryGrid;
	
	@UiField
	HTMLPanel agreementSalaryTableMessage;
	
	@UiField
	HTMLPanel agreementLevelMessage;
	
	@UiField
	DisclosurePanel levelDiscPanel;
	
	@UiField(provided = true)
	AonToolbarButton levelDiscBtn;
	
	@UiField
	HTMLPanel levelDiscPanelContent;
	
	@UiField
	HTMLPanel levelTable;
	
	@UiField
	ScrollPanel levelScrollPanel;
	
	@UiField
	Grid levelGrid;
	
	@UiField
	DisclosurePanel paymentDiscPanel;
	
	@UiField(provided = true)
	AonToolbarButton paymentDiscBtn;
	
	@UiField
	HTMLPanel paymentDiscPanelContent;
	
	@UiField
	HTMLPanel paymentTable;
	
	@UiField
	ScrollPanel paymentScrollPanel;
	
	@UiField
	Grid paymentGrid;
	
	@UiField
	HTMLPanel agreementPaymentMessage;
	
	@UiField(provided = true)
	AonToolbar toolbarSimulator;
	
	@UiField
	FullViewer printPreviewViewer;
	
	// ------------------------------------------ Variables
	
	private DomainEnterprisesServiceAsync impl = DomainEnterprisesServiceAsync.newInstance();
	private DateTimeFormat formatDate = DateTimeFormat.getFormat("dd/MM/yyyy");
	private AgreementInfo agreement;
	
//	private ListBox levelLB;
	private ListBox categoryLB;
	
	private AonToolbarButton printPreviewButton;
	private AonToolbarButton serviAgreementUpdateButton;
	private AonToolbarButton agreementInfoButton;
	
	private boolean hasChange = false;
	private boolean workplaceView = false;
	private AonToolbarSmallButton saveBtn;
	
	private ListBox tc2ListBox;
	private ListBox levelListBox;
	private TextBox partialTextBox;
	private ListBox groupListBox;
	
	private ArrayList<Label> dateLabels;
	
	private Date selectedDate;
	
	private HandlerRegistration salaryOpenHandler;
	private HandlerRegistration paymentOpenHandler;
	
	// ------------------------------------------ Constructor

	protected AgreementPreview() {
		createToolbar();
		createToolbarSimulator();
		createDiscPanelButtons();
		initWidget(uiBinder.createAndBindUi(this));
		initDiscPanels();
		dateLabels = new ArrayList<>();
	}
	
	// ------------------------------------------ setAgreementPreview
	
	public void setAgreementPreview(AgreementInfo agreementIn) {
		agreement = agreementIn;
		toolbar.setTitle(agreement.getDescription());
		serviAgreementUpdateButton.setVisible(agreement.getOwner().equals(AgreementOwner.SERVICONVENIOS));
		hideLevelSalaryMessage();
		showAgreementPreview();
		fillAgreementInfo();
		hideMessage();
	}

	// ------------------------------------------ fillAgreementInfo
	
	private void fillAgreementInfo() {
		
		if(agreement.getDates().isEmpty())
			printPreviewButton.setVisible(false);
		
		description.setText(agreement.getDescription());
		description.removeStyleName(style.modify());
		description.addValueChangeHandler(e -> {
			agreement.setDescription(e.getValue());
			description.addStyleName(style.modify());
			setHasChange(true);
		});
		ssNumber.setText(agreement.getSSNumber());
		ssNumber.removeStyleName(style.modify());
		ssNumber.addValueChangeHandler(e -> {
			agreement.setSSNumber(e.getValue());
			ssNumber.addStyleName(style.modify());
			setHasChange(true);
		});
		
		if(agreement.getOwner().equals(AgreementOwner.SERVICONVENIOS))
			createServiAgreementPanel();
		else
			serviAgreementPanel.clear();
		
		createSalaryTableButtons();
		
		createSalaryTable();
		createCategoryTable();
		
		if(agreement.getActivePayments().isEmpty()) showPaymentMessage();
		else createPaymentTable();
		
		setTablesWidth();
		
	}
	
	// ------------------------------------------ serviAgreementPanel
	
	private void createServiAgreementPanel() {
		serviAgreementPanel.clear();
		
		Label serviAgreementLabel = new Label("Vinculado con ServiConvenios");
		serviAgreementLabel.getElement().getStyle().setMarginLeft(10, Unit.PX);
		serviAgreementPanel.add(serviAgreementLabel);
		
		AonToolbarButton serviAgreementPDFButton = new AonToolbarButton("ServiConvenios PDF", AON.CSS.aonIconPdf() );
		serviAgreementPDFButton.addClickHandler(e -> {
			String url = GWT.getModuleBaseURL() + "servi_agreement?fileType=pdf&ssNumber=" + agreement.getSSNumber();
			Window.open( url, "_blank", "status=0,toolbar=0,menubar=0,location=0");
		});
		serviAgreementPanel.add(serviAgreementPDFButton);
		
		AonToolbarButton serviAgreementXLSButton = new AonToolbarButton("ServiConvenios XLS", AON.CSS.aonIconExcel() );
		serviAgreementXLSButton.addClickHandler(e -> {
			String url = GWT.getModuleBaseURL() + "servi_agreement?fileType=xls&ssNumber=" + agreement.getSSNumber();
			Window.open( url, "_blank", "status=0,toolbar=0,menubar=0,location=0");
		});
		serviAgreementPanel.add(serviAgreementXLSButton);
		
	}

	// ------------------------------------------ salaryTableButtons

	private void createSalaryTableButtons() {
		salaryTableButtons.clear();
		
		selectedDate = agreement.getSortedDates().stream().findFirst().get();
		
		agreement.getSortedDates().forEach(date -> {
			Label dateLabel = new Label(formatDate.format(date));
			dateLabel.addStyleName(style.dateNoSelected());
			if(date.equals(selectedDate)) dateLabel.addStyleName(style.dateSelected());
			dateLabel.addClickHandler(e -> {
				Date dateClicked = formatDate.parse(dateLabel.getText());
				selectedDate = dateClicked;
				dateLabels.forEach(dateLabelIn -> dateLabelIn.removeStyleName(style.dateSelected()));
				dateLabel.addStyleName(style.dateSelected());
				createSalaryTable();
			});
			
			dateLabels.add(dateLabel);
			salaryTableButtons.add(dateLabel);
		});
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

	// ------------------------------------------ salaryTable
	
	private void createSalaryTable() {
		if(agreement.getSortedDates().isEmpty())
			showSalaryTableMessage();
		else {
			hideLevelSalaryMessage();
			getSalaryTableHeader();
			fillSalaryTable();
			salaryTableWidth();
		}
	}
	
	private void getSalaryTableHeader() {
		salaryGrid.clear();
		salaryGrid.resize(0, agreement.getVariablesByDate(selectedDate).size()+2);
//		salaryGrid.resize(0, agreement.getVariablesByDate(selectedDate).size()+3);
		int row = salaryGrid.insertRow(salaryGrid.getRowCount());
		
//		Label level = new Label("Nivel");
//		level.addStyleName(style.gridTitle());
//		level.addStyleName(style.textCenter());
//		level.addStyleName(style.cellWidth());
//		level.addStyleName(style.headerFSize());
//		salaryGrid.setWidget(row, 0, level);
//		salaryGrid.getCellFormatter().addStyleName(row, 0, style.headerLevelFixed());
//		salaryGrid.getColumnFormatter().addStyleName(0, style.columnBorder());
		
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
	}

	private void fillSalaryTable() {
		for(Level level : agreement.getLevels()) {
			
			if(level.getId() != 0 && ((level.isDeleted() || (null != agreement.getSelectedLevel() && !level.getId().equals(agreement.getSelectedLevel().getId()))) 
					/*|| (level.isDeleted() || !AonStringUtils.isBlank(agreement.getSelectedCategory()) && !agreement.getCategoriesMap().get(level.getId()).contains(agreement.getSelectedCategory()))*/)) continue;
			
			int row = salaryGrid.insertRow(salaryGrid.getRowCount());
			
//			Widget levelCell;
//			
//			if(level.getId() == 0) {
//				levelLB = new ListBox();
//				levelLB.getElement().getStyle().setHeight(1.7, Unit.EM);
//				levelLB.getElement().getStyle().setWidth(100, Unit.PX);
//				levelLB.getElement().getStyle().setBorderStyle(BorderStyle.NONE);
//				levelLB.addItem("Todos", "");
//				agreement.getLevels().forEach(levelIn -> {
//					if(levelIn.getId() == 0) levelLB.addItem("Por defecto", levelIn.getId().toString());
//					else levelLB.addItem(levelIn.getDescription(), levelIn.getId().toString());
//				});
//				setSelectedValueLB(levelLB, null == agreement.getSelectedLevel() ? "" : String.valueOf(agreement.getSelectedLevel().getId()));
//				levelLB.setVisible(!agreement.getLevels().isEmpty());
//				
//				levelLB.addChangeHandler(e -> filterSelectedLevel());
//				
//				levelCell = levelLB;
//			} else {
//				levelCell = new Label(level.getDescription());
//				levelCell.addStyleName(style.gridTitle());
//				levelCell.addStyleName(style.gridCell());
//				levelCell.addStyleName(style.levelCell());
//			}
//			
//			salaryGrid.setWidget(row, 0, levelCell);
//			salaryGrid.getCellFormatter().addStyleName(row, 0, style.levelFixed());
//			if(row % 2 == 0 ) salaryGrid.getCellFormatter().addStyleName(row, 0, style.oddRow());
			
			Widget categoryCell;
			
			if(level.getId() == 0) {
				categoryLB = createCategoryLB();
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
				Label cell = new Label();
				cell.addStyleName(style.gridCell());
				cell.addStyleName(style.valueCell());
				cell.setText(null == levelData ? null : SpecialExpresion.parse(levelData.getExpression()).getInput());
				cell.setTitle("Valor nivel retributivo");
				
				if(row % 2 == 0 ) cell.addStyleName(style.oddRow());
				cell.getElement().getStyle().setBorderStyle(BorderStyle.NONE);
				if(null != levelData && AonStringUtils.isNotBlank(levelData.getExpression()) && cell.getText().length() > 16)
					cell.setWidth((7.5 * levelData.getExpression().length()) + "px");
				else
					cell.setWidth("100%");
				
				if(levelData != null && levelData.isModify())
					cell.addStyleName(style.modify());
				else
					cell.removeStyleName(style.modify());
				
				// check if level 0 or default value
				if(level.getId() == 0 || null == levelData || AonStringUtils.isBlank(levelData.getExpression())) {
					levelData = agreement.getDefaultLevelData(variable, selectedDate);
					cell.setText(null == levelData ? null : SpecialExpresion.parse(levelData.getExpression()).getInput());
					cell.setTitle("Valor por defecto");
					cell.addStyleName(style.levelDefaultValue());
					
					if(null != levelData && AonStringUtils.isNotBlank(levelData.getExpression()) && cell.getText().length() > 20)
						cell.setWidth((7.5 * levelData.getExpression().length()) + "px");
					else
						cell.setWidth("100%");
				}
						
				salaryGrid.setWidget(row, col, cell);
				if(row % 2 == 0 ) salaryGrid.getCellFormatter().addStyleName(row, col, style.oddRow());
				col++;
			}
			
			Label emptyCell = new Label("");
			salaryGrid.setWidget(row, col, emptyCell);
			if(row % 2 == 0 ) salaryGrid.getCellFormatter().addStyleName(row, col, style.oddRow());
		}
	}

	private ListBox createCategoryLB() {
		Map<String, Integer> allCategories = new TreeMap<>();
		
		for(Level levelIT : agreement.getLevels()) {
			if(levelIT.getId() == 0) continue;
			Set<String> levelCategories = agreement.getCategoriesMap().get(levelIT.getId());
			Set<String> levelContracts = agreement.getContractsMap().get(levelIT.getId());
			if(workplaceView && null !=levelContracts && !levelContracts.isEmpty()) levelContracts.forEach(levelContract -> allCategories.put(levelIT.getDescription() + " - " + levelContract, levelIT.getId()));
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

	private void salaryTableWidth() {
		salaryGrid.getColumnFormatter().setWidth(0, "120px");
	}
	
	private String getLevelCategories(Level level) {
		StringBuilder categoriesBuilder = new StringBuilder();
		Set<String> levelContracts = agreement.getContractsMap().get(level.getId());
		if(workplaceView && null !=levelContracts && !levelContracts.isEmpty()) {
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
		if(workplaceView && null !=levelContracts && !levelContracts.isEmpty()) {
			
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

//	private void filterSelectedLevel() {
//		String levelId = levelLB.getSelectedValue();
//		agreement.setSelectedLevel(AonStringUtils.isBlank(levelId) ? null : agreement.getLevelById(Integer.parseInt(levelLB.getSelectedValue())));
//		setAgreementPreview(agreement);
//	}
	
	private void filterSelectedCategory() {
		String levelId = categoryLB.getSelectedValue();
		agreement.setSelectedLevel(AonStringUtils.isBlank(levelId) ? null : agreement.getLevelById(Integer.parseInt(categoryLB.getSelectedValue())));
		setAgreementPreview(agreement);
	}
	
	// ------------------------------------------ categoryTable

	private void createCategoryTable() {
		if(agreement.getActiveLevels().size()  <= 1)
			showLevelMessage();
		else {
			hideLevelSalaryMessage();
			getCategoryTableHeader();
			fillCategoryTable();
			categoryTableWidth();
		}
	}

	private void getCategoryTableHeader() {
		levelGrid.clear();
		levelGrid.resize(0, 2);
		int row = levelGrid.insertRow(levelGrid.getRowCount());
		
		Label level = new Label("Nivel");
		level.addStyleName(style.gridTitle());
		level.addStyleName(style.headerFSize());
		level.getElement().getStyle().setFontSize(1, Unit.EM);
		levelGrid.setWidget(row, 0, level);
		levelGrid.getCellFormatter().addStyleName(row, 0, style.headerFixed());

		
		Label category = new Label("Categoria");
		category.addStyleName(style.gridTitle());
		category.addStyleName(style.headerFSize());
		category.getElement().getStyle().setFontSize(1, Unit.EM);
		levelGrid.setWidget(row, 1, category);
		levelGrid.getCellFormatter().addStyleName(row, 1, style.headerFixed());

		levelGrid.getRowFormatter().addStyleName(row, style.headerColor());
	}

	private void fillCategoryTable() {
		for(Entry<Integer, Set<String>> e : agreement.getCategoriesMap().entrySet()) {
			Level level = agreement.getLevelById(e.getKey());
			
			if(level.getId() == 0 || level.isDeleted() || (null != agreement.getSelectedLevel() && !level.getId().equals(agreement.getSelectedLevel().getId()))) continue;
			
			int row = levelGrid.insertRow(levelGrid.getRowCount());
			
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
			levelCell.getElement().getStyle().setPaddingLeft(1, Unit.EM);
			
			Label categoryCell = new Label(categoriesBuilder.toString());
			categoryCell.setTitle("Categorias nivel " + level.getDescription());
			
			levelGrid.setWidget(row, 0, levelCell);
			levelGrid.setWidget(row, 1, categoryCell);
			
			if(row % 2 == 0 ) levelGrid.getCellFormatter().addStyleName(row, 0, style.oddRow());
			if(row % 2 == 0 ) levelGrid.getCellFormatter().addStyleName(row, 1, style.oddRow());
			
		}
	}

	private void categoryTableWidth() {
		levelGrid.setWidth("100%");
		levelGrid.getColumnFormatter().setWidth(0, "20%");
		levelGrid.getColumnFormatter().setWidth(1, "80%");
	}
	
	// ------------------------------------------ paymentTable

	private void createPaymentTable() {
		hidePaymentMessage();
		getPaymentTableHeader();
		fillPaymentTable();
		paymentTableWidth();
	}
	
	private void getPaymentTableHeader() {
		paymentGrid.clear();
		paymentGrid.resize(0, 4);
		int row = paymentGrid.insertRow(paymentGrid.getRowCount());
		
		Label cra = new Label("CRA");
		cra.addStyleName(style.gridTitle());
		cra.addStyleName(style.headerFSize());
		cra.getElement().getStyle().setTextAlign(TextAlign.CENTER);
		paymentGrid.setWidget(row, 0, cra);
		paymentGrid.getCellFormatter().addStyleName(row, 0, style.headerFixed());
		
		Label concept = new Label("Concepto");
		concept.addStyleName(style.gridTitle());
		concept.addStyleName(style.headerFSize());
		paymentGrid.setWidget(row, 1, concept);
		paymentGrid.getCellFormatter().addStyleName(row, 1, style.headerFixed());
		
		Label extraInfo = new Label("");
		extraInfo.addStyleName(style.gridTitle());
		extraInfo.addStyleName(style.headerFSize());
		paymentGrid.setWidget(row, 2, extraInfo);
		paymentGrid.getCellFormatter().addStyleName(row, 2, style.headerFixed());
		
		Label devengo = new Label("Devengo");
		devengo.addStyleName(style.gridTitle());
		devengo.addStyleName(style.headerFSize());
		paymentGrid.setWidget(row, 3, devengo);
		paymentGrid.getCellFormatter().addStyleName(row, 3, style.headerFixed());
		
		paymentGrid.getRowFormatter().addStyleName(row, style.headerColor());
	}

	private void fillPaymentTable() {
		for(Payment payment : agreement.getActivePayments()) {
			int row = paymentGrid.insertRow(paymentGrid.getRowCount());
			
			Label craCell = new Label(null == payment.getType() ? "" : AonStringUtils.leftPad(payment.getType().getCode() + "", 4, '0'));
			craCell.setTitle(payment.getType().getDescription());
			craCell.getElement().getStyle().setTextAlign(TextAlign.CENTER);
			
			Label conceptCell = new Label(payment.getDescription());
			
			Label extratCell = new Label(getExtraMessage(payment));
			extratCell.setTitle(getExtraTitle(payment));
			
			Label devengoCell = new Label();
			devengoCell.setText(getParsedExpression(payment.getExpression()));
			
			paymentGrid.setWidget(row, 0, craCell);
			paymentGrid.setWidget(row, 1, conceptCell);
			paymentGrid.setWidget(row, 2, extratCell);
			paymentGrid.setWidget(row, 3, devengoCell);
			
			if(row % 2 == 0 ) paymentGrid.getCellFormatter().addStyleName(row, 0, style.oddRow());
			if(row % 2 == 0 ) paymentGrid.getCellFormatter().addStyleName(row, 1, style.oddRow());
			if(row % 2 == 0 ) paymentGrid.getCellFormatter().addStyleName(row, 2, style.oddRow());
			if(row % 2 == 0 ) paymentGrid.getCellFormatter().addStyleName(row, 3, style.oddRow());
		}
	}
	
	private String getParsedExpression(String expression) {
		expression = AonStringUtils.isBlank(expression) ? expression : expression.replaceAll("HIDE\\(.*\\); ", "");
		return SpecialExpresion.parse(expression).getInput();
	}

	private void paymentTableWidth() {
		paymentGrid.setWidth("100%");
		paymentGrid.getColumnFormatter().setWidth(0, "10%");
		paymentGrid.getColumnFormatter().setWidth(1, "40%");
		paymentGrid.getColumnFormatter().setWidth(2, "10%");
		paymentGrid.getColumnFormatter().setWidth(3, "40%");
	}

	private String getExtraMessage(Payment payment) {
		AgreementExtra extra = agreement.getExtraPayment(payment.getId());
		if(	null == extra && 
			!payment.getType().equals(Payment.Type.CRA_0004) && 
			!payment.getType().equals(Payment.Type.CRA_0005)) return "";
		
		if(	null == extra && 
				(payment.getType().equals(Payment.Type.CRA_0004) || 
				payment.getType().equals(Payment.Type.CRA_0005))) return "Prorrateado";
		
		String period = null == extra ? "" : getExtraPeriod(extra);
		
		return extra.getIssueDate() + period;
	}
	
	private String getExtraPeriod(AgreementExtra extra) {
		if(AonStringUtils.containsIgnoreCase(extra.getStartDate(), "-1")) return " (Anual)";
		
		int startMonth = Integer.parseInt(extra.getStartDate().split("/")[1]);
		int endMonth = Integer.parseInt(extra.getEndDate().split("/")[1]);
		
		return endMonth - startMonth > 6 ? " (Anual)" : " (Semestral)";
	}

	private String getExtraTitle(Payment payment) {
		AgreementExtra extra = agreement.getExtraPayment(payment.getId());
		if(	null == extra && 
				!payment.getType().equals(Payment.Type.CRA_0004) && 
				!payment.getType().equals(Payment.Type.CRA_0005)) return "";
			
		if(	null == extra && 
				(payment.getType().equals(Payment.Type.CRA_0004) || 
				payment.getType().equals(Payment.Type.CRA_0005))) return "Prorrateado";
		
		return "Devenga desde: " + parseExtraDate(extra.getStartDate()) + ", hasta: " + parseExtraDate(extra.getEndDate());
	}
	
	private String parseExtraDate(String date) {
		if(AonStringUtils.isBlank(date)) return "";
		
		if(AonStringUtils.contains(date, "-1")) {
			return AonStringUtils.split(date, '-')[0].trim() + " (A\u00f1o anterior)";
		} else return date;
	}
	
	// ------------------------------------------ tables widht
	
	public void setTablesWidth() {
		Scheduler.get().scheduleDeferred(() -> {
			levelScrollPanel.setWidth((Window.getClientWidth() - 450) + "px");
			salaryTableButtons.setWidth((Window.getClientWidth() - 450) + "px");
			salaryScrollPanel.setWidth((Window.getClientWidth() - 450) + "px");
			paymentScrollPanel.setWidth((Window.getClientWidth() - 450) + "px");
			
			levelScrollPanel.setHeight((levelDiscPanelContent.getOffsetHeight() - 20) + "px");
			salaryScrollPanel.setHeight((salaryDiscPanelContent.getOffsetHeight() - 40) + "px");
			paymentScrollPanel.setHeight((paymentDiscPanelContent.getOffsetHeight() - 20) + "px");
		});
	}
	
	public void setTablesWidthCollapseMenu() {
		levelScrollPanel.setWidth((Window.getClientWidth() - 150) + "px");
		salaryTableButtons.setWidth((Window.getClientWidth() - 150) + "px");
		salaryScrollPanel.setWidth((Window.getClientWidth() - 150) + "px");
		paymentScrollPanel.setWidth((Window.getClientWidth() - 150) + "px");
	}

	// ------------------------------------------ deckLayoutPanel
	
	private void showAgreementPreview() {
		deckLayoutPanel.showWidget(0);
	}
	
	private void showAgreementSimulator() {
		deckLayoutPanel.showWidget(1);
	}
	
	// ------------------------------------------ salaryTable
	
	private void showSalaryTableMessage() {
		agreementSalaryTableMessage.getElement().getStyle().clearDisplay();
		agreementLevelMessage.getElement().getStyle().setDisplay(Display.NONE);
		salaryTable.getElement().getStyle().setDisplay(Display.NONE);
	}
	
	private void showLevelMessage() {
		agreementSalaryTableMessage.getElement().getStyle().setDisplay(Display.NONE);
		salaryTable.getElement().getStyle().setDisplay(Display.NONE);
		agreementLevelMessage.getElement().getStyle().clearDisplay();
	}
	
	private void hideLevelSalaryMessage() {
		salaryTable.getElement().getStyle().clearDisplay();
		agreementSalaryTableMessage.getElement().getStyle().setDisplay(Display.NONE);
		agreementLevelMessage.getElement().getStyle().setDisplay(Display.NONE);
	}
	
	// ------------------------------------------ payments
	
	private void showPaymentMessage() {
		agreementPaymentMessage.getElement().getStyle().clearDisplay();
		paymentTable.getElement().getStyle().setDisplay(Display.NONE);
	}
	
	private void hidePaymentMessage() {
		paymentTable.getElement().getStyle().clearDisplay();
		agreementPaymentMessage.getElement().getStyle().setDisplay(Display.NONE);
	}
	
	// ------------------------------------------ DisclosurePanel

	private void initDiscPanels() {
		levelDiscPanel.setAnimationEnabled(true);
		levelDiscPanel.addOpenHandler(e -> {
			handleIcon(levelDiscBtn, true);
			
			salaryDiscPanel.setOpen(false);
			handleIcon(salaryDiscBtn, false);
			
			paymentDiscPanel.setOpen(false);
			handleIcon(paymentDiscBtn, false);
		});
		levelDiscPanel.addCloseHandler(e -> handleIcon(levelDiscBtn, false));
		levelDiscPanelContent.setHeight((Window.getClientHeight() - 425) + "px");
		
		salaryDiscPanel.setAnimationEnabled(true);
		salaryOpenHandler = salaryDiscPanel.addOpenHandler(e -> {
			handleIcon(salaryDiscBtn, true);
			
			levelDiscPanel.setOpen(false);
			handleIcon(levelDiscBtn, false);
			
			paymentDiscPanel.setOpen(false);
			handleIcon(paymentDiscBtn, false);
		});
		salaryDiscPanel.addCloseHandler(e -> handleIcon(salaryDiscBtn, false));
		salaryDiscPanel.setOpen(true);
		salaryDiscPanelContent.setHeight((Window.getClientHeight() - 425) + "px");
		
		paymentDiscPanel.setAnimationEnabled(true);
		paymentOpenHandler = paymentDiscPanel.addOpenHandler(e -> {
			handleIcon(paymentDiscBtn, true);
			
			levelDiscPanel.setOpen(false);
			handleIcon(levelDiscBtn, false);
			
			salaryDiscPanel.setOpen(false);
			handleIcon(salaryDiscBtn, false);
		});
		paymentDiscPanel.addCloseHandler(e -> handleIcon(paymentDiscBtn, false));
		paymentDiscPanelContent.setHeight((Window.getClientHeight() - 425) + "px");
	}

	private void createDiscPanelButtons() {
		levelDiscBtn = new AonToolbarButton("Desplegar Nivel / Categoria", AON.CSS.aonIconRight());
		salaryDiscBtn = new AonToolbarButton("Desplegar Tabla Salarial", AON.CSS.aonIconRight());
		paymentDiscBtn = new AonToolbarButton("Desplegar Devengos", AON.CSS.aonIconRight());
		
		levelDiscBtn.addClickHandler(e -> handleIcon(levelDiscBtn, levelDiscPanel.isOpen()));
		salaryDiscBtn.addClickHandler(e -> handleIcon(salaryDiscBtn, salaryDiscPanel.isOpen()));
		paymentDiscBtn.addClickHandler(e -> handleIcon(paymentDiscBtn, paymentDiscPanel.isOpen()));
	}

	private void handleIcon(AonToolbarButton button, boolean open) {
		if(open) {
			button.removeStyleName(AON.CSS.aonIconRight());
			button.addStyleName(AON.CSS.aonIconDown());
			
			if(button.equals(levelDiscBtn)) button.setTitle("Colapsar Nivel / Categoria");
			if(button.equals(levelDiscBtn)) button.setTitle("Colapsar Tabla Salarial");
			if(button.equals(levelDiscBtn)) button.setTitle("Colapsar Devengos");
			if(button.equals(levelDiscBtn)) button.setTitle("Colapsar Extras");
		} else {
			button.removeStyleName(AON.CSS.aonIconDown());
			button.addStyleName(AON.CSS.aonIconRight());
			
			if(button.equals(levelDiscBtn)) button.setTitle("Desplegar Nivel / Categoria");
			if(button.equals(levelDiscBtn)) button.setTitle("Desplegar Tabla Salarial");
			if(button.equals(levelDiscBtn)) button.setTitle("Desplegar Devengos");
			if(button.equals(levelDiscBtn)) button.setTitle("Desplegar Extras");
		}
	}
	
	// ------------------------------------------ toolbar

	private void createToolbar() {
		toolbar = new AonToolbar("Convenio");
		
		saveBtn = new AonToolbarSmallButton(AON.MSG.saveAction(), AON.CSS.aonIconSave());
		saveBtn.addClickHandler(e -> {
			showLoading("Guardando convenio " + toolbar.getTitle() + " ...");
			setHasChange(false);
			onSaved();
		});
		
		toolbar.add(saveBtn);
		
		agreementInfoButton = new AonToolbarButton("Informaci\u00f3n Convenio", AON.CSS.aonIconInfo());
		agreementInfoButton.addClickHandler(e -> 
			impl.getAgreementUsedInfo(agreement.getId(), agreement.getDescription(), new AsyncCallback<String>() {
				
				@Override
				public void onSuccess(String message) {
					AonDialog dialog = new AonDialog(agreement.getDescription(), new HTML(message));
					dialog.setGlassStyleName(style.dialogGlass());
					dialog.addStyleName(style.dialogZIndex());
					dialog.info();
				}
				
				@Override
				public void onFailure(Throwable caught) {
					showError("Error informaci\u00f3n convenio", caught.getMessage());
				}
			})
		);
			
		toolbar.add(agreementInfoButton);
		
		serviAgreementUpdateButton = new AonToolbarButton("Actualizar Convenio", AON.CSS.aonIconCloudImport());
		serviAgreementUpdateButton.addClickHandler(e -> 
			impl.getDomainUserRoles(new AsyncCallback<DomainUserRoles>() {
				
				@Override
				public void onSuccess(DomainUserRoles userRole) {
					if(userRole.isConvenios()) {
						PaymentsCleanDialog dialog = new PaymentsCleanDialog(agreement.getPayments()) {
							
							@Override
							public void onAccept() {
								showLoading("Actualizando convenio");
								impl.checkAndUpdateServiAgreement(agreement, new AsyncCallback<Void>() {

									@Override
									public void onFailure(Throwable caught) {
										showError("Error actualizaci\u00F3n", caught.getMessage());
									}

									@Override
									public void onSuccess(Void result) {
										showSuccess("Actualizaci\u00F3n", "El convenio ha sido actualizado correctamente");
										reloadAgreement();
									}});
							}
						
						};
						
						dialog.setGlassStyleName(style.dialogGlass());
						dialog.addStyleName(style.dialogZIndex());
						
					} else 
						showError("Actualizaci\u00f3n no disponible", "Para poder actualizar un convenio a traves de ServiConvenios debe tener contrato el m\u00f3dulo.");
				}
				
				@Override
				public void onFailure(Throwable caught) {
					showError("Error", caught.getMessage());
				}
			})
		);
		toolbar.add(serviAgreementUpdateButton);
		
		
		printPreviewButton = new AonToolbarButton(AON.MSG.draftPrint(), AON.CSS.aonIconPdf() );
		printPreviewButton.addClickHandler(e -> {
			showAgreementSimulator();
			initTc2ListBox();
			initLevelListBox();
			printPreview();
		});
		toolbar.add(printPreviewButton);
	}
	
	// ------------------------------------------ abstractMethod
	
	protected abstract void reloadAgreement();
	
	// ------------------------------------------ toolbarSimulator

	private void createToolbarSimulator() {
		toolbarSimulator = new AonToolbar("Simulador");
		
		AonToolbarButton closeSimulatorBtn = new AonToolbarButton(AON.MSG.close(), AON.CSS.aonIconClose());
		closeSimulatorBtn.addClickHandler(e -> showAgreementPreview());
		toolbarSimulator.add(closeSimulatorBtn);
		
		tc2ListBox = new ListBox();
		tc2ListBox.setWidth("250px");
		tc2ListBox.addChangeHandler(e -> printPreview());
		toolbarSimulator.add(tc2ListBox);
		
		levelListBox = new ListBox();
		levelListBox.setWidth("250px");
		levelListBox.addChangeHandler(e -> printPreview());
		toolbarSimulator.add(levelListBox);
		
		Label partilialityL = new Label("Coef. Part.");
		partilialityL.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		partialTextBox = new TextBox();
		partialTextBox.setValue("1");
		partialTextBox.setMaxLength(5);
		partialTextBox.setVisibleLength(5);
		partialTextBox.setAlignment(TextAlignment.RIGHT);
		partialTextBox.addValueChangeHandler(e -> printPreview());
		toolbarSimulator.add(partilialityL);
		toolbarSimulator.add(partialTextBox);
		
		Label quoteGroupL = new Label("Grup. Cotiz.");
		quoteGroupL.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		groupListBox = new ListBox();
		initQuoteGroup(groupListBox);
		groupListBox.addChangeHandler(e -> printPreview());
		toolbarSimulator.add(quoteGroupL);
		toolbarSimulator.add(groupListBox);
	}

	private void initTc2ListBox() {
		tc2ListBox.clear();

		for (Entry<Integer, ContractTypeRecord> entry : new ContractType().getContractTypes().entrySet()) { 
			String value = AonStringUtils.leftPad(entry.getKey().toString(), 3, '0');
			String item = entry.getKey() + " - " + AonStringUtils.upperCase(AonStringUtils.abbreviate(entry.getValue().getContractTypeShortDescription(),40));
			tc2ListBox.addItem(item, value);
		}
		
		tc2ListBox.setSelectedIndex(1);// 100
	}
	
	private void initLevelListBox() {
		levelListBox.clear();
		for (Level level : agreement.getLevels()) {
			if (level.getId() == 0)
				continue;
			String levelDescription = level.getDescription();
			StringBuilder buffer = new StringBuilder();
			if (!AonStringUtils.isBlank(levelDescription))
				buffer.append(levelDescription);

			Set<String> categories = agreement.getCategoriesMap().get(level.getId());
			if (categories != null) {
				for (String category : categories) {
					if (!AonStringUtils.isBlank(category)) {
						buffer.append(" " + category);
						break;
					}
				}
			}
			levelListBox.addItem(buffer.toString(), Integer.toString(level.getId()));
		}
	}
	
	private void initQuoteGroup(ListBox quoteGroup) {
		quoteGroup.clear();
		quoteGroup.addItem("1", "01");
		quoteGroup.addItem("2", "02");
		quoteGroup.addItem("3", "03");
		quoteGroup.addItem("4", "04");
		quoteGroup.addItem("5", "05");
		quoteGroup.addItem("6", "06");
		quoteGroup.addItem("7", "07");
		quoteGroup.addItem("8", "08");
		quoteGroup.addItem("9", "09");
		quoteGroup.addItem("10", "10");
		quoteGroup.addItem("11", "11");
	}
	
	// ------------------------------------------ printPreview
	
	private void printPreview() {
		showLoading("Preparando simulador del borrador...");
		
		int levelId = getLevelId();
		String tc2 = getTc2();
		String group = getGroup();
		double partial = getPartial();
		
		List<Variable> context = new ArrayList<>();
		context.add(new StringVariable.Builder().setName("TC2").setValue(tc2).create());
		context.add(new StringVariable.Builder().setName("GRUPO_COTIZACION").setValue(group).create());
		context.add(new NumberVariable.Builder().setName("COEFICIENTE_PARCIALIDAD").setValue(partial).create());
		
		impl.getAgreementDraftReceipt(agreement, context, levelId, "application/pdf", new AsyncCallback<String>() {

			@Override
			public void onSuccess(String html) {
				hideMessage();
				setPartial(partial);
				printPreviewViewer.open(html);
			}

			@Override
			public void onFailure(Throwable caught) {
				showError("Error simulador", caught.getMessage());
			}
		});

	}

	private int getLevelId() {
		int index = levelListBox.getSelectedIndex();
		String value = levelListBox.getValue(index);
		return Integer.valueOf(value);
	}

	private String getTc2() {
		return tc2ListBox.getSelectedValue();
	}

	private String getGroup() {
		return groupListBox.getSelectedValue();
	}
	
	private void setPartial(double partial) {
		partialTextBox.setValue(Double.toString(partial), false);
	}
	
	private double getPartial() {
		String text =  partialTextBox.getText();
		try {
			return Double.parseDouble(text);
		} catch ( Exception e ) {
			return 1.0;
		}
	}
	
	// ------------------------------------------ setSelectedLevel
	
	public void setSelectedLevel(Integer levelId, String toolbarTitle) {
		blockElements();
		hideToolbarButtons();
		hideLevelDiscPanel();
		filterLevel(levelId);
		toolbar.setTitle(toolbarTitle);
		calcDiscPanelHeightsEmployee();
		createAgreementGoToBtn();
	}

	private void filterLevel(Integer levelId) {
		setSelectedValueLB(categoryLB, null == levelId ? "" : String.valueOf(levelId));
		filterSelectedCategory();
		salaryGrid.removeRow(1);
	}

	public void payrollPreview() {
		blockElements();
		hideToolbarButtons();
		hideLevelDiscPanel();
		workplaceView = true;
		createSalaryTable();
		calcDiscPanelHeightsPayroll();
		createAgreementGoToBtn();
	}
	
	private void blockElements() {
		description.setEnabled(false);
		ssNumber.setEnabled(false);
		description.getElement().getStyle().setBackgroundColor("transparent");
		ssNumber.getElement().getStyle().setBackgroundColor("transparent");
	}
	
	private void hideToolbarButtons() {
		saveBtn.addStyleName(style.displayNone());
		serviAgreementUpdateButton.addStyleName(style.displayNone());
		printPreviewButton.addStyleName(style.displayNone());
		agreementInfoButton.addStyleName(style.displayNone());
	}
	
	private void hideLevelDiscPanel() {
		levelDiscPanel.getElement().getStyle().setDisplay(Display.NONE);
	}
	
	private void calcDiscPanelHeightsEmployee() {
		int salaryHeight = (salaryGrid.getRowCount() * 20) + 50;
		
		salaryDiscPanelContent.setHeight(salaryHeight + "px");
		salaryScrollPanel.setHeight((salaryDiscPanelContent.getOffsetHeight() - 45) + "px");
		
		int paymentHeight = (paymentGrid.getRowCount() * 20) + 50;
		paymentHeight = paymentHeight + salaryHeight < (Window.getClientHeight() - 340 - salaryHeight) ? paymentHeight : (Window.getClientHeight() - 340 - salaryHeight);
		
		paymentDiscPanelContent.setHeight(paymentHeight + "px");
		paymentScrollPanel.setHeight((paymentDiscPanelContent.getOffsetHeight() - 25) + "px");
		
		salaryOpenHandler.removeHandler();
		salaryDiscPanel.addOpenHandler(e -> handleIcon(salaryDiscBtn, true));
		
		paymentOpenHandler.removeHandler();
		paymentDiscPanel.addOpenHandler(e -> handleIcon(paymentDiscBtn, true));
		
		salaryDiscPanel.setOpen(true);
		paymentDiscPanel.setOpen(true);
	}
	
	private void calcDiscPanelHeightsPayroll() {
		int salaryHeight = (salaryGrid.getRowCount() * 20) + 50;
		salaryHeight = (Window.getClientHeight() - 340) > salaryHeight ? salaryHeight : (Window.getClientHeight() - 340);
		
		salaryDiscPanelContent.setHeight(salaryHeight + "px");
		salaryScrollPanel.setHeight((salaryDiscPanelContent.getOffsetHeight() - 45) + "px");
		
		int paymentHeight = (paymentGrid.getRowCount() * 20) + 50;
		paymentHeight = paymentHeight + salaryHeight < (Window.getClientHeight() - 340) ? paymentHeight : (Window.getClientHeight() - 340);
		
		paymentDiscPanelContent.setHeight(paymentHeight + "px");
		paymentScrollPanel.setHeight((paymentDiscPanelContent.getOffsetHeight() - 25) + "px");
		
		if(paymentHeight + salaryHeight < (Window.getClientHeight() - 340)) {
			salaryOpenHandler.removeHandler();
			salaryDiscPanel.addOpenHandler(e -> handleIcon(salaryDiscBtn, true));
			
			paymentOpenHandler.removeHandler();
			paymentDiscPanel.addOpenHandler(e -> handleIcon(paymentDiscBtn, true));
			
			salaryDiscPanel.setOpen(true);
			paymentDiscPanel.setOpen(true);
		}
	}
	
	private void createAgreementGoToBtn() {
		FlowPanel toolbarButtons = toolbar.getButtonContainer();
		if(toolbarButtons.getWidgetCount() > 4)
			toolbarButtons.remove(toolbarButtons.getWidgetCount() - 1);
		
		AonToolbarButton goToAgreementBtn = new AonToolbarButton("Ir al convenio " + agreement.getDescription(), AON.CSS.aonIconOpenInNew());
		goToAgreementBtn.addClickHandler(e -> goToAgreement(agreement.getId()));
		
		// TODO: quitar esta linea cuando este implementado
		goToAgreementBtn.setVisible(false);
		
		toolbar.add(goToAgreementBtn);
	}
	
	private void goToAgreement(Integer agreementId) {
		// TODO: Aqui iria la navegacion a los convenios
		// MainAgreementTab mainAgreementTab = new MainAgreementTab();
		// mainAgreementTab.onModuleLoad();
		// mainAgreementTab.agreements.getAgreementsAndSelectImported(agreementId, s -> {});
	}
	
	public void setToolbarTitle(String title) {
		toolbar.setTitle(title);
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
	
	public void showLoading(String message) {
		AonMessagePanel.showLoading(messagePanel, message);
	}

	private void hideMessage() {
		AonMessagePanel.hideMessage(messagePanel);
	}
	
}
