package com.esferalia.aon.gwt.fiscal.client.mod303;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCnae2009Panel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDoubleBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessageDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTextBox;
import com.esferalia.aon.gwt.fiscal.client.mod303.Model303.Model303Callback;
import com.esferalia.aon.gwt.fiscal.client.mod303.Model303AEATActivity2016.IMod303ActivityCallback;
import com.esferalia.aon.gwt.fiscal.client.mod303.Model303AEATActivityFarmer.IMod303ActivityFarmerCallback;
import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.api.model.fiscal.Mod303Activity;
import com.esferalia.aon.occam.api.model.fiscal.Mod303ActivityFarmer;
import com.esferalia.aon.occam.api.model.fiscal.mod303.Model3032017AEAT390nfoScript;
import com.esferalia.aon.occam.api.model.fiscal.mod303.Model3032017AEATAdditionalDataScript;
import com.esferalia.aon.occam.api.model.fiscal.mod303.Model3032017AEATGeneralRegimeScript1;
import com.esferalia.aon.occam.api.model.fiscal.mod303.Model3032017AEATGeneralRegimeScript2;
import com.esferalia.aon.occam.api.model.fiscal.mod303.Model3032017AEATResultScript;
import com.esferalia.aon.occam.api.model.fiscal.mod303.Model3032017AEATSimplifiedRegime4TScript;
import com.esferalia.aon.occam.api.model.fiscal.mod303.Model3032017AEATSimplifiedRegimeScript;
import com.esferalia.aon.occam.api.model.type.Mod303Key;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

class Model303AEAT2017 extends Model303AEAT {

	private final Model303AEATActivityFarmerTable farmerTable;
	private final Model303AEATActivityTable activityTable;
	private ScrollPanel lastPeriodInformationScrollPanel; 
	
	private static final int GENERAL_REGIME_TAB = 2;
	private static final int SIMPLIFIED_REGIME_TAB = 3;
	private static final int RESULT_TAB = 4;
	private static final int LAST_PERIOD_INFORMATION_TAB = 6;
	
	protected Model303AEAT2017(Mod303 mod303,Model303Callback callback) {
		super(mod303,callback);
		TabLayoutPanel tabPanel = new TabLayoutPanel(26, Unit.PX);
		SimpleLayoutPanel centerPanel = new SimpleLayoutPanel();
		centerPanel.addStyleName(AON.CSS.aonScrollArea());
		centerPanel.setWidget(tabPanel);
		add(centerPanel);
		
		farmerTable = new Model303AEATActivityFarmerTable(mod303.isLastPeriod());
		activityTable = new Model303AEATActivityTable( mod303.isLastPeriod() );
		
		paintIdentificationTab(tabPanel);
		paintDeclarationTab(tabPanel);
		paintGeneralRegimeTab(tabPanel);
		paintSimplifiedRegimeTab(tabPanel);
		paintResultTab(tabPanel);
		paintAdditionalDataTab(tabPanel);
		if (getModel().isLastPeriod()) {
			paintLastPeriodInformationTab(tabPanel);
		}
		paintAdministrationTab(tabPanel);
		
		if (mod303.isFinished() || mod303.isSent()) {
			tabPanel.selectTab(RESULT_TAB);
		} else {
			if (mod303.getAmount(Mod303Key.CT_A02) == 0) {
				tabPanel.selectTab(SIMPLIFIED_REGIME_TAB);	
			} else {
				tabPanel.selectTab(GENERAL_REGIME_TAB);
			}
		}
		tabPanel.addBeforeSelectionHandler(this::beforeSelectTab);
	}
	
