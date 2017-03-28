package com.esferalia.aon.gwt.fiscal.client.mod202;

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
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod202;
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

public class Model202 extends MainEntryPoint {

	final static int IDENTIFICATION_TAB = 0;
	final static int LIQUIDATION_TAB = 1;
	final static int FISCAL_INFORMATION_TAB = 2;
	
	final static int NOTIFICATIONS_TAB = 0;
	final static int INFORMATION_TAB = 1;

	static FiscalServiceAsync fiscalService;
	
	interface Model202Binder extends UiBinder<Widget, Model202> {
	}
	private static final Model202Binder MODEL_202_BINDER = GWT
			.create(Model202Binder.class);
	
	private static final String MODEL202_PRINT = "/aon_gwt_fiscal/Model202Print";
	private static final String MODEL202_PRINT_PDF ="/aon_gwt_fiscal/Model202PrintPDF";
	private static final String MODEL202_FILE = "/aon_gwt_fiscal/Model202File";
	private static final String MODEL202_PRINT_AEAT = "/aon_gwt_fiscal/Model202PrintAEAT";

	public static interface IMod202Declaration extends IsWidget {
		Widget getInfoPanel(Mod202 mod202);
		void calculateAndRefresh(IFiscalModelCallback<Mod202> callback);
	}
	
	private Mod202 currentMod202;
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
	FiscalModelTable<Mod202> table;

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
	private IMod202Declaration declaration;
	@UiField
	ScrollPanel infoContainer;

	FormPanel diskForm;
	Hidden mod202Hidden;
	Hidden domainIdHidden;
	Hidden domainNameHidden;
	
	private abstract class FiscalModelCallback implements IFiscalModelCallback<Mod202> {
		
		@Override
		public void showErrorMsg(String msg) {
			showErrorMessage(msg);
		}
		
		@Override
		public void showInfoPanel(String htmlText) {
			Model202.this.showInfoPanel(htmlText);
		}
		
		@Override
		public boolean isFinished() {
			return (currentMod202.getStatus() == FiscalStatus.FINISHED);
		}
		
		@Override
		public Mod202 getFiscalModel() {
			return currentMod202;
		}

		@Override
		public boolean isDirty() {
			return Model202.this.isDirty();
		}
		@Override
		public void markAsDirty() {
			if (!isDirty()) {
				Model202.this.setDirty(true);
			}
		}

