package com.esferalia.aon.gwt.fiscal.client.mod202;

import java.util.EnumMap;
import java.util.Map.Entry;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonAuditDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonBoxLabel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog.AonConfirmDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDoubleBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonSplash;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTextBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToast;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.fiscal.client.FiscalModelUtils;
import com.esferalia.aon.gwt.fiscal.client.mod202.Model202.Model202Callback;
import com.esferalia.aon.gwt.fiscal.client.model.AonFiscalModelHeader;
import com.esferalia.aon.gwt.fiscal.client.model.FinishDeclarationPopup;
import com.esferalia.aon.gwt.fiscal.client.model.FinishDeclarationPopup.IFinishDeclarationPopupCallback;
import com.esferalia.aon.gwt.fiscal.client.model.FiscalModelAdmonPanel;
import com.esferalia.aon.gwt.fiscal.client.model.FiscalModelAdmonPanel.IFiscalModelAdmonPanelCallback;
import com.esferalia.aon.gwt.fiscal.client.model.AonFiscalModelIdentificationPanel;
import com.esferalia.aon.gwt.fiscal.shared.mod202.Model202ScriptProvider;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelDetail;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod202;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod202Key;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.TextArea;

public abstract class Model202Base extends DockLayoutPanel {
	
	protected FiscalModelAdmonPanel<Mod202, Model202ModuleOptions> admonPanel;

	private static final String WIDTH_140PX = "140px";
	private static final String BLANK = "_blank";
	private static final int MAX_LABEL_LENGTH = 180;
	private static final int COL_NUMBER = 8;
	private static final String MODEL202_PRINT = "/aon_gwt_fiscal/ms/Model202Print";
	protected static final String MODEL202_FILE = "/aon_gwt_fiscal/ms/Model202File";

	private Model202Callback callback;
	private Mod202 model;
	private EnumMap<Mod202Key,AonDoubleBox> fieldsMap;
	private boolean dirty;	
	
	private final AonToolbar toolbarPanel = new AonToolbar(); 
	private final AonToolbarButton newButton = new AonToolbarButton(AON.MSG.newAction(),AON.CSS.aonIconAdd());
	private final AonToolbarButton saveButton = new AonToolbarButton( AON.MSG.saveAction(), AON.CSS.aonIconSave());
	private final AonToolbarButton cancelButton = new AonToolbarButton(AON.MSG.cancelAction(),AON.CSS.aonIconBack());
	private final AonToolbarButton deleteButton = new AonToolbarButton(AON.MSG.deleteAction(),AON.CSS.aonIconDelete());
	private final AonToolbarButton resetButton = new AonToolbarButton(AON.MSG.resetAction(),AON.CSS.aonIconRefresh());
	private final AonToolbarButton printButton = new AonToolbarButton(AON.MSG.draft(),AON.CSS.aonIconExcel());
	private final AonToolbarButton markAsPendingButton = new AonToolbarButton(AON.MSG.reopen(),AON.CSS.aonIconModelReopen());
	private final AonToolbarButton markAsFinishedButton = new AonToolbarButton(AON.MSG.finish(),AON.CSS.aonIconModelFinish());
	private final AonToolbarButton markAsSentButton = new AonToolbarButton(AON.MSG.markAsSent(),AON.CSS.aonIconModelSent());
	private final AonToolbarButton commentsButton = new AonToolbarButton(AON.MSG.comments(), AON.CSS.aonIconNoComments());
	private final AonToolbarButton auditButton = new AonToolbarButton(AON.MSG.audit(),AON.CSS.aonIconAudit());
	
	private AonTextBox receiptBox;
	private FlowPanel paymentContainer;
	
	private final AonToolbar decToolbar = new AonToolbar();
	private final InlineLabel dirtyLabel = new InlineLabel();
	private final InlineLabel diffLabel = new InlineLabel();
	private final InlineLabel adjLabel = new InlineLabel();
	private final InlineLabel replacedLabel = new InlineLabel();
	private final Label statusLabel = new Label();
	
	protected FormPanel diskForm = new FormPanel(BLANK);
	protected Hidden mod202Hidden = new Hidden("mod202");
	protected Hidden domainIdHidden = new Hidden("domainId");
	protected Hidden domainNameHidden = new Hidden("domainName");
	protected Hidden userHidden = new Hidden("user");
	