	private void beforeSelectTab(BeforeSelectionEvent<Integer> event) {
		double a02 = getModel().getAmount(Mod303Key.CT_A02);
		if (event.getItem() == GENERAL_REGIME_TAB && a02 == 0) {
			event.cancel();
			AonMessageDialog.warning("No procede para este tipo de declaraci\u00F3n");
		}
		if (event.getItem() == SIMPLIFIED_REGIME_TAB && a02 == 2) {
			event.cancel();
			AonMessageDialog.warning("No procede para este tipo de declaraci\u00F3n");
		}
		if (getModel().isLastPeriod()) {
			double a11 = getModel().getAmount(Mod303Key.CT_A11);
			if (event.getItem() == LAST_PERIOD_INFORMATION_TAB && a11 == 0) {
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
		paintDeclaration(table,Model3032017AEATGeneralRegimeScript1.values(),8);
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
		paintDeclaration(table,Model3032017AEATGeneralRegimeScript2.values(),6);
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
		paintDeclaration(table,Model3032017AEATResultScript.values(),3);
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
		paintDeclaration(table,Model3032017AEATAdditionalDataScript.values(),3);
	}

	private void paintDeclarationTab(TabLayoutPanel tabPanel) {
		ScrollPanel declarationScrollPanel = new ScrollPanel();
		FlowPanel container = new FlowPanel();
		
		FlexTable table = createTable();
		paintWithoutActivityCheck(table);	// Sin actividad
		
		paintCheck(Mod303Key.CM_002,table);	// ¿Está inscrito en el Registro de devolució3n mensual (Art. 30 RIVA)?
		
		paintA02(Mod303Key.CT_A02,table);	// ¿Tributa exclusivamente en régimen simplificado?
		
		paintCheck(Mod303Key.CT_A03,table);	// ¿Es autoliquidación conjunta?
		
		paintCheck(Mod303Key.CT_A07,table);	// ¿Ha optado por el régimen especial del criterio de Caja (art. 163 undecies LIVA)?
		paintCheck(Mod303Key.CT_A08,table);	// ¿Es destinatario de operaciones a las que se aplique el régimen especial del criterio de caja?
		
		
		final ListBox a11 = new ListBox();
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
							,Mod303Key.CT_C80,Mod303Key.CT_C81,Mod303Key.CT_C82,Mod303Key.CT_C93,Mod303Key.CT_C94
							,Mod303Key.CT_C83,Mod303Key.CT_C84,Mod303Key.CT_C85,Mod303Key.CT_C86,Mod303Key.CT_C95
							,Mod303Key.CT_C96,Mod303Key.CT_C97,Mod303Key.CT_C98,Mod303Key.CT_C79,Mod303Key.CT_C99 
							,Mod303Key.CT_C87,Mod303Key.CT_C88
							
							,Mod303Key.CT_P1C,Mod303Key.CT_P1I,Mod303Key.CT_P1D,Mod303Key.CT_P1T,Mod303Key.CT_P1P
							,Mod303Key.CT_P2C,Mod303Key.CT_P2I,Mod303Key.CT_P2D,Mod303Key.CT_P2T,Mod303Key.CT_P2P
							,Mod303Key.CT_P3C,Mod303Key.CT_P3I,Mod303Key.CT_P3D,Mod303Key.CT_P3T,Mod303Key.CT_P3P
							,Mod303Key.CT_P4C,Mod303Key.CT_P4I,Mod303Key.CT_P4D,Mod303Key.CT_P4T,Mod303Key.CT_P4P
							,Mod303Key.CT_P5C,Mod303Key.CT_P5I,Mod303Key.CT_P5D,Mod303Key.CT_P5T,Mod303Key.CT_P5P
					};
					for (Mod303Key key : keys) {
						getModel().ensureDetail(key).clear();
					}
					lastPeriodInformationScrollPanel.clear();
					fillLastPeriodInformationScrollPanel();
					AonMessageDialog.warning("Se han inicializado los datos de la solapa \"Inf. Exonerados 390\"");
				}
				if (a11.getSelectedIndex() != 0) {
					AonMessageDialog.warning("Debe rellenar los datos de la solapa \"Inf. Exonerados 390\"");
				}
			});
		}
		
		paintCheck(Mod303Key.CT_A04,table);	// Ha sido declarado en concurso de acreedores en el presente período de liquidación?
		paintDate (Mod303Key.CT_A05,table);	// Fecha en que se dictó el auto de declaración de concurso
		paintCheck(Mod303Key.CT_A06,table);	// Auto de declaración de concurso dictado en el períDodo

		if (getModel().isComplementary()) {
			int row = table.getRowCount();
			paintLabel(table, row, AON.MSG.previousReceipt());
			
			table.getFlexCellFormatter().addStyleName(row, 0,AON.CSS.aonPaddingLeft() );
			table.getFlexCellFormatter().addStyleName(row, 0,AON.CSS.aonBorderBottom() );
			final AonTextBox receiptBox = new AonTextBox();
			receiptBox.setVisibleLength(15);
			receiptBox.setMaxLength(13);
			receiptBox.setValue( getModel().getReplacedNumber() );
			receiptBox.addValueChangeHandler( event -> {
				getModel().setReplacedNumber(receiptBox.getValue());
				markAsDirty();
			});
			table.setWidget(row, 1, receiptBox);
		}
		
		paintEmptyRow(table);
		paintCheck(Mod303Key.CT_A09,table);	// Opción por la aplicación de la prorrata especial
		paintCheck(Mod303Key.CT_A10,table);	// Revocación de la opción por la aplicación de la prorrata especial
		
		container.add(addGroupPanel("", table));
		
		declarationScrollPanel.setWidget(container);
		tabPanel.add(declarationScrollPanel, AON.MSG.declaration());
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
			actPanel.addValueChangeHandler(event1 -> 
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
		activityTable.addSelectionHandler(event -> {
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
			Model303AEATActivity2016 actPanel = new Model303AEATActivity2016(activityCallback, getModel().isLastPeriod());
			actPanel.addValueChangeHandler(event1 -> calculateAndRefresh( new AsyncCallback<Mod303>() {
					@Override 
					public void onFailure(Throwable caught) {
						// Nothing
					}

					@Override
					public void onSuccess(Mod303 result) {
						actPanel.populateActivity(result.getActivityList().get(currentIndex));
					}
				})
			);
			dialog.setCaption(AON.MSG.simplifieedActivities());
			dialog.setGlassEnabled(true);
			dialog.setAnimationEnabled(true);
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
			paintDeclaration(table,Model3032017AEATSimplifiedRegime4TScript.values(),3);
		} else {
			paintDeclaration(table,Model3032017AEATSimplifiedRegimeScript.values(),3);
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
		lastPeriodInformationScrollPanel = new ScrollPanel();
		fillLastPeriodInformationScrollPanel();
		tabPanel.add(lastPeriodInformationScrollPanel, "Inf. Exonerados 390.");
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
		
		paintScript(table,Model3032017AEAT390nfoScript.values(),3);
		
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
		
		lastPeriodInformationScrollPanel.setWidget(table);
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

		AonTextBox key = new AonTextBox();
		key.setVisibleLength(2);
		key.setMaxLength(1);
		key.setValue(getModel().getDescription(keyKey));
		key.addValueChangeHandler(event -> {
			getModel().putDescription(keyKey,key.getValue());
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
	
}
