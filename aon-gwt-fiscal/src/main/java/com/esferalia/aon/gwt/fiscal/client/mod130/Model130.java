package com.esferalia.aon.gwt.fiscal.client.mod130;

import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.common.client.widget.AonToast;
import com.esferalia.aon.gwt.common.client.widget.AuditDialog;
import com.esferalia.aon.gwt.common.client.widget.ConfirmDialog;
import com.esferalia.aon.gwt.common.client.widget.ConfirmDialog.ConfirmDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel.MaximizeEvent;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel.MinimizeEvent;
import com.esferalia.aon.gwt.common.client.widget.ResultsPanel;
import com.esferalia.aon.gwt.fiscal.client.FiscalModelUtils;
import com.esferalia.aon.gwt.fiscal.client.MainEntryPoint;
import com.esferalia.aon.gwt.fiscal.client.model.AonFiscalModelHeader;
import com.esferalia.aon.gwt.fiscal.client.model.FinishDeclarationPopup;
import com.esferalia.aon.gwt.fiscal.client.model.FinishDeclarationPopup.IFinishDeclarationPopupCallback;
import com.esferalia.aon.gwt.fiscal.client.model.FiscalModelIdentificationData;
import com.esferalia.aon.gwt.fiscal.client.model.FiscalModelProvidesKey;
import com.esferalia.aon.gwt.fiscal.client.model.FiscalModelTable;
import com.esferalia.aon.gwt.fiscal.client.model.IFiscalModelCallback;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod130;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.esferalia.aon.watson.util.Pair;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.AttachEvent.Handler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.logging.client.ConsoleLogHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DeckLayoutPanel;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.RangeChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent;

public class Model130 extends MainEntryPoint {

	private static final Logger LOGGER = Logger.getLogger(Model130.class.getName());
	static {
		LOGGER.addHandler( new ConsoleLogHandler() );
	}

	static final int IDENTIFICATION_TAB = 0;
	static final int LIQUIDATION_TAB = 1;
	
	static final int NOTIFICATIONS_TAB = 0;
	static final int INFORMATION_TAB = 1;
	static final int AEAT_TAB = 2;
	
	static final Mod130ServiceAsync SERVICE;
	private static final CommonServiceAsync COMMON_SERVICE;
	static {
		CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
		COMMON_SERVICE = new CommonServiceAsyncDecorator(commonServiceRaw); 
		
		Mod130ServiceAsync serviceRaw = GWT.create(Mod130Service.class);
		SERVICE = new Mod130ServiceAsyncDecorator(serviceRaw);
	}

	interface Model130Binder extends UiBinder<Widget, Model130> {
	}
	private static final Model130Binder MODEL_130_BINDER = GWT
			.create(Model130Binder.class);

	public static interface IMod130Declaration extends IsWidget {
		FlowPanel getDeclarationPanel();
		LinkedList<Pair<String, String>> getInformationLinks();
		void calculateAndRefresh(Model130Callback callback);
		void printButtonClick();
		HandlerRegistration addAttachHandler(Handler handler);
	}
	
	private Mod130 currentMod;
	private Model130ModuleOptions options;
	private boolean dirty;

	@UiField
	SplitLayoutPanel splitLayoutPanel;
	@UiField
	SimpleLayoutPanel headerPanel;
	@UiField
	DeckLayoutPanel deckPanel;
	@UiField
	Panel listPanel;
	@UiField
	DockLayoutPanel formPanel;
	@UiField
	TabLayoutPanel tabLayout;
	@UiField
	ResultsPanel resultsPanel;
	@UiField
	MinimizePanel footPanel;
	@UiField
	Label fiscalInformationLabel;
	@UiField
	ScrollPanel informationPanel;
	@UiField
	SimpleLayoutPanel aeatPanel;
	@UiField
	Panel formContainer;

	@UiField(provided = true)
	FiscalModelTable<Mod130> table;

	@UiField
	Button saveButton;
	@UiField
	Button deleteButton;
	@UiField
	Button resetButton;
	@UiField
	Button newButton;
	@UiField
	Button cancelButton;
	@UiField
	Button printButton;
	@UiField
	Button markAsPendingButton;
	@UiField
	Button markAsSentButton;	
	@UiField
	Button markAsFinishedButton;
	@UiField
	Button auditButton;