	protected Model202Base(Mod202 mod202, Model202Callback callback) {
		super(Unit.PX);
		setStyleName(AON.CSS.aonSelector());
		fieldsMap = new EnumMap<>(Mod202Key.class);
		this.callback = callback;

		select( mod202 );
		
		AonFiscalModelHeader modelHeader = new AonFiscalModelHeader( mod202 );
		addNorth(modelHeader, AonFiscalModelHeader.HEIGTH);
		addNorth(getToolbarPanel(), AonToolbar.HEIGTH);
		addNorth(getDeclarationToolbarPanel(), AonToolbar.HEIGTH);
		
		TabLayoutPanel tabPanel = new TabLayoutPanel(26, Unit.PX);
		SimpleLayoutPanel centerPanel = new SimpleLayoutPanel();
		centerPanel.addStyleName(AON.CSS.aonScrollArea());
		centerPanel.setWidget(tabPanel);
		add(centerPanel);
		
		showPaymentInfo(mod202);
		
		paintIdentificationTab(tabPanel);
		paintDeclarationTab(tabPanel);
		paintLiquidationTab(tabPanel);
		paintAdministrationTab(tabPanel);
	}

	public Model202Callback getCallback() {
		return callback;
	}
	protected Mod202 getModel() {
		return model;
	}
	public void setModel(Mod202 mod202) {
		this.model = mod202;
	}
	protected EnumMap<Mod202Key, AonDoubleBox> getFieldsMap() {
		return fieldsMap;
	}
	
	private void select( Mod202 mod202) {
		setModel(mod202);
		refreshToolbarState();
		styleStatusLabel(mod202);
	}
	
	protected void selectAndPopulate( Mod202 mod202) {
		select(mod202);
		populate(mod202);
		decorateDeclarationTab();
		decorateAdministrationTab();
	}
	
	private void populate(Mod202 mod202) {
		for (Entry<Mod202Key, AonDoubleBox> entry : fieldsMap.entrySet()) {
			double d1 = mod202.getAmount(entry.getKey());
			double d2 = entry.getValue().getValue();
			if (!AonNumberUtils.equals(d1, d2)) {
				entry.getValue().setValue(d1,false,true);
			}
		}
	}
	
	private AonToolbar getToolbarPanel() {
		 
		if (getCallback().getOptions().isBackButtonVisible() && getCallback().getOptions().hasExternalCallback()) {
			cancelButton.setText(AON.MSG.backAction());
			cancelButton.setTitle(AON.MSG.backAction());
		}
		cancelButton.addClickHandler(event ->  cancel() );
		toolbarPanel.add(cancelButton);

		newButton.addClickHandler( event ->  getCallback().onNew() );
		toolbarPanel.add(newButton);

		saveButton.addClickHandler(event ->  save());
		toolbarPanel.add(saveButton);
		
		deleteButton.addClickHandler(event -> delete());
		toolbarPanel.add(deleteButton);
		
		resetButton.addClickHandler( event -> onReset());
		toolbarPanel.add(resetButton);		
		
		printButton.addClickHandler( event ->  print());
		toolbarPanel.add(printButton);
		
		commentsButton.addClickHandler( event -> {
			final AonToast toast = new AonToast();
			FlowPanel commentPanel = new FlowPanel();
			commentPanel.setStyleName( FiscalModelUtils.getAdministrationBackgroundStyle(getModel().getAdministration()) );
			commentPanel.addStyleName(AON.CSS.aonHeightAll());
			commentPanel.addStyleName(AON.CSS.aonTextCenter());
			TextArea comment = new TextArea();
			comment.addValueChangeHandler(event1 -> {
				getModel().setComments(event1.getValue());
				styleCommentsButton();
				Model202.SERVICE.saveComments( getCallback().getOptions().getOccam(), getModel(), new AsyncCallback<Mod202>() {
					@Override
					public void onSuccess(Mod202 result) {
						toast.hide();
					}

					@Override
					public void onFailure(Throwable caught) {
						toast.hide();
						getCallback().showError(AON.MSG.unableToSaveDeclaration(caught.getMessage()));
					}
				});
			});
			comment.setText(getModel().getComments());
			comment.setWidth("90%");
			comment.setHeight("5em");
			commentPanel.add(comment);
			toast.show(AON.MSG.comments(), commentPanel);
		});
		toolbarPanel.add( commentsButton );
		styleCommentsButton();

		auditButton.addClickHandler( event -> audit());
		toolbarPanel.add(auditButton);
		
		toolbarPanel.add(diskForm);

		return toolbarPanel;
	}
	
