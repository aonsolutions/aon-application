package com.esferalia.aon.gwt.fiscal.client.mod303;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.Cnae2009Panel;
import com.esferalia.aon.gwt.common.client.widget.Cnae2009Panel.SelectionCallBack;
import com.esferalia.aon.gwt.common.client.widget.CustomDialog;
import com.esferalia.aon.gwt.common.client.widget.DoubleBox;
import com.esferalia.aon.gwt.common.client.widget.MessageDialog;
import com.esferalia.aon.gwt.common.shared.AonData;
import com.esferalia.aon.gwt.fiscal.client.CertificationPopup;
import com.esferalia.aon.gwt.fiscal.client.FiscalModelUtils;
import com.esferalia.aon.gwt.fiscal.client.mod303.Model303.Model303Callback;
import com.esferalia.aon.gwt.fiscal.client.mod303.Model303AEATActivity2018.IMod303ActivityCallback;
import com.esferalia.aon.gwt.fiscal.client.mod303.Model303AEATActivityFarmer.IMod303ActivityFarmerCallback;
import com.esferalia.aon.gwt.fiscal.shared.mod303.Model3032017AEAT390nfoScript;
import com.esferalia.aon.gwt.fiscal.shared.mod303.Model3032017AEATAdditionalDataScript;
import com.esferalia.aon.gwt.fiscal.shared.mod303.Model3032017AEATGeneralRegimeScript1;
import com.esferalia.aon.gwt.fiscal.shared.mod303.Model3032017AEATGeneralRegimeScript2;
import com.esferalia.aon.gwt.fiscal.shared.mod303.Model3032017AEATResultScript;
import com.esferalia.aon.gwt.fiscal.shared.mod303.Model3032017AEATSimplifiedRegime4TScript;
import com.esferalia.aon.gwt.fiscal.shared.mod303.Model3032017AEATSimplifiedRegimeScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.api.model.fiscal.Mod303Activity;
import com.esferalia.aon.occam.api.model.fiscal.Mod303ActivityFarmer;
import com.esferalia.aon.occam.api.model.type.CNAE2009;
import com.esferalia.aon.occam.api.model.type.Mod303Key;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.esferalia.aon.watson.util.Pair;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
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

public class Model3032018AEAT extends Model303Base {
	private static final String VALIDATE_PRINT_ACTION = "/aon_gwt_fiscal/ms/Model303PrintAEAT";
	
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
	private final Model303AEATActivityFarmerTable farmerTable;
	
	private final Mod303ActivityProvidesKey providesKey = new Mod303ActivityProvidesKey();
	private final Model303AEATActivityTable activityTable;
	private ScrollPanel LastPeriodInformationScrollPanel; 
	
	private final static int GENERAL_REGIME_TAB = 2;
	private final static int SIMPLIFIED_REGIME_TAB = 3;
	private final static int RESULT_TAB = 4;
	private final static int LAST_PERIOD_INFORMATION_TAB = 6;