	@UiField
	InlineLabel documentLabel;
	@UiField
	InlineLabel nameLabel;
	@UiField
	InlineLabel surnameLabel;
	@UiField
	InlineLabel dirtyLabel;
	@UiField
	Label statusLabel;
	@UiField
	Label replacementLabel;
	@UiField
	Label complementaryLabel;
	@UiField
	TextBox replacedNumber;
	@UiField
	Label replacedNumberLabel;
	@UiField
	CheckBox confidential;
	@UiField
	Button commentsButton;
	
	@UiField
	FlowPanel paymentInfo;

	@UiField
	TabLayoutPanel tabPanel;
	@UiField
	ScrollPanel identificationContainer;
	@UiField
	ScrollPanel declarationContainer;
	
	private IMod130Declaration declaration;
	
	@UiField
	ScrollPanel infoContainer;

	protected class Model130Callback implements IFiscalModelCallback<Mod130,Model130ModuleOptions> {
		
		@Override
		public void showError(String msg) {
			showErrorMessage(msg);
		}
		
		@Override
		public void showInfoPanel(String htmlText) {
			openFootPanelIfNeeded();
			tabLayout.selectTab(INFORMATION_TAB);
			HTMLPanel panel = new HTMLPanel(htmlText);
			informationPanel.setWidget(panel);
			informationPanel.scrollToTop();
		}
		
		@Override
		public void cleanInfoPanel() {
			Model130.this.cleanInfo();
		}

		@Override
		public Model130ModuleOptions getOptions() {
			return Model130.this.options;
		}

		@Override
		public void onAccept(Mod130 model) {
			// Nothing
		}

		@Override
		public void onCancel(Mod130 model) {
			// Nothing
			
		}

		@Override
		public void onRemove(Mod130 model) {
			// Nothing
			
		}

		@Override
		public void onNew() {
			// Nothing
			
		}

		@Override
		public void onReset(Mod130 oldModel) {
			// Nothing
		}
		public boolean isFinished() {
			return (currentMod.getStatus() == FiscalStatus.FINISHED);
		}
		public boolean isDirty() {
			return Model130.this.isDirty();
		}
		public void markAsDirty() {
			if (!isDirty()) {
				Model130.this.setDirty(true);
			}
		}
		public void identificationLabelChanged() {
			documentLabel.setText(currentMod.getDocument());
			nameLabel.setText(currentMod.getName());
			surnameLabel.setText(currentMod.getSurname());
		}

		public void onRefreshTable(AsyncCallback<LinkedList<Mod130>> cbk) {
			SERVICE.getMod130s(Model130.this.getOptions().getOccam(), cbk );
		}

	}

	@Override
	public void onModuleLoad() {
		COMMON_SERVICE.getAonConfiguration(getCurrentDomainName(), getCurrentDomain(), getCurrentUser(), new AsyncCallback<AonConfiguration>() {
			
			@Override
			public void onSuccess(AonConfiguration config) {
				RootLayoutPanel root = RootLayoutPanel.get(getRootPanel() != null ? getRootPanel() : "rootPanel");
				Model130ModuleOptions opts = new Model130ModuleOptions();
				opts.setParentWidget(root);
				opts.setDomainName(getCurrentDomainName());
				opts.setDomain(getCurrentDomain());
				opts.setUser(getCurrentUser());
				opts.setConfiguration(config);
				onModuleLoad( opts );
			}
			
			@Override public void onFailure(Throwable caught) {
				Window.alert( "Error al cargar el module" );
			}
		});
	}
	private Model130ModuleOptions getOptions() {
		if (this.options == null) {
			this.options = new Model130ModuleOptions();
		}
		return this.options;
	}
	
	public void onModuleLoad(Model130ModuleOptions options) {
		this.options = options;
		GWT.setUncaughtExceptionHandler(e -> LOGGER.log(Level.SEVERE,"No caught!",e));
		
		AON.ensureInjected();

		table = new FiscalModelTable<>(new Mod130SelectionHandler(), new FiscalModelProvidesKey<>());

		Widget ui = MODEL_130_BINDER.createAndBindUi(this);
		if (this.options.isBackButtonVisible() && this.options.hasExternalCallback()) {
			cancelButton.setText(AON.MSG.backAction());
		}

		HTMLPanel html = new HTMLPanel("<iframe name='aeatForm' width='100%' height='100%' style='border:none'/>");
		html.setWidth("100%");
		html.setHeight("100%");
		aeatPanel.setWidget(html);
		
		replacedNumber.setVisibleLength(13);
		replacedNumber.setMaxLength(13);

		getOptions().getParentWidget().add(ui);
		
		// http://code.google.com/p/google-web-toolkit/issues/detail?id=6889
		deckPanel.onResize();
		
		if (getOptions().getFiscalModelId() != null ) {
			LOGGER.info("Access to Model130 with a ID: " + getOptions().getFiscalModelId());
			onSelect(getOptions().getFiscalModelId());
		} else if (getOptions().getNewModel() != null ) {
			LOGGER.info("Access to Model130 new Model");
			newModel(getOptions().getNewModel()); 
		} else {
			table.setVisibleRangeAndClearData(table.getVisibleRange(), true);
			LOGGER.info("Model130 setting NOTIFICATIONS_TAB");
			tabLayout.selectTab(NOTIFICATIONS_TAB);
		}
		
		tabLayout.setAnimationDuration(300);
		tabLayout.addSelectionHandler(event -> openFootPanelIfNeeded());
	}

