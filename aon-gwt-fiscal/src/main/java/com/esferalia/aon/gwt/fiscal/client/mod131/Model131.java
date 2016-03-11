package com.esferalia.aon.gwt.fiscal.client.mod131;

import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

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
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod131;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.watson.util.AonStringUtils;
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
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DeckLayoutPanel;
import com.google.gwt.user.client.ui.DockLayoutPanel;
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

public class Model131 extends MainEntryPoint {

	final static int IDENTIFICATION_TAB = 0;
	final static int LIQUIDATION_TAB = 1;
	
	final static int NOTIFICATIONS_TAB = 0;
	final static int INFORMATION_TAB = 1;

	static FiscalServiceAsync fiscalService;
	
	interface Model131Binder extends UiBinder<Widget, Model131> {
	}
	private static final Model131Binder MODEL_131_BINDER = GWT
			.create(Model131Binder.class);

	private static final String MODEL131_PRINT = "/aon_gwt_fiscal/Model131Print";
	private static final String MODEL131_PRINT_PDF ="/aon_gwt_fiscal/Model131PrintPDF";
	private static final String MODEL131_FILE = "/aon_gwt_fiscal/Model131File";
	private static final String MODEL131_PRINT_AEAT = "/aon_gwt_fiscal/Model131PrintAEAT";
	
	public static interface IMod131Declaration extends IsWidget {
		Widget getInfoPanel(Mod131 mod131);
		void calculateAndRefresh(IFiscalModelCallback<Mod131> callback);
	}
	
	private Mod131 currentMod131;
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
	FiscalModelTable<Mod131> table;

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
	Button printPDFButton;
	@UiField
	Button reopenButton;
	@UiField
	Button finalizeButton;
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
	
	private IMod131Declaration declaration;
	
	@UiField
	ScrollPanel infoContainer;

	FormPanel diskForm;
	Hidden mod131Hidden;
	Hidden domainIdHidden;
	Hidden domainNameHidden;

	private abstract class FiscalModelCallback implements IFiscalModelCallback<Mod131> {
		
		@Override
		public void showErrorMsg(String msg) {
			showErrorMessage(msg);
		}
		
		@Override
		public void showInfoPanel(String htmlText) {
			Model131.this.showInfoPanel(htmlText);
		}
		
		@Override
		public boolean isFinished() {
			return (currentMod131.getStatus() == FiscalStatus.FINISHED);
		}
		
		@Override
		public Mod131 getFiscalModel() {
			return currentMod131;
		}

		@Override
		public boolean isDirty() {
			return Model131.this.isDirty();
		}
		@Override
		public void markAsDirty() {
			if (!isDirty()) {
				Model131.this.setDirty(true);
			}
		}

		@Override
		public void identificationLabelChanged() {
			documentLabel.setText(currentMod131.getDocument());
			nameLabel.setText(currentMod131.getName());
			surnameLabel.setText(currentMod131.getSurname());
		}
		@Override
		public String getDomainName() {
			return getCurrentDomainName();
		}

		@Override
		public int getDomain() {
			return getCurrentDomain();
		}

	};

	private Logger logger = Logger.getLogger(Mod131.class.getName()); 
	@Override
	public void onModuleLoad() {
		
		GWT.setUncaughtExceptionHandler(new GWT.UncaughtExceptionHandler() {
		    @Override
		    public void onUncaughtException(Throwable e) {
		    	logger.log(Level.SEVERE,"No caught!",e);
		    }
		  });
		
		AON.ensureInjected();

		FiscalServiceAsync fiscalServiceRaw = GWT.create(FiscalService.class);
		fiscalService = new FiscalServiceAsyncDecorator(fiscalServiceRaw);

		table = new FiscalModelTable<Mod131>(new Mod131SelectionHandler(), new FiscalModelProvidesKey<Mod131>());
		

		Widget ui = MODEL_131_BINDER.createAndBindUi(this);

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
		mod131Hidden = new Hidden("mod131");
		formFlowPanel.add(mod131Hidden);
		domainIdHidden = new Hidden("domainId");
		formFlowPanel.add(domainIdHidden);
		domainNameHidden = new Hidden("domainName");
		formFlowPanel.add(domainNameHidden);
		formContainer.add(diskForm);
		
		replacedNumber.setVisibleLength(13);
		replacedNumber.setMaxLength(13);

		RootLayoutPanel root = RootLayoutPanel.get("rootPanel");
		root.add(ui);
		
		// http://code.google.com/p/google-web-toolkit/issues/detail?id=6889
		deckPanel.onResize();
	}

