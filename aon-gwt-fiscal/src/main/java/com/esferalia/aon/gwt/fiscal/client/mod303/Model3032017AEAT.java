package com.esferalia.aon.gwt.fiscal.client.mod303;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.fiscal.client.FiscalModelUtils;
import com.esferalia.aon.gwt.fiscal.client.mod303.Model303.Model303Callback;
import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.api.model.fiscal.mod303.Model3032017AEATAdditionalDataScript;
import com.esferalia.aon.occam.api.model.fiscal.mod303.Model3032017AEATGeneralRegimeScript1;
import com.esferalia.aon.occam.api.model.fiscal.mod303.Model3032017AEATGeneralRegimeScript2;
import com.esferalia.aon.occam.api.model.fiscal.mod303.Model3032017AEATResultScript;
import com.esferalia.aon.occam.api.model.type.Mod303Key;
import com.esferalia.aon.watson.util.Pair;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.TextBox;

public class Model3032017AEAT extends Model303Base {
	private static final String VALIDATE_PRINT_ACTION = "/aon_gwt_fiscal/Model303PrintAEAT";
	
	public Model3032017AEAT(Mod303 mod303,Model303Callback callback) {
		super(mod303,callback);
		TabLayoutPanel tabPanel = new TabLayoutPanel(26, Unit.PX);
		SimpleLayoutPanel centerPanel = new SimpleLayoutPanel();
		centerPanel.addStyleName(AON.AON_CSS.aonScrollArea());
		centerPanel.setWidget(tabPanel);
		add(centerPanel);
		
		paintIdentificationTab(getCallback(),tabPanel);
		paintDeclarationTab(mod303,getCallback(),tabPanel);
		paintGeneralRegimenTab(getCallback(),tabPanel);
		paintSimplifiedRegimenTab(getCallback(),tabPanel);
		paintResultTab(getCallback(),tabPanel);
		paintAdditionalDataTab(getCallback(),tabPanel);
		paintAdministrationTab(getCallback(),tabPanel);
	}
	
	
	private void paintIdentificationTab(Model303Callback callback, TabLayoutPanel tabPanel) {
		Model303IdentificationData identificationData = new Model303IdentificationData( new Model303IdentificationDataCallback()) ;
		tabPanel.add(identificationData, TAB_TEMPLATE.render(AON.MSG.identification(), AON.AON_CSS.aonIconIdentification()));
	}
	
	private void paintGeneralRegimenTab(Model303Callback callback,TabLayoutPanel tabPanel) {
		ScrollPanel generalRegimeScrollPanel = new ScrollPanel();
		FlowPanel container = new FlowPanel();
		FlexTable table = new FlexTable();
		table.setWidth("100%");
		table.addStyleName(AON.AON_CSS.aonMarginBottom());
		table.getColumnFormatter().setWidth(0, "auto");
		table.getColumnFormatter().addStyleName(0, AON.AON_CSS.aonPaddingLeft() );
		table.getColumnFormatter().addStyleName(0, AON.AON_CSS.aonPaddingRight() );
		table.getColumnFormatter().setWidth(1, "40px");
		table.getColumnFormatter().setStyleName(1, AON.AON_CSS.aonTextCenter());
		table.getColumnFormatter().setWidth(2, "140px");
		table.getColumnFormatter().setWidth(3, "40px");
		table.getColumnFormatter().setStyleName(3, AON.AON_CSS.aonTextCenter());
		table.getColumnFormatter().setWidth(4, "60px");
		table.getColumnFormatter().setWidth(5, "40px");
		table.getColumnFormatter().setStyleName(5, AON.AON_CSS.aonTextCenter());
		table.getColumnFormatter().setWidth(6, "140px");
		table.getColumnFormatter().setWidth(7, "50px");
		paintDeclaration(table,Model3032017AEATGeneralRegimeScript1.values(),8);
		container.add(table);
		
		table = new FlexTable();
		table.setWidth("100%");
		table.addStyleName(AON.AON_CSS.aonMarginBottom());
		table.getColumnFormatter().setWidth(0, "auto");
		table.getColumnFormatter().addStyleName(0, AON.AON_CSS.aonPaddingLeft() );
		table.getColumnFormatter().addStyleName(0, AON.AON_CSS.aonPaddingRight() );
		table.getColumnFormatter().setWidth(1, "40px");
		table.getColumnFormatter().setStyleName(1, AON.AON_CSS.aonTextCenter());
		table.getColumnFormatter().setWidth(2, "140px");
		
		table.getColumnFormatter().setWidth(3, "40px");
		table.getColumnFormatter().setStyleName(3, AON.AON_CSS.aonTextCenter());
		table.getColumnFormatter().setWidth(4, "140px");
		table.getColumnFormatter().setWidth(5, "50px");
		paintDeclaration(table,Model3032017AEATGeneralRegimeScript2.values(),6);
		container.add(table);
		
		generalRegimeScrollPanel.setWidget(container);
		tabPanel.add(generalRegimeScrollPanel, TAB_TEMPLATE.render(AON.MSG.generalRegime(), AON.AON_CSS.aonIconModel()));
	}
	
