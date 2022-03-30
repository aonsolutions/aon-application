package com.esferalia.aon.gwt.mod200.client.mod200.e2020;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.Upload;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog.AonConfirmDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessageDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonSplash;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.mod200.client.AonFiscalModelHeader;
import com.esferalia.aon.gwt.mod200.client.mod200.Model200;
import com.esferalia.aon.gwt.mod200.client.mod200.Model200.Model200Callback;
import com.esferalia.aon.gwt.mod200.client.mod200.Model200ModuleOptions;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2020.Mod2002020;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.Widget;


//public class Model2002020 extends ResizeComposite  {
public class Model2002020 extends DockLayoutPanel {
	
	public static final int BOX_LENGTH = 5;
	
//	final static int NOTIFICATIONS_TAB = 0;
//	final static int INFORMATION_TAB = 1;

	protected interface Model200PageCallback {	
		public Mod2002020Object getMod200Object();
		public void markAsDirty(); 
	}
	
	private PageAbs[] PAGES = new PageAbs[20];
	private int P00 = 0;
	
//	interface Model2002020Binder extends
//			UiBinder<Widget, Model2002020> {
//	}
//
//	private static final Model2002020Binder binder = GWT
//			.create(Model2002020Binder.class);

	protected Mod2002020Object mod200Object;
	private Model200Callback mod200Callback;
	
//	@UiField
//	DockLayoutPanel dockLayoutPanel = new DockLayoutPanel(Unit.PX);
//	@UiField
//	SplitLayoutPanel splitLayoutPanel = new SplitLayoutPanel();
	
//	@UiField
//	SimplePanel headerPanel = new SimplePanel();
		
//	@UiField
	AonToolbarButton initializeButton = new AonToolbarButton("");
//	@UiField
	AonToolbarButton importAccountingButton = new AonToolbarButton("");
//	@UiField
	AonToolbarButton saveButton = new AonToolbarButton("");
//	@UiField
	AonToolbarButton removeButton = new AonToolbarButton("");
//	@UiField
	AonToolbarButton resetButton = new AonToolbarButton("");
//	@UiField
//	AonToolbarButton cancelButton;
//	@UiField
	AonToolbarButton validateButton = new AonToolbarButton("");
//	@UiField
	AonToolbarButton aeatAccountingFileButton = new AonToolbarButton("");
//	@UiField
	AonToolbarButton aeatFileButton = new AonToolbarButton("");
//	@UiField
	AonToolbarButton aeatPrintButton = new AonToolbarButton("");

//	@UiField
	SimpleLayoutPanel pageContainer = new SimpleLayoutPanel();
	
//	@UiField
//	ScrollPanel linkList;
	
//	@UiField
//	TabLayoutPanel tabLayout = new TabLayoutPanel(26, Unit.PX);
//	@UiField
//	ResultsPanel resultsPanel = new ResultsPanel();
//	@UiField
//	MinimizePanel footPanel = new MinimizePanel();
//	@UiField
//	Label fiscalInformationLabel = new Label();
//	@UiField
//	ScrollPanel infoContainer = new ScrollPanel();

//	@UiField
//	FlowPanel formContainer = new FlowPanel();
	FormPanel diskForm;
	Hidden modIdHidden;
	Hidden domainIdHidden;
	Hidden domainNameHidden;
	Hidden userHidden;

//	private ErrorPage errorPage;
	
	private Model200ModuleOptions options;
	
	private PopupPanel popup;
	protected final InlineLabel dirtyLabel = new InlineLabel();
	private boolean dirty;
	
