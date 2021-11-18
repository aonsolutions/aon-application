package com.esferalia.aon.gwt.fiscal.client.mod131;

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
import com.esferalia.aon.occam.api.model.fiscal.Mod131;
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

public class Model131 extends MainEntryPoint {

	private static final Logger LOGGER = Logger.getLogger(Model131.class.getName());
	static {
		LOGGER.addHandler( new ConsoleLogHandler() );
	}

	static final int IDENTIFICATION_TAB = 0;
	static final int LIQUIDATION_TAB = 1;
	
	static final int NOTIFICATIONS_TAB = 0;
	static final int INFORMATION_TAB = 1;
	static final int AEAT_TAB = 2;
	
	static final Mod131ServiceAsync SERVICE;
	private static final CommonServiceAsync COMMON_SERVICE;
	static {
		CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
		COMMON_SERVICE = new CommonServiceAsyncDecorator(commonServiceRaw); 
		
		Mod131ServiceAsync serviceRaw = GWT.create(Mod131Service.class);
		SERVICE = new Mod131ServiceAsyncDecorator(serviceRaw);
	}

	interface Model131Binder extends UiBinder<Widget, Model131> {
	}
	private static final Model131Binder MODEL_131_BINDER = GWT
			.create(Model131Binder.class);

	public static interface IMod131Declaration extends IsWidget {
		FlowPanel getDeclarationPanel();
		LinkedList<Pair<String, String>> getInformationLinks();
		void calculateAndRefresh(Model131Callback callback);
		void printButtonClick();
		HandlerRegistration addAttachHandler(Handler handler);
	}
	
	private Mod131 currentMod;
	private Model131ModuleOptions options;
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
	FiscalModelTable<Mod131> table;

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
	
	private IMod131Declaration declaration;
	
	@UiField
	ScrollPanel infoContainer;

	protected class Model131Callback implements IFiscalModelCallback<Mod131,Model131ModuleOptions> {
		
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
			Model131.this.cleanInfo();
		}
		
		@Override
		public Model131ModuleOptions getOptions() {
			return Model131.this.options;
		}

		@Override
		public void onAccept(Mod131 model) {
			// Nothing
		}

		@Override
		public void onCancel(Mod131 model) {
			// Nothing
			
		}

		@Override
		public void onRemove(Mod131 model) {
			// Nothing
			
		}

		@Override
		public void onNew() {
			// Nothing
			
		}

		@Override
		public void onReset(Mod131 oldModel) {
			// Nothing
		}

