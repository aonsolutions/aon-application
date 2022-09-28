package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.payroll.shared.AgreementInfo;
import com.esferalia.aon.gwt.payroll.shared.AgreementInfo.AgreementExtra;
import com.esferalia.aon.gwt.payroll.shared.AgreementInfo.AgreementOwner;
import com.esferalia.aon.gwt.payroll.shared.AgreementInfo.Level;
import com.esferalia.aon.gwt.payroll.shared.AgreementInfo.LevelData;
import com.esferalia.aon.gwt.payroll.shared.NumberVariable;
import com.esferalia.aon.gwt.payroll.shared.Payment;
import com.esferalia.aon.gwt.payroll.shared.StringVariable;
import com.esferalia.aon.gwt.payroll.shared.Variable;
import com.esferalia.aon.occam.api.model.aonsolutions.DomainUserRoles;
import com.esferalia.aon.occam.api.model.type.ContractType;
import com.esferalia.aon.occam.api.model.type.ContractType.ContractTypeRecord;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DeckLayoutPanel;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.ValueBoxBase.TextAlignment;
import com.google.gwt.user.client.ui.Widget;

import net.aonsolutions.gwt.pdfjs.client.FullViewer;

public abstract class AgreementPreview extends ResizeComposite {
	
	// ------------------------------------------ UiBinder 

	private static AgreementPreviewUiBinder uiBinder = GWT.create(AgreementPreviewUiBinder.class);

	interface AgreementPreviewUiBinder extends UiBinder<Widget, AgreementPreview> {}

