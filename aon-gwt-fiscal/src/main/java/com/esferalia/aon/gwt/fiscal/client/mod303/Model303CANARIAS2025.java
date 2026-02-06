package com.esferalia.aon.gwt.fiscal.client.mod303;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCnae2009Panel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDateBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDoubleBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTextBox;
import com.esferalia.aon.gwt.fiscal.client.mod303.Model303.Model303Callback;
import com.esferalia.aon.gwt.fiscal.client.model.FiscalModelAdmonPanel;
import com.esferalia.aon.gwt.fiscal.client.model.FiscalModelAdmonPanel.IFiscalModelAdmonPanelCallback;
import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.api.model.fiscal.mod303.Model3032025CANARIASAdditionalDataScript;
import com.esferalia.aon.occam.api.model.fiscal.mod303.Model3032025CANARIASIgicScript1;
import com.esferalia.aon.occam.api.model.fiscal.mod303.Model3032025CANARIASIgicScript2;
import com.esferalia.aon.occam.api.model.fiscal.mod303.Model3032025CANARIASLastPeriodScript1;
import com.esferalia.aon.occam.api.model.fiscal.mod303.Model3032025CANARIASLastPeriodScript2;
import com.esferalia.aon.occam.api.model.fiscal.mod303.Model3032026CANARIASIgicScript1;
import com.esferalia.aon.occam.api.model.type.Mod303Key;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;

class Model303CANARIAS2025 extends Model303Base {

	protected AonTextBox receiptBox;

	private ScrollPanel lastPeriodPanel;
	private TabLayoutPanel tabPanel;
	
	private CheckBox withoutActivityCheck;
	private AonTextBox previousReceiptBox;
	
	private CheckBox cm2;   // Inscrito en el Registro de devolución mensual (Solo modelo 417)
	private CheckBox x01;   // Autoliquidación conjunta (solo modelo 420)
	private CheckBox x02;   // Ha optado por el régimen especial del criterio de caja 
	private CheckBox x03;   // Ha sido destinatario de operaciones a las que se aplique el régimen especial del criterio de caja
	private CheckBox x04;   // Es una entidad no establecida con obligaciones periódicas
	private CheckBox x05;   // Ha sido declarado en concurso de acreedores en el presente período de liquidación
	private AonDateBox x06; // Fecha en que se dictó el auto de declaración de concurso
	private ListBox x07;    // Tipo de autoliquidación si declaración de concurso (preconcursal, postconcursal)
	
	private static final int LIQUIDATION_TAB = 2;

	protected Model303CANARIAS2025(Mod303 mod303,Model303Callback callback) {
		super(mod303,callback);
		tabPanel = new TabLayoutPanel(26, Unit.PX);
		
		SimpleLayoutPanel centerPanel = new SimpleLayoutPanel();
		centerPanel.addStyleName(AON.CSS.aonScrollArea());
		centerPanel.setWidget(tabPanel);
		add(centerPanel);
		
		paintIdentificationTab(tabPanel);
		paintDeclarationTab(tabPanel);
		paintLiquidationTab(tabPanel);
		showPaymentInfo(getModel());
		paintAdditionalDataTab(tabPanel);
		if (getModel().isMonthPeriod() && getModel().isLastPeriod()) {
			paintLastPeriodInformationTab(tabPanel); // Ultimo periodo Modelo 417
		}
		paintAdministrationTab(tabPanel);
		Scheduler.get().scheduleDeferred(this::selectDefaultTab);
	}
	
	private void selectDefaultTab() {
		tabPanel.selectTab(LIQUIDATION_TAB);
	}
	
