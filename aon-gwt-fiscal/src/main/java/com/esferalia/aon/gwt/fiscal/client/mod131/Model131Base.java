package com.esferalia.aon.gwt.fiscal.client.mod131;

import java.util.EnumMap;
import java.util.Map.Entry;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonAuditDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonBoxLabel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog.AonConfirmDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDoubleBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonSplash;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToast;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.fiscal.client.FiscalModelUtils;
import com.esferalia.aon.gwt.fiscal.client.mod131.Model131.Model131Callback;
import com.esferalia.aon.gwt.fiscal.client.mod131.Model131Activity.IMod131ActivityCallback;
import com.esferalia.aon.gwt.fiscal.client.model.AonFiscalModelHeader;
import com.esferalia.aon.gwt.fiscal.client.model.AonFiscalModelIdentificationPanel;
import com.esferalia.aon.gwt.fiscal.client.model.FinishDeclarationPopup;
import com.esferalia.aon.gwt.fiscal.client.model.FinishDeclarationPopup.IFinishDeclarationPopupCallback;
import com.esferalia.aon.gwt.fiscal.client.model.FiscalModelAdmonPanel;
import com.esferalia.aon.gwt.fiscal.shared.mod131.Model131AEATScript;
import com.esferalia.aon.gwt.fiscal.shared.mod131.Model131ScriptProvider;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelDetail;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod131;
import com.esferalia.aon.occam.api.model.fiscal.Mod131Activity;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod131Key;
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

public abstract class Model131Base extends DockLayoutPanel {

	private static final String BLANK = "_blank";

	private static final int MAX_LABEL_LENGTH = 300;
	private static final int COL_NUMBER = 8;
	
	protected static final String MODEL131_PRINT = "/aon_gwt_fiscal/ms/Model131Print";
	protected static final String MODEL131_FILE = "/aon_gwt_fiscal/ms/Model131File";
	
	private Model131Callback callback;
	private Mod131 model;
	private EnumMap<Mod131Key,AonDoubleBox> fieldsMap;
	private boolean dirty;
	
	protected FiscalModelAdmonPanel<Mod131, Model131ModuleOptions> admonPanel;
	protected Model131ActivityTable activityTable;
	
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

	private FlowPanel paymentContainer;
	
	private final AonToolbar decToolbar = new AonToolbar();
	private final InlineLabel dirtyLabel = new InlineLabel();
	private final InlineLabel diffLabel = new InlineLabel();
	private final InlineLabel adjLabel = new InlineLabel();
	private final InlineLabel replacedLabel = new InlineLabel();
	private final Label statusLabel = new Label();

	protected FormPanel diskForm = new FormPanel(BLANK);
	protected Hidden mod131Hidden = new Hidden("mod131");
	protected Hidden domainIdHidden = new Hidden("domainId");
	protected Hidden domainNameHidden = new Hidden("domainName");
	protected Hidden userHidden = new Hidden("user");
	
	
	Model131Base(Mod131 mod131, final Model131Callback callback) {
		super(Unit.PX);
		setStyleName(AON.CSS.aonSelector());
		this.callback = callback;
		this.model = mod131;
		this.fieldsMap = new EnumMap<>(Mod131Key.class);
		this.activityTable = new Model131ActivityTable();
		
		select( mod131 );
		
		AonFiscalModelHeader modelHeader = new AonFiscalModelHeader( mod131 );
		addNorth(modelHeader, AonFiscalModelHeader.HEIGTH);
		addNorth(getToolbarPanel(), AonToolbar.HEIGTH);
		addNorth(getDeclarationToolbarPanel(), AonToolbar.HEIGTH);
		
		TabLayoutPanel tabPanel = new TabLayoutPanel(26, Unit.PX);
		SimpleLayoutPanel centerPanel = new SimpleLayoutPanel();
		centerPanel.addStyleName(AON.CSS.aonScrollArea());
		centerPanel.setWidget(tabPanel);
		add(centerPanel);
		
		showPaymentInfo(mod131);
		
		paintIdentificationTab(tabPanel);
		paintDeclarationTab(tabPanel);
		paintLiquidationTab(tabPanel);
		paintAdministrationTab(tabPanel);
	}
	
