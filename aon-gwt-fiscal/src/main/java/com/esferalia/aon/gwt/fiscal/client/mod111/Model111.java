package com.esferalia.aon.gwt.fiscal.client.mod111;

import java.util.ArrayList;
import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.common.client.widget.AdministrationListBox;
import com.esferalia.aon.gwt.common.client.widget.AonToast;
import com.esferalia.aon.gwt.common.client.widget.AuditDialog;
import com.esferalia.aon.gwt.common.client.widget.ConfirmDialog;
import com.esferalia.aon.gwt.common.client.widget.ConfirmDialog.ConfirmDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.CreditorBox;
import com.esferalia.aon.gwt.common.client.widget.CustomDialog;
import com.esferalia.aon.gwt.common.client.widget.IbanTextBox;
import com.esferalia.aon.gwt.common.client.widget.IbanTextBox.IbanSuggestion;
import com.esferalia.aon.gwt.common.client.widget.IntegerBox;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel.MaximizeEvent;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel.MinimizeEvent;
import com.esferalia.aon.gwt.common.client.widget.PeriodListBox;
import com.esferalia.aon.gwt.common.client.widget.ResultsPanel;
import com.esferalia.aon.gwt.fiscal.client.FiscalModelUtils;
import com.esferalia.aon.gwt.fiscal.client.FiscalService;
import com.esferalia.aon.gwt.fiscal.client.FiscalServiceAsync;
import com.esferalia.aon.gwt.fiscal.client.FiscalServiceAsyncDecorator;
import com.esferalia.aon.gwt.fiscal.client.MainEntryPoint;
import com.esferalia.aon.gwt.fiscal.client.model.FiscalModelIdentificationData;
import com.esferalia.aon.gwt.fiscal.client.model.FiscalModelProvidesKey;
import com.esferalia.aon.gwt.fiscal.client.model.FiscalModelTable;
import com.esferalia.aon.gwt.fiscal.client.model.IFiscalModelCallback;
import com.esferalia.aon.occam.api.model.CompanyBank;
import com.esferalia.aon.occam.api.model.IIbanContainer;
import com.esferalia.aon.occam.api.model.finance.BankAccount;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod111;
import com.esferalia.aon.occam.api.model.registry.Creditor;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.Mod111DeclarationType;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
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
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HTMLTable.ColumnFormatter;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.SuggestOracle;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.RangeChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent;

public class Model111 extends MainEntryPoint {

	final static int IDENTIFICATION_TAB = 0;
	final static int LIQUIDATION_TAB = 1;
	
	final static int NOTIFICATIONS_TAB = 0;
	final static int INFORMATION_TAB = 1;

	static FiscalServiceAsync fiscalService;
	static CommonServiceAsync commonService;
	
	interface Model111Binder extends UiBinder<Widget, Model111> {
	}
	private static final Model111Binder MODEL_111_BINDER = GWT
			.create(Model111Binder.class);

	public static interface IMod111Declaration extends IsWidget {
		Widget getInfoPanel(Mod111 mod111);
		void calculateAndRefresh(Mod111 mod111);
	}
	
	private Mod111 currentMod111;
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
	IntegerBox replacedNumber;
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