	private void paintSimplifiedRegimenTab(Model303Callback callback,TabLayoutPanel tabPanel) {
		ScrollPanel simplifiedRegimeScrollPanel = new ScrollPanel();
		// ---
		simplifiedRegimeScrollPanel.addStyleName(AON.AON_CSS.aonTextCenter());
		Label notYet = new Label("NO IMPLEMENTADO");
		notYet.setStyleName(AON.AON_CSS.aonColorRed());
		notYet.addStyleName(AON.AON_CSS.aonFontBig());
		// ---
		
		simplifiedRegimeScrollPanel.setWidget(notYet);
		tabPanel.add(simplifiedRegimeScrollPanel, TAB_TEMPLATE.render(AON.MSG.simplifiedRegime(), AON.AON_CSS.aonIconModel()));
	}

	private void paintResultTab(Model303Callback callback,TabLayoutPanel tabPanel) {
		ScrollPanel resultScrollPanel = new ScrollPanel();
		FlexTable table = new FlexTable();
		table.setWidth("100%");
		table.addStyleName(AON.AON_CSS.aonMarginBottom());
		
		table.getColumnFormatter().setWidth(0, "auto");
		table.getColumnFormatter().addStyleName(0, AON.AON_CSS.aonPaddingLeft() );
		table.getColumnFormatter().addStyleName(0, AON.AON_CSS.aonPaddingRight() );
		table.getColumnFormatter().setWidth(1, "40px");
		table.getColumnFormatter().setStyleName(1, AON.AON_CSS.aonTextCenter());
		table.getColumnFormatter().setWidth(2, "140px");
		table.getColumnFormatter().setWidth(3, "50px");
		resultScrollPanel.setWidget(table);
		tabPanel.add(resultScrollPanel, TAB_TEMPLATE.render(AON.MSG.result(), AON.AON_CSS.aonIconModel()));
		paintDeclaration(table,Model3032017AEATResultScript.values(),3);
	}

	private void paintAdditionalDataTab(Model303Callback callback,TabLayoutPanel tabPanel) {
		ScrollPanel additionalDataScrollPanel = new ScrollPanel();
		FlexTable table = new FlexTable();
		table.setWidth("100%");
		table.addStyleName(AON.AON_CSS.aonMarginBottom());
		
		table.getColumnFormatter().setWidth(0, "auto");
		table.getColumnFormatter().addStyleName(0, AON.AON_CSS.aonPaddingLeft() );
		table.getColumnFormatter().addStyleName(0, AON.AON_CSS.aonPaddingRight() );

		table.getColumnFormatter().setWidth(1, "40px");
		table.getColumnFormatter().setStyleName(1, AON.AON_CSS.aonTextCenter());
		table.getColumnFormatter().setWidth(2, "140px");
		
		table.getColumnFormatter().setWidth(3, "40px");
		table.getColumnFormatter().setStyleName(3, AON.AON_CSS.aonTextCenter());
		table.getColumnFormatter().setWidth(4, "140px");
		
		table.getColumnFormatter().setWidth(5, "50px");
		additionalDataScrollPanel.setWidget(table);
		tabPanel.add(additionalDataScrollPanel, TAB_TEMPLATE.render(AON.MSG.additionalData(), AON.AON_CSS.aonIconCompanyData()));
		paintDeclaration(table,Model3032017AEATAdditionalDataScript.values(),3);
	}

	private void paintAdministrationTab(Model303Callback callback,TabLayoutPanel tabPanel) {
		FlowPanel panel = new FlowPanel();
		
		FlowPanel formContainer = new FlowPanel();
		diskForm.setMethod(FormPanel.METHOD_POST);
		FlowPanel formFlowPanel = new FlowPanel();
		diskForm.add(formFlowPanel);
		formFlowPanel.add(mod303Hidden);
		formFlowPanel.add(domainIdHidden);
		formFlowPanel.add(domainNameHidden);
		formContainer.add(diskForm);
		panel.add(formContainer);
		
		FlowPanel administrationPanel = getAdministrationPanel(callback); 
		panel.add(administrationPanel);
		FlowPanel informationPanel = getInformationPanel(callback);
		panel.add(informationPanel);
		tabPanel.add(panel,TAB_TEMPLATE.render("Agencia Tributaria", FiscalModelUtils.getAdministrationIconBW(getMod303().getAdministration())));
	}
	