	public Model131Callback getCallback() {
		return callback;
	}
	public Mod131 getModel() {
		return model;
	}
	public void setModel(Mod131 model) {
		this.model = model;
	}

	private void select( Mod131 mod131) {
		setModel(mod131);		
		refreshToolbarState();
		styleStatusLabel(mod131);
	}
	
	protected void selectAndPopulate( Mod131 mod131) {
		select(mod131);
		populate(mod131);
		decorateDeclarationTab();
		decorateAdministrationTab();
	}
	
	private void populate(Mod131 mod131) {
		for (Entry<Mod131Key, AonDoubleBox> entry : fieldsMap.entrySet()) {
			double d1 = mod131.getAmount(entry.getKey());
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
				Model131.SERVICE.saveComments( getCallback().getOptions().getOccam(), getModel(), new AsyncCallback<Mod131>() {
					@Override
					public void onSuccess(Mod131 result) {
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
//		markAsPendingButton.setVisible(!model.isNew() &&
//				(model.getStatus() == FiscalStatus.FINISHED 
//				|| model.getStatus() == FiscalStatus.BATCHED
//				|| model.getStatus() == FiscalStatus.SENT
//				|| model.getStatus() == FiscalStatus.CUSTOMER_CHECK
//				|| model.getStatus() == FiscalStatus.BLOCKED));
//		markAsFinishedButton.setVisible(!model.isNew() &&
//				(model.getStatus() == FiscalStatus.PENDING
//				|| model.getStatus() == FiscalStatus.CUSTOMER_CHECK
//				|| model.getStatus() == FiscalStatus.MISSING));
//		markAsSentButton.setVisible(!model.isNew() &&
//				(model.getStatus() == FiscalStatus.FINISHED));
		markAsPendingButton.setVisible(!model.isNew() && FiscalModelUtils.canChangeStatus(model, FiscalStatus.PENDING));
		markAsFinishedButton.setVisible(!model.isNew() && FiscalModelUtils.canChangeStatus(model, FiscalStatus.FINISHED));
		markAsSentButton.setVisible(!model.isNew() && FiscalModelUtils.canChangeStatus(model, FiscalStatus.SENT));
	}
	
	private void styleStatusLabel(Mod131 mod131) {
		statusLabel.setText(mod131.getStatus().getName());
		statusLabel.getElement().getStyle().setBackgroundColor(FiscalModelUtils.getStatusBckColorRGB( mod131.getStatus() ));
		statusLabel.getElement().getStyle().setColor(FiscalModelUtils.getStatusFrgColorRGB( mod131.getStatus() ));
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
	private void save(AsyncCallback<Mod131> cbk) {
		saveButton.setEnabled(false);
		final PopupPanel popup = new PopupPanel(false, true);
		popup.add(new AonSplash());
		popup.setGlassEnabled(true);
		popup.setAnimationEnabled(true);
		popup.center();
		Model131.SERVICE.save(getCallback().getOptions().getOccam(), getModel(), new AsyncCallback<Mod131>() {
			@Override
			public void onSuccess(Mod131 result) {
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
				Model131.SERVICE.delete(getCallback().getOptions().getOccam(),getModel(), new AsyncCallback<Void>() {
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
		Model131.SERVICE.markAsPending(getCallback().getOptions().getOccam(), getModel(), new AsyncCallback<Mod131>() {
					@Override
					public void onSuccess(Mod131 result) {
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
		Model131.SERVICE.markAsSent(getCallback().getOptions().getOccam(), getModel(), new AsyncCallback<Mod131>() {
					@Override
					public void onSuccess(Mod131 result) {
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
		Model131.SERVICE.initializeForFinish(getCallback().getOptions().getOccam(),getModel(),
				new AsyncCallback<Mod131>() {
					@Override
					public void onSuccess(Mod131 m131) {
						selectAndPopulate(m131);
						showFinalizePopup(model);
						markAsFinishedButton.setEnabled(true);
					}

					@Override
					public void onFailure(Throwable caught) {
						getCallback().showError(AON.MSG.unableToReadFiscalParameters(caught.getMessage()));
						markAsFinishedButton.setEnabled(true);
					}
				});
	}
	
	private void showFinalizePopup(Mod131 model) {
		FinishDeclarationPopup<Mod131,Model131ModuleOptions> finalizeDialog = new FinishDeclarationPopup<>(
			model,
			getCallback(), 
			new IFinishDeclarationPopupCallback<Mod131>() {

				@Override
				public void onAccept(Mod131 t) {
					Model131Base.this.doMarkAsFinished();
				}

				@Override
				public void onCancel(Mod131 t) {
					// Nothing
					
				}

				@Override
				public void onCustomerCheck(Mod131 t) {
					Model131Base.this.markAsCustomerCheck();
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
		Model131.SERVICE.markAsFinished(getCallback().getOptions().getOccam(), getModel(), new AsyncCallback<Mod131>() {
			@Override
			public void onSuccess(Mod131 result) {
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
		Model131.SERVICE.markAsCustomerCheck(getCallback().getOptions().getOccam(), getModel(), new AsyncCallback<Mod131>() {
			@Override
			public void onSuccess(Mod131 result) {
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

	private void showPaymentInfo(Mod131 mod) {
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
	
	private void paintLiquidationTab(TabLayoutPanel tabPanel) {
		ScrollPanel liquidationScrollPanel = new ScrollPanel();
		FlowPanel container = new FlowPanel();
		FlexTable table = new FlexTable();
		container.add(table);
		defineTable(table);
		for (IModelScript<Mod131Key> ms : Model131ScriptProvider.obtainScript(getModel())) {
			if (ms.paintHeaderBefore()) {
				paintHeader(table);
			}
			paintRow(table,getCallback(),ms);	
			if (ms == Model131AEATScript.R00) {
				paintActivityRow(table);
			}
		}
		liquidationScrollPanel.setWidget(container);
		tabPanel.add(liquidationScrollPanel, AON.MSG.liquidacion());
	}
	
	protected void defineTable(FlexTable table) {
		table.setWidth("100%");
		table.addStyleName(AON.CSS.aonMarginBottom());
		
		table.getColumnFormatter().setWidth(0, "20px");
		table.getColumnFormatter().setWidth(1, "140px");
		table.getColumnFormatter().setWidth(2, "auto");
		table.getColumnFormatter().setWidth(3, "140px");
		table.getColumnFormatter().setWidth(4, "100px");
		table.getColumnFormatter().setWidth(5, "40px");
		table.getColumnFormatter().setWidth(6, "140px");
		table.getColumnFormatter().setWidth(7, "50px");
	}
	
	protected void paintHeader(FlexTable table) {
		int row = table.getRowCount();
		table.setWidget(row, 0, new Label());
		table.setWidget(row, 1, new Label());
		table.setWidget(row, 2, new Label());
		table.setWidget(row, 3, new Label());
		table.setWidget(row, 4, new Label());
		table.setWidget(row, 5, new Label());
		table.setWidget(row, 6, new Label());
		table.setWidget(row, 6, new Label());
	}

	protected void paintRow(FlexTable table, final Model131Callback callback, IModelScript<Mod131Key> script) {
		if (script.hasGraphicParticularity()) {
			paintParticularyRow(table,callback,script);
		} else {
			int row = table.getRowCount();
			paintLabel(table, row,script);
			table.getFlexCellFormatter().setColSpan(row, 0, (script.getKeys() == null)?COL_NUMBER:(COL_NUMBER-3));
			if (script.getKeys() != null) {
				int col = 1;
				for (Mod131Key key : script.getKeys()) {
					col = paintBox( table, row, col, key );
					col = paintField( table, row, col, callback, script, key );
				}
				paintInfoCol(table, row,col,callback,script);	
			}
		}
	}
	
	protected void paintParticularyRow(FlexTable table, final Model131Callback callback, IModelScript<Mod131Key> script) {
		
	}

	protected void paintLabel( FlexTable table, int row, IModelScript<Mod131Key> script) {
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

	private void paintActivityRow(FlexTable table) {
		int row = table.getRowCount();
		activityTable.paint(getModel().getActivities());
		activityTable.addSelectionHandler(event -> {
			final Mod131Activity original = Mod131Activity.clone(event.getSelectedItem()); 
			int idx = 0;
			for (int i = 0; i < getModel().getActivities().size() ; i++ ) {
				if (getModel().getActivities().get(i) == event.getSelectedItem()) {
					idx = i;
				}
			}
			final int currentIndex = idx;
			final AonCustomDialog dialog = new AonCustomDialog();
			IMod131ActivityCallback activityCallback = new IMod131ActivityCallback() {
				
				@Override
				public Mod131 getModel() {
					return Model131Base.this.getModel();
				}
				
				@Override
				public void onCancel() {
					dialog.hide();
					getModel().getActivities().set(currentIndex, original);
					calculateAndRefresh( getCallback() );
					activityTable.paint(getModel().getActivities());
				}
				
				@Override
				public void onAccept(Mod131Activity act) {
					dialog.hide();
					getModel().getActivities().set(currentIndex, act);
					calculateAndRefresh( getCallback() );
					activityTable.paint(getModel().getActivities());
				}
				
				@Override
				public Mod131Activity getActivity() {
					return event.getSelectedItem();
				}

				@Override
				public void onRemove() {
					dialog.hide();
					for (int i = 0; i < getModel().getActivities().size() ; i++ ) {
						if (getModel().getActivities().get(i) == event.getSelectedItem()) {
							getModel().getActivities().get(i).initialize();
						}
					}
					calculateAndRefresh( getCallback() );
					activityTable.paint(getModel().getActivities());
				}

				@Override
				public Model131ModuleOptions getOptions() {
					return getCallback().getOptions();
				}
			};
			Model131Activity actPanel = new Model131Activity(activityCallback);
			dialog.setCaption(event.getSelectedItem().getFullDescription());
			dialog.setGlassEnabled(true);
			dialog.setAnimationEnabled(true);
			dialog.add(actPanel);
			dialog.setWidth("700px");
			dialog.setHeight("600px");
			dialog.show();
			dialog.center();
		});
		FlowPanel tableContainer = new FlowPanel();
		tableContainer.add( activityTable ) ;
		table.setWidget(row, 0, tableContainer );
		table.getFlexCellFormatter().setColSpan(row, 0, COL_NUMBER);
	}

	
	
	protected void paintParticularyRow(FlexTable table, IModelScript<Mod131Key> script) {
		if (script.getKeys() == null) return;
		if (script.getKeys()[0] == Mod131Key.P2) {
			paintRowP02(table, script);
		}
	}
	
	private void paintRowP02(FlexTable table, IModelScript<Mod131Key> script) {
		int row = table.getRowCount();
		final FiscalModelDetail p2 = getModel().ensureDetail(Mod131Key.P2);
		table.setWidget(row, 0, new Label(script.getLabel()));
		table.getFlexCellFormatter().addStyleName(row, 0,AON.CSS.aonTextRight() );
		table.getFlexCellFormatter().addStyleName(row, 0,AON.CSS.aonPaddingRight() );
		table.getFlexCellFormatter().setColSpan(row, 0, 6);
		
		final Label wP2 = new Label(p2.getAmount()==1?AON.MSG.yes():AON.MSG.no());
		table.setWidget(row, 1, wP2 );
		table.getFlexCellFormatter().setColSpan(row, 1, 2);
	}
	
	protected void paintEmptyRow(FlexTable table) {
		int row = table.getRowCount();
		table.setWidget(row, 0, new Label( "." ));
		table.getFlexCellFormatter().setColSpan(row, 0, COL_NUMBER);
	}

	
	private int paintBox(FlexTable table, int row, int col, Mod131Key key) {
		table.setWidget(row, col, new AonBoxLabel(key.getBox()));
		return ++col;
	}

	private int paintField(FlexTable table, int row, int col, final Model131Callback callback, IModelScript<Mod131Key> script, final Mod131Key key) {
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
		return ++col;
	}
	
	private void paintInfoCol(FlexTable table, int row, int col, final Model131Callback callback, final IModelScript<Mod131Key> script) {
		FlowPanel buttonContainer = new FlowPanel();
		for (final FiscalModelKeyInfo infoKey : script.getInfoKeys()) {
			buttonContainer.setStyleName(AON.CSS.aonNowrap());
			if 	(infoKey != FiscalModelKeyInfo.NONE) {
				final AonTableButton button = new AonTableButton("");
				button.setTitle(infoKey.getLabel());
				
				if 	(infoKey == FiscalModelKeyInfo.INVOICE) button.addStyleName(AON.CSS.aonIconList());
				else if	(infoKey == FiscalModelKeyInfo.DIFF_INVOICE) button.addStyleName(AON.CSS.aonIconDiff());
				else if	(infoKey == FiscalModelKeyInfo.SALARY) button.addStyleName(AON.CSS.aonIconEuro());
				else if	(infoKey == FiscalModelKeyInfo.SALARY_IN_KIND) button.addStyleName(AON.CSS.aonIconData());
				else if	(infoKey == FiscalModelKeyInfo.DIFF_SALARY) button.addStyleName(AON.CSS.aonIconDiff());
				else if	(infoKey == FiscalModelKeyInfo.COMPUTE) button.addStyleName(AON.CSS.aonIconCalc());
				else if	(infoKey == FiscalModelKeyInfo.COMPUTE_KEY) button.addStyleName(AON.CSS.aonIconCalc());
				else if	(infoKey == FiscalModelKeyInfo.IRPF_ACTIVITY) button.addStyleName(AON.CSS.aonIconBullet());
				
				button.addClickHandler(event -> Model131.SERVICE.getInfo(
					callback.getOptions().getOccam(),
					getModel(), script, infoKey,new AsyncCallback<String>() {

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

	public void calculateAndRefresh(final Model131Callback callback) {
		Model131.SERVICE.calculate(callback.getOptions().getOccam(),getModel(),
			new AsyncCallback<Mod131>() {

				@Override
				public void onFailure(Throwable caught) {
					callback.showError(AON.MSG.errorMessage());
				}

				@Override
				public void onSuccess(Mod131 result) {
					populate(result);
				}
			}
		);	
	}

	private void onReset() {
		resetButton.setEnabled(false);
		AonConfirmDialog cd = new AonConfirmDialog();
		cd.confirm(AON.MSG.confirmDeclarationinitializationAction(), new AonConfirmDialogCallback() {

			@Override
			public void onAccept() {
				Model131.SERVICE.reset(getCallback().getOptions().getOccam(),getModel(),
						new AsyncCallback<Mod131>() {
							@Override
							public void onSuccess(Mod131 m131) {
								setDirty( true );
								selectAndPopulate(m131);
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
	
	protected void submitForm(String action) {
		diskForm.setMethod(FormPanel.METHOD_POST);
		diskForm.setAction(GWT.getHostPageBaseURL() + action);
		diskForm.clear();
		FlowPanel diskPanel = new FlowPanel();
		diskPanel.add(mod131Hidden);
		diskPanel.add(domainIdHidden);
		diskPanel.add(domainNameHidden);
		diskPanel.add(userHidden);
		diskForm.add(diskPanel);
		mod131Hidden.setValue(String.valueOf(getModel().getId()));
		domainIdHidden.setValue(String.valueOf(getCallback().getOptions().getDomain()));
		domainNameHidden.setValue(getCallback().getOptions().getDomainName());
		userHidden.setValue(getCallback().getOptions().getUser());
		diskForm.submit();
	}

	private void paintIdentificationTab(TabLayoutPanel tabPanel) {
		AonFiscalModelIdentificationPanel<Mod131> identificationData = new AonFiscalModelIdentificationPanel<>( getModel() ) ;
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

	void paintDeclarationTab(TabLayoutPanel tabPanel) {}
	void paintAdministrationTab(TabLayoutPanel tabPanel) {}
	void decorateDeclarationTab() {}
	
}