	private void paintLiquidationTab(TabLayoutPanel tabPanel) {
		ScrollPanel generalRegimeScrollPanel = new ScrollPanel();
		FlowPanel container = new FlowPanel();
		FlexTable table = new FlexTable();
		table.setWidth("100%");
		table.addStyleName(AON.CSS.aonMarginBottom());
		table.getColumnFormatter().setWidth(0, "auto");
		table.getColumnFormatter().addStyleName(0, AON.CSS.aonPaddingLeft() );
		table.getColumnFormatter().addStyleName(0, AON.CSS.aonPaddingRight() );
		table.getColumnFormatter().setWidth(1, "40px");
		table.getColumnFormatter().setStyleName(1, AON.CSS.aonTextCenter());
		table.getColumnFormatter().setWidth(2, WIDTH_140PX);
		table.getColumnFormatter().setWidth(3, "40px");
		table.getColumnFormatter().setStyleName(3, AON.CSS.aonTextCenter());
		table.getColumnFormatter().setWidth(4, "60px");
		table.getColumnFormatter().setWidth(5, "40px");
		table.getColumnFormatter().setStyleName(5, AON.CSS.aonTextCenter());
		table.getColumnFormatter().setWidth(6, WIDTH_140PX);
		table.getColumnFormatter().setWidth(7, "50px");
		if (getModel().getYear() >= 2026) {
			paintDeclaration(table, Model3032026CANARIASIgicScript1.values(), 8);
		} else {
			paintDeclaration(table, Model3032025CANARIASIgicScript1.values(), 8);
		}
		container.add(table);
		
		table = new FlexTable();
		table.setWidth("100%");
		table.addStyleName(AON.CSS.aonMarginBottom());
		table.getColumnFormatter().setWidth(0, "auto");
		table.getColumnFormatter().addStyleName(0, AON.CSS.aonPaddingLeft() );
		table.getColumnFormatter().addStyleName(0, AON.CSS.aonPaddingRight() );
		table.getColumnFormatter().setWidth(1, "40px");
		table.getColumnFormatter().setStyleName(1, AON.CSS.aonTextCenter());
		table.getColumnFormatter().setWidth(2, WIDTH_140PX);
		
		table.getColumnFormatter().setWidth(3, "40px");
		table.getColumnFormatter().setStyleName(3, AON.CSS.aonTextCenter());
		table.getColumnFormatter().setWidth(4, WIDTH_140PX);
		table.getColumnFormatter().setWidth(5, "50px");
		paintDeclaration(table, Model3032025CANARIASIgicScript2.values(), 6);
		container.add(table);
		
		generalRegimeScrollPanel.setWidget(container);
		tabPanel.add(generalRegimeScrollPanel, AON.MSG.liquidacion());
	}

	private void paintAdditionalDataTab(TabLayoutPanel tabPanel) {
		ScrollPanel additionalDataScrollPanel = new ScrollPanel();
		FlexTable table = new FlexTable();
		table.setWidth("100%");
		table.addStyleName(AON.CSS.aonMarginBottom());
		
		table.getColumnFormatter().setWidth(0, "auto");
		table.getColumnFormatter().addStyleName(0, AON.CSS.aonPaddingLeft() );
		table.getColumnFormatter().addStyleName(0, AON.CSS.aonPaddingRight() );

		table.getColumnFormatter().setWidth(1, "40px");
		table.getColumnFormatter().setStyleName(1, AON.CSS.aonTextCenter());
		table.getColumnFormatter().setWidth(2, WIDTH_140PX);
		
		table.getColumnFormatter().setWidth(3, "40px");
		table.getColumnFormatter().setStyleName(3, AON.CSS.aonTextCenter());
		table.getColumnFormatter().setWidth(4, WIDTH_140PX);
		
		table.getColumnFormatter().setWidth(5, "50px");
		additionalDataScrollPanel.setWidget(table);
		tabPanel.add(additionalDataScrollPanel, AON.MSG.additionalData());
		paintDeclaration(table,Model3032025CANARIASAdditionalDataScript.values(),3);		
	}
		
