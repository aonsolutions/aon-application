package com.esferalia.aon.gwt.fiscal.client.mod303;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCnae2009Panel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDateBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDoubleBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessageDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTextBox;
import com.esferalia.aon.gwt.fiscal.client.mod303.Model303.Model303Callback;
import com.esferalia.aon.gwt.fiscal.client.mod303.Model303AEATActivity2020.IMod303ActivityCallback;
import com.esferalia.aon.gwt.fiscal.client.mod303.Model303AEATActivityFarmer.IMod303ActivityFarmerCallback;
import com.esferalia.aon.gwt.fiscal.client.mod390.e2022.ActivityTypeListBox;
import com.esferalia.aon.occam.api.model.fiscal.ActivityType;
import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.api.model.fiscal.Mod303Activity;
import com.esferalia.aon.occam.api.model.fiscal.Mod303ActivityFarmer;
import com.esferalia.aon.occam.api.model.fiscal.mod303.Model3032022AEAT390nfoScript;
import com.esferalia.aon.occam.api.model.fiscal.mod303.Model3032022AEATAdditionalDataScript;
import com.esferalia.aon.occam.api.model.fiscal.mod303.Model3032022AEATGeneralRegimeScript1;
import com.esferalia.aon.occam.api.model.fiscal.mod303.Model3032022AEATGeneralRegimeScript2;
import com.esferalia.aon.occam.api.model.fiscal.mod303.Model3032022AEATResultScript;
import com.esferalia.aon.occam.api.model.fiscal.mod303.Model3032022AEATSimplifiedRegime4TScript;
import com.esferalia.aon.occam.api.model.fiscal.mod303.Model3032022AEATSimplifiedRegimeScript;
import com.esferalia.aon.occam.api.model.type.Mod303Key;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

class Model303AEAT2022 extends Model303AEAT {

	private final Model303AEATActivityFarmerTable farmerTable;
	private final Model303AEATActivityTable activityTable;

	private ScrollPanel lastPeriodPanel;
	private TabLayoutPanel tabPanel;
	
	private CheckBox withoutActivityCheck;
	private ListBox a12;
	private CheckBox cm2;
	private CheckBox a03;
	private CheckBox a07;
	private CheckBox a08;
	private CheckBox a09;
	private CheckBox a10;
	private CheckBox a04;
	private AonDateBox  a05;
	private ListBox a02;
	private ListBox a06;
	private ListBox a13; 
	private ListBox a14;
	private ListBox a11;
	private AonTextBox previousReceiptBox;

	private static final int GENERAL_REGIME_TAB = 2;
	private static final int SIMPLIFIED_REGIME_TAB = 3;
	private static final int RESULT_TAB = 4;
	private static final int LAST_PERIOD_INFORMATION_TAB = 6;

	protected Model303AEAT2022(Mod303 mod303,Model303Callback callback) {
		super(mod303,callback);
		tabPanel = new TabLayoutPanel(26, Unit.PX);
		
		SimpleLayoutPanel centerPanel = new SimpleLayoutPanel();
		centerPanel.addStyleName(AON.CSS.aonScrollArea());
		centerPanel.setWidget(tabPanel);
		add(centerPanel);
		
		farmerTable = new Model303AEATActivityFarmerTable( mod303.isLastPeriod());
		activityTable = new Model303AEATActivityTable( mod303.isLastPeriod());
		
		paintIdentificationTab(tabPanel);
		paintDeclarationTab(tabPanel);
		paintGeneralRegimeTab(tabPanel);
		paintSimplifiedRegimeTab(tabPanel);
		paintResultTab(tabPanel);
		showPaymentInfo(getModel());
		paintAdditionalDataTab(tabPanel);
		if (getModel().isLastPeriod()) {
			paintLastPeriodInformationTab(tabPanel);
		}
		paintAdministrationTab(tabPanel);
		tabPanel.addBeforeSelectionHandler(this::beforeSelectTab);
		Scheduler.get().scheduleDeferred(this::selectDefaultTab);
	}
	
	private void selectDefaultTab() {
		if (getModel().isFinished() || getModel().isSent()) {
			tabPanel.selectTab(RESULT_TAB);
		} else {
			if (getModel().getAmount(Mod303Key.CT_A02) == 0) {
				tabPanel.selectTab(SIMPLIFIED_REGIME_TAB);	
			} else {
				tabPanel.selectTab(GENERAL_REGIME_TAB);
			}
		}
	}
	