	protected FlowPanel getAdministrationPanel(Model303Callback callback) {
		FlowPanel panel = new FlowPanel();
		panel.setStyleName(AON.AON_CSS.aonScrollArea());
		panel.addStyleName(AON.AON_CSS.aonWidthAll());
		panel.addStyleName(AON.AON_CSS.aonMarginTop());
		panel.addStyleName(AON.AON_CSS.aonPaddingTop());
		panel.addStyleName(AON.AON_CSS.aonPaddingLeft());
		 
		FlexTable tab = new FlexTable();
		tab.getColumnFormatter().setWidth(0, "30px");
		tab.getColumnFormatter().setWidth(1
				, "auto");
		tab.setStyleName(AON.AON_CSS.aonWidth90Percent());
		tab.addStyleName(AON.AON_CSS.aonBlockCenter());
		tab.addStyleName(AON.AON_CSS.aonPanelGrid());
		Label title = new Label("Presentaci\u00F3n del modelo");
		tab.getFlexCellFormatter().setColSpan(0, 0, 2);
		tab.getCellFormatter().setStyleName(0, 0, AON.AON_CSS.aonPanelGridEven());
		tab.getCellFormatter().addStyleName(0, 0, AON.AON_CSS.aonMarginTop());
		tab.getCellFormatter().addStyleName(0, 0, AON.AON_CSS.aonFiscalModelTableHeaderTitle());
		tab.getCellFormatter().addStyleName(0, 0, FiscalModelUtils.getAdministrationBG(getMod303().getAdministration()));
		tab.setWidget(0, 0, title);
		
		int row = 1;

		Label icon1 = new Label();
		icon1.addStyleName(FiscalModelUtils.getAdministrationIcon(getMod303().getAdministration()));
		tab.setWidget(row, 0, icon1 );
		tab.getCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonPanelGridEven());
		FlowPanel p1 = new FlowPanel();
		p1.setStyleName(AON.AON_CSS.aonPadding2());
		Button button1 = new Button("Descargar fichero para su presentaci\u00F3n");
		button1.setStyleName(AON.AON_CSS.aonPaddingLeft());
		button1.addStyleName(AON.AON_CSS.aonBorderNone());
		button1.addStyleName(AON.AON_CSS.aonEvenBackground());
		button1.addStyleName(AON.AON_CSS.aonClickable());
		button1.addClickHandler( new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (getMod303().isFinished()) {
					submitForm(DOWNLOAD_FILE_ACTION);
				} else {
					callback.showBreakdownPanel("Para generar el fichero debe finalizar la confecci\u00F3n del modelo.");
				}
			}
		});
		p1.add(button1);
		tab.setWidget(row, 1, p1 );
		tab.getCellFormatter().setStyleName(row, 1, AON.AON_CSS.aonPanelGridEven());
		row++;
		
		Label icon2 = new Label();
		icon2.addStyleName(FiscalModelUtils.getAdministrationIcon(getMod303().getAdministration()));
		tab.setWidget(row, 0, icon2 );
		tab.getCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonPanelGridEven());
		FlowPanel p2 = new FlowPanel();
		p2.setStyleName(AON.AON_CSS.aonPadding2());
		Button button2 = new Button("Validar e imprimir (PDF) via Agencia Tributaria (a partir de los datos guardados).");
		button2.setStyleName(AON.AON_CSS.aonPaddingLeft());
		button2.addStyleName(AON.AON_CSS.aonBorderNone());
		button2.addStyleName(AON.AON_CSS.aonEvenBackground());
		button2.addStyleName(AON.AON_CSS.aonClickable());
		button2.addClickHandler( new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (getMod303().isFinished()) {
					submitForm(VALIDATE_PRINT_ACTION);
				} else {
					callback.showBreakdownPanel("Para generar el fichero debe finalizar la confecci\u00F3n del modelo.");
				}
			}
		});
		p2.add(button2);
		tab.setWidget(row, 1, p2 );
		tab.getCellFormatter().setStyleName(row, 1, AON.AON_CSS.aonPanelGridEven());
		row++;


		panel.add(tab);
		return panel;
	}

	@Override
	protected LinkedList<Pair<String, String>> getInformationLinks() {
		LinkedList<Pair<String, String>> list = new LinkedList<Pair<String, String>>();
		list.add(new Pair<String, String>("Tr\u00E1mites."
				,"https://www.agenciatributaria.gob.es/AEAT.sede/tramitacion/G414.shtml"));
		list.add(new Pair<String, String>("Informaci\u00F3n general." 
				,"https://www.agenciatributaria.gob.es/AEAT.sede/Ayuda/G414.shtml"));
		list.add(new Pair<String, String>("Ficha."
				,"https://www.agenciatributaria.gob.es/AEAT.sede/procedimientos/G414.shtml"));
		return list;
	}
	
	private void paintDeclarationTab(Mod303 mod303,Model303Callback callback, TabLayoutPanel tabPanel) {
		ScrollPanel declarationScrollPanel = new ScrollPanel();
		FlowPanel container = new FlowPanel();
		
		FlexTable table = createTable();
		paintCheck(Mod303Key.CM_002,table);	// ¿Está inscrito en el Registro de devolució3n mensual (Art. 30 RIVA)?
		
		paintA02(Mod303Key.CT_A02,table);	// ¿Tributa exclusivamente en régimen simplificado?
		
		paintCheck(Mod303Key.CT_A03,table);	// ¿Es autoliquidación conjunta?
		
		paintCheck(Mod303Key.CT_A07,table);	// ¿Ha optado por el régimen especial del criterio de Caja (art. 163 undecies LIVA)?
		paintCheck(Mod303Key.CT_A08,table);	// ¿Es destinatario de operaciones a las que se aplique el régimen especial del criterio de caja?
		paintCheck(Mod303Key.CT_A11,table);	// Exonerados de la declaraci\u00F3n-resumen anual del IVA, modelo 390: ¿Existe volumen de operaciones (art. 121 LIVA)?
		
		paintCheck(Mod303Key.CT_A04,table);	// Ha sido declarado en concurso de acreedores en el presente período de liquidación?
		paintDate (Mod303Key.CT_A05,table);	// Fecha en que se dictó el auto de declaración de concurso
		paintCheck(Mod303Key.CT_A06,table);	// Auto de declaración de concurso dictado en el períDodo

		if (mod303.isComplementary()) {
			int row = table.getRowCount();
			paintLabel(table, row, AON.MSG.previousReceipt());
			
			table.getFlexCellFormatter().addStyleName(row, 0,AON.AON_CSS.aonPaddingLeft() );
			table.getFlexCellFormatter().addStyleName(row, 0,AON.AON_CSS.aonBorderBottomImportant() );
			final TextBox receiptBox = new TextBox();
			receiptBox.setVisibleLength(15);
			receiptBox.setMaxLength(13);
			receiptBox.setStyleName(AON.AON_CSS.aonInputText());
			receiptBox.setEnabled(mod303.isNotFinished());
			receiptBox.setValue( mod303.getReplacedNumber() );
			receiptBox.addValueChangeHandler( new ValueChangeHandler<String>() {
				@Override
				public void onValueChange(ValueChangeEvent<String> event) {
					mod303.setReplacedNumber(receiptBox.getValue());
					markAsDirty();
				}
			});
			table.setWidget(row, 1, receiptBox);
		}
		
		
		paintEmptyRow(table);
		paintCheck(Mod303Key.CT_A09,table);	// Opción por la aplicación de la prorrata especial
		paintCheck(Mod303Key.CT_A10,table);	// Revocación de la opción por la aplicación de la prorrata especial
		
		
		
		container.add(addGroupPanel("", table));
		
		declarationScrollPanel.setWidget(container);
		tabPanel.add(declarationScrollPanel, TAB_TEMPLATE.render(AON.MSG.declaration(), AON.AON_CSS.aonIconModel()));
	}
	
	private void paintA02(Mod303Key key, FlexTable table) {
		final ListBox a02 = new ListBox();
		a02.addItem("S\u00F3lo Reg. Simplificado");
		a02.addItem("Reg. General y Reg. Simpl.");
		a02.addItem("S\u00F3lo Reg. General");
		paintListBox(a02, key, table);
	}


	private FlexTable createTable() {
		FlexTable table = new FlexTable();
		table.setWidth("100%");
		table.addStyleName(AON.AON_CSS.aonMarginBottom());
		
		table.getColumnFormatter().setWidth(0, "auto");
		table.getColumnFormatter().setWidth(1, "300px");
		table.getColumnFormatter().addStyleName(0, AON.AON_CSS.aonPaddingLeft() );
		table.getColumnFormatter().addStyleName(0, AON.AON_CSS.aonPaddingRight() );
		return table;
	}
	
}