	@Override
	public void onModuleLoad() {
		AON.ensureInjected();

		FiscalServiceAsync fiscalServiceRaw = GWT.create(FiscalService.class);
		fiscalService = new FiscalServiceAsyncDecorator(fiscalServiceRaw);
		CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
		commonService = new CommonServiceAsyncDecorator(commonServiceRaw);
				

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

	public static native int getCurrentDomain()
	/*-{
		return $wnd.getCurrentDomain();
	}-*/;

	
	class Mod111SelectionHandler implements SelectionChangeEvent.Handler {
		@Override
		public void onSelectionChange(SelectionChangeEvent event) {
			Mod111 sel = table.getSelected();
			fiscalService.getMod111(getCurrentDomainName(), getCurrentDomain(),
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
		deleteButton.setVisible(!currentMod111.isNew());
		auditButton.setVisible(!currentMod111.isNew());
		newButton.setVisible(!currentMod111.isNew());
		cancelButton.setVisible(true);
		saveButton.setVisible(true);
		
		saveButton.setEnabled(!currentMod111.isFinished());
		deleteButton.setEnabled(!currentMod111.isFinished());
		
		printButton.setVisible(!currentMod111.isNew());
		printPDFButton.setVisible(!currentMod111.isNew());
		
		reopenButton.setVisible(!currentMod111.isNew() && currentMod111.getStatus() == FiscalStatus.FINISHED);
		finalizeButton.setVisible(!currentMod111.isNew() && currentMod111.getStatus() == FiscalStatus.PENDING );
		
		generateFileButton.setStyleName(AON.AON_CSS.aonIconCommandButton());
		generateFileButton.setVisible(!currentMod111.isNew());
		generateFileButton.setEnabled(currentMod111.isFinished ());
		generateFileButton.addStyleName(
				generateFileButton.isEnabled()
					?FiscalModelUtils.getAdministrationIcon(currentMod111.getAdministration())
					:FiscalModelUtils.getAdministrationIconBW(currentMod111.getAdministration())
							);
		printViaAeatButton.setVisible(!currentMod111.isNew() && currentMod111.isAEAT() && currentMod111.isFinished());
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
	
	private void select(Mod111 selected) {
		currentMod111 = selected;
		dirty = false;
		
		documentLabel.setText(currentMod111.getDocument());
		nameLabel.setText(currentMod111.getName());
		surnameLabel.setText(currentMod111.getSurname());
		
		styleDirtyLabel();
		styleStatusLabel();
		if (currentMod111.isReplacementDeclarationAvailable()) {
			replacementLabel.setText(AON.MSG.replacement());
			replacementLabel.setStyleName(AON.AON_CSS.aonIconPaddingLeft());
			replacementLabel.addStyleName(currentMod111.isReplacement()
					?AON.AON_CSS.aonIconChecked()
					:AON.AON_CSS.aonIconCheck()
				);
		} else {
			replacementLabel.setText("");
		}
		
		if (currentMod111.isComplementaryDeclarationAvailable()) {
			complementaryLabel.setText(AON.MSG.complementary());
			complementaryLabel.setStyleName(AON.AON_CSS.aonIconPaddingLeft());
			complementaryLabel.addStyleName(currentMod111.isReplacement()
					?AON.AON_CSS.aonIconChecked()
					:AON.AON_CSS.aonIconCheck()
				);
		} else {
			complementaryLabel.setText("");
		}
		
		replacedNumber.setValue(currentMod111.getReplacedNumber());
		replacedNumber.setEnabled(currentMod111.isReplacedNumberAvailable());
		
		confidential.setValue(currentMod111.isConfidential());
		domain = currentMod111.getDomain();
		
		styleCommentsButton();
		
		fiscalInformationLabel.setStyleName(AON.AON_CSS.aonPaddingRight());
		fiscalInformationLabel.addStyleName(AON.AON_CSS.aonPaddingLeft20());
		fiscalInformationLabel.addStyleName(FiscalModelUtils.getAdministrationIconBW(currentMod111.getAdministration()));

		refreshToolbarState();
		
		paintPaymentInfo();
		
		FiscalModelUtils.paintHeaderTable(headerPanel,currentMod111);
		
		IFiscalModelCallback<Mod111> callback = new IFiscalModelCallback<Mod111>() {
			
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
				return (currentMod111.getStatus() == FiscalStatus.FINISHED);
			}
			
			@Override
			public Mod111 getFiscalModel() {
				return currentMod111;
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
				documentLabel.setText(currentMod111.getDocument());
				nameLabel.setText(currentMod111.getName());
				surnameLabel.setText(currentMod111.getSurname());
			}
		};
		
		FiscalModelIdentificationData<Mod111> identificationData = new FiscalModelIdentificationData<Mod111>(callback);
		identificationContainer.setWidget( identificationData);
		if (currentMod111.getAdministration() == Administration.COMMON_TERRITORY) {
			declaration = new Model111AEAT(callback);
		} else  if (currentMod111.getAdministration() == Administration.GIPUZKOA) {
			if (currentMod111.getPeriod().isQuarterPeriod()) {
				declaration = new Model110Gipuzkoa(callback);
			} else {
				declaration = new Model111Gipuzkoa(callback);
			}
		} else  if (currentMod111.getAdministration() == Administration.BIZKAIA) {
			if (currentMod111.getPeriod().isQuarterPeriod()) {
				declaration = new Model110Bizkaia(callback);
			} else {
				declaration = new Model111Bizkaia(callback);
			}
		} else  if (currentMod111.getAdministration() == Administration.NAVARRA) {
			if (currentMod111.getPeriod().isQuarterPeriod()) {
				declaration = new Model715Navarra(callback);
			} else {
				declaration = new Model745Navarra(callback);
			}
		} else  if (currentMod111.getAdministration() == Administration.ALAVA) {
			if (currentMod111.getYear() > 2015) {
				declaration = new Model111Araba2016(callback);	
			} else {
				declaration = new Model111Araba(callback);
			}
		}
		if (declaration != null) {
			declarationContainer.setWidget( declaration );
			infoContainer.setWidget(declaration.getInfoPanel(currentMod111));
		} else {
			showErrorMessage("Administraci\u00F3n y/o ejercicio no soportado.");
			hideToolbarButtons();
			cancelButton.setVisible(true);
		}
	}
	
	private void paintPaymentInfo() {
		paymentInfo.clear();
		paymentInfo.setVisible(currentMod111.isFinished());
		InlineLabel l1 = new InlineLabel(AON.MSG.result());
		l1.setStyleName(AON.AON_CSS.aonInnerLabel());
		paymentInfo.add(l1);
		InlineLabel l2 = new InlineLabel(AON.FMT.format(currentMod111.getResult()));
		l2.setStyleName(AON.AON_CSS.aonInnerLabel());
		l2.addStyleName(AON.AON_CSS.aonBold());
		paymentInfo.add(l2);
		if (currentMod111.getDeclarationType() != null) {
			InlineLabel l3 = new InlineLabel(currentMod111.getDeclarationType().getDescription());
			l3.setStyleName(AON.AON_CSS.aonInnerLabel());
			l3.addStyleName(AON.AON_CSS.aonBold());
			paymentInfo.add(l3);
		}
		if (currentMod111.getFinance() != null && currentMod111.getFinance().getBankAccount() != null) {
			InlineLabel l4 = new InlineLabel(currentMod111.getFinance().getBankAccount().getIban());
			l4.setStyleName(AON.AON_CSS.aonInnerLabel());
			l4.addStyleName(AON.AON_CSS.aonBold());
			paymentInfo.add(l4);
			
			InlineLabel l5 = new InlineLabel(currentMod111.getFinance().getBankAlias());
			l5.setStyleName(AON.AON_CSS.aonInnerLabel());
			paymentInfo.add(l5);
		}
	}

	private void styleDirtyLabel() {
		dirtyLabel.setText(isDirty()?"[CAMBIOS]":"");
	}

	private void styleStatusLabel() {
		statusLabel.setText(currentMod111.getStatus().getName());
		statusLabel.setStyleName(
				currentMod111.getStatus() == FiscalStatus.FINISHED
					?AON.AON_CSS.aonIconLock()
					:AON.AON_CSS.aonIconUnlock()
				);
		statusLabel.addStyleName(AON.AON_CSS.aonIconPaddingLeft());
	}

	@UiHandler("table")
	void onTableRangeChange(RangeChangeEvent event) {
		fiscalService.getMod111s(getCurrentDomainName(), getCurrentDomain(),
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
		fiscalService.reopenMod111(getCurrentDomainName(), this.currentMod111, new AsyncCallback<Mod111>() {
					@Override
					public void onSuccess(Mod111 result) {
						select(result);
						popup.hide();
						reopenButton.setEnabled(true);
						paintPaymentInfo();
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
		fiscalService.initializeForFinishMod111(getCurrentDomainName(),currentMod111,
				new AsyncCallback<Mod111>() {
					@Override
					public void onSuccess(Mod111 m111) {
						currentMod111 = m111;
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
		fiscalService.saveMod111(getCurrentDomainName(), this.currentMod111, new AsyncCallback<Mod111>() {
					@Override
					public void onSuccess(Mod111 result) {
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
		fiscalService.finishMod111(getCurrentDomainName(), this.currentMod111, new AsyncCallback<Mod111>() {
					@Override
					public void onSuccess(Mod111 result) {
						select(result);
						popup.hide();
						finalizeButton.setEnabled(true);
						paintPaymentInfo();
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
				fiscalService.deleteMod111(getCurrentDomainName(),currentMod111, new AsyncCallback<Void>() {
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
		
		fiscalService.initializeMod111(getCurrentDomainName(),getCurrentDomain(),null,
				new AsyncCallback<Mod111>() {
					@Override
					public void onSuccess(Mod111 m111) {
						currentMod111 = m111;
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
		final CustomDialog newDialog = new CustomDialog();
		newDialog.setCaption(AON.MSG.newDeclaration());
		newDialog.setGlassEnabled(true);
		newDialog.setAnimationEnabled(true);
		
		FlexTable tab = new FlexTable();
		
		tab.setCellPadding(0);
		tab.setCellSpacing(0);
		tab.setStyleName(AON.AON_CSS.aonMarginTop());
		tab.addStyleName(AON.AON_CSS.aonMarginBottom());
		tab.addStyleName(AON.AON_CSS.aonPanelGrid());
		ColumnFormatter cf = tab.getColumnFormatter();
		cf.setWidth(0, "130px");
		cf.addStyleName(0, AON.AON_CSS.aonPaddingLeft() );
		cf.addStyleName(0, AON.AON_CSS.aonPaddingRight() );
		cf.setWidth(1, "250px");
		cf.addStyleName(0, AON.AON_CSS.aonPaddingLeft() );
		cf.addStyleName(0, AON.AON_CSS.aonPaddingRight() );
		FlexCellFormatter fmt = tab.getFlexCellFormatter();
		
		final Label previousLabel = new Label(AON.MSG.previousDeclaration()); 
		previousLabel.setVisible(currentMod111.isReplacedNumberAvailable());
		final IntegerBox previous = new IntegerBox();
		previous.setVisible(currentMod111.isReplacedNumberAvailable());
		
		final CheckBox replacement = new CheckBox(AON.MSG.replacement());
		final CheckBox complementary = new CheckBox(AON.MSG.complementary());
		replacement.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				currentMod111.setReplacement(replacement.getValue());
				complementary.setEnabled(!replacement.getValue());
				previousLabel.setVisible(currentMod111.isReplacedNumberAvailable());
				previous.setVisible(currentMod111.isReplacedNumberAvailable());
				if (replacement.getValue()) {
					complementary.setValue(false);
				}
			}
		});
		replacement.setVisible(currentMod111.isReplacementDeclarationAvailable());
		
		complementary.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				currentMod111.setComplementary(complementary.getValue());
				replacement.setEnabled(!complementary.getValue());
				previousLabel.setVisible(currentMod111.isReplacedNumberAvailable());
				previous.setVisible(currentMod111.isReplacedNumberAvailable());
				if (complementary.getValue()) {
					replacement.setValue(false);
				}
			}
		});
		complementary.setVisible(currentMod111.isComplementaryDeclarationAvailable());
		
		int row = 0;
		fmt.addStyleName(row, 0, AON.AON_CSS.aonPanelGridOdd());
		tab.setWidget(row, 0, new Label(AON.MSG.administration()));
		fmt.addStyleName(row, 1, AON.AON_CSS.aonPanelGridEven());
		final AdministrationListBox admonList = new AdministrationListBox();
		admonList.setSelectedIndex( currentMod111.getAdministration().ordinal());
		admonList.addChangeHandler( new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				currentMod111.setAdministration( admonList.getValue() );
				replacement.setVisible(currentMod111.isReplacementDeclarationAvailable());
				complementary.setVisible(currentMod111.isComplementaryDeclarationAvailable());
				previousLabel.setVisible(currentMod111.isReplacedNumberAvailable());
				previous.setVisible(currentMod111.isReplacedNumberAvailable());
			}
		});
		tab.setWidget(row, 1, admonList);
		row++;
		
		fmt.addStyleName(row, 0, AON.AON_CSS.aonPanelGridOdd());
		tab.setWidget(row, 0, new Label(AON.MSG.year()));
		fmt.addStyleName(row, 1, AON.AON_CSS.aonPanelGridEven());
		final IntegerBox yearBox = new IntegerBox();
		yearBox.setValue(currentMod111.getYear());
		yearBox.setMaxLength(4);
		yearBox.setVisibleLength(4);
		yearBox.addValueChangeHandler(new ValueChangeHandler<Integer>() {
			@Override
			public void onValueChange(ValueChangeEvent<Integer> event) {
				currentMod111.setYear(yearBox.getValue());
			}
		});
		tab.setWidget(row, 1,yearBox);
		row++;
		
		fmt.addStyleName(row, 0, AON.AON_CSS.aonPanelGridOdd());
		tab.setWidget(row, 0, new Label(AON.MSG.period()));
		fmt.addStyleName(row, 1, AON.AON_CSS.aonPanelGridEven());
		final PeriodListBox periodList = new PeriodListBox();
		if (currentMod111.getPeriod() != null) {
			periodList.setSelectedIndex(currentMod111.getPeriod().ordinal() + 1);
		}
		periodList.addChangeHandler( new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				currentMod111.setPeriod( periodList.getValue() );
			}
		});
		tab.setWidget(row, 1, periodList);
		row++;
		
		fmt.addStyleName(row, 0, AON.AON_CSS.aonPanelGridOdd());
		tab.setWidget(row, 0, new Label());
		fmt.addStyleName(row, 1, AON.AON_CSS.aonPanelGridEven());
		tab.setWidget(row, 1, replacement);
		row++;
		
		fmt.addStyleName(row, 0, AON.AON_CSS.aonPanelGridOdd());
		tab.setWidget(row, 0, new Label());
		fmt.addStyleName(row, 1, AON.AON_CSS.aonPanelGridEven());
		tab.setWidget(row, 1, complementary);
		row++;
		
		fmt.addStyleName(row, 0, AON.AON_CSS.aonPanelGridOdd());
		tab.setWidget(row, 0, previousLabel);
		fmt.addStyleName(row, 1, AON.AON_CSS.aonPanelGridEven());
		previous.setMaxLength(13);
		previous.setVisibleLength(13);
		previous.setValue(currentMod111.getReplacedNumber());
		previous.addChangeHandler( new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				currentMod111.setReplacedNumber( previous.getValue() );
			}
		});
		tab.setWidget(row, 1, previous);
		row++;

		fmt.setColSpan(row, 0, 2);
		fmt.addStyleName(row, 0, AON.AON_CSS.aonPanelGridEven());
		FlowPanel flowPanel = new FlowPanel();
		flowPanel.setStyleName(AON.AON_CSS.aonPadding());
		flowPanel.addStyleName(AON.AON_CSS.aonMarginTop());
		flowPanel.addStyleName(AON.AON_CSS.aonTextCenter());
		Button acceptButton = new Button();
		acceptButton.setStyleName(AON.AON_CSS.aonConfirmDialogOkButton());
		acceptButton.setText( AON.MSG.accept());
		acceptButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				fiscalService.createMod111(getCurrentDomainName(),getCurrentDomain(),currentMod111,
						new AsyncCallback<Mod111>() {
							@Override
							public void onSuccess(Mod111 m111) {
								newDialog.hide();
								int i = deckPanel.getWidgetIndex(formPanel);
								deckPanel.showWidget(i);
								tabPanel.selectTab(IDENTIFICATION_TAB);
								select(m111);
							}

							@Override
							public void onFailure(Throwable caught) {
								newDialog.hide();
								showErrorMessage(AON.MSG.unableToReadFiscalParameters(caught.getMessage()));
							}
						});
			}
		});
		flowPanel.add(acceptButton);
		Button cancelButton = new Button();
    	cancelButton.setStyleName(AON.AON_CSS.aonConfirmDialogCancelButton());
    	cancelButton.addStyleName(AON.AON_CSS.aonMarginLeft());
    	cancelButton.setText( AON.MSG.cancelAction());
		cancelButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				newDialog.hide();
			}
			
		});
		flowPanel.add(cancelButton);
		tab.setWidget(row, 0, flowPanel);
		
		newDialog.add(tab);
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
		dialog.show(currentMod111);
	}

	// -------------------------------------------------------------- UiHandler
	@UiHandler("commentsButton")
	public void onComments(ClickEvent event) {
		final AonToast toast = new AonToast();
		FlowPanel commentPanel = new FlowPanel();
		commentPanel.setStyleName( FiscalModelUtils.getAdministrationBG(currentMod111.getAdministration()) );
		commentPanel.setStyleName(AON.AON_CSS.aonHeightAll());
		commentPanel.addStyleName(AON.AON_CSS.aonTextCenter());
		TextArea comment = new TextArea();
		comment.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				currentMod111.setComments(event.getValue());
				styleCommentsButton();
				fiscalService.saveCommentsMod111(getCurrentDomainName(), currentMod111, new AsyncCallback<Mod111>() {
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
		comment.setText(currentMod111.getComments());
		comment.setWidth("90%");
		comment.setHeight("5em");
		commentPanel.add(comment);
		toast.show(AON.MSG.comments(), commentPanel);
	}
	
	private void styleCommentsButton() {
		if (AonStringUtils.isEmpty(currentMod111.getComments())) {
			commentsButton.addStyleName(AON.AON_CSS.aonIconComment());
			commentsButton.removeStyleName(AON.AON_CSS.aonIconCommentRed());
		} else {
			commentsButton.addStyleName(AON.AON_CSS.aonIconCommentRed());
			commentsButton.removeStyleName(AON.AON_CSS.aonIconComment());
		}
	}
	
	@UiHandler("printButton")
	void onPrintButtonClick(ClickEvent event) {
		new ConfirmDialog().confirm(AON.MSG.draftPrint(),AON.MSG.draftPrintNote() 
				, new ConfirmDialogCallback() {
				
				@Override
				public void onAccept() {
					submitForm("/aon_gwt_fiscal/Model111Print");
				}

				@Override
				public void onCancel() {
					// Nothing
				}
			});
	}

	@UiHandler("printPDFButton")
	void onPrintPDFButtonClick(ClickEvent event) {
		new ConfirmDialog().confirm(AON.MSG.draftPrint(),AON.MSG.draftPrintNote() 
			, new ConfirmDialogCallback() {
			
			@Override
			public void onAccept() {
				submitForm("/aon_gwt_fiscal/Model111PrintPDF");
			}

			@Override
			public void onCancel() {
				// Nothing
			}
		});
	}
	
	@UiHandler("generateFileButton")
	void onGenerateFileButtonClick(ClickEvent event) {
		new ConfirmDialog().confirm(AON.MSG.fileGeneration(),AON.MSG.fileGenerationNote() 
			, new ConfirmDialogCallback() {
			
			@Override
			public void onAccept() {
				submitForm("/aon_gwt_fiscal/Model111File");
			}

			@Override
			public void onCancel() {
				// Nothing
			}
		});
	}

	private void submitForm(String action) {
		diskForm.setAction(GWT.getHostPageBaseURL() + action);
		mod111Hidden.setValue(String.valueOf(currentMod111.getId()));
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
					submitForm("/aon_gwt_fiscal/Model111PrintAEAT");
				}

				@Override
				public void onCancel() {
					// Nothing
				}
			});
	}

	@UiHandler("confidential")
	void onConfidentialClick(ClickEvent event) {
		currentMod111.setConfidential(confidential.getValue());
		setDirty(true);
	}
	
	@UiHandler("replacedNumber")
	void onConfidentialChange(ChangeEvent event) {
		currentMod111.setReplacedNumber(replacedNumber.getValue());
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
		SimplePanel panel = new SimplePanel();
		resultsPanel.setWidget(panel);
		closeFootPanel();
	}

	private void showErrorMessage(String msg) {
		openFootPanelIfNeeded();
		tabLayout.selectTab(NOTIFICATIONS_TAB);
		SimplePanel panel = new SimplePanel();
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
		final CustomDialog finalizeDialog = new CustomDialog();
		finalizeDialog.setCaption(AON.MSG.finish());
		finalizeDialog.setGlassEnabled(true);
		finalizeDialog.setAnimationEnabled(true);
		
		FlexTable tab = new FlexTable();
		
		tab.setCellPadding(0);
		tab.setCellSpacing(0);
		tab.setStyleName(AON.AON_CSS.aonMarginTop());
		tab.addStyleName(AON.AON_CSS.aonMarginBottom());
		tab.addStyleName(AON.AON_CSS.aonPanelGrid());
		ColumnFormatter cf = tab.getColumnFormatter();
		cf.setWidth(0, "130px");
		cf.addStyleName(0, AON.AON_CSS.aonPaddingLeft() );
		cf.addStyleName(0, AON.AON_CSS.aonPaddingRight() );
		cf.setWidth(1, "450px");
		cf.addStyleName(0, AON.AON_CSS.aonPaddingLeft() );
		cf.addStyleName(0, AON.AON_CSS.aonPaddingRight() );
		FlexCellFormatter fmt = tab.getFlexCellFormatter();
		
		int row = 0;
		fmt.addStyleName(row, 0, AON.AON_CSS.aonPanelGridOdd());
		tab.setWidget(row, 0, new Label(AON.MSG.fiscalDebt()));
		fmt.setStyleName(row, 1, AON.AON_CSS.aonPanelGridEven());
		fmt.addStyleName(row, 1, AON.AON_CSS.aonTextRight());
		fmt.addStyleName(row, 1, AON.AON_CSS.aonFontBig());
		fmt.addStyleName(row, 1, AON.AON_CSS.aonPaddingRight());
		fmt.addStyleName(row, 1, AON.AON_CSS.aonBold());
		tab.setWidget(row, 1, new Label( AON.FMT.format(currentMod111.getResult())));
		row++;
		
		fmt.addStyleName(row, 0, AON.AON_CSS.aonPanelGridOdd());
		tab.setWidget(row, 0, new Label(AON.MSG.declarationType()));
		fmt.setStyleName(row, 1, AON.AON_CSS.aonPanelGridEven());
		
		if (currentMod111.getDeclarationType() == Mod111DeclarationType.NEGATIVE) {
			fmt.addStyleName(row, 1, AON.AON_CSS.aonTextCenter());
			fmt.addStyleName(row, 1, AON.AON_CSS.aonBold());
			tab.setWidget(row, 1, new Label( Mod111DeclarationType.NEGATIVE.getDescription() ));
		} else {
			final CreditorBox creditorBox = new CreditorBox(getCurrentDomainName(),getCurrentDomain() );
			final IbanTextBox iban = new IbanTextBox( getSuggestOracle() );
			
			final ListBox listBox = new ListBox();
			listBox.setSelectedIndex(0);
			listBox.addItem(Mod111DeclarationType.DEPOSIT.getDescription(), Mod111DeclarationType.DEPOSIT.getValue());
			listBox.addItem(Mod111DeclarationType.BANK.getDescription(), Mod111DeclarationType.BANK.getValue());
			if (currentMod111.isAEAT()) {
				listBox.addItem(Mod111DeclarationType.CCT.getDescription(), Mod111DeclarationType.CCT.getValue());
			}
				listBox.addChangeHandler(new ChangeHandler() {
					@Override
					public void onChange(ChangeEvent event) {
						Mod111DeclarationType type = Mod111DeclarationType.safeValueOf(listBox.getSelectedValue());
						currentMod111.setDeclarationType( type );
						iban.setEnabled( type.isBankRequired() );
						creditorBox.setEnabled(type.mustCreateFinance());
					}
				});
				tab.setWidget(row, 1, listBox );
				row++;
			
			fmt.addStyleName(row, 0, AON.AON_CSS.aonPanelGridOdd());
			tab.setWidget(row, 0, new Label(AON.MSG.creditor()));
			fmt.addStyleName(row, 1, AON.AON_CSS.aonPanelGridEven());
			Finance finance = currentMod111.getFinance();
			creditorBox.setValue(new Creditor()
									.setId(finance.getRegistry().getId())
									.setRegistry(finance.getRegistry())
								);
			creditorBox.addSelectionHandler(new SelectionHandler<Creditor>() {
				
				@Override
				public void onSelection(SelectionEvent<Creditor> event) {
					Registry registry = event.getSelectedItem().getRegistry();
					currentMod111.getFinance().setRegistry(registry);
					currentMod111.getFinance().setRegistryDocument(registry.getDocument());
					currentMod111.getFinance().setRegistryDocumentCountry(registry.getDocumentCountry());
					currentMod111.getFinance().setRegistryDocumentType(registry.getDocumentType());
					currentMod111.getFinance().setRegistryName(registry.getName());
				}
			});
			tab.setWidget(row, 1, creditorBox);
			row++;

			fmt.addStyleName(row, 0, AON.AON_CSS.aonPanelGridOdd());
			tab.setWidget(row, 0, new Label(AON.MSG.bankAccount()));
			fmt.setStyleName(row, 1, AON.AON_CSS.aonPanelGridEven());
			
			tab.setWidget(row, 1, iban);
			iban.setEnabled( false );
			iban.addSelectionHandler(new SelectionHandler<SuggestOracle.Suggestion>() {
				
				@Override
				public void onSelection(SelectionEvent<Suggestion> event) {
					IbanSuggestion suggestion = (IbanSuggestion) event.getSelectedItem();
					IIbanContainer cont = suggestion.getIbanContainer();
					iban.setValue(cont.getIBan());
					BankAccount bankAccount = new BankAccount(cont.getIBan());
					currentMod111.getFinance().setBankAccount(bankAccount);
					currentMod111.getFinance().setBankAlias(cont.getAlias());
					currentMod111.getFinance().setBic(cont.getBic());
				}
			});
		}
		
		row++;

		fmt.setColSpan(row, 0, 2);
		fmt.addStyleName(row, 0, AON.AON_CSS.aonPanelGridEven());
		FlowPanel flowPanel = new FlowPanel();
		flowPanel.setStyleName(AON.AON_CSS.aonPadding());
		flowPanel.addStyleName(AON.AON_CSS.aonMarginTop());
		flowPanel.addStyleName(AON.AON_CSS.aonTextCenter());
		Button acceptButton = new Button();
		acceptButton.setStyleName(AON.AON_CSS.aonConfirmDialogOkButton());
		acceptButton.setText( AON.MSG.accept());
		acceptButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				finalizeDialog.hide();
				finish();
			}
		});
		
