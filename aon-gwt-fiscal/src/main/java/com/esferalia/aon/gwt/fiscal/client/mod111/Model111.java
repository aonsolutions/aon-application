package com.esferalia.aon.gwt.fiscal.client.mod111;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
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
import com.esferalia.aon.gwt.fiscal.client.FiscalService;
import com.esferalia.aon.gwt.fiscal.client.FiscalServiceAsync;
import com.esferalia.aon.gwt.fiscal.client.FiscalServiceAsyncDecorator;
import com.esferalia.aon.gwt.fiscal.client.MainEntryPoint;
import com.esferalia.aon.gwt.fiscal.client.model.FinishDeclarationPopup;
import com.esferalia.aon.gwt.fiscal.client.model.FiscalModelIdentificationData;
import com.esferalia.aon.gwt.fiscal.client.model.FiscalModelProvidesKey;
import com.esferalia.aon.gwt.fiscal.client.model.FiscalModelTable;
import com.esferalia.aon.gwt.fiscal.client.model.IFiscalModelCallback;
import com.esferalia.aon.gwt.fiscal.client.model.NewDeclarationPopup;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod111;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.esferalia.aon.watson.util.Pair;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DeckLayoutPanel;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.RangeChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent;

public class Model111 extends MainEntryPoint {

	final static int IDENTIFICATION_TAB = 0;
	final static int LIQUIDATION_TAB = 1;
	final static int FISCAL_INFORMATION_TAB = 2;
	
	final static int NOTIFICATIONS_TAB = 0;
	final static int INFORMATION_TAB = 1;

	protected static FiscalServiceAsync FISCAL_SERVICE;
	protected static Mod111ServiceAsync SERVICE;
	
	interface Model111Binder extends UiBinder<Widget, Model111> {
	}
	private static final Model111Binder MODEL_111_BINDER = GWT
			.create(Model111Binder.class);
	
	private static final String MODEL111_PRINT = "/aon_gwt_fiscal/Model111Print";
	private static final String MODEL111_FILE = "/aon_gwt_fiscal/Model111File";
	private static final String MODEL111_PRINT_AEAT = "/aon_gwt_fiscal/Model111PrintAEAT";

	public static interface IMod111Declaration extends IsWidget {
		LinkedList<Pair<String, String>> getInformationLinks();
		void calculateAndRefresh(IFiscalModelCallback<Mod111> callback);
	}
	
	private Mod111 currentMod;
	private boolean dirty;

	@UiField
	SplitLayoutPanel splitLayoutPanel;
	@UiField
	SimplePanel headerPanel;
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
	Panel formContainer;

	@UiField(provided = true)
	FiscalModelTable<Mod111> table;

	private int domain;

	@UiField
	Button saveButton;
	@UiField
	Button deleteButton;
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
	Button generateFileButton;
	@UiField
	Button printViaAeatButton;
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
	private IMod111Declaration declaration;
	@UiField
	ScrollPanel infoContainer;

	FormPanel diskForm;
	Hidden mod111Hidden;
	Hidden domainIdHidden;
	Hidden domainNameHidden;
	
	private abstract class FiscalModelCallback implements IFiscalModelCallback<Mod111> {
		
		@Override
		public void showErrorMsg(String msg) {
			showErrorMessage(msg);
		}
		
		@Override
		public void showInfoPanel(String htmlText) {
			Model111.this.showInfoPanel(htmlText);
		}
		
		@Override
		public boolean isFinished() {
			return (currentMod.getStatus() == FiscalStatus.FINISHED);
		}
		
		@Override
		public Mod111 getFiscalModel() {
			return currentMod;
		}

		@Override
		public boolean isDirty() {
			return Model111.this.isDirty();
		}
		@Override
		public void markAsDirty() {
			if (!isDirty()) {
				Model111.this.setDirty(true);
			}
		}

		@Override
		public void identificationLabelChanged() {
			documentLabel.setText(currentMod.getDocument());
			nameLabel.setText(currentMod.getName());
			surnameLabel.setText(currentMod.getSurname());
		}