		public boolean isFinished() {
			return (currentMod.getStatus() == FiscalStatus.FINISHED);
		}
		public boolean isDirty() {
			return Model131.this.isDirty();
		}
		public void markAsDirty() {
			if (!isDirty()) {
				Model131.this.setDirty(true);
			}
		}
		public void identificationLabelChanged() {
			documentLabel.setText(currentMod.getDocument());
			nameLabel.setText(currentMod.getName());
			surnameLabel.setText(currentMod.getSurname());
		}

	}

	@Override
	public void onModuleLoad() {
		COMMON_SERVICE.getAonConfiguration(getCurrentDomainName(), getCurrentDomain(), getCurrentUser(), new AsyncCallback<AonConfiguration>() {
			
			@Override
			public void onSuccess(AonConfiguration aonConfiguration) {
				RootLayoutPanel root = RootLayoutPanel.get(getRootPanel() != null ? getRootPanel() : "rootPanel");
				Model131ModuleOptions opts = new Model131ModuleOptions();
				opts.setParentWidget(root);
				opts.setDomainName(getCurrentDomainName());
				opts.setDomain(getCurrentDomain());
				opts.setUser(getCurrentUser());
				opts.setConfiguration(aonConfiguration);
				onModuleLoad( opts );
			}
			
			@Override public void onFailure(Throwable caught) {
				Window.alert( "Error al cargar el module" );
			}
		});
	}
	private Model131ModuleOptions getOptions() {
		if (this.options == null) {
			this.options = new Model131ModuleOptions();
		}
		return this.options;
	}
	
	public void onModuleLoad(Model131ModuleOptions options) {
		this.options = options;
		GWT.setUncaughtExceptionHandler(e -> LOGGER.log(Level.SEVERE,"No caught!",e));
		
		AON.ensureInjected();

		table = new FiscalModelTable<>(new Mod131SelectionHandler(), new FiscalModelProvidesKey<>());

		Widget ui = MODEL_131_BINDER.createAndBindUi(this);

		if (this.options.isBackButtonVisible() && this.options.hasExternalCallback()) {
			cancelButton.setText(AON.MSG.backAction());
		}
		HTMLPanel html = new HTMLPanel("<iframe name='aeatForm' width='100%' height='100%' style='border:none'/>");
		html.setWidth("100%");
		html.setHeight("100%");
		aeatPanel.setWidget(html);
		
		tabLayout.setAnimationDuration(300);
		tabLayout.selectTab(NOTIFICATIONS_TAB);
		tabLayout.addSelectionHandler(event -> openFootPanelIfNeeded());

		table.setVisibleRangeAndClearData(table.getVisibleRange(), true);

		replacedNumber.setVisibleLength(13);
		replacedNumber.setMaxLength(13);

		getOptions().getParentWidget().add(ui);
		if (getOptions().getFiscalModelId() != null ) {
			LOGGER.info("Access to Model131 with a ID: " + getOptions().getFiscalModelId());
			onSelect(getOptions().getFiscalModelId());
		} else if (getOptions().getNewModel() != null ) {
			LOGGER.info("Access to Model131 new Model");
			newModel(getOptions().getNewModel()); 
		} else {
			table.setVisibleRangeAndClearData(table.getVisibleRange(), true);
			LOGGER.info("Model131 setting NOTIFICATIONS_TAB");
			tabLayout.selectTab(NOTIFICATIONS_TAB);
		}
		tabLayout.setAnimationDuration(300);
		tabLayout.addSelectionHandler(event -> openFootPanelIfNeeded());
	}

	private void onSelect(Integer id ) {
		LOGGER.info("OnSelect Model131 with a ID: " + getOptions().getFiscalModelId());
		SERVICE.getMod131(getOptions().getOccam(), id , new AsyncCallback<Mod131>() {
					@Override
					public void onSuccess(Mod131 selected) {
						if (selected == null) {
							LOGGER.info("onSuccess Model131 with a NULL selected Model ID: ");
							showErrorMessage(AON.MSG.unableToFindDeclaration());
						} else {
							LOGGER.info("onSuccess Model131 with a ID: " + selected.getId());
							select(selected);
						}
					}

					@Override
					public void onFailure(Throwable caught) {
						showErrorMessage(AON.MSG.unableToReadDeclaration(caught.getMessage()));
					}
				});
	}

	class Mod131SelectionHandler implements SelectionChangeEvent.Handler {
		@Override
		public void onSelectionChange(SelectionChangeEvent event) {
			Mod131 sel = table.getSelected();
			SERVICE.getMod131(getOptions().getOccam(),
					sel.getId(), new AsyncCallback<Mod131>() {
						@Override
						public void onSuccess(Mod131 selected) {
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
	
	private void newModel(Mod131 newModel) {
		SERVICE.initialize(getOptions().getOccam(),newModel,
				new AsyncCallback<Mod131>() {
					@Override
					public void onSuccess(Mod131 m131) {
						currentMod = m131;
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
		LOGGER.info("Model131 refreshToolbarState! ");
		newButton.setVisible(!currentMod.isNew() && !this.options.isBackButtonVisible() && !this.options.hasExternalCallback());
		saveButton.setVisible(!currentMod.isFinished() && !currentMod.isSent());
		cancelButton.setVisible(true);
		deleteButton.setVisible(!currentMod.isNew() && !currentMod.isFinished() && !currentMod.isSent());
		resetButton.setVisible(!currentMod.isNew() && !currentMod.isFinished() && !currentMod.isSent());
		printButton.setVisible(!currentMod.isNew());
		markAsPendingButton.setVisible(!currentMod.isNew() &&
				(currentMod.getStatus() == FiscalStatus.FINISHED 
				|| currentMod.getStatus() == FiscalStatus.BATCHED
				|| currentMod.getStatus() == FiscalStatus.SENT
				|| currentMod.getStatus() == FiscalStatus.CUSTOMER_CHECK
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
	
	private void select(Mod131 selected) {
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
		fiscalInformationLabel.addStyleName(FiscalModelUtils.getAdministrationBWIconStyle(currentMod.getAdministration()));

		refreshToolbarState();
		
		FiscalModelUtils.fillPaymentInfo(paymentInfo,currentMod);
		headerPanel.setWidget( new AonFiscalModelHeader(currentMod) );
		Model131Callback callback = new Model131Callback(); 
		FiscalModelIdentificationData<Mod131> identificationData = new FiscalModelIdentificationData<>(currentMod);
		identificationData.addValueChangeHandler(event -> {
			callback.identificationLabelChanged();
			callback.markAsDirty();
		});
		identificationContainer.setWidget( identificationData);
		if (currentMod.getAdministration() == Administration.COMMON_TERRITORY) {
			declaration = new Model131AEAT(currentMod,callback);
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
		SERVICE.getMod131s(getOptions().getOccam(),
				new AsyncCallback<LinkedList<Mod131>>() {
					@Override
					public void onSuccess(LinkedList<Mod131> result) {
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
		SERVICE.markAsPending(getOptions().getOccam(), this.currentMod, new AsyncCallback<Mod131>() {
					@Override
					public void onSuccess(Mod131 result) {
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
		SERVICE.markAsSent(getOptions().getOccam(), this.currentMod, new AsyncCallback<Mod131>() {
					@Override
					public void onSuccess(Mod131 result) {
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
				new AsyncCallback<Mod131>() {
					@Override
					public void onSuccess(Mod131 m131) {
						currentMod = m131;
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
		SERVICE.save(getOptions().getOccam(), this.currentMod, new AsyncCallback<Mod131>() {
					@Override
					public void onSuccess(Mod131 result) {
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
		SERVICE.markAsFinished(getOptions().getOccam(), this.currentMod, new AsyncCallback<Mod131>() {
					@Override
					public void onSuccess(Mod131 result) {
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

	private void markAsCustomerCheck() {
		markAsFinishedButton.setEnabled(false);
		cleanErrorMessage();
		final PopupPanel popup = new PopupPanel(false, true);
		Label label = new Label(AON.MSG.processing());
		label.addStyleName(AON.AON_CSS.aonTimer());
		popup.add(label);
		popup.setGlassEnabled(true);
		popup.setAnimationEnabled(true);
		popup.center();
		SERVICE.markAsCustomerCheck(getOptions().getOccam(), this.currentMod, new AsyncCallback<Mod131>() {
					@Override
					public void onSuccess(Mod131 result) {
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
				new AsyncCallback<Mod131>() {
					@Override
					public void onSuccess(Mod131 m131) {
						currentMod = m131;
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
		Model131NewDeclarationPopup newDialog = new Model131NewDeclarationPopup(currentMod,
			new Model131Callback() {

				@Override
				public void onAccept( Mod131 mod131) {
					SERVICE.create(getOptions().getOccam(),mod131,
							new AsyncCallback<Mod131>() {
								@Override
								public void onSuccess(Mod131 m131) {
									int i = deckPanel.getWidgetIndex(formPanel);
									deckPanel.showWidget(i);
									tabPanel.selectTab(IDENTIFICATION_TAB);
									select(m131);
								}

								@Override
								public void onFailure(Throwable caught) {
									showErrorMessage(AON.MSG.unableToReadFiscalParameters(caught.getMessage()));
								}
							});
				}

				@Override
				public void onCancel(Mod131 mod131) {
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
			SERVICE.saveComments(getOptions().getOccam(), currentMod, new AsyncCallback<Mod131>() {
				@Override
				public void onSuccess(Mod131 result) {
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
		FinishDeclarationPopup<Mod131,Model131ModuleOptions> finalizeDialog 
			= new FinishDeclarationPopup<>(
				currentMod,
				new Model131Callback(),
				new IFinishDeclarationPopupCallback<Mod131>() {
			
			@Override
			public void onAccept(Mod131 mod131) {
				finish();
			}
			@Override
			public void onCustomerCheck(Mod131 fiscalModel) {
				Model131.this.markAsCustomerCheck();
			}
			@Override
			public void onCancel(Mod131 t) {
				// Nothing
			}
		});
		finalizeDialog.center();
		finalizeDialog.show();
	}
	
	@UiHandler("resetButton")
	void onResetButtonClick(ClickEvent event) {
		resetButton.setEnabled(false);
		cleanErrorMessage();
		
		SERVICE.initialize(getCurrentDomainName(), getCurrentUser(),getCurrentDomain(),null,
				new AsyncCallback<Mod131>() {
					@Override
					public void onSuccess(Mod131 newMod131) {						
						newMod131.setAdministration(currentMod.getAdministration());
						newMod131.setYear(currentMod.getYear());
						newMod131.setPeriod(currentMod.getPeriod());
						newMod131.setComplementary(currentMod.isComplementary());
						newMod131.setReplacement(currentMod.isReplacement());
						newMod131.setReplacedNumber(currentMod.getReplacedNumber());
						showResetDeclarationPopup(newMod131, currentMod);
						resetButton.setEnabled(true);
					}

					@Override
					public void onFailure(Throwable caught) {
						showErrorMessage(AON.MSG.unableToReadFiscalParameters(caught.getMessage()));
						resetButton.setEnabled(true);
					}
				});
	}
	
	private void showResetDeclarationPopup(Mod131 newMod131, Mod131 oldMod131) {
		Model131NewDeclarationPopup newDialog = new Model131NewDeclarationPopup(true,
			new FiscalModelCallback() {

				@Override
				public void onAccept() {
					
					SERVICE.delete(getCurrentDomainName(), getCurrentUser(), oldMod131, new AsyncCallback<Void>() {
						
						@Override
						public void onSuccess(Void result) {
							SERVICE.create(getCurrentDomainName(), getCurrentUser(),getCurrentDomain(), newMod131,
									new AsyncCallback<Mod131>() {
										@Override
										public void onSuccess(Mod131 m131) {
											int i = deckPanel.getWidgetIndex(formPanel);
											deckPanel.showWidget(i);
											tabPanel.selectTab(IDENTIFICATION_TAB);
											select(m131);
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

				@Override
				public void onCancel() {
					
				}
				
				@Override
				public Mod131 getFiscalModel() {
					return newMod131;
				};
			}
		); 
		newDialog.center();
		newDialog.show();
	}
	 
}
