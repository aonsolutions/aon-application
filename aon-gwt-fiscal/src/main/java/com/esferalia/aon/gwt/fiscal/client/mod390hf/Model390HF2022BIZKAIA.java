package com.esferalia.aon.gwt.fiscal.client.mod390hf;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCnae2009Panel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDoubleBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTextBox;
import com.esferalia.aon.gwt.fiscal.client.mod390.AonActivityPanel;
import com.esferalia.aon.gwt.fiscal.client.mod390hf.Model390HF.Model390HFCallback;
import com.esferalia.aon.gwt.fiscal.client.model.FiscalModelAdmonPanel;
import com.esferalia.aon.gwt.fiscal.client.model.FiscalModelAdmonPanel.IFiscalModelAdmonPanelCallback;
import com.esferalia.aon.occam.api.model.fiscal.Mod390HF;
import com.esferalia.aon.occam.api.model.fiscal.mod390hf.Model3902022BIZKAIAAdditionalDataScript;
import com.esferalia.aon.occam.api.model.fiscal.mod390hf.Model3902022BIZKAIAScript1;
import com.esferalia.aon.occam.api.model.fiscal.mod390hf.Model3902022BIZKAIAScript2;
import com.esferalia.aon.occam.api.model.fiscal.mod390hf.Model3902022BIZKAIASpecificOperationsScript;
import com.esferalia.aon.occam.api.model.type.Mod390Key;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;

public class Model390HF2022BIZKAIA extends Model390HFBase {
	
	
	public Model390HF2022BIZKAIA(Model390HFCallback callback,Mod390HF mod390HF) {
		super(mod390HF,callback);
		
		TabLayoutPanel tabPanel = new TabLayoutPanel(26, Unit.PX);
		SimpleLayoutPanel centerPanel = new SimpleLayoutPanel();
		centerPanel.addStyleName(AON.CSS.aonScrollArea());
		centerPanel.setWidget(tabPanel);
		add(centerPanel);
		
		paintIdentificationTab(tabPanel);
		paintDeclarationTab(tabPanel);
		paintLiquidationTab(tabPanel);
		paintAdditionalDataTab(tabPanel);
		paintSpecificOperationsTab(tabPanel);
		paintExtraTab(tabPanel);
		paintAdministrationTab(tabPanel);
		
	}
	
	private void paintAdditionalDataTab(TabLayoutPanel tabPanel) {
		ScrollPanel additionalDataScrollPanel = new ScrollPanel();
		FlowPanel container = new FlowPanel();
		container.add(getExistenciasTable());
		
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

		table.getColumnFormatter().setWidth(7, "40px");
		table.getColumnFormatter().setStyleName(7, AON.CSS.aonTextCenter());
		table.getColumnFormatter().setWidth(8, WIDTH_140PX);

		table.getColumnFormatter().setWidth(9, "50px");
		container.add(table);
		additionalDataScrollPanel.setWidget(container);
		tabPanel.add(additionalDataScrollPanel, "Inf. Adicional");
		paintDeclaration(table,Model3902022BIZKAIAAdditionalDataScript.values(),10);
	}

	private FlexTable getExistenciasTable() {
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
		
		paintLabel(table, 0, "Existencias Iniciales (1 de enero)", false);
		paintBox(table, 0, 1, Mod390Key.BZ_C140);
		paintField(table, 0, 2, Mod390Key.BZ_C140, 12, true);
		
		paintLabel(table, 1, "Existencias Finales (31 de diciembre)", false);
		paintBox(table, 1, 1, Mod390Key.BZ_C140);
		paintField(table, 1, 2, Mod390Key.BZ_C141, 12, true);
		
		return table;
	}
	
	private void paintSpecificOperationsTab(TabLayoutPanel tabPanel) {
		ScrollPanel specificOpDataScrollPanel = new ScrollPanel();
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
		specificOpDataScrollPanel.setWidget(table);
		tabPanel.add(specificOpDataScrollPanel, "Vol. Oper. / Op. Especif.");
		paintDeclaration(table,Model3902022BIZKAIASpecificOperationsScript.values(),4);
	}

