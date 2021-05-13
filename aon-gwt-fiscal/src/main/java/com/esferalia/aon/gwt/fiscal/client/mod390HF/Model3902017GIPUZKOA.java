package com.esferalia.aon.gwt.fiscal.client.mod390HF;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.Cnae2009Panel;
import com.esferalia.aon.gwt.common.client.widget.DoubleBox;
import com.esferalia.aon.gwt.fiscal.client.FiscalModelUtils;
import com.esferalia.aon.gwt.fiscal.client.mod390HF.Model390HF.Model390HFCallback;
import com.esferalia.aon.gwt.fiscal.shared.mod390.Model3902017GIPUZKOAAdditionalDataScript;
import com.esferalia.aon.gwt.fiscal.shared.mod390.Model3902017GIPUZKOARScript1;
import com.esferalia.aon.gwt.fiscal.shared.mod390.Model3902017GIPUZKOAResultScript;
import com.esferalia.aon.gwt.fiscal.shared.mod390.Model3902017GIPUZKOASpecificOperationsScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod390HF;
import com.esferalia.aon.occam.api.model.type.CNAE2009;
import com.esferalia.aon.occam.api.model.type.Mod390Key;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.esferalia.aon.watson.util.Pair;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
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

public class Model3902017GIPUZKOA extends Model390HFBase {
	
	public Model3902017GIPUZKOA(Mod390HF mod303,Model390HFModuleOptions options,Model390HFCallback callback) {
		super(mod303,options,callback);
		
		TabLayoutPanel tabPanel = new TabLayoutPanel(26, Unit.PX);
		SimpleLayoutPanel centerPanel = new SimpleLayoutPanel();
		centerPanel.addStyleName(AON.AON_CSS.aonScrollArea());
		centerPanel.setWidget(tabPanel);
		add(centerPanel);
		
		paintIdentificationTab(tabPanel);
		paintDeclarationTab(tabPanel);
		paintGeneralRegimenTab(options,tabPanel);
		paintResultTab(options,tabPanel);
		paintAdditionalDataTab(options,tabPanel);
		paintSpecificOperationsTab(options,tabPanel);
		paintExtraTab(tabPanel);
		paintAdministrationTab(options,tabPanel);
		
	}

	private void paintDeclarationTab(TabLayoutPanel tabPanel) {
		ScrollPanel declarationScrollPanel = new ScrollPanel();
		FlowPanel container = new FlowPanel();
		
		FlexTable table = new FlexTable();
		table.setWidth("100%");
		table.addStyleName(AON.AON_CSS.aonMarginBottom());
		
		table.getColumnFormatter().setWidth(0, "auto");
		table.getColumnFormatter().setWidth(1, "300px");
		table.getColumnFormatter().addStyleName(0, AON.AON_CSS.aonPaddingLeft() );
		table.getColumnFormatter().addStyleName(0, AON.AON_CSS.aonPaddingRight() );
		
		paintTextBox(table, Mod390Key.GP_I000, 9, true);
		
		paintWithoutActivityCheck(table);	// Sin actividad

		paintCheck(Mod390Key.GP_A000,table);	// Gran empresa
		paintCheck(Mod390Key.GP_A003,table);	// ¿Está inscrito en el Registro de devolució3n mensual (Art. 30 RIVA)?
		
		paintCheck(Mod390Key.GP_A001,table);	// Autoliquidación concursal. PRE
		paintCheck(Mod390Key.GP_A002,table);	// Autoliquidación concursal. POST
		
		paintCheck(Mod390Key.GP_A078,table);	// Opci\u00F3n por la aplicaci\u00F3n de la prorrata especial
		paintCheck(Mod390Key.GP_A079,table);	// Revocaci\u00F3n de la opci\u00F3n por la aplicaci\u00F3n de la prorrata especial

		paintCheck(Mod390Key.GP_A100,table);	// Incluido en el r\u00E9gimen especial de bienes usados, objetos de arte, antig\u00fcedades y objetos de colecci\u00F3n o agencias de viajes.
		
		container.add(addGroupPanel("", table));
		declarationScrollPanel.setWidget(container);
		tabPanel.add(declarationScrollPanel, TAB_TEMPLATE.render(AON.MSG.declaration(), AON.AON_CSS.aonIconModel()));
	}

