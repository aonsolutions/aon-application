package com.esferalia.aon.gwt.fiscal.client.mod390hf;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCnae2009Panel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDoubleBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTextBox;
import com.esferalia.aon.gwt.fiscal.client.mod390hf.Model390HF.Model390HFCallback;
import com.esferalia.aon.gwt.fiscal.client.model.FiscalModelAdmonPanel;
import com.esferalia.aon.gwt.fiscal.client.model.FiscalModelAdmonPanel.IFiscalModelAdmonPanelCallback;
import com.esferalia.aon.occam.api.model.fiscal.Mod390HF;
import com.esferalia.aon.occam.api.model.fiscal.mod390hf.Model3902017GIPUZKOAAdditionalDataScript;
import com.esferalia.aon.occam.api.model.fiscal.mod390hf.Model3902017GIPUZKOARScript1;
import com.esferalia.aon.occam.api.model.fiscal.mod390hf.Model3902017GIPUZKOAResultScript;
import com.esferalia.aon.occam.api.model.fiscal.mod390hf.Model3902021GIPUZKOASpecificOperationsScript;
import com.esferalia.aon.occam.api.model.type.Mod390Key;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;

public class Model390HF2021GIPUZKOA extends Model390HFBase {
	
	public Model390HF2021GIPUZKOA(Model390HFCallback callback,Mod390HF mod390HF) {
		super(mod390HF,callback);
		
		TabLayoutPanel tabPanel = new TabLayoutPanel(26, Unit.PX);
		SimpleLayoutPanel centerPanel = new SimpleLayoutPanel();
		centerPanel.addStyleName(AON.CSS.aonScrollArea());
		centerPanel.setWidget(tabPanel);
		add(centerPanel);
		
		paintIdentificationTab(tabPanel);
		paintDeclarationTab(tabPanel);
		paintGeneralRegimenTab(tabPanel);
		paintResultTab(tabPanel);
		paintAdditionalDataTab(tabPanel);
		paintSpecificOperationsTab(tabPanel);
		paintExtraTab(tabPanel);
		paintAdministrationTab(tabPanel);
		
	}

	private void paintDeclarationTab(TabLayoutPanel tabPanel) {
		ScrollPanel declarationScrollPanel = new ScrollPanel();
		FlowPanel container = new FlowPanel();
		
		FlexTable table = new FlexTable();
		table.setWidth("100%");
		table.addStyleName(AON.CSS.aonMarginBottom());
		
		table.getColumnFormatter().setWidth(0, "auto");
		table.getColumnFormatter().setWidth(1, "300px");
		table.getColumnFormatter().addStyleName(0, AON.CSS.aonPaddingLeft() );
		table.getColumnFormatter().addStyleName(0, AON.CSS.aonPaddingRight() );
		
		paintTextBox(table, Mod390Key.GP_I000, 9, true);
		
		paintWithoutActivityCheck(table);	// Sin actividad

		paintCheck(Mod390Key.GP_A000,table);	// Gran empresa
		paintCheck(Mod390Key.GP_A003,table);	// ¿Está inscrito en el Registro de devolució3n mensual (Art. 30 RIVA)?
		
		paintCheck(Mod390Key.GP_A001,table);	// Autoliquidación concursal. PRE
		paintCheck(Mod390Key.GP_A002,table);	// Autoliquidación concursal. POST
		
		paintCheck(Mod390Key.GP_A078,table);	// Opci\u00F3n por la aplicaci\u00F3n de la prorrata especial
		paintCheck(Mod390Key.GP_A079,table);	// Revocaci\u00F3n de la opci\u00F3n por la aplicaci\u00F3n de la prorrata especial

		paintCheck(Mod390Key.GP_A100,table);	// Incluido en el r\u00E9gimen especial de bienes usados, objetos de arte, antig\u00fcedades y objetos de colecci\u00F3n o agencias de viajes.
		
		container.add(table);
		declarationScrollPanel.setWidget(container);
		tabPanel.add(declarationScrollPanel, AON.MSG.declaration());
	}