		@Override
		public String getDomainName() {
			return getCurrentDomainName();
		}

		@Override
		public String getUser() {
			return getCurrentUser();
		}

		@Override
		public int getDomain() {
			return getCurrentDomain();
		}

	};
	

	@Override
	public void onModuleLoad() {
		AON.ensureInjected();

		FiscalServiceAsync fiscalServiceRaw = GWT.create(FiscalService.class);
		FISCAL_SERVICE = new FiscalServiceAsyncDecorator(fiscalServiceRaw);

		Mod111ServiceAsync serviceRaw = GWT.create(Mod111Service.class);
		SERVICE = new Mod111ServiceAsyncDecorator(serviceRaw);

		table = new FiscalModelTable<Mod111>(new Mod111SelectionHandler(), new FiscalModelProvidesKey<Mod111>());

		Widget ui = MODEL_111_BINDER.createAndBindUi(this);

		tabLayout.setAnimationDuration(300);
		tabLayout.selectTab(NOTIFICATIONS_TAB);
		tabLayout.addSelectionHandler(new SelectionHandler<Integer>() {
			
			@Override
			public void onSelection(SelectionEvent<Integer> event) {
				openFootPanelIfNeeded();
			}
		});

		table.setVisibleRangeAndClearData(table.getVisibleRange(), true);
		
		diskForm = new FormPanel("_blank");
		diskForm.setMethod(FormPanel.METHOD_POST);
		FlowPanel formFlowPanel = new FlowPanel();
		diskForm.add(formFlowPanel);
		mod111Hidden = new Hidden("mod111");
		formFlowPanel.add(mod111Hidden);
		domainIdHidden = new Hidden("domainId");
		formFlowPanel.add(domainIdHidden);
		domainNameHidden = new Hidden("domainName");
		formFlowPanel.add(domainNameHidden);
		formContainer.add(diskForm);
		
		replacedNumber.setVisibleLength(13);
		replacedNumber.setMaxLength(13);

		RootLayoutPanel root = RootLayoutPanel.get("rootPanel");
		root.add(ui);
	}

	public static native String getCurrentDomainName()
	/*-{
		return $wnd.getCurrentDomainName();
	}-*/;

	public static native String getCurrentUser()
	/*-{
		return $wnd.getCurrentUser();
	}-*/;