	private void paintIdentificationTab(TabLayoutPanel tabPanel) {
		Model390HFIdentificationData identificationData = new Model390HFIdentificationData( new Model390HFIdentificationDataCallback()) ;
		tabPanel.add(identificationData, TAB_TEMPLATE.render(AON.MSG.identification(), AON.AON_CSS.aonIconIdentification()));
	}
	
	private void paintGeneralRegimenTab(Model390HFModuleOptions options,TabLayoutPanel tabPanel) {
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
		paintDeclaration(options,table,Model3902017GIPUZKOARScript1.values(),8);
		container.add(table);
		
		generalRegimeScrollPanel.setWidget(container);
		tabPanel.add(generalRegimeScrollPanel, TAB_TEMPLATE.render(AON.MSG.generalRegime(), AON.AON_CSS.aonIconModel()));
	}
	
	private void paintResultTab(Model390HFModuleOptions options,TabLayoutPanel tabPanel) {
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
		paintDeclaration(options,table,Model3902017GIPUZKOAResultScript.values(),3);
	}

	private void paintAdditionalDataTab(Model390HFModuleOptions options,TabLayoutPanel tabPanel) {
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
		table.getColumnFormatter().setWidth(4, "60px");
		
		table.getColumnFormatter().setWidth(5, "40px");
		table.getColumnFormatter().setStyleName(5, AON.AON_CSS.aonTextCenter());
		table.getColumnFormatter().setWidth(6, "140px");

		table.getColumnFormatter().setWidth(7, "40px");
		table.getColumnFormatter().setStyleName(7, AON.AON_CSS.aonTextCenter());
		table.getColumnFormatter().setWidth(8, "140px");

		table.getColumnFormatter().setWidth(9, "50px");
		additionalDataScrollPanel.setWidget(table);
		tabPanel.add(additionalDataScrollPanel, TAB_TEMPLATE.render("Inf. Adicional", AON.AON_CSS.aonIconCompanyData()));
		paintDeclaration(options,table,Model3902017GIPUZKOAAdditionalDataScript.values(),10);
	}
	
	private void paintAdministrationTab(Model390HFModuleOptions options,TabLayoutPanel tabPanel) {
		//FlowPanel panel = getInformationPanel();
		//tabPanel.add(panel,TAB_TEMPLATE.render("Foru Aldundia / Diputaci\u00F3n Foral", FiscalModelUtils.getAdministrationIconBW(getMod390HF().getAdministration())));
		
		FlowPanel panel = new FlowPanel();
		
		FlowPanel formContainer = new FlowPanel();
		diskForm.setMethod(FormPanel.METHOD_POST);
		FlowPanel formFlowPanel = new FlowPanel();
		diskForm.add(formFlowPanel);
		formFlowPanel.add(mod390Hidden);
		formFlowPanel.add(domainIdHidden);
		formFlowPanel.add(domainNameHidden);
		formFlowPanel.add(userHidden);
		formContainer.add(diskForm);
		panel.add(formContainer);
		
		FlowPanel administrationPanel = getAdministrationPanel(options); 
		panel.add(administrationPanel);
		
		FlowPanel informationPanel = getInformationPanel();
		panel.add(informationPanel);

		tabPanel.add(panel,TAB_TEMPLATE.render("Foru Aldundia / Diputaci\u00F3n Foral", FiscalModelUtils.getAdministrationIconBW(getMod390HF().getAdministration())));
	}
	
