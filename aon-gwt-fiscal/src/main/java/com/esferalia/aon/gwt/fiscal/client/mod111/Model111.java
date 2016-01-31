package com.esferalia.aon.gwt.fiscal.client.mod111;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.common.client.widget.AdministrationListBox;
import com.esferalia.aon.gwt.common.client.widget.AonToast;
import com.esferalia.aon.gwt.common.client.widget.AuditDialog;
import com.esferalia.aon.gwt.common.client.widget.ConfirmDialog;
import com.esferalia.aon.gwt.common.client.widget.ConfirmDialog.ConfirmDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.CustomDialog;
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
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod111;
import com.esferalia.aon.occam.api.model.type.Administration;
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
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.RangeChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent;

public class Model111 extends MainEntryPoint {

	final static int IDENTIFICATION_TAB = 0;
	final static int LIQUIDATION_TAB = 1;
	
	final static int NOTIFICATIONS_TAB = 1;
	final static int INFORMATION_TAB = 1;

	static FiscalServiceAsync fiscalService;
	
	interface Model111Binder extends UiBinder<Widget, Model111> {
	}
	private static final Model111Binder MODEL_111_BINDER = GWT
			.create(Model111Binder.class);

	public static interface IMod111Declaration extends IsWidget {
		void calculateAndRefresh(Mod111 mod111);
		Widget getInfoPanel();
	}
	
	protected static interface IFiscalModelCallback<T extends FiscalModel> {
		T getFiscalModel();
		void showErrorMsg(String msg);
		boolean isAuthomaticCalculationEnabled(); 
		boolean isFinished();
		boolean isDirty();
		void markAsDirty();
		void identificationLabelChanged();
		void showInfoPanel(String text);
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
	ScrollPanel informationPanel;
	@UiField
	Panel formContainer;

	@UiField(provided = true)
	Model111Table table;

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
	Button generateFileButton;
	@UiField
	Button printViaAeatButton;
	@UiField
	Button calculateButton;
	@UiField
	Button calculateCheckButton;
	private boolean authomaticCalculation;
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
	CheckBox replacement;
	@UiField
	CheckBox confidential;
	@UiField
	Button commentsButton;
	
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

		table = new Model111Table(new Mod111SelectionHandler());

		Widget ui = MODEL_111_BINDER.createAndBindUi(this);