	public static native int getCurrentDomain()
	/*-{
		return $wnd.getCurrentDomain();
	}-*/;

	
	class Mod111SelectionHandler implements SelectionChangeEvent.Handler {
		@Override
		public void onSelectionChange(SelectionChangeEvent event) {
			Mod111 sel = table.getSelected();
			SERVICE.getMod111(getCurrentDomainName(), getCurrentDomain(),
					sel.getId(), new AsyncCallback<Mod111>() {
						@Override
						public void onSuccess(Mod111 selected) {
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
	
	private void refreshToolbarState() {
		newButton.setVisible(!currentMod.isNew());
		saveButton.setVisible(!currentMod.isFinished() && !currentMod.isSent());
		cancelButton.setVisible(true);
		deleteButton.setVisible(!currentMod.isNew() && !currentMod.isFinished() && !currentMod.isSent());
		printButton.setVisible(!currentMod.isNew());
		markAsPendingButton.setVisible(!currentMod.isNew() &&
				(currentMod.getStatus() == FiscalStatus.FINISHED 
				|| currentMod.getStatus() == FiscalStatus.BATCHED
				|| currentMod.getStatus() == FiscalStatus.SENT
				|| currentMod.getStatus() == FiscalStatus.BLOCKED));
		markAsSentButton.setVisible(!currentMod.isNew() &&
				(currentMod.getStatus() == FiscalStatus.FINISHED));
		markAsFinishedButton.setVisible(!currentMod.isNew() &&
				(currentMod.getStatus() == FiscalStatus.PENDING 
				|| currentMod.getStatus() == FiscalStatus.MISSING));
		auditButton.setVisible(!currentMod.isNew());
		
		generateFileButton.setStyleName(AON.AON_CSS.aonIconCommandButton());
		generateFileButton.setVisible(!currentMod.isNew() && (currentMod.isFinished() || currentMod.isSent()) && currentMod.getYear() > 2015);
		generateFileButton.addStyleName(
				generateFileButton.isEnabled()
					?FiscalModelUtils.getAdministrationIcon(currentMod.getAdministration())
					:FiscalModelUtils.getAdministrationIconBW(currentMod.getAdministration())
							);
		printViaAeatButton.setVisible(!currentMod.isNew() && currentMod.isAEAT() && 
				(currentMod.isFinished() || currentMod.isSent()));
	}
	
	private void toolbarForTable() {
		hideToolbarButtons();
		cancelButton.setVisible(false);
	}
	
	private void hideToolbarButtons() {
		deleteButton.setVisible(false);
		auditButton.setVisible(false);
		newButton.setVisible(true);
		cancelButton.setVisible(true);
		saveButton.setVisible(false);
		printButton.setVisible(false);
		markAsPendingButton.setVisible(false);
		markAsFinishedButton.setVisible(false);
		markAsSentButton.setVisible(false);
		generateFileButton.setVisible(false);
		printViaAeatButton.setVisible(false);
	}
	
	private void select(Mod111 selected) {
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
		domain = currentMod.getDomain();
		
		styleCommentsButton();
		
		fiscalInformationLabel.setStyleName(AON.AON_CSS.aonPaddingRight());
		fiscalInformationLabel.addStyleName(AON.AON_CSS.aonPaddingLeft20());
		fiscalInformationLabel.addStyleName(FiscalModelUtils.getAdministrationIconBW(currentMod.getAdministration()));

		refreshToolbarState();
		
		FiscalModelUtils.paintPaymentInfo(paymentInfo,currentMod);
		FiscalModelUtils.paintHeaderTable(headerPanel,currentMod);
		
		FiscalModelCallback callback = new FiscalModelCallback(){
			@Override
			public void onAccept() {}

			@Override
			public void onCancel() {}
			
		}; 
		FiscalModelIdentificationData<Mod111> identificationData = new FiscalModelIdentificationData<Mod111>(callback);
		identificationContainer.setWidget( identificationData);
		if (currentMod.getAdministration() == Administration.COMMON_TERRITORY) {
			declaration = new Model111AEAT(callback);
		} else  if (currentMod.getAdministration() == Administration.GIPUZKOA) {
			if (currentMod.getPeriod().isQuarterPeriod()) {
				declaration = new Model110Gipuzkoa(callback);
			} else {
				declaration = new Model111Gipuzkoa(callback);
			}
		} else  if (currentMod.getAdministration() == Administration.BIZKAIA) {
			if (currentMod.getPeriod().isQuarterPeriod()) {
				declaration = new Model110Bizkaia(callback);
			} else {
				declaration = new Model111Bizkaia(callback);
			}
		} else  if (currentMod.getAdministration() == Administration.NAVARRA) {
			if (currentMod.getPeriod().isQuarterPeriod()) {
				declaration = new Model715Navarra(callback);
			} else {
				declaration = new Model745Navarra(callback);
			}
		} else  if (currentMod.getAdministration() == Administration.ALAVA) {
			if (currentMod.getYear() > 2015) {
				declaration = new Model111Araba2016(callback);	
			} else {
				declaration = new Model111Araba(callback);
			}
		}
		if (declaration != null) {
			declarationContainer.setWidget( declaration );
			infoContainer.setWidget( getInformationPanel(declaration.getInformationLinks()) );
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
		statusLabel.setStyleName( FiscalModelUtils.getStatusIconStyle(currentMod.getStatus()));
		statusLabel.addStyleName(AON.AON_CSS.aonIconPaddingLeft());
	}

	@UiHandler("table")
	void onTableRangeChange(RangeChangeEvent event) {
		SERVICE.getMod111s(getCurrentDomainName(), getCurrentDomain(),
				new AsyncCallback<LinkedList<Mod111>>() {
					@Override
					public void onSuccess(LinkedList<Mod111> result) {
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
		SERVICE.reopen(getCurrentDomainName(), this.currentMod, new AsyncCallback<Mod111>() {
					@Override
					public void onSuccess(Mod111 result) {
						select(result);
						popup.hide();
						markAsPendingButton.setEnabled(true);
						FiscalModelUtils.paintPaymentInfo(paymentInfo,currentMod);
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
		SERVICE.markAsSent(getCurrentDomainName(), this.currentMod, new AsyncCallback<Mod111>() {
					@Override
					public void onSuccess(Mod111 result) {
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
		SERVICE.initializeForFinish(getCurrentDomainName(),currentMod,
				new AsyncCallback<Mod111>() {
					@Override
					public void onSuccess(Mod111 m111) {
						currentMod = m111;
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
		SERVICE.save(getCurrentDomainName(), this.currentMod, new AsyncCallback<Mod111>() {
					@Override
					public void onSuccess(Mod111 result) {
						select(result);
						popup.hide();
						saveButton.setEnabled(true);
						cleanInfo();
						tabLayout.selectTab(INFORMATION_TAB);
						closeFootPanel();
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
		SERVICE.finish(getCurrentDomainName(), this.currentMod, new AsyncCallback<Mod111>() {
					@Override
					public void onSuccess(Mod111 result) {
						select(result);
						popup.hide();
						markAsFinishedButton.setEnabled(true);
						FiscalModelUtils.paintPaymentInfo(paymentInfo,currentMod);
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
				SERVICE.delete(getCurrentDomainName(),currentMod, new AsyncCallback<Void>() {
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
		
		SERVICE.initialize(getCurrentDomainName(),getCurrentDomain(),null,
				new AsyncCallback<Mod111>() {
					@Override
					public void onSuccess(Mod111 m111) {
						currentMod = m111;
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
		
	private void showNewDeclarationPopup() {
		NewDeclarationPopup<Mod111> newDialog = new NewDeclarationPopup<Mod111>(
			new FiscalModelCallback() {

					@Override
					public void onAccept() {
						SERVICE.create(getCurrentDomainName(),getCurrentDomain(),currentMod,
								new AsyncCallback<Mod111>() {
									@Override
									public void onSuccess(Mod111 m111) {
										int i = deckPanel.getWidgetIndex(formPanel);
										deckPanel.showWidget(i);
										tabPanel.selectTab(IDENTIFICATION_TAB);
										select(m111);
									}

									@Override
									public void onFailure(Throwable caught) {
										showErrorMessage(AON.MSG.unableToReadFiscalParameters(caught.getMessage()));
									}
								});
					}

					@Override
					public void onCancel() {
						cancel();
					}

					@Override
					public Mod111 getFiscalModel() {
						return currentMod;
					}
				}
			); 
			newDialog.center();
			newDialog.show();
	}

	@UiHandler("cancelButton")
	void onCancelButtonClick(ClickEvent event) {
		if (!isDirty()) {
			cancel();
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
		commentPanel.setStyleName( FiscalModelUtils.getAdministrationBG(currentMod.getAdministration()) );
		commentPanel.setStyleName(AON.AON_CSS.aonHeightAll());
		commentPanel.addStyleName(AON.AON_CSS.aonTextCenter());
		TextArea comment = new TextArea();
		comment.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				currentMod.setComments(event.getValue());
				styleCommentsButton();
				SERVICE.saveComments(getCurrentDomainName(), currentMod, new AsyncCallback<Mod111>() {
					@Override
					public void onSuccess(Mod111 result) {
						toast.hide();
					}

					@Override
					public void onFailure(Throwable caught) {
						toast.hide();
						showErrorMessage(AON.MSG.unableToSaveDeclaration(caught.getMessage()));
					}
				});
				
				
				
			}
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
		if (isDirty()) {
			new ConfirmDialog().confirm(AON.MSG.draftPrint(),AON.MSG.draftPrintNote() 
					, new ConfirmDialogCallback() {
					
					@Override
					public void onAccept() {
						submitForm(MODEL111_PRINT);
					}
	
					@Override
					public void onCancel() {
						// Nothing
					}
				});
		} else {
			submitForm(MODEL111_PRINT);
		}
	}

	@UiHandler("generateFileButton")
	void onGenerateFileButtonClick(ClickEvent event) {
		if (isDirty()) {
			new ConfirmDialog().confirm(AON.MSG.fileGeneration(),AON.MSG.fileGenerationNote() 
				, new ConfirmDialogCallback() {
				
				@Override
				public void onAccept() {
					submitForm(MODEL111_FILE);
				}
	
				@Override
				public void onCancel() {
					// Nothing
				}
			});
		} else {
			submitForm(MODEL111_FILE);
		}
	}

	private void submitForm(String action) {
		diskForm.setAction(GWT.getHostPageBaseURL() + action);
		mod111Hidden.setValue(String.valueOf(currentMod.getId()));
		domainIdHidden.setValue(String.valueOf(getCurrentDomain()));
		domainNameHidden.setValue(getCurrentDomainName());
		diskForm.submit();
	}

	@UiHandler("printViaAeatButton")
	void onPrintViaAeatButtonClick(ClickEvent event) {
		new ConfirmDialog().confirm(AON.MSG.fileGeneration()
				,"Se va a proceder a la validaci\u00F3n en los servidores de la \n"
				+ "Agencia Tributaria. En el caso de validaci\u00F3n correcta,la Agencia \n"
				+ "Tributaria devolver\u00E1 un documento PDF borrador con la declarai\u00F3n\n\n"
				+ "Aseg\u00FArese de haber guardado la declaraci\u00F3n.\n\n"
				+ "La petici\u00F3n se genera a partir de los datos guardados." 
				, new ConfirmDialogCallback() {
				
				@Override
				public void onAccept() {
					submitForm(MODEL111_PRINT_AEAT);
				}

				@Override
				public void onCancel() {
					// Nothing
				}
			});
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
	
	private void showInfoPanel(String htmlText) {
		openFootPanelIfNeeded();
		tabLayout.selectTab(INFORMATION_TAB);
		HTMLPanel panel = new HTMLPanel(htmlText);
		informationPanel.setWidget(panel);
		informationPanel.scrollToTop();
	}

	private void showFinalizePopup() {
		FinishDeclarationPopup<Mod111> finalizeDialog = new FinishDeclarationPopup<>( 
				new FiscalModelCallback() {
			
			@Override
			public void onAccept() {
				finish();
			}
			@Override
			public void onCancel() {
				
			}
			
		});
		finalizeDialog.center();
		finalizeDialog.show();
	}
	
	protected FlowPanel getInformationPanel(LinkedList<Pair<String,String>> infoList) {
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
		Label title = new Label("Informaci\u00F3n \u00FAtil para la confecci\u00F3n del modelo");
		tab.getFlexCellFormatter().setColSpan(0, 0, 2);
		tab.getCellFormatter().setStyleName(0, 0, AON.AON_CSS.aonPanelGridEven());
		tab.getCellFormatter().addStyleName(0, 0, AON.AON_CSS.aonMarginTop());
		tab.getCellFormatter().addStyleName(0, 0, AON.AON_CSS.aonFiscalModelTableHeaderTitle());
		tab.getCellFormatter().addStyleName(0, 0, FiscalModelUtils.getAdministrationBG(currentMod.getAdministration()));
		tab.setWidget(0, 0, title);
		
		int row = 1;
		for (Pair<String, String> pair : infoList) {
			Label icon = new Label();
			icon.addStyleName(FiscalModelUtils.getAdministrationIcon(currentMod.getAdministration()));
			tab.setWidget(row, 0, icon );
			tab.getCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonPanelGridEven());

			FlowPanel p = new FlowPanel();
			p.setStyleName(AON.AON_CSS.aonPadding2());
			Anchor a = new Anchor(pair.getLeft(),pair.getRight(), "_blank");
			a.setStyleName(AON.AON_CSS.aonPaddingLeft());
			p.add(a);
			tab.setWidget(row, 1, p );
			tab.getCellFormatter().setStyleName(row, 1, AON.AON_CSS.aonPanelGridEven());
			row++;
		}
		panel.add(tab);
		return panel;
	}
	
	
}