	private AonToolbar getDeclarationToolbarPanel() {
		
		markAsFinishedButton.setText(markAsFinishedButton.getTitle());
		markAsFinishedButton.addClickHandler( event -> markAsFinished());
		decToolbar.add(markAsFinishedButton);

		markAsSentButton.setText(markAsSentButton.getTitle());
		markAsSentButton.addClickHandler( event -> markAsSent());
		decToolbar.add(markAsSentButton);

		markAsPendingButton.setText(markAsPendingButton.getTitle());
		markAsPendingButton.addClickHandler( event -> reopenDeclaration());
		decToolbar.add(markAsPendingButton);

		FlowPanel marksPanels = new FlowPanel();
		marksPanels.setStyleName(AON.CSS.aonFlexBlock());
		
		dirtyLabel.setStyleName(AON.CSS.aonIconLabel());
		dirtyLabel.addStyleName(AON.CSS.aonIconDirty());
		dirtyLabel.setTitle("Cambios sin guardar");
		dirtyLabel.getElement().getStyle().setWidth(10, Unit.PX);
		dirtyLabel.getElement().getStyle().setHeight(10, Unit.PX);
		marksPanels.add(dirtyLabel);
		
		diffLabel.setStyleName(AON.CSS.aonMarginLeft());
		diffLabel.addStyleName(AON.CSS.aonLabelWithIcon());
		diffLabel.addStyleName(AON.CSS.aonIconDiff());
		diffLabel.setTitle("C\u00E1lculo por diferencia habilitado");
		marksPanels.add(diffLabel);

		adjLabel.setStyleName(AON.CSS.aonMarginLeft());
		adjLabel.addStyleName(AON.CSS.aonIconLabel());
		adjLabel.addStyleName(AON.CSS.aonIconWrench());
		adjLabel.addStyleName(AON.CSS.aonColorBlue());
		adjLabel.setTitle("Ajustes realizados");
		marksPanels.add(adjLabel);

		if (getModel().isReplacement()) {
			replacedLabel.setText(AON.MSG.replacement());
			replacedLabel.setStyleName(AON.CSS.aonMarginLeft());
			replacedLabel.addStyleName(AON.CSS.aonIconChecked());
			replacedLabel.addStyleName(AON.CSS.aonLabelWithIcon());
		}
		if (getModel().isComplementary()) {
			replacedLabel.setText( AON.MSG.complementary());
			replacedLabel.setStyleName(AON.CSS.aonMarginLeft());
			replacedLabel.addStyleName(AON.CSS.aonIconChecked());
			replacedLabel.addStyleName(AON.CSS.aonLabelWithIcon());
		}
		marksPanels.add(replacedLabel);

		styleDirtyLabel();
		styleStatusLabel(this.getModel());
		
		decToolbar.getMessagePanel().add(marksPanels);
		
		decToolbar.setTitle(statusLabel);
		return decToolbar;
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
	}

	private void styleDirtyLabel() {
		dirtyLabel.setVisible(isDirty());
		diffLabel.setVisible(!this.getModel().isDiffCalculationDisabled()); 		
		
		boolean adjusted = false;
		for (FiscalModelDetail det : this.getModel().getMap().values()) {
			if (AonMathUtils.isNotZero( det.getAdjustAmount())) {
				adjusted = true;
				break;
			}
		}
		adjLabel.setVisible(adjusted);
	}

	private void refreshToolbarState() {
		toolbarPanel.setTitle(AonStringUtils.join(model.getDocument(),AonStringUtils.SPACE,model.getFullName()));
		resetButton.setVisible(!model.isNew() && !model.isFinished() && !model.isSent());
		auditButton.setVisible(!model.isNew());
		newButton.setVisible(!model.isNew() && !getCallback().getOptions().isBackButtonVisible() && !getCallback().getOptions().hasExternalCallback());
		cancelButton.setVisible(true);
		saveButton.setVisible(!model.isFinished() && !model.isSent());
		deleteButton.setVisible(!model.isNew() && !model.isFinished() && !model.isSent());
		printButton.setVisible(!model.isNew());
		markAsPendingButton.setVisible(!model.isNew() &&
				(model.getStatus() == FiscalStatus.FINISHED 
				|| model.getStatus() == FiscalStatus.BATCHED
				|| model.getStatus() == FiscalStatus.SENT
				|| model.getStatus() == FiscalStatus.CUSTOMER_CHECK
				|| model.getStatus() == FiscalStatus.BLOCKED));
		markAsFinishedButton.setVisible(!model.isNew() &&
				(model.getStatus() == FiscalStatus.PENDING
				|| model.getStatus() == FiscalStatus.CUSTOMER_CHECK
				|| model.getStatus() == FiscalStatus.MISSING));
		markAsSentButton.setVisible(!model.isNew() &&
				(model.getStatus() == FiscalStatus.FINISHED));
	}
	
	private void styleStatusLabel(Mod202 mod202) {
		statusLabel.setText(mod202.getStatus().getName());
		statusLabel.getElement().getStyle().setBackgroundColor(FiscalModelUtils.getStatusBckColorRGB( mod202.getStatus() ));
		statusLabel.getElement().getStyle().setColor(FiscalModelUtils.getStatusFrgColorRGB( mod202.getStatus() ));
		statusLabel.setStyleName(AON.CSS.aonToolbarTitle());
		statusLabel.addStyleName(AON.CSS.aonPaddingLeft());
		statusLabel.addStyleName(AON.CSS.aonPaddingRight());
		statusLabel.addStyleName(AON.CSS.aonTextCenter());
		statusLabel.addStyleName(AON.CSS.aonBorder());
		statusLabel.addStyleName(AON.CSS.aonNowrap());
	}
	