	private void paintExtraTab(TabLayoutPanel tabPanel) {
		ScrollPanel extraTabScrollPanel = new ScrollPanel();
		FlexTable table = new FlexTable();
		table.setWidth("100%");
		table.addStyleName(AON.CSS.aonMarginBottom());
		
		table.getColumnFormatter().setWidth(0, "auto");
		table.getColumnFormatter().addStyleName(0, AON.CSS.aonPaddingLeft() );
		table.getColumnFormatter().addStyleName(0, AON.CSS.aonPaddingRight() );
		
		paintLabel(table, table.getRowCount() , AON.MSG.invoiceBook(),true);
		FlowPanel invoiceContainer = new FlowPanel();
		invoiceContainer.setStyleName(AON.CSS.aonBlockCenter());
		invoiceContainer.addStyleName(AON.CSS.aonWidthAlmostAll());
		invoiceContainer.add(getInvoiceTable());
		int row = table.getRowCount();
		table.setWidget(row, 0, invoiceContainer);

		paintLabel(table, table.getRowCount() , AON.MSG.prorrata(),true);
		FlowPanel prorrataContainer = new FlowPanel();
		prorrataContainer.setStyleName(AON.CSS.aonBlockCenter());
		prorrataContainer.addStyleName(AON.CSS.aonWidthAlmostAll());
		prorrataContainer.add(getProrrataTable());
		row = table.getRowCount();
		table.setWidget(row, 0, prorrataContainer);

		extraTabScrollPanel.setWidget(table);
		tabPanel.add(extraTabScrollPanel, "Libro Fact. / Prorratas");
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
		paintDeclaration(table,Model3902022BIZKAIAScript1.values(),8);
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
		table.getColumnFormatter().setWidth(4, "60px");

		table.getColumnFormatter().setWidth(5, "50px");
		paintDeclaration(table,Model3902022BIZKAIAScript2.values(),6);
		container.add(table);
		
		generalRegimeScrollPanel.setWidget(container);
		tabPanel.add(generalRegimeScrollPanel, AON.MSG.liquidacion());
	}