	private void onSelect(Integer id ) {
		LOGGER.info("OnSelect Model130 with a ID: " + getOptions().getFiscalModelId());
		SERVICE.getMod130(getOptions().getOccam(), id , new AsyncCallback<Mod130>() {
					@Override
					public void onSuccess(Mod130 selected) {
						if (selected == null) {
							LOGGER.info("onSuccess Model130 with a NULL selected Model ID: ");
							showErrorMessage(AON.MSG.unableToFindDeclaration());
						} else {
							LOGGER.info("onSuccess Model130 with a ID: " + selected.getId());
							select(selected);
						}
					}

					@Override
					public void onFailure(Throwable caught) {
						showErrorMessage(AON.MSG.unableToReadDeclaration(caught.getMessage()));
					}
				});
	}
	
	class Mod130SelectionHandler implements SelectionChangeEvent.Handler {
		@Override
		public void onSelectionChange(SelectionChangeEvent event) {
			Mod130 sel = table.getSelected();
			SERVICE.getMod130(getOptions().getOccam(),
					sel.getId(), new AsyncCallback<Mod130>() {
						@Override
						public void onSuccess(Mod130 selected) {
							if (selected == null) {
								showErrorMessage(AON.MSG.unableToFindDeclaration());
							} else {
								select(selected);
								tabPanel.selectTab(LIQUIDATION_TAB);
								int i = deckPanel.getWidgetIndex(formPanel);
								deckPanel.showWidget(i);
								cleanErrorMessage();
							}
						}

						@Override
						public void onFailure(Throwable caught) {
							showErrorMessage(AON.MSG.unableToReadDeclaration(caught.getMessage()));
						}
					});
		}
	}
	public boolean isDirty() {
		return dirty;
	}
	public void setDirty(boolean dirty) {
		this.dirty = dirty;
		styleDirtyLabel();
	}

	private void newModel(Mod130 newModel) {
		SERVICE.initialize(getOptions().getOccam(),newModel,
				new AsyncCallback<Mod130>() {
					@Override
					public void onSuccess(Mod130 m130) {
						currentMod = m130;
						cleanInfo();
						tabLayout.selectTab(INFORMATION_TAB);
						closeFootPanel();
						showNewDeclarationPopup();
						newButton.setEnabled(true);
					}


					@Override
					public void onFailure(Throwable caught) {
						showErrorMessage(AON.MSG.unableToReadFiscalParameters(caught.getMessage()));
						newButton.setEnabled(true);
					}
				});
	}

	private void refreshToolbarState() {
		LOGGER.info("Model130 refreshToolbarState! ");
		newButton.setVisible(!currentMod.isNew() && !this.options.isBackButtonVisible() && !this.options.hasExternalCallback());
		saveButton.setVisible(!currentMod.isFinished() && !currentMod.isSent());
		cancelButton.setVisible(true);
		deleteButton.setVisible(!currentMod.isNew() && !currentMod.isFinished() && !currentMod.isSent());
		resetButton.setVisible(!currentMod.isNew() && !currentMod.isFinished() && !currentMod.isSent());
		printButton.setVisible(!currentMod.isNew());
		markAsPendingButton.setVisible(!currentMod.isNew() &&
				(currentMod.getStatus() == FiscalStatus.FINISHED 
				|| currentMod.getStatus() == FiscalStatus.BATCHED
				|| currentMod.getStatus() == FiscalStatus.CUSTOMER_CHECK
				|| currentMod.getStatus() == FiscalStatus.SENT
				|| currentMod.getStatus() == FiscalStatus.BLOCKED));
		markAsSentButton.setVisible(!currentMod.isNew() &&
				(currentMod.getStatus() == FiscalStatus.FINISHED));
		markAsFinishedButton.setVisible(!currentMod.isNew() &&
				(currentMod.getStatus() == FiscalStatus.PENDING 
				|| currentMod.getStatus() == FiscalStatus.CUSTOMER_CHECK
				|| currentMod.getStatus() == FiscalStatus.MISSING));
		auditButton.setVisible(!currentMod.isNew());
	}
	