	public Model2002020(Model200ModuleOptions options, Model200Callback mod200Callback) {
		super(Unit.PX);
		
		popup = new PopupPanel(false, true);
		popup.add( new AonSplash());
		popup.setGlassEnabled(true);
		popup.setAnimationEnabled(true);
//		popup.center();

//		Window.alert("Model2002020 PASO 1");
		
		this.mod200Callback = mod200Callback;
		this.options = options;

//		Window.alert("Model2002020 PASO 2");
		
//		Widget ui = binder.createAndBindUi(this);
//		initWidget(ui);
		
//		fillLinkContainer();
//		errorPage = new ErrorPage();
//		errorPage.addSelectionHandler( new  SelectionHandler<ValidationMessage2020>() {
//			
//			@Override
//			public void onSelection(SelectionEvent<ValidationMessage2020> event) {
//				ValidationMessage2020 msg = event.getSelectedItem();
//				if ( msg.getPage() >= 0 ) {
//					boolean mustDump = getPage(msg.getPage()) == null;
//					PageAbs page = ensurePage(msg.getPage(), new Model200PageCallback(){
//
//						@Override
//						public Mod2002020Object getMod200Object() {
//							return mod200Object;
//						}
//						
//					});
//					if (mustDump) page.dump();
//					pageContainer.setWidget(page);
//					if (msg.getKey() != null) {
//						DoubleBox d = page.getInputs().get(msg.getKey());
//						if (d != null) {
//							d.setFocus(true);
//						}
//						BoxLabel l = page.getLabels().get(msg.getKey());
//						if (l != null) {
//							l.addErrorState(msg.getMessage());
//						}
//					}
//				}
//				
//			}
//		});
		
//		Window.alert("Model2002020 PASO 3");
		
//		tabLayout.add(resultsPanel, "Notificaciones");
//		tabLayout.add(infoContainer, "Agencia Tributaria");
//		
//		tabLayout.setAnimationDuration(300);
//		tabLayout.selectTab(NOTIFICATIONS_TAB);
//		tabLayout.addSelectionHandler(new SelectionHandler<Integer>() {
//			
//			@Override
//			public void onSelection(SelectionEvent<Integer> event) {
//				openFootPanelIfNeeded();
//			}
//		});
		
//		Window.alert("Model2002020 PASO 4");

		diskForm = new FormPanel("_blank");
		diskForm.setMethod(FormPanel.METHOD_POST);
		FlowPanel formFlowPanel = new FlowPanel();
		diskForm.add(formFlowPanel);
		modIdHidden = new Hidden("modId");
		formFlowPanel.add(modIdHidden);
		domainIdHidden = new Hidden("domainId");
		formFlowPanel.add(domainIdHidden);
		domainNameHidden = new Hidden("domainName");
		formFlowPanel.add(domainNameHidden);
		userHidden = new Hidden("user");
		formFlowPanel.add(userHidden);		
//		formContainer.add(diskForm);	
		
//		Window.alert("Model2002020 PASO 5");
	}
	
//	private void fillInfo() {
//		fiscalInformationLabel.setStyleName(AON.AON_CSS.aonPaddingRight());
//		fiscalInformationLabel.addStyleName(AON.AON_CSS.aonPaddingLeft20());
//		fiscalInformationLabel.addStyleName(FiscalModelUtils.getAdministrationIconBW(mod200Object.getMod200().getAdministration()));
//		
//		FlowPanel panel = new FlowPanel();
//		panel.setStyleName(AON.AON_CSS.aonScrollArea());
//		panel.add(FiscalModelUtils.getAnchorPanel( mod200Object.getMod200()
//				,"Tr\u00E1mites."
//				,"https://www.agenciatributaria.gob.es/AEAT.sede/tramitacion/GE04.shtml"));
//		panel.add(FiscalModelUtils.getAnchorPanel( mod200Object.getMod200()
//				,"Informaci\u00F3n general." 
//				,"https://www.agenciatributaria.gob.es/AEAT.sede/Ayuda/GE04.shtml"));
//		panel.add(FiscalModelUtils.getAnchorPanel(mod200Object.getMod200()
//				,"Ficha."
//				,"https://www.agenciatributaria.gob.es/AEAT.sede/procedimientos/GE04.shtml"));
//		infoContainer.setWidget(panel);
//	}
	
	
//	protected void paintHeaderTable(final Mod2002020 mod200) {
//		headerPanel.setStyleName(AON.AON_CSS.aonWidthAll());
//		
//		FlexTable headerTable = new FlexTable();
//		headerTable.setStyleName(AON.AON_CSS.aonFiscalModelTable());
//		
//		Label image = new Label("");
//		image.setStyleName(FiscalModelUtils.getAdministrationImage(mod200.getAdministration()));
//		
//		headerTable.setWidget(0, 0, image);
//		headerTable.getFlexCellFormatter().setStyleName(0, 0, AON.AON_CSS.aonFiscalModelTableHeaderImage());
//		headerTable.getFlexCellFormatter().setRowSpan(0, 0, 2);
//		
//		headerTable.setWidget(0, 1, new Label( AON.MSG.fiscalModelDescriptionlong(mod200.getModel())));
//		headerTable.getFlexCellFormatter().setStyleName(0, 1, AON.AON_CSS.aonFiscalModelTableHeaderTitle());
//		headerTable.getFlexCellFormatter().addStyleName(0, 1, FiscalModelUtils.getAdministrationBG(mod200.getAdministration()));
//		headerTable.getFlexCellFormatter().setRowSpan(0, 1, 2);
//		
//		headerTable.setWidget(0, 2, new Label(mod200.getModel().getName()));
//		headerTable.getFlexCellFormatter().setStyleName(0, 2, AON.AON_CSS.aonFiscalModelTableHeaderModel());
//		headerTable.getFlexCellFormatter().addStyleName(0, 2, FiscalModelUtils.getAdministrationBG(mod200.getAdministration()));
//		
//		headerTable.setWidget(1, 0, new Label(""+mod200.getYear()));
//		headerTable.getFlexCellFormatter().setStyleName(1, 0, AON.AON_CSS.aonFiscalModelTableHeaderModel());
//		headerTable.getFlexCellFormatter().addStyleName(1, 0, FiscalModelUtils.getAdministrationBG(mod200.getAdministration()));
//		
//		headerPanel.setWidget(headerTable);
//	}
	
	private class PopupAsyncCallback implements AsyncCallback<Mod2002020> {
		PopupPanel popup;
		public void setPopup(PopupPanel popup) {
			this.popup = popup;
		}

		@Override
		public void onSuccess(Mod2002020 result) {
			popup.hide();
		}

		@Override
		public void onFailure(Throwable caught) {
			popup.hide();
		}
		
	}

	public void startModel(final Mod2002020Object modObject ) {
		mod200Object = modObject;
//		fillInfo();
//		final PopupPanel popup = new PopupPanel(false, true);
//		Label label = new Label(AON.MSG.processing());
//		label.addStyleName(AON.AON_CSS.aonTimer());
//		popup.add(label);
//		popup.setGlassEnabled(true);
//		popup.setAnimationEnabled(true);
//		popup.center();
//		paintHeaderTable(mod200Object.getMod200());
		
//		final PopupPanel popup = new PopupPanel(false, true);
//		popup.add( new AonSplash());
//		popup.setGlassEnabled(true);
//		popup.setAnimationEnabled(true);
		popup.center();
		select(mod200Object.getMod200());
		dump((mod200Object.getMod200().getId() == null) );
		popup.hide();
		if (mod200Object.getMod200().getId() == null) 
			markAsDirty();
	}


	private void dump( boolean charactersEnabled) {
		ensurePage(P00,new Model200PageCallback(){

			@Override
			public Mod2002020Object getMod200Object() {
				return mod200Object;
			}

			@Override
			public void markAsDirty() {
				Model2002020.this.markAsDirty();
			}
			
		}).dump();
		pageContainer.setWidget(getPage(P00));
		refreshButtonsVisibility();
		((Page00) getPage(P00)).enableCharacters( charactersEnabled );
	}
	