	private void styleCommentsButton() {
		if (AonStringUtils.isEmpty(getModel().getComments())) {
			commentsButton.addStyleName(AON.CSS.aonIconNoComments());
			commentsButton.removeStyleName(AON.CSS.aonIconComments());
		} else {
			commentsButton.addStyleName(AON.CSS.aonIconComments());
			commentsButton.removeStyleName(AON.CSS.aonIconNoComments());
		}
		commentsButton.setTitle(getModel().getComments());
	}
		
	private void cancel() {
		cancelButton.setEnabled(false);
		if (isDirty()) {
			AonConfirmDialog cd = new AonConfirmDialog();
			cd.confirm(AON.MSG.confirmDeclarationCancelAction(), new AonConfirmDialogCallback() {

				@Override
				public void onAccept() {
					if (getCallback().getOptions().isBackButtonVisible() && getCallback().getOptions().hasExternalCallback()) {
						getCallback().getOptions().getExternalCallback().onExit(getModel());
					} else {
						getCallback().onCancel(getModel());
					}
				}
				@Override
				public void onCancel() {
					cancelButton.setEnabled(true);
				}
			});
		} else {
			if (getCallback().getOptions().isBackButtonVisible() && getCallback().getOptions().hasExternalCallback()) {
				getCallback().getOptions().getExternalCallback().onExit(getModel());
			} else {
				getCallback().onCancel(getModel());
			}
		}
	}
	
	private void save() {
		save(null);
	}
	private void save(AsyncCallback<Mod202> cbk) {
		saveButton.setEnabled(false);
		final PopupPanel popup = new PopupPanel(false, true);
		popup.add(new AonSplash());
		popup.setGlassEnabled(true);
		popup.setAnimationEnabled(true);
		popup.center();
		Model202.SERVICE.save(getCallback().getOptions().getOccam(), getModel(), new AsyncCallback<Mod202>() {
			@Override
			public void onSuccess(Mod202 result) {
				setDirty( false );
				select(result);
				popup.hide();
				saveButton.setEnabled(true);
				if (cbk != null) cbk.onSuccess(result);
			}

			@Override
			public void onFailure(Throwable caught) {
				popup.hide();
				getCallback().showError(AON.MSG.unableToSaveDeclaration(caught.getMessage()));
				saveButton.setEnabled(true);
			}
		});
	}
	
	private void delete() {
		deleteButton.setEnabled(false);
		AonConfirmDialog cd = new AonConfirmDialog();
		cd.confirm(AON.MSG.confirmDeclarationDeleteAction(), new AonConfirmDialogCallback() {

			@Override
			public void onAccept() {
				Model202.SERVICE.delete(getCallback().getOptions().getOccam(),getModel(), new AsyncCallback<Void>() {
					@Override
					public void onSuccess(Void result) {
						deleteButton.setEnabled(true);
						getCallback().onRemove(getModel());
					}

					@Override
					public void onFailure(Throwable caught) {
						deleteButton.setEnabled(true);
						getCallback().showError(AON.MSG.unableToDeleteDeclaration(caught.getMessage()));
					}
				});
			}

			@Override
			public void onCancel() {
				deleteButton.setEnabled(true);
			}
		});
	}
	
	private void print() {
		if (isDirty()) {
			new AonConfirmDialog().confirm(AON.MSG.draftPrint(),AON.MSG.draftPrintNote(), new AonConfirmDialogCallback() {
				@Override
				public void onAccept() {
					submitForm(MODEL202_PRINT);
				}
				@Override
				public void onCancel() {
					// Nothing
				}
			});
		} else {
			submitForm(MODEL202_PRINT);
		}
	}

	private void audit() {
		AonAuditDialog dialog = new AonAuditDialog();
		dialog.show(getModel());
	}
	
	private void reopenDeclaration() {
		markAsPendingButton.setEnabled(false);
		if (getModel().isSent() && AonStringUtils.isNotBlank(getModel().getNumber())) {
			AonConfirmDialog cd = new AonConfirmDialog();
			cd.confirm(AON.MSG.confirmReopenDeclarationAction(), new AonConfirmDialogCallback() {
				
				@Override
				public void onAccept() {
					doReopenDeclaration();
				}
				
				@Override
				public void onCancel() {
					markAsPendingButton.setEnabled(true);
				}
			});
		} else {
			doReopenDeclaration();
		}
	}
	