	private void toolbarForTable() {
		hideToolbarButtons();
		cancelButton.setVisible(false);
	}
	
	private void hideToolbarButtons() {
		deleteButton.setVisible(false);
		resetButton.setVisible(false);
		auditButton.setVisible(false);
		newButton.setVisible(true);
		cancelButton.setVisible(true);
		saveButton.setVisible(false);
		printButton.setVisible(false);
		markAsPendingButton.setVisible(false);
		markAsSentButton.setVisible(false);
		markAsFinishedButton.setVisible(false);
	}
	
	private void select(Mod130 selected) {
		currentMod = selected;
		dirty = false;
		
		documentLabel.setText(currentMod.getDocument());
		nameLabel.setText(currentMod.getName());
		surnameLabel.setText(currentMod.getSurname());
		
		styleDirtyLabel();
		styleStatusLabel();
		if (currentMod.isReplacementDeclarationAvailable()) {
			replacementLabel.setText(AON.MSG.replacement());
			replacementLabel.setStyleName(AON.AON_CSS.aonIconPaddingLeft());
			replacementLabel.addStyleName(currentMod.isReplacement()
					?AON.AON_CSS.aonIconChecked()
					:AON.AON_CSS.aonIconCheck()
				);
		} else {
			replacementLabel.setText("");
		}
		
		if (currentMod.isComplementaryDeclarationAvailable()) {
			complementaryLabel.setText(AON.MSG.complementary());
			complementaryLabel.setStyleName(AON.AON_CSS.aonIconPaddingLeft());
			complementaryLabel.addStyleName(currentMod.isComplementary()
					?AON.AON_CSS.aonIconChecked()
					:AON.AON_CSS.aonIconCheck()
				);
		} else {
			complementaryLabel.setText("");
		}
		
		replacedNumber.setValue(currentMod.getReplacedNumber());
		replacedNumber.setEnabled(currentMod.isReplacedNumberAvailable());
		
		confidential.setValue(currentMod.isConfidential());
		
		styleCommentsButton();
		
		fiscalInformationLabel.setStyleName(AON.AON_CSS.aonPaddingRight());
		fiscalInformationLabel.addStyleName(AON.AON_CSS.aonPaddingLeft20());
		fiscalInformationLabel.addStyleName(FiscalModelUtils.getAdministrationBackgroundStyle(currentMod.getAdministration()));

		refreshToolbarState();
		
		FiscalModelUtils.fillPaymentInfo(paymentInfo,currentMod);
		headerPanel.setWidget( new AonFiscalModelHeader(currentMod) );
		Model130Callback callback = new Model130Callback(); 
		FiscalModelIdentificationData<Mod130> identificationData = new FiscalModelIdentificationData<>(currentMod);
		identificationData.addValueChangeHandler(event -> {
			callback.identificationLabelChanged();
			callback.markAsDirty();
		});
		identificationContainer.setWidget( identificationData);
		if (currentMod.getAdministration() == Administration.COMMON_TERRITORY) {
			declaration = new Model130AEAT(currentMod,callback);
		} else if (currentMod.getAdministration() == Administration.BIZKAIA) {
			declaration = new Model130Bizkaia(currentMod,callback);
		}
		if (declaration != null) {
			declaration.addAttachHandler(event -> {
				if (event.isAttached() ) {
					
					Scheduler.get().scheduleDeferred(() -> {
						LOGGER.info("Declaration Attached!");
						tabPanel.selectTab(LIQUIDATION_TAB);
						int i = deckPanel.getWidgetIndex(formPanel);
						deckPanel.showWidget(i);
						cleanErrorMessage();
						refreshToolbarState();
					});		
				}
			});
			declarationContainer.setWidget( declaration );
			infoContainer.setWidget(declaration.getDeclarationPanel());
		} else {
			showErrorMessage("Administraci\u00F3n y/o ejercicio no soportado.");
			hideToolbarButtons();
			cancelButton.setVisible(true);
		}
	}
	
