package com.esferalia.aon.gwt.fiscal.client.mod390HF;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.Cnae2009Panel;
import com.esferalia.aon.gwt.common.client.widget.DoubleBox;
import com.esferalia.aon.gwt.fiscal.client.FiscalModelUtils;
import com.esferalia.aon.gwt.fiscal.client.mod303.Model303ModuleOptions;
import com.esferalia.aon.gwt.fiscal.client.mod390HF.Model390HF.Model390HFCallback;
import com.esferalia.aon.gwt.fiscal.client.widget.ActivityPanel;
import com.esferalia.aon.gwt.fiscal.client.widget.ActivityPanel.SelectionCallBack;
import com.esferalia.aon.gwt.fiscal.shared.mod390.Model3902017BIZKAIAAdditionalDataScript;
import com.esferalia.aon.gwt.fiscal.shared.mod390.Model3902017BIZKAIAScript1;
import com.esferalia.aon.gwt.fiscal.shared.mod390.Model3902017BIZKAIAScript2;
import com.esferalia.aon.gwt.fiscal.shared.mod390.Model3902017BIZKAIASpecificOperationsScript;
import com.esferalia.aon.occam.api.model.fiscal.Activity;
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

public class Model3902017BIZKAIA extends Model390HFBase {
	
	
	public Model3902017BIZKAIA(Mod390HF mod303,Model390HFModuleOptions options,Model390HFCallback callback) {
		super(mod303,options,callback);
		
		TabLayoutPanel tabPanel = new TabLayoutPanel(26, Unit.PX);
		SimpleLayoutPanel centerPanel = new SimpleLayoutPanel();
		centerPanel.addStyleName(AON.AON_CSS.aonScrollArea());
		centerPanel.setWidget(tabPanel);
		add(centerPanel);
		
		paintIdentificationTab(tabPanel);
		paintDeclarationTab(tabPanel);
		paintLiquidationTab(options,tabPanel);
		paintAdditionalDataTab(options,tabPanel);
		paintSpecificOperationsTab(options,tabPanel);
		paintExtraTab(tabPanel);
		paintAdministrationTab(options, tabPanel);
		
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
		paintDeclaration(options,table,Model3902017BIZKAIAAdditionalDataScript.values(),10);
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
		paintDeclaration(options,table,Model3902017BIZKAIASpecificOperationsScript.values(),4);
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

	private void paintLiquidationTab(Model390HFModuleOptions options, TabLayoutPanel tabPanel) {
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
		paintDeclaration(options,table,Model3902017BIZKAIAScript1.values(),8);
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
		table.getColumnFormatter().setWidth(4, "60px");

		table.getColumnFormatter().setWidth(5, "50px");
		paintDeclaration(options,table,Model3902017BIZKAIAScript2.values(),6);
		container.add(table);
		
		generalRegimeScrollPanel.setWidget(container);
		tabPanel.add(generalRegimeScrollPanel, TAB_TEMPLATE.render(AON.MSG.liquidacion(), AON.AON_CSS.aonIconModel()));
	}

	private void paintDeclarationTab(TabLayoutPanel tabPanel) {
		ScrollPanel declarationScrollPanel = new ScrollPanel();
		FlowPanel container = new FlowPanel();
		
		FlexTable table = new FlexTable();
		table.setWidth("100%");
		table.addStyleName(AON.AON_CSS.aonMarginBottom());
		
		table.getColumnFormatter().setWidth(0, "auto");
		table.getColumnFormatter().setWidth(1, "400px");
		table.getColumnFormatter().addStyleName(0, AON.AON_CSS.aonPaddingLeft() );
		table.getColumnFormatter().addStyleName(0, AON.AON_CSS.aonPaddingRight() );

		int row = table.getRowCount();
		paintLabel(table, row, Mod390Key.BZ_A000.getDescription());
		TextBox groupText = new TextBox( );
		groupText.setMaxLength(5);
		groupText.setVisibleLength(5);
		groupText.setValue(getCallback().getMod390HF().getDescription(Mod390Key.BZ_A000));
		groupText.setStyleName(AON.AON_CSS.aonInputText());
		groupText.addValueChangeHandler(new ValueChangeHandler<String>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				getCallback().getMod390HF().putDescription(Mod390Key.BZ_A000,groupText.getValue());
				markAsDirty();
			}
		});
		table.setWidget(row, 1, groupText);