	private void doReopenDeclaration() {
		markAsPendingButton.setEnabled(false);
		final PopupPanel popup = new PopupPanel(false, true);
		popup.add( new AonSplash());
		popup.setGlassEnabled(true);
		popup.setAnimationEnabled(true);
		popup.center();
		Model202.SERVICE.markAsPending(getCallback().getOptions().getOccam(), getModel(), new AsyncCallback<Mod202>() {
					@Override
					public void onSuccess(Mod202 result) {
						setDirty(false);
						selectAndPopulate(result);
						popup.hide();
						markAsPendingButton.setEnabled(true);
						showPaymentInfo(getModel());
					}

					@Override
					public void onFailure(Throwable caught) {
						popup.hide();
						getCallback().showError(AON.MSG.unableToReopenDeclaration(caught.getMessage()));
						markAsPendingButton.setEnabled(true);
					}
				});
	}

	private void markAsSent() {
		markAsSentButton.setEnabled(false);
		Model202.SERVICE.markAsSent(getCallback().getOptions().getOccam(), getModel(), new AsyncCallback<Mod202>() {
					@Override
					public void onSuccess(Mod202 result) {
						setDirty(false);
						selectAndPopulate(result);
						markAsSentButton.setEnabled(true);
					}

					@Override
					public void onFailure(Throwable caught) {
						getCallback().showError(AON.MSG.unableToMarkAsSentDeclaration(caught.getMessage()));
						markAsSentButton.setEnabled(true);
					}
				});
	}

	private void markAsFinished() {
		markAsFinishedButton.setEnabled(false);
		Model202.SERVICE.initializeForFinish(getCallback().getOptions().getOccam(),getModel(),
				new AsyncCallback<Mod202>() {
					@Override
					public void onSuccess(Mod202 m202) {
						selectAndPopulate(m202);
						showFinalizePopup(m202);
						markAsFinishedButton.setEnabled(true);
					}

					@Override
					public void onFailure(Throwable caught) {
						getCallback().showError(AON.MSG.unableToReadFiscalParameters(caught.getMessage()));
						markAsFinishedButton.setEnabled(true);
					}
				});
	}
	
	private void showFinalizePopup(Mod202 model) {
		FinishDeclarationPopup<Mod202,Model202ModuleOptions> finalizeDialog = new FinishDeclarationPopup<>(
			model,
			getCallback(), 
			new IFinishDeclarationPopupCallback<Mod202>() {

				@Override
				public void onAccept(Mod202 t) {
					Model202Base.this.doMarkAsFinished();
				}

				@Override
				public void onCancel(Mod202 t) {
					// Nothing
					
				}

				@Override
				public void onCustomerCheck(Mod202 t) {
					Model202Base.this.markAsCustomerCheck();
				}
		});
		finalizeDialog.center();
		finalizeDialog.show();
	}
	
	private void doMarkAsFinished() {
		markAsFinishedButton.setEnabled(false);
		final PopupPanel popup = new PopupPanel(false, true);
		popup.add( new AonSplash());
		popup.setGlassEnabled(true);
		popup.setAnimationEnabled(true);
		popup.center();
		Model202.SERVICE.markAsFinished(getCallback().getOptions().getOccam(), getModel(), new AsyncCallback<Mod202>() {
			@Override
			public void onSuccess(Mod202 result) {
				setDirty(false);
				selectAndPopulate(result);
				popup.hide();
				markAsFinishedButton.setEnabled(true);
				showPaymentInfo(getModel());
			}

			@Override
			public void onFailure(Throwable caught) {
				popup.hide();
				getCallback().showError(AON.MSG.unableToSaveDeclaration(caught.getMessage()));
				markAsFinishedButton.setEnabled(true);
			}
		});
	}
	
	
	private void markAsCustomerCheck() {
		markAsFinishedButton.setEnabled(false);
		final PopupPanel popup = new PopupPanel(false, true);
		popup.add( new AonSplash());
		popup.setGlassEnabled(true);
		popup.setAnimationEnabled(true);
		popup.center();
		Model202.SERVICE.markAsCustomerCheck(getCallback().getOptions().getOccam(), getModel(), new AsyncCallback<Mod202>() {
			@Override
			public void onSuccess(Mod202 result) {
				setDirty(false);
				select(result);
				popup.hide();
				markAsFinishedButton.setEnabled(true);
				showPaymentInfo(getModel());
			}

			@Override
			public void onFailure(Throwable caught) {
				popup.hide();
				getCallback().showError(AON.MSG.unableToSaveDeclaration(caught.getMessage()));
				markAsFinishedButton.setEnabled(true);
			}
		});
	}