	private void paintDeclarationTab(TabLayoutPanel tabPanel) {
		ScrollPanel declarationScrollPanel = new ScrollPanel();
		FlowPanel container = new FlowPanel();

		FlexTable table = createTable();
		withoutActivityCheck = paintWithoutActivityCheck(table);	// Sin actividad

		if (getModel().isMonthPeriod()) {
			cm2 = paintCheck(Mod303Key.CM_002,table);  // Inscrito en el Registro de devolución mensual (Solo modelo 417)	
		} else {
			x01 = paintCheck(Mod303Key.CA_X01,table);  // Autoliquidación conjunta (solo modelo 420)		
		}
		x02 = paintCheck(Mod303Key.CA_X02,table);  // Ha optado por el régimen especial del criterio de caja 
		x03 = paintCheck(Mod303Key.CA_X03,table);  // Ha sido destinatario de operaciones a las que se aplique el régimen especial del criterio de caja
		x04 = paintCheck(Mod303Key.CA_X04,table);  // Es una entidad no establecida con obligaciones periódicas
		x05 = paintCheck(Mod303Key.CA_X05,table);  // Ha sido declarado en concurso de acreedores en el presente período de liquidación
		x06 = paintDate(Mod303Key.CA_X06,table);   // Fecha en que se dictó el auto de declaración de concurso
		x07 = new ListBox(); 					   // Tipo de autoliquidación si declaración de concurso (preconcursal, postconcursal)
		x07.setWidth("200px");
		x07.addItem("NO", "0");
		x07.addItem("(1) SI Preconcursal", "1");
		x07.addItem("(2) SI Postconcursal", "2");
		paintListBox(x07, Mod303Key.CA_X07, table);
		
		int row = table.getRowCount();
		paintLabel(table, row, AON.MSG.receipt());
		
		table.getFlexCellFormatter().addStyleName(row, 0,AON.CSS.aonPaddingLeft() );
		table.getFlexCellFormatter().addStyleName(row, 0,AON.CSS.aonBorderBottom() );
		
		receiptBox = new AonTextBox();
		receiptBox.setVisibleLength(15);
		receiptBox.setMaxLength(13);
		receiptBox.setEnabled(getModel().isEditable());
		receiptBox.setValue( getModel().getNumber() );
		receiptBox.addValueChangeHandler( event -> {
			getModel().setNumber(receiptBox.getValue());
			markAsDirty();
		});
		table.setWidget(row, 1, receiptBox);

		// Complementaria: Numero justificante de la declaración anterior
		if (getModel().isComplementary()) {
			row = table.getRowCount();
			paintLabel(table, row, AON.MSG.previousReceipt());
			
			table.getFlexCellFormatter().addStyleName(row, 0,AON.CSS.aonPaddingLeft() );
			table.getFlexCellFormatter().addStyleName(row, 0,AON.CSS.aonBorderBottom() );
			previousReceiptBox = new AonTextBox();
			previousReceiptBox.setVisibleLength(15);
			previousReceiptBox.setMaxLength(13);
			previousReceiptBox.setEnabled(getModel().isEditable());
			previousReceiptBox.setValue( getModel().getReplacedNumber() );
			previousReceiptBox.addValueChangeHandler( event -> {
				getModel().setReplacedNumber(previousReceiptBox.getValue());
				markAsDirty();
			});
			table.setWidget(row, 1, previousReceiptBox);
		}	
		
		container.add(addGroupPanel("", table));
		
		declarationScrollPanel.setWidget(container);
		tabPanel.add(declarationScrollPanel, AON.MSG.declaration());
	}
	
	private FlexTable createTable() {
		FlexTable table = new FlexTable();
		table.setWidth("100%");
		table.addStyleName(AON.CSS.aonMarginBottom());
		
		table.getColumnFormatter().setWidth(0, "auto");
		table.getColumnFormatter().setWidth(1, "300px");
		table.getColumnFormatter().addStyleName(0, AON.CSS.aonPaddingLeft() );
		table.getColumnFormatter().addStyleName(0, AON.CSS.aonPaddingRight() );
		return table;
	}
	
	private void paintLastPeriodInformationTab(TabLayoutPanel tabPanel) {
		lastPeriodPanel = new ScrollPanel();
		fillLastPeriodInformationScrollPanel();
		tabPanel.add(lastPeriodPanel, "Exonerados Resumen Anual");
	}