	protected FlowPanel getAdministrationPanel(Model390HFModuleOptions options) {
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
		tab.getCellFormatter().addStyleName(0, 0, FiscalModelUtils.getAdministrationBG(getMod390HF().getAdministration()));
		tab.setWidget(0, 0, title);
		
		int row = 1;

		Label icon1 = new Label();
		icon1.addStyleName(FiscalModelUtils.getAdministrationIcon(getMod390HF().getAdministration()));
		tab.setWidget(row, 0, icon1 );
		tab.getCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonPanelGridEven());
		FlowPanel p1 = new FlowPanel();
		p1.setStyleName(AON.AON_CSS.aonPadding2());
		Button button1 = new Button("Descargar fichero para programa de ayuda.");
		button1.setStyleName(AON.AON_CSS.aonPaddingLeft());
		button1.addStyleName(AON.AON_CSS.aonBorderNone());
		button1.addStyleName(AON.AON_CSS.aonEvenBackground());
		button1.addStyleName(AON.AON_CSS.aonClickable());
		button1.addClickHandler( new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (getMod390HF().isFinished() || getMod390HF().isSent()) {
					submitForm(options,DOWNLOAD_FILE_ACTION);
				} else {
					getCallback().showBreakdownPanel("Para generar el fichero debe finalizar la confecci\u00F3n del modelo.");
				}
			}
		});
		p1.add(button1);
		tab.setWidget(row, 1, p1 );
		tab.getCellFormatter().setStyleName(row, 1, AON.AON_CSS.aonPanelGridEven());
		row++;
		
		panel.add(tab);
		return panel;
	}
	
	private void paintSpecificOperationsTab(Model390HFModuleOptions options,TabLayoutPanel tabPanel) {
		ScrollPanel specificOpDataScrollPanel = new ScrollPanel();
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
		specificOpDataScrollPanel.setWidget(table);
		tabPanel.add(specificOpDataScrollPanel, TAB_TEMPLATE.render("Vol. Oper. / Op. Especif.", AON.AON_CSS.aonIconCompanyData()));
		paintDeclaration(options,table,Model3902017GIPUZKOASpecificOperationsScript.values(),4);
	}
	

	private void paintExtraTab(TabLayoutPanel tabPanel) {
		ScrollPanel extraTabScrollPanel = new ScrollPanel();
		FlexTable table = new FlexTable();
		table.setWidth("100%");
		table.addStyleName(AON.AON_CSS.aonMarginBottom());
		
		table.getColumnFormatter().setWidth(0, "auto");
		table.getColumnFormatter().addStyleName(0, AON.AON_CSS.aonPaddingLeft() );
		table.getColumnFormatter().addStyleName(0, AON.AON_CSS.aonPaddingRight() );
		
		paintLabel(table, table.getRowCount() , AON.MSG.invoiceBook(),true);
		FlowPanel invoiceContainer = new FlowPanel();
		invoiceContainer.setStyleName(AON.AON_CSS.aonBlockCenter());
		invoiceContainer.addStyleName(AON.AON_CSS.aonWidth90Percent());
		invoiceContainer.add(getInvoiceTable());
		int row = table.getRowCount();
		table.setWidget(row, 0, invoiceContainer);

		paintLabel(table, table.getRowCount() , AON.MSG.prorrata(),true);
		FlowPanel prorrataContainer = new FlowPanel();
		prorrataContainer.setStyleName(AON.AON_CSS.aonBlockCenter());
		prorrataContainer.addStyleName(AON.AON_CSS.aonWidth90Percent());
		prorrataContainer.add(getProrrataTable());
		row = table.getRowCount();
		table.setWidget(row, 0, prorrataContainer);

		extraTabScrollPanel.setWidget(table);
		tabPanel.add(extraTabScrollPanel, TAB_TEMPLATE.render("Libro Fact. / Prorratas", AON.AON_CSS.aonIconCompanyData()));
	}

	@Override
	protected LinkedList<Pair<String, String>> getInformationLinks() {
		LinkedList<Pair<String, String>> list = new LinkedList<Pair<String, String>>();
		list.add(new Pair<String, String>("Ficha."
				,"https://egoitza.gipuzkoa.eus/es/listado-tramites/-/tramiteak/xehetasuna/800"));
		return list;
	}

	private FlexTable getProrrataTable() {
		FlexTable tab2 = new FlexTable();
		tab2.addStyleName(AON.AON_CSS.aonDataTable());
		tab2.addStyleName(AON.AON_CSS.aonMarginBottom());
		
		tab2.getColumnFormatter().setWidth(0, "80px");
		tab2.getColumnFormatter().setWidth(1, "20px");
		tab2.getColumnFormatter().setWidth(2, "250px");
		tab2.getColumnFormatter().setWidth(3, "250px");
		tab2.getColumnFormatter().setWidth(4, "80px");
		tab2.getColumnFormatter().setWidth(5, "250px");
		tab2.getColumnFormatter().setWidth(6, "auto");
		
		tab2.setWidget(1, 0, new Label( "C.N.A.E.") ); 
		tab2.getFlexCellFormatter().addStyleName(1,0,AON.AON_CSS.aonDataTableHeader());
		tab2.setWidget(1, 1, new Label() ); 
		tab2.getFlexCellFormatter().addStyleName(1,1,AON.AON_CSS.aonDataTableHeader());
		tab2.setWidget(1, 2, new Label( AON.MSG.operationsAmount()) );
		tab2.getFlexCellFormatter().addStyleName(1,2,AON.AON_CSS.aonDataTableHeader());
		tab2.setWidget(1, 3, new Label( AON.MSG.operationsAmountWithRight()) );
		tab2.getFlexCellFormatter().addStyleName(1,3,AON.AON_CSS.aonDataTableHeader());
		tab2.setWidget(1, 4, new Label( AON.MSG.type()) );
		tab2.getFlexCellFormatter().addStyleName(1,4,AON.AON_CSS.aonDataTableHeader());
		tab2.setWidget(1, 5, new Label( AON.MSG.prorrataPercent()) );
		tab2.getFlexCellFormatter().addStyleName(1,5,AON.AON_CSS.aonDataTableHeader());
		tab2.setWidget(1, 6, new Label() ); 
		tab2.getFlexCellFormatter().addStyleName(1,6,AON.AON_CSS.aonDataTableHeader());
		
		paintProrrateRow(tab2,Mod390Key.GP_P1C,Mod390Key.GP_P1I,Mod390Key.GP_P1D,Mod390Key.GP_P1T,Mod390Key.GP_P1P);
		paintProrrateRow(tab2,Mod390Key.GP_P2C,Mod390Key.GP_P2I,Mod390Key.GP_P2D,Mod390Key.GP_P2T,Mod390Key.GP_P2P);
		paintProrrateRow(tab2,Mod390Key.GP_P3C,Mod390Key.GP_P3I,Mod390Key.GP_P3D,Mod390Key.GP_P3T,Mod390Key.GP_P3P);
		paintProrrateRow(tab2,Mod390Key.GP_P4C,Mod390Key.GP_P4I,Mod390Key.GP_P4D,Mod390Key.GP_P4T,Mod390Key.GP_P4P);
		paintProrrateRow(tab2,Mod390Key.GP_P5C,Mod390Key.GP_P5I,Mod390Key.GP_P5D,Mod390Key.GP_P5T,Mod390Key.GP_P5P);
		return tab2;
	}

	private void paintProrrateRow(FlexTable tab, Mod390Key cnaeKey, Mod390Key amountKey, Mod390Key amountRightKey, Mod390Key typeKey, Mod390Key percentKey) {
		int row = tab.getRowCount();
		
		TextBox cnae = new TextBox();
		cnae.setVisibleLength(4);
		cnae.setMaxLength(4);
		cnae.setStyleName(AON.AON_CSS.aonInputText());
		cnae.setValue(getCallback().getMod390HF().getDescription(cnaeKey));
		cnae.addValueChangeHandler(new ValueChangeHandler<String>() {

			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				getCallback().getMod390HF().putDescription(cnaeKey, cnae.getValue() );
				markAsDirty();
			}
			
		});
		tab.setWidget(row, 0, cnae);

		Cnae2009Panel panel = new Cnae2009Panel( new com.esferalia.aon.gwt.common.client.widget.Cnae2009Panel.SelectionCallBack() {
			@Override public void onClose() {}
			@Override
			public void onSelect(CNAE2009 selected) {
				cnae.setValue(selected.getCodeWithoutPoint(),false);
				getCallback().getMod390HF().putDescription(cnaeKey, selected.getCodeWithoutPoint());
				markAsDirty();
			}
		});
		Button button = new Button();
		button.setStyleName(AON.AON_CSS.aonIconLoupe());
		button.addStyleName(AON.AON_CSS.aonIconCommandButton());
		button.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				panel.onShow();
			}
		});
		tab.setWidget(row, 1, button);
		
		DoubleBox amount = new DoubleBox();
		amount.setValue(getCallback().getMod390HF().getAmount(amountKey));
		amount.addValueChangeHandler(new ValueChangeHandler<Double>() {

			@Override
			public void onValueChange(ValueChangeEvent<Double> event) {
				getCallback().getMod390HF().putAmount(amountKey, amount.getValue() );
				markAsDirty();
			}
			
		});
		tab.setWidget(row, 2, amount);
		
		DoubleBox amountRight = new DoubleBox();
		amountRight.setValue(getCallback().getMod390HF().getAmount(amountRightKey));
		amountRight.addValueChangeHandler(new ValueChangeHandler<Double>() {

			@Override
			public void onValueChange(ValueChangeEvent<Double> event) {
				getCallback().getMod390HF().putAmount(amountRightKey, amountRight.getValue() );
				markAsDirty();
			}
			
		});
		tab.setWidget(row, 3, amountRight);
		
		ListBox typeBox = new ListBox();
		typeBox.setWidth("40px");
		typeBox.addItem(" - ", "");
		typeBox.addItem("G - General", "G");
		typeBox.addItem("E - Especial", "E");
		String type = getCallback().getMod390HF().getDescription(typeKey);
		if (AonStringUtils.equals(type, "G")) typeBox.setSelectedIndex(1);
		else if (AonStringUtils.equals(type, "E")) typeBox.setSelectedIndex(2);
		else typeBox.setSelectedIndex(0); 
		typeBox.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				getCallback().getMod390HF().putDescription(typeKey,typeBox.getSelectedValue());
				markAsDirty();
			}
		});
		tab.setWidget(row, 4, typeBox);
		
		
		DoubleBox percent = new DoubleBox();
		percent.setValue(getCallback().getMod390HF().getAmount(percentKey));
		percent.addValueChangeHandler(new ValueChangeHandler<Double>() {

			@Override
			public void onValueChange(ValueChangeEvent<Double> event) {
				getCallback().getMod390HF().putAmount(percentKey, percent.getValue() );
				markAsDirty();
			}
			
		});
		tab.setWidget(row, 5, percent);
	}

	private FlexTable getInvoiceTable() {
		FlexTable tab2 = new FlexTable();
		tab2.addStyleName(AON.AON_CSS.aonDataTable());
		tab2.addStyleName(AON.AON_CSS.aonMarginBottom());
		
		tab2.getColumnFormatter().setWidth(0, "80px");
		tab2.getColumnFormatter().setWidth(1, "200px");
		tab2.getColumnFormatter().setWidth(2, "250px");
		tab2.getColumnFormatter().setWidth(3, "80px");
		tab2.getColumnFormatter().setWidth(4, "200px");
		tab2.getColumnFormatter().setWidth(5, "250px");
		tab2.getColumnFormatter().setWidth(6, "auto");
		
		tab2.setWidget(0, 0, new Label( "Facturas emitidas") );
		tab2.getFlexCellFormatter().addStyleName(0,0,AON.AON_CSS.aonDataTableHeader());
		tab2.getFlexCellFormatter().setColSpan(0, 0, 4);
		tab2.setWidget(0, 1, new Label("Facturas recibidas") ); 
		tab2.getFlexCellFormatter().addStyleName(0,1,AON.AON_CSS.aonDataTableHeader());
		tab2.getFlexCellFormatter().setColSpan(0, 1, 4);

		tab2.setWidget(1, 0, new Label( "Serie") );
		tab2.getFlexCellFormatter().addStyleName(1,0,AON.AON_CSS.aonDataTableHeader());
		tab2.setWidget(1, 1, new Label("Inicio") ); 
		tab2.getFlexCellFormatter().addStyleName(1,1,AON.AON_CSS.aonDataTableHeader());
		tab2.setWidget(1, 2, new Label("Terminaci\u00F3n") );
		tab2.getFlexCellFormatter().addStyleName(1,2,AON.AON_CSS.aonDataTableHeader());
		tab2.setWidget(1, 3, new Label("Registros") );
		tab2.getFlexCellFormatter().addStyleName(1,3,AON.AON_CSS.aonDataTableHeader());
		
		tab2.setWidget(1, 4, new Label( "Serie") );
		tab2.getFlexCellFormatter().addStyleName(1,4,AON.AON_CSS.aonDataTableHeader());
		tab2.setWidget(1, 5, new Label("Inicio") ); 
		tab2.getFlexCellFormatter().addStyleName(1,5,AON.AON_CSS.aonDataTableHeader());
		tab2.setWidget(1, 6, new Label("Terminaci\u00F3n") );
		tab2.getFlexCellFormatter().addStyleName(1,6,AON.AON_CSS.aonDataTableHeader());
		tab2.setWidget(1, 7, new Label("Registros") );
		tab2.getFlexCellFormatter().addStyleName(1,7,AON.AON_CSS.aonDataTableHeader());

		tab2.setWidget(1, 8, new Label() ); 
		
		
		paintInvoiceFields(tab2,2,0,Mod390Key.GP_SE1N,Mod390Key.GP_SE1D,Mod390Key.GP_SE1H,Mod390Key.GP_SE1X);
		paintInvoiceFields(tab2,2,4,Mod390Key.GP_SR1N,Mod390Key.GP_SR1D,Mod390Key.GP_SR1H,Mod390Key.GP_SR1X);
		paintInvoiceFields(tab2,3,0,Mod390Key.GP_SE2N,Mod390Key.GP_SE2D,Mod390Key.GP_SE2H,Mod390Key.GP_SE2X);
		paintInvoiceFields(tab2,3,4,Mod390Key.GP_SR2N,Mod390Key.GP_SR2D,Mod390Key.GP_SR2H,Mod390Key.GP_SR2X);
		paintInvoiceFields(tab2,4,0,Mod390Key.GP_SE3N,Mod390Key.GP_SE3D,Mod390Key.GP_SE3H,Mod390Key.GP_SE3X);
		paintInvoiceFields(tab2,4,4,Mod390Key.GP_SR3N,Mod390Key.GP_SR3D,Mod390Key.GP_SR3H,Mod390Key.GP_SR3X);
		paintInvoiceFields(tab2,5,0,Mod390Key.GP_SE4N,Mod390Key.GP_SE4D,Mod390Key.GP_SE4H,Mod390Key.GP_SE4X);
		paintInvoiceFields(tab2,5,4,Mod390Key.GP_SR4N,Mod390Key.GP_SR4D,Mod390Key.GP_SR4H,Mod390Key.GP_SR4X);
		paintInvoiceFields(tab2,6,0,Mod390Key.GP_SE5N,Mod390Key.GP_SE5D,Mod390Key.GP_SE5H,Mod390Key.GP_SE5X);
		paintInvoiceFields(tab2,6,4,Mod390Key.GP_SR5N,Mod390Key.GP_SR5D,Mod390Key.GP_SR5H,Mod390Key.GP_SR5X);
		return tab2;
	}

	private void paintInvoiceFields(FlexTable tab, int row, int col, Mod390Key seriesKey, Mod390Key fromKey,Mod390Key toKey,Mod390Key countKey) {
		TextBox series = new TextBox();
		series.setVisibleLength(8);
		series.setMaxLength(5);
		series.setStyleName(AON.AON_CSS.aonInputText());
		series.setValue(getCallback().getMod390HF().getDescription(seriesKey));
		series.addValueChangeHandler(new ValueChangeHandler<String>() {

			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				getCallback().getMod390HF().putDescription(seriesKey, series.getValue() );
				markAsDirty();
			}
			
		});
		tab.setWidget(row, col, series);
		
		TextBox from = new TextBox();
		from.setVisibleLength(10);
		from.setMaxLength(10);
		from.setStyleName(AON.AON_CSS.aonInputText());
		from.setValue(getCallback().getMod390HF().getDescription(fromKey));
		from.addValueChangeHandler(new ValueChangeHandler<String>() {

			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				getCallback().getMod390HF().putDescription(fromKey, from.getValue() );
				markAsDirty();
			}
			
		});
		tab.setWidget(row, col+1, from);

		TextBox to = new TextBox();
		to.setVisibleLength(10);
		to.setMaxLength(10);
		to.setStyleName(AON.AON_CSS.aonInputText());
		to.setValue(getCallback().getMod390HF().getDescription(toKey));
		to.addValueChangeHandler(new ValueChangeHandler<String>() {

			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				getCallback().getMod390HF().putDescription(toKey, to.getValue() );
				markAsDirty();
			}
			
		});
		tab.setWidget(row, col+2, to);
		
		DoubleBox count = new DoubleBox();
		count.setVisibleLength(9);
		count.setMaxLength(9);
		count.setValue(getCallback().getMod390HF().getAmount(countKey));
		count.addValueChangeHandler(new ValueChangeHandler<Double>() {

			@Override
			public void onValueChange(ValueChangeEvent<Double> event) {
				getCallback().getMod390HF().putAmount(countKey, count.getValue() );
				markAsDirty();
			}
			
		});
		
		tab.setWidget(row, col+3, count);
	}
}