	private void showPaymentInfo(Mod202 mod) {
		if (paymentContainer != null) {
			this.remove(paymentContainer);
			this.forceLayout();
		}
		if (mod.isFinished() || mod.isSent()) {
			StringBuilder buff = new StringBuilder(AON.MSG.result());
			buff.append(AonStringUtils.SPACE);
			buff.append(AON.FMT.format(mod.getResult()));
			if (mod.getDeclarationType() != null) {
				buff.append(AonStringUtils.SPACE);
				buff.append(mod.getDeclarationType().getDescription());
			}
			if (mod.getFinance() != null && mod.getFinance().getBankAccount() != null && AonStringUtils.isNotBlank(mod.getFinance().getBankAccount().getIban())) {
				buff.append(AonStringUtils.SPACE);
				buff.append(AonStringUtils.defaultString(mod.getFinance().getBankAccount().getIban()));
				buff.append(AonStringUtils.SPACE);
				buff.append(AonStringUtils.defaultString(mod.getFinance().getBankAlias()));
			}
			paymentContainer = new FlowPanel();
			paymentContainer.setStyleName(AON.CSS.aonWidthAll());
			Label label = new Label( buff.toString() );
			label.setStyleName(AON.CSS.aonWidthAll());
			label.addStyleName(AON.CSS.aonTextCenter());
			label.addStyleName(AON.CSS.aonBold());
			paymentContainer.add(label);
			this.insert( paymentContainer, Direction.NORTH, 30, decToolbar);
			this.forceLayout();
		}
	}

	protected void paintLiquidationTab(TabLayoutPanel tabPanel) {
		ScrollPanel liquidationScrollPanel = new ScrollPanel();
		FlowPanel container = new FlowPanel();
		FlexTable table = new FlexTable();
		container.add(table);
		defineTable(table);
		for (IModelScript<Mod202Key> ms : Model202ScriptProvider.obtainScript(getModel())) {
			paintRow(table,getCallback(),ms);	
		}

		liquidationScrollPanel.setWidget(container);
		tabPanel.add(liquidationScrollPanel, AON.MSG.liquidacion());
	}
	
	private void defineTable(FlexTable table) {
		table.setWidth("100%");
		table.addStyleName(AON.CSS.aonMarginBottom());
		
		table.getColumnFormatter().setWidth(0, "auto");
		table.getColumnFormatter().addStyleName(0, AON.CSS.aonPaddingLeft() );
		table.getColumnFormatter().addStyleName(0, AON.CSS.aonPaddingRight() );
		
		table.getColumnFormatter().setWidth(1, "40px");
		table.getColumnFormatter().setStyleName(1, AON.CSS.aonTextCenter());
		table.getColumnFormatter().setWidth(2, WIDTH_140PX);
		
		table.getColumnFormatter().setWidth(3, "40px");
		table.getColumnFormatter().setStyleName(3, AON.CSS.aonTextCenter());
		table.getColumnFormatter().setWidth(4, WIDTH_140PX);
		
		table.getColumnFormatter().setWidth(5, "40px");
		table.getColumnFormatter().setStyleName(5, AON.CSS.aonTextCenter());
		table.getColumnFormatter().setWidth(6, WIDTH_140PX);
		
		table.getColumnFormatter().setWidth(7, "50px");
	}

	protected void paintEmptyRow(FlexTable table) {
		int row = table.getRowCount();
		table.setWidget(row, 0, new Label());
		table.getFlexCellFormatter().setColSpan(row, 0, COL_NUMBER);
	}

	protected void paintRow(FlexTable table, final Model202Callback callback, IModelScript<Mod202Key> script) {
		if (script.hasGraphicParticularity()) {
			paintParticularyRow(table,callback,script);		
		} else {
			int row = table.getRowCount();
			paintLabel(table,row,script);
			if (script.getKeys() == null) {
				table.getFlexCellFormatter().setColSpan(row, 0, COL_NUMBER);	
			} else {
				int c = 1;
				if (script.getKeys().length==1) c = 5;
				if (script.getKeys().length==1) c = 3;
				table.getFlexCellFormatter().setColSpan(row, 0, c );
				int col = 1;
				for (Mod202Key key : script.getKeys()) {
					col = paintBox( table,row, col, key );
					col = paintField( table,row, col, callback, script, key );
				}
				paintInfoCol(table,row,col,callback,script);	
			}
		}
	}
	
	protected void paintParticularyRow(FlexTable table, final Model202Callback callback, IModelScript<Mod202Key> script) {
		
	}
	
	protected void paintLabel(FlexTable table, int row, IModelScript<Mod202Key> script) {
		String labelText = script.getLabel();
		Label label = new Label();
		if (AonStringUtils.length(labelText) > MAX_LABEL_LENGTH) {
			label.setTitle(labelText);	
			labelText = AonStringUtils.abbreviate(labelText, MAX_LABEL_LENGTH);
		}
		label.setText(labelText);
		table.setWidget(row, 0, label);
		table.getFlexCellFormatter().addStyleName(row, 0,AON.CSS.aonBorderBottom() );
		table.getFlexCellFormatter().addStyleName(row, 0,AON.CSS.aonPaddingLeft() );
		if (script.isTitle() ) {
			table.getFlexCellFormatter().addStyleName(row, 0,AON.CSS.aonBold() );
		} else {
			table.getFlexCellFormatter().addStyleName(row, 0,AON.CSS.aonPaddingLeft() );
		}
	}
	