	private void styleDirtyLabel() {
		dirtyLabel.setText(isDirty()?"[CAMBIOS]":"");
	}

	private void styleStatusLabel() {
		statusLabel.setText(currentMod.getStatus().getName());
		statusLabel.getElement().getStyle().setBackgroundColor(FiscalModelUtils.getStatusBckColorRGB( currentMod.getStatus() ));
		statusLabel.getElement().getStyle().setColor(FiscalModelUtils.getStatusFrgColorRGB( currentMod.getStatus() ));
		statusLabel.setStyleName(AON.CSS.aonToolbarTitle());
		statusLabel.addStyleName(AON.CSS.aonPaddingLeft());
		statusLabel.addStyleName(AON.CSS.aonPaddingRight());
		statusLabel.addStyleName(AON.CSS.aonTextCenter());
		statusLabel.addStyleName(AON.CSS.aonBorder());
		statusLabel.addStyleName(AON.CSS.aonNowrap());
	}

	@UiHandler("table")
	void onTableRangeChange(RangeChangeEvent event) {
		SERVICE.getMod130s(getOptions().getOccam(),
				new AsyncCallback<LinkedList<Mod130>>() {
					@Override
					public void onSuccess(LinkedList<Mod130> result) {
						int i = deckPanel.getWidgetIndex(listPanel);
						table.setRowData(result);
						deckPanel.showWidget(i);
						toolbarForTable();	
					}

					@Override
					public void onFailure(Throwable caught) {
						showErrorMessage(AON.MSG.unableToReadDeclaration(caught.getMessage()));
					}
				});
	}

	@UiHandler("saveButton")
	void onAcceptButtonClick(ClickEvent event) {
		save();
	}
	@UiHandler("markAsPendingButton")
	void markAsPendingButtonClick(ClickEvent event) {
		markAsPendingButton.setEnabled(false);
		cleanErrorMessage();
		final PopupPanel popup = new PopupPanel(false, true);
		Label label = new Label(AON.MSG.processing());
		label.addStyleName(AON.AON_CSS.aonTimer());
		popup.add(label);
		popup.setGlassEnabled(true);
		popup.setAnimationEnabled(true);
		popup.center();
		SERVICE.markAsPending(getOptions().getOccam(), this.currentMod, new AsyncCallback<Mod130>() {
					@Override
					public void onSuccess(Mod130 result) {
						select(result);
						popup.hide();
						markAsPendingButton.setEnabled(true);
						FiscalModelUtils.fillPaymentInfo(paymentInfo,currentMod);
					}

					@Override
					public void onFailure(Throwable caught) {
						popup.hide();
						showErrorMessage(AON.MSG.unableToReopenDeclaration(caught.getMessage()));
						markAsPendingButton.setEnabled(true);
					}
				});
	}
	@UiHandler("markAsSentButton")
	void markAsSentButtonClick(ClickEvent event) {
		markAsSentButton.setEnabled(false);
		cleanErrorMessage();
		SERVICE.markAsSent(getOptions().getOccam(), this.currentMod, new AsyncCallback<Mod130>() {
					@Override
					public void onSuccess(Mod130 result) {
						select(result);
						markAsSentButton.setEnabled(true);
					}

					@Override
					public void onFailure(Throwable caught) {
						showErrorMessage(AON.MSG.unableToMarkAsSentDeclaration(caught.getMessage()));
						markAsSentButton.setEnabled(true);
					}
				});
	}

	@UiHandler("markAsFinishedButton")
	void markAsFinishedButtonClick(ClickEvent event) {
		markAsFinishedButton.setEnabled(false);
		cleanErrorMessage();
		SERVICE.initializeForFinish(getOptions().getOccam(),currentMod,
				new AsyncCallback<Mod130>() {
					@Override
					public void onSuccess(Mod130 m130) {
						currentMod = m130;
						showFinalizePopup();
						markAsFinishedButton.setEnabled(true);
					}

					@Override
					public void onFailure(Throwable caught) {
						showErrorMessage(AON.MSG.unableToReadFiscalParameters(caught.getMessage()));
						markAsFinishedButton.setEnabled(true);
					}
				});
	}
	
