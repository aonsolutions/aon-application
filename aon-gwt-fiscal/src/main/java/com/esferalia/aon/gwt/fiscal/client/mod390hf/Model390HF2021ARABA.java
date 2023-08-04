package com.esferalia.aon.gwt.fiscal.client.mod390hf;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTextBox;
import com.esferalia.aon.gwt.fiscal.client.mod390hf.Model390HF.Model390HFCallback;
import com.esferalia.aon.gwt.fiscal.client.model.FiscalModelAdmonPanel;
import com.esferalia.aon.gwt.fiscal.client.model.FiscalModelAdmonPanel.IFiscalModelAdmonPanelCallback;
import com.esferalia.aon.occam.api.model.fiscal.Mod390HF;
import com.esferalia.aon.occam.api.model.fiscal.mod390hf.Model3902017ARABARScript1;
import com.esferalia.aon.occam.api.model.fiscal.mod390hf.Model3902017ARABAResultScript;
import com.esferalia.aon.occam.api.model.fiscal.mod390hf.Model3902017ARABAScript2;
import com.esferalia.aon.occam.api.model.fiscal.mod390hf.Model3902021ARABAAdditionalDataScript;
import com.esferalia.aon.occam.api.model.type.Mod390Key;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;

public class Model390HF2021ARABA extends Model390HFBase {
	 
	public Model390HF2021ARABA(Model390HFCallback callback,Mod390HF mod390HF) {
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

		paintWithoutActivityCheck(table);	// Sin actividad

		paintCheck(Mod390Key.AR_C918,table);		// ¿Está inscrito en el Registro de devolució3n mensual (Art. 30 RIVA)?
		
		paintCheck(Mod390Key.AR_C910,table);	// ¿Ha optado por el régimen especial del criterio de Caja?
		paintCheck(Mod390Key.AR_C911,table);	// ¿Es destinatario de operaciones a las que se aplique el régimen especial del criterio de caja?
		
		paintCheck(Mod390Key.AR_C907 ,table);	// ¿Ha sido declarado en concurso de acreedores en el presente per\u00EDodo de liquidaci\u00F3n?
		paintDate( Mod390Key.AR_C908,table);	// Fecha en que se dictó el auto de declaración de concurso
		paintC909(Mod390Key.AR_C909,table);	// Si se ha dictado auto de declaración de concurso en este periodo, indique el tipo de autoliquidación
		
		paintCheck(Mod390Key.AR_C250,table);	// Opci\u00F3n por la aplicaci\u00F3n de la prorrata especial
		paintCheck(Mod390Key.AR_C251,table);	// Revocaci\u00F3n de la opci\u00F3n por la aplicaci\u00F3n de la prorrata especial
		paintCheck(Mod390Key.AR_C251,table);	// Prorrata general"
		paintCheck(Mod390Key.AR_C251,table);	// Prorrata especial"
		
		
		// Número de identificación declaracion anterior (necesario si la anterior se presento telematicamente)
		// Debe introducirse Ejercicio+Numero (EEEENNNNNN)
		if (getModel().isReplacement()) {
			int row = table.getRowCount();
			paintLabel(table, row, AON.MSG.previousReceipt() + " Formato EEEENNNNNN (Ejercicio + N\u00FAmero)");
			
			table.getFlexCellFormatter().addStyleName(row, 0,AON.CSS.aonPaddingLeft() );
			table.getFlexCellFormatter().addStyleName(row, 0,AON.CSS.aonBorderBottom() );
			final AonTextBox receiptBox = new AonTextBox();
			receiptBox.setVisibleLength(15);
			receiptBox.setMaxLength(12);
			receiptBox.setValue( getModel().getReplacedNumber() );
			receiptBox.addValueChangeHandler( event -> {
				getModel().setReplacedNumber(receiptBox.getValue());
				markAsDirty();
			});
			table.setWidget(row, 1, receiptBox);
		}		
		
		container.add(table);
		declarationScrollPanel.setWidget(container);
		tabPanel.add(declarationScrollPanel, AON.MSG.declaration());
	}

	private void paintC909(Mod390Key key, FlexTable table) {
		final ListBox c909 = new ListBox();
		c909.addItem("--");
		c909.addItem("Preconsursal");
		c909.addItem("Postconsursal");
		paintListBox(c909, key, table);
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
		paintDeclaration(table,Model3902017ARABARScript1.values(),8);
		
		paintScript(table,Model3902017ARABAScript2.values() ,8);
		
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
		paintDeclaration(table,Model3902017ARABAResultScript.values(),3);
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
		paintDeclaration(table,Model3902021ARABAAdditionalDataScript.values(),3);
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
						return Model390HF2021ARABA.this.getModel();
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
						return "https://egoitza.araba.eus/es/-/modelo-390";
					}
			};
			admonPanel = new FiscalModelAdmonPanel<>(cbk);
			tabPanel.add( admonPanel, AON.MSG.administrationName(getModel().getAdministration()));		
	}
}