	private void beforeSelectTab(BeforeSelectionEvent<Integer> event) {
		double aa02 = getModel().getAmount(Mod303Key.CT_A02);
		if (event.getItem() == GENERAL_REGIME_TAB && aa02 == 0) {
			event.cancel();
			AonMessageDialog.warning("No procede para este tipo de declaraci\u00F3n");
		}
		if (event.getItem() == SIMPLIFIED_REGIME_TAB && aa02 == 2) {
			event.cancel();
			AonMessageDialog.warning("No procede para este tipo de declaraci\u00F3n");
		}
		if (getModel().isLastPeriod()) {
			double aa11 = getModel().getAmount(Mod303Key.CT_A11);
			if (event.getItem() == LAST_PERIOD_INFORMATION_TAB && aa11 == 0) {
				event.cancel();
				AonMessageDialog.warning("Para rellenar estos datos, debe rellenar la casilla \""+Mod303Key.CT_A11.getDescription()+ "\" en la solapa \"Declaraci\u00F3n\"");
			}
		}
	}
	
	private void paintGeneralRegimeTab(TabLayoutPanel tabPanel) {
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
		paintDeclaration(table,Model3032022AEATGeneralRegimeScript1.values(),8);
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
		paintDeclaration(table,Model3032022AEATGeneralRegimeScript2.values(),6);
		container.add(table);
		
		generalRegimeScrollPanel.setWidget(container);
		tabPanel.add(generalRegimeScrollPanel, AON.MSG.generalRegime());
	}
	
	private void paintSimplifiedRegimeTab(TabLayoutPanel tabPanel) {
		ScrollPanel simplifiedRegimeScrollPanel = new ScrollPanel();
		simplifiedRegimeScrollPanel.setWidget(getSimplifiedRegimePanel());
		tabPanel.add(simplifiedRegimeScrollPanel, AON.MSG.simplifiedRegime());
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
		paintDeclaration(table,Model3032022AEATResultScript.values(),3);
		if (getModel().isLastPeriod() && AonMathUtils.isNotZero(getModel().getAmount(Mod303Key.CT_C110))) {
			Widget c78 = table.getWidget(7, 0);
			AonDisplayTable fp78 = new AonDisplayTable();
			fp78.addStyleName(AON.CSS.aonWidthAll());
			InlineLabel l780 =  new InlineLabel("REVISE CONTENIDO. EDITE SI PROCEDE.");
			l780.setStyleName(AON.CSS.aonMarginLeft());
			l780.addStyleName(AON.CSS.aonColorRed());
			
			InlineLabel l781 =  new InlineLabel("[Poner a cero]");
			l781.setStyleName(AON.CSS.aonClickableLabel());
			l781.addStyleName(AON.CSS.aonMarginLeft());
			l781.addClickHandler(event -> getFieldsMap().get(Mod303Key.CT_C78).setValue(0.0,true));
			
			InlineLabel l782 =  new InlineLabel("Copiar [110]");
			l782.setStyleName(AON.CSS.aonClickableLabel());
			l782.addStyleName(AON.CSS.aonMarginLeft());
			l782.addClickHandler(event -> getFieldsMap().get(Mod303Key.CT_C78).setValue(getModel().getAmount(Mod303Key.CT_C110),true));

			InlineLabel l783 =  new InlineLabel("Asignar [066+077]");
			l783.setStyleName(AON.CSS.aonClickableLabel());
			l783.addStyleName(AON.CSS.aonMarginLeft());
			l783.addClickHandler(event -> {
				double c66 = AonNumberUtils.todouble( getFieldsMap().get(Mod303Key.CT_C66).getValue());
				double c77 = AonNumberUtils.todouble( getFieldsMap().get(Mod303Key.CT_C77).getValue());
				double amount = AonMathUtils.round(c66+c77); 
				getFieldsMap().get(Mod303Key.CT_C78).setValue(amount,true);
			});
			
			FlowPanel fp = new FlowPanel();
			fp.add(l781);
			fp.add(l782);
			fp.add(l783);
			
			fp78.addRow()
				.addCell(new Label(), AON.CSS.aonWidthAuto())
				.addCell(l780, AON.CSS.aonTextCenter(),AON.CSS.aonNowrap(),AON.CSS.aonBold(),AON.CSS.aonWidth80());
			fp78.addRow()
				.addCell(c78, AON.CSS.aonWidthAuto())
				.addCell(fp  , AON.CSS.aonTextRight(),AON.CSS.aonNowrap(),AON.CSS.aonWidth80());
			table.setWidget(7, 0, fp78);
		}
		
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
		paintDeclaration(table,Model3032022AEATAdditionalDataScript.values(),3);
	}
		
