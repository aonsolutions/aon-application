package com.esferalia.aon.gwt.fiscal.client.mod303;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDateBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTextBox;
import com.esferalia.aon.gwt.fiscal.client.mod303.Model303.Model303Callback;
import com.esferalia.aon.gwt.fiscal.client.model.FiscalModelAdmonPanel;
import com.esferalia.aon.gwt.fiscal.client.model.FiscalModelAdmonPanel.IFiscalModelAdmonPanelCallback;
import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.api.model.fiscal.mod303.Model3032022ARABAAdditionalDataScript;
import com.esferalia.aon.occam.api.model.fiscal.mod303.Model3032022ARABAResultScript;
import com.esferalia.aon.occam.api.model.fiscal.mod303.Model3032022ARABAScript2;
import com.esferalia.aon.occam.api.model.fiscal.mod303.Model3032023ARABARScript1;
import com.esferalia.aon.occam.api.model.type.Mod303Key;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;

class Model303ARABA2023 extends Model303Base {
	private AonTextBox receiptBox;
	private AonTextBox previousReceiptBox;
	private CheckBox withoutActivityCheck;
	private CheckBox cm02;
	private CheckBox arC910;
	private CheckBox arC911;
	private CheckBox arC930;
	private CheckBox arC907;
	private AonDateBox arC908;
	private ListBox arC909;
	
	protected Model303ARABA2023(Mod303 mod303,Model303Callback callback) {
		super(mod303,callback);
		
		TabLayoutPanel tabPanel = new TabLayoutPanel(26, Unit.PX);
		SimpleLayoutPanel centerPanel = new SimpleLayoutPanel();
		centerPanel.addStyleName(AON.CSS.aonScrollArea());
		centerPanel.setWidget(tabPanel);
		add(centerPanel);
		
		paintIdentificationTab(tabPanel);
		paintDeclarationTab(tabPanel);
		paintGeneralRegimenTab(tabPanel);
		paintResultTab(tabPanel);
		showPaymentInfo(getModel());
		paintAdditionalDataTab(tabPanel);
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

		withoutActivityCheck = paintWithoutActivityCheck(table);	// Sin actividad
		arC930 = paintCheck(Mod303Key.AR_C930 ,table);	// Presentación fuera de plazo por requerimiento
		arC907 = paintCheck(Mod303Key.AR_C907 ,table);	// ¿Ha sido declarado en concurso de acreedores en el presente per\u00EDodo de liquidaci\u00F3n?
		arC908 = paintDate( Mod303Key.AR_C908,table);	// Fecha en que se dictó el auto de declaración de concurso
		arC909 = paintC909(Mod303Key.AR_C909,table);	// Si se ha dictado auto de declaración de concurso en este periodo, indique el tipo de autoliquidación
		cm02 = paintCheck(Mod303Key.CM_002,table);		// ¿Está inscrito en el Registro de devolució3n mensual (Art. 30 RIVA)?
		arC910 = paintCheck(Mod303Key.AR_C910,table);	// ¿Ha optado por el régimen especial del criterio de Caja?
		arC911 = paintCheck(Mod303Key.AR_C911,table);	// ¿Es destinatario de operaciones a las que se aplique el régimen especial del criterio de caja?
		
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

		// Número de identificación declaracion anterior (necesario si la anterior se presento telematicamente)
		// Debe introducirse Ejercicio+Numero (EEEENNNNNN)
		if (getModel().isReplacement() || getModel().isComplementary()) {
			row = table.getRowCount();
			paintLabel(table, row, AON.MSG.previousReceipt() + " Formato EEEENNNNNN (Ejercicio + N\u00FAmero)");
			
			table.getFlexCellFormatter().addStyleName(row, 0,AON.CSS.aonPaddingLeft() );
			table.getFlexCellFormatter().addStyleName(row, 0,AON.CSS.aonBorderBottom() );
			previousReceiptBox = new AonTextBox();
			previousReceiptBox.setVisibleLength(15);
			previousReceiptBox.setEnabled(getModel().isEditable());
			previousReceiptBox.setMaxLength(12);
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

	private ListBox paintC909(Mod303Key key, FlexTable table) {
		arC909 = new ListBox();
		arC909.addItem("--");
		arC909.addItem("Preconsursal");
		arC909.addItem("Postconsursal");
		paintListBox(arC909, key, table);
		return arC909;
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
		paintDeclaration(table,Model3032023ARABARScript1.values(),8);
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
		paintDeclaration(table,Model3032022ARABAScript2.values(),6);
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
		paintDeclaration(table,Model3032022ARABAResultScript.values(),3);
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
		paintDeclaration(table,Model3032022ARABAAdditionalDataScript.values(),3);
	}
	
	private void paintAdministrationTab(TabLayoutPanel tabPanel) {
		IFiscalModelAdmonPanelCallback<Mod303, Model303ModuleOptions> cbk = 
				new IFiscalModelAdmonPanelCallback<Mod303, Model303ModuleOptions>() {

					@Override
					public Model303ModuleOptions getOptions() {
						return getCallback().getOptions();
					}

					@Override
					public Mod303 getModel() {
						return Model303ARABA2023.this.getModel();
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
						return Model303Base.MODEL303_FILE;
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
						return "https://egoitza.araba.eus/es/-/modelo-303-iva-autoliquidacion";
					}
			};
			admonPanel = new FiscalModelAdmonPanel<>(cbk);
			tabPanel.add( admonPanel, AON.MSG.administrationName(getModel().getAdministration()));		
	}
	
	@Override
	protected void decorateDeclarationTab() {
		enable(withoutActivityCheck);
		enable(cm02);
		enable(arC910);
		enable(arC911);
		enable(arC930);
		enable(arC907);
		enable(arC908);
		enable(arC909);
		enable(receiptBox);
		enable(previousReceiptBox);
	}
	
	
}