	private void save() {
		saveButton.setEnabled(false);
		cleanErrorMessage();
		final PopupPanel popup = new PopupPanel(false, true);
		Label label = new Label(AON.MSG.processing());
		label.addStyleName(AON.AON_CSS.aonTimer());
		popup.add(label);
		popup.setGlassEnabled(true);
		popup.setAnimationEnabled(true);
		popup.center();
		SERVICE.save(getOptions().getOccam(), this.currentMod, new AsyncCallback<Mod130>() {
					@Override
					public void onSuccess(Mod130 result) {
						select(result);
						popup.hide();
						saveButton.setEnabled(true);
					}

					@Override
					public void onFailure(Throwable caught) {
						popup.hide();
						showErrorMessage(AON.MSG.unableToSaveDeclaration(caught.getMessage()));
						saveButton.setEnabled(true);
					}
				});
	}
	
	private void finish() {
		markAsFinishedButton.setEnabled(false);
		cleanErrorMessage();
		final PopupPanel popup = new PopupPanel(false, true);
		Label label = new Label(AON.MSG.processing());
		label.addStyleName(AON.AON_CSS.aonTimer());
		popup.add(label);
		popup.setGlassEnabled(true);
		popup.setAnimationEnabled(true);
		popup.center();
		SERVICE.markAsFinished(getOptions().getOccam(), this.currentMod, new AsyncCallback<Mod130>() {
					@Override
					public void onSuccess(Mod130 result) {
						select(result);
						popup.hide();
						markAsFinishedButton.setEnabled(true);
						FiscalModelUtils.fillPaymentInfo(paymentInfo,currentMod);
					}

					@Override
					public void onFailure(Throwable caught) {
						popup.hide();
						showErrorMessage(AON.MSG.unableToSaveDeclaration(caught.getMessage()));
						markAsFinishedButton.setEnabled(true);
					}
				});
	}

	@UiHandler("deleteButton")
	void onDeleteButtonClick(ClickEvent event) {
		deleteButton.setEnabled(false);
		ConfirmDialog cd = new ConfirmDialog();
		cd.confirm(AON.MSG.confirmDeclarationDeleteAction(), new ConfirmDialogCallback() {

			@Override
			public void onAccept() {
				SERVICE.delete(getOptions().getOccam(),currentMod, new AsyncCallback<Void>() {
					@Override
					public void onSuccess(Void result) {
						table.setVisibleRangeAndClearData(table.getVisibleRange(), true);
						deleteButton.setEnabled(true);
					}

					@Override
					public void onFailure(Throwable caught) {
						deleteButton.setEnabled(true);
						showErrorMessage(AON.MSG.unableToDeleteDeclaration(caught.getMessage()));
					}
				});
			}

			@Override
			public void onCancel() {
				deleteButton.setEnabled(true);
			}
		});
	}

	@UiHandler("newButton")
	void onNewButtonClick(ClickEvent event) {
		newButton.setEnabled(false);
		cleanErrorMessage();
		
		SERVICE.initialize(getOptions().getOccam(),null,
				new AsyncCallback<Mod130>() {
					@Override
					public void onSuccess(Mod130 m130) {
						currentMod = m130;
						showNewDeclarationPopup();
						newButton.setEnabled(true);
					}


					@Override
					public void onFailure(Throwable caught) {
						showErrorMessage(AON.MSG.unableToReadFiscalParameters(caught.getMessage()));
						newButton.setEnabled(true);
					}
				});
	}
	private void showNewDeclarationPopup() {
		Model130NewDeclarationPopup newDialog = new Model130NewDeclarationPopup(currentMod,
			new Model130Callback() {

				@Override
				public void onAccept(Mod130 mod130) {
					SERVICE.create(getOptions().getOccam(),mod130,
							new AsyncCallback<Mod130>() {
								@Override
								public void onSuccess(Mod130 m130) {
									int i = deckPanel.getWidgetIndex(formPanel);
									deckPanel.showWidget(i);
									tabPanel.selectTab(IDENTIFICATION_TAB);
									select(m130);
								}

								@Override
								public void onFailure(Throwable caught) {
									showErrorMessage(AON.MSG.unableToReadFiscalParameters(caught.getMessage()));
								}
							});
				}

				@Override
				public void onCancel(Mod130 mod130) {
					cancel();
				}
			}
		); 
		newDialog.center();
		newDialog.show();
	}