	private void paintDeclarationTab(TabLayoutPanel tabPanel) {
		ScrollPanel declarationScrollPanel = new ScrollPanel();
		FlowPanel container = new FlowPanel();
		
		FlexTable table = createTable();
		withoutActivityCheck = paintWithoutActivityCheck(table);	// Sin actividad
		
		// Tributacion exclusivamente foral
		a12 = new ListBox();
		a12.setWidth(WIDTH_150PX);
		a12.addItem("(0) Para el mes de enero (01)", "0");
		a12.addItem(SI_1, "1");
		a12.addItem(NO_2, "2");
		paintListBox(a12, Mod303Key.CT_A12, table);
		
		cm2 = paintCheck(Mod303Key.CM_002,table);	// Inscrito en el Registro de devolución mensual (Art. 30 RIVA)
		paintA02(Mod303Key.CT_A02,table);	// Tributa exclusivamente en régimen simplificado
		a03 = paintCheck(Mod303Key.CT_A03,table);	// Autoliquidación conjunta
		a07 = paintCheck(Mod303Key.CT_A07,table);	// Acogido al régimen especial del criterio de Caja (art. 163 undecies LIVA)
		a08 = paintCheck(Mod303Key.CT_A08,table);	// Destinatario de operaciones acogidas al régimen especial del criterio de caja
		
		a09 = paintCheck(Mod303Key.CT_A09,table);	// Opción por la aplicación de la prorrata especial
		a10 = paintCheck(Mod303Key.CT_A10,table);	// Revocación de la opción por la aplicación de la prorrata especial
		
		a04 = paintCheck(Mod303Key.CT_A04,table);	// Declarado en concurso de acreedores en el presente período de liquidación
		a05 = paintDate (Mod303Key.CT_A05,table);	// Fecha en que se dictó el auto de declaración de concurso

		// Auto de declaración de concurso dictado en el período
		a06 = new ListBox();
		a06.setWidth("200px");
		a06.addItem("NO", "0");
		a06.addItem("(1) SI Preconcursal", "1");
		a06.addItem("(2) SI Postconcursal", "2");
		paintListBox(a06, Mod303Key.CT_A06, table);
		
		// Acogido voluntariamente al SII
		a13 = new ListBox();
		a13.setWidth(WIDTH_150PX);
		a13.addItem("(0) Para el mes de enero (01)", "0");
		a13.addItem(SI_1, "1");
		a13.addItem(NO_2, "2");
		paintListBox(a13, Mod303Key.CT_A13, table);
		
		// Exonerado de la declaracion resumen anual del IVA (modelo 390)
		a14 = new ListBox();
		a14.setWidth(WIDTH_150PX);
		a14.addItem("(0) Para todos los periodos distintos del \u00FAltimo (12 y 4T)", "0");   
		a14.addItem(SI_1, "1");
		a14.addItem(NO_2, "2");
		paintListBox(a14, Mod303Key.CT_A14, table);
		
		// Volumen anual de operaciones distinto de cero
		a11 = new ListBox();
		a11.setWidth("200px");
		a11.addItem("(0) NO exonerado (\u00FAltimo periodo), o la declaraci\u00F3n no es del \u00FAltimo periodo", "0");
		a11.addItem("(1) Exonerados, cuando se tiene volumen de operaciones  (art. 121 LIVA)", "1");
		a11.addItem("(2) Exonerados, cuando NO se tiene volumen de operaciones  (art. 121 LIVA)", "2");
		paintListBox(a11, Mod303Key.CT_A11, table);
		a11.setEnabled(getModel().isLastPeriod());
		if (getModel().isLastPeriod()) {
			a11.addChangeHandler( event -> {
				if (a11.getSelectedIndex() != 0 && (
					AonStringUtils.isNotBlank(getModel().getDescription(Mod303Key.CT_U1D))
				 || AonStringUtils.isNotBlank(getModel().getDescription(Mod303Key.CT_U2D))								
				 || AonStringUtils.isNotBlank(getModel().getDescription(Mod303Key.CT_U3D))								
				 || AonStringUtils.isNotBlank(getModel().getDescription(Mod303Key.CT_U4D))
				 || AonMathUtils.isNotZero( getModel().getAmount(Mod303Key.CT_C88))
				 || AonStringUtils.isNotBlank(getModel().getDescription(Mod303Key.CT_P1C))
				 || AonStringUtils.isNotBlank(getModel().getDescription(Mod303Key.CT_P2C))
				 || AonStringUtils.isNotBlank(getModel().getDescription(Mod303Key.CT_P3C))
				 || AonStringUtils.isNotBlank(getModel().getDescription(Mod303Key.CT_P4C))
				 || AonStringUtils.isNotBlank(getModel().getDescription(Mod303Key.CT_P5C)))) {

					Mod303Key[] keys = new Mod303Key[]{
							 Mod303Key.CT_U1D,Mod303Key.CT_U1C,Mod303Key.CT_U1E
							,Mod303Key.CT_U2D,Mod303Key.CT_U2C,Mod303Key.CT_U2E
							,Mod303Key.CT_U3D,Mod303Key.CT_U3C,Mod303Key.CT_U3E
							,Mod303Key.CT_U4D,Mod303Key.CT_U4C,Mod303Key.CT_U4E
							,Mod303Key.CT_U5D,Mod303Key.CT_U5C,Mod303Key.CT_U5E
							
							,Mod303Key.CT_U13,Mod303Key.CT_C89,Mod303Key.CT_C90,Mod303Key.CT_C91,Mod303Key.CT_C92
							,Mod303Key.CT_C107
							,Mod303Key.CT_C80,Mod303Key.CT_C81,Mod303Key.CT_C93,Mod303Key.CT_C94
							,Mod303Key.CT_C83,Mod303Key.CT_C84,Mod303Key.CT_C85,Mod303Key.CT_C86,Mod303Key.CT_C95
							,Mod303Key.CT_C96,Mod303Key.CT_C97,Mod303Key.CT_C98,Mod303Key.CT_C79,Mod303Key.CT_C99 
							,Mod303Key.CT_C88
							
							,Mod303Key.CT_P1C,Mod303Key.CT_P1I,Mod303Key.CT_P1D,Mod303Key.CT_P1T,Mod303Key.CT_P1P
							,Mod303Key.CT_P2C,Mod303Key.CT_P2I,Mod303Key.CT_P2D,Mod303Key.CT_P2T,Mod303Key.CT_P2P
							,Mod303Key.CT_P3C,Mod303Key.CT_P3I,Mod303Key.CT_P3D,Mod303Key.CT_P3T,Mod303Key.CT_P3P
							,Mod303Key.CT_P4C,Mod303Key.CT_P4I,Mod303Key.CT_P4D,Mod303Key.CT_P4T,Mod303Key.CT_P4P
							,Mod303Key.CT_P5C,Mod303Key.CT_P5I,Mod303Key.CT_P5D,Mod303Key.CT_P5T,Mod303Key.CT_P5P
					};
					for (Mod303Key key : keys) {
						getModel().ensureDetail(key).clear();
					}
					lastPeriodPanel.clear();
					fillLastPeriodInformationScrollPanel();
					AonMessageDialog.warning("Se han inicializado los datos de la solapa \"Inf. Exonerados 390\"");
				}
				
				if (a11.getSelectedIndex() != 0) {
					AonMessageDialog.warning("Debe rellenar los datos de la solapa \"Inf. Exonerados 390\"");
				}
			});
		}
		
		
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
	
	private void paintA02(Mod303Key key, FlexTable table) {
		a02 = new ListBox();
		a02.addItem("S\u00F3lo Reg. Simplificado");
		a02.addItem("Reg. General y Reg. Simpl.");
		a02.addItem("S\u00F3lo Reg. General");
		paintListBox(a02, key, table);
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
	
	private Widget getSimplifiedRegimePanel() {
		FlowPanel tableContainer = new FlowPanel();
		tableContainer.add( addGroupPanel(AON.MSG.farmerActivity(), getActivityFarmerTable()) );
		tableContainer.add( addGroupPanel(AON.MSG.simplifieedActivities(), getActivityTable()) );
		
		tableContainer.add( getSimplifiedTable()); 
		
		return tableContainer;
	}

	private Model303AEATActivityFarmerTable getActivityFarmerTable() {
		farmerTable.paint(getModel().getActivityFarmerList());
		farmerTable.addSelectionHandler(event -> {
			final Mod303ActivityFarmer original = Mod303ActivityFarmer.clone(event.getSelectedItem()); 
			int idx = 0;
			for (int i = 0; i < getModel().getActivityList().size() ; i++ ) {
				if (getModel().getActivityFarmerList().get(i) == event.getSelectedItem()) {
					idx = i;
				}
			}
			final int currentIndex = idx;
			
			final AonCustomDialog dialog = new AonCustomDialog();
			IMod303ActivityFarmerCallback activityCallback = new IMod303ActivityFarmerCallback() {
				
				@Override
				public void onCancel() {
					dialog.hide();
					getModel().getActivityFarmerList().set(currentIndex, original);
					calculateAndRefresh();
					farmerTable.paint(getModel().getActivityFarmerList());
				}
				
				@Override
				public void onAccept(Mod303ActivityFarmer act) {
					getModel().getActivityFarmerList().set(currentIndex, act);
					dialog.hide();
					farmerTable.paint(getModel().getActivityFarmerList());
				}
				
				@Override
				public void onRemove() {
					dialog.hide();
					for (int i = 0; i < getModel().getActivityList().size() ; i++ ) {
						if (getModel().getActivityFarmerList().get(i) == event.getSelectedItem()) {
							getModel().getActivityFarmerList().get(i).initialize();
						}
					}
					calculateAndRefresh();
					farmerTable.paint(getModel().getActivityFarmerList());
				}

				@Override
				public Mod303ActivityFarmer getActivity() {
					return event.getSelectedItem();
				}
			};
			Model303AEATActivityFarmer actPanel = new Model303AEATActivityFarmer(activityCallback, getModel().isLastPeriod());
			actPanel.addValueChangeHandler( event1 -> 
				calculateAndRefresh( new AsyncCallback<Mod303>() {

					@Override public void onFailure(Throwable caught) {
						// Nothing
					}

					@Override
					public void onSuccess(Mod303 result) {
						actPanel.populateActivity(result.getActivityFarmerList().get(currentIndex));
					}
				})
			);
			dialog.setCaption(AON.MSG.farmerActivity());
			dialog.setGlassEnabled(true);
			dialog.setAnimationEnabled(true);
			dialog.add(actPanel);
			dialog.setWidth("700px");
			dialog.setHeight("280px");
			dialog.show();
			dialog.center();
		});
		farmerTable.paint(getModel().getActivityFarmerList());
		return farmerTable;
	}
	
	private Model303AEATActivityTable getActivityTable() {
		activityTable.paint(getModel().getActivityList());
		activityTable.addSelectionHandler( event -> {
			final Mod303Activity original = Mod303Activity.clone(event.getSelectedItem()); 
			int idx = 0;
			for (int i = 0; i < getModel().getActivityList().size() ; i++ ) {
				if (getModel().getActivityList().get(i) == event.getSelectedItem()) {
					idx = i;
				}
			}
			final int currentIndex = idx;
			final AonCustomDialog dialog = new AonCustomDialog();
			IMod303ActivityCallback activityCallback = new IMod303ActivityCallback() {
				
				@Override
				public Mod303 getMod303() {
					return getModel();
				}

				@Override
				public void onCancel() {
					dialog.hide();
					getModel().getActivityList().set(currentIndex, original);
					calculateAndRefresh();
					activityTable.paint(getModel().getActivityList());
				}
				
				@Override
				public void onAccept(Mod303Activity act) {
					dialog.hide();
					getModel().getActivityList().set(currentIndex, act);
					calculateAndRefresh();
					activityTable.paint(getModel().getActivityList());
				}
				
				@Override
				public void onRemove() {
					dialog.hide();
					for (int i = 0; i < getModel().getActivityList().size() ; i++ ) {
						if (getModel().getActivityList().get(i) == event.getSelectedItem()) {
							getModel().getActivityList().get(i).initialize();
						}
					}
					calculateAndRefresh();
					activityTable.paint(getModel().getActivityList());
				}

				@Override
				public Mod303Activity getActivity() {
					return event.getSelectedItem();
				}
			};
			Model303AEATActivity2020 actPanel = new Model303AEATActivity2020(activityCallback, getModel().isLastPeriod());
			actPanel.addValueChangeHandler( event1 -> {
				getModel().getActivityList().set(currentIndex, event1.getValue());
				calculateAndRefresh( new AsyncCallback<Mod303>() {

					@Override public void onFailure(Throwable caught) {
						// Nothing
					}

					@Override
					public void onSuccess(Mod303 result) {
						actPanel.populateActivity(result.getActivityList().get(currentIndex));
					}
				});
			});
			dialog.setCaption(AON.MSG.simplifieedActivities());
			dialog.setGlassEnabled(true);
			dialog.setAnimationEnabled(true);
			dialog.showCloseButton(true);
			dialog.add(actPanel);
			dialog.setWidth("700px");
			dialog.setHeight("620px");
			dialog.show();
			dialog.center();
		});
		return activityTable;
	}

	private FlexTable getSimplifiedTable() {
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
		if (getModel().isLastPeriod()) {
			paintDeclaration(table,Model3032022AEATSimplifiedRegime4TScript.values(),3);
		} else {
			paintDeclaration(table,Model3032022AEATSimplifiedRegimeScript.values(),3);
		}
		return table;
	}
	
	@Override
	protected void populate(Mod303 mod303) {
		super.populate(mod303);
		farmerTable.paint(getModel().getActivityFarmerList());
		activityTable.paint(getModel().getActivityList());
	}
	
	@Override
	protected void save() {
		save(new AsyncCallback<Mod303>() {
			@Override public void onFailure(Throwable caught) { 
				// Nothing 
			}
			@Override
			public void onSuccess(Mod303 result) {
				farmerTable.paint(getModel().getActivityFarmerList());
				activityTable.paint(getModel().getActivityList());
			}
		});
	}

	private void paintLastPeriodInformationTab(TabLayoutPanel tabPanel) {
		lastPeriodPanel = new ScrollPanel();
		fillLastPeriodInformationScrollPanel();
		tabPanel.add(lastPeriodPanel, "Inf. Exonerados 390.");
	}


	private void fillLastPeriodInformationScrollPanel() {
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

		paintLabel(table, 0, AON.MSG.activities(),true);

		FlowPanel actContainer = new FlowPanel();
		actContainer.setStyleName(AON.CSS.aonBlockCenter());
		actContainer.addStyleName(AON.CSS.aonWidthAlmostAll());

		FlexTable tab = new FlexTable();
		tab.addStyleName(AON.CSS.aonTable());
		tab.addStyleName(AON.CSS.aonMarginBottom());
		
		tab.getColumnFormatter().setWidth(0, "40px");
		tab.getColumnFormatter().setWidth(1, "80px");
		tab.getColumnFormatter().setWidth(2, "60px");
		tab.getColumnFormatter().setWidth(3, "250px");
		tab.getColumnFormatter().setWidth(4, "auto");
		
		tab.setWidget(1, 0, new Label() );
		tab.setWidget(1, 1, new Label( AON.MSG.epigraph()) );
		tab.getFlexCellFormatter().addStyleName(1,1,AON.CSS.aonBold());
		tab.getFlexCellFormatter().addStyleName(1,1,AON.CSS.aonBorderBottom());
		tab.setWidget(1, 2, new Label( AON.MSG.key()) );
		tab.getFlexCellFormatter().addStyleName(1,2,AON.CSS.aonBold());
		tab.getFlexCellFormatter().addStyleName(1,2,AON.CSS.aonBorderBottom());
		tab.setWidget(1, 3, new Label( AON.MSG.description()) );
		tab.getFlexCellFormatter().addStyleName(1,3,AON.CSS.aonBold());
		tab.getFlexCellFormatter().addStyleName(1,3,AON.CSS.aonBorderBottom());
		tab.setWidget(1, 4, new Label() );
		tab.getFlexCellFormatter().addStyleName(1,4,AON.CSS.aonBold());
		tab.getFlexCellFormatter().addStyleName(1,4,AON.CSS.aonBorderBottom());
		
		
		paintActivityRow(tab,Mod303Key.CT_U1D,Mod303Key.CT_U1C,Mod303Key.CT_U1E);
		paintActivityRow(tab,Mod303Key.CT_U2D,Mod303Key.CT_U2C,Mod303Key.CT_U2E);
		paintActivityRow(tab,Mod303Key.CT_U3D,Mod303Key.CT_U3C,Mod303Key.CT_U3E);
		paintActivityRow(tab,Mod303Key.CT_U4D,Mod303Key.CT_U4C,Mod303Key.CT_U4E);
		paintActivityRow(tab,Mod303Key.CT_U5D,Mod303Key.CT_U5C,Mod303Key.CT_U5E);
		
		actContainer.add(tab);
		table.setWidget(1, 0, actContainer);
		table.getFlexCellFormatter().setColSpan(1, 0, 4);
		
		paintCheck(Mod303Key.CT_U13, table);
		
		paintScript(table,Model3032022AEAT390nfoScript.values(),3);
		
		paintLabel(table, table.getRowCount() , AON.MSG.prorrata(),true);
		
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
		
		paintProrrateRow(tab2,Mod303Key.CT_P1C,Mod303Key.CT_P1I,Mod303Key.CT_P1D,Mod303Key.CT_P1T,Mod303Key.CT_P1P);
		paintProrrateRow(tab2,Mod303Key.CT_P2C,Mod303Key.CT_P2I,Mod303Key.CT_P2D,Mod303Key.CT_P2T,Mod303Key.CT_P2P);
		paintProrrateRow(tab2,Mod303Key.CT_P3C,Mod303Key.CT_P3I,Mod303Key.CT_P3D,Mod303Key.CT_P3T,Mod303Key.CT_P3P);
		paintProrrateRow(tab2,Mod303Key.CT_P4C,Mod303Key.CT_P4I,Mod303Key.CT_P4D,Mod303Key.CT_P4T,Mod303Key.CT_P4P);
		paintProrrateRow(tab2,Mod303Key.CT_P5C,Mod303Key.CT_P5I,Mod303Key.CT_P5D,Mod303Key.CT_P5T,Mod303Key.CT_P5P);

		actContainer2.add(tab2);
		
		int row = table.getRowCount();
		table.setWidget(row, 0, actContainer2);
		table.getFlexCellFormatter().setColSpan(row, 0, 4);
		
		lastPeriodPanel.setWidget(table);
	}


	private void paintProrrateRow(FlexTable tab, Mod303Key cnaeKey, Mod303Key amountKey, Mod303Key amountRightKey, Mod303Key typeKey, Mod303Key percentKey) {
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
		typeBox.addChangeHandler(event ->  {
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


	private void paintActivityRow(FlexTable tab, Mod303Key desKey, Mod303Key keyKey, Mod303Key epiKey) {
		int row = tab.getRowCount();

		tab.setWidget(row, 0, new Label( row == 2 ? "Principal" : "Otras" ) );

		AonTextBox epi = new AonTextBox();
		epi.setVisibleLength(5);
		epi.setMaxLength(4);
		epi.setValue(getModel().getDescription(epiKey));
		epi.addValueChangeHandler(event -> {
			getModel().putDescription(epiKey,epi.getValue());
			markAsDirty();
		});
		tab.setWidget(row, 1, epi);

		ActivityTypeListBox key = new ActivityTypeListBox();
		key.setValue( ActivityType.ensure(getModel().getDescription(keyKey)) );
		key.addChangeHandler(event ->  {
			getModel().putDescription(keyKey,ActivityType.toString(key.getValue()));
			markAsDirty();
		});
		tab.setWidget(row, 2, key);

		AonTextBox description = new AonTextBox();
		description.setVisibleLength(40);
		description.setMaxLength(40);
		description.setValue(getModel().getDescription(desKey));
		description.addValueChangeHandler(event -> {
			getModel().putDescription(desKey,description.getValue());
			markAsDirty();
		});
		tab.setWidget(row, 3, description);
		
		tab.setWidget(row, 4, new Label() );

	}
	
	@Override
	protected void decorateDeclarationTab() {
		super.decorateDeclarationTab();
		enable( withoutActivityCheck );
		enable(a12);
		enable(cm2);
		enable(a03);
		enable(a07);
		enable(a08);
		enable(a09);
		enable(a10);
		enable(a04);
		enable(a05);
		enable(a02);
		enable(a06);
		enable(a13);
		enable(a14);
		enable(a11);
		enable(previousReceiptBox);
		enable(receiptBox);
		if (receiptBox != null) {
			receiptBox.setValue( getModel().getNumber() );
		}
	}
	
}