	private void fillLastPeriodInformationScrollPanel() {
		
		// Modelo 417:
		// Exclusivamente a cumplimentar en el último período de liquidación por aquellos sujetos pasivos que 
		// queden exonerados de la declaración-resumen anual de I.G.I.C.
		
		FlexTable table = new FlexTable();
		table.setWidth("100%");
		table.addStyleName(AON.CSS.aonMarginTop());
		table.addStyleName(AON.CSS.aonMarginBottom());
		
		table.getColumnFormatter().setWidth(0, "auto");
		table.getColumnFormatter().addStyleName(0, AON.CSS.aonPaddingLeft() );
		table.getColumnFormatter().addStyleName(0, AON.CSS.aonPaddingRight() );
		table.getColumnFormatter().setWidth(1, "40px");
		table.getColumnFormatter().setStyleName(1, AON.CSS.aonTextCenter());
		table.getColumnFormatter().setWidth(2, WIDTH_140PX);
		table.getColumnFormatter().setWidth(3, "50px");
		
		int row = 0;
		
		table.setWidget(row, 0, new Label("Exclusivamente a cumplimentar en el \u00FAltimo per\u00EDodo de liquidaci\u00F3n por aquellos sujetos pasivos que queden exonerados de la declaraci\u00F3n-resumen anual de I.G.I.C."));
		table.getFlexCellFormatter().setStyleName(row, 0, AON.CSS.aonTextCenter());
		table.getFlexCellFormatter().addStyleName(row, 0, AON.CSS.aonBold());
		table.getFlexCellFormatter().addStyleName(row, 0, AON.CSS.aonItalic());
		table.getFlexCellFormatter().addStyleName(row, 0, AON.CSS.aonBorder());
		table.getFlexCellFormatter().setColSpan(row, 0, 4);
		
		// 10.- Datos estadísticos - Actividades a las que se refiere la declaración

		paintLabel(table, ++row, AON.MSG.activities(),true);
		table.getFlexCellFormatter().addStyleName(table.getRowCount()-1, 0, AON.CSS.aonPaddingTop());

		FlowPanel actContainer = new FlowPanel();
		actContainer.setStyleName(AON.CSS.aonBlockCenter());
		actContainer.addStyleName(AON.CSS.aonWidthAlmostAll());

		FlexTable tab = new FlexTable();
		tab.addStyleName(AON.CSS.aonTable());
		tab.addStyleName(AON.CSS.aonMarginBottom());
		
		tab.getColumnFormatter().setWidth(0, "40px");  // Principal/Otras
		tab.getColumnFormatter().setWidth(1, "80px");  // Epígrafe (Código)
		tab.getColumnFormatter().setWidth(2, "60px");  // Clave
		tab.getColumnFormatter().setWidth(3, "250px"); // Descripción
		tab.getColumnFormatter().setWidth(4, "90px");  // Régimen aplicable
		tab.getColumnFormatter().setWidth(5, "auto");  // Casilla vacia
		
		tab.setWidget(1, 0, new Label() );
		
		tab.setWidget(1, 1, new Label(AON.MSG.epigraph()));
		tab.getFlexCellFormatter().addStyleName(1,1,AON.CSS.aonBold());
		tab.getFlexCellFormatter().addStyleName(1,1,AON.CSS.aonBorderBottom());
		
		tab.setWidget(1, 2, new Label(AON.MSG.key()));
		tab.getFlexCellFormatter().addStyleName(1,2,AON.CSS.aonBold());
		tab.getFlexCellFormatter().addStyleName(1,2,AON.CSS.aonBorderBottom());
		
		tab.setWidget(1, 3, new Label(AON.MSG.description()));
		tab.getFlexCellFormatter().addStyleName(1,3,AON.CSS.aonBold());
		tab.getFlexCellFormatter().addStyleName(1,3,AON.CSS.aonBorderBottom());
		
		tab.setWidget(1, 4, new Label("R\u00E9gimen aplicable"));
		tab.getFlexCellFormatter().addStyleName(1,3,AON.CSS.aonBold());
		tab.getFlexCellFormatter().addStyleName(1,3,AON.CSS.aonBorderBottom());
		
		tab.setWidget(1, 5, new Label() );
		tab.getFlexCellFormatter().addStyleName(1,4,AON.CSS.aonBold());
		tab.getFlexCellFormatter().addStyleName(1,4,AON.CSS.aonBorderBottom());
		
		paintActivityRow(tab, Mod303Key.CA_U1D, Mod303Key.CA_U1C, Mod303Key.CA_U1E, Mod303Key.CA_U1R); // Principal
		paintActivityRow(tab, Mod303Key.CA_U2D, Mod303Key.CA_U2C, Mod303Key.CA_U2E, Mod303Key.CA_U2R); // Otras 
		paintActivityRow(tab, Mod303Key.CA_U3D, Mod303Key.CA_U3C, Mod303Key.CA_U3E, Mod303Key.CA_U3R); // Otras 
		paintActivityRow(tab, Mod303Key.CA_U4D, Mod303Key.CA_U4C, Mod303Key.CA_U4E, Mod303Key.CA_U4R); // Otras 
		paintActivityRow(tab, Mod303Key.CA_U5D, Mod303Key.CA_U5C, Mod303Key.CA_U5E, Mod303Key.CA_U5R); // Otras 
		
		actContainer.add(tab);
		table.setWidget(++row, 0, actContainer);
		table.getFlexCellFormatter().setColSpan(row, 0, 4);
		
		// 11.- Operaciones realizadas en el ejercicio
		
		paintScript(table, Model3032025CANARIASLastPeriodScript1.values(), 3);
		
		// 12.- Prorrata
		
		paintLabel(table, table.getRowCount(), AON.MSG.prorrata(), true);
		table.getFlexCellFormatter().addStyleName(table.getRowCount()-1, 0, AON.CSS.aonPaddingTop());
		
		FlowPanel actContainer2 = new FlowPanel();
		actContainer2.setStyleName(AON.CSS.aonBlockCenter());
		actContainer2.addStyleName(AON.CSS.aonWidthAlmostAll());

		FlexTable tab2 = new FlexTable();
		tab2.addStyleName(AON.CSS.aonTable());
		tab2.addStyleName(AON.CSS.aonMarginBottom());
		
		tab2.getColumnFormatter().setWidth(0, "80px");
		tab2.getColumnFormatter().setWidth(1, "20px");
		tab2.getColumnFormatter().setWidth(2, WIDTH_150PX);
		tab2.getColumnFormatter().setWidth(3, WIDTH_150PX);
		tab2.getColumnFormatter().setWidth(4, "80px");
		tab2.getColumnFormatter().setWidth(5, WIDTH_150PX);
		tab2.getColumnFormatter().setWidth(6, "auto");
		
		tab2.setWidget(1, 0, new Label( "C.N.A.E.") ); 
		tab2.getFlexCellFormatter().addStyleName(1,0,AON.CSS.aonBold());
		tab2.getFlexCellFormatter().addStyleName(1,0,AON.CSS.aonBorderBottom());
		tab2.setWidget(1, 1, new Label() ); 
		tab2.getFlexCellFormatter().addStyleName(1,1,AON.CSS.aonBold());
		tab2.getFlexCellFormatter().addStyleName(1,1,AON.CSS.aonBorderBottom());
		tab2.setWidget(1, 2, new Label( AON.MSG.operationsAmount()) );
		tab2.getFlexCellFormatter().addStyleName(1,2,AON.CSS.aonBold());
		tab2.getFlexCellFormatter().addStyleName(1,2,AON.CSS.aonBorderBottom());
		tab2.setWidget(1, 3, new Label( AON.MSG.operationsAmountWithRight()) );
		tab2.getFlexCellFormatter().addStyleName(1,3,AON.CSS.aonBold());
		tab2.getFlexCellFormatter().addStyleName(1,3,AON.CSS.aonBorderBottom());
		tab2.setWidget(1, 4, new Label( AON.MSG.type()) );
		tab2.getFlexCellFormatter().addStyleName(1,4,AON.CSS.aonBold());
		tab2.getFlexCellFormatter().addStyleName(1,4,AON.CSS.aonBorderBottom());
		tab2.setWidget(1, 5, new Label( AON.MSG.prorrataPercent()) );
		tab2.getFlexCellFormatter().addStyleName(1,5,AON.CSS.aonBold());
		tab2.getFlexCellFormatter().addStyleName(1,5,AON.CSS.aonBorderBottom());
		tab2.setWidget(1, 6, new Label() ); 
		tab2.getFlexCellFormatter().addStyleName(1,6,AON.CSS.aonBold());
		tab2.getFlexCellFormatter().addStyleName(1,6,AON.CSS.aonBorderBottom());
		
		paintProrrateRow(tab2, Mod303Key.CA_P1C, Mod303Key.CA_P1I, Mod303Key.CA_P1D, Mod303Key.CA_P1T, Mod303Key.CA_P1P);
		paintProrrateRow(tab2, Mod303Key.CA_P2C, Mod303Key.CA_P2I, Mod303Key.CA_P2D, Mod303Key.CA_P2T, Mod303Key.CA_P2P);
		paintProrrateRow(tab2, Mod303Key.CA_P3C, Mod303Key.CA_P3I, Mod303Key.CA_P3D, Mod303Key.CA_P3T, Mod303Key.CA_P3P);
		paintProrrateRow(tab2, Mod303Key.CA_P4C, Mod303Key.CA_P4I, Mod303Key.CA_P4D, Mod303Key.CA_P4T, Mod303Key.CA_P4P);
		paintProrrateRow(tab2, Mod303Key.CA_P5C, Mod303Key.CA_P5I, Mod303Key.CA_P5D, Mod303Key.CA_P5T, Mod303Key.CA_P5P);

		actContainer2.add(tab2);
		
		row = table.getRowCount();
		table.setWidget(row, 0, actContainer2);
		table.getFlexCellFormatter().setColSpan(row, 0, 4);
		
		// 13.- Actividades con regímenes de deducción diferenciados
		
		row = table.getRowCount();
		paintLabel(table, row, "Actividades con reg\u00EDmenes de deducci\u00F3n diferenciados", true);
		table.setWidget(row, 1, new Label());
		table.setWidget(row, 2, new Label("Base"));
		table.setWidget(row, 3, new Label());
		table.setWidget(row, 4, new Label("Cuota"));
		table.getFlexCellFormatter().addStyleName(row, 2, AON.CSS.aonTextCenter());
		table.getFlexCellFormatter().addStyleName(row, 4, AON.CSS.aonTextCenter());
		
		paintScript(table,Model3032025CANARIASLastPeriodScript2.values(),5);
		
		lastPeriodPanel.setWidget(table);
	}
	