		row = table.getRowCount();
		paintLabel(table, row, AON.MSG.mainActivity());
		FlowPanel actPanel = new FlowPanel();
		TextBox epiText = new TextBox( );
		epiText.setMaxLength(5);
		epiText.setVisibleLength(5);
		epiText.setValue(getCallback().getMod390HF().getDescription(Mod390Key.BZ_A001));
		epiText.setStyleName(AON.AON_CSS.aonInputText());
		epiText.addStyleName(AON.AON_CSS.aonMarginLeft());
		TextBox descText = new TextBox( );
		descText.setMaxLength(35);
		descText.setVisibleLength(15);
		descText.setValue(getCallback().getMod390HF().getDescription(Mod390Key.BZ_A002));
		descText.setStyleName(AON.AON_CSS.aonInputText());
		descText.addStyleName(AON.AON_CSS.aonMarginLeft());
		final Button clearButton = new Button();
		clearButton.setVisible(AonStringUtils.isNotBlank( getCallback().getMod390HF().getDescription(Mod390Key.BZ_A001))		
				|| AonStringUtils.isNotBlank( getCallback().getMod390HF().getDescription(Mod390Key.BZ_A001)));
		ActivityPanel activityPanel = new ActivityPanel();
		activityPanel.setCallback(new SelectionCallBack() {
			@Override
			public void onSelect(Activity activity) {
				getCallback().getMod390HF().putDescription(Mod390Key.BZ_A001, activity.getEpigraph());
				epiText.setValue(activity.getEpigraph()); 
				getCallback().getMod390HF().putDescription(Mod390Key.BZ_A002, activity.getDescription());
				descText.setValue(activity.getDescription());
				clearButton.setVisible(AonStringUtils.isNotBlank( getCallback().getMod390HF().getDescription(Mod390Key.BZ_A001))		
					|| AonStringUtils.isNotBlank( getCallback().getMod390HF().getDescription(Mod390Key.BZ_A001)));
				markAsDirty();
			}
			
			@Override
			public void onClose() {
				
			}
		});