		@Override
		public void identificationLabelChanged() {
			documentLabel.setText(currentMod202.getDocument());
			nameLabel.setText(currentMod202.getName());
			surnameLabel.setText(currentMod202.getSurname());
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
	

	@Override
	public void onModuleLoad() {
		AON.ensureInjected();

		FiscalServiceAsync fiscalServiceRaw = GWT.create(FiscalService.class);
		fiscalService = new FiscalServiceAsyncDecorator(fiscalServiceRaw);

		table = new FiscalModelTable<Mod202>(new Mod202SelectionHandler(), new FiscalModelProvidesKey<Mod202>());

		Widget ui = MODEL_202_BINDER.createAndBindUi(this);

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
		mod202Hidden = new Hidden("mod202");
		formFlowPanel.add(mod202Hidden);
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

	public static native int getCurrentDomain()
	/*-{
		return $wnd.getCurrentDomain();
	}-*/;

	
	class Mod202SelectionHandler implements SelectionChangeEvent.Handler {
		@Override
		public void onSelectionChange(SelectionChangeEvent event) {
			Mod202 sel = table.getSelected();
			fiscalService.getMod202(getCurrentDomainName(), getCurrentDomain(),
					sel.getId(), new AsyncCallback<Mod202>() {
						@Override
						public void onSuccess(Mod202 selected) {
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
		
		boolean updatable = (currentMod202.getYear()>= 2017);
		
		deleteButton.setVisible(!currentMod202.isNew());
		auditButton.setVisible(!currentMod202.isNew());
		newButton.setVisible(!currentMod202.isNew());
		cancelButton.setVisible(true);
		saveButton.setVisible(true);
		
		saveButton.setEnabled(updatable && !currentMod202.isFinished());
		deleteButton.setEnabled(!currentMod202.isFinished());
		
		printButton.setVisible(!currentMod202.isNew());
		printPDFButton.setVisible(!currentMod202.isNew());
		
		reopenButton.setVisible(updatable && !currentMod202.isNew() && currentMod202.getStatus() == FiscalStatus.FINISHED);
		finalizeButton.setVisible(updatable && !currentMod202.isNew() && currentMod202.getStatus() == FiscalStatus.PENDING );
		
		generateFileButton.setStyleName(AON.AON_CSS.aonIconCommandButton());
		generateFileButton.setVisible(!currentMod202.isNew());
		generateFileButton.setEnabled(currentMod202.isFinished() && currentMod202.getYear() > 2015);
		generateFileButton.addStyleName(
				generateFileButton.isEnabled()
					?FiscalModelUtils.getAdministrationIcon(currentMod202.getAdministration())
					:FiscalModelUtils.getAdministrationIconBW(currentMod202.getAdministration())
							);
		// printViaAeatButton.setVisible(!currentMod202.isNew() && currentMod202.isAEAT() && currentMod202.isFinished());
		printViaAeatButton.setVisible( false );
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
	
	private void select(Mod202 selected) {
		currentMod202 = selected;
		dirty = false;
		
		documentLabel.setText(currentMod202.getDocument());
		nameLabel.setText(currentMod202.getName());
		surnameLabel.setText(currentMod202.getSurname());
		
		styleDirtyLabel();
		styleStatusLabel();
		if (currentMod202.isReplacementDeclarationAvailable()) {
			replacementLabel.setText(AON.MSG.replacement());
			replacementLabel.setStyleName(AON.AON_CSS.aonIconPaddingLeft());
			replacementLabel.addStyleName(currentMod202.isReplacement()
					?AON.AON_CSS.aonIconChecked()
					:AON.AON_CSS.aonIconCheck()
				);
		} else {
			replacementLabel.setText("");
		}
		
		if (currentMod202.isComplementaryDeclarationAvailable()) {
			complementaryLabel.setText(AON.MSG.complementary());
			complementaryLabel.setStyleName(AON.AON_CSS.aonIconPaddingLeft());
			complementaryLabel.addStyleName(currentMod202.isComplementary()
					?AON.AON_CSS.aonIconChecked()
					:AON.AON_CSS.aonIconCheck()
				);
		} else {
			complementaryLabel.setText("");
		}
		
		replacedNumber.setValue(currentMod202.getReplacedNumber());
		replacedNumber.setEnabled(currentMod202.isReplacedNumberAvailable());
		
		confidential.setValue(currentMod202.isConfidential());
		domain = currentMod202.getDomain();
		
		styleCommentsButton();
		
		fiscalInformationLabel.setStyleName(AON.AON_CSS.aonPaddingRight());
		fiscalInformationLabel.addStyleName(AON.AON_CSS.aonPaddingLeft20());
		fiscalInformationLabel.addStyleName(FiscalModelUtils.getAdministrationIconBW(currentMod202.getAdministration()));

		refreshToolbarState();
		
		FiscalModelUtils.paintPaymentInfo(paymentInfo,currentMod202);
		FiscalModelUtils.paintHeaderTable(headerPanel,currentMod202);
		
		FiscalModelCallback callback = new FiscalModelCallback(){
			@Override
			public void onAccept() {}

			@Override
			public void onCancel() {}
			
		}; 
		FiscalModelIdentificationData<Mod202> identificationData = new FiscalModelIdentificationData<Mod202>(callback);
		identificationContainer.setWidget( identificationData);
		if (currentMod202.getAdministration() == Administration.COMMON_TERRITORY) {
			declaration = new Model202AEAT(callback);
//		} else  if (currentMod202.getAdministration() == Administration.GIPUZKOA) {
//			if (currentMod202.getPeriod().isQuarterPeriod()) {
//				declaration = new Model110Gipuzkoa(callback);
//			} else {
//				declaration = new Model202Gipuzkoa(callback);
//			}
//		} else  if (currentMod202.getAdministration() == Administration.BIZKAIA) {
//			if (currentMod202.getPeriod().isQuarterPeriod()) {
//				declaration = new Model110Bizkaia(callback);
//			} else {
//				declaration = new Model202Bizkaia(callback);
//			}
//		} else  if (currentMod202.getAdministration() == Administration.NAVARRA) {
//			if (currentMod202.getPeriod().isQuarterPeriod()) {
//				declaration = new Model715Navarra(callback);
//			} else {
//				declaration = new Model745Navarra(callback);
//			}
//		} else  if (currentMod202.getAdministration() == Administration.ALAVA) {
//			if (currentMod202.getYear() > 2015) {
//				declaration = new Model202Araba2016(callback);	
//			} else {
//				declaration = new Model202Araba(callback);
//			}
		}
		if (declaration != null) {
			declarationContainer.setWidget( declaration );
			infoContainer.setWidget(declaration.getInfoPanel(currentMod202));
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
		statusLabel.setText(currentMod202.getStatus().getName());
		statusLabel.setStyleName(
				currentMod202.getStatus() == FiscalStatus.FINISHED
					?AON.AON_CSS.aonIconLock()
					:AON.AON_CSS.aonIconUnlock()
				);
		statusLabel.addStyleName(AON.AON_CSS.aonIconPaddingLeft());
	}

	@UiHandler("table")
	void onTableRangeChange(RangeChangeEvent event) {
		fiscalService.getMod202s(getCurrentDomainName(), getCurrentDomain(),
				new AsyncCallback<LinkedList<Mod202>>() {
					@Override
					public void onSuccess(LinkedList<Mod202> result) {
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
		fiscalService.reopenMod202(getCurrentDomainName(), this.currentMod202, new AsyncCallback<Mod202>() {
					@Override
					public void onSuccess(Mod202 result) {
						select(result);
						popup.hide();
						reopenButton.setEnabled(true);
						FiscalModelUtils.paintPaymentInfo(paymentInfo,currentMod202);
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
		fiscalService.initializeForFinishMod202(getCurrentDomainName(),currentMod202,
				new AsyncCallback<Mod202>() {
					@Override
					public void onSuccess(Mod202 m202) {
						currentMod202 = m202;
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
		fiscalService.saveMod202(getCurrentDomainName(), this.currentMod202, new AsyncCallback<Mod202>() {
					@Override
					public void onSuccess(Mod202 result) {
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
		finalizeButton.setEnabled(false);
		cleanErrorMessage();
		final PopupPanel popup = new PopupPanel(false, true);
		Label label = new Label(AON.MSG.processing());
		label.addStyleName(AON.AON_CSS.aonTimer());
		popup.add(label);
		popup.setGlassEnabled(true);
		popup.setAnimationEnabled(true);
		popup.center();
		fiscalService.finishMod202(getCurrentDomainName(), this.currentMod202, new AsyncCallback<Mod202>() {
					@Override
					public void onSuccess(Mod202 result) {
						select(result);
						popup.hide();
						finalizeButton.setEnabled(true);
						FiscalModelUtils.paintPaymentInfo(paymentInfo,currentMod202);
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
				fiscalService.deleteMod202(getCurrentDomainName(),currentMod202, new AsyncCallback<Void>() {
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
		
		fiscalService.initializeMod202(getCurrentDomainName(),getCurrentDomain(),null,
				new AsyncCallback<Mod202>() {
					@Override
					public void onSuccess(Mod202 m202) {
						currentMod202 = m202;
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
		Model202NewDeclarationPopup newDialog = new Model202NewDeclarationPopup(
			new FiscalModelCallback() {

					@Override
					public void onAccept() {
						fiscalService.createMod202(getCurrentDomainName(),getCurrentDomain(),currentMod202,
								new AsyncCallback<Mod202>() {
									@Override
									public void onSuccess(Mod202 m202) {
										int i = deckPanel.getWidgetIndex(formPanel);
										deckPanel.showWidget(i);
										tabPanel.selectTab(IDENTIFICATION_TAB);
										select(m202);
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
					public Mod202 getFiscalModel() {
						return currentMod202;
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
		dialog.show(currentMod202);
	}

	// -------------------------------------------------------------- UiHandler
	@UiHandler("commentsButton")
	public void onComments(ClickEvent event) {
		final AonToast toast = new AonToast();
		FlowPanel commentPanel = new FlowPanel();
		commentPanel.setStyleName( FiscalModelUtils.getAdministrationBG(currentMod202.getAdministration()) );
		commentPanel.setStyleName(AON.AON_CSS.aonHeightAll());
		commentPanel.addStyleName(AON.AON_CSS.aonTextCenter());
		TextArea comment = new TextArea();
		comment.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				currentMod202.setComments(event.getValue());
				styleCommentsButton();
				fiscalService.saveCommentsMod202(getCurrentDomainName(), currentMod202, new AsyncCallback<Mod202>() {
					@Override
					public void onSuccess(Mod202 result) {
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
		comment.setText(currentMod202.getComments());
		comment.setWidth("90%");
		comment.setHeight("5em");
		commentPanel.add(comment);
		toast.show(AON.MSG.comments(), commentPanel);
	}
	
	private void styleCommentsButton() {
		if (AonStringUtils.isEmpty(currentMod202.getComments())) {
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
						//showViewer();
						submitForm(MODEL202_PRINT);
					}
	
					@Override
					public void onCancel() {
						// Nothing
					}
				});
		} else {
			//showViewer();
			submitForm(MODEL202_PRINT);
		}
	}

	@UiHandler("printPDFButton")
	void onPrintPDFButtonClick(ClickEvent event) {
		if (isDirty()) {
			new ConfirmDialog().confirm(AON.MSG.draftPrint(),AON.MSG.draftPrintNote() 
				, new ConfirmDialogCallback() {
				
				@Override
				public void onAccept() {
					submitForm(MODEL202_PRINT_PDF);
				}
	
				@Override
				public void onCancel() {
					// Nothing
				}
			});
		} else {
			submitForm(MODEL202_PRINT_PDF);
		}
	}
	
	@UiHandler("generateFileButton")
	void onGenerateFileButtonClick(ClickEvent event) {
		if (isDirty()) {
			new ConfirmDialog().confirm(AON.MSG.fileGeneration(),AON.MSG.fileGenerationNote() 
				, new ConfirmDialogCallback() {
				
				@Override
				public void onAccept() {
					submitForm(MODEL202_FILE);
				}
	
				@Override
				public void onCancel() {
					// Nothing
				}
			});
		} else {
			submitForm(MODEL202_FILE);
		}
	}

	private void submitForm(String action) {
		diskForm.setAction(GWT.getHostPageBaseURL() + action);
		mod202Hidden.setValue(String.valueOf(currentMod202.getId()));
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
					submitForm(MODEL202_PRINT_AEAT);
				}

				@Override
				public void onCancel() {
					// Nothing
				}
			});
	}

	@UiHandler("confidential")
	void onConfidentialClick(ClickEvent event) {
		currentMod202.setConfidential(confidential.getValue());
		setDirty(true);
	}
	
	@UiHandler("replacedNumber")
	void onConfidentialChange(ChangeEvent event) {
		currentMod202.setReplacedNumber(replacedNumber.getValue());
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
		FinishDeclarationPopup<Mod202> finalizeDialog = new FinishDeclarationPopup<>( 
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