	public static native String getCurrentDomainName()
	/*-{
		return $wnd.getCurrentDomainName();
	}-*/;

	public static native int getCurrentDomain()
	/*-{
		return $wnd.getCurrentDomain();
	}-*/;

	
	class Mod131SelectionHandler implements SelectionChangeEvent.Handler {
		@Override
		public void onSelectionChange(SelectionChangeEvent event) {
			Mod131 sel = table.getSelected();
			fiscalService.getMod131(getCurrentDomainName(), getCurrentDomain(),
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
	
	private void refreshToolbarState() {
		deleteButton.setVisible(!currentMod131.isNew());
		auditButton.setVisible(!currentMod131.isNew());
		newButton.setVisible(!currentMod131.isNew());
		cancelButton.setVisible(true);
		saveButton.setVisible(true);
		
		saveButton.setEnabled(!currentMod131.isFinished());
		deleteButton.setEnabled(!currentMod131.isFinished());
		
		printButton.setVisible(!currentMod131.isNew());
		printPDFButton.setVisible(!currentMod131.isNew());
		
		reopenButton.setVisible(!currentMod131.isNew() && currentMod131.getStatus() == FiscalStatus.FINISHED);
		finalizeButton.setVisible(!currentMod131.isNew() && currentMod131.getStatus() == FiscalStatus.PENDING );
		
		generateFileButton.setStyleName(AON.AON_CSS.aonIconCommandButton());
		generateFileButton.setVisible(!currentMod131.isNew());
		generateFileButton.setEnabled(currentMod131.isFinished() && currentMod131.getYear() > 2015);
		generateFileButton.addStyleName(
				generateFileButton.isEnabled()
					?FiscalModelUtils.getAdministrationIcon(currentMod131.getAdministration())
					:FiscalModelUtils.getAdministrationIconBW(currentMod131.getAdministration())
							);
		printViaAeatButton.setVisible(!currentMod131.isNew() && currentMod131.isAEAT() && currentMod131.isFinished());
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
		printPDFButton.setVisible(false);
		reopenButton.setVisible(false);
		finalizeButton.setVisible(false);
		generateFileButton.setVisible(false);
		printViaAeatButton.setVisible(false);
	}
	
	private void select(Mod131 selected) {
		currentMod131 = selected;
		dirty = false;
		
		documentLabel.setText(currentMod131.getDocument());
		nameLabel.setText(currentMod131.getName());
		surnameLabel.setText(currentMod131.getSurname());
	
		styleDirtyLabel();
		styleStatusLabel();
		if (currentMod131.isReplacementDeclarationAvailable()) {
			replacementLabel.setText(AON.MSG.replacement());
			replacementLabel.setStyleName(AON.AON_CSS.aonIconPaddingLeft());
			replacementLabel.addStyleName(currentMod131.isReplacement()
					?AON.AON_CSS.aonIconChecked()
					:AON.AON_CSS.aonIconCheck()
				);
		} else {
			replacementLabel.setText("");
		}
		
		if (currentMod131.isComplementaryDeclarationAvailable()) {
			complementaryLabel.setText(AON.MSG.complementary());
			complementaryLabel.setStyleName(AON.AON_CSS.aonIconPaddingLeft());
			complementaryLabel.addStyleName(currentMod131.isComplementary()
					?AON.AON_CSS.aonIconChecked()
					:AON.AON_CSS.aonIconCheck()
				);
		} else {
			complementaryLabel.setText("");
		}
		
		replacedNumber.setValue(currentMod131.getReplacedNumber());
		replacedNumber.setEnabled(currentMod131.isReplacedNumberAvailable());
		
		confidential.setValue(currentMod131.isConfidential());
		domain = currentMod131.getDomain();
		
		styleCommentsButton();
		
		fiscalInformationLabel.setStyleName(AON.AON_CSS.aonPaddingRight());
		fiscalInformationLabel.addStyleName(AON.AON_CSS.aonPaddingLeft20());
		fiscalInformationLabel.addStyleName(FiscalModelUtils.getAdministrationIconBW(currentMod131.getAdministration()));

		refreshToolbarState();
		
		FiscalModelUtils.paintPaymentInfo(paymentInfo,currentMod131);
		FiscalModelUtils.paintHeaderTable(headerPanel,currentMod131);
		
		FiscalModelCallback callback = new FiscalModelCallback(){
			@Override
			public void onAccept() {}

			@Override
			public void onCancel() {}
			
		}; 
		
		FiscalModelIdentificationData<Mod131> identificationData = new FiscalModelIdentificationData<Mod131>(callback);
		identificationContainer.setWidget( identificationData);
		if (currentMod131.getAdministration() == Administration.COMMON_TERRITORY) {
			declaration = new Model131AEAT(callback);
		}
		if (declaration != null) {
			declarationContainer.setWidget( declaration );
			infoContainer.setWidget(declaration.getInfoPanel(currentMod131));
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
		statusLabel.setText(currentMod131.getStatus().getName());
		statusLabel.setStyleName(
				currentMod131.getStatus() == FiscalStatus.FINISHED
					?AON.AON_CSS.aonIconLock()
					:AON.AON_CSS.aonIconUnlock()
				);
		statusLabel.addStyleName(AON.AON_CSS.aonIconPaddingLeft());
	}

	@UiHandler("table")
	void onTableRangeChange(RangeChangeEvent event) {
		fiscalService.getMod131s(getCurrentDomainName(), getCurrentDomain(),
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
	@UiHandler("reopenButton")
	void onReopenButtonClick(ClickEvent event) {
		reopenButton.setEnabled(false);
		cleanErrorMessage();
		final PopupPanel popup = new PopupPanel(false, true);
		Label label = new Label(AON.MSG.processing());
		label.addStyleName(AON.AON_CSS.aonTimer());
		popup.add(label);
		popup.setGlassEnabled(true);
		popup.setAnimationEnabled(true);
		popup.center();
		fiscalService.reopenMod131(getCurrentDomainName(), this.currentMod131, new AsyncCallback<Mod131>() {
					@Override
					public void onSuccess(Mod131 result) {
						select(result);
						popup.hide();
						reopenButton.setEnabled(true);
						FiscalModelUtils.paintPaymentInfo(paymentInfo,currentMod131);
					}

					@Override
					public void onFailure(Throwable caught) {
						popup.hide();
						showErrorMessage(AON.MSG.unableToReopenDeclaration(caught.getMessage()));
						reopenButton.setEnabled(true);
					}
				});
	}
	@UiHandler("finalizeButton")
	void onFinalizeButtonClick(ClickEvent event) {
		finalizeButton.setEnabled(false);
		cleanErrorMessage();
		fiscalService.initializeForFinishMod131(getCurrentDomainName(),currentMod131,
				new AsyncCallback<Mod131>() {
					@Override
					public void onSuccess(Mod131 m131) {
						currentMod131 = m131;
						showFinalizePopup();
						finalizeButton.setEnabled(true);
					}

					@Override
					public void onFailure(Throwable caught) {
						showErrorMessage(AON.MSG.unableToReadFiscalParameters(caught.getMessage()));
						finalizeButton.setEnabled(true);
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
		fiscalService.saveMod131(getCurrentDomainName(), this.currentMod131, new AsyncCallback<Mod131>() {
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
		finalizeButton.setEnabled(false);
		cleanErrorMessage();
		final PopupPanel popup = new PopupPanel(false, true);
		Label label = new Label(AON.MSG.processing());
		label.addStyleName(AON.AON_CSS.aonTimer());
		popup.add(label);
		popup.setGlassEnabled(true);
		popup.setAnimationEnabled(true);
		popup.center();
		fiscalService.finishMod131(getCurrentDomainName(), this.currentMod131, new AsyncCallback<Mod131>() {
					@Override
					public void onSuccess(Mod131 result) {
						select(result);
						popup.hide();
						finalizeButton.setEnabled(true);
						FiscalModelUtils.paintPaymentInfo(paymentInfo,currentMod131);
					}

					@Override
					public void onFailure(Throwable caught) {
						popup.hide();
						showErrorMessage(AON.MSG.unableToSaveDeclaration(caught.getMessage()));
						finalizeButton.setEnabled(true);
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
				fiscalService.deleteMod131(getCurrentDomainName(),currentMod131, new AsyncCallback<Void>() {
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
		
		fiscalService.initializeMod131(getCurrentDomainName(),getCurrentDomain(),null,
				new AsyncCallback<Mod131>() {
					@Override
					public void onSuccess(Mod131 m131) {
						currentMod131 = m131;
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
		Model131NewDeclarationPopup newDialog = new Model131NewDeclarationPopup(
			new FiscalModelCallback() {

				@Override
				public void onAccept() {
					fiscalService.createMod131(getCurrentDomainName(),getCurrentDomain(),currentMod131,
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
				public void onCancel() {
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
		dialog.show(currentMod131);
	}

	// -------------------------------------------------------------- UiHandler
	@UiHandler("commentsButton")
	public void onComments(ClickEvent event) {
		final AonToast toast = new AonToast();
		FlowPanel commentPanel = new FlowPanel();
		commentPanel.setStyleName( FiscalModelUtils.getAdministrationBG(currentMod131.getAdministration()) );
		commentPanel.setStyleName(AON.AON_CSS.aonHeightAll());
		commentPanel.addStyleName(AON.AON_CSS.aonTextCenter());
		TextArea comment = new TextArea();
		comment.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				currentMod131.setComments(event.getValue());
				styleCommentsButton();
				fiscalService.saveCommentsMod131(getCurrentDomainName(), currentMod131, new AsyncCallback<Mod131>() {
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
				
				
				
			}
		});
		comment.setText(currentMod131.getComments());
		comment.setWidth("90%");
		comment.setHeight("5em");
		commentPanel.add(comment);
		toast.show(AON.MSG.comments(), commentPanel);
	}
	
	private void styleCommentsButton() {
		if (AonStringUtils.isEmpty(currentMod131.getComments())) {
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
						submitForm(MODEL131_PRINT);
					}
	
					@Override
					public void onCancel() {
						// Nothing
					}
				});
		} else {
			submitForm(MODEL131_PRINT);
		}
			
	}

	@UiHandler("printPDFButton")
	void onPrintPDFButtonClick(ClickEvent event) {
		if (isDirty()) {
			new ConfirmDialog().confirm(AON.MSG.draftPrint(),AON.MSG.draftPrintNote() 
				, new ConfirmDialogCallback() {
				
				@Override
				public void onAccept() {
					submitForm(MODEL131_PRINT_PDF);
				}
	
				@Override
				public void onCancel() {
					// Nothing
				}
			});
		} else {
			submitForm(MODEL131_PRINT_PDF);
		}
	}
	
	@UiHandler("generateFileButton")
	void onGenerateFileButtonClick(ClickEvent event) {
		if (isDirty()) {
			new ConfirmDialog().confirm(AON.MSG.fileGeneration(),AON.MSG.fileGenerationNote() 
				, new ConfirmDialogCallback() {
				
				@Override
				public void onAccept() {
					submitForm(MODEL131_FILE);
				}
	
				@Override
				public void onCancel() {
					// Nothing
				}
			});
		} else {
			submitForm(MODEL131_FILE);
		}
	}

	private void submitForm(String action) {
		diskForm.setAction(GWT.getHostPageBaseURL() + action);
		mod131Hidden.setValue(String.valueOf(currentMod131.getId()));
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
					submitForm(MODEL131_PRINT_AEAT);
				}

				@Override
				public void onCancel() {
					// Nothing
				}
			});
	}

	@UiHandler("confidential")
	void onConfidentialClick(ClickEvent event) {
		currentMod131.setConfidential(confidential.getValue());
		setDirty(true);
	}
	
	@UiHandler("replacedNumber")
	void onConfidentialChange(ChangeEvent event) {
		currentMod131.setReplacedNumber(replacedNumber.getValue());
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
	
	private void showInfoPanel(String htmlText) {
		openFootPanelIfNeeded();
		tabLayout.selectTab(INFORMATION_TAB);
		HTMLPanel panel = new HTMLPanel(htmlText);
		informationPanel.setWidget(panel);
		informationPanel.scrollToTop();
	}

	private void showFinalizePopup() {
		FinishDeclarationPopup<Mod131> finalizeDialog = new FinishDeclarationPopup<>( 
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
	 
}