	private int paintBox(FlexTable table,int row, int col, Mod202Key key) {
		if (key != null && key.getBox() != 0) {
			table.setWidget(row, col, new AonBoxLabel(key.getBox()));
		}
		return ++col;
	}

	private int paintField(FlexTable table, int row, int col, final Model202Callback callback, IModelScript<Mod202Key> script, final Mod202Key key) {
		if (key != null) {
			final FiscalModelDetail det1 = getModel().ensureDetail(key);
			final AonDoubleBox input = new AonDoubleBox();
			fieldsMap.put(key, input);
			input.setEnabled(script.isEnabled()); 
			input.setValue(det1.getAmount());
			input.addValueChangeHandler(event -> {
				if (event.getValue() == null) input.setValue(0.0, false);
				double result = getModel().getResultAmount(key);
				double adjust = getModel().getAdjustAmount(key);
				double amount = input.getValue();
				if (AonMathUtils.isNotZero(result - adjust - amount)) {
					getModel().ensureDetail(key).setAdjustAmount( result - amount);	
				}
				getModel().ensureDetail(key).setAmount(input.getValue());
				if (input.isEnabled()) {
					calculateAndRefresh( callback );
				}
				markAsDirty();
			});
			table.setWidget(row, col, input);
		}
		return ++col;
	}
	
	private void paintInfoCol(FlexTable table, int row, int col, final Model202Callback callback, final IModelScript<Mod202Key> script) {
		FlowPanel buttonContainer = new FlowPanel();
		for (final FiscalModelKeyInfo infoKey : script.getInfoKeys()) {
			buttonContainer.setStyleName(AON.CSS.aonNowrap());
			if 	(infoKey != FiscalModelKeyInfo.NONE) {
				final AonTableButton button = new AonTableButton("");
				button.setTitle(infoKey.getLabel());
				
				if 	(infoKey == FiscalModelKeyInfo.CORPORATE) button.addStyleName(AON.CSS.aonIconList());
				if 	(infoKey == FiscalModelKeyInfo.COMPUTE) button.addStyleName(AON.CSS.aonIconCalc());
				if 	(infoKey == FiscalModelKeyInfo.ACT_ACCOUNT) button.addStyleName(AON.CSS.aonIconBullet());
				
				button.addClickHandler(event -> Model202.SERVICE.getInfo(
					callback.getOptions().getOccam(),
					getModel(),script, infoKey,new AsyncCallback<String>() {

							@Override
							public void onFailure(Throwable caught) {
								callback.showError(AON.MSG.errorMessage());
							}

							@Override
							public void onSuccess(String result) {
								callback.showInfoPanel(result);
							}
					
						}
					));
				buttonContainer.add(button);
			}
			table.setWidget(row, col, buttonContainer);
		}
	}

	private void onReset() {
		resetButton.setEnabled(false);
		AonConfirmDialog cd = new AonConfirmDialog();
		cd.confirm(AON.MSG.confirmDeclarationinitializationAction(), new AonConfirmDialogCallback() {

			@Override
			public void onAccept() {
				Model202.SERVICE.reset(getCallback().getOptions().getOccam(),getModel(),
						new AsyncCallback<Mod202>() {
							@Override
							public void onSuccess(Mod202 m202) {
								setDirty( true );
								selectAndPopulate(m202);
							}

							@Override
							public void onFailure(Throwable caught) {
								getCallback().showError(AON.MSG.unableToInitializeDeclaration(caught.getMessage()));
								
							}
						});
			}
			@Override
			public void onCancel() {
				resetButton.setEnabled(true);
			}
		});
	}

	public void calculateAndRefresh(final Model202Callback callback) {
		Model202.SERVICE.calculate(callback.getOptions().getOccam(),getModel(),
				new AsyncCallback<Mod202>() {

					@Override
					public void onFailure(Throwable caught) {
						callback.showError(AON.MSG.errorMessage());
					}

					@Override
					public void onSuccess(Mod202 result) {
						populate(result);
					}
			
				}
			);	
	}

	protected void submitForm(String action) {
		diskForm.setMethod(FormPanel.METHOD_POST);
		diskForm.setAction(GWT.getHostPageBaseURL() + action);
		diskForm.clear();
		FlowPanel diskPanel = new FlowPanel();
		diskPanel.add(mod202Hidden);
		diskPanel.add(domainIdHidden);
		diskPanel.add(domainNameHidden);
		diskPanel.add(userHidden);
		diskForm.add(diskPanel);
		mod202Hidden.setValue(String.valueOf(getModel().getId()));
		domainIdHidden.setValue(String.valueOf(getCallback().getOptions().getDomain()));
		domainNameHidden.setValue(getCallback().getOptions().getDomainName());
		userHidden.setValue(getCallback().getOptions().getUser());
		diskForm.submit();
	}
	