	private void paintActivityRow(FlexTable tab, Mod303Key desKey, Mod303Key keyKey, Mod303Key epiKey, Mod303Key regKey) {

		int row = tab.getRowCount();

		// Principal/Otras
		tab.setWidget(row, 0, new Label(row == 2 ? "Principal" : "Otras"));

		// Epígrafe (código)
		AonTextBox epi = new AonTextBox();
		epi.setVisibleLength(5);
		epi.setMaxLength(5);
		epi.setValue(getModel().getDescription(epiKey));
		epi.addValueChangeHandler(event -> {
			getModel().putDescription(epiKey,epi.getValue());
			markAsDirty();
		});
		tab.setWidget(row, 1, epi);
		
		// Clave
		// 1 - Actividades sujetas al Impuesto de Actividades Económicas (Actividades empresariales)
		// 2 - Actividades sujetas al Impuesto de Actividades Económicas (Actividades Profesionales y Artísticas)
		// 3 - Actividades arrendadoras de locales de negocios
		// 4 - Actividades Agrícolas y Ganaderas no sujetas al IAE
		// 5 - Sujetos pasivos que no hayan iniciado su actividad y no estén dados de alta en el IAE
		ListBox key = new ListBox();
		key.addItem("---","");
		key.addItem("Actividades sujetas al IAE (Actividades empresariales)", "1");
		key.addItem("Actividades sujetas al IAE (Actividades Profesionales y Art\u00EDsticas)", "2");
		key.addItem("Actividades arrendadoras de locales de negocios", "3");
		key.addItem("Actividades Agr\u00EDcolas y Ganaderas no sujetas al IAE", "4");
		key.addItem("Suj. pas. que no han iniciado su actividad y no est\u00E9n de alta en IAE", "5");
		setSelectedIndex(key, getModel().getDescription(keyKey));
		key.addChangeHandler(event ->  {
			getModel().putDescription(keyKey, key.getSelectedValue());
			markAsDirty();
		});
		tab.setWidget(row, 2, key);
	
		// Descripción epígrafe
		AonTextBox description = new AonTextBox();
		description.setVisibleLength(40);
		description.setMaxLength(40);
		description.setValue(getModel().getDescription(desKey));
		description.addValueChangeHandler(event -> {
			getModel().putDescription(desKey,description.getValue());
			markAsDirty();
		});
		tab.setWidget(row, 3, description);
		
		// Régimen aplicable
		// 1 - Régimen ordinario
		// 2 - Régimen especial de bienes usados
		// 3 - Régimen especial de objetos de arte, antigüedades y objetos de colección
		// 4 - Régimen especial de comerciantes minoristas
		// 6 - Régimen especial de la agricultura, ganadería y pesca
		// 7 - Régimen especial de agencias de viajes
		// 8 - Régimen especial aplicable a las operaciones con oro de inversión
		// 10 - Régimen especial del pequeño empresario o profesional
		// 11 - Régimen especial del criterio de caja		
		ListBox reg = new ListBox();
		reg.addItem("---","");
		reg.addItem("R\u00E9gimen ordinario","1");
		reg.addItem("R\u00E9gimen especial de bienes usados","2");
		reg.addItem("R\u00E9gimen especial de obj. de arte, antig. y obj. de colecci\u00F3n","3");
		reg.addItem("R\u00E9gimen especial de comerciantes minoristas","4");
		reg.addItem("R\u00E9gimen especial de la agricultura, ganader\u00EDa y pesca","6");
		reg.addItem("R\u00E9gimen especial de agencias de viajes","7");
		reg.addItem("R\u00E9gimen especial aplicable a las op. con oro de inversi\u00F3n","8");
		reg.addItem("R\u00E9gimen especial del peque\u00F1o empresario o profesional","10");
		reg.addItem("R\u00E9gimen especial del criterio de caja","11");
		setSelectedIndex(reg, getModel().getDescription(regKey));
		reg.addChangeHandler(event ->  {
			getModel().putDescription(regKey, reg.getSelectedValue());
			markAsDirty();
		});
		tab.setWidget(row, 4, reg);

		// Casilla en blanco
		tab.setWidget(row, 5, new Label() );
	
	}
	