	@UiHandler("cancelButton")
	void onCancelButtonClick(ClickEvent event) {
		if (!isDirty()) {
			if (this.options.isBackButtonVisible() && this.options.hasExternalCallback()) {
				this.options.getExternalCallback().onExit(currentMod);
			} else {
				cancel();
			}
		} else {
			if (this.options.isBackButtonVisible() && this.options.hasExternalCallback()) {
				this.options.getExternalCallback().onExit(currentMod);
			} else {
				cancelButton.setEnabled(false);
				ConfirmDialog cd = new ConfirmDialog();
				cd.confirm(AON.MSG.confirmDeclarationCancelAction(), new ConfirmDialogCallback() {
	
					@Override
					public void onAccept() {
						cancel();
					}
	
					@Override
					public void onCancel() {
						cancelButton.setEnabled(true);
					}
				});
			}
		}
	}
	
	private void cancel() {
		cleanErrorMessage();
		cleanInfo();
		tabLayout.selectTab(INFORMATION_TAB);
		closeFootPanel();
		int i = deckPanel.getWidgetIndex(listPanel);
		deckPanel.showWidget(i);
		table.setVisibleRangeAndClearData(table.getVisibleRange(), true);
		cancelButton.setEnabled(true);
	}

	@UiHandler("auditButton")
	public void onAudit(ClickEvent event) {
		AuditDialog dialog = new AuditDialog();
		dialog.show(currentMod);
	}

	// -------------------------------------------------------------- UiHandler
	@UiHandler("commentsButton")
	public void onComments(ClickEvent event) {
		final AonToast toast = new AonToast();
		FlowPanel commentPanel = new FlowPanel();
		commentPanel.setStyleName( FiscalModelUtils.getAdministrationBackgroundStyle(currentMod.getAdministration()) );
		commentPanel.setStyleName(AON.AON_CSS.aonHeightAll());
		commentPanel.addStyleName(AON.AON_CSS.aonTextCenter());
		TextArea comment = new TextArea();
		comment.addValueChangeHandler(event1 -> {
			currentMod.setComments(event1.getValue());
			styleCommentsButton();
			SERVICE.saveComments(getOptions().getOccam(), currentMod, new AsyncCallback<Mod130>() {
				@Override
				public void onSuccess(Mod130 result) {
					toast.hide();
				}

				@Override
				public void onFailure(Throwable caught) {
					toast.hide();
					showErrorMessage(AON.MSG.unableToSaveDeclaration(caught.getMessage()));
				}
			});
		});
		comment.setText(currentMod.getComments());
		comment.setWidth("90%");
		comment.setHeight("5em");
		commentPanel.add(comment);
		toast.show(AON.MSG.comments(), commentPanel);
	}
	
	private void styleCommentsButton() {
		if (AonStringUtils.isEmpty(currentMod.getComments())) {
			commentsButton.addStyleName(AON.AON_CSS.aonIconComment());
			commentsButton.removeStyleName(AON.AON_CSS.aonIconCommentRed());
		} else {
			commentsButton.addStyleName(AON.AON_CSS.aonIconCommentRed());
			commentsButton.removeStyleName(AON.AON_CSS.aonIconComment());
		}
	}
	
	@UiHandler("printButton")
	void onPrintButtonClick(ClickEvent event) {
		declaration.printButtonClick();
	}

	@UiHandler("confidential")
	void onConfidentialClick(ClickEvent event) {
		currentMod.setConfidential(confidential.getValue());
		setDirty(true);
	}
	
	@UiHandler("replacedNumber")
	void onConfidentialChange(ChangeEvent event) {
		currentMod.setReplacedNumber(replacedNumber.getValue());
		setDirty(true);
	}
	
	
	@UiHandler("footPanel")
	void onFootMinimize(MinimizeEvent event) {
		closeFootPanel();
	}

	@UiHandler("footPanel")
	void onFootMaximize(MaximizeEvent event) {
		splitLayoutPanel.setWidgetSize(footPanel, Window.getClientHeight() / 2.0);
		splitLayoutPanel.animate(500);
	}

	private void closeFootPanel() {
		splitLayoutPanel.setWidgetSize(footPanel, 30);
		splitLayoutPanel.animate(500);
	}

	private void openFootPanel() {
		splitLayoutPanel.setWidgetSize(footPanel, Window.getClientHeight() / 4.0);
		splitLayoutPanel.animate(500);
	}
	private void openFootPanelIfNeeded() {
		if (splitLayoutPanel.getWidgetSize(footPanel) <= 30) {
			openFootPanel();
		}
	}
	
	private void cleanErrorMessage() {
		SimpleLayoutPanel panel = new SimpleLayoutPanel();
		resultsPanel.setWidget(panel);
		closeFootPanel();
	}