		tabLayout.setAnimationDuration(300);
		tabLayout.selectTab(NOTIFICATIONS_TAB);
		tabLayout.addSelectionHandler(new SelectionHandler<Integer>() {
			
			@Override
			public void onSelection(SelectionEvent<Integer> event) {
				if (splitLayoutPanel.getWidgetSize(footPanel) <= 30) {
					splitLayoutPanel.setWidgetSize(footPanel,
							Window.getClientHeight() / 4);
					splitLayoutPanel.animate(500);
				}
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
		authomaticCalculation = true;
		calculateCheckButton.addStyleName( AON.AON_CSS.aonIconChecked() );
		calculateButton.setVisible(!authomaticCalculation);

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
	}
	
	private void select(Mod111 selected) {
		currentMod111 = selected;
		dirty = false;
		
		documentLabel.setText(currentMod111.getDocument());
		nameLabel.setText(currentMod111.getName());
		surnameLabel.setText(currentMod111.getSurname());
		
		styleDirtyLabel();
		styleStatusLabel();
		
		replacement.setValue(currentMod111.isReplacement());
		confidential.setValue(currentMod111.isConfidential());
		domain = currentMod111.getDomain();
		
		styleCommentsButton();
		
		// Toolbar states
		deleteButton.setVisible(currentMod111.getId() != null);
		auditButton.setVisible(currentMod111.getId() != null);
		newButton.setVisible(currentMod111.getId() != null);
		cancelButton.setVisible(true);
		saveButton.setVisible(true);
		printButton.setVisible(true);
		generateFileButton.setVisible(currentMod111.getId() != null);
		generateFileButton.addStyleName(FiscalModelUtils.getAdministrationIcon(currentMod111.getAdministration()));
		printViaAeatButton.setVisible(currentMod111.getAdministration() == Administration.COMMON_TERRITORY);
		calculateCheckButton.setVisible( authomaticCalculation );
		calculateButton.setVisible( !authomaticCalculation );
		
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
			public boolean isAuthomaticCalculationEnabled() {
				return authomaticCalculation;
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
					styleDirtyLabel();
				}
			}

			@Override
			public void identificationLabelChanged() {
				documentLabel.setText(currentMod111.getDocument());
				nameLabel.setText(currentMod111.getName());
				surnameLabel.setText(currentMod111.getSurname());
			}
		};
		
		FiscalModelIdentificationData identificationData = new FiscalModelIdentificationData(callback);
		identificationContainer.setWidget( identificationData);
		if (currentMod111.getAdministration() == Administration.COMMON_TERRITORY) {
			declaration = new Model111AEAT(callback);
		} else  if (currentMod111.getAdministration() == Administration.BIZKAIA) {
			if (currentMod111.getPeriod().isQuarterPeriod()) {
				declaration = new Model110Bizkaia(callback);
			} else {
				declaration = new Model111Bizkaia(callback);
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
			infoContainer.setWidget(declaration.getInfoPanel());
		} else {
			showErrorMessage("Administraci\u00F3n y/o ejercicio no soportado.");
			cancelButton.setVisible(false);
			saveButton.setVisible(false);
			deleteButton.setVisible(false);
			auditButton.setVisible(false);
			printButton.setVisible(false);
			generateFileButton.setVisible(false);
			newButton.setVisible(true);
			printViaAeatButton.setVisible(false);
			calculateButton.setVisible(false);
			calculateCheckButton.setVisible(false);
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
						cancelButton.setVisible(false);
						saveButton.setVisible(false);
						deleteButton.setVisible(false);
						auditButton.setVisible(false);
						printButton.setVisible(false);
						generateFileButton.setVisible(false);
						newButton.setVisible(true);
						printViaAeatButton.setVisible(false);
						calculateButton.setVisible(false);
						calculateCheckButton.setVisible(false);
					}

					@Override
					public void onFailure(Throwable caught) {
						showErrorMessage(AON.MSG.unableToReadDeclaration(caught.getMessage()));
					}
				});
	}