	private void paintIdentificationTab(TabLayoutPanel tabPanel) {
		AonFiscalModelIdentificationPanel<Mod202> identificationData = new AonFiscalModelIdentificationPanel<>( getModel() ) ;
		identificationData.addValueChangeHandler(event -> {
			toolbarPanel.setTitle(AonStringUtils.join(getModel().getDocument(),AonStringUtils.SPACE,getModel().getFullName()));
			markAsDirty();			
		});
		tabPanel.add(identificationData, AON.MSG.identification());
	}

	void decorateAdministrationTab() {
		if (admonPanel != null) {
			admonPanel.manageLinks();
		}
	}

	protected void paintDeclarationTab(TabLayoutPanel tabPanel) {
		ScrollPanel declarationScrollPanel = new ScrollPanel();
		FlowPanel container = new FlowPanel();
		AonDisplayTable table = new AonDisplayTable();
		table.addStyleName(AON.CSS.aonBlockCenter());
		
		receiptBox = new AonTextBox();
		receiptBox.setVisibleLength(15);
		receiptBox.setMaxLength(13);
		receiptBox.setValue( getModel().getNumber() );
		receiptBox.addValueChangeHandler( event -> {
			getModel().setNumber(receiptBox.getValue());
			markAsDirty();
		});

		table.addRow()
			.addCell( new Label(AON.MSG.receipt()), AON.CSS.aonTableLabel())
			.addCell(receiptBox);
		
		// Complementaria: Numero justificante de la declaración anterior
		if (getModel().isComplementary()) {
			final AonTextBox previousReceiptBox = new AonTextBox();
			previousReceiptBox.setVisibleLength(15);
			previousReceiptBox.setMaxLength(13);
			previousReceiptBox.setValue( getModel().getReplacedNumber() );
			previousReceiptBox.addValueChangeHandler( event -> {
				getModel().setReplacedNumber(previousReceiptBox.getValue());
				markAsDirty();
			});
			table.addRow()
				.addCell( new Label(AON.MSG.previousReceipt()), AON.CSS.aonTableLabel())
				.addCell(previousReceiptBox);
		}	
		container.add(table);
		declarationScrollPanel.setWidget(container);
		tabPanel.add(declarationScrollPanel, AON.MSG.declaration());
	}
	
	protected void paintAdministrationTab(TabLayoutPanel tabPanel) {
		IFiscalModelAdmonPanelCallback<Mod202, Model202ModuleOptions> cbk = 
			new IFiscalModelAdmonPanelCallback<Mod202, Model202ModuleOptions>() {

				@Override
				public Model202ModuleOptions getOptions() {
					return getCallback().getOptions();
				}

				@Override
				public Mod202 getModel() {
					return Model202Base.this.getModel();
				}

				@Override
				public void showError(String msg) {
					getCallback().showError(msg);
				}

				@Override
				public String getValidatePrintAction() {
					return GWT.getHostPageBaseURL() + "aon_gwt_fiscal/ms/Mod202ValidatePrintAEAT";
					
				}

				@Override
				public String getDownloadFileAction() {
					return Model202Base.MODEL202_FILE;
				}

				@Override
				public String getSendAction() {
					return GWT.getHostPageBaseURL() +"aon_gwt_fiscal/ms/Mod202SendAEAT";
				}

				@Override
				public void sendSuccessfully() {
					Model202.SERVICE.getMod202(getCallback().getOptions().getOccam(), 
							getModel().getId(), new AsyncCallback<Mod202>() {
						@Override
						public void onSuccess(Mod202 selected) {
							setDirty(false);
							selectAndPopulate(selected);
						}
						@Override
						public void onFailure(Throwable caught) {
							getCallback().showError(AON.MSG.unableToReadDeclaration(caught.getMessage()));
						}
					});
				}

				@Override
				public String getCheckAction() {
					return GWT.getHostPageBaseURL() +"aon_gwt_fiscal/ms/Mod202CheckAEAT";
				}

				@Override
				public String getCheckDataResponseDataAction() {
					return GWT.getHostPageBaseURL() +"aon_gwt_fiscal/ms/Mod202CheckDataResponseData";
				}

				@Override
				public String getModelInformationURL() {
					return "https://sede.agenciatributaria.gob.es/Sede/procedimientoini/GE00.shtml";
				}
		};
		admonPanel = new FiscalModelAdmonPanel<>(cbk);
		tabPanel.add( admonPanel, AON.MSG.administrationName(getModel().getAdministration()));		
	}
	
	void decorateDeclarationTab() {
		if (receiptBox != null) {
			receiptBox.setValue( getModel().getNumber() );
		}
	}

}