	private void paintProrrateRow(FlexTable tab, Mod303Key cnaeKey, Mod303Key amountKey, Mod303Key amountRightKey, Mod303Key typeKey, Mod303Key percentKey) {

		int row = tab.getRowCount();
		
		// CNAE
		// EN EL PROGRAMA DE AYUDA EL CNAE ES DE 3 DIGITOS, SE GRABARA COMPLETO PARA APROVECHAR LA TABLA QUE HAY AHORA Y LUEGO AL CREAR EL ARCHIVO SE PONDRAN SOLO 3 
		
		AonTextBox cnae = new AonTextBox();
		cnae.setVisibleLength(4);
		cnae.setMaxLength(4);
		cnae.setValue(getModel().getDescription(cnaeKey));
		cnae.addValueChangeHandler(event -> {
			getModel().putDescription(cnaeKey, cnae.getValue());
			markAsDirty();
		});
		tab.setWidget(row, 0, cnae);

		AonCnae2009Panel panel = new AonCnae2009Panel();
		panel.addSelectionHandler(event -> {
			cnae.setValue(event.getSelectedItem().getCodeWithoutPoint(),false);
			getModel().putDescription(cnaeKey,event.getSelectedItem().getCodeWithoutPoint());
			markAsDirty();
		});
		AonTableButton button = new AonTableButton("CNAE",AON.CSS.aonIconSearch());
		button.addClickHandler(event -> panel.onShow());
		tab.setWidget(row, 1, button);
		
		// Importe total de las operaciones
		
		AonDoubleBox amount = new AonDoubleBox();
		amount.setValue(getModel().getAmount(amountKey));
		amount.addValueChangeHandler(event -> {
			getModel().putAmount(amountKey, amount.getValue() );
			markAsDirty();
		});
		tab.setWidget(row, 2, amount);
		
		// Importe operaciones con derecho a deducción
		
		AonDoubleBox amountRight = new AonDoubleBox();
		amountRight.setValue(getModel().getAmount(amountRightKey));
		amountRight.addValueChangeHandler(event -> {
			getModel().putAmount(amountRightKey, amountRight.getValue() );
			markAsDirty();
		});
		tab.setWidget(row, 3, amountRight);
		
		// Tipo (G - General, E - Especial)
		
		ListBox typeBox = new ListBox();
		typeBox.setWidth("40px");
		typeBox.addItem(" - ", "");
		typeBox.addItem("G - General", "G");
		typeBox.addItem("E - Especial", "E");
		setSelectedIndex(typeBox, getModel().getDescription(typeKey));
		typeBox.addChangeHandler(event ->  {
			getModel().putDescription(typeKey,typeBox.getSelectedValue());
			markAsDirty();
		});
		tab.setWidget(row, 4, typeBox);
		
		// Porcentaje de prorrata
		
		AonDoubleBox percent = new AonDoubleBox();
		percent.setValue(getModel().getAmount(percentKey));
		percent.addValueChangeHandler(event -> {
			getModel().putAmount(percentKey, percent.getValue() );
			markAsDirty();
		});
		tab.setWidget(row, 5, percent);
		
	}
	
