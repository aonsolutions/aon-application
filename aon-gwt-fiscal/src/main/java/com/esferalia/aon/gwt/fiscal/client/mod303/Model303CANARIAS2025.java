package com.esferalia.aon.gwt.fiscal.client.mod303;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDateBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTextBox;
import com.esferalia.aon.gwt.fiscal.client.mod303.Model303.Model303Callback;
import com.esferalia.aon.gwt.fiscal.client.model.FiscalModelAdmonPanel;
import com.esferalia.aon.gwt.fiscal.client.model.FiscalModelAdmonPanel.IFiscalModelAdmonPanelCallback;
import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.api.model.fiscal.mod303.Model3032025CANARIASAdditionalDataScript;
import com.esferalia.aon.occam.api.model.fiscal.mod303.Model3032025CANARIASIgicScript1;
import com.esferalia.aon.occam.api.model.fiscal.mod303.Model3032025CANARIASIgicScript2;
import com.esferalia.aon.occam.api.model.type.Mod303Key;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;

class Model303CANARIAS2025 extends Model303Base {

	protected AonTextBox receiptBox;

	// FALTA - ULTIMO PERIODO (MODELO 417) POR AHORA NO SE AÑADE
//	private ScrollPanel lastPeriodPanel;
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
//	private static final int LAST_PERIOD_INFORMATION_TAB = 3;

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
		// FALTA - ULTIMO PERIODO (MODELO 417) POR AHORA NO SE AÑADE
//		if (getModel().isLastPeriod()) {
//			paintLastPeriodInformationTab(tabPanel);
//		}
		paintAdministrationTab(tabPanel);
		tabPanel.addBeforeSelectionHandler(this::beforeSelectTab);
		Scheduler.get().scheduleDeferred(this::selectDefaultTab);
	}
	
	private void selectDefaultTab() {
		tabPanel.selectTab(LIQUIDATION_TAB);
	}
	
	private void beforeSelectTab(BeforeSelectionEvent<Integer> event) {
//		double aa02 = getModel().getAmount(Mod303Key.CT_A02);
//		if (event.getItem() == GENERAL_REGIME_TAB && aa02 == 0) {
//			event.cancel();
//			AonMessageDialog.warning("No procede para este tipo de declaraci\u00F3n");
//		}
//		if (event.getItem() == SIMPLIFIED_REGIME_TAB && aa02 == 2) {
//			event.cancel();
//			AonMessageDialog.warning("No procede para este tipo de declaraci\u00F3n");
//		}
		// FALTA - ULTIMO PERIODO SOLO MODELO 417 
//		if (getModel().isLastPeriod()) {
//			double aa11 = getModel().getAmount(Mod303Key.CT_A11);
//			if (event.getItem() == LAST_PERIOD_INFORMATION_TAB && aa11 == 0) {
//				event.cancel();
//				AonMessageDialog.warning("Para rellenar estos datos, debe rellenar la casilla \""+Mod303Key.CT_A11.getDescription()+ "\" en la solapa \"Declaraci\u00F3n\"");
//			}
//		}
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
		paintDeclaration(table, Model3032025CANARIASIgicScript1.values(), 8);
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
	
// FALTA - METODOS PARA EL ULTIMO PERIODO (MODELO 417) POR AHORA NO SE AÑADE	
	
//	private void paintLastPeriodInformationTab(TabLayoutPanel tabPanel) {
//		lastPeriodPanel = new ScrollPanel();
//		fillLastPeriodInformationScrollPanel();
//		tabPanel.add(lastPeriodPanel, "Inf. Exonerados 390.");
//	}

//	private void fillLastPeriodInformationScrollPanel() {
//		FlexTable table = new FlexTable();
//		table.setWidth("100%");
//		table.addStyleName(AON.CSS.aonMarginBottom());
//		
//		table.getColumnFormatter().setWidth(0, "auto");
//		table.getColumnFormatter().addStyleName(0, AON.CSS.aonPaddingLeft() );
//		table.getColumnFormatter().addStyleName(0, AON.CSS.aonPaddingRight() );
//		table.getColumnFormatter().setWidth(1, "40px");
//		table.getColumnFormatter().setStyleName(1, AON.CSS.aonTextCenter());
//		table.getColumnFormatter().setWidth(2, WIDTH_140PX);
//		table.getColumnFormatter().setWidth(3, "50px");
//
//		paintLabel(table, 0, AON.MSG.activities(),true);
//
//		FlowPanel actContainer = new FlowPanel();
//		actContainer.setStyleName(AON.CSS.aonBlockCenter());
//		actContainer.addStyleName(AON.CSS.aonWidthAlmostAll());
//
//		FlexTable tab = new FlexTable();
//		tab.addStyleName(AON.CSS.aonTable());
//		tab.addStyleName(AON.CSS.aonMarginBottom());
//		
//		tab.getColumnFormatter().setWidth(0, "40px");
//		tab.getColumnFormatter().setWidth(1, "80px");
//		tab.getColumnFormatter().setWidth(2, "60px");
//		tab.getColumnFormatter().setWidth(3, "250px");
//		tab.getColumnFormatter().setWidth(4, "auto");
//		
//		tab.setWidget(1, 0, new Label() );
//		tab.setWidget(1, 1, new Label( AON.MSG.epigraph()) );
//		tab.getFlexCellFormatter().addStyleName(1,1,AON.CSS.aonBold());
//		tab.getFlexCellFormatter().addStyleName(1,1,AON.CSS.aonBorderBottom());
//		tab.setWidget(1, 2, new Label( AON.MSG.key()) );
//		tab.getFlexCellFormatter().addStyleName(1,2,AON.CSS.aonBold());
//		tab.getFlexCellFormatter().addStyleName(1,2,AON.CSS.aonBorderBottom());
//		tab.setWidget(1, 3, new Label( AON.MSG.description()) );
//		tab.getFlexCellFormatter().addStyleName(1,3,AON.CSS.aonBold());
//		tab.getFlexCellFormatter().addStyleName(1,3,AON.CSS.aonBorderBottom());
//		tab.setWidget(1, 4, new Label() );
//		tab.getFlexCellFormatter().addStyleName(1,4,AON.CSS.aonBold());
//		tab.getFlexCellFormatter().addStyleName(1,4,AON.CSS.aonBorderBottom());
//		
//		
//		paintActivityRow(tab,Mod303Key.CT_U1D,Mod303Key.CT_U1C,Mod303Key.CT_U1E);
//		paintActivityRow(tab,Mod303Key.CT_U2D,Mod303Key.CT_U2C,Mod303Key.CT_U2E);
//		paintActivityRow(tab,Mod303Key.CT_U3D,Mod303Key.CT_U3C,Mod303Key.CT_U3E);
//		paintActivityRow(tab,Mod303Key.CT_U4D,Mod303Key.CT_U4C,Mod303Key.CT_U4E);
//		paintActivityRow(tab,Mod303Key.CT_U5D,Mod303Key.CT_U5C,Mod303Key.CT_U5E);
//		
//		actContainer.add(tab);
//		table.setWidget(1, 0, actContainer);
//		table.getFlexCellFormatter().setColSpan(1, 0, 4);
//		
//		paintCheck(Mod303Key.CT_U13, table);
//		
//		paintScript(table,Model3032022AEAT390nfoScript.values(),3);
//		
//		paintLabel(table, table.getRowCount() , AON.MSG.prorrata(),true);
//		
//		FlowPanel actContainer2 = new FlowPanel();
//		actContainer2.setStyleName(AON.CSS.aonBlockCenter());
//		actContainer2.addStyleName(AON.CSS.aonWidthAlmostAll());
//
//		FlexTable tab2 = new FlexTable();
//		tab2.addStyleName(AON.CSS.aonTable());
//		tab2.addStyleName(AON.CSS.aonMarginBottom());
//		
//		tab2.getColumnFormatter().setWidth(0, "80px");
//		tab2.getColumnFormatter().setWidth(1, "20px");
//		tab2.getColumnFormatter().setWidth(2, WIDTH_150PX);
//		tab2.getColumnFormatter().setWidth(3, WIDTH_150PX);
//		tab2.getColumnFormatter().setWidth(4, "80px");
//		tab2.getColumnFormatter().setWidth(5, WIDTH_150PX);
//		tab2.getColumnFormatter().setWidth(6, "auto");
//		
//		tab2.setWidget(1, 0, new Label( "C.N.A.E.") ); 
//		tab2.getFlexCellFormatter().addStyleName(1,0,AON.CSS.aonBold());
//		tab2.getFlexCellFormatter().addStyleName(1,0,AON.CSS.aonBorderBottom());
//		tab2.setWidget(1, 1, new Label() ); 
//		tab2.getFlexCellFormatter().addStyleName(1,1,AON.CSS.aonBold());
//		tab2.getFlexCellFormatter().addStyleName(1,1,AON.CSS.aonBorderBottom());
//		tab2.setWidget(1, 2, new Label( AON.MSG.operationsAmount()) );
//		tab2.getFlexCellFormatter().addStyleName(1,2,AON.CSS.aonBold());
//		tab2.getFlexCellFormatter().addStyleName(1,2,AON.CSS.aonBorderBottom());
//		tab2.setWidget(1, 3, new Label( AON.MSG.operationsAmountWithRight()) );
//		tab2.getFlexCellFormatter().addStyleName(1,3,AON.CSS.aonBold());
//		tab2.getFlexCellFormatter().addStyleName(1,3,AON.CSS.aonBorderBottom());
//		tab2.setWidget(1, 4, new Label( AON.MSG.type()) );
//		tab2.getFlexCellFormatter().addStyleName(1,4,AON.CSS.aonBold());
//		tab2.getFlexCellFormatter().addStyleName(1,4,AON.CSS.aonBorderBottom());
//		tab2.setWidget(1, 5, new Label( AON.MSG.prorrataPercent()) );
//		tab2.getFlexCellFormatter().addStyleName(1,5,AON.CSS.aonBold());
//		tab2.getFlexCellFormatter().addStyleName(1,5,AON.CSS.aonBorderBottom());
//		tab2.setWidget(1, 6, new Label() ); 
//		tab2.getFlexCellFormatter().addStyleName(1,6,AON.CSS.aonBold());
//		tab2.getFlexCellFormatter().addStyleName(1,6,AON.CSS.aonBorderBottom());
//		
//		paintProrrateRow(tab2,Mod303Key.CT_P1C,Mod303Key.CT_P1I,Mod303Key.CT_P1D,Mod303Key.CT_P1T,Mod303Key.CT_P1P);
//		paintProrrateRow(tab2,Mod303Key.CT_P2C,Mod303Key.CT_P2I,Mod303Key.CT_P2D,Mod303Key.CT_P2T,Mod303Key.CT_P2P);
//		paintProrrateRow(tab2,Mod303Key.CT_P3C,Mod303Key.CT_P3I,Mod303Key.CT_P3D,Mod303Key.CT_P3T,Mod303Key.CT_P3P);
//		paintProrrateRow(tab2,Mod303Key.CT_P4C,Mod303Key.CT_P4I,Mod303Key.CT_P4D,Mod303Key.CT_P4T,Mod303Key.CT_P4P);
//		paintProrrateRow(tab2,Mod303Key.CT_P5C,Mod303Key.CT_P5I,Mod303Key.CT_P5D,Mod303Key.CT_P5T,Mod303Key.CT_P5P);
//
//		actContainer2.add(tab2);
//		
//		int row = table.getRowCount();
//		table.setWidget(row, 0, actContainer2);
//		table.getFlexCellFormatter().setColSpan(row, 0, 4);
//		
//		lastPeriodPanel.setWidget(table);
//	}

//	private void paintProrrateRow(FlexTable tab, Mod303Key cnaeKey, Mod303Key amountKey, Mod303Key amountRightKey, Mod303Key typeKey, Mod303Key percentKey) {
//		int row = tab.getRowCount();
//		
//		AonTextBox cnae = new AonTextBox();
//		cnae.setVisibleLength(4);
//		cnae.setMaxLength(4);
//		cnae.setValue(getModel().getDescription(cnaeKey));
//		cnae.addValueChangeHandler(event -> {
//			getModel().putDescription(cnaeKey, cnae.getValue() );
//			markAsDirty();
//		});
//		tab.setWidget(row, 0, cnae);
//
//		AonCnae2009Panel panel = new AonCnae2009Panel();
//		panel.addSelectionHandler(event -> {
//			cnae.setValue(event.getSelectedItem().getCodeWithoutPoint(),false);
//			getModel().putDescription(cnaeKey, event.getSelectedItem().getCodeWithoutPoint());
//			markAsDirty();
//		});
//		AonTableButton button = new AonTableButton("CNAE",AON.CSS.aonIconSearch());
//		button.addClickHandler(event -> panel.onShow());
//		tab.setWidget(row, 1, button);
//		
//		AonDoubleBox amount = new AonDoubleBox();
//		amount.setValue(getModel().getAmount(amountKey));
//		amount.addValueChangeHandler(event -> {
//			getModel().putAmount(amountKey, amount.getValue() );
//			markAsDirty();
//		});
//		tab.setWidget(row, 2, amount);
//		
//		AonDoubleBox amountRight = new AonDoubleBox();
//		amountRight.setValue(getModel().getAmount(amountRightKey));
//		amountRight.addValueChangeHandler(event -> {
//			getModel().putAmount(amountRightKey, amountRight.getValue() );
//			markAsDirty();
//		});
//		tab.setWidget(row, 3, amountRight);
//		
//		ListBox typeBox = new ListBox();
//		typeBox.setWidth("40px");
//		typeBox.addItem(" - ", "");
//		typeBox.addItem("G - General", "G");
//		typeBox.addItem("E - Especial", "E");
//		String type = getModel().getDescription(typeKey);
//		if (AonStringUtils.equals(type, "G")) typeBox.setSelectedIndex(1);
//		else if (AonStringUtils.equals(type, "E")) typeBox.setSelectedIndex(2);
//		else typeBox.setSelectedIndex(0); 
//		typeBox.addChangeHandler(event ->  {
//			getModel().putDescription(typeKey,typeBox.getSelectedValue());
//			markAsDirty();
//		});
//		tab.setWidget(row, 4, typeBox);
//		
//		
//		AonDoubleBox percent = new AonDoubleBox();
//		percent.setValue(getModel().getAmount(percentKey));
//		percent.addValueChangeHandler(event -> {
//			getModel().putAmount(percentKey, percent.getValue() );
//			markAsDirty();
//		});
//		tab.setWidget(row, 5, percent);
//	}

//	private void paintActivityRow(FlexTable tab, Mod303Key desKey, Mod303Key keyKey, Mod303Key epiKey) {
//		int row = tab.getRowCount();
//
//		tab.setWidget(row, 0, new Label( row == 2 ? "Principal" : "Otras" ) );
//
//		AonTextBox epi = new AonTextBox();
//		epi.setVisibleLength(5);
//		epi.setMaxLength(4);
//		epi.setValue(getModel().getDescription(epiKey));
//		epi.addValueChangeHandler(event -> {
//			getModel().putDescription(epiKey,epi.getValue());
//			markAsDirty();
//		});
//		tab.setWidget(row, 1, epi);
//
//		ActivityTypeListBox key = new ActivityTypeListBox();
//		key.setValue( ActivityType.ensure(getModel().getDescription(keyKey)) );
//		key.addChangeHandler(event ->  {
//			getModel().putDescription(keyKey,ActivityType.toString(key.getValue()));
//			markAsDirty();
//		});
//		tab.setWidget(row, 2, key);
//
//		AonTextBox description = new AonTextBox();
//		description.setVisibleLength(40);
//		description.setMaxLength(40);
//		description.setValue(getModel().getDescription(desKey));
//		description.addValueChangeHandler(event -> {
//			getModel().putDescription(desKey,description.getValue());
//			markAsDirty();
//		});
//		tab.setWidget(row, 3, description);
//		
//		tab.setWidget(row, 4, new Label() );
//
//	}
	
	@Override
	protected void decorateDeclarationTab() {
		super.decorateDeclarationTab();
		enable( withoutActivityCheck );
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
			receiptBox.setValue( getModel().getNumber() );
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
						//return GWT.getHostPageBaseURL() +"aon_gwt_fiscal/ms/Mod303ValidatePrintAEAT";
						return null;
					}

					@Override
					public String getDownloadFileAction() {
						if (getModel().isQuarterPeriod())
							return "/aon_gwt_fiscal/ms/Model420File"; // Por ahora solo para el modelo 420
						else 
							return null;
					}

					@Override
					public String getSendAction() {
//						return GWT.getHostPageBaseURL() +"aon_gwt_fiscal/ms/Mod303SendAEAT";
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
//						return GWT.getHostPageBaseURL() +"aon_gwt_fiscal/ms/Mod303CheckAEAT";
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
			tabPanel.add( admonPanel, AON.MSG.administrationName(getModel().getAdministration()));		
	}
	
}