	private void refreshButtonsVisibility() {
		initializeButton.setVisible(!mod200Object.isInitialized());
		importAccountingButton.setVisible(mod200Object.isInitialized());
		saveButton.setVisible(mod200Object.isInitialized());
		removeButton.setVisible(mod200Object.getMod200().getId() != null);
		resetButton.setVisible(mod200Object.getMod200().getId() != null);
//		cancelButton.setVisible(true);
		//validateButton.setVisible(mod200Object.isInitialized() && mod200Object.getMod200().getId() != null);
		validateButton.setVisible(false); // A partir del 2020 no se utiliza "VALIDAR" de AON ya que está desactualizado y "Validar/Imprimir" de la Agencia Tributaria hace todas las validaciones posibles
		aeatAccountingFileButton.setVisible(mod200Object.isInitialized() && mod200Object.getMod200().getId() != null);
		aeatAccountingFileButton.setEnabled(!isDirty());
		aeatFileButton.setVisible(mod200Object.isInitialized() && mod200Object.getMod200().getId() != null);
		aeatFileButton.setEnabled(!isDirty());
		aeatPrintButton.setVisible(mod200Object.isInitialized() && mod200Object.getMod200().getId() != null);
		aeatPrintButton.setEnabled(!isDirty());
	}
	
//	protected void raiseException(Throwable t) {
//		errorPage.addErrorMsg(t);
//		resultsPanel.setWidget(errorPage);
//		showResultsPanel();
//	}
	
	private void populatePages() {
		for (PageAbs page : PAGES) {
			if (page != null) page.populate();
		}
	}
	
//	@UiHandler("validateButton")
//	void onValidateButton(ClickEvent event) {
//		validate(new PopupAsyncCallback() {
//			@Override
//			public void onSuccess(Mod2002020 result) {
//				super.onSuccess(result);
//				if (result.getMessages() != null && !result.getMessages().isEmpty()) {
//					errorPage.addErrorMsg( result.getMessages() );
//					resultsPanel.setWidget(errorPage);
//				} else {
//					errorPage.clearMessages();
//					errorPage.addInfoMsg( AON.MSG.noValidationMessages() );
//				}
//				resultsPanel.setWidget(errorPage);
//				showResultsPanel();
//			}
//
//			@Override
//			public void onFailure(Throwable caught) {
//				super.onFailure(caught);
//				errorPage.addErrorMsg(caught);
//				resultsPanel.setWidget(errorPage);
//				showResultsPanel();
//			}
//			
//		});
//	}

//	private void validate(PopupAsyncCallback callback) {
//		final PopupPanel popup = new PopupPanel(false, true);
//		Label label = new Label(AON.MSG.processing());
//		label.addStyleName(AON.AON_CSS.aonTimer());
//		popup.add(label);
//		popup.setGlassEnabled(true);
//		popup.setAnimationEnabled(true);
//		popup.center();
//		callback.setPopup(popup);
//		populatePages();
//		mod200Object.validate(callback);
//	}
	
	private void submitForm(String action) {
		
		diskForm.setAction(GWT.getHostPageBaseURL() + action);
        modIdHidden.setValue(String.valueOf(mod200Object.getMod200().getId()));
        domainIdHidden.setValue(String.valueOf(options.getDomain()));
        domainNameHidden.setValue(options.getDomainName());
        userHidden.setValue(options.getUser());
        diskForm.submit();
		
	}
	
//	private void submitForm(String action) {
//	diskForm.setMethod(FormPanel.METHOD_POST);
//	diskForm.setAction(GWT.getHostPageBaseURL() + action);
//	diskForm.clear();
//	FlowPanel diskPanel = new FlowPanel();
//	diskPanel.add(mod200Hidden);
//	diskPanel.add(domainIdHidden);
//	diskPanel.add(domainNameHidden);
//	diskPanel.add(userHidden);
//	diskForm.add(diskPanel);
//	mod200Hidden.setValue(String.valueOf(getModel().getId()));
//	domainIdHidden.setValue(String.valueOf(getCallback().getOptions().getDomain()));
//	domainNameHidden.setValue(getCallback().getOptions().getDomainName());
//	userHidden.setValue(getCallback().getOptions().getUser());
//	diskForm.submit();
//}
	
	private class WestFocusPanel extends FocusPanel {
		
		public WestFocusPanel(int pag, String label) {
			super();
//			setStyleName(AON.AON_CSS.aonLinkItem());
//			FlexTable focTab = new FlexTable();
//			focTab.setCellPadding(0);
//			focTab.setCellSpacing(0);
//			focTab.setStyleName(AON.AON_CSS.aonWidthAll());
//			focTab.getColumnFormatter().setWidth(0, "30px");
//			focTab.getColumnFormatter().setWidth(1, "auto");
//			
//			focTab.setWidget(0, 0, new InlineLabel(AonNumberUtils.toString(pag)));
//			focTab.getCellFormatter().setStyleName(0, 0, AON.AON_CSS.aonLinkListItem());
//			focTab.getCellFormatter().addStyleName(0, 0, AON.AON_CSS.aonTextCenter());
//			focTab.getCellFormatter().addStyleName(0, 0, AON.AON_CSS.aonColorWhite());
//			focTab.getCellFormatter().addStyleName(0, 0, AON.AON_CSS.aonBold());
//			
//			focTab.setWidget(0, 1, new InlineLabel(label));
//			focTab.getCellFormatter().setStyleName(0, 1, AON.AON_CSS.aonLinkListItem());
//			focTab.getCellFormatter().addStyleName(0, 1, AON.AON_CSS.aonColorWhite());
//			
//			setWidget(focTab);
			
			setStyleName(AON.CSS.aonWidthAll());
			FlowPanel container = new FlowPanel();
			container.setWidth("95%");
			container.setStyleName(AON.CSS.aonBlockCenter());
			container.addStyleName(AON.CSS.aonClickableBlock());
			container.addStyleName(AON.CSS.aonFlexBlock());
			container.addStyleName(AON.CSS.aonBorder());
			container.getElement().getStyle().setMarginTop(5.0, Unit.PX);
			container.getElement().getStyle().setProperty("min-height", "30px");
			
			InlineLabel cardLabel = new InlineLabel( label );
			cardLabel.getElement().getStyle().setPaddingLeft(5.0, Unit.PX);
			container.add( cardLabel );
			setWidget(container);
			
			addClickHandler( new  ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					int realPag = (pag - 1);
					if (realPag > 0) {
						checkAndShowPage(realPag);	
					} else {
						showPage(realPag);
					}
					
				}
			});
		}
		
		private void showPage(int page) {
			FlowPanel parent = 	(FlowPanel) getParent();
			for (int i = 0 ; i < parent.getWidgetCount(); i++) {
				parent.getWidget(i).removeStyleName(AON.CSS.aonBackgroundLigthGray());
			}
			
			PageAbs pageAbs = ensurePage(page, 
					new Model200PageCallback(){
						@Override 
						public Mod2002020Object getMod200Object() {
							return mod200Object;
						}

						@Override
						public void markAsDirty() {
							Model2002020.this.markAsDirty();						
						}
					});
			if (pageAbs.isAvailable()) {
				pageAbs.dump();
				pageContainer.setWidget(pageAbs);
				addStyleName(AON.CSS.aonBackgroundLigthGray());
			} else {
				AonMessageDialog.warning(AON.MSG.pageNotAvailable());
			}
			
		}
		
		private void checkAndShowPage(int page) {

			if (mod200Object.isInitialized() ) {
				showPage(page);
			} else {
				AonMessageDialog.warning(AON.MSG.mustInitialzeMod200());
			}
		}
	}

