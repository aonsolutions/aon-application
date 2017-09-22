package com.esferalia.aon.gwt.fiscal.client.mod303;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.CustomDialog;
import com.esferalia.aon.gwt.common.client.widget.MessageDialog;
import com.esferalia.aon.gwt.fiscal.client.FiscalModelUtils;
import com.esferalia.aon.gwt.fiscal.client.mod303.Model303.Model303Callback;
import com.esferalia.aon.gwt.fiscal.client.mod303.Model303AEATActivity.IMod303ActivityCallback;
import com.esferalia.aon.gwt.fiscal.client.mod303.Model303AEATActivityFarmer.IMod303ActivityFarmerCallback;
import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.api.model.fiscal.Mod303Activity;
import com.esferalia.aon.occam.api.model.fiscal.Mod303ActivityFarmer;
import com.esferalia.aon.occam.api.model.fiscal.mod303.Model3032017AEATAdditionalDataScript;
import com.esferalia.aon.occam.api.model.fiscal.mod303.Model3032017AEATGeneralRegimeScript1;
import com.esferalia.aon.occam.api.model.fiscal.mod303.Model3032017AEATGeneralRegimeScript2;
import com.esferalia.aon.occam.api.model.fiscal.mod303.Model3032017AEATResultScript;
import com.esferalia.aon.occam.api.model.fiscal.mod303.Model3032017AEATSimplifiedRegimeScript;
import com.esferalia.aon.occam.api.model.type.Mod303Key;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.esferalia.aon.watson.util.Pair;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
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
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.RangeChangeEvent;
import com.google.gwt.view.client.RangeChangeEvent.Handler;

public class Model3032017AEAT extends Model303Base {
	private static final String VALIDATE_PRINT_ACTION = "/aon_gwt_fiscal/Model303PrintAEAT";
	
	private static class Mod303ActivityProvidesKey implements ProvidesKey<Mod303Activity> {
		@Override
		public Object getKey(Mod303Activity model) {
			return AonStringUtils.isBlank(model.getEpigraph()) ? null : model.getEpigraph();
		}
	}
	private static class Mod303ActivityFarmerProvidesKey implements ProvidesKey<Mod303ActivityFarmer> {
		@Override
		public Object getKey(Mod303ActivityFarmer model) {
			return AonStringUtils.isBlank(model.getCode()) ? null : model.getCode();
		}
	}
	private final Mod303ActivityFarmerProvidesKey providesFarmerKey = new Mod303ActivityFarmerProvidesKey();
	private final Model303AEATActivityFarmerTable farmerTable = new Model303AEATActivityFarmerTable( providesFarmerKey );
	
	private final Mod303ActivityProvidesKey providesKey = new Mod303ActivityProvidesKey();
	private final Model303AEATActivityTable activityTable = new Model303AEATActivityTable( providesKey );
	
	public Model3032017AEAT(Mod303 mod303,Model303Callback callback) {
		super(mod303,callback);
		TabLayoutPanel tabPanel = new TabLayoutPanel(26, Unit.PX);
		SimpleLayoutPanel centerPanel = new SimpleLayoutPanel();
		centerPanel.addStyleName(AON.AON_CSS.aonScrollArea());
		centerPanel.setWidget(tabPanel);
		add(centerPanel);
		
		paintIdentificationTab(tabPanel);
		paintDeclarationTab(tabPanel);
		paintGeneralRegimenTab(tabPanel);
		paintSimplifiedRegimenTab(tabPanel);
		paintResultTab(tabPanel);
		paintAdditionalDataTab(tabPanel);
		paintAdministrationTab(tabPanel);
	}
	
	
	private void paintIdentificationTab(TabLayoutPanel tabPanel) {
		Model303IdentificationData identificationData = new Model303IdentificationData( new Model303IdentificationDataCallback()) ;
		tabPanel.add(identificationData, TAB_TEMPLATE.render(AON.MSG.identification(), AON.AON_CSS.aonIconIdentification()));
	}
	