	final Mod303ServiceAsync MOD303Service = GWT.create(Mod303Service.class);

	
	public Model3032018AEAT(Mod303 mod303,Model303Callback callback, AonData aonData) {
		super(mod303,callback, aonData);
		TabLayoutPanel tabPanel = new TabLayoutPanel(26, Unit.PX);
		SimpleLayoutPanel centerPanel = new SimpleLayoutPanel();
		centerPanel.addStyleName(AON.AON_CSS.aonScrollArea());
		centerPanel.setWidget(tabPanel);
		add(centerPanel);
		
		farmerTable = new Model303AEATActivityFarmerTable( providesFarmerKey, mod303.isLastPeriod());
		activityTable = new Model303AEATActivityTable( providesKey, mod303.isLastPeriod());
		
		paintIdentificationTab(tabPanel);
		paintDeclarationTab(tabPanel);
		paintGeneralRegimeTab(tabPanel);
		paintSimplifiedRegimeTab(tabPanel);
		paintResultTab(tabPanel);
		paintAdditionalDataTab(tabPanel);
		if (getCallback().getMod303().isLastPeriod()) {
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
		
		tabPanel.addBeforeSelectionHandler(new BeforeSelectionHandler<Integer>() {
			@Override
			public void onBeforeSelection(BeforeSelectionEvent<Integer> event) {
				double a02 = getCallback().getMod303().getAmount(Mod303Key.CT_A02);
				if (event.getItem() == GENERAL_REGIME_TAB && a02 == 0) {
					event.cancel();
					MessageDialog.warning("No procede para este tipo de declaraci\u00F3n");
				}
				if (event.getItem() == SIMPLIFIED_REGIME_TAB && a02 == 2) {
					event.cancel();
					MessageDialog.warning("No procede para este tipo de declaraci\u00F3n");
				}
				if (getCallback().getMod303().isLastPeriod()) {
					double a11 = getCallback().getMod303().getAmount(Mod303Key.CT_A11);
					if (event.getItem() == LAST_PERIOD_INFORMATION_TAB && a11 == 0) {
						event.cancel();
						MessageDialog.warning("Para rellenar estos datos, debe rellenar la casilla \""+Mod303Key.CT_A11.getDescription()+ "\" en la solapa \"Declaraci\u00F3n\"");
					}
				}
				
			}
		});
	}
	
	
	private void paintIdentificationTab(TabLayoutPanel tabPanel) {
		Model303IdentificationData identificationData = new Model303IdentificationData( new Model303IdentificationDataCallback()) ;
		tabPanel.add(identificationData, TAB_TEMPLATE.render(AON.MSG.identification(), AON.AON_CSS.aonIconIdentification()));
	}
	
	private void paintGeneralRegimeTab(TabLayoutPanel tabPanel) {
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
	
	private void paintSimplifiedRegimeTab(TabLayoutPanel tabPanel) {
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
		aeatForm.setMethod(FormPanel.METHOD_POST);
		diskForm.setMethod(FormPanel.METHOD_POST);
		FlowPanel formFlowPanel = new FlowPanel();
		FlowPanel aeatFormFlowPanel = new FlowPanel();
		diskForm.add(formFlowPanel);
		aeatForm.add(aeatFormFlowPanel);
				
		formFlowPanel.add(mod303Hidden);
		formFlowPanel.add(domainIdHidden);
		formFlowPanel.add(domainNameHidden);
		formFlowPanel.add(userHidden);
				
		aeatFormFlowPanel.add(modAeatHidden);
		aeatFormFlowPanel.add(domainIdAeatHidden);
		aeatFormFlowPanel.add(domainNameAeatHidden);
		aeatFormFlowPanel.add(userAeatHidden);
		aeatFormFlowPanel.add(nameAeatHidden);
		aeatFormFlowPanel.add(documentAeatHidden);
		aeatFormFlowPanel.add(certAeatHidden);
		aeatFormFlowPanel.add(passAeatHidden);
			
		formContainer.add(diskForm);
		formContainer.add(aeatForm);

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
		tab.getColumnFormatter().setWidth(1, "auto");
		tab.getColumnFormatter().setWidth(2, "30px");
		tab.setStyleName(AON.AON_CSS.aonWidth90Percent());
		tab.addStyleName(AON.AON_CSS.aonBlockCenter());
		tab.addStyleName(AON.AON_CSS.aonPanelGrid());
		Label title = new Label("Presentaci\u00F3n del modelo");
		tab.getFlexCellFormatter().setColSpan(0, 0, 3);
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
				if (getMod303().isFinished() || getMod303().isSent()) {
					submitForm(DOWNLOAD_FILE_ACTION);
				} else {
					getCallback().showBreakdownPanel("Para generar el fichero debe finalizar la confecci\u00F3n del modelo.");
				}
			}
		});
		p1.add(button1);
		tab.setWidget(row, 1, p1 );
		tab.getCellFormatter().setStyleName(row, 1, AON.AON_CSS.aonPanelGridEven());
		tab.getFlexCellFormatter().setColSpan(row, 1, 2);
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
				if (getMod303().isFinished() || getMod303().isSent()) {
					submitAEAT(VALIDATE_PRINT_ACTION);
					getCallback().showVisorAEAT();
				} else {
					getCallback().showBreakdownPanel("Para generar el fichero debe finalizar la confecci\u00F3n del modelo.");
				}
			}
		});
		p2.add(button2);
		tab.setWidget(row, 1, p2 );
		tab.getCellFormatter().setStyleName(row, 1, AON.AON_CSS.aonPanelGridEven());
		tab.getFlexCellFormatter().setColSpan(2, 1, 2);
		
		row++;
		
		// CON FIRMA NO CRIPTOGRAFICA
		Label icon3 = new Label();
		icon3.addStyleName(FiscalModelUtils.getAdministrationIcon(getMod303().getAdministration()));
		tab.setWidget(row, 0, icon3 );
		tab.getCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonPanelGridEven());
		FlowPanel p3 = new FlowPanel();
		p3.setStyleName(AON.AON_CSS.aonPadding2());
		Button button3 = new Button("Presentaci\u00F3n via Agencia Tributaria con firma no criptogr\u00e1fica (a partir de los datos guardados).");
		button3.setStyleName(AON.AON_CSS.aonPaddingLeft());
		button3.addStyleName(AON.AON_CSS.aonBorderNone());
		button3.addStyleName(AON.AON_CSS.aonEvenBackground());
		button3.addStyleName(AON.AON_CSS.aonClickable());
		button3.addStyleName("aon-icon-beta-text");
		button3.getElement().getStyle().setPaddingLeft(20, Unit.PX);
		button3.addClickHandler( new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {		
				CertificationPopup certPopup = new CertificationPopup(getAPI(), getMod303().getName(), getMod303().getDocument()) {
							
					@Override
					protected void onCancel() {
					
					}
							
					@Override
					protected void onAccept() {
						if(getMod303().isSent()) {
							getCallback().showBreakdownPanel("La presentaci\u00F3n del modelo ya se ha realizado con anterioridad.");
						} else if (getMod303().isFinished() || getMod303().isSent()) {
							submitAEAT(VALIDATE_PRINT_ACTION, getCert(), getPass(), getName(), getDocument());
							getCallback().showVisorAEAT();
						} else {
							getCallback().showBreakdownPanel("Para generar el fichero debe finalizar la confecci\u00F3n del modelo.");
						}	
					}
				};
				certPopup.center();
			}
		});
		p3.add(button3);
		tab.setWidget(row, 1, p3 );
		tab.getCellFormatter().setStyleName(row, 1, AON.AON_CSS.aonPanelGridEven());

		MOD303Service.presentationFile(getCallback().getDomainName(), getCallback().getDomain(), getCallback().getUser(), getMod303().getId(), new AsyncCallback<Integer>() {

			@Override public void onFailure(Throwable caught) {}

			@Override
			public void onSuccess(Integer result) {
				if(result > 0) {
					Button download2 = new Button();
					download2.setStyleName("aon-icon-mail-save");
					download2.addStyleName(AON.AON_CSS.aonIconCommandButton());
					download2.getElement().getStyle().setPaddingTop(16, Unit.PX);
					download2.addClickHandler(new ClickHandler() {
						
						@Override
						public void onClick(ClickEvent event) {
							getAPI().getFiscal().download(result +"");
						}
					});
					tab.setWidget(3, 2, download2 );
					tab.getCellFormatter().setStyleName(3, 2, AON.AON_CSS.aonPanelGridEven());
				} else tab.getFlexCellFormatter().setColSpan(3, 1, 2);
			}
		});
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
		list.add(new Pair<String, String>("Predeclaraci\u00F3n via AEAT (Papel)"
				,"https://www2.agenciatributaria.gob.es/wlpl/A303-PW18/index.zul?EDFI"));
		return list;
	}
	
	private void paintDeclarationTab(TabLayoutPanel tabPanel) {
		ScrollPanel declarationScrollPanel = new ScrollPanel();
		FlowPanel container = new FlowPanel();
		
		FlexTable table = createTable();
		paintWithoutActivityCheck(table);	// Sin actividad
		
		paintCheck(Mod303Key.CM_002,table);	// ¿Está inscrito en el Registro de devolució3n mensual (Art. 30 RIVA)?
		
		final ListBox a12 = new ListBox();
		a12.setWidth("150px");
		a12.addItem("(0) Para el mes de enero (01)", "0");
		a12.addItem("(1) SI", "1");
		a12.addItem("(2) NO", "2");
		paintListBox(a12, Mod303Key.CT_A12, table);

		paintA02(Mod303Key.CT_A02,table,tabPanel);	// ¿Tributa exclusivamente en régimen simplificado?
		
		paintCheck(Mod303Key.CT_A03,table);	// ¿Es autoliquidación conjunta?
		
		paintCheck(Mod303Key.CT_A07,table);	// ¿Ha optado por el régimen especial del criterio de Caja (art. 163 undecies LIVA)?
		paintCheck(Mod303Key.CT_A08,table);	// ¿Es destinatario de operaciones a las que se aplique el régimen especial del criterio de caja?
		
		
		final ListBox a11 = new ListBox();
		a11.setWidth("200px");
		a11.addItem("(0) NO exonerado (\u00FAltimo periodo), o la declaraci\u00F3n no es del \u00FAltimo periodo", "0");
		a11.addItem("(1) Exonerados, cuando se tiene volumen de operaciones  (art. 121 LIVA)", "1");
		a11.addItem("(2) Exonerados, cuando NO se tiene volumen de operaciones  (art. 121 LIVA)", "2");
		paintListBox(a11, Mod303Key.CT_A11, table);
		a11.setEnabled(getCallback().getMod303().isLastPeriod());
		if (getCallback().getMod303().isLastPeriod()) {
			a11.addChangeHandler( new ChangeHandler() {
				
				@Override
				public void onChange(ChangeEvent event) {
					if (a11.getSelectedIndex() != 0) {
						if (AonStringUtils.isNotBlank(getCallback().getMod303().getDescription(Mod303Key.CT_U1D))
						 || AonStringUtils.isNotBlank(getCallback().getMod303().getDescription(Mod303Key.CT_U2D))								
						 || AonStringUtils.isNotBlank(getCallback().getMod303().getDescription(Mod303Key.CT_U3D))								
						 || AonStringUtils.isNotBlank(getCallback().getMod303().getDescription(Mod303Key.CT_U4D))
						 || AonMathUtils.isNotZero( getCallback().getMod303().getAmount(Mod303Key.CT_C88))
						 || AonStringUtils.isNotBlank(getCallback().getMod303().getDescription(Mod303Key.CT_P1C))
						 || AonStringUtils.isNotBlank(getCallback().getMod303().getDescription(Mod303Key.CT_P2C))
						 || AonStringUtils.isNotBlank(getCallback().getMod303().getDescription(Mod303Key.CT_P3C))
						 || AonStringUtils.isNotBlank(getCallback().getMod303().getDescription(Mod303Key.CT_P4C))
						 || AonStringUtils.isNotBlank(getCallback().getMod303().getDescription(Mod303Key.CT_P5C))) {

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
								getCallback().getMod303().ensureDetail(key).clear();
							}
							LastPeriodInformationScrollPanel.clear();
							fillLastPeriodInformationScrollPanel();
							MessageDialog.warning("Se han inicializado los datos de la solapa \"Inf. Exonerados 390\"");
						}
					}
					if (a11.getSelectedIndex() != 0) {
						MessageDialog.warning("Debe rellenar los datos de la solapa \"Inf. Exonerados 390\"");
					}
				}
			});
		}
		
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
		
		final ListBox a13 = new ListBox();
		a13.setWidth("150px");
		a13.addItem("(0) Para el mes de enero (01)", "0");
		a13.addItem("(1) SI", "1");
		a13.addItem("(2) NO", "2");
		paintListBox(a13, Mod303Key.CT_A13, table);
		
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
				Model303AEATActivityFarmer actPanel = new Model303AEATActivityFarmer(activityCallback, getCallback().getMod303().isLastPeriod());
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
				Model303AEATActivity2018 actPanel = new Model303AEATActivity2018(activityCallback, getCallback().getMod303().isLastPeriod());
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
				dialog.setHeight("620px");
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
		if (getCallback().getMod303().isLastPeriod()) {
			paintDeclaration(table,Model3032017AEATSimplifiedRegime4TScript.values(),3);
		} else {
			paintDeclaration(table,Model3032017AEATSimplifiedRegimeScript.values(),3);
		}
		return table;
	}
	
	@Override
	protected void populate(Mod303 mod303) {
		super.populate(mod303);
//		farmerTable.setRowData(mod303.getActivityFarmerList());
//		activityTable.setRowData(mod303.getActivityList());
		farmerTable.setRowData(getCallback().getMod303().getActivityFarmerList());
		activityTable.setRowData(getCallback().getMod303().getActivityList());
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

	private void paintLastPeriodInformationTab(TabLayoutPanel tabPanel) {
		LastPeriodInformationScrollPanel = new ScrollPanel();
		fillLastPeriodInformationScrollPanel();
		tabPanel.add(LastPeriodInformationScrollPanel, TAB_TEMPLATE.render("Inf. Exonerados 390.", AON.AON_CSS.aonIconModel()));
	}


	private void fillLastPeriodInformationScrollPanel() {
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

		paintLabel(table, 0, AON.MSG.activities(),true);

		FlowPanel actContainer = new FlowPanel();
		actContainer.setStyleName(AON.AON_CSS.aonBlockCenter());
		actContainer.addStyleName(AON.AON_CSS.aonWidth90Percent());

		FlexTable tab = new FlexTable();
		tab.addStyleName(AON.AON_CSS.aonDataTable());
		tab.addStyleName(AON.AON_CSS.aonMarginBottom());
		
		tab.getColumnFormatter().setWidth(0, "40px");
		tab.getColumnFormatter().setWidth(1, "80px");
		tab.getColumnFormatter().setWidth(2, "60px");
		tab.getColumnFormatter().setWidth(3, "250px");
		tab.getColumnFormatter().setWidth(4, "auto");
		
		tab.setWidget(1, 0, new Label() );
		tab.setWidget(1, 1, new Label( AON.MSG.epigraph()) );
		tab.getFlexCellFormatter().addStyleName(1,1,AON.AON_CSS.aonDataTableHeader());
		tab.setWidget(1, 2, new Label( AON.MSG.key()) );
		tab.getFlexCellFormatter().addStyleName(1,2,AON.AON_CSS.aonDataTableHeader());
		tab.setWidget(1, 3, new Label( AON.MSG.description()) );
		tab.getFlexCellFormatter().addStyleName(1,3,AON.AON_CSS.aonDataTableHeader());
		tab.setWidget(1, 4, new Label() );
		tab.getFlexCellFormatter().addStyleName(1,4,AON.AON_CSS.aonDataTableHeader());
		
		
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
		actContainer2.setStyleName(AON.AON_CSS.aonBlockCenter());
		actContainer2.addStyleName(AON.AON_CSS.aonWidth90Percent());

		FlexTable tab2 = new FlexTable();
		tab2.addStyleName(AON.AON_CSS.aonDataTable());
		tab2.addStyleName(AON.AON_CSS.aonMarginBottom());
		
		tab2.getColumnFormatter().setWidth(0, "80px");
		tab2.getColumnFormatter().setWidth(1, "20px");
		tab2.getColumnFormatter().setWidth(2, "150px");
		tab2.getColumnFormatter().setWidth(3, "150px");
		tab2.getColumnFormatter().setWidth(4, "80px");
		tab2.getColumnFormatter().setWidth(5, "150px");
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
		
		paintProrrateRow(tab2,Mod303Key.CT_P1C,Mod303Key.CT_P1I,Mod303Key.CT_P1D,Mod303Key.CT_P1T,Mod303Key.CT_P1P);
		paintProrrateRow(tab2,Mod303Key.CT_P2C,Mod303Key.CT_P2I,Mod303Key.CT_P2D,Mod303Key.CT_P2T,Mod303Key.CT_P2P);
		paintProrrateRow(tab2,Mod303Key.CT_P3C,Mod303Key.CT_P3I,Mod303Key.CT_P3D,Mod303Key.CT_P3T,Mod303Key.CT_P3P);
		paintProrrateRow(tab2,Mod303Key.CT_P4C,Mod303Key.CT_P4I,Mod303Key.CT_P4D,Mod303Key.CT_P4T,Mod303Key.CT_P4P);
		paintProrrateRow(tab2,Mod303Key.CT_P5C,Mod303Key.CT_P5I,Mod303Key.CT_P5D,Mod303Key.CT_P5T,Mod303Key.CT_P5P);

		actContainer2.add(tab2);
		
		int row = table.getRowCount();
		table.setWidget(row, 0, actContainer2);
		table.getFlexCellFormatter().setColSpan(row, 0, 4);
		
		LastPeriodInformationScrollPanel.setWidget(table);
	}


	private void paintProrrateRow(FlexTable tab, Mod303Key cnaeKey, Mod303Key amountKey, Mod303Key amountRightKey, Mod303Key typeKey, Mod303Key percentKey) {
		int row = tab.getRowCount();
		
		TextBox cnae = new TextBox();
		cnae.setVisibleLength(4);
		cnae.setMaxLength(4);
		cnae.setStyleName(AON.AON_CSS.aonInputText());
		cnae.setValue(getCallback().getMod303().getDescription(cnaeKey));
		cnae.addValueChangeHandler(new ValueChangeHandler<String>() {

			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				getCallback().getMod303().putDescription(cnaeKey, cnae.getValue() );
				markAsDirty();
			}
			
		});
		tab.setWidget(row, 0, cnae);

		Cnae2009Panel panel = new Cnae2009Panel( new SelectionCallBack() {
			@Override public void onClose() {}
			@Override
			public void onSelect(CNAE2009 selected) {
				cnae.setValue(selected.getCodeWithoutPoint(),false);
				getCallback().getMod303().putDescription(cnaeKey, selected.getCodeWithoutPoint());
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
		amount.setValue(getCallback().getMod303().getAmount(amountKey));
		amount.addValueChangeHandler(new ValueChangeHandler<Double>() {

			@Override
			public void onValueChange(ValueChangeEvent<Double> event) {
				getCallback().getMod303().putAmount(amountKey, amount.getValue() );
				markAsDirty();
			}
			
		});
		tab.setWidget(row, 2, amount);
		
		DoubleBox amountRight = new DoubleBox();
		amountRight.setValue(getCallback().getMod303().getAmount(amountRightKey));
		amountRight.addValueChangeHandler(new ValueChangeHandler<Double>() {

			@Override
			public void onValueChange(ValueChangeEvent<Double> event) {
				getCallback().getMod303().putAmount(amountRightKey, amountRight.getValue() );
				markAsDirty();
			}
			
		});
		tab.setWidget(row, 3, amountRight);
		
		ListBox typeBox = new ListBox();
		typeBox.setWidth("40px");
		typeBox.addItem(" - ", "");
		typeBox.addItem("G - General", "G");
		typeBox.addItem("E - Especial", "E");
		String type = getCallback().getMod303().getDescription(typeKey);
		if (AonStringUtils.equals(type, "G")) typeBox.setSelectedIndex(1);
		else if (AonStringUtils.equals(type, "E")) typeBox.setSelectedIndex(2);
		else typeBox.setSelectedIndex(0); 
		typeBox.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				getCallback().getMod303().putDescription(typeKey,typeBox.getSelectedValue());
				markAsDirty();
			}
		});
		tab.setWidget(row, 4, typeBox);
		
		
		DoubleBox percent = new DoubleBox();
		percent.setValue(getCallback().getMod303().getAmount(percentKey));
		percent.addValueChangeHandler(new ValueChangeHandler<Double>() {

			@Override
			public void onValueChange(ValueChangeEvent<Double> event) {
				getCallback().getMod303().putAmount(percentKey, percent.getValue() );
				markAsDirty();
			}
			
		});
		tab.setWidget(row, 5, percent);
	}


	private void paintActivityRow(FlexTable tab, Mod303Key desKey, Mod303Key keyKey, Mod303Key epiKey) {
		int row = tab.getRowCount();

		tab.setWidget(row, 0, new Label( row == 2 ? "Principal" : "Otras" ) );

		TextBox epi = new TextBox();
		epi.setVisibleLength(5);
		epi.setMaxLength(4);
		epi.setStyleName(AON.AON_CSS.aonInputText());
		epi.setValue(getCallback().getMod303().getDescription(epiKey));
		epi.addValueChangeHandler(new ValueChangeHandler<String>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				getCallback().getMod303().putDescription(epiKey,epi.getValue());
				markAsDirty();
			}
		});
		tab.setWidget(row, 1, epi);

		TextBox key = new TextBox();
		key.setVisibleLength(2);
		key.setMaxLength(1);
		key.setStyleName(AON.AON_CSS.aonInputText());
		key.setValue(getCallback().getMod303().getDescription(keyKey));
		key.addValueChangeHandler(new ValueChangeHandler<String>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				getCallback().getMod303().putDescription(keyKey,key.getValue());
				markAsDirty();
			}
		});
		tab.setWidget(row, 2, key);

		TextBox description = new TextBox();
		description.setVisibleLength(40);
		description.setMaxLength(40);
		description.setStyleName(AON.AON_CSS.aonInputText());
		description.setValue(getCallback().getMod303().getDescription(desKey));
		description.addValueChangeHandler(new ValueChangeHandler<String>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				getCallback().getMod303().putDescription(desKey,description.getValue());
				markAsDirty();
			}
		});
		tab.setWidget(row, 3, description);
		
		tab.setWidget(row, 4, new Label() );

	}
}