		final Button actButton = new Button();
		actButton.setStyleName(AON.AON_CSS.aonIconCommandButton());
		actButton.addStyleName(AON.AON_CSS.aonIconLoupe());
		actButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				activityPanel.center();
				activityPanel.show();
			}
		});
		
		clearButton.setStyleName(AON.AON_CSS.aonIconCommandButton());
		clearButton.addStyleName(AON.AON_CSS.aonIconDelete());
		clearButton.addStyleName(AON.AON_CSS.aonMarginLeft());
		clearButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				getCallback().getMod390HF().putDescription(Mod390Key.BZ_A001, "");
				epiText.setText(""); 
				getCallback().getMod390HF().putDescription(Mod390Key.BZ_A002, "");
				descText.setText("");
				markAsDirty();
			}
		});
		
		actPanel.add(actButton);
		actPanel.add(epiText);
		actPanel.add(descText);
		actPanel.add(clearButton);
		table.setWidget(row, 1, actPanel);
		
		paintDate( Mod390Key.BZ_C001D,table);	// Fraccionamiento de per\u00EDodo en concursal. Desde.
		paintDate( Mod390Key.BZ_C001H,table);	// Fraccionamiento de per\u00EDodo en concursal. Hasta.

		paintCheck(Mod390Key.BZ_C003,table);	// Sujeto pasivo acogido al régimen especial del criterio de caja.
		paintCheck(Mod390Key.BZ_C004,table);	// Destinatario/a de operaciones a las que se aplica el r\u00E9gimen especial del criterio de caja

		paintCheck(Mod390Key.BZ_C080,table);	// ¿Está inscrito en el Registro de devolució3n mensual (Art. 30 RIVA)?
		
		paintCheck(Mod390Key.BZ_C005, table);	// Opción por la aplicación de la prorrata especial
		paintCheck(Mod390Key.BZ_C006, table);	// Revocación de la opción por la aplicación de la prorrata especial
		paintCheck(Mod390Key.BZ_C193, table);	// Prorrata general	
		paintCheck(Mod390Key.BZ_C194, table);	// Prorrata especial
		
		paintCheck(Mod390Key.BZ_C007, table);	// Aplicar el r\u00E9gimen especial art. 163 Sexies. Cinco de la NF del IVA
			
		paintWithoutActivityCheck(table);		// Sin actividad
		
		container.add(addGroupPanel("", table));
		declarationScrollPanel.setWidget(container);
		tabPanel.add(declarationScrollPanel, TAB_TEMPLATE.render(AON.MSG.declaration(), AON.AON_CSS.aonIconModel()));
	}

	private void paintIdentificationTab(TabLayoutPanel tabPanel) {
		Model390HFIdentificationData identificationData = new Model390HFIdentificationData( new Model390HFIdentificationDataCallback()) ;
		tabPanel.add(identificationData, TAB_TEMPLATE.render(AON.MSG.identification(), AON.AON_CSS.aonIconIdentification()));
	}

	private void paintAdministrationTab(Model390HFModuleOptions options, TabLayoutPanel tabPanel) {
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
		Button button1 = new Button("Descargar fichero para m\u00F3dulo de impresi\u00F3n.");
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

	@Override
	protected LinkedList<Pair<String, String>> getInformationLinks() {
		LinkedList<Pair<String, String>> list = new LinkedList<Pair<String, String>>();
		list.add(new Pair<String, String>("Impreso"
			,"http://www.bizkaia.eus/fitxategiak/05/ogasuna/ereduak/Argitaratu/390EurCas.pdf"));
		list.add(new Pair<String, String>("Instrucciones"
			,"http://www.bizkaia.eus/fitxategiak/05/ogasuna/ereduak/Argitaratu/390EurCasInst.PDF"));
		list.add(new Pair<String, String>("ORDEN FORAL 2198/2016, de 14 de diciembre."
			,"http://www.bizkaia.eus/lehendakaritza/Bao_bob/2016/12/20161223a243.pdf#page=3"));
		list.add(new Pair<String, String>("Enlace a las fechas de vencimiento en el a\u00F1o vigente"
			,"http://www.bizkaia.eus/ogasuna/egutegia/egutegia_anual.asp?id=0&Modelos=271&Age_Codigo=27/12/2017&Tem_Codigo=5346"));
		list.add(new Pair<String, String>("Enlace a la gu\u00EDa de informaci\u00F3n tributaria GURE GIDA"
			,"http://www.bizkaia.eus/ogasuna/guregida/fitxabisorea.asp?Idioma=ca&Tem_Codigo=7884&bnetmobile=0&dpto_biz=5&codpath_biz=5|3405|7884&IdPublicoMostrar=1322"));
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
		
		paintProrrateRow(tab2,Mod390Key.BZ_P1C,Mod390Key.BZ_P1I,Mod390Key.BZ_P1D,Mod390Key.BZ_P1T,Mod390Key.BZ_P1P);
		paintProrrateRow(tab2,Mod390Key.BZ_P2C,Mod390Key.BZ_P2I,Mod390Key.BZ_P2D,Mod390Key.BZ_P2T,Mod390Key.BZ_P2P);
		paintProrrateRow(tab2,Mod390Key.BZ_P3C,Mod390Key.BZ_P3I,Mod390Key.BZ_P3D,Mod390Key.BZ_P3T,Mod390Key.BZ_P3P);
		paintProrrateRow(tab2,Mod390Key.BZ_P4C,Mod390Key.BZ_P4I,Mod390Key.BZ_P4D,Mod390Key.BZ_P4T,Mod390Key.BZ_P4P);
		paintProrrateRow(tab2,Mod390Key.BZ_P5C,Mod390Key.BZ_P5I,Mod390Key.BZ_P5D,Mod390Key.BZ_P5T,Mod390Key.BZ_P5P);
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
		tab2.getFlexCellFormatter().setColSpan(0, 0, 3);
		tab2.setWidget(0, 1, new Label("Facturas recibidas") ); 
		tab2.getFlexCellFormatter().addStyleName(0,1,AON.AON_CSS.aonDataTableHeader());
		tab2.getFlexCellFormatter().setColSpan(0, 1, 3);

		tab2.setWidget(1, 0, new Label( "Serie") );
		tab2.getFlexCellFormatter().addStyleName(1,0,AON.AON_CSS.aonDataTableHeader());
		tab2.setWidget(1, 1, new Label("Inicio") ); 
		tab2.getFlexCellFormatter().addStyleName(1,1,AON.AON_CSS.aonDataTableHeader());
		tab2.setWidget(1, 2, new Label("terminaci\u00F3n") );
		tab2.getFlexCellFormatter().addStyleName(1,2,AON.AON_CSS.aonDataTableHeader());
		
		tab2.setWidget(1, 3, new Label( "Serie") );
		tab2.getFlexCellFormatter().addStyleName(1,3,AON.AON_CSS.aonDataTableHeader());
		tab2.setWidget(1, 4, new Label("Inicio") ); 
		tab2.getFlexCellFormatter().addStyleName(1,4,AON.AON_CSS.aonDataTableHeader());
		tab2.setWidget(1, 5, new Label("terminaci\u00F3n") );
		tab2.getFlexCellFormatter().addStyleName(1,5,AON.AON_CSS.aonDataTableHeader());
		tab2.setWidget(1, 6, new Label() ); 
		tab2.getFlexCellFormatter().addStyleName(1,6,AON.AON_CSS.aonDataTableHeader());
		
		paintInvoiceFields(tab2,2,0,Mod390Key.BZ_SE1N,Mod390Key.BZ_SE1D,Mod390Key.BZ_SE1H);
		paintInvoiceFields(tab2,2,3,Mod390Key.BZ_SR1N,Mod390Key.BZ_SR1D,Mod390Key.BZ_SR1H);
		paintInvoiceFields(tab2,3,0,Mod390Key.BZ_SE2N,Mod390Key.BZ_SE2D,Mod390Key.BZ_SE2H);
		paintInvoiceFields(tab2,3,3,Mod390Key.BZ_SR2N,Mod390Key.BZ_SR2D,Mod390Key.BZ_SR2H);
		paintInvoiceFields(tab2,4,0,Mod390Key.BZ_SE3N,Mod390Key.BZ_SE3D,Mod390Key.BZ_SE3H);
		paintInvoiceFields(tab2,4,3,Mod390Key.BZ_SR3N,Mod390Key.BZ_SR3D,Mod390Key.BZ_SR3H);
		paintInvoiceFields(tab2,5,0,Mod390Key.BZ_SE4N,Mod390Key.BZ_SE4D,Mod390Key.BZ_SE4H);
		paintInvoiceFields(tab2,5,3,Mod390Key.BZ_SR4N,Mod390Key.BZ_SR4D,Mod390Key.BZ_SR4H);
		paintInvoiceFields(tab2,6,0,Mod390Key.BZ_SE5N,Mod390Key.BZ_SE5D,Mod390Key.BZ_SE5H);
		paintInvoiceFields(tab2,6,3,Mod390Key.BZ_SR5N,Mod390Key.BZ_SR5D,Mod390Key.BZ_SR5H);
		return tab2;
	}

	private void paintInvoiceFields(FlexTable tab, int row, int col, Mod390Key seriesKey, Mod390Key fromKey,Mod390Key toKey) {
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
		from.setVisibleLength(15);
		from.setMaxLength(15);
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
		to.setVisibleLength(15);
		to.setMaxLength(15);
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
	}
}