	private void paintGeneralRegimenTab(TabLayoutPanel tabPanel) {
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
		paintDeclaration(table,Model3902017GIPUZKOARScript1.values(),8);
		container.add(table);
		
		generalRegimeScrollPanel.setWidget(container);
		tabPanel.add(generalRegimeScrollPanel, AON.MSG.generalRegime());
	}
	
	private void paintResultTab(TabLayoutPanel tabPanel) {
		ScrollPanel resultScrollPanel = new ScrollPanel();
		FlexTable table = new FlexTable();
		table.setWidth("100%");
		table.addStyleName(AON.CSS.aonMarginBottom());
		
		table.getColumnFormatter().setWidth(0, "auto");
		table.getColumnFormatter().addStyleName(0, AON.CSS.aonPaddingLeft() );
		table.getColumnFormatter().addStyleName(0, AON.CSS.aonPaddingRight() );
		table.getColumnFormatter().setWidth(1, "40px");
		table.getColumnFormatter().setStyleName(1, AON.CSS.aonTextCenter());
		table.getColumnFormatter().setWidth(2, WIDTH_140PX);
		table.getColumnFormatter().setWidth(3, "50px");
		resultScrollPanel.setWidget(table);
		tabPanel.add(resultScrollPanel, AON.MSG.result());
		paintDeclaration(table,Model3902017GIPUZKOAResultScript.values(),3);
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
		table.getColumnFormatter().setWidth(4, "60px");
		
		table.getColumnFormatter().setWidth(5, "40px");
		table.getColumnFormatter().setStyleName(5, AON.CSS.aonTextCenter());
		table.getColumnFormatter().setWidth(6, WIDTH_140PX);

		table.getColumnFormatter().setWidth(7, "40px");
		table.getColumnFormatter().setStyleName(7, AON.CSS.aonTextCenter());
		table.getColumnFormatter().setWidth(8, WIDTH_140PX);

		table.getColumnFormatter().setWidth(9, "50px");
		additionalDataScrollPanel.setWidget(table);
		tabPanel.add(additionalDataScrollPanel, "Inf. Adicional");
		paintDeclaration(table,Model3902017GIPUZKOAAdditionalDataScript.values(),10);
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
		paintDeclaration(table,Model3902021GIPUZKOASpecificOperationsScript.values(),4);
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
		tab2.getFlexCellFormatter().addStyleName(1,3,AON.CSS.aonTextCenter());
		tab2.setWidget(1, 4, new Label( AON.MSG.type()) );
		tab2.getFlexCellFormatter().addStyleName(1,4,AON.CSS.aonDisplayTableHeader());
		tab2.setWidget(1, 5, new Label( AON.MSG.prorrataPercent()) );
		tab2.getFlexCellFormatter().addStyleName(1,5,AON.CSS.aonDisplayTableHeader());
		tab2.setWidget(1, 6, new Label() ); 
		tab2.getFlexCellFormatter().addStyleName(1,6,AON.CSS.aonDisplayTableHeader());
		
		paintProrrateRow(tab2,Mod390Key.GP_P1C,Mod390Key.GP_P1I,Mod390Key.GP_P1D,Mod390Key.GP_P1T,Mod390Key.GP_P1P);
		paintProrrateRow(tab2,Mod390Key.GP_P2C,Mod390Key.GP_P2I,Mod390Key.GP_P2D,Mod390Key.GP_P2T,Mod390Key.GP_P2P);
		paintProrrateRow(tab2,Mod390Key.GP_P3C,Mod390Key.GP_P3I,Mod390Key.GP_P3D,Mod390Key.GP_P3T,Mod390Key.GP_P3P);
		paintProrrateRow(tab2,Mod390Key.GP_P4C,Mod390Key.GP_P4I,Mod390Key.GP_P4D,Mod390Key.GP_P4T,Mod390Key.GP_P4P);
		paintProrrateRow(tab2,Mod390Key.GP_P5C,Mod390Key.GP_P5I,Mod390Key.GP_P5D,Mod390Key.GP_P5T,Mod390Key.GP_P5P);
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
		panel.addSelectionHandler( event -> {
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
		tab.getFlexCellFormatter().addStyleName(row, 2, AON.CSS.aonTextCenter());		
		
		AonDoubleBox amountRight = new AonDoubleBox();
		amountRight.setValue(getModel().getAmount(amountRightKey));
		amountRight.addValueChangeHandler(event -> {
			getModel().putAmount(amountRightKey, amountRight.getValue() );
			markAsDirty();
		});
		tab.setWidget(row, 3, amountRight);
		tab.getFlexCellFormatter().addStyleName(row, 3, AON.CSS.aonTextCenter());
		
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
		
		AonDoubleBox percent = new AonDoubleBox(6);
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
		tab2.getFlexCellFormatter().setColSpan(0, 0, 4);
		tab2.setWidget(0, 1, new Label("Facturas recibidas") ); 
		tab2.getFlexCellFormatter().addStyleName(0,1,AON.CSS.aonDisplayTableHeader());
		tab2.getFlexCellFormatter().setColSpan(0, 1, 4);

		tab2.setWidget(1, 0, new Label( "Serie") );
		tab2.getFlexCellFormatter().addStyleName(1,0,AON.CSS.aonDisplayTableHeader());
		tab2.setWidget(1, 1, new Label("Inicio") ); 
		tab2.getFlexCellFormatter().addStyleName(1,1,AON.CSS.aonDisplayTableHeader());
		tab2.setWidget(1, 2, new Label("Terminaci\u00F3n") );
		tab2.getFlexCellFormatter().addStyleName(1,2,AON.CSS.aonDisplayTableHeader());
		tab2.setWidget(1, 3, new Label("Registros") );
		tab2.getFlexCellFormatter().addStyleName(1,3,AON.CSS.aonDisplayTableHeader());
		
		tab2.setWidget(1, 4, new Label( "Serie") );
		tab2.getFlexCellFormatter().addStyleName(1,4,AON.CSS.aonDisplayTableHeader());
		tab2.setWidget(1, 5, new Label("Inicio") ); 
		tab2.getFlexCellFormatter().addStyleName(1,5,AON.CSS.aonDisplayTableHeader());
		tab2.setWidget(1, 6, new Label("Terminaci\u00F3n") );
		tab2.getFlexCellFormatter().addStyleName(1,6,AON.CSS.aonDisplayTableHeader());
		tab2.setWidget(1, 7, new Label("Registros") );
		tab2.getFlexCellFormatter().addStyleName(1,7,AON.CSS.aonDisplayTableHeader());

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
		from.setVisibleLength(10);
		from.setMaxLength(10);
		from.setValue(getModel().getDescription(fromKey));
		from.addValueChangeHandler(event -> {
			getModel().putDescription(fromKey, from.getValue() );
			markAsDirty();
		});
		tab.setWidget(row, col+1, from);

		AonTextBox to = new AonTextBox();
		to.setVisibleLength(10);
		to.setMaxLength(10);
		to.setValue(getModel().getDescription(toKey));
		to.addValueChangeHandler(event -> {
			getModel().putDescription(toKey, to.getValue() );
			markAsDirty();
		});
		tab.setWidget(row, col+2, to);
		
		AonDoubleBox count = new AonDoubleBox();
		count.setVisibleLength(9);
		count.setMaxLength(9);
		count.setValue(getModel().getAmount(countKey));
		count.addValueChangeHandler(event -> {
			getModel().putAmount(countKey, count.getValue() );
			markAsDirty();
		});
		
		tab.setWidget(row, col+3, count);
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
						return Model390HF2021GIPUZKOA.this.getModel();
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
						return GWT.getHostPageBaseURL() +"aon_gwt_fiscal/ms/Mod390HFCheckDataResponseData";
					}

					@Override
					public String getModelInformationURL() {
						return "https://www.gipuzkoa.eus/es/web/ogasuna/impuestos/modelo/390";
					}
			};
			admonPanel = new FiscalModelAdmonPanel<>(cbk);
			tabPanel.add( admonPanel, AON.MSG.administrationName(getModel().getAdministration()));		
	}

}