//	private void fillLinkContainer() {
//		FlowPanel linkContainer = new FlowPanel();
//		linkContainer.setStyleName(AON.AON_CSS.aonPaddingLeft());
//		linkContainer.setStyleName(AON.AON_CSS.aonPaddingRight());
//		// En el método showPage, hay un cast a FlowPanel. Cuidado con la estrutura. 
//		linkContainer.add(new WestFocusPanel( 1,AON.MSG.identification()	 ));
//		linkContainer.add(new WestFocusPanel( 2,AON.MSG.administratorPage()	 ));
//		linkContainer.add(new WestFocusPanel( 3,AON.MSG.participations2019() ));
//		linkContainer.add(new WestFocusPanel( 4,AON.MSG.balanceActivo()		 ));
//		linkContainer.add(new WestFocusPanel( 5,AON.MSG.balancePasivoAbbrv()	 ));
//		linkContainer.add(new WestFocusPanel( 6,AON.MSG.pyg() 				 ));
//		linkContainer.add(new WestFocusPanel( 7,AON.MSG.patrimonioIngresosAbbrv() ));
//		linkContainer.add(new WestFocusPanel( 8,AON.MSG.patrimonioCambios()	 ));
//		linkContainer.add(new WestFocusPanel( 9,AON.MSG.liquidacionI()		 ));
//		linkContainer.add(new WestFocusPanel(10,AON.MSG.liquidacionII()		 ));
//		linkContainer.add(new WestFocusPanel(11,AON.MSG.liquidacionIII()	 ));
//		linkContainer.add(new WestFocusPanel(12,AON.MSG.liquidacionIV() 	 ));
//		linkContainer.add(new WestFocusPanel(13,AON.MSG.liquidacionV() 	 	 ));
//		linkContainer.add(new WestFocusPanel(14,AON.MSG.combinedTaxationAbbrv()	 ));
//		linkContainer.add(new WestFocusPanel(15,"Aplicaci\u00F3n de resultados / Documentaci\u00F3n previa"));
//		linkContainer.add(new WestFocusPanel(16,AON.MSG.deducibleLimitationAbbrv()));
//		linkContainer.add(new WestFocusPanel(17,AON.MSG.page17()		 	 ));
//		linkContainer.add(new WestFocusPanel(18,AON.MSG.page18()		 	 ));
//		linkContainer.add(new WestFocusPanel(19,"U.T.E.S."));
//		linkContainer.add(new WestFocusPanel(20,AON.MSG.bussinessAmount()	 ));
//		linkContainer.add(new WestFocusPanel(21,AON.MSG.idDocument()		 ));
//		linkList.setWidget( linkContainer );
//	}
	
//	private void showResultsPanel() {
//		openFootPanel();
//	}