	// ------------------------------------------ UiFields
	
	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {
		String columnBorder();
		String extraCellHeight();
		String gridCell();
		String gridTitle();
		String headerColor();
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
	
	@UiField
	DeckLayoutPanel deckLayoutPanel;
	
	@UiField(provided = true)
	AonToolbar toolbar;
	
	@UiField
	ScrollPanel scrollPanel;
	
	@UiField
	Label description;
	
	@UiField
	Label ssNumber;
	
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
	
	@UiField
	DisclosurePanel extraDiscPanel;
	
	@UiField(provided = true)
	AonToolbarButton extraDiscBtn;
	
	@UiField
	HTMLPanel extraDiscPanelContent;
	
	@UiField
	HTMLPanel extraTable;
	
	@UiField
	ScrollPanel extraScrollPanel;
	
	@UiField
	Grid extraGrid;
	
	@UiField
	HTMLPanel agreementExtraMessage;
	
	@UiField(provided = true)
	AonToolbar toolbarSimulator;
	
	@UiField
	FullViewer printPreviewViewer;
	
	// ------------------------------------------ Variables
	
	private DomainEnterprisesServiceAsync impl = DomainEnterprisesServiceAsync.newInstance();
	private DateTimeFormat formatDate = DateTimeFormat.getFormat("dd/MM/yyyy");
	private AgreementInfo agreement;
	
	private ListBox datesLB;
	
	private AonToolbarButton printPreviewButton;
	private AonToolbarButton serviAgreementUpdateButton;
	
	private ListBox tc2ListBox;
	private ListBox levelListBox;
	private TextBox partialTextBox;
	private ListBox groupListBox;
	
	// ------------------------------------------ Constructor

	protected AgreementPreview() {
		createToolbar();
		createToolbarSimulator();
		createDiscPanelButtons();
		initWidget(uiBinder.createAndBindUi(this));
		initDiscPanels();
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
		scrollPanel.setHeight((Window.getClientHeight() - 200) + "px");
		
		if(agreement.getDates().isEmpty())
			printPreviewButton.setVisible(false);
		
		description.setText(agreement.getDescription());
		ssNumber.setText(agreement.getSSNumber());
		
		if(agreement.getOwner().equals(AgreementOwner.SERVICONVENIOS))
			createServiAgreementPanel();
		else
			serviAgreementPanel.clear();
		
		createSalaryTableButtons();
		
		createSalaryTable();
		createCategoryTable();
		
		if(agreement.getActivePayments().isEmpty()) showPaymentMessage();
		else createPaymentTable();
		
		if(agreement.getActiveExtras().isEmpty()) showExtraMessage();
		else createExtraTable();
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
		
		datesLB = new ListBox();
		datesLB.getElement().getStyle().setHeight(1.7, Unit.EM);
		agreement.getSortedDates().forEach(date -> datesLB.addItem(formatDate.format(date), formatDate.format(date)));
		datesLB.setSelectedIndex(0);
		datesLB.setVisible(!agreement.getSortedDates().isEmpty());
		
		datesLB.addChangeHandler(e -> createSalaryTable());
		
		salaryTableButtons.add(datesLB);
	}

	// ------------------------------------------ salaryTable
	
	private void createSalaryTable() {
		if(agreement.getSortedDates().isEmpty())
			showSalaryTableMessage();
		else {
			hideLevelSalaryMessage();
			salaryScrollPanel.setWidth((Window.getClientWidth() - 450) + "px");
//			salaryScrollPanel.getElement().getStyle().setHeight((Window.getClientHeight() - 620), Unit.PX);
			getSalaryTableHeader();
			fillSalaryTable();
			salaryTableWidth();
		}
	}
	
	private void getSalaryTableHeader() {
		Date selectedDate = formatDate.parse(datesLB.getSelectedValue());
		
		salaryGrid.clear();
		salaryGrid.resize(0, agreement.getVariablesByDate(selectedDate).size()+2);
		int row = salaryGrid.insertRow(salaryGrid.getRowCount());
		
		Label level = new Label("Nivel");
		level.addStyleName(style.gridTitle());
		level.addStyleName(style.textCenter());
		level.addStyleName(style.headerFSize());
		salaryGrid.setWidget(row, 0, level);
		salaryGrid.getCellFormatter().addStyleName(row, 0, style.headerLevelFixed());
		salaryGrid.getColumnFormatter().addStyleName(0, style.columnBorder());
		
		int col = 1;
		
		for(String variable : agreement.getVariablesByDate(selectedDate)) {
			Label label = new Label(variable);
			label.addStyleName(style.gridTitle());
			label.addStyleName(style.textCenter());
			label.addStyleName(style.headerFSize());
			salaryGrid.setWidget(row, col, label);
			salaryGrid.getColumnFormatter().setWidth(col, "100px");
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
		Date selectedDate = formatDate.parse(datesLB.getSelectedValue());
		
		for(Level level : agreement.getLevels()) {
			
			if(level.isDeleted()) continue;
			
			int row = salaryGrid.insertRow(salaryGrid.getRowCount());
			
			Label levelCell = new Label(level.getDescription());
			levelCell.addStyleName(style.gridTitle());
			levelCell.addStyleName(style.gridCell());
			
			salaryGrid.setWidget(row, 0, levelCell);
			salaryGrid.getCellFormatter().addStyleName(row, 0, style.levelFixed());
			if(row % 2 == 0 ) salaryGrid.getCellFormatter().addStyleName(row, 0, style.oddRow());
			
			int col = 1;
			
			for(String variable : agreement.getVariablesByDate(selectedDate)) {
				LevelData levelData = agreement.getLevelData(level.getId(), variable, selectedDate);
				TextBox cell = new ExpressionBox();
				cell.addStyleName(style.gridCell());
				cell.setValue(null == levelData ? null : levelData.getExpression());
				cell.setReadOnly(true);
						
				salaryGrid.setWidget(row, col, cell);
				if(row % 2 == 0 ) salaryGrid.getCellFormatter().addStyleName(row, col, style.oddRow());
				col++;
			}
			
			Label emptyCell = new Label("");
			salaryGrid.setWidget(row, col, emptyCell);
			if(row % 2 == 0 ) salaryGrid.getCellFormatter().addStyleName(row, col, style.oddRow());
		}
	}

	private void salaryTableWidth() {
		salaryGrid.setWidth("100%");
		salaryGrid.getColumnFormatter().setWidth(0, "100px");
	}
	
	// ------------------------------------------ categoryTable

	private void createCategoryTable() {
		if(agreement.getActiveLevels().size()  <= 1)
			showLevelMessage();
		else {
			hideLevelSalaryMessage();
			levelScrollPanel.setWidth("100%");
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
			if(level.getId() == 0 || level.isDeleted()) continue;
			
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
		paymentScrollPanel.setWidth("100%");
		getPaymentTableHeader();
		fillPaymentTable();
		paymentTableWidth();
	}

	private void getPaymentTableHeader() {
		paymentGrid.clear();
		paymentGrid.resize(0, 5);
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
		
		Label tributa = new Label("Tributa");
		tributa.addStyleName(style.gridTitle());
		tributa.addStyleName(style.headerFSize());
		tributa.getElement().getStyle().setTextAlign(TextAlign.CENTER);
		paymentGrid.setWidget(row, 2, tributa);
		paymentGrid.getCellFormatter().addStyleName(row, 2, style.headerFixed());
		
		Label cotiza = new Label("Cotiza");
		cotiza.addStyleName(style.gridTitle());
		cotiza.addStyleName(style.headerFSize());
		cotiza.getElement().getStyle().setTextAlign(TextAlign.CENTER);
		paymentGrid.setWidget(row, 3, cotiza);
		paymentGrid.getCellFormatter().addStyleName(row, 3, style.headerFixed());
		
		Label devengo = new Label("Devengo");
		devengo.addStyleName(style.gridTitle());
		devengo.addStyleName(style.headerFSize());
		paymentGrid.setWidget(row, 4, devengo);
		paymentGrid.getCellFormatter().addStyleName(row, 4, style.headerFixed());
		
		paymentGrid.getRowFormatter().addStyleName(row, style.headerColor());
	}

	private void fillPaymentTable() {
		for(Payment payment : agreement.getActivePayments()) {
			int row = paymentGrid.insertRow(paymentGrid.getRowCount());
			
			Label craCell = new Label(null == payment.getType() ? "" : AonStringUtils.leftPad(payment.getType().getCode() + "", 4, '0'));
			craCell.setTitle(payment.getType().getDescription());
			craCell.getElement().getStyle().setTextAlign(TextAlign.CENTER);
			
			Label conceptCell = new Label(payment.getDescription());
			
			Label taxedCell = new Label(getTaxedType(payment.getIrpfExpression()));
			taxedCell.setTitle(getTaxedDescription(payment.getIrpfExpression()));
			taxedCell.getElement().getStyle().setTextAlign(TextAlign.CENTER);
			
			Label quoteCell = new Label(getQuoteType(payment.getQuoteExpression()));
			quoteCell.setTitle(getQuoteDescription(payment.getQuoteExpression()));
			quoteCell.getElement().getStyle().setTextAlign(TextAlign.CENTER);
			
			TextBox devengoCell = new ExpressionBox();
			devengoCell.setWidth("100%");
			devengoCell.setValue(null == payment.getExpression() ? null : payment.getExpression());
			devengoCell.setReadOnly(true);
			
			paymentGrid.setWidget(row, 0, craCell);
			paymentGrid.setWidget(row, 1, conceptCell);
			paymentGrid.setWidget(row, 2, taxedCell);
			paymentGrid.setWidget(row, 3, quoteCell);
			paymentGrid.setWidget(row, 4, devengoCell);
			
			if(row % 2 == 0 ) paymentGrid.getCellFormatter().addStyleName(row, 0, style.oddRow());
			if(row % 2 == 0 ) paymentGrid.getCellFormatter().addStyleName(row, 1, style.oddRow());
			if(row % 2 == 0 ) paymentGrid.getCellFormatter().addStyleName(row, 2, style.oddRow());
			if(row % 2 == 0 ) paymentGrid.getCellFormatter().addStyleName(row, 3, style.oddRow());
			if(row % 2 == 0 ) paymentGrid.getCellFormatter().addStyleName(row, 4, style.oddRow());
		}
	}

	private String getTaxedType(String irpfExpression) {
		if(AonStringUtils.isBlank(irpfExpression)) return "P";
		if(AonStringUtils.equalsIgnoreCase(irpfExpression, "_P")) return "I";
		if(AonStringUtils.equalsIgnoreCase(irpfExpression, "0.00")) return "E";
		if(AonStringUtils.containsIgnoreCase(irpfExpression, "BASE_CTA_ESP")) return "C";
		return "N/D";
	}

	private String getTaxedDescription(String irpfExpression) {
		if(AonStringUtils.isBlank(irpfExpression)) return "Personalizado";
		if(AonStringUtils.equalsIgnoreCase(irpfExpression, "_P")) return "Importe \u00cdntegro";
		if(AonStringUtils.equalsIgnoreCase(irpfExpression, "0.00")) return "Exento";
		if(AonStringUtils.containsIgnoreCase(irpfExpression, "BASE_CTA_ESP")) return "Ingreso a Cuenta";
		return "No definido";
	}

	private String getQuoteType(String quoteExpression) {
		if(AonStringUtils.isBlank(quoteExpression)) return "P";
		if(AonStringUtils.equalsIgnoreCase(quoteExpression, "_P")) return "I";
		if(AonStringUtils.equalsIgnoreCase(quoteExpression, "0.00")) return "E";
		if(AonStringUtils.containsIgnoreCase(quoteExpression, "PRORRATEAR")) return "PR";
		return "N/D";
	}

	private String getQuoteDescription(String quoteExpression) {
		if(AonStringUtils.isBlank(quoteExpression)) return "Personalizado";
		if(AonStringUtils.equalsIgnoreCase(quoteExpression, "_P")) return "Importe \u00cdntegro";
		if(AonStringUtils.equalsIgnoreCase(quoteExpression, "0.00")) return "Exento";
		if(AonStringUtils.containsIgnoreCase(quoteExpression, "PRORRATEAR")) return "Prorrateado";
		return "No definido";
	}

	private void paymentTableWidth() {
		paymentGrid.setWidth("100%");
		paymentGrid.getColumnFormatter().setWidth(0, "10%");
		paymentGrid.getColumnFormatter().setWidth(1, "30%");
		paymentGrid.getColumnFormatter().setWidth(2, "10%");
		paymentGrid.getColumnFormatter().setWidth(3, "10%");
		paymentGrid.getColumnFormatter().setWidth(4, "40%");
	}
	
	// ------------------------------------------ extraTable

	private void createExtraTable() {
		hideExtraMessage();
		extraScrollPanel.setWidth("100%");
		getExtraTableHeader();
		fillExtraTable();
		extraTableWidth();
	}

	private void getExtraTableHeader() {
		extraGrid.clear();
		extraGrid.resize(0, 4);
		int row = extraGrid.insertRow(extraGrid.getRowCount());
		
		Label start = new Label("F. Inicio");
		start.addStyleName(style.gridTitle());
		start.addStyleName(style.headerFSize());
		start.getElement().getStyle().setTextAlign(TextAlign.CENTER);
		extraGrid.setWidget(row, 0, start);
		extraGrid.getCellFormatter().addStyleName(row, 0, style.headerFixed());
		
		Label end = new Label("F. Fin");
		end.addStyleName(style.gridTitle());
		end.addStyleName(style.headerFSize());
		end.getElement().getStyle().setTextAlign(TextAlign.CENTER);
		extraGrid.setWidget(row, 1, end);
		extraGrid.getCellFormatter().addStyleName(row, 1, style.headerFixed());
		
		Label issue = new Label("F. Cobro");
		issue.addStyleName(style.gridTitle());
		issue.addStyleName(style.headerFSize());
		issue.getElement().getStyle().setTextAlign(TextAlign.CENTER);
		extraGrid.setWidget(row, 2, issue);
		extraGrid.getCellFormatter().addStyleName(row, 2, style.headerFixed());
		
		Label payment = new Label("Concepto");
		payment.addStyleName(style.gridTitle());
		payment.addStyleName(style.headerFSize());
		extraGrid.setWidget(row, 3, payment);
		extraGrid.getCellFormatter().addStyleName(row, 3, style.headerFixed());
		
		extraGrid.getRowFormatter().addStyleName(row, style.headerColor());
	}

	private void fillExtraTable() {
		for(AgreementExtra extra : agreement.getActiveExtras()) {
			int row = extraGrid.insertRow(extraGrid.getRowCount());
			
			Payment extraPayment = agreement.getPaymentById(extra.getAgreementPayment());
			
			Label startCell = new Label(parseExtraDate(extra.getStartDate()));
			startCell.getElement().getStyle().setTextAlign(TextAlign.CENTER);
			Label endCell = new Label(parseExtraDate(extra.getEndDate()));
			endCell.getElement().getStyle().setTextAlign(TextAlign.CENTER);
			Label issueCell = new Label(extra.getIssueDate());
			issueCell.getElement().getStyle().setTextAlign(TextAlign.CENTER);
			Label paymentCell = new Label(extraPayment.getDescription());
			
			extraGrid.setWidget(row, 0, startCell);
			extraGrid.setWidget(row, 1, endCell);
			extraGrid.setWidget(row, 2, issueCell);
			extraGrid.setWidget(row, 3, paymentCell);
			
			extraGrid.getCellFormatter().addStyleName(row, 0, style.extraCellHeight());
			extraGrid.getCellFormatter().addStyleName(row, 1, style.extraCellHeight());
			extraGrid.getCellFormatter().addStyleName(row, 2, style.extraCellHeight());
			extraGrid.getCellFormatter().addStyleName(row, 3, style.extraCellHeight());
			
			if(row % 2 == 0 ) extraGrid.getCellFormatter().addStyleName(row, 0, style.oddRow());
			if(row % 2 == 0 ) extraGrid.getCellFormatter().addStyleName(row, 1, style.oddRow());
			if(row % 2 == 0 ) extraGrid.getCellFormatter().addStyleName(row, 2, style.oddRow());
			if(row % 2 == 0 ) extraGrid.getCellFormatter().addStyleName(row, 3, style.oddRow());
		}
	}

	private String parseExtraDate(String date) {
		if(AonStringUtils.isBlank(date)) return "";
		
		if(AonStringUtils.contains(date, "-1")) {
			return AonStringUtils.split(date, '-')[0].trim() + " (A\u00f1o anterior)";
		} else return date;
	}

	private void extraTableWidth() {
		extraGrid.setWidth("100%");
		extraGrid.getColumnFormatter().setWidth(0, "20%");
		extraGrid.getColumnFormatter().setWidth(1, "20%");
		extraGrid.getColumnFormatter().setWidth(2, "20%");
		extraGrid.getColumnFormatter().setWidth(3, "40%");
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
	
	// ------------------------------------------ extra
	
	private void showExtraMessage() {
		agreementExtraMessage.getElement().getStyle().clearDisplay();
		extraTable.getElement().getStyle().setDisplay(Display.NONE);
	}
	
	private void hideExtraMessage() {
		extraTable.getElement().getStyle().clearDisplay();
		agreementExtraMessage.getElement().getStyle().setDisplay(Display.NONE);
	}
	
	// ------------------------------------------ DisclosurePanel

	private void initDiscPanels() {
		levelDiscPanel.setAnimationEnabled(true);
		levelDiscPanel.addOpenHandler(e -> handleIcon(levelDiscBtn, true));
		levelDiscPanel.addCloseHandler(e -> handleIcon(levelDiscBtn, false));
		
		salaryDiscPanel.setAnimationEnabled(true);
		salaryDiscPanel.addOpenHandler(e -> handleIcon(salaryDiscBtn, true));
		salaryDiscPanel.addCloseHandler(e -> handleIcon(salaryDiscBtn, false));
		salaryDiscPanel.setOpen(true);
		
		paymentDiscPanel.setAnimationEnabled(true);
		paymentDiscPanel.addOpenHandler(e -> handleIcon(paymentDiscBtn, true));
		paymentDiscPanel.addCloseHandler(e -> handleIcon(paymentDiscBtn, false));
		paymentDiscPanel.setOpen(true);
		
		extraDiscPanel.setAnimationEnabled(true);
		extraDiscPanel.addOpenHandler(e -> handleIcon(extraDiscBtn, true));
		extraDiscPanel.addCloseHandler(e -> handleIcon(extraDiscBtn, false));
	}

	private void createDiscPanelButtons() {
		levelDiscBtn = new AonToolbarButton("Desplegar Nivel / Categoria", AON.CSS.aonIconRight());
		salaryDiscBtn = new AonToolbarButton("Desplegar Tabla Salarial", AON.CSS.aonIconRight());
		paymentDiscBtn = new AonToolbarButton("Desplegar Devengos", AON.CSS.aonIconRight());
		extraDiscBtn = new AonToolbarButton("Desplegar Extras", AON.CSS.aonIconRight());
		
		levelDiscBtn.addClickHandler(e -> handleIcon(levelDiscBtn, levelDiscPanel.isOpen()));
		salaryDiscBtn.addClickHandler(e -> handleIcon(salaryDiscBtn, salaryDiscPanel.isOpen()));
		paymentDiscBtn.addClickHandler(e -> handleIcon(paymentDiscBtn, paymentDiscPanel.isOpen()));
		extraDiscBtn.addClickHandler(e -> handleIcon(extraDiscBtn, extraDiscPanel.isOpen()));
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
		
		AonToolbarButton agreementInfoButton = new AonToolbarButton("Informaci\u00f3n Convenio", AON.CSS.aonIconInfo());
		agreementInfoButton.addClickHandler(e -> 
			impl.getAgreementUsedInfo(agreement.getId(), agreement.getDescription(), new AsyncCallback<String>() {
				
				@Override
				public void onSuccess(String message) {
					AonDialog dialog = new AonDialog(agreement.getDescription(), new HTML(message));
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
						new PaymentsCleanDialog(agreement.getPayments()) {
							
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
						
					} else {
						AonDialog error = new AonDialog("Actualizaci\u00f3n no disponible", new HTMLPanel("Para poder actualizar un convenio a traves de ServiConvenios debe tener contrato el m\u00f3dulo."));
						error.warning();
					}
				}
				
				@Override
				public void onFailure(Throwable caught) {
					AonDialog error = new AonDialog("Error", new HTMLPanel(caught.getMessage()));
					error.warning();
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
	
	// ------------------------------------------------- Aon Messages panel

	private void showSuccess(String title, String message) {
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