	@UiHandler("saveButton")
	void onAcceptButtonClick(ClickEvent event) {
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
		cf.setWidth(0, "100px");
		cf.addStyleName(0, AON.AON_CSS.aonPaddingLeft() );
		cf.addStyleName(0, AON.AON_CSS.aonPaddingRight() );
		cf.setWidth(1, "150px");
		cf.addStyleName(0, AON.AON_CSS.aonPaddingLeft() );
		cf.addStyleName(0, AON.AON_CSS.aonPaddingRight() );
		FlexCellFormatter fmt = tab.getFlexCellFormatter();
		
		fmt.addStyleName(0, 0, AON.AON_CSS.aonPanelGridOdd());
		tab.setWidget(0, 0, new Label(AON.MSG.administration()));
		fmt.addStyleName(0, 1, AON.AON_CSS.aonPanelGridEven());
		final AdministrationListBox admonList = new AdministrationListBox();
		
		admonList.getElement().getElementsByTagName("option").getItem(2).setAttribute("disabled", "disabled");
		admonList.getElement().getElementsByTagName("option").getItem(3).setAttribute("disabled", "disabled");
		
		admonList.setSelectedIndex( currentMod111.getAdministration().ordinal());
		admonList.addChangeHandler( new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				currentMod111.setAdministration( admonList.getValue() );
			}
		});
		tab.setWidget(0, 1, admonList);
		
		
		fmt.addStyleName(1, 0, AON.AON_CSS.aonPanelGridOdd());
		tab.setWidget(1, 0, new Label(AON.MSG.year()));
		fmt.addStyleName(1, 1, AON.AON_CSS.aonPanelGridEven());
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
		tab.setWidget(1, 1,yearBox);

		fmt.addStyleName(2, 0, AON.AON_CSS.aonPanelGridOdd());
		tab.setWidget(2, 0, new Label(AON.MSG.period()));
		fmt.addStyleName(2, 1, AON.AON_CSS.aonPanelGridEven());
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
		tab.setWidget(2, 1, periodList);

		fmt.setColSpan(3, 0, 2);
		fmt.addStyleName(3, 0, AON.AON_CSS.aonPanelGridEven());
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
		tab.setWidget(3, 0, flowPanel);
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
				toast.hide();
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
		Window.alert("Imprimir");
	}

	@UiHandler("generateFileButton")
	void onGenerateFileButtonClick(ClickEvent event) {
		Window.alert(
				  "Se va a proceder a la generaci\u00F3n de un fichero\n"
				+ "con los datos de la declaraci\u00F3n, para su \n"
				+ "presentaci\u00F3n en Hacienda.\n\n"
				+ "Aseg\u00FArese de haber guardado la declaraci\u00F3n.\n\n"
				+ "El fichero se genera a partir de los datos guardados.");
		diskForm.setAction(GWT.getHostPageBaseURL()
				+ "/aon_gwt_fiscal/Model111File");
		mod111Hidden.setValue(String.valueOf(currentMod111.getId()));
		domainIdHidden.setValue(String.valueOf(getCurrentDomain()));
		domainNameHidden.setValue(getCurrentDomainName());
		diskForm.submit();
	}

	@UiHandler("printViaAeatButton")
	void onPrintViaAeatButtonClick(ClickEvent event) {
		Window.alert(
				  "Se va a proceder a la validaci\u00F3n en los servidores de la \n"
				+ "Agencia Tributaria. En el caso de validaci\u00F3n correcta,la Agencia \n"
				+ "Tributaria devolver\u00E1 un documento PDF borrador con la declarai\u00F3n\n\n"
				+ "Aseg\u00FArese de haber guardado la declaraci\u00F3n.\n\n"
				+ "La petici\u00F3n se genera a partir de los datos guardados.");
		diskForm.setAction(GWT.getHostPageBaseURL()
				+ "/aon_gwt_fiscal/Model111Print");
		mod111Hidden.setValue(String.valueOf(currentMod111.getId()));
		domainIdHidden.setValue(String.valueOf(getCurrentDomain()));
		domainNameHidden.setValue(getCurrentDomainName());
		diskForm.submit();
	}

	@UiHandler("calculateCheckButton")
	void onCalculateCheckClick(ClickEvent event) {
		authomaticCalculation = !authomaticCalculation;
		calculateButton.setVisible(!authomaticCalculation);
		if (authomaticCalculation) {
			calculateCheckButton.removeStyleName(AON.AON_CSS.aonIconCheck());
			calculateCheckButton.addStyleName(AON.AON_CSS.aonIconChecked());
			declaration.calculateAndRefresh(currentMod111);
		} else {
			calculateCheckButton.addStyleName(AON.AON_CSS.aonIconCheck());
			calculateCheckButton.removeStyleName(AON.AON_CSS.aonIconChecked());
		}
	}
	@UiHandler("calculateButton")
	void onCalculateButtonClick(ClickEvent event) {
		declaration.calculateAndRefresh(currentMod111);
	}
	
	@UiHandler("replacement")
	void onReplacementClick(ClickEvent event) {
		currentMod111.setReplacement(replacement.getValue());
		setDirty(true);
	}
	@UiHandler("confidential")
	void onConfidentialClick(ClickEvent event) {
		currentMod111.setConfidential(confidential.getValue());
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
	
	private void showResultsPanel() {
		splitLayoutPanel.setWidgetSize(footPanel, Window.getClientHeight() / 5);
	}

	private boolean isResultsPanelVisible() {
		return splitLayoutPanel.getWidgetSize(footPanel) > 0;
	}
	
	private void cleanErrorMessage() {
		SimplePanel panel = new SimplePanel();
		resultsPanel.setWidget(panel);
		closeFootPanel();
	}

	private void showErrorMessage(String msg) {
		showResultsPanel();
		addErrorMessage(msg);
	}

	private void showInfoPanel(String htmlText) {
		if (splitLayoutPanel.getWidgetSize(footPanel) <= 30) {
			splitLayoutPanel.setWidgetSize(footPanel,
					Window.getClientHeight() / 4);
			splitLayoutPanel.animate(500);
		}
		tabLayout.selectTab(INFORMATION_TAB);
		HTMLPanel panel = new HTMLPanel(htmlText);
		informationPanel.setWidget(panel);
		informationPanel.scrollToTop();
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
}