//	@UiHandler("footPanel")
//	void onFootMinimize(MinimizeEvent event) {
//		closeFootPanel();
//	}
//
//	@UiHandler("footPanel")
//	void onFootMaximize(MaximizeEvent event) {
//		splitLayoutPanel.setWidgetSize(footPanel, Window.getClientHeight() / 2);
//		splitLayoutPanel.animate(500);
//	}
//
//	private void closeFootPanel() {
//		splitLayoutPanel.setWidgetSize(footPanel, 30);
//		splitLayoutPanel.animate(500);
//	}
//	
//	private void openFootPanel() {
//		splitLayoutPanel.setWidgetSize(footPanel, Window.getClientHeight() / 4);
//		splitLayoutPanel.animate(500);
//	}
//	
//	private void openFootPanelIfNeeded() {
//		if (splitLayoutPanel.getWidgetSize(footPanel) <= 50) {
//			openFootPanel();
//		}
//	}

	private PageAbs getPage( int i) {
		return PAGES[i];
	}
	
	private PageAbs ensurePage(int i,Model200PageCallback cbk) {
		if (PAGES[i] == null) {
			if (i ==  0) PAGES[i] = new Page00(cbk); 
			if (i ==  1) PAGES[i] = new Page01(cbk); 
			if (i ==  2) PAGES[i] = new Page02(cbk); 
			if (i ==  3) PAGES[i] = new Page03(cbk); 
			if (i ==  4) PAGES[i] = new Page04(cbk); 
			if (i ==  5) PAGES[i] = new Page05(cbk); 
			if (i ==  6) PAGES[i] = new Page06(cbk); 
			if (i ==  7) PAGES[i] = new Page07(cbk); 
			if (i ==  8) PAGES[i] = new Page08(cbk); 
			if (i ==  9) PAGES[i] = new Page09(cbk); 
			if (i == 10) PAGES[i] = new Page10(cbk); 
			if (i == 11) PAGES[i] = new Page11(cbk);
			if (i == 12) PAGES[i] = new Page12(cbk); 
			if (i == 13) PAGES[i] = new Page13(cbk);
			if (i == 14) PAGES[i] = new Page14(cbk); 
			if (i == 15) PAGES[i] = new Page15(cbk); 
			if (i == 16) PAGES[i] = new Page16(cbk);
			if (i == 17) PAGES[i] = new Page17(cbk); 
			if (i == 18) PAGES[i] = new Page18(cbk); 
			if (i == 19) PAGES[i] = new Page19(cbk); 
			if (i == 20) PAGES[i] = new Page20(cbk); 
		}
		return getPage(i);
	}
	
	public void select(Mod2002020 mod200) {
		
		clear();
		
		AonFiscalModelHeader modelHeader = new AonFiscalModelHeader(mod200);
		addNorth(modelHeader, AonFiscalModelHeader.HEIGTH);
		addNorth(getToolbar(mod200), AonToolbar.HEIGTH);
//		addNorth(getDeclarationToolbarPanel(mod200), AonToolbar.HEIGTH);
		addWest(getLinksPanel(), 300);
		add(pageContainer);		
		
	}
	
	private Widget getToolbar(Mod2002020 mod200) {
		AonToolbar toolbarPanel = new AonToolbar(AonStringUtils.join(mod200.getDocument(),AonStringUtils.SPACE, mod200.getFullName()));
		
		AonToolbarButton cancelButton = new AonToolbarButton(AON.MSG.cancelAction(), AON.CSS.aonIconBack() );
		if (mod200Callback.getOptions().isBackButtonVisible() && mod200Callback.getOptions().hasExternalCallback()) {
			cancelButton.setText(AON.MSG.backAction());
			cancelButton.setTitle(AON.MSG.backAction());
		}
		cancelButton.addClickHandler(event -> {
			if (mod200Callback.getOptions().isBackButtonVisible() && mod200Callback.getOptions().hasExternalCallback()) {
				mod200Callback.getOptions().getExternalCallback().onExit(mod200);
			} else {
				mod200Callback.onCancel(mod200);
			}
		});
		toolbarPanel.add(cancelButton);
		
		initializeButton = new AonToolbarButton(AON.MSG.continueAction(), AON.CSS.aonIconSave());
		initializeButton.addClickHandler(event -> {
			mod200Callback.cleanErrorPanel();
//			final PopupPanel popup = new PopupPanel(false, true);
//			Label label = new Label(AON.MSG.processing());
//			label.addStyleName(AON.AON_CSS.aonTimer());
//			popup.add(label);
//			popup.setGlassEnabled(true);
//			popup.setAnimationEnabled(true);
			popup.center();
			getPage(P00).populate( );
			mod200Object.initializeMod200(new AsyncCallback<Mod2002020>() {
				@Override
				public void onSuccess(Mod2002020 result) {
					popup.hide();
					dump(false);
					markAsDirty();
				}
				
				@Override
				public void onFailure(Throwable e) {
					popup.hide();
					mod200Callback.showError(e.getMessage());
//					raiseException(e);
				}
			});
		});
		toolbarPanel.add(initializeButton);
		
		saveButton = new AonToolbarButton(AON.MSG.saveAction(), AON.CSS.aonIconSave());
		saveButton.addClickHandler(event -> {
			saveButton.setEnabled(false);
			mod200Callback.cleanErrorPanel();
			
			PopupAsyncCallback callback = new PopupAsyncCallback(){
				@Override
				public void onSuccess(Mod2002020 result) {
					super.onSuccess(result);
					saveButton.setEnabled(true);
					setDirty(false);					
//					refreshButtonsVisibility();
				}
				@Override
				public void onFailure(Throwable caught) {
					super.onFailure(caught);
//					AonMessageDialog.error("No se han podido guardar los datos.");
//					errorPage.clearMessages();
//					errorPage.addErrorMsg( caught.getMessage() );
//					resultsPanel.setWidget(errorPage);
//					showResultsPanel();
					mod200Callback.showError(caught.getMessage());
					saveButton.setEnabled(true);
				}
			};
//			final PopupPanel popup = new PopupPanel(false, true);
//			Label label = new Label(AON.MSG.processing());
//			label.addStyleName(AON.AON_CSS.aonTimer());
//			popup.add(label);
//			popup.setGlassEnabled(true);
//			popup.setAnimationEnabled(true);
			popup.center();
			callback.setPopup(popup);
			populatePages();
			try {
				mod200Object.save(callback);
			} catch (IllegalArgumentException e) {
				refreshButtonsVisibility();
				popup.hide();
				AonMessageDialog.error(e.getMessage());
			}
		});
		toolbarPanel.add(saveButton);
		
//		@UiHandler("removeButton")
//		void onRemoveButtonClick(ClickEvent event) {
			
		removeButton = new AonToolbarButton(AON.MSG.deleteAction(), AON.CSS.aonIconDelete());
		removeButton.addClickHandler(event -> {
			mod200Callback.cleanErrorPanel();
			
			AonConfirmDialog cd = new AonConfirmDialog();
			cd.confirm(AON.MSG.confirmDeclarationDeleteAction(), new AonConfirmDialogCallback() {

				@Override
				public void onCancel() {
				}

				@Override
				public void onAccept() {
//					final PopupPanel popup = new PopupPanel(false, true);
//					Label label = new Label(AON.MSG.processing());
//					label.addStyleName(AON.AON_CSS.aonTimer());
//					popup.add(label);
//					popup.setGlassEnabled(true);
//					popup.setAnimationEnabled(true);
					popup.center();
					try {
						mod200Object.delete(new AsyncCallback<Void>() {
							
							@Override
							public void onSuccess(Void result) {
								popup.hide();
								mod200Callback.removed();
							}
							
							@Override
							public void onFailure(Throwable caught) {
								popup.hide();
								AonMessageDialog.error("No se han podido borrar los datos.");
							}
						});
					} catch (IllegalArgumentException e) {
						popup.hide();
						AonMessageDialog.error("No se han podido borrar los datos.");
					}
				}
			});
		});
		toolbarPanel.add(removeButton);
		
//		@UiHandler("resetButton")
//		void onResetButtonClick(ClickEvent event) {
			
		resetButton = new AonToolbarButton(AON.MSG.resetAction(), AON.CSS.aonIconRefresh());
		resetButton.addClickHandler(event -> {
			mod200Callback.cleanErrorPanel();
			
			AonConfirmDialog cd = new AonConfirmDialog();
			cd.confirm(AON.MSG.confirmDeclarationinitializationAction(), new AonConfirmDialogCallback() {

				@Override
				public void onCancel() {
				}

				@Override
				public void onAccept() {
//					final PopupPanel popup = new PopupPanel(false, true);
//					Label label = new Label(AON.MSG.processing());
//					label.addStyleName(AON.AON_CSS.aonTimer());
//					popup.add(label);
//					popup.setGlassEnabled(true);
//					popup.setAnimationEnabled(true);
					popup.center();
					try {
						// Primero borramos el modelo actual
						mod200Object.delete(new AsyncCallback<Void>() {
							
							@Override
							public void onSuccess(Void result) {
								// Si todo ha ido bien, creamos el nuevo modelo
								Model200.getMod2002020Service().createMod2002020(options.getOccam(), 2020
										, new AsyncCallback<Mod2002020>() {

											@Override
											public void onSuccess(Mod2002020 mod200) {
												popup.hide();
												mod200Callback.reset(options, mod200);
//												mod200Callback.onSelect(options, mod200);
												markAsDirty();
											}

											@Override
											public void onFailure(Throwable caught) {
												popup.hide();
												AonMessageDialog.error("No se ha podido inicializar el modelo.");
												mod200Callback.showError(caught.getMessage());
											}
										});							
							}
							
							@Override
							public void onFailure(Throwable caught) {
								popup.hide();
								AonMessageDialog.error("No se han podido borrar los datos.");								
								mod200Callback.showError(caught.getMessage());
							}
						});
					} catch (IllegalArgumentException e) {
						popup.hide();
						AonMessageDialog.error("No se han podido borrar los datos.");
						mod200Callback.showError(e.getMessage());
					}
				}
			});
		});
		toolbarPanel.add(resetButton);
		
		// Importar XML con información contable (formato AEAT)  
//		@UiHandler("importAccountingButton")
//		void onImportAccountingButtonClick(ClickEvent event) {
			
		importAccountingButton = new AonToolbarButton(AON.MSG.importAccounting(), AON.CSS.aonIconUpload());
		importAccountingButton.addClickHandler(event -> {
			importAccountingButton.setEnabled(false);
			mod200Callback.cleanErrorPanel();			
			Upload upload = new Upload() {
				
				@Override
				protected void onUpload(String data) {
					mod200Object.fillMod2002020AccountingData(options.getDomainName(), options.getDomain(), options.getUser(), data, new AsyncCallback<Mod2002020>() {
						@Override public void onSuccess(Mod2002020 result) {	
							importAccountingButton.setEnabled(true);
							markAsDirty();
						}
						@Override public void onFailure(Throwable caught) {
							importAccountingButton.setEnabled(true);
							mod200Callback.showError(caught.getMessage());
						}
					});				
				}
			};
			upload.upload();
		});
		toolbarPanel.add(importAccountingButton);
		
		// Exportar XML con información contable (formato AEAT)
		
//		@UiHandler("aeatAccountingFileButton")  // Generar XML con información contable
//		void onAeatAccountingFileButtonClick(ClickEvent event) {
			
		aeatAccountingFileButton = new AonToolbarButton(AON.MSG.aeatAccountingFile(), AON.CSS.aonIconDownload());
		aeatAccountingFileButton.addClickHandler(event -> {
			mod200Callback.cleanErrorPanel();
			
			AonConfirmDialog cd = new AonConfirmDialog();
			cd.confirm("Se va a proceder a la generaci\u00F3n de un fichero\n"
					+ "con los datos contables, para su importaci\u00F3n en\n"
					+ "el programa de ayuda de la Agencia Tributaria.\n\n"
					+ "Aseg\u00FArese de haber guardado la declaraci\u00F3n.\n\n"
					+ "El fichero se genera a partir de los datos guardados.",
				new AonConfirmDialogCallback() {
					
					@Override
					public void onCancel() {}
					
					@Override
					public void onAccept() {
						submitForm("/aon_gwt_mod200/ms/Model2002020AccountingFile");
					}
				}
			);
		});
		toolbarPanel.add(aeatAccountingFileButton);
		
		// Los botones de Fichero AEAT y Borrador AEAT, se mueven a la pagina de Agencia Tributaria dentro de los links
		// POR AHORA SE PONEN AQUI HASTA QUE SE DESARROLLE LO QUE REQUIERE LA PAGINA DE LA AGENCIA TRIBUTARIA

//		@UiHandler("aeatFileButton")  // Generar fichero modelo 200 para la presentación
//		void onAeatFileButtonClick(ClickEvent event) {
			
		aeatFileButton = new AonToolbarButton(AON.MSG.generateFile(), AON.CSS.aonIconAeat());
		aeatFileButton.addClickHandler(event -> {
			mod200Callback.cleanErrorPanel();
			
			AonConfirmDialog cd = new AonConfirmDialog();
			cd.confirm("Se va a proceder a la generaci\u00F3n de un fichero\n"
					+ "con los datos de la declaraci\u00F3n, para su \n"
					+ "presentaci\u00F3n en la web de la Agencia Tributaria.\n\n"
					+ "Aseg\u00FArese de haber guardado la declaraci\u00F3n.\n\n"
					+ "El fichero se genera a partir de los datos guardados.",
				new AonConfirmDialogCallback() {
					
					@Override
					public void onCancel() {}
					
					@Override
					public void onAccept() {
						submitForm("/aon_gwt_mod200/ms/Model2002020File");
					}
				}
			);
		});
		toolbarPanel.add(aeatFileButton);

//		@UiHandler("aeatPrintButton")  // Invocación al Servicio de Validación y Prueba
//		void onAeatPrintButtonClick(ClickEvent event) {
		aeatPrintButton = new AonToolbarButton(AON.MSG.validatePrintViaAeat(), AON.CSS.aonIconAeatBw());
		aeatPrintButton.addClickHandler(event -> {
				mod200Callback.cleanErrorPanel();
			
			AonConfirmDialog cd = new AonConfirmDialog();
			cd.confirm("La impresi\u00F3n se genera en los servidores de la Agencia Tributaria "
					+ "y se realiza a partir de los datos guardados. En el caso de validaci\u00F3n "
					+ "correcta se devolver\u00E1 el documento PDF de la declarai\u00F3n. Aseg\u00FArese "
					+ "de haber guardado la declaraci\u00F3n."
				,new AonConfirmDialogCallback() {
					
					@Override
					public void onCancel() {}
					
					@Override
					public void onAccept() {
						submitForm("/aon_gwt_mod200/ms/Model2002020Print");
					}
				}
			);
		});
		toolbarPanel.add(aeatPrintButton);
		
		// FALTA - BOTON COMENTARIOS
		// FALTA - BOTON AUDITORIA
		
		// Marca "Cambios sin guardar"
		FlowPanel marksPanels = new FlowPanel();
		marksPanels.setStyleName(AON.CSS.aonFlexBlock());
		
		dirtyLabel.setStyleName(AON.CSS.aonIconLabel());
		dirtyLabel.addStyleName(AON.CSS.aonIconDirty());
		dirtyLabel.setTitle("Cambios sin guardar");
		styleDirtyLabel();
		marksPanels.add(dirtyLabel);

		toolbarPanel.getMessagePanel().add(marksPanels);		

		toolbarPanel.add(diskForm);		
		return toolbarPanel;
	}
	
	private Widget getLinksPanel() {
		ScrollPanel scrollPanel = new ScrollPanel(); 
		scrollPanel.setStyleName(AON.CSS.aonBorderRight());
		
		FlowPanel linkContainer = new FlowPanel();
		linkContainer.setStyleName(AON.AON_CSS.aonPaddingLeft());
		linkContainer.setStyleName(AON.AON_CSS.aonPaddingRight());
		 
		linkContainer.add(new WestFocusPanel( 1,AON.MSG.identification() + ", Estados de Cuentas, Personal Asalariado, Caracteres de la declaraci\u00F3n"	 ));
		linkContainer.add(new WestFocusPanel( 2,AON.MSG.administratorPage()	 ));
		linkContainer.add(new WestFocusPanel( 3,AON.MSG.participations2019() ));
		linkContainer.add(new WestFocusPanel( 4,AON.MSG.balanceActivo()		 ));
		linkContainer.add(new WestFocusPanel( 5,AON.MSG.balancePasivoAbbrv()	 ));
		linkContainer.add(new WestFocusPanel( 6,AON.MSG.pyg() 				 ));
		linkContainer.add(new WestFocusPanel( 7,AON.MSG.patrimonioIngresosAbbrv() ));
		linkContainer.add(new WestFocusPanel( 8,AON.MSG.patrimonioCambios()	 ));
		linkContainer.add(new WestFocusPanel( 9,AON.MSG.liquidacionI()		 ));
		linkContainer.add(new WestFocusPanel(10,AON.MSG.liquidacionII()		 ));
		linkContainer.add(new WestFocusPanel(11,AON.MSG.liquidacionIII()	 ));
		linkContainer.add(new WestFocusPanel(12,AON.MSG.liquidacionIV() 	 ));
		linkContainer.add(new WestFocusPanel(13,AON.MSG.liquidacionV() 	 	 ));
		linkContainer.add(new WestFocusPanel(14,AON.MSG.combinedTaxationAbbrv()	 ));
		linkContainer.add(new WestFocusPanel(15,"Aplicaci\u00F3n de resultados / Documentaci\u00F3n previa"));
		linkContainer.add(new WestFocusPanel(16,AON.MSG.deducibleLimitationAbbrv()));
		linkContainer.add(new WestFocusPanel(17,AON.MSG.page17()		 	 ));
		linkContainer.add(new WestFocusPanel(18,AON.MSG.page18()		 	 ));
		linkContainer.add(new WestFocusPanel(19,"U.T.E.S."));
		linkContainer.add(new WestFocusPanel(20,AON.MSG.bussinessAmount()	 ));
		linkContainer.add(new WestFocusPanel(21,AON.MSG.idDocument()		 ));		
		// FALTA - PAGINA AGENCIA TRIBUTARIA CON INFO, FICHERO Y BORRADOR - POR AHORA SE PONEN 
		// LOS BOTONES COMO ESTABAN ANTES, PUES EN LOS OTROS MODELOS SE ESTA LLAMANDO A UNA CLASE
		// DE AON-GWT-FISCAL LA CUAL LLAMA A VARIAS CLASES DEL MISMO PROYECTO, ADEMAS SE REQUIERE
		// QUE YA ESTE DESAROLLADO LO DEL ESTADO DEL MODELO (FINALIZADO, ENVIADO, ETC..)
		
		scrollPanel.add(linkContainer);
//		if (pageSelected == -1) {
//			showContent(pageLinks, 0, new Page00New(cbk),false);
//		}
		return scrollPanel;
	}
	