	private void paintDeclarationTab(TabLayoutPanel tabPanel) {
		ScrollPanel declarationScrollPanel = new ScrollPanel();
		FlowPanel container = new FlowPanel();
		
		FlexTable table = new FlexTable();
		table.setWidth("100%");
		table.addStyleName(AON.CSS.aonMarginBottom());
		
		table.getColumnFormatter().setWidth(0, "auto");
		table.getColumnFormatter().setWidth(1, "400px");
		table.getColumnFormatter().addStyleName(0, AON.CSS.aonPaddingLeft() );
		table.getColumnFormatter().addStyleName(0, AON.CSS.aonPaddingRight() );

		int row = table.getRowCount();
		paintLabel(table, row, Mod390Key.BZ_A000.getDescription());
		AonTextBox groupText = new AonTextBox( );
		groupText.setMaxLength(5);
		groupText.setVisibleLength(5);
		groupText.setValue(getModel().getDescription(Mod390Key.BZ_A000));
		groupText.addValueChangeHandler(event -> {
			getModel().putDescription(Mod390Key.BZ_A000,groupText.getValue());
			markAsDirty();
		});
		table.setWidget(row, 1, groupText);

		row = table.getRowCount();
		paintLabel(table, row, AON.MSG.mainActivity());
		FlowPanel actPanel = new FlowPanel();
		AonTextBox epiText = new AonTextBox( );
		epiText.setMaxLength(5);
		epiText.setVisibleLength(5);
		epiText.setValue(getModel().getDescription(Mod390Key.BZ_A001));
		epiText.addStyleName(AON.CSS.aonMarginLeft());
		AonTextBox descText = new AonTextBox( );
		descText.setMaxLength(35);
		descText.setVisibleLength(15);
		descText.setValue(getModel().getDescription(Mod390Key.BZ_A002));
		descText.addStyleName(AON.CSS.aonMarginLeft());
		final AonTableButton clearButton = new AonTableButton(AON.MSG.clean(),AON.CSS.aonIconDelete());
		clearButton.setVisible(
			AonStringUtils.isNotBlank( getModel().getDescription(Mod390Key.BZ_A001))		
		 || AonStringUtils.isNotBlank( getModel().getDescription(Mod390Key.BZ_A002)));
		AonActivityPanel activityPanel = new AonActivityPanel();
		activityPanel.addSelectionHandler(event -> {
				getModel().putDescription(Mod390Key.BZ_A001, event.getSelectedItem().getEpigraph());
				epiText.setValue(event.getSelectedItem().getEpigraph()); 
				getModel().putDescription(Mod390Key.BZ_A002, event.getSelectedItem().getDescription());
				descText.setValue(event.getSelectedItem().getDescription());
				clearButton.setVisible(AonStringUtils.isNotBlank( getModel().getDescription(Mod390Key.BZ_A001))		
					|| AonStringUtils.isNotBlank( getModel().getDescription(Mod390Key.BZ_A002)));
				markAsDirty();
			}
		);

		final AonTableButton actButton = new AonTableButton("Actividad",AON.CSS.aonIconSearch());
		actButton.addClickHandler(event -> {
			activityPanel.center();
			activityPanel.show();
		});
		
		clearButton.addStyleName(AON.CSS.aonMarginLeft());
		clearButton.addClickHandler(event -> {
			getModel().putDescription(Mod390Key.BZ_A001, "");
			epiText.setText(""); 
			getModel().putDescription(Mod390Key.BZ_A002, "");
			descText.setText("");
			markAsDirty();
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
		
		container.add(table);
		declarationScrollPanel.setWidget(container);
		tabPanel.add(declarationScrollPanel, AON.MSG.declaration());
	}

	private void paintAdministrationTab(TabLayoutPanel tabPanel) {
		IFiscalModelAdmonPanelCallback<Mod390HF, Model390HFModuleOptions> cbk = 
				new IFiscalModelAdmonPanelCallback<Mod390HF, Model390HFModuleOptions>() {

					@Override
					public Model390HFModuleOptions getOptions() {
						return getCallback().getOptions();
					}

					@Override
					public Mod390HF getModel() {
						return Model390HF2022BIZKAIA.this.getModel();
					}

					@Override
					public void showError(String msg) {
						getCallback().showError(msg);
					}

					@Override
					public String getValidatePrintAction() {
						return null;
						
					}

					@Override
					public String getDownloadFileAction() {
						return Model390HFBase.MODEL390HF_FILE;
					}

					@Override
					public String getSendAction() {
						return null;
					}

					@Override
					public void sendSuccessfully() {
						// Nothing
					}

					@Override
					public String getCheckAction() {
						return null;
					}

					@Override
					public String getCheckDataResponseDataAction() {
						return null;
					}

					@Override
					public String getModelInformationURL() {
						return "https://www.bizkaia.eus/ogasuna/ereduak/modelos.asp?textomodelo=390&idioma=CA&aceptar=Buscar&Tem_Codigo=2093&dpto_biz=5&codpath_biz=5%7C3587%7C2093";
					}
			};
			admonPanel = new FiscalModelAdmonPanel<>(cbk);
			tabPanel.add( admonPanel, AON.MSG.administrationName(getModel().getAdministration()));		
	}


	private FlexTable getProrrataTable() {
		FlexTable tab2 = new FlexTable();
		tab2.addStyleName(AON.CSS.aonDisplayTable());
		tab2.addStyleName(AON.CSS.aonMarginBottom());
		
		tab2.getColumnFormatter().setWidth(0, "80px");
		tab2.getColumnFormatter().setWidth(1, "20px");
		tab2.getColumnFormatter().setWidth(2, WIDTH_250PX);
		tab2.getColumnFormatter().setWidth(3, WIDTH_250PX);
		tab2.getColumnFormatter().setWidth(4, "80px");
		tab2.getColumnFormatter().setWidth(5, WIDTH_250PX);
		tab2.getColumnFormatter().setWidth(6, "auto");
		
		tab2.setWidget(1, 0, new Label( "C.N.A.E.") ); 
		tab2.getFlexCellFormatter().addStyleName(1,0,AON.CSS.aonDisplayTableHeader());
		tab2.setWidget(1, 1, new Label() ); 
		tab2.getFlexCellFormatter().addStyleName(1,1,AON.CSS.aonDisplayTableHeader());
		tab2.setWidget(1, 2, new Label( AON.MSG.operationsAmount()) );
		tab2.getFlexCellFormatter().addStyleName(1,2,AON.CSS.aonDisplayTableHeader());
		tab2.setWidget(1, 3, new Label( AON.MSG.operationsAmountWithRight()) );
		tab2.getFlexCellFormatter().addStyleName(1,3,AON.CSS.aonDisplayTableHeader());
		tab2.setWidget(1, 4, new Label( AON.MSG.type()) );
		tab2.getFlexCellFormatter().addStyleName(1,4,AON.CSS.aonDisplayTableHeader());
		tab2.setWidget(1, 5, new Label( AON.MSG.prorrataPercent()) );
		tab2.getFlexCellFormatter().addStyleName(1,5,AON.CSS.aonDisplayTableHeader());
		tab2.setWidget(1, 6, new Label() ); 
		tab2.getFlexCellFormatter().addStyleName(1,6,AON.CSS.aonDisplayTableHeader());
		
		paintProrrateRow(tab2,Mod390Key.BZ_P1C,Mod390Key.BZ_P1I,Mod390Key.BZ_P1D,Mod390Key.BZ_P1T,Mod390Key.BZ_P1P);
		paintProrrateRow(tab2,Mod390Key.BZ_P2C,Mod390Key.BZ_P2I,Mod390Key.BZ_P2D,Mod390Key.BZ_P2T,Mod390Key.BZ_P2P);
		paintProrrateRow(tab2,Mod390Key.BZ_P3C,Mod390Key.BZ_P3I,Mod390Key.BZ_P3D,Mod390Key.BZ_P3T,Mod390Key.BZ_P3P);
		paintProrrateRow(tab2,Mod390Key.BZ_P4C,Mod390Key.BZ_P4I,Mod390Key.BZ_P4D,Mod390Key.BZ_P4T,Mod390Key.BZ_P4P);
		paintProrrateRow(tab2,Mod390Key.BZ_P5C,Mod390Key.BZ_P5I,Mod390Key.BZ_P5D,Mod390Key.BZ_P5T,Mod390Key.BZ_P5P);
		return tab2;
	}

	private void paintProrrateRow(FlexTable tab, Mod390Key cnaeKey, Mod390Key amountKey, Mod390Key amountRightKey, Mod390Key typeKey, Mod390Key percentKey) {
		int row = tab.getRowCount();
		
		AonTextBox cnae = new AonTextBox();
		cnae.setVisibleLength(4);
		cnae.setMaxLength(4);
		cnae.setValue(getModel().getDescription(cnaeKey));
		cnae.addValueChangeHandler(event -> {
			getModel().putDescription(cnaeKey, cnae.getValue() );
			markAsDirty();
		});
		tab.setWidget(row, 0, cnae);

		AonCnae2009Panel panel = new AonCnae2009Panel();
		panel.addSelectionHandler(event -> { 
			cnae.setValue(event.getSelectedItem().getCodeWithoutPoint(),false);
			getModel().putDescription(cnaeKey, event.getSelectedItem().getCodeWithoutPoint());
			markAsDirty();
		});
		AonTableButton button = new AonTableButton("CNAE",AON.CSS.aonIconSearch());
		button.addClickHandler(event -> panel.onShow());
		tab.setWidget(row, 1, button);
		
		AonDoubleBox amount = new AonDoubleBox();
		amount.setValue(getModel().getAmount(amountKey));
		amount.addValueChangeHandler(event -> {
			getModel().putAmount(amountKey, amount.getValue() );
			markAsDirty();
		});
		tab.setWidget(row, 2, amount);
		
		AonDoubleBox amountRight = new AonDoubleBox();
		amountRight.setValue(getModel().getAmount(amountRightKey));
		amountRight.addValueChangeHandler(event -> {
			getModel().putAmount(amountRightKey, amountRight.getValue() );
			markAsDirty();
		});
		tab.setWidget(row, 3, amountRight);
		
		ListBox typeBox = new ListBox();
		typeBox.setWidth("40px");
		typeBox.addItem(" - ", "");
		typeBox.addItem("G - General", "G");
		typeBox.addItem("E - Especial", "E");
		String type = getModel().getDescription(typeKey);
		if (AonStringUtils.equals(type, "G")) typeBox.setSelectedIndex(1);
		else if (AonStringUtils.equals(type, "E")) typeBox.setSelectedIndex(2);
		else typeBox.setSelectedIndex(0); 
		typeBox.addChangeHandler(event -> {
			getModel().putDescription(typeKey,typeBox.getSelectedValue());
			markAsDirty();
		});
		tab.setWidget(row, 4, typeBox);
		
		
		AonDoubleBox percent = new AonDoubleBox();
		percent.setValue(getModel().getAmount(percentKey));
		percent.addValueChangeHandler(event -> {
			getModel().putAmount(percentKey, percent.getValue() );
			markAsDirty();
		});
		tab.setWidget(row, 5, percent);
	}

	private FlexTable getInvoiceTable() {
		FlexTable tab2 = new FlexTable();
		tab2.addStyleName(AON.CSS.aonDisplayTable());
		tab2.addStyleName(AON.CSS.aonMarginBottom());
		
		tab2.getColumnFormatter().setWidth(0, "80px");
		tab2.getColumnFormatter().setWidth(1, "200px");
		tab2.getColumnFormatter().setWidth(2, WIDTH_250PX);
		tab2.getColumnFormatter().setWidth(3, "80px");
		tab2.getColumnFormatter().setWidth(4, "200px");
		tab2.getColumnFormatter().setWidth(5, WIDTH_250PX);
		tab2.getColumnFormatter().setWidth(6, "auto");
		
		tab2.setWidget(0, 0, new Label( "Facturas emitidas") );
		tab2.getFlexCellFormatter().addStyleName(0,0,AON.CSS.aonDisplayTableHeader());
		tab2.getFlexCellFormatter().setColSpan(0, 0, 3);
		tab2.setWidget(0, 1, new Label("Facturas recibidas") ); 
		tab2.getFlexCellFormatter().addStyleName(0,1,AON.CSS.aonDisplayTableHeader());
		tab2.getFlexCellFormatter().setColSpan(0, 1, 3);

		tab2.setWidget(1, 0, new Label( "Serie") );
		tab2.getFlexCellFormatter().addStyleName(1,0,AON.CSS.aonDisplayTableHeader());
		tab2.setWidget(1, 1, new Label("Inicio") ); 
		tab2.getFlexCellFormatter().addStyleName(1,1,AON.CSS.aonDisplayTableHeader());
		tab2.setWidget(1, 2, new Label("terminaci\u00F3n") );
		tab2.getFlexCellFormatter().addStyleName(1,2,AON.CSS.aonDisplayTableHeader());
		
		tab2.setWidget(1, 3, new Label( "Serie") );
		tab2.getFlexCellFormatter().addStyleName(1,3,AON.CSS.aonDisplayTableHeader());
		tab2.setWidget(1, 4, new Label("Inicio") ); 
		tab2.getFlexCellFormatter().addStyleName(1,4,AON.CSS.aonDisplayTableHeader());
		tab2.setWidget(1, 5, new Label("terminaci\u00F3n") );
		tab2.getFlexCellFormatter().addStyleName(1,5,AON.CSS.aonDisplayTableHeader());
		tab2.setWidget(1, 6, new Label() ); 
		tab2.getFlexCellFormatter().addStyleName(1,6,AON.CSS.aonDisplayTableHeader());
		
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
		AonTextBox series = new AonTextBox();
		series.setVisibleLength(8);
		series.setMaxLength(5);
		series.setValue(getModel().getDescription(seriesKey));
		series.addValueChangeHandler(event -> {
			getModel().putDescription(seriesKey, series.getValue() );
			markAsDirty();
		});
		tab.setWidget(row, col, series);
		
		AonTextBox from = new AonTextBox();
		from.setVisibleLength(15);
		from.setMaxLength(15);
		from.setValue(getModel().getDescription(fromKey));
		from.addValueChangeHandler(event -> {
			getModel().putDescription(fromKey, from.getValue() );
			markAsDirty();
		});
		tab.setWidget(row, col+1, from);

		AonTextBox to = new AonTextBox();
		to.setVisibleLength(15);
		to.setMaxLength(15);
		to.setValue(getModel().getDescription(toKey));
		to.addValueChangeHandler(event -> {
			getModel().putDescription(toKey, to.getValue() );
			markAsDirty();
		});
		tab.setWidget(row, col+2, to);
	}
}