	private void paintGeneralRegimenTab(TabLayoutPanel tabPanel) {
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
	
	private void paintSimplifiedRegimenTab(TabLayoutPanel tabPanel) {
		ScrollPanel simplifiedRegimeScrollPanel = new ScrollPanel();
		simplifiedRegimeScrollPanel.setWidget(getSimplifiedRegimePanel());
		tabPanel.add(simplifiedRegimeScrollPanel, TAB_TEMPLATE.render(AON.MSG.simplifiedRegime(), AON.AON_CSS.aonIconModel()));
	}

	private void paintResultTab(TabLayoutPanel tabPanel) {
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

	private void paintAdditionalDataTab(TabLayoutPanel tabPanel) {
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

	private void paintAdministrationTab(TabLayoutPanel tabPanel) {
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
		
		FlowPanel administrationPanel = getAdministrationPanel(); 
		panel.add(administrationPanel);
		FlowPanel informationPanel = getInformationPanel();
		panel.add(informationPanel);
		tabPanel.add(panel,TAB_TEMPLATE.render("Agencia Tributaria", FiscalModelUtils.getAdministrationIconBW(getMod303().getAdministration())));
	}
	
	protected FlowPanel getAdministrationPanel() {
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
					getCallback().showBreakdownPanel("Para generar el fichero debe finalizar la confecci\u00F3n del modelo.");
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
					getCallback().showBreakdownPanel("Para generar el fichero debe finalizar la confecci\u00F3n del modelo.");
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
	
	private void paintDeclarationTab(TabLayoutPanel tabPanel) {
		ScrollPanel declarationScrollPanel = new ScrollPanel();
		FlowPanel container = new FlowPanel();
		
		FlexTable table = createTable();
		paintCheck(Mod303Key.CM_002,table);	// ¿Está inscrito en el Registro de devolució3n mensual (Art. 30 RIVA)?
		
		paintA02(Mod303Key.CT_A02,table,tabPanel);	// ¿Tributa exclusivamente en régimen simplificado?
		
		paintCheck(Mod303Key.CT_A03,table);	// ¿Es autoliquidación conjunta?
		
		paintCheck(Mod303Key.CT_A07,table);	// ¿Ha optado por el régimen especial del criterio de Caja (art. 163 undecies LIVA)?
		paintCheck(Mod303Key.CT_A08,table);	// ¿Es destinatario de operaciones a las que se aplique el régimen especial del criterio de caja?
		paintCheck(Mod303Key.CT_A11,table);	// Exonerados de la declaraci\u00F3n-resumen anual del IVA, modelo 390: ¿Existe volumen de operaciones (art. 121 LIVA)?
		
		paintCheck(Mod303Key.CT_A04,table);	// Ha sido declarado en concurso de acreedores en el presente período de liquidación?
		paintDate (Mod303Key.CT_A05,table);	// Fecha en que se dictó el auto de declaración de concurso
		paintCheck(Mod303Key.CT_A06,table);	// Auto de declaración de concurso dictado en el períDodo

		if (getCallback().getMod303().isComplementary()) {
			int row = table.getRowCount();
			paintLabel(table, row, AON.MSG.previousReceipt());
			
			table.getFlexCellFormatter().addStyleName(row, 0,AON.AON_CSS.aonPaddingLeft() );
			table.getFlexCellFormatter().addStyleName(row, 0,AON.AON_CSS.aonBorderBottomImportant() );
			final TextBox receiptBox = new TextBox();
			receiptBox.setVisibleLength(15);
			receiptBox.setMaxLength(13);
			receiptBox.setStyleName(AON.AON_CSS.aonInputText());
//			receiptBox.setEnabled(mod303.isNotFinished());
			receiptBox.setValue( getCallback().getMod303().getReplacedNumber() );
			receiptBox.addValueChangeHandler( new ValueChangeHandler<String>() {
				@Override
				public void onValueChange(ValueChangeEvent<String> event) {
					getCallback().getMod303().setReplacedNumber(receiptBox.getValue());
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
	
	private void paintA02(Mod303Key key, FlexTable table,final TabLayoutPanel tabPanel) {
		final ListBox a02 = new ListBox();
		a02.addItem("S\u00F3lo Reg. Simplificado");
		a02.addItem("Reg. General y Reg. Simpl.");
		a02.addItem("S\u00F3lo Reg. General");
		paintListBox(a02, key, table);
		tabPanel.addBeforeSelectionHandler(new BeforeSelectionHandler<Integer>() {
			  @Override
			  public void onBeforeSelection(BeforeSelectionEvent<Integer> event) {
			    if (event.getItem() == 2 && a02.getSelectedIndex() == 0) {
			    	event.cancel();
			    	MessageDialog.warning("No procede para este tipo de declaraci\u00F3n");
			    }
			    if (event.getItem() == 3 && a02.getSelectedIndex() == 2) {
			    	event.cancel();
			    	MessageDialog.warning("No procede para este tipo de declaraci\u00F3n");
			    }
			  }
			});		
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
	
	private Widget getSimplifiedRegimePanel() {
		FlowPanel tableContainer = new FlowPanel();
		tableContainer.add( addGroupPanel(AON.MSG.farmerActivity(), getActivityFarmerTable()) );
		tableContainer.add( addGroupPanel(AON.MSG.simplifieedActivities(), getActivityTable()) );
		
		tableContainer.add( getSimplifiedTable()); 
		
		return tableContainer;
	}

	private Model303AEATActivityFarmerTable getActivityFarmerTable() {
		farmerTable.addRangeChangeHandler(new Handler() {
			
			@Override
			public void onRangeChange(RangeChangeEvent event) {
				farmerTable.setRowData(getCallback().getMod303().getActivityFarmerList());
			}
		});
		
		farmerTable.addSelectionHandler(new SelectionHandler<Mod303ActivityFarmer>() {
			
			@Override
			public void onSelection(SelectionEvent<Mod303ActivityFarmer> event) {
				final Mod303ActivityFarmer original = Mod303ActivityFarmer.clone(event.getSelectedItem()); 
				int idx = 0;
				for (int i = 0; i < getCallback().getMod303().getActivityList().size() ; i++ ) {
					if (getCallback().getMod303().getActivityFarmerList().get(i) == event.getSelectedItem()) {
						idx = i;
					}
				}
				final int currentIndex = idx;
				
				final CustomDialog dialog = new CustomDialog();
				IMod303ActivityFarmerCallback activityCallback = new IMod303ActivityFarmerCallback() {
					
					@Override
					public void onCancel() {
						dialog.hide();
						getCallback().getMod303().getActivityFarmerList().set(currentIndex, original);
						calculateAndRefresh();
						farmerTable.setRowData(getCallback().getMod303().getActivityFarmerList());
						farmerTable.redraw();
					}
					
					@Override
					public void onAccept(Mod303ActivityFarmer act) {
						getCallback().getMod303().getActivityFarmerList().set(currentIndex, act);
						dialog.hide();
						farmerTable.setRowData(getCallback().getMod303().getActivityFarmerList());
						farmerTable.redraw();
					}
					
					@Override
					public void onRemove() {
						dialog.hide();
						for (int i = 0; i < getCallback().getMod303().getActivityList().size() ; i++ ) {
							if (getCallback().getMod303().getActivityFarmerList().get(i) == event.getSelectedItem()) {
								getCallback().getMod303().getActivityFarmerList().get(i).initialize();
							}
						}
						calculateAndRefresh();
						farmerTable.redraw();
					}

					@Override
					public Mod303ActivityFarmer getActivity() {
						return event.getSelectedItem();
					}
				};
				Model303AEATActivityFarmer actPanel = new Model303AEATActivityFarmer(activityCallback);
				actPanel.addValueChangeHandler(new ValueChangeHandler<Mod303ActivityFarmer>() {
					
					@Override
					public void onValueChange(ValueChangeEvent<Mod303ActivityFarmer> event) {
						calculateAndRefresh( new AsyncCallback<Mod303>() {

							@Override public void onFailure(Throwable caught) {}

							@Override
							public void onSuccess(Mod303 result) {
								
								actPanel.populateActivity(result.getActivityFarmerList().get(currentIndex));
							}
						});
						
					}
				});
				dialog.setCaption(AON.MSG.farmerActivity());
				dialog.setGlassEnabled(true);
				dialog.setAnimationEnabled(true);
				dialog.add(actPanel);
				dialog.setWidth("700px");
				dialog.setHeight("280px");
				dialog.show();
				dialog.center();
			}
		});
		farmerTable.setVisibleRangeAndClearData(farmerTable.getVisibleRange(), true);
		return farmerTable;
	}
	
	private Model303AEATActivityTable getActivityTable() {
		activityTable.addRangeChangeHandler(new Handler() {
			
			@Override
			public void onRangeChange(RangeChangeEvent event) {
				activityTable.setRowData(getCallback().getMod303().getActivityList());
			}
		});
		
		activityTable.addSelectionHandler(new SelectionHandler<Mod303Activity>() {
			
			@Override
			public void onSelection(SelectionEvent<Mod303Activity> event) {
				final Mod303Activity original = Mod303Activity.clone(event.getSelectedItem()); 
				int idx = 0;
				for (int i = 0; i < getCallback().getMod303().getActivityList().size() ; i++ ) {
					if (getCallback().getMod303().getActivityList().get(i) == event.getSelectedItem()) {
						idx = i;
					}
				}
				final int currentIndex = idx;
				final CustomDialog dialog = new CustomDialog();
				IMod303ActivityCallback activityCallback = new IMod303ActivityCallback() {
					
					@Override
					public void onCancel() {
						dialog.hide();
						getCallback().getMod303().getActivityList().set(currentIndex, original);
						calculateAndRefresh();
						activityTable.setRowData(getCallback().getMod303().getActivityList());
						activityTable.redraw();
					}
					
					@Override
					public void onAccept(Mod303Activity act) {
						dialog.hide();
						getCallback().getMod303().getActivityList().set(currentIndex, act);
						calculateAndRefresh();
						activityTable.setRowData(getCallback().getMod303().getActivityList());
						activityTable.redraw();
					}
					
					@Override
					public void onRemove() {
						dialog.hide();
						for (int i = 0; i < getCallback().getMod303().getActivityList().size() ; i++ ) {
							if (getCallback().getMod303().getActivityList().get(i) == event.getSelectedItem()) {
								getCallback().getMod303().getActivityList().get(i).initialize();
							}
						}
						calculateAndRefresh();
						activityTable.redraw();
					}

					@Override
					public Mod303Activity getActivity() {
						return event.getSelectedItem();
					}
				};
				Model303AEATActivity actPanel = new Model303AEATActivity(activityCallback);
				actPanel.addValueChangeHandler(new ValueChangeHandler<Mod303Activity>() {
					
					@Override
					public void onValueChange(ValueChangeEvent<Mod303Activity> event) {
						calculateAndRefresh( new AsyncCallback<Mod303>() {

							@Override public void onFailure(Throwable caught) {}

							@Override
							public void onSuccess(Mod303 result) {
								actPanel.populateActivity(result.getActivityList().get(currentIndex));
							}
						});
						
					}
				});
				dialog.setCaption(AON.MSG.simplifieedActivities());
				dialog.setGlassEnabled(true);
				dialog.setAnimationEnabled(true);
				dialog.add(actPanel);
				dialog.setWidth("700px");
				dialog.setHeight("580px");
				dialog.show();
				dialog.center();
			}
		});
		activityTable.setVisibleRangeAndClearData(activityTable.getVisibleRange(), true);
		return activityTable;
	}

	private FlexTable getSimplifiedTable() {
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
		paintDeclaration(table,Model3032017AEATSimplifiedRegimeScript.values(),3);
		return table;
	}
	
	@Override
	protected void populate(Mod303 mod303) {
		super.populate(mod303);
	}
	
	protected void save() {
		save(new AsyncCallback<Mod303>() {
			@Override public void onFailure(Throwable caught) {}
			@Override
			public void onSuccess(Mod303 result) {
				farmerTable.setRowData(getCallback().getMod303().getActivityFarmerList());
				activityTable.setRowData(getCallback().getMod303().getActivityList());
			}
		});
	}

}