//	private AonToolbar getDeclarationToolbarPanel(Mod2002020 mod200) {
//		AonToolbar decToolbar = new AonToolbar();
//		
//		// FALTA - LO DEL ESTADO AUN NO ESTÁ EN EL MODELO 200, ASI QUE POR AHORA NO SE PONEN LOS BOTONES QUE CAMBIAN EL ESTADO
//		
////		AonToolbarButton markAsFinishedButton = new AonToolbarButton(AON.MSG.finish(),AON.CSS.aonIconModelFinish());
////		markAsFinishedButton.setText(markAsFinishedButton.getTitle());
////		markAsFinishedButton.setVisible(!getModel().isNew() &&
////				(getModel().getStatus() == FiscalStatus.PENDING 
////				|| getModel().getStatus() == FiscalStatus.MISSING));
////		markAsFinishedButton.addClickHandler(event -> {
////			markAsFinishedButton.setEnabled(false);
////			MOD2002020_SERVICE.changeStatus(getCallback().getOptions().getOccam(),getModel(), FiscalStatus.FINISHED, new AsyncCallback<Mod2002020>() {
////				@Override
////				public void onSuccess(Mod2002020 result) {
////					getCallback().reload(result.getId());
////				}
////
////				@Override
////				public void onFailure(Throwable caught) {
////					markAsFinishedButton.setEnabled(true);
////					getCallback().showError(AON.MSG.unableToSaveDeclaration(caught.getMessage()));
////				}
////			});
////		});
////		decToolbar.add(markAsFinishedButton);
//
////		AonToolbarButton markAsSentButton = new AonToolbarButton(AON.MSG.markAsSent(),AON.CSS.aonIconModelSent());
////		markAsSentButton.setText(markAsSentButton.getTitle());
////		markAsSentButton.setVisible(!getModel().isNew() && (getModel().isFinished()));		
////		markAsSentButton.addClickHandler(event -> {
////			markAsSentButton.setEnabled(false);
////			MOD2002020_SERVICE.changeStatus(getCallback().getOptions().getOccam(),getModel(), FiscalStatus.SENT, new AsyncCallback<Mod2002020>() {
////				@Override
////				public void onSuccess(Mod2002020 result) {
////					getCallback().reload(result.getId());
////				}
////
////				@Override
////				public void onFailure(Throwable caught) {
////					markAsSentButton.setEnabled(true);
////					getCallback().showError(AON.MSG.unableToSaveDeclaration(caught.getMessage()));
////				}
////			});
////		});
////		decToolbar.add(markAsSentButton);
//		
////		AonToolbarButton markAsPendingButton = new AonToolbarButton(AON.MSG.reopen(),AON.CSS.aonIconModelReopen());
////		markAsPendingButton.setText(markAsPendingButton.getTitle());
////		markAsPendingButton.setVisible(!getModel().isNew() 
////				&& (getModel().isFinished() 
////				|| getModel().getStatus() == FiscalStatus.BATCHED 
////				|| getModel().isSent()));
////		markAsPendingButton.addClickHandler(event -> {
////			markAsPendingButton.setEnabled(false);
////			MOD2002020_SERVICE.changeStatus(getCallback().getOptions().getOccam(),getModel(), FiscalStatus.PENDING, new AsyncCallback<Mod2002020>() {
////				@Override
////				public void onSuccess(Mod2002020 result) {
////					getCallback().reload(result.getId());
////				}
////
////				@Override
////				public void onFailure(Throwable caught) {
////					markAsPendingButton.setEnabled(true);
////					getCallback().showError(AON.MSG.unableToSaveDeclaration(caught.getMessage()));
////				}
////			});
////		});
////		decToolbar.add(markAsPendingButton);
//
//		FlowPanel marksPanels = new FlowPanel();
//		marksPanels.setStyleName(AON.CSS.aonFlexBlock());
//
//		// LO DE COMPLEMENTARIA SE PUEDE VER EN LA PAGINA DE IDENTIFICACION ASI QUE TAMPOCO SE PONE
//		// ADEMAS SE PERMITE PONER O QUITAR LO DE LA COMPLEMENTARIA A DECISION DEL USUARIO
////		InlineLabel replacedLabel = new InlineLabel();		
////		if (getModel().isComplementary()) {
////			replacedLabel.setText(AON.MSG.complementary());
////			replacedLabel.setStyleName(AON.CSS.aonMarginLeft());
////			replacedLabel.addStyleName(AON.CSS.aonIconChecked());
////			replacedLabel.addStyleName(AON.CSS.aonLabelWithIcon());
////		}
////		marksPanels.add(replacedLabel);
//
//		dirtyLabel.setStyleName(AON.CSS.aonIconLabel());
//		dirtyLabel.addStyleName(AON.CSS.aonIconDirty());
//		dirtyLabel.setTitle("Cambios sin guardar");
//		styleDirtyLabel();
//		marksPanels.add(dirtyLabel);
//
//		decToolbar.getMessagePanel().add(marksPanels);
//		
//		// FALTA - LO DEL ESTADO NO SE USABA HASTA AHORA EN EL MODELO 200, O SEA QUE IGUAL AHORA NO SE DEBERIA PONER
//		// NADA AUN SI ESTA EN NULO... 
//		Label statusLabel = new Label();
//		statusLabel.setText(mod200.getStatus() == null?"PENDIENTE":mod200.getStatus().getName());
//		statusLabel.getElement().getStyle().setBackgroundColor(FiscalModelUtils.getStatusBckColorRGB( mod200.getStatus() ));
//		statusLabel.getElement().getStyle().setColor(FiscalModelUtils.getStatusFrgColorRGB( mod200.getStatus() ));
//		statusLabel.setStyleName(AON.CSS.aonToolbarTitle());
//		statusLabel.addStyleName(AON.CSS.aonPaddingLeft());
//		statusLabel.addStyleName(AON.CSS.aonPaddingRight());
//		statusLabel.addStyleName(AON.CSS.aonTextCenter());
//		statusLabel.addStyleName(AON.CSS.aonBorder());
//		statusLabel.addStyleName(AON.CSS.aonNowrap());
//		
//		decToolbar.setTitle(statusLabel);
//		return decToolbar;
//	}
	
	protected void styleDirtyLabel() {
		dirtyLabel.setVisible(isDirty());
	}
	
	protected void markAsDirty() {
		setDirty(true);
	}
	private boolean isDirty() {
		return this.dirty;
	}
	private void setDirty(boolean dirty) {
		this.dirty = dirty;
		styleDirtyLabel();
		refreshButtonsVisibility();
	}

	

}