	private void showErrorMessage(String msg) {
		openFootPanelIfNeeded();
		tabLayout.selectTab(NOTIFICATIONS_TAB);
		SimpleLayoutPanel panel = new SimpleLayoutPanel();
		Label label = new Label(msg);
		label.addStyleName("aon-icon-errorwarning");
		label.addStyleName("aon-message-error");
		label.addStyleName("aon-icon");
		panel.add(label);
		resultsPanel.setWidget(panel);
	}
	
	private void cleanInfo() {
		Widget w = informationPanel.getWidget();
		if (w != null) {
			informationPanel.remove( informationPanel.getWidget() ); 
		}
	}

	private void showFinalizePopup() {
		FinishDeclarationPopup<Mod130,Model130ModuleOptions> finalizeDialog = new FinishDeclarationPopup<>(
			currentMod,
			new Model130Callback(),
			new IFinishDeclarationPopupCallback<Mod130>() {
				@Override
				public void onAccept(Mod130 mod130) {
					finish();
				}

				@Override
				public void onCancel(Mod130 t) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void onCustomerCheck(Mod130 t) {
					markAsFinishedButton.setEnabled(false);
					cleanErrorMessage();
					final PopupPanel popup = new PopupPanel(false, true);
					Label label = new Label(AON.MSG.processing());
					label.addStyleName(AON.AON_CSS.aonTimer());
					popup.add(label);
					popup.setGlassEnabled(true);
					popup.setAnimationEnabled(true);
					popup.center();
					SERVICE.markAsCustomerCheck(getOptions().getOccam(), currentMod, new AsyncCallback<Mod130>() {
						@Override
						public void onSuccess(Mod130 result) {
							select(result);
							popup.hide();
							markAsFinishedButton.setEnabled(true);
							FiscalModelUtils.fillPaymentInfo(paymentInfo,currentMod);
						}

						@Override
						public void onFailure(Throwable caught) {
							popup.hide();
							showErrorMessage(AON.MSG.unableToSaveDeclaration(caught.getMessage()));
							markAsFinishedButton.setEnabled(true);
						}
					});
					
				}
			});
		finalizeDialog.center();
		finalizeDialog.show();
	}
	
	@UiHandler("resetButton")
	void onResetButtonClick(ClickEvent event) {
		resetButton.setEnabled(false);
		cleanErrorMessage();
		
		SERVICE.initialize(getOptions().getOccam(),null,
				new AsyncCallback<Mod130>() {
					@Override
					public void onSuccess(Mod130 newMod130) {
						newMod130.setAdministration(currentMod.getAdministration());
						newMod130.setYear(currentMod.getYear());
						newMod130.setPeriod(currentMod.getPeriod());
						newMod130.setComplementary(currentMod.isComplementary());
						newMod130.setReplacement(currentMod.isReplacement());
						newMod130.setReplacedNumber(currentMod.getReplacedNumber());
						showResetDeclarationPopup(newMod130, currentMod);
						resetButton.setEnabled(true);
					}

					@Override
					public void onFailure(Throwable caught) {
						showErrorMessage(AON.MSG.unableToReadFiscalParameters(caught.getMessage()));
						resetButton.setEnabled(true);
					}
				});
	}
	
	private void showResetDeclarationPopup(Mod130 newMod130, Mod130 oldMod130) {
		Model130NewDeclarationPopup newDialog = new Model130NewDeclarationPopup(newMod130,true,
			new Model130Callback() {

				@Override
				public void onAccept(Mod130 mod130) {
					SERVICE.delete(getOptions().getOccam(), oldMod130, new AsyncCallback<Void>() {
						
						@Override
						public void onSuccess(Void result) {
							SERVICE.create(getOptions().getOccam(), newMod130,
									new AsyncCallback<Mod130>() {
										@Override
										public void onSuccess(Mod130 m130) {
											int i = deckPanel.getWidgetIndex(formPanel);
											deckPanel.showWidget(i);
											tabPanel.selectTab(IDENTIFICATION_TAB);
											select(m130);
										}

										@Override
										public void onFailure(Throwable caught) {
											showErrorMessage(AON.MSG.unableToReadFiscalParameters(caught.getMessage()));
										}
									});
						}
						
						@Override
						public void onFailure(Throwable caught) {
							showErrorMessage(AON.MSG.unableToDeleteDeclaration(caught.getMessage()));						}
					});
					
				}
			}
		); 
		newDialog.center();
		newDialog.show();
	}
	
}