		flowPanel.add(acceptButton);
		Button cancelButton = new Button();
    	cancelButton.setStyleName(AON.AON_CSS.aonConfirmDialogCancelButton());
    	cancelButton.addStyleName(AON.AON_CSS.aonMarginLeft());
    	cancelButton.setText( AON.MSG.cancelAction());
		cancelButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				finalizeDialog.hide();
			}
			
		});
		flowPanel.add(cancelButton);
		tab.setWidget(row, 0, flowPanel);
		
		finalizeDialog.add(tab);
		finalizeDialog.center();
		finalizeDialog.show();
	}
	
	private class EnterpriseSuggestOracle extends MultiWordSuggestOracle {
		@Override
		public void requestSuggestions(final Request request,
				final Callback callback) {
			commonService.getCompanyBanks (getCurrentDomainName(),getCurrentDomain(), 
					new AsyncCallback<LinkedList<CompanyBank>>() {

						public void onFailure(Throwable caught) {
							showErrorMessage(AON.MSG.unableToShowCompanyBanks(caught.getMessage()));
						}

						public void onSuccess(LinkedList<CompanyBank> result) {
							ArrayList<Suggestion> suggestions = new ArrayList<Suggestion>();
							if (result != null) {
								for (final CompanyBank cb : result) {
									suggestions.add(new IbanTextBox.IbanSuggestion(cb));
								}
							}
							Response resp = new Response(suggestions);
							callback.onSuggestionsReady(request, resp);
						}
					});
		}
	}
	
	private SuggestOracle getSuggestOracle() {
		return new EnterpriseSuggestOracle();
	}
}
