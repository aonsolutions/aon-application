package com.esferalia.aon.gwt.fiscal.client.mod200.e2016;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.BoxLabel;
import com.esferalia.aon.gwt.common.client.widget.ConfirmDialog;
import com.esferalia.aon.gwt.common.client.widget.ConfirmDialog.ConfirmDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.DoubleBox;
import com.esferalia.aon.gwt.common.client.widget.MessageDialog;
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
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class Model2002016 extends ResizeComposite  {
	
	public static final int BOX_LENGTH = 5;
	
	final static int NOTIFICATIONS_TAB = 0;
	final static int INFORMATION_TAB = 1;

	protected interface Model200PageCallback {
		public Mod2002016Object getMod200Object();
	}
	
	private PageAbs[] PAGES = new PageAbs[20];
	private int P00 = 0;
	
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
	Button aeatAccountingFileButton;
	@UiField
	Button aeatFileButton;
	@UiField
	Button aeatPrintButton;

	@UiField
	SimpleLayoutPanel pageContainer;
	
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
//		GWT.setUncaughtExceptionHandler(new GWT.UncaughtExceptionHandler() {
//			public void onUncaughtException(Throwable e) {
//				raiseException(e);
//			}
//		});

		
		this.mod200Callback = mod200Callback;
		
		Widget ui = binder.createAndBindUi(this);
		initWidget(ui);
		
		fillLinkContainer();
		errorPage = new ErrorPage();
		errorPage.addSelectionHandler( new  SelectionHandler<ValidationMessage2016>() {
			
			@Override
			public void onSelection(SelectionEvent<ValidationMessage2016> event) {
				ValidationMessage2016 msg = event.getSelectedItem();
				if ( msg.getPage() >= 0 ) {
					boolean mustDump = getPage(msg.getPage()) == null;
					PageAbs page = ensurePage(msg.getPage(), new Model200PageCallback(){

						@Override
						public Mod2002016Object getMod200Object() {
							return mod200Object;
						}
						
					});
					if (mustDump) page.dump();
					pageContainer.setWidget(page);
					if (msg.getKey() != null) {
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
		});
		
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

//	public DeckLayoutPanel getDeckPanel() {
//		return deckPanel;
//	}
//	public void addToDeckPanel(Widget w) {
//		deckPanel.add(w);
//	}

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
		dump((mod200Object.getMod200().getId() == null) );
		popup.hide();
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
		getPage(P00).populate( );
		mod200Object.initializeMod200(new AsyncCallback<Mod2002016>() {
			@Override
			public void onSuccess(Mod2002016 result) {
				popup.hide();
				dump(false);
			}
			
			@Override
			public void onFailure(Throwable e) {
				popup.hide();
				raiseException(e);
			}
		});
	}

	private void dump( boolean charactersEnabled) {
		ensurePage(P00,new Model200PageCallback(){

			@Override
			public Mod2002016Object getMod200Object() {
				return mod200Object;
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
		cancelButton.setVisible(true);
		validateButton.setVisible(mod200Object.isInitialized() && mod200Object.getMod200().getId() != null);
		aeatAccountingFileButton.setVisible(mod200Object.isInitialized() && mod200Object.getMod200().getId() != null);
		aeatFileButton.setVisible(mod200Object.isInitialized() && mod200Object.getMod200().getId() != null);
		aeatPrintButton.setVisible(mod200Object.isInitialized() && mod200Object.getMod200().getId() != null);

//		saveButton.setVisible(false);
//		removeButton.setVisible(false);
//		aeatFileButton.setVisible(false);
//		aeatPrintButton.setVisible(false);
		
	}
	
	protected void raiseException(Throwable t) {
		errorPage.addErrorMsg(t);
		resultsPanel.setWidget(errorPage);
		showResultsPanel();
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
				MessageDialog.error("No se han podido guardar los datos.");
				errorPage.clearMessages();
				errorPage.addErrorMsg( caught.getMessage() );
				resultsPanel.setWidget(errorPage);
				showResultsPanel();
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
		populatePages();
		try {
			mod200Object.save(callback);
		} catch (IllegalArgumentException e) {
			refreshButtonsVisibility();
			popup.hide();
			MessageDialog.error(e.getMessage());
		}
	}
	@UiHandler("cancelButton")
	void onCancelButtonClick(ClickEvent event) {
		ConfirmDialog cd = new ConfirmDialog();
		cd.confirm(AON.MSG.cancelAction(), new ConfirmDialogCallback() {
			@Override 
			public void onCancel() {}		
			
			@Override 
			public void onAccept() {
				mod200Callback.canceled();
			}
		});
	}
	
	@UiHandler("removeButton")
	void onRemoveButtonClick(ClickEvent event) {
		ConfirmDialog cd = new ConfirmDialog();
		cd.confirm(AON.MSG.confirmDeleteAction(), new ConfirmDialogCallback() {

			@Override
			public void onCancel() {
			}

			@Override
			public void onAccept() {
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
							MessageDialog.error("No se han podido borrar los datos.");
						}
					});
				} catch (IllegalArgumentException e) {
					popup.hide();
					MessageDialog.error("No se han podido borrar los datos.");
				}
			}
		});
	}

	private void populatePages() {
		for (PageAbs page : PAGES) {
			if (page != null) page.populate();
		}
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
				} else {
					errorPage.clearMessages();
					errorPage.addInfoMsg( AON.MSG.noValidationMessages() );
				}
				resultsPanel.setWidget(errorPage);
				showResultsPanel();
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
		populatePages();
		mod200Object.validate(callback);
	}

	@UiHandler("aeatAccountingFileButton")
	void onAeatAccountingFileButtonClick(ClickEvent event) {
		ConfirmDialog cd = new ConfirmDialog();
		cd.confirm("Se va a proceder a la generaci\u00F3n de un fichero\n"
				+ "con los datos contables, para su importaci\u00F3n en\n"
				+ "el programa de ayuda de la Agencia Tributaria.\n\n"
				+ "Aseg\u00FArese de haber guardado la declaraci\u00F3n.\n\n"
				+ "El fichero se genera a partir de los datos guardados.",
			new ConfirmDialogCallback() {
				
				@Override
				public void onCancel() {}
				
				@Override
				public void onAccept() {
					diskForm.setAction(GWT.getHostPageBaseURL()
							+ "/aon_gwt_fiscal/Model2002016AccountingFile");
					modIdHidden.setValue(String.valueOf(mod200Object.getMod200().getId()));
					domainIdHidden.setValue(String.valueOf(Model200.getCurrentDomain()));
					domainNameHidden.setValue(Model200.getCurrentDomainName());
					diskForm.submit();
				}
			}
		);
	}

	@UiHandler("aeatFileButton")
	void onAeatFileButtonClick(ClickEvent event) {
		ConfirmDialog cd = new ConfirmDialog();
		cd.confirm("Se va a proceder a la generaci\u00F3n de un fichero\n"
				+ "con los datos de la declaraci\u00F3n, para su \n"
				+ "presentaci\u00F3n en la web de la Agencia Tributaria.\n\n"
				+ "Aseg\u00FArese de haber guardado la declaraci\u00F3n.\n\n"
				+ "El fichero se genera a partir de los datos guardados.",
			new ConfirmDialogCallback() {
				
				@Override
				public void onCancel() {}
				
				@Override
				public void onAccept() {
					diskForm.setAction(GWT.getHostPageBaseURL()
							+ "/aon_gwt_fiscal/Model2002016File");
					modIdHidden.setValue(String.valueOf(mod200Object.getMod200().getId()));
					domainIdHidden.setValue(String.valueOf(Model200.getCurrentDomain()));
					domainNameHidden.setValue(Model200.getCurrentDomainName());
					diskForm.submit();
				}
			}
		);
	}

	@UiHandler("aeatPrintButton")
	void onAeatPrintButtonClick(ClickEvent event) {
		ConfirmDialog cd = new ConfirmDialog();
		cd.confirm("Se va a proceder a la validaci\u00F3n en los servidores de la \n"
				+ "Agencia Tributaria. En el caso de validaci\u00F3n correcta,la Agencia \n"
				+ "Tributaria devolver\u00E1 un documento PDF borrador con la declarai\u00F3n\n\n"
				+ "Aseg\u00FArese de haber guardado la declaraci\u00F3n.\n\n"
				+ "La petici\u00F3n se genera a partir de los datos guardados.",
			new ConfirmDialogCallback() {
				
				@Override
				public void onCancel() {}
				
				@Override
				public void onAccept() {
					diskForm.setAction(GWT.getHostPageBaseURL()
							+ "/aon_gwt_fiscal/Model2002016Print");
					modIdHidden.setValue(String.valueOf(mod200Object.getMod200().getId()));
					domainIdHidden.setValue(String.valueOf(Model200.getCurrentDomain()));
					domainNameHidden.setValue(Model200.getCurrentDomainName());
					diskForm.submit();
				}
			}
		);
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
		
		public WestFocusPanel(int pag, String label) {
			super();
			setStyleName(AON.AON_CSS.aonLinkItem());
			FlexTable focTab = new FlexTable();
			focTab.setCellPadding(0);
			focTab.setCellSpacing(0);
			focTab.setStyleName(AON.AON_CSS.aonWidthAll());
			focTab.getColumnFormatter().setWidth(0, "30px");
			focTab.getColumnFormatter().setWidth(1, "auto");
			
			focTab.setWidget(0, 0, new InlineLabel(AonNumberUtils.toString(pag)));
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
			for (int i = 0 ; i < parent.getWidgetCount(); i ++) {
				parent.getWidget(i).removeStyleName(AON.AON_CSS.aonLinkItemSelected());
			}
			PageAbs pageAbs = ensurePage(page, 
					new Model200PageCallback(){
						@Override 
						public Mod2002016Object getMod200Object() {
							return mod200Object;
						}
					});
			if (pageAbs.isAvailable()) {
				pageAbs.dump();
				pageContainer.setWidget(pageAbs);
				addStyleName(AON.AON_CSS.aonLinkItemSelected());
			} else {
				MessageDialog.warning(AON.MSG.pageNotAvailable());
			}
		}
		
		private void checkAndShowPage(int page) {

			if (mod200Object.isInitialized() ) {
				showPage(page);
			} else {
				MessageDialog.warning(AON.MSG.mustInitialzeMod200());
			}
		}
	}


	private void fillLinkContainer() {
		FlowPanel linkContainer = new FlowPanel();
		linkContainer.setStyleName(AON.AON_CSS.aonPaddingLeft());
		linkContainer.setStyleName(AON.AON_CSS.aonPaddingRight());
		// En el método showPage, hay un cast a FlowPanel. Cuidado con la estrutura. 
		linkContainer.add(new WestFocusPanel( 1,AON.MSG.identification()	 ));
		linkContainer.add(new WestFocusPanel( 2,AON.MSG.administratorPage()	 ));
		linkContainer.add(new WestFocusPanel( 3,AON.MSG.participations()	 ));
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
		linkContainer.add(new WestFocusPanel(15,AON.MSG.incomeDistribution() ));
		linkContainer.add(new WestFocusPanel(16,AON.MSG.deducibleLimitationAbbrv()));
		linkContainer.add(new WestFocusPanel(17,AON.MSG.page17()		 	 ));
		linkContainer.add(new WestFocusPanel(18,AON.MSG.page18()		 	 ));
		linkContainer.add(new WestFocusPanel(19,"U.T.E.S."));
		linkContainer.add(new WestFocusPanel(20,AON.MSG.bussinessAmount()	 ));
		linkContainer.add(new WestFocusPanel(21,AON.MSG.idDocument()		 ));
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

}