    private void setSelectedIndex(ListBox listBox, String value) {
		if (AonStringUtils.isBlank(value)) {
			listBox.setSelectedIndex(0);	
		} else {
			for (int i = 0; i < listBox.getItemCount(); i++) {
				if (listBox.getValue(i).equals(value)) {
					listBox.setSelectedIndex(i);
				}
			}
		}
	}
	
	@Override
	protected void decorateDeclarationTab() {
		super.decorateDeclarationTab();
		enable(withoutActivityCheck);
		enable(cm2);
		enable(x01);
		enable(x02);
		enable(x03);
		enable(x04);
		enable(x05);
		enable(x06);
		enable(x07);
		enable(previousReceiptBox);
		enable(receiptBox);
		if (receiptBox != null) {
			receiptBox.setValue(getModel().getNumber());
		}
	}
	
	protected void paintAdministrationTab(TabLayoutPanel tabPanel) {
		IFiscalModelAdmonPanelCallback<Mod303, Model303ModuleOptions> cbk = 
				new IFiscalModelAdmonPanelCallback<Mod303, Model303ModuleOptions>() {

					@Override
					public Model303ModuleOptions getOptions() {
						return getCallback().getOptions();
					}

					@Override
					public Mod303 getModel() {
						return Model303CANARIAS2025.this.getModel();
					}

					@Override
					public void showError(String msg) {
						getCallback().showError(msg);
					}

					@Override
					public String getValidatePrintAction() {
						return GWT.getHostPageBaseURL() +"aon_gwt_fiscal/ms/Mod420ValidatePrintATC";
					}

					@Override
					public String getDownloadFileAction() {						
						return GWT.getHostPageBaseURL() +"aon_gwt_fiscal/ms/Model420File";
					}

					@Override
					public String getSendAction() {
						return null;
					}

					@Override
					public void sendSuccessfully() {
						Model303.service.getMod303(getCallback().getOptions().getOccam(), 
								getModel().getId(), new AsyncCallback<Mod303>() {
							@Override
							public void onSuccess(Mod303 selected) {
								selectAndPopulate(selected);
								showPaymentInfo(selected);
							}
							@Override
							public void onFailure(Throwable caught) {
								getCallback().showError(AON.MSG.unableToReadDeclaration(caught.getMessage()));
							}
						});
					}

					@Override
					public String getCheckAction() {
						return null;
					}

					@Override
					public String getCheckDataResponseDataAction() {
						return GWT.getHostPageBaseURL() +"aon_gwt_fiscal/ms/Mod303CheckDataResponseData";
					}

					@Override
					public String getModelInformationURL() {
						return "https://www3.gobiernodecanarias.org/tributos/atc/jsf/publico/asistenciaContribuyente/modelos/listado.jsp?tributo=IGIC&jftfdi=&jffi=listado.jsp";
					}
			};
			admonPanel = new FiscalModelAdmonPanel<>(cbk);
			tabPanel.add(admonPanel, AON.MSG.administrationName(getModel().getAdministration()));		
	}
	
}
