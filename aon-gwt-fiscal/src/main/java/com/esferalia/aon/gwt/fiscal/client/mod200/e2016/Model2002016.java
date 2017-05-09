package com.esferalia.aon.gwt.fiscal.client.mod200.e2016;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.i18n.DialogMessages;
import com.esferalia.aon.gwt.common.client.widget.BoxLabel;
import com.esferalia.aon.gwt.common.client.widget.DoubleBox;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel.MaximizeEvent;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel.MinimizeEvent;
import com.esferalia.aon.gwt.common.client.widget.ResultsPanel;
import com.esferalia.aon.gwt.fiscal.client.FiscalModelUtils;
import com.esferalia.aon.gwt.fiscal.client.mod200.Model200;
import com.esferalia.aon.gwt.fiscal.client.mod200.Model200.Model200Callback;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2016.ValidationMessage2016;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DeckLayoutPanel;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class Model2002016 extends ResizeComposite  {
	
	public static final int BOX_LENGTH = 5;
	
	final static int NOTIFICATIONS_TAB = 0;
	final static int INFORMATION_TAB = 1;

	
	interface Model2002016Binder extends
			UiBinder<Widget, Model2002016> {
	}

	private static final Model2002016Binder binder = GWT
			.create(Model2002016Binder.class);

	protected Mod2002016Object mod200Object;
	private Model200Callback mod200Callback;
	
	@UiField
	DockLayoutPanel dockLayoutPanel;
	@UiField
	SplitLayoutPanel splitLayoutPanel;
	
	@UiField
	SimplePanel headerPanel;
	
	@UiField
	DeckLayoutPanel deckPanel;
	
	@UiField
	Button initializeButton;
	@UiField
	Button importAccountingButton;
	@UiField
	Button saveButton;
	@UiField
	Button removeButton;
	@UiField
	Button cancelButton;
	@UiField
	Button validateButton;
	@UiField
	Button calculateButton;
	@UiField
	Button aeatAccountingFileButton;
	@UiField
	Button aeatFileButton;
	@UiField
	Button aeatPrintButton;
	@UiField
	CheckBox calculateCheck;

	@UiField
	SimplePanel page;
	@UiField
	Page00 page00;
	@UiField
	Page01 page01;
	@UiField
	Page02 page02;
	@UiField
	Page03 page03;
	@UiField
	Page04 page04;
	@UiField
	Page05 page05;
	@UiField
	Page06 page06;
	@UiField
	Page07 page07;
	@UiField
	Page08 page08;
	@UiField
	Page09 page09;
	@UiField
	Page10 page10;
	@UiField
	Page11 page11;
	@UiField
	Page12 page12;
	@UiField
	Page13 page13;
	@UiField
	Page14 page14;

	@UiField
	ScrollPanel linkList;
	
	@UiField
	TabLayoutPanel tabLayout;
	@UiField
	ResultsPanel resultsPanel;
	@UiField
	MinimizePanel footPanel;
	@UiField
	Label fiscalInformationLabel;
	@UiField
	ScrollPanel infoContainer;

	@UiField
	Panel formContainer;
	FormPanel diskForm;
	Hidden modIdHidden;
	Hidden domainIdHidden;
	Hidden domainNameHidden;

	ErrorPage errorPage;
	
	public Model2002016(Model200Callback mod200Callback) {
		this.mod200Callback = mod200Callback;
		Widget ui = binder.createAndBindUi(this);
		initWidget(ui);
		
		fillLinkContainer();
		errorPage = new ErrorPage();
		errorPage.addSelectionListener(this);
		
		tabLayout.setAnimationDuration(300);
		tabLayout.selectTab(NOTIFICATIONS_TAB);
		tabLayout.addSelectionHandler(new SelectionHandler<Integer>() {
			
			@Override
			public void onSelection(SelectionEvent<Integer> event) {
				openFootPanelIfNeeded();
			}
		});

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
		formContainer.add(diskForm);
		
	}
	
	private void fillInfo() {
		fiscalInformationLabel.setStyleName(AON.AON_CSS.aonPaddingRight());
		fiscalInformationLabel.addStyleName(AON.AON_CSS.aonPaddingLeft20());
		fiscalInformationLabel.addStyleName(FiscalModelUtils.getAdministrationIconBW(mod200Object.getMod200().getAdministration()));
		
		FlowPanel panel = new FlowPanel();
		panel.setStyleName(AON.AON_CSS.aonScrollArea());
		panel.add(FiscalModelUtils.getAnchorPanel( mod200Object.getMod200()
				,"Tr\u00E1mites."
				,"https://www.agenciatributaria.gob.es/AEAT.sede/tramitacion/GE04.shtml"));
		panel.add(FiscalModelUtils.getAnchorPanel( mod200Object.getMod200()
				,"Informaci\u00F3n general." 
				,"https://www.agenciatributaria.gob.es/AEAT.sede/Ayuda/GE04.shtml"));
		panel.add(FiscalModelUtils.getAnchorPanel(mod200Object.getMod200()
				,"Ficha."
				,"https://www.agenciatributaria.gob.es/AEAT.sede/procedimientos/GE04.shtml"));
		infoContainer.setWidget(panel);
	}

	public DeckLayoutPanel getDeckPanel() {
		return deckPanel;
	}
	public void addToDeckPanel(Widget w) {
		deckPanel.add(w);
	}

	protected void paintHeaderTable(final Mod2002016 mod200) {
		headerPanel.setStyleName(AON.AON_CSS.aonWidthAll());
		
		FlexTable headerTable = new FlexTable();
		headerTable.setStyleName(AON.AON_CSS.aonFiscalModelTable());
		
		Label image = new Label("");
		image.setStyleName(FiscalModelUtils.getAdministrationImage(mod200.getAdministration()));
		
		headerTable.setWidget(0, 0, image);
		headerTable.getFlexCellFormatter().setStyleName(0, 0, AON.AON_CSS.aonFiscalModelTableHeaderImage());
		headerTable.getFlexCellFormatter().setRowSpan(0, 0, 2);
		
		headerTable.setWidget(0, 1, new Label( AON.MSG.fiscalModelDescriptionlong(mod200.getModel())));
		headerTable.getFlexCellFormatter().setStyleName(0, 1, AON.AON_CSS.aonFiscalModelTableHeaderTitle());
		headerTable.getFlexCellFormatter().addStyleName(0, 1, FiscalModelUtils.getAdministrationBG(mod200.getAdministration()));
		headerTable.getFlexCellFormatter().setRowSpan(0, 1, 2);
		
		headerTable.setWidget(0, 2, new Label(mod200.getModel().getName()));
		headerTable.getFlexCellFormatter().setStyleName(0, 2, AON.AON_CSS.aonFiscalModelTableHeaderModel());
		headerTable.getFlexCellFormatter().addStyleName(0, 2, FiscalModelUtils.getAdministrationBG(mod200.getAdministration()));
		
		headerTable.setWidget(1, 0, new Label(""+mod200.getYear()));
		headerTable.getFlexCellFormatter().setStyleName(1, 0, AON.AON_CSS.aonFiscalModelTableHeaderModel());
		headerTable.getFlexCellFormatter().addStyleName(1, 0, FiscalModelUtils.getAdministrationBG(mod200.getAdministration()));
		
		headerPanel.setWidget(headerTable);
	}
	
	private class PopupAsyncCallback implements AsyncCallback<Mod2002016> {
		PopupPanel popup;
		public void setPopup(PopupPanel popup) {
			this.popup = popup;
		}

		@Override
		public void onSuccess(Mod2002016 result) {
			popup.hide();
		}

		@Override
		public void onFailure(Throwable caught) {
			popup.hide();
		}
		
	}

	@UiHandler("initializeButton")
	void onInitializeClick(ClickEvent event) {
		final PopupPanel popup = new PopupPanel(false, true);
		Label label = new Label(AON.MSG.processing());
		label.addStyleName(AON.AON_CSS.aonTimer());
		popup.add(label);
		popup.setGlassEnabled(true);
		popup.setAnimationEnabled(true);
		popup.center();
		page00.populate( mod200Object );
		mod200Object.initializeMod200(new AsyncCallback<Mod2002016>() {
			@Override
			public void onSuccess(Mod2002016 result) {
				popup.hide();
				dump();
			}
			
			@Override
			public void onFailure(Throwable e) {
				popup.hide();
				raiseException(e);
			}
		});
	}

	private void dump() {
		page00.dump(mod200Object);
		page01.dump(mod200Object);
		page02.dump(mod200Object);
		page03.dump(mod200Object);
		page04.dump(mod200Object);
		page05.dump(mod200Object);
		page06.dump(mod200Object);
		page07.dump(mod200Object);
		page08.dump(mod200Object);
		page09.dump(mod200Object);
		page10.dump(mod200Object);
		page11.dump(mod200Object);
		page12.dump(mod200Object);
		page13.dump(mod200Object);
		page14.dump(mod200Object);
		deckPanel.showWidget(deckPanel.getWidgetIndex(page00));
		
		refreshButtonsVisibility();
		page00.enableCharacters( false );
	}
	
	private void refreshButtonsVisibility() {
		initializeButton.setVisible(!mod200Object.isInitialized());
		importAccountingButton.setVisible(mod200Object.isInitialized());
		saveButton.setVisible(mod200Object.isInitialized());
		removeButton.setVisible(mod200Object.isInitialized());
		cancelButton.setVisible(true);
		validateButton.setVisible(mod200Object.isInitialized() && mod200Object.getMod200().getId() != null);
		calculateCheck.setVisible(mod200Object.isInitialized() && mod200Object.getMod200().getId() != null);
		calculateButton.setVisible(mod200Object.isInitialized() && mod200Object.getMod200().getId() != null 
								&& !calculateCheck.isVisible());
		aeatAccountingFileButton.setVisible(mod200Object.isInitialized() && mod200Object.getMod200().getId() != null);
		aeatFileButton.setVisible(mod200Object.isInitialized() && mod200Object.getMod200().getId() != null);
		aeatPrintButton.setVisible(mod200Object.isInitialized() && mod200Object.getMod200().getId() != null);
	}
	
	protected void raiseException(Throwable t) {
		errorPage.addErrorMsg(t);
		resultsPanel.setWidget(errorPage);
		showResultsPanel();
	}
	
	public void startModel(final Mod2002016Object modObject ) {
		mod200Object = modObject;
		fillInfo();
		final PopupPanel popup = new PopupPanel(false, true);
		Label label = new Label(AON.MSG.processing());
		label.addStyleName(AON.AON_CSS.aonTimer());
		popup.add(label);
		popup.setGlassEnabled(true);
		popup.setAnimationEnabled(true);
		popup.center();
		paintHeaderTable(mod200Object.getMod200());				
		calculateCheck.setValue(mod200Object.isAuthomaticCalculation());
		deckPanel.showWidget(deckPanel.getWidgetIndex(page00));
		if (mod200Object.getMod200().getId() == null) {
			page00.enableCharacters( true );
			page00.dump(modObject);
		} else {
			dump();
		}
		refreshButtonsVisibility();
		popup.hide();
	}
	
	@UiHandler("saveButton")
	void onSaveButtonClick(ClickEvent event) {
		PopupAsyncCallback callback = new PopupAsyncCallback(){
			@Override
			public void onSuccess(Mod2002016 result) {
				super.onSuccess(result);
				refreshButtonsVisibility();
			}
			@Override
			public void onFailure(Throwable caught) {
				super.onFailure(caught);
				Window.alert("No se han podido guardar los datos. \n"
						+"Causa: \n" 
						+ caught.getMessage());
			}
		};
		final PopupPanel popup = new PopupPanel(false, true);
		Label label = new Label(AON.MSG.processing());
		label.addStyleName(AON.AON_CSS.aonTimer());
		popup.add(label);
		popup.setGlassEnabled(true);
		popup.setAnimationEnabled(true);
		popup.center();
		callback.setPopup(popup);
		populatePages(mod200Object);
		try {
			mod200Object.save(callback);
		} catch (IllegalArgumentException e) {
			refreshButtonsVisibility();
			popup.hide();
			DialogMessages.alertErrorWidget(e.getMessage()).center();
		}
	}
	@UiHandler("cancelButton")
	void onCancelButtonClick(ClickEvent event) {
		if (Window.confirm(AON.MSG.cancelAction())) {
			mod200Callback.canceled();
		}
	}
	
	@UiHandler("removeButton")
	void onRemoveButtonClick(ClickEvent event) {
		if (Window.confirm(AON.MSG.confirmDeleteAction())) {
			final PopupPanel popup = new PopupPanel(false, true);
			Label label = new Label(AON.MSG.processing());
			label.addStyleName(AON.AON_CSS.aonTimer());
			popup.add(label);
			popup.setGlassEnabled(true);
			popup.setAnimationEnabled(true);
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
						Window.alert("No se han podido borrar los datos. \n"
								+"Causa: \n" 
								+ caught.getMessage());
					}
				});
			} catch (IllegalArgumentException e) {
				popup.hide();
				DialogMessages.alertErrorWidget(e.getMessage()).center();
			}
		}
	}

	private void populatePages(Mod2002016Object mod200Object) {
		page00.populate(mod200Object);
		page01.populate(mod200Object);
		page02.populate(mod200Object);
		page12.populate(mod200Object);
		page14.populate(mod200Object);
	}
	
	@UiHandler("validateButton")
	void onValidateButton(ClickEvent event) {
		validate(new PopupAsyncCallback() {
			@Override
			public void onSuccess(Mod2002016 result) {
				super.onSuccess(result);
				if (result.getMessages() != null && !result.getMessages().isEmpty()) {
					errorPage.addErrorMsg( result.getMessages() );
					resultsPanel.setWidget(errorPage);
					showResultsPanel();
				} else {
					Window.alert(AON.MSG.noValidationMessages() );
				}
			}

			@Override
			public void onFailure(Throwable caught) {
				super.onFailure(caught);
				errorPage.addErrorMsg(caught);
				resultsPanel.setWidget(errorPage);
				showResultsPanel();
			}
			
		});
	}

	private void validate(PopupAsyncCallback callback) {
		final PopupPanel popup = new PopupPanel(false, true);
		Label label = new Label(AON.MSG.processing());
		label.addStyleName(AON.AON_CSS.aonTimer());
		popup.add(label);
		popup.setGlassEnabled(true);
		popup.setAnimationEnabled(true);
		popup.center();
		callback.setPopup(popup);
		populatePages(mod200Object);
		mod200Object.validate(callback);
	}


	// -------------------------------------------------------------- UiHandler
	/*
	@UiHandler("footPanel")
	void onFootMinimize(MinimizeEvent event) {
		closeFootPanel();
	}
	@UiHandler("footPanel")
	void onFootMaximize(MinimizeEvent event) {
	}

	private void closeFootPanel() {
		splitLayoutPanel.setWidgetSize(footPanel, 0);
	}

	private void maximizeFootPanel() {
		dockLayoutPanel.setWidgetSize(footPanel, 0);
	}

	private boolean isResultsPanelVisible() {
		return dockLayoutPanel.getWidgetSize(footPanel) > 0;
	}
	
	private void cleanErrorMessage() {
		resultsPanel.clearFlowPanel();
		resultsPanel.setWidget(new SimplePanel());
		closeFootPanel();
	}

	private void showErrorMessage(String msg) {
		showResultsPanel();
		addErrorMessage(msg);
	}

	private void addErrorMessage(String msg) {
		SimplePanel panel = new SimplePanel();
		Label label = new Label(msg);
		label.addStyleName("aon-icon-errorwarning");
		label.addStyleName("aon-message-error");
		label.addStyleName("aon-icon");
		panel.add(label);
		resultsPanel.setWidget(panel);
	}
*/
	public void validationMessageSelected(ValidationMessage2016 msg) {
		if ( msg.getPage() >= 0 ) {
			deckPanel.showWidget(msg.getPage() + 1);
			if (msg.getKey() != null) {
				PageAbs page = (PageAbs) deckPanel.getWidget(msg.getPage()  + 1);
				DoubleBox d = page.getInputs().get(msg.getKey());
				if (d != null) {
					d.setFocus(true);
				}
				BoxLabel l = page.getLabels().get(msg.getKey());
				if (l != null) {
					l.addErrorState(msg.getMessage());
				}
			}
		}
	}

	@UiHandler("aeatAccountingFileButton")
	void onAeatAccountingFileButtonClick(ClickEvent event) {
		Window.alert(
				  "Se va a proceder a la generaci\u00F3n de un fichero\n"
				+ "con los datos contables, para su importaci\u00F3n en\n"
				+ "el programa de ayuda de la Agencia Tributaria.\n\n"
				+ "Aseg\u00FArese de haber guardado la declaraci\u00F3n.\n\n"
				+ "El fichero se genera a partir de los datos guardados.");
		diskForm.setAction(GWT.getHostPageBaseURL()
				+ "/aon_gwt_fiscal/Model2002016AccountingFile");
		modIdHidden.setValue(String.valueOf(mod200Object.getMod200().getId()));
		domainIdHidden.setValue(String.valueOf(Model200.getCurrentDomain()));
		domainNameHidden.setValue(Model200.getCurrentDomainName());
		diskForm.submit();
	}

	@UiHandler("aeatFileButton")
	void onAeatFileButtonClick(ClickEvent event) {
		Window.alert(
				  "Se va a proceder a la generaci\u00F3n de un fichero\n"
				+ "con los datos de la declaraci\u00F3n, para su \n"
				+ "presentaci\u00F3n en la web de la Agencia Tributaria.\n\n"
				+ "Aseg\u00FArese de haber guardado la declaraci\u00F3n.\n\n"
				+ "El fichero se genera a partir de los datos guardados.");
		diskForm.setAction(GWT.getHostPageBaseURL()
				+ "/aon_gwt_fiscal/Model2002016File");
		modIdHidden.setValue(String.valueOf(mod200Object.getMod200().getId()));
		domainIdHidden.setValue(String.valueOf(Model200.getCurrentDomain()));
		domainNameHidden.setValue(Model200.getCurrentDomainName());
		diskForm.submit();
	}

	@UiHandler("aeatPrintButton")
	void onAeatPrintButtonClick(ClickEvent event) {
		Window.alert(
				  "Se va a proceder a la validaci\u00F3n en los servidores de la \n"
				+ "Agencia Tributaria. En el caso de validaci\u00F3n correcta,la Agencia \n"
				+ "Tributaria devolver\u00E1 un documento PDF borrador con la declarai\u00F3n\n\n"
				+ "Aseg\u00FArese de haber guardado la declaraci\u00F3n.\n\n"
				+ "La petici\u00F3n se genera a partir de los datos guardados.");
		diskForm.setAction(GWT.getHostPageBaseURL()
				+ "/aon_gwt_fiscal/Model2002016Print");
		modIdHidden.setValue(String.valueOf(mod200Object.getMod200().getId()));
		domainIdHidden.setValue(String.valueOf(Model200.getCurrentDomain()));
		domainNameHidden.setValue(Model200.getCurrentDomainName());
		diskForm.submit();
	}

	@UiHandler("calculateButton")
	void onCalculateButtonClick(ClickEvent event) {
		mod200Object.calculate();
	}
	@UiHandler("calculateCheck")
	void onCalculateCheckClick(ClickEvent event) {
		mod200Object.setAuthomaticCalculation(calculateCheck.getValue());
		calculateButton.setVisible(!calculateCheck.getValue());
		if (calculateCheck.getValue())
			mod200Object.calculate();
	}
	
	@UiHandler("importAccountingButton")
	void onImportAccountingButtonClick(ClickEvent event) {
		UploadDialog ud = new UploadDialog(AON.MSG.importAccounting(),GWT.getModuleBaseURL() +"Mod2002016AccountingUpload") {
			
			@Override
			protected void onCancel() {
				hide();
			}
			
			@Override
			protected void onAccept() {
				mod200Object.fillMod2002016AccountingData(new AsyncCallback<Mod2002016>() {
					@Override
					public void onSuccess(Mod2002016 result) {
						hide();
					}
					
					@Override
					public void onFailure(Throwable e) {
						hide();
						raiseException(e);
					}
				});
			}
		};
		ud.addStyleName("gwt-PopupPanel-template");
		ud.setGlassEnabled(true);
		ud.center();
		ud.show();
	}
	
	private class WestFocusPanel extends FocusPanel {
		
		public WestFocusPanel(int page, String label, final PageAbs content, final boolean check) {
			super();
			setStyleName(AON.AON_CSS.aonLinkItem());
			FlexTable focTab = new FlexTable();
			focTab.setCellPadding(0);
			focTab.setCellSpacing(0);
			focTab.setStyleName(AON.AON_CSS.aonWidthAll());
			focTab.getColumnFormatter().setWidth(0, "30px");
			focTab.getColumnFormatter().setWidth(1, "auto");
			
			focTab.setWidget(0, 0, new InlineLabel(AonNumberUtils.toString(page)));
			focTab.getCellFormatter().setStyleName(0, 0, AON.AON_CSS.aonLinkListItem());
			focTab.getCellFormatter().addStyleName(0, 0, AON.AON_CSS.aonTextCenter());
			focTab.getCellFormatter().addStyleName(0, 0, AON.AON_CSS.aonColorWhite());
			focTab.getCellFormatter().addStyleName(0, 0, AON.AON_CSS.aonBold());
			
			focTab.setWidget(0, 1, new InlineLabel(label));
			focTab.getCellFormatter().setStyleName(0, 1, AON.AON_CSS.aonLinkListItem());
			focTab.getCellFormatter().addStyleName(0, 1, AON.AON_CSS.aonColorWhite());
			
			setWidget(focTab);
			addClickHandler( new  ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					if (check) {
						checkAndShowPage(content);	
					} else {
						showPage(content);
					}
					
				}
			});
		}
		private void showPage(PageAbs content) {
			FlowPanel parent = 	(FlowPanel) getParent();
			for (int i = 0 ; i < parent.getWidgetCount(); i ++) {
				parent.getWidget(i).removeStyleName(AON.AON_CSS.aonLinkItemSelected());
			}
			deckPanel.showWidget(deckPanel.getWidgetIndex(content));
			addStyleName(AON.AON_CSS.aonLinkItemSelected());
		}
		private void checkAndShowPage(PageAbs content) {
			if (mod200Object.isInitialized()) {
				showPage(content);
			} else {
				Window.alert( AON.MSG.mustInitialzeMod200());
			}
		}
	}


	private void fillLinkContainer() {
		FlowPanel linkContainer = new FlowPanel();
		linkContainer.setStyleName(AON.AON_CSS.aonPaddingLeft());
		linkContainer.setStyleName(AON.AON_CSS.aonPaddingRight());
		// En el método showPage, hay un cast a FlowPanel. Cuidado con la estrutura. 
		linkContainer.add(new WestFocusPanel( 1,AON.MSG.identification()	, page00, false));
		linkContainer.add(new WestFocusPanel( 2,AON.MSG.administratorPage()	, page01, true));
		linkContainer.add(new WestFocusPanel( 3,AON.MSG.participations()	, page02, true));
		linkContainer.add(new WestFocusPanel( 4,AON.MSG.balanceActivo()		, page03, true));
		linkContainer.add(new WestFocusPanel( 5,AON.MSG.balancePasivo()		, page04, true));
		linkContainer.add(new WestFocusPanel( 6,AON.MSG.pyg() 				, page05, true));
		linkContainer.add(new WestFocusPanel( 7,AON.MSG.patrimonioIngresos(), page06, true));
		linkContainer.add(new WestFocusPanel( 8,AON.MSG.patrimonioCambios()	, page07, true));
		linkContainer.add(new WestFocusPanel( 9,AON.MSG.liquidacionI()		, page08, true));
		linkContainer.add(new WestFocusPanel(10,AON.MSG.liquidacionII()		, page09, true));
		linkContainer.add(new WestFocusPanel(11,AON.MSG.liquidacionIII()	, page10, true));
		linkContainer.add(new WestFocusPanel(12,AON.MSG.liquidacionIV() 	, page11, true));
		linkContainer.add(new WestFocusPanel(13,AON.MSG.incomeDistribution() , page12, true));
		linkContainer.add(new WestFocusPanel(14,AON.MSG.deducibleLimitation(), page13, true));
		linkContainer.add(new WestFocusPanel(15,AON.MSG.idDocument()		, page14, true));
		
		linkList.setWidget( linkContainer );
	}
	
	private void showResultsPanel() {
		openFootPanel();
	}

	@UiHandler("footPanel")
	void onFootMinimize(MinimizeEvent event) {
		closeFootPanel();
	}

	@UiHandler("footPanel")
	void onFootMaximize(MaximizeEvent event) {
		splitLayoutPanel.setWidgetSize(footPanel, Window.getClientHeight() / 2);
		splitLayoutPanel.animate(500);
	}

	private void closeFootPanel() {
		splitLayoutPanel.setWidgetSize(footPanel, 30);
		splitLayoutPanel.animate(500);
	}
	
	private void openFootPanel() {
		splitLayoutPanel.setWidgetSize(footPanel, Window.getClientHeight() / 4);
		splitLayoutPanel.animate(500);
	}
	private void openFootPanelIfNeeded() {
		if (splitLayoutPanel.getWidgetSize(footPanel) <= 50) {
			openFootPanel();
		}
	}
	
}
